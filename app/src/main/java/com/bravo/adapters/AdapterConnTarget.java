package com.bravo.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.R;
import com.bravo.data_ben.TargetDataStruct;
import com.bravo.database.TargetUser;
import com.bravo.database.TargetUserDao;
import com.bravo.database.User;
import com.bravo.database.UserDao;
import com.bravo.femto.BcastCommonApi;
import com.bravo.parse_generate_xml.TargetDetach;
import com.bravo.utils.SharePreferenceUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack.liao on 2016/9/14.
 */
public class AdapterConnTarget extends BaseAdapter {
    private final static String TAG = "AdapterConnTarget";
    private final ArrayList<TargetDataStruct> targetDataStructs = new ArrayList<>();
    private Context mContext;
    //private int iCurPosition;
    private String strCurTech;
    private int iCurAuthTotal = 0;
    private RadioButton radioButton;
    private TextView tvTotal;
    public AdapterConnTarget(Context context, RadioButton radioButton) {
        this.radioButton = radioButton;
        this.mContext = context;
        //iCurPosition = -1;
    }
    public AdapterConnTarget(Context context, TextView txView) {
        this.tvTotal = txView;
        this.mContext = context;
        //iCurPosition = -1;
    }

    public void updateCurTech(String strCurTech) {
        this.strCurTech = strCurTech;
    }

    @Override
    public int getCount() {
        return targetDataStructs.size();
    }

    @Override
    public TargetDataStruct getItem(int position) {
        return targetDataStructs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<TargetDataStruct> getList() {
        return targetDataStructs;
    }

    public void updateGpsDistance(TargetDataStruct targetDataStruct, View view) {
        for (int i = 0; i < iCurAuthTotal; i++) {
            if (targetDataStruct.getImsi().equals(targetDataStructs.get(i).getImsi())) {
                targetDataStructs.get(i).setStrLatitude(targetDataStruct.getStrLatitude());
                targetDataStructs.get(i).setStrLongitude(targetDataStruct.getStrLongitude());
                targetDataStructs.get(i).setDistance(targetDataStruct.getDistance());
                targetDataStructs.get(i).setSignal(targetDataStruct.getSignal());
                notifyDataSetChanged();
                return;
            }
        }
        targetDataStructs.add(0, targetDataStruct);
        iCurAuthTotal++;
        for (int i = iCurAuthTotal; i < targetDataStructs.size(); i++) {
            if (targetDataStruct.getImsi().equals(targetDataStructs.get(i).getImsi())) {
                targetDataStructs.remove(i);
                return;
            }
        }
        updateTotal();
    }

    public void addTarget(TargetDataStruct targetDataStruct) {
        //Logs.d(TAG, "lmj->" + targetDataStructs.size() + ",iCurAuthTotal=" + iCurAuthTotal + ",targetDataStruct.getAuthState()=" + targetDataStruct.getAuthState());
        if (targetDataStruct.getAuthState() == 2) {
            targetDataStructs.add(iCurAuthTotal, targetDataStruct);
        } else {
            targetDataStructs.add(targetDataStruct);
        }
        updateTotal();
    }

    public void AttachTarget(TargetDataStruct targetDataStruct) {
        for (int i = 0; i < iCurAuthTotal; i++) {
            if (targetDataStruct.getImsi().equals(targetDataStructs.get(i).getImsi())) {
                targetDataStructs.get(i).setbPositionStatus(true);
                return;
            }
        }
        targetDataStructs.add(0, targetDataStruct);
        iCurAuthTotal++;
        for (int i = iCurAuthTotal; i < targetDataStructs.size(); i++) {
            if (targetDataStruct.getImsi().equals(targetDataStructs.get(i).getImsi())) {
                if (/*!TextUtils.isEmpty(targetDataStructs.get(i).getImei()) && */TextUtils.isEmpty(targetDataStruct.getImei())) {
                    targetDataStructs.get(0).setImei(targetDataStructs.get(i).getImei());
                }
                targetDataStructs.get(0).setStrConntime(targetDataStructs.get(i).getStrConntime());
                targetDataStructs.remove(i);
                i = targetDataStructs.size();
            }
        }
        updateTotal();
    }

    public void RepeatTarget(TargetDataStruct targetDataStruct) {
        for (int i = 0; i < targetDataStructs.size(); i++) {
            if (targetDataStruct.getImsi().equals(targetDataStructs.get(i).getImsi())) {
                targetDataStructs.get(i).setStrConntime(targetDataStruct.getStrConntime());
                targetDataStructs.get(i).setCount(targetDataStruct.getCount());
                notifyDataSetChanged();
                return;
            }
        }
        addTarget(targetDataStruct);
    }

    public void TargetDetach(TargetDataStruct targetDataStruct) {
        for (int i = 0; i < iCurAuthTotal; i++) {
            if (targetDataStruct.getImsi().equals(targetDataStructs.get(i).getImsi())) {
                targetDataStruct = targetDataStructs.get(i);
                targetDataStruct.setAuthState(2);
                targetDataStruct.setStrConntime(targetDataStructs.get(i).getStrConntime());
                targetDataStruct.setStrAttachtime(targetDataStructs.get(i).getStrAttachtime());
                targetDataStructs.remove(i);
                iCurAuthTotal--;
                addTarget(targetDataStruct);
                return;
            }
        }
    }

    private void updateTotal(){
        if (radioButton != null) {
            radioButton.setText("Uesr(" + targetDataStructs.size() + ") / Target(" + iCurAuthTotal + ")");
        } else if (tvTotal != null) {
            tvTotal.setText("Uesr(" + targetDataStructs.size() + ") / Target(" + iCurAuthTotal + ")");
        }
        notifyDataSetChanged();
    }

    public void removeTarget(int iPosition) {
        targetDataStructs.remove(iPosition);
        notifyDataSetChanged();
    }

    public void RemoveAll() {
        targetDataStructs.clear();
        iCurAuthTotal = 0;
        if (radioButton != null) {
            radioButton.setText("Uesr(0) / Target(0)");
        } else if (tvTotal != null) {
            tvTotal.setText("Uesr(0) / Target(0)");
        }
        notifyDataSetChanged();
    }

    public void checkAuthTarget() {
        for (int i = 0; i < iCurAuthTotal; i++) {
            if (!targetDataStructs.get(i).isbPositionStatus()) {
                TargetDataStruct target = new TargetDataStruct();
                target = targetDataStructs.get(i);
                target.setAuthState(2);
                target.setbPositionStatus(false);
                targetDataStructs.remove(i);
                iCurAuthTotal--;
                i--;
                addTarget(target);
                /////
                String Unique = SharePreferenceUtils.getInstance(mContext).getString("status_notif_unique" + ((ProxyApplication)mContext.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)mContext.getApplicationContext()).getiTcpPort(), "");
                List<User> users = ProxyApplication.getDaoSession().getUserDao().queryBuilder().orderDesc().where(UserDao.Properties.Unique.eq(Unique), UserDao.Properties.SrtImsi.eq(target.getImsi()),
                        UserDao.Properties.ConnTime.gt(SharePreferenceUtils.getInstance(mContext).getLong("status_notif_starttime" + ((ProxyApplication)mContext.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)mContext.getApplicationContext()).getiTcpPort(), System.currentTimeMillis()))).build().list();
                int iAuth = 0;
                TargetUser targetUser = ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(TargetUserDao.Properties.StrImsi.eq(target.getImsi())).build().unique();
                if (targetUser !=  null) { iAuth = 2; }
                if (users.size() != 0) {
                    User updateData = users.get(0);
                    updateData.setIAuth(iAuth);
                    updateData.setDetachTime(System.currentTimeMillis());
                    ProxyApplication.getDaoSession().getUserDao().update(updateData);
                } else {
                    User insertData = new User(null, target.getImsi(), null, iAuth, false, 1, null, null,  System.currentTimeMillis(), Unique);
                    ProxyApplication.getDaoSession().getUserDao().insert(insertData);
                }
                /////
                TargetDetach td = new TargetDetach();
                td.setImsi(target.getImsi());
                EventBus.getDefault().post(td);
                try {
                    BcastCommonApi.soundRing(mContext);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        notifyDataSetChanged();
    }

    public int getAuthTotal() {
        return iCurAuthTotal;
    }
    private class ViewHolder {
        LinearLayout layout_name;
        TextView textViewName;
        TextView textViewImsi;
        TextView textViewConntime;
        LinearLayout layout_imei;
        TextView textViewImei;
        TextView textViewCount;
        ImageView iv_user_icon;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_conn_target, null);
            holder.textViewName = ((TextView) convertView.findViewById(R.id.name));
            holder.textViewImsi = ((TextView) convertView.findViewById(R.id.imsi));
            holder.textViewConntime = ((TextView)convertView.findViewById(R.id.conntime));
            holder.layout_imei = (LinearLayout) convertView.findViewById(R.id.layout_imei);
            holder.textViewImei = ((TextView) convertView.findViewById(R.id.imei));
            holder.textViewCount = ((TextView)convertView.findViewById(R.id.count));
            holder.iv_user_icon = ((ImageView)convertView.findViewById(R.id.user_icon));
            holder.layout_name = ((LinearLayout) convertView.findViewById(R.id.layout_name));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        //imsi
        holder.textViewImsi.setText(targetDataStructs.get(position).getImsi());
        //imei
        if ("4G".equals(strCurTech) && targetDataStructs.get(position).getAuthState() !=  1) {
            holder.layout_imei.setVisibility(View.GONE);
        } else {
            holder.layout_imei.setVisibility(View.VISIBLE);
            holder.textViewImei.setText(targetDataStructs.get(position).getImei());
        }
        String strImeiName = "", strImsiName = "";
        if (targetDataStructs.get(position).getAuthState() ==  1) {
            TargetUser targetUser;
            if (!TextUtils.isEmpty(targetDataStructs.get(position).getImei())) {
                targetUser = ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(TargetUserDao.Properties.StrImsi.eq(targetDataStructs.get(position).getImsi()),
                        TargetUserDao.Properties.StrImei.eq(targetDataStructs.get(position).getImei())).build().unique();
                if (targetUser != null) {
                    strImsiName = targetUser.getStrName();
                } else {
                    targetUser = ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(TargetUserDao.Properties.StrImsi.eq(targetDataStructs.get(position).getImsi())).build().unique();
                    if (targetUser != null) {
                        strImsiName = targetUser.getStrName();
                    }
                    targetUser = ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(TargetUserDao.Properties.StrImei.eq(targetDataStructs.get(position).getImei())).build().unique();
                    if (targetUser != null) {
                        strImeiName = targetUser.getStrName();
                    }
                }
            } else {
                targetUser = ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(TargetUserDao.Properties.StrImsi.eq(targetDataStructs.get(position).getImsi())).build().unique();
                if (targetUser != null) {
                    strImsiName = targetUser.getStrName();
                }
            }
            holder.layout_name.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(strImeiName) && !TextUtils.isEmpty(strImsiName)) {
                holder.textViewName.setText(strImsiName);
            } else if (!TextUtils.isEmpty(strImeiName) && TextUtils.isEmpty(strImsiName)) {
                holder.textViewName.setText(strImeiName);
            } else if (!TextUtils.isEmpty(strImeiName) && !TextUtils.isEmpty(strImsiName)) {
                holder.textViewName.setText(strImsiName + "/" + strImeiName);
            } else {
                holder.layout_name.setVisibility(View.GONE);
            }
            holder.iv_user_icon.setImageResource(R.mipmap.user_red_icon);
            convertView.findViewById(R.id.layout_attachtime).setVisibility(View.VISIBLE);
            ((TextView)convertView.findViewById(R.id.attachtime)).setText(targetDataStructs.get(position).getStrAttachtime());
        } else if(targetDataStructs.get(position).getAuthState() ==  2) {
            TargetUser targetUser;
            if (!TextUtils.isEmpty(targetDataStructs.get(position).getImei())) {
                targetUser = ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(TargetUserDao.Properties.StrImsi.eq(targetDataStructs.get(position).getImsi()),
                        TargetUserDao.Properties.StrImei.eq(targetDataStructs.get(position).getImei())).build().unique();
                if (targetUser != null) {
                    strImsiName = targetUser.getStrName();
                } else {
                    targetUser = ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(TargetUserDao.Properties.StrImsi.eq(targetDataStructs.get(position).getImsi())).build().unique();
                    if (targetUser != null) {
                        strImsiName = targetUser.getStrName();
                    }
                    targetUser = ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(TargetUserDao.Properties.StrImei.eq(targetDataStructs.get(position).getImei())).build().unique();
                    if (targetUser != null) {
                        strImeiName = targetUser.getStrName();
                    }
                }
            } else {
                targetUser = ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(TargetUserDao.Properties.StrImsi.eq(targetDataStructs.get(position).getImsi())).build().unique();
                if (targetUser != null) {
                    strImsiName = targetUser.getStrName();
                }
            }
            holder.layout_name.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(strImeiName) && !TextUtils.isEmpty(strImsiName)) {
                holder.textViewName.setText(strImsiName);
            } else if (!TextUtils.isEmpty(strImeiName) && TextUtils.isEmpty(strImsiName)) {
                holder.textViewName.setText(strImeiName);
            } else if (!TextUtils.isEmpty(strImeiName) && !TextUtils.isEmpty(strImsiName)) {
                holder.textViewName.setText(strImsiName + "/" + strImeiName);
            } else {
                holder.layout_name.setVisibility(View.GONE);
            }
            holder.iv_user_icon.setImageResource(R.mipmap.user_yellow_icon);
            convertView.findViewById(R.id.layout_attachtime).setVisibility(View.GONE);
        } else {
            holder.layout_name.setVisibility(View.GONE);
            holder.iv_user_icon.setImageResource(R.mipmap.user_icon);
            //attachtime
            convertView.findViewById(R.id.layout_attachtime).setVisibility(View.GONE);
        }

        //conntime
        holder.textViewConntime.setText(targetDataStructs.get(position).getStrConntime());

        //count
        holder.textViewCount.setText("Conn Count " + targetDataStructs.get(position).getCount());
        return convertView;
    }
}