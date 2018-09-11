package com.bravo.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Jack.liao on 2017/1/5.
 */
@Entity
public class User {
    @Id(autoincrement = true)
    private Long id;
    private String srtImsi;
    private String strImei;
    private int iAuth;
    private boolean bSilent;
    private int iCount;
    private Long ConnTime;
    private Long AttachTime;
    private Long DetachTime;
    private String Unique;
    @Generated(hash = 204055469)
    public User(Long id, String srtImsi, String strImei, int iAuth, boolean bSilent,
            int iCount, Long ConnTime, Long AttachTime, Long DetachTime,
            String Unique) {
        this.id = id;
        this.srtImsi = srtImsi;
        this.strImei = strImei;
        this.iAuth = iAuth;
        this.bSilent = bSilent;
        this.iCount = iCount;
        this.ConnTime = ConnTime;
        this.AttachTime = AttachTime;
        this.DetachTime = DetachTime;
        this.Unique = Unique;
    }
    @Generated(hash = 586692638)
    public User() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getSrtImsi() {
        return this.srtImsi;
    }
    public void setSrtImsi(String srtImsi) {
        this.srtImsi = srtImsi;
    }
    public String getStrImei() {
        return this.strImei;
    }
    public void setStrImei(String strImei) {
        this.strImei = strImei;
    }
    public int getIAuth() {
        return this.iAuth;
    }
    public void setIAuth(int iAuth) {
        this.iAuth = iAuth;
    }
    public boolean getBSilent() {
        return this.bSilent;
    }
    public void setBSilent(boolean bSilent) {
        this.bSilent = bSilent;
    }
    public int getICount() {
        return this.iCount;
    }
    public void setICount(int iCount) {
        this.iCount = iCount;
    }
    public Long getConnTime() {
        return this.ConnTime;
    }
    public void setConnTime(Long ConnTime) {
        this.ConnTime = ConnTime;
    }
    public Long getAttachTime() {
        return this.AttachTime;
    }
    public void setAttachTime(Long AttachTime) {
        this.AttachTime = AttachTime;
    }
    public Long getDetachTime() {
        return this.DetachTime;
    }
    public void setDetachTime(Long DetachTime) {
        this.DetachTime = DetachTime;
    }
    public String getUnique() {
        return this.Unique;
    }
    public void setUnique(String Unique) {
        this.Unique = Unique;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", srtImsi='" + srtImsi + '\'' +
                ", strImei='" + strImei + '\'' +
                ", iAuth=" + iAuth +
                ", bSilent=" + bSilent +
                ", iCount=" + iCount +
                ", ConnTime=" + ConnTime +
                ", AttachTime=" + AttachTime +
                ", DetachTime=" + DetachTime +
                ", Unique='" + Unique + '\'' +
                '}';
    }
}
