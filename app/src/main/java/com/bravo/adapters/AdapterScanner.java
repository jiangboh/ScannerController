package com.bravo.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.R;
import com.bravo.data_ben.DeviceDataStruct;
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
 * Created by admin on 2018-9-14.
 */

public class AdapterScanner extends BaseAdapter {
    private final static String TAG = "AdapterConnTarget";
    private final ArrayList<TargetDataStruct> targetDataStructs = new ArrayList<>();
    private Context mContext;
    //private int iCurPosition;
    private String strCurTech;
    private int iCurAuthTotal = 0;
    //private RadioButton radioButton;
    private TextView tvTotal;
    public AdapterScanner(Context context) {
        //this.radioButton = radioButton;
        this.mContext = context;
        //iCurPosition = -1;
    }
    public AdapterScanner(Context context, TextView txView) {
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
        if (targetDataStruct.getiUserType() == 2) {
            targetDataStructs.add(iCurAuthTotal, targetDataStruct);
        } else {
            targetDataStructs.add(targetDataStruct);
        }
        updateTotal();
    }

    public void AttachTarget(TargetDataStruct targetDataStruct) {
        String strImsi = targetDataStruct.getImsi();
        if (TextUtils.isEmpty(strImsi))
        {
            Log.d(TAG,"Imsi为空！");
            return;
        }

        for (int i = 0; i < iCurAuthTotal; i++) {
            if (strImsi.equals(targetDataStructs.get(i).getImsi())) {
                Log.d(TAG,"Imsi重复");
                //targetDataStructs.get(i).setbPositionStatus(true);
                return;
            }
        }
        //Log.d(TAG,"%%%%%%%%%%%%% " + targetDataStruct.getiUserType());
        targetDataStructs.add(targetDataStruct);
        iCurAuthTotal++;
        /*for (int i = iCurAuthTotal; i < targetDataStructs.size(); i++) {
            if (targetDataStruct.getImsi().equals(targetDataStructs.get(i).getImsi())) {
                if (TextUtils.isEmpty(targetDataStruct.getImei())) {
                    targetDataStructs.get(0).setImei(targetDataStructs.get(i).getImei());
                }
                targetDataStructs.get(0).setStrConntime(targetDataStructs.get(i).getStrConntime());
                targetDataStructs.remove(i);
                i = targetDataStructs.size();
            }
        }*/
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
                targetDataStruct.setiUserType(2);
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
        if (tvTotal != null) {
            tvTotal.setText(String.valueOf(iCurAuthTotal));
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
        if (tvTotal != null) {
            tvTotal.setText("0");
        }
        notifyDataSetChanged();
    }

    public void checkAuthTarget() {
        for (int i = 0; i < iCurAuthTotal; i++) {
            if (!targetDataStructs.get(i).isbPositionStatus()) {
                TargetDataStruct target = new TargetDataStruct();
                target = targetDataStructs.get(i);
                target.setiUserType(2);
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
        LinearLayout layout_conntime;
        LinearLayout layout_name;
        TextView textViewName;
        TextView textViewImsi;
        TextView textViewConntime;
        TextView attachtime;
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
            holder.layout_conntime = (LinearLayout) convertView.findViewById(R.id.layout_conntime);
            holder.textViewName = ((TextView) convertView.findViewById(R.id.name));
            holder.textViewImsi = ((TextView) convertView.findViewById(R.id.imsi));
            holder.textViewConntime = ((TextView)convertView.findViewById(R.id.conntime));
            holder.layout_imei = (LinearLayout) convertView.findViewById(R.id.layout_imei);
            holder.textViewImei = ((TextView) convertView.findViewById(R.id.imei));
            holder.textViewCount = ((TextView)convertView.findViewById(R.id.count));
            holder.iv_user_icon = ((ImageView)convertView.findViewById(R.id.user_icon));
            holder.layout_name = ((LinearLayout) convertView.findViewById(R.id.layout_name));
            holder.attachtime = ((TextView)convertView.findViewById(R.id.attachtime));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        //userType
        int iUserType = targetDataStructs.get(position).getiUserType();
        //Log.d(TAG,"************ = " + iUserType);
        if (iUserType == 0)
            holder.iv_user_icon.setImageResource(R.mipmap.user_red_icon);
        else if (iUserType == 1)
            holder.iv_user_icon.setImageResource(R.mipmap.user_yellow_icon);
        else if (iUserType == 2)
            holder.iv_user_icon.setImageResource(R.mipmap.user_icon);
        else
            holder.iv_user_icon.setImageResource(R.mipmap.user_icon);

        //name
        holder.textViewName.setText(targetDataStructs.get(position).getName());
        //imsi
        holder.textViewImsi.setText(targetDataStructs.get(position).getImsi());
        //imei
        String strImei = targetDataStructs.get(position).getImei();
        if (strImei == null || strImei.isEmpty() || DeviceDataStruct.MODE.LTE.equals(strImei)) {
            holder.layout_imei.setVisibility(View.GONE);
        } else {
            holder.layout_imei.setVisibility(View.VISIBLE);
            holder.textViewImei.setText(targetDataStructs.get(position).getImei());
        }

        //conntime
        holder.attachtime.setText(targetDataStructs.get(position).getStrAttachtime());
        holder.layout_conntime.setVisibility(View.GONE);
        //count
        //holder.textViewCount.setText("Conn Count " + targetDataStructs.get(position).getCount());
        return convertView;
    }
}
