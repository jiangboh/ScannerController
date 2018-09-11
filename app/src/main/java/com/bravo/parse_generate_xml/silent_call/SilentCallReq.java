package com.bravo.parse_generate_xml.silent_call;

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
@XStreamAlias("silent-call-req")
public class SilentCallReq {
    @XStreamAsAttribute
    private String version = EventBusMsgConstant.XML_VERSION;
    private String imsi;
    private String mode;

    public static String toXml(SilentCallReq bsr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("silent-call-req",SilentCallReq.class);
        xStream.useAttributeFor(SilentCallReq.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(bsr);
    }

    public static SilentCallReq xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(SilentCallReq.class);
        return (SilentCallReq) xStream.fromXML(is);
    }

    public static SilentCallReq xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(SilentCallReq.class);
        return (SilentCallReq) xStream.fromXML(str);
    }

    public static SilentCallReq xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(SilentCallReq.class);
        return (SilentCallReq) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "TargetRedirectReq{" +
                "version='" + version + '\'' +
                ", imsi='" + imsi + '\'' +
                ", mode='" + mode + '\'' +
                '}';
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }
}
