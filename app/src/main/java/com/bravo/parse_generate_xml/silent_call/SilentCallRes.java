package com.bravo.parse_generate_xml.silent_call;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("silent-call-res")
public class SilentCallRes {
    @XStreamAsAttribute
    private String version;
    private String status;
    private String imsi;

    public static String toXml(SilentCallRes bsr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("silent-call-res",SilentCallRes.class);
        xStream.useAttributeFor(SilentCallRes.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(bsr);
    }

    public static SilentCallRes xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(SilentCallRes.class);
        return (SilentCallRes) xStream.fromXML(is);
    }

    public static SilentCallRes xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(SilentCallRes.class);
        return (SilentCallRes) xStream.fromXML(str);
    }

    public static SilentCallRes xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(SilentCallRes.class);
        return (SilentCallRes) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "TargetRedirectRes{" +
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

    public String getImsi() {
        return imsi;
    }
}
