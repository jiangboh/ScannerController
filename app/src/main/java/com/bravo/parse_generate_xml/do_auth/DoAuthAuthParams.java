package com.bravo.parse_generate_xml.do_auth;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("auth-params")
public class DoAuthAuthParams {
    private String rand;
    private String xres;
    private String autn;
    @XStreamAlias("tech-specific")
    private DoAuthTechSpecific techSpecific;

    @Override
    public String toString() {
        return "DoAuthAuthParams{" +
                "rand='" + rand + '\'' +
                ", xres='" + xres + '\'' +
                ", autn='" + autn + '\'' +
                ", techSpecific=" + techSpecific +
                '}';
    }

    public void setRand(String rand) {
        this.rand = rand;
    }

    public DoAuthTechSpecific getTechSpecific() {
        return techSpecific;
    }

    public void setTechSpecific(DoAuthTechSpecific techSpecific) {
        this.techSpecific = techSpecific;
    }

    public void setXres(String xres) {
        this.xres = xres;
    }

    public void setAutn(String autn) {
        this.autn = autn;
    }
}
