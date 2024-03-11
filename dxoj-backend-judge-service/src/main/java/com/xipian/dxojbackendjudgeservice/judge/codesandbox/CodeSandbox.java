package com.xipian.dxojbackendjudgeservice.judge.codesandbox;

import com.xipian.dxojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.xipian.dxojbackendmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 */
public interface CodeSandbox {

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
