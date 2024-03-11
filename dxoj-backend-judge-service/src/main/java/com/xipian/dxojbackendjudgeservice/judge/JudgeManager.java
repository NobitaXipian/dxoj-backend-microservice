package com.xipian.dxojbackendjudgeservice.judge;

import com.xipian.dxojbackendjudgeservice.judge.strategy.JudgeContext;
import com.xipian.dxojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.xipian.dxojbackendjudgeservice.judge.strategy.DefaultJudgeStrategy;
import com.xipian.dxojbackendjudgeservice.judge.strategy.JavaLanguageJudgeStrategy;
import com.xipian.dxojbackendmodel.model.codesandbox.JudgeInfo;
import com.xipian.dxojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
