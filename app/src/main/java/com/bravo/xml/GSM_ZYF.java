package com.bravo.xml;

import android.content.Context;

import com.bravo.adapters.AdapterScanner;
import com.bravo.data_ben.DeviceDataStruct;
import com.bravo.data_ben.DeviceFragmentStruct;
import com.bravo.data_ben.TargetDataStruct;
import com.bravo.data_ben.WaitDialogData;
import com.bravo.scanner.FragmentScannerListen;
import com.bravo.socket_service.CommunicationService;
import com.bravo.socket_service.EventBusMsgSendUDPMsg;
import com.bravo.utils.Logs;
import com.bravo.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.bravo.xml.FindMsgStruct.GetMsgIntValueInList;
import static com.bravo.xml.FindMsgStruct.GetMsgStringValueInList;
import static com.bravo.xml.XmlCodec.EncodeApXmlMessage;

/**
 * Created by admin on 2018-9-28.
 */

public class GSM_ZYF {
    private final String TAG = "GSM_ZYF";

    private final int RECV_MSG_LEN = 12;

    private final int MsgHadeLen = 12;

    public static final int Sys1 = 0;
    public static final int Sys2 = 1;

    private static final int GSM = 241;
    private static final int CDMA = 242;

    private static final int INITIAL_MSG = 0;
    private static final int SUCC_OUTCOME = 1;
    private static final int UNSUCC_OUTCOME = 2;

    /// <summary>
    /// 消息类型
    /// </summary>
    private static final int QUERY_FAP_PARAM_MSG = 197;  //GUI 查询FAP运行参数
    private static final int QUERY_NB_CELL_INFO_MSG = 202;  //	GUI查询邻区信息
    private static final int FAP_NB_CELL_INFO_MSG = 203;  //FAP上报邻区信息
    private static final int CONFIG_FAP_MSG = 204;  //GUI配置FAP的启动参数
    private static final int CONTROL_FAP_REBOOT_MSG = 205;  //GUI控制FAP的重启。
    private static final int CONTROL_FAP_RF_MSG = 206;  //控制FAP射频的消息
    private static final int FAP_HEARTBEAT_MSG = 208;  //FAP心跳消息，FAP启动成功后，每10秒发送一次该消息给GUI。心跳消息包含基站的一些状态信息，比如工作模式、同步状态等
    private static final int FAP_TRACE_MSG = 210;  //FAP上报一些事件和状态给GUI，GUI程序需要显示给操作者看。
    private static final int UE_STATUS_REPORT_MSG = 212;  //FAP上报UE相关状态.
    private static final int UE_ORM_REPORT_MSG = 219;  //FAP上报UE主叫信息，只用于GSM和CDMA
    private static final int CONFIG_SMS_CONTENT_MSG_ID = 192;  //FAP 配置下发短信号码和内容
    private static final int FAP_PARAM_REPORT_MSG = 198;  //FAP上报FAP运行参数.
    private static final int CONTROL_FAP_RADIO_ON_MSG = 193;  //	GUI 控制FAP开启射频
    private static final int CONTROL_FAP_RADIO_OFF_MSG = 194;  //GUI 控制FAP关闭射频
    private static final int CONTROL_FAP_RESET_MSG = 195;  //GUI 控制FAP的软复位
    private static final int CONFIG_CDMA_CARRIER_MSG = 196;  //GUI 配置CDMA多载波参数
    private static final int CONFIG_IMSI_MSG_V3_ID = 245;  //大数量imsi名单，用于配置不同的目标IMSI不同的行为

    private Context mContext;
    private int Protocol = GSM;

    public GSM_ZYF(Context context,String mode){
        if (mode.equals(DeviceDataStruct.MODE.GSM_V2)) {
            this.Protocol = GSM;
        } else {
            this.Protocol = CDMA;
        }
        this.mContext = context;
    }

    /// <summary>
    /// 发送给设备的消息结构
    /// </summary>
    private class MsgSendStruct {
        public int bProtocolSap;            //头部标识 0xAAAA
        public int bMsgId; //消息Id
        public int bMsgType; //消息类型
        public int bCellIdx; //系统号，0表示系统1或通道1或射频1，1表示系统2或通道2或射频2
        public int wMsgLen;      //消息数据长度   [数据长度是消息ID和消息数据长度的总和]
        public int wSqn;     //消息的序列号，每发送完一个消息FAP将此序列号加1，GUI可以用该字段简单查看FAP到GUI有没有丢包发生。
        public Long dwTimeStamp;            //消息时间
        public String data;

        public MsgSendStruct(int MsgId, int sys, String data) {
            this.bProtocolSap = Protocol;
            this.bMsgId = MsgId;
            this.bMsgType = INITIAL_MSG;
            this.bCellIdx = sys;
            this.wMsgLen = (int) (MsgHadeLen + (data.replace(" ", "").length() / 2));
            this.wSqn = 0;
            this.dwTimeStamp = System.currentTimeMillis()/1000;
            this.data = data;
        }

        public MsgSendStruct(int MsgId, int sys, int sqn, String data) {
            this.bProtocolSap = Protocol;
            this.bMsgId = MsgId;
            this.bMsgType = INITIAL_MSG;
            this.bCellIdx = sys;
            this.wMsgLen = (int) (12 + (data.replace(" ", "").length() / 2));
            this.wSqn = sqn;
            this.dwTimeStamp = System.currentTimeMillis();
            this.data = data;
        }
    }

    private class MsgRecvStruct {
        public int bProtocolSap;            //头部标识 0xAAAA
        public int bMsgId; //消息Id
        public int bMsgType; //消息类型
        public int bCellIdx; //系统号，0表示系统1或通道1或射频1，1表示系统2或通道2或射频2
        public int wMsgLen;      //消息数据长度   [数据长度是消息ID和消息数据长度的总和]
        public int wSqn;     //消息的序列号，每发送完一个消息FAP将此序列号加1，GUI可以用该字段简单查看FAP到GUI有没有丢包发生。
        public Long dwTimeStamp;            //消息时间
        public String data;
    }

    private class STRUCT_CONFIG_FAP_MSG {
        public int bWorkingMode;         //工作模式:1 为侦码模式 ;3驻留模式.
        public int bC;                   //是否自动切换模式。保留
        public int wRedirectCellUarfcn;  //CDMA黑名单频点
        public Long dwDateTime;           //当前时间
        public String bPLMNId;              //PLMN标志
        public int bTxPower;             //实际发射功率.设置发射功率衰减寄存器, 0输出最大功率, 每增加1, 衰减1DB
        public int bRxGain;              //接收信号衰减寄存器. 每增加1增加1DB的增益
        public int wPhyCellId;           //物理小区ID.
        public int wLAC;                 //追踪区域码。GSM：LAC;CDMA：REG_ZONE
        public int wUARFCN;              //小区频点. CDMA 制式为BSID
        public Long dwCellId;             //小区ID。注意在CDMA制式没有小区ID，高位WORD 是SID ， 低位WORD 是NID
    }

    private class STRUCT_CONFIG_CDMA_CARRIER_MSG {
        public int wARFCN1;          // 工作频点
        public Byte bARFCN1Mode;      //工作频点模式。0表示扫描，1表示常开,2表示关闭。
        public int wARFCN1Duration;  //工作频点扫描时长
        public int wARFCN1Period;    //工作频点扫描间隔
        public int wARFCN2;          // 工作频点
        public Byte bARFCN2Mode;        //工作频点模式。0表示扫描，1表示常开,2表示关闭。
        public int wARFCN2Duration;  //工作频点扫描时长
        public int wARFCN2Period;    //工作频点扫描间隔
        public int wARFCN3;          // 工作频点
        public Byte bARFCN3Mode;        //工作频点模式。0表示扫描，1表示常开,2表示关闭。
        public int wARFCN3Duration;  //工作频点扫描时长
        public int wARFCN3Period;    //工作频点扫描间隔
        public int wARFCN4;          // 工作频点
        public Byte bARFCN4Mode;        //工作频点模式。0表示扫描，1表示常开,2表示关闭。
        public int wARFCN4Duration;  //工作频点扫描时长
        public int wARFCN4Period;    //工作频点扫描间隔
    }

    private class STRUCT_CONFIG_IMSI_MSG_V3 {
        public int wTotalImsi;
        public Byte bIMSINum;
        public Byte bSegmentType;
        public Byte bSegmentID;
        public Byte bActionType;
        public String[] bIMSI;
        public Byte[] bUeActionFlag;

        public STRUCT_CONFIG_IMSI_MSG_V3(int arrayNum) {
            this.wTotalImsi = 0;
            this.bIMSINum = 0;
            this.bSegmentType = 0;
            this.bSegmentID = 0;
            this.bActionType = 0;
            this.bIMSI = new String[arrayNum];
            //this.bUeActionFlag = new int[arrayNum];
        }
    }

    private boolean NoEmpty(String str)
    {
        return ((str != null) && (!str.isEmpty()));
    }

    public void HandleMsg(DeviceDataStruct dds, Msg_Body_Struct msg) {
        if (msg.type.equals(Msg_Body_Struct.straight_msg)) {
            MsgRecvStruct recv = null;
            String msg_data = GetMsgStringValueInList("data", msg.dic);
            msg_data = msg_data.replace(" ", "");
            if (!NoEmpty(msg_data))
            {
                Logs.e(TAG, "收到XML消息格式错误，XML中data字段为空！",true);
                return;
            }
            if (msg_data.length() < MsgHadeLen)
            {
                Logs.e(TAG, "收到XML消息格式错误，XML中data字段长度过短！",true);
                return;
            }

            recv = DecodeGsmMsg(true,msg_data);
            if (recv == null)
            {
                Logs.e(TAG, "收到XML消息格式错误！",true);
                return;
            }

            HandleStraightMsg(dds, recv);
        } else if (msg.type.equalsIgnoreCase(Msg_Body_Struct.get_general_para_response)) {
            CDMA_GeneralPara gPara = new CDMA_GeneralPara();

            gPara.setSn(DeviceFragmentStruct.getDevice(dds.getIp(),dds.getPort()).getSN());
            String reportType = GetMsgStringValueInList("reportType", msg.dic, "report");
            int sys = GetMsgIntValueInList("sys", msg.dic, Sys1);

            if (sys == Sys1) {
                gPara.setSys1(DecodeParaResp(msg));
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
                        SendDataAlignOver(Sys1,dds.getIp(), dds.getPort(), 0);
                    } else {
                        SetParameterRequest(dds.getIp(), dds.getPort(), whiteurl, blackurl);
                    }
                }
            } else if (sys == Sys2) {
                gPara.setSys2(DecodeParaResp(msg));
                if (reportType.equals("report")) {
                    //回复数据对齐完成
                    SendDataAlignOver(Sys2,dds.getIp(),dds.getPort(),0);
                }
            }

            DeviceFragmentStruct.ChangeGeneralPara(dds.getIp(),dds.getPort(),gPara);

            EventBus.getDefault().post(gPara);

        } else if(msg.type.equalsIgnoreCase(Msg_Body_Struct.set_general_para_response)) {
            //回复数据对齐完成
            SendDataAlignOver(Sys1,dds.getIp(), dds.getPort(),GetMsgIntValueInList("result", msg.dic, 0));
        } else if(msg.type.equalsIgnoreCase(Msg_Body_Struct.ack_msg)) {
            MsgRecvStruct recv = null;
            String msg_data = GetMsgStringValueInList("data", msg.dic);
            msg_data = msg_data.replace(" ", "");
            if (!NoEmpty(msg_data))
            {
                Logs.e(TAG, "收到XML消息格式错误，XML中data字段为空！",true);
                return;
            }
            if (msg_data.length() < MsgHadeLen)
            {
                Logs.e(TAG, "收到XML消息格式错误，XML中data字段长度过短！",true);
                return;
            }

            recv = DecodeGsmMsg(true,msg_data);
            if (recv == null)
            {
                Logs.e(TAG, "收到XML消息格式错误！",true);
                return;
            }
            HandleAckMsg(dds,recv);
        } else if(msg.type.equalsIgnoreCase(Msg_Body_Struct.DataAlignOverAck)) {

        } else {
            Logs.e(TAG, String.format("消息类型(%s)为不支持的消息类型！", msg.type),true);
        }
    }

    private void HandleAckMsg(DeviceDataStruct dds, MsgRecvStruct recv) {
        if (recv.bMsgId == CONFIG_FAP_MSG) {
            CDMA_GeneralPara.ConfigOrCarrierPara  para = new CDMA_GeneralPara().new ConfigOrCarrierPara();
            CDMA_GeneralPara.GeneralPara sys_para = new CDMA_GeneralPara().new GeneralPara();

            //小区配置
            String data = "";
            data = recv.data;
            data = data.replace(" ","");
            GetDataValue gdv = new GetDataValue(data);

            sys_para.setbWorkingMode(gdv.GetValueByString_Byte());
            sys_para.setbC(gdv.GetValueByString_Byte());
            sys_para.setwRedirectCellUarfcn(gdv.GetValueByString_U16());
            gdv.GetValue_Reserved(4);
            gdv.GetValue_Reserved(4);
            sys_para.setbPLMNId(asciiToString(gdv.GetValueByString_String(10)));
            sys_para.setbTxPower(gdv.GetValueByString_Byte());
            gdv.GetValueByString_SByte();
            sys_para.setbRxGain(gdv.GetValueByString_Byte());
            sys_para.setwPhyCellId(gdv.GetValueByString_U16());
            sys_para.setwLAC(gdv.GetValueByString_U16());
            sys_para.setwUARFCN(gdv.GetValueByString_U16());
            gdv.GetValue_Reserved(2);
            sys_para.setwCellId(gdv.GetValueByString_U32());
            gdv.GetValue_Reserved(32);

            para.setFlag(0);
            para.setSn(dds.getSN());
            para.setSys(recv.bCellIdx);
            para.setgPara(sys_para);

            EventBus.getDefault().post(para);

        } else if (recv.bMsgId  == CONFIG_CDMA_CARRIER_MSG) {
            CDMA_GeneralPara.ConfigOrCarrierPara  para = new CDMA_GeneralPara().new ConfigOrCarrierPara();
            CDMA_GeneralPara.GeneralPara sys_para = new CDMA_GeneralPara().new GeneralPara();

            //多载波配置
            String data = "";
            data = recv.data;
            data = data.replace(" ","");
            GetDataValue carr_gdv = new GetDataValue(data);

            sys_para.setwARFCN1(carr_gdv.GetValueByString_U16());
            sys_para.setbARFCN1Mode(carr_gdv.GetValueByString_Byte());
            carr_gdv.GetValue_Reserved(1);
            sys_para.setwARFCN1Duration(carr_gdv.GetValueByString_U16());
            sys_para.setwARFCN1Period(carr_gdv.GetValueByString_U16());

            sys_para.setwARFCN2(carr_gdv.GetValueByString_U16());
            sys_para.setbARFCN2Mode(carr_gdv.GetValueByString_Byte());
            carr_gdv.GetValue_Reserved(1);
            sys_para.setwARFCN2Duration(carr_gdv.GetValueByString_U16());
            sys_para.setwARFCN2Period(carr_gdv.GetValueByString_U16());

            sys_para.setwARFCN3(carr_gdv.GetValueByString_U16());
            sys_para.setbARFCN3Mode(carr_gdv.GetValueByString_Byte());
            carr_gdv.GetValue_Reserved(1);
            sys_para.setwARFCN3Duration(carr_gdv.GetValueByString_U16());
            sys_para.setwARFCN3Period(carr_gdv.GetValueByString_U16());

            sys_para.setwARFCN4(carr_gdv.GetValueByString_U16());
            sys_para.setbARFCN4Mode(carr_gdv.GetValueByString_Byte());
            carr_gdv.GetValue_Reserved(1);
            sys_para.setwARFCN4Duration(carr_gdv.GetValueByString_U16());
            sys_para.setwARFCN4Period(carr_gdv.GetValueByString_U16());

            para.setFlag(1);
            para.setSn(dds.getSN());
            para.setSys(recv.bCellIdx);
            para.setgPara(sys_para);

            EventBus.getDefault().post(para);

        } else {
            Logs.w(TAG, "HandleAckMsg收到的Ap消息类型(" + recv.bMsgId + ")错误！",true);
        }
    }

    private void HandleStraightMsg(DeviceDataStruct dds, MsgRecvStruct recv) {
        Logs.d(TAG, String.format("处理HandleStraightMsg消息(%d)！",recv.bMsgId),true);
        String data = recv.data;
        if (recv.bMsgId == CONFIG_FAP_MSG ) {
            if (recv.bMsgType == SUCC_OUTCOME) {
                EventBus.getDefault().post(new WaitDialogData(
                        HandleRecvXmlMsg.CDMA_CELL_CONFIG,"", WaitDialogData.RUSULT_OK));
            } else if (recv.bMsgType == UNSUCC_OUTCOME) {
                EventBus.getDefault().post(new WaitDialogData(
                        HandleRecvXmlMsg.CDMA_CELL_CONFIG,"", WaitDialogData.RUSULT_FAIL));
            }
        } else if (recv.bMsgId  == CONFIG_CDMA_CARRIER_MSG) {
            if (recv.bMsgType == SUCC_OUTCOME) {
                EventBus.getDefault().post(new WaitDialogData(
                        HandleRecvXmlMsg.CDMA_CARRIER_SET,"", WaitDialogData.RUSULT_OK));
            } else if (recv.bMsgType == UNSUCC_OUTCOME) {
                EventBus.getDefault().post(new WaitDialogData(
                        HandleRecvXmlMsg.CDMA_CARRIER_SET,"", WaitDialogData.RUSULT_FAIL));
            }
        } else if (recv.bMsgId == QUERY_NB_CELL_INFO_MSG ||
                recv.bMsgId ==CONTROL_FAP_REBOOT_MSG ||
                recv.bMsgId ==CONFIG_SMS_CONTENT_MSG_ID ||
                recv.bMsgId ==CONTROL_FAP_RADIO_ON_MSG ||
                recv.bMsgId ==CONTROL_FAP_RADIO_OFF_MSG ||
                recv.bMsgId ==CONTROL_FAP_RESET_MSG ||
                recv.bMsgId ==CONFIG_IMSI_MSG_V3_ID )
        {
            //Send2Main_SEND_REQ_CNF(apToKen, recv);
        }
        else if (recv.bMsgId ==FAP_NB_CELL_INFO_MSG)
        {
            //Send2Main_FAP_NB_CELL_INFO_MSG(apToKen, recv);
        }
        else if (recv.bMsgId ==FAP_TRACE_MSG)
        {
            //Send2Main_FAP_TRACE_MSG(apToKen, recv,Main2ApControllerMsgType.gsm_msg_recv);
        }
        else if (recv.bMsgId ==UE_STATUS_REPORT_MSG)
        {
            Send2Main_UE_STATUS_REPORT_MSG(dds, recv, Msg_Body_Struct.gsm_msg_recv);
        }
        else if (recv.bMsgId ==UE_ORM_REPORT_MSG)
        {
            //Send2Main_UE_ORM_REPORT_MSG(apToKen, recv, Main2ApControllerMsgType.gsm_msg_recv);
        }
        else if (recv.bMsgId ==FAP_PARAM_REPORT_MSG)
        {
            //Send2Main_FAP_PARAM_REPORT_MSG(apToKen, recv);
        }
        else
        {
            Logs.w(TAG, "HandleStraightMsg收到的Ap消息类型(" + recv.bMsgId + ")错误！",true);
        }
    }
    
    /// <summary>
    /// 解析收到的GSM消息
    /// </summary>
    /// <param name="recvFlag">设备发消息类型</param>
    /// <param name="recv">解析后的消息内容</param>
    /// <param name="msg_data">收到的消息</param>
    /// <returns>解析是否成功</returns>
    private MsgRecvStruct DecodeGsmMsg(boolean recvFlag, String msg_data) {
        MsgRecvStruct recv = new MsgRecvStruct();
        GetDataValue gdv = new GetDataValue(msg_data);
        int bProtocolSap = gdv.GetValueByString_Byte();
        if (bProtocolSap != (int)GSM && bProtocolSap != (int)CDMA)
        {
            Logs.e(TAG, String.format("解析GSM_V2消息格式错误，bProtocolSap为%d,字段错误！", bProtocolSap),true);
            return null;
        }
        recv.bProtocolSap = bProtocolSap;

        int bMsgId = gdv.GetValueByString_Byte();
        recv.bMsgId = bMsgId;

        int bMsgType = gdv.GetValueByString_Byte();
        if (bMsgType != INITIAL_MSG && bMsgType !=SUCC_OUTCOME && bMsgType != UNSUCC_OUTCOME )
        {
            Logs.e(TAG, String.format("解析GSM_V2消息格式错误，bMsgType%d，不在定义中！", bMsgType),true);
            return null;
        }
        recv.bMsgType = bMsgType;

        int bCellIdx = gdv.GetValueByString_Byte();
        if (bCellIdx != (int)Sys1 && bCellIdx != (int)Sys2)
        {
            Logs.e(TAG, String.format("解析GSM_V2消息格式错误，bCellIdx为%d,字段错误！", bCellIdx),true);
            return null;
        }
        recv.bCellIdx = bCellIdx;

        recv.wMsgLen = gdv.GetValueByString_U16();
        recv.wMsgLen -= MsgHadeLen;//去掉消息头后的净数据长度
        if (recv.wMsgLen < 0)
        {
            Logs.e(TAG, "解析GSM消息格式错误，wMsgLen字段错误！",true);
            return null;
        }

        recv.wSqn = gdv.GetValueByString_U16();
        if (recv.wSqn < 0)
        {
            Logs.e(TAG,"解析GSM消息格式错误，wSqn字段错误！",true);
            return null;
        }

        recv.dwTimeStamp = gdv.GetValueByString_U32();

        recv.data = "";
        if (recv.wMsgLen > 0)
        {
            //data = msg_data.substring(24, data_length*2);
            recv.data = gdv.GetValueByString_String(recv.wMsgLen * 2);
            if (!NoEmpty(recv.data))
            {
                Logs.e(TAG,"解析GSM消息格式错误，data字段错误！",true);
                return null;
            }
        }

        return recv;
    }

    private String asciiToString(String value)  {
        StringBuffer sbu = new StringBuffer();
        for (int i = 0; i < value.length(); i+=2) {
            sbu.append((char) Integer.parseInt(value.substring(i,i+2),16));
        }
        return sbu.toString();
    }

    private String stringToAscii(String value) {
        String sbu = "";
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            sbu = String.format("%s%02X",sbu,(int)chars[i]);
        }

        return sbu;
    }

    private CDMA_GeneralPara.GeneralPara DecodeParaResp(Msg_Body_Struct msg) {
        CDMA_GeneralPara.GeneralPara sys_para = new CDMA_GeneralPara().new GeneralPara();

        //小区配置
        String data = "";
        data = GetMsgStringValueInList("fap_cfg", msg.dic);
        data = data.replace(" ","");
        GetDataValue gdv = new GetDataValue(data);
        gdv.GetValueByString_String(RECV_MSG_LEN * 2);

        sys_para.setbWorkingMode(gdv.GetValueByString_Byte());
        sys_para.setbC(gdv.GetValueByString_Byte());
        sys_para.setwRedirectCellUarfcn(gdv.GetValueByString_U16());
        gdv.GetValue_Reserved(4);
        gdv.GetValue_Reserved(4);
        sys_para.setbPLMNId(asciiToString(gdv.GetValueByString_String(10)));
        sys_para.setbTxPower(gdv.GetValueByString_Byte());
        gdv.GetValueByString_SByte();
        sys_para.setbRxGain(gdv.GetValueByString_Byte());
        sys_para.setwPhyCellId(gdv.GetValueByString_U16());
        sys_para.setwLAC(gdv.GetValueByString_U16());
        sys_para.setwUARFCN(gdv.GetValueByString_U16());
        gdv.GetValue_Reserved(2);
        sys_para.setwCellId(gdv.GetValueByString_U32());
        gdv.GetValue_Reserved(32);

        //多载波配置
        data = "";
        data = GetMsgStringValueInList("cdma_carrier_cfg", msg.dic);
        data = data.replace(" ","");
        GetDataValue carr_gdv = new GetDataValue(data);
        carr_gdv.GetValueByString_String(RECV_MSG_LEN * 2);

        sys_para.setwARFCN1(carr_gdv.GetValueByString_U16());
        sys_para.setbARFCN1Mode(carr_gdv.GetValueByString_Byte());
        carr_gdv.GetValue_Reserved(1);
        sys_para.setwARFCN1Duration(carr_gdv.GetValueByString_U16());
        sys_para.setwARFCN1Period(carr_gdv.GetValueByString_U16());

        sys_para.setwARFCN2(carr_gdv.GetValueByString_U16());
        sys_para.setbARFCN2Mode(carr_gdv.GetValueByString_Byte());
        carr_gdv.GetValue_Reserved(1);
        sys_para.setwARFCN2Duration(carr_gdv.GetValueByString_U16());
        sys_para.setwARFCN2Period(carr_gdv.GetValueByString_U16());

        sys_para.setwARFCN3(carr_gdv.GetValueByString_U16());
        sys_para.setbARFCN3Mode(carr_gdv.GetValueByString_Byte());
        carr_gdv.GetValue_Reserved(1);
        sys_para.setwARFCN3Duration(carr_gdv.GetValueByString_U16());
        sys_para.setwARFCN3Period(carr_gdv.GetValueByString_U16());

        sys_para.setwARFCN4(carr_gdv.GetValueByString_U16());
        sys_para.setbARFCN4Mode(carr_gdv.GetValueByString_Byte());
        carr_gdv.GetValue_Reserved(1);
        sys_para.setwARFCN4Duration(carr_gdv.GetValueByString_U16());
        sys_para.setwARFCN4Period(carr_gdv.GetValueByString_U16());


        return sys_para;
    }

    private class GetDataValue {
        String MsgData;
    
        public GetDataValue(String MsgData)
        {
            this.MsgData = MsgData;
        }

        private String GetValueByString_String(int len) {
            if (MsgData.length() < len) {
                MsgData = "";
                return "";
            }
            String value = MsgData.substring(0, len);
            MsgData = MsgData.substring(len);
            return value;
        }

        private Integer GetValueByString_Byte() {
            int len = 2;
            if (MsgData.length() < len) {
                MsgData = "";
                return 0;
            }
            int value = Integer.parseInt(MsgData.substring(0, len), 16);
            MsgData = MsgData.substring(len);
            return value;
        }

        private int GetValueByString_SByte() {
            int len = 2;
            if (MsgData.length() < len) {
                MsgData = "";
                return 0;
            }
            int value = Integer.parseInt(MsgData.substring(0, len), 16);
            if (value > Byte.MAX_VALUE)
            {
                value = Byte.MAX_VALUE - value;
            }
            MsgData = MsgData.substring(len);
            return value;
        }

        private int GetValueByString_U16() {
            int len = 4;
            if (MsgData.length() < len) {
                MsgData = "";
                return 0;
            }
            int value = Integer.parseInt(MsgData.substring(0, len), 16);
            MsgData = MsgData.substring(len);
            return value;
        }

        private long GetValueByString_U32() {
            int len = 8;
            if (MsgData.length() < len) {
                MsgData = "";
                return 0;
            }
            long value = Long.parseLong(MsgData.substring(0, len), 16);
            MsgData = MsgData.substring(len);
            return value;
        }

        private void GetValue_Reserved(int len) {
            len = len * 2;
            if (MsgData.length() < len) {
                MsgData = "";
                return;
            }
            String value = MsgData.substring(0, len);
            MsgData = MsgData.substring(len);
            return;
        }
}

    /// <summary>
    /// 获取保留字段
    /// </summary>
    /// <param name="len">保留字段长度</param>
    /// <returns></returns>
    private String getReservedString(int len)
    {
        return String.format("%0" + len * 2 + "s", "0");
    }

    /// <summary>
    /// 向AP发送消息
    /// </summary>
    /// <param name="apToKen">AP信息</param
    /// <param name="msg">GSM文档格式的消息内容，十六进制“AA AA 01 00 15 0E 55 66 00 4B 00 54 02 2A 00 00 00 00 00 00”</param>
    private void Send2ap_GSM(String ip,int port, MsgSendStruct sendMsg) {
        String data = String.format("%02X%02X%02X%02X%04X%04X%08X%s",
                sendMsg.bProtocolSap ,
                sendMsg.bMsgId,
                sendMsg.bMsgType,
                sendMsg.bCellIdx,
                sendMsg.wMsgLen,
                sendMsg.wSqn,
                sendMsg.dwTimeStamp,
                sendMsg.data);

        //在两个字符间加上空格
        String sendData = data.replaceAll ("(.{2})", "$1 ").toUpperCase();

        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.straight_msg);
        msg.dic.put("data",sendData.trim());

        EventBusMsgSendUDPMsg ebmsm = new EventBusMsgSendUDPMsg(ip,port,EncodeApXmlMessage(msg));
        EventBus.getDefault().post(ebmsm);

        return;
    }

    private void Send2Main_UE_STATUS_REPORT_MSG(DeviceDataStruct dds, MsgRecvStruct recv, String msgType){
        GetDataValue gdv = new GetDataValue(recv.data);
        Msg_Body_Struct TypeKeyValue = new Msg_Body_Struct(msgType);
        TypeKeyValue.dic.put("sys", recv.bCellIdx);
        TypeKeyValue.dic.put("hardware_id", 0);

        Name_DIC_Struct nDic = new Name_DIC_Struct();
        nDic.name = "UE_STATUS_REPORT_MSG";

        int addFlag = 0;
        int type1 = gdv.GetValueByString_Byte();

        String msg = gdv.GetValueByString_String(30);
        GetDataValue item1 = new GetDataValue(msg);
        int rsrp = gdv.GetValueByString_SByte();
        int len = gdv.GetValueByString_Byte();

        if (type1 == 1)
        {
            String imsi = "";
            for (int i = 0; i < len; i++)
            {
                imsi = String.format("%s%s", imsi, item1.GetValueByString_Byte().toString());
            }
            nDic.dic.put("imsi", imsi);
            addFlag |= 0x1;
        }
        else if (type1 == 2)
        {
            String imsi = "";
            for (int i = 0; i < len; i++)
            {
                imsi = String.format("%s%s", imsi, Integer.toHexString(item1.GetValueByString_Byte() & 0xFF));
            }
            nDic.dic.put("tmsi", "0x" + imsi.toString());
            addFlag |= 0x2;
        }
        else if (type1 == 3)
        {
            String imsi = "";
            for (int i = 0; i < len; i++)
            {
                if (len == 8) {//cdma esn
                    imsi = String.format("%s%s", imsi, Integer.toHexString(item1.GetValueByString_Byte() & 0xFF));
                } else {
                    imsi = String.format("%s%s", imsi, item1.GetValueByString_Byte().toString());
                }
            }
            if (len == 8 ) {
                nDic.dic.put("imei", "0x" + imsi.toString());
            }else {
                nDic.dic.put("imei", imsi.toString());
            }
            addFlag |= 0x4;
        }


        int type2 = gdv.GetValueByString_Byte();
        msg = gdv.GetValueByString_String(30).toString();
        GetDataValue item2 = new GetDataValue(msg);
        len = gdv.GetValueByString_Byte();

        if (type2 == 1)
        {
            String imsi = "";
            for (int i = 0; i < len; i++)
            {
                imsi = String.format("%s%s", imsi, item2.GetValueByString_Byte().toString());
            }
            nDic.dic.put("imsi", imsi.toString());
            addFlag |= 0x1;
        }
        else if (type2 == 2)
        {
            String imsi = "";
            for (int i = 0; i < len; i++)
            {
                imsi = String.format("%s%s", imsi, Integer.toHexString(item2.GetValueByString_Byte() & 0xFF));
            }
            nDic.dic.put("tmsi", "0x" + imsi.toString());
            addFlag |= 0x2;
        }
        else if (type2 == 3)
        {
            String imsi = "";
            for (int i = 0; i < len; i++)
            {
                if (len == 8) {//cdma esn
                    imsi = String.format("%s%s", imsi, Integer.toHexString(item2.GetValueByString_Byte() & 0xFF));
                } else {
                    imsi = String.format("%s%s", imsi, item2.GetValueByString_Byte().toString());
                }
            }
            if (len == 8 ) {
                nDic.dic.put("imei", "0x" + imsi.toString());
            }else {
                nDic.dic.put("imei", imsi.toString());
            }
            addFlag |= 0x4;
        }


        int type3 = gdv.GetValueByString_Byte();
        msg = gdv.GetValueByString_String(30).toString();
        GetDataValue item3 = new GetDataValue(msg);
        len = gdv.GetValueByString_Byte();

        if (type3 == 1)
        {
            String imsi = "";
            for (int i = 0; i < len; i++)
            {
                imsi = String.format("%s%s", imsi, item3.GetValueByString_Byte().toString());
            }
            nDic.dic.put("imsi", imsi.toString());
            addFlag |= 0x1;
        }
        else if (type3 == 2)
        {
            String imsi = "";
            for (int i = 0; i < len; i++)
            {
                imsi = String.format("%s%s", imsi, Integer.toHexString(item3.GetValueByString_Byte() & 0xFF));
            }
            nDic.dic.put("tmsi", "0x" + imsi.toString());
            addFlag |= 0x2;
        }
        else if (type3 == 3)
        {
            String imsi = "";
            for (int i = 0; i < len; i++)
            {
                if (len == 8) {//cdma esn
                    imsi = String.format("%s%s", imsi, Integer.toHexString(item3.GetValueByString_Byte() & 0xFF));
                } else {
                    imsi = String.format("%s%s", imsi, item3.GetValueByString_Byte().toString());
                }
            }
            if (len == 8 ) {
                nDic.dic.put("imei", "0x" + imsi.toString());
            }else {
                nDic.dic.put("imei", imsi.toString());
            }
            addFlag |= 0x4;
        }

        int userType = gdv.GetValueByString_Byte();

        if ((addFlag & 0X1) <= 0)
        {
            nDic.dic.put("imsi", "");
        }
        if ((addFlag & 0X2) <= 0)
        {
            nDic.dic.put("tmsi", "");
        }
        if ((addFlag & 0X4) <= 0)
        {
            nDic.dic.put("imei", "");
        }

        nDic.dic.put("rsrp", String.valueOf(rsrp));
        if (userType == 0) {
            nDic.dic.put("userType", 2);
        } else if (userType == 1) {
            nDic.dic.put("userType", 0);
        } else if (userType == 5) {
            nDic.dic.put("userType", 1);
        } else {
            nDic.dic.put("userType", 2);
        }
        nDic.dic.put("sn", dds.getSN());

        TypeKeyValue.n_dic.add(nDic);

        TargetDataStruct targetDataStruct = new TargetDataStruct();

        targetDataStruct.setSN(dds.getSN());
        targetDataStruct.setIP(dds.getIp());
        targetDataStruct.setPort(dds.getPort());
        targetDataStruct.setFullName(dds.getFullName());
        targetDataStruct.setDeviceType(dds.getMode());

        targetDataStruct.setImsi(GetMsgStringValueInList("imsi", nDic.dic, ""));
        targetDataStruct.setiUserType(GetMsgIntValueInList("userType", nDic.dic, 2));
        //Logs.d(TAG,"用户类型：" + targetDataStruct.getiUserType());
        targetDataStruct.setImei(GetMsgStringValueInList("imei", nDic.dic, ""));
        targetDataStruct.setTmsi(GetMsgStringValueInList("tmsi", nDic.dic, ""));
        targetDataStruct.setRsrp(GetMsgIntValueInList("rsrp", nDic.dic, 0));
        targetDataStruct.setbPositionStatus(true);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
        targetDataStruct.setStrAttachtime(formatter.format(new Date()));

        //OnSendMsg2Main(recv.wSqn, MsgType.NOTICE, dds, TypeKeyValue);
        if (FragmentScannerListen.isOpen) {
            //更新界面
            EventBus.getDefault().post(targetDataStruct);
        } else {
            //保存到列表
            AdapterScanner.AddScannerImsi(targetDataStruct);
        }
    }

    private void Send2ap_CONTROL_FAP_RADIO_ON_MSG(String ip,int port, int sys) {
        Send2ap_GSM(ip,port, new MsgSendStruct(CONTROL_FAP_RADIO_ON_MSG, sys, ""));
    }

    private void Send2ap_CONTROL_FAP_RADIO_OFF_MSG(String ip,int port,int sys) {
        Send2ap_GSM(ip,port, new MsgSendStruct(CONTROL_FAP_RADIO_OFF_MSG, sys, ""));
    }

    private void Send2ap_CONTROL_FAP_REBOOT_MSG(String ip,int port, int sys,int flag) {
        String data = String.format("%02X%s", flag,getReservedString(3));
        Send2ap_GSM(ip,port, new MsgSendStruct(CONTROL_FAP_REBOOT_MSG, sys,data));
    }

    public void SetApRedio(String ip,int port,int sys,boolean isOn) {
        if (isOn) {
            Send2ap_CONTROL_FAP_RADIO_ON_MSG(ip, port, sys);
        } else {
            Send2ap_CONTROL_FAP_RADIO_OFF_MSG(ip, port, sys);
        }
    }

    public void SetApReboot(String ip,int port,int flag) {
        Send2ap_CONTROL_FAP_REBOOT_MSG(ip,port,GSM_ZYF.Sys1,flag);
        Send2ap_CONTROL_FAP_REBOOT_MSG(ip,port,GSM_ZYF.Sys2,flag);
        return;
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

    public void SendDataAlignOver(int sys,String ip,int port,int result) {
        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.DataAlignOver);
        msg.dic.put("sys",sys);
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

    public void SendStatusRequest(String ip,int port) {
        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.status_request);
        msg.dic.put("timeout",0);
        String sendText = EncodeApXmlMessage(msg);

        EventBusMsgSendUDPMsg ebmsm = new EventBusMsgSendUDPMsg(ip,port,sendText);
        EventBus.getDefault().post(ebmsm);

        return ;
    }

    public void SendGeneralParaRequest(int sys,String ip,int port) {
        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.get_general_para_request);
        msg.dic.put("sys",sys);
        msg.dic.put("timeout",0);
        String sendText = EncodeApXmlMessage(msg);

        EventBusMsgSendUDPMsg ebmsm = new EventBusMsgSendUDPMsg(ip,port,sendText);
        EventBus.getDefault().post(ebmsm);

        return ;
    }

    public void Send2ap_CONFIG_FAP_MSG(String ip,int port, int sys,CDMA_GeneralPara.GeneralPara para) {
        String data = String.format("%02X%02X%04X%08X%08X%s%02X%02X%02X%04X%04X%04X%04X%08X%032X",
                para.getbWorkingMode(),
                para.getbC(),
                para.getwRedirectCellUarfcn(),
                0,
                System.currentTimeMillis()/1000,
                stringToAscii(para.getbPLMNId()),
                para.getbTxPower(),
                0,
                para.getbRxGain(),
                para.getwPhyCellId(),
                para.getwLAC(),
                para.getwUARFCN(),
                0,
                para.getwCellId(),
                0);

        Send2ap_GSM(ip,port, new MsgSendStruct(CONFIG_FAP_MSG, sys, data));
        EventBus.getDefault().post(new WaitDialogData(
                HandleRecvXmlMsg.CDMA_CELL_CONFIG,"", WaitDialogData.SEND));
    }

    public void Send2ap_CONFIG_CDMA_CARRIER_MSG(String ip,int port, int sys,CDMA_GeneralPara.GeneralPara para) {
        String data = String.format("%04X%02X%02X%04X%04X%04X%02X%02X%04X%04X%04X%02X%02X%04X%04X%04X%02X%02X%04X%04X",
                para.getwARFCN1(),
                para.getbARFCN1Mode(),
                0,
                para.getwARFCN1Duration(),
                para.getwARFCN1Period(),
                para.getwARFCN2(),
                para.getbARFCN2Mode(),
                0,
                para.getwARFCN2Duration(),
                para.getwARFCN2Period(),
                para.getwARFCN3(),
                para.getbARFCN3Mode(),
                0,
                para.getwARFCN3Duration(),
                para.getwARFCN3Period(),
                para.getwARFCN4(),
                para.getbARFCN4Mode(),
                0,
                para.getwARFCN4Duration(),
                para.getwARFCN4Period());

        Send2ap_GSM(ip,port, new MsgSendStruct(CONFIG_CDMA_CARRIER_MSG, sys, data));
        EventBus.getDefault().post(new WaitDialogData(
                HandleRecvXmlMsg.CDMA_CARRIER_SET,"", WaitDialogData.SEND));
    }

    public void SetParameterRequest(String ip,int port,String whiteurl,String blackurl) {
        if (whiteurl.equals("") && blackurl.equals(""))  return;

        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.set_general_para_request);
        msg.dic.put("sys",0);
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
}
