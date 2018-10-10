package com.bravo.data_ben;

import com.bravo.parse_generate_xml.Find.FindDeviceInfo;
import com.bravo.utils.Logs;
import com.bravo.xml.FindMsgStruct;
import com.bravo.xml.Msg_Body_Struct;

import java.util.ArrayList;

/**
 * Created by admin on 2018-9-17.
 */

public class DeviceDataStruct {
    private static final String TAG = "DeviceDataStruct";

    public static final int ON_LINE = 1;
    public static final int OFF_LINE = 0;

    public static class MODE {
        public static final String NOMODE = "NOMODE";
        public static final String LTE = "LTE";
        public static final String WCDMA = "WCDMA";
        public static final String GSM = "GSM";
        public static final String GSM_V2 = "GSM_ZYF";
        public static final String CDMA = "CDMA";
    }

    public static String String2Mode(String mode)
    {
        if (mode.equalsIgnoreCase("lte_fdd"))
            return MODE.LTE;
        else if (mode.equalsIgnoreCase("lte_tdd"))
            return MODE.LTE;
        else if (mode.equalsIgnoreCase("wcdma"))
            return MODE.WCDMA;
        else if (mode.equalsIgnoreCase("gsm"))
            return MODE.GSM;
        else if (mode.equalsIgnoreCase("gsm_v2"))
            return MODE.GSM_V2;
        else if (mode.equalsIgnoreCase("cdma"))
            return MODE.CDMA;
        else
            return MODE.NOMODE;
    }


    private String Ip;
    private int Port;
    private String SN;
    private String Mode;
    private String FullName;
    private Long LastTime;
    private Long detail;
    private String version;
    private ArrayList<Msg_Body_Struct> msgList = new ArrayList<>();
    private int iState = OFF_LINE;

    public DeviceDataStruct() {

    }

    public DeviceDataStruct xmlToBean(FindDeviceInfo fdi) {
        DeviceDataStruct deviceDataStruct = new DeviceDataStruct();
        deviceDataStruct.setSN(fdi.getSN());
        deviceDataStruct.setFullName(fdi.getFullName());
        deviceDataStruct.setMode(fdi.getMode());
        deviceDataStruct.setIp(fdi.getIp());
        deviceDataStruct.setPort(fdi.getPort());
        if (-1 == DeviceFragmentStruct.inListIndex(fdi.getSN())) {
            deviceDataStruct.setiState(DeviceDataStruct.OFF_LINE);
        }
        else
        {
            deviceDataStruct.setiState(DeviceDataStruct.ON_LINE);
        }
        return deviceDataStruct;
    }

    public DeviceDataStruct xmlToBean(String ip,int port,Msg_Body_Struct msg) {
        DeviceDataStruct deviceInfo = new DeviceDataStruct();

        String sn = FindMsgStruct.GetMsgStringValueInList("sn",msg.dic,"");
        String mode = FindMsgStruct.GetMsgStringValueInList("mode", msg.dic, MODE.LTE);
        if (mode.equals(DeviceDataStruct.MODE.NOMODE))
        {
            Logs.e(TAG,String.format("设备%s[%s:%d]心跳消息中Mode类型错误。",sn,ip,port));
            return null;
        }
        mode = mode.replace("-","_");
        if (mode.equals("LTE_FDD") || mode.equals("LTE_TDD")) {
            mode = MODE.LTE;
        }

        deviceInfo.setIp(ip);
        deviceInfo.setPort(port);
        deviceInfo.setMode(mode);
        deviceInfo.setSN(sn);
        deviceInfo.setLastTime( System.currentTimeMillis());
        deviceInfo.setFullName(FindMsgStruct.GetMsgStringValueInList("fullname",msg.dic,""));
        deviceInfo.setVersion(FindMsgStruct.GetMsgStringValueInList("version",msg.dic,""));
        String sd = FindMsgStruct.GetMsgStringValueInList("detail",msg.dic,"0x0");
        Long detail = Long.valueOf(sd.replace("0x","").replace("0X",""),16);
        deviceInfo.setDetail(detail);

        return deviceInfo;
    }

    public Long getLastTime() {
        return LastTime;
    }

    public void setLastTime(Long lastTime) {
        LastTime = lastTime;
    }

    public Long getDetail() {
        return detail;
    }

    public void setDetail(Long detail) {
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

    public ArrayList<Msg_Body_Struct> getMsgList() {
        return msgList;
    }

    public void setMsgList(ArrayList<Msg_Body_Struct> msgList) {
        this.msgList = msgList;
    }


    /*@Override
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
        dest.writeArray(msgList);
    }

    public static final Creator<DeviceDataStruct> CREATOR = new Creator<DeviceDataStruct>()
    {
        @Override
        public DeviceDataStruct[] newArray(int size)
        {
            return new DeviceDataStruct[size];
        }

        @Override
        public DeviceDataStruct createFromParcel(Parcel in)
        {
            return new DeviceDataStruct(in);
        }
    };

    public DeviceDataStruct(Parcel in) {
        SN = in.readString();
        FullName = in.readString();
        Ip = in.readString();
        Port = in.readInt();
        detail = in.readInt();
        Mode = in.readString();

        version = in.readString();
        iState = in.readInt();
        LastTime = in.readLong();
        msgList = in.readArrayList();
    }*/


}