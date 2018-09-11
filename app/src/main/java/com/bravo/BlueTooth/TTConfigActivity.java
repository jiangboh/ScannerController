package com.bravo.BlueTooth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.OneBtnHintDialog;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.utils.Logs;
import com.bravo.utils.SharePreferenceUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TTConfigActivity extends Activity {
    //channel区间
    private final String TAG = "TTConfigActivity";
    private final int iGSMChannels[][] = new int[][]{{0,0,512,512,0,128,0,0,975},{0,0,810,885,0,251,0,124,1023}};
    private final int iUMTSChannels[][] = new int[][]{{0,10562,9662,1162,1537,4357,4387,2237,2937},{0,10838,9938,1513,1738,4458,4413,2563,3088}};
    private final static int iLteChannels[][] = new int[][]{{0,0,600,1200,1950,2400,2650,2750,3450,3800,4150,
            4750,5010,5180,5280,0,0,5730,5850,6000,6150,6450,6600,7500,7700,8040,8690,9040,9210,9660,9770,
            9870,0,36000,36200,36350,36950,37550,37750,38250,38650,39650,41590,43590,45590 },
            {0,599,1199,1949,2399,2649,2749,3449,3799,4149,4749,4949,5179,5279,5379,0,0,5849,5999,6149,6449,
                    6599,7399,7699,8039,8689,9039,9209,9659,9769,9869,9919,0,36199,36349,36949,37549,37749,38249,
                    38649,39649,41589,43589,45589,46589}};
    private int iUMTSDLULArrays[][] = new int[][]{{0, 10550, 9650, 1150, 1525, 4345, 830, 2225, 2925, 1749, 1710, 1427},
            {0, 9600, 9250, 925, 1300, 4120, 875, 2000, 2700, 1844, 2110, 1475}};
    private final static int iUMTS2Additional[] = new int[]{412,437,462,487,512,537,562,587,612,637,662,687};
    private final static int iUMTS5Additional[] = new int[]{1007,1012,1032,1037,1062,1087};
    private final static int iUMTS4Additional[] = new int []{1887,1912,1937,1962,1987,2012,2037,2062,2087};
    private final static int iUMTS7Additional[] = new int []{2587,2612,2637,2662,2687,2712,2737,2762,2787,2812,2837,2862,2887,2912};
    private Spinner spinnerTech;
    private Spinner spinnerBand;
    private EditText etChannel;
    private ImageView ivBattery;
    private TextToSpeech tts;
    private TextView tvMac;
    private TextView tvBattery;
    private Spinner spinnerSound;
    private Spinner spinnerMode;
    private CircleProgressBar circleProgressBar; // 自定义的进度条
    private int[] colors = new int[] {Color.parseColor("#27B197"), Color.parseColor("#00A6D5") };
    //title
    private ImageView ivConfig;
    private boolean bInitMode = true;
    private boolean bInitBand = false;
    private int iBand;
    private ImageView ivConnStatus;
    private OneBtnHintDialog hintDialog;
    private Timer initTimer;//小区定时器
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ttconfig);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initView();
        initData();
    }
    class MyTimer extends TimerTask implements Serializable {

        @Override
        public void run() {
            EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_GET_DEVICE_REQUEST));
            EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_INIT_REQUEST));
        }
    }
    public void initView() {
        circleProgressBar = (CircleProgressBar) findViewById(R.id.circleProgressBar);
        circleProgressBar.setFirstColor(Color.LTGRAY);
        circleProgressBar.setSecondColor(Color.BLUE);
        circleProgressBar.setColorArray(colors);
        circleProgressBar.setCircleWidth(13);
        spinnerBand = (Spinner) findViewById(R.id.tt_band);
        spinnerTech = (Spinner) findViewById(R.id.tt_tech);
        spinnerTech.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    updateBand();
                    if (bInitBand) {
                        bInitBand = false;
                        initBand();
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //tuktuk channel
        etChannel = (EditText) findViewById(R.id.tt_channel);
        etChannel.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                configChannel();
                return false;
            }
        });
        ivConnStatus = (ImageView) findViewById(R.id.tt_connect_status);
        ivConnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName("com.bravo.FemtoController", "com.bravo.BlueTooth.tukTukActivity");
                startActivity(intent);
            }
        });
        tvMac = (TextView) findViewById(R.id.tt_mac_address);
        ivBattery = (ImageView) findViewById(R.id.tt_battery);
        tvBattery = (TextView) findViewById(R.id.tt_tv_battery);
        spinnerSound = (Spinner) findViewById(R.id.tt_sound);
        spinnerMode = (Spinner) findViewById(R.id.tt_mode);
        spinnerMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (bInitMode) {
                    bInitMode = false;
                } else {
                    byte data[] = {(byte) position};
                    EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_WRITE, EventBusTukTukMsg.CHARACTERISTIC_UUID7, data));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ivConfig = (ImageView) findViewById(R.id.iv_activity_right);
        ivConfig.setImageResource(R.drawable.btn_config_selector);
        ivConfig.setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                super.recordOnClick(v, "Setting tuktuk channel Event");
                configChannel();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0); //强制隐藏键盘
            }
        });
        ivConfig.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent  intent = new Intent();
                intent.setClassName("com.bravo.FemtoController", "com.bravo.BlueTooth.TTSettingActivity");
                startActivity(intent);
                return true;//true只执行长按，false还会执行单击
            }
        });
        ((TextView) findViewById(R.id.tv_activity_title)).setText("TT");
        findViewById(R.id.iv_activity_back).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                onBackPressed();
                super.recordOnClick(v, "Back Event " + TAG);
            }
        });
    }
    
    public void initData() {
        initTTS();
        hintDialog = new OneBtnHintDialog(this, R.style.dialog_style);
        initTimer = new Timer();
        initTimer.schedule(new MyTimer(), 500, 2000);
        loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveData();
        if (tts != null) { // 关闭TTS引擎
            tts.shutdown();
        }
        if (hintDialog.isShowing()) {
            hintDialog.dismiss();
        }
        if (initTimer != null) {
            initTimer.purge();
            initTimer.cancel();
            initTimer = null;
        }
        EventBus.getDefault().unregister(this);
    }

    private void configChannel() {
        if (!TextUtils.isEmpty(etChannel.getText().toString()) && checkChannel(Integer.parseInt(etChannel.getText().toString()))) {
            etChannel.setError(null);
        } else {
            etChannel.setError(legalChannel());
        }
    }

    private String legalChannel() {
        int iBand = Integer.parseInt(spinnerBand.getSelectedItem().toString());
        switch (spinnerTech.getSelectedItemPosition()) {
            case 0://2G
                if (iBand == 8) {//0-124,975-1023
                    return "(" + iGSMChannels[0][iBand] + "-" + iGSMChannels[1][iBand] + "," +
                            iGSMChannels[0][iBand - 1] + "-" + iGSMChannels[1][iBand - 1] + ")";
                } else {
                    return "(" + iGSMChannels[0][iBand] + "-" + iGSMChannels[1][iBand] + ")";
                }
            case 1://3G
                int iOffset = iUMTSDLULArrays[1][iBand] - iUMTSDLULArrays[0][iBand];
                String strExDLBand = "", strExULBand = "";
                switch (iBand) {
                    case 2:
                        for (int i = 0;i < iUMTS2Additional.length; i++) {
                            strExDLBand += "," + iUMTS2Additional[i];
                            strExULBand += "," + (iUMTS2Additional[i] + iOffset);
                        }
                        break;
                    case 4:
                        for (int i = 0;i < iUMTS4Additional.length; i++) {
                            strExDLBand += "," + iUMTS4Additional[i];
                            strExULBand += "," + (iUMTS4Additional[i] + iOffset);
                        }
                        break;
                    case 5:
                        for (int i = 0;i < iUMTS5Additional.length; i++) {
                            strExDLBand += "," + iUMTS5Additional[i];
                            strExULBand += "," + (iUMTS5Additional[i] + iOffset);
                        }
                        break;
                    case 7:
                        for (int i = 0;i < iUMTS7Additional.length; i++) {
                            strExDLBand += "," + iUMTS7Additional[i];
                            strExULBand += "," + (iUMTS7Additional[i] + iOffset);
                        }
                        break;
                }
                return /*"DL(" + iUMTSChannels[0][iBand] + "-" + iUMTSChannels[1][iBand] + strExDLBand + "),*/"(" +
                        (iUMTSChannels[0][iBand] + iOffset) + "-" + (iUMTSChannels[1][iBand] + iOffset) + strExULBand + ")";
            case 2://4G
                if (iBand >=0 && iBand <= 8) {
                    return /*"DL(" + iLteChannels[0][iBand] + "-" + iLteChannels[1][iBand] + "),*/"(" +
                            (iLteChannels[0][iBand] + 18000) + "-" + (iLteChannels[1][iBand] + 18000) + ")";
                } else if (iBand == 30 || iBand == 31) {
                    return /*"DL(" + iLteChannels[0][iBand] + "-" + iLteChannels[1][iBand] + "),*/"(" +
                            (iLteChannels[0][iBand] + 17890) + "-" + (iLteChannels[1][iBand] + 17890) + ")";
                } else {
                    return "(" + iLteChannels[0][iBand] + "-" + iLteChannels[1][iBand] + ")";
                }
            default:
                return "Invalid Channel";
        }
    }
    /*
            Name: checkChannel
            Fun:  根据tech band 判断channel是否符合
             */
    private boolean checkChannel(int iChannel) {
        Logs.d(TAG, "input Channel =" + iChannel);
        int iBand = Integer.parseInt(spinnerBand.getSelectedItem().toString());
        switch (spinnerTech.getSelectedItemPosition()) {
            case 0://2G
                if (iBand == 8) {//0-124,975-1023
                    if ((iChannel >= iGSMChannels[0][iBand] && iChannel <= iGSMChannels[1][iBand]) ||
                            (iChannel >= iGSMChannels[0][iBand - 1] && iChannel <= iGSMChannels[1][iBand - 1])){
                    } else {
                        return false;
                    }
                } else if (iChannel < iGSMChannels[0][iBand] || iChannel > iGSMChannels[1][iBand]) {
                    return false;
                }
                break;
            case 1://3G
                int iOffset = iUMTSDLULArrays[1][iBand] - iUMTSDLULArrays[0][iBand];
                if (iChannel >= (iUMTSChannels[0][iBand] + iOffset) && iChannel <= (iUMTSChannels[1][iBand] + iOffset)) {
                } /*else if (iChannel >= iUMTSChannels[0][iBand] && iChannel <= iUMTSChannels[1][iBand]) {
                    iChannel += iOffset;
                } */else {
                    switch (iBand) {
                        case 2:
                            if (/*(iChannel >= iUMTS2Additional[0] && iChannel <= iUMTS2Additional[iUMTS2Additional.length - 1]) ||*/
                                    (iChannel >= (iUMTS2Additional[0] + iOffset) && iChannel <= (iUMTS2Additional[iUMTS2Additional.length - 1] + iOffset))) {
                                for (int i = 0; i < iUMTS2Additional.length; i++) {
                                    /*if (iChannel == iUMTS2Additional[i]) {
                                        iChannel += iOffset;
                                        break;
                                    } else */if (iChannel == iUMTS2Additional[i] + iOffset) {
                                        break;
                                    } else if (i == iUMTS2Additional.length) {
                                        return false;
                                    }
                                }
                            } else {
                                return false;
                            }
                            break;
                        case 4:
                            if (/*(iChannel >= iUMTS4Additional[0] && iChannel <= iUMTS4Additional[iUMTS4Additional.length - 1]) ||*/
                                    (iChannel >= (iUMTS4Additional[0] + iOffset) && iChannel <= (iUMTS4Additional[iUMTS4Additional.length - 1] + iOffset))) {
                                for (int i = 0; i < iUMTS4Additional.length; i++) {
                                    /*if (iChannel == iUMTS4Additional[i]) {
                                        iChannel += iOffset;
                                        break;
                                    } else */if (iChannel == iUMTS4Additional[i] + iOffset) {
                                        break;
                                    } else if (i == iUMTS4Additional.length) {
                                        return false;
                                    }
                                }
                            } else {
                                return false;
                            }
                            break;
                        case 5:
                            if (/*(iChannel >= iUMTS5Additional[0] && iChannel <= iUMTS5Additional[iUMTS5Additional.length - 1]) ||*/
                                    (iChannel >= (iUMTS5Additional[0] + iOffset) && iChannel <= (iUMTS5Additional[iUMTS5Additional.length - 1] + iOffset))) {
                                for (int i = 0; i < iUMTS5Additional.length; i++) {
                                    /*if (iChannel == iUMTS5Additional[i]) {
                                        iChannel += iOffset;
                                        break;
                                    } else */if (iChannel == iUMTS5Additional[i] + iOffset) {
                                        break;
                                    } else if (i == iUMTS5Additional.length) {
                                        return false;
                                    }
                                }
                            } else {
                                return false;
                            }
                            break;
                        case 7:
                            if (/*(iChannel >= iUMTS7Additional[0] && iChannel <= iUMTS7Additional[iUMTS7Additional.length - 1]) ||*/
                                    (iChannel >= (iUMTS7Additional[0] + iOffset) && iChannel <= (iUMTS7Additional[iUMTS7Additional.length - 1] + iOffset))) {
                                for (int i = 0; i < iUMTS7Additional.length; i++) {
                                    /*if (iChannel == iUMTS7Additional[i]) {
                                        iChannel += iOffset;
                                        break;
                                    } else */if (iChannel == iUMTS7Additional[i] + iOffset) {
                                        break;
                                    } else if (i == iUMTS7Additional.length) {
                                        return false;
                                    }
                                }
                            } else {
                                return false;
                            }
                            break;
                        default:
                            return false;
                    }
                }
                break;
            case 2://4G
                if (iBand >=0 && iBand <= 8) {
                    /*if (iChannel >= iLteChannels[0][iBand] && iChannel <= iLteChannels[1][iBand]) {
                        iChannel += 18000;
                    } else */if (iChannel >= (iLteChannels[0][iBand] + 18000) && iChannel <= (iLteChannels[1][iBand] + 18000)) {
                    } else {
                        return false;
                    }
                } else if (iBand == 30 || iBand == 31) {
                    /*if (iChannel >= iLteChannels[0][iBand] && iChannel <= iLteChannels[1][iBand]) {
                        iChannel += 17890;
                    } else */if (iChannel >= (iLteChannels[0][iBand] + 17890) && iChannel <= (iLteChannels[1][iBand] + 17890)) {
                    } else {
                        return false;
                    }

                } else {
                    if (iChannel < iLteChannels[0][iBand] || iChannel > iLteChannels[1][iBand]) {
                        return false;
                    }
                }
                break;
            default:
                return false;
        }
        Logs.d(TAG, "calculate input Channel =" + iChannel);
        String strHex = String.format("%04x", Integer.parseInt(String.valueOf(iChannel)));
        byte data[] = toBytes(strHex);
        data[2] = 0x01;
        data[3] = 0x00;
        data[4] = (byte)spinnerTech.getSelectedItemPosition();
        data[5] = (byte)Integer.parseInt(spinnerBand.getSelectedItem().toString());
        EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_WRITE, EventBusTukTukMsg.CHARACTERISTIC_UUID2, data));
        return true;
    }
    /*
    Name: updateBand
    Fun:  根据spinner选择2/3/4,band对应刷新
     */
    private void updateBand() {
        switch (spinnerTech.getSelectedItemPosition() + 1) {
            case 1:
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                        this, R.array.band_2g, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerBand.setAdapter(adapter);
                break;
            case 2:
                adapter = ArrayAdapter.createFromResource(
                        this, R.array.band_3g, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerBand.setAdapter(adapter);
                break;
            case 3:
                adapter = ArrayAdapter.createFromResource(
                        this, R.array.band_4g, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerBand.setAdapter(adapter);
            default:
                return;
        }
        SharePreferenceUtils.getInstance(this).setInt("tuktuk_tech", spinnerTech.getSelectedItemPosition());
    }

    private void initTTS(){
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.setSpeechRate(1.3f);
                tts.setLanguage(Locale.getDefault());
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void blueToothReceiver(EventBusTukTukMsg eventBusTukTukMsg) {
        switch (eventBusTukTukMsg.getAction()) {
            case EventBusTukTukMsg.BT_NONSUPPORT:
                if (eventBusTukTukMsg.getUUID().equals(EventBusTukTukMsg.CHARACTERISTIC_UUID7)) {
                    CustomToast.showToast(this, "Firmware version is too low, nonsupport mode");
                }
                break;
            case EventBusTukTukMsg.BT_DISCONNECT_NOTIF:
                disConnectHint();
                break;
            case EventBusTukTukMsg.BT_RECEIVER:
                parseMsg(eventBusTukTukMsg);
                break;
            case EventBusTukTukMsg.BT_GET_DEVICE_RESPONSE:
                if (eventBusTukTukMsg.getBluetoothDevice() == null) {
//                    disConnectHint();
                } else {
                    tvMac.setText(eventBusTukTukMsg.getBluetoothDevice().getAddress());
                }
                break;
            case EventBusTukTukMsg.BT_INIT_SUCCESS:
                if (initTimer != null) {
                    initTimer.purge();
                    initTimer.cancel();
                }
                EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_READ, EventBusTukTukMsg.CHARACTERISTIC_UUID7));
                EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_READ, EventBusTukTukMsg.CHARACTERISTIC_UUID2));
                break;
            default:
                break;
        }
    }

    private void disConnectHint() {
        ivConnStatus.setImageResource(R.drawable.icon_conn_disconn);
        hintDialog.setCanceledOnTouchOutside(false);
        hintDialog.show();
        hintDialog.setBtnContent("OK");
        hintDialog.setTitle("Warning");
        hintDialog.setContent("TUK TUK Disconnected");
        hintDialog.setListener(new OneBtnHintDialog.BtnClickListener() {
            @Override
            public void onBtnClick(View v) {
                finish();
                Intent intent = new Intent();
                intent.setClassName("com.bravo.FemtoController", "com.bravo.BlueTooth.tukTukActivity");
                startActivity(intent);
            }
        });
    }
    private void initBand() {
        String[] items;
        switch (spinnerTech.getSelectedItemPosition()) {
            case 0:
                items = getResources().getStringArray(R.array.band_2g);
                break;
            case 1:
                items = getResources().getStringArray(R.array.band_3g);
                break;
            case 2:
                items = getResources().getStringArray(R.array.band_4g);
                break;
            default:
                return;
        }
        for (int i = 0; i < items.length; i++) {
            if (Integer.parseInt(items[i]) == iBand) {
                spinnerBand.setSelection(i);
                return;
            }
        }
    }
    private void parseMsg(EventBusTukTukMsg eventBusTukTukMsg) {
        switch (eventBusTukTukMsg.getUUID()) {
            case EventBusTukTukMsg.CHARACTERISTIC_UUID2:
            case EventBusTukTukMsg.CHARACTERISTIC_UUID6:
                byte[] channel = new byte[2];
                System.arraycopy(eventBusTukTukMsg.getBytes(), 0, channel, 0, 2);
                etChannel.setText(String.valueOf(Integer.valueOf(bytesToHexString(channel), 16)));
                if (findViewById(R.id.tt_init).getVisibility() != View.GONE) {
                    findViewById(R.id.tt_init).setVisibility(View.GONE);
                    ivConfig.setVisibility(View.VISIBLE);
                    eventBusTukTukMsg.getBytes()[2] = 0x01;
                    EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_WRITE, EventBusTukTukMsg.CHARACTERISTIC_UUID2, eventBusTukTukMsg.getBytes()));
                }
                if (eventBusTukTukMsg.getBytes().length == 6) {
                    iBand = eventBusTukTukMsg.getBytes()[5];
                    if (spinnerTech.getSelectedItemPosition() == eventBusTukTukMsg.getBytes()[4]) {
                       initBand();
                    } else {
                        bInitBand = true;
                        spinnerTech.setSelection(eventBusTukTukMsg.getBytes()[4]);
                    }
                }
                break;
            case EventBusTukTukMsg.CHARACTERISTIC_UUID3:
                byte data[] =  eventBusTukTukMsg.getBytes();
//                Logs.d(TAG, "Characterisitic=" + eventBusTukTukMsg.getUUID() + ",Value=" + bytesToHexString(eventBusTukTukMsg.getBytes()));
                int iSignal = data[0];
                if (iSignal > 100 || iSignal < 0) {
                    circleProgressBar.setProgress(0);
                } else {
                    circleProgressBar.setProgress(iSignal);
                    if(spinnerSound.getSelectedItemPosition() == 1) {
                        tts.speak(Integer.toString(iSignal), TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
                byte[] b = {data[5], data[6]};
                if (bytesToHexString(b).equalsIgnoreCase("FFFF")) {
                    tvBattery.setText("");
                    ivBattery.setImageResource(R.drawable.icon_battery_charging);
                } else {
                    int iBattery = Integer.parseInt(bytesToHexString(b));
                    iBattery -= 300;
                    if (iBattery < 0) {
                        iBattery = 0;
                    } else if (iBattery > 100) {
                        iBattery = 100;
                    }
                    tvBattery.setText(iBattery + "%");
                    if (iBattery <= 10) {
                        ivBattery.setImageResource(R.drawable.icon_battery10);
                    } else if (iBattery <= 20 && iBattery > 10) {
                        ivBattery.setImageResource(R.drawable.icon_battery20);
                    } else if (iBattery <= 30 && iBattery > 20) {
                        ivBattery.setImageResource(R.drawable.icon_battery30);
                    } else if (iBattery <= 40 && iBattery > 30) {
                        ivBattery.setImageResource(R.drawable.icon_battery40);
                    } else if (iBattery <= 50 && iBattery > 40) {
                        ivBattery.setImageResource(R.drawable.icon_battery50);
                    } else if (iBattery <= 60 && iBattery > 50) {
                        ivBattery.setImageResource(R.drawable.icon_battery60);
                    } else if (iBattery <= 70 && iBattery > 60) {
                        ivBattery.setImageResource(R.drawable.icon_battery70);
                    } else if (iBattery <= 80 && iBattery > 70) {
                        ivBattery.setImageResource(R.drawable.icon_battery80);
                    } else if (iBattery <= 90 && iBattery > 80) {
                        ivBattery.setImageResource(R.drawable.icon_battery90);
                    } else if (iBattery > 90) {
                        ivBattery.setImageResource(R.drawable.icon_battery100);
                    }
                }
                break;
            case EventBusTukTukMsg.CHARACTERISTIC_UUID7:
                if (spinnerMode.getSelectedItemPosition() != eventBusTukTukMsg.getBytes()[0]) {
                    bInitMode = true;
                    spinnerMode.setSelection(eventBusTukTukMsg.getBytes()[0]);
                }
                break;
            default:
                break;
        }
    }
    private String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    private   byte[] toBytes(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[6];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }

    private void saveData() {
        SharePreferenceUtils.getInstance(this).setString("tt_channel", etChannel.getText().toString());
        SharePreferenceUtils.getInstance(this).setInt("tt_tech", spinnerTech.getSelectedItemPosition());
        SharePreferenceUtils.getInstance(this).setInt("tt_band", spinnerBand.getSelectedItemPosition());
        SharePreferenceUtils.getInstance(this).setInt("tt_tts", spinnerSound.getSelectedItemPosition());
    }

    private void loadData() {
//        bInitBand = true;
//        iBand = SharePreferenceUtils.getInstance(this).getInt("tt_band", 0);
//        spinnerTech.setSelection(SharePreferenceUtils.getInstance(this).getInt("tt_tech", 0));
//        etChannel.setText(SharePreferenceUtils.getInstance(this).getString("tt_channel", ""));
        spinnerSound.setSelection(SharePreferenceUtils.getInstance(this).getInt("tt_tts", 0));
    }

    //检测TTS
    //            Intent checkIntent = new Intent();
//            checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
//            startActivityForResult(checkIntent, 133);

    //    private TextToSpeech mTts;
//    protected void onActivityResult( int requestCode, int resultCode, Intent data) {
//        Logs.d(TAG, "requestCode=" + requestCode + ",resultCode=" + resultCode);
//        if (requestCode == 133) {
//            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
//// sess, create the TTS instance
//                mTts = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
//                    @Override
//                    public void onInit(int status) {
//                        mTts.setSpeechRate(1.5f);
//                        mTts.setLanguage(Locale.getDefault());
//                    }
//                });
//            } else {
//// missing data, install it
//                Intent installIntent = new Intent();
//                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
//                startActivity(installIntent);
//            }
//        }
//    }
}
