package com.bravo.parse_generate_xml.cs_fallback;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("cs-fallback-res")
public class CsFallbackRes {
    @XStreamAsAttribute
    private String version;
    private String status;

    public String toXml(CsFallbackRes bsr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("cs-fallback-res",CsFallbackRes.class);
        xStream.useAttributeFor(CsFallbackRes.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(bsr);
    }

    public CsFallbackRes xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(CsFallbackRes.class);
        return (CsFallbackRes) xStream.fromXML(is);
    }

    public static CsFallbackRes xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(CsFallbackRes.class);
        return (CsFallbackRes) xStream.fromXML(str);
    }

    public static CsFallbackRes xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(CsFallbackRes.class);
        return (CsFallbackRes) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "CsFallbackRes{" +
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
