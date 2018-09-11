package com.bravo.parse_generate_xml.ex_status;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by lenovo on 2017/2/17.
 */
@XStreamAlias("sw-ver")
public class StatusSWVersion {
    @XStreamAlias("build-ver")
    private String buildVer;
    @XStreamAlias("pixcell-agent-ver")
    private String pixcellagentVer;
    @XStreamAlias("epc-ver")
    private String epcVer;
    @XStreamAlias("stack-ver")
    private String stackVer;
    @XStreamAlias("oam-ver")
    private String oamVer;
    @XStreamAlias("rem-ver")
    private String remVer;
    @XStreamAlias("kernel-ver")
    private String kernelVer;
    @XStreamAlias("uboot-ver")
    private String ubootVer;

    @Override
    public String toString() {
        return "StatusSWVersion{" +
                "buildVer='" + buildVer + '\'' +
                ", pixcellagentVer='" + pixcellagentVer + '\'' +
                ", epcVer='" + epcVer + '\'' +
                ", stackVer='" + stackVer + '\'' +
                ", oamVer='" + oamVer + '\'' +
                ", remVer='" + remVer + '\'' +
                ", kernelVer='" + kernelVer + '\'' +
                ", ubootVer='" + ubootVer + '\'' +
                '}';
    }

    public String getBuildVer() {
        return buildVer;
    }

    public void setBuildVer(String buildVer) {
        this.buildVer = buildVer;
    }

    public String getPixcellagentVer() {
        return pixcellagentVer;
    }

    public void setPixcellagentVer(String pixcellagentVer) {
        this.pixcellagentVer = pixcellagentVer;
    }

    public String getEpcVer() {
        return epcVer;
    }

    public void setEpcVer(String epcVer) {
        this.epcVer = epcVer;
    }

    public String getStackVer() {
        return stackVer;
    }

    public void setStackVer(String stackVer) {
        this.stackVer = stackVer;
    }

    public String getOamVer() {
        return oamVer;
    }

    public void setOamVer(String oamVer) {
        this.oamVer = oamVer;
    }

    public String getRemVer() {
        return remVer;
    }

    public void setRemVer(String remVer) {
        this.remVer = remVer;
    }

    public String getKernelVer() {
        return kernelVer;
    }

    public void setKernelVer(String kernelVer) {
        this.kernelVer = kernelVer;
    }

    public String getUbootVer() {
        return ubootVer;
    }

    public void setUbootVer(String ubootVer) {
        this.ubootVer = ubootVer;
    }
}
