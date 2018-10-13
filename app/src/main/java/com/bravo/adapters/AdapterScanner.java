package com.bravo.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.data_ben.TargetDataStruct;
import com.bravo.utils.Logs;

import java.util.ArrayList;

/**
 * Created by admin on 2018-9-14.
 */

public class AdapterScanner extends BaseAdapter {
    private final static String TAG = "AdapterConnTarget";
    private final static ArrayList<TargetDataStruct> targetDataStructs = new ArrayList<>();
    private Context mContext;
    private ListView listView;
    private static int iCurAuthTotal = 0;
    private Long changedTime = System.currentTimeMillis();
    private TextView tvTotal;
    private static int MAX_TOTAL = 15000;
    private static boolean isDupRemo = true;

    /*public AdapterScanner(Context context) {
        this.mContext = context;
    }*/
    public AdapterScanner(Context context, TextView txView, ListView listView) {
        this.tvTotal = txView;
        this.mContext = context;
        this.listView = listView;
    }

    public static void setMaxTotal(int maxTotal) {
        MAX_TOTAL = maxTotal;
    }

    public static void setIsDupRemo(boolean isDupRemo) {
        AdapterScanner.isDupRemo = isDupRemo;
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

    public static ArrayList<TargetDataStruct> getList() {
        return targetDataStructs;
    }

    //添加imsi到列表
    public static void AddScannerImsi(TargetDataStruct tds) {
        String strImsi = tds.getImsi();
        //Logs.d(TAG, "iCurAuthTotal = " + strImsi);
        if (TextUtils.isEmpty(strImsi)) {
            Logs.d(TAG, "Imsi为空！",true);
            return;
        }

        if(isDupRemo) { //去重
            //Logs.d(TAG, "去重时数组中的数量 = " + targetDataStructs.size());
            for (int i = 0; i < targetDataStructs.size(); i++) {
                if (strImsi.equals(targetDataStructs.get(i).getImsi())) {
                    //Logs.d(TAG,"Imsi重复");
                    return;
                }
            }
        }

        //Logs.d(TAG, "判断后数组中的数量 = " + targetDataStructs.size());
        iCurAuthTotal++;
        tds.setCount(iCurAuthTotal);
        targetDataStructs.add(tds);
    }

    //刷新捕号数量显示
    public void ChangedTotal(int count) {
        if (tvTotal != null) {
            tvTotal.setText(String.valueOf(iCurAuthTotal));
        }
    }

    public void AttachTarget(TargetDataStruct tds) {
        //Logs.d(TAG, "targetDataStruct==" + targetDataStruct.getImsi());
        AddScannerImsi(tds);
        updateTotal();

    }

    public void AttachTarget(ArrayList<TargetDataStruct> tdsList) {
        for(int i=0;i<tdsList.size();i++)
        {
            try {
                AddScannerImsi(tdsList.get(i));
            }catch (Exception e) {
                Logs.e(TAG,"出错：" + e.getMessage(),true);
            }
        }

        updateTotal();

        int len = getCount();
        //Logs.d(TAG, "添加前删除个数：" + len );
        for (int i = MAX_TOTAL; i < len ; i++) {
            //Logs.d(TAG, "添加前删除。。。" );
            targetDataStructs.remove(0);
        }
        //Logs.d(TAG, "添加前删除个数：" + getCount());
    }

    //刷新界面
    private void updateTotal() {
        if (tvTotal != null) {
            tvTotal.setText(String.valueOf(iCurAuthTotal));
        }
        //Logs.d(TAG, "刷新时个数：" + getCount());
        //this.notifyDataSetChanged(getCount() - 1);
        this.notifyDataSetChanged();
    }

    private void checkCount()
    {
        if (iCurAuthTotal > MAX_TOTAL)
        {
            //Logs.d(TAG, "删除。。。" );
            targetDataStructs.remove(0);
            notifyDataSetChanged();
        }
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

    public void setSelectionEnd()
    {
        listView.setSelection(listView.getCount() - 1);
    }

    private void notifyDataSetChanged(int position) {
        setSelectionEnd();
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        int lastVisiblePosition = listView.getLastVisiblePosition();

        //Logs.d(TAG, "局部刷新:f=" + firstVisiblePosition + ";l=" + lastVisiblePosition  +";p=" + position);

        if (lastVisiblePosition - firstVisiblePosition < 1)
        {
            notifyDataSetChanged();
        } else {
            if (iCurAuthTotal > MAX_TOTAL) {
                //Logs.d(TAG, "刷新：" + position);
                for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                    View item = listView.getChildAt(firstVisiblePosition - i);
                    //Logs.d(TAG, "局部刷新:f=" + (i+1));
                    getView(i + 1, item, listView);
                }
            }
        }
    }

    /*private void notifyDataSetChanged(int position) {
        listView.setSelection(listView.getCount() - 1);
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        int lastVisiblePosition = listView.getLastVisiblePosition();

        int len = lastVisiblePosition - firstVisiblePosition + 1;
        //Logs.d(TAG, "局部刷新:f=" + firstVisiblePosition + ";l=" + lastVisiblePosition + ";len=" + len +";p=" + position);
        if (len <= 1) {
            notifyDataSetChanged();
        } else {
            //if (lastVisiblePosition != (position-1) ) return;
            *//*for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                View item = listView.getChildAt(firstVisiblePosition - i);
                //Logs.d(TAG, "局部刷新:f=" + (i+1));
                getView(i+1, item, listView);
            }*//*
        }

    }*/

    private class ViewHolder {
        LinearLayout layout_conntime;
        LinearLayout layout_name;
        TextView textViewName;
        TextView textViewImsi;
        TextView textViewConntime;
        TextView attachtime;
        LinearLayout layout_tmsi;
        LinearLayout layout_imei;
        TextView textViewImei;
        TextView textViewTmsi;
        TextView textViewCount;
        ImageView iv_user_icon;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //Logs.e(TAG,"position=" + position);
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
            holder.layout_tmsi = ((LinearLayout) convertView.findViewById(R.id.layout_tmsi));
            holder.textViewTmsi = ((TextView) convertView.findViewById(R.id.tmsi));
            holder.attachtime = ((TextView) convertView.findViewById(R.id.attachtime));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //userType
        int iUserType = targetDataStructs.get(position).getiUserType();
        if (iUserType == TargetDataStruct.BLACK_IMSI)
            holder.iv_user_icon.setImageResource(R.mipmap.user_red_icon);
        else if (iUserType == TargetDataStruct.WHITE_IMSI)
            holder.iv_user_icon.setImageResource(R.mipmap.user_green_icon);
        else if (iUserType == TargetDataStruct.OTHER_IMSI)
            holder.iv_user_icon.setImageResource(R.mipmap.user_icon);
        else
            holder.iv_user_icon.setImageResource(R.mipmap.user_yellow_icon);

        //name
        String name = targetDataStructs.get(position).getName();
        if (name == null || name.isEmpty()) {
            holder.layout_name.setVisibility(View.GONE);
        } else {
            holder.layout_name.setVisibility(View.VISIBLE);
            holder.textViewName.setText(name);
        }
        String tmsi = targetDataStructs.get(position).getTmsi();
        if (tmsi == null || tmsi.isEmpty()) {
            holder.layout_tmsi.setVisibility(View.GONE);
        } else {
            holder.layout_tmsi.setVisibility(View.VISIBLE);
            holder.textViewTmsi.setText(tmsi);
        }
        //imsi
        holder.textViewImsi.setText(targetDataStructs.get(position).getImsi());
        //imei
        String strImei = targetDataStructs.get(position).getImei();
        if (strImei == null || strImei.isEmpty()) {
            holder.layout_imei.setVisibility(View.GONE);
        } else {
            holder.layout_imei.setVisibility(View.VISIBLE);
            holder.textViewImei.setText(strImei);
        }

        //conntime
        holder.attachtime.setText(targetDataStructs.get(position).getStrAttachtime());
        holder.layout_conntime.setVisibility(View.GONE);
        //count
        holder.textViewCount.setText(String.valueOf(targetDataStructs.get(position).getCount()));
        return convertView;
    }
}
