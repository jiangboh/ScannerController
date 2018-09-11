package com.bravo.status;

import android.os.Bundle;
import android.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.parse_generate_xml.ex_status.StatusNotif;
import com.bravo.parse_generate_xml.ex_status.StatusNotifHw;
import com.bravo.parse_generate_xml.ex_status.StatusReq;
import com.bravo.utils.Logs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.bravo.femto.BcastCommonApi.sendUdpMsg;


/**
 * A simple {@link Fragment} subclass.
 */
public class HwMonitor_Fragment extends RevealAnimationBaseFragment {
    private final String TAG = "HWMONITOR_FRAGMENT";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_hw_monitor);
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
        statusReq.setType("8");//hw status type=8
        sendUdpMsg(context, StatusReq.toXml(statusReq));
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity)context).getSettingBtn().setImageResource(R.drawable.btn_refresh_selector);
        ((RevealAnimationActivity)context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                StatusReq statusReq = new StatusReq();
                statusReq.setType("8");//hw status type=1
                sendUdpMsg(context, StatusReq.toXml(statusReq));
                super.recordOnClick(v, "Refresh HW Monitor Event");
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
        Logs.d(TAG, "ExStatusNotif HW");
        StatusNotifHw statusNotifHw = s.getHw();
        if (statusNotifHw != null) {
            ((TextView) contentView.findViewById(R.id.cpu)).setText(statusNotifHw.getCpu() + "%");
            ((TextView) contentView.findViewById(R.id.memory)).setText(statusNotifHw.getMem() + "%");
            ((TextView) contentView.findViewById(R.id.load)).setText(statusNotifHw.getLoad());
            ((TextView) contentView.findViewById(R.id.flash)).setText(statusNotifHw.getFlash() + "%");
            ((TextView) contentView.findViewById(R.id.temperature)).setText(statusNotifHw.getTemperature());
            ((TextView) contentView.findViewById(R.id.battery_level)).setText(statusNotifHw.getBatteryLevel() + "%");
            ((TextView) contentView.findViewById(R.id.battery_charge)).setText(statusNotifHw.getBatteryCharging());
            if (statusNotifHw.getRadio() != null) {
                ((TextView) contentView.findViewById(R.id.tx_s)).setText(statusNotifHw.getRadio().getTxS());
                ((TextView) contentView.findViewById(R.id.tx_f)).setText(statusNotifHw.getRadio().getTxF());
            }
        } else {
            StatusReq statusReq = new StatusReq();
            statusReq.setType("8");//hw status type=8
            sendUdpMsg(context, StatusReq.toXml(statusReq));
        }
    }
}
