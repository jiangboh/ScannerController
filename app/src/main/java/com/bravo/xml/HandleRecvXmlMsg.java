package com.bravo.xml;

import android.content.Context;

import com.bravo.Find.FragmentFind;
import com.bravo.adapters.AdapterScanner;
import com.bravo.config.Fragment_Device;
import com.bravo.data_ben.DeviceDataStruct;
import com.bravo.data_ben.DeviceFragmentStruct;
import com.bravo.data_ben.TargetDataStruct;
import com.bravo.parse_generate_xml.Find.FindDeviceInfo;
import com.bravo.scanner.FragmentScannerListen;
import com.bravo.socket_service.EventBusMsgRecvXmlMsg;
import com.bravo.utils.Logs;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.bravo.xml.XmlCodec.DecodeApXmlMessage;

/**
 * Created by admin on 2018-9-19.
 */

public class HandleRecvXmlMsg {
    private final String TAG = "HandleRecvXmlMsg";
    Context mContext;

    public HandleRecvXmlMsg(Context context)
    {
        this.mContext=context;
    }

    public void HandleRecvMsg(EventBusMsgRecvXmlMsg msgType)
    {
        String ip = msgType.getIp();
        int port = msgType.getPort();
        String result = msgType.getMsg();

        Msg_Body_Struct msg = DecodeApXmlMessage(result);
        if (msg == null) return;

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

        int index =  DeviceFragmentStruct.inListIndex(ip,port);

        //心跳消息
        if (msg.type.equalsIgnoreCase(Msg_Body_Struct.status_response)) {
            DeviceDataStruct deviceInfo = new DeviceDataStruct().xmlToBean(ip,port,msg);
            if (deviceInfo == null)
            {
                Logs.e(TAG,String.format("设备%s[%s:%d]心跳消息中参数错误。",ip,port));
                return;
            }
            DeviceFragmentStruct.addList(deviceInfo);
            if (Fragment_Device.isOpen) {
                //更新设备列表界面
                EventBus.getDefault().post(deviceInfo);
            }

            if(FragmentFind.isOpen) {
                //更新设备搜索界面
                EventBus.getDefault().post(msg);
            }

            return;
        }

        if (index == -1) {
            Logs.d(TAG,String.format("等待设备[%s:%d]心跳消息。",ip,port));
            return;
        } else {
            DeviceFragmentStruct.setListLastTime(index, System.currentTimeMillis());

            if (msg.type.equalsIgnoreCase(Msg_Body_Struct.scanner)) {
                DeviceDataStruct dds = DeviceFragmentStruct.getDevice(index);
                TargetDataStruct targetDataStruct = new TargetDataStruct();

                targetDataStruct.setSN(dds.getSN());
                targetDataStruct.setIP(dds.getIp());
                targetDataStruct.setPort(dds.getPort());
                targetDataStruct.setFullName(dds.getFullName());

                targetDataStruct.setImsi(FindMsgStruct.GetMsgStringValueInList("imsi",msg.dic,""));
                targetDataStruct.setiUserType(FindMsgStruct.GetMsgIntValueInList("userType",msg.dic,0));
                //Log.d(TAG,"用户类型：" + targetDataStruct.getiUserType());
                targetDataStruct.setImei(FindMsgStruct.GetMsgStringValueInList("imei",msg.dic,""));
                targetDataStruct.setTmsi(FindMsgStruct.GetMsgStringValueInList("tmsi",msg.dic,""));
                targetDataStruct.setRsrp(FindMsgStruct.GetMsgIntValueInList("rsrp",msg.dic,0));
                targetDataStruct.setbPositionStatus(true);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
                targetDataStruct.setStrAttachtime(formatter.format(new Date()));

                //if (!FragmentScannerListen.isStart) return;

                if(FragmentScannerListen.isOpen) {
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

}