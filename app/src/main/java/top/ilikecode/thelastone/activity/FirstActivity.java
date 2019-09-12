package top.ilikecode.thelastone.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import top.ilikecode.thelastone.R;

import java.util.Timer;
import java.util.TimerTask;

public class FirstActivity extends AppCompatActivity {
    private boolean isOpen = false;     //App是否已经启动过
    private int time = 3;
    private Button btnStepOver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(flag,flag);                            //全屏显示欢迎界面
        setContentView(R.layout.activity_first);

        jump2MainActivity();
        init();
    }

    private void init(){
        btnStepOver = (Button) findViewById(R.id.step_over);
        btnStepOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOpen = true;
                Intent intent = new Intent();
                intent.setClass(FirstActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    TimerTask changeText = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(time>=0){
                        btnStepOver.setText(String.valueOf(time)+"S");
                        time--;
                    }
                }
            });
        }
    };

    TimerTask jump = new TimerTask() {
        @Override
        public void run() {
            if (!isOpen){
                Intent intent = new Intent();
                intent.setClass(FirstActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };

    private void jump2MainActivity(){
        Timer timer = new Timer();
        timer.schedule(jump,4000);
        Timer wait = new Timer();
        wait.schedule(changeText,0,1000);       //等待0s开始changeText之后每s减一次
    }
}
