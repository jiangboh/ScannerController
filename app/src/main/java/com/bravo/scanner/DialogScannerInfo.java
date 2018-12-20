package com.bravo.scanner;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bravo.R;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.data_ben.TargetDataStruct;
import com.bravo.utils.Logs;

/**
 * Created by admin on 2018-9-27.
 */

public class DialogScannerInfo extends Dialog {
    private final String TAG = "DialogScannerInfo";
    private Context context;
    private TargetDataStruct tds;
    private EditText sInfo_name;
    private EditText sInfo_imsi;
    private EditText sInfo_type ;
    private EditText sInfo_tmsi;
    private EditText sInfo_imei;
    private EditText sInfo_time;
    private EditText sInfo_fullname;
    private EditText sInfo_sn ;
    private EditText sInfo_ip;
    private EditText sInfo_devType;
    private EditText sInfo_rsrp ;
    private EditText sInfo_we ;

    private Button bClose;
    private Button bWhite;
    private Button bBlack;

    public DialogScannerInfo(@NonNull Context context,TargetDataStruct tds) {
        super(context);
        this.context = context;
        this.tds = tds;
    }

    protected void onCreate(final Bundle savedInstanceState) {
        Logs.d(TAG,"onCreate",true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_scanner_info);

        sInfo_name = (EditText) findViewById(R.id.sInfo_name);
        sInfo_name.setText(tds.getName());
        sInfo_imsi = (EditText) findViewById(R.id.sInfo_imsi);
        sInfo_imsi.setText(tds.getImsi());

        sInfo_type = (EditText) findViewById(R.id.sInfo_type);
        if (tds.getiUserType() == TargetDataStruct.WHITE_IMSI){
            sInfo_type.setText("白名单用户");
        } else if (tds.getiUserType() == TargetDataStruct.BLACK_IMSI){
            sInfo_type.setText("黑名单用户");
        } else {
            sInfo_type.setText("普通用户");
        }
        sInfo_tmsi = (EditText) findViewById(R.id.sInfo_tmsi);
        sInfo_tmsi.setText(tds.getTmsi());
        sInfo_imei = (EditText) findViewById(R.id.sInfo_imei);
        sInfo_imei.setText(tds.getImei());
        sInfo_time = (EditText) findViewById(R.id.sInfo_time);
        sInfo_time.setText(tds.getStrAttachtime());
        sInfo_fullname = (EditText) findViewById(R.id.sInfo_fullname);
        sInfo_fullname.setText(tds.getFullName());
        sInfo_sn = (EditText) findViewById(R.id.sInfo_sn);
        sInfo_sn.setText(tds.getSN());
        sInfo_ip = (EditText) findViewById(R.id.sInfo_ip);
        sInfo_ip.setText(tds.getIP() + ":" + String.valueOf(tds.getPort()));
        sInfo_devType = (EditText) findViewById(R.id.sInfo_devType);
        sInfo_devType.setText(tds.getDeviceType());
        sInfo_rsrp = (EditText) findViewById(R.id.sInfo_rsrp);
        sInfo_rsrp.setText(String.valueOf(tds.getRsrp()));
        sInfo_we = (EditText) findViewById(R.id.sInfo_we);
        if (!TextUtils.isEmpty(tds.getStrLongitude()) && !TextUtils.isEmpty(tds.getStrLatitude()))
            sInfo_we.setText(tds.getStrLongitude() + ";" + tds.getStrLatitude());
        else
            sInfo_we.setText("没有经纬度信息");

        bClose = (Button)findViewById(R.id.sInfo_ok);
        bClose.setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                cancel();
                super.recordOnClick(v, "Cancel Event");
            }
        });


    }

}
