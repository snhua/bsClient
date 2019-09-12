package top.ilikecode.thelastone.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IPUtils {
    private String intenetType = "";

    //TODO 判断是否联网
    public static boolean isNetWorkAvailable(Context context){
        boolean isAvailable = false ;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isAvailable()){
            isAvailable = true;
        }
        return isAvailable;
    }

    //TODO 获取本机mac地址
    public String getMacAddress(){
        String macAddress = "";
        StringBuffer buffer = new StringBuffer();
        NetworkInterface networkInterface = null;
        try{
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface==null){
                networkInterface = NetworkInterface.getByName("wlan0");
            }

            if (networkInterface==null){
                return "02:00:00:00:00:02";
            }

            byte[] addr = networkInterface.getHardwareAddress();
            for (byte b:addr){
                buffer.append(String.format("%02X:",b));
            }

            if(buffer.length()>0){
                buffer.deleteCharAt(buffer.length()-1);
            }

            macAddress = buffer.toString();
        }catch (SocketException e){
            e.printStackTrace();
            return "02:00:00:00:00:02";
        }
        return macAddress;
    }

    //TODO 获取公网IP地址和网络类型,返回公网IP
    public String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                intenetType = "数据连接";
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                intenetType = "无线网络";
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                //调用方法将int转换为地址字符串
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
            Toast.makeText(context,"当前网络未连接，请检查网络连接",Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    //TODO 获取网络类型
    public String getIntenetType(){
        return intenetType;
    }

    //TODO 将得到的int转String
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
}
