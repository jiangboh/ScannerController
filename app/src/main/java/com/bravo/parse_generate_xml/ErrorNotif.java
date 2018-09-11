package com.bravo.parse_generate_xml;

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
@XStreamAlias("error-notif")
public class ErrorNotif {
    @XStreamAsAttribute
    private String version;
    @XStreamAlias("error-code")
    private String errorCode;
    private String details;
    @XStreamOmitField
    private String ipAddress;
    @XStreamOmitField
    private String name;
    @XStreamOmitField
    private String time;

    public String toXml(ErrorNotif tlr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("error-notif",ErrorNotif.class);
        xStream.useAttributeFor(ErrorNotif.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(tlr);
    }

    public static ErrorNotif xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(ErrorNotif.class);
        return (ErrorNotif) xStream.fromXML(is);
    }

    public static ErrorNotif xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(ErrorNotif.class);
        return (ErrorNotif) xStream.fromXML(str);
    }

    public static ErrorNotif xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(ErrorNotif.class);
        return (ErrorNotif) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "ErrorNotif{" +
                "version='" + version + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", details='" + details + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", name='" + name + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
