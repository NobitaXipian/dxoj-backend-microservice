package com.xipian.dxojbackendjudgeservice.judge.codesandbox.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.xipian.dxojbackendcommon.common.BaseResponse;
import com.xipian.dxojbackendcommon.common.ErrorCode;
import com.xipian.dxojbackendcommon.exception.BusinessException;
import com.xipian.dxojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.xipian.dxojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.xipian.dxojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 远程代码沙箱（实际调用接口的沙箱）
 */
@Slf4j
public class RemoteCodeSandbox implements CodeSandbox {

    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "secretKey";


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("远程代码沙箱");
        String url = "http://localhost:8090/executeCode";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        // String responseStr = HttpUtil.createPost(url)
        //         .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
        //         .body(json)
        //         .execute()
        //         .body();
        HttpResponse httpResponse = HttpRequest.post(url)
                .header(AUTH_REQUEST_HEADER,AUTH_REQUEST_SECRET)
                .body(json)
                .execute();
        if (!httpResponse.isOk()) {
            return null;
        }
        String responseStr = httpResponse.body();
        log.info("远程代码沙箱调用完成");
        //responseStr为BaseResponse<ExecuteCodeResponse>,通过JSONUtil得到ExecuteCodeResponse对象
        BaseResponse baseResponse = JSONUtil.toBean(responseStr, BaseResponse.class);
        ExecuteCodeResponse executeCodeResponse = JSONUtil.toBean(baseResponse.getData().toString(), ExecuteCodeResponse.class);
        return executeCodeResponse;
    }
}
