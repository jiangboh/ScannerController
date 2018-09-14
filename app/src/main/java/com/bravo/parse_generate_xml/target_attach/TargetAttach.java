package com.bravo.parse_generate_xml.target_attach;

import com.bravo.xml.FindMsgStruct;
import com.bravo.xml.Msg_Body_Struct;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("target-attach-notif")
public class TargetAttach {
    @XStreamAsAttribute
    private String version;
    private String imsi;
    private String imei;
    @XStreamAlias("tech-specific")
    private TargetAttachTechSpecific techSpecific;

    public static TargetAttach xmlToBean(Msg_Body_Struct struct) {
        if (!struct.type.equals("scanner")) return null;
        TargetAttach target = new TargetAttach();
        target.version = "1.0.1";
        target.imsi = FindMsgStruct.GetMsgStringValueInList("imsi",struct.dic,"");
        target.imei = "dd";
        return target;
    }

    public static String toXml(TargetAttach tlr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("target-attach-notif",TargetAttach.class);
        xStream.useAttributeFor(TargetAttach.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(tlr);
    }

    public static TargetAttach xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(TargetAttach.class);
        return (TargetAttach) xStream.fromXML(is);
    }

    public static TargetAttach xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(TargetAttach.class);
        return (TargetAttach) xStream.fromXML(str);
    }

    public static TargetAttach xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(TargetAttach.class);
        return (TargetAttach) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "TargetAttach{" +
                "version='" + version + '\'' +
                ", imsi='" + imsi + '\'' +
                ", imei='" + imei + '\'' +
                ", techSpecific=" + techSpecific +
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

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public TargetAttachTechSpecific getTechSpecific() {
        return techSpecific;
    }

    public void setTechSpecific(TargetAttachTechSpecific techSpecific) {
        this.techSpecific = techSpecific;
    }
}
