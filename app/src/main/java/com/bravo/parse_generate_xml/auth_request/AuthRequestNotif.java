package com.bravo.parse_generate_xml.auth_request;

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
@XStreamAlias("auth-request-notif")
public class AuthRequestNotif {
    @XStreamAsAttribute
    private String version = EventBusMsgConstant.XML_VERSION;
    private String imsi;
    @XStreamAlias("network-type")
    private String networkType;
    @XStreamAlias("auth-request-params")
    private AuthRequestParams params;

    public static String toXml(AuthRequestNotif tlr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("auth-request-notif",AuthRequestNotif.class);
        xStream.useAttributeFor(AuthRequestNotif.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(tlr);
    }

    public static AuthRequestNotif xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(AuthRequestNotif.class);
        return (AuthRequestNotif) xStream.fromXML(is);
    }

    public static AuthRequestNotif xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(AuthRequestNotif.class);
        return (AuthRequestNotif) xStream.fromXML(str);
    }

    public static AuthRequestNotif xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(AuthRequestNotif.class);
        return (AuthRequestNotif) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "DoAuthReq{" +
                "version='" + version + '\'' +
                ", imsi='" + imsi + '\'' +
                ", networkType=" + networkType +
                ", params=" + params +
                '}';
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public AuthRequestParams getParams() {
        return params;
    }

    public void setParams(AuthRequestParams params) {
        this.params = params;
    }
}
