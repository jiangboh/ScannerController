package com.bravo.socket_service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.FemtoController.TranslucentActivity;
import com.bravo.parse_generate_xml.ErrorNotif;
import com.bravo.parse_generate_xml.udp.UnregisterClient;
import com.bravo.socket.SocketTCP;
import com.bravo.socket.SocketUDP;
import com.bravo.socket.udpBroadCast;
import com.bravo.utils.Logs;
import com.bravo.utils.SharePreferenceUtils;
import com.bravo.utils.SimpleDateUtils;
import com.bravo.wifi.WifiAP;
import com.bravo.wifi.WifiAdmin;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

/**
 * Created by lenovo on 2016/12/28.
 */

public class CommunicationService extends Service {
    public final String TAG = "CommunicationService";
    private SocketUDP socketUdp;
    private SocketTCP socketTCP;
    private udpBroadCast udpBroadCast;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        socketUdp = new SocketUDP(this);
        socketUdp.startReceive();
        socketTCP = new SocketTCP(this);
        udpBroadCast = new udpBroadCast(this);
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        //错误警告测试
//        errorMsgTest();
    }

    private void errorMsgTest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0,j = 0;
                while(true){
                    ErrorNotif en = new ErrorNotif();
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(i >= 2){
                        i = 0;
                        j ++;
                        if(j > 3){
                            break;
                           // j = 0;
                        }
                    }
                    i++;
                    en.setIpAddress("" + j);
                    en.setName("error test" + j);
                    en.setErrorCode("" + i);
                    en.setDetails("错误信息"+i);
                    en.setTime(SimpleDateUtils.formatTime("yyyy-MM-dd HH:mm:ss"));
                    EventBus.getDefault().post(en);

                }
            }
        }).start();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void sendUdpMsg(EventBusMsgSendUDPMsg msgType){
        Logs.w(TAG, "IP=" + msgType.getIpAddress() + ",port=" + msgType.getPort() + "发送的UDP数据为：" + msgType.toString(),"Record_Event",true,true);
        if (socketUdp != null) {
            socketUdp.send(msgType.getIpAddress(), msgType.getPort(), msgType.getMsg());
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void sendTcpMsg(EventBusMsgSendTCPMsg msgType){
        Logs.w(TAG, "IP=" + msgType.getIpAddress() + ",port=" + msgType.getPort()+ ",发送的TCP数据为：" + msgType.toString(),"Record_Event",true,true);
        socketTCP.sendData(msgType.getIpAddress(),msgType.getPort(),msgType.getMsg());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void sendUdpBroadcast(EventBusMsgSendUDPBroadcastMsg msgType){
        Logs.w(TAG, "IP=" + msgType.getIpAddress() + ",port=" + msgType.getPort()+ ",发送的UDP广播数据为：" + msgType.toString(),"Record_Event",true,true);
        udpBroadCast.sendBroadMsg(msgType.getIpAddress(),msgType.getPort(),msgType.getMsg());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void stopService(String strState) {
        switch (strState) {
            case EventBusMsgConstant.STOP_SERVICE:
                stopSelf();
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void unregisteSocket(EventBusMsgCloseSocket closeSocket) {
        HashMap<String, Socket> sockets = socketTCP.getSockets();
        Socket socket = sockets.get(closeSocket.getIpAddress());
        if (!TextUtils.isEmpty(((ProxyApplication)getApplicationContext()).getCurSocketAddress())
            && closeSocket.getIpAddress().equals(((ProxyApplication)getApplicationContext()).getCurSocketAddress())) {
            ((ProxyApplication)getApplicationContext()).setCurSocket(null);
        }
        if(socket == null) {
            UnregisterClient unregister = new UnregisterClient();
            String unregisteXml = UnregisterClient.toXml(unregister);
            sendUdpMsg( new EventBusMsgSendUDPMsg(closeSocket.getIpAddress(),closeSocket.getiUdpProt(),unregisteXml));
        } else {
            unregiste(socket, closeSocket.getiUdpProt());
        }
        EventBus.getDefault().post(EventBusMsgConstant.UNREGISTE_SOCKET_SUCCESS);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void unregisteAllSocket(String unregisteAllTag) {
        if(EventBusMsgConstant.UNREGISTE_ALL_SOCKET.equals(unregisteAllTag)){
            ((ProxyApplication)getApplicationContext()).setCurSocket(null);
            HashMap<String, Socket> sockets = socketTCP.getSockets();
            if(sockets.size()>0){
                for(Map.Entry<String, Socket> entry:sockets.entrySet()){
                    Socket socket = entry.getValue();
                    unregiste(socket, 8021);
                    unregiste(socket, 8031);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void clearAllSocket(String unregisteAllTag) {
        if(EventBusMsgConstant.CLEAR_ALL_SOCKET.equals(unregisteAllTag)){
           socketTCP.stopAllSockets();
        }
    }

//    @Subscribe(threadMode = ThreadMode.BACKGROUND)
//    public void unregisteSocket(ActionResponse ar) {
//        Logs.d(TAG,"接收到注册或注销响应" + ar.toString());
//    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void socketDisconnect(final EventBusMsgSocketDisconnect eventBusMsgSocketDisconnect) {
        if (SharePreferenceUtils.getInstance(this).getString("password", "").equals("Test")) {
            return;
        }
        WifiAdmin mWifiAdmin = new WifiAdmin(this);
        WifiAP mWifiAP = new WifiAP(this);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mWifiAdmin.isWifiConnected() || mWifiAP.isApEnabled() ||
                (mBluetoothAdapter.isEnabled() && mBluetoothAdapter.getProfileConnectionState(5) == BluetoothProfile.STATE_CONNECTED)) {
            Intent intent = new Intent(this, TranslucentActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("dialogType", TranslucentActivity.DIALOG_TYPE_ONE);
            intent.putExtra("title", "Hint");
            if(EventBusMsgConstant.TCP_SOCKET_DISCONNECT.equals(eventBusMsgSocketDisconnect.getType())){
                if (eventBusMsgSocketDisconnect.getIpAddress().equals(((ProxyApplication)getApplicationContext()).getCurSocketAddress())) {//前台断开
                    intent.putExtra("ipAddress", eventBusMsgSocketDisconnect.getIpAddress());
                    intent.putExtra("port", eventBusMsgSocketDisconnect.getPort());
                    intent.putExtra("dialogType", TranslucentActivity.DIALOG_TYPE_DISCONNECT);
                } else {//后台断开

                }
                intent.putExtra("message", eventBusMsgSocketDisconnect.getIpAddress() + "," + eventBusMsgSocketDisconnect.getPort() + " Disconnected");
            }else if(EventBusMsgConstant.REGISTER_FAILED.equals(eventBusMsgSocketDisconnect.getType())){
                intent.putExtra("message", eventBusMsgSocketDisconnect.getIpAddress() + "," + eventBusMsgSocketDisconnect.getPort() + " Register failed");
                ((ProxyApplication)getApplicationContext()).setCurSocket(null);
            }
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, TranslucentActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("dialogType", TranslucentActivity.DIALOG_TYPE_WIFI_CLOSE);
            intent.putExtra("title", "Hint");
            intent.putExtra("message", "WIFI or Bluetooth Disconnected");
            startActivity(intent);
        }
    }
    /**
     * @param socket
     */
    public void unregiste(Socket socket, int iUdpPort) {
        UnregisterClient unregister = new UnregisterClient();
        String unregisteXml = UnregisterClient.toXml(unregister);
        EventBusMsgSendUDPMsg unRegiste = new EventBusMsgSendUDPMsg(socket.getInetAddress().getHostAddress(), iUdpPort, unregisteXml);
        sendUdpMsg(unRegiste);
        if(socket != null){
            try {
                sleep(10);
                socketTCP.stopSocket(socket.getInetAddress().getHostAddress(),socket.getPort());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        unregisteAllSocket(EventBusMsgConstant.UNREGISTE_ALL_SOCKET);
        if(socketUdp != null){
            socketUdp.stopReceive();
            socketUdp = null;
        }
        if(socketTCP != null){
            socketTCP.stopAllSockets();
            socketTCP = null;
        }
        if (udpBroadCast != null) {
            udpBroadCast.closeMS();
            udpBroadCast = null;
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
