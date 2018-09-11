package com.bravo.socket;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lenovo on 2016/12/23.
 */

public class ProtocolStartAndEndTag {

    private ArrayList<String> tcpHeader = new ArrayList<String>();
    private ArrayList<String> udpHeader = new ArrayList<String>();
    private HashMap<String,String> correspondEnd = new HashMap<String,String>();

    public ProtocolStartAndEndTag(){
        udpHeader.add("<bts-online");
        correspondEnd.put("<bts-online","</bts-online>");

        udpHeader.add("<register-client");
        correspondEnd.put("<register-client","</register-client>");

        udpHeader.add("<set-config");
        correspondEnd.put("<set-config","</set-config>");

        udpHeader.add("<unregister-client");
        correspondEnd.put("<unregister-client","</unregister-client>");

        udpHeader.add("<action-response");
        correspondEnd.put("<action-response","</action-response>");

        udpHeader.add("<ex-config-rsp");
        correspondEnd.put("<ex-config-rsp","</ex-config-rsp>");

        udpHeader.add("<ex-status-notif");
        correspondEnd.put("<ex-status-notif","</ex-status-notif>");

        /*以下是tcp协议的头和尾*/
        /*tcpHeader.add("<cell-scan-req");
        correspondEnd.put("<cell-scan-req","</cell-scan-req>");*/

        tcpHeader.add("<cell-scan-notif");
        correspondEnd.put("<cell-scan-notif","</cell-scan-notif>");

        /*tcpHeader.add("<bcast-start-req");
        correspondEnd.put("<bcast-start-req","</bcast-start-req>");*/

        tcpHeader.add("<bcast-start-res");
        correspondEnd.put("<bcast-start-res","</bcast-start-res>");

        /*tcpHeader.add("<target-list-req");
        correspondEnd.put("<target-list-req","</target-list-req>");*/

        tcpHeader.add("<conn-request-notif");
        correspondEnd.put("<conn-request-notif","</conn-request-notif>");

        tcpHeader.add("<auth-request-notif");
        correspondEnd.put("<auth-request-notif","</auth-request-notif>");

        /*tcpHeader.add("<do-auth-req");
        correspondEnd.put("<do-auth-req","</do-auth-req>");*/

        tcpHeader.add("<target-attach-notif");
        correspondEnd.put("<target-attach-notif","</target-attach-notif>");

        tcpHeader.add("<target-position-notif");
        correspondEnd.put("<target-position-notif","</target-position-notif>");

        tcpHeader.add("<release-target");
        correspondEnd.put("<release-target","</release-target>");

        tcpHeader.add("<target-detach-notif");
        correspondEnd.put("<target-detach-notif","</target-detach-notif>");

        /*tcpHeader.add("<silent-call-req");
        correspondEnd.put("<silent-call-req","</silent-call-req>");*/

        tcpHeader.add("<silent-call-res");
        correspondEnd.put("<silent-call-res","</silent-call-res>");

        /*tcpHeader.add("<target-redir-req");
        correspondEnd.put("<target-redir-req","</target-redir-req>");*/

        tcpHeader.add("<target-redir-res");
        correspondEnd.put("<target-redir-res","</target-redir-res>");

        /*tcpHeader.add("<send-sms-req");
        correspondEnd.put("<send-sms-req","</send-sms-req>");*/

        tcpHeader.add("<send-sms-res");
        correspondEnd.put("<send-sms-res","</send-sms-res>");

        tcpHeader.add("<param-change-res");
        correspondEnd.put("<param-change-res","</param-change-res>");

        /*tcpHeader.add("<bcast-end-req");
        correspondEnd.put("<bcast-end-req","</bcast-end-req>");*/

        tcpHeader.add("<bcast-end-res");
        correspondEnd.put("<bcast-end-res","</bcast-end-res>");

        tcpHeader.add("<bts-gps-notif");
        correspondEnd.put("<bts-gps-notif","</bts-gps-notif>");

        tcpHeader.add("<error-notif");
        correspondEnd.put("<error-notif","</error-notif>");

        tcpHeader.add("<status-notif");
        correspondEnd.put("<status-notif","</status-notif>");

        tcpHeader.add("<switch-tech-res");
        correspondEnd.put("<switch-tech-res","</switch-tech-res>");

        tcpHeader.add("<cs-fallback-res");
        correspondEnd.put("<cs-fallback-res","</cs-fallback-res>");

    }

    public ArrayList<String> getTcpHeaders(){
        return tcpHeader;
    }

    public ArrayList<String> getUdpHeaders(){
        return udpHeader;
    }

    public HashMap<String,String> getCorrespondEnd(){
        return correspondEnd;
    }

    public String getEndTag(String headTag){
        return correspondEnd.get(headTag);
    }
}
