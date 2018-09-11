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
@XStreamAlias("unregister-client")
public class UnregisterClient {
    @XStreamAsAttribute
    private String version = EventBusMsgConstant.XML_VERSION;

    public static String toXml(UnregisterClient tlr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("unregister-client",UnregisterClient.class);
        xStream.useAttributeFor(UnregisterClient.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(tlr);
    }

    public static UnregisterClient xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(UnregisterClient.class);
        return (UnregisterClient) xStream.fromXML(is);
    }

    public static UnregisterClient xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(UnregisterClient.class);
        return (UnregisterClient) xStream.fromXML(str);
    }

    public static UnregisterClient xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(UnregisterClient.class);
        return (UnregisterClient) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "UnregisterClient{" +
                "version='" + version + '\'' +
                '}';
    }
}
