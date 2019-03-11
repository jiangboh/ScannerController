package com.bravo.socket;

import android.content.Context;

import com.bravo.socket_service.EventBusMsgRecvXmlMsg;
import com.bravo.utils.Logs;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

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
            Logs.d(TAG,"启动UDP监听，监听端口：" + port,true);
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
                        //String msg_utf8 = URLEncoder.encode(msg,"UTF-8");
                        //Logs.d(TAG, "UDP send ip= " + ipAddress + ",Port=" + serverPort + ",data=" + msg);
                        byte[] buf = msg.getBytes("utf-8");
                        InetAddress address = InetAddress.getByName(ipAddress);//服务器地址
                        //创建发送方的数据报信息(包的最大长度为64k)
                        //String str = new String(buf, 0, buf.length, "utf-8");
                        //Logs.d(TAG, "发送消息:" + str);
                        DatagramPacket dataGramPacket = new DatagramPacket(buf, buf.length, address, serverPort);
                        socket.send(dataGramPacket);  //通过套接字发送数据
                    }
                    else
                    {
                        Logs.e(TAG,"发送UDP消息内容为空!",true);
                    }
                } catch (UnknownHostException e) {
                    Logs.e(TAG,"发送UDP消息异常：" + e.getMessage(),true);
                    e.printStackTrace();
                } catch (SocketException e) {
                    Logs.e(TAG,"发送UDP消息异常：" + e.getMessage(),true);
                    e.printStackTrace();
                } catch (IOException e) {
                    Logs.e(TAG,"发送UDP消息异常：" + e.getMessage(),true);
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
                Logs.d(TAG,"启动UDP消息接收线程，监听端口：" + port,true);
                try {
                    byte data[] = new byte[8 * 1024];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    while(!stopReceive){
                        try {
                            socket.receive(packet);
                            //把接收到的data转换为String字符串
                            String result = new String(packet.getData(), packet.getOffset(), packet.getLength(),"utf-8");
                            //Logs.w(TAG, "接收到的UDP数据为：" + result,true,true);
                            //EventBus.getDefault().post(new EventBusMsgSendUDPMsg(packet.getAddress().getHostAddress(),packet.getPort(),result));
                            EventBus.getDefault().post(new EventBusMsgRecvXmlMsg(packet.getAddress().getHostAddress(),packet.getPort(),result));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Logs.d(TAG, "接收UDP消息异常：" + e.getMessage(),true);
                        }
                    }
                    socket.close();
                    socket = null;
                } catch (Exception e) {
                    Logs.d(TAG,"接收UDP消息异常：" + e.getMessage(),true);
                    e.printStackTrace();
                    socket.close();
                    socket = null;
                }
            }
        }.start();
    }



    /**
     *@author Jack.liao
     *@name stopReceive
     *@return void
     **/
    public void stopReceive(){
        Logs.d(TAG,"暂停UDP消息接，监听端口：" + port,true);
        stopReceive = true;
    }



}
