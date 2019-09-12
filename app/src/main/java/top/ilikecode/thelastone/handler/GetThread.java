package top.ilikecode.thelastone.handler;


import android.content.Context;
import top.ilikecode.thelastone.infos.InfoCache;
import top.ilikecode.thelastone.infos.DownloadInfo;
import top.ilikecode.thelastone.socket.Connect2Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

//负责获取信息并处理的线程
public class GetThread extends Thread{
    private Socket socket;
    private Context context;

    public GetThread(Socket socket, Context mContext){
        this.socket = socket;
        this.context = mContext;
    }

    @Override
    public void run() {
        try {
            socket.setSoTimeout(3000);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        while (!isClose()){
            try {
                ObjectInputStream i = new ObjectInputStream(socket.getInputStream());
                DownloadInfo down = (DownloadInfo) i.readObject();
                InfoCache.dice = down.getDice();
                InfoCache.isStart = down.isStart();
                InfoCache.playerNames = down.getPlayerNames();
                InfoCache.roomId = down.getRoomId();
            } catch (IOException e) {
                if (e instanceof SocketTimeoutException){           //捕捉超时
                    run();
                    return;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (InfoCache.isClose){
            this.interrupt();
        }else {
            Connect2Server connect2Server = new Connect2Server(context);        //断开连接重新连接
            connect2Server.connect();
        }
    }

    //TODO 判断是否断开连接
    private boolean isClose(){
        try {
            socket.sendUrgentData(0xFF);
        } catch (IOException e) {
//            Toast.makeText(context,"与服务器断开连接，正在尝试重新连接",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
