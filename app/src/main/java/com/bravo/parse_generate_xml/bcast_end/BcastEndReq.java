package com.bravo.parse_generate_xml.bcast_end;

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
@XStreamAlias("bcast-end-req")
public class BcastEndReq {
    @XStreamAsAttribute
    private String version = EventBusMsgConstant.XML_VERSION;

    public static String toXml(BcastEndReq bsr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("bcast-end-req",BcastEndReq.class);
        xStream.useAttributeFor(BcastEndReq.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(bsr);
    }

    public static BcastEndReq xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(BcastEndReq.class);
        return (BcastEndReq) xStream.fromXML(is);
    }

    public static BcastEndReq xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(BcastEndReq.class);
        return (BcastEndReq) xStream.fromXML(str);
    }

    public static BcastEndReq xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(BcastEndReq.class);
        return (BcastEndReq) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "BcastEndReq{" +
                "version='" + version + '}';
    }
}
