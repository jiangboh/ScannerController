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
import android.widget.ListView;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.data_ben.DeviceDataStruct;
import com.bravo.data_ben.TargetDataStruct;

import java.util.ArrayList;

/**
 * Created by admin on 2018-9-14.
 */

public class AdapterScanner extends BaseAdapter {
    private final static String TAG = "AdapterConnTarget";
    private final static ArrayList<TargetDataStruct> targetDataStructs = new ArrayList<>();
    private Context mContext;
    private ListView listView;
    private String strCurTech;
    private static int iCurAuthTotal = 0;
    private Long changedTime = System.currentTimeMillis();
    private TextView tvTotal;
    public AdapterScanner(Context context) {
        //this.radioButton = radioButton;
        this.mContext = context;
        //iCurPosition = -1;
    }
    public AdapterScanner(Context context, TextView txView,ListView listView) {
        this.tvTotal = txView;
        this.mContext = context;
        this.listView = listView;
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

    public static void AddScannerImsi(TargetDataStruct targetDataStruct)
    {
        String strImsi = targetDataStruct.getImsi();
        if (TextUtils.isEmpty(strImsi))
        {
            Log.d(TAG,"Imsi为空！");
            return;
        }

        for (int i = 0; i < iCurAuthTotal; i++) {
            if (strImsi.equals(targetDataStructs.get(i).getImsi())) {
                //Log.d(TAG,"Imsi重复");
                //targetDataStructs.get(i).setbPositionStatus(true);
                return;
            }
        }

        iCurAuthTotal++;
        targetDataStruct.setCount(iCurAuthTotal);
        targetDataStructs.add(targetDataStruct);

    }


    public void ChangedTotal(int count) {
        if (tvTotal != null) {
            tvTotal.setText(String.valueOf(iCurAuthTotal + count));
        }
    }

    public void AttachTarget(TargetDataStruct targetDataStruct) {
        //Logs.d(TAG,"targetDataStruct==" + isChanged + ";" + targetDataStruct.getImsi());
        AddScannerImsi(targetDataStruct);
        updateTotal(true);
    }

    private void updateTotal(boolean isChanged){
        //Long curTime = System.currentTimeMillis();
        //大于秒才刷新界面
        //if ((curTime - changedTime) > 1000) {
            //changedTime = System.currentTimeMillis();

            if (tvTotal != null) {
                tvTotal.setText(String.valueOf(iCurAuthTotal));
            }

            if (isChanged)
                this.notifyDataSetChanged(iCurAuthTotal - 1);
        //}
    }

    private void updateTotal(){
        this.updateTotal(true);
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


     private void notifyDataSetChanged( int position){
        listView.setSelection(listView.getCount() - 1);
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        int lastVisiblePosition = listView.getLastVisiblePosition();
        //Log.d(TAG,"局部刷新:fisrst=" + firstVisiblePosition + ";last=" + lastVisiblePosition + ";p=" +position);

         int len = lastVisiblePosition-firstVisiblePosition + 1;
         if (len <= 1) {
             notifyDataSetChanged();
         } else {
             //if (lastVisiblePosition != (position-1) ) return;
             for (int i = firstVisiblePosition; i < len; i++) {
                 View item = listView.getChildAt(i);
                 item = getView(i, item, listView);
             }
             listView.setSelection(lastVisiblePosition);
         }

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
        //Log.e(TAG,"position=" + position);
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
            holder.textViewCount = ((TextView)convertView.findViewById(R.id.scanner_count));
            holder.iv_user_icon = ((ImageView)convertView.findViewById(R.id.user_icon));
            holder.layout_name = ((LinearLayout) convertView.findViewById(R.id.layout_name));
            holder.attachtime = ((TextView)convertView.findViewById(R.id.attachtime));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        //userType
        int iUserType = targetDataStructs.get(position).getiUserType();
        if (iUserType == 0)
            holder.iv_user_icon.setImageResource(R.mipmap.user_red_icon);
        else if (iUserType == 1)
            holder.iv_user_icon.setImageResource(R.mipmap.user_green_icon);
        else if (iUserType == 2)
            holder.iv_user_icon.setImageResource(R.mipmap.user_icon);
        else
            holder.iv_user_icon.setImageResource(R.mipmap.user_yellow_icon);

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
        holder.textViewCount.setText(String.valueOf(targetDataStructs.get(position).getCount()));
        return convertView;
    }
}
