package com.bravo.FemtoController;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.log.Local_Fragment;
import com.bravo.log.Remote_Fragment;
import com.bravo.socket_service.CommunicationService;
import com.bravo.socket_service.EventBusMsgConstant;
import com.bravo.utils.FileUtils;
import com.bravo.utils.SharePreferenceUtils;
import com.bravo.xml.HandleRecvXmlMsg;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lenovo on 2016/12/20.
 */

public class LoginActivity extends BaseActivity {

    private EditText account, password;
    private ImageView clearAccount, clearPassword;
//    private BlueToothReceiver receiver;

    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 2;

    private Button btnLogin;
    @Override
    protected void initView() {
        setContentView(R.layout.activity_login);
        //login btn
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                StartFemtoActivity();
                super.recordOnClick(v, "Login Event Account=" + account.getText().toString() +
                        ",Password=" + password.getText().toString());
            }
        });

        account = (EditText) findViewById(R.id.et_account);
        account.setText(SharePreferenceUtils.getInstance(mContext).getString("account", ""));
        account.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    clearAccount.setVisibility(View.VISIBLE);
                } else {
                    clearAccount.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password = (EditText) findViewById(R.id.et_password);
        password.setText(SharePreferenceUtils.getInstance(mContext).getString("password", ""));
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    clearPassword.setVisibility(View.VISIBLE);
                } else {
                    clearPassword.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        clearAccount = (ImageView) findViewById(R.id.iv_account_clear);
        clearAccount.setOnClickListener(new RecordOnClick(){
            @Override
            public void recordOnClick(View v, String strMsg) {
                account.setText("");
                super.recordOnClick(v, "Clear Account Event");
            }
        });
        clearPassword = (ImageView) findViewById(R.id.iv_password_clear);
        clearPassword.setOnClickListener(new RecordOnClick(){
            @Override
            public void recordOnClick(View v, String strMsg) {
                password.setText("");
                super.recordOnClick(v, "Clear Password Event");
            }
        });

        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
            TextView textView = (TextView) findViewById(R.id.version);
            textView.setText(pi.versionName + "." + pi.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if(Build.VERSION.SDK_INT >= 23){
            requestPermission();
        }else{
            //创建SDCard文件目录
            new FileUtils(this).initPath();
        }

//        findViewById(R.id.layout_version).setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                final DialogSuperUser dialogSuperUser = new DialogSuperUser(mContext, R.style.dialog_style);
//                dialogSuperUser.show();
//                return false;
//            }
//        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Intent intent = new Intent(this,CommunicationService.class);
        startService(intent);
        Intent intent1 = new Intent(this,HandleRecvXmlMsg.class);
        startService(intent1);
    }

    private void requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            }
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
            }
        }
        //创建SDCard文件目录
        new FileUtils(this).initPath();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_REQUEST_CODE: {
                //创建SDCard文件目录
                new FileUtils(this).initPath();
                /*// If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }*/
                return;
            }
        }
    }

    private void onLogClicked() {
        Intent intent = new Intent(mContext, RevealAnimationActivity.class);
        ArrayList<String> menuList = new ArrayList<String>();
        menuList.add("Remote");
        menuList.add("Local");
        intent.putStringArrayListExtra(RevealAnimationActivity.MENU_LIST, menuList);

        ArrayList<Integer> iconsResId = new ArrayList<Integer>();
        iconsResId.add(R.drawable.icon_remote_selector);
        iconsResId.add(R.drawable.icon_local_selector);
        intent.putExtra(RevealAnimationActivity.ICON_RES_LIST, iconsResId);

        ArrayList<RevealAnimationBaseFragment> fragments = new ArrayList<RevealAnimationBaseFragment>();
        fragments.add(new Remote_Fragment());
        fragments.add(new Local_Fragment());
        intent.putExtra(RevealAnimationActivity.FRAGMENTS, (Serializable) fragments);

        intent.putExtra(RevealAnimationActivity.TITLE, "Log");
        startActivityWithAnimation(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(EventBusMsgConstant.UNREGISTE_ALL_SOCKET);
        EventBus.getDefault().post(EventBusMsgConstant.STOP_SERVICE);
        SharePreferenceUtils.getInstance(mContext).setString("account", account.getText().toString());
        SharePreferenceUtils.getInstance(mContext).setString("password", password.getText().toString());
//        Intent intent = new Intent(this, tukTukService.class);
//        stopService(intent);
    }



    private void StartFemtoActivity() {
        String check = "bravo";
        //if ((account.getText().toString().trim().equals(check) && password.getText().toString().trim().equals(check))){
            Intent intent = new Intent();
            intent.setClassName("com.bravo.FemtoController", "com.bravo.FemtoController.FunActivity");
            startActivityWithAnimation(intent);
       /* } else if (account.getText().toString().trim().equals(check) && password.getText().toString().trim().equals("Test")) {
            Intent intent = new Intent();
            intent.setClassName("com.bravo.FemtoController", "com.bravo.FemtoController.FemtoListActivity");
            startActivityWithAnimation(intent);
        } else {
            CustomToast.showToast(this, "account or password  incorrect");
        }*/
        SharePreferenceUtils.getInstance(mContext).setString("account", account.getText().toString());
        SharePreferenceUtils.getInstance(mContext).setString("password", password.getText().toString());
    }
}
