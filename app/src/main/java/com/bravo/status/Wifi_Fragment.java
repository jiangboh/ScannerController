package com.bravo.status;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.parse_generate_xml.ex_status.StatusNotif;
import com.bravo.parse_generate_xml.ex_status.StatusNotifWifi;
import com.bravo.parse_generate_xml.ex_status.StatusReq;
import com.bravo.utils.Logs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.bravo.femto.BcastCommonApi.sendUdpMsg;

/**
 * Created by Jack.liao on 2016/7/28.
 */
public class Wifi_Fragment extends RevealAnimationBaseFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_wifi);
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData(Bundle savedInstanceState) {
    }

    @Override
    public void onResume() {
        super.onResume();
        StatusReq statusReq = new StatusReq();
        statusReq.setType("2");//wifi status type=2
        sendUdpMsg(context, StatusReq.toXml(statusReq));
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity)context).getSettingBtn().setImageResource(R.drawable.btn_refresh_selector);
        ((RevealAnimationActivity)context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                StatusReq statusReq = new StatusReq();
                statusReq.setType("2");//wifi status type=1
               sendUdpMsg(context, StatusReq.toXml(statusReq));
                super.recordOnClick(v, "Refresh WIFI Event");
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ExStatusNotif(StatusNotif s) {
        Logs.d(TAG, "ExStatusNotif WIFI");
        StatusNotifWifi statusNotifWifi = s.getWifi();
        if (statusNotifWifi != null) {
            ((TextView) contentView.findViewById(R.id.wifi_mode)).setText("WIFI_Mode:" + statusNotifWifi.getMode());
            ((TextView) contentView.findViewById(R.id.channels)).setText("Channels：" + statusNotifWifi.getChannel());
            ((TextView) contentView.findViewById(R.id.ssid)).setText("SSID:" + statusNotifWifi.getSsid());
            ((TextView) contentView.findViewById(R.id.security_mode)).setText("SecurityMode:" + statusNotifWifi.getSecurityMode());
            ((TextView) contentView.findViewById(R.id.encryption_algorithm)).setText("EncryptionAlgorithm:" + statusNotifWifi.getEncryptionAlgorithm());
            ((TextView) contentView.findViewById(R.id.passkey)).setText("Passkey:" + statusNotifWifi.getPasskey());
        } else {
            StatusReq statusReq = new StatusReq();
            statusReq.setType("2");//wifi status type=2
            sendUdpMsg(context, StatusReq.toXml(statusReq));
        }
    }
}
