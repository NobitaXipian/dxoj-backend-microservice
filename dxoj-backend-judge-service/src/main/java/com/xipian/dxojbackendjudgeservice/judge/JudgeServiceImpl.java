package com.xipian.dxojbackendjudgeservice.judge;

import cn.hutool.json.JSONUtil;
import com.xipian.dxojbackendcommon.common.ErrorCode;
import com.xipian.dxojbackendcommon.exception.BusinessException;
import com.xipian.dxojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.xipian.dxojbackendjudgeservice.judge.codesandbox.CodeSandboxFactory;
import com.xipian.dxojbackendjudgeservice.judge.codesandbox.CodeSandboxProxy;
import com.xipian.dxojbackendjudgeservice.judge.strategy.JudgeContext;
import com.xipian.dxojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.xipian.dxojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.xipian.dxojbackendmodel.model.codesandbox.JudgeInfo;
import com.xipian.dxojbackendmodel.model.dto.question.JudgeCase;
import com.xipian.dxojbackendmodel.model.entity.Question;
import com.xipian.dxojbackendmodel.model.entity.QuestionSubmit;
import com.xipian.dxojbackendmodel.model.enums.JudgeInfoMessageEnum;
import com.xipian.dxojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.xipian.dxojbackendserviceclient.service.QuestionFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionFeignClient questionFeignClient;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;


    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1）传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionFeignClient.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 2）如果题目提交状态不为等待中，就不用重复执行了
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
        // 3）更改判题（题目提交）的状态为 “判题中”，防止重复执行
        questionSubmitUpdate(questionSubmitId,QuestionSubmitStatusEnum.RUNNING.getValue(),null);

        // 4）调用沙箱，获取到执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        // 获取输入用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        // 得到代码沙箱响应
        ExecuteCodeResponse executeCodeResponse = null;
        try {
            executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        } catch (Exception e) {
            log.error("代码沙箱http请求出错:",e);
        }finally {
            if (executeCodeResponse==null){
                JudgeInfo judgeInfo = new JudgeInfo();
                judgeInfo.setMessage(JudgeInfoMessageEnum.SYSTEM_ERROR.getValue());
                questionSubmitUpdate(questionSubmitId,QuestionSubmitStatusEnum.FAILED.getValue(), judgeInfo);
                throw new BusinessException(ErrorCode.API_REQUEST_ERROR,"连接代码沙箱失败");
            }
        }

        // 5）根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setExecuteCodeResponse(executeCodeResponse);
        judgeContext.setInputList(inputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);
        // 6）修改数据库中的判题结果
        questionSubmitUpdate(questionSubmitId,QuestionSubmitStatusEnum.SUCCEED.getValue(), judgeInfo);

        // 修改题目通过数和提交数
        Question questionUpdate = new Question();
        questionUpdate.setId(questionId);
        if (judgeInfo.getMessage().equals(JudgeInfoMessageEnum.ACCEPTED.getValue())){
            questionUpdate.setAcceptedNum(question.getAcceptedNum() + 1);
        }
        questionUpdate.setSubmitNum(question.getSubmitNum() + 1);
        boolean updateQuestionNum = questionFeignClient.updateQuestionById(questionUpdate);
        if (!updateQuestionNum) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目通过数和提交数更新错误");
        }

        QuestionSubmit questionSubmitResult = questionFeignClient.getQuestionSubmitById(questionId);
        return questionSubmitResult;
    }

    private boolean questionSubmitUpdate(long questionSubmitId, Integer status,JudgeInfo judgeInfo) {
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(status);
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        boolean update =  questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        return update;
    }
}
