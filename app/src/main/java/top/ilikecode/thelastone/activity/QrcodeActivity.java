package top.ilikecode.thelastone.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import top.ilikecode.thelastone.R;
import top.ilikecode.thelastone.infos.InfoCache;

public class QrcodeActivity extends AppCompatActivity {
    private ImageView iv_qrCode;
    private Button btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        iv_qrCode = (ImageView)findViewById(R.id.iv_qrcode);
        iv_qrCode.setImageBitmap(InfoCache.qrCodeBit);          //加载缓存区bitmap
        btnBack = (Button)findViewById(R.id.qr_btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QrcodeActivity.this.finish();
            }
        });
    }
}
