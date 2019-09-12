package top.ilikecode.thelastone.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import top.ilikecode.thelastone.R;
import top.ilikecode.thelastone.ZXing.ZXingUtil;
import top.ilikecode.thelastone.game.GameYaohuangshang;
import top.ilikecode.thelastone.handler.SendThread;
import top.ilikecode.thelastone.socket.Connect2Server;
import top.ilikecode.thelastone.infos.InfoCache;

import java.util.Timer;
import java.util.TimerTask;

public class RoomActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnStart,btnMakeQrcode,btnBack;
    private TextView tvPlayerNames;
    private SendThread sendThread;
    private Intent intent = new Intent();
    private Connect2Server connect2Server = new Connect2Server(RoomActivity.this);
    private boolean isClose = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        init();

//        TimerTask UIHandler = new TimerTask() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    int playerNum = 1;
//                    @Override
//                    public void run() {
//                        boolean isSet = false;
//                        String users = "当前玩家：\n";
//                        if (InfoCache.playerNames != null && InfoCache.playerNames.size() != playerNum) {
//                            for (String s : InfoCache.playerNames) {
//                                users = users + s + "\n";
//                            }
//                            tvPlayerNames.setText(users);
//                            playerNum = InfoCache.playerNames.size();
//                        } else if (InfoCache.playerNames == null) {
//                            if (!isSet) {
//                                users = users + InfoCache.userName + "\n";
//                                tvPlayerNames.setText(users);
//                                isSet = true;
//                            }
//                        }else if (InfoCache.isStart) {
//                            return;
//                        }
//                    }
//                });
//            }
//        };
//
//        Timer timer = new Timer();
//        timer.schedule(UIHandler,0,100);
    }

    private void init(){
        btnStart = (Button)findViewById(R.id.btn_start);
        btnBack = (Button)findViewById(R.id.btn_back);
        btnMakeQrcode = (Button)findViewById(R.id.btn_makeQrcode);
        tvPlayerNames = (TextView)findViewById(R.id.tv_usernames);

        btnMakeQrcode.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        if (!InfoCache.isMaster){
            btnStart.setVisibility(View.GONE);
            new Thread(new Runnable() {             //开启线程监听游戏开始
                @Override
                public void run() {
                    while (true){
                        if (InfoCache.isStart){
                            intent.setClass(RoomActivity.this,GameYaohuangshang.class);
                            startActivity(intent);
                            break;
                        }else if (isClose){
                            break;
                        }
                    }
                }
            }).start();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                isClose = true;
                RoomActivity.this.finish();
                connect2Server.close();
                break;
            case R.id.btn_makeQrcode:
                int wh = getWindowManager().getDefaultDisplay().getWidth()-40;      //调整二维码大小适应屏幕
                if (InfoCache.qrCodeBit==null){
                    InfoCache.request = 1;
                    sendThread = new SendThread(connect2Server.getSocket());
                    sendThread.start();
                    while (true){
                        if (!InfoCache.roomId.equals("")){      //等待服务器房间号,等待get线程刷新数据
                            InfoCache.qrCodeBit = ZXingUtil.createQRCodeBitmap(InfoCache.roomId,wh,wh,"utf-8",
                                    "H","1", Color.BLACK,Color.WHITE);
                            intent.setClass(RoomActivity.this,QrcodeActivity.class);
                            startActivity(intent);
                            break;
                        }
                    }
                }else{
                    intent.setClass(RoomActivity.this,QrcodeActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.btn_start:
                InfoCache.request = 3;
                sendThread = new SendThread(connect2Server.getSocket());
                sendThread.start();
                intent.setClass(RoomActivity.this, GameYaohuangshang.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

}
