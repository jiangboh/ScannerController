package com.bravo.parse_generate_xml.target_redirect;

import com.bravo.socket_service.EventBusMsgConstant;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("target-redir-req")
public class TargetRedirectReq {
    @XStreamAsAttribute
    private String version = EventBusMsgConstant.XML_VERSION;
    private String imsi;
    private String tech;
    private String band;
    private String channel;
    @XStreamAlias("move-cell")
    private String moveCell;

    public static String toXml(TargetRedirectReq bsr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("target-redir-req",TargetRedirectReq.class);
        xStream.useAttributeFor(TargetRedirectReq.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(bsr);
    }

    public static TargetRedirectReq xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(TargetRedirectReq.class);
        return (TargetRedirectReq) xStream.fromXML(is);
    }

    public static TargetRedirectReq xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(TargetRedirectReq.class);
        return (TargetRedirectReq) xStream.fromXML(str);
    }

    public static TargetRedirectReq xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(TargetRedirectReq.class);
        return (TargetRedirectReq) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "TargetRedirectReq{" +
                "version='" + version + '\'' +
                ", imsi='" + imsi + '\'' +
                ", tech='" + tech + '\'' +
                ", band=" + band +
                ", channel=" + channel +
                ", moveCell=" + moveCell +
                '}';
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
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

    public String isMoveCell() {
        return moveCell;
    }

    public void setMoveCell(String moveCell) {
        this.moveCell = moveCell;
    }
}
