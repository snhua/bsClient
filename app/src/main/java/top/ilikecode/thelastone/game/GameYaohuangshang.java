package top.ilikecode.thelastone.game;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;
import top.ilikecode.thelastone.R;
import top.ilikecode.thelastone.activity.MainActivity;
import top.ilikecode.thelastone.handler.SendThread;
import top.ilikecode.thelastone.infos.InfoCache;
import top.ilikecode.thelastone.socket.Connect2Server;

public class GameYaohuangshang extends AppCompatActivity implements View.OnClickListener {
    private ImageView shzh1,shzh2,dizuo;        //色盅盖；色盅；底座
    private ImageView iv1,iv2;      //2个色子
    private Drawable p01,p02,p03,p04,p05,p06;   //1到6的点数图片
    private Drawable sh01,sh02,sh03,sh04,sh05;  //色盅摇晃动画的5种状态图片
    private Button btnBack,btnShake,btnOpen,btnRestart;
    private AnimationDrawable ad1;               //色盅的摇晃动画
    private Animation animation;                //色盅盖的渐变动画
    private SensorManagerHelper sensorHelper;  //重力感应
    private MediaPlayer music,music2;          //背景音乐
    double speed=0;                             //手机移动的速度
    public static boolean ZhongLiValue = InfoCache.sensor;     //重力感应的状态值（是否开启）
    public static boolean BGMusicValue = InfoCache.bgMusic;     //背景音乐的状态值（是否开启）
    private static final int SPEED_SHRESHOLD = 3000;    //阈值（当spend大于该值时执行相应操作）
    private Handler mHandler;
    private Connect2Server connect2Server = new Connect2Server(GameYaohuangshang.this);
    private boolean isOpen = false;
    private static final int SHAKE = 1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case SHAKE:
                    if (isOpen){
                        shzh1.clearAnimation();     //释放色盅盖的渐变动画设置
                        animation.cancel();         //关闭色盅盖的渐变动画
                        setIvVilityOutPut();        //设置色子为不可见，色盅盖设置为可见
                    }
                    isOpen = true;

                    setShzhStarts();            //开始动画
                    if (GameYaohuangshang.BGMusicValue)
                        setMediaPlayer();
                    setTimer();
                    setDice(InfoCache.dice[0],InfoCache.dice[1]);
                    setVisibility(2);
                    InfoCache.dice[0] = 0;
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_yaohuangshang);
        initView();     //视图布局
        if (InfoCache.isMaster){
            sensorHelper = new SensorManagerHelper(this);       //创建重力感应对象
            startSensorHelper();    //重力感应启动
        }else {
            setVisibility(0);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        if (InfoCache.dice[0]!=0){
                            Message msg = new Message();
                            msg.what = SHAKE;
                            handler.sendMessage(msg);
                        }
                    }
                }
            }).start();

        }
    }
    //重力感应启动
    private void startSensorHelper(){
        sensorHelper.setOnShakeListener(new SensorManagerHelper.OnShakeListener() {     //重力感应监听事件

            @Override
            public void getaSpeed(double sp) {
                speed=sp;
            }   //获取手机移动速度

            @Override
            public void onShake() {
                if(ZhongLiValue) {
                    if ((speed > SPEED_SHRESHOLD)&&(!ad1.isRunning())) {
                        InfoCache.request = 2;
                        SendThread sendThread = new SendThread(connect2Server.getSocket());
                        sendThread.start();

                        setVisibility(2);

                        setShzhStarts();        //色盅动画
                        if(BGMusicValue)
                            setMediaPlayer();   //背景音乐
                            setTimer();
                    }
                }
            }
        });
    }

    //视图布局
    private void initView() {
        btnBack = (Button) findViewById(R.id.game_btn_back);
        btnOpen = (Button)findViewById(R.id.game_btn_open);
        btnShake = (Button)findViewById(R.id.game_btn_shake);
        btnRestart = (Button)findViewById(R.id.game_btn_restart);

        btnBack.setOnClickListener(this);       //添加点击事件监听器
        btnOpen.setOnClickListener(this);
        btnShake.setOnClickListener(this);
        btnRestart.setOnClickListener(this);

        animation=AnimationUtils.loadAnimation(this,R.anim.gones);      //色盅盖的渐变动画

        music=MediaPlayer.create(this,R.raw.bgmusic);            //背景音乐1
        music2=MediaPlayer.create(this,R.raw.bgmusic);           //背景音乐2

        //构造动画所需图片
        Resources res=getResources();
        p01=res.getDrawable(R.drawable.dice_1);
        p02=res.getDrawable(R.drawable.dice_2);
        p03=res.getDrawable(R.drawable.dice_3);
        p04=res.getDrawable(R.drawable.dice_4);
        p05=res.getDrawable(R.drawable.dice_5);
        p06=res.getDrawable(R.drawable.dice_6);

        //捕捉2个色子控件并初始化
        iv1= (ImageView) findViewById(R.id.imageView1);
        iv1.setImageDrawable(p01);
        iv2= (ImageView) findViewById(R.id.imageView2);
        iv2.setImageDrawable(p01);

        shzh1= (ImageView) findViewById(R.id.iv_shzh1);         //色盅盖
        shzh2= (ImageView) findViewById(R.id.iv_shzh2);         //色盅
        dizuo= (ImageView) findViewById(R.id.ll_center);        //底座

        setAnimationDrawable();     //色盅摇晃的动画
        //摇一摇按钮的点击事件
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.game_btn_back:
                Intent intent = new Intent();
                intent.setClass(GameYaohuangshang.this, MainActivity.class);
                startActivity(intent);
                connect2Server.close();
                InfoCache.isStart = false;
                break;
            case R.id.game_btn_shake:
                setVisibility(2);
                InfoCache.request = 2;
                SendThread sendThread = new SendThread(connect2Server.getSocket());
                sendThread.start();             //发送请求码

                setShzhStarts();            //开始动画
                if (GameYaohuangshang.BGMusicValue)
                    setMediaPlayer();
                setTimer();
                break;
            case R.id.game_btn_open:
                if (InfoCache.isMaster){
                    setVisibility(3);
                }else {
                    setVisibility(0);
                }
                shzh1.startAnimation(animation);    //启动色盅盖的渐变动画
                setIvVilityOpne();      //设置色子为可见，色盅盖设置为隐藏
                //设置色子的点数
                if (InfoCache.isMaster){
                    while (true){       //等待缓存区数据刷新
                        if (InfoCache.dice[0]!=0){
                            setDice(InfoCache.dice[0],InfoCache.dice[1]);
                            break;
                        }
                    }
                }
                InfoCache.dice[0] = 0;
                break;
            case R.id.game_btn_restart:
                setVisibility(1);
                shzh1.clearAnimation();     //释放色盅盖的渐变动画设置
                animation.cancel();         //关闭色盅盖的渐变动画
                setIvVilityOutPut();        //设置色子为不可见，色盅盖设置为可见
                sensorHelper.start();      //重力感应启动
                break;
            default:
                break;
        }
    }

    /**
     *
     * @param btn 0     隐藏所有按钮
     *            1     显示摇一摇按钮
     *            2     显示打开按钮
     *            3     显示重摇按钮
     */
    private void setVisibility(int btn){
        switch (btn){
            case 0:
                btnShake.setVisibility(View.GONE);
                btnRestart.setVisibility(View.GONE);
                btnOpen.setVisibility(View.GONE);
                break;
            case 1:
                btnShake.setVisibility(View.VISIBLE);
                btnRestart.setVisibility(View.GONE);
                btnOpen.setVisibility(View.GONE);
                break;
            case 2:
                btnShake.setVisibility(View.GONE);
                btnRestart.setVisibility(View.GONE);
                btnOpen.setVisibility(View.VISIBLE);
                break;
            case 3:
                btnShake.setVisibility(View.GONE);
                btnRestart.setVisibility(View.VISIBLE);
                btnOpen.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    //定时停止动画与音乐
    public void setTimer(){
        Timer timer=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                if (music.isPlaying()||music2.isPlaying()) {//如果music与music2都处于播放状态
                    music.pause();      //music暂停
                    music2.pause();     //music2暂停
                    ad1.stop();     //色盅摇晃动画的结束
                    Message message = new Message();
                    message.what = 1;
                    mHandler.sendMessage(message);
                }else {
                    if (ad1.isRunning()) {
                        ad1.stop();     //色盅摇晃动画的结束
                        Message message = new Message();
                        message.what = 1;
                        mHandler.sendMessage(message);
                    }
                }
            }
        };
        timer.schedule(task,3000);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    setIvVilityOpne();  //设置色子为可见，色盅盖设置为隐藏
                    shzh1.setVisibility(View.VISIBLE);  //色盅盖设置为可见
                    dizuo.setVisibility(View.VISIBLE);  //底座设置为可见
                    shzh2.setVisibility(View.INVISIBLE);//色盅设置为不可见
                }
            }
        };
    }
    //设置色盅摇晃时的背景音乐
    public void setMediaPlayer(){
        if(!music.isPlaying()) {
            music.setVolume(0.75f, 0.75f);
            music.start();

            music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.start();
                    mp.setLooping(true);
                }
            });
            Timer timer = new Timer();
            final TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    music2.start();
                    music2.setVolume(0.75f, 0.75f);
                    music2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.start();
                            mp.setLooping(true);
                        }
                    });
                }
            };
            timer.schedule(task, (music.getDuration() / 2));
        }
    }
    //设置色盅摇晃动画的启动
    public void setShzhStarts(){
        setIvVilityOutPut();    //色盅摇晃的动画
        shzh1.setVisibility(View.INVISIBLE);//色盅盖设置为不可见
        dizuo.setVisibility(View.INVISIBLE);//底座设置为不可见
        shzh2.setVisibility(View.VISIBLE);  //完整色盅设置为可见
        shzh2.setImageDrawable(ad1);        //为色盅添加摇晃动画
        ad1.start();                        //动画启动
    }
    //设置色盅摇晃动画的结束
    public void setShzhStops(){
        ad1.stop();     //动画停止
        setIvVilityOpne();  //设置色子为可见，色盅盖设置为隐藏
        shzh1.setVisibility(View.VISIBLE);  //色盅盖设置为可见
        dizuo.setVisibility(View.VISIBLE);  //底座设置为可见
        shzh2.setVisibility(View.INVISIBLE);//色盅设置为不可见
    }
    //构造色盅摇晃的动画
    public void setAnimationDrawable() {
        Resources res = getResources();
        sh01 = res.getDrawable(R.drawable.shaizhong1);
        sh02 = res.getDrawable(R.drawable.shaizhong3);
        sh03 = res.getDrawable(R.drawable.shaizhong2);
        sh04 = res.getDrawable(R.drawable.shaizhong4);
        sh05 = res.getDrawable(R.drawable.shaizhong5);

        ad1 = new AnimationDrawable();
        ad1.setOneShot(false);
        int duration =50;
        ad1.addFrame(sh01,duration);
        ad1.addFrame(sh02,duration);
        ad1.addFrame(sh03,duration);
        ad1.addFrame(sh02,duration);
        ad1.addFrame(sh01,duration);
        ad1.addFrame(sh04,duration);
        ad1.addFrame(sh05,duration);
        ad1.addFrame(sh04,duration);
        ad1.addFrame(sh01,duration);
    }
    //设置色子为可见，色盅盖设置为隐藏
    public void setIvVilityOpne(){
        shzh1.setVisibility(View.GONE);
        iv1.setVisibility(View.VISIBLE);
        iv2.setVisibility(View.VISIBLE);
    }
    //设置色子为不可见，色盅盖设置为可见
    public void setIvVilityOutPut(){
        iv1.setVisibility(View.INVISIBLE);
        iv2.setVisibility(View.INVISIBLE);
        shzh1.setVisibility(View.VISIBLE);
    }

    //设置色子的点数
    public void setDice(int dice1,int dice2){
        switch (dice1) {                    //skr？？fw！！
            case 1:
                iv1.setImageDrawable(p01);
                break;
            case 2:
                iv1.setImageDrawable(p02);
                break;
            case 3:
                iv1.setImageDrawable(p03);
                break;
            case 4:
                iv1.setImageDrawable(p04);
                break;
            case 5:
                    iv1.setImageDrawable(p05);
                    break;
            case 6:
                iv1.setImageDrawable(p06);
                break;
            default:
                break;
            }

        switch (dice2) {
            case 1:
                iv2.setImageDrawable(p01);
                break;
            case 2:
                iv2.setImageDrawable(p02);
                break;
            case 3:
                iv2.setImageDrawable(p03);
                break;
            case 4:
                iv2.setImageDrawable(p04);
                break;
            case 5:
                iv2.setImageDrawable(p05);
                break;
            case 6:
                iv2.setImageDrawable(p06);
                break;
            default:
                break;
        }
    }
}
