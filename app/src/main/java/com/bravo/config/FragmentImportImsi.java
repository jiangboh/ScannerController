package com.bravo.config;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.FileSlelect.SelectTxtFileActivity;
import com.bravo.R;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.database.BlackWhiteImsi;
import com.bravo.database.BlackWhiteImsiDao;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.utils.Logs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.bravo.R.drawable.btn_import_selector;

/**
 * Created by admin on 2019-6-20.
 */

public class FragmentImportImsi extends RevealAnimationBaseFragment {
    private final String TAG = "FragmentImportImsi";
    public static boolean isOpen = false;

    private final int IMSI = 0;
    private final int IMEI = 1;
    private final int UserType = 2;
    private final int StartRb = 3;
    private final int StopRb = 4;
    private final int NAME = 5;

    public static final String TABLE_NAME = "ImportImsiInfo";
    public static final String ImportPath = "ImportPath";
    private String SelectPath = "";

    private Button bAddFile;
    private TextView tFilePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logs.d(TAG,"onCreate",true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_import_imsi);
    }

    @Override
    public void onResume() {
        Logs.d(TAG,"onResume",true);
        super.onResume();
        loadData();

        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity)context).getSettingBtn().setImageResource(btn_import_selector);
        ((RevealAnimationActivity)context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                ImportTxtFile2Db();
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
        bAddFile = (Button) contentView.findViewById(R.id.btnSelect);
        bAddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,SelectTxtFileActivity.class);
                startActivityForResult(intent,1);
            }
        });

        tFilePath = (TextView) contentView.findViewById(R.id.filePath);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==1 || resultCode==2){
            //当请求码是1&&返回码是2进行下面操作
            loadData();
            Logs.d(TAG,"当前路径：" + this.SelectPath);
        }
    }

    private void loadData() {
        SharedPreferences sp = context.getSharedPreferences(this.TABLE_NAME, MODE_PRIVATE);
        SelectPath = sp.getString(this.ImportPath,"");
        Log.v("初始值：", SelectPath);
        tFilePath.setText(this.SelectPath);
    }

    @Override
    public void onPause() {
        Logs.d(TAG,"onPause",true);
        //saveData();
        isOpen = false;
        super.onPause();
    }

    @Override
    public void onStop() {
        Logs.d(TAG,"onStop",true);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Logs.d(TAG,"onDestroy",true);
        super.onDestroy();

    }

    public void ImportTxtFile2Db() {
        BufferedReader bre = null;
        String str = null;
        boolean isError = false;
        ArrayList<BlackWhiteImsi> imsiInfoList = new ArrayList<>();
        try {
            String file =  SelectPath ; //"/User/user.txt";
            bre = new BufferedReader(new FileReader(file));//此时获取到的bre就是整个文件的缓存流
            int line = 0;

            while ((str = bre.readLine()) != null) { // 判断最后一行不存在，为空结束循环
                line ++;
                //Log.e(TAG, "ImportTxtFile2Db: a------------" + str);
                if (str.trim().length() == 0) //去掉空行
                    continue;

                String[] arr = str.split(",");
                if (arr.length != 6) {
                    isError = true;
                    CustomToast.showToast(context, "第" + line + "行格式错误！");
                    break;
                } else {
                    BlackWhiteImsi imsiInfo = new BlackWhiteImsi();
                    imsiInfo.setImsi(arr[IMSI]);
                    imsiInfo.setImei(arr[IMEI]);
                    if ("白名单".equals(arr[UserType])) {
                        imsiInfo.setType(BlackWhiteImsi.WHITE);
                    } else if ("黑名单".equals(arr[UserType])) {
                        imsiInfo.setType(BlackWhiteImsi.BLACK);
                    } else {
                        isError = true;
                        CustomToast.showToast(context, "第" + line + "行,用户类型错误!");
                        break;
                    }
                    if (arr[StopRb].trim().length()<=0) {
                        imsiInfo.setStopRb(0);
                    } else {
                        imsiInfo.setStopRb(Integer.parseInt(arr[StopRb]));
                    }
                    if (arr[StartRb].trim().length()<=0) {
                        imsiInfo.setStartRb(0);
                    } else {
                        imsiInfo.setStartRb(Integer.parseInt(arr[StartRb]));
                    }
                    imsiInfo.setName(arr[NAME]);

                    imsiInfoList.add(imsiInfo);
                }
            }
        } catch (Exception e) {
            isError = true;
            CustomToast.showToast(context, "读取导入文件出错！");
        }

        if (!isError) {
            saveData(imsiInfoList);
            CustomToast.showToast(context, "批量导入黑/白名单完成!");
        }

        if (bre != null) {
            try {
                bre.close();
            } catch (IOException e) {

            }
        }
    }

    private boolean saveData(ArrayList<BlackWhiteImsi> imsiInfoList) {
        for (BlackWhiteImsi imsiInfo : imsiInfoList) {
            BlackWhiteImsi FindImsi = null;
            if (imsiInfo.getImsi().trim().length() > 0) {
                FindImsi = ProxyApplication.getDaoSession().getBlackWhiteImsiDao().
                        queryBuilder().where(BlackWhiteImsiDao.Properties.Type.eq(imsiInfo.getType()),
                        BlackWhiteImsiDao.Properties.Imsi.eq(imsiInfo.getImsi())).
                        build().unique();
                if (FindImsi != null) { //数据存在更新
                    imsiInfo.setId(FindImsi.getId());
                    ProxyApplication.getDaoSession().getBlackWhiteImsiDao().update(imsiInfo);
                    //CustomToast.showToast(context, "该IMSI(" + imsiInfo.getImsi() + ")存在,已被更新");
                } else {//不存在保存
                    ProxyApplication.getDaoSession().getBlackWhiteImsiDao().insert(imsiInfo);
                }
            } else {
                if (imsiInfo.getImei().trim().length() > 0) {
                    FindImsi = ProxyApplication.getDaoSession().getBlackWhiteImsiDao().
                            queryBuilder().where(BlackWhiteImsiDao.Properties.Type.eq(imsiInfo.getType()),
                            BlackWhiteImsiDao.Properties.Imei.eq(imsiInfo.getImei())).
                            build().unique();
                    if (FindImsi != null) { //数据存在更新
                        imsiInfo.setId(FindImsi.getId());
                        ProxyApplication.getDaoSession().getBlackWhiteImsiDao().update(imsiInfo);
                        //CustomToast.showToast(context, "该IMEI(" + imsiInfo.getImei() + ")存在,已被更新");
                    } else {//不存在保存
                        ProxyApplication.getDaoSession().getBlackWhiteImsiDao().insert(imsiInfo);
                    }
                }
            }
        }
        return true;
    }
}
