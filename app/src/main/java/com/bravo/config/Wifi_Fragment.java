package com.bravo.config;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.parse_generate_xml.Status;
import com.bravo.parse_generate_xml.ex_config.ConfigRsp;
import com.bravo.parse_generate_xml.udp.ActionResponse;
import com.bravo.parse_generate_xml.udp.SetConfig;
import com.bravo.parse_generate_xml.udp.WifiConfig;
import com.bravo.utils.SharePreferenceUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.bravo.femto.BcastCommonApi.checkTextEmpty;
import static com.bravo.femto.BcastCommonApi.sendUdpMsg;

/**
 * A simple {@link Fragment} subclass.
 */
public class Wifi_Fragment extends RevealAnimationBaseFragment {
    private Spinner spinner_mode;
    private String strControlIP;
    private EditText edit_ssid;
    private EditText edit_passkey;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_wifi_config);
    }

    @Override
    public void initView() {
        spinner_mode = (Spinner)contentView.findViewById(R.id.mode);
        spinner_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout linearLayout = (LinearLayout)contentView.findViewById(R.id.wifi_client_layout);
                if (position == 1) {
                    linearLayout.setVisibility(View.VISIBLE);
                } else {
                    linearLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        edit_passkey = (EditText) contentView.findViewById(R.id.passkey);
        edit_ssid = (EditText) contentView.findViewById(R.id.ssid);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        strControlIP = SharePreferenceUtils.getInstance(context).getString("status_notif_controller_ip" +
                ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() +
                ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        LoadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity)context).getSettingBtn().setImageResource(R.drawable.btn_config_selector);
        ((RevealAnimationActivity)context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                if (CheckObserverMode(strControlIP)){
                    SetWifi();
                }

                InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0); //强制隐藏键盘
                super.recordOnClick(v, "Set Config WIFI Event");
            }
        });
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        SaveData();
    }

    private void SetWifi() {
        SetConfig setConfig = new SetConfig();
        setConfig.setConnectivityMode(spinner_mode.getSelectedItem().toString());
        if (spinner_mode.getSelectedItemPosition() == 1) {
            if (!checkTextEmpty(edit_ssid) || !checkTextEmpty(edit_passkey)) {
                return;
            } else if (edit_passkey.getText().length() < 8) {
                edit_passkey.requestFocus();
                edit_passkey.setError("len >= 8");
                return;
            }
            WifiConfig wifiConfig = new WifiConfig();
            wifiConfig.setSsid(edit_ssid.getText().toString());
            wifiConfig.setSecurityMode(((Spinner) contentView.findViewById(R.id.security_mode)).getSelectedItem().toString());
            wifiConfig.setEncryptionAlgorithm(((Spinner) contentView.findViewById(R.id.encryption_algorithm)).getSelectedItem().toString());
            wifiConfig.setPasskey(edit_passkey.getText().toString());
            setConfig.setWifiConfig(wifiConfig);
        }
        sendUdpMsg(context, setConfig.toXml(setConfig));
        SaveData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ExStatusNotifi(ConfigRsp configRsp){
        if (configRsp.getStatus().equals("SUCCESS")) {
            CustomToast.showToast(context, "Set Config Success");
        } else {
            CustomToast.showToast(context, "Set Config Failure");
        }
    }

    private void SaveData() {
        SharedPreferences preferences = context.getSharedPreferences("wifi_config",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("conn_mode", spinner_mode.getSelectedItemPosition());
        editor.putString("ssid", edit_ssid.getText().toString());
        editor.putInt("security_mode", ((Spinner)contentView.findViewById(R.id.security_mode)).getSelectedItemPosition());
        editor.putInt("encryption_algorithm", ((Spinner)contentView.findViewById(R.id.encryption_algorithm)).getSelectedItemPosition());
        editor.putString("passkey", edit_passkey.getText().toString());
        editor.commit();
    }

    private void LoadData() {
        SharedPreferences sp = context.getSharedPreferences("wifi_config", MODE_PRIVATE);
        spinner_mode.setSelection(sp.getInt("conn_mode", 0));
        edit_ssid.setText(sp.getString("ssid", ""));
        ((Spinner)contentView.findViewById(R.id.security_mode)).setSelection(sp.getInt("security_mode", 0));
        ((Spinner)contentView.findViewById(R.id.encryption_algorithm)).setSelection(sp.getInt("encryption_algorithm", 0));
        edit_passkey.setText(sp.getString("passkey", ""));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void StatusNotif(Status s) {
        strControlIP = s.getControllerClient();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ActionRes(ActionResponse as) {
        if (as.getActionType().equals("set-config")) {
            if (as.getActionStatus().equals("SUCCESS")) {
                CustomToast.showToast(context, "Set Config Success");
                }
            } else {
                CustomToast.showToast(context, "Set Config Failure");
            }
    }
}
