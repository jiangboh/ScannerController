package com.bravo.parse_generate_xml.target_position;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("tech-specific")
public class TargetPositionTechSpecific {
    private int rscp;
    private int rsrp;
    private int rssi;

    @Override
    public String toString() {
        return "TargetPositionTechSpecific{" +
                "rscp=" + rscp +
                ", rsrp=" + rsrp +
                ", rssi=" + rssi +
                '}';
    }

    public int getRscp() {
        return rscp;
    }

    public void setRscp(int rscp) {
        this.rscp = rscp;
    }

    public int getRsrp() {
        return rsrp;
    }

    public void setRsrp(int rsrp) {
        this.rsrp = rsrp;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
