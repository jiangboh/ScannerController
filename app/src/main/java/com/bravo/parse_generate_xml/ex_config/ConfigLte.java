package com.bravo.parse_generate_xml.ex_config;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by lenovo on 2017/2/8.
 */
@XStreamAlias("lte")
public class ConfigLte {
    @XStreamAlias("epc-log-lev")
    private String epcLogLev;
    @XStreamAlias("enb-log-lev")
    private String enbLogLev;
    @XStreamAlias("pixcell-agent-log-lev")
    private String pixcellAgentLogLev;
    @XStreamAlias("monitor-ip")
    private String monitorIp;
    private String bandwidth;
    @XStreamAlias("crs-power")
    private String crsPower;
    @XStreamAlias("epc-ip")
    private String epcIp;

    @Override
    public String toString() {
        return "ConfigLte{" +
                "epcLogLev='" + epcLogLev + '\'' +
                ", enbLogLev='" + enbLogLev + '\'' +
                ", pixcellAgentLogLev='" + pixcellAgentLogLev + '\'' +
                ", monitorIp='" + monitorIp + '\'' +
                ", bandwidth='" + bandwidth + '\'' +
                ", crsPower='" + crsPower + '\'' +
                ", epcIp='" + epcIp + '\'' +
                '}';
    }

    public String getEpcLogLev() {
        return epcLogLev;
    }

    public void setEpcLogLev(String epcLogLev) {
        this.epcLogLev = epcLogLev;
    }

    public String getEnbLogLev() {
        return enbLogLev;
    }

    public void setEnbLogLev(String enbLogLev) {
        this.enbLogLev = enbLogLev;
    }

    public String getPixcellAgentLogLev() {
        return pixcellAgentLogLev;
    }

    public void setPixcellAgentLogLev(String pixcellAgentLogLev) {
        this.pixcellAgentLogLev = pixcellAgentLogLev;
    }

    public String getMonitorIp() {
        return monitorIp;
    }

    public void setMonitorIp(String monitorIp) {
        this.monitorIp = monitorIp;
    }

    public String getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(String bandwidth) {
        this.bandwidth = bandwidth;
    }

    public String getCrsPower() {
        return crsPower;
    }

    public void setCrsPower(String crsPower) {
        this.crsPower = crsPower;
    }

    public String getEpcIp() {
        return epcIp;
    }

    public void setEpcIp(String epcIp) {
        this.epcIp = epcIp;
    }
}
