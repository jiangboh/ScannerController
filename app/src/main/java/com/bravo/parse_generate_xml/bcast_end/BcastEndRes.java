package com.bravo.parse_generate_xml.bcast_end;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("bcast-end-res")
public class BcastEndRes {
    @XStreamAsAttribute
    private String version;
    private String status;

    public static String toXml(BcastEndRes bsr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("bcast-end-res",BcastEndRes.class);
        xStream.useAttributeFor(BcastEndRes.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(bsr);
    }

    public static BcastEndRes xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(BcastEndRes.class);
        return (BcastEndRes) xStream.fromXML(is);
    }

    public static BcastEndRes xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(BcastEndRes.class);
        return (BcastEndRes) xStream.fromXML(str);
    }

    public static BcastEndRes xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(BcastEndRes.class);
        return (BcastEndRes) xStream.fromXML(file);
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
}
