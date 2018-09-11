package com.bravo.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Jack.liao on 2017/4/20.
 */
@Entity
public class AdjacentCell {
    @Id(autoincrement = true)
    private Long id;
    private String strTech;
    private int iChannel;
    private int iRncid;
    private int iLac;
    private int iPsc;
    private int iCid;
    private int iTac;
    private int iPci;
    private boolean bCheck;
    @Generated(hash = 919719176)
    public AdjacentCell(Long id, String strTech, int iChannel, int iRncid, int iLac,
            int iPsc, int iCid, int iTac, int iPci, boolean bCheck) {
        this.id = id;
        this.strTech = strTech;
        this.iChannel = iChannel;
        this.iRncid = iRncid;
        this.iLac = iLac;
        this.iPsc = iPsc;
        this.iCid = iCid;
        this.iTac = iTac;
        this.iPci = iPci;
        this.bCheck = bCheck;
    }
    @Generated(hash = 1447068434)
    public AdjacentCell() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getStrTech() {
        return this.strTech;
    }
    public void setStrTech(String strTech) {
        this.strTech = strTech;
    }
    public int getIChannel() {
        return this.iChannel;
    }
    public void setIChannel(int iChannel) {
        this.iChannel = iChannel;
    }
    public int getIRncid() {
        return this.iRncid;
    }
    public void setIRncid(int iRncid) {
        this.iRncid = iRncid;
    }
    public int getILac() {
        return this.iLac;
    }
    public void setILac(int iLac) {
        this.iLac = iLac;
    }
    public int getIPsc() {
        return this.iPsc;
    }
    public void setIPsc(int iPsc) {
        this.iPsc = iPsc;
    }
    public int getICid() {
        return this.iCid;
    }
    public void setICid(int iCid) {
        this.iCid = iCid;
    }
    public int getITac() {
        return this.iTac;
    }
    public void setITac(int iTac) {
        this.iTac = iTac;
    }
    public int getIPci() {
        return this.iPci;
    }
    public void setIPci(int iPci) {
        this.iPci = iPci;
    }
    public boolean getBCheck() {
        return this.bCheck;
    }
    public void setBCheck(boolean bCheck) {
        this.bCheck = bCheck;
    }
}
