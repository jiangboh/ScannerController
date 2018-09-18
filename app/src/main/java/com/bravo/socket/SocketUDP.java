package com.bravo.socket;

import android.content.Context;

import com.bravo.parse_generate_xml.Find.FindDeviceInfo;
import com.bravo.utils.Logs;
import com.bravo.xml.Msg_Body_Struct;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;

import static com.bravo.xml.XmlCodec.DecodeApXmlMessage;

/**
 * Created by lenovo on 2016/12/22.
 */

public class SocketUDP {
    private boolean stopReceive = false;
    private final String TAG = "SocketUDP";
    private DatagramSocket socket;
    private Context context;
    private int port;

    public SocketUDP(Context context,int port) {
        this.context = context;
        this.port = port;
        try {
            //socket = new DatagramSocket(8001 + new Random().nextInt(1000));
            socket = new DatagramSocket(port);
            Logs.d(TAG,"启动UDP监听，监听端口：" + port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    /**
     * 发送UDP消息
     * @param ipAddress
     * @param serverPort
     * @param msg
     */
    public void send(final String ipAddress,final int serverPort,final String msg){
        new Thread(){
            @Override
            public void run() {
                try {
                    if (!msg.isEmpty()) {
//                      Log.d(TAG, "UDP send ip= " + ipAddress + ",Port=" + serverPort + ",data=" + msg);
                        byte[] buf = msg.getBytes();
                        InetAddress address = InetAddress.getByName(ipAddress);//服务器地址
                        //创建发送方的数据报信息(包的最大长度为64k)
                        DatagramPacket dataGramPacket = new DatagramPacket(buf, buf.length, address, serverPort);
                        socket.send(dataGramPacket);  //通过套接字发送数据
                    }
                    else
                    {
                        Logs.d(TAG,"发送UDP消息内容为空!");
                    }
                } catch (UnknownHostException e) {
                    Logs.d(TAG,"发送UDP消息异常：" + e.getMessage());
                    e.printStackTrace();
                } catch (SocketException e) {
                    Logs.d(TAG,"发送UDP消息异常：" + e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    Logs.d(TAG,"发送UDP消息异常：" + e.getMessage());
                    e.printStackTrace();
                }

            }
        }.start();
    }

    /**
     * 开始接收UDP消息
     */
    public void startReceive(){
        new Thread(){
            @Override
            public void run() {
                Logs.d(TAG,"启动UDP消息接收线程，监听端口：" + port);
                try {
                    byte data[] = new byte[8 * 1024];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    while(!stopReceive){
                        try {
                            socket.receive(packet);
                            //把接收到的data转换为String字符串
                            String result = new String(packet.getData(), packet.getOffset(), packet.getLength(),"UTF-8");
                            Logs.w(TAG, "接收到的UDP数据为：" + result,"receivedUdpData",true,true);
                            try {
                                HandleMsg(packet.getAddress().getHostAddress(),packet.getPort(),result);
                            } catch (Exception e) {
                                Logs.e(TAG, "处理收到的消息出错：" + e.getMessage());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Logs.d(TAG, "接收UDP消息异常：" + e.getMessage());
                        }
                    }
                    socket.close();
                    socket = null;
                } catch (Exception e) {
                    Logs.d(TAG,"接收UDP消息异常：" + e.getMessage());
                    e.printStackTrace();
                    socket.close();
                    socket = null;
                }
            }
        }.start();
    }

    private void HandleMsg(String ip,int port,String result)
    {
        Msg_Body_Struct msg = DecodeApXmlMessage(result);
        if (msg == null) return;

        Logs.d(TAG,"接收消息id：" + msg.msgId);
        Logs.d(TAG,"接收消息类型：" + msg.type);
        for (Map.Entry<String, Object> kvp : msg.dic.entrySet())
        {
            Logs.d(TAG,"接收消息内容=" + kvp.getKey() + "；值=" + kvp.getValue());
        }

        if (msg.type.equalsIgnoreCase(Msg_Body_Struct.BroadCast_result))
        {
            FindDeviceInfo fdi = FindDeviceInfo.xmlToBean(msg);
            if (fdi != null)
                EventBus.getDefault().post(fdi);
        }
        else
        {
            Logs.e(TAG,String.format("消息类型(%s)为不支持的消息类型！",msg.type));
        }
        /*TargetAttach ta = TargetAttach.xmlToBean(msg);
                        if (ta != null)
                            EventBus.getDefault().post(ta);*/



       // EventBus.getDefault().post(new EventBusMsgDevResponse(dp.getAddress().getHostAddress(), dp.getPort(), btsOnline));
    }

    /**
     *@author Jack.liao
     *@name stopReceive
     *@return void
     **/
    public void stopReceive(){
        Logs.d(TAG,"暂停UDP消息接，监听端口：" + port);
        stopReceive = true;
    }



}
