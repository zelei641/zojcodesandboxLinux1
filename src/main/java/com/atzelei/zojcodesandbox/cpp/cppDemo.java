package com.atzelei.zojcodesandbox.cpp;

import cn.hutool.core.date.StopWatch;
import com.atzelei.zojcodesandbox.CodeSandboxTemplate;
import com.atzelei.zojcodesandbox.model.ExecuteMessage;
import com.atzelei.zojcodesandbox.utils.ProcessUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class cppDemo extends CodeSandboxTemplate
{

    public static void main(String[] args) {
        cppDemo cppDemo = new cppDemo();
        String code = "#include <bits/stdc++.h>\n" +
                "using namespace std;\n" +
                "\n" +
                "int main()\n" +
                "{\n" +
                "\tint a;\n" +
                "\tint b;\n" +
                "\n" +
                "\tcin >> a >> b;\n" +
                "\tcout << (a + b);\n" +
                "\t\n" +
                "\treturn 0;\n" +
                " } \n";
        File file = cppDemo.saveCodeToFile(code);
        ExecuteMessage executeMessage = cppDemo.compileFile(file);
        List<String> inputList = new ArrayList<>();
        inputList.add("1 2");

        List<ExecuteMessage> executeMessages = cppDemo.runFile(file, inputList);
        for (ExecuteMessage message : executeMessages) {
            System.out.println("message = " + message);
        }
    }

    //用户使用的语言
    public static final String USER_LANGUAGE = "cppCode";

    //存放代码的文件名
    public static final String GLOBAL_CODE_NAME = "main.cpp";

    //可执行文件的名字
    public static final String GLOBAL_CODE_EXE_NAME = "main.exe";

    //代码的最多执行时间
    public static final long TIME_OUT = 5000L;





    public cppDemo() {
        super.USER_LANGUAGE = USER_LANGUAGE;
        super.GLOBAL_CODE_NAME = GLOBAL_CODE_NAME;
    }

    /**
     * 2、编译代码
     * @param userCodeFile
     * @return
     */
    @Override
    public ExecuteMessage compileFile(File userCodeFile) {
        //编译c++代码
        String compileCmd = String.format("g++ %s -o %s", userCodeFile.getAbsolutePath(), GLOBAL_CODE_EXE_NAME);
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "编译");
            if (executeMessage.getExitValue() != 0) {
                throw new RuntimeException("编译错误");
            }
            return executeMessage;
        } catch (Exception e) {
//            return getErrorResponse(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 3、执行文件，获得执行结果列表
     * @param userCodeFile
     * @param inputList
     * @return
     */
    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {
        //exe文件所在的路径
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();

        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputList) {
//            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentPath, inputArgs);
            String runCmd = String.format(GLOBAL_CODE_EXE_NAME);
            System.out.println("命令行执行的exe的命令：" + runCmd);
            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);

                // 超时控制
                new Thread(() -> {
                    try {
                        Thread.sleep(TIME_OUT);
                        System.out.println("超时了，中断");
                        runProcess.destroy();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                ExecuteMessage executeMessage = ProcessUtils.runInteractProcessAndGetMessage(runProcess, "运行", inputArgs);
                System.out.println(executeMessage);
                executeMessageList.add(executeMessage);
            } catch (Exception e) {
                throw new RuntimeException("执行错误", e);
            }
        }
        return executeMessageList;
    }
}


