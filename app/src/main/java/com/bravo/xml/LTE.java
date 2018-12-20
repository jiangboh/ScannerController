package com.bravo.xml;

import android.content.Context;
import android.util.Log;

import com.bravo.adapters.AdapterScanner;
import com.bravo.config.FragmentRedirection;
import com.bravo.data_ben.DeviceDataStruct;
import com.bravo.data_ben.DeviceFragmentStruct;
import com.bravo.data_ben.PositionDataStruct;
import com.bravo.data_ben.TargetDataStruct;
import com.bravo.data_ben.WaitDialogData;
import com.bravo.scanner.FragmentScannerListen;
import com.bravo.scanner.FragmentpPositionListen;
import com.bravo.socket_service.CommunicationService;
import com.bravo.socket_service.EventBusMsgSendUDPMsg;
import com.bravo.utils.Logs;
import com.bravo.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.bravo.xml.FindMsgStruct.GetMsgIntValueInList;
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

    private class TimeAndRssi {
        public Long tm;
        public int sumRssi;
        public int num;

        public TimeAndRssi(Long tm) {
            this.tm = tm;
            this.sumRssi = 0;
            this.num = 0;
        }
    }
    private static HashMap<String,TimeAndRssi> LastReportTime = new HashMap();

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
            targetDataStruct.setiUserType(GetMsgIntValueInList("userType", msg.dic, 0));
            //Logs.d(TAG,"用户类型：" + targetDataStruct.getiUserType());
            targetDataStruct.setImei(GetMsgStringValueInList("imei", msg.dic, ""));
            targetDataStruct.setTmsi(GetMsgStringValueInList("tmsi", msg.dic, ""));
            targetDataStruct.setRsrp(GetMsgIntValueInList("rsrp", msg.dic, 0));
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
        } else if (msg.type.equalsIgnoreCase(Msg_Body_Struct.meas_report)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
            String imsi = GetMsgStringValueInList("imsi", msg.dic, "");
            int rsrp = GetMsgIntValueInList("rsrp",msg.dic,-128);
            Log.d(TAG,"收到RSRP：" + rsrp);
            //加上上行衰减值
            rsrp = rsrp - ((LTE_GeneralPara)dds.getGeneralPara()).getRfTxGain();
            Log.d(TAG,"加衰减后RSRP：" + rsrp);
            boolean isReport = false;
            Long currTime = System.currentTimeMillis();
            if (LastReportTime.containsKey(imsi)) {
                ((TimeAndRssi) LastReportTime.get(imsi)).num++;
                ((TimeAndRssi) LastReportTime.get(imsi)).sumRssi += rsrp;
                Long nowTime = ((TimeAndRssi) LastReportTime.get(imsi)).tm;

                if ((currTime - nowTime) / 1000 > 2) {
                    isReport = true;
                    rsrp = ((TimeAndRssi) LastReportTime.get(imsi)).sumRssi / ((TimeAndRssi) LastReportTime.get(imsi)).num;

                    LastReportTime.remove(imsi);
                }
            } else {
                isReport = true;
            }
            if (isReport) {
                LastReportTime.put(imsi,new TimeAndRssi(currTime));
                Log.d(TAG,"上报RSRP：" + rsrp);
                PositionDataStruct data = new PositionDataStruct(dds.getSN(), imsi,
                        simpleDateFormat.format(System.currentTimeMillis()).toString(), rsrp);
                FragmentpPositionListen.addPositionData(data);
                EventBus.getDefault().post(data);
            }
        } else if (msg.type.equalsIgnoreCase(Msg_Body_Struct.get_general_para_response)) {
            LTE_GeneralPara gPara = new LTE_GeneralPara();
            gPara.setSn(DeviceFragmentStruct.getDevice(dds.getIp(),dds.getPort()).getSN());

            String reportType = GetMsgStringValueInList("reportType", msg.dic, "report");

            gPara.setBootmode(GetMsgIntValueInList("bootMode", msg.dic, 1));
            gPara.setManualfreq(GetMsgIntValueInList("manualfreq", msg.dic, 1));

            gPara.setEarfcndl(GetMsgIntValueInList("earfcndl", msg.dic, 0));
            gPara.setEarfcnul(GetMsgIntValueInList("earfcnul", msg.dic, 0));
            gPara.setPci(GetMsgIntValueInList("pci", msg.dic, 0));
            gPara.setBandwitch(GetMsgIntValueInList("bandwidth", msg.dic, 5));
            gPara.setCid(GetMsgIntValueInList("cellid", msg.dic, 0));
            gPara.setTac(GetMsgIntValueInList("tac", msg.dic, 0));
            String plmn = GetMsgStringValueInList("primaryplmn", msg.dic, "46000");
            gPara.setMcc(plmn.substring(0,3));
            gPara.setMnc(plmn.substring(3));
            gPara.setPower(GetMsgIntValueInList("txpower", msg.dic, 0));
            gPara.setPeriodtac(GetMsgIntValueInList("periodtac", msg.dic, 0));
            gPara.setRfTxGain(GetMsgIntValueInList("RfTxGain",msg.dic,0));

            gPara.setEarfcnlist(GetMsgStringValueInList("Earfcnlist", msg.dic, ""));

            gPara.setOtherplmn(GetMsgStringValueInList("otherplmn", msg.dic, ""));

            String[] periodFreq = FindMsgStruct.GetMsgStringValueInList("periodFreq", msg.dic, "").split(":");
            if (periodFreq.length == 1) {
                gPara.setPeriodFreqTime(Integer.parseInt(periodFreq[0]));
                gPara.setPeriodFreqFreq("");
            } else if (periodFreq.length == 2) {
                gPara.setPeriodFreqTime(Integer.parseInt(periodFreq[0]));
                gPara.setPeriodFreqFreq(periodFreq[1]);
            } else {
                gPara.setPeriodFreqTime(0);
                gPara.setPeriodFreqFreq("");
            }

            gPara.setNtpServer(GetMsgStringValueInList("NTP", msg.dic, ""));
            gPara.setNtppri(GetMsgIntValueInList("ntppri", msg.dic, 0));
            gPara.setGps_select(GetMsgIntValueInList("gps_select", msg.dic, 0));
            gPara.setBandoffset(GetMsgStringValueInList("Bandoffset", msg.dic, ""));

            gPara.setSource(GetMsgIntValueInList("source", msg.dic, 0));
            gPara.setManualEnable(GetMsgIntValueInList("ManualEnable", msg.dic, 0));
            gPara.setManualEarfcn(GetMsgIntValueInList("ManualEarfcn", msg.dic, 0));
            gPara.setManualPci(GetMsgIntValueInList("ManualPci", msg.dic, 0));
            gPara.setManualBw(GetMsgIntValueInList("ManualBw", msg.dic, 5));

            if (reportType.equals("report")) {
                String whiteurl = GetMsgStringValueInList("whiteimsi_md5", msg.dic, "");
                String blackurl = GetMsgStringValueInList("blackimsi_md5", msg.dic, "");
                if (whiteurl.equals(CommunicationService.WhiteMd5)) {
                    whiteurl = "";
                } else {
                    whiteurl = String.format("ftp://%s:2121/whitelist.txt",
                            Utils.getWifiIp(mContext));
                }
                if (blackurl.equals(CommunicationService.BlackMd5)) {
                    blackurl = "";
                } else {
                    blackurl = String.format("ftp://%s:2121/blacklist.txt",
                            Utils.getWifiIp(mContext));
                }

                if (whiteurl.equals("") && blackurl.equals("")) {
                    //回复数据对齐完成
                    SendDataAlignOver(dds.getIp(), dds.getPort(),0);
                } else {
                    SetGeneralParaRequest(dds.getIp(),dds.getPort(),whiteurl,blackurl);
                }
            }

            DeviceFragmentStruct.ChangeGeneralPara(dds.getIp(),dds.getPort(),gPara);

            EventBus.getDefault().post(gPara);

        }  else if (msg.type.equalsIgnoreCase(Msg_Body_Struct.set_general_para_response)) {
            //回复数据对齐完成
            SendDataAlignOver(dds.getIp(), dds.getPort(),GetMsgIntValueInList("result", msg.dic, 0));
            int result = FindMsgStruct.GetMsgIntValueInList("result", msg.dic, 0);
            if (result == 0) {
                EventBus.getDefault().post(new WaitDialogData(
                        HandleRecvXmlMsg.AP_DATA_ALIGN_SET, dds.getSN(), WaitDialogData.RUSULT_OK));
            } else {
                EventBus.getDefault().post(new WaitDialogData(
                        HandleRecvXmlMsg.AP_DATA_ALIGN_SET, dds.getSN(), WaitDialogData.RUSULT_FAIL));
            }
        }  else if (msg.type.equalsIgnoreCase(Msg_Body_Struct.set_configuration_result)) {
            int result = FindMsgStruct.GetMsgIntValueInList("result", msg.dic, 0);
            if (result == 0) {
                EventBus.getDefault().post(new WaitDialogData(
                        HandleRecvXmlMsg.LTE_CELL_CONFIG, "", WaitDialogData.RUSULT_OK));
            } else {
                EventBus.getDefault().post(new WaitDialogData(
                        HandleRecvXmlMsg.LTE_CELL_CONFIG, "", WaitDialogData.RUSULT_FAIL));
            }
        } else if (msg.type.equalsIgnoreCase(Msg_Body_Struct.set_son_earfcn_response)) {
            int result = FindMsgStruct.GetMsgIntValueInList("result", msg.dic, 0);
            if (result == 0) {
                EventBus.getDefault().post(new WaitDialogData(
                        HandleRecvXmlMsg.LTE_SON_CONFIG, "", WaitDialogData.RUSULT_OK));
            } else {
                EventBus.getDefault().post(new WaitDialogData(
                        HandleRecvXmlMsg.LTE_SON_CONFIG, "", WaitDialogData.RUSULT_FAIL));
            }
        } else if (msg.type.equalsIgnoreCase(Msg_Body_Struct.set_system_response)) {
            int result = FindMsgStruct.GetMsgIntValueInList("result", msg.dic, 0);
            if (result == 0) {
                EventBus.getDefault().post(new WaitDialogData(
                        HandleRecvXmlMsg.LTE_SYSTEM_SET, "", WaitDialogData.RUSULT_OK));
            } else {
                EventBus.getDefault().post(new WaitDialogData(
                        HandleRecvXmlMsg.LTE_SYSTEM_SET, "", WaitDialogData.RUSULT_FAIL));
            }
        } else if (msg.type.equalsIgnoreCase(Msg_Body_Struct.set_param_response)) {
            int result = FindMsgStruct.GetMsgIntValueInList("result", msg.dic, 0);
            String name = FindMsgStruct.GetMsgStringValueInList("paramName", msg.dic, "");
            if (result == 0) {
                if (name.equals("CFG_OTHER_PLMN")) {
                    EventBus.getDefault().post(new WaitDialogData(
                            HandleRecvXmlMsg.LTE_OTHER_PLMN, "", WaitDialogData.RUSULT_OK));
                } else if (name.equals("CFG_PERIOD_FREQ")) {
                    EventBus.getDefault().post(new WaitDialogData(
                            HandleRecvXmlMsg.LTE_PERIOD_FREQ, "", WaitDialogData.RUSULT_OK));
                }
            } else {
                if (name.equals("CFG_OTHER_PLMN")) {
                    EventBus.getDefault().post(new WaitDialogData(
                            HandleRecvXmlMsg.LTE_OTHER_PLMN, "", WaitDialogData.RUSULT_FAIL));
                } else if (name.equals("CFG_PERIOD_FREQ")) {
                    EventBus.getDefault().post(new WaitDialogData(
                            HandleRecvXmlMsg.LTE_PERIOD_FREQ, "", WaitDialogData.RUSULT_FAIL));
                }
            }
        } else if (msg.type.equalsIgnoreCase(Msg_Body_Struct.set_work_mode_reponse)) {
            int result = FindMsgStruct.GetMsgIntValueInList("result", msg.dic, 0);
            if (result == 0) {
                EventBus.getDefault().post(new WaitDialogData(
                        HandleRecvXmlMsg.LTE_WORKE_MODE, "", WaitDialogData.RUSULT_OK));
            } else {
                EventBus.getDefault().post(new WaitDialogData(
                        HandleRecvXmlMsg.LTE_WORKE_MODE, "", WaitDialogData.RUSULT_FAIL));
            }
        } else if (msg.type.equalsIgnoreCase(Msg_Body_Struct.Syncinfo_set_response)) {
            int result = FindMsgStruct.GetMsgIntValueInList("result", msg.dic, 0);
            if (result == 0) {
                EventBus.getDefault().post(new WaitDialogData(
                        HandleRecvXmlMsg.LTE_SYNC_SET, "", WaitDialogData.RUSULT_OK));
            } else {
                EventBus.getDefault().post(new WaitDialogData(
                        HandleRecvXmlMsg.LTE_SYNC_SET, "", WaitDialogData.RUSULT_FAIL));
            }
        } if (msg.type.equalsIgnoreCase(Msg_Body_Struct.DataAlignOverAck)) {
            int result = FindMsgStruct.GetMsgIntValueInList("result", msg.dic, 0);
            if (result == 0) {
                EventBus.getDefault().post(new WaitDialogData(
                        HandleRecvXmlMsg.AP_DATA_ALIGN_SET, dds.getSN(), WaitDialogData.RUSULT_OK));
            } else {
                EventBus.getDefault().post(new WaitDialogData(
                        HandleRecvXmlMsg.AP_DATA_ALIGN_SET, dds.getSN(), WaitDialogData.RUSULT_FAIL));
            }
        } if (msg.type.equalsIgnoreCase(Msg_Body_Struct.set_redirection_rsp)) {
            int result = FindMsgStruct.GetMsgIntValueInList("result", msg.dic, 0);
            if (result == 0) {
                EventBus.getDefault().post(new WaitDialogData(
                        HandleRecvXmlMsg.LTE_REDIRECTION_SET, "", WaitDialogData.RUSULT_OK));
            } else {
                EventBus.getDefault().post(new WaitDialogData(
                        HandleRecvXmlMsg.LTE_REDIRECTION_SET, "", WaitDialogData.RUSULT_FAIL));
            }
        } if (msg.type.equalsIgnoreCase(Msg_Body_Struct.get_redirection_rsp)) {
                FragmentRedirection.Redirection data = new FragmentRedirection().new Redirection();
                FragmentRedirection.Redirection.Category category = data.new Category();

                category.setFreq(GetMsgIntValueInList("redirectInfo/freq", msg.dic, 0));
                category.setPriorty(GetMsgIntValueInList("redirectInfo/priority", msg.dic, 0));
                category.setRejectMethod(GetMsgIntValueInList("redirectInfo/rejectMethod", msg.dic, 0));
                category.setAddFreq(GetMsgStringValueInList("redirectInfo/additionalFreq", msg.dic, ""));
                if (GetMsgIntValueInList("redirectInfo/category", msg.dic, 0) == FragmentRedirection.Redirection.WHITE) {
                    data.setWhite(category);
                } else if (GetMsgIntValueInList("redirectInfo/category", msg.dic, 0) == FragmentRedirection.Redirection.BLACK) {
                    data.setBlack(category);
                } else if (GetMsgIntValueInList("redirectInfo/category", msg.dic, 0) == FragmentRedirection.Redirection.OTHER) { //其它名单
                    data.setOther(category);
                }

                FragmentRedirection.Redirection.Category category1 = data.new Category();
                category1.setFreq(GetMsgIntValueInList("redirectInfo/freq_#1#", msg.dic, 0));
                category1.setPriorty(GetMsgIntValueInList("redirectInfo/priority_#1#", msg.dic, 0));
                category1.setRejectMethod(GetMsgIntValueInList("redirectInfo/rejectMethod_#1#", msg.dic, 0));
                category1.setAddFreq(GetMsgStringValueInList("redirectInfo/additionalFreq_#1#", msg.dic, ""));
                if (GetMsgIntValueInList("redirectInfo/category_#1#", msg.dic, 0) == FragmentRedirection.Redirection.WHITE) {
                    data.setWhite(category1);
                } else if (GetMsgIntValueInList("redirectInfo/category_#1#", msg.dic, 0) == FragmentRedirection.Redirection.BLACK) {
                    data.setBlack(category1);
                } else if (GetMsgIntValueInList("redirectInfo/category_#1#", msg.dic, 0) == FragmentRedirection.Redirection.OTHER) { //其它名单
                    data.setOther(category1);
                }

                FragmentRedirection.Redirection.Category category2 = data.new Category();
                category2.setFreq(GetMsgIntValueInList("redirectInfo/freq_#2#", msg.dic, 0));
                category2.setPriorty(GetMsgIntValueInList("redirectInfo/priority_#2#", msg.dic, 0));
                category2.setRejectMethod(GetMsgIntValueInList("redirectInfo/rejectMethod_#2#", msg.dic, 0));
                category2.setAddFreq(GetMsgStringValueInList("redirectInfo/additionalFreq_#2#", msg.dic, ""));
                if (GetMsgIntValueInList("redirectInfo/category_#2#", msg.dic, 0) == FragmentRedirection.Redirection.WHITE) { //黑名单
                    data.setWhite(category2);
                } else if (GetMsgIntValueInList("redirectInfo/category_#2#", msg.dic, 0) == FragmentRedirection.Redirection.BLACK) {
                    data.setBlack(category2);
                } else if (GetMsgIntValueInList("redirectInfo/category_#2#", msg.dic, 0) == FragmentRedirection.Redirection.OTHER) { //其它名单
                    data.setOther(category2);
                }

                data.setSn(dds.getSN());
                EventBus.getDefault().post(data);
            } else {
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

    /*public void SetApParameter(String ip,int port,String ...args) {
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
    }*/

    public void SetApParameter(String ip,int port,String name,String value) {
        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.set_parameter_request);
        msg.dic.put("paramName", name);
        msg.dic.put("paramValue", value);

        String sendText = EncodeApXmlMessage(msg);

        EventBusMsgSendUDPMsg ebmsm = new EventBusMsgSendUDPMsg(ip,port,sendText);
        EventBus.getDefault().post(ebmsm);

        if (name.equals("CFG_OTHER_PLMN")) {
            EventBus.getDefault().post(new WaitDialogData(
                    HandleRecvXmlMsg.LTE_OTHER_PLMN, "", WaitDialogData.SEND));
        } else if (name.equals("CFG_PERIOD_FREQ")) {
            EventBus.getDefault().post(new WaitDialogData(
                    HandleRecvXmlMsg.LTE_PERIOD_FREQ, "", WaitDialogData.SEND));
        }

        return ;
    }

    public void SendGeneralParaRequest(String ip,int port,String sn) {
        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.get_general_para_request);
        msg.dic.put("timeout",0);
        String sendText = EncodeApXmlMessage(msg);

        EventBusMsgSendUDPMsg ebmsm = new EventBusMsgSendUDPMsg(ip,port,sendText);
        EventBus.getDefault().post(ebmsm);

        EventBus.getDefault().post(new WaitDialogData(
                HandleRecvXmlMsg.LTE_SON_CONFIG,sn, WaitDialogData.SEND));

        return ;
    }

    public void SendStatusRequest(String ip,int port) {
        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.status_request);
        msg.dic.put("timeout",0);
        String sendText = EncodeApXmlMessage(msg);

        EventBusMsgSendUDPMsg ebmsm = new EventBusMsgSendUDPMsg(ip,port,sendText);
        EventBus.getDefault().post(ebmsm);

        return ;
    }

    public void SendDataAlignOver(String ip,int port,int result) {
        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.DataAlignOver);
        msg.dic.put("ReturnCode",result);
        if (result == 0) {
            msg.dic.put("ReturnStr", "success");
        } else {
            msg.dic.put("ReturnStr", "failed");
        }
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

        WaitDialogData wdd = new WaitDialogData(
                HandleRecvXmlMsg.LTE_WORKE_MODE,"", WaitDialogData.SEND);
        EventBus.getDefault().post(wdd);

        return ;
    }

    public void SetConfiguration(String ip,int port,String mode,LTE_GeneralPara para) {
        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.set_configuration);
        msg.dic.put("euarfcn",para.getEarfcndl());
        msg.dic.put("mcc",para.getMcc());
        msg.dic.put("mnc",para.getMnc());
        msg.dic.put("pci",para.getPci());
        msg.dic.put("tac",para.getTac());
        msg.dic.put("txpower",para.getPower());
        msg.dic.put("periodTac",para.getPeriodtac());
        if (!mode.equals(DeviceDataStruct.MODE.WCDMA)) {
            msg.dic.put("bandwidth", para.getBandwitch());
            msg.dic.put("cellid", para.getCid());
        }
        msg.dic.put("timeout",0);

        String sendText = EncodeApXmlMessage(msg);

        EventBusMsgSendUDPMsg ebmsm = new EventBusMsgSendUDPMsg(ip,port,sendText);
        EventBus.getDefault().post(ebmsm);

        EventBus.getDefault().post(new WaitDialogData(
                HandleRecvXmlMsg.LTE_CELL_CONFIG,"", WaitDialogData.SEND));

        return ;
    }

    public void SetSonEarfcn(String ip,int port,LTE_GeneralPara para) {
        String[] earfcn = para.getEarfcnlist().split(",");
        if (earfcn.length <=0 ) return;

        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.set_son_earfcn);
        for(int i =0;i<earfcn.length;i++) {
            Name_DIC_Struct n_dic = new Name_DIC_Struct();
            n_dic.dic.put("earfcn", earfcn[i]);
            msg.n_dic.add(n_dic);
        }
        String sendText = EncodeApXmlMessage(msg);

        EventBusMsgSendUDPMsg ebmsm = new EventBusMsgSendUDPMsg(ip,port,sendText);
        EventBus.getDefault().post(ebmsm);

        EventBus.getDefault().post(new WaitDialogData(
                HandleRecvXmlMsg.LTE_SON_CONFIG,"", WaitDialogData.SEND));

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

        EventBus.getDefault().post(new WaitDialogData(
                HandleRecvXmlMsg.LTE_SYSTEM_SET,"", WaitDialogData.SEND));

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

        WaitDialogData wdd =new WaitDialogData(
                HandleRecvXmlMsg.LTE_SYNC_SET,"", WaitDialogData.SEND);
        EventBus.getDefault().post(wdd);

        return ;
    }

    public void SetGeneralParaRequest(String ip, int port, String whiteurl, String blackurl) {
        if (whiteurl.equals("") && blackurl.equals(""))  return;

        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.set_general_para_request);
        msg.dic.put("ApIsBase",0);
        if (!whiteurl.equals(""))
            msg.dic.put("FtpUrl_White",whiteurl);
        if (!blackurl.equals(""))
            msg.dic.put("FtpUrl_Black",blackurl);

        msg.dic.put("FtpUser","user");
        msg.dic.put("FtpPas","password");
        String sendText = EncodeApXmlMessage(msg);

        EventBusMsgSendUDPMsg ebmsm = new EventBusMsgSendUDPMsg(ip,port,sendText);
        EventBus.getDefault().post(ebmsm);

        return ;
    }

    public void SendGetRedirectionRequest(String ip, int port) {
        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.get_redirection_req);
        msg.dic.put("timeout",0);
        String sendText = EncodeApXmlMessage(msg);

        EventBusMsgSendUDPMsg ebmsm = new EventBusMsgSendUDPMsg(ip,port,sendText);
        EventBus.getDefault().post(ebmsm);

        return ;
    }

    public void SendSetRedirectionRequest(String ip, int port,int category,FragmentRedirection.Redirection.Category data) {
        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.set_redirection_req);
        msg.dic.put("category",category);
        msg.dic.put("priority",data.getPriorty());
        if (data.getPriorty() == 2) {// 2G
            msg.dic.put("GeranRedirect",1);
            msg.dic.put("UtranRedirect",0);
            msg.dic.put("EtranRedirect",0);
            msg.dic.put("arfcn",data.getFreq());
        } else if (data.getPriorty() == 3) {// 3G
            msg.dic.put("UtranRedirect",1);
            msg.dic.put("GeranRedirect",0);
            msg.dic.put("EtranRedirect",0);
            msg.dic.put("uarfcn",data.getFreq());
        } else if (data.getPriorty() == 4) {// 4G
            msg.dic.put("EtranRedirect",1);
            msg.dic.put("UtranRedirect",0);
            msg.dic.put("GeranRedirect",0);
            msg.dic.put("earfcn",data.getFreq());
        } else {
            msg.dic.put("EtranRedirect",0);
            msg.dic.put("UtranRedirect",0);
            msg.dic.put("GeranRedirect",0);
        }
        msg.dic.put("RejectMethod",data.getRejectMethod());
        msg.dic.put("additionalFreq",data.getAddFreq());

        String sendText = EncodeApXmlMessage(msg);

        EventBusMsgSendUDPMsg ebmsm = new EventBusMsgSendUDPMsg(ip,port,sendText);
        EventBus.getDefault().post(ebmsm);

        return ;
    }
}
