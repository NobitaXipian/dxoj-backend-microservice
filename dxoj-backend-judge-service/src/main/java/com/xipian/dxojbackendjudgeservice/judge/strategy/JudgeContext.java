package com.xipian.dxojbackendjudgeservice.judge.strategy;

import com.xipian.dxojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.xipian.dxojbackendmodel.model.codesandbox.JudgeInfo;
import com.xipian.dxojbackendmodel.model.dto.question.JudgeCase;
import com.xipian.dxojbackendmodel.model.entity.Question;
import com.xipian.dxojbackendmodel.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定义在策略中传递的参数）
 */
@Data
public class JudgeContext {

    // private JudgeInfo judgeInfo;

    private ExecuteCodeResponse executeCodeResponse;

    private List<String> inputList;

    // private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;

}
