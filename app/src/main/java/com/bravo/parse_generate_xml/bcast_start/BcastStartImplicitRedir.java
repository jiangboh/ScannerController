package com.bravo.parse_generate_xml.bcast_start;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("implicit-redir")
public class BcastStartImplicitRedir {
    private String tech;
    private String band;
    private String channel;

    @Override
    public String toString() {
        return "BcastStartImplicitRedir{" +
                "tech='" + tech + '\'' +
                ", band=" + band +
                ", channel=" + channel +
                '}';
    }

    public String getTech() {
        return tech;
    }

    public void setTech(String tech) {
        this.tech = tech;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
