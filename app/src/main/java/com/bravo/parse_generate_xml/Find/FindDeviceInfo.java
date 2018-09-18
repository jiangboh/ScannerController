package com.bravo.parse_generate_xml.Find;

import com.bravo.xml.FindMsgStruct;
import com.bravo.xml.Msg_Body_Struct;

/**
 * Created by admin on 2018-9-17.
 */

public class FindDeviceInfo {
    private final String TAG = "FindDeviceInfo";

    public static final int ON_LINE = 1;
    public static final int OFF_LINE = 0;

    private String Ip;
    private int Port;
    private String SN;
    private String Mode;
    private String FullName;
    private int iState = OFF_LINE;

    public String getIp() {
        return Ip;
    }

    public int getPort() {
        return Port;
    }

    public String getSN() {
        return SN;
    }

    public String getMode() {
        return Mode;
    }

    public String getFullName() {
        return FullName;
    }

    public int getiState() {
        return iState;
    }

    public static FindDeviceInfo xmlToBean(Msg_Body_Struct struct) {
        FindDeviceInfo target = new FindDeviceInfo();
        target.Port = FindMsgStruct.GetMsgIntValueInList("port",struct.dic,0);
        target.Ip = FindMsgStruct.GetMsgStringValueInList("ip",struct.dic,"");
        target.FullName = FindMsgStruct.GetMsgStringValueInList("fullname",struct.dic,"");
        target.SN = FindMsgStruct.GetMsgStringValueInList("sn",struct.dic,"");
        target.Mode = FindMsgStruct.GetMsgStringValueInList("mode",struct.dic,"");
        return target;
    }
}
