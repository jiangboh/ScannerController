package com.bravo.Find;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.View;
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
