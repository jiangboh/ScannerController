package com.bravo.status;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.adapters.ThreeLevelExpandableAdapter;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.parse_generate_xml.ex_status.NeighborListCell;
import com.bravo.parse_generate_xml.ex_status.SibCell;
import com.bravo.parse_generate_xml.ex_status.StatusNotif;
import com.bravo.parse_generate_xml.ex_status.StatusReq;
import com.bravo.utils.Logs;
import com.bravo.utils.SharePreferenceUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.bravo.femto.BcastCommonApi.sendUdpMsg;

public class Sniffer_Fragment extends RevealAnimationBaseFragment {
    private final String TAG = "SNIFFER_FRAGMENT";
    private String strCurTech;
    private List<NeighborListCell> cells = new ArrayList<>();
    private AdapterView.OnItemClickListener mOnItemClickListener;
    private AdapterCell adapterCell;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sniffer);
    }

    @Override
    public void initView() {
        mOnItemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Logs.d(TAG, "position:" + position);
            }
        };
        ExpandableListView listView = (ExpandableListView) contentView.findViewById(R.id.celllist);
        listView.setGroupIndicator(null);
        adapterCell = new AdapterCell(context, mOnItemClickListener);
        listView.setAdapter(adapterCell);
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public void onResume() {
        super.onResume();
        //send request
        StatusReq statusReq = new StatusReq();
        statusReq.setType("16");//sniffer status type=16
        sendUdpMsg(context, StatusReq.toXml(statusReq));
        //cur tech

        strCurTech = SharePreferenceUtils.getInstance(context).getString("status_notif_tech" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");


        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity)context).getSettingBtn().setImageResource(R.drawable.btn_refresh_selector);
        ((RevealAnimationActivity)context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                cells.clear();
                adapterCell.UpdateList();
                StatusReq statusReq = new StatusReq();
                statusReq.setType("16");//sniffer status type=1
                sendUdpMsg(context, StatusReq.toXml(statusReq));
                super.recordOnClick(v, "Refresh Sniffer Event");
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ExStatusNotif(StatusNotif s) {
        Logs.d(TAG, "ExStatusNotif sniffer");
        List<NeighborListCell> neighborListCellList = s.getSniff();
        if (neighborListCellList != null) {
            if (neighborListCellList.size() == 0) {
                Toast.makeText(context, "Neighbor info is null", Toast.LENGTH_LONG).show();
            } else {
                for (int i = 0; i < neighborListCellList.size(); i++) {
                    neighborListCellList.get(i).setChannel(neighborListCellList.get(i).getChannel());
                    cells.add(neighborListCellList.get(i));
                }
                adapterCell.UpdateList();
            }
        } else {
            Toast.makeText(context, "Neighbor info is null", Toast.LENGTH_LONG).show();
        }
    }

    class AdapterCell extends ThreeLevelExpandableAdapter {

        public AdapterCell(Context context, AdapterView.OnItemClickListener litener) {
            super(context, litener);
        }

        public void UpdateList() {
            notifyDataSetChanged();
        }

        @Override
        public int getGroupCount() {
            return cells.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 0;//cellScanNotif.getCells().get(groupPosition).getSibCells().size();
        }

        @Override
        public NeighborListCell getGroup(int groupPosition) {
            return cells.get(groupPosition);
        }

        @Override
        public SibCell getChild(int groupPosition, int childPosition) {
            return cells.get(groupPosition).getSibList().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            NeighborListCell neighborListCell = getGroup(groupPosition);
            convertView = LayoutInflater.from(mContext).inflate(R.layout.cell_list_item, null);
            ((TextView) convertView.findViewById(R.id.listchannel)).setText(neighborListCell.getChannel());
            ((TextView) convertView.findViewById(R.id.listcid)).setText(neighborListCell.getCid());
            ((TextView) convertView.findViewById(R.id.listmcc)).setText(neighborListCell.getMcc());
            ((TextView) convertView.findViewById(R.id.listmnc)).setText(neighborListCell.getMnc());

            if (strCurTech.equals("4G")) {
                (convertView.findViewById(R.id.layout_lte)).setVisibility(View.VISIBLE);
                ((TextView) convertView.findViewById(R.id.lte_tac)).setText(neighborListCell.getTechSpecific().getTac());
                ((TextView) convertView.findViewById(R.id.lte_pci)).setText(neighborListCell.getTechSpecific().getPci());
                ((TextView) convertView.findViewById(R.id.lte_rsrp)).setText(neighborListCell.getTechSpecific().getRsrp());
            } else if (strCurTech.equals("3G")) {
                (convertView.findViewById(R.id.layout_umts)).setVisibility(View.VISIBLE);
                ((TextView) convertView.findViewById(R.id.umts_lac)).setText(neighborListCell.getTechSpecific().getLac());
                ((TextView) convertView.findViewById(R.id.umts_psc)).setText(neighborListCell.getTechSpecific().getPsc());
                ((TextView) convertView.findViewById(R.id.umts_rscp)).setText(neighborListCell.getTechSpecific().getRscp());
            } else {
                (convertView.findViewById(R.id.layout_gsm)).setVisibility(View.VISIBLE);
                ((TextView) convertView.findViewById(R.id.gsm_rssi)).setText(neighborListCell.getTechSpecific().getRssi());
                ((TextView) convertView.findViewById(R.id.gsm_lac)).setText(neighborListCell.getTechSpecific().getLac());
                ((TextView) convertView.findViewById(R.id.gsm_bsic)).setText(neighborListCell.getTechSpecific().getBsic());
            }
            return convertView;
        }

        @Override
        public View getSecondLevleView(int firstLevelPosition,
                                       int secondLevelPosition, boolean isExpanded, View convertView,
                                       ViewGroup parent) {
            return null;
        }
    }
}
