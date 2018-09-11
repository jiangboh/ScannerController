package com.bravo.parse_generate_xml.target_list;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by Jack.liao on 2017/7/18.
 */
@XStreamAlias("target")
public class Target {
    private String imsi;
    private String imei;
    @XStreamAlias("redir")
    private TargetRedir targetRedir;

    @Override
    public String toString() {
        return "Target{" +
                "imsi='" + imsi + '\'' +
                ", imei='" + imei + '\'' +
                ", targetRedir=" + targetRedir +
                '}';
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public TargetRedir getTargetRedir() {
        return targetRedir;
    }

    public void setTargetRedir(TargetRedir targetRedir) {
        this.targetRedir = targetRedir;
    }
}
