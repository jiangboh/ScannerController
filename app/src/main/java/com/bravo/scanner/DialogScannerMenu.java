package com.bravo.scanner;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.R;
import com.bravo.adapters.SimpleBaseAdapter;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.RecordOnItemClick;
import com.bravo.custom_view.RecordOnItemLongClick;
import com.bravo.database.BlackWhiteImsi;
import com.bravo.database.BlackWhiteImsiDao;
import com.bravo.dialog.DialogAddImsi;
import com.bravo.utils.Logs;

import java.util.List;

/**
 * Created by admin on 2018-12-19.
 */

public class DialogScannerMenu extends Dialog {
    private final String TAG = "DialogScannerMenu";
    private Context context;

    private TextView titleName;
    private ListView listview;
    private MyListArrayAdapter adapter;

    private BlackWhiteImsi sInfo_imsi;

    public DialogScannerMenu(@NonNull Context context, BlackWhiteImsi imsiInfo) {
        super(context);
        this.context = context;
        this.sInfo_imsi = imsiInfo;
    }

    private class MyListArrayAdapter extends SimpleBaseAdapter<String> {
        public List<String> data;
        private Context context;

        private class ViewHolder {
            private TextView textid;
        }

        public MyListArrayAdapter(Context context) {
            //this.context = context;
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.my_simple_list_item, null);
                holder.textid = ((TextView) convertView.findViewById(R.id.str_text));
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textid.setText(lists.get(position));

            //设置item的点击效果
            convertView.setBackgroundResource(R.drawable.dialog_btn_bg_selector);

            return convertView;
        }
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

        listview = (ListView) findViewById(R.id.menulist);
        adapter = new MyListArrayAdapter(context);
        listview.setAdapter(adapter);
        adapter.addData("添加到黑名单");
        adapter.addData("添加到白名单");

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
                    String str = (String)adapter.getItem(arg2);
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
                        sInfo_imsi.setStartRb(3);
                        sInfo_imsi.setStopRb(5);
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
