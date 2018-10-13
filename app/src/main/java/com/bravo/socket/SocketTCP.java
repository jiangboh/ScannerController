package com.bravo.socket;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.FemtoController.TranslucentActivity;
import com.bravo.database.SnifferHistory;
import com.bravo.database.TargetUser;
import com.bravo.database.TargetUserDao;
import com.bravo.database.User;
import com.bravo.database.UserDao;
import com.bravo.femto.BcastCommonApi;
import com.bravo.parse_generate_xml.ErrorNotif;
import com.bravo.parse_generate_xml.ReleaseTarget;
import com.bravo.parse_generate_xml.Status;
import com.bravo.parse_generate_xml.TargetDetach;
import com.bravo.parse_generate_xml.auth_request.AuthRequestNotif;
import com.bravo.parse_generate_xml.bcast_end.BcastEndRes;
import com.bravo.parse_generate_xml.bcast_start.BcastStartRes;
import com.bravo.parse_generate_xml.cell_scan.CellScanCell;
import com.bravo.parse_generate_xml.cell_scan.CellScanNotif;
import com.bravo.parse_generate_xml.cell_scan.CellScanTechSpecific;
import com.bravo.parse_generate_xml.conn_request.ConnRequestNotif;
import com.bravo.parse_generate_xml.cs_fallback.CsFallbackRes;
import com.bravo.parse_generate_xml.do_auth.DoAuthAuthParams;
import com.bravo.parse_generate_xml.do_auth.DoAuthReq;
import com.bravo.parse_generate_xml.do_auth.DoAuthTechSpecific;
import com.bravo.parse_generate_xml.parameter_change.ParameterChangeRes;
import com.bravo.parse_generate_xml.send_sms.SendSmsRes;
import com.bravo.parse_generate_xml.silent_call.SilentCallRes;
import com.bravo.parse_generate_xml.switch_tech.SwitchTechRes;
import com.bravo.parse_generate_xml.target_attach.TargetAttach;
import com.bravo.parse_generate_xml.target_position.TargetPosition;
import com.bravo.parse_generate_xml.target_redirect.TargetRedirectRes;
import com.bravo.parse_generate_xml.udp.RegisterClient;
import com.bravo.socket_service.EventBusMsgCloseSocket;
import com.bravo.socket_service.EventBusMsgConstant;
import com.bravo.socket_service.EventBusMsgSendUDPMsg;
import com.bravo.socket_service.EventBusMsgSocketDisconnect;
import com.bravo.utils.FileUtils;
import com.bravo.utils.Logs;
import com.bravo.utils.SharePreferenceUtils;
import com.bravo.utils.SimpleDateUtils;
import com.bravo.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.bravo.femto.BcastCommonApi.updateBcastInfoEndTime;

/**
 * Created by lenovo on 2016/12/22.
 */

public class SocketTCP {
    private final String TAG = "SocketTCP";
    private final String EXCEPTION_FILE_NAME = "TcpSocketException";
    private Context context;
    private int socketBufLen = 32 * 1024;
    private static HashMap<String, Socket> sockets = new HashMap<String, Socket>();
    private HashMap<Socket, SocketReceiverThread> threads = new HashMap<Socket, SocketReceiverThread>();
    private HashMap<String, String> lostModifyFilePaths = new HashMap<String, String>();

    public SocketTCP(Context context) {
        this.context = context;
    }

    public void stopSocket(String ipAddress,int port){
        String strBTS = SharePreferenceUtils.getInstance(context).getString("status_notif_bts" + ipAddress + port, "1");
        Logs.d(TAG,"stopSocket ip=" + ipAddress + ",bts=" + strBTS, "Record_Event", true);
        if (strBTS.equals("3") || strBTS.equals("4")) {
            updateBcastInfoEndTime(context, false);
        }
        lostModifyFilePaths.remove(ipAddress);
        Socket socket = sockets.get(ipAddress + port);
        SocketTCP.SocketReceiverThread thread = threads.get(socket);
        if(thread != null){
            thread.onStop();
            threads.remove(socket);
        }
        if(socket != null){
            try {
//                if (socket.getInetAddress().getHostAddress().equals(((ProxyApplication)context.getApplicationContext()).getCurSocketAddress())) {
//                    ((ProxyApplication)context.getApplicationContext()).setCurSocket(null);
//                }
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Logs.d(TAG,"close Socket exception=" + e.getMessage(),EXCEPTION_FILE_NAME,true);
            }
            sockets.remove(ipAddress + port);
        }
        Utils.deleteErrorsByIp(ipAddress);
    }

    public void stopAllSockets() {
        for (Map.Entry<Socket, SocketReceiverThread> entry : threads.entrySet()) {
            stopSocket(entry.getKey().getInetAddress().getHostAddress(),entry.getKey().getPort());
        }
        threads.clear();
        sockets.clear();
        lostModifyFilePaths.clear();
    }

    public static HashMap<String, Socket> getSockets(){
        return sockets;
    }

    public void sendData(String ipAddress, int port, String msg) {
        Socket socket = sockets.get(ipAddress + port);
        if(socket == null){
            registe(ipAddress,port,msg);
        }else{
            ((ProxyApplication)context.getApplicationContext()).setCurSocket(socket);
            if(TextUtils.isEmpty(msg)){
                EventBus.getDefault().post(EventBusMsgConstant.TCP_CONNECT_ALREADY);
            }else{
                try {
//                    Logs.d(TAG, "Send TCP data = " + msg);
                    OutputStream os = socket.getOutputStream();
                    os.write(msg.getBytes());
                    os.flush();
                } catch (IOException e) {
                    Logs.e(TAG, "send tcp data exception=" + e.getMessage(),EXCEPTION_FILE_NAME,true);
                    sendDisconntctMsg(ipAddress, socket.getPort());
                }
            }
        }
    }

    private void initSocket(String ipAddress, int port, String str){
        try {
            Socket socket = new Socket(ipAddress, port);
            socket.setSoTimeout(20000);
            socket.setReceiveBufferSize(socketBufLen);
            socket.setSendBufferSize(socketBufLen);
            socket.setKeepAlive(true);
            sockets.put(ipAddress + port, socket);

            SocketReceiverThread thread = new SocketReceiverThread(socket,ipAddress);
            threads.put(socket, thread);
            thread.start();
            if (!EventBusMsgConstant.TCP_RECONNECT_REQUEST.equals(str)) {
                if (!TextUtils.isEmpty(str)) {
                    sendData(ipAddress, port, str);//发送需要发送的消息
                }
            }
            EventBus.getDefault().post(EventBusMsgConstant.TCP_CONNECT_SUCCESS);
            ((ProxyApplication)context.getApplicationContext()).setCurSocket(socket);
        } catch (IOException e) {
            e.printStackTrace();
            stopSocket(ipAddress,port);
            if (ipAddress.equals(((ProxyApplication)context.getApplicationContext()).getCurSocketAddress())) {
                ((ProxyApplication)context.getApplicationContext()).setCurSocket(null);
            }
            EventBus.getDefault().post(EventBusMsgConstant.TCP_CONNECT_FAILED);
            Logs.e(TAG, "init Socket exception=" + e.getMessage(),EXCEPTION_FILE_NAME,true);
        }
    }

    private void unRegiste(String ipAddress){
        EventBus.getDefault().post(new EventBusMsgCloseSocket(ipAddress, ((ProxyApplication)context.getApplicationContext()).getiUdpPort()));
    }

    private void registe(final String ipAddress, final int port, final String str){
        SharePreferenceUtils.getInstance(context).delete("registe");
        unRegiste(ipAddress);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //开启Tcp注册用户
        RegisterClient register = new RegisterClient();
        register.setClientType(0 + "");//
        register.setCurrentTime(System.currentTimeMillis() / 1000);//根据需要的格式转换,建议使用SimpleDateUtils转换格式
        String registeXml = RegisterClient.toXml(register);
        EventBusMsgSendUDPMsg udpMsg = new EventBusMsgSendUDPMsg(ipAddress,((ProxyApplication)context.getApplicationContext()).getiUdpPort(),registeXml);
        EventBus.getDefault().post(udpMsg);

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Logs.d(TAG,"执行timer注册！！！");
                if("SUCCESS".equals(SharePreferenceUtils.getInstance(context).getString("registe",null))){
                    Logs.d(TAG,"注册成功！！！");
                    initSocket(ipAddress,port,str);
                }else{
                    EventBus.getDefault().post(EventBusMsgConstant.REGISTER_FAILED);
                    EventBusMsgSocketDisconnect ebmsd = new EventBusMsgSocketDisconnect(ipAddress, port,EventBusMsgConstant.REGISTER_FAILED);
                    EventBus.getDefault().post(ebmsd);
                    if (EventBusMsgConstant.TCP_RECONNECT_REQUEST.equals(str)) {
                        Intent intent = new Intent(context, TranslucentActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("dialogType", TranslucentActivity.DIALOG_TYPE_ONE);
                        intent.putExtra("title", "Hint");
                        ((ProxyApplication) context.getApplicationContext()).setCurSocket(null);
                        context.startActivity(intent);
                    }
                }
                timer.purge();
                timer.cancel();
            }
        },2000);
    }

    public class SocketReceiverThread extends Thread {

        private boolean stop = false;
        private Socket socket;
        private String ipAddress;

        public SocketReceiverThread(Socket socket,String ipAddress) {
            this.socket = socket;
            this.ipAddress = ipAddress;
        }

        public void onStop() {
            stop = true;
        }

        @Override
        public void run() {
            int length,count = 0;
            byte[] buffer = new byte[socketBufLen];
            InputStream is = null;
            try {
                is = socket.getInputStream();
                while (!stop) {
                    length = is.read(buffer);
                    Logs.w(TAG, "读到数据长度=" + length);
                    if (length == -1) {
                        Logs.e(TAG, "读到-1",EXCEPTION_FILE_NAME,true);
                        count++;
                        if(count >= 5){
                            count = 0;
                            sendDisconntctMsg(ipAddress, socket.getPort());
                            return;
                        }
                    }else if (length > 0) {
                        count = 0;
                        String temp = new String(buffer, 0, length);
                        Logs.w(TAG, "receive TCP data=" + temp,"receivedTcpData",true,true);
                        List<String> ss  = parseUnabridgedString(temp,ipAddress,new ArrayList<String>());
                        for(String s : ss){
                            Logs.w(TAG, "parse TCP data=" + s,"receivedTcpData",true,true);
                            String str = parseXml(socket.getInetAddress().getHostAddress(), socket.getPort(), s);
                        }
                    }
                }
                is.close();
            } catch (Exception e) {
                Logs.e(TAG, "receive tcp exception=" + e.getMessage(),EXCEPTION_FILE_NAME,true);
                sendDisconntctMsg(ipAddress, socket.getPort());
            }
        }
    }

    private void sendDisconntctMsg(String ipAddress, int iPort) {
        stopSocket(ipAddress,iPort);
        EventBusMsgSocketDisconnect ebmsd = new EventBusMsgSocketDisconnect(ipAddress, iPort, EventBusMsgConstant.TCP_SOCKET_DISCONNECT);
        EventBus.getDefault().post(ebmsd);
    }

    public List<String> parseUnabridgedString(String temp,String ipAddress,List<String> unabridgedStrs){
        ProtocolStartAndEndTag startAndEndTag = new ProtocolStartAndEndTag();
        ArrayList<String> headers = startAndEndTag.getTcpHeaders();
        HashMap<String, String> correspondEnds = startAndEndTag.getCorrespondEnd();
        boolean haveNoHeaderAndEndTag = true;
        for (String header : headers) {
            String endTag = correspondEnds.get(header);
            if (temp.contains(header) && temp.contains(endTag)) {
//                Logs.d(TAG, "匹配上的header == " + header + "  endTag == " + endTag);
                haveNoHeaderAndEndTag = false;
                String str = temp;
                while(str.contains(header) && str.contains(endTag)){
                    int headerIndex = str.indexOf(header);
                    int endIndex = str.indexOf(endTag,headerIndex+1) + endTag.length();
                    if(endIndex < headerIndex){
                        //当数据是由同一个类型的一条数据的后半部分加另一条数据的前半部分组成时
                        String startPartStr = str.substring(0,str.indexOf(endTag) + endTag.length());
                        String endPartStr = str.substring(headerIndex);
                        parseUnabridgedString(startPartStr,ipAddress,unabridgedStrs);
                        parseUnabridgedString(endPartStr,ipAddress,unabridgedStrs);
                        str = "";
                        break;
                    }else{
                        String unabridgedStr = str.substring(headerIndex, endIndex);
                        unabridgedStrs.add(unabridgedStr);
                        str = str.substring(0,headerIndex) + str.substring(endIndex);
                    }
                }
                if (!TextUtils.isEmpty(str.trim())) {
                    Logs.d(TAG, "剩余的数据为:" + str.trim());
                }
                temp = str.trim();
                if(!TextUtils.isEmpty(temp)){
                    parseUnabridgedString(temp,ipAddress,unabridgedStrs);
                }
                break;
            } else if (temp.contains(header) && !temp.contains(endTag)) {
                Logs.d(TAG, "匹配上的header == " + header);
                haveNoHeaderAndEndTag = false;
                File file = new File(new FileUtils(context).getFileCacheDir(), header+ipAddress);
                if (file.exists()) {
                    file.delete();
                }
                lostModifyFilePaths.put(ipAddress,file.getAbsolutePath());
                String headerString = temp.substring(temp.indexOf(header));
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    Logs.w(TAG, "写到文件的前段数据为：" + headerString);
                    fos.write(headerString.getBytes());
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (!temp.contains(header) && temp.contains(endTag)&&!TextUtils.isEmpty(temp)) {
                Logs.d(TAG, "匹配上的endTag == " + endTag + "  header == " + header);
                haveNoHeaderAndEndTag = false;
                String endString = temp.substring(0, temp.indexOf(endTag) + endTag.length());
                File file = new File(new FileUtils(context).getFileCacheDir(), header+ipAddress);;
                if (file.exists()&&file.isFile()&&file.length()>0) {
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file, true);
                        Logs.w(TAG, "写到文件的尾段数据为：" + endString);
                        fos.write(endString.getBytes());
                        fos.flush();
                        fos.close();
                        String unabridgedStr = new FileUtils(context).readTextInputStream(new FileInputStream(file));
                        Logs.d(TAG,"从File中读出来的数据为："+unabridgedStr);
                        unabridgedStrs.add(unabridgedStr);
                        lostModifyFilePaths.remove(ipAddress);
                        file.delete();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        /*不包含任何头尾标签，并且有接收到包含前半部分的数据，
        并且接收到的数据不为空，则续写到包含前半部分的文件中*/
        if(haveNoHeaderAndEndTag && lostModifyFilePaths.get(ipAddress) != null) {
            File file = new File(lostModifyFilePaths.get(ipAddress));
            if (file.exists()&&file.isFile()&&file.length()>0) {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file, true);
                    Logs.w(TAG, "写到文件的中段数据为：" + temp);
                    fos.write(temp.getBytes());
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return unabridgedStrs;
    }


    public String parseXml(String ipAddress,int port, String str) {
        String strParseResult = "";
        //服务器的socket
        Socket curSocket = sockets.get(ipAddress + port);
        if (str.startsWith("<auth-request-notif")) {//
            AuthRequestNotif arn = AuthRequestNotif.xmlToBean(str);
            String msg = ParserHss(arn);
            if (!TextUtils.isEmpty(msg)) {
                try {
                    Logs.w(TAG, "IP=" + ipAddress + ",port=" +curSocket.getPort() + ",send TCP data=" + msg ,"Record_Event",true,true);
                    OutputStream os = curSocket.getOutputStream();
                    os.write(msg.getBytes());
                    os.flush();
                } catch (IOException e) {
                    Logs.e(TAG, e.getMessage());
                }
            }
            return  arn.toString();
        } else if (str.startsWith("<conn-request-notif")) {
            ConnRequestNotif crn = ConnRequestNotif.xmlToBean(str);
            crn.setiAuth(0);
            crn.setiCount(1);
            //插入数据
            User insertData;
            String Unique = SharePreferenceUtils.getInstance(context).getString("status_notif_unique" + ipAddress + curSocket.getPort(), "");
            List<User> users = ProxyApplication.getDaoSession().getUserDao().queryBuilder().where(UserDao.Properties.Unique.eq(Unique), UserDao.Properties.SrtImsi.eq(crn.getImsi()),
                    UserDao.Properties.ConnTime.gt(SharePreferenceUtils.getInstance(context).getLong("status_notif_starttime" + ipAddress + curSocket.getPort(), System.currentTimeMillis()))).build().list();
            if (users.size() == 0) {
                int iAuth = 0;
                if (crn.getTechSpecific() == null || TextUtils.isEmpty(crn.getTechSpecific().getImei())) {
                    TargetUser targetUser = ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(TargetUserDao.Properties.StrImsi.eq(crn.getImsi())).build().unique();
                    if (targetUser !=  null) {
                        iAuth = 2;//存在target中
                    }
                    insertData = new User(null, crn.getImsi(), null, iAuth, false, 1, System.currentTimeMillis(), null, null, Unique);
                } else {
                    List<TargetUser> targetUsers = ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(
                            ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().or(
                                    TargetUserDao.Properties.StrImei.eq(crn.getTechSpecific().getImei()), TargetUserDao.Properties.StrImsi.eq(crn.getImsi()))).build().list();
                    if (targetUsers.size() > 0) {
                        iAuth = 2;
                    }
                    insertData = new User(null, crn.getImsi(), crn.getTechSpecific().getImei(), iAuth, false, 1, System.currentTimeMillis(), null, null, Unique);
                }
                crn.setiAuth(iAuth);
                Logs.d(TAG, "lmj target insert update-" + insertData.toString());
                ProxyApplication.getDaoSession().getUserDao().insert(insertData);
            } else {
                User updateData = users.get(0);
                if (crn.getTechSpecific() != null && !TextUtils.isEmpty(crn.getTechSpecific().getImei())) {
                    updateData.setStrImei(crn.getTechSpecific().getImei());
                }
                int iTotal = updateData.getICount() + 1;
                updateData.setConnTime(System.currentTimeMillis());
                updateData.setICount(iTotal);
                crn.setiCount(iTotal);
                ProxyApplication.getDaoSession().getUserDao().update(updateData);
            }
            if (ipAddress.equals(((ProxyApplication) context.getApplicationContext()).getCurSocketAddress())) {
                EventBus.getDefault().post(crn);
            }
            return crn.toString();
        } else if (str.startsWith("<target-attach-notif")) {
            try {
                BcastCommonApi.soundRing(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            TargetAttach ta = TargetAttach.xmlToBean(str);
            //update数据
            String Unique = SharePreferenceUtils.getInstance(context).getString("status_notif_unique" + ipAddress + curSocket.getPort(), "");
            List<User> users = ProxyApplication.getDaoSession().getUserDao().queryBuilder().orderDesc().where(UserDao.Properties.Unique.eq(Unique), UserDao.Properties.SrtImsi.eq(ta.getImsi()),
                    UserDao.Properties.ConnTime.gt(SharePreferenceUtils.getInstance(context).getLong("status_notif_starttime" + ipAddress + curSocket.getPort(), System.currentTimeMillis()))).build().list();
            if (users.size() == 0) {
                User insertData = new User(null, ta.getImsi(), ta.getImei(), 1, false, 1,System.currentTimeMillis(), System.currentTimeMillis(), null, Unique);
                ProxyApplication.getDaoSession().getUserDao().insert(insertData);
            } else{

                User updateData = users.get(0);
                updateData.setIAuth(1);
                updateData.setAttachTime(System.currentTimeMillis());
                if (!TextUtils.isEmpty(ta.getImei())) {
                    updateData.setStrImei(ta.getImei());
                }
                Logs.d(TAG, "lmj target attach update-" + updateData.toString());
                ProxyApplication.getDaoSession().getUserDao().update(updateData);
            }
            if (ipAddress.equals(((ProxyApplication) context.getApplicationContext()).getCurSocketAddress())) {
                EventBus.getDefault().post(ta);
            }
            return ta.toString();
        } else if (str.startsWith("<target-detach-notif")) {
            try {
                BcastCommonApi.soundRing(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            TargetDetach td = TargetDetach.xmlToBean(str);

            String Unique = SharePreferenceUtils.getInstance(context).getString("status_notif_unique" + ipAddress + curSocket.getPort(), "");
            List<User> users = ProxyApplication.getDaoSession().getUserDao().queryBuilder().orderDesc().where(UserDao.Properties.Unique.eq(Unique), UserDao.Properties.SrtImsi.eq(td.getImsi()),
                    UserDao.Properties.ConnTime.gt(SharePreferenceUtils.getInstance(context).getLong("status_notif_starttime" + ipAddress + curSocket.getPort(), System.currentTimeMillis()))).build().list();
            if (users.size() != 0) {
                User updateData = users.get(0);
                updateData.setIAuth(2);
                updateData.setDetachTime(System.currentTimeMillis());
                ProxyApplication.getDaoSession().getUserDao().update(updateData);
            } else {
                User insertData = new User(null, td.getImsi(), null, 2, false, 1, null, null,  System.currentTimeMillis(), Unique);
                ProxyApplication.getDaoSession().getUserDao().insert(insertData);
            }
            try {
                BcastCommonApi.soundRing(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (ipAddress.equals(((ProxyApplication) context.getApplicationContext()).getCurSocketAddress())) {
                EventBus.getDefault().post(td);
            }
            return td.toString();
        } else if (str.startsWith("<status-notif")) {
            Status s = Status.xmlToBean(str);
           if (ipAddress.equals(((ProxyApplication) context.getApplicationContext()).getCurSocketAddress()) && curSocket.getPort() == ((ProxyApplication) context.getApplicationContext()).getiTcpPort()) {
                EventBus.getDefault().post(s);
            }
            //save status notif param lmj2017-06-15
            SharePreferenceUtils.getInstance(context).setString("status_notif_mode" + ipAddress + curSocket.getPort(), s.getMode());
            SharePreferenceUtils.getInstance(context).setString("status_notif_sn" + ipAddress + curSocket.getPort(), s.getFemtoSn());
            SharePreferenceUtils.getInstance(context).setString("status_notif_ver" + ipAddress + curSocket.getPort(), s.getFemtoVer());
            SharePreferenceUtils.getInstance(context).setString("status_notif_band" + ipAddress + curSocket.getPort(), s.getBand());
            SharePreferenceUtils.getInstance(context).setString("status_notif_tech" + ipAddress + curSocket.getPort(), s.getTech());
            SharePreferenceUtils.getInstance(context).setString("status_notif_channel" + ipAddress + curSocket.getPort(), s.getChannel());
            SharePreferenceUtils.getInstance(context).setString("status_notif_cid" + ipAddress + curSocket.getPort(), s.getCid());
            SharePreferenceUtils.getInstance(context).setString("status_notif_bts" + ipAddress + curSocket.getPort(), s.getBtsState());
            SharePreferenceUtils.getInstance(context).setString("status_notif_controller_ip" + ipAddress + curSocket.getPort(), s.getControllerClient());
            SharePreferenceUtils.getInstance(context).setString("status_notif_tech_capability" + ipAddress + curSocket.getPort(), s.getTechCapability());
            if (s.getBaterryCharging().equals("FALSE")) {
                SharePreferenceUtils.getInstance(context).setString("status_notif_battery_level" + ipAddress + curSocket.getPort(), "N/A");
            } else {
                SharePreferenceUtils.getInstance(context).setString("status_notif_battery_level" + ipAddress + curSocket.getPort(), s.getBaterryLevel());
            }
            if (s.getBtsState().equals("3")) {//bts=3时,存储唯一标识码,用户target存储
                String Unique = s.getFemtoSn() + s.getBand() + s.getTech() + s.getCid();//s.getChannel() +
                        SharePreferenceUtils.getInstance(context).setString("status_notif_unique" + ipAddress + curSocket.getPort(), Unique);
            }
            return s.toString();
        } else if (str.startsWith("<error-notif")) {
            ErrorNotif en = ErrorNotif.xmlToBean(str);
            en.setIpAddress(ipAddress);
            en.setName(((ProxyApplication)context.getApplicationContext()).getDevName(ipAddress));
            en.setTime(SimpleDateUtils.formatTime("yyyy-MM-dd HH:mm:ss"));
            EventBus.getDefault().post(en);
            return en.toString();
        }

        /////////////////////////////////////////////////////////////////////////////////////
        if (ipAddress.equals(((ProxyApplication) context.getApplicationContext()).getCurSocketAddress()) &&
                curSocket.getPort() == ((ProxyApplication) context.getApplicationContext()).getiTcpPort()) {
            if (str.startsWith("<cell-scan-notif")) {
                CellScanNotif scn = CellScanNotif.xmlToBean(str);
                EventBus.getDefault().post(scn);
                strParseResult = scn.toString();
                for (int i = 0; i < scn.getCells().size(); i++) {
                    CellScanCell cellScanCell = scn.getCells().get(i);
                    CellScanTechSpecific cellScanTechSpecific = cellScanCell.getTechSpecific();
                    SnifferHistory snifferHisroty = new SnifferHistory(null, System.currentTimeMillis(), scn.getChannel(),
                            SharePreferenceUtils.getInstance(context).getString("status_notif_band" + ipAddress + curSocket.getPort(), "N/A"),
                            SharePreferenceUtils.getInstance(context).getString("status_notif_tech" + ipAddress + curSocket.getPort(), "N/A"),
                            cellScanCell.getCid(), cellScanCell.getRncid(),cellScanCell.getMcc(), cellScanCell.getMnc(), cellScanTechSpecific.getBsic(),
                            cellScanTechSpecific.getRssi(), cellScanTechSpecific.getLac(), cellScanTechSpecific.getPsc(), cellScanTechSpecific.getRscp(),
                            cellScanTechSpecific.getBandwidth(), cellScanTechSpecific.getTac(), cellScanTechSpecific.getPci(),cellScanTechSpecific.getRsrp());
                    ProxyApplication.getDaoSession().getSnifferHistoryDao().insert(snifferHisroty);
                }
            } else if (str.startsWith("<bcast-start-res")) {
                BcastStartRes bsr = BcastStartRes.xmlToBean(str);
                if (bsr.getStatus().equals("SUCCESS")) {
                    SharePreferenceUtils.getInstance(context).setLong("status_notif_starttime" + ipAddress + curSocket.getPort(), System.currentTimeMillis());
                }
                EventBus.getDefault().post(bsr);
                strParseResult = bsr.toString();
            } else if (str.startsWith("<target-position-notif")) {
                TargetPosition tp = TargetPosition.xmlToBean(str);
                EventBus.getDefault().post(tp);
                return tp.toString();
            } else if (str.startsWith("<release-target")) {
                ReleaseTarget rt = ReleaseTarget.xmlToBean(str);
                EventBus.getDefault().post(rt);
                strParseResult = rt.toString();
            } else if (str.startsWith("<silent-call-res")) {
                SilentCallRes scr = SilentCallRes.xmlToBean(str);
                EventBus.getDefault().post(scr);
                strParseResult = scr.toString();
            } else if (str.startsWith("<target-redir-res")) {
                TargetRedirectRes trr = TargetRedirectRes.xmlToBean(str);
                EventBus.getDefault().post(trr);
                strParseResult = trr.toString();
            } else if (str.startsWith("<send-sms-res")) {
                SendSmsRes ssr = SendSmsRes.xmlToBean(str);
                EventBus.getDefault().post(ssr);
                strParseResult = ssr.toString();
            } else if (str.startsWith("<param-change-res")) {
                ParameterChangeRes pcr = ParameterChangeRes.xmlToBean(str);
                EventBus.getDefault().post(pcr);
                strParseResult = pcr.toString();
            } else if (str.startsWith("<bcast-end-res")) {
                BcastEndRes ber = BcastEndRes.xmlToBean(str);
                EventBus.getDefault().post(ber);
                strParseResult = ber.toString();
            } else if (str.startsWith("<bts-gps-notif")) {
                BcastEndRes ber = BcastEndRes.xmlToBean(str);
                EventBus.getDefault().post(ber);
                strParseResult = ber.toString();
            } else if(str.startsWith("<switch-tech-res")){
                SwitchTechRes switchTechRes = SwitchTechRes.xmlToBean(str);
                EventBus.getDefault().post(switchTechRes);
                strParseResult = switchTechRes.toString();
            } else if(str.startsWith("<cs-fallback-res")){
                CsFallbackRes csFallbackRes = CsFallbackRes.xmlToBean(str);
                EventBus.getDefault().post(csFallbackRes);
                strParseResult = csFallbackRes.toString();
            }
            Logs.d(TAG, ipAddress + "," + curSocket.getPort() + ",parser XML before TCP data=" + str, "Record_Event",true,true);
        }
        return strParseResult;
    }

//    private void queryAll() {
//        List<User> users = ProxyApplication.getDaoSession().getUserDao().loadAll();
//        Logs.i(TAG, "当前数量：" + users.size());
//        for (int i = 0; i < users.size(); i++) {
//            Logs.i(TAG, "结果：" + users.get(i).getId() + "," + users.get(i).getUnique() + "," + users.get(i).getSrtImsi() + "," + users.get(i).getStrImei() + "," + users.get(i).getIAuth() + "," + users.get(i).getBSilent());
//        }
//    }

    private String ParserHss(AuthRequestNotif arn) {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream ims = assetManager.open("hss.xml");
            String str = new String(InputStreamToByte(ims));
            return parser_hss_xml(str, arn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] InputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = is.read()) != -1) {
            bytestream.write(ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;
    }

    private String strCurTech;
    private String strRand;

    private String parser_hss_xml(String str, AuthRequestNotif arn) {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new ByteArrayInputStream(str.getBytes()), "UTF-8");
            int eventType = parser.getEventType();
            HssUeCtxt hssUeCtxt = new HssUeCtxt();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("imsi")) {
                            String strImsi = parser.nextText();
                            if (arn.getImsi().equals(strImsi)) {
                                hssUeCtxt.imsi = arn.getImsi();
                                if (TextUtils.isEmpty(arn.getParams().getTechSpecific().getSnId())) {
                                    strCurTech = "3G";
                                    hssUeCtxt.snid = "";
                                    hssUeCtxt.amf = "0000";
                                } else {
                                    strCurTech = "4G";
                                    hssUeCtxt.snid = arn.getParams().getTechSpecific().getSnId();
                                }
                                parser.next();
                                parser.next();
                                hssUeCtxt.authK = parser.nextText();
                                Logs.d(TAG, "imsi=" + hssUeCtxt.imsi + ",snid=" + hssUeCtxt.snid + ",authk=" + hssUeCtxt.authK + ",amf=" + hssUeCtxt.amf + ",op=" + hssUeCtxt.op);
                                Logs.d(TAG, "arn.getParams().getReSyncInfo()=" + arn.getParams().getReSyncInfo());
                                if (arn.getParams().getReSyncInfo() != null) {
                                    hssUeCtxt.checkrand = strRand;
                                    hssUeCtxt.Resyncrand = arn.getParams().getReSyncInfo().getRand();
                                    hssUeCtxt.Resyncauts = arn.getParams().getReSyncInfo().getAuts();
                                    PixcellAuthDataReqProc(hssUeCtxt, true);
                                } else {
                                    hssUeCtxt.authSqn = SharePreferenceUtils.getInstance(context).getString(arn.getImsi(), "");
                                    Logs.d(TAG, "authSqn=" + hssUeCtxt.authSqn);
                                    PixcellAuthDataReqProc(hssUeCtxt, false);//jni
                                }

                                DoAuthReq doAuthReq = new DoAuthReq();
                                DoAuthAuthParams authParams = new DoAuthAuthParams();
                                DoAuthTechSpecific techSpecific = new DoAuthTechSpecific();
                                doAuthReq.setAuthParams(authParams);
                                doAuthReq.getAuthParams().setTechSpecific(techSpecific);
                                doAuthReq.setImsi(hssUeCtxt.imsi);
                                authParams.setRand(bytesToHexString(hssUeCtxt.rand).toLowerCase());
                                authParams.setXres(bytesToHexString(hssUeCtxt.sres).toLowerCase());
                                authParams.setAutn(bytesToHexString(hssUeCtxt.autn).toLowerCase());
                                techSpecific.setXres(bytesToHexString(hssUeCtxt.sres).toLowerCase());
                                techSpecific.setAutn(bytesToHexString(hssUeCtxt.autn).toLowerCase());
                                techSpecific.setTech(strCurTech);
                                strRand = bytesToHexString(hssUeCtxt.rand).toLowerCase();
                                if (strCurTech.equals("3G")) {
                                    techSpecific.setIk(bytesToHexString(hssUeCtxt.ik).toLowerCase());
                                    techSpecific.setKc(bytesToHexString(hssUeCtxt.kc).toLowerCase());
                                } else if (strCurTech.equals("4G")) {
                                    techSpecific.setKasme(bytesToHexString(hssUeCtxt.kasme).toLowerCase());
                                }
                                //sendTcpMsg(context, doAuthReq.toXml(doAuthReq));
                                Logs.d(TAG, doAuthReq.toXml(doAuthReq));
                                Logs.d(TAG, "sres=" + bytesToHexString(hssUeCtxt.sres).toLowerCase());
                                Logs.d(TAG, "rand=" + bytesToHexString(hssUeCtxt.rand).toLowerCase());
                                Logs.d(TAG, "ik=" + bytesToHexString(hssUeCtxt.ik).toLowerCase());
                                Logs.d(TAG, "kc=" + bytesToHexString(hssUeCtxt.kc).toLowerCase());
                                Logs.d(TAG, "ak=" + bytesToHexString(hssUeCtxt.ak).toLowerCase());
                                Logs.d(TAG, "sqn=" + bytesToHexString(hssUeCtxt.sqn).toLowerCase());
                                if (arn.getParams().getReSyncInfo() != null) {
                                    SharePreferenceUtils.getInstance(context).setString(arn.getImsi(), bytesToHexString(hssUeCtxt.sqn).toLowerCase());
                                }
                                Logs.d(TAG, "kasme=" + bytesToHexString(hssUeCtxt.kasme).toLowerCase());
                                Logs.d(TAG, "tmsi=" + bytesToHexString(hssUeCtxt.tmsi).toLowerCase());
                                Logs.d(TAG, "autn=" + bytesToHexString(hssUeCtxt.autn).toLowerCase());
                                return doAuthReq.toXml(doAuthReq);
                            }
                        } else if (parser.getName().equals("amf")) {
                            hssUeCtxt.amf = parser.nextText();
                        } else if (parser.getName().equals("op")) {
                            hssUeCtxt.op = parser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class HssUeCtxt {
        String imsi;
        byte[] tmsi;
        String authK;
        byte[] sres;
        byte[] kc;
        byte[] ik;
        byte[] ak;
        byte[] sqn;
        byte[] rand;
        byte[] kasme;
        byte[] autn;
        String authSqn;
        String Resyncauts;
        String Resyncrand;
        String amf;
        String op;
        String snid;
        String checkrand;
    }

    private static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public native int PixcellAuthDataReqProc(HssUeCtxt hssUeCtxt, boolean bResync);

    static {
        System.loadLibrary("authentication_jni");
    }
}
