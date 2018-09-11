package com.bravo.parse_generate_xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("release-target")
public class ReleaseTarget {
    @XStreamAsAttribute
    private String version;
    private String imsi;

    public static String toXml(ReleaseTarget tlr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("release-target",ReleaseTarget.class);
        xStream.useAttributeFor(ReleaseTarget.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(tlr);
    }

    public static ReleaseTarget xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(ReleaseTarget.class);
        return (ReleaseTarget) xStream.fromXML(is);
    }

    public static ReleaseTarget xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(ReleaseTarget.class);
        return (ReleaseTarget) xStream.fromXML(str);
    }

    public static ReleaseTarget xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(ReleaseTarget.class);
        return (ReleaseTarget) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "ReleaseTarget{" +
                "version='" + version + '\'' +
                ", imsi='" + imsi + '\'' +
                '}';
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }
}
