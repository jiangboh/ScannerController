package com.bravo.parse_generate_xml.send_sms;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("send-sms-res")
public class SendSmsRes {
    @XStreamAsAttribute
    private String version;
    private String status;

    public static String toXml(SendSmsRes bsr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("send-sms-res",SendSmsRes.class);
        xStream.useAttributeFor(SendSmsRes.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(bsr);
    }

    public static SendSmsRes xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(SendSmsRes.class);
        return (SendSmsRes) xStream.fromXML(is);
    }

    public static SendSmsRes xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(SendSmsRes.class);
        return (SendSmsRes) xStream.fromXML(str);
    }

    public static SendSmsRes xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(SendSmsRes.class);
        return (SendSmsRes) xStream.fromXML(file);
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
