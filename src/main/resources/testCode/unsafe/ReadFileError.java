

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * 读取服务器文件(文件信息泄露)
 */
public class Main
{
    public static void main(String[] args) throws IOException {
        //获取用户资源文件目录
        String userDir = System.getProperty("user.dir");

        String filePath = userDir + File.separator + "src/main/resources/application.yml";

        List<String> strings = Files.readAllLines(Paths.get(filePath));

        System.out.println(String.join("\n",strings));

    }
}
