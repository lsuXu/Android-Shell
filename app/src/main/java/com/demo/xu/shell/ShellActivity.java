package com.demo.xu.shell;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.demo.xu.shell.linux.ShellManager;
import com.demo.xu.shell.linux.ShellProcess;

import java.io.IOException;

public class ShellActivity extends AppCompatActivity {

    private static final String TAG = ShellActivity.class.getSimpleName();

    private static final String END_FLAG = ">";

    private EditText editText ;

    private ScrollView scrollView ;

    private RelativeLayout relativeLayout ;

    private TextView showTV ,pathTV;

    private ShellProcess shellProcess ;

    private Handler handler ;

    private ShellProcess.ExecuteCallback executeCallback ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shell);
        scrollView = findViewById(R.id.scrollView);
        relativeLayout = findViewById(R.id.view_content);
        editText = findViewById(R.id.et_input);
        showTV = findViewById(R.id.tv_show);
        pathTV = findViewById(R.id.tv_path);
        showTV.setMovementMethod(ScrollingMovementMethod.getInstance());
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //非输入键触发
                if(event == null){
                    //获取指令
                    String cmd = v.getText().toString();
                    appendMessage(END_FLAG + cmd  + "\n\n");
                    shellProcess.execute(cmd ,executeCallback);
                    //若shell程序已经关闭，则退出，否则获取shell的当前工作路径
                    if(!shellProcess.isClose()) {
                        shellProcess.execute("pwd", executeCallback);
                    }else{
                        //shell进程已经关闭，退出程序
                        finish();
                    }
                    //清除输入
                    v.setText("");
                }
                return false;
            }
        });

        init();

    }

    private void init(){
        //初始化handler
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1){
                    String appendString = pathTV.getText().toString();
                    StringBuffer text = new StringBuffer(String.valueOf(msg.obj));
                    text.setLength(text.length() -1);
                    showTV.append(appendString);
                    pathTV.setText(text.toString());
                    scrollView.smoothScrollTo(0,relativeLayout.getBottom());
                }
            }
        };

        //初始化linux命令执行回调
        executeCallback = new ShellProcess.ExecuteCallback() {
            @Override
            public void executeResult(String result) {
                appendMessage(result);
            }
        };

        try {
            shellProcess = ShellManager.getInstance().getConnected();
            shellProcess.execute("pwd",executeCallback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendMessage(String info ){
        Message message = Message.obtain();
        message.what = 1 ;
        message.obj = info ;
        handler.sendMessage(message);
    }


}
