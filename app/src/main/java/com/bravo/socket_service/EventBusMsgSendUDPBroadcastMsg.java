package com.bravo.socket_service;

/**
 * Created by Jack.liao on 2017/11/15.
 */

public class EventBusMsgSendUDPBroadcastMsg {
    private String ipAddress;
    private int port;
    private String msg;

    public EventBusMsgSendUDPBroadcastMsg(String ipAddress, int port, String msg) {
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
        return "EventBusMsgSendUDPBroadcastMsg{" +
                "ipAddress='" + ipAddress + '\'' +
                ", port=" + port +
                ", msg='" + msg + '\'' +
                '}';
    }
}
