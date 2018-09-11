package com.bravo.BlueTooth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.OneBtnHintDialog;
import com.bravo.custom_view.RecordOnClick;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class TTSettingActivity extends AppCompatActivity {
    private final String TAG = "TTSettingActivity";
    private Spinner spinnerLow;
    private Spinner spinnerHigh;
    private Spinner spinnerTXCO;
    private Spinner spinnerAVG;
    private TextView txHWver;
    private TextView txSWver;
    private TextView txExSWver;
    private TextView txSerialNum;
    private ImageView ivBattery;
    private TextView tvBattery;
    //title
    private ImageView ivConfig;
    private TextView tvMac;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ttsetting);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initView();
        EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_GET_DEVICE_REQUEST));
        EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_READ, EventBusTukTukMsg.CHARACTERISTIC_UUID4));
    }

    public void initView() {
        spinnerLow = (Spinner) findViewById(R.id.tt_low);
        spinnerHigh = (Spinner) findViewById(R.id.tt_high);
        spinnerTXCO = (Spinner) findViewById(R.id.tt_txco);
        spinnerAVG = (Spinner) findViewById(R.id.tt_avg);
        txHWver = (TextView) findViewById(R.id.tt_hw_ver);
        txSWver = (TextView) findViewById(R.id.tt_sw_ver);
        txExSWver = (TextView) findViewById(R.id.tt_ex_sw_ver);
        txSerialNum = (TextView) findViewById(R.id.tt_serial_number);
        tvMac = (TextView) findViewById(R.id.tt_mac_address);
        ivBattery = (ImageView) findViewById(R.id.tt_battery);
        tvBattery = (TextView) findViewById(R.id.tt_tv_battery);
        ivConfig = (ImageView) findViewById(R.id.iv_activity_right);
        ivConfig.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_activity_title)).setText("TT Setting");
        findViewById(R.id.iv_activity_back).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                onBackPressed();
                super.recordOnClick(v, "Back Event " + TAG);
            }
        });
        controlEnable(false);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void blueToothReceiver(EventBusTukTukMsg eventBusTukTukMsg) {
        switch (eventBusTukTukMsg.getAction()) {
            case EventBusTukTukMsg.BT_GET_DEVICE_RESPONSE:
                if (eventBusTukTukMsg.getBluetoothDevice() == null) {
                    disConnectHint();
                } else {
                    tvMac.setText(eventBusTukTukMsg.getBluetoothDevice().getAddress());
                }
                break;
            case EventBusTukTukMsg.BT_DISCONNECT_NOTIF:
//                disConnectHint();
                finish();
                break;
            case EventBusTukTukMsg.BT_RECEIVER:
                parseMsg(eventBusTukTukMsg);
                break;
            case EventBusTukTukMsg.BT_WRITE_RESULT:
                if (eventBusTukTukMsg.getStrResult().equals(EventBusTukTukMsg.BT_WRITE_SUCCESS)) {
                    CustomToast.showToast(this, "Config Success");
                    finish();
                }
                break;
            default:
                break;
        }
    }

    private void disConnectHint() {
        OneBtnHintDialog hintDialog = new OneBtnHintDialog(this, R.style.dialog_style);
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

    private void parseMsg(EventBusTukTukMsg eventBusTukTukMsg) {
        switch (eventBusTukTukMsg.getUUID()) {
            case EventBusTukTukMsg.CHARACTERISTIC_UUID3:
                byte data[] =  eventBusTukTukMsg.getBytes();
                byte[] b = {data[5], data[6]};
                int iBattery = Integer.parseInt(bytesToHexString(b));
                iBattery -= 300;
                if (iBattery < 0) {
                    iBattery = 0;
                } else if (iBattery > 100) {
                    iBattery = 100;
                }
                tvBattery.setText(iBattery + "%");
                if(iBattery <= 10){
                    ivBattery.setImageResource(R.drawable.icon_battery10);
                }else if(iBattery <= 20 && iBattery > 10){
                    ivBattery.setImageResource(R.drawable.icon_battery20);
                }else if(iBattery <= 30 && iBattery > 20){
                    ivBattery.setImageResource(R.drawable.icon_battery30);
                }else if(iBattery <= 40 && iBattery > 30){
                    ivBattery.setImageResource(R.drawable.icon_battery40);
                }else if(iBattery <= 50 && iBattery > 40){
                    ivBattery.setImageResource(R.drawable.icon_battery50);
                }else if(iBattery <= 60 && iBattery > 50){
                    ivBattery.setImageResource(R.drawable.icon_battery60);
                }else if(iBattery <= 70 && iBattery > 60){
                    ivBattery.setImageResource(R.drawable.icon_battery70);
                }else if(iBattery <= 80 && iBattery > 70){
                    ivBattery.setImageResource(R.drawable.icon_battery80);
                }else if(iBattery <= 90 && iBattery > 80){
                    ivBattery.setImageResource(R.drawable.icon_battery90);
                }else if(iBattery > 90) {
                    ivBattery.setImageResource(R.drawable.icon_battery100);
                }
                break;
            case EventBusTukTukMsg.CHARACTERISTIC_UUID4:
                spinnerLow.setSelection(eventBusTukTukMsg.getBytes()[0]);
                spinnerHigh.setSelection(eventBusTukTukMsg.getBytes()[1]);
                spinnerTXCO.setSelection(eventBusTukTukMsg.getBytes()[2]);
                spinnerAVG.setSelection(eventBusTukTukMsg.getBytes()[3]);
                txHWver.setText(bytetToVer(eventBusTukTukMsg.getBytes()[4]));
                txSWver.setText(bytetToVer(eventBusTukTukMsg.getBytes()[5]));
                controlEnable(true);
                EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_READ, EventBusTukTukMsg.CHARACTERISTIC_UUID5));
                break;
            case EventBusTukTukMsg.CHARACTERISTIC_UUID5:
                txSerialNum.setText("" + Long.valueOf(bytesToHexString(eventBusTukTukMsg.getBytes()), 16));
                EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_READ, EventBusTukTukMsg.CHARACTERISTIC_UUID7));
                break;
            case EventBusTukTukMsg.CHARACTERISTIC_UUID7:
                if (eventBusTukTukMsg.getBytes().length == 4) {
                    txExSWver.setText("V" + eventBusTukTukMsg.getBytes()[1] + "." + eventBusTukTukMsg.getBytes()[2] + "." + eventBusTukTukMsg.getBytes()[3]);
                } else {
                    txExSWver.setText("N/A");
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

    private String bytetToVer(byte src) {
        StringBuilder stringBuilder = new StringBuilder("");
        String hv = Integer.toHexString(src & 0xFF);
        if (hv.length() < 2) {
            return "V0." + hv;
        } else {
            return "V" + hv.substring(0, 1) + "." + hv.substring(1);
        }
    }

    private void controlEnable(boolean bEnable) {
        spinnerLow.setEnabled(bEnable);
        spinnerHigh.setEnabled(bEnable);
        spinnerAVG.setEnabled(bEnable);
        spinnerTXCO.setEnabled(bEnable);
        if (bEnable) {
            ivConfig.setImageResource(R.drawable.btn_config_selector);
            ivConfig.setOnClickListener(new RecordOnClick() {
                @Override
                public void recordOnClick(View v, String strMsg) {
                    super.recordOnClick(v, "Setting tuktuk Event");
                    byte data[] = new byte[4];
                    data[0] = (byte) spinnerLow.getSelectedItemPosition();
                    data[1] = (byte) spinnerHigh.getSelectedItemPosition();
                    data[2] = (byte) spinnerTXCO.getSelectedItemPosition();
                    data[3] = (byte) spinnerAVG.getSelectedItemPosition();
                    EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_WRITE, EventBusTukTukMsg.CHARACTERISTIC_UUID4, data));
                }
            });
        } else {
            ivConfig.setImageResource(R.drawable.btn_refresh_selector);
            ivConfig.setOnClickListener(new RecordOnClick() {
                @Override
                public void recordOnClick(View v, String strMsg) {
                    super.recordOnClick(v, "Refresh tuktuk setting Event");
                    EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_READ, EventBusTukTukMsg.CHARACTERISTIC_UUID4));
                }
            });
        }
    }
}
