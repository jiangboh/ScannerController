package com.bravo.FemtoController;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.adapters.AdapterFemtoList;
import com.bravo.custom_view.CustomProgressDialog;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.OneBtnHintDialog;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.custom_view.RecordOnItemClick;
import com.bravo.custom_view.RecordOnItemLongClick;
import com.bravo.data_ben.FemtoDataStruct;
import com.bravo.database.FemtoList;
import com.bravo.database.FemtoListDao;
import com.bravo.dialog.DialogConfig;
import com.bravo.listview.UserDefineListView;
import com.bravo.parse_generate_xml.conn_request.ConnRequestNotif;
import com.bravo.parse_generate_xml.target_attach.TargetAttach;
import com.bravo.parse_generate_xml.udp.BTSQuery;
import com.bravo.socket_service.CommunicationService;
import com.bravo.socket_service.EventBusMsgConstant;
import com.bravo.socket_service.EventBusMsgDevResponse;
import com.bravo.socket_service.EventBusMsgSendTCPMsg;
import com.bravo.socket_service.EventBusMsgSendUDPBroadcastMsg;
import com.bravo.utils.Logs;
import com.bravo.utils.SharePreferenceUtils;
import com.bravo.wifi.WifiAP;
import com.bravo.wifi.WifiAdmin;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import static com.bravo.femto.BcastCommonApi.isServiceRunning;
import static com.bravo.femto.BcastCommonApi.saveBcastInfo;

public class FemtoListActivity extends BaseActivity implements View.OnClickListener{
    public UserDefineListView userDefineListView;
    private AdapterFemtoList adapterFemtoList;
    private boolean bDialogState = true;
    private boolean bConnectState = false;
    private final int WIFI_AP_MODE = 1;
    private final int WIFI_CLIENT_MODE = 2;
    private final int BLUETOOTH_MODE = 3;
    private final int BACK_PRESSED = 5;
    private final int HINT_DIALOG = 6;
    private final int BTS_STATUS_ZERO = 8;
    private final int CONNECT_TIMEOUT = 10;
//    private ScanDeviceTool scanDeviceTool = new ScanDeviceTool();
    private CustomProgressDialog proDialog;
    private ReceiveBroadCast receiveBroadCast;
    private ImageView iv_rotate;

    private ImageView upArrows;
    private ImageButton upBtn,downArrows;
    private LinearLayout downLayout;
    private DisplayMetrics outMetrics;
    private OneBtnHintDialog startSettingDialog;

    private EditText ipAddress1,ipAddress2,ipAddress3,ipAddress4,port;
    private ImageView rightBtn;
    @Override
    protected void initView() {
        if (!isServiceRunning(mContext, "com.bravo.socket_service.CommunicationService")){
            Intent intent = new Intent(this,CommunicationService.class);
            startService(intent);
        }
        setContentView(R.layout.activity_femto_list);
        findViewById(R.id.iv_activity_back).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                onBackPressed();
                super.recordOnClick(v, "Back Event " + TAG);
            }
        });
        ((TextView) findViewById(R.id.tv_activity_title)).setText(R.string.femto_list);
        WindowManager wm = (WindowManager)getSystemService(
                Context.WINDOW_SERVICE);
        outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        upArrows = (ImageView) findViewById(R.id.iv_up_arrows);
        upArrows.setOnClickListener(this);
        upArrows.setClickable(false);
        AnimationDrawable ad = (AnimationDrawable) upArrows.getDrawable();
        ad.start();
        upBtn = (ImageButton) findViewById(R.id.ib_up_ib);
        upBtn.setOnClickListener(this);
        upBtn.setClickable(false);
        downArrows = (ImageButton) findViewById(R.id.ib_down_arrows);
        downArrows.setOnClickListener(this);
        AnimationDrawable ad1 = (AnimationDrawable) downArrows.getDrawable();
        ad1.start();
        downLayout = (LinearLayout) findViewById(R.id.ll_down_layout);
        rightBtn = (ImageView) findViewById(R.id.iv_activity_right);
        iv_rotate = (ImageView) findViewById(R.id.rotate_image);
        userDefineListView = (UserDefineListView) findViewById(R.id.pixcell_list);
        //listview = (PixcellListView) findViewById(R.id.pixcell_list);
        adapterFemtoList = new AdapterFemtoList(this);
        userDefineListView.setAdapter(adapterFemtoList);
        //pixcellListView.setChoiceMode(pixcellListView.CHOICE_MODE_SINGLE);
        userDefineListView.setOnItemClickListener(new RecordOnItemClick() {
            @Override
            public void recordOnItemClick(AdapterView<?> parent, View view, int position, long id, String strMsg) {
                if (userDefineListView.canClick()) {
                    //lmj 2017-07-03 add test model
                    ((ProxyApplication)getApplicationContext()).setiUdpPort(adapterFemtoList.getItem(position).getiUdpPort());
                    connRequest(adapterFemtoList.getItem(position).getIPAddress(), adapterFemtoList.getItem(position).getiTcpPort());
                    super.recordOnItemClick(parent, view, position, id, "Connect Event IP=" + adapterFemtoList.getItem(position).getIPAddress() +
                            ",tcpPort=" + adapterFemtoList.getItem(position).getiTcpPort() + ",udpPort=" + adapterFemtoList.getItem(position).getiUdpPort());
                }
            }
        });
        userDefineListView.setOnItemLongClickListener(new RecordOnItemLongClick() {
            @Override
            public void recordOnItemLongClick(AdapterView<?> parent, View view, final int position, long id, String strMsg) {
                if (userDefineListView.canClick()) {
                    DialogConfig dialogConfig = new DialogConfig(mContext, R.style.dialog_style, adapterFemtoList.getList().get(position),
                            new DialogConfig.OnCustomDialogListener() {
                                @Override
                                public void DialogCallBack(String UserName) {
                                    if (!TextUtils.isEmpty(UserName)) {
                                        adapterFemtoList.UpdateUserName(position, UserName);
                                        updateFemtoInfo(adapterFemtoList.getList().get(position));
                                    }
                                }
                            });
                    dialogConfig.show();
                    super.recordOnItemLongClick(parent, view, position, id, "Femto List Item Long Click Event");
                }
            }
        });

        ipAddress1 = (EditText) findViewById(R.id.femto_list_ip_address_first_et);
        ipAddress2 = (EditText) findViewById(R.id.femto_list_ip_address_second_et);
        ipAddress3 = (EditText) findViewById(R.id.femto_list_ip_address_third_et);
        ipAddress4 = (EditText) findViewById(R.id.femto_list_ip_address_fourth_et);
        port  = (EditText) findViewById(R.id.femto_list_port_et);
        ipAddress1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s)){
                    int address1 = Integer.valueOf(s.toString());
                    if(address1 > 255){
                        CustomToast.showToast(mContext, "The value you enter more than the maximum value!");
                        ipAddress1.setText(s.subSequence(0,s.length()-1));
                        ipAddress1.setSelection(s.length()-1);
                        return;
                    }
                    if(s.length() >= 3){
                        ipAddress2.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        
        ipAddress2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s)){
                    int address1 = Integer.valueOf(s.toString());
                    if(address1 > 255){
                        CustomToast.showToast(mContext, "The value you enter more than the maximum value!");
                        ipAddress2.setText(s.subSequence(0,s.length()-1));
                        ipAddress2.setSelection(s.length()-1);
                        return;
                    }
                    if(s.length()>=3){
                        ipAddress3.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ipAddress3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s)){
                    int address1 = Integer.valueOf(s.toString());
                    if(address1 > 255){
                        CustomToast.showToast(mContext, "The value you enter more than the maximum value!");
                        ipAddress3.setText(s.subSequence(0,s.length()-1));
                        ipAddress3.setSelection(s.length()-1);
                        return;
                    }
                    if(s.length()>=3){
                        ipAddress4.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ipAddress4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s)){
                    int address1 = Integer.valueOf(s.toString());
                    if(address1 > 255){
                        CustomToast.showToast(mContext, "The value you enter more than the maximum value!");
                        ipAddress4.setText(s.subSequence(0,s.length()-1));
                        ipAddress4.setSelection(s.length()-1);
                        return;
                    }
                    if(s.length()>=3){
                        port.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        port.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s)){
                    int address1 = Integer.valueOf(s.toString());
                    if(address1 > 65535){
                        CustomToast.showToast(mContext, "The value you enter more than the maximum value!");
                        port.setText(s.subSequence(0,s.length()-1));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ((Button) findViewById(R.id.femto_list_connect_btn)).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                if (TextUtils.isEmpty(ipAddress1.getText().toString())
                        && TextUtils.isEmpty(ipAddress2.getText().toString())
                        && TextUtils.isEmpty(ipAddress3.getText().toString())
                        && TextUtils.isEmpty(ipAddress4.getText().toString())
                        && TextUtils.isEmpty(port.getText().toString())) {
                    CustomToast.showToast(FemtoListActivity.this, "Not Null");
                } else {
                    //lmj 2017-07-03 add test model
                    BTSQuery btsQuery = new BTSQuery();
                    String strBtsQuery = BTSQuery.toXml(btsQuery);
                    EventBus.getDefault().post(new EventBusMsgSendUDPBroadcastMsg(ipAddress1.getText().toString() + "." + ipAddress2.getText().toString() + "." + ipAddress3.getText().toString() + "." + ipAddress4.getText().toString(), 8021, strBtsQuery));
                    EventBus.getDefault().post(new EventBusMsgSendUDPBroadcastMsg(ipAddress1.getText().toString() + "." + ipAddress2.getText().toString() + "." + ipAddress3.getText().toString() + "." + ipAddress4.getText().toString(), 8031, strBtsQuery));
                    ((ProxyApplication)getApplicationContext()).setiUdpPort(8021);
                    connRequest(ipAddress1.getText().toString() + "." + ipAddress2.getText().toString() + "." + ipAddress3.getText().toString() + "." + ipAddress4.getText().toString(), Integer.parseInt(port.getText().toString()));
                }
                super.recordOnClick(v, "Connect Event IP=" + ipAddress1.getText().toString() + "." + ipAddress2.getText().toString() + "." + ipAddress3.getText().toString() + "." + ipAddress4.getText().toString() + ",Port=" + port.getText().toString());
            }
        });

        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setImageResource(R.drawable.btn_refresh_selector);
        rightBtn.setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                CheckFemto();
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0); //强制隐藏键盘
                super.recordOnClick(v, "Refresh Femto List Event");
            }
        });
//        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.image_rotate);
//        LinearInterpolator lin = new LinearInterpolator();
//        operatingAnim.setInterpolator(lin);
//        if(operatingAnim != null && iv_rotate != null){
//            iv_rotate.startAnimation(operatingAnim);
//        } else {
//            findViewById(R.id.rotate_view).setVisibility(View.GONE);
//        }
    }
    private void connRequest(String strIP, int iPort) {
        if (SharePreferenceUtils.getInstance(mContext).getString("password", "").equals("Test")) {
            startAutomaticTestActivity(strIP, iPort);
        } else {
            Connect_Dialog(strIP);
            EventBus.getDefault().post(new EventBusMsgSendTCPMsg(strIP, iPort, ""));
        }
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ib_down_arrows:
                ObjectAnimator animator = ObjectAnimator.ofFloat(downLayout, "translationY", 0.0f, 353 * outMetrics.density);
                animator.setDuration(800);//动画时间
                animator.setInterpolator(new AccelerateDecelerateInterpolator());//动画插值
                animator.setRepeatCount(0);//设置动画重复次数
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {}
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        upBtn.setClickable(true);
                        upArrows.setClickable(true);
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {}
                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });
                animator.start();//启动动画
            break;
            case R.id.ib_up_ib:
            case R.id.iv_up_arrows:
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(downLayout, "translationY", 353 * outMetrics.density, 0.0f);
                animator1.setDuration(800);//动画时间
                animator1.setInterpolator(new AccelerateDecelerateInterpolator());//动画插值
                animator1.setRepeatCount(0);//设置动画重复次数
                animator1.start();//启动动画
                upBtn.setClickable(false);
                upArrows.setClickable(false);
                break;
        }
    }
    @Override
    protected void initData(Bundle savedInstanceState) {
        loadAddress();
        startSettingDialog = new OneBtnHintDialog(this, R.style.dialog_style);
        //init dialog
        proDialog = new CustomProgressDialog(this,R.style.dialog_style);
        proDialog.setCancelable(false);
        CheckFemto();
        registeEventBus();
        if (receiveBroadCast == null) {
            receiveBroadCast = new ReceiveBroadCast();
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            registerReceiver(receiveBroadCast, filter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapterFemtoList.updateFemtoList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        scanDeviceTool.destory();
//        iv_rotate.clearAnimation();
        if (receiveBroadCast != null) {
            unregisterReceiver(receiveBroadCast);
            receiveBroadCast = null;
        }
        if (proDialog.isShowing()) {
            proDialog.dismiss();
        }
        saveAddress();
        if (((ProxyApplication)mContext.getApplicationContext()).getCurSocket() == null &&
                !SharePreferenceUtils.getInstance(this).getString("password", "").equals("Test")) {
            Intent intent = new Intent();
            intent.setClassName("com.bravo.FemtoController", "com.bravo.FemtoController.FunActivity");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityWithAnimation(intent);
        }
    }

    private void updateFemtoInfo(FemtoDataStruct femtoDataStruct) {
        //保存数据
        FemtoList femtoList = ProxyApplication.getDaoSession().getFemtoListDao().queryBuilder().where(FemtoListDao.Properties.Mac.eq(SharePreferenceUtils.getInstance(this).getString(femtoDataStruct.getIPAddress(), "0.0.0.0")),
                FemtoListDao.Properties.UdpPort.eq(femtoDataStruct.getiUdpPort())).build().unique();
        if (femtoList != null) { //数据存在更新
            femtoList.setSSID(femtoDataStruct.getSSID());
            ProxyApplication.getDaoSession().getFemtoListDao().update(femtoList);
        } else { //数据不存在 新添加
            femtoList = new FemtoList(null, femtoDataStruct.getIPAddress(), femtoDataStruct.getMacAddress(), femtoDataStruct.getiTcpPort(), femtoDataStruct.getiUdpPort(), femtoDataStruct.getSSID());
            ProxyApplication.getDaoSession().getFemtoListDao().insert(femtoList);
        }
    }

    private void saveFemtoInfo(FemtoDataStruct femtoDataStruct) {
        FemtoList femtoList = ProxyApplication.getDaoSession().getFemtoListDao().queryBuilder().where(FemtoListDao.Properties.Mac.eq(SharePreferenceUtils.getInstance(this).getString(femtoDataStruct.getIPAddress(), "0.0.0.0")),
                FemtoListDao.Properties.UdpPort.eq(femtoDataStruct.getiUdpPort())).build().unique();
        if (femtoList != null) { //数据存在更新
            femtoDataStruct.setiTcpPort(femtoList.getPort());
            femtoDataStruct.setSSID(femtoList.getSSID());
        } else {//不存在保存
            femtoList = new FemtoList(null, femtoDataStruct.getIPAddress(), femtoDataStruct.getMacAddress(), femtoDataStruct.getiTcpPort(),femtoDataStruct.getiUdpPort(), femtoDataStruct.getSSID());
            ProxyApplication.getDaoSession().getFemtoListDao().insert(femtoList);
        }
    }

    private void CheckFemto() {
        Message msg = handler.obtainMessage();
        WifiAdmin mWifiAdmin = new WifiAdmin(this);
        WifiAP mWifiAP = new WifiAP(this);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mWifiAdmin.isWifiConnected() && !mWifiAP.isApEnabled() && !mBluetoothAdapter.isEnabled()) {
            msg.what = HINT_DIALOG;
        } else if (mWifiAdmin.isWifiConnected()) {
            msg.what = WIFI_CLIENT_MODE;
        } else if (mWifiAP.isApEnabled()) {
            msg.what = WIFI_AP_MODE;
        } else if (mBluetoothAdapter.getProfileConnectionState(5) == BluetoothProfile.STATE_CONNECTED) {
            msg.what = BLUETOOTH_MODE;
        } else {
            msg.what = HINT_DIALOG;
        }
        handler.sendMessage(msg);
    }

    private void InitFemtoList(int iWifiMode) {
        adapterFemtoList.removeAll();
        //开启Tcp注册用户
        BTSQuery btsQuery = new BTSQuery();
        String strBtsQuery = BTSQuery.toXml(btsQuery);
        switch (iWifiMode) {
            case WIFI_AP_MODE:
                WifiAP mWifiAP = new WifiAP(mContext);
                ArrayList<FemtoDataStruct> femtoDataStructArrayList = mWifiAP.getIpList();
                for (int i = 0; i < femtoDataStructArrayList.size(); i++) {
                    EventBus.getDefault().post(new EventBusMsgSendUDPBroadcastMsg(femtoDataStructArrayList.get(i).getIPAddress(), 8021, strBtsQuery));
                    EventBus.getDefault().post(new EventBusMsgSendUDPBroadcastMsg(femtoDataStructArrayList.get(i).getIPAddress(), 8031, strBtsQuery));
                }
                break;
            case BLUETOOTH_MODE:
                mWifiAP = new WifiAP(mContext);
                femtoDataStructArrayList = mWifiAP.getIpList();
                for (int i = 0; i < femtoDataStructArrayList.size(); i++) {
                    EventBus.getDefault().post(new EventBusMsgSendUDPBroadcastMsg(femtoDataStructArrayList.get(i).getIPAddress(), 8021, strBtsQuery));
                    EventBus.getDefault().post(new EventBusMsgSendUDPBroadcastMsg(femtoDataStructArrayList.get(i).getIPAddress(), 8031, strBtsQuery));
                }
                break;
            case WIFI_CLIENT_MODE:
                WifiAdmin wifiAdmin = new WifiAdmin(this);
                EventBus.getDefault().post(new EventBusMsgSendUDPBroadcastMsg(wifiAdmin.getGateway() + "255", 8021, strBtsQuery));
                EventBus.getDefault().post(new EventBusMsgSendUDPBroadcastMsg(wifiAdmin.getGateway() + "255", 8031, strBtsQuery));
                break;
            default:
                break;
        }
    }

    private void Connect_Dialog(String strIP) {
        if (!proDialog.isShowing()) {
            proDialog.show();
            proDialog.setTitle("Connect " + strIP);
            proDialog.setContent("Please wait...");
            bDialogState = true;
            final int TIMEOUT = 30;
            Thread thread = new Thread() {
                public void run() {
                    try {
                        int iCount = 0;
                        while (bDialogState && iCount < TIMEOUT) {
                            iCount++;
                            sleep(300);
                        }

                        if (iCount >= TIMEOUT) {
                            Message msg = new Message();
                            msg.what = CONNECT_TIMEOUT;
                            handler.sendMessage(msg);
                        } else {
                            if (bConnectState) {
                                proDialog.dismiss();
                                StartFunActivity();
                            }
                        }
                        proDialog.dismiss();
                    } catch (InterruptedException e) {
                        // TODO 自动生成的 catch 块
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CONNECT_TIMEOUT:
                    CustomToast.showToast(mContext, "Connect Femto Timeout");
                    break;
                case HINT_DIALOG:
                    adapterFemtoList.removeAll();
                    StartSettingApk();
                    break;
                case BLUETOOTH_MODE:
                case WIFI_CLIENT_MODE:
                case WIFI_AP_MODE:
                    InitFemtoList(msg.what);
                    break;
                case BTS_STATUS_ZERO:
                    //ShowDisconnectDialog("BTS=0&Band=0,System Error！！！");
                    CustomToast.showToast(mContext, "BTS=0,Band=N/A,System Error！！！");
                    break;
                case BACK_PRESSED:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    private void updateFemtoList() {
        if(adapterFemtoList.getCount() >= 4){
            downArrows.setVisibility(View.VISIBLE);
        } else {
            if (adapterFemtoList.getCount() == 0) {
                CustomToast.showToast(FemtoListActivity.this, "Not Found Femto");
            }
            downArrows.setVisibility(View.GONE);
        }
        adapterFemtoList.updateFemtoList();
    }

    private void StartFunActivity() {
        String Bts = SharePreferenceUtils.getInstance(this).getString("status_notif_bts" + ((ProxyApplication) getApplicationContext()).getCurSocketAddress() + ((ProxyApplication) getApplicationContext()).getiTcpPort(), "1");
        String Band = SharePreferenceUtils.getInstance(this).getString("status_notif_band" + ((ProxyApplication) getApplicationContext()).getCurSocketAddress() + ((ProxyApplication) getApplicationContext()).getiTcpPort(), "0");
        if (Bts.equals("0") && Band.equals("0")) {
            Message msg = handler.obtainMessage();
            msg.what = BTS_STATUS_ZERO;
            handler.sendMessage(msg);
//        } else if(Bts.equals("5")) {
//            Intent intent = new Intent(this, TranslucentActivity.class);
//            intent.putExtra("dialogType",TranslucentActivity.DIALOG_TYPE_ONE);
//            intent.putExtra("title", "Warning");
//            intent.putExtra("message", "Please Reboot Femto");
//            startActivity(intent);
        } else {
            saveBcastHistory();
        }

        Message msg = handler.obtainMessage();
        msg.what = BACK_PRESSED;
        handler.sendMessage(msg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void connectResult(String strConnectState) {
        if (SharePreferenceUtils.getInstance(mContext).getString("password", "").equals("Test")) {
            return;
        }
        switch (strConnectState) {
            case EventBusMsgConstant.TCP_CONNECT_FAILED:
                bConnectState = false;
                bDialogState = false;
                CustomToast.showToast(FemtoListActivity.this, "Connect Femto failure");
                break;
            case EventBusMsgConstant.TCP_CONNECT_ALREADY:
                Logs.d(TAG, "EventBusMsgConstant.TCP_CONNECT_ALREADY");
                bConnectState = true;
                bDialogState = false;//lmj 2017 7 25
                break;
            case EventBusMsgConstant.TCP_CONNECT_SUCCESS:
                Logs.d(TAG, "EventBusMsgConstant.TCP_CONNECT_SUCCESS");
                bConnectState = true;
                bDialogState = false;
                break;
            case EventBusMsgConstant.REGISTER_FAILED:
                bConnectState = false;
                bDialogState = false;
                break;
            default:
                break;
        }
    }

    public class ReceiveBroadCast extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            WifiAP mWifiAP = new WifiAP(mContext);
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION) && !mWifiAP.isApEnabled() && !mBluetoothAdapter.isEnabled()) {//wifi连接上与否
                //Logs.e(TAG, "网络状态改变");
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    EventBus.getDefault().post(EventBusMsgConstant.CLEAR_ALL_SOCKET);
                    adapterFemtoList.removeAll();
                }
            }
        }
    }

    // start setting apk
    private void StartSettingApk() {
        if (!startSettingDialog.isShowing()) {
            startSettingDialog.setListener(new OneBtnHintDialog.BtnClickListener() {
                @Override
                public void onBtnClick(View v) {
                    PackageManager packageManager = getPackageManager();
                    Intent intent = packageManager.getLaunchIntentForPackage("com.android.settings");
                    startActivity(intent);
                }
            });
            startSettingDialog.setCanceledOnTouchOutside(false);
            startSettingDialog.show();
            startSettingDialog.setBtnContent("Start");
            startSettingDialog.setTitle("Warning");
            startSettingDialog.setContent("Please Connect WIFI or Bluetooth");
        }

    }

    private void saveAddress() {
        SharePreferenceUtils.getInstance(mContext).setString("ip1", ipAddress1.getText().toString());
        SharePreferenceUtils.getInstance(mContext).setString("ip2", ipAddress2.getText().toString());
        SharePreferenceUtils.getInstance(mContext).setString("ip3", ipAddress3.getText().toString());
        SharePreferenceUtils.getInstance(mContext).setString("ip4", ipAddress4.getText().toString());
        SharePreferenceUtils.getInstance(mContext).setString("connect_port", port.getText().toString());
    }

    private void loadAddress() {
        ipAddress1.setText(SharePreferenceUtils.getInstance(mContext).getString("ip1", ""));
        ipAddress2.setText(SharePreferenceUtils.getInstance(mContext).getString("ip2", ""));
        ipAddress3.setText(SharePreferenceUtils.getInstance(mContext).getString("ip3", ""));
        ipAddress4.setText(SharePreferenceUtils.getInstance(mContext).getString("ip4", ""));
        port.setText(SharePreferenceUtils.getInstance(mContext).getString("connect_port", ""));
    }

    //首次连接Femto,如果Bts=3,证明小区已起来,保存当前小区信息.
    private void saveBcastHistory() {
        String strBts = SharePreferenceUtils.getInstance(this).getString("status_notif_bts" +
                ((ProxyApplication)getApplicationContext()).getCurSocketAddress() +
                ((ProxyApplication)getApplicationContext()).getiTcpPort(), "1");
        if (strBts.equals("3") || strBts.equals("4")) {
            saveBcastInfo(mContext, false);
        }
    }

    private void startAutomaticTestActivity(String ip, int port) {
        Intent intent = new Intent();
        intent.setClassName("com.bravo.FemtoController", "com.bravo.test.AutomaticTestActivity");
        intent.putExtra("ip", ip);
        intent.putExtra("port", port);
        startActivityWithAnimation(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void EventBusMsgDevResponse(EventBusMsgDevResponse eventBusMsgDevResponse) {
        Logs.w(TAG, "EventBusMsgDevResponse=" + eventBusMsgDevResponse.getIpAddress() + ","+ eventBusMsgDevResponse.getProt(), "Record_Event", true);
        if (eventBusMsgDevResponse.getBtsOnline().getFemtoBand().contains(",")) {
            SharePreferenceUtils.getInstance(this).setBoolean("multi_band_status" + eventBusMsgDevResponse.getBtsOnline().getFemtoSn(), true);
            SharePreferenceUtils.getInstance(this).setString("multi_band_info" + eventBusMsgDevResponse.getBtsOnline().getFemtoSn(),
                    eventBusMsgDevResponse.getBtsOnline().getFemtoBand());
        } else {
            SharePreferenceUtils.getInstance(this).setBoolean("multi_band_status" + eventBusMsgDevResponse.getBtsOnline().getFemtoSn(), false);
        }
        WifiAP mWifiAP = new WifiAP(mContext);
        ArrayList<FemtoDataStruct> femtoDataStructArrayList = mWifiAP.getIpList();
        for (int i = 0; i < femtoDataStructArrayList.size(); i++) {
            if (femtoDataStructArrayList.get(i).getIPAddress().equals(eventBusMsgDevResponse.getIpAddress())) {
                femtoDataStructArrayList.get(i).setiUdpPort(eventBusMsgDevResponse.getProt());
                femtoDataStructArrayList.get(i).setiTcpPort(eventBusMsgDevResponse.getProt());
                saveFemtoInfo(femtoDataStructArrayList.get(i));
                adapterFemtoList.addFemto(femtoDataStructArrayList.get(i));
                updateFemtoList();
                return;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ConnReqNotif(ConnRequestNotif crn) {
        adapterFemtoList.updateFemtoList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void TargetAttach(TargetAttach ta) {
        adapterFemtoList.updateFemtoList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void unregisterResult(String strState) {
        switch (strState) {
            case EventBusMsgConstant.UNREGISTE_SOCKET_SUCCESS:
                adapterFemtoList.updateFemtoList();
                break;
            default:
                break;
        }
    }
//    /**
//     * 开启动画
//     */
//    public void startRotate(){
//        findViewById(R.id.rotate_view).setVisibility(View.VISIBLE);
//    }
//    /**
//     * 关闭动画
//     */
//    public void stopRotate() {
//        findViewById(R.id.rotate_view).setVisibility(View.GONE);
//    }
}
