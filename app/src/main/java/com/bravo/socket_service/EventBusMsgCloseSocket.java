package com.bravo.socket_service;

/**
 * Created by lenovo on 2016/12/28.
 */

public class EventBusMsgCloseSocket {
    private String ipAddress;
    private int iUdpPort;

    public EventBusMsgCloseSocket(String ipAddress, int iUdpPort) {
        this.ipAddress = ipAddress;
        this.iUdpPort = iUdpPort;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getiUdpProt() {
        return iUdpPort;
    }

}
