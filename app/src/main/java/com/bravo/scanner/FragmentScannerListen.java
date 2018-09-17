package com.bravo.scanner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bravo.FemtoController.BaseActivity;
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
import com.bravo.database.TargetUser;
import com.bravo.database.User;
import com.bravo.database.UserDao;
import com.bravo.dialog.DialogAddTarget;
import com.bravo.femto.AttachInfoActivity;
import com.bravo.femto.IPEdit;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.fragments.SerializableHandler;
import com.bravo.parse_generate_xml.target_attach.TargetAttach;
import com.bravo.utils.Logs;
import com.bravo.utils.SharePreferenceUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.POWER_SERVICE;
import static com.bravo.femto.BcastCommonApi.secToTime;
import static com.bravo.femto.BcastCommonApi.sendTargetList;

/**
 * Created by admin on 2018-9-14.
 */

public class FragmentScannerListen extends RevealAnimationBaseFragment {
    private final String TAG = "ScannerFragment";

    private final int TIMER_SECOND = 1;
    private final int BCAST_END_TIMEOUT = 4;
    private final int BCAST_SCANNING = 0;
    private final int BCAST_START = 13;
    private final int BCAST_END = 14;
    private final int BCAST_FAILURE = 3;
    private int iBcastTimer;//小区运行时间单位s
    private Timer bcastTimer;//小区定时器

    private CustomProgressDialog proDialog;
    private OneBtnHintDialog hintDialog;

    private PowerManager.WakeLock mWakeLock;

    private Spinner spinner_mode;
    private EditText edit_port;
    private EditText edit_retry;
    private IPEdit ipEdit_default_gw;
    private IPEdit ipEdit_nb_gw;
    private String strControlIP;
    private Spinner spinner_interval;

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
               /* if (CheckObserverMode(strControlIP) && !ButtonUtils.isFastDoubleClick()){
                    SetConfig();
                }

                InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0); //强制隐藏键盘
                super.recordOnClick(v, "Set Config Femto Event");*/
            }
        });

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

    @Override
    public void initView() {
        TargetListView = (ListView) contentView.findViewById(R.id.scannerlist);
        adapterScanner = new AdapterScanner(context,(TextView) contentView.findViewById(R.id.cur_Total));
        TargetListView.setAdapter(adapterScanner);
        TargetListView.setOnItemLongClickListener(new RecordOnItemLongClick() {
            @Override
            public void recordOnItemLongClick(AdapterView<?> parent, View view, final int position, long id, String strMsg) {
                final TargetDataStruct targetDataStruct = adapterScanner.getItem(position);
                if (targetDataStruct.getAuthState() == 0) {
                    //((RevealAnimationActivity)context).changeFragment(3, new Bundle());
                    DialogAddTarget dialogAddTarget = new DialogAddTarget(context, R.style.dialog_style, new DialogAddTarget.OnAddTargetDialogListener() {
                        @Override
                        public void AddTargetCallBack(TargetDataStruct addTarget) {
                            List<User> users = ProxyApplication.getDaoSession().getUserDao().queryBuilder().where(UserDao.Properties.Unique.eq(SharePreferenceUtils.getInstance(context).getString("status_notif_unique" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "")),
                                    UserDao.Properties.SrtImsi.eq(addTarget.getImsi())).build().list();
                            //判断用户是否存在数据库 存在改变状态
                            if (users.size() != 0) {
                                User updateData = users.get(0);
                                updateData.setIAuth(2);
                                ProxyApplication.getDaoSession().getUserDao().update(updateData);
                                adapterScanner.removeTarget(position);
                                targetDataStruct.setAuthState(2);
                                adapterScanner.addTarget(targetDataStruct);
                            }
                            //add target
                            TargetUser targetUser = new TargetUser(null, addTarget.getImsi(), addTarget.getImei(),
                                    addTarget.getName(), true, addTarget.getStrTech(),
                                    addTarget.getStrBand(), addTarget.getStrChannel(), addTarget.isbRedir());
                            ProxyApplication.getDaoSession().getTargetUserDao().insert(targetUser);
                            sendTargetList(context, strCurTech);
                        }
                    }, targetDataStruct.getImsi(), targetDataStruct.getImei());
                    dialogAddTarget.show();
                }
                super.recordOnItemLongClick(parent, view, position, id, "User Item Long Click Event " + targetDataStruct.getImsi());
            }
        });
        TargetListView.setOnItemClickListener(new RecordOnItemClick() {
            @Override
            public void recordOnItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                          long arg3, String strMsg) {
                TargetDataStruct targetDataStruct = adapterScanner.getItem(arg2);
                if (targetDataStruct.getAuthState() == 1) {
                    Intent intent = new Intent(context, AttachInfoActivity.class);
                    intent.putExtra("imsi",targetDataStruct.getImsi());
                    intent.putExtra("imei", targetDataStruct.getImei());
                    ((BaseActivity)context).startActivityWithAnimation(intent);
                }
                super.recordOnItemClick(arg0, arg1, arg2, arg3, "User Item Click Event " + targetDataStruct.getImsi());
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

    class MyTimer extends TimerTask implements Serializable {
        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = TIMER_SECOND;
            handler.sendMessage(message);
        }
    }

    private void switchBcastTimer(boolean bFlag) {
        if (bFlag) {//open
            Long starttime = SharePreferenceUtils.getInstance(context).getLong("status_notif_starttime" +
                            ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() +
                            ((ProxyApplication)context.getApplicationContext()).getiTcpPort(),
                    System.currentTimeMillis());
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
                case BCAST_FAILURE:
                    CustomToast.showToast(context, "Bcast Start Failure");
                    break;
                case BCAST_END_TIMEOUT:
                    CustomToast.showToast(context, "Bcast Start Timeout");
                    break;
                case TIMER_SECOND://小区计时
                    iBcastTimer++;
                    ((TextView)contentView.findViewById(R.id.scanner_timemeter)).setText(secToTime(iBcastTimer));
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    public void onPause() {
        Logs.d(TAG,"onPause***************");
        saveData();
        super.onPause();
    }

    @Override
    public void onStop() {
        Logs.d(TAG,"onStop***************");
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Logs.d(TAG,"onDestroy***************");
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
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void TargetAttach(TargetAttach ta) {
        Logs.d(TAG,"接收消息内容***************");
        TargetDataStruct targetDataStruct = new TargetDataStruct();
        targetDataStruct.setImsi(ta.getImsi());
        targetDataStruct.setImei(ta.getImei());
        targetDataStruct.setAuthState(1);
        targetDataStruct.setbPositionStatus(true);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
        targetDataStruct.setStrAttachtime(formatter.format(new Date()));
        adapterScanner.AttachTarget(targetDataStruct);
    }


}
