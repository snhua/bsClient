package top.ilikecode.thelastone.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import top.ilikecode.thelastone.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvAbout;
    private Button btnBack,btnUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        init();
    }

    private void init(){
        tvAbout = (TextView)findViewById(R.id.about_tv);
        btnUpdate = (Button)findViewById(R.id.about_btn_update);
        btnBack = (Button)findViewById(R.id.about_btn_back);

        tvAbout.setText("本项目使用Java的socket，通过流的形式传递包含信息的对象，" +
                "通过对接收到的内容的判断来进行不同的操作，" +
                "由于Android不能在主线程内进行网络操作，" +
                "所以使用了大量的多线程操作");
        btnBack.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.about_btn_back:
                this.finish();
                break;
            case R.id.about_btn_update:
                update();
                break;
            default:
                break;
        }
    }

    //TODO 检查更新点击事件
    private void update() {
//        try{
//            String path = "47.106.82.224:8080/application/ver1/app.apk";
//            URL url = new URL(path);
//            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
//            String msg = httpURLConnection.getHeaderField(0);
//            if (msg.startsWith("HTTP/1.1 404")){
//                Toast.makeText(AboutActivity.this,"已经是最新版本!",Toast.LENGTH_SHORT).show();
//            }else{  //询问是否下载新版本
//
//            }
//        }catch (IOException e){
//            e.printStackTrace();
//        }
        Uri uri = Uri.parse("http://www.ilikecode.top:8080/application/v2.apk".trim());
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(uri);
        startActivity(intent);
    }
}
