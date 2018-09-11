package com.bravo.parse_generate_xml.auth_request;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("auth-request-params")
public class AuthRequestParams {
    @XStreamAlias("tech-specific")
    private AuthRequestTechSpecific techSpecific;
    @XStreamAlias("re-sync-info")
    private AuthRequestReSyncInfo reSyncInfo;

    @Override
    public String toString() {
        return "AuthRequestParams{" +
                "techSpecific=" + techSpecific +
                ", reSyncInfo=" + reSyncInfo +
                '}';
    }

    public AuthRequestTechSpecific getTechSpecific() {
        return techSpecific;
    }

    public void setTechSpecific(AuthRequestTechSpecific techSpecific) {
        this.techSpecific = techSpecific;
    }

    public AuthRequestReSyncInfo getReSyncInfo() {
        return reSyncInfo;
    }

    public void setReSyncInfo(AuthRequestReSyncInfo reSyncInfo) {
        this.reSyncInfo = reSyncInfo;
    }
}
