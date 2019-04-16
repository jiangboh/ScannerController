package com.bravo.FemtoController;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bravo.BlueTooth.EventBusTukTukMsg;
import com.bravo.R;
import com.bravo.audio.VoiceSpeaker;
import com.bravo.audio.VoiceTemplate;
import com.bravo.data_ben.PositionDataStruct;
import com.bravo.fragments.SerializableHandler;
import com.bravo.no_http.download.DownloadListener;
import com.bravo.no_http.download.DownloadQueue;
import com.bravo.no_http.download.DownloadRequest;
import com.bravo.no_http.rest.OnResponseListener;
import com.bravo.no_http.rest.Request;
import com.bravo.no_http.rest.RequestQueue;
import com.bravo.parse_generate_xml.ErrorNotif;
import com.bravo.parse_generate_xml.Status;
import com.bravo.scanner.FragmentScannerConfig;
import com.bravo.scanner.FragmentpPositionListen;
import com.bravo.socket_service.EventBusMsgConstant;
import com.bravo.utils.Logs;
import com.bravo.utils.SharePreferenceUtils;
import com.bravo.utils.SimpleDateUtils;
import com.bravo.utils.StatusBarCompat;
import com.bravo.utils.Utils;
import com.bravo.wifi.WifiAdmin;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *@author jintian.ming
 *@createDate 2016/12/20
 * 完成activity基类的封装（后继会根据具体需要添加）
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{

    private Timer timer;
    private final int BLACK_IMSI_ONLING = 1;
    private final int BLACK_IMSI_OFFLING = 2;

    protected String TAG = getClass().getSimpleName();
    protected Context mContext;
    protected RequestQueue mRequestQueue;
    protected DownloadQueue mDownloadQueue;
    protected ImageView statusConn,statusError,statusGps;
    protected TextView statusBts,statusTech,statusWifi,statusBattery,statusBattery1,statusTime,txTuktuk;
    protected Handler updateTimeHandler;
    protected static final int UPDATE_TIME_FLAG = 1111;

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        mContext = this;
        mRequestQueue = ((ProxyApplication)getApplication()).getmRequestQueue();
        mDownloadQueue = ((ProxyApplication)getApplication()).getmDownloadQueue();
        initView();
        initData(savedInstanceState);
    }

    /**
     * 初始化view
     */
    protected abstract void initView();

    /**
     * 初始化Status View
     */
    public void initStatusView(){
        findViewById(R.id.statusbar_layout).setVisibility(View.VISIBLE);
        statusConn = (ImageView) findViewById(R.id.connect_status_iv);
        txTuktuk = (TextView) findViewById(R.id.tuktuk_enter);
        statusError = (ImageView) findViewById(R.id.error_status_iv);
        statusGps = (ImageView) findViewById(R.id.gps_status_iv);
        statusBts = (TextView) findViewById(R.id.bts_status_tv);
        statusTech = (TextView) findViewById(R.id.tech_status_tv);
        statusWifi = (TextView) findViewById(R.id.wifi_status_tv);
        statusBattery = (TextView) findViewById(R.id.battery_status_tv);
        statusBattery1 = (TextView) findViewById(R.id.battery_status_tv1);
        statusTime = (TextView) findViewById(R.id.time_status_tv);
        statusConn.setOnClickListener(this);
        statusError.setOnClickListener(this);
        statusGps.setOnClickListener(this);
        statusBts.setOnClickListener(this);
        txTuktuk.setOnClickListener(this);

        statusConn.setVisibility(View.GONE);
        statusError.setVisibility(View.GONE);
        statusGps.setVisibility(View.GONE);
        txTuktuk.setVisibility(View.GONE);

        String time = SimpleDateUtils.formatTime("HH:mm");
        //////////////////////////////////////////////////////
        statusTime.setText(time);
        updateTimeHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what == UPDATE_TIME_FLAG){
                    String time = SimpleDateUtils.formatTime("HH:mm");
                    statusTime.setText(time);
                    WifiAdmin wifiAdmin = new WifiAdmin(mContext);
                    showWifiStrength(wifiAdmin.getWifiLevel(),statusWifi);
                    updateTimeHandler.sendEmptyMessageDelayed(UPDATE_TIME_FLAG,10000);
                }
                return false;
            }
        });
        updateTimeHandler.sendEmptyMessageDelayed(UPDATE_TIME_FLAG,10000);
        int strengthLevel = SharePreferenceUtils.getInstance(this).getInt("strengthLevel", 101);
        showBatteryStrength(strengthLevel,statusBattery,"");
        registeEventBus();
    }

    protected void registeEventBus(){
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    private void showWifiStrength(int strengthLevel,View wifiView){
        if(strengthLevel >= -55){
            wifiView.setBackgroundResource(R.drawable.ic_wifi_signal_4_dark);
        }else if(strengthLevel < -55 && strengthLevel >= -70){
            wifiView.setBackgroundResource(R.drawable.ic_wifi_signal_3_dark);
        }else if(strengthLevel < -70 && strengthLevel >= -85){
            wifiView.setBackgroundResource(R.drawable.ic_wifi_signal_2_dark);
        }else if(strengthLevel < -85 && strengthLevel >= -100){
            wifiView.setBackgroundResource(R.drawable.ic_wifi_signal_1_dark);
        }
    }

    private void showBatteryStrength(int strengthLevel,View batteryView,String strDevType){
        SharePreferenceUtils.getInstance(this).setInt("strengthLevel", strengthLevel);
        if (strDevType.indexOf("M100") != -1) {
            statusBattery1.setText("");
        } else if(strDevType.indexOf("S2600") != -1 ||
                strDevType.indexOf("S2602") != -1) {
            statusBattery1.setText("");
            strengthLevel = 102;
        }else if(strengthLevel>0 && strengthLevel <= 100) {
            statusBattery1.setText(strengthLevel + "%");
        }else{
            statusBattery1.setText("Charging");
        }
        if(0 < strengthLevel && strengthLevel <= 10){
            batteryView.setBackgroundResource(R.drawable.icon_battery10);
        }else if(strengthLevel <= 20 && strengthLevel > 10){
            batteryView.setBackgroundResource(R.drawable.icon_battery20);
        }else if(strengthLevel <= 30 && strengthLevel > 20){
            batteryView.setBackgroundResource(R.drawable.icon_battery30);
        }else if(strengthLevel <= 40 && strengthLevel > 30){
            batteryView.setBackgroundResource(R.drawable.icon_battery40);
        }else if(strengthLevel <= 50 && strengthLevel > 40){
            batteryView.setBackgroundResource(R.drawable.icon_battery50);
        }else if(strengthLevel <= 60 && strengthLevel > 50){
            batteryView.setBackgroundResource(R.drawable.icon_battery60);
        }else if(strengthLevel <= 70 && strengthLevel > 60){
            batteryView.setBackgroundResource(R.drawable.icon_battery70);
        }else if(strengthLevel <= 80 && strengthLevel > 70){
            batteryView.setBackgroundResource(R.drawable.icon_battery80);
        }else if(strengthLevel <= 90 && strengthLevel > 80){
            batteryView.setBackgroundResource(R.drawable.icon_battery90);
        }else if(strengthLevel <= 100 && strengthLevel > 90){
            batteryView.setBackgroundResource(R.drawable.icon_battery100);
        }else {
            batteryView.setBackgroundResource(R.drawable.icon_battery_charging);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStatus();
    }

    public void refreshStatus(){
        if (statusConn != null) {
            if (((ProxyApplication) getApplicationContext()).getCurSocketAddress() == null) {
                statusConn.setImageResource(R.drawable.icon_conn_disconn);
            } else {
                statusConn.setImageResource(R.drawable.icon_conn_default);
            }
        }
        if(statusError != null){
            if(Utils.hasAlert()){
                statusError.setImageResource(R.drawable.icon_error_errors);
            }else{
                statusError.setImageResource(R.drawable.icon_error_default);
            }
        }
        if (findViewById(R.id.statusbar_layout) != null) {
            if (findViewById(R.id.statusbar_layout).getVisibility() == View.VISIBLE) {
                if (((ProxyApplication) getApplicationContext()).getCurSocket() != null) {
                    WifiAdmin wifiAdmin = new WifiAdmin(mContext);
                    showWifiStrength(wifiAdmin.getWifiLevel(),statusWifi);
                    /*String batteryLength = SharePreferenceUtils.getInstance(this).getString("status_notif_battery_level" +
                            ((ProxyApplication) getApplicationContext()).getCurSocketAddress() +
                            ((ProxyApplication) getApplicationContext()).getiTcpPort(), "N/A");
                    Logs.d("123456","sharePreference读到的电池电量为：" + batteryLength);
                    if("N/A".equals(batteryLength)){
                        showBatteryStrength(101,statusBattery);
                    }else{
                        showBatteryStrength(Integer.valueOf(batteryLength),statusBattery);
                    }*/
                    statusBts.setText(SharePreferenceUtils.getInstance(this).getString("status_notif_bts" +
                            ((ProxyApplication) getApplicationContext()).getCurSocketAddress() +
                            ((ProxyApplication) getApplicationContext()).getiTcpPort(), "N/A"));
                    statusTech.setText(SharePreferenceUtils.getInstance(this).getString("status_notif_tech" +
                            ((ProxyApplication) getApplicationContext()).getCurSocketAddress() +
                            ((ProxyApplication) getApplicationContext()).getiTcpPort(), "N/A"));
                } else {
                    statusWifi.setVisibility(View.GONE);
                    statusBattery.setVisibility(View.GONE);
                    statusBattery1.setVisibility(View.GONE);
                    statusBts.setVisibility(View.GONE);
                    statusTech.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.connect_status_iv:
                //TODO
                /*Intent intent = new Intent();
                intent.setClassName("com.bravo.FemtoController", "com.bravo.FemtoController.FemtoListActivity");
                startActivityWithAnimation(intent);*/
                break;
            case R.id.error_status_iv:
                //Intent intent1 = new Intent(this,ErrorMsgActivity.class);
                //startActivityWithAnimation(intent1);
                break;
            case R.id.gps_status_iv:
                //TODO
                //startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                break;
            case R.id.bts_status_tv:
                //TODO
                break;
            case R.id.tuktuk_enter:
                //检查蓝牙服务是否打开
//                if (!isServiceRunning(mContext, "com.bravo.BlueTooth.tukTukService")){//检测蓝牙服务是否启动
                //intent = new Intent(this, tukTukService.class);
                //startService(intent);
                break;
        }
    }

    /**
     * 初始化data
     * @param savedInstanceState
     */
    protected abstract void initData(Bundle savedInstanceState);

    /**
     * 设置statusBar的背景色，该方法在setContentView之后调用才有效果
     * 只对4.4以后的版本有效果
     * @param colorRes
     */
    protected void setStatusBarsColor(int colorRes){
        StatusBarCompat.compat(this, ContextCompat.getColor(this, colorRes));
    }

    public void startActivityWithAnimation(Intent intent)
    {
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_no_anim);
    }

    /**
     * 发起一个请求。
     *
     * @param what     what.
     * @param request  请求对象。
     * @param listener 结果监听。
     * @param <T>      要请求到的数据类型。
     */
    public <T> void request(int what, Request<T> request, OnResponseListener<T> listener) {
        request.setCancelSign(TAG);
        mRequestQueue.add(what, request, listener);
    }

    public void downLoad(int what, DownloadRequest request, DownloadListener listener){
        request.setCancelSign(TAG);
        mDownloadQueue.add(what,request,listener);
    }

    @Override
    public void onBackPressed()
    {
        overridePendingTransition(0, R.anim.out_to_right);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        mRequestQueue.cancelBySign(TAG);
        mDownloadQueue.cancelBySign(TAG);
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        if(updateTimeHandler != null){
            updateTimeHandler.removeMessages(UPDATE_TIME_FLAG);
            updateTimeHandler = null;
        }
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void errorNotif(ErrorNotif en) {
        if(Utils.isTopActivy(this,TAG)){
            if(Utils.isIgnoreError(en)){
//                Logs.d("123456","接收到已忽略的错误：" + en.toString());
                return;
            }else{
//                Logs.e("123456","接收到栈顶activity需要处理的错误：" + en.toString());
                Utils.addError(en);
                refreshStatus();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void StatusNotif(Status s) {
        //battery bts
        if (statusBattery != null) {
            statusWifi.setVisibility(View.VISIBLE);
            statusBattery.setVisibility(View.VISIBLE);
            statusBattery1.setVisibility(View.VISIBLE);
            statusBts.setVisibility(View.VISIBLE);
            statusTech.setVisibility(View.VISIBLE);
            //S2600-1727000002
            if (s.getBaterryCharging().equals("FALSE")) {
                showBatteryStrength(Integer.valueOf(s.getBaterryLevel()), statusBattery, s.getFemtoSn());
            } else {
                showBatteryStrength(102,statusBattery,s.getFemtoSn());
            }
            statusBts.setText(s.getBtsState());
            statusTech.setText(s.getTech());
        }

    }

    private boolean checkGPSIsOpen() {
        boolean isOpen;
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        return isOpen;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void connectResult(String strConnectState) {
        if (strConnectState.equals(EventBusMsgConstant.TCP_CONNECT_SUCCESS)) {
            refreshStatus();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void TTConnectStatus(EventBusTukTukMsg eventBusTukTukMsg) {
        switch (eventBusTukTukMsg.getAction()) {
            case EventBusTukTukMsg.BT_STATUS_ALREADY_CONNECT:
                Intent  intent = new Intent();
                intent.setClassName("com.bravo.FemtoController", "com.bravo.BlueTooth.TTConfigActivity");
                startActivityWithAnimation(intent);
                break;
            case EventBusTukTukMsg.BT_STATUS_NOCONNECT:
                intent = new Intent();
                intent.setClassName("com.bravo.FemtoController", "com.bravo.BlueTooth.tukTukActivity");
                startActivityWithAnimation(intent);
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void TargetPosition(PositionDataStruct data) {

        Message message = new Message();
        message.what = BLACK_IMSI_ONLING;
        Bundle bundle = new Bundle();
        bundle.putInt("RSSI",data.getValue());
        bundle.putInt("GAIN",data.getRxGain());
        message.setData(bundle);
        handler.sendMessage(message);
    }

    class MyTimer extends TimerTask implements Serializable {
        @Override
        public void run() {
            Message message = new Message();
            message.what = BLACK_IMSI_OFFLING;
            handler.sendMessage(message);
        }
    }

    private Handler handler = new SerializableHandler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BLACK_IMSI_ONLING:
                    int iRssi = msg.getData().getInt("RSSI",-100);

                    SharedPreferences sp = mContext.getSharedPreferences("FragmentpPositionListen", MODE_PRIVATE);
                    boolean soundOpen = sp.getBoolean("soundOpen",false);

                    SharedPreferences sp1 = mContext.getSharedPreferences(FragmentScannerConfig.TABLE_NAME, MODE_PRIVATE);
                    boolean openOffset = sp1.getBoolean(FragmentScannerConfig.tn_OpenOffset,FragmentScannerConfig.DefultOpenOffset);

                    if (openOffset) {
                        txTuktuk.setText(String.valueOf(iRssi - msg.getData().getInt("GAIN",0)));
                    } else {
                        txTuktuk.setText(String.valueOf(iRssi));
                    }
                    txTuktuk.setVisibility(View.VISIBLE);

                    if (timer != null) {
                        Logs.d(TAG, "启动定时检查在线状态线程。。。");
                        timer.cancel();
                        timer = null;
                    }
                    timer = new Timer();
                    timer.schedule(new MyTimer(), 10000);  //10秒未上报，认为黑名单用户下线了

                    if (soundOpen && !FragmentpPositionListen.isOpen) {
                        List<String> list = new VoiceTemplate()
                                .numString(String.valueOf(Math.abs(iRssi)))
                                .gen();

                        VoiceSpeaker.getInstance().startSpeak(mContext, list);
                    }
                    break;

                case BLACK_IMSI_OFFLING:
                    txTuktuk.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }

        }
    };
}
