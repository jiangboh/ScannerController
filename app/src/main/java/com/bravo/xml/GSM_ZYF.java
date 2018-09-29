package com.bravo.xml;

import android.content.Context;

import com.bravo.data_ben.DeviceDataStruct;

/**
 * Created by admin on 2018-9-28.
 */

public class GSM_ZYF {
    private final String TAG = "GSM_ZYF";

    private final byte MsgHadeLen = 12;

    private static final int Sys1 = 0;
    private static final int Sys2 = 1;

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
    public GSM_ZYF(Context context){
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
            this.bProtocolSap = GSM;
            this.bMsgId = MsgId;
            this.bMsgType = INITIAL_MSG;
            this.bCellIdx = sys;
            this.wMsgLen = (int) (MsgHadeLen + (data.replace(" ", "").length() / 2));
            this.wSqn = 0;
            this.dwTimeStamp = System.currentTimeMillis();
            this.data = data;
        }

        public MsgSendStruct(int MsgId, int sys, int sqn, String data) {
            this.bProtocolSap = GSM;
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
        public byte bWorkingMode;         //工作模式:1 为侦码模式 ;3驻留模式.
        public byte bC;                   //是否自动切换模式。保留
        public int wRedirectCellUarfcn;  //CDMA黑名单频点
        public Long dwDateTime;           //当前时间
        public String bPLMNId;              //PLMN标志
        public byte bTxPower;             //实际发射功率.设置发射功率衰减寄存器, 0输出最大功率, 每增加1, 衰减1DB
        public byte bRxGain;              //接收信号衰减寄存器. 每增加1增加1DB的增益
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
            //this.bUeActionFlag = new byte[arrayNum];
        }
    }

    public void HandleMsg(DeviceDataStruct dds, Msg_Body_Struct msg) {
        if (msg.type.equalsIgnoreCase(Msg_Body_Struct.scanner)) {

        }
    }
/*
    private String GetValueByString_String(int len, String data)
    {
        if (data.length() < len)
        {
            data = "";
            return "";
        }
        String value = data.substring(0, len);
        data = data.substring(len);
        return value;
    }
    private Byte GetValueByString_Byte(String data)
    {
        int len = 2;
        if (data.length() < len)
        {
            data = "";
            return 0;
        }
        Byte value = Byte.parseByte(data.substring(0, len), 16);
        data = data.substring(len);
        return value;
    }
    private int GetValueByString_U16(String data)
    {
        int len = 4;
        if (data.length() < len)
        {
            data = "";
            return 0;
        }
        int value = Integer.parseInt(data.substring(0, len), 16);
        data = data.substring(len);
        return value;
    }
    private Long GetValueByString_U32(String data)
    {
        int len = 8;
        if (data.length() < len)
        {
            data = "";
            return 0L;
        }
        Long value = Long.parseLong(data.substring(0, len), 16);
        data = data.substring(len);
        return value;
    }
    private void GetValue_Reserved(int len,String data)
    {
        len = len * 2;
        if (data.length() < len)
        {
            data = "";
            return ;
        }
        String value = data.substring(0, len);
        data = data.substring(len);
        return;
    }

    public static Long heartbeatMsgNum = 0L;
    public static Long imsiMsgNum = 0L;
    private final byte MsgHadeLen = 12;
    
   

    private void HandleGsmMsg(DeviceDataStruct apToKen, MsgRecvStruct recv)
    {
        String data = recv.data;
        if (recv.bMsgId == QUERY_NB_CELL_INFO_MSG ||
                recv.bMsgId == CONFIG_FAP_MSG ||
                recv.bMsgId == CONTROL_FAP_REBOOT_MSG ||
                recv.bMsgId == CONFIG_SMS_CONTENT_MSG_ID ||
                recv.bMsgId == CONTROL_FAP_RADIO_ON_MSG ||
                recv.bMsgId == CONTROL_FAP_RADIO_OFF_MSG ||
                recv.bMsgId == CONTROL_FAP_RESET_MSG ||
                recv.bMsgId == CONFIG_CDMA_CARRIER_MSG ||
                recv.bMsgId == CONFIG_IMSI_MSG_V3_ID)
        {
            Send2Main_SEND_REQ_CNF(apToKen, recv);
        }
        else if (recv.bMsgId == FAP_NB_CELL_INFO_MSG)
        {
            Send2Main_FAP_NB_CELL_INFO_MSG(apToKen, recv);
        }
        else if (recv.bMsgId == FAP_TRACE_MSG)
        {
            Send2Main_FAP_TRACE_MSG(apToKen, recv,Msg_Body_Struct.gsm_msg_recv);
        }
        else if (recv.bMsgId == UE_STATUS_REPORT_MSG)
        {
            Send2Main_UE_STATUS_REPORT_MSG(apToKen, recv, Msg_Body_Struct.gsm_msg_recv);
        }
        else if (recv.bMsgId == UE_ORM_REPORT_MSG)
        {
            Send2Main_UE_ORM_REPORT_MSG(apToKen, recv, Msg_Body_Struct.gsm_msg_recv);
        }
        else if (recv.bMsgId == FAP_PARAM_REPORT_MSG)
        {
            Send2Main_FAP_PARAM_REPORT_MSG(apToKen, recv);
        }
        else
        {
            Log.w(TAG, "HandleGsmMsg收到的Ap消息类型错误！");
        }
    }

    private void HandleGsmAckMsg(DeviceDataStruct apToKen, MsgRecvStruct recv)
    {
        String data = recv.data;
        if (recv.bMsgId == CONFIG_FAP_MSG)
        {
            Send2Main_CONFIG_FAP_MSG(apToKen, recv);
        }
        else if (recv.bMsgId == FAP_TRACE_MSG)
        {
            Send2Main_FAP_TRACE_MSG(apToKen, recv, Msg_Body_Struct.ReportGenPara);
        }
        else if (recv.bMsgId == UE_STATUS_REPORT_MSG)
        {
            //Send2Main_UE_STATUS_REPORT_MSG(apToKen, recv, Msg_Body_Struct.ReportGenPara);
        }
        else if (recv.bMsgId == UE_ORM_REPORT_MSG)
        {
            Send2Main_UE_ORM_REPORT_MSG(apToKen, recv, Msg_Body_Struct.ReportGenPara);
        }
        else if (recv.bMsgId == CONFIG_SMS_CONTENT_MSG_ID)
        {
            Send2Main_CONFIG_SMS_CONTENT_MSG_ID(apToKen, recv);
        }
        else if (recv.bMsgId == CONFIG_CDMA_CARRIER_MSG)
        {
            Send2Main_CONFIG_CDMA_CARRIER_MSG(apToKen, recv);
        }
        else if (recv.bMsgId == CONFIG_IMSI_MSG_V3_ID)
        {
            Send2Main_CONFIG_IMSI_MSG_V3_ID(apToKen, recv);
        }
        else
        {
            Log.w(TAG, "HandleGsmAckMsg收到的Ap消息类型错误！");
        }
    }

    /// <summary>
    /// 获取保留字段
    /// </summary>
    /// <param name="len">保留字段长度</param>
    /// <returns></returns>
    private String getReservedString(byte len)
    {
        return "0".PadLeft(len * 2, '0');
    }

    private String fullString(byte len;String str)
    {
        if (str.length() >= len)
            return str;
        else
            return str + "0".PadLeft(len - str.length(), '0');
    }

    private String StringaddZero(String str)
    {
        String data = "";
        for (int i=0;i<str.length();i++)
        {
            data = String.format("{0}{1}",data,str[i].ToString().PadLeft(2,'0'));
        }
        return data;
    }

    private void SendMainMsgParaVlaueError(DeviceDataStruct ApInfo, App_Info_Struct AppInfo, String type, String name)
    {
        String str = String.format("发送给GSM设备消息({0})错误。消息中参数项({1})缺失或值错误。", type.ToString(), name);
        Log.w(TAG, str);
        Send2APP_GeneralError(ApInfo, AppInfo, type, str);
        return;
    }

    /// <summary>
    /// 向Ap发送Main模块过来的消息
    /// </summary>
    /// <param name="MainMsg"></param>
    private void Send2ap_RecvMainMsg(InterModuleMsgStruct MainMsg)
    {
        AsyncUserToken apToKen = MyDeviceList.FindByApInfo(MainMsg.ApInfo);
        if (apToKen == null)
        {
            Log.w(TAG, String.format("在线AP列表中找不到Ap[{0}:{1}]设备({2})!",
                    MainMsg.ApInfo.IP, MainMsg.ApInfo.Port.ToString(), MainMsg.ApInfo.Fullname));
            return;
        }

        MsgId2App msgId2App = new MsgId2App();
        msgId2App.id = ApMsgIdClass.addNormalMsgId();
        msgId2App.AppInfo = MainMsg.AppInfo;

        if (MyDeviceList.addMsgId2App(apToKen, msgId2App))
        {
            if (!SendMsg2Ap(apToKen, msgId2App.id, MainMsg.Body))
            {
                Send2APP_GeneralError(MainMsg.ApInfo, MainMsg.AppInfo, MainMsg.Body.type,
                        String.format("封装向AP发送的XML消息({0})出错！", MainMsg.Body.type));
            }
        }
    }

    /// <summary>
    /// 透传MainController模块过来的消息给设备
    /// </summary>
    /// <param name="MainMsg"></param>
    private void Send2ap_TransparentMsg(InterModuleMsgStruct MainMsg)
    {
        AsyncUserToken apToKen = MyDeviceList.FindByApInfo(MainMsg.ApInfo);
        if (apToKen == null)
        {
            Log.w(TAG, String.format("在线AP列表中找不到Ap[{0}:{1}]设备({2})!",
                    MainMsg.ApInfo.IP, MainMsg.ApInfo.Port.ToString(), MainMsg.ApInfo.Fullname));
            return;
        }

        int msgId = ApMsgIdClass.addTransparentMsgId();

        MsgId2App msgId2App = new MsgId2App();
        msgId2App.id = msgId;
        msgId2App.AppInfo = MainMsg.AppInfo;

        if (!MyDeviceList.addMsgId2App(apToKen, msgId2App))
        {
             Log.e(TAG, String.format("添加消息Id到设备列表出错！"));
            Send2APP_GeneralError(MainMsg.ApInfo, MainMsg.AppInfo, MainMsg.Body.type,
                    String.format("添加消息Id到设备列表出错！"));
            return;
        }

        String sendMsg = GetMsgStringValueInList("transparent_msg", MainMsg.Body);
        if (String.IsNullOrEmpty(sendMsg))
        {
             Log.e(TAG, String.format("封装XML消息(Send2ap_TransparentMsg)出错！"));
            Send2APP_GeneralError(MainMsg.ApInfo, MainMsg.AppInfo, MainMsg.Body.type,
                    String.format("封装向AP发送的XML消息出错！"));
            return;
        }
        sendMsg = sendMsg.replace(" ", "");
        sendMsg = sendMsg.Remove(12, 4);
        sendMsg = sendMsg.Insert(12, String.format("{0}", msgId.ToString("X").PadLeft(4, '0')));
        sendMsg = Regex.replace(sendMsg, @".{2}", "$0 ");

        Msg_Body_Struct TypeKeyValue =
                new Msg_Body_Struct(ApMsgType.agent_straight_msg,
                        "data", sendMsg.Trim());

        SendMsg2Ap(apToKen, msgId2App.id, TypeKeyValue);
    }

    /// <summary>
    /// 向AP发送消息
    /// </summary>
    /// <param name="apToKen">AP信息</param
    /// <param name="msg">GSM文档格式的消息内容，十六进制“AA AA 01 00 15 0E 55 66 00 4B 00 54 02 2A 00 00 00 00 00 00”</param>
    private void Send2ap_GSM(AsyncUserToken apToKen, App_Info_Struct AppInfo, MsgSendStruct sendMsg)
    {
        String data = String.format("{0}{1}{2}{3}{4}{5}{6}{7}",
                ((byte)sendMsg.bProtocolSap).ToString("X").PadLeft(2, '0'),
                ((byte)sendMsg.bMsgId).ToString("X").PadLeft(2, '0'),
                ((byte)sendMsg.bMsgType).ToString("X").PadLeft(2, '0'),
                ((byte)sendMsg.bCellIdx).ToString("X").PadLeft(2, '0'),
                ((int)sendMsg.wMsgLen).ToString("X").PadLeft(4, '0'),
                ((int)sendMsg.wSqn).ToString("X").PadLeft(4, '0'),
                ((Long)sendMsg.dwTimeStamp).ToString("X").PadLeft(8, '0'),
                sendMsg.data);

        //在两个字符间加上空格
        String sendData = Regex.replace(data, @".{2}", "$0 ");

        MsgId2App msgId2App = new MsgId2App();
        msgId2App.id = sendMsg.wSqn;
        msgId2App.AppInfo = AppInfo;

        if (!MyDeviceList.addMsgId2App(apToKen, msgId2App))
        {
             Log.e(TAG, String.format("添加消息Id到设备列表出错！"));
            Send2APP_GeneralError(apToKen, AppInfo, sendMsg.bMsgId.ToString(),
                    String.format("添加消息Id到设备列表出错！"));
            return;
        }

        Msg_Body_Struct TypeKeyValue =
                new Msg_Body_Struct(ApMsgType.agent_straight_msg,
                        "data", sendData.Trim());

        SendMsg2Ap(apToKen, sendMsg.wSqn, TypeKeyValue);
    }

    private void Send2ap_QUERY_NB_CELL_INFO_MSG(AsyncUserToken apToKen, App_Info_Struct AppInfo , Device_Sys sys)
    {
        Send2ap_GSM(apToKen, AppInfo, new MsgSendStruct(Send_Msg_Id.QUERY_NB_CELL_INFO_MSG,sys, getReservedString(32)));
    }

    private void Send2ap_CONFIG_FAP_MSG(AsyncUserToken apToKen, App_Info_Struct AppInfo, Device_Sys sys, STRUCT_CONFIG_FAP_MSG para)
    {
        String paraMnc = "";

        //String plmn = "";
        //for (int i = 0; i < 5; i++)
        //{
        //    plmn = String.format("{0}{1}",plmn, GetValueByString_String(1,ref para.bPLMNId).PadLeft(2, '0'));
        //}

        String data = String.format("{0}{1}{2}{3}{4}{5}{6}{7}{8}{9}{10}{11}{12}{13}{14}",
                para.bWorkingMode.ToString("X").PadLeft(2, '0'),
                para.bC.ToString("X").PadLeft(2, '0'),
                para.wRedirectCellUarfcn.ToString("X").PadLeft(4, '0'),
                getReservedString(4),
                para.dwDateTime.ToString("X").PadLeft(8, '0'),
                CodeConver.String2HexString(para.bPLMNId).PadLeft(10, '0'),
                para.bTxPower.ToString("X").PadLeft(2, '0'),
                getReservedString(1),
                para.bRxGain.ToString("X").PadLeft(2, '0'),
                para.wPhyCellId.ToString("X").PadLeft(4, '0'),
                para.wLAC.ToString("X").PadLeft(4, '0'),
                para.wUARFCN.ToString("X").PadLeft(4, '0'),
                getReservedString(2),
                para.dwCellId.ToString("X").PadLeft(8, '0'),
                getReservedString(16));
        Send2ap_GSM(apToKen, AppInfo, new MsgSendStruct(Send_Msg_Id.CONFIG_FAP_MSG,sys, data));
    }

    private void Send2ap_CONFIG_SMS_CONTENT_MSG(AsyncUserToken apToKen, App_Info_Struct AppInfo, Device_Sys sys, String num;String text)
    {
        int len = num.length();
        String phoneNum  = CodeConver.String2HexString(num);
        String phoneText = CodeConver.String2Unicode(text,false);
        if(phoneText.length() >40 || phoneText.length()<=0)
        {
            Send2APP_GeneralError(apToKen,AppInfo,Send_Msg_Id.CONFIG_SMS_CONTENT_MSG.ToString(),
                    "编码后的消息内容长度错误!");
            return;
        }

        String data = String.format("{0}{1}{2}{3}",
                len.ToString("X").PadLeft(2, '0'),
                fullString(36,phoneNum),
                text.length().ToString("X").PadLeft(2, '0'),
                phoneText);
        Send2ap_GSM(apToKen, AppInfo, new MsgSendStruct(Send_Msg_Id.CONFIG_SMS_CONTENT_MSG, sys, data));
    }

    private void Send2ap_CONTROL_FAP_REBOOT_MSG(AsyncUserToken apToKen, App_Info_Struct AppInfo, Device_Sys sys,byte flag)
    {
        String data = String.format("{0}{1}",
                flag.ToString("X").PadLeft(2, '0'),
                getReservedString(3));
        Send2ap_GSM(apToKen, AppInfo, new MsgSendStruct(Send_Msg_Id.CONTROL_FAP_REBOOT_MSG, sys,data));
    }

    private void Send2ap_CONTROL_FAP_RADIO_ON_MSG(AsyncUserToken apToKen, App_Info_Struct AppInfo, Device_Sys sys)
    {
        Send2ap_GSM(apToKen, AppInfo, new MsgSendStruct(Send_Msg_Id.CONTROL_FAP_RADIO_ON_MSG, sys, ""));
    }

    private void Send2ap_CONTROL_FAP_RADIO_OFF_MSG(AsyncUserToken apToKen, App_Info_Struct AppInfo, Device_Sys sys)
    {
        Send2ap_GSM(apToKen, AppInfo, new MsgSendStruct(Send_Msg_Id.CONTROL_FAP_RADIO_OFF_MSG, sys, ""));
    }

    private void Send2ap_CONTROL_FAP_RESET_MSG(AsyncUserToken apToKen, App_Info_Struct AppInfo, Device_Sys sys)
    {
        Send2ap_GSM(apToKen, AppInfo, new MsgSendStruct(Send_Msg_Id.CONTROL_FAP_RESET_MSG, sys, ""));
    }

    private void Send2ap_CONFIG_CDMA_CARRIER_MSG(AsyncUserToken apToKen, App_Info_Struct AppInfo, Device_Sys sys, STRUCT_CONFIG_CDMA_CARRIER_MSG para)
    {
        String data = String.format("{0}{1}{2}{3}{4}{5}{6}{7}{8}{9}{10}{11}{12}{13}{14}{15}{16}{17}{18}{19}",
                para.wARFCN1.ToString("X").PadLeft(4, '0'),
                para.bARFCN1Mode.ToString("X").PadLeft(2, '0'),
                getReservedString(1),
                para.wARFCN1Duration.ToString("X").PadLeft(4, '0'),
                para.wARFCN1Period.ToString("X").PadLeft(4, '0'),
                para.wARFCN2.ToString("X").PadLeft(4, '0'),
                para.bARFCN2Mode.ToString("X").PadLeft(2, '0'),
                getReservedString(1),
                para.wARFCN2Duration.ToString("X").PadLeft(4, '0'),
                para.wARFCN2Period.ToString("X").PadLeft(4, '0'),
                para.wARFCN3.ToString("X").PadLeft(4, '0'),
                para.bARFCN3Mode.ToString("X").PadLeft(2, '0'),
                getReservedString(1),
                para.wARFCN3Duration.ToString("X").PadLeft(4, '0'),
                para.wARFCN3Period.ToString("X").PadLeft(4, '0'),
                para.wARFCN4.ToString("X").PadLeft(4, '0'),
                para.bARFCN4Mode.ToString("X").PadLeft(2, '0'),
                getReservedString(1),
                para.wARFCN4Duration.ToString("X").PadLeft(4, '0'),
                para.wARFCN4Period.ToString("X").PadLeft(4, '0'));
        Send2ap_GSM(apToKen, AppInfo, new MsgSendStruct(Send_Msg_Id.CONFIG_CDMA_CARRIER_MSG, sys, data));
    }

    private void Send2ap_QUERY_FAP_PARAM_MSG(AsyncUserToken apToKen, App_Info_Struct AppInfo, Device_Sys sys)
    {
        Send2ap_GSM(apToKen, AppInfo, new MsgSendStruct(Send_Msg_Id.QUERY_FAP_PARAM_MSG, sys, ""));
    }

    private void Send2ap_CONFIG_IMSI_MSG_V3(AsyncUserToken apToKen, App_Info_Struct AppInfo, Device_Sys sys, STRUCT_CONFIG_IMSI_MSG_V3 para)
    {
        if (para.wTotalImsi <= 50 || para.bActionType == 1)
        {
            String data = "";
            data = String.format("{0}{1}{2}{3}{4}{5}",
                    para.wTotalImsi.ToString("X").PadLeft(4, '0'),
                    para.wTotalImsi.ToString("X").PadLeft(2, '0'),
                    4.ToString("X").PadLeft(2, '0'), //Complete
                    0.ToString("X").PadLeft(2, '0'),
                    para.bActionType.ToString("X").PadLeft(2, '0'),
                    getReservedString(2));

            for (int i = 0; i < para.wTotalImsi; i++)
            {
                data = data + StringaddZero(para.bIMSI[i]);
            }
            for (int i = para.wTotalImsi; i < 50; i++)
            {
                data = data + getReservedString(15);
            }
            for (int i = 0; i < para.wTotalImsi; i++)
            {
                data = data + para.bUeActionFlag[i].ToString("X").PadLeft(2, '0');
            }
            for (int i = para.wTotalImsi; i < 50; i++)
            {
                data = data + getReservedString(1);
            }

            Send2ap_GSM(apToKen, AppInfo, new MsgSendStruct(Send_Msg_Id.CONFIG_IMSI_MSG_V3_ID, sys, data));
        }
        else
        {
            String data = "";
            byte bSegmentType = 0;
            byte bSegmentID = 0;
            for (int j = 0; j < para.wTotalImsi; j += 50)
            {
                Thread.Sleep(300);

                int lastId = j + 50;
                int imsiNum = 50;
                if (j == 0)
                {
                    bSegmentType = 1;
                }
                else if (lastId >= para.wTotalImsi)
                {
                    bSegmentType = 3;
                    imsiNum = para.wTotalImsi - j;
                }
                else
                {
                    bSegmentType = 2;
                }

                data = String.format("{0}{1}{2}{3}{4}{5}",
                        para.wTotalImsi.ToString("X").PadLeft(4, '0'),
                        imsiNum.ToString("X").PadLeft(2, '0'),
                        bSegmentType.ToString("X").PadLeft(2, '0'), //Complete
                        bSegmentID.ToString("X").PadLeft(2, '0'),
                        para.bActionType.ToString("X").PadLeft(2, '0'),
                        getReservedString(2));

                bSegmentID++;

                if (lastId < para.wTotalImsi)
                {
                    for (int i = j; i < lastId; i++)
                    {
                        data = data + StringaddZero(para.bIMSI[i]);
                    }
                    for (int i = j; i < lastId; i++)
                    {
                        data = data + para.bUeActionFlag[i].ToString("X").PadLeft(2, '0');
                    }
                }
                else
                {
                    for (int i = j; i < para.wTotalImsi; i++)
                    {
                        data = data + StringaddZero(para.bIMSI[i]);
                    }
                    for (int i = para.wTotalImsi; i < lastId; i++)
                    {
                        data = data + getReservedString(15);
                    }
                    for (int i = j; i < para.wTotalImsi; i++)
                    {
                        data = data + para.bUeActionFlag[i].ToString("X").PadLeft(2, '0');
                    }
                    for (int i = para.wTotalImsi; i < lastId; i++)
                    {
                        data = data + getReservedString(1);
                    }
                }

                Send2ap_GSM(apToKen, AppInfo, new MsgSendStruct(Send_Msg_Id.CONFIG_IMSI_MSG_V3_ID, sys, data));
            }
        }
    }

    //private void Send2ap_SET_PARA_REQ(AsyncUserToken apToKen, App_Info_Struct AppInfo, Gsm_Device_Sys sys, int Flag,
    //    RecvSysPara para, RecvSysOption option, RecvRfOption rf, byte mode)
    //{
    //    int msgId = addMsgId();

    //    Msg_Body_Struct TypeKeyValue = new Msg_Body_Struct(ApMsgType.set_general_para_request);

    //    if ((Flag & 0x01) > 0)
    //    {
    //        String paraMnc = "";
    //        if (para.paraMnc < 0xFF)
    //        {
    //            paraMnc = String.format("0F{0}", para.paraMnc.ToString("X").PadLeft(2, '0'));
    //        }
    //        else
    //        {
    //            paraMnc = String.format("{0}", para.paraMnc.ToString("X").PadLeft(4, '0'));
    //        }

    //        String SysParaData = String.format("{0}{1}{2}{3}{4}{5}{6}{7}{8}{9}",
    //            para.paraMcc.ToString("X").PadLeft(4, '0'),
    //            paraMnc,
    //            para.paraBsic.ToString("X").PadLeft(2, '0'),
    //            para.paraLac.ToString("X").PadLeft(4, '0'),
    //            para.paraCellId.ToString("X").PadLeft(4, '0'),
    //            para.paraC2.ToString("X").PadLeft(2, '0'),
    //            para.paraPeri.ToString("X").PadLeft(2, '0'),
    //            para.paraAccPwr.ToString("X").PadLeft(2, '0'),
    //            para.paraMsPwr.ToString("X").PadLeft(2, '0'),
    //            para.paraRejCau.ToString("X").PadLeft(2, '0'));

    //        gsm_msg_send SysParaSendMsg = new gsm_msg_send(Send_Msg_Id.RECV_SYS_PARA, sys, SysParaData);

    //        String SysPara = String.format("{0}{1}{2}{3}{4}{5}{6}",
    //            SysParaSendMsg.head, SysParaSendMsg.addr, ((byte)SysParaSendMsg.sys).ToString("X").PadLeft(2, '0'),
    //            ((byte)SysParaSendMsg.type).ToString("X").PadLeft(2, '0'), SysParaSendMsg.data_length.ToString("X").PadLeft(2, '0'),
    //            SysParaSendMsg.message_id.ToString("X").PadLeft(4, '0'), SysParaSendMsg.data);

    //        //在两个字符间加上空格
    //        SysPara = Regex.replace(SysPara, @".{2}", "$0 ");

    //        TypeKeyValue.dic.put(String.format("sys_para_{0}", sys), SysPara.Trim());
    //    }

    //    if ((Flag & 0x02) > 0)
    //    {
    //        String optionData = String.format("{0}{1}{2}{3}{4}{5}",
    //        option.opLuSms.ToString("X").PadLeft(2, '0'),
    //        option.opLuImei.ToString("X").PadLeft(2, '0'),
    //        option.opCallEn.ToString("X").PadLeft(2, '0'),
    //        option.opDebug.ToString("X").PadLeft(2, '0'),
    //        option.opLuType.ToString("X").PadLeft(2, '0'),
    //        option.opSmsType.ToString("X").PadLeft(2, '0'));

    //        gsm_msg_send OptionSendMsg = new gsm_msg_send(Send_Msg_Id.RECV_SYS_OPTION, sys, optionData);

    //        String SysOption = String.format("{0}{1}{2}{3}{4}{5}{6}",
    //            OptionSendMsg.head, OptionSendMsg.addr, ((byte)OptionSendMsg.sys).ToString("X").PadLeft(2, '0'),
    //            ((byte)OptionSendMsg.type).ToString("X").PadLeft(2, '0'), OptionSendMsg.data_length.ToString("X").PadLeft(2, '0'),
    //            OptionSendMsg.message_id.ToString("X").PadLeft(4, '0'), OptionSendMsg.data);

    //        //在两个字符间加上空格
    //        SysOption = Regex.replace(SysOption, @".{2}", "$0 ");

    //        TypeKeyValue.dic.put(String.format("sys_option_{0}", sys), SysOption.Trim());
    //    }

    //    if ((Flag & 0x04) > 0)
    //    {
    //        String rfData = String.format("{0}{1}{2}",
    //       rf.rfEnable.ToString("X").PadLeft(2, '0'),
    //       rf.rfFreq.ToString("X").PadLeft(4, '0'),
    //       rf.rfPwr.ToString("X").PadLeft(2, '0'));

    //        gsm_msg_send RfSendMsg = new gsm_msg_send(Send_Msg_Id.RECV_RF_PARA, sys, rfData);

    //        String SysRf = String.format("{0}{1}{2}{3}{4}{5}{6}",
    //            RfSendMsg.head, RfSendMsg.addr, ((byte)RfSendMsg.sys).ToString("X").PadLeft(2, '0'),
    //            ((byte)RfSendMsg.type).ToString("X").PadLeft(2, '0'), RfSendMsg.data_length.ToString("X").PadLeft(2, '0'),
    //            RfSendMsg.message_id.ToString("X").PadLeft(4, '0'), RfSendMsg.data);

    //        //在两个字符间加上空格
    //        SysRf = Regex.replace(SysRf, @".{2}", "$0 ");

    //        TypeKeyValue.dic.put(String.format("sys_rf_{0}", sys), SysRf.Trim());
    //    }

    //    if ((Flag & 0x08) > 0)
    //    {
    //        String ModeData = String.format("{0}",
    //        mode.ToString("X").PadLeft(2, '0'));

    //        gsm_msg_send ModeSendMsg = new gsm_msg_send(Send_Msg_Id.RECV_REG_MODE, sys, ModeData);

    //        String SysMode = String.format("{0}{1}{2}{3}{4}{5}{6}",
    //            ModeSendMsg.head, ModeSendMsg.addr, ((byte)ModeSendMsg.sys).ToString("X").PadLeft(2, '0'),
    //            ((byte)ModeSendMsg.type).ToString("X").PadLeft(2, '0'), ModeSendMsg.data_length.ToString("X").PadLeft(2, '0'),
    //            ModeSendMsg.message_id.ToString("X").PadLeft(4, '0'), ModeSendMsg.data);

    //        //在两个字符间加上空格
    //        SysMode = Regex.replace(SysMode, @".{2}", "$0 ");

    //        TypeKeyValue.dic.put(String.format("sys_workMode_{0}", sys), SysMode.Trim());
    //    }


    //    MsgId2App msgId2App = new MsgId2App();
    //    msgId2App.id = msgId;
    //    msgId2App.AppInfo = AppInfo;

    //    if (!MyDeviceList.addMsgId2App(apToKen, msgId2App))
    //    {
    //         Log.e(TAG, String.format("添加消息Id到设备列表出错！"));
    //        Send2APP_GeneralError(apToKen, AppInfo, ApMsgType.set_general_para_request.ToString(),
    //            String.format("添加消息Id到设备列表出错！"));
    //        return;
    //    }

    //    byte[] bMsg = EncodeApXmlMessage(msgId, TypeKeyValue);
    //    if (bMsg == null)
    //    {
    //         Log.e(TAG, String.format("封装XML消息(Send2ap_SET_PARA_REQ)出错！"));
    //        return;
    //    }
    //    SendMsg2Ap(apToKen, bMsg);
    //}

        #endregion

        #endregion

        #region MainController侧消息处理

    /// <summary>
    /// 重载收到Main模块消息处理
    /// </summary>
    /// <param name="msg">消息内容</param>
    public void OnReceiveMainMsg(MsgStruct.InterModuleMsgStruct MainMsg)
    {
        if (MainMsg == null || MainMsg.Body == null)
        {
             Log.e(TAG, "收到MainController模块消息内容为空!", LogCategory.R);
            return;
        }

        if (MainMsg.ApInfo.IP == null || MainMsg.ApInfo.Type == null)
        {
             Log.e(TAG, "收到MainController模块消息ApInfo内容错误!", LogCategory.R);
            return;
        }

        if ((!MainMsg.ApInfo.IP.Equals(MsgStruct.AllDevice)) && (!MainMsg.ApInfo.Type.Equals(MODE_NAME)))
        {
            //Log.d(TAG, "收到MainController模块消息，不是本模块消息！");
            return;
        }

        //处理透传消息
        if (MainMsg.MsgType == MsgType.TRANSPARENT.ToString())
        {
            Send2ap_TransparentMsg(MainMsg);
            return;
        }

        HandleMainMsg(MainMsg);
        return;
    }

    /// <summary>
    /// 处理收到的Main模块的消息
    /// </summary>
    /// <param name="MainMsg">消息内容</param>
    private void HandleMainMsg(MsgStruct.InterModuleMsgStruct MainMsg)
    {
        Log.i(TAG, String.format("处理MainController消息。消息类型:{0}。", MainMsg.Body.type));

        //上、下线消息回复
        if (MainMsg.Body.type == Msg_Body_Struct.OnOffLine_Ack)
        {
            //所有在线AP数与数据库不一至，回复所有在线AP
            if (GetMsgIntValueInList("ReturnCode", MainMsg.Body) != 0)
            {
                 Log.e(TAG,
                        "[OnOffLine_Ack]Main模块返回错误:" + GetMsgStringValueInList("ReturnStr", MainMsg.Body));
                //暂时不发送，等待后续定义
                //Send2main_OnLineList();
            }
            else
            {
                String status = GetMsgStringValueInList("Status", MainMsg.Body);
                if (status.Equals(OnLine) || status.Equals(OffLine))
                {
                    //修改状态
                    MyDeviceList.SetMainControllerStatus(status, MainMsg.ApInfo.IP, MainMsg.ApInfo.Port);
                }
                else
                {
                     Log.e(TAG, "Main模块返回消息中，Status字段错误!");
                }
            }
        }
        else if (MainMsg.Body.type == Msg_Body_Struct.ApDelete)
        {
            //修改状态为下线状态
            MyDeviceList.SetMainControllerStatus(OffLine, MainMsg.ApInfo.IP, MainMsg.ApInfo.Port);
        }
        else if (MainMsg.Body.type == Msg_Body_Struct.ApSetRadio)
        {
            AsyncUserToken apToKen = MyDeviceList.FindByApInfo(MainMsg.ApInfo);
            if (apToKen == null)
            {
                Log.w(TAG, String.format("在线AP列表中找不到Ap[{0}:{1}]设备({2})!",
                        MainMsg.ApInfo.IP, MainMsg.ApInfo.Port.ToString(), MainMsg.ApInfo.Fullname));
                return;
            }

            byte carry = GetMsgByteValueInList("carry", MainMsg.Body.dic, Byte.MaxValue);
            if (carry != 0 && carry != 1)
            {
                 Log.e(TAG, String.format("Main模块发送消息[{0}]中，carry字段非法!",
                        Msg_Body_Struct.ApSetRadio));
                return;
            }

            Byte RADIO = GetMsgByteValueInList("RADIO", MainMsg.Body.dic, Byte.MaxValue);
            if (RADIO == 1)
            {
                Send2ap_CONTROL_FAP_RADIO_ON_MSG(apToKen, MainMsg.AppInfo, (Device_Sys)carry);
            }
            else if (RADIO == 0)
            {
                Send2ap_CONTROL_FAP_RADIO_OFF_MSG(apToKen, MainMsg.AppInfo, (Device_Sys)carry);
            }
            else
            {
                 Log.e(TAG, String.format("Main模块发送消息[{0}]中，RADIO字段非法!",
                        Msg_Body_Struct.ApSetRadio));
                return;
            }

        }
        //状态改变回复
        else if (MainMsg.Body.type == Msg_Body_Struct.ApStatusChange_Ack)
        {
            RecvAckSaveApStatus(MainMsg);
        }
        else if (MainMsg.Body.type == Msg_Body_Struct.OnOffLineCheck)
        {
            String status = GetMsgStringValueInList("Status", MainMsg.Body);
            Send2main_OnOffLineCheck(status, MainMsg.ApInfo);
        }
        else if (MainMsg.Body.type == Msg_Body_Struct.ReportGenParaAck)
        {
            if (GetMsgIntValueInList("ReturnCode", MainMsg.Body) != 0)
            {
                 Log.e(TAG,
                        "[ReportGenParaAck]Main模块返回错误:" + GetMsgStringValueInList("ReturnStr", MainMsg.Body));
            }
            return;
        }
        else if (MainMsg.Body.type == Msg_Body_Struct.gsm_msg_send)
        {
            //String Protocol = GetMsgStringValueInList("Protocol", MainMsg.Body.dic, "");
            //if (!Protocol.Equals(Protocol_Sap.CDMA.ToString()))
            //{
            //     Log.e(TAG, "发送给CDMA设备消息错误。消息中协议类型不为" + Protocol_Sap.CDMA + ".");
            //    Send2APP_GeneralError(MainMsg.ApInfo, MainMsg.AppInfo, MainMsg.Body.type,
            //       String.format("发送给CDMA设备消息错误。消息中系统号不为" + Protocol_Sap.CDMA + "."));
            //    return;
            //}

            byte sys = GetMsgByteValueInList("sys", MainMsg.Body.dic, Byte.MaxValue);
            if (sys != (byte)Device_Sys.Sys1 && sys != (byte)Device_Sys.Sys2)
            {
                 Log.e(TAG, "发送给GSM_V2设备消息错误。消息中系统号不为0或1。");
                Send2APP_GeneralError(MainMsg.ApInfo, MainMsg.AppInfo, MainMsg.Body.type,
                        String.format("发送给GSM_V2设备消息错误。消息中系统号不为0或1。"));
                return;
            }
            if (MainMsg.Body.n_dic == null)
            {
                 Log.e(TAG, "发送给GSM_V2设备消息错误。消息中没有可设置的参数(n_dic项为NULL)。");
                Send2APP_GeneralError(MainMsg.ApInfo, MainMsg.AppInfo, MainMsg.Body.type,
                        "发送给GSM_V2设备消息错误。消息中没有可设置的参数(n_dic项为NULL)。");
                return;
            }

            foreach (Name_DIC_Struct x in MainMsg.Body.n_dic)
            {
                EncodeMainMsg(MainMsg.ApInfo, MainMsg.AppInfo, (Device_Sys)sys, x);
            }
            return;
        }
        else if (MainMsg.Body.type == Msg_Body_Struct.SetGenParaReq)  //数据对齐部分
        {
            byte Protocol = GetMsgByteValueInList("Protocol", MainMsg.Body.dic, Byte.MaxValue);
            if (Protocol != (byte)Protocol_Sap.GSM)
            {
                 Log.e(TAG, "发送给GSM_V2设备消息错误。消息中协议类型不为" + Protocol_Sap.GSM + ".");
                Send2APP_GeneralError(MainMsg.ApInfo, MainMsg.AppInfo, MainMsg.Body.type,
                        String.format("发送给GSM_V2设备消息错误。消息中系统号不为" + Protocol_Sap.GSM + "."));
                return;
            }
            byte sys = GetMsgByteValueInList("sys", MainMsg.Body.dic, Byte.MaxValue);
            if (sys != (byte)Device_Sys.Sys1 && sys!=(byte)Device_Sys.Sys2)
            {
                 Log.e(TAG, "发送给GSM_V2设备消息错误。消息中系统号不为0或1。");
                Send2APP_GeneralError(MainMsg.ApInfo, MainMsg.AppInfo, MainMsg.Body.type,
                        String.format("发送给GSM_V2设备消息错误。消息中系统号不为0或1。"));
                return;
            }
            if (MainMsg.Body.n_dic == null)
            {
                 Log.e(TAG, "发送给GSM_V2设备消息错误。消息中没有可设置的参数(n_dic项为NULL)。");
                Send2APP_GeneralError(MainMsg.ApInfo, MainMsg.AppInfo, MainMsg.Body.type,
                        "发送给GSM_V2设备消息错误。消息中没有可设置的参数(n_dic项为NULL)。");
                return;
            }

            //EncodeSetParaMsg(MainMsg.ApInfo, MainMsg.AppInfo, (Device_Sys)sys, MainMsg.Body.n_dic);

            return;
        }
        else if (MainMsg.Body.type == Msg_Body_Struct.set_parameter_request)
        {
            if ((String.IsNullOrEmpty(MainMsg.ApInfo.IP)) || (MainMsg.ApInfo.IP == MsgStruct.NullDevice))
            {
                Log.i(TAG, String.format("目的设备为Null，不向Ap发送信息！"));
                Send2APP_GeneralError(MainMsg.ApInfo, MainMsg.AppInfo, MainMsg.Body.type,
                        String.format("目的设备为Null，不向Ap发送信息！"));
            }
            else
            {
                Send2ap_RecvMainMsg(MainMsg);
            }
        }
        else //其它消息
        {
            String str = String.format("发送给GSM_V2设备({0}:{1})消息类型{2}错误！",
                    MainMsg.ApInfo.IP, MainMsg.ApInfo.Port.ToString(), MainMsg.Body.type.ToString());
            Log.w(TAG, str);
            Send2APP_GeneralError(MainMsg.ApInfo, MainMsg.AppInfo, Msg_Body_Struct.gsm_msg_send, str);
        }
        return;
    }

    /// <summary>
    /// 解析Main模块发过来的消息
    /// </summary>
    /// <param name="msgBody">消息内容</param>
    /// <param name="sys"></param>
    /// <param name="n_dic"></param>
    private void EncodeMainMsg(DeviceDataStruct ApInfo, App_Info_Struct AppInfo, Device_Sys sys, Name_DIC_Struct n_dic)
    {
        AsyncUserToken apToKen = MyDeviceList.FindByApInfo(ApInfo);
        if (apToKen == null)
        {
            Log.w(TAG, String.format("在线AP列表中找不到Ap[{0}:{1}]设备({2})!",
                    ApInfo.IP, ApInfo.Port.ToString(), ApInfo.Fullname));
            return;
        }

        if (n_dic.name.Equals(Send_Msg_Id.QUERY_NB_CELL_INFO_MSG.ToString()))
        {
            Send2ap_QUERY_NB_CELL_INFO_MSG(apToKen, AppInfo, sys);
        }
        else if (n_dic.name.Equals(Send_Msg_Id.CONFIG_FAP_MSG.ToString()))
        {
            STRUCT_CONFIG_FAP_MSG para = new STRUCT_CONFIG_FAP_MSG();
            para.bWorkingMode = GetMsgByteValueInList("bWorkingMode", n_dic.dic, Byte.MaxValue);
            if (para.bWorkingMode != 1 && para.bWorkingMode != 3)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_FAP_MSG.ToString(), "bWorkingMode");
                return;
            }

            para.bC = GetMsgByteValueInList("bC", n_dic.dic, Byte.MaxValue);
            if (para.bC == Byte.MaxValue)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_FAP_MSG.ToString(), "bC");
                return;
            }

            para.wRedirectCellUarfcn = GetMsgU16ValueInList("wRedirectCellUarfcn", n_dic.dic, int.MaxValue);
            if (para.wRedirectCellUarfcn == int.MaxValue)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_FAP_MSG.ToString(), "wRedirectCellUarfcn");
                return;
            }

            para.dwDateTime = (Long)DateTime.Now.Subtract(DateTime.Parse("1970-1-1")).TotalSeconds;
            //para.dwDateTime = GetMsgU32ValueInList("dwDateTime", n_dic.dic, Long.MaxValue);
            //if (para.dwDateTime == Long.MaxValue)
            //{
            //    SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_FAP_MSG.ToString(), "dwDateTime");
            //    return;
            //}

            para.bPLMNId = GetMsgStringValueInList("bPLMNId", n_dic.dic, "");
            if (para.bPLMNId == "")
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_FAP_MSG.ToString(), "bPLMNId");
                return;
            }

            para.bTxPower = GetMsgByteValueInList("bTxPower", n_dic.dic, Byte.MaxValue);
            if (para.bTxPower == Byte.MaxValue)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_FAP_MSG.ToString(), "bTxPower");
                return;
            }

            para.bRxGain = GetMsgByteValueInList("bRxGain", n_dic.dic, Byte.MaxValue);
            if (para.bRxGain == Byte.MaxValue)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_FAP_MSG.ToString(), "bRxGain");
                return;
            }

            para.wPhyCellId = GetMsgU16ValueInList("wPhyCellId", n_dic.dic, int.MaxValue);
            if (para.wPhyCellId == int.MaxValue)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_FAP_MSG.ToString(), "wPhyCellId");
                return;
            }

            para.wLAC = GetMsgU16ValueInList("wLAC", n_dic.dic, int.MaxValue);
            if (para.wLAC == int.MaxValue)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_FAP_MSG.ToString(), "wLAC");
                return;
            }

            para.wUARFCN = GetMsgU16ValueInList("wUARFCN", n_dic.dic, int.MaxValue);
            if (para.wUARFCN == int.MaxValue)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_FAP_MSG.ToString(), "wUARFCN");
                return;
            }

            para.dwCellId = GetMsgU32ValueInList("dwCellId", n_dic.dic, Long.MaxValue);
            if (para.dwCellId == Long.MaxValue)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_FAP_MSG.ToString(), "dwCellId");
                return;
            }

            Send2ap_CONFIG_FAP_MSG(apToKen, AppInfo, sys, para);
        }
        else if (n_dic.name.Equals(Send_Msg_Id.CONTROL_FAP_REBOOT_MSG.ToString()))
        {
            byte bRebootFlag = GetMsgByteValueInList("bRebootFlag", n_dic.dic, Byte.MaxValue);
            if (bRebootFlag != 1 && bRebootFlag != 3)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_FAP_MSG.ToString(), "bRebootFlag");
                return;
            }

            Send2ap_CONTROL_FAP_REBOOT_MSG(apToKen, AppInfo, sys, bRebootFlag);
        }
        else if (n_dic.name.Equals(Send_Msg_Id.CONFIG_SMS_CONTENT_MSG.ToString()))
        {
            String bSMSOriginalNum = GetMsgStringValueInList("bSMSOriginalNum", n_dic.dic, "");
            String bSMSContent = GetMsgStringValueInList("bSMSContent", n_dic.dic, "");
            if (String.IsNullOrEmpty(bSMSOriginalNum) || bSMSOriginalNum.length() <= 0)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_SMS_CONTENT_MSG.ToString(), "bSMSOriginalNum");
                return;
            }
            if (String.IsNullOrEmpty(bSMSContent))
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_SMS_CONTENT_MSG.ToString(), "bSMSContent");
                return;
            }
            Send2ap_CONFIG_SMS_CONTENT_MSG(apToKen, AppInfo, sys, bSMSOriginalNum, bSMSContent);
        }
        else if (n_dic.name.Equals(Send_Msg_Id.CONTROL_FAP_RADIO_ON_MSG.ToString()))
        {
            Send2ap_CONTROL_FAP_RADIO_ON_MSG(apToKen, AppInfo, sys);
        }
        else if (n_dic.name.Equals(Send_Msg_Id.CONTROL_FAP_RADIO_OFF_MSG.ToString()))
        {
            Send2ap_CONTROL_FAP_RADIO_OFF_MSG(apToKen, AppInfo, sys);
        }
        else if (n_dic.name.Equals(Send_Msg_Id.CONTROL_FAP_RESET_MSG.ToString()))
        {
            Send2ap_CONTROL_FAP_RESET_MSG(apToKen, AppInfo, sys);
        }
        else if (n_dic.name.Equals(Send_Msg_Id.CONFIG_CDMA_CARRIER_MSG.ToString()))
        {
            STRUCT_CONFIG_CDMA_CARRIER_MSG para = new STRUCT_CONFIG_CDMA_CARRIER_MSG();
            para.wARFCN1 = GetMsgU16ValueInList("wARFCN1", n_dic.dic, int.MaxValue);
            if (para.wARFCN1 == int.MaxValue)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_CDMA_CARRIER_MSG.ToString(), "wARFCN1");
                return;
            }

            para.bARFCN1Mode = GetMsgByteValueInList("bARFCN1Mode", n_dic.dic, Byte.MaxValue);
            if (para.bARFCN1Mode != 0 && para.bARFCN1Mode != 1 && para.bARFCN1Mode != 2)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_CDMA_CARRIER_MSG.ToString(), "bARFCN1Mode");
                return;
            }

            para.wARFCN1Duration = GetMsgU16ValueInList("wARFCN1Duration", n_dic.dic, int.MaxValue);
            if (para.wARFCN1Duration == int.MaxValue)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_CDMA_CARRIER_MSG.ToString(), "wARFCN1Duration");
                return;
            }

            para.wARFCN1Period = GetMsgU16ValueInList("wARFCN1Period", n_dic.dic, int.MaxValue);
            if (para.wARFCN1Period == int.MaxValue)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_CDMA_CARRIER_MSG.ToString(), "wARFCN1Period");
                return;
            }

            para.wARFCN2 = GetMsgU16ValueInList("wARFCN2", n_dic.dic, int.MaxValue);
            if (para.wARFCN2 == int.MaxValue)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_CDMA_CARRIER_MSG.ToString(), "wARFCN2");
                return;
            }

            para.bARFCN2Mode = GetMsgByteValueInList("bARFCN2Mode", n_dic.dic, Byte.MaxValue);
            if (para.bARFCN2Mode != 0 && para.bARFCN2Mode != 1 && para.bARFCN2Mode != 2)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_CDMA_CARRIER_MSG.ToString(), "bARFCN2Mode");
                return;
            }

            para.wARFCN2Duration = GetMsgU16ValueInList("wARFCN2Duration", n_dic.dic, int.MaxValue);
            if (para.wARFCN2Duration == int.MaxValue)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_CDMA_CARRIER_MSG.ToString(), "wARFCN2Duration");
                return;
            }

            para.wARFCN2Period = GetMsgU16ValueInList("wARFCN2Period", n_dic.dic, int.MaxValue);
            if (para.wARFCN2Period == int.MaxValue)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_CDMA_CARRIER_MSG.ToString(), "wARFCN2Period");
                return;
            }

            para.wARFCN3 = GetMsgU16ValueInList("wARFCN3", n_dic.dic, int.MaxValue);
            if (para.wARFCN3 == int.MaxValue)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_CDMA_CARRIER_MSG.ToString(), "wARFCN3");
                return;
            }

            para.bARFCN3Mode = GetMsgByteValueInList("bARFCN3Mode", n_dic.dic, Byte.MaxValue);
            if (para.bARFCN3Mode != 0 && para.bARFCN3Mode != 1 && para.bARFCN3Mode != 2)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_CDMA_CARRIER_MSG.ToString(), "bARFCN3Mode");
                return;
            }

            para.wARFCN3Duration = GetMsgU16ValueInList("wARFCN3Duration", n_dic.dic, int.MaxValue);
            if (para.wARFCN3Duration == int.MaxValue)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_CDMA_CARRIER_MSG.ToString(), "wARFCN3Duration");
                return;
            }

            para.wARFCN3Period = GetMsgU16ValueInList("wARFCN3Period", n_dic.dic, int.MaxValue);
            if (para.wARFCN3Period == int.MaxValue)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_CDMA_CARRIER_MSG.ToString(), "wARFCN3Period");
                return;
            }

            para.wARFCN4 = GetMsgU16ValueInList("wARFCN4", n_dic.dic, int.MaxValue);
            if (para.wARFCN4 == int.MaxValue)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_CDMA_CARRIER_MSG.ToString(), "wARFCN4");
                return;
            }

            para.bARFCN4Mode = GetMsgByteValueInList("bARFCN4Mode", n_dic.dic, Byte.MaxValue);
            if (para.bARFCN4Mode != 0 && para.bARFCN4Mode != 1 && para.bARFCN4Mode != 2)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_CDMA_CARRIER_MSG.ToString(), "bARFCN4Mode");
                return;
            }

            para.wARFCN4Duration = GetMsgU16ValueInList("wARFCN4Duration", n_dic.dic, int.MaxValue);
            if (para.wARFCN4Duration == int.MaxValue)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_CDMA_CARRIER_MSG.ToString(), "wARFCN4Duration");
                return;
            }

            para.wARFCN4Period = GetMsgU16ValueInList("wARFCN4Period", n_dic.dic, int.MaxValue);
            if (para.wARFCN4Period == int.MaxValue)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_CDMA_CARRIER_MSG.ToString(), "wARFCN4Period");
                return;
            }

            Send2ap_CONFIG_CDMA_CARRIER_MSG(apToKen, AppInfo, sys, para);
        }
        else if (n_dic.name.Equals(Send_Msg_Id.QUERY_FAP_PARAM_MSG.ToString()))
        {
            Send2ap_QUERY_FAP_PARAM_MSG(apToKen, AppInfo, sys);
        }
        else if (n_dic.name.Equals(Send_Msg_Id.CONFIG_IMSI_MSG_V3_ID.ToString()))
        {
            STRUCT_CONFIG_IMSI_MSG_V3 para = new STRUCT_CONFIG_IMSI_MSG_V3(1000);
            para.wTotalImsi = GetMsgU16ValueInList("wTotalImsi", n_dic.dic, int.MaxValue);
            if (para.wTotalImsi > 1000)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_IMSI_MSG_V3_ID.ToString(), "wTotalImsi");
                return;
            }

            //para.bIMSINum = GetMsgByteValueInList("bIMSINum", n_dic.dic, Byte.MaxValue);
            //if (para.bIMSINum <= 0 || para.bIMSINum > 50)
            //{
            //    SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_IMSI_MSG_V3.ToString(), "bIMSINum");
            //    return;
            //}

            //para.bSegmentType = GetMsgByteValueInList("bSegmentType", n_dic.dic, Byte.MaxValue);
            //if (para.bSegmentType != 1 && para.bSegmentType != 2 && para.bSegmentType != 3 && para.bSegmentType != 4)
            //{
            //    SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_IMSI_MSG_V3.ToString(), "bSegmentType");
            //    return;
            //}

            //para.bSegmentID = GetMsgByteValueInList("bSegmentID", n_dic.dic, Byte.MaxValue);
            //if (para.bSegmentID == Byte.MaxValue)
            //{
            //    SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_IMSI_MSG_V3.ToString(), "bSegmentID");
            //    return;
            //}

            para.bActionType = GetMsgByteValueInList("bActionType", n_dic.dic, Byte.MaxValue);
            if (para.bActionType != 1 && para.bActionType != 2 && para.bActionType != 3 && para.bActionType != 4)
            {
                SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_IMSI_MSG_V3_ID.ToString(), "bActionType");
                return;
            }

            for (int i = 0; i < para.wTotalImsi; i++)
            {
                para.bIMSI[i] = GetMsgStringValueInList(String.format("bIMSI_#{0}#", i), n_dic.dic, "");
                if (String.IsNullOrEmpty(para.bIMSI[i]))
                {
                    SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_IMSI_MSG_V3_ID.ToString(), String.format("bIMSI_#{0}#", i));
                    return;
                }
            }

            for (int i = 0; i < para.wTotalImsi; i++)
            {
                para.bUeActionFlag[i] = GetMsgByteValueInList(String.format("bUeActionFlag_#{0}#", i), n_dic.dic, Byte.MaxValue);
                if (para.bUeActionFlag[i] != 1 && para.bUeActionFlag[i] != 5)
                {
                    SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.CONFIG_IMSI_MSG_V3_ID.ToString(), String.format("bUeActionFlag#{0}#", i));
                    return;
                }
            }
            Send2ap_CONFIG_IMSI_MSG_V3(apToKen, AppInfo, sys, para);
        }
        else
        {
            String str = String.format("发送给GSM_V2设备消息({0})错误。暂不支持该消息类型)。", n_dic.name);
             Log.e(TAG, str);
            Send2APP_GeneralError(ApInfo, AppInfo, n_dic.name, str);
        }
    }

    //    private void EncodeSetParaMsg(DeviceDataStruct ApInfo, App_Info_Struct AppInfo, Device_Sys sys, List<Name_DIC_Struct> n_dic_List)
    //    {
    //        int Flag = 0;
    //        RecvSysPara para = new RecvSysPara();
    //        RecvSysOption option = new RecvSysOption();
    //        RecvRfOption rf = new RecvRfOption();
    //        byte regMode = 0;

    //        AsyncUserToken apToKen = MyDeviceList.FindByIpPort(ApInfo.IP, ApInfo.Port);
    //        if (apToKen == null)
    //        {
    //            String str = String.format("在线AP列表中找不到Ap[{0}:{1}]设备，通过FullName重新查询设备！",
    //                ApInfo.IP, ApInfo.Port.ToString());
    //            Log.w(TAG, str);
    //            apToKen = MyDeviceList.FindByFullname(ApInfo.Fullname);
    //        }

    //        if (apToKen == null)
    //        {
    //            String str = String.format("在线AP列表中找不到Ap[{0}:{1}],FullName:{2}。无法向AP发送消息！",
    //                ApInfo.IP, ApInfo.Port.ToString(), ApInfo.Fullname);
    //            Log.w(TAG, str);
    //            Send2APP_GeneralError(ApInfo, AppInfo, Msg_Body_Struct.gsm_msg_send, str);
    //            return;
    //        }

    //        foreach (Name_DIC_Struct n_dic in n_dic_List)
    //        {
    //        //    if (n_dic.name.Equals(Send_Msg_Id.RECV_SYS_PARA.ToString()))
    //        //    {
    //        //        para.paraMnc = GetMsgU16ValueInList("paraMnc", n_dic.dic, int.MaxValue);
    //        //        if (para.paraMnc == int.MaxValue)
    //        //        {
    //        //            SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.RECV_SYS_PARA.ToString(), "paraMnc");
    //        //            return;
    //        //        }
    //        //        para.paraMcc = GetMsgU16ValueInList("paraMcc", n_dic.dic, int.MaxValue);
    //        //        if (para.paraMcc == int.MaxValue)
    //        //        {
    //        //            SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.RECV_SYS_PARA.ToString(), "paraMcc");
    //        //            return;
    //        //        }
    //        //        para.paraBsic = GetMsgByteValueInList("paraBsic", n_dic.dic);
    //        //        if (para.paraBsic == 0)
    //        //        {
    //        //            SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.RECV_SYS_PARA.ToString(), "paraBsic");
    //        //            return;
    //        //        }
    //        //        para.paraLac = GetMsgU16ValueInList("paraLac", n_dic.dic);
    //        //        if (para.paraLac == 0)
    //        //        {
    //        //            SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.RECV_SYS_PARA.ToString(), "paraLac");
    //        //            return;
    //        //        }
    //        //        para.paraCellId = GetMsgU16ValueInList("paraCellId", n_dic.dic);
    //        //        //if (para.paraCellId == 0)
    //        //        //{
    //        //        //    SendMainMsgParaVlaueError(ApInfo,AppInfo, Send_Msg_Id.RECV_SYS_PARA.ToString(), "paraCellId");
    //        //        //    return;
    //        //        //}
    //        //        para.paraC2 = GetMsgSByteValueInList("paraC2", n_dic.dic, SByte.MaxValue);
    //        //        if (para.paraC2 == SByte.MaxValue)
    //        //        {
    //        //            SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.RECV_SYS_PARA.ToString(), "paraC2");
    //        //            return;
    //        //        }
    //        //        para.paraPeri = GetMsgByteValueInList("paraPeri", n_dic.dic, Byte.MaxValue);
    //        //        //if (para.paraPeri == 0)
    //        //        //{
    //        //        //    SendMainMsgParaVlaueError(ApInfo,AppInfo, Send_Msg_Id.RECV_SYS_PARA.ToString(), "paraPeri");
    //        //        //    return;
    //        //        //}
    //        //        para.paraAccPwr = GetMsgByteValueInList("paraAccPwr", n_dic.dic, Byte.MaxValue);
    //        //        if (para.paraAccPwr == Byte.MaxValue)
    //        //        {
    //        //            SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.RECV_SYS_PARA.ToString(), "paraAccPwr");
    //        //            return;
    //        //        }
    //        //        para.paraMsPwr = GetMsgByteValueInList("paraMsPwr", n_dic.dic, Byte.MaxValue);
    //        //        if (para.paraMsPwr == Byte.MaxValue)
    //        //        {
    //        //            SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.RECV_SYS_PARA.ToString(), "paraMsPwr");
    //        //            return;
    //        //        }
    //        //        para.paraRejCau = GetMsgByteValueInList("paraRejCau", n_dic.dic, Byte.MaxValue);
    //        //        if (para.paraRejCau == Byte.MaxValue)
    //        //        {
    //        //            SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.RECV_SYS_PARA.ToString(), "paraRejCau");
    //        //            return;
    //        //        }
    //        //        Flag |= 0x1;
    //        //    }
    //        //    else if (n_dic.name.Equals(Send_Msg_Id.RECV_SYS_OPTION.ToString()))
    //        //    {
    //        //        option.opLuSms = GetMsgByteValueInList("opLuSms", n_dic.dic, Byte.MaxValue);
    //        //        if (option.opLuSms != 0 && option.opLuSms != 1)
    //        //        {
    //        //            SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.RECV_SYS_OPTION.ToString(), "opLuSms");
    //        //            return;
    //        //        }
    //        //        option.opLuImei = GetMsgByteValueInList("opLuImei", n_dic.dic, Byte.MaxValue);
    //        //        if (option.opLuImei != 0 && option.opLuImei != 1)
    //        //        {
    //        //            SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.RECV_SYS_OPTION.ToString(), "opLuImei");
    //        //            return;
    //        //        }
    //        //        option.opCallEn = GetMsgByteValueInList("opCallEn", n_dic.dic, Byte.MaxValue);
    //        //        if (option.opCallEn != 0 && option.opCallEn != 1)
    //        //        {
    //        //            SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.RECV_SYS_OPTION.ToString(), "opCallEn");
    //        //            return;
    //        //        }
    //        //        option.opDebug = GetMsgByteValueInList("opDebug", n_dic.dic, Byte.MaxValue);
    //        //        if (option.opDebug != 0 && option.opDebug != 1)
    //        //        {
    //        //            SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.RECV_SYS_OPTION.ToString(), "opDebug");
    //        //            return;
    //        //        }
    //        //        option.opLuType = GetMsgByteValueInList("opLuType", n_dic.dic, Byte.MaxValue);
    //        //        if (option.opLuType != 1 && option.opLuType != 2 && option.opLuType != 3)
    //        //        {
    //        //            SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.RECV_SYS_OPTION.ToString(), "opLuType");
    //        //            return;
    //        //        }
    //        //        option.opSmsType = GetMsgByteValueInList("opSmsType", n_dic.dic, Byte.MaxValue);
    //        //        if (option.opSmsType != 1 && option.opSmsType != 2 && option.opSmsType != 3)
    //        //        {
    //        //            SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.RECV_SYS_OPTION.ToString(), "opSmsType");
    //        //            return;
    //        //        }
    //        //        Flag |= 0x02;
    //        //    }
    //        //    else if (n_dic.name.Equals(Send_Msg_Id.RECV_RF_PARA.ToString()))
    //        //    {
    //        //        rf.rfEnable = GetMsgByteValueInList("rfEnable", n_dic.dic, Byte.MaxValue);
    //        //        if (rf.rfEnable != 0 && rf.rfEnable != 1)
    //        //        {
    //        //            SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.RECV_RF_PARA.ToString(), "rfEnable");
    //        //            return;
    //        //        }
    //        //        rf.rfFreq = GetMsgU16ValueInList("rfFreq", n_dic.dic, int.MaxValue);
    //        //        if (rf.rfFreq == int.MaxValue)
    //        //        {
    //        //            SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.RECV_RF_PARA.ToString(), "rfFreq");
    //        //            return;
    //        //        }
    //        //        rf.rfPwr = GetMsgByteValueInList("rfPwr", n_dic.dic, Byte.MaxValue);
    //        //        if (rf.rfPwr == Byte.MaxValue)
    //        //        {
    //        //            SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.RECV_RF_PARA.ToString(), "rfPwr");
    //        //            return;
    //        //        }
    //        //        Flag |= 0x04;
    //        //    }
    //        //    else if (n_dic.name.Equals(Send_Msg_Id.RECV_REG_MODE.ToString()))
    //        //    {
    //        //        regMode = GetMsgByteValueInList("regMode", n_dic.dic, Byte.MaxValue);
    //        //        if (regMode != 0 && regMode != 1)
    //        //        {
    //        //            SendMainMsgParaVlaueError(ApInfo, AppInfo, Send_Msg_Id.RECV_REG_MODE.ToString(), "regMode");
    //        //            return;
    //        //        }

    //        //        Flag |= 0x08;
    //        //    }
    //        //}

    //        if (Flag == 0)
    //        {
    //            String str = String.format("发送给GSM设备消息({0})错误。消息中没有n_dic项。", Msg_Body_Struct.SetGenParaReq);
    //            Log.w(TAG, str);
    //            return;
    //        }

    //        Send2ap_SET_PARA_REQ(apToKen, AppInfo, sys, Flag, para, option, rf, regMode);
    //    }

        #region 封装发送给Main模块消息

    private String StringDelZero(String str)
    {
        String data = "";
        for (int i = 0; i < str.length(); i+=2)
        {
            data = String.format("{0}{1}", data, str[i+1].ToString());
        }
        return data;
    }

    /// <summary>
    /// 向Main模块发送系统参数设备更改通知
    /// </summary>
    /// <param name="apToKen">AP设备信息</param>
    /// <param name="recv">收到的参数</param>
    private void Send2Main_SEND_REQ_CNF(AsyncUserToken apToKen, MsgRecvStruct recv)
    {
        String data = recv.data;
        Msg_Body_Struct TypeKeyValue = new Msg_Body_Struct(Msg_Body_Struct.gsm_msg_recv);
        TypeKeyValue.dic.put("sys", recv.bCellIdx);
        TypeKeyValue.dic.put("hardware_id", 0);

        Name_DIC_Struct nDic = new Name_DIC_Struct();
        nDic.name = "SEND_REQ_CNF".ToString();

        GetValue_Reserved(1, ref data);
        nDic.dic.put("cnfType", Enum.GetName(typeof(Recv_Msg_Id), recv.bMsgId));
        if (recv.bMsgType == Msg_Type.SUCC_OUTCOME)
            nDic.dic.put("cnfInd",  0);
        else
            nDic.dic.put("cnfInd", 1);

        TypeKeyValue.n_dic.put(nDic);

        OnSendMsg2Main(recv.wSqn, MsgType.CONFIG, apToKen, TypeKeyValue);
    }

    private void Send2Main_FAP_NB_CELL_INFO_MSG(AsyncUserToken apToKen, MsgRecvStruct recv)
    {
        String data = recv.data;
        Msg_Body_Struct TypeKeyValue = new Msg_Body_Struct(Msg_Body_Struct.gsm_msg_recv);
        TypeKeyValue.dic.put("sys", recv.bCellIdx);
        TypeKeyValue.dic.put("hardware_id", 0);

        Name_DIC_Struct nDic = new Name_DIC_Struct();
        nDic.name = FAP_NB_CELL_INFO_MSG.ToString();

        Byte bFapNbCellNum = GetValueByString_Byte(ref data);
        if (bFapNbCellNum<0 || bFapNbCellNum>16)
        {
            Log.w(TAG, String.format("邻小区的个数错误，上报个数为{0}。",bFapNbCellNum));
            return;
        }
        nDic.dic.put("bFapNbCellNum", bFapNbCellNum.ToString());

        GetValue_Reserved(3,ref data);

        for (int i = 0; i < bFapNbCellNum; i++)
        {
            Long bGlobalCellId = GetValueByString_U32(ref data);
            nDic.dic.put(String.format("Cell_#{0}#/bGlobalCellId", i), bGlobalCellId.ToString());
            String bPLMNId = StringDelZero(GetValueByString_String(10, ref data));
            nDic.dic.put(String.format("Cell_#{0}#/bPLMNId", i), bPLMNId.ToString());
            SByte cRSRP = GetValueByString_SByte(ref data);
            nDic.dic.put(String.format("Cell_#{0}#/cRSRP", i), cRSRP.ToString());
            int wTac = GetValueByString_U16(ref data);
            nDic.dic.put(String.format("Cell_#{0}#/wTac", i), wTac.ToString());
            int wPhyCellId = GetValueByString_U16(ref data);
            nDic.dic.put(String.format("Cell_#{0}#/wPhyCellId", i), wPhyCellId.ToString());
            int wUARFCN = GetValueByString_U16(ref data);
            nDic.dic.put(String.format("Cell_#{0}#/wUARFCN", i), wUARFCN.ToString());
            SByte cRefTxPower = GetValueByString_SByte(ref data);
            nDic.dic.put(String.format("Cell_#{0}#/cRefTxPower", i), cRefTxPower.ToString());
            Byte bNbCellNum = GetValueByString_Byte(ref data);
            if (bNbCellNum < 0 || bNbCellNum > 32)
            {
                Log.w(TAG, String.format("邻小区{0}的个数错误，上报个数为{1}。", i, bNbCellNum));
                return;
            }
            nDic.dic.put(String.format("Cell_#{0}#/bNbCellNum", i), bNbCellNum.ToString());
            nDic.dic.put(String.format("Cell_#{0}#/bReserved", i), GetValueByString_String(2,ref data));
            Byte bC2 = GetValueByString_Byte(ref data);
            nDic.dic.put(String.format("Cell_#{0}#/bC2", i), bC2.ToString());
            nDic.dic.put(String.format("Cell_#{0}#/bReserved1", i), GetValueByString_String(8, ref data));

            for (int j = 0; j < bNbCellNum; j++)
            {
                int wUarfcn = GetValueByString_U16(ref data);
                nDic.dic.put(String.format("Cell_#{0}#/NeighCell_#{1}#/wUarfcn", i, j), wUarfcn.ToString());
                int wPhyCellId1 = GetValueByString_U16(ref data);
                nDic.dic.put(String.format("Cell_#{0}#/NeighCell_#{1}#/wPhyCellId", i, j), wPhyCellId1.ToString());
                SByte cRSRP1 = GetValueByString_SByte(ref data);
                nDic.dic.put(String.format("Cell_#{0}#/NeighCell_#{1}#/cRSRP", i, j), cRSRP1.ToString());
                GetValue_Reserved(1, ref data);
                SByte cC1 = GetValueByString_SByte(ref data);
                nDic.dic.put(String.format("Cell_#{0}#/NeighCell_#{1}#/cC1", i, j), cC1.ToString());
                Byte bC21 = GetValueByString_Byte(ref data);
                nDic.dic.put(String.format("Cell_#{0}#/NeighCell_#{1}#/bC2", i, j), bC21.ToString());
            }

            for (int j = bNbCellNum; j < 32; j++)
            {
                GetValue_Reserved(8, ref data);
            }
        }

        TypeKeyValue.n_dic.put(nDic);

        OnSendMsg2Main(recv.wSqn, MsgType.CONFIG, apToKen, TypeKeyValue);
    }

    private void Send2Main_CONFIG_FAP_MSG(AsyncUserToken apToKen, MsgRecvStruct recv)
    {
        String data = recv.data;
        Msg_Body_Struct TypeKeyValue = new Msg_Body_Struct(Msg_Body_Struct.ReportGenPara);
        TypeKeyValue.dic.put("sys", recv.bCellIdx);
        TypeKeyValue.dic.put("hardware_id", 0);

        Name_DIC_Struct nDic = new Name_DIC_Struct();
        nDic.name = CONFIG_FAP_MSG.ToString();

        nDic.dic.put("bWorkingMode", GetValueByString_Byte(ref data).ToString());
        nDic.dic.put("bC", GetValueByString_Byte(ref data).ToString());
        nDic.dic.put("wRedirectCellUarfcn", GetValueByString_U16(ref data).ToString());
        GetValue_Reserved(4, ref data);
        GetValue_Reserved(4, ref data);
        //nDic.dic.put("dwDateTime", GetValueByString_U32(ref data).ToString());
        String plmn = CodeConver.AscStr2str(GetValueByString_String(10,ref data).ToString());
        //for (int i= 0;i<5;i++)
        //{
        //    plmn = String.format("{0}{1}",plmn, GetValueByString_Byte(ref data).ToString());
        //}

        nDic.dic.put("bPLMNId", plmn);
        nDic.dic.put("bTxPower", GetValueByString_Byte(ref data).ToString());
        nDic.dic.put("cReserved", GetValueByString_SByte(ref data).ToString());
        nDic.dic.put("bRxGain", GetValueByString_Byte(ref data).ToString());
        nDic.dic.put("wPhyCellId", GetValueByString_U16(ref data).ToString());
        nDic.dic.put("wLAC", GetValueByString_U16(ref data).ToString());
        nDic.dic.put("wUARFCN", GetValueByString_U16(ref data).ToString());
        GetValue_Reserved(2, ref data);
        nDic.dic.put("dwCellId", GetValueByString_U32(ref data).ToString());
        GetValue_Reserved(32, ref data);

        TypeKeyValue.n_dic.put(nDic);

        OnSendMsg2Main(recv.wSqn, MsgType.CONFIG, apToKen, TypeKeyValue);
    }

    private void Send2Main_CONTROL_FAP_REBOOT_MSG(AsyncUserToken apToKen, MsgRecvStruct recv)
    {
        String data = recv.data;
        Msg_Body_Struct TypeKeyValue = new Msg_Body_Struct(Msg_Body_Struct.gsm_msg_recv);
        TypeKeyValue.dic.put("sys", recv.bCellIdx);
        TypeKeyValue.dic.put("hardware_id", 0);

        Name_DIC_Struct nDic = new Name_DIC_Struct();
        nDic.name = CONTROL_FAP_REBOOT_MSG.ToString();

        nDic.dic.put("bRebootFlag", GetValueByString_Byte(ref data).ToString());
        GetValue_Reserved(3, ref data);

        TypeKeyValue.n_dic.put(nDic);

        OnSendMsg2Main(recv.wSqn, MsgType.CONFIG, apToKen, TypeKeyValue);
    }

    private void Send2Main_FAP_TRACE_MSG(AsyncUserToken apToKen, MsgRecvStruct recv;String msgType)
    {
        String data = recv.data;
        Msg_Body_Struct TypeKeyValue = new Msg_Body_Struct(msgType);
        TypeKeyValue.dic.put("sys", recv.bCellIdx);
        TypeKeyValue.dic.put("hardware_id", 0);

        Name_DIC_Struct nDic = new Name_DIC_Struct();
        nDic.name = FAP_TRACE_MSG.ToString();

        nDic.dic.put("wTraceLen", GetValueByString_U16(ref data).ToString());
        nDic.dic.put("cTrace", GetValueByString_String(1024,ref data).ToString());

        TypeKeyValue.n_dic.put(nDic);

        OnSendMsg2Main(recv.wSqn, MsgType.CONFIG, apToKen, TypeKeyValue);
    }

    private void Send2Main_UE_STATUS_REPORT_MSG(AsyncUserToken apToKen, MsgRecvStruct recv, String msgType)
    {
        String data = recv.data;
        Msg_Body_Struct TypeKeyValue = new Msg_Body_Struct(msgType);
        TypeKeyValue.dic.put("sys", recv.bCellIdx);
        TypeKeyValue.dic.put("hardware_id", 0);

        Name_DIC_Struct nDic = new Name_DIC_Struct();
        nDic.name = UE_STATUS_REPORT_MSG.ToString();

        byte addFlag = 0;
        byte type1 = GetValueByString_Byte(ref data);

        String msg = GetValueByString_String(30, ref data).ToString();
        sbyte rsrp = GetValueByString_SByte(ref data);
        byte len = GetValueByString_Byte(ref data);

        if (type1 == 1)
        {
            String imsi = "";
            for (int i = 0; i < len; i++)
            {
                imsi = String.format("{0}{1}", imsi, GetValueByString_Byte(ref msg).ToString());
            }
            nDic.dic.put("imsi", imsi.ToString());
            addFlag |= 0x1;
        }
        else if (type1 == 2)
        {
            String imsi = "";
            for (int i = 0; i < len; i++)
            {
                imsi = String.format("{0}{1}", imsi, GetValueByString_Byte(ref msg).ToString("X"));
            }
            nDic.dic.put("tmsi", "0x" + imsi.ToString());
            addFlag |= 0x2;
        }
        else if (type1 == 3)
        {
            String imsi = "";
            for (int i = 0; i < len; i++)
            {
                imsi = String.format("{0}{1}", imsi, GetValueByString_Byte(ref msg).ToString());
            }
            nDic.dic.put("imei", imsi.ToString());
            addFlag |= 0x4;
        }


        byte type2 = GetValueByString_Byte(ref data);
        msg = GetValueByString_String(30, ref data).ToString();
        len = GetValueByString_Byte(ref data);

        if (type2 == 1)
        {
            String imsi = "";
            for (int i = 0; i < len; i++)
            {
                imsi = String.format("{0}{1}", imsi, GetValueByString_Byte(ref msg).ToString());
            }
            nDic.dic.put("imsi", imsi.ToString());
            addFlag |= 0x1;
        }
        else if (type2 == 2)
        {
            String imsi = "";
            for (int i = 0; i < len; i++)
            {
                imsi = String.format("{0}{1}", imsi, GetValueByString_Byte(ref msg).ToString("X"));
            }
            nDic.dic.put("tmsi", "0x" + imsi.ToString());
            addFlag |= 0x2;
        }
        else if (type2 == 3)
        {
            String imsi = "";
            for (int i = 0; i < len; i++)
            {
                imsi = String.format("{0}{1}", imsi, GetValueByString_Byte(ref msg).ToString());
            }
            nDic.dic.put("imei", imsi.ToString());
            addFlag |= 0x4;
        }


        byte type3 = GetValueByString_Byte(ref data);
        msg = GetValueByString_String(30, ref data).ToString();
        len = GetValueByString_Byte(ref data);

        if (type3 == 1)
        {
            String imsi = "";
            for (int i = 0; i < len; i++)
            {
                imsi = String.format("{0}{1}", imsi, GetValueByString_Byte(ref msg).ToString());
            }
            nDic.dic.put("imsi", imsi.ToString());
            addFlag |= 0x1;
        }
        else if (type3 == 2)
        {
            String imsi = "";
            for (int i = 0; i < len; i++)
            {
                imsi = String.format("{0}{1}", imsi, GetValueByString_Byte(ref msg).ToString("X"));
            }
            nDic.dic.put("tmsi", "0x" + imsi.ToString());
            addFlag |= 0x2;
        }
        else if (type3 == 3)
        {
            String imsi = "";
            for (int i = 0; i < len; i++)
            {
                imsi = String.format("{0}{1}", imsi, GetValueByString_Byte(ref msg).ToString());
            }
            nDic.dic.put("imei", imsi.ToString());
            addFlag |= 0x4;
        }


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

        nDic.dic.put("rsrp", rsrp.ToString());
        nDic.dic.put("userType", "");
        nDic.dic.put("sn", apToKen.Sn);

        TypeKeyValue.n_dic.put(nDic);

        OnSendMsg2Main(recv.wSqn, MsgType.NOTICE, apToKen, TypeKeyValue);
    }

    private void Send2Main_UE_ORM_REPORT_MSG(AsyncUserToken apToKen, MsgRecvStruct recv, String msgType)
    {
        String data = recv.data;
        Msg_Body_Struct TypeKeyValue = new Msg_Body_Struct(msgType);
        TypeKeyValue.dic.put("sys", recv.bCellIdx);
        TypeKeyValue.dic.put("hardware_id", 0);

        Name_DIC_Struct nDic = new Name_DIC_Struct();
        nDic.name = UE_ORM_REPORT_MSG.ToString();

        Byte bOrmType = GetValueByString_Byte(ref data);
        nDic.dic.put("bOrmType",bOrmType.ToString());
        nDic.dic.put("bUeId", StringDelZero(GetValueByString_String(30, ref data).ToString()));
        nDic.dic.put("cRSRP", GetValueByString_SByte(ref data).ToString());
        Byte bUeContentLen = GetValueByString_Byte(ref data);
        nDic.dic.put("bUeContentLen", bUeContentLen.ToString());

        String bUeContent = GetValueByString_String(bUeContentLen * 2, ref data).ToString();

        if (bOrmType == 1 || bOrmType == 3)
        {
            nDic.dic.put("bUeContent", StringDelZero(bUeContent));
        }
        else
        {
            GetValueByString_String(6, ref bUeContent);

            SMS.SMSPARTS Parts = SMS.PDUDecoding(bUeContent);
            Log.i(TAG, String.format("短信解码内容:\n服务中心地址:{0}" +
                    "\n发送方地址:{1}\n用户数据:{2}",Parts.SCA,Parts.OA,Parts.UD));

            //Console.WriteLine("应答路径：" + Parts.RP.ToString());
            //Console.WriteLine("用户数据头标识：" + Parts.UDHI.ToString());
            //Console.WriteLine("状态报告指示：" + Parts.SRI.ToString());
            //Console.WriteLine("更多信息发送：" + Parts.MMS.ToString());
            //Console.WriteLine("信息类型指示：" + Parts.MTI.ToString());
            //Console.WriteLine("PID协议标识：" + Parts.PID.ToString("X2"));
            //Console.WriteLine("数据编码方案：" + Parts.DCS.ToString());
            //Console.WriteLine("文本压缩指示：" + Parts.TC.ToString());
            //Console.WriteLine("消息类型：" + Parts.MC.ToString());
            //if (Parts.UDH != null)
            //{
            //    Console.WriteLine("用户数据头信息：");
            //    foreach (SMS.PDUUDH Element in Parts.UDH)
            //    {
            //        Console.WriteLine("信息元素标识：" + Element.IEI.ToString("X2"));
            //        Console.Write("信息元素数据：");
            //        foreach (Byte b in Element.IED)
            //        {
            //            Console.Write(b.ToString("X2"));
            //        }
            //        Console.WriteLine();
            //}
            //}
            nDic.dic.put("bUeContent", Parts.OA + ";" + Parts.UD);
        }

        TypeKeyValue.n_dic.put(nDic);

        OnSendMsg2Main(0, MsgType.CONFIG, apToKen, TypeKeyValue);
    }

    private void Send2Main_CONFIG_SMS_CONTENT_MSG_ID(AsyncUserToken apToKen, MsgRecvStruct recv)
    {
        String data = recv.data;
        Msg_Body_Struct TypeKeyValue = new Msg_Body_Struct(Msg_Body_Struct.ReportGenPara);
        TypeKeyValue.dic.put("sys", recv.bCellIdx);
        TypeKeyValue.dic.put("hardware_id", 0);

        Name_DIC_Struct nDic = new Name_DIC_Struct();
        nDic.name = CONFIG_SMS_CONTENT_MSG_ID.ToString();

        nDic.dic.put("bSMSOriginalNumLen", GetValueByString_Byte(ref data).ToString());
        nDic.dic.put("bSMSOriginalNum", CodeConver.strToToHexByte(GetValueByString_String(36, ref data).ToString()));

        Byte bSMSContentLen = GetValueByString_Byte(ref data);
        nDic.dic.put("bSMSContentLen", bSMSContentLen.ToString());
        String bSMSContent = GetValueByString_String(bSMSContentLen* 2, ref data);
        nDic.dic.put("bSMSContent", CodeConver.Unicode2String(bSMSContent,false));

        TypeKeyValue.n_dic.put(nDic);

        OnSendMsg2Main(recv.wSqn, MsgType.CONFIG, apToKen, TypeKeyValue);
    }

    private void Send2Main_FAP_PARAM_REPORT_MSG(AsyncUserToken apToKen, MsgRecvStruct recv)
    {
        String data = recv.data;
        Msg_Body_Struct TypeKeyValue = new Msg_Body_Struct(Msg_Body_Struct.gsm_msg_recv);
        TypeKeyValue.dic.put("sys", recv.bCellIdx);
        TypeKeyValue.dic.put("hardware_id", 0);

        Name_DIC_Struct nDic = new Name_DIC_Struct();
        nDic.name = FAP_PARAM_REPORT_MSG.ToString();

        nDic.dic.put("bWorkingMode", GetValueByString_Byte(ref data).ToString());
        GetValue_Reserved(1, ref data);
        nDic.dic.put("wCDMAUarfcn", GetValueByString_U16(ref data).ToString());
        nDic.dic.put("bPLMNId", StringDelZero( GetValueByString_String(10, ref data).ToString()));
        nDic.dic.put("bDlAtt", GetValueByString_Byte(ref data).ToString());
        GetValue_Reserved(1, ref data);
        nDic.dic.put("bRxGain", GetValueByString_Byte(ref data).ToString());
        nDic.dic.put("wPhyCellId", GetValueByString_U16(ref data).ToString());
        nDic.dic.put("wUARFCN", GetValueByString_U16(ref data).ToString());
        GetValue_Reserved(2, ref data);
        nDic.dic.put("dwCellId", GetValueByString_U32(ref data).ToString());

        nDic.dic.put("wARFCN1", GetValueByString_U16(ref data).ToString());
        nDic.dic.put("bARFCN1Mode", GetValueByString_Byte(ref data).ToString());
        GetValue_Reserved(1, ref data);
        nDic.dic.put("wARFCN1Duration", GetValueByString_U16(ref data).ToString());
        nDic.dic.put("wARFCN1Period", GetValueByString_U16(ref data).ToString());

        nDic.dic.put("wARFCN2", GetValueByString_U16(ref data).ToString());
        nDic.dic.put("bARFCN2Mode", GetValueByString_Byte(ref data).ToString());
        GetValue_Reserved(1, ref data);
        nDic.dic.put("wARFCN2Duration", GetValueByString_U16(ref data).ToString());
        nDic.dic.put("wARFCN2Period", GetValueByString_U16(ref data).ToString());

        nDic.dic.put("wARFCN3", GetValueByString_U16(ref data).ToString());
        nDic.dic.put("bARFCN3Mode", GetValueByString_Byte(ref data).ToString());
        GetValue_Reserved(1, ref data);
        nDic.dic.put("wARFCN3Duration", GetValueByString_U16(ref data).ToString());
        nDic.dic.put("wARFCN3Period", GetValueByString_U16(ref data).ToString());

        nDic.dic.put("wARFCN4", GetValueByString_U16(ref data).ToString());
        nDic.dic.put("bARFCN4Mode", GetValueByString_Byte(ref data).ToString());
        GetValue_Reserved(1, ref data);
        nDic.dic.put("wARFCN4Duration", GetValueByString_U16(ref data).ToString());
        nDic.dic.put("wARFCN4Period", GetValueByString_U16(ref data).ToString());

        TypeKeyValue.n_dic.put(nDic);

        OnSendMsg2Main(recv.wSqn, MsgType.CONFIG, apToKen, TypeKeyValue);
    }

    private void Send2Main_CONTROL_FAP_RADIO_ON_MSG(AsyncUserToken apToKen, MsgRecvStruct recv)
    {
        String data = recv.data;
        Msg_Body_Struct TypeKeyValue = new Msg_Body_Struct(Msg_Body_Struct.gsm_msg_recv);
        TypeKeyValue.dic.put("sys", recv.bCellIdx);
        TypeKeyValue.dic.put("hardware_id", 0);

        Name_DIC_Struct nDic = new Name_DIC_Struct();
        nDic.name = CONTROL_FAP_RADIO_ON_MSG.ToString();

        nDic.dic.put("rfStatus", GetValueByString_Byte(ref data).ToString());

        TypeKeyValue.n_dic.put(nDic);

        OnSendMsg2Main(recv.wSqn, MsgType.CONFIG, apToKen, TypeKeyValue);
    }

    private void Send2Main_CONTROL_FAP_RADIO_OFF_MSG(AsyncUserToken apToKen, MsgRecvStruct recv)
    {
        String data = recv.data;
        Msg_Body_Struct TypeKeyValue = new Msg_Body_Struct(Msg_Body_Struct.gsm_msg_recv);
        TypeKeyValue.dic.put("sys", recv.bCellIdx);
        TypeKeyValue.dic.put("hardware_id", 0);

        Name_DIC_Struct nDic = new Name_DIC_Struct();
        nDic.name = CONTROL_FAP_RADIO_OFF_MSG.ToString();

        nDic.dic.put("rfStatus", GetValueByString_Byte(ref data).ToString());

        TypeKeyValue.n_dic.put(nDic);

        OnSendMsg2Main(recv.wSqn, MsgType.CONFIG, apToKen, TypeKeyValue);
    }

    private void Send2Main_CONTROL_FAP_RESET_MSG(AsyncUserToken apToKen, MsgRecvStruct recv)
    {
        String data = recv.data;
        Msg_Body_Struct TypeKeyValue = new Msg_Body_Struct(Msg_Body_Struct.gsm_msg_recv);
        TypeKeyValue.dic.put("sys", recv.bCellIdx);
        TypeKeyValue.dic.put("hardware_id", 0);

        Name_DIC_Struct nDic = new Name_DIC_Struct();
        nDic.name = CONTROL_FAP_RESET_MSG.ToString();

        //nDic.dic.put("rfStatus", GetValueByString_Byte(ref data).ToString());

        TypeKeyValue.n_dic.put(nDic);

        OnSendMsg2Main(recv.wSqn, MsgType.CONFIG, apToKen, TypeKeyValue);
    }

    private void Send2Main_CONFIG_CDMA_CARRIER_MSG(AsyncUserToken apToKen, MsgRecvStruct recv)
    {
        String data = recv.data;
        Msg_Body_Struct TypeKeyValue = new Msg_Body_Struct(Msg_Body_Struct.ReportGenPara);
        TypeKeyValue.dic.put("sys", recv.bCellIdx);
        TypeKeyValue.dic.put("hardware_id", 0);

        Name_DIC_Struct nDic = new Name_DIC_Struct();
        nDic.name = CONFIG_CDMA_CARRIER_MSG.ToString();

        nDic.dic.put("wARFCN1", GetValueByString_U16(ref data).ToString());
        nDic.dic.put("bARFCN1Mode", GetValueByString_Byte(ref data).ToString());
        GetValue_Reserved(1, ref data);
        nDic.dic.put("wARFCN1Duration", GetValueByString_U16(ref data).ToString());
        nDic.dic.put("wARFCN1Period", GetValueByString_U16(ref data).ToString());

        nDic.dic.put("wARFCN2", GetValueByString_U16(ref data).ToString());
        nDic.dic.put("bARFCN2Mode", GetValueByString_Byte(ref data).ToString());
        GetValue_Reserved(1, ref data);
        nDic.dic.put("wARFCN2Duration", GetValueByString_U16(ref data).ToString());
        nDic.dic.put("wARFCN2Period", GetValueByString_U16(ref data).ToString());

        nDic.dic.put("wARFCN3", GetValueByString_U16(ref data).ToString());
        nDic.dic.put("bARFCN3Mode", GetValueByString_Byte(ref data).ToString());
        GetValue_Reserved(1, ref data);
        nDic.dic.put("wARFCN3Duration", GetValueByString_U16(ref data).ToString());
        nDic.dic.put("wARFCN3Period", GetValueByString_U16(ref data).ToString());

        nDic.dic.put("wARFCN4", GetValueByString_U16(ref data).ToString());
        nDic.dic.put("bARFCN4Mode", GetValueByString_Byte(ref data).ToString());
        GetValue_Reserved(1, ref data);
        nDic.dic.put("wARFCN4Duration", GetValueByString_U16(ref data).ToString());
        nDic.dic.put("wARFCN4Period", GetValueByString_U16(ref data).ToString());


        TypeKeyValue.n_dic.put(nDic);

        OnSendMsg2Main(recv.wSqn, MsgType.CONFIG, apToKen, TypeKeyValue);
    }

    private void Send2Main_CONFIG_IMSI_MSG_V3_ID(AsyncUserToken apToKen, MsgRecvStruct recv)
    {
        String data = recv.data;
        Msg_Body_Struct TypeKeyValue = new Msg_Body_Struct(Msg_Body_Struct.ReportGenPara);
        TypeKeyValue.dic.put("sys", recv.bCellIdx);
        TypeKeyValue.dic.put("hardware_id", 0);

        Name_DIC_Struct nDic = new Name_DIC_Struct();
        nDic.name = CONFIG_IMSI_MSG_V3_ID.ToString();

        nDic.dic.put("wTotalImsi", GetValueByString_U16(ref data).ToString());
        Byte bIMSINum = GetValueByString_Byte(ref data);
        nDic.dic.put("bIMSINum", bIMSINum.ToString());
        if (bIMSINum < 0 || bIMSINum > 50)
        {
            Log.w(TAG, String.format("IMSI个数错误，上报个数为{0}。", bIMSINum));
            return;
        }
        nDic.dic.put("bSegmentType", GetValueByString_Byte(ref data).ToString());
        nDic.dic.put("bSegmentID", GetValueByString_Byte(ref data).ToString());
        nDic.dic.put("bActionType", GetValueByString_Byte(ref data).ToString());
        GetValue_Reserved(2, ref data);

        for (int i = 0; i < bIMSINum; i++)
        {
            nDic.dic.put(String.format("bIMSI_#{0}#",i),
                    StringDelZero(GetValueByString_String(30,ref data).ToString()));
            //nDic.dic.put(String.format("bUeActionFlag_#{0}#", i), GetValueByString_Byte(ref data).ToString());
        }

        for (int i = bIMSINum; i < 50; i++)
        {
            GetValue_Reserved(15, ref data);
        }

        for (int i = 0; i < bIMSINum; i++)
        {
            //nDic.dic.put(String.format("bIMSI_#{0}#", i), GetValueByString_String(30, ref data).ToString());
            nDic.dic.put(String.format("bUeActionFlag_#{0}#", i), GetValueByString_Byte(ref data).ToString());
        }

        for (int i = bIMSINum; i < 50; i++)
        {
            GetValue_Reserved(1, ref data);
        }

        TypeKeyValue.n_dic.put(nDic);

        OnSendMsg2Main(recv.wSqn, MsgType.CONFIG, apToKen, TypeKeyValue);
    }

        #endregion

        #endregion

}
*/
}
