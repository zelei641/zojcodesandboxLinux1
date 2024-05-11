package com.atzelei.zojcodesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 代码沙箱的相应信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeResponse
{
    private List<String> output;

    /**
     * 接口信息
     */
    private String message;


    /**
     * 执行状态
     */
    private Integer status;

    private JudgeInfo judgeInfo;

}
