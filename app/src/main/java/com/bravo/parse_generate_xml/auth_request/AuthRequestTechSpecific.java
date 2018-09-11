package com.bravo.parse_generate_xml.auth_request;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("tech-specific")
public class AuthRequestTechSpecific {
    private String domain;
    private String tmsi;
    private String lai;
    @XStreamAlias("guti")
    private AuthRequestGuti guti;
    @XStreamAlias("sn-id")
    private String snId;

    @Override
    public String toString() {
        return "AuthRequestTechSpecific{" +
                "domain='" + domain + '\'' +
                ", tmsi='" + tmsi + '\'' +
                ", lai='" + lai + '\'' +
                ", guti=" + guti +
                ", snId='" + snId + '\'' +
                '}';
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setTmsi(String tmsi) {
        this.tmsi = tmsi;
    }

    public void setLai(String lai) {
        this.lai = lai;
    }

    public void setGuti(AuthRequestGuti guti) {
        this.guti = guti;
    }

    public void setSnId(String snId) {
        this.snId = snId;
    }

    public String getDomain() {
        return domain;
    }

    public String getTmsi() {
        return tmsi;
    }

    public String getLai() {
        return lai;
    }

    public AuthRequestGuti getGuti() {
        return guti;
    }

    public String getSnId() {
        return snId;
    }
}
