package com.bravo.parse_generate_xml.ex_config;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("ex-config-req")
public class ConfigReq {

    /*@XStreamAsAttribute
    private String version;*/
    private ConfigGeneral general;
    private ConfigWifi wifi;
    private ConfigLte lte;
    private ConfigUmts umts;
    private ConfigGsm gsm;

    public static String toXml(ConfigReq cr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("ex-config-req",ConfigReq.class);
        xStream.autodetectAnnotations(true);
        return xStream.toXML(cr);
    }

    public static ConfigReq xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(ConfigReq.class);
        return (ConfigReq) xStream.fromXML(is);
    }

    public static ConfigReq xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(ConfigReq.class);
        return (ConfigReq) xStream.fromXML(str);
    }

    public static ConfigReq xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(ConfigReq.class);
        return (ConfigReq) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "ConfigReq{" +
                "general=" + general +
                ", wifi=" + wifi +
                ", lte=" + lte +
                ", umts=" + umts +
                ", gsm=" + gsm +
                '}';
    }

    public ConfigGeneral getGeneral() {
        return general;
    }

    public void setGeneral(ConfigGeneral general) {
        this.general = general;
    }

    public ConfigWifi getWifi() {
        return wifi;
    }

    public void setWifi(ConfigWifi wifi) {
        this.wifi = wifi;
    }

    public ConfigLte getLte() {
        return lte;
    }

    public void setLte(ConfigLte lte) {
        this.lte = lte;
    }

    public ConfigUmts getUmts() {
        return umts;
    }

    public void setUmts(ConfigUmts umts) {
        this.umts = umts;
    }

    public ConfigGsm getGsm() {
        return gsm;
    }

    public void setGsm(ConfigGsm gsm) {
        this.gsm = gsm;
    }
}
