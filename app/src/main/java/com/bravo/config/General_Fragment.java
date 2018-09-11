package com.bravo.config;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.parse_generate_xml.Status;
import com.bravo.parse_generate_xml.ex_config.ConfigGeneral;
import com.bravo.parse_generate_xml.ex_config.ConfigReq;
import com.bravo.parse_generate_xml.ex_config.ConfigRsp;
import com.bravo.utils.SharePreferenceUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.bravo.femto.BcastCommonApi.sendUdpMsg;


/**
 * A simple {@link Fragment} subclass.
 */
public class General_Fragment extends RevealAnimationBaseFragment {
    private final static String TAG = "General_Fragment";
    private String strControlIP;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_general);
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
                    SetGeneral();
                }
                InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0); //强制隐藏键盘
                super.recordOnClick(v, "Set Config General Event");
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

    @Override
    public void initView() {
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        strControlIP = SharePreferenceUtils.getInstance(context).getString("status_notif_controller_ip" +
                ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() +
                ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        LoadData();
    }

    private void SetGeneral() {
        ConfigGeneral configGeneral = new ConfigGeneral();
        configGeneral.setFanSpeed(String.valueOf(((Spinner)contentView.findViewById(R.id.fan_speed)).getSelectedItemPosition() + 1));
        configGeneral.setHwReportInterval(((EditText)contentView.findViewById(R.id.hw_report_interval)).getText().toString());
        configGeneral.setKpiInterval(((EditText)contentView.findViewById(R.id.kpi_report)).getText().toString());
        if (((CheckBox)contentView.findViewById(R.id.time)).isChecked()){
            configGeneral.setTime(String.valueOf(System.currentTimeMillis() / 1000));
        }
        configGeneral.setTimeZone(((EditText)contentView.findViewById(R.id.time_zone)).getText().toString());
        configGeneral.setWatchDog(((Spinner)contentView.findViewById(R.id.watch_dog)).getSelectedItem().toString());
        configGeneral.setAutoReboot(((Spinner)contentView.findViewById(R.id.auto_reboot)).getSelectedItem().toString());
        configGeneral.setRadio(new ConfigGeneral.Radio());
        configGeneral.getRadio().setTxF(((EditText)contentView.findViewById(R.id.tx_f)).getText().toString());
        configGeneral.getRadio().setTxS(((EditText)contentView.findViewById(R.id.tx_s)).getText().toString());
        configGeneral.setCellSelAlg(((EditText)contentView.findViewById(R.id.cell_sel_alg)).getText().toString());
        ConfigReq configReq = new ConfigReq();
        configReq.setGeneral(configGeneral);
        sendUdpMsg(context, ConfigReq.toXml(configReq));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ExStatusNotif(ConfigRsp configRsp) {
        if (configRsp.getStatus().equals("SUCCESS")) {
            CustomToast.showToast(context, "Set Config Success");
        } else {
            CustomToast.showToast(context, "Set Config Failure");
        }
    }

    private void SaveData() {
        SharedPreferences preferences = context.getSharedPreferences("general_config",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("fan_speed", ((Spinner)contentView.findViewById(R.id.fan_speed)).getSelectedItemPosition());
        editor.putString("hw_report_interval", ((EditText)contentView.findViewById(R.id.hw_report_interval)).getText().toString());
        editor.putString("kpi_report", ((EditText)contentView.findViewById(R.id.kpi_report)).getText().toString());
        editor.putBoolean("time", ((CheckBox)contentView.findViewById(R.id.time)).isChecked());
        editor.putString("time_zone", ((EditText)contentView.findViewById(R.id.time_zone)).getText().toString());
        editor.putInt("watch_dog", ((Spinner)contentView.findViewById(R.id.watch_dog)).getSelectedItemPosition());
        editor.putInt("auto_reboot", ((Spinner)contentView.findViewById(R.id.auto_reboot)).getSelectedItemPosition());
        editor.putString("tx_s", ((EditText)contentView.findViewById(R.id.tx_s)).getText().toString());
        editor.putString("tx_f", ((EditText)contentView.findViewById(R.id.tx_f)).getText().toString());
        editor.putString("cell_sel_alg", ((EditText)contentView.findViewById(R.id.cell_sel_alg)).getText().toString());
        editor.commit();
    }

    private void LoadData() {
        SharedPreferences sp = context.getSharedPreferences("general_config", MODE_PRIVATE);
        ((Spinner)contentView.findViewById(R.id.fan_speed)).setSelection(sp.getInt("fan_speed", 0));
        ((EditText)contentView.findViewById(R.id.hw_report_interval)).setText(sp.getString("hw_report_interval", ""));
        ((EditText)contentView.findViewById(R.id.kpi_report)).setText(sp.getString("kpi_report", ""));
        ((CheckBox)contentView.findViewById(R.id.time)).setChecked(sp.getBoolean("time", false));
        ((EditText)contentView.findViewById(R.id.time_zone)).setText(sp.getString("time_zone", ""));
        ((Spinner)contentView.findViewById(R.id.watch_dog)).setSelection(sp.getInt("watch_dog", 0));
        ((Spinner)contentView.findViewById(R.id.auto_reboot)).setSelection(sp.getInt("auto_reboot", 0));
        ((EditText)contentView.findViewById(R.id.tx_s)).setText(sp.getString("tx_s", ""));
        ((EditText)contentView.findViewById(R.id.tx_f)).setText(sp.getString("tx_f", ""));
        ((EditText)contentView.findViewById(R.id.cell_sel_alg)).setText(sp.getString("cell_sel_alg", ""));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void StatusNotif(Status s) {
        strControlIP = s.getControllerClient();
    }
}
