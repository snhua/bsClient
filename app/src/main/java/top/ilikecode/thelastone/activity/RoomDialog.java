package top.ilikecode.thelastone.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import top.ilikecode.thelastone.R;
import top.ilikecode.thelastone.game.GameYaohuangshang;
import top.ilikecode.thelastone.handler.SendThread;
import top.ilikecode.thelastone.socket.Connect2Server;

public class RoomDialog extends Dialog{
    private EditText et_set;
    private Button btnok,btncancel;
    private Context mContext;

    public RoomDialog(Context context) {
        super(context);
        mContext = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_dialog);
        setView();
    }

    private void setView() {
        et_set=findViewById(R.id.et_set);
        btnok=findViewById(R.id.btn_ok);
        btncancel=findViewById(R.id.btn_cancel);

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomId = et_set.getText().toString().trim();
                if (roomId.length()==6){
                    new Thread(new Runnable() {     //同上
                        @Override
                        public void run() {
                            connect();
                        }
                    }).start();
                    hide();
                }else{
                    Toast.makeText(mContext,"请输入6位房间号",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
    }

    private void connect(){
        Connect2Server connect2Server = new Connect2Server(mContext);
        connect2Server.connect();

        SendThread sendThread = new SendThread(connect2Server.getSocket());
        sendThread.start();

        Intent intent = new Intent();
        intent.setClass(mContext, GameYaohuangshang.class);
        mContext.startActivity(intent);
    }
}
