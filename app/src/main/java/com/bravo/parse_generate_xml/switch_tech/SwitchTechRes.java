package com.bravo.parse_generate_xml.switch_tech;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("switch-tech-res")
public class SwitchTechRes {
    @XStreamAsAttribute
    private String version;
    private String status;

    public static String toXml(SwitchTechRes bsr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("switch-tech-res",SwitchTechRes.class);
        xStream.useAttributeFor(SwitchTechRes.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(bsr);
    }

    public static SwitchTechRes xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(SwitchTechRes.class);
        return (SwitchTechRes) xStream.fromXML(is);
    }

    public static SwitchTechRes xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(SwitchTechRes.class);
        return (SwitchTechRes) xStream.fromXML(str);
    }

    public static SwitchTechRes xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(SwitchTechRes.class);
        return (SwitchTechRes) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "SwitchTechRes{" +
                "version='" + version + '\'' +
                ", status='" + status + '\'' +
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
}
