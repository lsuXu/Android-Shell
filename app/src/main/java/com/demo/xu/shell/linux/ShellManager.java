package com.demo.xu.shell.linux;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * 管理Shell进程，固定时间未被使用的进程将被关闭回收
 * Created by 12852 on 2018/8/1.
 */

public final class ShellManager{

    private static final String TAG = ShellManager.class.getSimpleName();

    //进程构造器
    private ProcessBuilder processBuilder ;
    //设置进程工作的初始目录
    private String rootFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() ;
    //被管理的进程
    private ShellProcess shellProcess;

    //初始化
    private ShellManager(){
        processBuilder = new ProcessBuilder("sh");
        //设置Shell进程初始路径
        processBuilder.directory(new File(rootFilePath));
        //合并结果输出流和错误结果输出流
        processBuilder.redirectErrorStream(true);
    }

    private static final ShellManager instance = new ShellManager();

    public static ShellManager getInstance(){
        return instance ;
    }

    //获取连接
    public synchronized ShellProcess getConnected() throws IOException {

        if(shellProcess == null || shellProcess.isClose()){
            //创建一个新的Linux窗口进程
            shellProcess = new ShellProcess(processBuilder.start());
        }

        return shellProcess ;
    }

}
