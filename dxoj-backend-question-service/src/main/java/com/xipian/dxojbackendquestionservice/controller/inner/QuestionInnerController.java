package com.xipian.dxojbackendquestionservice.controller.inner;

import com.xipian.dxojbackendmodel.model.entity.Question;
import com.xipian.dxojbackendmodel.model.entity.QuestionSubmit;
import com.xipian.dxojbackendquestionservice.service.QuestionService;
import com.xipian.dxojbackendquestionservice.service.QuestionSubmitService;
import com.xipian.dxojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 该服务仅内部调用，不是给前端的
 */
@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @GetMapping("/get/id")
    @Override
    public Question getQuestionById(@RequestParam("questionId") long questionId) {
        return questionService.getById(questionId);
    }

    @PostMapping("/update")
    @Override
    public boolean updateQuestionById(Question question) {
        return questionService.updateById(question);
    }

    @GetMapping("/question_submit/get/id")
    @Override
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionId") long questionSubmitId) {
        return questionSubmitService.getById(questionSubmitId);
    }

    @PostMapping("/question_submit/update")
    @Override
    public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit) {
        return questionSubmitService.updateById(questionSubmit);
    }

    @PostMapping("/update/Num")
    @Override
    public boolean updateQuestionNumById(Question question) {
        boolean update = questionService.update().eq("id", question.getId())
                .setSql(question.getAcceptedNum() != 0, "acceptedNum = acceptedNum + 1")
                .setSql(question.getSubmitNum() != 0, "submitNum = submitNum + 1")
                .setSql(question.getThumbNum() != 0, "thumbNum = thumbNum + 1")
                .setSql(question.getFavourNum() != 0, "favourNum = favourNum + 1")
                .update();
        return update;
    }

}
