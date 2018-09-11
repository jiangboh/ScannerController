package com.bravo.parse_generate_xml.ex_status;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

/**
 * Created by lenovo on 2017/2/8.
 */
@XStreamAlias("cell")
public class NeighborListCell {
    private String channel;
    private String cid;
    private String rncid;
    private String mcc;
    private String mnc;
    @XStreamAlias("frequency-offset")
    private String frequencyOffset;
    @XStreamAlias("tech-specific")
    private CellTechSpecific techSpecific;

    @XStreamAlias("sib-list")
    private List<SibCell> sibList;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "NeighborListCell{" +
                "channel='" + channel + '\'' +
                ", cid='" + cid + '\'' +
                ", rncid='" + rncid + '\'' +
                ", mcc='" + mcc + '\'' +
                ", mnc='" + mnc + '\'' +
                ", frequencyOffset='" + frequencyOffset + '\'' +
                ", techSpecific=" + techSpecific +
                ", sibList=" + sibList +
                '}';
    }

    public String getCid() {
        return cid;
    }

    public String getFrequencyOffset() {
        return frequencyOffset;
    }

    public void setFrequencyOffset(String frequencyOffset) {
        this.frequencyOffset = frequencyOffset;
    }

    public List<SibCell> getSibList() {
        return sibList;
    }

    public void setSibList(List<SibCell> sibList) {
        this.sibList = sibList;
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

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public CellTechSpecific getTechSpecific() {
        return techSpecific;
    }

    public void setTechSpecific(CellTechSpecific techSpecific) {
        this.techSpecific = techSpecific;
    }
}
