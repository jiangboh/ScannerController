package com.bravo.BlueTooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.bravo.custom_view.CustomToast;
import com.bravo.utils.Logs;

/**
 * Created by lenovo on 2017/9/8.
 */

public class BlueToothReceiver extends BroadcastReceiver {
    public BlueToothReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch(intent.getAction()){
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch(blueState){
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Logs.d("123456","打开蓝牙");
                        CustomToast.showToast(context,"打开蓝牙",Toast.LENGTH_LONG);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Logs.d("123456","蓝牙开启状态");
                        CustomToast.showToast(context,"蓝牙已开启",Toast.LENGTH_LONG);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Logs.d("123456","关闭蓝牙");
                        CustomToast.showToast(context,"关闭蓝牙",Toast.LENGTH_LONG);
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Logs.d("123456","蓝牙关闭状态");
                        CustomToast.showToast(context,"蓝牙已关闭",Toast.LENGTH_LONG);
                        break;
                }
                break;
            case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                Logs.d("123456", "device name: " + name);
                int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                switch (state) {
                    case BluetoothDevice.BOND_NONE:
                        Logs.d("123456", "BOND_NONE 删除配对");
                        CustomToast.showToast(context,"删除配对",Toast.LENGTH_LONG);
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        Logs.d("123456", "BOND_BONDING 正在配对");
                        CustomToast.showToast(context,"正在配对",Toast.LENGTH_LONG);
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Logs.d("123456", "BOND_BONDED 配对成功");
                        CustomToast.showToast(context,"删除配对",Toast.LENGTH_LONG);
                        break;
                }
                break;
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                BluetoothDevice device1 = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Logs.d("123456", device1.getName() + "已建立串口仿真协议");
                CustomToast.showToast(context,device1.getName() + "已建立串口仿真协议",Toast.LENGTH_LONG);
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                BluetoothDevice device2 = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Logs.d("123456", device2.getName() + " 已断开串口仿真协议");
                CustomToast.showToast(context,device2.getName() + " 已断开串口仿真协议",Toast.LENGTH_LONG);
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED:
                BluetoothDevice device3 = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Logs.d("123456", device3.getName() + " 请求断开串口仿真协议");
                CustomToast.showToast(context,device3.getName() + " 请求断开串口仿真协议",Toast.LENGTH_LONG);
                break;
            case BluetoothDevice.ACTION_CLASS_CHANGED:
                Logs.d("123456", "类变更");
                CustomToast.showToast(context,"类变更",Toast.LENGTH_LONG);
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                Logs.d("123456", "开始扫描");
                CustomToast.showToast(context,"开始扫描",Toast.LENGTH_LONG);
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                Logs.d("123456", "扫描结束");
                CustomToast.showToast(context,"扫描结束",Toast.LENGTH_LONG);
                break;
           case BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED:
                Logs.d("123456", "本地设备名称改变");
                CustomToast.showToast(context,"本地设备名称改变",Toast.LENGTH_LONG);
                break;
            case BluetoothAdapter.ACTION_SCAN_MODE_CHANGED:
                Logs.d("123456", "扫描模式改变");
                CustomToast.showToast(context,"扫描模式改变",Toast.LENGTH_LONG);
                break;
            case BluetoothDevice.ACTION_FOUND:
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Logs.d("123456", "Name=" + device.getName() + ",address=" + device.getAddress() + ",uuids=" + device.getUuids() + ",type" + device.getType());
                  //这个就是所获得的蓝牙设备。
                break;
            /*android.bluetooth.a2dp.action.SINK_STATE_CHANGED
            android.bluetooth.adapter.action.DISCOVERY_FINISHED
            android.bluetooth.adapter.action.DISCOVERY_STARTED
            android.bluetooth.adapter.action.LOCAL_NAME_CHANGED
            android.bluetooth.adapter.action.SCAN_MODE_CHANGED
            android.bluetooth.adapter.action.STATE_CHANGED
            android.bluetooth.device.action.ACL_CONNECTED
            android.bluetooth.device.action.ACL_DISCONNECTED
            android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED
            android.bluetooth.device.action.BOND_STATE_CHANGED
            android.bluetooth.device.action.CLASS_CHANGED
            android.bluetooth.device.action.FOUND
            android.bluetooth.device.action.NAME_CHANGED
            android.bluetooth.devicepicker.action.DEVICE_SELECTED
            android.bluetooth.devicepicker.action.LAUNCH
            android.bluetooth.headset.action.AUDIO_STATE_CHANGED
            android.bluetooth.headset.action.STATE_CHANGED*/
        }
    }
}
