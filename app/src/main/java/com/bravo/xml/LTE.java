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

import static com.bravo.xml.FindMsgStruct.GetMsgStringValueInList;
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

            targetDataStruct.setImsi(GetMsgStringValueInList("imsi", msg.dic, ""));
            targetDataStruct.setiUserType(FindMsgStruct.GetMsgIntValueInList("userType", msg.dic, 0));
            //Logs.d(TAG,"用户类型：" + targetDataStruct.getiUserType());
            targetDataStruct.setImei(GetMsgStringValueInList("imei", msg.dic, ""));
            targetDataStruct.setTmsi(GetMsgStringValueInList("tmsi", msg.dic, ""));
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
            String plmn = GetMsgStringValueInList("primaryplmn", msg.dic, "46000");
            gPara.setMcc(Integer.parseInt(plmn.substring(0,3)));
            gPara.setMnc(Integer.parseInt(plmn.substring(3)));
            gPara.setPower(FindMsgStruct.GetMsgIntValueInList("txpower", msg.dic, 0));
            gPara.setPeriodtac(FindMsgStruct.GetMsgIntValueInList("periodtac", msg.dic, 0));

            gPara.setEarfcnlist(GetMsgStringValueInList("Earfcnlist", msg.dic, ""));

            gPara.setOtherplmn(GetMsgStringValueInList("otherplmn", msg.dic, ""));
            String[] periodFreq = FindMsgStruct.GetMsgStringValueInList("periodFreq", msg.dic, "").split(":");
            if (periodFreq.length >= 2) {
                gPara.setPeriodFreqTime(Integer.parseInt(periodFreq[0]));
                gPara.setPeriodFreqFreq(periodFreq[1]);
            }

            gPara.setNtpServer(GetMsgStringValueInList("NTP", msg.dic, ""));
            gPara.setNtppri(FindMsgStruct.GetMsgIntValueInList("ntppri", msg.dic, 0));
            gPara.setGps_select(FindMsgStruct.GetMsgIntValueInList("gps_select", msg.dic, 0));
            gPara.setBandoffset(GetMsgStringValueInList("Bandoffset", msg.dic, ""));

            gPara.setSource(FindMsgStruct.GetMsgIntValueInList("source", msg.dic, 0));
            gPara.setManualEnable(FindMsgStruct.GetMsgIntValueInList("ManualEnable", msg.dic, 0));
            gPara.setManualEarfcn(FindMsgStruct.GetMsgIntValueInList("ManualEarfcn", msg.dic, 0));
            gPara.setManualPci(FindMsgStruct.GetMsgIntValueInList("ManualPci", msg.dic, 0));
            gPara.setManualBw(FindMsgStruct.GetMsgIntValueInList("ManualBw", msg.dic, 0));

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

    public void SetWorkMode(String ip,int port,LTE_GeneralPara para) {
        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.set_work_mode);
        msg.dic.put("boot",para.getBootmode());
        msg.dic.put("manualFreq",para.getBootmode());
        String sendText = EncodeApXmlMessage(msg);

        EventBusMsgSendUDPMsg ebmsm = new EventBusMsgSendUDPMsg(ip,port,sendText);
        EventBus.getDefault().post(ebmsm);

        return ;
    }

    public void SetConfiguration(String ip,int port,LTE_GeneralPara para) {
        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.set_configuration);
        msg.dic.put("euarfcn",para.getEarfcndl());
        msg.dic.put("mcc",para.getMcc());
        msg.dic.put("mnc",para.getMnc());
        msg.dic.put("pci",para.getPci());
        msg.dic.put("tac",para.getTac());
        msg.dic.put("txpower",para.getPower());
        msg.dic.put("periodTac",para.getPeriodtac());
        msg.dic.put("bandwidth",para.getBandwitch());
        msg.dic.put("cellid",para.getCid());
        msg.dic.put("timeout",0);

        String sendText = EncodeApXmlMessage(msg);

        EventBusMsgSendUDPMsg ebmsm = new EventBusMsgSendUDPMsg(ip,port,sendText);
        EventBus.getDefault().post(ebmsm);

        return ;
    }

    public void SetSonEarfcn(String ip,int port,LTE_GeneralPara para) {
        String[] earfcn = para.getEarfcnlist().split(",");
        if (earfcn.length <=0 ) return;

        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.set_work_mode);
        for(int i =0;i<earfcn.length;i++) {
            msg.dic.put("earfcn", earfcn[i]);
        }
        String sendText = EncodeApXmlMessage(msg);

        EventBusMsgSendUDPMsg ebmsm = new EventBusMsgSendUDPMsg(ip,port,sendText);
        EventBus.getDefault().post(ebmsm);

        return ;
    }

    public void SetSystemRequest(String ip,int port,LTE_GeneralPara para) {
        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.set_system_request);
        msg.dic.put("time/NTP",para.getNtpServer());
        msg.dic.put("time/Pri",para.getNtppri());
        msg.dic.put("GPS/Enable",para.getGps_select());
        msg.dic.put("GPS/BandOffset",para.getBandoffset());
        String sendText = EncodeApXmlMessage(msg);

        EventBusMsgSendUDPMsg ebmsm = new EventBusMsgSendUDPMsg(ip,port,sendText);
        EventBus.getDefault().post(ebmsm);

        return ;
    }

    public void SetSyncInfo(String ip,int port,LTE_GeneralPara para) {
        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.Syncinfo_set);
        msg.dic.put("CNMSyncpriority",para.getSource());
        msg.dic.put("ManualEnable",para.getManualEnable());
        msg.dic.put("Manualsyncinfo/ManualEarfcn",para.getManualEarfcn());
        msg.dic.put("Manualsyncinfo/ManualPci",para.getManualPci());
        msg.dic.put("Manualsyncinfo/ManualBw",para.getManualBw());
        String sendText = EncodeApXmlMessage(msg);

        EventBusMsgSendUDPMsg ebmsm = new EventBusMsgSendUDPMsg(ip,port,sendText);
        EventBus.getDefault().post(ebmsm);

        return ;
    }
}
