package com.bravo.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.data_ben.DeviceDataStruct;
import com.bravo.data_ben.DeviceFragmentStruct;
import com.bravo.utils.Logs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by admin on 2018-9-20.
 */

public class AdapeterDeviceList extends BaseAdapter {
    private final static String TAG = "AdapeterDeviceList";
    //private final ArrayList<DeviceDataStruct> deviceDataStructs = new ArrayList<>();
    private Context mContext;
    //private int iCurPosition;
    private int iCurFindTotal = 0;
    private TextView device_Total;

    public AdapeterDeviceList(Context context,TextView device_Total) {
        this.mContext = context;
        this.device_Total = device_Total;
        this.iCurFindTotal = DeviceFragmentStruct.getSize();
    }

    @Override
    public int getCount() {
        return DeviceFragmentStruct.getSize() ;
    }

    @Override
    public DeviceDataStruct getItem(int position) {
        return DeviceFragmentStruct.getDevice(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<DeviceDataStruct> getList() {
        return DeviceFragmentStruct.getList();
    }


    public void removeTarget(int iPosition) {
        DeviceFragmentStruct.removeList(iPosition);
        iCurFindTotal--;
        updataTotal();
    }

    public void removeTarget() {
        iCurFindTotal--;
        updataTotal();
    }

    public void RemoveAll() {
        DeviceFragmentStruct.clearList();
        iCurFindTotal = 0;

        updataTotal();
    }

    private void updataTotal()
    {
        Logs.d(TAG,"刷新界面。。。");
        if (device_Total != null) {
            device_Total.setText(String.valueOf(DeviceFragmentStruct.getSize()));
        }

        notifyDataSetChanged();
    }

    public void dataChanged() {
        iCurFindTotal = DeviceFragmentStruct.getSize();
        updataTotal();
    }

    public void dataChanged(DeviceDataStruct dds) {
       int index = DeviceFragmentStruct.inListIndex(dds.getSN());
        if (-1 == index)
        {
            //DeviceFragmentStruct.addList(dds);
            //iCurFindTotal++;
        }
        else
        {
            DeviceFragmentStruct.ChangeDetail(index,dds.getDetail());
        }

        iCurFindTotal = DeviceFragmentStruct.getSize();
        updataTotal();
    }

    public int getAuthTotal() {
        return iCurFindTotal;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_find_list, null);
            holder.addImage = (TextView) convertView.findViewById(R.id.addImageView);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            holder.fullname_layout = (LinearLayout)convertView.findViewById(R.id.fullname_layout);
            holder.LastTime_layout = (LinearLayout)convertView.findViewById(R.id.layout_LastTime);

            holder.SN = (TextView) convertView.findViewById(R.id.sn);
            holder.FullName = (TextView) convertView.findViewById(R.id.fullname);
            holder.LastTime = (TextView) convertView.findViewById(R.id.LastTime);
            holder.Mode = (TextView) convertView.findViewById(R.id.mode);
            holder.AddStatus = (TextView) convertView.findViewById(R.id.addStatus);
            holder.Ip = (TextView) convertView.findViewById(R.id.ip);
            holder.Port = (TextView) convertView.findViewById(R.id.port);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DeviceDataStruct dds = DeviceFragmentStruct.getDevice(position);
        holder.SN.setText(dds.getSN());
        holder.FullName.setText(dds.getFullName());
        holder.Mode.setText(dds.getMode());
        holder.AddStatus.setText(DeviceDataStruct.sAddStatus[dds.getAddStatus()]);
        holder.LastTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(dds.getLastTime()));
        holder.Ip.setText(dds.getIp());
        holder.Port.setText(String.valueOf(dds.getPort()));

        String mode = dds.getMode();
        if (mode.equalsIgnoreCase(DeviceDataStruct.MODE.LTE_TDD) || mode.equalsIgnoreCase(DeviceDataStruct.MODE.LTE_FDD)) {
            if (dds.isStatus_cell()) {
                if (dds.isStatus_radio()) {
                    holder.imageView.setImageDrawable(ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.icon_4g_normal));
                } else {
                    holder.imageView.setImageDrawable(ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.icon_4g_redio_off));
                }
            } else {
                holder.imageView.setImageDrawable(ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.icon_4g_fail));
            }
        } else if (mode.equalsIgnoreCase(DeviceDataStruct.MODE.WCDMA) || mode.equalsIgnoreCase(DeviceDataStruct.MODE.CDMA)) {
            if (dds.isStatus_cell()) {
                if (dds.isStatus_radio()) {
                    holder.imageView.setImageDrawable(ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.icon_3g_normal));
                } else {
                    holder.imageView.setImageDrawable(ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.icon_3g_redio_off));
                }
            } else {
                holder.imageView.setImageDrawable(ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.icon_3g_fail));
            }
        } else if (mode.equalsIgnoreCase(DeviceDataStruct.MODE.GSM) || mode.equalsIgnoreCase(DeviceDataStruct.MODE.GSM_V2)) {
            if (dds.isStatus_cell()) {
                if (dds.isStatus_radio()) {
                    holder.imageView.setImageDrawable(ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.icon_2g_normal));
                } else {
                    holder.imageView.setImageDrawable(ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.icon_2g_redio_off));
                }
            } else {
                holder.imageView.setImageDrawable(ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.icon_2g_fail));
            }
        } else {
            //holder.imageView.setImageDrawable(ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.icon_4g_default));
        }

        holder.LastTime_layout.setVisibility(View.VISIBLE);
        holder.imageView.setVisibility(View.VISIBLE);
        holder.addImage.setVisibility(View.GONE);

        //设置item的点击效果
        convertView.setBackgroundResource(R.drawable.dialog_btn_bg_selector);

        return convertView;
    }

    private class ViewHolder {
        TextView addImage;
        ImageView imageView;
        TextView SN;
        TextView FullName;
        TextView LastTime;
        TextView Mode;
        TextView AddStatus;
        TextView Ip;
        TextView Port;
        LinearLayout fullname_layout;
        LinearLayout LastTime_layout;
    }

    public void DeviceListTarget(DeviceDataStruct deviceDataStruct) {
        boolean newDevice = true;

        for (int i = 0; i < iCurFindTotal; i++) {
            if (deviceDataStruct.getSN().equals(DeviceFragmentStruct.getDevice(i).getSN())) {
                if (DeviceFragmentStruct.getDevice(i).getiState() == DeviceDataStruct.ON_LINE)
                    DeviceFragmentStruct.getDevice(i).setiState(DeviceDataStruct.OFF_LINE);
                else
                    DeviceFragmentStruct.getDevice(i).setiState(DeviceDataStruct.ON_LINE);

                newDevice = false;
                //return;
            }
        }

        if (newDevice) {
            DeviceFragmentStruct.addList(deviceDataStruct);
            iCurFindTotal++;
        }

        notifyDataSetChanged();
    }
}
