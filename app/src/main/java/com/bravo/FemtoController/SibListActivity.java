package com.bravo.FemtoController;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.parse_generate_xml.cell_scan.CellScanSibCell;
import com.bravo.utils.SharePreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class SibListActivity extends BaseActivity {
    private final static String TAG = "SibListActivity";
    private ExpandableListView expandListView;
    private SibListAdapter sibListAdapter;
    private List<CellScanSibCell> sibCells;
    private final int GSM_TECH = 0;
    private final int UMTS_LTE_TECH = 1;
    private String strCurTech;
    private int iTech;
    @Override
    protected void initView() {
        strCurTech = SharePreferenceUtils.getInstance(this).getString("status_notif_tech" + ((ProxyApplication) getApplicationContext()).getCurSocketAddress() + ((ProxyApplication) getApplicationContext()).getiTcpPort(), "");
        if (!TextUtils.isEmpty(strCurTech)) {
            initlist();
        } else {
            sibCells = new ArrayList<>();
            iTech = UMTS_LTE_TECH;
        }
        setContentView(R.layout.activity_sib_list);
        expandListView = (ExpandableListView) this.findViewById(R.id.sib_list);
        sibListAdapter = new SibListAdapter();
        expandListView.setGroupIndicator(null);
        expandListView.setAdapter(sibListAdapter);
    }
    @Override
    protected void initData(Bundle savedInstanceState) {

    }
    private void initlist() {
        sibCells = (ArrayList) getIntent().getExtras().getSerializable("siblist");
        if (strCurTech.equals("2G")) {
            iTech = GSM_TECH;
        } else {
            iTech = UMTS_LTE_TECH;
        }
    }

    class SibListAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return sibCells.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return iTech;
        }

        @Override
        public CellScanSibCell getGroup(int groupPosition) {
            return sibCells.get(groupPosition);
        }

        @Override
        public CellScanSibCell getChild(int groupPosition, int childPosition) {
            return sibCells.get(groupPosition);
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
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView = new TextView(SibListActivity.this);
            textView.setWidth(parent.getWidth());
            textView.setHeight(100);
            textView.setTextSize(20);
            CellScanSibCell cellScanSibCell = getGroup(groupPosition);
            textView.setText(String.valueOf(groupPosition+1));
            return textView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.cell_list_item,
                    null);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

    }
}

