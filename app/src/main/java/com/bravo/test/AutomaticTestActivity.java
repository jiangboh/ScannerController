package com.bravo.test;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bravo.FemtoController.BaseActivity;
import com.bravo.FemtoController.ProxyApplication;
import com.bravo.R;
import com.bravo.adapters.AdapterAutomaticTest;
import com.bravo.custom_view.CustomProgressDialog;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.OneBtnHintDialog;
import com.bravo.femto.ButtonUtils;
import com.bravo.parse_generate_xml.Status;
import com.bravo.parse_generate_xml.bcast_end.BcastEndReq;
import com.bravo.parse_generate_xml.bcast_end.BcastEndRes;
import com.bravo.parse_generate_xml.bcast_start.BcastStartReq;
import com.bravo.parse_generate_xml.bcast_start.BcastStartRes;
import com.bravo.parse_generate_xml.cell_scan.CellScanNotif;
import com.bravo.parse_generate_xml.cell_scan.CellScanReq;
import com.bravo.parse_generate_xml.cell_scan.CellScanTechSpecific;
import com.bravo.parse_generate_xml.parameter_change.ParameterChangeRes;
import com.bravo.parse_generate_xml.udp.ActionResponse;
import com.bravo.parse_generate_xml.udp.RegisterClient;
import com.bravo.parse_generate_xml.udp.SetConfig;
import com.bravo.parse_generate_xml.udp.WifiConfig;
import com.bravo.socket_service.EventBusMsgCloseSocket;
import com.bravo.socket_service.EventBusMsgConstant;
import com.bravo.socket_service.EventBusMsgSendTCPMsg;
import com.bravo.socket_service.EventBusMsgSendUDPMsg;
import com.bravo.socket_service.EventBusMsgSocketDisconnect;
import com.bravo.utils.Logs;
import com.bravo.utils.SharePreferenceUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.Socket;

import static java.lang.Thread.sleep;


public class AutomaticTestActivity extends BaseActivity {
    private final int UPDATE_RESULT_SUCCESS = 0x55;
    private final int UPDATE_RESULT_FAILURE = 0x56;
    private final int SEND_MSG = 0x57;
    private final int AUTO_TEST = 0x58;
    public final int TYPE_CONFIG = 1;
    public final int TYPE_SCAN = 2;
    public final int TYPE_ACTIVE = 3;
    private final int TYPE_DEACTIVE = 4;
    private String ip;
    private int port;
    //config
    private ListView lvConfig;
    private AdapterAutomaticTest adapterConfig;
    private String[] strConfigs = new String[]{"CN mode", "CNE mode", "WIFI Client Mode", "WIFI AP Mode", "Change tcp port and tcp retry"};
    //scan
    private ListView lvScan;
    private AdapterAutomaticTest adapterScan;
    private String[] strScans = new String[]{"Cell Scan with 1,2,3 channels", "Cell Scan with a range of channels"};
    //active
    private ListView lvActive;
    private AdapterAutomaticTest adapterActive;
    private String[] strActives = new String[]{"Static Config", "Cell Scan", "Auto Switch"};
    //status
    private int iCurType;
    private int iCurPosition;
    //dialog
    private CustomProgressDialog connDialog;
    private CustomProgressDialog scanDialog;
    //status notif msg flag
    private boolean bconnStatus = false;
    private boolean bconnDialog = true;
    private boolean bscanDialog = false;
    //check channel resule
    private int iCheckChannel = 0x00;
    private boolean CHANNEL_SINGLE = true;
    private boolean CHANNEL_SERIES = false;
    private boolean bChannelStatus;
    //bts
    private int iCurBtsStatus = 1;
    //auto switch status;
    private boolean bAutoSwitch = false;
    //
    private int iAutoIndex = -1;
    private boolean[] bChecks = {false, false, false, false, false};
    private boolean bAutoState = false;
    //dialog
    private OneBtnHintDialog hintDialog;
    //channel
    private final static String singleLTEChannel[] = new String[]{"", "50", "650", "1650", "2150", "2500", "2700", "2800", "3500", "3900", "4200",
            "4800", "5100", "5200", "5300", "", "", "5800", "5900", "6100", "6200", "6500", "6800", "7600", "7800", "8100", "8700", "9100", "9300",
            "9700", "9800", "9900", "", "36100", "36250", "36400", "37000", "37600", "37800", "38500", "39000", "39700", "42000", "44000", "46000"};
    private final static String singleUMTSChannel[] = new String[]{"", "10713", "9700", "", "1540", "4400", "", "", "3000"};
    private final static String singleGSMChannel[] = new String[]{"", "", "520", "600", "", "150", "", "", "31"};
    private final static String arrayLTEChannel[] = new String[]{"","1,2,3", "600,601,602", "1300,1652,1825", "1950,1951,1952", "2400,2401,2402",
            "2650,2651,2652", "2750,2751,2752", "3450,3451,3452", "3800,3801,3802", "4150,4151,4152", "4750,4751,4752", "5010,5011,5012",
            "5180,5181,5182", "5280,5281,5282", "", "", "5730,5731,5732", "5850,5851,5852", "6000,6001,6002", "6150,6151,6152", "6450,6451,6452",
            "6600,6601,6602", "7500,7501,7502", "7700,7701,7702", "8040,8041,8042", "8690,8691,8692", "9040,8041,9042", "9210,9211,9212",
            "9660,9661,9662", "9770,9771,9772", "9870,9871,9872", "", "36000,36001,36002", "36200,36201,36202", "36350,36351,36352",
            "36950,36951,36952", "37550,37551,37552", "37750,37751,37752", "38250,38251,38252", "38650,38651,38652", "39650,39651,39652",
            "41590,41591,41592", "43590,43591,43592", "45590,45591,45592"};
    private final static String arrayUMTSChannel[] = new String[]{"", "10562,10688,10713", "9662,9663,9664", "", "1537,1538,1539",
            "4357,4358,4359", "", "", "2937,2938,2939"};
    private final static String arrayGSMChannel[] = new String[]{"", "", "515,516,517", "811,812,813", "", "128,129,130", "", "",
            "31,32,33"};
    //channels
    private final static String arrayLTEChannels[] = new String[]{"","4..6", "603..605", "1653..1655", "1953..1955", "2403..2405", "2653..2655",
            "2753..2755", "3453..3455", "3803..3805", "4153..4155", "4753..4755", "5013..5015", "5183..5185", "5283..5285", "", "", "5733..5735",
            "5853..5855", "6003..6005", "6153..6155", "6453..6455", "6603..6605", "7503..7505", "7703..7705", "8043..8045", "8693..8695",
            "9043..9045", "9213..9215", "9663..9665", "9773..9775", "9873..9875", "", "36003..36005", "36203..36205", "36353..36355",
            "36953..36955", "37553..37555", "37753..37755", "38253..38255", "38653..38655", "39653..39655", "41593..41595", "43593..43595",
            "45593..45595"};
    private final static String arrayUMTSChannels[] = new String[]{"", "10603..10605", "9665..9667", "", "1540..1542",
            "4360..4362", "", "", "2940..2942"};
    private final static String arrayGSMChannels[] = new String[]{"", "", "512..514", "814..816", "", "131..133", "", "",
            "980..982"};

    @Override
    protected void initView() {
        setContentView(R.layout.activity_automatic_test);
        findViewById(R.id.iv_activity_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //titile name
        ((TextView) findViewById(R.id.tv_activity_title)).setText("AutoMatic Test");
        //config
        lvConfig = (ListView) findViewById(R.id.list_config);
        adapterConfig = new AdapterAutomaticTest(this, strConfigs, TYPE_CONFIG);
        lvConfig.setAdapter(adapterConfig);

        //scan
        lvScan = (ListView) findViewById(R.id.list_scan);
        adapterScan = new AdapterAutomaticTest(this, strScans, TYPE_SCAN);
        lvScan.setAdapter(adapterScan);

        //active
        lvActive = (ListView) findViewById(R.id.list_active);
        adapterActive = new AdapterAutomaticTest(this, strActives, TYPE_ACTIVE);
        lvActive.setAdapter(adapterActive);

        //title right button
        ImageView rightBtn = (ImageView) findViewById(R.id.iv_activity_right);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setImageResource(R.drawable.btn_start_selector);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //auto test start
                if (!ButtonUtils.isFastDoubleClick()) {
                    bAutoState = true;
                    iAutoIndex = -1;
                    Message msg = handler.obtainMessage();
                    msg.what = AUTO_TEST;
                    handler.sendMessage(msg);
                }
            }
        });
        //init dialog
        connDialog = new CustomProgressDialog(this,R.style.dialog_style);
        connDialog.setCancelable(false);
        scanDialog = new CustomProgressDialog(this,R.style.dialog_style);
        scanDialog.setCancelable(false);

        //hint dialog
        hintDialog = new OneBtnHintDialog(this, R.style.dialog_style);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        ip = getIntent().getStringExtra("ip");
        port = getIntent().getIntExtra("port", 8021);
    }

    public void changeCheckBox(int iIndex, boolean bCheckBox) {
        Logs.d(TAG, "lmj changeCheckBox iIndex = " + iIndex + ",bCheckBox=" + bCheckBox);
        bChecks[iIndex] = bCheckBox;
    }

    public void sendMsg(int iType, int iPosition) {
        //check observer mode
        if (((ProxyApplication) getApplicationContext()).getCurSocket() != null &&
                !CheckObserverMode(SharePreferenceUtils.getInstance(this).getString("status_notif_controller_ip" + ip + port, "0.0.0.0"))) {
            return;
        }
        Logs.d(TAG, "lmj sendMsg iType=" + iType + ",iPosition=" + iPosition + ",bts=" + iCurBtsStatus);
        iCurType = iType;
        iCurPosition = iPosition;
        iCheckChannel = 0x00;
        switch (iType) {
            case TYPE_CONFIG:
                registerFemto();
                setConfigMsg(iPosition);
                break;
            case TYPE_SCAN:
                /*if (((ProxyApplication) getApplicationContext()).getCurSocket() == null) {
                    registerFemto();

                    connectDialogShow();
                    EventBus.getDefault().post(new EventBusMsgSendTCPMsg(ip, port, ""));
                } else {*/
                if (iCurBtsStatus == 6 && ((ProxyApplication)getApplicationContext()).getCurSocket() != null) {
                    if (bAutoState) {
                        iAutoIndex--;
                        Message msg = handler.obtainMessage();
                        msg.what = AUTO_TEST;
                        handler.sendMessageDelayed(msg,1000);
                    }
                } else {
                    Logs.d(TAG, "lmj TYPE_SCAN iCurBtsStatus=" + iCurBtsStatus);
                    scanMsg(iPosition);
                }
                break;
            case TYPE_ACTIVE:
                /*if (((ProxyApplication) getApplicationContext()).getCurSocket() == null) {
                    registerFemto();

                    connectDialogShow();
                    EventBus.getDefault().post(new EventBusMsgSendTCPMsg(ip, port, ""));
                } else {*/
                    Logs.d(TAG, "lmj TYPE_ACTIVE iCurBtsStatus=" + iCurBtsStatus);
                    if (iCurBtsStatus == 3 || iCurBtsStatus == 4) {
                        if (bAutoState) {
                            iAutoIndex--;
                        }
                        sendMsg(TYPE_DEACTIVE, -1);
                    } else {
                        activeMsg(iPosition);
                    }
                //}
                break;
            case TYPE_DEACTIVE:
                BcastEndReq bcastEndReq = new BcastEndReq();
                EventBus.getDefault().post(new EventBusMsgSendTCPMsg(ip, port, BcastEndReq.toXml(bcastEndReq)));
                Logs.d(TAG, "lmj send bcast end");
                break;
            default:
                break;
        }
    }

    private void setConfigMsg(int iPosition) {
        SetConfig setConfig = new SetConfig();
        switch (iPosition) {
            case 0://CN
                setConfig.setMode("CN");
                setConfig.setNbGw("172.17.1.18");
                break;
            case 1://CNE
                setConfig.setMode("CNE");
                break;
            case 2://client mode
                setConfig.setConnectivityMode("WIFI-CLIENT");
                WifiConfig wifiConfig = new WifiConfig();
                wifiConfig.setSsid("magent");
                wifiConfig.setSecurityMode("WPA2-PSK");
                wifiConfig.setEncryptionAlgorithm("AES");
                wifiConfig.setPasskey("12345678");
                setConfig.setWifiConfig(wifiConfig);
                break;
            case 3://ap mode
                setConfig.setConnectivityMode("WIFI-AP");
                break;
            case 4://tcp port and tcp retry
                setConfig.setTcpPort("8021");
                setConfig.setTcpRetry("3");
                break;
            default:
                return;
        }
        EventBus.getDefault().post(new EventBusMsgSendUDPMsg(ip, port, setConfig.toXml(setConfig)));
    }

    private void scanMsg(int iPosition) {
        CellScanReq cellScanReq = new CellScanReq();
        String strBand = SharePreferenceUtils.getInstance(this).getString("status_notif_band" + ip + port, "-1");
        Logs.d(TAG, "lmj scanMsg position=" + iPosition + ",band=" + strBand);
        if (strBand.equals("255") || strBand.equals("0")) {
            updateResult(TYPE_SCAN, iCurPosition, "FAILURE");
            return;
        } else {
            cellScanReq.setBand(strBand);
        }
        cellScanReq.setThreshold("-100");

        switch (iPosition) {
            case 0: //channel1,2,3,port=1,afc = false
                cellScanReq.setAfc("FALSE");
                cellScanReq.setAntennaPort("1");
                cellScanReq.setChannels(getChannel(CHANNEL_SINGLE));
                break;
            case 1://channel 1..3,port = 2, false = true
                cellScanReq.setAfc("TRUE");
                cellScanReq.setAntennaPort("2");
                cellScanReq.setChannels(getChannel(CHANNEL_SERIES));
                break;
            default:
                return;
        }
        EventBus.getDefault().post(new EventBusMsgSendTCPMsg(ip, port, CellScanReq.toXml(cellScanReq)));
        scanDialogShow();
    }

    private void activeMsg(int iPosition) {
        Logs.d(TAG, "lmj activeMsg=" + iPosition);
        BcastStartReq bcastStartReq = new BcastStartReq();
        bcastStartReq.setMcc("123");
        bcastStartReq.setMnc("456");
        String strBand = SharePreferenceUtils.getInstance(this).getString("status_notif_band" + ip + port, "-1");
        if (strBand.equals("255") || strBand.equals("0")) {
            updateResult(TYPE_ACTIVE, iCurPosition, "FAILURE");
            return;
        } else {
            bcastStartReq.setBand(strBand);

        }

        switch (iPosition) {
            case 0:
                bcastStartReq.setCid("1");
                CellScanTechSpecific techSpecific = new CellScanTechSpecific();
                //
                switch (SharePreferenceUtils.getInstance(this).getString("status_notif_tech" + ip + port, "0")) {
                    case "4G"://tac pci
                        techSpecific.setTac("1");
                        techSpecific.setPci("1");
                        break;
                    case "3G"://lac psc
                        techSpecific.setLac("1");
                        techSpecific.setPsc("1");
                        break;
                    case "2G"://ncc bcc lac
                        techSpecific.setNcc("1");
                        techSpecific.setBcc("1");
                        techSpecific.setLac("1");
                        break;
                    default:
                        break;
                }
                bcastStartReq.setTechSpecific(techSpecific);
                bcastStartReq.setChannels(getSingleChannel());
                bcastStartReq.setAntennaPort(String.valueOf("1"));
                break;
            case 1:
                bcastStartReq.setChannels(getChannel(CHANNEL_SERIES));
                bcastStartReq.setAntennaPort(String.valueOf("2"));
                break;
            case 2:
                bcastStartReq.setChannels(getChannel(CHANNEL_SERIES));
                bcastStartReq.setAntennaPort(String.valueOf("1"));
                bcastStartReq.setAutoSwitchInterval("35");
                break;
            default:
                break;
        }

        EventBus.getDefault().post(new EventBusMsgSendTCPMsg(ip, port, bcastStartReq.toXml(bcastStartReq)));
        scanDialogShow();
    }

    private String getSingleChannel(){
        String strChannel = "";
        int iBand = Integer.parseInt(SharePreferenceUtils.getInstance(this).getString("status_notif_band" + ip + port, "0"));
        switch (SharePreferenceUtils.getInstance(this).getString("status_notif_tech" + ip + port, "0")) {
            case "4G":
                strChannel = singleLTEChannel[iBand];
                break;
            case "3G":
                strChannel = singleUMTSChannel[iBand];
                break;
            case "2G":
                strChannel = singleGSMChannel[iBand];
                break;
            default:
                break;
        }
        return strChannel;
    }

    private String getChannel(boolean bType) {
        bChannelStatus = bType;
        String strChannel = "";
        if (bType) {//1,2,3
            switch (SharePreferenceUtils.getInstance(this).getString("status_notif_tech" + ip + port, "0")) {
                case "4G"://4G
                    int iBand = Integer.parseInt(SharePreferenceUtils.getInstance(this).getString("status_notif_band" + ip + port, "0"));
                    if (iBand > 0 && iBand <= arrayLTEChannel.length) {
                        strChannel = arrayLTEChannel[iBand];
                    }
                    break;
                case "3G"://3G
                    iBand = Integer.parseInt(SharePreferenceUtils.getInstance(this).getString("status_notif_band" + ip + port, "0"));
                    if (iBand == 1 || iBand == 2 || iBand == 4 || iBand == 5 || iBand == 8) {
                        strChannel = arrayUMTSChannel[iBand];
                    }
                    break;
                case "2G"://2G
                    iBand = Integer.parseInt(SharePreferenceUtils.getInstance(this).getString("status_notif_band" + ip + port, "0"));
                    if (iBand == 2 || iBand == 3 ||iBand == 5 || iBand == 8) {
                        strChannel = arrayGSMChannel[iBand];
                    }
                    break;
                default:
                    break;
            }
        } else {//1..3
            switch (SharePreferenceUtils.getInstance(this).getString("status_notif_tech" + ip + port, "0")) {
                case "4G":
                    int iBand = Integer.parseInt(SharePreferenceUtils.getInstance(this).getString("status_notif_band" + ip + port, "0"));
                    if (iBand > 0 && iBand <= arrayLTEChannels.length) {
                        strChannel = arrayLTEChannels[iBand];
                    }
                    break;
                case "3G"://3G
                    iBand = Integer.parseInt(SharePreferenceUtils.getInstance(this).getString("status_notif_band" + ip + port, "0"));
                    if (iBand == 1 || iBand == 2 || iBand == 4 || iBand == 5 || iBand == 8) {
                        strChannel = arrayUMTSChannels[iBand];
                    }
                    break;
                case "2G"://2G
                    iBand = Integer.parseInt(SharePreferenceUtils.getInstance(this).getString("status_notif_band" + ip + port, "0"));
                    if (iBand == 2 || iBand == 3 ||iBand == 5 || iBand == 8) {
                        strChannel = arrayGSMChannels[iBand];
                    }
                    break;
                default:
                    break;
            }
        }
        return strChannel;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        registerFemto();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connDialog.isShowing()) {
            connDialog.dismiss();
        }
        if (scanDialog.isShowing()) {
            scanDialog.dismiss();
        }
        unregisterFemto();
        EventBus.getDefault().unregister(this);
    }

    //register
    private void registerFemto() {
        RegisterClient register = new RegisterClient();
        register.setClientType(0 + "");//
        register.setCurrentTime(System.currentTimeMillis() / 1000);//根据需要的格式转换,建议使用SimpleDateUtils转换格式
        String registeXml = RegisterClient.toXml(register);
        EventBus.getDefault().post(new EventBusMsgSendUDPMsg(ip, ((ProxyApplication)getApplicationContext()).getiUdpPort(), registeXml));
    }

    //unregister
    private void unregisterFemto() {
        EventBus.getDefault().post(new EventBusMsgCloseSocket(ip, ((ProxyApplication)getApplicationContext()).getiUdpPort()));
    }

    private void updateResult(int iType, int iPosition, String strResult) {
        switch (iType) {
            case TYPE_CONFIG:
                adapterConfig.setResult(iPosition, strResult);
                if (iPosition == 2) {
                    new AlertDialog.Builder(this)
                            .setTitle("Wifi Client")
                            .setMessage("magent/12345678/WPA2-PSK/AES")
                            .show();
                }
                break;
            case TYPE_SCAN:
                adapterScan.setResult(iPosition, strResult);
                if (bAutoState){
                    Message msg = handler.obtainMessage();
                    msg.what = AUTO_TEST;
                    handler.sendMessage(msg);
                }
                break;
            case TYPE_ACTIVE:
                adapterActive.setResult(iPosition, strResult);
                if (strResult.equals("FAILURE")) {
                    if (bAutoState){
                        Message msg = handler.obtainMessage();
                        msg.what = AUTO_TEST;
                        handler.sendMessage(msg);
                    }
                }
                break;
            default:
                break;
        }
        Logs.d(TAG, "lmj updateResult iType=" + iType + ",iPosition=" + iPosition + ",strResult=" +strResult + "BTS=" + iCurBtsStatus);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ActionRes(ActionResponse as) {
        if (as.getActionType().equals("set-config") && iCurType == TYPE_CONFIG) {
            updateResult(TYPE_CONFIG, iCurPosition, as.getActionStatus());
        }
    }

    private  void showHintDialog(String strMessage) {
        if (!hintDialog.isShowing()) {
            hintDialog.setCanceledOnTouchOutside(false);
            hintDialog.show();
            hintDialog.setBtnContent("OK");
            hintDialog.setTitle("Warning");
            hintDialog.setContent(strMessage);
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void connectResult(String strConnectState) {
        switch (strConnectState) {
            case EventBusMsgConstant.TCP_CONNECT_FAILED:
                bconnStatus = false;
                bconnDialog = false;
                bAutoState = false;
                Toast.makeText(this, "Connect Femto failure", Toast.LENGTH_SHORT).show();
                break;
            case EventBusMsgConstant.TCP_CONNECT_ALREADY:
                bconnStatus = true;
                Logs.d(TAG, "EventBusMsgConstant.TCP_CONNECT_ALREADY");
                break;
            case EventBusMsgConstant.TCP_CONNECT_SUCCESS:
                bconnStatus = true;
                //Logs.d(TAG, "EventBusMsgConstant.TCP_CONNECT_SUCCESS");
                break;
            case EventBusMsgConstant.TCP_RECONNECT_FAILED:
                bconnStatus = false;
                bconnDialog = false;
                bAutoState = false;
                showHintDialog("Socket Disconnect");
                break;
            case EventBusMsgConstant.TCP_SOCKET_TIMEOUT:
                bconnStatus = false;
                bconnDialog = false;
                bAutoState = false;
                showHintDialog("Socket Timeout");
                break;
            case EventBusMsgConstant.REGISTER_FAILED:
                bconnStatus = false;
                bconnDialog = false;
                Toast.makeText(this, "Register Femto failure", Toast.LENGTH_SHORT).show();
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void statusNotifi(Status s) {
        bconnDialog = false;
        iCurBtsStatus = Integer.parseInt(s.getBtsState());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void parameterChangeRes(ParameterChangeRes pcr) {
        bAutoSwitch = false;
        updateResult(TYPE_ACTIVE, 2, "SUCCESS");
        sendMsg(TYPE_DEACTIVE, -1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void cellScanNotif(CellScanNotif scn) {
        if (iCurType != TYPE_SCAN/* && iCurPosition == 2*/) {
            return;
        }
        if (bChannelStatus) {
            switch (SharePreferenceUtils.getInstance(this).getString("status_notif_tech" + ip + port, "0")) {
                case "4G":
                    int iBand = Integer.parseInt(SharePreferenceUtils.getInstance(this).getString("status_notif_band" + ip + port, "0"));
                    if (iBand > 0 && iBand <= arrayLTEChannel.length) {
                        String bands[] = arrayLTEChannel[iBand].split(",");
                        if (bands.length == 3) {
                            if (scn.getChannel().equals(bands[0])) {
                                iCheckChannel = iCheckChannel | 0x01;
                            } else if (scn.getChannel().equals(bands[1])) {
                                iCheckChannel = iCheckChannel | 0x10;
                            } else if (scn.getChannel().equals(bands[2])) {
                                iCheckChannel = iCheckChannel | 0x100;
                            } else {
                                iCheckChannel = 0xFF;
                            }
                        } else {
                            iCheckChannel = 0xFF;
                        }
                    } else {
                        iCheckChannel = 0xFF;
                    }
                    break;
                case "3G":
                    iBand = Integer.parseInt(SharePreferenceUtils.getInstance(this).getString("status_notif_band" + ip + port, "0"));
                    if (iBand == 1 || iBand == 2 || iBand == 4 || iBand == 5 || iBand == 8) {
                        String bands[] = arrayUMTSChannel[iBand].split(",");
                        if (bands.length == 3) {
                            if (scn.getChannel().equals(bands[0])) {
                                iCheckChannel = iCheckChannel | 0x01;
                            } else if (scn.getChannel().equals(bands[1])) {
                                iCheckChannel = iCheckChannel | 0x10;
                            } else if (scn.getChannel().equals(bands[2])) {
                                iCheckChannel = iCheckChannel | 0x100;
                            } else {
                                iCheckChannel = 0xFF;
                            }
                        } else {
                            iCheckChannel = 0xFF;
                        }
                    } else {
                        iCheckChannel = 0xFF;
                    }
                    break;
                case "2G":
                    iBand = Integer.parseInt(SharePreferenceUtils.getInstance(this).getString("status_notif_band" + ip + port, "0"));
                    if (iBand == 2 || iBand == 3 ||iBand == 5 || iBand == 8) {
                        String bands[] = arrayGSMChannel[iBand].split(",");
                        if (bands.length == 3) {
                            if (scn.getChannel().equals(bands[0])) {
                                iCheckChannel = iCheckChannel | 0x01;
                            } else if (scn.getChannel().equals(bands[1])) {
                                iCheckChannel = iCheckChannel | 0x10;
                            } else if (scn.getChannel().equals(bands[2])) {
                                iCheckChannel = iCheckChannel | 0x100;
                            } else {
                                iCheckChannel = 0xFF;
                            }
                        } else {
                            iCheckChannel = 0xFF;
                        }
                    } else {
                        iCheckChannel = 0xFF;
                    }
                    break;
                default:
                    break;
            }
        } else {
            switch (SharePreferenceUtils.getInstance(this).getString("status_notif_tech" + ip + port, "0")) {
                case "4G":
                    int iBand = Integer.parseInt(SharePreferenceUtils.getInstance(this).getString("status_notif_band" + ip + port, "0"));
                    if (iBand > 0 && iBand <= arrayLTEChannel.length) {
                        String bands[] = arrayLTEChannels[iBand].split("\\.{2}");
                        if (bands.length == 2) {
                            if (scn.getChannel().equals(bands[0])) {
                                iCheckChannel = iCheckChannel | 0x01;
                            } else if (scn.getChannel().equals(String.valueOf(Integer.parseInt(bands[0]) + 1))) {
                                iCheckChannel = iCheckChannel | 0x10;
                            } else if (scn.getChannel().equals(bands[1])) {
                                iCheckChannel = iCheckChannel | 0x100;
                            } else {
                                iCheckChannel = 0xFF;
                            }
                        } else {
                            iCheckChannel = 0xFF;
                        }
                    } else {
                        iCheckChannel = 0xFF;
                    }
                    break;
                case "3G":
                    iBand = Integer.parseInt(SharePreferenceUtils.getInstance(this).getString("status_notif_band" + ip + port, "0"));
                    if (iBand == 1 || iBand == 2 || iBand == 4 || iBand == 5 || iBand == 8) {
                        String bands[] = arrayUMTSChannels[iBand].split("\\.{2}");
                        if (bands.length == 2) {
                            if (scn.getChannel().equals(bands[0])) {
                                iCheckChannel = iCheckChannel | 0x01;
                            } else if (scn.getChannel().equals(String.valueOf(Integer.parseInt(bands[0]) + 1))) {
                                iCheckChannel = iCheckChannel | 0x10;
                            } else if (scn.getChannel().equals(bands[1])) {
                                iCheckChannel = iCheckChannel | 0x100;
                            } else {
                                iCheckChannel = 0xFF;
                            }
                        } else {
                            iCheckChannel = 0xFF;
                        }
                    }
                    break;
                case "2G":
                    iBand = Integer.parseInt(SharePreferenceUtils.getInstance(this).getString("status_notif_band" + ip + port, "0"));
                    if (iBand == 2 || iBand == 3 ||iBand == 5 || iBand == 8) {
                        String bands[] = arrayGSMChannels[iBand].split("\\.{2}");
                        if (bands.length == 2) {
                            if (scn.getChannel().equals(bands[0])) {
                                iCheckChannel = iCheckChannel | 0x01;
                            } else if (scn.getChannel().equals(String.valueOf(Integer.parseInt(bands[0]) + 1))) {
                                iCheckChannel = iCheckChannel | 0x10;
                            } else if (scn.getChannel().equals(bands[1])) {
                                iCheckChannel = iCheckChannel | 0x100;
                            } else {
                                iCheckChannel = 0xFF;
                            }
                        } else {
                            iCheckChannel = 0xFF;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        if (iCheckChannel == 0x111) {
            bscanDialog = false;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updateResult(iCurType, iCurPosition, "SUCCESS");
        } else if (iCheckChannel == 0xFF || ((iCheckChannel & 0x001) != 0x001)) {
            updateResult(iCurType, iCurPosition, "FAILURE");
        }
    }

    private void scanDialogShow() {
        if (true) {
            Logs.d(TAG, "lmj dialog show");
            scanDialog.show();
            scanDialog.setTitle("Scanning");
            scanDialog.setContent("Please wait...");
            bscanDialog = true;
            Thread thread = new Thread() {
                public void run() {
                    try {
                        int iCount = 0;
                        while (bscanDialog && iCurBtsStatus != 6 && iCount < 60) {
                            sleep(200);
                            iCount++;
                        }
                        if (iCount < 60) {
                            while (iCurBtsStatus == 6 && bscanDialog) {
                                sleep(50);
                            }
                            if (iCurType == TYPE_ACTIVE && iCurPosition == 2) {
                                bAutoSwitch = true;
                                iCount = 0;
                                while (bAutoSwitch && iCount < 600) {
                                    sleep(100);
                                    iCount++;
                                }
                                if (iCount >= 600) {
                                    Message msg = handler.obtainMessage();
                                    msg.what = UPDATE_RESULT_FAILURE;
                                    handler.sendMessage(msg);
                                }
                            } else if (iCurType == TYPE_ACTIVE) {
                                iCount = 0;
                                while (bscanDialog && iCount < 100) {
                                    sleep(100);
                                    iCount++;
                                }
                            }
                        } else if (iCount >= 60 && bscanDialog) {
                            Message msg = handler.obtainMessage();
                            msg.what = UPDATE_RESULT_FAILURE;
                            handler.sendMessage(msg);
                        }
                        scanDialog.dismiss();
                        Logs.d(TAG, "dialog dismiss");
                    } catch (InterruptedException e) {
                        // TODO 自动生成的 catch 块
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BcastEndRes(BcastEndRes ber) {
        Logs.d(TAG, "lmj bcastendres = " + ber.getStatus());
        if (!ber.getStatus().equals("SUCCESS") && (iCurBtsStatus == 3 || iCurBtsStatus == 4)) {
            sendMsg(TYPE_DEACTIVE, -1);
        } else if (ber.getStatus().equals("SUCCESS")) {
            iCurBtsStatus = 1;
            if (bAutoState){
                Message msg = handler.obtainMessage();
                msg.what = AUTO_TEST;
                handler.sendMessage(msg);
            }
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_RESULT_SUCCESS:
                    updateResult(iCurType, iCurPosition, "SUCCESS");
                    break;
                case UPDATE_RESULT_FAILURE:
                    updateResult(iCurType, iCurPosition, "FAILURE");
                    break;
                case SEND_MSG:
                    sendMsg(iCurType, iCurPosition);
                    break;
                case AUTO_TEST:
                    setConfigMsg(1);
                    Logs.d(TAG, "lmj AUTO_TEST");
                    if (scanDialog != null) {
                        scanDialog.dismiss();
                    }
                    while (iAutoIndex < 4) {
                        iAutoIndex++;
                        if (bChecks[iAutoIndex]) {
                            switch (iAutoIndex) {
                                case 0:
                                case 1:
                                    iCurType = TYPE_SCAN;
                                    iCurPosition = iAutoIndex;
                                    break;
                                case 2:
                                case 3:
                                case 4:
                                    iCurType = TYPE_ACTIVE;
                                    iCurPosition = iAutoIndex - 2;
                                    break;
                                default:
                                    return;
                            }
                            sendMsg(iCurType, iCurPosition);
                            return;
                        }
                    }
                    bAutoState = false;
                    break;
                default:
                    break;
            }
        }
    };

    //check observer mode
    private boolean CheckObserverMode(String strControlIP) {
        String strSCIP ="N/A";
        Socket socket = ((ProxyApplication)mContext.getApplicationContext()).getCurSocket();
        if (socket != null ){
            strSCIP = socket.getLocalAddress().getHostAddress();
        }

		Logs.d(TAG, "lmj sc ip=" + strSCIP + "Scontroller ip=" + strControlIP);
		if (!strSCIP.equals(strControlIP)) {
			CustomToast.showToast(this, "Observer mode(Control IP=" + strControlIP + ",SC IP=" + strSCIP);
			return false;
		} else {
			return true;
		}
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BcastStartRes(BcastStartRes bsr) {
        if (iCurType == TYPE_ACTIVE && iCurPosition == 2) {
            return;
        }
        bscanDialog = false;
        if (bsr.getStatus().equals("SUCCESS")) {
            try {
                sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updateResult(iCurType, iCurPosition, "SUCCESS");
            sendMsg(TYPE_DEACTIVE, -1);
        } else {
            updateResult(iCurType, iCurPosition, "FAILURE");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void socketDisconnect(final EventBusMsgSocketDisconnect eventBusMsgSocketDisconnect) {
        if (eventBusMsgSocketDisconnect.getIpAddress().equals(ip)) {//断开连接
            bscanDialog = false;
            bAutoState = false;
            showHintDialog("Socket Disconnect");
            ((ProxyApplication)getApplicationContext()).setCurSocket(null);
        }
    }
}
