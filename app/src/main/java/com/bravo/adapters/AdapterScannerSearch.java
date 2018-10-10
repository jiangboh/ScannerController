package com.bravo.adapters;

import android.content.Context;
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
 * Created by admin on 2018-10-8.
 */

public class AdapterScannerSearch  extends BaseAdapter {
    private final static String TAG = "AdapterConnTarget";
    private ArrayList<TargetDataStruct> targetDataStructs;
    private Context mContext;
    private ListView listView;
    private Long changedTime = System.currentTimeMillis();

    /*public AdapterScanner(Context context) {
        this.mContext = context;
    }*/
    public AdapterScannerSearch(Context context, ArrayList<TargetDataStruct> targetDataStructs, ListView listView) {
        this.targetDataStructs = targetDataStructs;
        this.mContext = context;
        this.listView = listView;
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



    //刷新界面
    private void updateTotal() {
        this.notifyDataSetChanged();
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
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_conn_target, null);
            holder.layout_conntime = (LinearLayout) convertView.findViewById(R.id.layout_conntime);
            holder.textViewName = ((TextView) convertView.findViewById(R.id.name));
            holder.textViewImsi = ((TextView) convertView.findViewById(R.id.imsi));
            holder.textViewConntime = ((TextView) convertView.findViewById(R.id.conntime));
            holder.layout_imei = (LinearLayout) convertView.findViewById(R.id.layout_imei);
            holder.textViewImei = ((TextView) convertView.findViewById(R.id.imei));
            holder.textViewCount = ((TextView) convertView.findViewById(R.id.scanner_count));
            holder.iv_user_icon = ((ImageView) convertView.findViewById(R.id.user_icon));
            holder.layout_name = ((LinearLayout) convertView.findViewById(R.id.layout_name));
            holder.attachtime = ((TextView) convertView.findViewById(R.id.attachtime));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
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
