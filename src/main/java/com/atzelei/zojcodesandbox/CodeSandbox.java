package com.atzelei.zojcodesandbox;


import com.atzelei.zojcodesandbox.model.ExecuteCodeRequest;
import com.atzelei.zojcodesandbox.model.ExecuteCodeResponse;

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
