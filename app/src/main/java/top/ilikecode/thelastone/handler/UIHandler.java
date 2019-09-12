package top.ilikecode.thelastone.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class UIHandler extends Thread {
    private Handler mHandler;
    @Override
    public void run() {
        Looper.prepare();
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

            }
        };
    }
}
