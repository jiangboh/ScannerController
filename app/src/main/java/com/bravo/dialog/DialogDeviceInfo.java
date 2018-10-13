package com.bravo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.data_ben.DeviceDataStruct;
import com.bravo.utils.Logs;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by admin on 2018-10-13.
 */

public class DialogDeviceInfo extends Dialog {
    private final String TAG = "DialogDeviceInfo";
    private Context context;
    private DeviceDataStruct dds;

    private LinearLayout layout_sctp;
    private LinearLayout layout_s1;
    private LinearLayout layout_sync;
    private LinearLayout layout_redio1;
    private LinearLayout layout_wSelfStudy;
    private LinearLayout layout_licenss;
    private LinearLayout layout_gps;

    private EditText s_sctp;
    private EditText s_s1;
    private EditText s_gps ;
    private EditText s_cell;
    private EditText s_sync;
    private EditText s_wSelfStudy;
    private EditText s_licenss;
    private EditText sInfo_redio0;
    private EditText sInfo_redio1;
    private TextView sInfo_redio_name;

    private EditText sInfo_fullname;
    private EditText sInfo_sn ;
    private EditText sInfo_ip;
    private EditText sInfo_devType;
    private EditText sInfo_version ;
    private EditText sInfo_lastTime;

    public DialogDeviceInfo(@NonNull Context context, DeviceDataStruct dds) {
        super(context);
        this.context = context;
        this.dds = dds;
    }

    protected void onCreate(final Bundle savedInstanceState) {
        Logs.d(TAG,"onCreate",true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_device_info);

        layout_gps = (LinearLayout)findViewById(R.id.gps_layout);
        layout_sctp = (LinearLayout)findViewById(R.id.sctp_layout);
        layout_s1 = (LinearLayout)findViewById(R.id.s1_layout);
        layout_sync = (LinearLayout)findViewById(R.id.sync_layout);
        layout_wSelfStudy = (LinearLayout)findViewById(R.id.wSelfStudy_layout);
        layout_redio1 = (LinearLayout)findViewById(R.id.redio1_layout);
        layout_licenss = (LinearLayout)findViewById(R.id.licenss_layout);

        sInfo_redio_name = (TextView) findViewById(R.id.sInfo_redio_name);
        if (dds.getMode().equals(DeviceDataStruct.MODE.GSM) || dds.getMode().equals(DeviceDataStruct.MODE.GSM_V2)){
            sInfo_redio_name.setText("载波0射频:");
        }else {
            sInfo_redio_name.setText("射频开关:");
        }

        sInfo_redio0 = (EditText) findViewById(R.id.sInfo_redio);
        if (dds.isStatus_radio()) {
            sInfo_redio0.setText("打开");
            sInfo_redio0.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusOk));
        } else {
            sInfo_redio0.setText("关闭");
            sInfo_redio0.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusFail));
        }

        sInfo_redio1 = (EditText) findViewById(R.id.sInfo_redio_1);
        if (dds.getMode().equals(DeviceDataStruct.MODE.GSM) || dds.getMode().equals(DeviceDataStruct.MODE.GSM_V2)){
            if (dds.isStatus_redio2()) {
                sInfo_redio1.setText("打开");
                sInfo_redio1.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusOk));
            } else {
                sInfo_redio1.setText("关闭");
                sInfo_redio1.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusFail));
            }
            layout_redio1.setVisibility(View.VISIBLE);
        } else {
            layout_redio1.setVisibility(View.GONE);
        }

        s_gps = (EditText) findViewById(R.id.sInfo_gps);
        if (dds.getMode().equals(DeviceDataStruct.MODE.LTE_FDD) || dds.getMode().equals(DeviceDataStruct.MODE.LTE_TDD)
                || dds.getMode().equals(DeviceDataStruct.MODE.WCDMA)) {
            if (dds.isStatus_redio2()) {
                s_gps.setText("打开");
                s_gps.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusOk));
            } else {
                s_gps.setText("关闭");
                s_gps.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusFail));
            }
            layout_gps.setVisibility(View.VISIBLE);
        } else {
            layout_gps.setVisibility(View.GONE);
        }

        s_sctp = (EditText) findViewById(R.id.sInfo_sctp);
        if (dds.getMode().equals(DeviceDataStruct.MODE.LTE_FDD) || dds.getMode().equals(DeviceDataStruct.MODE.LTE_TDD)
                || dds.getMode().equals(DeviceDataStruct.MODE.WCDMA)) {
            if (dds.isStatus_radio()) {
                s_sctp.setText("连接");
                s_sctp.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusOk));
            } else {
                s_sctp.setText("断开");
                s_sctp.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusFail));
            }
            layout_sctp.setVisibility(View.VISIBLE);
        } else {
            layout_sctp.setVisibility(View.GONE);
        }

        s_s1 = (EditText) findViewById(R.id.sInfo_s1);
        if (dds.getMode().equals(DeviceDataStruct.MODE.LTE_FDD) || dds.getMode().equals(DeviceDataStruct.MODE.LTE_TDD)
                || dds.getMode().equals(DeviceDataStruct.MODE.WCDMA)) {
            if (dds.isStatus_radio()) {
                s_s1.setText("连接");
                s_s1.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusOk));
            } else {
                s_s1.setText("断开");
                s_s1.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusFail));
            }
            layout_s1.setVisibility(View.VISIBLE);
        } else {
            layout_s1.setVisibility(View.GONE);
        }

        s_cell = (EditText) findViewById(R.id.sInfo_cell);
        if (dds.isStatus_radio()) {
            s_cell.setText("正常");
            s_cell.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusOk));
        } else {
            s_cell.setText("异常");
            s_cell.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusFail));
        }

        s_sync = (EditText) findViewById(R.id.sInfo_sync);
        if (dds.getMode().equals(DeviceDataStruct.MODE.LTE_FDD) || dds.getMode().equals(DeviceDataStruct.MODE.LTE_TDD)
                || dds.getMode().equals(DeviceDataStruct.MODE.WCDMA)) {
            if (dds.isStatus_radio()) {
                s_sync.setText("同步");
                s_sync.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusOk));
            } else {
                s_sync.setText("失步");
                s_sync.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusFail));
            }
            layout_sctp.setVisibility(View.VISIBLE);
        } else {
            layout_sync.setVisibility(View.GONE);
        }

        s_licenss = (EditText) findViewById(R.id.sInfo_licenss);
        if (dds.getMode().equals(DeviceDataStruct.MODE.LTE_FDD) || dds.getMode().equals(DeviceDataStruct.MODE.LTE_TDD)
                || dds.getMode().equals(DeviceDataStruct.MODE.WCDMA)) {
            if (dds.isStatus_radio()) {
                s_licenss.setText("有效");
                s_licenss.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusOk));
            } else {
                s_licenss.setText("失效");
                s_licenss.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusFail));
            }
            layout_licenss.setVisibility(View.VISIBLE);
        } else {
            layout_licenss.setVisibility(View.GONE);
        }

        s_wSelfStudy = (EditText) findViewById(R.id.sInfo_wSelfStudy);
        if (dds.getMode().equals(DeviceDataStruct.MODE.LTE_FDD) || dds.getMode().equals(DeviceDataStruct.MODE.LTE_TDD)
                || dds.getMode().equals(DeviceDataStruct.MODE.WCDMA)) {
            if (dds.isStatus_radio()) {
                s_wSelfStudy.setText("学习中");
                s_wSelfStudy.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusOk));
            } else {
                s_wSelfStudy.setText("学习结束");
                s_wSelfStudy.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colordialogvalue));
            }
            layout_wSelfStudy.setVisibility(View.VISIBLE);
        } else {
            layout_wSelfStudy.setVisibility(View.GONE);
        }

        sInfo_fullname = (EditText) findViewById(R.id.sInfo_fullname);
        sInfo_fullname.setText(dds.getFullName());
        sInfo_sn = (EditText) findViewById(R.id.sInfo_sn);
        sInfo_sn.setText(dds.getSN());
        sInfo_ip = (EditText) findViewById(R.id.sInfo_ip);
        sInfo_ip.setText(dds.getIp() + ":" + String.valueOf(dds.getPort()));
        sInfo_devType = (EditText) findViewById(R.id.sInfo_devType);
        sInfo_devType.setText(dds.getMode());
        sInfo_version = (EditText) findViewById(R.id.sInfo_version);
        sInfo_version.setText(String.valueOf(dds.getVersion()));
        sInfo_lastTime = (EditText) findViewById(R.id.sInfo_LastTime);
        sInfo_lastTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(dds.getLastTime()));


        findViewById(R.id.sInfo_ok).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                cancel();
                super.recordOnClick(v, "Cancel Event");
            }
        });
    }
}
