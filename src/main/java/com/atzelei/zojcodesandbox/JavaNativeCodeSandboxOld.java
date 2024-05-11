package com.atzelei.zojcodesandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import com.atzelei.zojcodesandbox.model.ExecuteCodeRequest;
import com.atzelei.zojcodesandbox.model.ExecuteCodeResponse;
import com.atzelei.zojcodesandbox.model.ExecuteMessage;
import com.atzelei.zojcodesandbox.model.JudgeInfo;
import com.atzelei.zojcodesandbox.utils.ProcessUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class JavaNativeCodeSandboxOld implements CodeSandbox {


    private static final String GLOBAL_CODE_DIR_NAME = "tmpCode";

    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    private static final long TIME_OUT = 10000L;

    private static final List<String> blackList = Arrays.asList("Files","exec");

    private static final WordTree WORD_TREE;

    public static final String SECURITY_MANAGER_PATH = "D:\\Javaideaporject\\zoj-code-sandbox\\src\\main\\resources\\security";

    public static final String SECURITY_MANAGER_CLASS_NAME = "MySecurityManager";

    static {
        //添加黑名单校验 初始化字典树
        WORD_TREE = new WordTree();
        WORD_TREE.addWords(blackList);
    }

    public static void main(String[] args) {
        JavaNativeCodeSandboxOld javaNativeCodeSandbox = new JavaNativeCodeSandboxOld();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();

        executeCodeRequest.setInput(Arrays.asList("1 2", "1 3"));

        //读取Resource下的代码
        String code = ResourceUtil.readStr("testCode/unsafe/RunFileError.java", StandardCharsets.UTF_8);

        //放到请求对象中
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");

        //运行代码拿到返回结果
        ExecuteCodeResponse executeCodeResponse = javaNativeCodeSandbox.executeCode(executeCodeRequest);

        System.out.println("executeCodeResponse = " + executeCodeResponse);
        return ;
    }


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        //System.setSecurityManager(new DenySecurityManager());
        //读取请求参数
        List<String> inputList = executeCodeRequest.getInput();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();

        //黑名单校验 (字典树) 校验是否包含有黑名单的关键词
        FoundWord foundWord = WORD_TREE.matchWord(code);
        //如果找到了关键词
//        if (foundWord != null)
//        {
//            System.out.println("包含禁止词" + foundWord.getFoundWord());
//            return null;
//        }

        //1.把用户的代码保存为文件
        String userDir = System.getProperty("user.dir"); //项目根目录
        //获取用户的资源文件目录
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME; // File.separator文件分隔符 为了兼容不同的系统用这个表示文件分隔符

        //判断全局代码目录是否存在 没有则新建
        if (!FileUtil.exist(globalCodePathName))
        {
            File mkdir = FileUtil.mkdir(globalCodePathName);
        }

        // 把用户的代码 隔离存放  用户代码的父文件夹
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        //用户代码保存的实际路径
        String userCodePath =  userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
        //保存代码
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);


        //2.编译代码，得到class文件  String.format:字符串连接
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsoluteFile());
        System.out.println("compileCmd = " + compileCmd);
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage compileMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "编译");
            System.out.println("compileMessage = " + compileMessage);
        } catch (Exception e) {
            return getErrorResponse(e);
        }

        //3.执行代码 java -Xmx256 -Dfile.encoding=UTF-8 -cp %s;%s -Djava %s Main %s
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String s : inputList) {
            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s;%s -Djava.security.manager=%s Main %s", userCodeParentPath, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME, s);
            System.out.println("runCmd = " + runCmd);

            try {
                //开启一个新的子进程
                Process runProcess = Runtime.getRuntime().exec(runCmd); //Process 类提供了执行从进程输入、执行输出到进程、等待进程完成、检查进程的退出状态以及销毁（杀掉）进程的方法。
                new Thread(() -> {
                    try {
                        Thread.sleep(TIME_OUT);
                        System.out.println("超时了中断");
                        runProcess.destroy();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }).start();
                ExecuteMessage runMessage = ProcessUtils.runProcessAndGetMessage(runProcess, "运行");
                System.out.println("runMessage = " + runMessage);
                executeMessageList.add(runMessage);
            } catch (IOException e) {
                return getErrorResponse(e);
            }
        }
        //4.整理输出java.lang.ExceptionInInitializerError
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<String> outputList = new ArrayList<>();
        long maxTime = 0;
        for (ExecuteMessage executeMessage : executeMessageList) {
            if (StrUtil.isNotBlank(executeMessage.getErrorMessage()))
            {
                executeCodeResponse.setMessage(executeMessage.getErrorMessage());
                //执行中存在错误
                executeCodeResponse.setStatus(3);
                break;
            }
            outputList.add(executeMessage.getMessage());
            //处理程序运行消时
            Long time = executeMessage.getTime();
            if (time != null)
            {
                maxTime = Math.max(maxTime, time);
            }
        }
        if (outputList.size() == executeMessageList.size())
        {
            executeCodeResponse.setStatus(1);

        }
        executeCodeResponse.setOutput(outputList);
        //正常运行完成
        executeCodeResponse.setStatus(1);
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setTime(maxTime);
        
        executeCodeResponse.setJudgeInfo(judgeInfo);
        
        //5.文件清理
        //服务器容量不够 有可能自动删除了 所以得判断一下 不然会报错
        if (userCodeFile.getParentFile() != null)
        {
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除" + (del ? "删除成功" : "删除失败"));
        }

        //6.错误处理,提升程序健壮性


        return executeCodeResponse;
    }


    /**
     * 获取错误相应
     * @param e
     * @return
     */
    private ExecuteCodeResponse getErrorResponse(Exception e)
    {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutput(new ArrayList<>());
        executeCodeResponse.setMessage(e.getMessage());
        executeCodeResponse.setStatus(2); //表示代码沙箱错误 3是用户代码错误
        executeCodeResponse.setJudgeInfo(new JudgeInfo());
        return executeCodeResponse;
        
    }



}
