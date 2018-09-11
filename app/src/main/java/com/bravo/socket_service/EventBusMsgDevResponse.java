package com.bravo.socket_service;

import com.bravo.parse_generate_xml.udp.BTSOnline;

/**
 * Created by Jack.liao on 2017/11/14.
 */

public class EventBusMsgDevResponse {
    private String ipAddress;
    private int iPort;
    private BTSOnline btsOnline;

    public EventBusMsgDevResponse(String ipAddress, int iPort, BTSOnline btsOnline) {
        this.ipAddress = ipAddress;
        this.iPort = iPort;
        this.btsOnline = btsOnline;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getProt() {
        return iPort;
    }

    public BTSOnline getBtsOnline() {
        return btsOnline;
    }
}
