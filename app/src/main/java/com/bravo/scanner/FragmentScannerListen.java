package com.bravo.scanner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.adapters.AdapterScanner;
import com.bravo.custom_view.CustomProgressDialog;
import com.bravo.custom_view.OneBtnHintDialog;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.custom_view.RecordOnItemClick;
import com.bravo.custom_view.RecordOnItemLongClick;
import com.bravo.data_ben.TargetDataStruct;
import com.bravo.database.TargetUser;
import com.bravo.database.TargetUserDao;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.fragments.SerializableHandler;
import com.bravo.socket_service.CommunicationService;
import com.bravo.utils.Logs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.POWER_SERVICE;
import static com.bravo.femto.BcastCommonApi.secToTime;

/**
 * Created by admin on 2018-9-14.
 */

public class FragmentScannerListen extends RevealAnimationBaseFragment {
    private final String TAG = "ScannerFragment";

    public static boolean isOpen = false; //界面是否打开
    public static boolean isStart = true; //是否为刷新状态

    private final int TIMER_SECOND = 1;
    private final int STOP_FLAG = 2;
    private final int START_FLAG = 3;
    private final int SEND_IMSI_FLAG = 4;

    private int iBcastTimer ;//开始捕号时间
    private Timer bcastTimer;//小区定时器

    private CustomProgressDialog proDialog;
    private OneBtnHintDialog hintDialog;

    private static Lock lock = new ReentrantLock();
    private final ArrayList<TargetDataStruct> targetDataStructs = new ArrayList<>();

    private PowerManager.WakeLock mWakeLock;

    private ListView TargetListView;
    private AdapterScanner adapterScanner;
    String strCurTech;
    @Override
    public void onResume() {
        super.onResume();
        loadData();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity)context).getSettingBtn().setImageResource(R.drawable.btn_end_normal);
        ((RevealAnimationActivity)context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                switchBtu();
            }
        });

        isOpen = true;
        switchBcastTimer(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_scanner_listen);

        //屏幕不休眠
        PowerManager pManager = ((PowerManager) context.getSystemService(POWER_SERVICE));
        mWakeLock = pManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ON_AFTER_RELEASE, TAG);
        mWakeLock.acquire();
    }
    private void coverTarget(TargetDataStruct newTarget, final TargetDataStruct oldTarget){
        TargetUser targetUser = ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(
                TargetUserDao.Properties.StrImsi.eq(oldTarget.getImsi()),
                TargetUserDao.Properties.StrImei.eq(oldTarget.getImei())).build().unique();
        if (targetUser != null) {
            targetUser.setStrImsi(newTarget.getImsi());
            targetUser.setStrImei(newTarget.getImei());
            targetUser.setStrName(newTarget.getName());
            targetUser.setStrTech(newTarget.getStrTech());
            targetUser.setStrBand(newTarget.getStrBand());
            targetUser.setStrChannel(newTarget.getStrChannel());
            targetUser.setBRedir(newTarget.isbRedir());
            ProxyApplication.getDaoSession().getTargetUserDao().update(targetUser);
        }
    }
    @Override
    public void initView() {
        TargetListView = (ListView) contentView.findViewById(R.id.scannerlist);
        adapterScanner = new AdapterScanner(context,(TextView) contentView.findViewById(R.id.cur_Total),TargetListView);
        TargetListView.setAdapter(adapterScanner);

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


    }

    @Override
    public void initData(Bundle savedInstanceState) {
        InitBcastEndDialog();
    }

    protected void InitBcastEndDialog() {
        proDialog = new CustomProgressDialog(context, R.style.dialog_style);
        hintDialog = new OneBtnHintDialog(context, R.style.dialog_style);
    }

    private void saveData() {
        
    }

    private void loadData() {

    }

    private void switchBtu()
    {
        if(isStart)
        {
            Message message = new Message();
            message.what = STOP_FLAG;
            handler.sendMessage(message);
        }
        else
        {
            Message message = new Message();
            message.what = START_FLAG;
            handler.sendMessage(message);
        }
    }

    class MyTimer extends TimerTask implements Serializable {
        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = TIMER_SECOND;
            handler.sendMessage(message);
        }
    }

    private void SendImsiTimer (){
        //Logs.d(TAG,"执行定时器。。。");

        lock.lock();
        try {
            //Logs.d(TAG,"接收到Scanner消息数量" + targetDataStructs.size());
            if (!isStart) {
                adapterScanner.ChangedTotal(targetDataStructs.size());
            } else {
                for(int i=targetDataStructs.size()-1;i>=0;i--)
                {
                    try {
                        adapterScanner.AttachTarget(targetDataStructs.get(i));
                    }catch (Exception e) {};
                    targetDataStructs.remove(i);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void switchBcastTimer(boolean bFlag) {
        if (bFlag) {//open
            SharedPreferences sp = context.getSharedPreferences(CommunicationService.TABLE_NAME, MODE_PRIVATE);
            Long starttime = sp.getLong(CommunicationService.tn_StartTime,System.currentTimeMillis());

            if (System.currentTimeMillis() > starttime) {
                iBcastTimer = (int)(System.currentTimeMillis() - starttime)/1000;
            } else {
                iBcastTimer = 0;
            }
            if (bcastTimer == null) {
                bcastTimer = new Timer();
                bcastTimer.schedule(new MyTimer(), 1000, 1500);
            }
        } else {
            iBcastTimer = 0;
            if (bcastTimer != null) {
                bcastTimer.purge();
                bcastTimer.cancel();
                bcastTimer = null;
            }
        }
    }

    private Handler handler = new SerializableHandler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_FLAG:
                    isStart = true;
                    ((RevealAnimationActivity)context).getSettingBtn().setImageResource(R.drawable.btn_end_normal);
                    break;
                case STOP_FLAG:
                    isStart = false;
                    ((RevealAnimationActivity)context).getSettingBtn().setImageResource(R.drawable.btn_refresh_normal);
                    break;
                case TIMER_SECOND://小区计时
                    iBcastTimer++;
                    ((TextView)contentView.findViewById(R.id.scanner_timemeter)).setText(secToTime(iBcastTimer));
                    break;
                case SEND_IMSI_FLAG://
                    SendImsiTimer();
                    //((TextView)contentView.findViewById(R.id.scanner_timemeter)).setText(secToTime(iBcastTimer));
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    public void onPause() {
        Logs.d(TAG,"onPause");
        saveData();
        isOpen = false;
        super.onPause();
    }

    @Override
    public void onStop() {
        Logs.d(TAG,"onStop");
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Logs.d(TAG,"onDestroy");
        super.onDestroy();
        switchBcastTimer(false);

        if (proDialog.isShowing()) {
            proDialog.dismiss();
        }
        if (hintDialog.isShowing()) {
            hintDialog.dismiss();
        }
        if(null != mWakeLock){
            mWakeLock.release();
        }
        if (bcastTimer != null) {
            bcastTimer.cancel();
            bcastTimer = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void TargetAttach(TargetDataStruct tds) {
        //Logs.d(TAG,"接收到Scanner消息" + tds.getiUserType());
        lock.lock();
        try{
            //Logs.d(TAG,"接收到Scanner消息:" + tds.getImsi());
            targetDataStructs.add(0,tds);
        } finally {
            lock.unlock();
        }

        Message message = new Message();
        message.what = SEND_IMSI_FLAG;
        handler.sendMessage(message);
    }


}
