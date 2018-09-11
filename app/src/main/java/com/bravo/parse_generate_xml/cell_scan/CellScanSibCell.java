package com.bravo.parse_generate_xml.cell_scan;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/12/26.
 */

@XStreamAlias("sib-cell")
public class CellScanSibCell implements  Serializable{
    private String cid;
    private String rncid;
    private String channel;

    @XStreamAlias("tech-specific")
    private CellScanTechSpecific techSpecific;

    @XStreamOmitField
    private boolean bCheck = false;

    public boolean getbCheck() {
        return bCheck;
    }

    public void setbCheck(boolean bCheck) {
        this.bCheck = bCheck;
    }

    @Override
    public String toString() {
        return "CellScanSibCell{" +
                "cid=" + cid +
                ", rncid=" + rncid +
                ", channel=" + channel +
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

    public CellScanTechSpecific getTechSpecific() {
        return techSpecific;
    }

    public void setTechSpecific(CellScanTechSpecific techSpecific) {
        this.techSpecific = techSpecific;
    }
}
