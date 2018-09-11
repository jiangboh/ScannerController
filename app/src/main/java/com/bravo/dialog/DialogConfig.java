package com.bravo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.data_ben.FemtoDataStruct;

/**
 * Created by Jack.liao on 2016/10/19.
 */

public class DialogConfig extends Dialog {
    private Button BtnOK;
    private Button BtnCanacel;
    private EditText EditName;
    private TextView TextIP;
    private TextView TextMac;
    private TextView TextPort;
    private OnCustomDialogListener customDialogListener;
    private FemtoDataStruct femtoDataStruct;

    public interface OnCustomDialogListener{
        void DialogCallBack(String UserName);
    }

    public DialogConfig(Context context, int theme, FemtoDataStruct femtoDataStruct, OnCustomDialogListener customListener) {
        super(context, theme);
        this.customDialogListener = customListener;
        this.femtoDataStruct = femtoDataStruct;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_list_config);

        BtnOK = (Button) findViewById(R.id.ok);
        BtnCanacel = (Button) findViewById(R.id.cancel);
        EditName = (EditText) findViewById(R.id.name);
        TextIP = (TextView) findViewById(R.id.ip_address);
        TextMac = (TextView) findViewById(R.id.mac_address);
        TextPort = (TextView) findViewById(R.id.port);

        TextIP.setText(femtoDataStruct.getIPAddress());
        TextMac.setText(femtoDataStruct.getMacAddress());
        TextPort.setText(femtoDataStruct.getiTcpPort()+ "");
        EditName.setText(femtoDataStruct.getSSID());
        if (!TextUtils.isEmpty(femtoDataStruct.getSSID())) {
            EditName.setSelection(femtoDataStruct.getSSID().length());
        }

        BtnOK.setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                customDialogListener.DialogCallBack(EditName.getText().toString());
                dismiss();
                super.recordOnClick(v, "Change Femto Name Event " + EditName.getText().toString());
            }
        });

        BtnCanacel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }
}
