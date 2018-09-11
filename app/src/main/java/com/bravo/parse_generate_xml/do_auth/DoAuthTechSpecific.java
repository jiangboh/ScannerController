package com.bravo.parse_generate_xml.do_auth;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("tech-specific")
public class DoAuthTechSpecific {
    private String ik;
    private String kc;
    private String kasme;
    private String xres;
    private String autn;
    @XStreamOmitField
    private String tech;

    @Override
    public String toString() {
        return "DoAuthTechSpecific{" +
                "ik='" + ik + '\'' +
                ", kc='" + kc + '\'' +
                ", kasme='" + kasme + '\'' +
                ", tech='" + tech + '\'' +
                '}';
    }

    public String getTech() {
        return tech;
    }

    public void setTech(String tech) {
        this.tech = tech;
    }

    public String getIk() {
        return ik;
    }

    public void setIk(String ik) {
        this.ik = ik;
    }

    public String getKc() {
        return kc;
    }

    public void setKc(String kc) {
        this.kc = kc;
    }

    public String getKasme() {
        return kasme;
    }

    public void setKasme(String kasme) {
        this.kasme = kasme;
    }

    public String getXres() {
        return xres;
    }

    public void setXres(String xres) {
        this.xres = xres;
    }

    public String getAutn() {
        return autn;
    }

    public void setAutn(String autn) {
        this.autn = autn;
    }
}
