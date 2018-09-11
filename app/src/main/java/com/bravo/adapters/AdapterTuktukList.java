package com.bravo.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.socket.SocketTCP;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdapterTuktukList extends BaseAdapter {
    private final static String TAG = "AdapterTuktukList";
    private Context mContext;
    private List<BluetoothDevice> mDevices = new ArrayList<>();
    private String strMacAddress;

    public AdapterTuktukList(Context context) {
        this.mContext = context;
    }

    public void addTuktuk(BluetoothDevice bluetoothDevice) {
        for (int i = 0; i < mDevices.size(); i++) {
            if (bluetoothDevice.getAddress().equals(mDevices.get(i).getAddress())) {
                return;
            }
        }
        mDevices.add(bluetoothDevice);
        notifyDataSetChanged();
    }
    public void setCurMacAdderss(String strMacAddress) {
        this.strMacAddress = strMacAddress;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return mDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return mDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public void removeAll() {
        mDevices.clear();
        notifyDataSetChanged();
    }

    private class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_ip;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_femto_list, null);
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.femto_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.femto_name);
            viewHolder.tv_ip = (TextView) convertView.findViewById(R.id.femto_ip);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.tv_name.setText(mDevices.get(position).getName());
        HashMap<String, Socket> socketHashMap = SocketTCP.getSockets();
        viewHolder.iv_icon.setBackgroundResource(R.mipmap.femto_disconnect_icon);
        viewHolder.tv_ip.setText(mDevices.get(position).getAddress());
        if (!TextUtils.isEmpty(strMacAddress) && mDevices.get(position).getAddress().equals(strMacAddress)) {
            viewHolder.tv_ip.setTextColor(0x00FF00);
        }
        return convertView;
    }
}
