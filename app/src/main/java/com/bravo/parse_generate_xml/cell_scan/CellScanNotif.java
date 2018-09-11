package com.bravo.parse_generate_xml.cell_scan;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Created by lenovo on 2016/12/23.
 */

@XStreamAlias("cell-scan-notif")
public class CellScanNotif {
    @XStreamAsAttribute
    private String version;
    private String channel;
    @XStreamAlias("neighbor-list")
    private List<CellScanCell> cells;

    public static String toXml(CellScanNotif tcsn){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("cell-scan-notif",CellScanNotif.class);
        xStream.useAttributeFor(CellScanNotif.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(tcsn);
    }

    public static CellScanNotif xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(CellScanNotif.class);
        return (CellScanNotif) xStream.fromXML(is);
    }

    public static CellScanNotif xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(CellScanNotif.class);
        return (CellScanNotif) xStream.fromXML(str);
    }

    public static CellScanNotif xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(CellScanNotif.class);
        return (CellScanNotif) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "CellScanNotif{" +
                "version='" + version + '\'' +
                ", channel='" + channel + '\'' +
                ", cells=" + cells +
                '}';
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setCells(List<CellScanCell> cells) {
        this.cells = cells;
    }

    public String getVersion() {
        return version;
    }

    public String getChannel() {
        return channel;
    }

    public List<CellScanCell> getCells() {
        return cells;
    }
}
