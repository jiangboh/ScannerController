package com.bravo.parse_generate_xml.bcast_start;

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
@XStreamAlias("bcast-start-res")
public class BcastStartRes {
    @XStreamAsAttribute
    private String version;
    private String status;
    private String cid;
    private String rncid;
    private String channel;
    @XStreamAlias("tech-specific")
    private CellScanTechSpecific techSpecific;

    public String toXml(BcastStartRes bsr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("bcast-start-res",BcastStartRes.class);
        xStream.useAttributeFor(BcastStartRes.class,"version");
        xStream.omitField(CellScanTechSpecific.class,"rscp");
        xStream.omitField(CellScanTechSpecific.class,"rsrp");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(bsr);
    }

    public BcastStartRes xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(BcastStartRes.class);
        return (BcastStartRes) xStream.fromXML(is);
    }

    public static BcastStartRes xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(BcastStartRes.class);
        return (BcastStartRes) xStream.fromXML(str);
    }

    public BcastStartRes xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(BcastStartRes.class);
        return (BcastStartRes) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "TargetRedirectRes{" +
                "version='" + version + '\'' +
                ", status='" + status + '\'' +
                ", cid=" + cid +
                ", rncid=" + rncid +
                ", channel=" + channel +
                ", techSpecific=" + techSpecific +
                '}';
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
