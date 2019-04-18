package com.bravo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.data_ben.DeviceDataStruct;
import com.bravo.utils.Logs;
import com.bravo.utils.Utils;
import com.bravo.xml.HandleRecvXmlMsg;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by admin on 2018-10-13.
 */

public class DialogDeviceInfo extends Dialog {
    private final String TAG = "DialogDeviceInfo";
    public static Boolean isOpen = false;
    private Context context;
    private DeviceDataStruct dds;

    private boolean isEditMode = false;
    private String fullname = "";

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

    private Button b_redio;
    private Button b_redio1;
    private Button b_fullname;

    public DialogDeviceInfo(@NonNull Context context, DeviceDataStruct dds) {
        super(context);
        this.context = context;
        this.dds = dds;
    }

    private void setButtonImage(Button button,Drawable drawable)
    {
        drawable.setBounds(0, 0,drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
        button.setCompoundDrawables(null,null,drawable,null);
    }

    @Override
    public void onStop() {
        Logs.d(TAG,"onStop",true);
        EventBus.getDefault().unregister(this);
        DialogDeviceInfo.isOpen = false;
        super.onStop();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        Logs.d(TAG, "onCreate", true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_device_info);

        initView();
        loadViewData();

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        DialogDeviceInfo.isOpen = true;
        return;
    }

    private void initView() {
        layout_gps = (LinearLayout)findViewById(R.id.gps_layout);
        layout_sctp = (LinearLayout)findViewById(R.id.sctp_layout);
        layout_s1 = (LinearLayout)findViewById(R.id.s1_layout);
        layout_sync = (LinearLayout)findViewById(R.id.sync_layout);
        layout_wSelfStudy = (LinearLayout)findViewById(R.id.wSelfStudy_layout);
        layout_redio1 = (LinearLayout)findViewById(R.id.redio1_layout);
        layout_licenss = (LinearLayout)findViewById(R.id.licenss_layout);

        b_redio = (Button) findViewById(R.id.bInfo_redio);
        //监听button事件
        b_redio.setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                //cancel();
                if (dds.isStatus_radio()) {
                    new HandleRecvXmlMsg(context,dds).SetDeviceRedio(0,false);
                    CustomToast.showToast(context, "已向AP发送【关闭射频】命令");
                } else {
                    new HandleRecvXmlMsg(context,dds).SetDeviceRedio(0,true);
                    CustomToast.showToast(context, "已向AP发送【打开射频】命令");
                }
            }
        });

        b_redio1 = (Button) findViewById(R.id.bInfo_redio1);
        b_redio1.setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                //cancel();
                if (dds.isStatus_radio()) {
                    new HandleRecvXmlMsg(context,dds).SetDeviceRedio(1,false);
                    CustomToast.showToast(context, "已向AP发送【关闭射频】命令");
                } else {
                    new HandleRecvXmlMsg(context,dds).SetDeviceRedio(1,true);
                    CustomToast.showToast(context, "已向AP发送【打开射频】命令");
                }
            }
        });

        sInfo_redio_name = (TextView) findViewById(R.id.sInfo_redio_name);
        if (dds.getMode().equals(DeviceDataStruct.MODE.GSM) || dds.getMode().equals(DeviceDataStruct.MODE.GSM_V2)){
            sInfo_redio_name.setText("载波0射频:");
        }else {
            sInfo_redio_name.setText("射频开关:");
        }
        sInfo_redio0 = (EditText) findViewById(R.id.sInfo_redio);

        sInfo_redio1 = (EditText) findViewById(R.id.sInfo_redio_1);
        s_gps = (EditText) findViewById(R.id.sInfo_gps);
        s_sctp = (EditText) findViewById(R.id.sInfo_sctp);
        s_sctp.setVisibility(View.GONE);
        s_s1 = (EditText) findViewById(R.id.sInfo_s1);
        s_s1.setVisibility(View.GONE);
        s_cell = (EditText) findViewById(R.id.sInfo_cell);
        s_sync = (EditText) findViewById(R.id.sInfo_sync);
        s_licenss = (EditText) findViewById(R.id.sInfo_licenss);
        s_wSelfStudy = (EditText) findViewById(R.id.sInfo_wSelfStudy);

        sInfo_fullname = (EditText) findViewById(R.id.sInfo_fullname);
        b_fullname = (Button) findViewById(R.id.bInfo_fullname);
        isEditMode = false;
        setButtonImage(b_fullname,ContextCompat.getDrawable(context.getApplicationContext(),R.mipmap.icon_edit));
        b_fullname.setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                if (!isEditMode) {
                    isEditMode = true;
                    fullname = sInfo_fullname.getText().toString().trim();
                    setButtonImage(b_fullname,ContextCompat.getDrawable(context.getApplicationContext(),R.mipmap.icon_edit_ok));

                    sInfo_fullname.setFocusable(true);
                    sInfo_fullname.setFocusableInTouchMode(true);
                    sInfo_fullname.requestFocus();
                    sInfo_fullname.setSelection(sInfo_fullname.getText().length());
                    Utils.showSoftInput(context,sInfo_fullname);
                } else {  //设置全名完成
                    isEditMode = false;
                    setButtonImage(b_fullname,ContextCompat.getDrawable(context.getApplicationContext(),R.mipmap.icon_edit));

                    sInfo_fullname.setFocusable(false);
                    sInfo_fullname.setFocusableInTouchMode(false);
                    sInfo_fullname.clearFocus();
                    Utils.hidenSoftInput(context,sInfo_fullname);

                    //cancel();
                    if (!fullname.equals(sInfo_fullname.getText().toString().trim())) {
                        new HandleRecvXmlMsg(context, dds).SetGeneralParaRequest("CFG_FULL_NAME", sInfo_fullname.getText().toString().trim());
                        CustomToast.showToast(context, "已向AP发送【设置全名】命令");
                    } else {
                        CustomToast.showToast(context, "全名没有变化");
                    }
                }
            }
        });

        sInfo_sn = (EditText) findViewById(R.id.sInfo_sn);
        sInfo_ip = (EditText) findViewById(R.id.sInfo_ip);
        sInfo_devType = (EditText) findViewById(R.id.sInfo_devType);
        sInfo_version = (EditText) findViewById(R.id.sInfo_version);
        sInfo_lastTime = (EditText) findViewById(R.id.sInfo_LastTime);

        findViewById(R.id.sInfo_ok).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                cancel();
                super.recordOnClick(v, "Cancel Event");
            }
        });

        return;
    }

    private void loadViewData() {
        if (dds.isStatus_radio()) {
            sInfo_redio0.setText("打开");
            sInfo_redio0.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusOk));
            //b_redio.setText("关闭");
            //b_redio.setBackgroundColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusFail));
            setButtonImage(b_redio,ContextCompat.getDrawable(context.getApplicationContext(),R.mipmap.icon_redio_off));
        } else {
            sInfo_redio0.setText("关闭");
            sInfo_redio0.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusFail));
            //b_redio.setText("打开");
            //b_redio.setBackgroundColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusOk));
            setButtonImage(b_redio,ContextCompat.getDrawable(context.getApplicationContext(),R.mipmap.icon_redio_on));
        }

        if (dds.getMode().equals(DeviceDataStruct.MODE.GSM) || dds.getMode().equals(DeviceDataStruct.MODE.GSM_V2)){
            if (dds.isStatus_redio2()) {
                sInfo_redio1.setText("打开");
                sInfo_redio1.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusOk));
                //b_redio1.setText("关闭");
                //b_redio1.setBackgroundColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusFail));
                setButtonImage(b_redio1,ContextCompat.getDrawable(context.getApplicationContext(),R.mipmap.icon_redio_off));
            } else {
                sInfo_redio1.setText("关闭");
                sInfo_redio1.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusFail));
                //b_redio1.setText("打开");
                //b_redio1.setBackgroundColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusOk));
                setButtonImage(b_redio1,ContextCompat.getDrawable(context.getApplicationContext(),R.mipmap.icon_redio_on));
            }
            layout_redio1.setVisibility(View.VISIBLE);
        } else {
            layout_redio1.setVisibility(View.GONE);
        }

        if (dds.getMode().equals(DeviceDataStruct.MODE.LTE_FDD) || dds.getMode().equals(DeviceDataStruct.MODE.LTE_TDD)
                || dds.getMode().equals(DeviceDataStruct.MODE.WCDMA)) {
            if (dds.isStatus_gps()) {
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

        if (dds.getMode().equals(DeviceDataStruct.MODE.LTE_FDD) || dds.getMode().equals(DeviceDataStruct.MODE.LTE_TDD)
                || dds.getMode().equals(DeviceDataStruct.MODE.WCDMA)) {
            if (dds.isStatus_sctp()) {
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

        if (dds.getMode().equals(DeviceDataStruct.MODE.LTE_FDD) || dds.getMode().equals(DeviceDataStruct.MODE.LTE_TDD)
                || dds.getMode().equals(DeviceDataStruct.MODE.WCDMA)) {
            if (dds.isStatus_s1()) {
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

        if (dds.isStatus_cell()) {
            s_cell.setText("正常");
            s_cell.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusOk));
        } else {
            s_cell.setText("异常");
            s_cell.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorStatusFail));
        }

        if (dds.getMode().equals(DeviceDataStruct.MODE.LTE_FDD) || dds.getMode().equals(DeviceDataStruct.MODE.LTE_TDD)
                || dds.getMode().equals(DeviceDataStruct.MODE.WCDMA)) {
            if (dds.isStatus_sync()) {
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

        if (dds.getMode().equals(DeviceDataStruct.MODE.LTE_FDD) || dds.getMode().equals(DeviceDataStruct.MODE.LTE_TDD)
                || dds.getMode().equals(DeviceDataStruct.MODE.WCDMA)) {
            if (dds.isStatus_licens()) {
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

        if (dds.getMode().equals(DeviceDataStruct.MODE.LTE_FDD) || dds.getMode().equals(DeviceDataStruct.MODE.LTE_TDD)
                || dds.getMode().equals(DeviceDataStruct.MODE.WCDMA)) {
            if (dds.isStatus_wSelf()) {
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

        if (!isEditMode) { //非编辑模式下才更新全名
            sInfo_fullname.setText(dds.getFullName());
        }

        sInfo_sn.setText(dds.getSN());
        sInfo_ip.setText(dds.getIp() + ":" + String.valueOf(dds.getPort()));
        sInfo_devType.setText(dds.getMode());
        sInfo_version.setText(String.valueOf(dds.getVersion()));
        sInfo_lastTime.setText(dds.getDeviceTime());

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ParaChangesEvens(DeviceDataStruct deviceData) {
        if (deviceData.getSN().equals(dds.getSN())) {
            Logs.d(TAG, "接收到参数改变改变事件", true, true);
            this.dds = deviceData;
            loadViewData();
        }
    }

}
