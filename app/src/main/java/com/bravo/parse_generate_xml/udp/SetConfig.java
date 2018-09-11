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
@XStreamAlias("set-config")
public class SetConfig {
    @XStreamAsAttribute
    private String version = EventBusMsgConstant.XML_VERSION;
    private String mode;
    @XStreamAlias("tcp-port")
    private String tcpPort;
    @XStreamAlias("tcp-retry")
    private String tcpRetry;
    @XStreamAlias("default-gw")
    private String defaultGw;
    @XStreamAlias("nb-gw")
    private String nbGw;
    @XStreamAlias("connectivity-mode")
    private String connectivityMode;
    @XStreamAlias("wifi-config")
    private WifiConfig wifiConfig;
    @XStreamAlias("status-interval")
    private String statusInterval;

    public String toXml(SetConfig tlr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.autodetectAnnotations(true);
        xStream.alias("set-config",SetConfig.class);
        xStream.useAttributeFor(SetConfig.class,"version");
        return xStream.toXML(tlr);
    }

    public static SetConfig xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(SetConfig.class);
        return (SetConfig) xStream.fromXML(is);
    }

    public static SetConfig xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(SetConfig.class);
        return (SetConfig) xStream.fromXML(str);
    }

    public static SetConfig xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(SetConfig.class);
        return (SetConfig) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "SetConfig{" +
                "version='" + version + '\'' +
                ", mode='" + mode + '\'' +
                ", tcpPort=" + tcpPort +
                ", tcpRetry=" + tcpRetry +
                ", defaultGw='" + defaultGw + '\'' +
                ", nbGw='" + nbGw + '\'' +
                ", connectivityMode='" + connectivityMode + '\'' +
                ", wifiConfig=" + wifiConfig +
                '}';
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(String tcpPort) {
        this.tcpPort = tcpPort;
    }

    public String getTcpRetry() {
        return tcpRetry;
    }

    public void setTcpRetry(String tcpRetry) {
        this.tcpRetry = tcpRetry;
    }

    public String getDefaultGw() {
        return defaultGw;
    }

    public void setDefaultGw(String defaultGw) {
        this.defaultGw = defaultGw;
    }

    public String getNbGw() {
        return nbGw;
    }

    public void setNbGw(String nbGw) {
        this.nbGw = nbGw;
    }

    public String getConnectivityMode() {
        return connectivityMode;
    }

    public void setConnectivityMode(String connectivityMode) {
        this.connectivityMode = connectivityMode;
    }

    public WifiConfig getWifiConfig() {
        return wifiConfig;
    }

    public void setWifiConfig(WifiConfig wifiConfig) {
        this.wifiConfig = wifiConfig;
    }

    public void setStatusInterval(String statusInterval) {
        this.statusInterval = statusInterval;
    }
}
