package com.bravo.data_ben;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jack.liao on 2016/9/19.
 */
public class FemtoDataStruct implements Parcelable {
    private final String TAG = "FemtoDataStruct";
    private String ipAddress;
    private String MacAddress;
    private int iTcpPort = 8021;
    private int iUdpPort = 8021;
    private String SSID;
    private String strDeviceType;
    private int iConnectState = 0;

    public FemtoDataStruct() {

    }

    public int getiTcpPort() {
        return iTcpPort;
    }

    public void setiTcpPort(int iTcpPort) {
        this.iTcpPort = iTcpPort;
    }

    public int getiUdpPort() {
        return iUdpPort;
    }

    public void setiUdpPort(int iUdpPort) {
        this.iUdpPort = iUdpPort;
    }

    public String getStrDeviceType() {
        return strDeviceType;
    }

    public void setStrDeviceType(String strDeviceType) {
        this.strDeviceType = strDeviceType;
    }

    public void setConnectState (int iState) {
        iConnectState = iState;
    }
    public int getConnectState() {
        return iConnectState;
    }

    public void setIPAddress(String ipAddress) {
            this.ipAddress = ipAddress;
    }
    public String getIPAddress() {
        return ipAddress;
    }

    public void setMacAddress(String MacAddress) {
        this.MacAddress = MacAddress;
    }
    public String getMacAddress() {
        return MacAddress;
    }


    public void setSSID(String SSID) {
        this.SSID = SSID;
    }
    public String getSSID() {
        return SSID;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ipAddress);
        dest.writeString(MacAddress);
        dest.writeInt(iTcpPort);
        dest.writeInt(iUdpPort);
        dest.writeString(SSID);
        dest.writeInt(iConnectState);
        dest.writeString(strDeviceType);
    }

    public static final Creator<FemtoDataStruct> CREATOR = new Creator<FemtoDataStruct>()
    {
        @Override
        public FemtoDataStruct[] newArray(int size)
        {
            return new FemtoDataStruct[size];
        }

        @Override
        public FemtoDataStruct createFromParcel(Parcel in)
        {
            return new FemtoDataStruct(in);
        }
    };

    public FemtoDataStruct(Parcel in) {
        ipAddress = in.readString();
        MacAddress = in.readString();
        iTcpPort = in.readInt();
        iUdpPort = in.readInt();
        SSID = in.readString();
        iConnectState = in .readInt();
        strDeviceType = in.readString();
    }
}
