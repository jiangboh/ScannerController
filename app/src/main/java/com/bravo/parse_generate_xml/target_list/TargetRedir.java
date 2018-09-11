package com.bravo.parse_generate_xml.target_list;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by Jack.liao on 2017/7/18.
 */
@XStreamAlias("redir")
public class TargetRedir {
    private String tech;
    private String band;
    private String channel;

    @Override
    public String toString() {
        return "TargetRedir{" +
                "tech='" + tech + '\'' +
                ", band='" + band + '\'' +
                ", channel='" + channel + '\'' +
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
