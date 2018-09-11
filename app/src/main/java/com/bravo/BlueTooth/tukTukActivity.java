package com.bravo.BlueTooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.adapters.AdapterTuktukList;
import com.bravo.custom_view.CustomProgressDialog;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.OneBtnHintDialog;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.custom_view.RecordOnItemClick;
import com.bravo.listview.UserDefineListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class tukTukActivity extends AppCompatActivity {
    private final String TAG = "tukTukActivity";
    //适配器与蓝牙管理器的成员变量。
    private BluetoothAdapter mBluetoothAdapter;
    private ImageView iv_rotate;
    private ImageView rightBtn;
    public UserDefineListView userDefineListView;
    private AdapterTuktukList adapterTuktukList;
    private CustomProgressDialog proDialog;
    private boolean bDialogState = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuk_tuk);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initView();
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_GET_DEVICE_REQUEST));
    }

    protected void initView () {
        ((TextView) findViewById(R.id.tv_activity_title)).setText("TT List");
        findViewById(R.id.iv_activity_back).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                onBackPressed();
                super.recordOnClick(v, "Back Event " + TAG);
            }
        });

        rightBtn = (ImageView) findViewById(R.id.iv_activity_right);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setImageResource(R.drawable.btn_refresh_selector);
        rightBtn.setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                if (!mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.startDiscovery();
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0); //强制隐藏键盘
                super.recordOnClick(v, "Refresh TukTuk List Event");
            }
        });
        //scan动画
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.image_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        iv_rotate = (ImageView) findViewById(R.id.rotate_image);
        if(operatingAnim != null && iv_rotate != null){
            iv_rotate.startAnimation(operatingAnim);
        } else {
            findViewById(R.id.rotate_view).setVisibility(View.GONE);
        }
        //list
        userDefineListView = (UserDefineListView) findViewById(R.id.pixcell_list);
        adapterTuktukList = new AdapterTuktukList(this);
        userDefineListView.setAdapter(adapterTuktukList);
        userDefineListView.setOnItemClickListener(new RecordOnItemClick() {
            @Override
            public void recordOnItemClick(AdapterView<?> parent, View view, int position, long id, String strMsg) {
                if (userDefineListView.canClick()) {
                    BluetoothDevice bluetoothDevice = (BluetoothDevice)adapterTuktukList.getItem(position);
                    EventBusTukTukMsg eventBusTukTukMsg = new EventBusTukTukMsg(EventBusTukTukMsg.BT_CONNECT_REQUEST, bluetoothDevice);
                    EventBus.getDefault().post(eventBusTukTukMsg);
                    Connect_Dialog(bluetoothDevice.getName());
                    super.recordOnItemClick(parent, view, position, id, "Connect Tuktuk Event Name=" + bluetoothDevice.getName() + ",MAC=" + bluetoothDevice.getAddress());
                }
            }
        });

        //init dialog
        proDialog = new CustomProgressDialog(this,R.style.dialog_style);
        proDialog.setCancelable(false);
    }

    protected void initData (){
        if (checkIfSupportBle()) {//支持蓝牙设备
            mBluetoothAdapter = getAdapter();
            if (enableBluetooth()) {//修改到线程打开
                if (mBluetoothAdapter.isDiscovering()) {
                    EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_START_SCAN));
                }
            }
        } else {//不支持蓝牙设备
            OneBtnHintDialog hintDialog = new OneBtnHintDialog(this, R.style.dialog_style);
            hintDialog.setCanceledOnTouchOutside(false);
            hintDialog.show();
            hintDialog.setBtnContent("OK");
            hintDialog.setTitle("Warning");
            hintDialog.setContent("Phone nonsupport Bluetooth");
            hintDialog.setListener(new OneBtnHintDialog.BtnClickListener() {
                @Override
                public void onBtnClick(View v) {
                    finish();
                }
            });
        }
//        if (blueToothReceiver== null) {
//            blueToothReceiver = new BlueToothReceiver();
//            IntentFilter filter = new IntentFilter();
//            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
//            filter.addAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            ACTION_DISCOVERY_STARTED
//                    ACTION_DISCOVERY_FINISHED
//            ACTION_FOUND
//            registerReceiver(blueToothReceiver, filter);
//        }
    }

//    private static final long SCAN_PERIOD = 10000;
//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
//    private void scanLeDevice(final boolean enable) {
//        if (enable) {
//            // 经过预定扫描期后停止扫描
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Logs.d(TAG, "timeout stopLeScan");
//                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                }
//            }, SCAN_PERIOD);
//            Logs.d(TAG, "startLeScan");
//            mBluetoothAdapter.startLeScan(new UUID[]{UUID.fromString("05845767-7705-4449-4944-abcdef000001")}, mLeScanCallback);
//        } else {
//            Logs.d(TAG, "stopLeScan");
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//        }
//    }
    // Device scan callback.
//    private BluetoothAdapter.LeScanCallback mLeScanCallback =
//            new BluetoothAdapter.LeScanCallback() {
//                @Override
//                public void onLeScan(final BluetoothDevice device, int rssi,
//                                     byte[] scanRecord) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            EventBus.getDefault().post(device);
//                        }
//                    });
//                }
//            };
    //检查设备是否支持BLE功能。
    private boolean checkIfSupportBle(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    //如果设备支持BLE，那么就可以获取蓝牙适配器。
    private BluetoothAdapter getAdapter(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBluetoothAdapter = ((BluetoothManager) getSystemService(this.BLUETOOTH_SERVICE)).getAdapter();
        } else {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        return mBluetoothAdapter;
    }

    //获取完适配器后，需要检测是否已经打开蓝牙功能，如果没有，就需要开启。
//开启蓝牙功能需要一小段时间，具体涉及的线程操作或同步对象不在此讨论，视实际情况按需编写。

    private static final int REQUEST_ENABLE_BT = 2;
    private boolean enableBluetooth() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return false;
        }
        return true;
    }

    //此方法用于获取在手机中已经获取并绑定了的设备
//    private void getBoundDevices(){
//        Set<BluetoothDevice> boundDevices = mBluetoothAdapter.getBondedDevices();
//        for(BluetoothDevice device : boundDevices){
//            //对device进行其他操作，比如连接等。
//            Logs.d(TAG, "device name =" + device.getName());
//        }
//    }
    private void Connect_Dialog(String btName) {
        if (!proDialog.isShowing()) {
            proDialog.show();
            proDialog.setTitle("Connect " + btName);
            proDialog.setContent("Please wait...");
            bDialogState = true;
            final int TIMEOUT = 30;
            Thread thread = new Thread() {
                public void run() {
                    try {
                        int iCount = 0;
                        while (bDialogState && iCount < TIMEOUT) {
                            iCount++;
                            sleep(300);
                        }
                        if (iCount >= TIMEOUT) {
                            //连接超时
                            EventBus.getDefault().post(new EventBusTukTukMsg(EventBusTukTukMsg.BT_CONNECT_FAILURE));
                        }
                        proDialog.dismiss();
                    } catch (InterruptedException e) {
                        // TODO 自动生成的 catch 块
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        iv_rotate.clearAnimation();
        EventBus.getDefault().unregister(this);
        if (proDialog.isShowing()) {
            proDialog.dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void blueToothReceiver(EventBusTukTukMsg eventBusTukTukMsg) {
        switch (eventBusTukTukMsg.getAction()) {
            case EventBusTukTukMsg.BT_GET_DEVICE_RESPONSE:
                if (eventBusTukTukMsg.getBluetoothDevice() != null) {
                    adapterTuktukList.setCurMacAdderss(eventBusTukTukMsg.getBluetoothDevice().getAddress());
                }
                break;
            case EventBusTukTukMsg.BT_START_SCAN:
//                rightBtn.setEnabled(false);
                findViewById(R.id.rotate_view).setVisibility(View.VISIBLE);
                adapterTuktukList.removeAll();
                break;
            case EventBusTukTukMsg.BT_STOP_SCAN:
                findViewById(R.id.rotate_view).setVisibility(View.GONE);
//                rightBtn.setEnabled(true);
                bDialogState = false;
                break;
            case EventBusTukTukMsg.BT_CONNECT_SUCCESS:
                bDialogState = false;
                finish();
                Intent  intent = new Intent();
                intent.setClassName("com.bravo.FemtoController", "com.bravo.BlueTooth.TTConfigActivity");
                startActivity(intent);
                break;
            case EventBusTukTukMsg.BT_CONNECT_FAILURE:
                CustomToast.showToast(this, "Connect TUK TUK Failure");
                break;
            case EventBusTukTukMsg.BT_SCAN_RESULT:
                adapterTuktukList.addTuktuk(eventBusTukTukMsg.getBluetoothDevice());
                break;
            case EventBusTukTukMsg.BT_DISCONNECT_NOTIF:
                bDialogState = true;
                OneBtnHintDialog hintDialog = new OneBtnHintDialog(this, R.style.dialog_style);
                hintDialog.setCanceledOnTouchOutside(false);
                hintDialog.show();
                hintDialog.setBtnContent("OK");
                hintDialog.setTitle("Warning");
                hintDialog.setContent("TUK TUK Disconnected");
                hintDialog.setListener(new OneBtnHintDialog.BtnClickListener() {
                    @Override
                    public void onBtnClick(View v) {
                        adapterTuktukList.setCurMacAdderss(null);
                    }
                });
                break;
            default:
                break;
        }
    }
}