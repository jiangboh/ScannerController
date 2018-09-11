package com.bravo.parse_generate_xml.target_redirect;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("target-redir-res")
public class TargetRedirectRes {
    @XStreamAsAttribute
    private String version;
    private String imsi;
    private String status;

    public static String toXml(TargetRedirectRes bsr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("target-redir-res",TargetRedirectRes.class);
        xStream.useAttributeFor(TargetRedirectRes.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(bsr);
    }

    public static TargetRedirectRes xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(TargetRedirectRes.class);
        return (TargetRedirectRes) xStream.fromXML(is);
    }

    public static TargetRedirectRes xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(TargetRedirectRes.class);
        return (TargetRedirectRes) xStream.fromXML(str);
    }

    public static TargetRedirectRes xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(TargetRedirectRes.class);
        return (TargetRedirectRes) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "TargetRedirectRes{" +
                "version='" + version + '\'' +
                ", imsi='" + imsi + '\'' +
                ", status='" + status + '\'' +
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
