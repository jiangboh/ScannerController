package com.bravo.parse_generate_xml.ex_status;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("hw")
public class StatusNotifHw {
    private String cpu;
    private String mem;
    private String load;
    private String flash;
    private String temperature;
    @XStreamAlias("battery-level")
    private String batteryLevel;
    @XStreamAlias("battery-charging")
    private String batteryCharging;
    private Radio radio;

    public class Radio{
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
        return "StatusNotifHw{" +
                "cpu='" + cpu + '\'' +
                ", mem='" + mem + '\'' +
                ", load='" + load + '\'' +
                ", flash='" + flash + '\'' +
                ", temperature='" + temperature + '\'' +
                ", batteryLevel='" + batteryLevel + '\'' +
                ", batteryCharging='" + batteryCharging + '\'' +
                ", radio=" + radio +
                '}';
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getMem() {
        return mem;
    }

    public void setMem(String mem) {
        this.mem = mem;
    }

    public String getLoad() {
        return load;
    }

    public void setLoad(String load) {
        this.load = load;
    }

    public String getFlash() {
        return flash;
    }

    public void setFlash(String flash) {
        this.flash = flash;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(String batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public String getBatteryCharging() {
        return batteryCharging;
    }

    public void setBatteryCharging(String batteryCharging) {
        this.batteryCharging = batteryCharging;
    }

    public Radio getRadio() {
        return radio;
    }

    public void setRadio(Radio radio) {
        this.radio = radio;
    }
}
