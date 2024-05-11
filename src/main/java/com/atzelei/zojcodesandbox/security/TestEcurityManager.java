package com.atzelei.zojcodesandbox.security;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TestEcurityManager
{
    public static void main(String[] args) {
        System.setSecurityManager(new MySecurityManager());

        List<String> strings = FileUtil.readLines("D:\\Javaideaporject\\zoj-code-sandbox\\src\\main\\resources\\asdf.bat", StandardCharsets.UTF_8);

        System.out.println("strings = " + strings);

    }
}
