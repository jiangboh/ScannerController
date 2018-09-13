package com.bravo.socket;

import android.content.Context;

import com.bravo.utils.Logs;
import com.bravo.utils.Utils;
import com.bravo.xml.Msg_Body_Struct;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Map;

import static com.bravo.xml.XmlCodec.DecodeApXmlMessage;
import static com.bravo.xml.XmlCodec.EncodeApXmlMessage;

/**
 * Created by user on 15-5-4.
 */

/* 发送udp多播 */
public  class udpBroadCast extends Thread {
    private final static String TAG = "udpBroadCast";
    private int port;
    private MulticastSocket ms = null;
    private DatagramPacket dp = null;
    private InetAddress group = null;
    private boolean bThred = true;
    private Context context;
    public udpBroadCast(Context context,int port) {
        this.context = context;
        this.port = port;

        if (ms == null) {
            try {
                //ms = new MulticastSocket(7001 + new Random().nextInt(1000));
                ms = new MulticastSocket(port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.start();
        }
    }

    public void sendBroadMsg(String dataString) {
        byte[] buf = dataString.getBytes();
        //创建发送方的数据报信息(包的最大长度为64k)
        try {
            String strIP = Utils.getWifiBroadcastIp(context);
            group = InetAddress.getByName(strIP);
            ms.send(new DatagramPacket(buf, buf.length ,group, port));//通过套接字发送数据
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] data = new byte[1024];
        dp = new DatagramPacket(data,data.length);
        //WifiAP wifiAP = new WifiAP(context);
        while (bThred) {
            try {
                if (ms != null) {
                    ms.receive(dp);
                    if (dp.getAddress() != null) {
                        String result = new String(dp.getData(), dp.getOffset(), dp.getLength(), "UTF-8");
                        //BTSOnline btsOnline = BTSOnline.xmlToBean(result);
                        Logs.w(TAG, "接收到的UDP广播数据为：" + result);

                        Msg_Body_Struct msg = DecodeApXmlMessage(result);
                        if (msg == null) continue;

                        Logs.d(TAG,"接收消息id：" + msg.msgId);
                        Logs.d(TAG,"接收消息类型：" + msg.type);
                        for (Map.Entry<String, Object> kvp : msg.dic.entrySet())
                        {
                            Logs.d(TAG,"接收消息内容=" + kvp.getKey() + "；值=" + kvp.getValue());
                        }
                        sendBroadMsg(EncodeApXmlMessage(msg.msgId+1,msg));

                        //wifiAP.StartPing(dp.getAddress().getHostAddress());
                        //EventBus.getDefault().post(new EventBusMsgDevResponse(dp.getAddress().getHostAddress(), dp.getPort(), btsOnline));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ms.close();
        ms = null;
    }

    public void closeMS() {
        bThred = false;
        ms.close();
        ms = null;
    }
}