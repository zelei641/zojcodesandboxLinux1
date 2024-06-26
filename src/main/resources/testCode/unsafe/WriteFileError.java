

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * 向服务器写文件(植入危险程序)
 */
public class Main
{
    public static void main(String[] args) throws IOException {
        //获取用户资源文件目录
        String userDir = System.getProperty("user.dir");

        String filePath = userDir + File.separator + "src/main/resources/asdf.bat";

        String errorProgram = "java -version 2>&1";
        Files.write(Paths.get(filePath), Arrays.asList(errorProgram));

        System.out.println("危险程序");

    }
}
