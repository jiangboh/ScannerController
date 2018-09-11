package com.bravo.parse_generate_xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;

/**
 * Created by lenovo on 2016/12/27.
 */
@XStreamAlias("bts-gps-notif")
public class BTSGps {
    @XStreamAsAttribute
    private String version;
    //格式：“FLOAT,FLOAT”
    private String gps;
    private String accuracy;
    //格式：“[0;180;-180;0]”
    private String bearing;

    public static String toXml(BTSGps tlr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("bts-gps-notif",BTSGps.class);
        xStream.useAttributeFor(BTSGps.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(tlr);
    }

    public static BTSGps xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(BTSGps.class);
        return (BTSGps) xStream.fromXML(is);
    }

    public static BTSGps xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(BTSGps.class);
        return (BTSGps) xStream.fromXML(str);
    }

    public static BTSGps xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(BTSGps.class);
        return (BTSGps) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "BTSOnline{" +
                "version='" + version + '\'' +
                ", gps='" + gps + '\'' +
                ", accuracy=" + accuracy +
                ", bearing=" + bearing +
                '}';
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getBearing() {
        return bearing;
    }

    public void setBearing(String bearing) {
        this.bearing = bearing;
    }
}
