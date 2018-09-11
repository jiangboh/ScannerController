package com.bravo.config;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.custom_view.TwoBtnHintDialog;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.fragments.SerializableHandler;
import com.bravo.parse_generate_xml.Status;
import com.bravo.parse_generate_xml.switch_tech.SwitchTechReq;
import com.bravo.parse_generate_xml.switch_tech.SwitchTechRes;
import com.bravo.socket_service.EventBusMsgCloseSocket;
import com.bravo.utils.SharePreferenceUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.bravo.femto.BcastCommonApi.sendTcpMsg;

/**
 * Created by Jack.liao on 2017/5/4.
 */

public class Switch_Fragment extends RevealAnimationBaseFragment {
    private final String TAG = "Switch_Fragment";
    private final int SWITCH_SUCCESS = 0;
    private final int SWITCH_FAILURE = 1;
    private String strControlIP;
    private boolean bDialogState = true;
    private String strSwitchTech = "";
    private ProgressDialog progressDialog;
    private TwoBtnHintDialog dialog;
    private Spinner spinner_tech;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_switch);
    }

    @Override
    public void initView() {
        dialog = new TwoBtnHintDialog(context, R.style.dialog_style);
        spinner_tech = (Spinner) contentView.findViewById(R.id.switch_tech);
    }

    private void ParseStatusNotif() {
        strControlIP = SharePreferenceUtils.getInstance(context).getString("status_notif_controller_ip" +
                ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() +
                ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        String strTech = SharePreferenceUtils.getInstance(context).getString("status_notif_tech" +
                ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() +
                ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
            switch (strTech) {
                case "2G":
                    strTech = "GSM";
                    spinner_tech.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, Arrays.asList("UMTS", "LTE")));
                    break;
                case "3G":
                    strTech = "UMTS";
                    spinner_tech.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, Arrays.asList("GSM", "LTE")));
                    break;
                case "4G":
                    strTech = "LTE";
                    spinner_tech.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, Arrays.asList("GSM", "UMTS")));
                    break;
                default:
                    break;
            }
        ((TextView) contentView.findViewById(R.id.cur_tech)).setText(strTech);
        String TechCapability = SharePreferenceUtils.getInstance(context).getString("status_notif_tech_capability" +
                ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() +
                ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        ((TextView) contentView.findViewById(R.id.tech_capability)).setText(TechCapability);
        if (!TextUtils.isEmpty(TechCapability)) {
            String techs[] = TechCapability.split(",");
            if (techs.length == 3) {
                contentView.findViewById(R.id.layout_switch).setVisibility(View.VISIBLE);
            } else {
                for (int i = 0; i < techs.length; i++) {
                    if (!techs[i].equals(strTech)) {
                        strSwitchTech = techs[i];
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        ParseStatusNotif();
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
                    SwitchTech();
                }
                InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0); //强制隐藏键盘
                super.recordOnClick(v, "Set Config Switch Event");
            }
        });
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    private void SwitchTech() {
        dialog.setOnBtnClickListener(new TwoBtnHintDialog.OnBtnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.two_btn_dialog_right_btn){
                    if (progressDialog == null || !progressDialog.isShowing()) {
                        progressDialog = android.app.ProgressDialog.show(context, "Switching", "Please wait...！", true, false);
                    }
                    bDialogState = true;
                    Thread thread = new Thread() {
                        public void run() {
                            try {
                                sleep(1000);
                                SwitchTechReq switchTechReq = new SwitchTechReq();
                                switchTechReq.setTech(strSwitchTech);
                                sendTcpMsg(context, SwitchTechReq.toXml(switchTechReq));
                                int iCount = 0;
                                while (bDialogState && iCount < 5) {
                                    iCount++;
                                    sleep(1000);
                                }
                                if (iCount >= 5) {
                                    Message msg = new Message();
                                    msg.what = SWITCH_FAILURE;
                                    handler.sendMessage(msg);
                                }
                            } catch (InterruptedException e) {
                                // TODO 自动生成的 catch 块
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
        dialog.setTitle("Hint");
        if (contentView.findViewById(R.id.layout_switch).getVisibility() == View.VISIBLE) {
            strSwitchTech = spinner_tech.getSelectedItem().toString();
        }
        dialog.setContent("Are you sure switch to " + strSwitchTech + "?");
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private Handler handler = new SerializableHandler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SWITCH_FAILURE:
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    CustomToast.showToast(context, "Switch Failure");
                    break;
                case SWITCH_SUCCESS:
                    EventBus.getDefault().post(new EventBusMsgCloseSocket(((ProxyApplication)context.getApplicationContext()).getCurSocketAddress(),
                            ((ProxyApplication)context.getApplicationContext()).getiUdpPort()));
                    CustomToast.showToast(context, "Switch Success");
                    Intent it = new Intent();
                    it.setClassName("com.bravo.FemtoController", "com.bravo.FemtoController.FemtoListActivity");
                    it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(it);
                    break;
                default:
                    break;
            }

        }
    };
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SwitchTech(SwitchTechRes str) {
        bDialogState = false;
        Message msg = new Message();
        if (str.getStatus().equals("SUCCESS")) {
            msg.what = SWITCH_SUCCESS;
        } else {
            msg.what = SWITCH_FAILURE;
        }
        handler.sendMessage(msg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void StatusNotif(Status s) {
      ParseStatusNotif();
    }
}
