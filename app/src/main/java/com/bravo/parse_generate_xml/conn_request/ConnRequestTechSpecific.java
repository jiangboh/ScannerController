package com.bravo.parse_generate_xml.conn_request;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("tech-specific")
public class ConnRequestTechSpecific {
    private String imei;
    private String rscp;
    private String rssi;
    private String rsrp;

    @Override
    public String toString() {
        return "DoAuthAuthParams{" +
                "imei='" + imei + '\'' +
                ", rscp=" + rscp + '\'' +
                ", rssi=" + rssi +
                '}';
    }
    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }
    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getRscp() {
        return rscp;
    }

    public void setRscp(String rscp) {
        this.rscp = rscp;
    }

    public String getRsrp() {
        return rsrp;
    }

    public void setRsrp(String rsrp) {
        this.rsrp = rsrp;
    }
}
