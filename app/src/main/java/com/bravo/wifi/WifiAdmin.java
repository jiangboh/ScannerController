package com.bravo.wifi;

/**
 * Created by Jack.liao on 2016/8/17.
 */
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

public class WifiAdmin {
    private final static String TAG = "WifiAdmin";
    //定义一个WifiManager对象
    private WifiManager mWifiManager;
    //定义一个WifiInfo对象
    private WifiInfo mWifiInfo;
    //扫描出的网络连接列表
    private List<ScanResult> mWifiList;
    //网络连接列表
    private List<WifiConfiguration> mWifiConfigurations;
    private WifiLock mWifiLock;
    private Context context;
    public WifiAdmin(Context context){
        this.context = context;
        //取得WifiManager对象
        mWifiManager=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //取得WifiInfo对象
        mWifiInfo=mWifiManager.getConnectionInfo();
    }
    //打开wifi
    public void openWifi(){
        if(!mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(true);
        }
    }
    //关闭wifi
    public void closeWifi(){
        if(!mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(false);
        }
    }
    // 检查当前wifi状态
    public int GetWifiState() {
        return mWifiManager.getWifiState();
    }
    //锁定wifiLock
    public void acquireWifiLock(){
        mWifiLock.acquire();
    }
    //解锁wifiLock
    public void releaseWifiLock(){
        //判断是否锁定
        if(mWifiLock.isHeld()){
            mWifiLock.acquire();
        }
    }
    //创建一个wifiLock
    public void createWifiLock(){
        mWifiLock=mWifiManager.createWifiLock("test");
    }
    //得到配置好的网络
    public List<WifiConfiguration> getConfiguration(){
        return mWifiConfigurations;
    }
    //指定配置好的网络进行连接
    public void connetionConfiguration(int index){
        if(index > mWifiConfigurations.size()){
            return;
        }
        //连接配置好指定ID的网络
        mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId, true);
    }
    public void startScan(){
        mWifiManager.startScan();
    }
    //得到网络列表
    public List<ScanResult> getWifiList(){
        //得到扫描结果
        mWifiList=mWifiManager.getScanResults();
        //得到配置好的网络连接
        mWifiConfigurations = mWifiManager.getConfiguredNetworks();
        return mWifiList;
    }
    //查看扫描结果
    public StringBuffer lookUpScan(){
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<mWifiList.size();i++){
            //Log.e(TAG, sb.toString());
            sb.append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            sb.append((mWifiList.get(i)).toString()).append("\n");
        }
        return sb;
    }
    public  String getGateway() {
        return long2ip(mWifiManager.getDhcpInfo().gateway);
    }

    public String getSubGateway() {
        return IPtoString(mWifiManager.getDhcpInfo().netmask);
    }

    public String getLocalMacAddress(){
        return (mWifiInfo==null)?"NULL":mWifiInfo.getMacAddress();
    }
    public String getBSSID(){
        //ap mac address
        return (mWifiInfo==null)?"NULL":mWifiInfo.getBSSID();
    }
    //wifi name
    public String getSSID() {
        return (mWifiInfo==null)?"NULL":mWifiInfo.getSSID();
    }

    public String getIpAddress() { return (mWifiInfo==null)?null:IPtoString(mWifiInfo.getIpAddress()); }
    //得到连接的ID
    public int getNetWorkId(){
        return (mWifiInfo==null)?0:mWifiInfo.getNetworkId();
    }
    //得到wifiInfo的所有信息
    public WifiInfo getWifiInfo(){
        //return (mWifiInfo==null)?"NULL":mWifiInfo.toString();
        return (mWifiManager.getConnectionInfo()==null)?null:mWifiManager.getConnectionInfo();
    }
    public int getWifiLevel() {
        return mWifiInfo.getRssi();
    }
    //添加一个网络并连接
    public int addNetWork(WifiConfiguration configuration){
        int wcgId = mWifiManager.addNetwork(configuration);
        if (wcgId != -1) {
            mWifiManager.enableNetwork(wcgId, true);
        }
        return wcgId;
    }
    //断开指定ID的网络
    public void disConnectionWifi(int netId){
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }
    //删除
    public void RemoveWifi(int netId) {
        mWifiManager.removeNetwork(mWifiConfigurations.get(netId).networkId);
    }

    //判定指定WIFI是否已经配置好,依据WIFI的地址BSSID,返回mWifiConfigurations item
    public int IsConfiguration(String SSID){
        Log.d(TAG, "Ready connect SSID=" + SSID + ", mWifiConfigurations.size()=" + mWifiConfigurations.size());
        for(int i = 0; i < mWifiConfigurations.size(); i++){
            Log.d(TAG, "mWifiConfigurations.get(" +i + ")" + ",SSID=" + mWifiConfigurations.get(i).SSID);
            if(mWifiConfigurations.get(i).SSID.equals(SSID)){//same address
                return i;//mWifiConfigurations.get(i).networkId;
            }
        }
        return -1;
    }
    //判断wifi状态
    public boolean isWifiConnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetworkInfo.isConnected())
        {
            return true ;
        }
        return false ;
    }

    public int getSecurityMode(ScanResult result) {
        Log.d(TAG, "result.capabilities=" + result.capabilities);
        if (result.capabilities.contains("WEP")) {
            return 1;
        } else if (result.capabilities.contains("PSK")) {
            return 2;
        } else if (result.capabilities.contains("EAP")) {
            return 3;
        }
        return 0;
    }

    public WifiConfiguration CreateWifiInfo(String SSID, String password, int SecurityMode) {
        //config network info
        WifiConfiguration wificonfig = new WifiConfiguration();
        //setting network attribute
        wificonfig.allowedAuthAlgorithms.clear();
        wificonfig.allowedGroupCiphers.clear();
        wificonfig.allowedKeyManagement.clear();
        wificonfig.allowedPairwiseCiphers.clear();
        wificonfig.allowedProtocols.clear();
        wificonfig.SSID = "\""+SSID+"\"";//\"转义字符，代表"
        switch (SecurityMode) {
            case 0:
                RemoveWifi(IsConfiguration("\""+SSID+"\""));;
                //mWifiManager.removeNetwork()
               // wificonfig.wepKeys[0] = "";
                wificonfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                //wificonfig.wepTxKeyIndex = 0;
                break;
            default:
                wificonfig.preSharedKey = "\""+"Mt456$%^963852741,.9"+"\"";//WPA-PSK password
                wificonfig.hiddenSSID = false;
                wificonfig.status = WifiConfiguration.Status.ENABLED;
                break;
        }
        return wificonfig;
    }

    //ip type to String type
    private String IPtoString(int iAddress) {
        return (iAddress & 0xFF ) + "." +
                ((iAddress >> 8 ) & 0xFF) + "." +
                ((iAddress >> 16 ) & 0xFF) + "." +
                (iAddress >> 24 & 0xFF) ;
    }

    String long2ip(long ip){
        StringBuffer sb=new StringBuffer();
        sb.append(String.valueOf((int)(ip&0xff)));
        sb.append('.');
        sb.append(String.valueOf((int)((ip>>8)&0xff)));
        sb.append('.');
        sb.append(String.valueOf((int)((ip>>16)&0xff)));
        sb.append('.');
        return sb.toString();
    }
}

