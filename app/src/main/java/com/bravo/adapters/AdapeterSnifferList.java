package com.bravo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.R;
import com.bravo.database.SnifferHistory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapeterSnifferList extends BaseAdapter {
    private final String TAG = "AdapeterSnifferList";
    private Context mContext;
    private List<SnifferHistory> snifferHistories;

    public AdapeterSnifferList(Context mContext) {
        this.mContext = mContext;
        snifferHistories = ProxyApplication.getDaoSession().getSnifferHistoryDao().queryBuilder().build().list();
    }

    public void deleteAll() {
        ProxyApplication.getDaoSession().getSnifferHistoryDao().deleteAll();
        snifferHistories = ProxyApplication.getDaoSession().getSnifferHistoryDao().queryBuilder().build().list();
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return snifferHistories.size();
    }

    @Override
    public Object getItem(int position) {
        return snifferHistories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView tv_channel;
        TextView tv_band;
        TextView tv_tech;
        TextView tv_cid;
        TextView tv_time;
        TextView tv_rncid;
        TextView tv_mcc;
        TextView tv_mnc;
        TextView tv_tac;
        TextView tv_pci;
        TextView tv_rsrp;
        TextView tv_bandwidth;
        TextView tv_umts_lac;
        TextView tv_psc;
        TextView tv_rscp;
        TextView tv_rssi;
        TextView tv_gsm_lac;
        TextView tv_bsic;
        LinearLayout layout_lte;
        LinearLayout layout_umts;
        LinearLayout layout_gsm;
        LinearLayout layout_techband;
        LinearLayout layout_timerncid;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.cell_list_item, null);
            viewHolder.tv_channel = (TextView) convertView.findViewById(R.id.listchannel);
            viewHolder.tv_band = (TextView) convertView.findViewById(R.id.sniffer_band);
            viewHolder.tv_tech = (TextView) convertView.findViewById(R.id.sniffer_tech);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.sniffer_time);
            viewHolder.tv_rncid = (TextView) convertView.findViewById(R.id.sniffer_rncid);
            viewHolder.tv_cid = (TextView) convertView.findViewById(R.id.listcid);
            viewHolder.tv_mcc = (TextView) convertView.findViewById(R.id.listmcc);
            viewHolder.tv_mnc = (TextView) convertView.findViewById(R.id.listmnc);
            viewHolder.tv_tac = (TextView) convertView.findViewById(R.id.lte_tac);
            viewHolder.tv_pci = (TextView) convertView.findViewById(R.id.lte_pci);
            viewHolder.tv_rsrp = (TextView) convertView.findViewById(R.id.lte_rsrp);
            viewHolder.tv_bandwidth = (TextView) convertView.findViewById(R.id.lte_bandwidth);
            viewHolder.tv_umts_lac = (TextView) convertView.findViewById(R.id.umts_lac);
            viewHolder.tv_psc = (TextView) convertView.findViewById(R.id.umts_psc);
            viewHolder.tv_rscp = (TextView) convertView.findViewById(R.id.umts_rscp);
            viewHolder.tv_rssi = (TextView) convertView.findViewById(R.id.gsm_rssi);
            viewHolder.tv_gsm_lac = (TextView) convertView.findViewById(R.id.gsm_lac);
            viewHolder.tv_bsic = (TextView) convertView.findViewById(R.id.gsm_bsic);
            viewHolder.layout_lte = (LinearLayout) convertView.findViewById(R.id.layout_lte);
            viewHolder.layout_umts = (LinearLayout) convertView.findViewById(R.id.layout_umts);
            viewHolder.layout_gsm = (LinearLayout) convertView.findViewById(R.id.layout_gsm);
            viewHolder.layout_techband = (LinearLayout) convertView.findViewById(R.id.tech_band_layout);
            viewHolder.layout_timerncid = (LinearLayout) convertView.findViewById(R.id.time_rncid_layout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        SnifferHistory snifferHisroty = snifferHistories.get(position);
        viewHolder.layout_timerncid.setVisibility(View.VISIBLE);
        viewHolder.layout_techband.setVisibility(View.VISIBLE);
        viewHolder.tv_channel.setText(snifferHisroty.getChannel());
        viewHolder.tv_rncid.setText(snifferHisroty.getRncid());
        viewHolder.tv_band.setText(snifferHisroty.getBand());
        viewHolder.tv_tech.setText(snifferHisroty.getTech());
        viewHolder.tv_cid.setText(snifferHisroty.getCid());
        viewHolder.tv_mcc.setText(snifferHisroty.getMcc());
        viewHolder.tv_mnc.setText(snifferHisroty.getMnc());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
        viewHolder.tv_time.setText(String.valueOf(formatter.format(new Date(snifferHisroty.getTime()))));

        switch (snifferHisroty.getTech()) {
            case "4G":
                viewHolder.layout_lte.setVisibility(View.VISIBLE);
                viewHolder.tv_tac.setText(snifferHisroty.getTac());
                viewHolder.tv_pci.setText(snifferHisroty.getPci());
                viewHolder.tv_rsrp.setText(snifferHisroty.getRsrp());
                viewHolder.tv_bandwidth.setText(snifferHisroty.getBandwidth());
                break;
            case "3G":
                viewHolder.layout_umts.setVisibility(View.VISIBLE);
                viewHolder.tv_umts_lac.setText(snifferHisroty.getLac());
                viewHolder.tv_psc.setText(snifferHisroty.getPsc());
                viewHolder.tv_rscp.setText(snifferHisroty.getRscp());
                break;
            case "2G":
                viewHolder.layout_gsm.setVisibility(View.VISIBLE);
                viewHolder.tv_rssi.setText(snifferHisroty.getRssi());
                viewHolder.tv_gsm_lac.setText(snifferHisroty.getLac());
                viewHolder.tv_bsic.setText(snifferHisroty.getBsic());
                break;
            default:
                break;
        }
        return convertView;
    }
}
