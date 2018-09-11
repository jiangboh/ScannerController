package com.bravo.parse_generate_xml.ex_config;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by lenovo on 2017/2/8.
 */
@XStreamAlias("general")
public class ConfigGeneral {
    @XStreamAlias("fan-speed")
    private String fanSpeed;
    @XStreamAlias("hw-report-interval")
    private String hwReportInterval;
    @XStreamAlias("kpi-interval")
    private String kpiInterval;
    private String time;
    @XStreamAlias("time-zone")
    private String timeZone;
    @XStreamAlias("watch-dog")
    private String watchDog;
    @XStreamAlias("auto-reboot")
    private String autoReboot;
    private Radio radio;
    @XStreamAlias("cell-sel-alg")
    private String cellSelAlg;

    static public class Radio{
        @XStreamAlias("tx-s")
        private String txS;
        @XStreamAlias("tx-f")
        private String txF;

        @Override
        public String toString() {
            return "Radio{" +
                    "txS='" + txS + '\'' +
                    ", txF='" + txF + '\'' +
                    '}';
        }

        public String getTxS() {
            return txS;
        }

        public void setTxS(String txS) {
            this.txS = txS;
        }

        public String getTxF() {
            return txF;
        }

        public void setTxF(String txF) {
            this.txF = txF;
        }
    }

    @Override
    public String toString() {
        return "ConfigGeneral{" +
                "fanSpeed='" + fanSpeed + '\'' +
                ", hwReportInterval='" + hwReportInterval + '\'' +
                ", kpiInterval='" + kpiInterval + '\'' +
                ", time='" + time + '\'' +
                ", timeZone='" + timeZone + '\'' +
                ", watchDog='" + watchDog + '\'' +
                ", autoReboot='" + autoReboot + '\'' +
                ", radio=" + radio +
                ", cellSelAlg='" + cellSelAlg + '\'' +
                '}';
    }

    public String getCellSelAlg() {
        return cellSelAlg;
    }

    public void setCellSelAlg(String cellSelAlg) {
        this.cellSelAlg = cellSelAlg;
    }

    public String getFanSpeed() {
        return fanSpeed;
    }

    public void setFanSpeed(String fanSpeed) {
        this.fanSpeed = fanSpeed;
    }

    public String getHwReportInterval() {
        return hwReportInterval;
    }

    public void setHwReportInterval(String hwReportInterval) {
        this.hwReportInterval = hwReportInterval;
    }

    public String getKpiInterval() {
        return kpiInterval;
    }

    public void setKpiInterval(String kpiInterval) {
        this.kpiInterval = kpiInterval;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getWatchDog() {
        return watchDog;
    }

    public void setWatchDog(String watchDog) {
        this.watchDog = watchDog;
    }

    public String getAutoReboot() {
        return autoReboot;
    }

    public void setAutoReboot(String autoReboot) {
        this.autoReboot = autoReboot;
    }

    public Radio getRadio() {
        return radio;
    }

    public void setRadio(Radio radio) {
        this.radio = radio;
    }
}
