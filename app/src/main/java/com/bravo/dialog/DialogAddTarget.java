package com.bravo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.R;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.data_ben.TargetDataStruct;
import com.bravo.database.TargetUserDao;
import com.bravo.utils.Logs;

import static com.bravo.femto.BcastCommonApi.changeBand;
import static com.bravo.femto.BcastCommonApi.checkChannel;

/**
 * Created by Jack.liao on 2017/4/20.
 */

public class DialogAddTarget extends Dialog {
    private final String TAG = "DialogAddTarget";
    private Context context;
    private DialogAddTarget.OnAddTargetDialogListener addTargetDialogListener;
    private EditText edit_name;
    private EditText edit_imsi;
    private EditText edit_imei;
    private Spinner spinner_tech;
    private Spinner spinner_band;
    private EditText edit_channel;
    private String strImei = null,strImsi = null;
    private TargetDataStruct targetDataStruct = null;

    public DialogAddTarget(@NonNull Context context, int theme, DialogAddTarget.OnAddTargetDialogListener addTargetDialogListener) {
        super(context, theme);
        this.context = context;
        this.addTargetDialogListener = addTargetDialogListener;
    }

    public DialogAddTarget(@NonNull Context context, int theme, DialogAddTarget.OnAddTargetDialogListener addTargetDialogListener, String strImsi, String strImei) {
        super(context, theme);
        this.context = context;
        this.addTargetDialogListener = addTargetDialogListener;
        this.strImei = strImei;
        this.strImsi = strImsi;
    }

    public DialogAddTarget(@NonNull Context context, int theme, DialogAddTarget.OnAddTargetDialogListener addTargetDialogListener, TargetDataStruct targetDataStruct) {
        super(context, theme);
        this.context = context;
        this.addTargetDialogListener = addTargetDialogListener;
        this.targetDataStruct = targetDataStruct;
    }

    public interface OnAddTargetDialogListener{
        void AddTargetCallBack(TargetDataStruct targetDataStruct);
    }

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_target);

        edit_imsi = (EditText) findViewById(R.id.imsi);
        edit_name = (EditText) findViewById(R.id.name);
        edit_imei = (EditText) findViewById(R.id.imei);
        edit_imei.setText(strImei);
        edit_imsi.setText(strImsi);
        if (!TextUtils.isEmpty(strImei) || !TextUtils.isEmpty(strImsi)) {
            edit_imsi.setFocusable(false);
            edit_imei.setFocusable(false);
        }
        edit_imei.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edit_imsi.setError(null);
                if (s.length() < 15) {
                    edit_imei.setError("imei len:15");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_imsi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edit_imei.setError(null);
                if (s.length() < 14) {
                    edit_imsi.setError("imsi len:14-15");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        spinner_tech = (Spinner) findViewById(R.id.tech);
        spinner_band = (Spinner) findViewById(R.id.band);
        edit_channel = (EditText) findViewById(R.id.channel);
        spinner_tech.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeBand(context, position, (LinearLayout) findViewById(R.id.layout_bc), spinner_band);
                if (targetDataStruct != null) {
                    String [] strings;
                    switch (position) {
                        case 1:
                            strings = context.getResources().getStringArray(R.array.band_2g);
                            break;
                        case 2:
                            strings = context.getResources().getStringArray(R.array.band_3g);
                            break;
                        case 3:
                            strings = context.getResources().getStringArray(R.array.band_4g);
                            break;
                        default:
                            targetDataStruct = null;
                            return;
                    }
                    for (int i = 0; i < strings.length; i++) {
                        if (targetDataStruct.getStrBand().equals(strings[i])) {
                            spinner_band.setSelection(i);
                            targetDataStruct = null;
                            return;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        findViewById(R.id.ok).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                if (CheckTextEmpty()) {
                    TargetDataStruct targetDataStruct = new TargetDataStruct();
                    targetDataStruct.setName(edit_name.getText().toString());
                    targetDataStruct.setImsi(edit_imsi.getText().toString());
                    targetDataStruct.setImei(edit_imei.getText().toString());
                    if (spinner_tech.getSelectedItemPosition() != 0) {
                        targetDataStruct.setStrTech(spinner_tech.getSelectedItem().toString());
                        targetDataStruct.setStrBand(spinner_band.getSelectedItem().toString());
                        targetDataStruct.setStrChannel(edit_channel.getText().toString());
                        targetDataStruct.setbRedir(true);
                    } else {
                        targetDataStruct.setbRedir(false);
                    }
                    addTargetDialogListener.AddTargetCallBack(targetDataStruct);
                    dismiss();
                    super.recordOnClick(v, "OK Add Target Event " + targetDataStruct.toString());
                }

            }
        });

        findViewById(R.id.cancel).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                cancel();
                super.recordOnClick(v, "Cancel Add Target Event");
            }
        });

        if (targetDataStruct != null) {
            edit_name.setText(targetDataStruct.getName());
            if (!TextUtils.isEmpty(targetDataStruct.getImsi())) {
                edit_imsi.setText(targetDataStruct.getImsi());
                strImsi = targetDataStruct.getImsi();
            }
            if (!TextUtils.isEmpty(targetDataStruct.getImei())) {
                edit_imei.setText(targetDataStruct.getImei());
                strImei = targetDataStruct.getImei();
            }
            boolean bRedir =  targetDataStruct.isbRedir();
            if (bRedir) {
                switch (targetDataStruct.getStrTech()) {
                    case "GSM":
                        spinner_tech.setSelection(1);
                        break;
                    case "UMTS":
                        spinner_tech.setSelection(2);
                        break;
                    case "LTE":
                        spinner_tech.setSelection(3);
                        break;
                    default:
                        return;
                }
                edit_channel.setText(targetDataStruct.getStrChannel());
            }
        }
    }

    private boolean CheckTextEmpty() {
        Logs.d(TAG, strImsi + "," + strImei);
        edit_name.setError(null);
        edit_imei.setError(null);
        edit_imsi.setError(null);
        if (TextUtils.isEmpty(edit_name.getText().toString())) {
            edit_name.requestFocus();
            edit_name.setError("N/A");
        } else if (TextUtils.isEmpty(edit_imsi.getText().toString()) && TextUtils.isEmpty(edit_imei.getText().toString())) {
            edit_imsi.setError("N/A");
            edit_imei.setError("N/A");
        } else if (!TextUtils.isEmpty(edit_imsi.getText().toString()) && edit_imsi.getText().toString().length() < 14) {
            edit_imsi.requestFocus();
            edit_imsi.setError("imsi len:14-15");
        } else if (!TextUtils.isEmpty(edit_imei.getText().toString()) && edit_imei.getText().length() < 15) {
            edit_imei.requestFocus();
            edit_imei.setError("imei len:15");
        } else if ((TextUtils.isEmpty(strImsi) && !TextUtils.isEmpty(edit_imsi.getText().toString()) &&
                ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(TargetUserDao.Properties.StrImsi.eq(edit_imsi.getText().toString())).build().unique() != null) ||
                (!TextUtils.isEmpty(strImsi) && !strImsi.equals(edit_imsi.getText().toString()) &&
                        ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(TargetUserDao.Properties.StrImsi.eq(edit_imsi.getText().toString())).build().unique() != null)) {
            edit_imsi.setError("already exists");
        } else if ((TextUtils.isEmpty(strImei) && !TextUtils.isEmpty(edit_imei.getText().toString()) &&
                ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(TargetUserDao.Properties.StrImei.eq(edit_imei.getText().toString())).build().unique() != null) ||
                (!TextUtils.isEmpty(strImei) && !strImei.equals(edit_imei.getText().toString()) &&
                        ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(TargetUserDao.Properties.StrImei.eq(edit_imei.getText().toString())).build().unique() != null)) {
            edit_imei.setError("already exists");
        } else if(spinner_tech.getSelectedItemPosition() != 0) {
            if (!checkChannel(spinner_tech.getSelectedItemPosition(),
                    Integer.parseInt(spinner_band.getSelectedItem().toString()),
                    edit_channel.getText().toString())) {
                edit_channel.requestFocus();
                edit_channel.setError("Invalid channel");
            } else {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }
}
