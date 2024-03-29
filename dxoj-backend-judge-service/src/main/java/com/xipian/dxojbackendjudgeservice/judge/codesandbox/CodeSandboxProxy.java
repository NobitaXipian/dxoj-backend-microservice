package com.xipian.dxojbackendjudgeservice.judge.codesandbox;

import com.xipian.dxojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.xipian.dxojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CodeSandboxProxy implements CodeSandbox {

    private final CodeSandbox codeSandbox;


    public CodeSandboxProxy(CodeSandbox codeSandbox) {
        this.codeSandbox = codeSandbox;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("代码沙箱请求信息：" + executeCodeRequest.toString());
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        if (executeCodeResponse!=null){
            log.info("代码沙箱响应信息：" + executeCodeResponse.toString());
        }else {
            log.error("代码沙箱响应错误");
        }
        return executeCodeResponse;
    }
}
