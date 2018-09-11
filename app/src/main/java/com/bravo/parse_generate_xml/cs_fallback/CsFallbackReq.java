package com.bravo.parse_generate_xml.cs_fallback;

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
@XStreamAlias("cs-fallback-req")
public class CsFallbackReq {
    @XStreamAsAttribute
    private String version = EventBusMsgConstant.XML_VERSION;
    private String tech;
    private String band;
    private String channel;

    public String toXml(CsFallbackReq bsr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("cs-fallback-req",CsFallbackReq.class);
        xStream.useAttributeFor(CsFallbackReq.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(bsr);
    }

    public CsFallbackReq xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(CsFallbackReq.class);
        return (CsFallbackReq) xStream.fromXML(is);
    }

    public CsFallbackReq xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(CsFallbackReq.class);
        return (CsFallbackReq) xStream.fromXML(str);
    }

    public CsFallbackReq xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(CsFallbackReq.class);
        return (CsFallbackReq) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "CS_Fallback_Req{" +
                "version='" + version + '\'' +
                ", tech='" + tech + '\'' +
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
