package com.bravo.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import org.greenrobot.greendao.annotation.Generated;
@Entity
public class SnifferHistory {
    @Id(autoincrement = true)
    private Long id;
    private Long time;
    private String channel;
    private String band;
    private String tech;
    private String cid;
    private String rncid;
    private String mcc;
    private String mnc;
    private String bsic;
    private String rssi;
    private String lac;
    private String psc;
    private String rscp;
    private String bandwidth;
    private String tac;
    private String pci;
    private String rsrp;
    @Generated(hash = 2098031143)
    public SnifferHistory(Long id, Long time, String channel, String band,
            String tech, String cid, String rncid, String mcc, String mnc,
            String bsic, String rssi, String lac, String psc, String rscp,
            String bandwidth, String tac, String pci, String rsrp) {
        this.id = id;
        this.time = time;
        this.channel = channel;
        this.band = band;
        this.tech = tech;
        this.cid = cid;
        this.rncid = rncid;
        this.mcc = mcc;
        this.mnc = mnc;
        this.bsic = bsic;
        this.rssi = rssi;
        this.lac = lac;
        this.psc = psc;
        this.rscp = rscp;
        this.bandwidth = bandwidth;
        this.tac = tac;
        this.pci = pci;
        this.rsrp = rsrp;
    }
    @Generated(hash = 569086021)
    public SnifferHistory() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getTime() {
        return this.time;
    }
    public void setTime(Long time) {
        this.time = time;
    }
    public String getChannel() {
        return this.channel;
    }
    public void setChannel(String channel) {
        this.channel = channel;
    }
    public String getBand() {
        return this.band;
    }
    public void setBand(String band) {
        this.band = band;
    }
    public String getTech() {
        return this.tech;
    }
    public void setTech(String tech) {
        this.tech = tech;
    }
    public String getCid() {
        return this.cid;
    }
    public void setCid(String cid) {
        this.cid = cid;
    }
    public String getRncid() {
        return this.rncid;
    }
    public void setRncid(String rncid) {
        this.rncid = rncid;
    }
    public String getMcc() {
        return this.mcc;
    }
    public void setMcc(String mcc) {
        this.mcc = mcc;
    }
    public String getMnc() {
        return this.mnc;
    }
    public void setMnc(String mnc) {
        this.mnc = mnc;
    }
    public String getBsic() {
        return this.bsic;
    }
    public void setBsic(String bsic) {
        this.bsic = bsic;
    }
    public String getRssi() {
        return this.rssi;
    }
    public void setRssi(String rssi) {
        this.rssi = rssi;
    }
    public String getLac() {
        return this.lac;
    }
    public void setLac(String lac) {
        this.lac = lac;
    }
    public String getPsc() {
        return this.psc;
    }
    public void setPsc(String psc) {
        this.psc = psc;
    }
    public String getRscp() {
        return this.rscp;
    }
    public void setRscp(String rscp) {
        this.rscp = rscp;
    }
    public String getBandwidth() {
        return this.bandwidth;
    }
    public void setBandwidth(String bandwidth) {
        this.bandwidth = bandwidth;
    }
    public String getTac() {
        return this.tac;
    }
    public void setTac(String tac) {
        this.tac = tac;
    }
    public String getPci() {
        return this.pci;
    }
    public void setPci(String pci) {
        this.pci = pci;
    }
    public String getRsrp() {
        return this.rsrp;
    }
    public void setRsrp(String rsrp) {
        this.rsrp = rsrp;
    }

    @Override
    public String toString() {
        return "SnifferHistory{" +
                "id=" + id +
                ", time=" + time +
                ", channel='" + channel + '\'' +
                ", band='" + band + '\'' +
                ", tech='" + tech + '\'' +
                ", cid='" + cid + '\'' +
                ", rncid='" + rncid + '\'' +
                ", mcc='" + mcc + '\'' +
                ", mnc='" + mnc + '\'' +
                ", bsic='" + bsic + '\'' +
                ", rssi='" + rssi + '\'' +
                ", lac='" + lac + '\'' +
                ", psc='" + psc + '\'' +
                ", rscp='" + rscp + '\'' +
                ", bandwidth='" + bandwidth + '\'' +
                ", tac='" + tac + '\'' +
                ", pci='" + pci + '\'' +
                ", rsrp='" + rsrp + '\'' +
                '}';
    }
}
