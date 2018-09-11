package com.bravo.parse_generate_xml.auth_request;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by lenovo on 2016/12/26.
 */
@XStreamAlias("guti")
public class AuthRequestGuti {

    private String mcc;
    private String mnc;
    @XStreamAlias("mme-grp-id")
    private String mmeGrpId;
    @XStreamAlias("mme-code")
    private String mmeCode;
    @XStreamAlias("m-tmsi")
    private String mTmsi;

    @Override
    public String toString() {
        return "AuthRequestGuti{" +
                "mcc=" + mcc +
                ", mnc=" + mnc +
                ", mmeGrpId=" + mmeGrpId +
                ", mmcCode=" + mmeCode +
                ", mTmsi='" + mTmsi + '\'' +
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

    public String getMmeGrpId() {
        return mmeGrpId;
    }

    public void setMmeGrpId(String mmeGrpId) {
        this.mmeGrpId = mmeGrpId;
    }

    public String getMmcCode() {
        return mmeCode;
    }

    public void setMmcCode(String mmcCode) {
        this.mmeCode = mmcCode;
    }

    public String getmTmsi() {
        return mTmsi;
    }

    public void setmTmsi(String mTmsi) {
        this.mTmsi = mTmsi;
    }
}
