package top.ilikecode.thelastone.infos;

import java.io.Serializable;

public class UploadInfo implements Serializable {
    private int request;
    private String userName;
    private String macAddress;
    private String ipAddress;
    private boolean isStart;

    public void setRequest(int request) {
        this.request = request;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public int getRequest() {
        return request;
    }

    public String getUserName() {
        return userName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public boolean isStart() {
        return isStart;
    }
}
