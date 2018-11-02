package com.bravo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.database.BlackWhiteImsi;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by admin on 2018-11-2.
 */

public class AdapeterBlackWhiteImsi extends BaseAdapter {
    private final static String TAG = "AdapeterBlackWhiteImsi";
    private ArrayList<BlackWhiteImsi> dataList = new ArrayList<>();
    private Context mContext;
    private static Lock lock = new ReentrantLock();

    private boolean blackImsi = true;

    public AdapeterBlackWhiteImsi(Context context, boolean blackImsi) {
        this.blackImsi = blackImsi;
        this.mContext = context;
    }


    public ArrayList<BlackWhiteImsi> getDataList() {
        lock.lock();
        try {
            return dataList;
        } finally {
            lock.unlock();
        }
    }

    public void setData(BlackWhiteImsi data) {
        lock.lock();
        try {
            this.dataList.add(data);
        } finally {
            lock.unlock();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public BlackWhiteImsi getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<BlackWhiteImsi> getList() {
        return dataList;
    }


    public void removeItem(int iPosition) {
        dataList.remove(iPosition);
        notifyDataSetChanged();
    }

    public void RemoveAll() {
        dataList.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_black_white_list, null);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.imsi = (TextView) convertView.findViewById(R.id.imsi);
            holder.imei = (TextView) convertView.findViewById(R.id.imei);
            holder.tmsi = (TextView) convertView.findViewById(R.id.tmsi);
            holder.startRb = (TextView) convertView.findViewById(R.id.startRb);
            holder.stopRb = (TextView) convertView.findViewById(R.id.stopRb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        lock.lock();
        try {
            holder.name.setText(dataList.get(position).getName());
            holder.imsi.setText(dataList.get(position).getImsi());
            holder.imei.setText(dataList.get(position).getImei());
            holder.tmsi.setText(dataList.get(position).getTmsi());

            if (blackImsi) {
                holder.stopRb.setVisibility(View.VISIBLE);
                holder.startRb.setVisibility(View.VISIBLE);
                holder.startRb.setText(String.valueOf(dataList.get(position).getStartRb()));
                holder.stopRb.setText(String.valueOf(dataList.get(position).getStopRb()));
            } else {
                holder.stopRb.setVisibility(View.GONE);
                holder.startRb.setVisibility(View.GONE);
            }
        } finally {
            lock.unlock();
        }

        return convertView;
    }

    private class ViewHolder {
        TextView name;
        TextView imsi;
        TextView imei;
        TextView tmsi;
        TextView startRb;
        TextView stopRb;
    }


}