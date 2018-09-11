package com.bravo.parse_generate_xml.udp;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;

/**
 * Created by lenovo on 2016/12/27.
 */
@XStreamAlias("action-response")
public class ActionResponse {
    @XStreamAsAttribute
    private String version;
    @XStreamAlias("action-type")
    private String actionType;
    @XStreamAlias("action-status")
    private String actionStatus;

    public static String toXml(ActionResponse tlr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("action-response",ActionResponse.class);
        xStream.useAttributeFor(ActionResponse.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(tlr);
    }

    public static ActionResponse xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(ActionResponse.class);
        return (ActionResponse) xStream.fromXML(is);
    }

    public static ActionResponse xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(ActionResponse.class);
        return (ActionResponse) xStream.fromXML(str);
    }

    public static ActionResponse xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(ActionResponse.class);
        return (ActionResponse) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "ActionResponse{" +
                "version='" + version + '\'' +
                ", actionType=" + actionType +
                ", actionStatus='" + actionStatus + '\'' +
                '}';
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionStatus() {
        return actionStatus;
    }

    public void setActionStatus(String actionStatus) {
        this.actionStatus = actionStatus;
    }
}
