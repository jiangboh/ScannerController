package com.bravo.parse_generate_xml.target_attach;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("tech-specific")
public class TargetAttachTechSpecific {
    private String domain;

    @Override
    public String toString() {
        return "TargetPositionTechSpecific{" +
                "domain='" + domain + '\'' +
                '}';
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
