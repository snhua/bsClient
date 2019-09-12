package top.ilikecode.thelastone.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import top.ilikecode.thelastone.R;
import top.ilikecode.thelastone.infos.InfoCache;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnSensor,btnBgmusic,btnBack;
    private TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
    }

    private void init(){
        btnSensor = (Button)findViewById(R.id.set_btn_sensor);
        btnBgmusic = (Button)findViewById(R.id.set_btn_bgmusic);
        btnBack = (Button)findViewById(R.id.set_btn_back);
        tvUserName = (TextView)findViewById(R.id.set_tv_username);

        btnBgmusic.setOnClickListener(this);
        btnSensor.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        tvUserName.setOnClickListener(this);

        if (InfoCache.bgMusic){
            btnBgmusic.setText("开");
        }else{
            btnBgmusic.setText("关");
        }

        if (InfoCache.sensor){
            btnSensor.setText("开");
        }else{
            btnSensor.setText("关");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_btn_back:
                SettingsActivity.this.finish();
                break;
            case R.id.set_btn_bgmusic:
                if (btnBgmusic.getText().toString().equals("开")){
                    btnBgmusic.setText("关");
                    InfoCache.bgMusic = false;
                }else{
                    btnBgmusic.setText("开");
                    InfoCache.bgMusic = true;
                }
                break;
            case R.id.set_btn_sensor:
                if (btnSensor.getText().toString().equals("开")){
                    btnSensor.setText("关");
                    InfoCache.sensor = false;
                }else{
                    btnSensor.setText("开");
                    InfoCache.sensor = true;
                }
                break;
            case R.id.set_tv_username:
//                Dialog dialog = new Dialog(SettingsActivity.this);
//                dialog.setContentView(R.layout.user_dialog);
//                dialog.show();
                SetDialog setDialog = new SetDialog(SettingsActivity.this);
                setDialog.show();
                break;
            default:
                break;
        }
    }
}
