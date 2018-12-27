package com.bravo.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

/**
 * Created by admin on 2018-11-2.
 */
@Entity
public class BlackWhiteImsi {
    @Id(autoincrement = true)
    private Long Id;
    @Transient
    public static final int WHITE = 0;
    @Transient
    public static final int BLACK = 1;
    @Transient
    private boolean isChecked = false; // 是否选中CheckBox
    private String Name;
    private String Imsi;
    private String Imei;
    private String Tmsi;
    private int StartRb=-1;
    private int StopRb=-1;
    private int Type; //名单类型：0白名单；1黑名单

    @Generated(hash = 819069845)
    public BlackWhiteImsi(Long Id, String Name, String Imsi, String Imei,
            String Tmsi, int StartRb, int StopRb, int Type) {
        this.Id = Id;
        this.Name = Name;
        this.Imsi = Imsi;
        this.Imei = Imei;
        this.Tmsi = Tmsi;
        this.StartRb = StartRb;
        this.StopRb = StopRb;
        this.Type = Type;
    }

    @Generated(hash = 213742470)
    public BlackWhiteImsi() {
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
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
