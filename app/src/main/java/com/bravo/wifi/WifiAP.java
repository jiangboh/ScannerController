package com.bravo.wifi;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.bravo.data_ben.FemtoDataStruct;
import com.bravo.utils.Logs;
import com.bravo.utils.SharePreferenceUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by Jack.liao on 2016/10/11.
 */

public class WifiAP {
    private final static String TAG = "WifiAP";
    //wifi ap state parameter
    public static final int WIFI_AP_STATE_DISABLING = 10;
    public static final int WIFI_AP_STATE_DISABLED = 11;
    public static final int WIFI_AP_STATE_ENABLING = 12;
    public static final int WIFI_AP_STATE_ENABLED = 13;
    public static final int WIFI_AP_STATE_FAILED = 14;
    private final String MATCH_ADDRESS1 = "7E:CD";
    private final String MATCH_ADDRESS2 = "7C:CD";
    private Context context;
    public WifiAP(Context context) {
        this.context = context;
    }
    //get wifi ap state
    public int GetWifiApState() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApState");
            int i = (Integer) method.invoke(wifiManager);
            Log.d(TAG, "wifi state:  " + i);
            return i;
        } catch (Exception e) {
            Log.d(TAG, "Cannot get WiFi AP state" + e);
            return WIFI_AP_STATE_FAILED;
        }
    }

    //check wifi ap Enabled
    public boolean isApEnabled() {
//        int state = getWifiApState(mContext);
//        return WIFI_AP_STATE_ENABLING == state || WIFI_AP_STATE_ENABLED == state;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifiManager.getClass().getMethod("isWifiApEnabled");
            return (boolean) method.invoke(wifiManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void getWifiConfig() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration wifiConfiguration = (WifiConfiguration) method.invoke(wifiManager);
            Logs.d(TAG, "BSSID=" + wifiConfiguration.BSSID);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<FemtoDataStruct> getIpList() {
        ArrayList<FemtoDataStruct> femtoDataStructArrayList = new ArrayList<FemtoDataStruct>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 6 && splitted[0].contains("1")) {
                    SharePreferenceUtils.getInstance(context).setString(splitted[0], splitted[3]);
                    if (splitted[3].toUpperCase().indexOf(MATCH_ADDRESS1.toUpperCase()) != -1 ||
                            splitted[3].toUpperCase().indexOf(MATCH_ADDRESS2.toUpperCase()) != -1 ||
                            splitted[5].equals("bt-pan")) {
                    /*if (StartPing(splitted[0]) && splitted[3].toUpperCase().indexOf(MATCH_ADDRESS.toUpperCase()) != -1) {*/
//                        Log.d(TAG, line);
                        FemtoDataStruct femtoDataStruct = new FemtoDataStruct();
                        femtoDataStruct.setSSID(splitted[0]);
                        femtoDataStruct.setIPAddress(splitted[0]);
                        femtoDataStruct.setMacAddress(splitted[3]);
                        femtoDataStruct.setStrDeviceType(splitted[5]);
                        femtoDataStructArrayList.add(femtoDataStruct);

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return femtoDataStructArrayList;
    }

    public String getGateway() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4 && splitted[0].contains("1")) {
                    if (splitted[0].substring(9,10).equals(".")) {
                        return (splitted[0].substring(0,10) + "1");
                    } else if (splitted[0].substring(10,11).equals(".")) {
                        return (splitted[0].substring(0,11) + "1");
                    } else if (splitted[0].substring(11,12).equals(".")) {
                        return (splitted[0].substring(0,12) + "1");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean StartPing(String strAddress) {
        boolean success = false;
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("ping -c 1 -i 0.2 -W 1 " +strAddress);
            int status = p.waitFor();
            if (status == 0) {
                success=true;
            } else {
                success=false;
            }
        } catch (IOException e) {
            success=false;
        } catch (InterruptedException e) {
            success=false;
        } finally {
            p.destroy();
        }
        return success;
    }
}
