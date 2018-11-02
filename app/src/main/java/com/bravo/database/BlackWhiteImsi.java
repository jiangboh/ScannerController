package com.bravo.database;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by admin on 2018-11-2.
 */

public class BlackWhiteImsi {
    @Id(autoincrement = true)
    private Long Id;
    private String Name;
    private String Imsi;
    private String Imei;
    private String Tmsi;
    private int StartRb;
    private int StopRb;
    private int Type; //名单类型：0白名单；1黑名单

    @Generated(hash = 1719352708)
    public BlackWhiteImsi(Long id, String name, String imsi, String imei, String tmsi, int startRb, int stopRb, int type) {
        this.Id = id;
        this.Name = name;
        this.Imsi = imsi;
        this.Imei = imei;
        this.Tmsi = tmsi;
        this.StartRb = startRb;
        this.StopRb = stopRb;
        this.Type = type;
    }

    @Generated(hash = 1719352558)
    public BlackWhiteImsi() {

    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        this.Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getImsi() {
        return Imsi;
    }

    public void setImsi(String imsi) {
        this.Imsi = imsi;
    }

    public String getImei() {
        return Imei;
    }

    public void setImei(String imei) {
        this.Imei = imei;
    }

    public String getTmsi() {
        return Tmsi;
    }

    public void setTmsi(String tmsi) {
        this.Tmsi = tmsi;
    }

    public int getStartRb() {
        return StartRb;
    }

    public void setStartRb(int startRb) {
        this.StartRb = startRb;
    }

    public int getStopRb() {
        return StopRb;
    }

    public void setStopRb(int stopRb) {
        this.StopRb = stopRb;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        this.Type = type;
    }

    @Override
    public String toString() {
        return "BlackWhiteImsi{" +
                "Id=" + Id +
                ", Name='" + Name + '\'' +
                ", Imsi='" + Imsi + '\'' +
                ", Imei='" + Imei + '\'' +
                ", Tmsi='" + Tmsi + '\'' +
                ", StartRb=" + StartRb +
                ", StopRb=" + StopRb +
                ", Type=" + Type +
                '}';
    }
}
