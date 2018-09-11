package com.bravo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bravo.FemtoController.FemtoListActivity;
import com.bravo.FemtoController.ProxyApplication;
import com.bravo.R;
import com.bravo.data_ben.FemtoDataStruct;
import com.bravo.database.UserDao;
import com.bravo.socket.SocketTCP;
import com.bravo.socket_service.EventBusMsgCloseSocket;
import com.bravo.utils.SharePreferenceUtils;

import org.greenrobot.eventbus.EventBus;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jack.liao on 2016/9/14.
 */
public class AdapterFemtoList extends BaseAdapter {
    private final static int FEMTO_LIST_MAX_NUM = 10;
    private final static String TAG = "AdapterFemtoList";
    private Context mContext;
    private ArrayList<FemtoDataStruct> femtoDataStructArrayList = new ArrayList<>();
//    private List<Map<String, String>> list = new ArrayList<Map<String, String>>();

    public AdapterFemtoList(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return femtoDataStructArrayList.size();
    }

    @Override
    public FemtoDataStruct getItem(int position) {
        return femtoDataStructArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<FemtoDataStruct> getList() {
        return femtoDataStructArrayList;
    }

    public void addFemto(FemtoDataStruct femtoDataStruct) {
        for (int i = 0; i < femtoDataStructArrayList.size(); i++) {
            if (femtoDataStruct.getIPAddress().equals(femtoDataStructArrayList.get(i).getIPAddress()) &&
                femtoDataStruct.getiUdpPort() == femtoDataStructArrayList.get(i).getiUdpPort()) {
                return;
            }
        }
        femtoDataStructArrayList.add(femtoDataStruct);
    }

    public void UpdateUserName(int position, String UserName) {
        if (femtoDataStructArrayList.size() >= position) {
            femtoDataStructArrayList.get(position).setSSID(UserName);
            notifyDataSetChanged();
        }
    }

    public void deleteFemto(int iIndex) {
        femtoDataStructArrayList.remove(iIndex);
        notifyDataSetChanged();
    }

    public void removeAll() {
        femtoDataStructArrayList.clear();
        notifyDataSetChanged();
    }
    public void updateFemtoList() {
        notifyDataSetChanged();
    }

    private class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_delete;
        TextView tv_ip;
        LinearLayout layout_target;
        LinearLayout layout_mobile;
        TextView tv_target;
        TextView tv_mobile;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_femto_list, null);
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.femto_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.femto_name);
            viewHolder.tv_delete = (TextView) convertView.findViewById(R.id.pixcell_delete);
            viewHolder.tv_ip = (TextView) convertView.findViewById(R.id.femto_ip);
            viewHolder.layout_target = (LinearLayout) convertView.findViewById(R.id.layout_target);
            viewHolder.layout_mobile = (LinearLayout) convertView.findViewById(R.id.layout_mobile);
            viewHolder.tv_target = (TextView) convertView.findViewById(R.id.targettotal);
            viewHolder.tv_mobile = (TextView) convertView.findViewById(R.id.mobiletotal);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.tv_name.setText(femtoDataStructArrayList.get(position).getSSID());
        HashMap<String, Socket> socketHashMap = SocketTCP.getSockets();
        Socket socket = socketHashMap.get(femtoDataStructArrayList.get(position).getIPAddress() + femtoDataStructArrayList.get(position).getiTcpPort());
        if (socket == null) {
            viewHolder.layout_target.setVisibility(View.GONE);
            viewHolder.layout_mobile.setVisibility(View.GONE);
            viewHolder.tv_ip.setText(femtoDataStructArrayList.get(position).getIPAddress());
            viewHolder.iv_icon.setBackgroundResource(R.mipmap.femto_disconnect_icon);
        } else {
            viewHolder.iv_icon.setBackgroundResource(R.mipmap.femto_connect_icon);
            String strBts = SharePreferenceUtils.getInstance(mContext).getString("status_notif_bts" + socket.getInetAddress().getHostAddress()+ socket.getPort(), "1");
            String strFemtoInfo = "Tech:" + SharePreferenceUtils.getInstance(mContext).getString("status_notif_tech" + socket.getInetAddress().getHostAddress()+ socket.getPort(), "") +
                    " , Band:" + SharePreferenceUtils.getInstance(mContext).getString("status_notif_band" + socket.getInetAddress().getHostAddress()+ socket.getPort(), "") +
                    " , Channel:" + SharePreferenceUtils.getInstance(mContext).getString("status_notif_channel" + socket.getInetAddress().getHostAddress()+ socket.getPort(), "");
            if (strBts.equals("3")) {
                viewHolder.layout_target.setVisibility(View.VISIBLE);
                viewHolder.layout_mobile.setVisibility(View.VISIBLE);
                Long starttime = SharePreferenceUtils.getInstance(mContext).getLong("status_notif_starttime" +
                    socket.getInetAddress().getHostAddress()+ socket.getPort(), System.currentTimeMillis());
                viewHolder.tv_target.setText("0");
                viewHolder.tv_mobile.setText(ProxyApplication.getDaoSession().getUserDao().queryBuilder().where(UserDao.Properties.Unique.eq(SharePreferenceUtils.getInstance(mContext).getString("status_notif_unique" + socket.getInetAddress().getHostAddress()+ socket.getPort(), "")),
                        UserDao.Properties.ConnTime.gt(starttime)).build().list().size() + "");
            } else if (strBts.equals("4")) {
                viewHolder.layout_target.setVisibility(View.VISIBLE);
                viewHolder.layout_mobile.setVisibility(View.VISIBLE);
                Long starttime = SharePreferenceUtils.getInstance(mContext).getLong("status_notif_starttime" +
                        socket.getInetAddress().getHostAddress()+ socket.getPort(), System.currentTimeMillis());
                viewHolder.tv_target.setText(ProxyApplication.getDaoSession().getUserDao().queryBuilder().where(UserDao.Properties.Unique.eq(SharePreferenceUtils.getInstance(mContext).getString("status_notif_unique" + socket.getInetAddress().getHostAddress()+ socket.getPort(), "")),
                        UserDao.Properties.ConnTime.gt(starttime), UserDao.Properties.IAuth.eq("1")).build().list().size() + "");
                viewHolder.tv_mobile.setText(ProxyApplication.getDaoSession().getUserDao().queryBuilder().where(UserDao.Properties.Unique.eq(SharePreferenceUtils.getInstance(mContext).getString("status_notif_unique" + socket.getInetAddress().getHostAddress()+ socket.getPort(), "")),
                        UserDao.Properties.ConnTime.gt(starttime)).build().list().size() + "");
            } else {
                viewHolder.layout_target.setVisibility(View.GONE);
                viewHolder.layout_mobile.setVisibility(View.GONE);
            }
            viewHolder.tv_ip.setText(strFemtoInfo);
        }
        viewHolder.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                pixcelllist.remove(position);
                notifyDataSetChanged();
                FemtoListActivity femtoListActivity = (FemtoListActivity) mContext;
                femtoListActivity.userDefineListView.turnToNormal();
                EventBus.getDefault().post(new EventBusMsgCloseSocket(femtoDataStructArrayList.get(position).getIPAddress(),
                        femtoDataStructArrayList.get(position).getiUdpPort()));
            }
        });
        return convertView;
    }
}