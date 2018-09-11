package com.bravo.parse_generate_xml.parameter_change;

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
@XStreamAlias("param-change-res")
public class ParameterChangeRes {
    @XStreamAsAttribute
    private String version;
    private String cid;
    private String rncid;
    private String channel;
    @XStreamAlias("tech-specific")
    private CellScanTechSpecific techSpecific;


    public static String toXml(ParameterChangeRes bsr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("param-change-res",ParameterChangeRes.class);
        xStream.useAttributeFor(ParameterChangeRes.class,"version");
        xStream.omitField(CellScanTechSpecific.class,"rscp");
        xStream.omitField(CellScanTechSpecific.class,"rsrp");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(bsr);
    }

    public static ParameterChangeRes xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(ParameterChangeRes.class);
        return (ParameterChangeRes) xStream.fromXML(is);
    }

    public static ParameterChangeRes xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(ParameterChangeRes.class);
        return (ParameterChangeRes) xStream.fromXML(str);
    }

    public static ParameterChangeRes xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(ParameterChangeRes.class);
        return (ParameterChangeRes) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "ParameterChangeRes{" +
                "version='" + version + '\'' +
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
