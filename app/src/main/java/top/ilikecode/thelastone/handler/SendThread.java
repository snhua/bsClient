package top.ilikecode.thelastone.handler;

import top.ilikecode.thelastone.infos.InfoCache;
import top.ilikecode.thelastone.infos.UploadInfo;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SendThread extends Thread {
    private Socket socket;
    public SendThread(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
        try{
            UploadInfo up = new UploadInfo();
            up.setUserName(InfoCache.userName);
            up.setStart(InfoCache.isStart);
            up.setRequest(InfoCache.request);
            up.setMacAddress(InfoCache.macAddress);
            up.setIpAddress(InfoCache.ipAddress);
            ObjectOutputStream o = new ObjectOutputStream(socket.getOutputStream());
            o.writeObject(up);
            o.flush();
            reWriteCache();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //TODO 初始化缓存区数据
    private void reWriteCache(){
//        InfoCache.isStart = false;
        InfoCache.request = 0;
    }
}
