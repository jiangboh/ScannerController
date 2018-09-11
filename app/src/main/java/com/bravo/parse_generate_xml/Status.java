package com.bravo.parse_generate_xml;

import com.bravo.parse_generate_xml.cell_scan.CellScanTechSpecific;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("status-notif")
public class Status {
    @XStreamAsAttribute
    private String version;
    @XStreamAlias("femto-sn")
    private String femtoSn;
    @XStreamAlias("femto-ver")
    private String femtoVer;
    //格式：“STR, STR, STR,...: IP Addresses”
    @XStreamAlias("connected-clients")
    private String connectedClients;
    @XStreamAlias("controller-client")
    private String controllerClient;
    private String band;
    private String tech;
    private String channel;
    private String cid;
    private String rncid;
    private String mnc;
    private String mcc;
    private String mode;
    @XStreamAlias("tech-capability")
    private String techCapability;
    @XStreamAlias("tech-specific")
    private CellScanTechSpecific techSpecific;
    //格式：“[0;100]”
    @XStreamAlias("battery-level")
    private String batteryLevel;
    @XStreamAlias("battery-charging")
    private String batteryCharging;
    @XStreamAlias("bts-state")
    private String btsState;

    public static String toXml(Status tlr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("status-notif",Status.class);
        xStream.useAttributeFor(Status.class,"version");
        xStream.omitField(CellScanTechSpecific.class,"rscp");
        xStream.omitField(CellScanTechSpecific.class,"rsrp");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(tlr);
    }

    public static Status xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(Status.class);
        return (Status) xStream.fromXML(is);
    }

    public static Status xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(Status.class);
        return (Status) xStream.fromXML(str);
    }

    public static Status xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(Status.class);
        return (Status) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "Status{" +
                "version='" + version + '\'' +
                ", femtoSn='" + femtoSn + '\'' +
                ", femtoVer='" + femtoVer + '\'' +
                ", connectedClients='" + connectedClients + '\'' +
                ", controllerClient='" + controllerClient + '\'' +
                ", band=" + band +
                ", tech='" + tech + '\'' +
                ", channel='" + channel + '\'' +
                ", cid=" + cid +
                ", rncid=" + rncid +
                ", mnc=" + mnc +
                ", mcc=" + mcc +
                ", techCapability=" + techCapability +
                ", techSpecific=" + techSpecific +
                ", baterryLevel='" + batteryLevel + '\'' +
                ", baterryCharging='" + batteryCharging + '\'' +
                ", btsState='" + btsState + '\'' +
                '}';
    }

    public String getVersion() {
        return version;
    }

    public String getFemtoSn() {
        return femtoSn;
    }

    public String getFemtoVer() {
        return femtoVer;
    }

    public String getConnectedClients() {
        return connectedClients;
    }

    public String getControllerClient() {
        return controllerClient;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public String getTech() {
        return tech;
    }

    public void setTech(String tech) {
        this.tech = tech;
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

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getTechCapability() {
        return techCapability;
    }

    public CellScanTechSpecific getTechSpecific() {
        return techSpecific;
    }

    public void setTechSpecific(CellScanTechSpecific techSpecific) {
        this.techSpecific = techSpecific;
    }

    public String getBaterryLevel() {
        return batteryLevel;
    }


    public String getBaterryCharging() {
        return batteryCharging;
    }


    public String getBtsState() {
        return btsState;
    }

    public String getMode() {
        return mode;
    }

}
