package com.bravo.parse_generate_xml.ex_config;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;

/**
 * Created by lenovo on 2017/2/8.
 */
@XStreamAlias("ex-config-rsp")
public class ConfigRsp {
    private String status;

    public static String toXml(ConfigRsp cr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("ex-config-rsp",ConfigRsp.class);
        xStream.autodetectAnnotations(true);
        return xStream.toXML(cr);
    }

    public static ConfigRsp xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(ConfigRsp.class);
        return (ConfigRsp) xStream.fromXML(is);
    }

    public static ConfigRsp xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(ConfigRsp.class);
        return (ConfigRsp) xStream.fromXML(str);
    }

    public static ConfigRsp xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(ConfigRsp.class);
        return (ConfigRsp) xStream.fromXML(file);
    }
    @Override
    public String toString() {
        return "ConfigRsp{" +
                "status='" + status + '\'' +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
