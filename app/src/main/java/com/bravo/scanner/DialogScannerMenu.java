package com.bravo.scanner;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.R;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.RecordOnItemClick;
import com.bravo.custom_view.RecordOnItemLongClick;
import com.bravo.database.BlackWhiteImsi;
import com.bravo.database.BlackWhiteImsiDao;
import com.bravo.dialog.DialogAddImsi;
import com.bravo.utils.Logs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018-12-19.
 */

public class DialogScannerMenu extends Dialog {
    private final String TAG = "DialogScannerMenu";
    private Context context;

    private TextView titleName;
    private ListView listview;
    private ArrayAdapter<String> adapter;
    private List<String> data;

    private BlackWhiteImsi sInfo_imsi;

    public DialogScannerMenu(@NonNull Context context, BlackWhiteImsi imsiInfo) {
        super(context);
        this.context = context;
        this.sInfo_imsi = imsiInfo;
    }

    protected void onCreate(final Bundle savedInstanceState) {
        Logs.d(TAG,"onCreate",true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_scanner_menu);

        titleName = (TextView)findViewById(R.id.title);
        if (sInfo_imsi.getImsi().length() > 0) {
            titleName.setText("IMSI:" + sInfo_imsi.getImsi());
        } else if (sInfo_imsi.getImei().length() > 0) {
            titleName.setText("IMEI:" + sInfo_imsi.getImei());
        } else if (sInfo_imsi.getTmsi().length() > 0) {
            titleName.setText("TMSI:" + sInfo_imsi.getTmsi());
        } else {
            CustomToast.showToast(context,"没有要添加的IMSI或IMEI或TMSI");
            cancel();
        }

        data=new ArrayList<String>();
        data.add(0,"添加到黑名单");
        data.add(1,"添加到白名单");
        listview = (ListView) findViewById(R.id.menulist);
        //设定列表项的选择模式为单选
        adapter=new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, data);
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listview.setAdapter(adapter);

        try {
            listview.setOnItemLongClickListener(new RecordOnItemLongClick() {
                @Override
                public void recordOnItemLongClick(AdapterView<?> parent, View view, final int position, long id, String strMsg) {
                    return;
                }
            });
            listview.setOnItemClickListener(new RecordOnItemClick() {
                @Override
                public void recordOnItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3, String strMsg) {
                    String str = adapter.getItem(arg2);
                    Logs.d(TAG,"点击：" + str,true);
                    if (str.equals("添加到白名单")) {
                        DialogAddImsi dialog = new DialogAddImsi(context,false,sInfo_imsi);
                        dialog.setSaveListener(new DialogAddImsi.OnSaveData2Database() {
                            @Override
                            public boolean onSave(BlackWhiteImsi imsiInfo) {
                                if (addImsiToWhite(imsiInfo)) {
                                    CustomToast.showToast(context,"添加到白名单列表成功\n请到白名单配置页下发到设备");
                                    return true;
                                }
                                return false;
                            }
                        });
                        //这句话，就是决定上面的那个黑框，也就是dialog的title。
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.show();
                        cancel();
                    } else if (str.equals("添加到黑名单")) {
                        DialogAddImsi dialog = new DialogAddImsi(context,true,sInfo_imsi);
                        dialog.setSaveListener(new DialogAddImsi.OnSaveData2Database() {
                            @Override
                            public boolean onSave(BlackWhiteImsi imsiInfo) {
                                if (addImsiToBlack(imsiInfo)) {
                                    CustomToast.showToast(context,"添加到黑名单列表成功\n请到黑名单配置页下发到设备");
                                    return true;
                                }
                                return false;
                            }
                        });
                        //这句话，就是决定上面的那个黑框，也就是dialog的title。
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.show();
                        cancel();
                    } else {
                        Log.e(TAG,"菜单选择错误。");
                    }
                }
            });
        }catch (Exception e) {
            Logs.e(TAG,"点击界面出错：" + e.getMessage(),true);
        }
    }

    private boolean addImsiToBlack(BlackWhiteImsi imsiInfo) {
        BlackWhiteImsi FindImsi = null;
        if (imsiInfo.getImsi().trim().length() > 0) {
            FindImsi = ProxyApplication.getDaoSession().getBlackWhiteImsiDao().
                    queryBuilder().where(BlackWhiteImsiDao.Properties.Type.eq(BlackWhiteImsi.BLACK),
                    BlackWhiteImsiDao.Properties.Imsi.eq(imsiInfo.getImsi())).
                    build().unique();
            if (FindImsi != null) { //数据存在更新
                imsiInfo.setId(FindImsi.getId());
                ProxyApplication.getDaoSession().getBlackWhiteImsiDao().update(imsiInfo);
                CustomToast.showToast(context, "该IMSI(" + imsiInfo.getImsi() + ")存在,已被更新");
            } else {//不存在保存
                ProxyApplication.getDaoSession().getBlackWhiteImsiDao().insert(imsiInfo);
            }
        } else {
            if (imsiInfo.getImei().trim().length() > 0) {
                FindImsi = ProxyApplication.getDaoSession().getBlackWhiteImsiDao().
                        queryBuilder().where(BlackWhiteImsiDao.Properties.Type.eq(BlackWhiteImsi.BLACK),
                        BlackWhiteImsiDao.Properties.Imei.eq(imsiInfo.getImei())).
                        build().unique();
                if (FindImsi != null) { //数据存在更新
                    imsiInfo.setId(FindImsi.getId());
                    ProxyApplication.getDaoSession().getBlackWhiteImsiDao().update(imsiInfo);
                    CustomToast.showToast(context, "该IMEI(" + imsiInfo.getImei() + ")存在,已被更新");
                } else {//不存在保存
                    ProxyApplication.getDaoSession().getBlackWhiteImsiDao().insert(imsiInfo);
                }
            }
        }
        return true;
    }

    private boolean addImsiToWhite(BlackWhiteImsi imsiInfo) {
        BlackWhiteImsi FindImsi = null;
        if (imsiInfo.getImsi().trim().length() > 0) {
            FindImsi = ProxyApplication.getDaoSession().getBlackWhiteImsiDao().
                    queryBuilder().where(BlackWhiteImsiDao.Properties.Type.eq(BlackWhiteImsi.WHITE),
                    BlackWhiteImsiDao.Properties.Imsi.eq(imsiInfo.getImsi())).
                    build().unique();
            if (FindImsi != null) { //数据存在更新
                imsiInfo.setId(FindImsi.getId());
                ProxyApplication.getDaoSession().getBlackWhiteImsiDao().update(imsiInfo);
                CustomToast.showToast(context, "该IMSI(" + imsiInfo.getImsi() + ")存在,已被更新");
            } else {//不存在保存
                ProxyApplication.getDaoSession().getBlackWhiteImsiDao().insert(imsiInfo);
            }
        } else {
            if (imsiInfo.getImei().trim().length() > 0) {
                FindImsi = ProxyApplication.getDaoSession().getBlackWhiteImsiDao().
                        queryBuilder().where(BlackWhiteImsiDao.Properties.Type.eq(BlackWhiteImsi.WHITE),
                        BlackWhiteImsiDao.Properties.Imei.eq(imsiInfo.getImei())).
                        build().unique();
                if (FindImsi != null) { //数据存在更新
                    imsiInfo.setId(FindImsi.getId());
                    ProxyApplication.getDaoSession().getBlackWhiteImsiDao().update(imsiInfo);
                    CustomToast.showToast(context, "该IMEI(" + imsiInfo.getImei() + ")存在,已被更新");
                } else {//不存在保存
                    ProxyApplication.getDaoSession().getBlackWhiteImsiDao().insert(imsiInfo);
                }
            }
        }
        return true;
    }
}
