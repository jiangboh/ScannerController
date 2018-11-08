package com.bravo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.bravo.R;
import com.bravo.database.BlackWhiteImsi;
import com.bravo.utils.Logs;

/**
 * Created by admin on 2018-11-6.
 */

public class DialogAddImsi extends Dialog {
    private final String TAG = "DialogDeviceInfo";
    private Context context;
    private BlackWhiteImsi imsiInfo = null;
    private boolean isBlackImsi = false;
    private OnSaveData2Database saverListener;

    private LinearLayout layout_startRb;
    private LinearLayout layout_stopRb;
    private View view_startRb;
    private View view_stopRb;

    private EditText et_name;
    private EditText et_imsi;
    private EditText et_imei;
    private EditText et_tmsi;
    private EditText et_startRb;
    private EditText et_stopRb;

    private Button but_ok;
    private Button but_cancel;

    public DialogAddImsi(@NonNull Context context, boolean isBlackImsi,BlackWhiteImsi imsiInfo) {
        super(context);
        this.context = context;
        this.imsiInfo = imsiInfo;
        this.isBlackImsi = isBlackImsi;
    }

    public interface OnSaveData2Database{
        boolean onSave(BlackWhiteImsi imsiInfo);
    }

    public void setSaveListener(OnSaveData2Database listener) {
        this.saverListener = listener;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        Logs.d(TAG, "onCreate", true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_imsi);

        initView();
        loadData();
        return;
    }

    @Override
    public void onStop() {
        Logs.d(TAG,"onStop",true);
        super.onStop();
    }

    private void initView() {
        layout_startRb = (LinearLayout) findViewById(R.id.layout_startRb);
        layout_stopRb = (LinearLayout) findViewById(R.id.layout_stopRb);
        view_startRb = (View) findViewById(R.id.view_startRb);
        view_stopRb = (View) findViewById(R.id.view_stopRb);

        et_name = (EditText) findViewById(R.id.name);
        et_imsi = (EditText) findViewById(R.id.imsi);
        et_imsi.addTextChangedListener(new MyTextWatcher());
        et_imei = (EditText) findViewById(R.id.imei);
        et_imei.addTextChangedListener(new MyTextWatcher());
        et_tmsi = (EditText) findViewById(R.id.tmsi);
        et_tmsi.addTextChangedListener(new MyTextWatcher());
        et_startRb = (EditText) findViewById(R.id.startRb);
        et_stopRb = (EditText) findViewById(R.id.stopRb);

        but_ok = (Button) findViewById(R.id.ok);
        but_ok.setEnabled(false);
        but_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                BlackWhiteImsi info = new BlackWhiteImsi();
                info.setName(et_name.getText().toString());
                info.setImsi(et_imsi.getText().toString());
                info.setImei(et_imei.getText().toString());
                info.setTmsi(et_tmsi.getText().toString());
                if (isBlackImsi) {
                    info.setType(BlackWhiteImsi.BLACK);
                } else {
                    info.setType(BlackWhiteImsi.WHITE);
                }
                if (et_startRb.getText().toString().trim().length() == 0) {
                    info.setStartRb(0);
                } else {
                    info.setStartRb(Integer.parseInt(et_startRb.getText().toString()));
                }
                if (et_stopRb.getText().toString().trim().length() == 0) {
                    info.setStopRb(0);
                } else {
                    info.setStopRb(Integer.parseInt(et_stopRb.getText().toString()));
                }
                if (saverListener != null) {
                    if (saverListener.onSave(info)) {
                        cancel();
                    }
                }
            }
        });
        but_cancel = (Button) findViewById(R.id.cancel);
        but_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        if (isBlackImsi) {
            layout_startRb.setVisibility(View.VISIBLE);
            layout_stopRb.setVisibility(View.VISIBLE);
            view_startRb.setVisibility(View.VISIBLE);
            view_stopRb.setVisibility(View.VISIBLE);
        } else {
            layout_startRb.setVisibility(View.GONE);
            layout_stopRb.setVisibility(View.GONE);
            view_startRb.setVisibility(View.GONE);
            view_stopRb.setVisibility(View.GONE);
        }

    }

    private void loadData() {
        if (imsiInfo != null) {
            et_name.setText(imsiInfo.getName());
            et_imsi.setText(imsiInfo.getImsi());
            et_imei.setText(imsiInfo.getImei());
            et_tmsi.setText(imsiInfo.getTmsi());
            et_startRb.setText(String.valueOf(imsiInfo.getStartRb()));
            et_stopRb.setText(String.valueOf(imsiInfo.getStopRb()));
        }
    }

    private  boolean checkInput() {
        String imsi = et_imsi.getText().toString();
        String imei = et_imei.getText().toString();
        String tmsi = et_tmsi.getText().toString();

        if (imsi.length() == 15 || imei.length() == 15 || tmsi.length()==8) {
            return true;
        }

        return false;
    }



    private class  MyTextWatcher implements TextWatcher {
        // 输入文本之前的状态
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        // 输入文本中的状态
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        // 输入文本之后的状态
        @Override
        public void afterTextChanged(Editable s) {
            if (checkInput()) {
                but_ok.setEnabled(true);
            } else {
                but_ok.setEnabled(false);
            }
        }
    }
}
