package top.ilikecode.thelastone.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import top.ilikecode.thelastone.socket.Connect2Server;

public class SocketListener extends Service {
    public SocketListener() {

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
//        Connect2Server.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
