package com.bravo.femto;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.bravo.FemtoController.BaseActivity;
import com.bravo.FemtoController.ProxyApplication;
import com.bravo.R;
import com.bravo.adapters.AdapterConnTarget;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.data_ben.TargetDataStruct;
import com.bravo.database.User;
import com.bravo.database.UserDao;
import com.bravo.parse_generate_xml.cell_scan.CellScanCell;
import com.bravo.utils.SharePreferenceUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryActivity extends BaseActivity {
    private ListView TargetListView;
    private List<CellScanCell> cells = new ArrayList<>();
    private AdapterConnTarget adapterConnTarget;
    private String strCurTech;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_history);

        strCurTech = SharePreferenceUtils.getInstance(this).getString("status_notif_tech" + ((ProxyApplication) getApplicationContext()).getCurSocketAddress() + ((ProxyApplication) getApplicationContext()).getiTcpPort(), "");

        //back button
        findViewById(R.id.iv_activity_back).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                onBackPressed();
                super.recordOnClick(v, "Back Event " + TAG);
            }
        });
        ((TextView) findViewById(R.id.tv_activity_title)).setText("History");
        findViewById(R.id.user_total).setVisibility(View.VISIBLE);
        //target list
        TargetListView = (ListView) findViewById(R.id.target_list);
        adapterConnTarget = new AdapterConnTarget(this, (TextView)findViewById(R.id.target_tab));
        adapterConnTarget.updateCurTech(strCurTech);
        TargetListView.setAdapter(adapterConnTarget);

        setStatusBarsColor(R.color.colorPrimaryDark);
        initStatusView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        String Unique = getIntent().getStringExtra("unique");
        Long StatrTime = getIntent().getLongExtra("starttime", 0);
        Long EndTime = getIntent().getLongExtra("endtime", 0);
        List<User> users = null;
        if (EndTime != null && StatrTime < EndTime) {
            users = ProxyApplication.getDaoSession().getUserDao().queryBuilder().where(UserDao.Properties.Unique.eq(Unique),
                    UserDao.Properties.ConnTime.gt(StatrTime), UserDao.Properties.ConnTime.lt(EndTime)).build().list();
        } else {
            users = ProxyApplication.getDaoSession().getUserDao().queryBuilder().where(UserDao.Properties.Unique.eq(Unique),
                    UserDao.Properties.ConnTime.gt(StatrTime)).build().list();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
//        Logs.d(TAG, "lmj: S->" + formatter.format(new Date(StatrTime)) + "E->" + formatter.format(new Date(EndTime)));
        for (int i = 0; i < users.size(); i++) {
//            Logs.d(TAG, "lmj结果：" + users.get(i).getId() + "," + users.get(i).getUnique() + "," + users.get(i).getSrtImsi() + "," + users.get(i).getStrImei() + "," + users.get(i).getIAuth() + "," + users.get(i).getBSilent());
            TargetDataStruct targetDataStruct = new TargetDataStruct();
            targetDataStruct.setImsi(users.get(i).getSrtImsi());
            targetDataStruct.setImei(users.get(i).getStrImei());
            targetDataStruct.setAuthState(users.get(i).getIAuth());
            targetDataStruct.setSilentState(users.get(i).getBSilent());
            targetDataStruct.setStrConntime(formatter.format(new Date(users.get(i).getConnTime())));
            if (users.get(i).getDetachTime() != null) {
                targetDataStruct.setStrDetachtime(formatter.format(new Date(users.get(i).getDetachTime())));
            }
            targetDataStruct.setCount(users.get(i).getICount());
            if (targetDataStruct.getAuthState() == 1) {
                targetDataStruct.setStrAttachtime(formatter.format(new Date(users.get(i).getAttachTime())));
                adapterConnTarget.AttachTarget(targetDataStruct);
            } else {
                adapterConnTarget.addTarget(targetDataStruct);
            }
        }
    }
}
