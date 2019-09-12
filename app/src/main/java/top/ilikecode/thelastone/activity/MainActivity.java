package top.ilikecode.thelastone.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;
import top.ilikecode.thelastone.R;
import top.ilikecode.thelastone.handler.SendThread;
import top.ilikecode.thelastone.infos.InfoCache;
import top.ilikecode.thelastone.socket.Connect2Server;
import top.ilikecode.thelastone.utils.BitmapUtils;
import top.ilikecode.thelastone.zbar.CaptureActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private NavigationView navigationView;
    private ImageButton ibStart,ibAdd;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private static final int REQUEST_CODE_SCAN = 0x0000;// 扫描二维码
    private Connect2Server connect2Server = new Connect2Server(MainActivity.this);
    private Intent intent = new Intent();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    //TODO 初始化
    /**
     *  初始化控件，添加点击事件监听器
     *  连接服务器
     *  开启任务线程
     * */
    private void init(){
        navigationView = (NavigationView)findViewById(R.id.navigationview);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerlayout);
        ibStart = (ImageButton)findViewById(R.id.ib_start);
        ibAdd = (ImageButton)findViewById(R.id.ib_add);

        ibAdd.setOnClickListener(this);
        ibStart.setOnClickListener(this);

        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_header);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bitmap userIcon = BitmapFactory.decodeResource(getResources(),R.mipmap.smallusericon);
        userIcon = BitmapUtils.createCircleBitmap(userIcon);
        BitmapDrawable bd = new BitmapDrawable(userIcon);
        actionBar.setHomeAsUpIndicator(bd);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_settings:
                        intent.setClass(MainActivity.this,SettingsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_about:
                        intent.setClass(MainActivity.this,AboutActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_exit:     //退出
                        finish();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    //TODO 按钮点击事件
    @Override
    public void onClick(View v) {
        intent.setClass(MainActivity.this,RoomActivity.class);
        switch (v.getId()){
            case R.id.ib_start:
                InfoCache.isMaster = true;      //创建房间即为房主
                startActivity(intent);
                new Thread(new Runnable() {     //独立线程进行网络操作,连接服务器并开启接收线程
                    @Override
                    public void run() {
                        connect2Server.connect();
                        SendThread sendThread = new SendThread(connect2Server.getSocket());
                        sendThread.start();
                    }
                }).start();
                break;
            case R.id.ib_add:
                InfoCache.isMaster = false;     //加入房间为非房主
                RoomDialog roomDialog = new RoomDialog(MainActivity.this);
                roomDialog.show();
                break;
            default:
                break;
        }
    }

    //导航栏点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.header_qrcode:
                gotoQrCode();       //二维码识别
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO 跳转至二维码识别界面
    private void gotoQrCode(){
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent,REQUEST_CODE_SCAN);
    }

    //TODO 数据回传
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_SCAN:
                if (resultCode==RESULT_OK){
                    if (data!=null){
                        Bundle bundle = data.getExtras();
                        String result = bundle.getString(CaptureActivity.EXTRA_STRING);     //二维码数据
                        Toast.makeText(MainActivity.this,"二维码内容："+result,Toast.LENGTH_SHORT).show();
                        InfoCache.qrCode = result;
                        Toast.makeText(MainActivity.this,result,Toast.LENGTH_SHORT).show();
//                        result = result.substring(6);
                        if (result.length()==6){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    connect2Server.connect();
                                    SendThread sendThread = new SendThread(connect2Server.getSocket());
                                    sendThread.start();
                                    InfoCache.isMaster = false;
                                    intent.setClass(MainActivity.this,RoomActivity.class);
                                    startActivity(intent);
                                }
                            }).start();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_header,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
