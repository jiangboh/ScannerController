package com.bravo.parse_generate_xml.send_sms;

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
@XStreamAlias("send-sms-req")
public class SendSmsReq {
    @XStreamAsAttribute
    private String version = EventBusMsgConstant.XML_VERSION;
    private String imsi;
    private String origin;
    private String text;

    public static String toXml(SendSmsReq bsr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("send-sms-req",SendSmsReq.class);
        xStream.useAttributeFor(SendSmsReq.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(bsr);
    }

    public static SendSmsReq xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(SendSmsReq.class);
        return (SendSmsReq) xStream.fromXML(is);
    }

    public static SendSmsReq xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(SendSmsReq.class);
        return (SendSmsReq) xStream.fromXML(str);
    }

    public static SendSmsReq xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(SendSmsReq.class);
        return (SendSmsReq) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "BcastEndReq{" +
                "version='" + version + '\'' +
                ", imsi='" + imsi + '\'' +
                ", origin='" + origin + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
