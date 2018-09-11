package com.bravo.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Jack.liao on 2017/6/14.
 */
@Entity
public class BcastHistory {
    @Id(autoincrement = true)
    private Long id;
    private String FemtoSn;
    private String FemtoVer;
    private String MacAddress;
    private Long statrtime;
    private Long endtime;
    private String Tech;
    private String Band;
    private String Channel;
    private String Cid;
    private boolean bRealEnd;
    @Generated(hash = 1719352706)
    public BcastHistory(Long id, String FemtoSn, String FemtoVer, String MacAddress,
            Long statrtime, Long endtime, String Tech, String Band, String Channel,
            String Cid, boolean bRealEnd) {
        this.id = id;
        this.FemtoSn = FemtoSn;
        this.FemtoVer = FemtoVer;
        this.MacAddress = MacAddress;
        this.statrtime = statrtime;
        this.endtime = endtime;
        this.Tech = Tech;
        this.Band = Band;
        this.Channel = Channel;
        this.Cid = Cid;
        this.bRealEnd = bRealEnd;
    }
    @Generated(hash = 870344869)
    public BcastHistory() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFemtoSn() {
        return this.FemtoSn;
    }
    public void setFemtoSn(String FemtoSn) {
        this.FemtoSn = FemtoSn;
    }
    public String getFemtoVer() {
        return this.FemtoVer;
    }
    public void setFemtoVer(String FemtoVer) {
        this.FemtoVer = FemtoVer;
    }
    public String getMacAddress() {
        return this.MacAddress;
    }
    public void setMacAddress(String MacAddress) {
        this.MacAddress = MacAddress;
    }
    public Long getStatrtime() {
        return this.statrtime;
    }
    public void setStatrtime(Long statrtime) {
        this.statrtime = statrtime;
    }
    public Long getEndtime() {
        return this.endtime;
    }
    public void setEndtime(Long endtime) {
        this.endtime = endtime;
    }
    public String getTech() {
        return this.Tech;
    }
    public void setTech(String Tech) {
        this.Tech = Tech;
    }
    public String getBand() {
        return this.Band;
    }
    public void setBand(String Band) {
        this.Band = Band;
    }
    public String getChannel() {
        return this.Channel;
    }
    public void setChannel(String Channel) {
        this.Channel = Channel;
    }
    public String getCid() {
        return this.Cid;
    }
    public void setCid(String Cid) {
        this.Cid = Cid;
    }
    public boolean getBRealEnd() {
        return this.bRealEnd;
    }
    public void setBRealEnd(boolean bRealEnd) {
        this.bRealEnd = bRealEnd;
    }

    @Override
    public String toString() {
        return "BcastHistory{" +
                "id=" + id +
                ", FemtoSn='" + FemtoSn + '\'' +
                ", FemtoVer='" + FemtoVer + '\'' +
                ", MacAddress='" + MacAddress + '\'' +
                ", statrtime=" + statrtime +
                ", endtime=" + endtime +
                ", Tech='" + Tech + '\'' +
                ", Band='" + Band + '\'' +
                ", Channel='" + Channel + '\'' +
                ", Cid='" + Cid + '\'' +
                ", bRealEnd=" + bRealEnd +
                '}';
    }
}
