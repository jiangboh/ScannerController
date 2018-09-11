package com.bravo.parse_generate_xml.udp;

/**
 * Created by Jack.liao on 2017/11/27.
 */

import com.bravo.socket_service.EventBusMsgConstant;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;

@XStreamAlias("bts-query")
public class BTSQuery {
    @XStreamAsAttribute
    private String version = EventBusMsgConstant.XML_VERSION;

    public static String toXml(BTSQuery tlr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("bts-online",BTSQuery.class);
        xStream.useAttributeFor(BTSQuery.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(tlr);
    }

    public static BTSQuery xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(BTSQuery.class);
        return (BTSQuery) xStream.fromXML(is);
    }

    public static BTSQuery xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(BTSQuery.class);
        return (BTSQuery) xStream.fromXML(str);
    }

    public static BTSQuery xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(BTSQuery.class);
        return (BTSQuery) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "BTSQuery{" +
                "version='" + version + '\'' +
                '}';
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
