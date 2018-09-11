package com.bravo.FemtoController;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.bravo.R;
import com.bravo.custom_view.OneBtnHintDialog;
import com.bravo.custom_view.TwoBtnHintDialog;
import com.bravo.database.FemtoList;
import com.bravo.database.FemtoListDao;
import com.bravo.socket_service.EventBusMsgConstant;
import com.bravo.socket_service.EventBusMsgSendTCPMsg;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lenovo on 2017/5/23.
 */

public class TranslucentActivity extends BaseActivity {

    public static final int DIALOG_TYPE_ONE = 1;
    public static final int DIALOG_TYPE_DISCONNECT = 2;
    public static final int DIALOG_TYPE_WIFI_CLOSE = 3;
    private int dialog_type = DIALOG_TYPE_ONE;

    @Override
    protected void initView() {
        dialog_type = getIntent().getIntExtra("dialogType", DIALOG_TYPE_ONE);
        String title = getIntent().getStringExtra("title");
        String msg = getIntent().getStringExtra("message");
        switch (dialog_type) {
            case  DIALOG_TYPE_ONE:
                OneBtnHintDialog dialog = new OneBtnHintDialog(this,R.style.dialog_style);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        TranslucentActivity.this.finish();
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
                dialog.setTitle(title);
                dialog.setContent(msg);
                break;
            case DIALOG_TYPE_DISCONNECT:
                final String ipAddress = getIntent().getStringExtra("ipAddress");
                final int port = getIntent().getIntExtra("port", 8021);
                TwoBtnHintDialog twoBtnHintDialog = new TwoBtnHintDialog(this,R.style.dialog_style);
                twoBtnHintDialog.setOnBtnClickListener(new TwoBtnHintDialog.OnBtnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.two_btn_dialog_left_btn:
                                ((ProxyApplication) getApplicationContext()).setCurSocket(null);
                                Intent intent = new Intent(getApplicationContext(), FunActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                break;
                            case R.id.two_btn_dialog_right_btn:
                                FemtoList femtoList = ProxyApplication.getDaoSession().getFemtoListDao().queryBuilder().where(FemtoListDao.Properties.Mac.eq(((ProxyApplication) getApplicationContext()).getCurMacAddress()),
                                        FemtoListDao.Properties.UdpPort.eq(((ProxyApplication) getApplicationContext()).getiUdpPort())).build().unique();
                                EventBusMsgSendTCPMsg eventBusMsgSendTCPMsg;
                                if (femtoList != null) {
                                    eventBusMsgSendTCPMsg = new EventBusMsgSendTCPMsg(ipAddress, femtoList.getPort(), EventBusMsgConstant.TCP_RECONNECT_REQUEST);
                                } else {
                                    eventBusMsgSendTCPMsg = new EventBusMsgSendTCPMsg(ipAddress, port, EventBusMsgConstant.TCP_RECONNECT_REQUEST);
                                }
                                EventBus.getDefault().post(eventBusMsgSendTCPMsg);
                                break;
                        }
                    }
                });
                twoBtnHintDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        TranslucentActivity.this.finish();
                    }
                });
                twoBtnHintDialog.setCancelable(false);
                twoBtnHintDialog.show();
                twoBtnHintDialog.setTitle(title);
                twoBtnHintDialog.setContent(msg);
                twoBtnHintDialog.setLeftBtnContent("Exit");
                twoBtnHintDialog.setRightBtnContent("Reconnect");
                break;
            case DIALOG_TYPE_WIFI_CLOSE:
                twoBtnHintDialog = new TwoBtnHintDialog(this,R.style.dialog_style);
                twoBtnHintDialog.setOnBtnClickListener(new TwoBtnHintDialog.OnBtnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ProxyApplication)getApplicationContext()).setCurSocket(null);
                        Intent intent = new Intent(getApplicationContext(), FunActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        switch (v.getId()){
                            case R.id.two_btn_dialog_right_btn:
                                PackageManager packageManager = getPackageManager();
                                intent = packageManager.getLaunchIntentForPackage("com.android.settings");
                                startActivity(intent);
//                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                break;
                        }
                    }
                });
                twoBtnHintDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        TranslucentActivity.this.finish();

                    }
                });
                twoBtnHintDialog.setCancelable(false);
                twoBtnHintDialog.show();
                twoBtnHintDialog.setTitle(title);
                twoBtnHintDialog.setContent(msg);
                twoBtnHintDialog.setLeftBtnContent("Exit");
                twoBtnHintDialog.setRightBtnContent("Setting");
                break;
            default:
                break;
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }
}
