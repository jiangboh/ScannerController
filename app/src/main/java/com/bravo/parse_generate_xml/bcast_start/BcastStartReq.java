package com.bravo.parse_generate_xml.bcast_start;

import com.bravo.parse_generate_xml.cell_scan.CellScanSibCell;
import com.bravo.parse_generate_xml.cell_scan.CellScanTechSpecific;
import com.bravo.socket_service.EventBusMsgConstant;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("bcast-start-req")
public class BcastStartReq {
    @XStreamAsAttribute
    private String version = EventBusMsgConstant.XML_VERSION;
    private String cid;
    private String rncid;
    private String mcc;
    private String mnc;
    private String band;
    //格式：“INT,INT,...”
    private String channels;
    //格式：“PLMN; PLMN; ...”
    private String eplmn;
    @XStreamAlias("operator-name")
    private String operatorName;
    @XStreamAlias("auto-switch-interval")
    private String autoSwitchInterval;
    @XStreamAlias("tech-specific")
    private CellScanTechSpecific techSpecific;
    private String power;
    @XStreamAlias("antenna-port")
    private String antennaPort;
    @XStreamAlias("antenna-gain")
    private String antennaGain;
    @XStreamAlias("implicit-redir")
    private BcastStartImplicitRedir implicitRedir;
    @XStreamAlias("sib-list")
    private List<CellScanSibCell> sibCells;
    @XStreamAlias("cellscan-threshold")
    private String cellscanThreshold;
    @XStreamOmitField
    private String tech;

    public String toXml(BcastStartReq bsr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("bcast-start-req",BcastStartReq.class);
        xStream.omitField(BcastStartReq.class,"tech");
        xStream.useAttributeFor(BcastStartReq.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(bsr);
    }

    public BcastStartReq xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(BcastStartReq.class);
        return (BcastStartReq) xStream.fromXML(is);
    }

    public BcastStartReq xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(BcastStartReq.class);
        return (BcastStartReq) xStream.fromXML(str);
    }

    public BcastStartReq xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(BcastStartReq.class);
        return (BcastStartReq) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "BcastStartReq{" +
                "version='" + version + '\'' +
                ", cid='" + cid + '\'' +
                ", rncid='" + rncid + '\'' +
                ", mcc='" + mcc + '\'' +
                ", mnc='" + mnc + '\'' +
                ", band='" + band + '\'' +
                ", channels='" + channels + '\'' +
                ", eplmn='" + eplmn + '\'' +
                ", operatorName='" + operatorName + '\'' +
                ", autoSwitchInterval='" + autoSwitchInterval + '\'' +
                ", techSpecific=" + techSpecific +
                ", power='" + power + '\'' +
                ", antennaPort='" + antennaPort + '\'' +
                ", antennaGain='" + antennaGain + '\'' +
                ", implicitRedir=" + implicitRedir +
                ", sibCells=" + sibCells +
                ", cellscanThreshold='" + cellscanThreshold + '\'' +
                ", tech='" + tech + '\'' +
                '}';
    }

    public String getCellscanThreshold() {
        return cellscanThreshold;
    }

    public void setCellscanThreshold(String cellscanThreshold) {
        this.cellscanThreshold = cellscanThreshold;
    }

    public String getTech() {
        return tech;
    }

    public void setTech(String tech) {
        this.tech = tech;
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

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public String getChannels() {
        return channels;
    }

    public void setChannels(String channels) {
        this.channels = channels;
    }

    public String getEplmn() {
        return eplmn;
    }

    public void setEplmn(String eplmn) {
        this.eplmn = eplmn;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getAutoSwitchInterval() {
        return autoSwitchInterval;
    }

    public void setAutoSwitchInterval(String autoSwitchInterval) {
        this.autoSwitchInterval = autoSwitchInterval;
    }

    public CellScanTechSpecific getTechSpecific() {
        return techSpecific;
    }

    public void setTechSpecific(CellScanTechSpecific techSpecific) {
        this.techSpecific = techSpecific;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getAntennaPort() {
        return antennaPort;
    }

    public void setAntennaPort(String antennaPort) {
        this.antennaPort = antennaPort;
    }

    public String getAntennaGain() {
        return antennaGain;
    }

    public void setAntennaGain(String antennaGain) {
        this.antennaGain = antennaGain;
    }

    public BcastStartImplicitRedir getImplicitRedir() {
        return implicitRedir;
    }

    public void setImplicitRedir(BcastStartImplicitRedir implicitRedir) {
        this.implicitRedir = implicitRedir;
    }

    public List<CellScanSibCell> getSibCells() {
        return sibCells;
    }

    public void setSibCells(List<CellScanSibCell> sibCells) {
        this.sibCells = sibCells;
    }
}
