package com.bravo.parse_generate_xml.udp;

import com.bravo.socket_service.EventBusMsgConstant;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;

/**
 * Created by lenovo on 2016/12/27.
 */
@XStreamAlias("register-client")
public class RegisterClient {
    @XStreamAsAttribute
    private String version = EventBusMsgConstant.XML_VERSION;
    @XStreamAlias("client-type")
    private String clientType;
    @XStreamAlias("current-time")
    private long currentTime;

    public static String toXml(RegisterClient tlr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("register-client",RegisterClient.class);
        xStream.useAttributeFor(RegisterClient.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(tlr);
    }

    public static RegisterClient xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(RegisterClient.class);
        return (RegisterClient) xStream.fromXML(is);
    }

    public static RegisterClient xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(RegisterClient.class);
        return (RegisterClient) xStream.fromXML(str);
    }

    public static RegisterClient xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(RegisterClient.class);
        return (RegisterClient) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "RegisterClient{" +
                "version='" + version + '\'' +
                ", clientType=" + clientType +
                ", currentTime=" + currentTime +
                '}';
    }


    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }
}
