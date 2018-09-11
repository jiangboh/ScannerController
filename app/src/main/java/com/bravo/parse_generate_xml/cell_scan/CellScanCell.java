package com.bravo.parse_generate_xml.cell_scan;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.util.List;

/**
 * Created by lenovo on 2016/12/23.
 */
@XStreamAlias("cell")
public class CellScanCell {
    private String cid;
    private String rncid;
    private String mcc;
    private String mnc;
    @XStreamAlias("tech-specific")
    private CellScanTechSpecific techSpecific;
    @XStreamAlias("sib-list")
    private List<CellScanSibCell> sibCells;

    @XStreamOmitField
    private String channel;

    @Override
    public String toString() {
        return "TcpCell{" +
                "cid=" + cid +
                ", rncid=" + rncid +
                ", mcc=" + mcc +
                ", mnc=" + mnc +
                ", techSpecific=" + techSpecific +
                ", sibCells=" + sibCells +
                '}';
    }
    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
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

    public CellScanTechSpecific getTechSpecific() {
        return techSpecific;
    }

    public void setTechSpecific(CellScanTechSpecific techSpecific) {
        this.techSpecific = techSpecific;
    }

    public List<CellScanSibCell> getSibCells() {
        return sibCells;
    }

    public void setSibCells(List<CellScanSibCell> sibCells) {
        this.sibCells = sibCells;
    }
}
