package com.bravo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.database.BlackWhiteImsi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by admin on 2018-11-2.
 */

public class AdapeterBlackWhiteImsi extends BaseAdapter {
    private final static String TAG = "AdapeterBlackWhiteImsi";
    private OnShowItemClickListener onShowItemClickListener;
    private ArrayList<BlackWhiteImsi> dataList = new ArrayList<>();
    private boolean isShow = false; // 是否显示CheckBox标识
    private BlackWhiteImsi bean;
    private Context mContext;
    private static Lock lock = new ReentrantLock();
    private int selectedItem = -1;
    private OnAllSelect selectlistener;

    private boolean blackImsi = true;

    public AdapeterBlackWhiteImsi(Context context, boolean blackImsi) {
        this.blackImsi = blackImsi;
        this.mContext = context;
        this.isShow = false;
    }

    public interface OnAllSelect{
        boolean onSelect(int allnum,int selnum);
    }

    public void setAllSelectListener(OnAllSelect listener) {
        this.selectlistener = listener;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setIsShow(boolean isShow) {
        lock.lock();
        try {
            this.isShow = isShow;
            if (!isShow) {
                for(int i=0;i<dataList.size();i++) {
                    this.dataList.get(i).setChecked(false);
                }
            }
            MyDataSetChanged();
        } finally {
            lock.unlock();
        }
    }

    public void setDataList(List<BlackWhiteImsi> imsiList) {
        lock.lock();
        try {
            if (imsiList==null) return;

            this.dataList.clear();
            for(int i=0;i<imsiList.size();i++) {
                this.dataList.add(imsiList.get(i));
            }
        } finally {
            lock.unlock();
        }
    }

    public ArrayList<BlackWhiteImsi> getDataList() {
        lock.lock();
        try {
            return dataList;
        } finally {
            lock.unlock();
        }
    }

    public void addData(BlackWhiteImsi data) {
        lock.lock();
        try {
            this.dataList.add(data);
            MyDataSetChanged();
        } finally {
            lock.unlock();
        }
    }

    public void updataByImsi(BlackWhiteImsi data) {
        lock.lock();
        try {
            for(int i=0;i<dataList.size();i++) {
                if (this.dataList.get(i).getImsi().equals(data.getImsi())) {
                    dataList.set(i,data);
                    break;
                }
            }
            MyDataSetChanged();
        } finally {
            lock.unlock();
        }
    }

    public void updataByImei(BlackWhiteImsi data) {
        lock.lock();
        try {
            for(int i=0;i<dataList.size();i++) {
                if (this.dataList.get(i).getImei().equals(data.getImei())) {
                    dataList.set(i,data);
                    break;
                }
            }
            MyDataSetChanged();
        } finally {
            lock.unlock();
        }
    }

    public void setChecked(int index,boolean isCheck) {
        lock.lock();
        try {
            this.dataList.get(index).setChecked(isCheck);
            //MyDataSetChanged();
        } finally {
            lock.unlock();
        }
    }

    public void setChecked(int index) {
        lock.lock();
        try {
            this.dataList.get(index).setChecked(!this.dataList.get(index).isChecked());
            //MyDataSetChanged();
        } finally {
            lock.unlock();
        }
    }

    public void setAllChecked(boolean isCheck) {
        lock.lock();
        try {
            for(int i=0;i<dataList.size();i++) {
                this.dataList.get(i).setChecked(isCheck);
            }
            MyDataSetChanged();
        } finally {
            lock.unlock();
        }
    }

    public void setSelectedItem(int selectedItem)
    {
        this.selectedItem = selectedItem;
        MyDataSetChanged();
    }

    public void MyDataSetChanged()
    {
        notifyDataSetChanged();
        selectlistenerChanged();
    }

    private void selectlistenerChanged()
    {
        if (selectlistener != null) {
            int selectNum = 0;  //选中的数量
            int allNum = 0;
            lock.lock();
            try {
                allNum = dataList.size();
                for(int i=0;i<dataList.size();i++) {
                    if (this.dataList.get(i).isChecked()){
                        selectNum++;
                    }
                }
            } finally {
                lock.unlock();
            }
            selectlistener.onSelect(allNum,selectNum);
        }
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
        MyDataSetChanged();
    }

    public void RemoveAll() {
        dataList.clear();
        MyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_black_white_list, null);
            holder.image = (ImageView) convertView.findViewById(R.id.user_icon);
            holder.allNum = (TextView) convertView.findViewById(R.id.all_count);

            holder.lImsi = (LinearLayout)convertView.findViewById(R.id.layout_imsi);
            holder.lImei = (LinearLayout)convertView.findViewById(R.id.layout_imei);
            holder.lTmsi = (LinearLayout)convertView.findViewById(R.id.layout_tmsi);
            holder.lRb = (LinearLayout)convertView.findViewById(R.id.layout_rb);

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.imsi = (TextView) convertView.findViewById(R.id.imsi);
            holder.imei = (TextView) convertView.findViewById(R.id.imei);
            holder.tmsi = (TextView) convertView.findViewById(R.id.tmsi);
            holder.startRb = (TextView) convertView.findViewById(R.id.startRb);
            holder.stopRb = (TextView) convertView.findViewById(R.id.stopRb);

            holder.cb = (CheckBox) convertView.findViewById(R.id.checkstate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(position == selectedItem)
        {
            convertView.setBackgroundResource(R.drawable.item_select_bg);
        } else {
            convertView.setBackgroundResource(R.drawable.item_bg);
        }

        lock.lock();
        try {
            holder.allNum.setText(String.valueOf(position + 1));

            bean = dataList.get(position);
            // 是否是多选状态
            if (this.isShow()) {
                holder.cb.setVisibility(View.VISIBLE);
            } else {
                holder.cb.setVisibility(View.GONE);
            }

            holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        dataList.get(position).setChecked(true);
                    } else {
                        dataList.get(position).setChecked(false);
                    }
                    selectlistenerChanged();
                }
            });
            // 必须放在监听后面
            holder.cb.setChecked(bean.isChecked());

            holder.name.setText(bean.getName());

            if (bean.getImsi() == null  || bean.getImsi().equals("")) {
                holder.lImsi.setVisibility(View.GONE);
            } else {
                holder.imsi.setText(bean.getImsi());
                holder.lImsi.setVisibility(View.VISIBLE);
            }
            if (bean.getImei() == null || bean.getImei().equals("")) {
                holder.lImei.setVisibility(View.GONE);
            } else {
                holder.imei.setText(bean.getImei());
                holder.lImei.setVisibility(View.VISIBLE);
            }
            if (bean.getTmsi() == null || bean.getTmsi().equals("")) {
                holder.lTmsi.setVisibility(View.GONE);
            } else {
                holder.tmsi.setText(bean.getTmsi());
                holder.lTmsi.setVisibility(View.VISIBLE);
            }

            if (blackImsi) {
                holder.image.setImageResource(R.mipmap.user_red_icon);
                holder.lRb.setVisibility(View.VISIBLE);
                holder.startRb.setText(String.valueOf(bean.getStartRb()));
                holder.stopRb.setText(String.valueOf(bean.getStopRb()));
            } else {
                holder.image.setImageResource(R.mipmap.user_green_icon);
                holder.lRb.setVisibility(View.GONE);
            }
        } finally {
            lock.unlock();
        }

        return convertView;
    }

    public interface OnShowItemClickListener {
        public void onShowItemClick(BlackWhiteImsi bean);
    }

    public void setOnShowItemClickListener(OnShowItemClickListener onShowItemClickListener) {
        this.onShowItemClickListener = onShowItemClickListener;
    }

    private class ViewHolder {
        ImageView image;
        TextView allNum;

        LinearLayout lImsi;
        LinearLayout lImei;
        LinearLayout lTmsi;
        LinearLayout lRb;

        TextView name;
        TextView imsi;
        TextView imei;
        TextView tmsi;
        TextView startRb;
        TextView stopRb;

        CheckBox cb;
    }


}