package com.bravo.FemtoController;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.config.Fragment_SystemConfig;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.log.Local_Fragment;
import com.bravo.log.Remote_Fragment;
import com.bravo.utils.FileUtils;
import com.bravo.utils.Logs;
import com.bravo.utils.SharePreferenceUtils;
import com.bravo.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/12/20.
 */

public class LoginActivity extends BaseActivity {

    private EditText account, password;
    private ImageView clearAccount, clearPassword;
//    private BlueToothReceiver receiver;


    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 2;
    private static final int MY_PERMISSIONS_REQUEST_CODE = 1;
    String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    // 声明一个集合，在后面的代码中用来存储用户拒绝授权的权
    List<String> mPermissionList = new ArrayList<>();

    private Button btnLogin;

    @Override
    protected void initView() {
        Logs.d(TAG,"initView");
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
            if (Utils.isDebugVersion()) {
                textView.setText(pi.versionName + "." + pi.versionCode + "_D");
            } else {
                textView.setText(pi.versionName + "." + pi.versionCode);
            }
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
        Logs.d(TAG,"initData");

        SharedPreferences sp = mContext.getSharedPreferences(Fragment_SystemConfig.TABLE_NAME, MODE_PRIVATE);
        Logs.setLEVEL(sp.getInt(Fragment_SystemConfig.tn_LogLevel,Logs.getLEVEL()));
    }

    private void requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Logs.d(TAG,"申请各种权限");
            mPermissionList.clear();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (!mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了
                String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
                ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST_CODE);
            }
        }
        //创建SDCard文件目录
        //new FileUtils(this).initPath();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        //boolean hasPermissionDismiss = false;//有权限没有通过
        if (requestCode == MY_PERMISSIONS_REQUEST_CODE){
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    //判断是否勾选禁止后不再询问
                    boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                    if (showRequestPermission) {
                        Logs.d(TAG,"权限未申请");
                        requestPermission();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
    public void onPause() {
        Logs.d(TAG,"onPause",true);
        //创建SDCard文件目录
        new FileUtils(this).initPath();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Logs.d(TAG,"onDestroy");
        super.onDestroy();
        SharePreferenceUtils.getInstance(mContext).setString("account", account.getText().toString());
        SharePreferenceUtils.getInstance(mContext).setString("password", password.getText().toString());
//        Intent intent = new Intent(this, tukTukService.class);
//        stopService(intent);
    }



    private void StartFemtoActivity() {
        String check = "bravo";
        if (Utils.isDebugVersion()) {
            Intent intent = new Intent();
            intent.setClassName("com.bravo.FemtoController", "com.bravo.FemtoController.FunActivity");
            startActivityWithAnimation(intent);
            finish();
        } else {
            if ((account.getText().toString().trim().equals(check) && password.getText().toString().trim().equals(check))) {
                Intent intent = new Intent();
                intent.setClassName("com.bravo.FemtoController", "com.bravo.FemtoController.FunActivity");
                startActivityWithAnimation(intent);
                finish();
            } /*else if (account.getText().toString().trim().equals(check) && password.getText().toString().trim().equals("Test")) {
            Intent intent = new Intent();
            intent.setClassName("com.bravo.FemtoController", "com.bravo.FemtoController.FemtoListActivity");
            startActivityWithAnimation(intent);
        } */ else {
                //CustomToast.showToast(this, "account or password  incorrect");
                CustomToast.showToast(this, "用户名或密码错误");
            }
            SharePreferenceUtils.getInstance(mContext).setString("account", account.getText().toString());
            SharePreferenceUtils.getInstance(mContext).setString("password", password.getText().toString());
        }
    }
}
