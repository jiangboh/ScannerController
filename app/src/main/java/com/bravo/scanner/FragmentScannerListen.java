package com.bravo.scanner;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.adapters.AdapterScanner;
import com.bravo.custom_view.CustomProgressDialog;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.OneBtnHintDialog;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.custom_view.RecordOnItemClick;
import com.bravo.custom_view.RecordOnItemLongClick;
import com.bravo.data_ben.TargetDataStruct;
import com.bravo.database.BlackWhiteImsi;
import com.bravo.database.TargetUser;
import com.bravo.database.TargetUserDao;
import com.bravo.dialog.DialogCustomBuilder;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.fragments.SerializableHandler;
import com.bravo.socket_service.CommunicationService;
import com.bravo.utils.Logs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private final int SEND_IMSI_START = 4;
    private final int SEND_IMSI_END = 5;

    private int iBcastTimer ;//开始捕号时间
    private Timer bcastTimer;//小区定时器

    private int index = 0;

    private Long changedTime = System.currentTimeMillis();

    private CustomProgressDialog proDialog;
    private OneBtnHintDialog hintDialog;

    private TextView toPositionListen;
    private TextView saveImsi;

    private static Lock lock = new ReentrantLock();
    private final ArrayList<TargetDataStruct> targetDataStructs = new ArrayList<>();

    private PowerManager.WakeLock mWakeLock;

    private ListView TargetListView;
    private AdapterScanner adapterScanner;
    //String strCurTech;
    private int iMaxNum;
    private boolean isDupRemo;

    //用户选择的保存imsi的文件路径
    private String SaveImsiName;

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
        adapterScanner.ChangedTotal(targetDataStructs.size());
        adapterScanner.setSelectionEnd();
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
        saveImsi = (TextView) contentView.findViewById(R.id.tv_SaveImsi);
        saveImsi.setEnabled(false);
        saveImsi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                saveImsi2file();
            }
        });
        saveImsi.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                saveImsi2file();
                return false;
            }
        });

        toPositionListen = (TextView) contentView.findViewById(R.id.tv_toPosition);
        toPositionListen.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
        toPositionListen.setEnabled(false);
        toPositionListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //跳转到捕号页面
                ((RevealAnimationActivity)context).changeFragment(1, new Bundle());
            }
        });
        toPositionListen.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((RevealAnimationActivity)context).changeFragment(1, new Bundle());
                return false;
            }
        });

        TargetListView = (ListView) contentView.findViewById(R.id.scannerlist);
        adapterScanner = new AdapterScanner(context,(TextView) contentView.findViewById(R.id.cur_Total),TargetListView);
        TargetListView.setAdapter(adapterScanner);

        try {
            TargetListView.setOnItemLongClickListener(new RecordOnItemLongClick() {
                @Override
                public void recordOnItemLongClick(AdapterView<?> parent, View view, final int position, long id, String strMsg) {
                   TargetDataStruct targetDataStruct = adapterScanner.getItem(position);
                   Logs.d(TAG,"长按：" + targetDataStruct.getImsi(),true);
                   BlackWhiteImsi info = new BlackWhiteImsi();
                    info.setStartRb(-1);
                    info.setStopRb(-1);
                   info.setImsi(targetDataStruct.getImsi());
                   info.setImei(targetDataStruct.getImei());
                   info.setTmsi(targetDataStruct.getTmsi());
                   info.setType(targetDataStruct.getiUserType());

                   DialogScannerMenu dialog = new DialogScannerMenu(context,info);

                   dialog.show();
               }
            });
            TargetListView.setOnItemClickListener(new RecordOnItemClick() {
                @Override
                public void recordOnItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3, String strMsg) {
                    TargetDataStruct targetDataStruct = adapterScanner.getItem(arg2);
                    //super.recordOnItemClick(arg0, arg1, arg2, arg3, "User Item Click Event " + targetDataStruct.getImsi());
                    Logs.d(TAG,"点击：" + targetDataStruct.getImsi(),true);
                    DialogScannerInfo dialog = new DialogScannerInfo(context,targetDataStruct);
                    //这句话，就是决定上面的那个黑框，也就是dialog的title。
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.show();
                }
            });
        }catch (Exception e) {
            Logs.e(TAG,"点击界面出错：" + e.getMessage(),true);
        }
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        InitBcastEndDialog();
    }


    private void saveImsi2file()
    {
        SharedPreferences sp = context.getSharedPreferences(FragmentScannerConfig.TABLE_NAME, MODE_PRIVATE);
        String SaveImsiPath = sp.getString(FragmentScannerConfig.ImsiSavePath,"");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");// HH:mm:ss
        // 获取当前时间
        Date date = new Date(System.currentTimeMillis());
        SaveImsiName = SaveImsiPath + "/" + simpleDateFormat.format(date) + ".txt";
        DialogCustomBuilder dialog = new DialogCustomBuilder(context,"保存捕号信息","确定将捕号信息保存到\n" + SaveImsiName );
        dialog.setOkListener(new DialogCustomBuilder.OkBtnClickListener() {
            @Override
            public void onBtnClick(DialogInterface arg0, int arg1) {
                saveImsi.setEnabled(false);
                Logs.i(TAG, "保存捕号的IMSI到：" + SaveImsiName, true);
                try {
                    File file = new File(SaveImsiName);
                    if (!file.exists()) {
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                    }
                    RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                    raf.seek(file.length());

                    lock.lock();
                    try {
                        ArrayList<TargetDataStruct> list = adapterScanner.getList();
                        for (int i = 0; i < list.size(); i++) {
                            String strContent = GetSaveStr(list.get(i));
                            raf.write(strContent.getBytes());
                        }
                    } finally {
                        lock.unlock();
                    }

                    raf.close();
                    CustomToast.showToast(context, "捕号信息保存成功");
                } catch (Exception e) {
                    CustomToast.showToast(context, "捕号信息保存失败");
                    Log.e(TAG, "保存捕号的IMSI到：" + SaveImsiName + "出错。出错原因：" + e.getMessage());
                }
                saveImsi.setEnabled(true);
            }
        });
        dialog.setCancelListener(new DialogCustomBuilder.CancelBtnClickListener() {
            @Override
            public void onBtnClick(DialogInterface arg0, int arg1) {
            }
        });
        dialog.show();
    }

    //写入的文件格式
    private String GetSaveStr(TargetDataStruct tds)
    {
        String userType = "";
        if (tds.getiUserType() == BlackWhiteImsi.BLACK)
        {
            userType = "Black";
        }
        else if (tds.getiUserType() == BlackWhiteImsi.WHITE)
        {
            userType = "White";
        }
        else
        {
            userType = "Other";
        }
        return String.format("%s:Sn=%s;DeviceType=%s;UserType=%s;Imsi=%s;Imei=%s;Tmsi=%s;" +
                        "Rsrp=%d;Latitude=%s;Longitude=%s\r\n",
                tds.getStrAttachtime(),tds.getSN(), tds.getDeviceType(),
                userType,tds.getImsi(),tds.getImei(),tds.getTmsi(),tds.getRsrp(),
                tds.getStrLatitude(),tds.getStrLongitude());
    }

    protected void InitBcastEndDialog() {
        proDialog = new CustomProgressDialog(context, R.style.dialog_style);
        hintDialog = new OneBtnHintDialog(context, R.style.dialog_style);
    }

    private void saveData() {
        
    }

    private void loadData() {
        SharedPreferences sp = context.getSharedPreferences(FragmentScannerConfig.TABLE_NAME, MODE_PRIVATE);
        iMaxNum = sp.getInt(FragmentScannerConfig.tn_MaxNum,FragmentScannerConfig.DefultMaxNum);
        Log.v("初始值：", String.valueOf(iMaxNum));
        isDupRemo = sp.getBoolean(FragmentScannerConfig.tn_DupRemo,FragmentScannerConfig.DefultDupRemo);

        AdapterScanner.setIsDupRemo(isDupRemo);
        AdapterScanner.setMaxTotal(iMaxNum);

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

    class SendEndTimer extends TimerTask implements Serializable {
        @Override
        public void run() {
            //Logs.d(TAG,"发送run。。。" + index);
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = SEND_IMSI_END;
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
                try {
                    //Logs.d(TAG,"发送IMSI开始。。。" + index);
                    TargetListView.setEnabled(false);
                    adapterScanner.AttachTarget(targetDataStructs);

                    new Timer().schedule(new SendEndTimer(), 100);
                }catch (Exception e) {
                    Logs.e(TAG,"刷新界面出错：" + e.getMessage(),true);
                }
                targetDataStructs.clear();
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
                    TargetListView.setEnabled(true);
                    ((RevealAnimationActivity)context).getSettingBtn().setImageResource(R.drawable.btn_refresh_normal);
                    break;
                case TIMER_SECOND://小区计时
                    iBcastTimer++;
                    ((TextView)contentView.findViewById(R.id.scanner_timemeter)).setText(secToTime(iBcastTimer));
                    break;
                case SEND_IMSI_START://
                    SendImsiTimer();
                    //((TextView)contentView.findViewById(R.id.scanner_timemeter)).setText(secToTime(iBcastTimer));
                    break;
                case SEND_IMSI_END://
                    //Logs.d(TAG,"发送IMSI完成！" + index);
                    index++;
                    TargetListView.setEnabled(true);
                    //((TextView)contentView.findViewById(R.id.scanner_timemeter)).setText(secToTime(iBcastTimer));
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
            if (tds.getiUserType() == TargetDataStruct.BLACK_IMSI)
            {
                toPositionListen.setEnabled(true);
            }
            targetDataStructs.add(0,tds);
            //Logs.d(TAG,"捕号数量:" + targetDataStructs.size());
        } finally {
            lock.unlock();
        }

        Long curTime = System.currentTimeMillis();
        //大于秒才刷新界面
        if ((curTime - changedTime) > 1000) {
            changedTime = System.currentTimeMillis();
            Message message = new Message();
            message.what = SEND_IMSI_START;
            handler.sendMessage(message);
            saveImsi.setEnabled(true);
        }
    }


}
