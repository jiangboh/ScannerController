package com.bravo.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Jack.liao on 2017/4/20.
 */
@Entity
public class TargetUser {
    @Id(autoincrement = true)
    private Long id;
    private String strImsi;
    private String strImei;
    private String strName;
    private boolean bCheck;
    private String strTech;
    private String strBand;
    private String strChannel;
    private boolean bRedir;
    @Generated(hash = 1527772843)
    public TargetUser(Long id, String strImsi, String strImei, String strName,
            boolean bCheck, String strTech, String strBand, String strChannel,
            boolean bRedir) {
        this.id = id;
        this.strImsi = strImsi;
        this.strImei = strImei;
        this.strName = strName;
        this.bCheck = bCheck;
        this.strTech = strTech;
        this.strBand = strBand;
        this.strChannel = strChannel;
        this.bRedir = bRedir;
    }
    @Generated(hash = 77665884)
    public TargetUser() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getStrImsi() {
        return this.strImsi;
    }
    public void setStrImsi(String strImsi) {
        this.strImsi = strImsi;
    }
    public String getStrImei() {
        return this.strImei;
    }
    public void setStrImei(String strImei) {
        this.strImei = strImei;
    }
    public String getStrName() {
        return this.strName;
    }
    public void setStrName(String strName) {
        this.strName = strName;
    }
    public boolean getBCheck() {
        return this.bCheck;
    }
    public void setBCheck(boolean bCheck) {
        this.bCheck = bCheck;
    }
    public String getStrTech() {
        return this.strTech;
    }
    public void setStrTech(String strTech) {
        this.strTech = strTech;
    }
    public String getStrBand() {
        return this.strBand;
    }
    public void setStrBand(String strBand) {
        this.strBand = strBand;
    }
    public String getStrChannel() {
        return this.strChannel;
    }
    public void setStrChannel(String strChannel) {
        this.strChannel = strChannel;
    }
    public boolean getBRedir() {
        return this.bRedir;
    }
    public void setBRedir(boolean bRedir) {
        this.bRedir = bRedir;
    }
}
