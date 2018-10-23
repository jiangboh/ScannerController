package com.bravo.xml;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import com.bravo.Find.FragmentFind;
import com.bravo.R;
import com.bravo.config.Fragment_Device;
import com.bravo.data_ben.DeviceDataStruct;
import com.bravo.data_ben.DeviceFragmentStruct;
import com.bravo.parse_generate_xml.Find.FindDeviceInfo;
import com.bravo.socket_service.EventBusMsgRecvXmlMsg;
import com.bravo.utils.Logs;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;

import static com.bravo.xml.XmlCodec.DecodeApXmlMessage;

/**
 * Created by admin on 2018-9-19.
 */


public class HandleRecvXmlMsg {
    private final String TAG = "HandleRecvXmlMsg";
    Context mContext;
    private DeviceDataStruct deviceDataStruct;

    public static final int LTE_CELL_CONFIG = 0;
    public static final int LTE_SON_CONFIG = 1;
    public static final int LTE_WORKE_MODE = 2;
    public static final int LTE_OTHER_PLMN = 3;
    public static final int LTE_PERIOD_FREQ = 4;
    public static final int LTE_SYSTEM_SET = 5;
    public static final int LTE_SYNC_SET = 6;
    public static final int MAX_CONFIG = 7;



    public HandleRecvXmlMsg(Context context) {
        DeviceDataStruct deviceDataStruct = new DeviceDataStruct();
        this.mContext = context;
    }

    public HandleRecvXmlMsg(Context context, DeviceDataStruct dds) {
        this.mContext = context;
        this.deviceDataStruct = dds;
    }

    public void HandleRecvMsg(EventBusMsgRecvXmlMsg RecvMsg) {
        String ip = RecvMsg.getIp();
        int port = RecvMsg.getPort();
        String result = RecvMsg.getMsg();

        Msg_Body_Struct msg = DecodeApXmlMessage(result);
        if (msg == null) {
            Logs.d(TAG, "解析消息出错:\n" + result, true);
            return;
        }

       /* Logs.d(TAG,"接收消息id：" + msg.msgId);
        Logs.d(TAG,"接收消息类型：" + msg.type);
        for (Map.Entry<String, Object> kvp : msg.dic.entrySet())
        {
            Logs.d(TAG,"接收消息内容=" + kvp.getKey() + "；值=" + kvp.getValue());
        }*/

        //广播消息
        if (msg.type.equalsIgnoreCase(Msg_Body_Struct.BroadCast_result)) {
            EventBus.getDefault().post(FindDeviceInfo.xmlToBean(msg));
            return;
        }

        int index = DeviceFragmentStruct.inListIndex(ip, port);

        //心跳消息
        if (msg.type.equalsIgnoreCase(Msg_Body_Struct.status_response)) {
            DeviceDataStruct deviceInfo = DeviceDataStruct.xmlToBean(ip, port, msg);
            if (deviceInfo == null) {
                Logs.e(TAG, String.format("设备%s[%s:%d]心跳消息中参数错误。", ip, port), true);
                return;
            }
            DeviceFragmentStruct.addList(deviceInfo);
            if (Fragment_Device.isOpen) {
                //更新设备列表界面
                EventBus.getDefault().post(deviceInfo);
            }

            if (FragmentFind.isOpen) {
                //更新设备搜索界面
                EventBus.getDefault().post(msg);
            }
            if (deviceInfo.isStatus_offline()) {
                Logs.e(TAG, String.format("第一次启动，发送获取参数消息!"));
                if (deviceInfo.getMode().equals(DeviceDataStruct.MODE.LTE_TDD) ||
                        deviceInfo.getMode().equals(DeviceDataStruct.MODE.LTE_FDD) ||
                        deviceInfo.getMode().equals(DeviceDataStruct.MODE.WCDMA)) {
                    new LTE(mContext).SendGeneralParaRequest(deviceInfo.getIp(), deviceInfo.getPort());
                } else if (deviceInfo.getMode().equals(DeviceDataStruct.MODE.CDMA) ||
                        deviceInfo.getMode().equals(DeviceDataStruct.MODE.GSM_V2)) {

                } else if (deviceInfo.getMode().equals(DeviceDataStruct.MODE.GSM)) {

                } else {
                    Logs.e(TAG, String.format("产品Mode为%s,目前不支持该类型!", deviceInfo.getMode()));
                }
            }
            return;
        }

        if (index == -1) {
            Logs.d(TAG, String.format("等待设备[%s:%d]心跳消息。", ip, port), true);
            return;
        } else {
            DeviceFragmentStruct.setListLastTime(index, System.currentTimeMillis());
            deviceDataStruct = DeviceFragmentStruct.getDevice(index);
            Logs.d(TAG, String.format("设备[%s:%d]型号(%s),消息类型(%s)", deviceDataStruct.getIp(), deviceDataStruct.getPort(), deviceDataStruct.getMode(), msg.type), true);
            if (deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.LTE_TDD) || deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.LTE_FDD) || deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.WCDMA)) {
                new LTE(mContext).HandleMsg(deviceDataStruct, msg);
            } else if (deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.CDMA) || deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.GSM_V2)) {
                new GSM_ZYF(mContext).HandleMsg(deviceDataStruct, msg);
            } else if (deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.GSM)) {
                new GSM_HJT(mContext).HandleMsg(deviceDataStruct, msg);
            } else {
                Logs.e(TAG, String.format("设备类型(%s)为不支持的设备类型！", deviceDataStruct.getMode()), true);
            }
        }
    }

    public boolean SetDeviceReboot() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setIcon(R.drawable.icon_error_errors);//设置图标
        builder.setTitle("重启设备");//设置对话框的标题
        builder.setMessage(String.format("确定要重启设备:\n%s", deviceDataStruct.getSN()));//设置对话框的内容
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  //这个是设置确定按钮
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.LTE_FDD) || deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.LTE_TDD)
                        || deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.WCDMA)) {
                    new LTE(mContext).SetApReboot(deviceDataStruct.getIp(), deviceDataStruct.getPort(), 1);
                    Toast.makeText(mContext, "重启" + deviceDataStruct.getSN(), Toast.LENGTH_SHORT).show();
                } else if (deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.GSM_V2) || deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.CDMA)) {
                    new GSM_ZYF(mContext).SetApReboot(deviceDataStruct.getIp(), deviceDataStruct.getPort(), 1);
                    Toast.makeText(mContext, "重启" + deviceDataStruct.getSN(), Toast.LENGTH_SHORT).show();
                } else if (deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.GSM)) {
                    Toast.makeText(mContext, String.format("设备类型为%s,不支持重启" + deviceDataStruct.getMode()), Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  //取消按钮
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });

        AlertDialog b = builder.create();
        b.show();  //必须show一下才能看到对话框，跟Toast一样的道理

        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object alertController = mAlert.get(b);

            Field mTitleView = alertController.getClass().getDeclaredField("mTitleView");
            mTitleView.setAccessible(true);
            TextView title = (TextView) mTitleView.get(alertController);
            title.setTextColor(Color.RED);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        b.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        b.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLUE);

        return true;
    }

    public boolean SetDeviceRedio(int sys, Boolean isOn) {
        String ip = deviceDataStruct.getIp();
        int port = deviceDataStruct.getPort();

        if (deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.LTE_FDD) || deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.LTE_TDD)
                || deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.WCDMA)) {
            int mode = 1;
            new LTE(mContext).SetApRedio(ip, port, mode, isOn);
        } else if (deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.GSM_V2) || deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.CDMA)) {
            if (sys == 1) {
                new GSM_ZYF(mContext).SetApRedio(ip, port, GSM_ZYF.Sys2, isOn);
            } else {
                new GSM_ZYF(mContext).SetApRedio(ip, port, GSM_ZYF.Sys1, isOn);
            }
        } else if (deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.GSM)) {
            if (sys == 1) {
                new GSM_HJT(mContext).SetApRedio(ip, port, GSM_HJT.Sys2, isOn);
            } else {
                new GSM_HJT(mContext).SetApRedio(ip, port, GSM_HJT.Sys1, isOn);
            }
        }

        return true;
    }

    public boolean SetDeviceParameter(String name,String value) {
        String ip = deviceDataStruct.getIp();
        int port = deviceDataStruct.getPort();

        if (deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.LTE_FDD) || deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.LTE_TDD)
                || deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.WCDMA)) {
            new LTE(mContext).SetApParameter(ip, port, name,value);
        } else if (deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.GSM_V2) || deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.CDMA)) {
            new GSM_ZYF(mContext).SetApParameter(ip, port, name,value);
        } else if (deviceDataStruct.getMode().equals(DeviceDataStruct.MODE.GSM)) {
            new GSM_HJT(mContext).SetApParameter(ip, port, name,value);
        }

        return true;
    }
}