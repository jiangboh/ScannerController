package com.bravo.config;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.adapters.AdapeterDeviceList;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.custom_view.RecordOnItemClick;
import com.bravo.custom_view.RecordOnItemLongClick;
import com.bravo.data_ben.DeviceDataStruct;
import com.bravo.dialog.DialogDeviceInfo;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.utils.Logs;
import com.bravo.xml.HandleRecvXmlMsg;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by admin on 2018-9-19.
 */

public class Fragment_Device extends RevealAnimationBaseFragment {
    private final String TAG = "Fragment_Device";

    public static boolean isOpen = false;

    private ListView TargetListView;
    private AdapeterDeviceList adapterList;

    private TextView device_Total;

    @Override
    public void onResume() {
        super.onResume();
        loadData();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.GONE);
        ((RevealAnimationActivity)context).getSettingBtn().setImageResource(R.drawable.btn_to_scan_normal);
        ((RevealAnimationActivity)context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {

            }
        });

        isOpen = true;
        adapterList.dataChanged();

    }

    @Override
    public void initView() {

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        device_Total = (TextView) contentView.findViewById(R.id.device_Total);

        TargetListView = (ListView) contentView.findViewById(R.id.devicelist);
        adapterList = new AdapeterDeviceList(context,device_Total);
        TargetListView.setAdapter(adapterList);
        TargetListView.setOnItemLongClickListener(new RecordOnItemLongClick() {
            @Override
            public void recordOnItemLongClick(AdapterView<?> parent, View view, final int position, long id, String strMsg) {
                new HandleRecvXmlMsg(context,adapterList.getItem(position)).SetDeviceReboot();
            }
        });

        TargetListView.setOnItemClickListener(new RecordOnItemClick() {
            @Override
            public void recordOnItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                          long arg3, String strMsg) {
                //DeviceDataStruct deviceDataStruct = adapterList.getItem(arg2);
                DialogDeviceInfo dialog = new DialogDeviceInfo(context,adapterList.getItem(arg2));
                //这句话，就是决定上面的那个黑框，也就是dialog的title。
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_device_list);
    }

    private void saveData() {

    }

    private void loadData() {

    }

    @Override
    public void onPause() {
        Logs.d(TAG,"onPause",true);
        saveData();
        isOpen = false;
        super.onPause();
    }

    @Override
    public void onStop() {
        Logs.d(TAG,"onStop",true);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Logs.d(TAG,"onDestroy",true);
        super.onDestroy();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void TargetAttach(DeviceDataStruct dds) {
        Logs.d(TAG,"接收心跳消息。",true);
        adapterList.dataChanged(dds);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void TargetRemove(int index) {
        Logs.d(TAG,"接收心跳消息。",true);
        adapterList.removeTarget();
    }
}
