package com.bravo.socket_service;

/**
 * Created by admin on 2018-9-19.
 */

public class EventBusMsgRecvXmlMsg {
    private String ip;
    private int port;
    private String msg;

    public EventBusMsgRecvXmlMsg(String ip, int port, String msg) {
        this.ip = ip;
        this.port = port;
        this.msg = msg;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "EventBusMsgRecvXmlMsg{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", msg='" + msg + '\'' +
                '}';
    }
}
