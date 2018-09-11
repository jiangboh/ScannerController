package com.bravo.parse_generate_xml.ex_status;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("basic")
public class StatusNotifBasic {
    private String sn;
    private String mac;
    private String rat;
    private String band;
    @XStreamAlias("antenna-gain")
    private String antennaGain;
    @XStreamAlias("hw-ver")
    private String hwVer;
    @XStreamAlias("sw-ver")
    private StatusSWVersion swVer;
    private String mode;
    @XStreamAlias("up-time")
    private String upTime;
    @XStreamAlias("cell-sel-alg")
    private String cellSelAlg;

    @Override
    public String toString() {
        return "StatusNotifBasic{" +
                "sn='" + sn + '\'' +
                ", mac='" + mac + '\'' +
                ", rat='" + rat + '\'' +
                ", band='" + band + '\'' +
                ", antennaGain='" + antennaGain + '\'' +
                ", hwVer='" + hwVer + '\'' +
                ", swVer=" + swVer +
                ", mode='" + mode + '\'' +
                ", upTime='" + upTime + '\'' +
                ", cellSelAlg='" + cellSelAlg + '\'' +
                '}';
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getRat() {
        return rat;
    }

    public void setRat(String rat) {
        this.rat = rat;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public String getAntennaGain() {
        return antennaGain;
    }

    public void setAntennaGain(String antennaGain) {
        this.antennaGain = antennaGain;
    }

    public String getHwVer() {
        return hwVer;
    }

    public void setHwVer(String hwVer) {
        this.hwVer = hwVer;
    }

    public StatusSWVersion getSwVer() {
        return swVer;
    }

    public void setSwVer(StatusSWVersion swVer) {
        this.swVer = swVer;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getUpTime() {
        return upTime;
    }

    public void setUpTime(String upTime) {
        this.upTime = upTime;
    }

    public String getCellSelAlg() {
        return cellSelAlg;
    }

    public void setCellSelAlg(String cellSelAlg) {
        this.cellSelAlg = cellSelAlg;
    }
}
