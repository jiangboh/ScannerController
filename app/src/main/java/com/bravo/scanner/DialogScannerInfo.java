package com.bravo.scanner;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.bravo.R;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.data_ben.TargetDataStruct;

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
    private EditText sInfo_time;
    private EditText sInfo_fullname;
    private EditText sInfo_sn ;
    private EditText sInfo_ip;
    private EditText sInfo_port;
    private EditText sInfo_rsrp ;
    private EditText sInfo_we ;

    public DialogScannerInfo(@NonNull Context context,TargetDataStruct tds) {
        super(context);
        this.context = context;
        this.tds = tds;
    }

    protected void onCreate(final Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_scanner_info);

        sInfo_name = (EditText) findViewById(R.id.sInfo_name);
        sInfo_name.setText(tds.getName());
        sInfo_imsi = (EditText) findViewById(R.id.sInfo_imsi);
        sInfo_imsi.setText(tds.getImsi());

        sInfo_type = (EditText) findViewById(R.id.sInfo_type);
        if (tds.getiUserType() == 0){
            sInfo_type.setText("普通用户");
        } else if (tds.getiUserType() == 0){
            sInfo_type.setText("白名单用户");
        } else if (tds.getiUserType() == 0){
            sInfo_type.setText("黑名单用户");
        } else {
            sInfo_type.setText("普通用户");
        }
        sInfo_tmsi = (EditText) findViewById(R.id.sInfo_tmsi);
        sInfo_tmsi.setText(tds.getTmsi());
        sInfo_time = (EditText) findViewById(R.id.sInfo_time);
        sInfo_time.setText(tds.getStrAttachtime());
        sInfo_fullname = (EditText) findViewById(R.id.sInfo_fullname);
        sInfo_fullname.setText(tds.getFullName());
        sInfo_sn = (EditText) findViewById(R.id.sInfo_sn);
        sInfo_sn.setText(tds.getSN());
        sInfo_ip = (EditText) findViewById(R.id.sInfo_ip);
        sInfo_ip.setText(tds.getIP());
        sInfo_port = (EditText) findViewById(R.id.sInfo_port);
        sInfo_port.setText(String.valueOf(tds.getPort()));
        sInfo_rsrp = (EditText) findViewById(R.id.sInfo_rsrp);
        sInfo_rsrp.setText(String.valueOf(tds.getRsrp()));
        sInfo_we = (EditText) findViewById(R.id.sInfo_we);
        if (!TextUtils.isEmpty(tds.getStrLongitude()) && !TextUtils.isEmpty(tds.getStrLatitude()))
            sInfo_we.setText(tds.getStrLongitude() + ";" + tds.getStrLatitude());
        else
            sInfo_we.setText("没有经纬度信息");


        findViewById(R.id.sInfo_ok).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                cancel();
                super.recordOnClick(v, "Cancel Event");
            }
        });
    }
}
