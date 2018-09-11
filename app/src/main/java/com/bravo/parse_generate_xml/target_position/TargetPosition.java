package com.bravo.parse_generate_xml.target_position;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("target-position-notif")
public class TargetPosition {
    @XStreamAsAttribute
    private String version;
    private String imsi;
    @XStreamAlias("tech-specific")
    private TargetPositionTechSpecific techSpecific;
    @XStreamAlias("signal-distance")
    private String signalDistance;
    @XStreamAlias("rtt-distance")
    private String rttDistance;
    //格式：“FLOAT,FLOAT”
    private String gps;


    public static String toXml(TargetPosition tlr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("target-position-notif",TargetPosition.class);
        xStream.useAttributeFor(TargetPosition.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(tlr);
    }

    public static TargetPosition xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(TargetPosition.class);
        return (TargetPosition) xStream.fromXML(is);
    }

    public static TargetPosition xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(TargetPosition.class);
        return (TargetPosition) xStream.fromXML(str);
    }

    public static TargetPosition xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(TargetPosition.class);
        return (TargetPosition) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "TargetPosition{" +
                "version='" + version + '\'' +
                ", imsi='" + imsi + '\'' +
                ", techSpecific=" + techSpecific +
                ", signalDistance=" + signalDistance +
                ", rttDistance=" + rttDistance +
                ", gps=" + gps +
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

    public TargetPositionTechSpecific getTechSpecific() {
        return techSpecific;
    }

    public void setTechSpecific(TargetPositionTechSpecific techSpecific) {
        this.techSpecific = techSpecific;
    }

    public String getSignalDistance() {
        return signalDistance;
    }

    public void setSignalDistance(String signalDistance) {
        this.signalDistance = signalDistance;
    }

    public String getRttDistance() {
        return rttDistance;
    }

    public void setRttDistance(String rttDistance) {
        this.rttDistance = rttDistance;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }
}
