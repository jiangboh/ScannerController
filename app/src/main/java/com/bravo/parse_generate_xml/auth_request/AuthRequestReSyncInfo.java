package com.bravo.parse_generate_xml.auth_request;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("re-sync-info")
public class AuthRequestReSyncInfo {

    private String rand;
    private String auts;

    @Override
    public String toString() {
        return "AuthRequestReSyncInfo{" +
                "rand=" + rand +
                ", auts=" + auts +
                '}';
    }

    public String getRand() {
        return rand;
    }

    public void setRand(String rand) {
        this.rand = rand;
    }

    public String getAuts() {
        return auts;
    }

    public void setAuts(String auts) {
        this.auts = auts;
    }
}
