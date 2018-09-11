package com.bravo.parse_generate_xml.ex_config;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by lenovo on 2017/2/8.
 */
@XStreamAlias("wifi")
public class ConfigWifi {
    private String mode;
    private String channel;
    private String ssid;
    @XStreamAlias("security-mode")
    private String securityMode;
    @XStreamAlias("encryption-algorithm")
    private String encryptionAlgorithm;
    private String passkey;

    @Override
    public String toString() {
        return "ConfigWifi{" +
                "mode='" + mode + '\'' +
                ", channel='" + channel + '\'' +
                ", ssid='" + ssid + '\'' +
                ", securityMode='" + securityMode + '\'' +
                ", encryptionAlgorithm='" + encryptionAlgorithm + '\'' +
                ", passkey='" + passkey + '\'' +
                '}';
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
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
