package com.bravo.parse_generate_xml.switch_tech;

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
@XStreamAlias("switch-tech-req")
public class SwitchTechReq {
    @XStreamAsAttribute
    private String version = EventBusMsgConstant.XML_VERSION;
    private String tech;

    public static String toXml(SwitchTechReq bsr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("switch-tech-req",SwitchTechReq.class);
        xStream.useAttributeFor(SwitchTechReq.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(bsr);
    }

    public static SwitchTechReq xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(SwitchTechReq.class);
        return (SwitchTechReq) xStream.fromXML(is);
    }

    public static SwitchTechReq xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(SwitchTechReq.class);
        return (SwitchTechReq) xStream.fromXML(str);
    }

    public static SwitchTechReq xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(SwitchTechReq.class);
        return (SwitchTechReq) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "SwitchTechReq{" +
                "version='" + version + '\'' +
                ", tech='" + tech + '\'' +
                '}';
    }

    public String getTech() {
        return tech;
    }

    public void setTech(String tech) {
        this.tech = tech;
    }
}
