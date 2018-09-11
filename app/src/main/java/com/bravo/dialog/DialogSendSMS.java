package com.bravo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.bravo.R;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.parse_generate_xml.send_sms.SendSmsReq;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Jack.liao on 2016/10/19.
 */

public class DialogSendSMS extends Dialog {
    private final String TAG = "DialogSendSMS";
    private EditText edit_origin;
    private EditText edit_sms;
    private OnCustomDialogListener customDialogListener;

    public interface OnCustomDialogListener{
        void DialogCallBack(SendSmsReq sendSmsReq);
    }

    public DialogSendSMS(Context context, int theme, OnCustomDialogListener customListener) {
        super(context, theme);
        this.customDialogListener = customListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_send_sms);
        edit_sms = (EditText) findViewById(R.id.sms_msg);
        edit_origin = (EditText) findViewById(R.id.origin);
        ((Button) findViewById(R.id.send_sms)).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                if (CheckTextEmpty(edit_origin) && CheckTextEmpty(edit_sms)) {
                    try {
                        SendSmsReq sendSmsReq = new SendSmsReq();
                        sendSmsReq.setOrigin(edit_origin.getText().toString());
                        sendSmsReq.setText(edit_sms.getText().toString());
                        customDialogListener.DialogCallBack(sendSmsReq);
                        dismiss();
                        super.recordOnClick(v, "SMS Event " + sendSmsReq.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        ((Button) findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        loadData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveData();
    }

    private void saveData() {
        SharedPreferences preferences = getContext().getSharedPreferences("send_sms",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("origin", edit_origin.getText().toString());
        editor.putString("sms", edit_sms.getText().toString());

        editor.commit();
    }
    private void loadData() {
        SharedPreferences sp = getContext().getSharedPreferences("send_sms", MODE_PRIVATE);
        edit_sms.setText(sp.getString("sms", ""));
        edit_origin.setText(sp.getString("origin", ""));

    }
    private boolean CheckTextEmpty(EditText editText) {
        if (editText.getText().toString().equals("")) {
            editText.requestFocus();
            editText.setError("N/A");
            return false;
        }
        return true;
    }
}
