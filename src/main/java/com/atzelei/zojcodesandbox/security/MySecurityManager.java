package com.atzelei.zojcodesandbox.security;

import java.rmi.server.ServerCloneException;
import java.security.Permission;

/**
 * 默认安全管理器
 */
public class MySecurityManager extends SecurityManager
{

    //检查所有的权限
    @Override
    public void checkPermission(Permission perm) {
        //全部开启
       // super.checkPermission(perm);
    }

    //检测程序是否可执行
    @Override
    public void checkExec(String cmd) {
        throw new SecurityException("权限异常" + cmd);
    }

    //检测程序是否允许读文件
    @Override
    public void checkRead(String file) {
        throw new SecurityException("权限异常" + file);
    }

    //检测程序是否允许写文件
    @Override
    public void checkWrite(String file) {
        throw new SecurityException("权限异常" + file);
    }

    //检测程序是否允许删除文件
    @Override
    public void checkDelete(String file) {
        throw new SecurityException("权限异常" + file);
    }

    //检测程序是否允许链接网络
    @Override
    public void checkConnect(String host, int port) {
        throw new SecurityException("权限异常" + host + ":" + port);
    }
}
