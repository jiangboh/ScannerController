package com.bravo.parse_generate_xml.cell_scan;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/12/23.
 */

@XStreamAlias("tech-specific")
public class CellScanTechSpecific implements Serializable{
    private String rscp;
    private String lac;
    private String psc;
    private String rsrp;
    private String tac;
    private String pci;
    private String bsic;
    private String rssi;
    private String bandwidth;
    private String ncc;
    private String bcc;

    public String getBsic() {
        return bsic;
    }

    public void setBsic(String bsic) {
        this.bsic = bsic;
    }

    @Override
    public String toString() {
        return "CellScanTechSpecific{" +
                "bandwidth=" + bandwidth +
                "rssi=" + rssi +
                "rscp=" + rscp +
                ", lac=" + lac +
                ", psc=" + psc +
                ", rsrp=" + rsrp +
                ", tac=" + tac +
                ", pci=" + pci +
                ", bsic=" + bsic +
                '}';
    }

    public String getRscp() {
        return rscp;
    }

    public void setRscp(String rscp) {
        this.rscp = rscp;
    }

    public String getLac() {
        return lac;
    }

    public void setLac(String lac) {
        this.lac = lac;
    }

    public String getPsc() {
        return psc;
    }

    public void setPsc(String psc) {
        this.psc = psc;
    }

    public String getRsrp() {
        return rsrp;
    }

    public void setRsrp(String rsrp) {
        this.rsrp = rsrp;
    }

    public String getTac() {
        return tac;
    }

    public void setTac(String tac) {
        this.tac = tac;
    }

    public String getPci() {
        return pci;
    }

    public void setPci(String pci) {
        this.pci = pci;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(String bandwidth) {
        this.bandwidth = bandwidth;
    }

    public String getNcc() {
        return ncc;
    }

    public void setNcc(String ncc) {
        this.ncc = ncc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }
}
