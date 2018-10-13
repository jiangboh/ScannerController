package com.bravo.femto;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.R;
import com.bravo.adapters.AdapterConnTarget;
import com.bravo.custom_view.CustomToast;
import com.bravo.data_ben.TargetDataStruct;
import com.bravo.database.AdjacentCell;
import com.bravo.database.AdjacentCellDao;
import com.bravo.database.BcastHistory;
import com.bravo.database.BcastHistoryDao;
import com.bravo.database.TargetUser;
import com.bravo.database.TargetUserDao;
import com.bravo.database.User;
import com.bravo.database.UserDao;
import com.bravo.parse_generate_xml.target_list.Target;
import com.bravo.parse_generate_xml.target_list.TargetListReq;
import com.bravo.parse_generate_xml.cell_scan.CellScanSibCell;
import com.bravo.parse_generate_xml.cell_scan.CellScanTechSpecific;
import com.bravo.parse_generate_xml.cs_fallback.CsFallbackReq;
import com.bravo.parse_generate_xml.target_list.TargetRedir;
import com.bravo.socket.SocketTCP;
import com.bravo.socket_service.EventBusMsgSendTCPMsg;
import com.bravo.socket_service.EventBusMsgSendUDPMsg;
import com.bravo.utils.Logs;
import com.bravo.utils.SharePreferenceUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jack.liao on 2017/3/15.
 */

public class BcastCommonApi {
    private final static String TAG = "BcastCommonApi";
    private final static int iLteChannels[][] = new int[][]{{0,0,600,1200,1950,2400,2650,2750,3450,3800,4150,
            4750,5010,5180,5280,0,0,5730,5850,6000,6150,6450,6600,7500,7700,8040,8690,9040,9210,9660,9770,
            9870,0,36000,36200,36350,36950,37550,37750,38250,38650,39650,41590,43590,45590},
            {0,599,1199,1949,2399,2649,2749,3449,3799,4149,4749,4949,5179,5279,5379,0,0,5849,5999,6149,6449,
            6599,7399,7699,8039,8689,9039,9209,9659,9769,9869,9919,0,36199,36349,36949,37549,37749,38249,
            38649,39649,41589,43589,45589,46589}};
    private final static int iGSMChannels[][] = new int[][]{{0,0,512,512,0,128,0,0,975},{0,0,810,885,0,251,0,124,1023}};
    private final static int iUMTSChannels[][] = new int[][]{{0,10562,9662,1162,1537,4357,4387,2237,2937},{0,10838,9938,1513,1738,4458,4413,2563,3088}};
    private final static int iUMTS2Additional[] = new int[]{412,437,462,487,512,537,562,587,612,637,662,687};
    private final static int iUMTS5Additional[] = new int[]{1007,1012,1032,1037,1062,1087};
    private final static int iUMTS4Additional[] = new int []{1887,1912,1937,1962,1987,2012,2037,2062,2087};

    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static void sendTargetList(Context context, String strCurTech) {
        List<TargetUser> targetUsers = ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(TargetUserDao.Properties.BCheck.eq(true)).build().list();
        if (targetUsers.size() > 0) {
            sendNewTargetList(context, targetUsers, strCurTech);
        } else {
            //new
            TargetListReq targetListReq = new TargetListReq();
            targetListReq.setTargetImsis("");
            targetListReq.setTargetImeis("");
            sendTcpMsg(context, TargetListReq.toXml(targetListReq));
        }
        CustomToast.showToast(context, "Update Target List Success");
    }
    //new target list
    private static void sendNewTargetList(Context context, List<TargetUser> targetUsers, String strCurTech) {
        TargetListReq targetListReq = new TargetListReq();
        //new
        if (strCurTech.equals("4G")) {
            List<Target> targets = new ArrayList<>();
            for (int i = 0; i < targetUsers.size(); i++) {
                if (targetUsers.get(i).getBRedir()) {//redir
                    TargetRedir targetRedir = new TargetRedir();
                    targetRedir.setChannel(targetUsers.get(i).getStrChannel());
                    targetRedir.setBand(targetUsers.get(i).getStrBand());
                    targetRedir.setTech(targetUsers.get(i).getStrTech());
                    if (!TextUtils.isEmpty(targetUsers.get(i).getStrImsi())) {//imsi
                        Target target = new Target();
                        target.setImsi(targetUsers.get(i).getStrImsi());
                        target.setTargetRedir(targetRedir);
                        targets.add(target);
                    }
                } else {if (!TextUtils.isEmpty(targetUsers.get(i).getStrImsi())) {//imsi
                        Target target = new Target();
                        target.setImsi(targetUsers.get(i).getStrImsi());
                        targets.add(target);
                    }
                }
            }
            targetListReq.setTargets(targets);
        } else {
            //old
            String strImsis = "";
            String strImeis = "";
            for (int iIndex = 0; iIndex < targetUsers.size(); iIndex++) {
                if (!TextUtils.isEmpty(targetUsers.get(iIndex).getStrImsi())) {
                    if (TextUtils.isEmpty(strImsis)) {
                        strImsis += targetUsers.get(iIndex).getStrImsi();
                    } else {
                        strImsis += "," + targetUsers.get(iIndex).getStrImsi();
                    }
                }
                if (!TextUtils.isEmpty(targetUsers.get(iIndex).getStrImei())) {
                    if (TextUtils.isEmpty(strImeis)) {
                        strImeis += targetUsers.get(iIndex).getStrImei();
                    } else {
                        strImeis += "," + targetUsers.get(iIndex).getStrImei();
                    }
                }
            }
            targetListReq.setTargetImsis(strImsis);
            targetListReq.setTargetImeis(strImeis);
        }
        sendTcpMsg(context, TargetListReq.toXml(targetListReq));
    }
    public static boolean checkTextEmpty(EditText editText) {
        editText.setError(null);
        if (TextUtils.isEmpty(editText.getText().toString())) {
            editText.requestFocus();
            editText.setError("N/A");
            return false;
        }
        return true;
    }

    public static void loadCurTargetList(AdapterConnTarget adapterConnTarget, Context context) {
        adapterConnTarget.RemoveAll();
        Long starttime = SharePreferenceUtils.getInstance(context).getLong("status_notif_starttime" +
                ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() +
                ((ProxyApplication)context.getApplicationContext()).getiTcpPort(),
                System.currentTimeMillis());
        List<User> users = ProxyApplication.getDaoSession().getUserDao().queryBuilder().where(UserDao.Properties.Unique.eq(SharePreferenceUtils.getInstance(context).getString("status_notif_unique" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "")),
                UserDao.Properties.ConnTime.gt(starttime)).build().list();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
        Logs.d(TAG, "lmj Load Target 小区启动时间=" + formatter.format(new Date(starttime)));
        for (int i = 0; i < users.size(); i++) {
            Logs.d(TAG, "lmj结果：" + users.get(i).toString());
            TargetDataStruct targetDataStruct = new TargetDataStruct();
            targetDataStruct.setImsi(users.get(i).getSrtImsi());
            targetDataStruct.setImei(users.get(i).getStrImei());
            targetDataStruct.setiUserType(users.get(i).getIAuth());
            targetDataStruct.setSilentState(users.get(i).getBSilent());
            targetDataStruct.setStrConntime(formatter.format(new Date(users.get(i).getConnTime())));
            targetDataStruct.setCount(users.get(i).getICount());
            if (targetDataStruct.getiUserType() == 1 &&
                    SharePreferenceUtils.getInstance(context).getString("status_notif_bts" +
                            ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() +
                            ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "1").equals("3")) {
                targetDataStruct.setiUserType(2);
                users.get(i).setIAuth(2);
                users.get(i).setDetachTime(System.currentTimeMillis());
                ProxyApplication.getDaoSession().getUserDao().update(users.get(i));
                adapterConnTarget.addTarget(targetDataStruct);
            } else if (targetDataStruct.getiUserType() == 1) {
                targetDataStruct.setImei(users.get(i).getStrImei());
                targetDataStruct.setStrAttachtime(formatter.format(new Date(users.get(i).getAttachTime())));
                adapterConnTarget.AttachTarget(targetDataStruct);
            } else {
                adapterConnTarget.addTarget(targetDataStruct);
            }
        }
        adapterConnTarget.notifyDataSetChanged();
    }

    public static void switchTechView(String strCurTech, View contentView) {
        contentView.findViewById(R.id.layout_fallback).setVisibility(View.GONE);
        contentView.findViewById(R.id.layout_psc).setVisibility(View.GONE);
        contentView.findViewById(R.id.tech_specific_gsm).setVisibility(View.GONE);
        contentView.findViewById(R.id.tech_specific_lte).setVisibility(View.GONE);
        contentView.findViewById(R.id.layout_lac).setVisibility(View.GONE);
        if (strCurTech.equals("2G")) {
            contentView.findViewById(R.id.tech_specific_gsm).setVisibility(View.VISIBLE);
            contentView.findViewById(R.id.layout_lac).setVisibility(View.VISIBLE);
        } else if (strCurTech.equals("3G")) {
            contentView.findViewById(R.id.layout_psc).setVisibility(View.VISIBLE);
            contentView.findViewById(R.id.layout_lac).setVisibility(View.VISIBLE);
        } else if (strCurTech.equals("4G")) {
            contentView.findViewById(R.id.tech_specific_lte).setVisibility(View.VISIBLE);
            contentView.findViewById(R.id.layout_fallback).setVisibility(View.VISIBLE);
        }
    }

    public static boolean checkChannels(EditText editText, String strBand, String strTech) {
        String strChannels = editText.getText().toString();
        if (strChannels.equals("")) {
            editText.requestFocus();
            editText.setError("N/A");
            return false;
        } else {
            if (strTech.equals("4G")) {
                return isDataLegal(editText, strChannels,iLteChannels[0][Integer.parseInt(strBand)], iLteChannels[1][Integer.parseInt(strBand)]);
            } else if (strTech.equals("3G")) {
                if (strBand.equals("1") || strBand.equals("8")) {
                    return isDataLegal(editText, strChannels,iUMTSChannels[0][Integer.parseInt(strBand)], iUMTSChannels[1][Integer.parseInt(strBand)]);
                } else {
                    return isUMTS245Legal(editText, strChannels,iUMTSChannels[0][Integer.parseInt(strBand)], iUMTSChannels[1][Integer.parseInt(strBand)], Integer.parseInt(strBand));
                }
            } else if (strTech.equals("2G")) {
                if (strBand.equals("8")){
                    String strChannel[] = strChannels.split(",");
                    for (int i = 0; i < strChannel.length; i++) {
                        if(isChannel(strChannel[i])) {//判断数据合法性
                            String strTemp[] = strChannel[i].split("\\.{2}");
                            if (strTemp.length > 1) {
                                if (!((Integer.parseInt(strTemp[0]) >= iGSMChannels[0][8] && Integer.parseInt(strTemp[0]) <= iGSMChannels[1][8]
                                        && Integer.parseInt(strTemp[1]) >= iGSMChannels[0][8] && Integer.parseInt(strTemp[1]) <= iGSMChannels[1][8])
                                        || (Integer.parseInt(strTemp[0]) >= iGSMChannels[0][7] && Integer.parseInt(strTemp[0]) <= iGSMChannels[1][7]
                                        && Integer.parseInt(strTemp[1]) >= iGSMChannels[0][7] && Integer.parseInt(strTemp[1]) <= iGSMChannels[1][7]))) {
                                    editText.requestFocus();
                                    editText.setError("Invalid channel " + strChannel[i]);
                                    return false;
                                }
                            } else if (!((Integer.parseInt(strChannel[i]) >= iGSMChannels[0][8] && Integer.parseInt(strChannel[i]) <= iGSMChannels[1][8])
                                     || (Integer.parseInt(strChannel[i]) >= iGSMChannels[0][7] && Integer.parseInt(strChannel[i]) <= iGSMChannels[1][7]))) {
                                editText.requestFocus();
                                editText.setError("Invalid channel " + strChannel[i]);
                                return false;
                            }
                        } else {
                            editText.requestFocus();
                            editText.setError("Invalid channel " + strChannel[i]);
                            return false;
                        }
                    }
                    return true;
                } else {
                    return isDataLegal(editText, strChannels,iGSMChannels[0][Integer.parseInt(strBand)], iGSMChannels[1][Integer.parseInt(strBand)]);
                }
            } else {
                return false;
            }
        }
    }

    private static boolean isChannel(String strChannel) {
        Pattern p = Pattern.compile("^[0-9]{1,5}|[0-9]{1,5}[.]{2}[0-9]{1,5}$");
        Matcher m = p.matcher(strChannel);
        return m.matches();
    }

    private static boolean isDataLegal(EditText editText, String strChannels, int iMin, int iMax) {
        String strChannel[] = strChannels.split(",");
        for (int i = 0; i < strChannel.length; i++) {
            if(isChannel(strChannel[i])) {//判断数据合法性
                String strTemp[] = strChannel[i].split("\\.{2}");
                if (strTemp.length > 1) {
                    if (!(Integer.parseInt(strTemp[0]) >= iMin && Integer.parseInt(strTemp[0]) <= iMax
                            && Integer.parseInt(strTemp[1]) >= iMin && Integer.parseInt(strTemp[1]) <= iMax)) {
                        editText.requestFocus();
                        editText.setError("Invalid channel " + strChannel[i]);
                        return false;
                    }
                } else if (!(Integer.parseInt(strChannel[i]) >= iMin && Integer.parseInt(strChannel[i]) <= iMax)) {
                    editText.requestFocus();
                    editText.setError("Invalid channel " + strChannel[i]);
                    return false;
                }
            } else {
                editText.requestFocus();
                editText.setError("Invalid channel " + strChannel[i]);
                return false;
            }
        }
        return true;
    }

    private static boolean isUMTS245Legal(EditText editText, String strChannels, int iMin, int iMax, int iBand) {
        String strChannel[] = strChannels.split(",");
        for (int i = 0; i < strChannel.length; i++) {
            if(isChannel(strChannel[i])) {//判断数据合法性
                String strTemp[] = strChannel[i].split("\\.{2}");
                if (strTemp.length > 1) {
                    if (!(Integer.parseInt(strTemp[0]) >= iMin && Integer.parseInt(strTemp[0]) <= iMax
                            && Integer.parseInt(strTemp[1]) >= iMin && Integer.parseInt(strTemp[1]) <= iMax)) {
                        editText.requestFocus();
                        editText.setError("Invalid channel " + strChannel[i]);
                        return false;
                    }
                } else if (!(Integer.parseInt(strChannel[i]) >= iMin && Integer.parseInt(strChannel[i]) <= iMax)) {
                    if (iBand == 2) {
                        if (!Arraycontains(iUMTS2Additional, Integer.parseInt(strChannel[i]))) {
                            editText.requestFocus();
                            editText.setError("Invalid channel " + strChannel[i]);
                            return false;
                        }
                    } else if (iBand == 5){
                        if (!Arraycontains(iUMTS5Additional, Integer.parseInt(strChannel[i]))) {
                            editText.requestFocus();
                            editText.setError("Invalid channel " + strChannel[i]);
                            return false;
                        }
                    } else if (iBand == 4) {
                        if (!Arraycontains(iUMTS4Additional, Integer.parseInt(strChannel[i]))) {
                            editText.requestFocus();
                            editText.setError("Invalid channel " + strChannel[i]);
                            return false;
                        }
                    }

                }
            } else {
                editText.requestFocus();
                editText.setError("Invalid channel " + strChannel[i]);
                return false;
            }
        }
        return true;
    }

    private static boolean Arraycontains(int Arrays[], int data) {
        for (int i = 0; i < Arrays.length; i++) {
            if (data == Arrays[i]){
                return true;
            }
        }
        return false;
    }

    public static void soundRing(Context context) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
        MediaPlayer mp = new MediaPlayer();
        mp.reset();
        mp.setDataSource(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mp.prepare();
        mp.start();
    }

    public static void getAdjCell(ArrayList<CellScanSibCell> sibCells, String strTech) {
        sibCells.clear();
        List<AdjacentCell> adjacentCells = ProxyApplication.getDaoSession().getAdjacentCellDao().queryBuilder().where(AdjacentCellDao.Properties.StrTech.eq(strTech), AdjacentCellDao.Properties.BCheck.eq(true)).build().list();
        if (strTech.equals("4G")) {
            for (int i = 0; i < adjacentCells.size(); i++) {
                CellScanSibCell cellScanSibCell = new CellScanSibCell();
                cellScanSibCell.setChannel(String.valueOf(adjacentCells.get(i).getIChannel()));
                cellScanSibCell.setRncid(String.valueOf(adjacentCells.get(i).getIRncid()));
                cellScanSibCell.setTechSpecific(new CellScanTechSpecific());
                cellScanSibCell.getTechSpecific().setTac(String.valueOf(adjacentCells.get(i).getITac()));
                cellScanSibCell.getTechSpecific().setPci(String.valueOf(adjacentCells.get(i).getIPci()));
                if (adjacentCells.get(i).getICid() != -1) {
                    cellScanSibCell.setCid(String.valueOf(adjacentCells.get(i).getICid()));
                }
                cellScanSibCell.setbCheck(adjacentCells.get(i).getBCheck());
                sibCells.add(cellScanSibCell);
            }
        } else if (strTech.equals("3G")) {
            for (int i = 0; i < adjacentCells.size(); i++) {
                CellScanSibCell cellScanSibCell = new CellScanSibCell();
                cellScanSibCell.setChannel(String.valueOf(adjacentCells.get(i).getIChannel()));
                cellScanSibCell.setRncid(String.valueOf(adjacentCells.get(i).getIRncid()));
                cellScanSibCell.setTechSpecific(new CellScanTechSpecific());
                cellScanSibCell.getTechSpecific().setLac(String.valueOf(adjacentCells.get(i).getILac()));
                cellScanSibCell.getTechSpecific().setPsc(String.valueOf(adjacentCells.get(i).getIPsc()));
                if (adjacentCells.get(i).getICid() != -1) {
                    cellScanSibCell.setCid(String.valueOf(adjacentCells.get(i).getICid()));
                }
                cellScanSibCell.setbCheck(adjacentCells.get(i).getBCheck());
                sibCells.add(cellScanSibCell);
            }
        } else {
            for (int i = 0; i < adjacentCells.size(); i++) {
                CellScanSibCell cellScanSibCell = new CellScanSibCell();
                cellScanSibCell.setChannel(String.valueOf(adjacentCells.get(i).getIChannel()));
                cellScanSibCell.setRncid(String.valueOf(adjacentCells.get(i).getIRncid()));
                if (adjacentCells.get(i).getICid() != -1) {
                    cellScanSibCell.setCid(String.valueOf(adjacentCells.get(i).getICid()));
                }
                cellScanSibCell.setbCheck(adjacentCells.get(i).getBCheck());
                sibCells.add(cellScanSibCell);
            }
        }
    }

    //2017-06-15 Send tcp msg common api
    public static void sendTcpMsg(Context context, String strMsg) {
        EventBus.getDefault().post(new EventBusMsgSendTCPMsg(((ProxyApplication)context.getApplicationContext()).getCurSocketAddress(),
                ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), strMsg));
    }

    //2017-06-15 Send udp msg common api
    public static void sendUdpMsg(Context context, String strMsg) {
        EventBus.getDefault().post(new EventBusMsgSendUDPMsg(((ProxyApplication)context.getApplicationContext()).getCurSocketAddress(),
                ((ProxyApplication)context.getApplicationContext()).getiUdpPort(), strMsg));
    }

    //2016-6-16 save bcast info
    public static void saveBcastInfo(Context context, boolean bFlag) {
        Logs.d(TAG, "lmj saveBcastInfo bFlag=" + bFlag +",ip=" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ",port=" + ((ProxyApplication)context.getApplicationContext()).getiTcpPort());
        //bFlag = true,新小区，= false 判断小区是否存在，不存在则添加到数据库
        String FemtoSn = SharePreferenceUtils.getInstance(context).getString("status_notif_sn" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        String Tech = SharePreferenceUtils.getInstance(context).getString("status_notif_tech" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        String Band = SharePreferenceUtils.getInstance(context).getString("status_notif_band" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        String Channel = SharePreferenceUtils.getInstance(context).getString("status_notif_channel" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        String Cid = SharePreferenceUtils.getInstance(context).getString("status_notif_cid" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");

        if (!bFlag) {//首次连接，如果bts=3或者bts=4代表小区已建起来，去数据库找一下小区信息是否存在
            Logs.d(TAG, "lmj cur->" + FemtoSn + "," + Tech + "," + Band + "," + Channel + "," + Cid);
            List<BcastHistory> bcastHistories = ProxyApplication.getDaoSession().getBcastHistoryDao().queryBuilder().orderDesc(BcastHistoryDao.Properties.Id).where(BcastHistoryDao.Properties.BRealEnd.eq(false)).list();
            for (int index = 0; index < bcastHistories.size(); index++) {
                Logs.d(TAG, "lmj recover->" + bcastHistories.get(index).toString());
                BcastHistory bcastHistory = bcastHistories.get(index);
                if (bcastHistories.get(index).getFemtoSn().equals(FemtoSn) &&
                        bcastHistories.get(index).getTech().equals(Tech) &&
                        bcastHistories.get(index).getBand().equals(Band) &&
                        bcastHistories.get(index).getCid().equals(Cid)) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
                    Logs.d(TAG, "lmj 小区启动时间=" + formatter.format(new Date(bcastHistories.get(index).getStatrtime())));
                    SharePreferenceUtils.getInstance(context).setLong("status_notif_starttime" + ((ProxyApplication) context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication) context.getApplicationContext()).getiTcpPort(), bcastHistories.get(index).getStatrtime());
                    return;
                }
            }
            SharePreferenceUtils.getInstance(context).setLong("status_notif_starttime" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), System.currentTimeMillis());
        }
        String MacAddress = ((ProxyApplication)context.getApplicationContext()).getCurMacAddress();
        String FemtoVer = SharePreferenceUtils.getInstance(context).getString("status_notif_ver" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        BcastHistory bcastHistory = new BcastHistory(null,FemtoSn, FemtoVer, MacAddress, System.currentTimeMillis(), (long) 0, Tech, Band, Channel, Cid, false);
        ProxyApplication.getDaoSession().getBcastHistoryDao().insert(bcastHistory);
    }

    public static void updateBcastInfoEndTime(Context context, boolean isEnd) {//isend= true是暂停小区，false断开连接
        Logs.d(TAG, "lmj updateBcastInfoEndTime");
        String FemtoSn = SharePreferenceUtils.getInstance(context).getString("status_notif_sn" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        String Tech = SharePreferenceUtils.getInstance(context).getString("status_notif_tech" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        String Band = SharePreferenceUtils.getInstance(context).getString("status_notif_band" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        String Channel = SharePreferenceUtils.getInstance(context).getString("status_notif_channel" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        String Cid = SharePreferenceUtils.getInstance(context).getString("status_notif_cid" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
       List<BcastHistory> bcastHistories = ProxyApplication.getDaoSession().getBcastHistoryDao().queryBuilder().where(
                BcastHistoryDao.Properties.FemtoSn.eq(FemtoSn),
                BcastHistoryDao.Properties.Tech.eq(Tech),
                BcastHistoryDao.Properties.Band.eq(Band),
                BcastHistoryDao.Properties.Cid.eq(Cid),
               BcastHistoryDao.Properties.BRealEnd.eq(false)).build().list();
        if (bcastHistories.size() == 0) {
            String MacAddress = ((ProxyApplication)context.getApplicationContext()).getCurMacAddress();
            String FemtoVer = SharePreferenceUtils.getInstance(context).getString("status_notif_ver" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
            BcastHistory bcastHistory = new BcastHistory(null,FemtoSn, FemtoVer, MacAddress, System.currentTimeMillis(), System.currentTimeMillis(), Tech, Band, Channel, Cid, isEnd);
            ProxyApplication.getDaoSession().getBcastHistoryDao().insert(bcastHistory);
            Logs.d(TAG, "lmj insert->" + FemtoSn + "," + Tech + "," + Band + "," + Channel + "," + Cid);
        } else {
            int iLastIndex = bcastHistories.size() - 1;
            BcastHistory bcastHistory = bcastHistories.get(iLastIndex);
            bcastHistories.get(iLastIndex).setBRealEnd(isEnd);
            String btsState = SharePreferenceUtils.getInstance(context).getString("status_notif_bts" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "1");
            if (System.currentTimeMillis() > bcastHistory.getStatrtime() &&
                    (btsState.equals("3") || btsState.equals("4") || isEnd)) {
                Logs.d(TAG, "update CurEndTime=" + bcastHistory.getEndtime() + ",NewEndTime=" + System.currentTimeMillis());
                bcastHistory.setEndtime(System.currentTimeMillis());
            }
            Logs.d(TAG, "lmj update->" + bcastHistories.get(iLastIndex).toString());
            ProxyApplication.getDaoSession().getBcastHistoryDao().update(bcastHistory);
        }
    }

    //send cs fallback
    public static void sendFallBack(Context context, View view){
        int iTech = ((Spinner)view.findViewById(R.id.cs_tech)).getSelectedItemPosition();
        if (iTech != 0) {
            String strBand = ((Spinner)view.findViewById(R.id.cs_band)).getSelectedItem().toString();
            String strChannel =((TextView)view.findViewById(R.id.cs_channel)).getText().toString();
            if (!TextUtils.isEmpty(strChannel)) {
                CsFallbackReq csFallbackReq = new CsFallbackReq();
                csFallbackReq.setChannel(strChannel);
                csFallbackReq.setBand(strBand);
                csFallbackReq.setTech(((Spinner) view.findViewById(R.id.cs_tech)).getSelectedItem().toString());
                sendTcpMsg(context, csFallbackReq.toXml(csFallbackReq));
            }
        }
    }
    //set cs fallback and implicit redirect band
    public static void changeBand(Context context, int iPosition, LinearLayout linearLayout, Spinner spinner) {
        switch (iPosition) {
            case 0:
                linearLayout.setVisibility(View.GONE);
                break;
            case 1:
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                        context, R.array.band_2g, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                linearLayout.setVisibility(View.VISIBLE);
                spinner.setAdapter(adapter);
                break;
            case 2:
                adapter = ArrayAdapter.createFromResource(
                        context, R.array.band_3g, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                linearLayout.setVisibility(View.VISIBLE);
                spinner.setAdapter(adapter);
                break;
            case 3:
                adapter = ArrayAdapter.createFromResource(
                        context, R.array.band_4g, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                linearLayout.setVisibility(View.VISIBLE);
                spinner.setAdapter(adapter);
            default:
                break;
        }
    }
    public static boolean checkChannel(int iTech, int iBand, String strChannel) {
        if (TextUtils.isEmpty(strChannel)){
            return false;
        }
        int iChannel = Integer.parseInt(strChannel);
        boolean bResult = false;
        switch (iTech) {
            case 0://null
                bResult = true;
                break;
            case 1://GSM
                bResult =  checkGSMChannel(iBand, iChannel);
                break;
            case 2://UMTS
                bResult = checkUMTSChannel(iBand, iChannel);
                break;
            case 3://LTE
                if (iChannel >= iLteChannels[0][iBand] && iChannel <= iLteChannels[1][iBand]) {
                    bResult = true;
                } else {
                    bResult = false;
                }
                break;
            default:
                break;
        }
        return bResult;
    }

    private static boolean checkUMTSChannel(int iBand, int iChannel) {
        switch (iBand){
            case 1:
            case 8:
                if (iChannel >= iUMTSChannels[0][iBand] && iChannel <= iUMTSChannels[1][iBand]) {
                    return true;
                }
                break;
            case 2:
                if (iChannel >= iUMTSChannels[0][iBand] && iChannel <= iUMTSChannels[1][iBand]) {
                    return true;
                } else {
                    for (int i = 0; i < iUMTS2Additional.length; i++) {
                        if (iChannel == iUMTS2Additional[i]) {
                            return true;
                        }
                    }
                }
                break;
            case 5:
                if (iChannel >= iUMTSChannels[0][iBand] && iChannel <= iUMTSChannels[1][iBand]) {
                    return true;
                }else {
                    for (int i = 0; i < iUMTS5Additional.length; i++) {
                        if (iChannel == iUMTS5Additional[i]) {
                            return true;
                        }
                    }
                }
                break;
            case 4:
                if (iChannel >= iUMTSChannels[0][iBand] && iChannel <= iUMTSChannels[1][iBand]) {
                    return true;
                }else {
                    for (int i = 0; i < iUMTS4Additional.length; i++) {
                        if (iChannel == iUMTS4Additional[i]) {
                            return true;
                        }
                    }
                }
            default:
                break;
        }
        return false;
    }

    private static boolean checkGSMChannel(int iBand, int iChannel) {
        switch (iBand) {
            case 2:
            case 3:
            case 5:
                if (iChannel >= iGSMChannels[0][iBand] && iChannel <= iGSMChannels[1][iBand]) {
                    return true;
                }
                break;
            case 8:
                if (iChannel >= iGSMChannels[0][iBand] && iChannel <= iGSMChannels[1][iBand]) {
                    return true;
                } else  if (iChannel >= iGSMChannels[0][iBand - 1] && iChannel <= iGSMChannels[1][iBand - 1]) {
                    return true;
                }
                break;
            default:
                break;
        }
        return false;
    }

    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (("").equals(ServiceName) || ServiceName == null)
            return false;
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(ServiceName)) {
                return true;
            }
        }
        return false;
    }

    //获取本机地址
//    static public String getLocAddress() {
//        String ipaddress = "";
//        try {
//            Enumeration<NetworkInterface> en = NetworkInterface
//                    .getNetworkInterfaces();
//            // 遍历所用的网络接口
//            while (en.hasMoreElements()) {
//                NetworkInterface networks = en.nextElement();
//                // 得到每一个网络接口绑定的所有ip
//                Enumeration<InetAddress> address = networks.getInetAddresses();
//                // 遍历每一个接口绑定的所有ip
//                while (address.hasMoreElements()) {
//                    InetAddress ip = address.nextElement();
//                    if (!ip.isLoopbackAddress()
//                            && InetAddressUtils.isIPv4Address(ip
//                            .getHostAddress())) {
//                        ipaddress = ip.getHostAddress();
//                    }
//                }
//            }
//        } catch (SocketException e) {
//            Logs.e("", "获取本地ip地址失败");
//            e.printStackTrace();
//        }
//
//        Logs.i(TAG, "本机IP:" + ipaddress);
//        return ipaddress;
//    }

    //获取网段
//    public static String getLocAddrIndex(String devAddress) {
//        if (!devAddress.equals("")) {
//            return devAddress.substring(0, devAddress.lastIndexOf(".") + 1);
//        }
//        return null;
//    }
    public static boolean isAvilible( Context context, String packageName )
    {
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for ( int i = 0; i < pinfo.size(); i++ )
        {
            if(pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    //检查是否有相同band建小区或sniffer
    public static boolean checkBandClash(Context context, String checkBand) {
        HashMap<String, Socket> socketHashMap = SocketTCP.getSockets();
        for (int i = 0; i < socketHashMap.size(); i++) {
            if (socketHashMap.get(i) != null) {
                String strIP = socketHashMap.get(i).getInetAddress().getHostAddress();
                if (!strIP.equals(((ProxyApplication) context.getApplicationContext()).getCurSocketAddress())) {
                    String strPort = String.valueOf(socketHashMap.get(i).getPort());
                    String strBts = SharePreferenceUtils.getInstance(context).getString("status_notif_bts" + strIP + strPort, "1");
                    String strBand = SharePreferenceUtils.getInstance(context).getString("status_notif_band" + strIP + strPort, "255");
                    if ((strBts.equals("3") || strBts.equals("4")) ) {
                        //相同或1和2,2和3,5和8,8和18,18和20,5和20,1和4,5和18,20和28
                        if (checkBand.equals(strBand)) return true;
                        switch (checkBand) {
                            case "1":
                                if (strBand.equals("2") || strBand.equals("4")) return true;
                                break;
                            case "2":
                                if (strBand.equals("1") || strBand.equals("3")) return true;
                                break;
                            case "3":
                                if (strBand.equals("2")) return true;
                                break;
                            case "4":
                                if (strBand.equals("1")) return true;
                                break;
                            case "5":
                                if (strBand.equals("8") || strBand.equals("18")) return true;
                                break;
                            case "8":
                                if (strBand.equals("5") || strBand.equals("18")) return true;
                                break;
                            case "18":
                                if (strBand.equals("5") || strBand.equals("8")) return true;
                                break;
                            case "20":
                                if (strBand.equals("28") || strBand.equals("18")) return true;
                                break;
                            case "28":
                                if (strBand.equals("20")) return true;
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean copyApkFromAssets(Context context, String fileName, String path) {
        boolean copyIsFinish = false;
        try {
            InputStream is = context.getAssets().open(fileName);
            File file = new File(path);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();
            copyIsFinish = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return copyIsFinish;
    }
}
