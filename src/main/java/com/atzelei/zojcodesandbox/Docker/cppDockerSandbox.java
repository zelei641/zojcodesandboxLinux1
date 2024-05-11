package com.atzelei.zojcodesandbox.Docker;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ArrayUtil;
import com.atzelei.zojcodesandbox.CodeSandboxTemplate;
import com.atzelei.zojcodesandbox.JavaDockerCodeSandbox;
import com.atzelei.zojcodesandbox.model.ExecuteCodeRequest;
import com.atzelei.zojcodesandbox.model.ExecuteCodeResponse;
import com.atzelei.zojcodesandbox.model.ExecuteMessage;
import com.atzelei.zojcodesandbox.utils.ProcessUtils;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.StatsCmd;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.springframework.util.StopWatch;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * cpp判题 demo
 */
public class cppDockerSandbox extends CodeSandboxTemplate
{

    //全局代码目录
    private static final String GLOBAL_CODE_DIR_NAME = "tmpCode";
    private static final long TIME_OUT = 5L;
    private static final Boolean FIRST_INIT = true;



    public static void main(String[] args) {
        cppDockerSandbox javaNativeCodeSandbox = new cppDockerSandbox();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInput(Arrays.asList("1 2", "1 3"));
        String code = "#include <bits/stdc++.h>\n" +
                "using namespace std;\n" +
                "\n" +
                "int main()\n" +
                "{\n" +
                "\tint a; int b;\n" +
                "\tcin >> a >> b;\n" +
                "\t\n" +
                "\t\n" +
                "\tcout << (a + b);\n" +
                "\t\n" +
                "\treturn 0;\n" +
                " } \n";
//        String code = ResourceUtil.readStr("testCode/unsafeCode/RunFileError.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/simpleCompute/Main.java", StandardCharsets.UTF_8);
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("cpp");
        ExecuteCodeResponse executeCodeResponse = javaNativeCodeSandbox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }


    /**
     * 1. 把用户的代码保存为文件
     * @param code 用户代码
     * @return
     */
    public File saveCodeToFile(String code) {
        //得到项目的 resources路径
        String userDir = System.getProperty("user.dir");
        //所有的代码存放在temCode目录下 也就是全局代码目录
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;
        // 判断全局代码目录是否存在，没有则新建
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }

        // 把用户的代码隔离存放
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + "HelloWorld.cpp";
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
        return userCodeFile;
    }



    /**
     * 2、编译代码
     * @param userCodeFile
     * @return
     */
    public ExecuteMessage compileFile(File userCodeFile) {
        String compileCmd = String.format("g++ %s -o %s/HelloWorld", userCodeFile.getAbsolutePath(),userCodeFile.getParentFile().getAbsoluteFile());
        System.out.println("compileCmd = " + compileCmd);
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
     * 3、创建容器，把文件复制到容器内
     * @param userCodeFile
     * @param inputList
     * @return
     */
    @Override
    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList){
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
//        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
//                .withDockerHost("tcp://119.3.220.170:2375")
//                .build();
        // 1.获取默认的 Docker Client
        DockerClient dockerClient;

        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("unix:///var/run/docker.sock")
//                .withDockerHost("tcp://119.3.220.170:2375")
                .build();
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(100))
                .responseTimeout(Duration.ofSeconds(100))
                .build();
        dockerClient = DockerClientImpl.getInstance(config, httpClient);

        // 拉取镜像
        String image = "gcc:10.1";
//        if (FIRST_INIT) {
//            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
//            PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
//                @Override
//                public void onNext(PullResponseItem item) {
//                    System.out.println("下载镜像：" + item.getStatus());
//                    super.onNext(item);
//                }
//            };
//            try {
//                pullImageCmd
//                        .exec(pullImageResultCallback)
//                        .awaitCompletion();
//            } catch (InterruptedException e) {
//                System.out.println("拉取镜像异常");
//                throw new RuntimeException(e);
//            }
//        }
//
//        System.out.println("下载完成");

        // 2.创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(100 * 1000 * 1000L);
        hostConfig.withMemorySwap(0L);
        hostConfig.withCpuCount(1L);
        //hostConfig.withSecurityOpts(Arrays.asList("seccomp=安全管理配置字符串"));
        System.out.println("userCodeParentPath = " + userCodeParentPath);
        hostConfig.setBinds(new Bind(userCodeParentPath, new Volume("/app")));
        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
                .withNetworkDisabled(true)
                .withAttachStdin(true)
                .withAttachStderr(true)
                .withAttachStdout(true)
                .withTty(true)
                .exec();
        System.out.println(createContainerResponse);
        String containerId = createContainerResponse.getId();

        // 3.启动容器
        dockerClient.startContainerCmd(containerId).exec();

        // docker exec keen_blackwell java -cp /app Main 1 3
        // 执行命令并获取结果
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputList) {
            StopWatch stopWatch = new StopWatch();
            String[] inputArgsArray = inputArgs.split(" ");
            //String[] cmdArray = ArrayUtil.append(new String[]{"java", "-cp", "/app", "Main"}, inputArgsArray);
            //String[] cmdArray = ArrayUtil.append(new String[]{"ls"});
            String[] cmdArray1 = ArrayUtil.append(new String[]{"cp","app/HelloWorld","HelloWorld"});
            String[] cmdArray = ArrayUtil.append(new String[]{"./HelloWorld"});
            //String[] cmdArray = ArrayUtil.append(new String[]{"/bin/bash", "-c", "read -p 'Enter input: ' input; echo $input"});




            System.out.println("创建执行命令：" + inputArgsArray);
            //4.创建命令
            ExecCreateCmdResponse execCreateCmdResponseFront = dockerClient.execCreateCmd(containerId)
                    .withCmd(cmdArray1)
                    .exec();

            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd(cmdArray)
                    .withAttachStderr(true)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .exec();
            String execIdFront = execCreateCmdResponseFront.getId();
            String execId = execCreateCmdResponse.getId();

            final ExecuteMessage executeMessage = new ExecuteMessage();
            final String[] message = {null};
            final String[] errorMessage = {null};
            long time = 0L;
            // 判断是否超时
            final boolean[] timeout = {true};

            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
                @Override
                public void onComplete() {
                    // 如果执行完成，则表示没超时
//                    timeout[0] = false;
//                    super.onComplete();
//                    System.out.println("超时");
                }

                @Override
                public void onNext(Frame frame) {

                    StreamType streamType = frame.getStreamType();
                    if (StreamType.STDERR.equals(streamType)) {
                        errorMessage[0] = new String(frame.getPayload());
                        System.out.println("输出错误结果：" + errorMessage[0]);
                        executeMessage.setErrorMessage(errorMessage[0]);
                    } else {
                        message[0] = new String(frame.getPayload());
                        System.out.println("输出结果：" + message[0]);
                        executeMessage.setMessage(message[0]);
                    }
                    super.onNext(frame);
                }
            };

            final long[] maxMemory = {0L};

            // 获取占用的内存
            StatsCmd statsCmd = dockerClient.statsCmd(containerId);
            ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {

                @Override
                public void onNext(Statistics statistics) {
                    System.out.println("内存占用：" + statistics.getMemoryStats().getUsage());
                    maxMemory[0] = Math.max(statistics.getMemoryStats().getUsage(), maxMemory[0]);
                    executeMessage.setMemory(maxMemory[0]);
                }

                @Override
                public void close() throws IOException {

                }

                @Override
                public void onStart(Closeable closeable) {

                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onComplete() {

                }
            });
            statsCmd.exec(statisticsResultCallback);
            // 3. 准备输入数据

            try {
                stopWatch.start();
                InputStream inputStream = new ByteArrayInputStream(new String(inputArgs + "\n").getBytes());

                //InputStream inputStream = new ByteArrayInputStream(new String(inputArgs + "\n").getBytes());
                //InputStream inputStream = new ByteArrayInputStream(new String(inputData + "\n").getBytes());

//                int b = 0;
//                while ((b = inputStream.read()) != -1)
//                {
//                    char ch = (char)b;
//                    // Print the character
//                    System.out.println("Char:" + ch);
//                }
                dockerClient.execStartCmd(execIdFront)
                        .exec(new ExecStartResultCallback(){})
                        .awaitCompletion(TIME_OUT, TimeUnit.SECONDS);

                dockerClient.execStartCmd(execId)
                        .withStdIn(inputStream)
                        .exec(execStartResultCallback)
                        .awaitCompletion(TIME_OUT, TimeUnit.SECONDS);

                inputStream.close();


                stopWatch.stop();
                time = stopWatch.getLastTaskTimeMillis();
                statsCmd.close();
            } catch (InterruptedException | IOException e) {
                System.out.println("程序执行异常");
                throw new RuntimeException(e);
            }
            executeMessage.setTime(time);
            executeMessageList.add(executeMessage);
        }
        dockerClient.stopContainerCmd(containerId).exec();
        dockerClient.removeContainerCmd(containerId).exec();
        return executeMessageList;
    }



}
