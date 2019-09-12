package top.ilikecode.thelastone.socket;

import android.content.Context;
import android.widget.Toast;
import top.ilikecode.thelastone.handler.GetThread;
import top.ilikecode.thelastone.infos.InfoCache;
import top.ilikecode.thelastone.utils.IPUtils;

import java.io.*;
import java.net.*;

public class Connect2Server {
    private static final String ip = "47.106.82.224";
//    private static final String ip = "10.0.17.42";
    private static final int port = 55211;
    private static Socket socket;
    private Context mContext;
    private static GetThread getThread;

    public Connect2Server(Context mContext){
        this.mContext = mContext;
    }

    //TODO 连接至服务器获取流，并开启接收线程
    public void connect(){
        try{
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip,port));
            getThread = new GetThread(socket,mContext);
            getThread.start();
            getAddress();
        }catch (IOException e){
            e.printStackTrace();
            if (e instanceof SocketTimeoutException){
                Toast.makeText(mContext,"连接服务器超时，正在尝试重新连接！",Toast.LENGTH_SHORT).show();
//                connect();
            }else if(e instanceof NoRouteToHostException){
                Toast.makeText(mContext,"未找到服务器或服务器维护中！",Toast.LENGTH_SHORT).show();
            }else if (e instanceof ConnectException){
                Toast.makeText(mContext,"连接异常或被拒绝，请检查！",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getAddress(){
        IPUtils ipUtils = new IPUtils();
        InfoCache.ipAddress = ipUtils.getIPAddress(mContext);       //把IP和mac存到缓存区
        InfoCache.macAddress = ipUtils.getMacAddress();
    }

    //TODO 关闭Socket连接并重置相关对象
    public void close(){
        InfoCache.isClose = true;
        try {
            socket.close();
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isClose(){
        try {
            socket.sendUrgentData(0xFF);
        } catch (IOException e) {
            return true;
        }
        return false;
    }

    public Socket getSocket(){
        return socket;
    }
}
