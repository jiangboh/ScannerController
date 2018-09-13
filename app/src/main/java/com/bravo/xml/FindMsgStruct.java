package com.bravo.xml;

import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 2018-9-12.
 */

public class FindMsgStruct {

    static public String AllNum = "AllNum";

    /// <summary>
    /// 在字典中查找值
    /// </summary>
    /// <param name="key">键</param>
    /// <param name="dic">字典</param>
    /// <returns>值，未找到返回空</returns>
    static public Object GetMsgValueInList(String key, HashMap<String, Object> dic)
    {
        if (!dic.containsKey(key)) return null;
        return dic.get(key);
    }
    static public Object GetMsgValueInList(String key, HashMap<String, Object> dic ,Object def)
    {
        if (!dic.containsKey(key)) return def;
        return dic.get(key);
    }
    static public String GetMsgStringValueInList(String key, HashMap<String, Object> dic)
    {
        if (!dic.containsKey(key)) return "";
        if (dic.get(key) == null) return "";
        return String.valueOf(dic.get(key));
    }
    static public String GetMsgStringValueInList(String key, HashMap<String, Object> dic, String def)
    {
        if (!dic.containsKey(key)) return def;
        if (dic.get(key) == null) return def;
        return String.valueOf(dic.get(key));
    }
    static public Byte GetMsgByteValueInList(String key, HashMap<String, Object> dic)
    {
        if (!dic.containsKey(key)) return 0;
        String tmp = GetMsgStringValueInList(key,dic);
        if (tmp == null || tmp.isEmpty()) return 0;
        return Byte.parseByte(tmp);
    }
    static public Byte GetMsgByteValueInList(String key, HashMap<String, Object> dic,byte def)
    {
        if (!dic.containsKey(key)) return def;
        String tmp = GetMsgStringValueInList(key,dic);
        if (tmp == null || tmp.isEmpty()) return def;
        return Byte.parseByte(tmp);
    }
    /*static public SByte GetMsgSByteValueInList(String key, HashMap<String, Object> dic)
    {
        if (!dic.containsKey(key)) return 0;
        return Convert.ToSByte(dic.get(key));
    }
    static public SByte GetMsgSByteValueInList(String key, HashMap<String, Object> dic, sbyte def)
    {
        if (!dic.containsKey(key)) return def;
        return Convert.ToSByte(dic.get(key));
    }
    static public UInt16 GetMsgU16ValueInList(String key, HashMap<String, Object> dic)
    {
        if (!dic.containsKey(key)) return 0;
        return Convert.ToUInt16(dic.get(key));
    }
    static public UInt16 GetMsgU16ValueInList(String key, HashMap<String, Object> dic,UInt16 def)
    {
        if (!dic.containsKey(key)) return def;
        return Convert.ToUInt16(dic.get(key));
    }
    static public UInt32 GetMsgU32ValueInList(String key, HashMap<String, Object> dic)
    {
        if (!dic.containsKey(key)) return 0;
        return Convert.ToUInt32(dic.get(key));
    }
    static public UInt32 GetMsgU32ValueInList(String key, HashMap<String, Object> dic, UInt32 def)
    {
        if (!dic.containsKey(key)) return def;
        return Convert.ToUInt32(dic.get(key));
    }
    */
    static public int GetMsgIntValueInList(String key, HashMap<String, Object> dic)
    {
        if (!dic.containsKey(key)) return 0;
        String tmp = GetMsgStringValueInList(key,dic);
        if (tmp == null || tmp.isEmpty()) return 0;
        return Integer.parseInt(tmp);
    }
    static public int GetMsgIntValueInList(String key, HashMap<String, Object> dic,int def)
    {
        if (!dic.containsKey(key)) return def;
        String tmp = GetMsgStringValueInList(key,dic);
        if (tmp == null || tmp.isEmpty()) return def;
        return Integer.parseInt(tmp);
    }
    static public double GetMsgDoubleValueInList(String key, HashMap<String, Object> dic)
    {
        if (!dic.containsKey(key)) return 0;
        String tmp = GetMsgStringValueInList(key,dic);
        if (tmp == null || tmp.isEmpty()) return 0;
        return Double.parseDouble(tmp);
    }
    static public double GetMsgDoubleValueInList(String key, HashMap<String, Object> dic,double def)
    {
        if (!dic.containsKey(key)) return def;
        String tmp = GetMsgStringValueInList(key,dic);
        if (tmp == null || tmp.isEmpty()) return def;
        return Double.parseDouble(tmp);
    }

    /// <summary>
    /// 在第一层字典里查找值
    /// </summary>
    /// <param name="name">第二层名称</param>
    /// <param name="key">键</param>
    /// <param name="msgBody">消息内容</param>
    /// <returns>值。未找到时返回空</returns>
    static public Object GetMsgValueInList(String name, String key, Msg_Body_Struct msgBody)
    {
        List<Name_DIC_Struct> n_dic = msgBody.n_dic;
        if (n_dic == null)
            return "";
        if (name == null || name.isEmpty())
            return "";

        int size = n_dic.size();
        for (int i = 0; i < size; i++)
        {
            Name_DIC_Struct tmp = n_dic.get(i);
            if (name.equalsIgnoreCase(tmp.name))
            {
                HashMap<String, Object> dic = tmp.dic;
                //没有该键
                if (!dic.containsKey(key)) return "";

                return dic.get(key);
            }
        }
        return "";
    }

    /*/// <summary>
    /// 在第一层字典里查找值
    /// </summary>
    /// <param name="key">键</param>
    /// <param name="msgBody">消息内容</param>
    /// <returns>值。未找到时返回空</returns>
    static public Object GetMsgValueInList(String key, Msg_Body_Struct msgBody)
    {
        HashMap<String, Object> dic = msgBody.dic;
        if (!dic.containsKey(key)) return "";
        return dic.get(key);
    }
    static public int GetMsgIntValueInList(String key, Msg_Body_Struct msgBody)
    {
        HashMap<String, Object> dic = msgBody.dic;
        if (!dic.containsKey(key)) return 0;
        return Convert.ToInt32(dic.get(key));
    }
    static public String GetMsgStringValueInList(String key, Msg_Body_Struct msgBody)
    {
        HashMap<String, Object> dic = msgBody.dic;
        if (!dic.containsKey(key)) return "";
        return Convert.ToString(dic.get(key));
    }
    static public double GetMsgDoubleValueInList(String key, Msg_Body_Struct msgBody)
    {
        HashMap<String, Object> dic = msgBody.dic;
        if (!dic.containsKey(key)) return 0;
        return Convert.ToDouble(dic.get(key));
    }*/
}
