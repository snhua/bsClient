package top.ilikecode.thelastone.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import top.ilikecode.thelastone.R;
import top.ilikecode.thelastone.infos.InfoCache;

public class SetDialog extends Dialog{
    private EditText etNewName;
    private Button btnOk,btnCancel;
    private Context mContext;
    public SetDialog(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init(){
        setContentView(R.layout.user_dialog);
        btnOk = (Button)findViewById(R.id.user_btn_ok);
        btnCancel = (Button)findViewById(R.id.user_btn_cancel);
        etNewName = (EditText)findViewById(R.id.user_et_newname);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etNewName.getText().toString().equals("")){
                    InfoCache.userName = etNewName.getText().toString();
                    hide();
                }else{
                    Toast.makeText(mContext,"请输入用户名",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
