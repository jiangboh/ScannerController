package com.bravo.parse_generate_xml.conn_request;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("conn-request-notif")
public class ConnRequestNotif {
    @XStreamAsAttribute
    private String version;
    private String imsi;
    @XStreamAlias("tech-specific")
    private ConnRequestTechSpecific techSpecific;
    private String distance;
    @XStreamOmitField
    private int iCount;
    @XStreamOmitField
    private int iAuth;

    public static String toXml(ConnRequestNotif crn){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("conn-request-notif",ConnRequestNotif.class);
        xStream.useAttributeFor(ConnRequestNotif.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(crn);
    }

    public static ConnRequestNotif xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(ConnRequestNotif.class);
        return (ConnRequestNotif) xStream.fromXML(is);
    }

    public static ConnRequestNotif xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(ConnRequestNotif.class);
        return (ConnRequestNotif) xStream.fromXML(str);
    }

    public static ConnRequestNotif xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(ConnRequestNotif.class);
        return (ConnRequestNotif) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "ConnRequestNotif{" +
                "version='" + version + '\'' +
                ", imsi='" + imsi + '\'' +
                ", techSpecific=" + techSpecific +
                ", distance=" + distance +
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

    public ConnRequestTechSpecific getTechSpecific() {
        return techSpecific;
    }

    public void setTechSpecific(ConnRequestTechSpecific techSpecific) {
        this.techSpecific = techSpecific;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getiCount() {
        return iCount;
    }

    public void setiCount(int iCount) {
        this.iCount = iCount;
    }

    public int getiAuth() {
        return iAuth;
    }

    public void setiAuth(int iAuth) {
        this.iAuth = iAuth;
    }
}
