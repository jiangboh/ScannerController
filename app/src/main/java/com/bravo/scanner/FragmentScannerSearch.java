package com.bravo.scanner;

import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.adapters.AdapterScanner;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.custom_view.RecordOnItemClick;
import com.bravo.custom_view.RecordOnItemLongClick;
import com.bravo.data_ben.TargetDataStruct;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.utils.Logs;

import org.greenrobot.eventbus.EventBus;

import static android.content.Context.POWER_SERVICE;

/**
 * Created by admin on 2018-9-30.
 */

public class FragmentScannerSearch extends RevealAnimationBaseFragment {
    private final String TAG = "FragmentScannerSearch";
    private PowerManager.WakeLock mWakeLock;
    private ListView TargetListView;
    private AdapterScanner adapterScanner;
    @Override
    public void onResume() {
        super.onResume();
        //loadData();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity)context).getSettingBtn().setImageResource(R.drawable.btn_end_normal);
        ((RevealAnimationActivity)context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                //switchBtu();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_scanner_search);

        //屏幕不休眠
        PowerManager pManager = ((PowerManager) context.getSystemService(POWER_SERVICE));
        mWakeLock = pManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ON_AFTER_RELEASE, TAG);
        mWakeLock.acquire();
    }

    @Override
    public void initView() {
        TargetListView = (ListView) contentView.findViewById(R.id.scannerlist);
        adapterScanner = new AdapterScanner(context,(TextView) contentView.findViewById(R.id.cur_Total),TargetListView);
        TargetListView.setAdapter(adapterScanner);

        try {
            TargetListView.setOnItemLongClickListener(new RecordOnItemLongClick() {
                @Override
                public void recordOnItemLongClick(AdapterView<?> parent, View view, final int position, long id, String strMsg) {
                    return;
                }
            });
            TargetListView.setOnItemClickListener(new RecordOnItemClick() {
                @Override
                public void recordOnItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3, String strMsg) {
                    TargetDataStruct targetDataStruct = adapterScanner.getItem(arg2);
                    new DialogScannerInfo(context,targetDataStruct).show();
                    //super.recordOnItemClick(arg0, arg1, arg2, arg3, "User Item Click Event " + targetDataStruct.getImsi());
                    Log.d(TAG,"点击：" + targetDataStruct.getImsi());
                }
            });
        }catch (Exception e) {
            Logs.e(TAG,"点击界面出错：" + e.getMessage());
        }
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }
}
