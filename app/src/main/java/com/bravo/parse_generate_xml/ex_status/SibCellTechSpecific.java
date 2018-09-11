package com.bravo.parse_generate_xml.ex_status;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by lenovo on 2017/2/8.
 */
@XStreamAlias("tech-specific")
public class SibCellTechSpecific {
    private String rscp;
    private String lac;
    private String psc;
    private String rsrp;
    private String tac;
    private String pci;

    @Override
    public String toString() {
        return "SibCellTechSpecific{" +
                "rscp='" + rscp + '\'' +
                ", lac='" + lac + '\'' +
                ", psc='" + psc + '\'' +
                ", rsrp='" + rsrp + '\'' +
                ", tac='" + tac + '\'' +
                ", pci='" + pci + '\'' +
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
}
