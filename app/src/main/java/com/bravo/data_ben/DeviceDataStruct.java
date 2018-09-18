package com.bravo.data_ben;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by admin on 2018-9-17.
 */

public class DeviceDataStruct implements Parcelable {
    public static final int ON_LINE = 1;
    public static final int OFF_LINE = 0;

    private final String TAG = "DeviceDataStruct";

    private String Ip;
    private int Port;
    private String SN;
    private String Mode;
    private String FullName;
    private Long LastTime;
    private int detail;
    private String version;
    private int iState = OFF_LINE;

    public DeviceDataStruct() {
    }

    public Long getLastTime() {
        return LastTime;
    }

    public void setLastTime(Long lastTime) {
        LastTime = lastTime;
    }

    public int getDetail() {
        return detail;
    }

    public void setDetail(int detail) {
        this.detail = detail;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "DeviceDataStruct{" +
                "Ip='" + Ip + '\'' +
                ", Port=" + Port +
                ", SN='" + SN + '\'' +
                ", Mode='" + Mode + '\'' +
                ", FullName='" + FullName + '\'' +
                ", LastTime=" + LastTime +
                ", detail=" + detail +
                ", version='" + version + '\'' +
                ", iState=" + iState +
                '}';
    }

    public void setIp(String ip) {
        Ip = ip;
    }

    public void setPort(int port) {
        Port = port;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public void setMode(String mode) {
        Mode = mode;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public void setiState(int iState) {
        this.iState = iState;
    }

    public String getIp() {

        return Ip;
    }

    public int getPort() {
        return Port;
    }

    public String getSN() {
        return SN;
    }

    public String getMode() {
        return Mode;
    }

    public String getFullName() {
        return FullName;
    }

    public int getiState() {
        return iState;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Ip);
        dest.writeString(SN);
        dest.writeInt(iState);
        dest.writeInt(Port);
        dest.writeString(Mode);
        dest.writeString(FullName);
        dest.writeLong(LastTime);
        dest.writeInt(detail);
        dest.writeString(version);
    }

}