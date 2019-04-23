package com.bravo.Find;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.adapters.AdapeterFind;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.custom_view.RecordOnItemClick;
import com.bravo.custom_view.RecordOnItemLongClick;
import com.bravo.data_ben.DeviceDataStruct;
import com.bravo.data_ben.DeviceFragmentStruct;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.parse_generate_xml.Find.FindDeviceInfo;
import com.bravo.utils.Logs;
import com.bravo.xml.HandleRecvXmlMsg;
import com.bravo.xml.Msg_Body_Struct;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Timer;

import static com.bravo.R.drawable.btn_refresh_selector;

/**
 * Created by admin on 2019-4-23.
 */

public class FragmentNeighborCell extends RevealAnimationBaseFragment {
    private final String TAG = "FragmentNeighborCell";
    public static boolean isOpen = false;

    //保存选择的设备数据
    private DeviceDataStruct deviceDate = null;
    //所有上线的设备列表
    private ArrayList<String> dList;
    private Spinner s_deviceSelect;

    private Timer timer = null;
    private int StartFindNum = 0;
    private LinearLayout findImageViewLayout;
    private ImageView imageView;
    private TextView residualTime;

    private ListView TargetListView;
    private AdapeterFind adapterFind;

    private Boolean isFind = false;
    private int udpPort;

    private int AllFindTime ; //搜索时间

    @Override
    public void onResume() {
        Logs.d(TAG,"onResume",true);
        super.onResume();
        loadData();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity)context).getSettingBtn().setImageResource(btn_refresh_selector);
        ((RevealAnimationActivity)context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                SendGetSonCellInfo();
            }
        });
        isOpen = true;
    }

    @Override
    public void initView() {
        Logs.d(TAG,"initView",true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        Logs.d(TAG,"initData",true);

        dList = DeviceFragmentStruct.getSnList();

        s_deviceSelect = (Spinner) contentView.findViewById(R.id.deviceSelect);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,R.layout.my_spinner ,dList);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item ,dList);
        s_deviceSelect.setAdapter(adapter);
        //添加事件Spinner事件监听
        s_deviceSelect.setOnItemSelectedListener(new SpinnerSelectedListener());
        s_deviceSelect.setSelection(0 ,true);



        findImageViewLayout= (LinearLayout)contentView.findViewById(R.id.FindImageViewLayout);
        findImageViewLayout.setVisibility(View.GONE);

        imageView = (ImageView) contentView.findViewById(R.id.FindImageView);

        TargetListView = (ListView) contentView.findViewById(R.id.findDeviceList);
        adapterFind = new AdapeterFind(context,true);
        TargetListView.setAdapter(adapterFind);
        TargetListView.setOnItemLongClickListener(new RecordOnItemLongClick() {
            @Override
            public void recordOnItemLongClick(AdapterView<?> parent, View view, final int position, long id, String strMsg) {

            }
        });
        TargetListView.setOnItemClickListener(new RecordOnItemClick() {
            @Override
            public void recordOnItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                          long arg3, String strMsg) {

            }
        });
        residualTime = (TextView) contentView.findViewById(R.id.residualTime);
    }

    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Logs.d(TAG, "您选择了:" + dList.get(position));

            deviceDate = DeviceFragmentStruct.getDevice(dList.get(position));
            SendGetSonCellInfo();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    private void SendGetSonCellInfo(){
        if (deviceDate == null) {
            Logs.e(TAG, String.format("您选择的产品,未在设备列表中找到该设备!"));
            return;
        }
        new HandleRecvXmlMsg(context,deviceDate).GetSonCellInfoRequest();
        CustomToast.showToast(context, "获取邻区信息已下发，请等待返回结果");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logs.d(TAG,"onCreate",true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_neighbor_cell);
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
    public void TargetAttach(FindDeviceInfo fdi) {
        Logs.d(TAG,"接收到广播回应消息",true);
        if (!isFind)
        {
            Logs.d(TAG,"不是设备搜索接收时间！",true);
            return;
        }

        adapterFind.DeviceListTarget(new DeviceDataStruct().xmlToBean(fdi));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void TargetAttach(Msg_Body_Struct mbs) {
        Logs.d(TAG,"接收到设备上线消息",true);
        FindDeviceInfo fdi = FindDeviceInfo.xmlToBean(mbs);
        DeviceDataStruct dds = new DeviceDataStruct();
        dds.setSN(fdi.getSN());
        dds.setiState(DeviceDataStruct.ON_LINE);
        adapterFind.DeviceListChange(dds);
    }
}
