package com.bravo.parse_generate_xml.cell_scan;

import com.bravo.socket_service.EventBusMsgConstant;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.InputStream;

/**
 * Created by lenovo on 2016/12/23.
 */
@XStreamAlias("cell-scan-req")
public class CellScanReq {
    private String band;
    @XStreamAsAttribute()
    private String version = EventBusMsgConstant.XML_VERSION;
    private String afc;
    //格式“INT..INT,INT,...”
    private String channels;
    //格式“[-80;-120]”
    private String threshold;
    @XStreamAlias("antenna-port")
    private String antennaPort;

    public static String toXml(CellScanReq tcsr){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.alias("cell-scan-req",CellScanReq.class);
        xStream.useAttributeFor(CellScanReq.class,"version");
        xStream.autodetectAnnotations(true);
        return xStream.toXML(tcsr);
    }

    public static CellScanReq xmlToBean(InputStream is) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(CellScanReq.class);
        return (CellScanReq) xStream.fromXML(is);
    }

    public static CellScanReq xmlToBean(String str) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(CellScanReq.class);
        return (CellScanReq) xStream.fromXML(str);
    }

    public static CellScanReq xmlToBean(File file) {
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        xStream.processAnnotations(CellScanReq.class);
        return (CellScanReq) xStream.fromXML(file);
    }

    @Override
    public String toString() {
        return "CellScanReq{" +
                "band='" + band + '\'' +
                ", version='" + version + '\'' +
                ", afc='" + afc + '\'' +
                ", channels='" + channels + '\'' +
                ", threshold='" + threshold + '\'' +
                ", antennaPort='" + antennaPort + '\'' +
                '}';
    }

    public void setBand(String band) {
        this.band = band;
    }

    public void setAfc(String afc) {
        this.afc = afc;
    }

    public void setChannels(String channels) {
        this.channels = channels;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public void setAntennaPort(String antennaPort) {
        this.antennaPort = antennaPort;
    }
}
