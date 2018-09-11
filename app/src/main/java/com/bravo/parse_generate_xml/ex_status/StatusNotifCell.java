package com.bravo.parse_generate_xml.ex_status;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("cell")
public class StatusNotifCell {
    private String mcc;
    private String mnc;
    private String cid;
    private String tac;
    private String lac;
    private String channel;
    private String pci;
    private String psc;
    private String bsic;
    private String status;
    private String clients;

    @Override
    public String toString() {
        return "StatusNotifCell{" +
                "mcc='" + mcc + '\'' +
                ", mnc='" + mnc + '\'' +
                ", cid='" + cid + '\'' +
                ", tac='" + tac + '\'' +
                ", lac='" + lac + '\'' +
                ", channel='" + channel + '\'' +
                ", pci='" + pci + '\'' +
                ", psc='" + psc + '\'' +
                ", bsic='" + bsic + '\'' +
                ", status='" + status + '\'' +
                ", clients='" + clients + '\'' +
                '}';
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getTac() {
        return tac;
    }

    public void setTac(String tac) {
        this.tac = tac;
    }

    public String getLac() {
        return lac;
    }

    public void setLac(String lac) {
        this.lac = lac;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getPci() {
        return pci;
    }

    public void setPci(String pci) {
        this.pci = pci;
    }

    public String getPsc() {
        return psc;
    }

    public void setPsc(String psc) {
        this.psc = psc;
    }

    public String getBsic() {
        return bsic;
    }

    public void setBsic(String bsic) {
        this.bsic = bsic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClients() {
        return clients;
    }

    public void setClients(String clients) {
        this.clients = clients;
    }
}
