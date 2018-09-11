package com.bravo.status;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.parse_generate_xml.ex_status.StatusNotif;
import com.bravo.parse_generate_xml.ex_status.StatusNotifBasic;
import com.bravo.parse_generate_xml.ex_status.StatusReq;
import com.bravo.parse_generate_xml.ex_status.StatusSWVersion;
import com.bravo.utils.Logs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.bravo.femto.BcastCommonApi.sendUdpMsg;

/**
 * Created by Jack.liao on 2016/7/28.
 */
public class Basic_Fragment extends RevealAnimationBaseFragment {
    private final static String TAG = "Basic_Fragment";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_basic);
    }

    @Override
    public void onResume() {
        super.onResume();
        StatusReq statusReq = new StatusReq();
        statusReq.setType("1");//basic status type=1
        sendUdpMsg(context, StatusReq.toXml(statusReq));
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity)context).getSettingBtn().setImageResource(R.drawable.btn_refresh_selector);
        ((RevealAnimationActivity)context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                StatusReq statusReq = new StatusReq();
                statusReq.setType("1");//basic status type=1
                sendUdpMsg(context, StatusReq.toXml(statusReq));
                super.recordOnClick(v, "Refresh Basic Event");
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ExStatusNotif(StatusNotif s) {
        Logs.d(TAG, "ExStatusNotif Basic");
        StatusNotifBasic statusNotifBasic = s.getBasic();
        if (statusNotifBasic != null) {
            ((TextView) contentView.findViewById(R.id.sn)).setText(statusNotifBasic.getSn());
            ((TextView) contentView.findViewById(R.id.mac)).setText(statusNotifBasic.getMac());
            ((TextView) contentView.findViewById(R.id.rat)).setText(statusNotifBasic.getRat());
            ((TextView) contentView.findViewById(R.id.band)).setText(statusNotifBasic.getBand());
            ((TextView) contentView.findViewById(R.id.antenna_gain)).setText(statusNotifBasic.getAntennaGain());
            ((TextView) contentView.findViewById(R.id.hw_ver)).setText(statusNotifBasic.getHwVer());
            ((TextView) contentView.findViewById(R.id.mode)).setText(statusNotifBasic.getMode());
            ((TextView) contentView.findViewById(R.id.up_time)).setText(statusNotifBasic.getUpTime());
            ((TextView) contentView.findViewById(R.id.cell_sel_alg)).setText(statusNotifBasic.getCellSelAlg());
            StatusSWVersion statusSWVersion = statusNotifBasic.getSwVer();
            if (statusSWVersion != null){
                ((TextView) contentView.findViewById(R.id.build_ver)).setText(statusSWVersion.getBuildVer());
                ((TextView) contentView.findViewById(R.id.pixcellagent_ver)).setText(statusSWVersion.getPixcellagentVer());
                ((TextView) contentView.findViewById(R.id.epc_ver)).setText(statusSWVersion.getEpcVer());
                ((TextView) contentView.findViewById(R.id.stack_ver)).setText(statusSWVersion.getStackVer());
                ((TextView) contentView.findViewById(R.id.oam_ver)).setText(statusSWVersion.getOamVer());
                ((TextView) contentView.findViewById(R.id.rem_ver)).setText(statusSWVersion.getRemVer());
                ((TextView) contentView.findViewById(R.id.kernel_ver)).setText(statusSWVersion.getKernelVer());
                ((TextView) contentView.findViewById(R.id.uboot_ver)).setText(statusSWVersion.getUbootVer());
            } else {
            }
        } else {
            StatusReq statusReq = new StatusReq();
            statusReq.setType("1");//basic status type=1
            sendUdpMsg(context, StatusReq.toXml(statusReq));
        }
    }
}
