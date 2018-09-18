package com.bravo.FemtoController;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bravo.Find.FragmentFind;
import com.bravo.R;
import com.bravo.config.FragmentSetConfig;
import com.bravo.config.GSM_Fragment;
import com.bravo.config.General_Fragment;
import com.bravo.config.LTE_Fragment;
import com.bravo.config.Network_Fragment;
import com.bravo.config.Switch_Fragment;
import com.bravo.config.UMTS_Fragment;
import com.bravo.custom_view.CircleMenuLayout;
import com.bravo.custom_view.OneBtnHintDialog;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.femto.FragmentAdjacentCell;
import com.bravo.femto.FragmentBcastHistory;
import com.bravo.femto.FragmentBcastStart;
import com.bravo.femto.FragmentCellScan;
import com.bravo.femto.FragmentTarget;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.log.Local_Fragment;
import com.bravo.log.Remote_Fragment;
import com.bravo.parse_generate_xml.Status;
import com.bravo.scanner.FragmentScannerListen;
import com.bravo.status.Basic_Fragment;
import com.bravo.status.Cell_Fragment;
import com.bravo.status.HwMonitor_Fragment;
import com.bravo.status.Sniffer_Fragment;
import com.bravo.status.Wifi_Fragment;
import com.bravo.system.Upgrade_Fragment;
import com.bravo.test.Terminal_Fragmen;
import com.bravo.utils.Logs;
import com.bravo.utils.SharePreferenceUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;


public class FunActivity extends BaseActivity {
    private CircleMenuLayout mCircleMenuLayout;
    private int[] mItemImgs = new int[]{R.drawable.circle_menu_femto_selector,
            R.drawable.circle_menu_config_selector, R.drawable.circle_menu_log_selector,
            R.drawable.circle_menu_status_selector, R.drawable.circle_menu_system_selector,
            R.drawable.circle_menu_test_selector};
    private ImageView selectedIV;
    private OneBtnHintDialog connHintDialog;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == CircleMenuLayout.EFFECT_FOUR){
                switch (msg.arg1){
                    case 1:
                        selectedIV.setImageResource(R.drawable.femto_selected);
                        break;
                    case 2:
                        selectedIV.setImageResource(R.drawable.config_selected);
                        break;
                    case 3:
                        selectedIV.setImageResource(R.drawable.log_selected);
                        break;
                    case 4:
                        selectedIV.setImageResource(R.drawable.status_selected);
                        break;
                    case 5:
                        selectedIV.setImageResource(R.drawable.system_selected);
                        break;
                    case 6:
                        selectedIV.setImageResource(R.drawable.test_selected);
                        break;
                }
            }
        }
    };

    @Override
    protected void initView() {
        setContentView(R.layout.activity_fun);
        findViewById(R.id.iv_activity_back).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                onBackPressed();
                super.recordOnClick(v, "Back Event " + TAG);
            }
        });
        ((TextView) findViewById(R.id.tv_activity_title)).setText("ScannerController");
        selectedIV = (ImageView) findViewById(R.id.circle_menu_selected_iv);

        mCircleMenuLayout = (CircleMenuLayout) findViewById(R.id.circleMenuLayout);
        mCircleMenuLayout.setMenuItemIcons(mItemImgs);
        mCircleMenuLayout.setOnMenuItemClickListener(new CircleMenuLayout.OnMenuItemClickListener() {

            @Override
            public void itemClick(View view, int pos) {
                skipActivity(pos);
            }

            @Override
            public void itemCenterClick(View view) {
                skipActivity(mCircleMenuLayout.getSelectedPos() - 1);
            }
        });

        mCircleMenuLayout.setSelectedEffect(CircleMenuLayout.EFFECT_THREE);
        selectedIV.setVisibility(View.GONE);
        mCircleMenuLayout.setHandler(null);

        initStatusView();
    }

    private void skipActivity(int pos){
       /* if (((ProxyApplication)mContext.getApplicationContext()).getCurSocket() == null && pos != 2 && pos != 5) {
            connHintDialog.setListener(new OneBtnHintDialog.BtnClickListener() {
                @Override
                public void onBtnClick(View v) {
                    Intent intent = new Intent();
                    intent.setClassName("com.bravo.FemtoController", "com.bravo.FemtoController.FemtoListActivity");
                    startActivityWithAnimation(intent);
                }
            });
            connHintDialog.setCanceledOnTouchOutside(true);
            connHintDialog.show();
            connHintDialog.setBtnContent("Connect");
            connHintDialog.setTitle("Warning");
            connHintDialog.setContent("Please Connect Femto");
            return;
        }*/
        switch(pos){
            case 0:
                //CustomToast.showToast(this, "功能开发进行中，敬请期待...");
                onFemtoClicked();
                Logs.w("RecordOnClick", "Enter Femto Function", "Record_Event", true);
                break;
            case 1:
                //CustomToast.showToast(this, "功能开发进行中，敬请期待...");
                onConfigClicked();
                Logs.w("RecordOnClick", "Enter Config Function", "Record_Event", true);
                break;
            case 2:
                //CustomToast.showToast(this, "功能开发进行中，敬请期待...");
                //onLogClicked();

                //发送广播
                //EventBus.getDefault().post(new EventBusMsgSendUDPBroadcastMsg("", 0, "Ip=" + getWifiIp(this)));
                onFindClicked();
                Logs.w("RecordOnClick", "Enter Log Function", "Record_Event", true);
                break;
            case 3:
                //CustomToast.showToast(this, "功能开发进行中，敬请期待...");
                onStatusClicked();
                Logs.w("RecordOnClick", "Enter Status Function", "Record_Event", true);
                break;
            case 4:
                //CustomToast.showToast(this, "功能开发进行中，敬请期待...");
                //onSystemClicked();
                onScannerClicked();
                Logs.w("RecordOnClick", "Enter System Function", "Record_Event", true);
                break;
            case 5:
                //CustomToast.showToast(this, "功能开发进行中，敬请期待...");
                onTestClicked();
                Logs.w("RecordOnClick", "Enter Test Function", "Record_Event", true);
                break;
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
       /* ((TextView) findViewById(R.id.tv_sn)).setText("SN:" + SharePreferenceUtils.getInstance(this).getString("status_notif_sn" + ((ProxyApplication) getApplicationContext()).getCurSocketAddress() + ((ProxyApplication) getApplicationContext()).getiTcpPort(), ""));
        ((TextView) findViewById(R.id.tv_sw)).setText(SharePreferenceUtils.getInstance(this).getString("status_notif_ver" + ((ProxyApplication) getApplicationContext()).getCurSocketAddress() + ((ProxyApplication) getApplicationContext()).getiTcpPort(), ""));
       ((TextView) findViewById(R.id.tv_tech_band)).setText("Tech:" + SharePreferenceUtils.getInstance(this).getString("status_notif_tech" + ((ProxyApplication) getApplicationContext()).getCurSocketAddress() + ((ProxyApplication) getApplicationContext()).getiTcpPort(), "0  ") +
                    "        Band:" + SharePreferenceUtils.getInstance(this).getString("status_notif_band" + ((ProxyApplication) getApplicationContext()).getCurSocketAddress() + ((ProxyApplication) getApplicationContext()).getiTcpPort(), "0"));
        */
        connHintDialog = new OneBtnHintDialog(this, R.style.dialog_style);
    }

    private void onStatusClicked(){
        Intent intent = new Intent(mContext,RevealAnimationActivity.class);
        ArrayList<String> menuList = new ArrayList<String>();
        menuList.add("Basic");
        menuList.add("WIFI");
        menuList.add("Cell");
        menuList.add("HW Monitor");
        //menuList.add("KPI");
        menuList.add("Sniffer Result");
        intent.putStringArrayListExtra(RevealAnimationActivity.MENU_LIST,menuList);

        ArrayList<Integer> iconsResId = new ArrayList<Integer>();
        iconsResId.add(R.drawable.icon_basic_selector);
        iconsResId.add(R.drawable.icon_wifi_selector);
        iconsResId.add(R.drawable.icon_cell_selector);
        iconsResId.add(R.drawable.icon_hw_monitor_selector);
        iconsResId.add(R.drawable.icon_sniffer_result_selector);
        intent.putExtra(RevealAnimationActivity.ICON_RES_LIST,iconsResId);

        ArrayList<RevealAnimationBaseFragment> fragments = new ArrayList<RevealAnimationBaseFragment>();
        fragments.add(new Basic_Fragment());
        fragments.add(new Wifi_Fragment());
        fragments.add(new Cell_Fragment());
        fragments.add(new HwMonitor_Fragment());
        //fragments.add(new Kpi_Fragment());
        fragments.add(new Sniffer_Fragment());
        intent.putExtra(RevealAnimationActivity.FRAGMENTS,(Serializable)fragments);

        intent.putExtra(RevealAnimationActivity.TITLE,"Status");
        startActivityWithAnimation(intent);
    }

    private void onConfigClicked(){
        Intent intent = new Intent(mContext,RevealAnimationActivity.class);
        ArrayList<String> menuList = new ArrayList<String>();
        menuList.add("General");
        menuList.add("WIFI");
        menuList.add("Femto");
        menuList.add("Network");

        ArrayList<RevealAnimationBaseFragment> fragments = new ArrayList<RevealAnimationBaseFragment>();
        fragments.add(new General_Fragment());
        fragments.add(new com.bravo.config.Wifi_Fragment());
        fragments.add(new FragmentSetConfig());
        fragments.add(new Network_Fragment());
        //add icon
        ArrayList<Integer> iconsResId = new ArrayList<Integer>();
        iconsResId.add(R.drawable.icon_general_selector);
        iconsResId.add(R.drawable.icon_wifi_selector);
        iconsResId.add(R.drawable.icon_config_selector);
        iconsResId.add(R.drawable.icon_config_selector);

        String Tech = SharePreferenceUtils.getInstance(mContext).getString("status_notif_tech" + ((ProxyApplication) getApplicationContext()).getCurSocketAddress() + ((ProxyApplication) getApplicationContext()).getiTcpPort(), "");
        switch (Tech) {
            case "4G":
                menuList.add("LTE");
                fragments.add(new LTE_Fragment());
                iconsResId.add(R.drawable.icon_4g_selector);
                break;
            case "3G":
                menuList.add("UMTS");
                fragments.add(new UMTS_Fragment());
                iconsResId.add(R.drawable.icon_3g_selector);
                break;
            case "2G":
                menuList.add("GSM");
                fragments.add(new GSM_Fragment());
                iconsResId.add(R.drawable.icon_2g_selector);
                break;
            default:
                break;
        }
        String TechCapability = SharePreferenceUtils.getInstance(mContext).getString("status_notif_tech_capability" + ((ProxyApplication) getApplicationContext()).getCurSocketAddress() + ((ProxyApplication) getApplicationContext()).getiTcpPort(), "");
        if (!TextUtils.isEmpty(TechCapability)) {
            String techs[] = TechCapability.split(",");
            if (techs.length > 1) {
                menuList.add("Switch");
                fragments.add(new Switch_Fragment());
                iconsResId.add(R.drawable.icon_basic_selector);
            }
        }

        intent.putStringArrayListExtra(RevealAnimationActivity.MENU_LIST,menuList);
        intent.putExtra(RevealAnimationActivity.FRAGMENTS,(Serializable)fragments);
        intent.putExtra(RevealAnimationActivity.ICON_RES_LIST,iconsResId);

        intent.putExtra(RevealAnimationActivity.TITLE, "Config");
        startActivityWithAnimation(intent);
    }

    private void onLogClicked(){
        Intent intent = new Intent(mContext,RevealAnimationActivity.class);
        ArrayList<String> menuList = new ArrayList<String>();
        menuList.add("Remote");
        menuList.add("Local");
        intent.putStringArrayListExtra(RevealAnimationActivity.MENU_LIST,menuList);

        ArrayList<Integer> iconsResId = new ArrayList<Integer>();
        iconsResId.add(R.drawable.icon_remote_selector);
        iconsResId.add(R.drawable.icon_local_selector);
        intent.putExtra(RevealAnimationActivity.ICON_RES_LIST,iconsResId);

        ArrayList<RevealAnimationBaseFragment> fragments = new ArrayList<RevealAnimationBaseFragment>();
        fragments.add(new Remote_Fragment());
        fragments.add(new Local_Fragment());
        intent.putExtra(RevealAnimationActivity.FRAGMENTS,(Serializable)fragments);

        intent.putExtra(RevealAnimationActivity.TITLE,"Log");
        startActivityWithAnimation(intent);
    }

    private void onFemtoClicked(){
        Intent intent = new Intent(mContext,RevealAnimationActivity.class);
        ArrayList<String> menuList = new ArrayList<String>();
        menuList.add("Broadcast");
        menuList.add("Scan");
        menuList.add("Target");
        menuList.add("Adjacent Cell");
        menuList.add("History");
        intent.putStringArrayListExtra(RevealAnimationActivity.MENU_LIST,menuList);

        ArrayList<RevealAnimationBaseFragment> fragments = new ArrayList<RevealAnimationBaseFragment>();
        fragments.add(new FragmentBcastStart());
        fragments.add(new FragmentCellScan());
        fragments.add(new FragmentTarget());
        fragments.add(new FragmentAdjacentCell());
        fragments.add(new FragmentBcastHistory());
        intent.putExtra(RevealAnimationActivity.FRAGMENTS,(Serializable)fragments);
        //icon
        ArrayList<Integer> iconsResId = new ArrayList<Integer>();
        iconsResId.add(R.drawable.icon_broadcast_selector);
        iconsResId.add(R.drawable.icon_scan_selector);
        iconsResId.add(R.drawable.icon_target_selector);
        iconsResId.add(R.drawable.icon_adjacent_cell_selector);
        iconsResId.add(R.drawable.icon_history_selector);
        intent.putExtra(RevealAnimationActivity.ICON_RES_LIST,iconsResId);

        intent.putExtra(RevealAnimationActivity.TITLE, "Femto");
        startActivityWithAnimation(intent);
    }

    private void onSystemClicked(){
        Intent intent = new Intent(mContext,RevealAnimationActivity.class);
        ArrayList<String> menuList = new ArrayList<String>();
        menuList.add("Upgrade");
        intent.putStringArrayListExtra(RevealAnimationActivity.MENU_LIST,menuList);

        ArrayList<Integer> iconsResId = new ArrayList<Integer>();
        iconsResId.add(R.drawable.icon_upgrade_selector);
        intent.putExtra(RevealAnimationActivity.ICON_RES_LIST,iconsResId);

        ArrayList<RevealAnimationBaseFragment> fragments = new ArrayList<RevealAnimationBaseFragment>();
        fragments.add(new Upgrade_Fragment());
        intent.putExtra(RevealAnimationActivity.FRAGMENTS,(Serializable)fragments);

        intent.putExtra(RevealAnimationActivity.TITLE,"System");
        startActivityWithAnimation(intent);
    }

    private void onTestClicked(){
        Intent intent = new Intent(mContext,RevealAnimationActivity.class);
        ArrayList<String> menuList = new ArrayList<String>();
        menuList.add("Terminal");
        intent.putStringArrayListExtra(RevealAnimationActivity.MENU_LIST,menuList);

        ArrayList<Integer> iconsResId = new ArrayList<Integer>();
        iconsResId.add(R.drawable.icon_terminal_selector);
        intent.putExtra(RevealAnimationActivity.ICON_RES_LIST,iconsResId);

        ArrayList<RevealAnimationBaseFragment> fragments = new ArrayList<RevealAnimationBaseFragment>();
        fragments.add(new Terminal_Fragmen());
        intent.putExtra(RevealAnimationActivity.FRAGMENTS,(Serializable)fragments);

        intent.putExtra(RevealAnimationActivity.TITLE,"Test");
        startActivityWithAnimation(intent);

        /*Intent intent = new Intent(this,NetTestActivity.class);
        startActivityWithAnimation(intent);*/
    }

    private void onScannerClicked(){
        Intent intent = new Intent(mContext,RevealAnimationActivity.class);
        ArrayList<String> menuList = new ArrayList<String>();
        menuList.add("Scanner");
        intent.putStringArrayListExtra(RevealAnimationActivity.MENU_LIST,menuList);

        ArrayList<RevealAnimationBaseFragment> fragments = new ArrayList<RevealAnimationBaseFragment>();
        fragments.add(new FragmentScannerListen());
        //fragments.add(new Terminal_Fragmen());
        intent.putExtra(RevealAnimationActivity.FRAGMENTS,(Serializable)fragments);
        //icon
        ArrayList<Integer> iconsResId = new ArrayList<Integer>();
        iconsResId.add(R.drawable.icon_broadcast_selector);
        intent.putExtra(RevealAnimationActivity.ICON_RES_LIST,iconsResId);

        intent.putExtra(RevealAnimationActivity.TITLE, "Scanner");
        startActivityWithAnimation(intent);
    }

    private void onFindClicked(){
        Intent intent = new Intent(mContext,RevealAnimationActivity.class);
        ArrayList<String> menuList = new ArrayList<String>();
        menuList.add("Find");
        intent.putStringArrayListExtra(RevealAnimationActivity.MENU_LIST,menuList);

        ArrayList<RevealAnimationBaseFragment> fragments = new ArrayList<RevealAnimationBaseFragment>();
        fragments.add(new FragmentFind());
        //fragments.add(new Terminal_Fragmen());
        intent.putExtra(RevealAnimationActivity.FRAGMENTS,(Serializable)fragments);
        //icon
        ArrayList<Integer> iconsResId = new ArrayList<Integer>();
        iconsResId.add(R.drawable.icon_scan_default);
        intent.putExtra(RevealAnimationActivity.ICON_RES_LIST,iconsResId);

        intent.putExtra(RevealAnimationActivity.TITLE, "Find");
        startActivityWithAnimation(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void StatusNotif(Status s) {
        super.StatusNotif(s);
        ((TextView) findViewById(R.id.tv_sn)).setText("SN:" + s.getFemtoSn());
        ((TextView) findViewById(R.id.tv_sw)).setText(s.getFemtoVer());
        ((TextView) findViewById(R.id.tv_tech_band)).setText("Tech:" + s.getTech() + "        Band:" + s.getBand());
    }
}
