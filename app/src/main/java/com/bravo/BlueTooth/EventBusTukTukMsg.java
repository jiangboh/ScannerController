package com.bravo.BlueTooth;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Jack.liao on 2017/11/9.
 */

public class EventBusTukTukMsg {
    public static final String BT_START_SCAN = "BT_START_SCAN";//BluetoothAdapter.ACTION_DISCOVERY_STARTED;
    public static final String BT_STOP_SCAN = "BT_STOP_SCAN";//BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
    public static final String BT_SCAN_RESULT = "BT_SCAN_RESULT";
    public static final String BT_CONNECT_REQUEST = "BT_CONNECT_REQUEST";
    public static final String BT_DISCONNECT_REQUEST = "BT_DISCONNECT_REQUEST";
    public static final String BT_STATUS_NOCONNECT = "BT_STATUS_NOCONNECT";
    public static final String BT_STATUS_ALREADY_CONNECT = "BT_STATUS_ALREADY_CONNECT";
    public static final String BT_DISCONNECT_NOTIF = "BT_DISCONNECT_NOTIF";
    public static final String BT_CONNECT_SUCCESS = "BT_CONNECT_SUCCESS";
    public static final String BT_CONNECT_FAILURE = "BT_CONNECT_FAILURE";
    public static final String BT_NONSUPPORT = "BT_NONSUPPORT";
    public static final String BT_WRITE = "BT_WRITE";
    public static final String BT_WRITE_RESULT = "BT_WRITE_RESULT";
    public static final String BT_WRITE_SUCCESS = "BT_WRITE_SUCCESS";
    public static final String BT_READ = "BT_READ";
    public static final String BT_RECEIVER = "BT_RECEIVER";
    public static final String SERVICE_UUID = "05845767-7705-4449-4944-abcdef000001";
    public static final String CHARACTERISTIC_UUID2 = "05845767-7705-4449-4944-abcdef000002";
    public static final String CHARACTERISTIC_UUID3 = "05845767-7705-4449-4944-abcdef000003";
    public static final String CHARACTERISTIC_UUID4 = "05845767-7705-4449-4944-abcdef000004";
    public static final String CHARACTERISTIC_UUID5 = "05845767-7705-4449-4944-abcdef000005";
    public static final String CHARACTERISTIC_UUID6 = "05845767-7705-4449-4944-abcdef000006";
    public static final String CHARACTERISTIC_UUID7 = "05845767-7705-4449-4944-abcdef000007";
    public static final String BT_GET_DEVICE_REQUEST = "BT_GET_DEVICE_REQUEST";
    public static final String BT_GET_DEVICE_RESPONSE = "BT_GET_DEVICE_RESPONSE";
    public static final String BT_INIT_SUCCESS = "BT_INIT_SUCCESS";
    public static final String BT_INIT_REQUEST = "BT_INIT_REQUEST";
    private BluetoothDevice bluetoothDevice;
    private byte[] bytes;
    private String strUUID;
    private String strAction;
    private String strResult;

    public EventBusTukTukMsg(String action) {
        this.strAction = action;
    }

    public EventBusTukTukMsg(String strAction, BluetoothDevice bluetoothDevice) {
        this.strAction = strAction;
        this.bluetoothDevice = bluetoothDevice;
    }
    public EventBusTukTukMsg(String strAction, String strUUID) {
        this.strAction = strAction;
        this.strUUID = strUUID;
    }
    public EventBusTukTukMsg(String strAction, String strUUID, byte[] bytes) {
        this.strAction = strAction;
        this.bytes = bytes;
        this.strUUID = strUUID;
    }
    public EventBusTukTukMsg(String strAction, String strUUID, String strResult) {
        this.strAction = strAction;
        this.strUUID = strUUID;
        this.strResult = strResult;
    }
    public String getAction() {
        return strAction;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getUUID() {
        return strUUID;
    }

    public String getStrResult() {
        return strResult;
    }

    //    static class Builder{
//        private String btMsg;
//
//        public Builder btMsg(String strAction) {
//            this.btMsg = btMsg;
//            return this;
//        }
//        public EventBusTukTukMsg build(){
//            return new EventBusTukTukMsg(this);
//        }
//    }
}
