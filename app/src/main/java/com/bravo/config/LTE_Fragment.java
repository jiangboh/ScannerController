package com.bravo.config;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.femto.IPEdit;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.parse_generate_xml.Status;
import com.bravo.parse_generate_xml.ex_config.ConfigLte;
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
public class LTE_Fragment extends RevealAnimationBaseFragment {
    private String strControlIP;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_lte);
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

    @Override
    public void onResume() {
        super.onResume();
        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity)context).getSettingBtn().setImageResource(R.drawable.btn_config_selector);
        ((RevealAnimationActivity)context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                if (CheckObserverMode(strControlIP)){
                    SetLTE();
                }

                InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0); //强制隐藏键盘
                super.recordOnClick(v, "Set Config LTE Event");
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

    private void SetLTE() {
        ConfigLte configLte = new ConfigLte();
        configLte.setEpcLogLev(((Spinner)contentView.findViewById(R.id.epc_log_lev)).getSelectedItem().toString());
        configLte.setEnbLogLev(((Spinner)contentView.findViewById(R.id.enb_log_lev)).getSelectedItem().toString());
        configLte.setPixcellAgentLogLev(((Spinner)contentView.findViewById(R.id.pixcell_agent_log_lev)).getSelectedItem().toString());
        configLte.setMonitorIp(((IPEdit)contentView.findViewById(R.id.monitor_ip)).getIPAddtrss());
        configLte.setBandwidth(((Spinner)contentView.findViewById(R.id.bandwidth)).getSelectedItem().toString());
        configLte.setCrsPower(((EditText)contentView.findViewById(R.id.crs_power)).getText().toString());
        ConfigReq configReq = new ConfigReq();
        configReq.setLte(configLte);
        sendUdpMsg(context, ConfigReq.toXml(configReq));
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
        SharedPreferences preferences = context.getSharedPreferences("lte_config",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("epc_log_lev", ((Spinner)contentView.findViewById(R.id.epc_log_lev)).getSelectedItemPosition());
        editor.putInt("enb_log_lev", ((Spinner)contentView.findViewById(R.id.enb_log_lev)).getSelectedItemPosition());
        editor.putInt("pixcell_agent_log_lev", ((Spinner)contentView.findViewById(R.id.pixcell_agent_log_lev)).getSelectedItemPosition());
        editor.putString("monitor_ip", ((IPEdit)contentView.findViewById(R.id.monitor_ip)).getIPAddtrss());
        editor.putInt("bandwidth", ((Spinner)contentView.findViewById(R.id.bandwidth)).getSelectedItemPosition());
        editor.putString("crs_power", ((EditText)contentView.findViewById(R.id.crs_power)).getText().toString());
        editor.commit();
    }

    private void LoadData() {
        SharedPreferences sp = context.getSharedPreferences("lte_config", MODE_PRIVATE);
        ((Spinner)contentView.findViewById(R.id.epc_log_lev)).setSelection(sp.getInt("epc_log_lev", 0));
        ((Spinner)contentView.findViewById(R.id.enb_log_lev)).setSelection(sp.getInt("enb_log_lev", 0));
        ((Spinner)contentView.findViewById(R.id.pixcell_agent_log_lev)).setSelection(sp.getInt("pixcell_agent_log_lev", 0));
        ((IPEdit)contentView.findViewById(R.id.monitor_ip)).initIpAddress(sp.getString("monitor_ip", ""));
        ((Spinner)contentView.findViewById(R.id.bandwidth)).setSelection(sp.getInt("bandwidth", 0));
        ((EditText)contentView.findViewById(R.id.crs_power)).setText(sp.getString("crs_power", ""));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void StatusNotif(Status s) {
        strControlIP = s.getControllerClient();
    }
}
