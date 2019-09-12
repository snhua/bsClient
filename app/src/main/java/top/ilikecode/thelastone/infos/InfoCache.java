package top.ilikecode.thelastone.infos;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class InfoCache {
    public volatile static int[] dice = {0,0};
    public static int request = 0;          //请求码
    public static String roomId = "";                //房间号
    public static String userName = "usernameaaa";    //自己名称
    public static ArrayList<String> playerNames = null;  //其他玩家名称
    public static String ipAddress = "";
    public static String macAddress = "";
    public static boolean isMaster = false;             //是否房主
    public static boolean isStart = false;      //游戏是否已经开始
    public static Bitmap qrCodeBit = null;        //二维码位图
    public static String qrCode = "";           //二维码内容
    public static boolean sensor = true;
    public static boolean bgMusic = true;
    public static boolean isClose = false;          //断开连接标志
}
