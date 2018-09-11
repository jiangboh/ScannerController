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
@XStreamAlias("bts-online")
public class BTSOnline {
    @XStreamAsAttribute
    private String version;
    @XStreamAlias("femto-sn")
    private String femtoSn;
    @XStreamAlias("femto-band")
    private String femtoBand;

    public static String toXml(BTSOnline tlr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("bts-online",BTSOnline.class);
        xStream.useAttributeFor(BTSOnline.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(tlr);
    }

    public static BTSOnline xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(BTSOnline.class);
        return (BTSOnline) xStream.fromXML(is);
    }

    public static BTSOnline xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(BTSOnline.class);
        return (BTSOnline) xStream.fromXML(str);
    }

    public static BTSOnline xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(BTSOnline.class);
        return (BTSOnline) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "BTSOnline{" +
                "version='" + version + '\'' +
                ", femtoSn='" + femtoSn + '\'' +
                ", femtoBand=" + femtoBand +
                '}';
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFemtoSn() {
        return femtoSn;
    }

    public String getFemtoBand() {
        return femtoBand;
    }
}
