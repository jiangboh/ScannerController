package com.bravo.data_ben;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

/**
 * Created by Jack.liao on 2016/11/8.
 */

public class TargetDataStruct {
    private final String TAG = "TargetDataStruct";
    private String strName;
    private String FullName ="";
    private String SN ="";
    private String IP ="";
    private int Port;
    private int rsrp;
    private String strImsi ="";
    private String strImei ="";
    private String strTmsi ="";
    private boolean bCheckbox = false;
    private int iUserType = 0;//0普通名用户 1鉴权用户 2targetlist存在
    private boolean bSilentState = false;
    private String strLongitude=""; //经度
    private String strLatitude=""; //纬度
    private String strConntime;
    private String strAttachtime;
    private String strDetachtime;
    private int iDistance;
    private int iSignal;
    private int iCount = 1;
    private ArrayList<Entry> distances = new ArrayList<>();
    private ArrayList<Entry> signals = new ArrayList<>();
    //new target list tag
    private String strTech;
    private String strBand;
    private String strChannel;
    private boolean bRedir;
    private boolean bPositionStatus = false;//判断position消息出否上报，用于处理异常断开未收到detach消息的用户

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getPort() {
        return Port;
    }

    public void setPort(int port) {
        Port = port;
    }

    public int getRsrp() {
        return rsrp;
    }

    public void setRsrp(int rsrp) {
        this.rsrp = rsrp;
    }

    public TargetDataStruct(){

    }
    public void setName(String strName) {
        this.strName = strName;
    }
    public String getName() {
        return strName;
    }

    public void setImsi(String strImsi) {
        this.strImsi = strImsi;
    }
    public String getImsi() {
        return strImsi;
    }
    public void setTmsi(String strTmsi) {
        this.strTmsi = strTmsi;
    }
    public String getTmsi() {
        return strTmsi;
    }
    public void setCheckbox(boolean bCheckbox) {
        this.bCheckbox = bCheckbox;
    }
    public boolean getCheckbox() {
        return bCheckbox;
    }

    public void setImei(String srtImei) {
        this.strImei = srtImei;
    }
    public String getImei() {
        return strImei;
    }

    public void setiUserType(int iAuthState) {
        this.iUserType = iAuthState;
    }
    public int getiUserType() {
        return iUserType;
    }

    public void setSilentState(boolean bSilentState) {
        this.bSilentState = bSilentState;
    }
    public boolean getSilentState() {
        return bSilentState;
    }

    public String getStrLongitude() {
        return strLongitude;
    }

    public void setStrLongitude(String strLongitude) {
        this.strLongitude = strLongitude;
    }

    public String getStrLatitude() {
        return strLatitude;
    }

    public void setStrLatitude(String strLatitude) {
        this.strLatitude = strLatitude;
    }

    public String getStrConntime() {
        return strConntime;
    }

    public void setStrConntime(String strConntime) {
        this.strConntime = strConntime;
    }

    public String getStrAttachtime() {
        return strAttachtime;
    }

    public void setStrAttachtime(String strAttachtime) {
        this.strAttachtime = strAttachtime;
    }

    public ArrayList<Entry> getDistances() {
        return distances;
    }

    public int getDistance() {
        return iDistance;
    }

    public void setDistance(int iDistance) {
        this.iDistance = iDistance;
    }

    public int getCount() {
        return iCount;
    }

    public void setCount(int iCount) {
        this.iCount = iCount;
    }

    public ArrayList<Entry> getSignals() {
        return signals;
    }

    public int getSignal() {
        return iSignal;
    }

    public void setSignal(int iSignal) {
        this.iSignal = iSignal;
    }

    public String getStrDetachtime() {
        return strDetachtime;
    }

    public void setStrDetachtime(String strDetachtime) {
        this.strDetachtime = strDetachtime;
    }

    public String getStrTech() {
        return strTech;
    }

    public void setStrTech(String strTech) {
        this.strTech = strTech;
    }

    public String getStrBand() {
        return strBand;
    }

    public void setStrBand(String strBand) {
        this.strBand = strBand;
    }

    public String getStrChannel() {
        return strChannel;
    }

    public void setStrChannel(String strChannel) {
        this.strChannel = strChannel;
    }

    public boolean isbRedir() {
        return bRedir;
    }

    public void setbRedir(boolean bRedir) {
        this.bRedir = bRedir;
    }

    public boolean isbPositionStatus() {
        return bPositionStatus;
    }

    public void setbPositionStatus(boolean bPositionStatus) {
        this.bPositionStatus = bPositionStatus;
    }

    @Override
    public String toString() {
        return "TargetDataStruct{" +
                "strName='" + strName + '\'' +
                ", FullName='" + FullName + '\'' +
                ", strImsi='" + strImsi + '\'' +
                ", strImei='" + strImei + '\'' +
                ", strTmsi='" + strTmsi + '\'' +
                ", bCheckbox=" + bCheckbox +
                ", iUserType=" + iUserType +
                ", bSilentState=" + bSilentState +
                ", strLongitude='" + strLongitude + '\'' +
                ", strLatitude='" + strLatitude + '\'' +
                ", strConntime='" + strConntime + '\'' +
                ", strAttachtime='" + strAttachtime + '\'' +
                ", strDetachtime='" + strDetachtime + '\'' +
                ", iDistance=" + iDistance +
                ", iSignal=" + iSignal +
                ", iCount=" + iCount +
                ", distances=" + distances +
                ", signals=" + signals +
                ", strTech='" + strTech + '\'' +
                ", strBand='" + strBand + '\'' +
                ", strChannel='" + strChannel + '\'' +
                ", bRedir=" + bRedir +
                ", bPositionStatus=" + bPositionStatus +
                '}';
    }
}
