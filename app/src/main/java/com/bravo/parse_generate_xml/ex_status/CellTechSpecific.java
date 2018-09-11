package com.bravo.parse_generate_xml.ex_status;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by lenovo on 2017/2/8.
 */
@XStreamAlias("tech-specific")
public class CellTechSpecific {
    private String bsic;
    private String rssi;
    private String lac;
    private String psc;
    private String rscp;
    private String tac;
    private String pci;
    private String rsrp;

    @Override
    public String toString() {
        return "CellTechSpecific{" +
                "bsic='" + bsic + '\'' +
                ", rssi='" + rssi + '\'' +
                ", lac='" + lac + '\'' +
                ", psc='" + psc + '\'' +
                ", rscp='" + rscp + '\'' +
                ", tac='" + tac + '\'' +
                ", pci='" + pci + '\'' +
                ", rsrp='" + rsrp + '\'' +
                '}';
    }

    public String getBsic() {
        return bsic;
    }

    public void setBsic(String bsic) {
        this.bsic = bsic;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
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

    public String getRscp() {
        return rscp;
    }

    public void setRscp(String rscp) {
        this.rscp = rscp;
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

    public String getRsrp() {
        return rsrp;
    }

    public void setRsrp(String rsrp) {
        this.rsrp = rsrp;
    }
}
