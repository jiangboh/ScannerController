package com.bravo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.R;
import com.bravo.database.BcastHistory;
import com.bravo.database.User;
import com.bravo.database.UserDao;
import com.bravo.utils.Logs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterBcastHistory extends BaseAdapter {
    private String TAG = "AdapterBcastHistory";
    private Context context;
    private List<BcastHistory> bcastHistories = new ArrayList<>();
    public AdapterBcastHistory(Context context) {
        this.context =  context;
    }

    @Override
    public int getCount() {
        return bcastHistories.size();
    }

    @Override
    public Object getItem(int position) {
        return bcastHistories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setBcastHistories(List<BcastHistory> bcastHistories) {
        this.bcastHistories = bcastHistories;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView tvFemtoSN;
        TextView tvFemtoVer;
        TextView tvCid;
        TextView tvTech;
        TextView tvBand;
        TextView tvChannel;
        TextView tvStatrTime;
        TextView tvEndTime;
        TextView tvUserTotal;
        TextView tvTargetTotal;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_bcast_history, null);
            viewHolder.tvFemtoSN = (TextView) convertView.findViewById(R.id.femtosn);
            viewHolder.tvFemtoVer = (TextView) convertView.findViewById(R.id.femtover);
            viewHolder.tvCid = (TextView) convertView.findViewById(R.id.cid);
            viewHolder.tvTech = (TextView) convertView.findViewById(R.id.tech);
            viewHolder.tvBand = (TextView) convertView.findViewById(R.id.band);
            viewHolder.tvChannel = (TextView) convertView.findViewById(R.id.channel);
            viewHolder.tvStatrTime = (TextView) convertView.findViewById(R.id.starttime);
            viewHolder.tvEndTime = (TextView) convertView.findViewById(R.id.endtime);
            viewHolder.tvUserTotal = (TextView) convertView.findViewById(R.id.user_total);
            viewHolder.tvTargetTotal = (TextView) convertView.findViewById(R.id.target_total);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.tvFemtoSN.setText(bcastHistories.get(position).getFemtoSn());
        viewHolder.tvFemtoVer.setText(bcastHistories.get(position).getFemtoVer());
        viewHolder.tvChannel.setText(bcastHistories.get(position).getChannel());
        viewHolder.tvCid.setText(bcastHistories.get(position).getCid());
        viewHolder.tvTech.setText(bcastHistories.get(position).getTech());
        viewHolder.tvBand.setText(bcastHistories.get(position).getBand());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
        viewHolder.tvStatrTime.setText(formatter.format(new Date(bcastHistories.get(position).getStatrtime())));
        if (bcastHistories.get(position).getEndtime() != 0) {
            viewHolder.tvEndTime.setText(formatter.format(new Date(bcastHistories.get(position).getEndtime())));
        } else {
            viewHolder.tvEndTime.setText("null");
        }
        Logs.d(TAG, "lmj Load->" + bcastHistories.get(position).toString());
        List<User> users = null;
        String Unique = bcastHistories.get(position).getFemtoSn() + bcastHistories.get(position).getBand() + bcastHistories.get(position).getTech() + /*bcastHistories.get(position).getChannel() + */bcastHistories.get(position).getCid();
        if (bcastHistories.get(position).getEndtime() != null && bcastHistories.get(position).getStatrtime() < bcastHistories.get(position).getEndtime()) {
            users = ProxyApplication.getDaoSession().getUserDao().queryBuilder().where(UserDao.Properties.Unique.eq(Unique),
                    UserDao.Properties.ConnTime.gt(bcastHistories.get(position).getStatrtime()),
                    UserDao.Properties.ConnTime.lt(bcastHistories.get(position).getEndtime())).build().list();
            viewHolder.tvUserTotal.setText(users.size() + "");
            users = ProxyApplication.getDaoSession().getUserDao().queryBuilder().where(UserDao.Properties.Unique.eq(Unique),
                    UserDao.Properties.ConnTime.gt(bcastHistories.get(position).getStatrtime()),
                    UserDao.Properties.ConnTime.lt(bcastHistories.get(position).getEndtime()),
                    UserDao.Properties.IAuth.notEq(0)).build().list();
            viewHolder.tvTargetTotal.setText(users.size() + "");
        } else {
            users = ProxyApplication.getDaoSession().getUserDao().queryBuilder().where(UserDao.Properties.Unique.eq(Unique),
                    UserDao.Properties.ConnTime.gt(bcastHistories.get(position).getStatrtime())).build().list();
            viewHolder.tvUserTotal.setText(users.size() + "");
            users = ProxyApplication.getDaoSession().getUserDao().queryBuilder().where(UserDao.Properties.Unique.eq(Unique),
                    UserDao.Properties.ConnTime.gt(bcastHistories.get(position).getStatrtime()),
                    UserDao.Properties.IAuth.notEq(0)).build().list();
            viewHolder.tvTargetTotal.setText(users.size() + "");
        }

        return convertView;
    }
}
