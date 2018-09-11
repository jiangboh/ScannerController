package com.bravo.parse_generate_xml.do_auth;

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
@XStreamAlias("do-auth-req")
public class DoAuthReq {
    @XStreamAsAttribute
    private String version = EventBusMsgConstant.XML_VERSION;
    private String imsi;
    @XStreamAlias("auth-params")
    private DoAuthAuthParams authParams;

    public String toXml(DoAuthReq tlr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("do-auth-req",DoAuthReq.class);
        xStream.useAttributeFor(DoAuthReq.class,"version");
        xStream.omitField(DoAuthTechSpecific.class,"tech");
        if("3G".equals(authParams.getTechSpecific().getTech())){
            xStream.omitField(DoAuthTechSpecific.class,"bsic");
            xStream.omitField(DoAuthTechSpecific.class,"kasme");
        }else if("4G".equals(authParams.getTechSpecific().getTech())){
            xStream.omitField(DoAuthTechSpecific.class,"bsic");
            xStream.omitField(DoAuthTechSpecific.class,"ik");
            xStream.omitField(DoAuthTechSpecific.class,"kc");
        }
        xStream.autodetectAnnotations(true);
        return xStream.toXML(tlr);
    }

    public static DoAuthReq xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(DoAuthReq.class);
        return (DoAuthReq) xStream.fromXML(is);
    }

    public static DoAuthReq xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(DoAuthReq.class);
        return (DoAuthReq) xStream.fromXML(str);
    }

    public static DoAuthReq xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(DoAuthReq.class);
        return (DoAuthReq) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "DoAuthReq{" +
                "version='" + version + '\'' +
                ", imsi='" + imsi + '\'' +
                ", authParams=" + authParams +
                '}';
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public DoAuthAuthParams getAuthParams() {
        return authParams;
    }

    public void setAuthParams(DoAuthAuthParams authParams) {
        this.authParams = authParams;
    }
}
