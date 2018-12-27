package com.bravo.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 2018-9-12.
 */

public class Msg_Body_Struct
{
    public int msgId;
    public String type;
    public HashMap<String, Object> dic;
    public List<Name_DIC_Struct> n_dic;

    public static final String BroadCast = "BroadCast";
    public static final String BroadCast_result = "BroadCast_result";
    public static final String SetUDPServerIp = "SetUDPServerIp";
    public static final String SetUDPServerIp_result = "SetUDPServerIp_result";

    public static final String status_response = "status_response";
    public static final String status_request = "status_request";

    public static final String scanner = "scanner";
    public static final String meas_report = "meas_report";

    public static final String gsm_msg = "gsm_msg";
    public static final String ack_msg = "ack_msg";
    public static final String gsm_msg_recv = "gsm_msg_recv";
    public static final String straight_msg = "straight_msg";

    public static final String activate_nodeb_request = "activate_nodeb_request";

    public static final String set_parameter_request = "set_parameter_request";
    public static final String set_param_response = "set_param_response";

    public static final String set_general_para_request = "set_general_para_request";
    public static final String set_general_para_response = "set_general_para_response";
    public static final String get_general_para_request = "get_general_para_request";
    public static final String get_general_para_response = "get_general_para_response";

    public static final String get_redirection_req = "get_redirection_req";
    public static final String get_redirection_rsp = "get_redirection_rsp";
    public static final String set_redirection_req = "set_redirection_req";
    public static final String set_redirection_rsp = "set_redirection_rsp";

    public static final String set_work_mode = "set_work_mode";
    public static final String set_work_mode_reponse = "set_work_mode_reponse";
    public static final String set_son_earfcn = "set_son_earfcn";
    public static final String set_son_earfcn_response = "set_son_earfcn_response";
    public static final String set_configuration = "set_configuration";
    public static final String set_configuration_result = "set_configuration_result";
    public static final String set_system_request = "set_system_request";
    public static final String set_system_response = "set_system_response";
    public static final String Syncinfo_set = "Syncinfo_set";
    public static final String Syncinfo_set_response = "Syncinfo_set_response";
    public static final String DataAlignOver = "DataAlignOver";
    public static final String DataAlignOverAck = "DataAlignOverAck";

    public static final String set_cnm_sync_status_request = "set_cnm_sync_status_request";
    public static final String set_cnm_sync_status_response = "set_cnm_sync_status_response";


    public Msg_Body_Struct(int id,String type)
    {
        this.msgId = id;
        this.type = type;
        dic = new HashMap<String, Object>();
        n_dic = new ArrayList<Name_DIC_Struct>();
    }

    public Msg_Body_Struct(String type)
    {
        this.msgId = 0;
        this.type = type;
        dic = new HashMap<String, Object>();
        n_dic = new ArrayList<Name_DIC_Struct>();
    }

    public Msg_Body_Struct()
    {
        this.msgId = 0;
        this.type = "";
        dic = new HashMap<String, Object>();
        n_dic = new ArrayList<Name_DIC_Struct>();
    }

    public Msg_Body_Struct(int id,String type, HashMap<String, Object> dic)
    {
        this.msgId = id;
        this.type = type;
        this.dic = dic;
        n_dic = null;
    }

    public Msg_Body_Struct(int id,String type, HashMap<String, Object> dic,List<Name_DIC_Struct> n_dic)
    {
        this.msgId = id;
        this.type = type;
        this.dic = dic;
        this.n_dic = n_dic;
    }

    /// <summary>
    /// 构造函数
    /// </summary>
    /// <param name="xmlType">xml消息类型</param>
    /// <param name="KeyValue">xml消息里的键值对，键值对必须成对出现。</param>
    public Msg_Body_Struct(int id,String xmlType, Object... KeyValue)
    {
        this.msgId = id;
        this.type = xmlType;
        this.dic = new HashMap<String, Object>();

        if ((KeyValue.length % 2) != 0)
        {
            //OutputLog("输入的参数不是2的倍数。键值对必须成对出现。");
        }
        else
        {
            for (int i = 0; i < KeyValue.length; i = i + 2)
            {
                this.dic.put((String)KeyValue[i], KeyValue[i + 1]);
            }

            //dic.Add(FindMsgStruct.AllNum, dic.Count + 1);
        }
    }

    public Msg_Body_Struct(int id,String xmlType, String... KeyValue)
    {
        this.msgId = id;
        this.type = xmlType;
        this.dic = new HashMap<String, Object>();

        if ((KeyValue.length % 2) != 0)
        {
            //OutputLog("输入的参数不是2的倍数。键值对必须成对出现。");
        }
        else
        {
            for (int i = 0; i < KeyValue.length; i = i + 2)
            {
                this.dic.put((String)KeyValue[i], KeyValue[i + 1]);
            }
            //dic.Add(FindMsgStruct.AllNum, dic.Count + 1);
        }
    }

}
