package com.bravo.socket_service;

/**
 * Created by lenovo on 2016/12/28.
 */

public class EventBusMsgSocketDisconnect {
    private String ipAddress;
    private int port;
    private String type;

    public EventBusMsgSocketDisconnect(String ipAddress, int port,String type) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "EventBusMsgSocketDisconnect{" +
                "ipAddress='" + ipAddress + '\'' +
                ", port=" + port +
                ", type='" + type + '\'' +
                '}';
    }
}
