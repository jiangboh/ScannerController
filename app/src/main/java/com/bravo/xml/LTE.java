package com.bravo.xml;

import android.content.Context;

import com.bravo.adapters.AdapterScanner;
import com.bravo.data_ben.DeviceDataStruct;
import com.bravo.data_ben.DeviceFragmentStruct;
import com.bravo.data_ben.TargetDataStruct;
import com.bravo.scanner.FragmentScannerListen;
import com.bravo.socket_service.EventBusMsgSendUDPMsg;
import com.bravo.utils.Logs;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.bravo.xml.XmlCodec.EncodeApXmlMessage;

/**
 * Created by admin on 2018-9-29.
 */

public class LTE {
    private final String TAG = "LTE";
    private final int ACTIVE_START = 1;
    private final int ACTIVE_STOP = 2;
    private final int ACTIVE_REBOOT = 3;

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
        } if (msg.type.equalsIgnoreCase(Msg_Body_Struct.get_general_para_response)) {
            LTE_GeneralPara gPara = new LTE_GeneralPara();
            gPara.setBootmode(FindMsgStruct.GetMsgIntValueInList("bootMode", msg.dic, 1));
            gPara.setManualfreq(FindMsgStruct.GetMsgIntValueInList("manualfreq", msg.dic, 1));
            gPara.setEarfcndl(FindMsgStruct.GetMsgIntValueInList("earfcndl", msg.dic, 0));
            gPara.setEarfcnul(FindMsgStruct.GetMsgIntValueInList("earfcnul", msg.dic, 0));
            gPara.setBandwitch(FindMsgStruct.GetMsgIntValueInList("bandwidth", msg.dic, 5));
            gPara.setCid(FindMsgStruct.GetMsgIntValueInList("cellid", msg.dic, 0));
            gPara.setTac(FindMsgStruct.GetMsgIntValueInList("tac", msg.dic, 0));
            String plmn = FindMsgStruct.GetMsgStringValueInList("primaryplmn", msg.dic, "46000");
            gPara.setMcc(Integer.parseInt(plmn.substring(0,3)));
            gPara.setMnc(Integer.parseInt(plmn.substring(3)));
            gPara.setPower(FindMsgStruct.GetMsgIntValueInList("txpower", msg.dic, 0));
            gPara.setPeriodtac(FindMsgStruct.GetMsgIntValueInList("periodtac", msg.dic, 0));
            gPara.setEarfcnlist(FindMsgStruct.GetMsgStringValueInList("Earfcnlist", msg.dic, ""));

            DeviceFragmentStruct.ChangeGeneralPara(dds.getIp(),dds.getPort(),gPara);
        }  else {
            Logs.e(TAG, String.format("消息类型(%s)为不支持的消息类型！", msg.type),true);
        }
    }

    public void SetApRedio(String ip,int port,int mode,boolean isOn) {
        int active_mode;
        if (isOn){
            active_mode = ACTIVE_START;
        } else {
            active_mode = ACTIVE_STOP;
        }
        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.activate_nodeb_request);
        msg.dic.put("active_mode", active_mode);
        msg.dic.put("mode",mode);
        msg.dic.put("timeout",0);
        String sendText = EncodeApXmlMessage(msg);

        EventBusMsgSendUDPMsg ebmsm = new EventBusMsgSendUDPMsg(ip,port,sendText);
        EventBus.getDefault().post(ebmsm);

        return ;
    }

    public void SetApReboot(String ip,int port,int mode) {
        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.activate_nodeb_request);
        msg.dic.put("active_mode", ACTIVE_REBOOT);
        msg.dic.put("mode",mode);
        msg.dic.put("timeout",0);
        String sendText = EncodeApXmlMessage(msg);

        EventBusMsgSendUDPMsg ebmsm = new EventBusMsgSendUDPMsg(ip,port,sendText);
        EventBus.getDefault().post(ebmsm);

        return ;
    }

    public void SetApParameter(String ip,int port,String ...args) {
        if ((args.length % 2) != 0) {
            Logs.w(TAG,"输入参数的键值对不匹配！");
            return;
        }
        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.set_parameter_request);
        for(int i=0;i<args.length;i+=2) {
            msg.dic.put("paramName", args[i]);
            msg.dic.put("paramValue", args[i+1]);
        }
        String sendText = EncodeApXmlMessage(msg);

        EventBusMsgSendUDPMsg ebmsm = new EventBusMsgSendUDPMsg(ip,port,sendText);
        EventBus.getDefault().post(ebmsm);

        return ;
    }

    public void SendGeneralParaRequest(String ip,int port) {
        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.get_general_para_request);
        msg.dic.put("timeout",0);
        String sendText = EncodeApXmlMessage(msg);

        EventBusMsgSendUDPMsg ebmsm = new EventBusMsgSendUDPMsg(ip,port,sendText);
        EventBus.getDefault().post(ebmsm);

        return ;
    }

}
