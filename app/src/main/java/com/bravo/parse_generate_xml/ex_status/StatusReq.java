package com.bravo.parse_generate_xml.ex_status;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("ex-status-req")
public class StatusReq {

    @XStreamAsAttribute
    private String version;
    /*
    *   0: all info
    *   1: Basic info
    *   2: Wifi info
    *   4: Cell info
    *   8: HW info
    *   16: Sniff info
    *   32: KPI info
    * */
    private String type;
     public static String toXml(StatusReq sr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("ex-status-req",StatusReq.class);
        xStream.autodetectAnnotations(true);
        return xStream.toXML(sr);
    }

    public static StatusReq xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(StatusReq.class);
        return (StatusReq) xStream.fromXML(is);
    }

    public static StatusReq xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(StatusReq.class);
        return (StatusReq) xStream.fromXML(str);
    }

    public static StatusReq xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(StatusReq.class);
        return (StatusReq) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "StatusReq{" +
                "version='" + version + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
