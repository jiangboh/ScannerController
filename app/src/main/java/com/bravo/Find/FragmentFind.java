package com.bravo.Find;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.adapters.AdapeterFind;
import com.bravo.config.Fragment_SystemConfig;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.custom_view.RecordOnItemClick;
import com.bravo.custom_view.RecordOnItemLongClick;
import com.bravo.data_ben.DeviceDataStruct;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.fragments.SerializableHandler;
import com.bravo.parse_generate_xml.Find.FindDeviceInfo;
import com.bravo.socket_service.EventBusMsgSendUDPBroadcastMsg;
import com.bravo.utils.Logs;
import com.bravo.xml.Msg_Body_Struct;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;
import static com.bravo.R.drawable.btn_scan_normal;
import static com.bravo.socket_service.CommunicationService.udpBroadCastPortArray;
import static com.bravo.utils.Utils.getWifiBroadcastIp;
import static com.bravo.utils.Utils.getWifiIp;
import static com.bravo.xml.XmlCodec.EncodeApXmlMessage;

/**
 * Created by admin on 2018-9-17.
 */

public class FragmentFind extends RevealAnimationBaseFragment {
    private final String TAG = "FragmentFind";
    public static boolean isOpen = false;

    private final int FIND_END = 0;
    private final int  FIND_START = 1;
    private final int FIND_BACK_0 = 2;
    private final int FIND_BACK_1 = 3;
    private final int FIND_BACK_2 = 4;
    private final int FIND_BACK_3 = 5;

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

    private int curItem = 0;

    @Override
    public void onResume() {
        Logs.d(TAG,"onResume",true);
        super.onResume();
        loadData();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity)context).getSettingBtn().setImageResource(btn_scan_normal);
        ((RevealAnimationActivity)context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                adapterFind.RemoveAll();
                //addDevice(); //加虚拟设备，测试时用，正式版本不用
                SwitchView(true);
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

        //记住上一次滚动时的位置信息
        TargetListView.setOnScrollListener(new AbsListView.OnScrollListener(){
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //滚动时一直回调，直到停止滚动时才停止回调。单击时回调一次。
                //firstVisibleItem：当前能看见的第一个列表项ID（从0开始）
                //visibleItemCount：当前能看见的列表项个数（小半个也算）
                //totalItemCount：列表项共数
                curItem = firstVisibleItem;
            }
            @Override
            public void onScrollStateChanged(AbsListView view , int scrollState){
                //正在滚动时回调，回调2-3次，手指没抛则回调2次。scrollState = 2的这次不回调
                //回调顺序如下
                //第1次：scrollState = SCROLL_STATE_TOUCH_SCROLL(1) 正在滚动
                //第2次：scrollState = SCROLL_STATE_FLING(2) 手指做了抛的动作（手指离开屏幕前，用力滑了一下）
                //第3次：scrollState = SCROLL_STATE_IDLE(0) 停止滚动
            }
        });

        TargetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // 于对选中的项进行处理
                //将上一次滚动时的第一条信息，重新展示为第一条信息，即：实现点击后点击条目的位置不变；
                TargetListView.setSelection(curItem);
            }
        });

        residualTime = (TextView) contentView.findViewById(R.id.residualTime);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logs.d(TAG,"onCreate",true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_find);
    }

    private void saveData() {

    }

    private void loadData() {
        SharedPreferences sp = context.getSharedPreferences(FragmentFindConfig.TABLE_NAME, MODE_PRIVATE);
        AllFindTime = sp.getInt(FragmentFindConfig.tn_AllFindTime,10);

        SharedPreferences sp1 = context.getSharedPreferences(Fragment_SystemConfig.TABLE_NAME, MODE_PRIVATE);
        udpPort = sp1.getInt(Fragment_SystemConfig.tn_LisenPort,Fragment_SystemConfig.DefultPort);
    }

    private void BroadCast()
    {
        Msg_Body_Struct msg = new Msg_Body_Struct(0,Msg_Body_Struct.BroadCast);
        msg.dic.put("ip",getWifiIp(context));
        msg.dic.put("port", udpPort);
        String sendText = EncodeApXmlMessage(msg);
        //发送广播
        for(int i =0;i<udpBroadCastPortArray.length;i++) {
            EventBus.getDefault().post(new EventBusMsgSendUDPBroadcastMsg(getWifiBroadcastIp(context),
                    udpBroadCastPortArray[i], sendText));
        }
        //EventBus.getDefault().post(new EventBusMsgSendUDPMsg("192.168.100.102", CommunicationService.udpBroadCastPort_lte, sendText));
    }


    class MyTimer extends TimerTask implements Serializable {
        @Override
        public void run() {
            StartFindNum ++;
            Logs.d(TAG,"StartFindNum=" + StartFindNum,true);

            BroadCast();
            if (StartFindNum > AllFindTime)
            {
                SwitchView(false);
            }
            else if (StartFindNum > 0)
            {
                Message message = new Message();
                if ((StartFindNum%4) == 0) {
                    message.what = FIND_BACK_0;
                }
                else if ((StartFindNum%4) == 1) {
                    message.what = FIND_BACK_1;
                }
                else if ((StartFindNum%4) == 2) {
                    message.what = FIND_BACK_2;
                }
                else if ((StartFindNum%4) == 3) {
                    message.what = FIND_BACK_3;
                }
                else
                {
                    message.what = FIND_BACK_0;
                }
                handler.sendMessage(message);
            }
        }
    }

    private void addDevice()
    {
        //虚拟添加设备
        DeviceDataStruct dds = new DeviceDataStruct();
        dds.setSN("EN1801S118220032");
        dds.setIp("172.17.18.31");
        dds.setPort(5001);
        dds.setMode("LTE_FDD");
        dds.setFullName("电信Band3");
        adapterFind.DeviceListTarget(dds);
        DeviceDataStruct dds1 = new DeviceDataStruct();
        dds1.setSN("EN1801S118220033");
        dds1.setIp("172.17.18.32");
        dds1.setPort(5001);
        dds1.setMode("LTE_FDD");
        dds1.setFullName("联通Band1");
        adapterFind.DeviceListTarget(dds1);
        DeviceDataStruct dds2 = new DeviceDataStruct();
        dds2.setSN("EN1801S118220085");
        dds2.setIp("172.17.18.33");
        dds2.setPort(5001);
        dds2.setMode("LTE_FDD");
        dds2.setFullName("电信Band5");
        adapterFind.DeviceListTarget(dds2);
        DeviceDataStruct dds3 = new DeviceDataStruct();
        dds3.setSN("EN1801S118220096");
        dds3.setIp("172.17.18.34");
        dds3.setPort(5001);
        dds3.setMode("LTE_TDD");
        dds3.setFullName("移动Band8");
        adapterFind.DeviceListTarget(dds3);
        DeviceDataStruct dds4 = new DeviceDataStruct();
        dds4.setSN("EN1801S118220089");
        dds4.setIp("172.17.18.35");
        dds4.setPort(5001);
        dds4.setMode("LTE_TDD");
        dds4.setFullName("移动Band39");
        adapterFind.DeviceListTarget(dds4);
        DeviceDataStruct dds5 = new DeviceDataStruct();
        dds5.setSN("EN1801S118220091");
        dds5.setIp("172.17.18.36");
        dds5.setPort(5001);
        dds5.setMode("LTE_TDD");
        dds5.setFullName("移动Band41");
        adapterFind.DeviceListTarget(dds5);
    }

    private void SwitchView(boolean bFind)
    {
        Message message = new Message();
        Logs.d(TAG,"SwitchView,bFind=" + bFind,true);
        if (bFind)
        {
            isFind = true;
            message.what = FIND_START;
            StartFindNum = 0;
            timer = new Timer();
            timer.schedule(new MyTimer(), 1000, 1000);
        }
        else
        {
            isFind = false;
            message.what = FIND_END;
            if (timer != null) {
                timer.purge();
                timer.cancel();
                timer = null;
            }
        }
        handler.sendMessage(message);
    }

    private Handler handler = new SerializableHandler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Logs.d(TAG,"what=" + msg.what,true);
            switch (msg.what) {
                case FIND_START:
                    residualTime.setText("搜索剩余" + (AllFindTime) + "秒");
                    findImageViewLayout.setVisibility(View.VISIBLE);
                    ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.GONE);
                    break;
                case FIND_END:
                    residualTime.setText("");
                    findImageViewLayout.setVisibility(View.GONE);
                    ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.VISIBLE);
                    break;
                case FIND_BACK_0:
                    residualTime.setText("搜索剩余" + (AllFindTime - StartFindNum) + "秒");
                    imageView.setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(),R.drawable.ic_wifi_signal_1_dark));
                    break;
                case FIND_BACK_1:
                    residualTime.setText("搜索剩余" + (AllFindTime - StartFindNum) + "秒");
                    imageView.setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(),R.drawable.ic_wifi_signal_2_dark));
                    break;
                case FIND_BACK_2:
                    residualTime.setText("搜索剩余" + (AllFindTime - StartFindNum) + "秒");
                    imageView.setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(),R.drawable.ic_wifi_signal_3_dark));
                    break;
                case FIND_BACK_3:
                    residualTime.setText("搜索剩余" + (AllFindTime - StartFindNum) + "秒");
                    imageView.setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext(),R.drawable.ic_wifi_signal_4_dark));
                    break;
                default:
                    break;
            }

        }
    };

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
