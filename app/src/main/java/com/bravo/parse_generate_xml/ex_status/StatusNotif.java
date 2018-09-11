package com.bravo.parse_generate_xml.ex_status;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Created by lenovo on 2017/2/8.
 */
@XStreamAlias("ex-status-notif")
public class StatusNotif {

    @XStreamAsAttribute
    private String version;
    private StatusNotifBasic basic;
    private StatusNotifWifi wifi;
    private StatusNotifCell cell;
    private StatusNotifHw hw;
    private String kpi;
    private List<NeighborListCell> sniff;

    public static String toXml(StatusNotif sn){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("ex-status-notif",StatusNotif.class);
        xStream.useAttributeFor(StatusNotif.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(sn);
    }

    public static StatusNotif xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(StatusNotif.class);
        return (StatusNotif) xStream.fromXML(is);
    }

    public static StatusNotif xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(StatusNotif.class);
        return (StatusNotif) xStream.fromXML(str);
    }

    public static StatusNotif xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(StatusNotif.class);
        return (StatusNotif) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "StatusNotif{" +
                "version='" + version + '\'' +
                ", basic=" + basic +
                ", wifi=" + wifi +
                ", cell=" + cell +
                ", hw=" + hw +
                ", kpi='" + kpi + '\'' +
                ", sniff=" + sniff +
                '}';
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public StatusNotifBasic getBasic() {
        return basic;
    }

    public void setBasic(StatusNotifBasic basic) {
        this.basic = basic;
    }

    public StatusNotifWifi getWifi() {
        return wifi;
    }

    public void setWifi(StatusNotifWifi wifi) {
        this.wifi = wifi;
    }

    public StatusNotifCell getCell() {
        return cell;
    }

    public void setCell(StatusNotifCell cell) {
        this.cell = cell;
    }

    public StatusNotifHw getHw() {
        return hw;
    }

    public void setHw(StatusNotifHw hw) {
        this.hw = hw;
    }

    public String getKpi() {
        return kpi;
    }

    public void setKpi(String kpi) {
        this.kpi = kpi;
    }

    public List<NeighborListCell> getSniff() {
        return sniff;
    }

    public void setSniff(List<NeighborListCell> sniff) {
        this.sniff = sniff;
    }
}
