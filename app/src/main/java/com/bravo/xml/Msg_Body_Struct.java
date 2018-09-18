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
