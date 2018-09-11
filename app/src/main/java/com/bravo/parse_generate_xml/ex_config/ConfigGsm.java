package com.bravo.parse_generate_xml.ex_config;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by lenovo on 2017/2/8.
 */
@XStreamAlias("gsm")
public class ConfigGsm {
    @XStreamAlias("monitor-ip")
    private String monitorIp;
    /*@XStreamAlias("msc-ip")
    private String mscIp;*/

    @Override
    public String toString() {
        return "ConfigGsm{" +
                "monitorIp='" + monitorIp + '\'' +
                '}';
    }

    public String getMonitorIp() {
        return monitorIp;
    }

    public void setMonitorIp(String monitorIp) {
        this.monitorIp = monitorIp;
    }

}
