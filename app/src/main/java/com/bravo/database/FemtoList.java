package com.bravo.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Jack.liao on 2017/6/29.
 */
@Entity
public class FemtoList {
    @Id(autoincrement = true)
    private Long id;
    private String ip;
    private String mac;
    private int port;
    private int udpPort;
    private String SSID;
    @Generated(hash = 22142627)
    public FemtoList(Long id, String ip, String mac, int port, int udpPort,
            String SSID) {
        this.id = id;
        this.ip = ip;
        this.mac = mac;
        this.port = port;
        this.udpPort = udpPort;
        this.SSID = SSID;
    }
    @Generated(hash = 1411923083)
    public FemtoList() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getIp() {
        return this.ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getMac() {
        return this.mac;
    }
    public void setMac(String mac) {
        this.mac = mac;
    }
    public int getPort() {
        return this.port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public int getUdpPort() {
        return this.udpPort;
    }
    public void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }
    public String getSSID() {
        return this.SSID;
    }
    public void setSSID(String SSID) {
        this.SSID = SSID;
    }
}
