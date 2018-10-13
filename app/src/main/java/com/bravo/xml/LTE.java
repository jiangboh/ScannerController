package com.bravo.xml;

import android.content.Context;

import com.bravo.adapters.AdapterScanner;
import com.bravo.data_ben.DeviceDataStruct;
import com.bravo.data_ben.TargetDataStruct;
import com.bravo.scanner.FragmentScannerListen;
import com.bravo.utils.Logs;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by admin on 2018-9-29.
 */

public class LTE {
    private final String TAG = "LTE";
    Context mContext;

    public LTE(Context context) {
        this.mContext = context;
    }

    public void HandleMsg(DeviceDataStruct dds, Msg_Body_Struct msg) {
        if (msg.type.equalsIgnoreCase(Msg_Body_Struct.scanner)) {
            TargetDataStruct targetDataStruct = new TargetDataStruct();

            targetDataStruct.setSN(dds.getSN());
            targetDataStruct.setIP(dds.getIp());
            targetDataStruct.setPort(dds.getPort());
            targetDataStruct.setFullName(dds.getFullName());
            targetDataStruct.setDeviceType(dds.getMode());

            targetDataStruct.setImsi(FindMsgStruct.GetMsgStringValueInList("imsi", msg.dic, ""));
            targetDataStruct.setiUserType(FindMsgStruct.GetMsgIntValueInList("userType", msg.dic, 0));
            //Logs.d(TAG,"用户类型：" + targetDataStruct.getiUserType());
            targetDataStruct.setImei(FindMsgStruct.GetMsgStringValueInList("imei", msg.dic, ""));
            targetDataStruct.setTmsi(FindMsgStruct.GetMsgStringValueInList("tmsi", msg.dic, ""));
            targetDataStruct.setRsrp(FindMsgStruct.GetMsgIntValueInList("rsrp", msg.dic, 0));
            targetDataStruct.setbPositionStatus(true);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
            targetDataStruct.setStrAttachtime(formatter.format(new Date()));

            //if (!FragmentScannerListen.isStart) return;

            if (FragmentScannerListen.isOpen) {
                //更新界面
                EventBus.getDefault().post(targetDataStruct);
            } else {
                //保存到列表
                AdapterScanner.AddScannerImsi(targetDataStruct);
            }

            return;
        } else {
            Logs.e(TAG, String.format("消息类型(%s)为不支持的消息类型！", msg.type));
        }
    }
}
