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
 * Created by admin on 2018-10-9.
 */

public class GSM_HJT {
    private final String TAG = "GSM_HJT";

    private final int MsgHadeLen = 12;

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
    class Gsm_Send_Msg_Type {
        public static final int RECV_SYS_PARA = 1;   //系统参数，在基本参数配置界面中，包括MCC、MNC等
        public static final int RECV_SYS_OPTION = 2;                   //系统选项，在可选配置界面中，如是否上报IMEI等
        public static final int RECV_DLRX_PARA = 3;                //下行接收参数，在测试界面中，用于获取网络信息
        public static final int RECV_RF_PARA = 4;                   //射频参数，在基本参数配置界面中，如频点、功率等
        public static final int RECV_QUERY_VER = 5;       //查询版本，在测试界面中（暂不用）
        public static final int RECV_TEST_CMD = 6;                  //测试命令，在测试界面中
        public static final int RECV_SMS_RPOA = 7;                  //短消息中心号码，在短信配置界面中
        public static final int RECV_SMS_TPOA = 8;                  //短消息原叫号码，在短信配置界面中
        public static final int RECV_SMS_SCTS = 9;                  //短消息发送时间，在短信配置界面中
        public static final int RECV_SMS_DATA = 10;         //短消息编辑内容，在短信配置界面中
        public static final int RECV_BS_SEAR = 11;                   //基站搜索，在基本参数配置界面中
        public static final int RECV_SMS_SEND = 12;                 //发送短信（暂不用）
        public static final int RECV_SMS_STOP = 13;                  //停止短信（暂不用）
        public static final int RECV_QUERY_RSP = 14;                //查询响应（暂不用）
        public static final int RECV_LIBRARY_REG_ADD = 15;          //登录控制库中添加IMSI
        public static final int RECV_LIBRARY_REG_DELALL = 16;            //登录控制库清空
        public static final int RECV_LIBRARY_REG_QUERY = 17;       //登录控制库中查询，返回所有库中IMSI
        public static final int RECV_LIBRARY_SMS_ADD = 18;          //短信控制库中添加IMSI
        public static final int RECV_LIBRARY_SMS_DELALL = 19;           //短信控制库清空
        public static final int RECV_LIBRARY_SMS_QUERY = 20;        //短信控制库中查询，返回所有库中IMSI
        public static final int RECV_NCELLTAB_SET = 21;              //配置邻区表
        public static final int RECV_NCELLTAB_DEL = 22;              //清空邻区表
        public static final int RECV_CHNUM = 23;                 //配置进程数（暂不用）
        public static final int RECV_REREG = 24;                     //重新获取
        public static final int RECV_BS_CALL_END = 25;         //BS挂机
        public static final int RECV_BS_CALL_CON = 26;              //BS接听
        public static final int RECV_MT_CALL = 27;                 //设备电话主叫，需要被呼叫的IMSI号，原叫号码
        public static final int RECV_MT_SMS = 28;                  //设备短信主叫，需要被呼叫的IMSI号，其他在短信界面配置
        public static final int RECV_MT_CALL_SILENCE = 29;              //设备连续安全电话主叫，需要被呼叫的IMSI号，呼叫周期
        public static final int RECV_MT_SMS_SILENCE = 30;           //设备连续安全短信主叫，需要被呼叫的IMSI号，呼叫周期（暂不用）
        public static final int RECV_ANT_DIREC = 31;                 //使用开关控制4个方向性定位天线
        public static final int RECV_TALK_PARA = 32;                //设置通话参数，包括频点和手机发射功率
        public static final int RECV_REG_MODE = 33;                  //手机注册时的工作模式
        public static final int RECV_SMS_DATA_SN = 35;               //长短信内容分段序号  (文档中用的34，实际中要用35)
        public static final int RECV_SMS_OPTION = 36;                   //未在文档中，CS软件内部定义
    }

    /// <summary>
    /// 设备发送给控制端的消息类型
    /// </summary>
    class Gsm_Recv_Msg_Type
    {
        public static final int SEND_REQ_CNF = 0x01;      //确认接收到的请求，在状态栏显示
        public static final int SEND_OM_INFO= 0x02;                   //设备OM信息，在状态栏显示，每60s发送一次
        public static final int SEND_VER_INFO= 0x03;             //设备版本信息，在状态栏显示
        public static final int SEND_UE_INFO= 0x04;                   //用户设备信息，在上报信息界面显示，包括IMSI、IMEI等
        public static final int SEND_TEST_INFO = 0x05;	//返回测试信息，在测试界面显示
        public static final int SEND_BS_INFO= 0x06;                   //返回基站信息，在配置页显示
        public static final int SEND_QUERY_REQ= 0x07;             //查询请求
        public static final int SEND_LIBRARY_REG= 0x08;                //登录库返回信息
        public static final int SEND_LIBRARY_SMS= 0x09;                //SMS库返回信息
        public static final int SEND_OBJECT_POWER = 0x0A;   //目标功率
        public static final int SEND_BS_SEAR_INFO= 0x0B;              //基站搜索到的信息
        public static final int SEND_MS_CALL_SETUP= 0x0C;              //手机主动发起呼叫，包括其IMSI，被叫号码
        public static final int SEND_MS_SMS_SEND= 0x0D;              //手机主动发起短信，包括其IMSI，被叫号码，短信内容
        public static final int SEND_MS_CALL_OPERATE= 0x0E;         //手机在被呼叫时的操作，可以是挂机、摘机或未操作而超时。
    }

        private Context mContext;

        public GSM_HJT(Context context){
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

    // <summary>
    /// 接收设备消息结构
    /// </summary>
    private class gsm_msg_recv
    {
        public String head;          //头部标识 0xAAAA
        public int addr;         //地址，0表示设备发
        public int sys;              //系统号，0表示系统1或通道1或射频1，1表示系统2或通道2或射频2
        public int type;         //消息类型
        public int data_length;      //消息数据长度  [数据长度是硬件ID、消息ID和消息数据的总和]
        public long hardware_id;   //硬件ID    　[硬件ID是每个硬件的固有编号]
        public int message_id;    //消息ID   　  [它是设备接收到消息ID的返回值，与接收到的值一样]
        public String data;		//消息数据
    }
    private class ack_msg_recv
    {
        public String head;          //头部标识 0xAAAA
        public int addr;         //地址，0表示设备发
        public int sys;              //系统号，0表示系统1或通道1或射频1，1表示系统2或通道2或射频2
        public int type;         //消息类型
        public int data_length;      //消息数据长度  [数据长度是硬件ID、消息ID和消息数据的总和]
        public Long hardware_id;   //硬件ID    　[硬件ID是每+个硬件的固有编号]
        public int message_id;    //消息ID   　  [它是设备接收到消息ID的返回值，与接收到的值一样]
        public String data;		//消息数据
    }


    private boolean NoEmpty(String str)
    {
        return ((str != null) && (!str.isEmpty()));
    }

    public void HandleMsg(DeviceDataStruct dds, Msg_Body_Struct msgBody) {
        if (msgBody.type.equals(Msg_Body_Struct.gsm_msg))
        {
            gsm_msg_recv recv = null;
            String msg_data = FindMsgStruct.GetMsgStringValueInList("data", msgBody.dic);
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
                Logs.e(TAG, "收到XML消息格式错误！");
                return;
            }

            HandleGsmMsg(dds, recv);

        }
        else
        {
            Logs.e(TAG, String.format("不支持的XML消息%s！",msgBody.type));
        }
    }

    private void HandleGsmMsg(DeviceDataStruct dds, gsm_msg_recv recv)
    {
        String data = recv.data;
        if (recv.type == Gsm_Recv_Msg_Type.SEND_REQ_CNF) //确认消息
        {
            //Send2Main_SEND_REQ_CNF(apToKen,recv);
        }
        else if (recv.type == Gsm_Recv_Msg_Type.SEND_OM_INFO)
        {
            //Send2Main_SEND_OM_INFO(apToKen, recv);
        }
        else if (recv.type == Gsm_Recv_Msg_Type.SEND_VER_INFO)
        {
            //Send2Main_SEND_VER_INFO(apToKen, recv);
        }
        else if (recv.type == Gsm_Recv_Msg_Type.SEND_UE_INFO)
        {
            Send2Main_SEND_UE_INFO(dds, recv);
        }
        else if (recv.type == Gsm_Recv_Msg_Type.SEND_TEST_INFO)
        {
            //Send2Main_SEND_TEST_INFO(apToKen, recv);
        }
        else if (recv.type == Gsm_Recv_Msg_Type.SEND_BS_INFO)
        {
            //Send2Main_SEND_BS_INFO(apToKen, recv);
        }
        else if (recv.type == Gsm_Recv_Msg_Type.SEND_QUERY_REQ)
        {
            //Send2Main_SEND_QUERY_REQ(apToKen, recv);
        }
        else if (recv.type == Gsm_Recv_Msg_Type.SEND_LIBRARY_REG)
        {
            //Send2Main_SEND_LIBRARY_REG(apToKen, recv);
        }
        else if (recv.type == Gsm_Recv_Msg_Type.SEND_LIBRARY_SMS)
        {
            //Send2Main_SEND_LIBRARY_SMS(apToKen, recv);
        }
        else if (recv.type == Gsm_Recv_Msg_Type.SEND_OBJECT_POWER)
        {
            //Send2Main_SEND_OBJECT_POWER(apToKen, recv);
        }
        else if (recv.type == Gsm_Recv_Msg_Type.SEND_BS_SEAR_INFO)
        {

        }
        else if (recv.type == Gsm_Recv_Msg_Type.SEND_MS_CALL_SETUP)
        {
            //Send2Main_SEND_MS_CALL_SETUP(apToKen, recv);
        }
        else if (recv.type == Gsm_Recv_Msg_Type.SEND_MS_SMS_SEND)
        {
            //Send2Main_SEND_MS_SMS_SEND(apToKen, recv);
        }
        else if (recv.type == Gsm_Recv_Msg_Type.SEND_MS_CALL_OPERATE)
        {
            //Send2Main_SEND_MS_CALL_OPERATE(apToKen, recv);
        }
        else
        {
            Logs.w(TAG, "HandleGsmMsg收到的Ap消息类型错误！");
        }
    }

    /// <summary>
    /// 解析收到的GSM消息
    /// </summary>
    /// <param name="recvFlag">设备发消息类型</param>
    /// <param name="recv">解析后的消息内容</param>
    /// <param name="msg_data">收到的消息</param>
    /// <returns>解析是否成功</returns>
    private gsm_msg_recv DecodeGsmMsg(boolean recvFlag, String msg_data)
    {
        gsm_msg_recv recv = new gsm_msg_recv();
        GetDataValue gdv = new GetDataValue(msg_data);

        recv.head = gdv.GetValueByString_String(4);
        if (!recv.head.equals("AAAA"))
        {
            Logs.e(TAG, "解析GSM消息格式错误，head字段错误！");
            return null;
        }

        //Byte addr = Convert.ToByte(msg_data.Substring(4, 2),16);
        recv.addr = gdv.GetValueByString_Byte();
        if (recv.addr != 0)
        {
            Logs.e(TAG, "解析GSM消息格式错误，addr字段不为0(设备发)！");
            return null;
        }
        //Byte sys = Convert.ToByte(msg_data.Substring(6, 2),16);
        int sys = gdv.GetValueByString_Byte();
        if ((sys != 0) && (sys != 1))
        {
            Logs.e(TAG, "解析GSM消息格式错误，sys字段不为0或1(0表示系统1或通道1或射频1，1表示系统2或通道2或射频2)！");
            return null;
        }
        recv.sys = sys;

        //Byte type = Convert.ToByte(msg_data.Substring(8, 2),16);
        int type = gdv.GetValueByString_Byte();
        if (recvFlag)
        {
            if (type < Gsm_Recv_Msg_Type.SEND_REQ_CNF || type > Gsm_Recv_Msg_Type.SEND_MS_CALL_OPERATE)
            {
                Logs.e(TAG, "解析GSM消息格式错误，type字段错误！");
                return null;
            }
        }
        else
        {
            if (type < Gsm_Send_Msg_Type.RECV_SYS_PARA || type > Gsm_Send_Msg_Type.RECV_SMS_OPTION)
            {
                Logs.e(TAG, "解析GSM消息格式错误，type字段错误！");
                return null;
            }
        }
        recv.type = type;

        //int data_length = Convert.ToInt16(msg_data.Substring(10, 2))-6;
        recv.data_length = gdv.GetValueByString_Byte();
        recv.data_length -= 6;//-6为去掉hardware_id和message_id后的净数据长度
        if (recv.data_length < 0)
        {
            Logs.e(TAG, "解析GSM消息格式错误，data_length字段错误！");
            return null;
        }


        //UInt32 hardware_id = Convert.ToUInt32(msg_data.Substring(12, 8),16);
        recv.hardware_id = gdv.GetValueByString_U32();
        if (recv.hardware_id <= 0)
        {
            Logs.e(TAG, "解析GSM消息格式错误，hardware_id字段错误！");
            return null;
        }

        //UInt16 message_id = Convert.ToUInt16(msg_data.Substring(20, 4),16);
        recv.message_id = gdv.GetValueByString_U16();
        if (recv.message_id < 0)
        {
            Logs.e(TAG, "解析GSM消息格式错误，message_id字段错误！",true);
            return null;
        }

        recv.data = "";
        if (recv.data_length > 0)
        {
            //data = msg_data.Substring(24, data_length*2);
            recv.data = gdv.GetValueByString_String(recv.data_length * 2);
            if (!NoEmpty(recv.data))
            {
                Logs.e(TAG, "解析GSM消息格式错误，data字段错误！",true);
                return null;
            }
        }

        return recv;
    }

    private void Send2Main_SEND_UE_INFO(DeviceDataStruct dds, gsm_msg_recv recv)
    {
        GetDataValue gdv = new GetDataValue(recv.data);
        Msg_Body_Struct TypeKeyValue = new Msg_Body_Struct(Msg_Body_Struct.gsm_msg_recv);
        TypeKeyValue.dic.put("sys", recv.sys);
        TypeKeyValue.dic.put("hardware_id", recv.hardware_id);

        Name_DIC_Struct nDic = new Name_DIC_Struct();
        nDic.name = "SEND_UE_INFO";

        String ueImsi = gdv.GetValueByString_String(16);
        nDic.dic.put("ueImsi", ueImsi.substring(0,ueImsi.length()-1));
        String ueImei = gdv.GetValueByString_String(16);
        nDic.dic.put("ueImei", ueImei.substring(0, ueImsi.length() - 1));
        String ueMsisdn = gdv.GetValueByString_String(8);
        nDic.dic.put("ueMsisdn", ueMsisdn);
        int uePwr =gdv. GetValueByString_SByte();
        nDic.dic.put("uePwr", uePwr);
        int UeRegtype = gdv.GetValueByString_Byte();
        nDic.dic.put("UeRegtype", UeRegtype);
        int ueQueryResult = gdv.GetValueByString_Byte();
        nDic.dic.put("ueQueryResult", ueQueryResult);
        long ueTmsi = gdv.GetValueByString_U32();
        nDic.dic.put("ueTmsi", "0x"+ Long.toHexString(ueTmsi));
        int ueLlac = gdv.GetValueByString_U16();
        nDic.dic.put("ueLlac", ueLlac);
        int ueSlac = gdv.GetValueByString_U16();
        nDic.dic.put("ueSlac", ueSlac);

        TypeKeyValue.n_dic.add(nDic);

        TargetDataStruct targetDataStruct = new TargetDataStruct();

        targetDataStruct.setSN(dds.getSN());
        targetDataStruct.setIP(dds.getIp());
        targetDataStruct.setPort(dds.getPort());
        targetDataStruct.setFullName(dds.getFullName());
        targetDataStruct.setDeviceType(dds.getMode());

        targetDataStruct.setImsi(FindMsgStruct.GetMsgStringValueInList("ueImsi", nDic.dic, ""));
        //targetDataStruct.setiUserType(FindMsgStruct.GetMsgIntValueInList("UeRegtype", nDic.dic, 0));
        //Logs.d(TAG,"用户类型：" + targetDataStruct.getiUserType());
        targetDataStruct.setImei(FindMsgStruct.GetMsgStringValueInList("ueImei", nDic.dic, ""));
        targetDataStruct.setTmsi(FindMsgStruct.GetMsgStringValueInList("ueTmsi", nDic.dic, ""));
        targetDataStruct.setRsrp(FindMsgStruct.GetMsgIntValueInList("uePwr", nDic.dic, 0));
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
}