package com.bravo.parse_generate_xml.target_list;

import com.bravo.socket_service.EventBusMsgConstant;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("target-list-req")
public class TargetListReq {
    @XStreamAsAttribute
    private String version = EventBusMsgConstant.XML_VERSION;
    //格式：“STR,STR,...”
    @XStreamAlias("target-imsis")
    private String targetImsis;
    //格式：“STR,STR,...”
    @XStreamAlias("target-imeis")
    private String targetImeis;
    private List<Target> targets;

    public static String toXml(TargetListReq tlr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("target-list-req",TargetListReq.class);
        xStream.useAttributeFor(TargetListReq.class,"version");
        xStream.addImplicitCollection(TargetListReq.class, "targets");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(tlr);
    }

    public static TargetListReq xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(TargetListReq.class);
        return (TargetListReq) xStream.fromXML(is);
    }

    public static TargetListReq xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(TargetListReq.class);
        return (TargetListReq) xStream.fromXML(str);
    }

    public static TargetListReq xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(TargetListReq.class);
        return (TargetListReq) xStream.fromXML(file);
    }

    public void setTargetImsis(String targetImsis) {
        this.targetImsis = targetImsis;
    }

    public void setTargetImeis(String targetImeis) {
        this.targetImeis = targetImeis;
    }

    public void setTargets(List<Target> targets) {
        this.targets = targets;
    }
}
