package com.bravo.data_ben;

import com.bravo.parse_generate_xml.Find.FindDeviceInfo;
import com.bravo.utils.Logs;
import com.bravo.utils.Utils;
import com.bravo.xml.CDMA_GeneralPara;
import com.bravo.xml.FindMsgStruct;
import com.bravo.xml.LTE_GeneralPara;
import com.bravo.xml.Msg_Body_Struct;

import static com.bravo.xml.FindMsgStruct.GetMsgStringValueInList;

/**
 * Created by admin on 2018-9-17.
 */

public class DeviceDataStruct {
    private static final String TAG = "DeviceDataStruct";

    public static final int ON_LINE = 1;
    public static final int OFF_LINE = 0;

    public static final String sAddStatus[] = {
            "未就绪状态",
            "就绪状态",
            "扫频状态",
            "小区准备就绪",
            "小区建立中",
            "小区建立完成",
            "小区建立失败",
            "未知状态"
    };

    public static class MODE {
        public static final String NOMODE = "NOMODE";
        public static final String LTE_TDD = "LTE_TDD";
        public static final String LTE_FDD = "LTE_FDD";
        public static final String WCDMA = "WCDMA";
        public static final String GSM = "GSM";
        public static final String GSM_V2 = "GSM_V2";
        public static final String CDMA = "CDMA";
    }

    public static String String2Mode(String mode) {
        if (mode.equalsIgnoreCase("lte_fdd"))
            return MODE.LTE_FDD;
        else if (mode.equalsIgnoreCase("lte_tdd"))
            return MODE.LTE_TDD;
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
    private String sDeviceTime;
    private Long detail;
    private boolean status_sctp;
    private boolean status_s1;
    private boolean status_gps;
    private boolean status_cell;
    private boolean status_sync;
    private boolean status_licens;
    private boolean status_offline;
    private boolean status_wSelf;
    private boolean status_redio2;
    private boolean status_radio;

    private int iAddStatus;
    private String version;
    private Object generalPara = null;

    private int iState = OFF_LINE;

    public DeviceDataStruct(String mode) {
        if (mode.equals(DeviceDataStruct.MODE.LTE_TDD)
                || mode.equals(DeviceDataStruct.MODE.LTE_FDD)
                || mode.equals(DeviceDataStruct.MODE.WCDMA)) {
            generalPara = new CDMA_GeneralPara();
        } else if (mode.equals(DeviceDataStruct.MODE.CDMA)
                || mode.equals(DeviceDataStruct.MODE.GSM_V2)) {
            generalPara = new LTE_GeneralPara();
        } else if (mode.equals(DeviceDataStruct.MODE.GSM)) {
            generalPara = new LTE_GeneralPara();
        }
    }
    public DeviceDataStruct() {
            generalPara = new LTE_GeneralPara();
    }

    private class AP_STATUS_LTE  {
        public static final int SCTP = 0x80000000;
        public static final int S1 = 0x40000000;
        public static final int GPS = 0x20000000;
        public static final int CELL = 0x10000000;
        public static final int SYNC = 0x8000000;
        public static final int LICENS = 0x4000000;
        public static final int RADIO = 0x2000000;
        //public static final int OffLine = 0x1000000;  //tcp上下线标志，android里不用
        public static final int wSelfStudy = 0x800000;
        public static final int OffLine = 0x400000;

        public static final int RADIO2 = 0x1;
    }

    private boolean GetStatus(long detail,int status) {
        if ((detail & status) == status)
            return true;

        return false;
    }

    public DeviceDataStruct xmlToBean(FindDeviceInfo fdi) {
        DeviceDataStruct deviceDataStruct = new DeviceDataStruct(fdi.getMode());
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

    public static DeviceDataStruct xmlToBean(String ip,int port,Msg_Body_Struct msg) {
        DeviceDataStruct deviceInfo;

        String sn = GetMsgStringValueInList("sn",msg.dic,"");
        String mode = MODE.NOMODE;
        if (Utils.isDebugVersion()) {
            mode = GetMsgStringValueInList("mode", msg.dic, MODE.LTE_TDD);
        } else {
            mode = GetMsgStringValueInList("mode", msg.dic, MODE.NOMODE);
        }

        mode = mode.replace("-","_");
        if (MODE.NOMODE.equals(String2Mode(mode))) {
            Logs.e(TAG,String.format("设备%s[%s:%d]心跳消息中Mode类型(%s)错误。",sn,ip,port,mode));
            return null;
        }

        deviceInfo = new DeviceDataStruct(String2Mode(mode));
        deviceInfo.setIp(ip);
        deviceInfo.setPort(port);
        deviceInfo.setMode(String2Mode(mode));
        deviceInfo.setSN(sn);
        deviceInfo.setLastTime(System.currentTimeMillis());
        deviceInfo.setDeviceTime(FindMsgStruct.GetMsgStringValueInList("timestamp",msg.dic,"1970-01-01 08:00:00"));

        deviceInfo.setFullName(FindMsgStruct.GetMsgStringValueInList("fullname",msg.dic,""));
        deviceInfo.setVersion(FindMsgStruct.GetMsgStringValueInList("version",msg.dic,""));
        String sd = FindMsgStruct.GetMsgStringValueInList("detail",msg.dic,"0x0");
        Long detail = Long.valueOf(sd.replace("0x","").replace("0X",""),16);
        deviceInfo.setDetail(detail);
        deviceInfo.setAddStatus(FindMsgStruct.GetMsgIntValueInList("addStatus",msg.dic,0));

        return deviceInfo;
    }

    public int getAddStatus()
    {
        return iAddStatus;
    }

    public void setAddStatus(int iAddStatus)
    {
        if (iAddStatus >= 6) iAddStatus = 7;
        this.iAddStatus = iAddStatus;
    }

    public String getDeviceTime()
    {
        return sDeviceTime;
    }

    public void setDeviceTime(String sDeviceTime)
    {
        this.sDeviceTime = sDeviceTime;
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

        this.status_sctp = GetStatus(detail,AP_STATUS_LTE.SCTP);
        this.status_s1 = GetStatus(detail,AP_STATUS_LTE.S1);
        this.status_gps = GetStatus(detail,AP_STATUS_LTE.GPS);
        this.status_cell = GetStatus(detail,AP_STATUS_LTE.CELL);
        this.status_sync = GetStatus(detail,AP_STATUS_LTE.SYNC);
        this.status_licens = GetStatus(detail,AP_STATUS_LTE.LICENS);
        this.status_radio = GetStatus(detail,AP_STATUS_LTE.RADIO);
        this.status_offline = GetStatus(detail,AP_STATUS_LTE.OffLine);
        this.status_wSelf = GetStatus(detail,AP_STATUS_LTE.wSelfStudy);
        this.status_redio2 = GetStatus(detail,AP_STATUS_LTE.RADIO2);
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

    public boolean isStatus_sctp() {
        return status_sctp;
    }

    public boolean isStatus_s1() {
        return status_s1;
    }

    public boolean isStatus_gps() {
        return status_gps;
    }

    public boolean isStatus_cell() {
        return status_cell;
    }

    public boolean isStatus_sync() {
        return status_sync;
    }

    public boolean isStatus_licens() {
        return status_licens;
    }

    public boolean isStatus_offline() {
        return status_offline;
    }

    public boolean isStatus_wSelf() {
        return status_wSelf;
    }

    public boolean isStatus_redio2() {
        return status_redio2;
    }

    public boolean isStatus_radio() {
        return status_radio;
    }

    public Object getGeneralPara() {
        return generalPara;
    }

    public void setGeneralPara(Object generalPara) {
        this.generalPara = generalPara;
    }
}