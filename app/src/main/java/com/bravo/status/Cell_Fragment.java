package com.bravo.status;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.parse_generate_xml.ex_status.StatusNotif;
import com.bravo.parse_generate_xml.ex_status.StatusNotifCell;
import com.bravo.parse_generate_xml.ex_status.StatusReq;
import com.bravo.utils.Logs;
import com.bravo.utils.SharePreferenceUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.bravo.femto.BcastCommonApi.sendUdpMsg;

public class Cell_Fragment extends RevealAnimationBaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_cell);
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        String Tech = SharePreferenceUtils.getInstance(context).getString("status_notif_tech" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        switch (Tech) {
            case "4G":
                contentView.findViewById(R.id.tac_layout).setVisibility(View.VISIBLE);
                contentView.findViewById(R.id.pci_layout).setVisibility(View.VISIBLE);
                break;
            case "3G":
                contentView.findViewById(R.id.lac_layout).setVisibility(View.VISIBLE);
                contentView.findViewById(R.id.psc_layout).setVisibility(View.VISIBLE);
                break;
            case "2G":
                contentView.findViewById(R.id.lac_layout).setVisibility(View.VISIBLE);
                contentView.findViewById(R.id.bsic_layout).setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        StatusReq statusReq = new StatusReq();
        statusReq.setType("4");//cell status type=4
        sendUdpMsg(context, StatusReq.toXml(statusReq));
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity)context).getSettingBtn().setImageResource(R.drawable.btn_refresh_selector);
        ((RevealAnimationActivity)context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                StatusReq statusReq = new StatusReq();
                statusReq.setType("4");//cell status type=1
                sendUdpMsg(context, StatusReq.toXml(statusReq));
                super.recordOnClick(v, "Refresh Cell Event");
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
        Logs.d(TAG, "ExStatusNotif CELL");
        StatusNotifCell statusNotifCell = s.getCell();
        if (statusNotifCell != null) {
            ((TextView) contentView.findViewById(R.id.mnc)).setText(statusNotifCell.getMnc());
            ((TextView) contentView.findViewById(R.id.mcc)).setText(statusNotifCell.getMcc());
            ((TextView) contentView.findViewById(R.id.cid)).setText(statusNotifCell.getCid());
            ((TextView) contentView.findViewById(R.id.tac)).setText(statusNotifCell.getTac());
            ((TextView) contentView.findViewById(R.id.lac)).setText(statusNotifCell.getLac());
            ((TextView) contentView.findViewById(R.id.channel)).setText(statusNotifCell.getChannel());
            ((TextView) contentView.findViewById(R.id.pci)).setText(statusNotifCell.getPci());
            ((TextView) contentView.findViewById(R.id.psc)).setText(statusNotifCell.getPsc());
            ((TextView) contentView.findViewById(R.id.bsic)).setText(statusNotifCell.getBsic());
            ((TextView) contentView.findViewById(R.id.status)).setText(statusNotifCell.getStatus());
            ((TextView) contentView.findViewById(R.id.clients)).setText(statusNotifCell.getClients());
        } else {
            StatusReq statusReq = new StatusReq();
            statusReq.setType("4");//cell status type=4
            sendUdpMsg(context, StatusReq.toXml(statusReq));
        }
    }
}
