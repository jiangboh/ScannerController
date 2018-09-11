package com.bravo.socket_service;

/**
 * Created by lenovo on 2016/12/28.
 */

public class EventBusMsgSendTCPMsg {
    private String ipAddress;
    private int port;
    private String msg;

    public EventBusMsgSendTCPMsg(String ipAddress, int port, String msg) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.msg = msg;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "EventBusMsgSendTCPMsg{" +
                "ipAddress='" + ipAddress + '\'' +
                ", port=" + port +
                ", msg='" + msg + '\'' +
                '}';
    }
}
