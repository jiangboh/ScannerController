package com.bravo.parse_generate_xml.ex_status;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by lenovo on 2017/2/8.
 */
@XStreamAlias("sib-cell")
public class SibCell {
    private String cid;
    private String rncid;
    private String channel;
    @XStreamAlias("tech-specific")
    private SibCellTechSpecific techSpecific;

    @Override
    public String toString() {
        return "SibCell{" +
                "cid='" + cid + '\'' +
                ", rncid='" + rncid + '\'' +
                ", channel='" + channel + '\'' +
                ", techSpecific=" + techSpecific +
                '}';
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getRncid() {
        return rncid;
    }

    public void setRncid(String rncid) {
        this.rncid = rncid;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public SibCellTechSpecific getTechSpecific() {
        return techSpecific;
    }

    public void setTechSpecific(SibCellTechSpecific techSpecific) {
        this.techSpecific = techSpecific;
    }
}
