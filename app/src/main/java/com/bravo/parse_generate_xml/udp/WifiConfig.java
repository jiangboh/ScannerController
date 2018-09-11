package com.bravo.parse_generate_xml.udp;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by lenovo on 2016/12/27.
 */
@XStreamAlias("wifi-config")
public class WifiConfig {
    private String ssid;
    @XStreamAlias("security-mode")
    private String securityMode;
    @XStreamAlias("encryption-algorithm")
    private String encryptionAlgorithm;
    private String passkey;

    @Override
    public String toString() {
        return "WifiConfig{" +
                "ssid='" + ssid + '\'' +
                ", securityMode='" + securityMode + '\'' +
                ", encryptionAlgorithm='" + encryptionAlgorithm + '\'' +
                ", passkey='" + passkey + '\'' +
                '}';
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getSecurityMode() {
        return securityMode;
    }

    public void setSecurityMode(String securityMode) {
        this.securityMode = securityMode;
    }

    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    public void setEncryptionAlgorithm(String encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    public String getPasskey() {
        return passkey;
    }

    public void setPasskey(String passkey) {
        this.passkey = passkey;
    }
}
