package com.bravo.femto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bravo.FemtoController.BaseActivity;
import com.bravo.FemtoController.ProxyApplication;
import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.adapters.ThreeLevelExpandableAdapter;
import com.bravo.custom_view.CustomProgressDialog;
import com.bravo.custom_view.OneBtnHintDialog;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.custom_view.RecordOnLongClick;
import com.bravo.custom_view.TwoBtnHintDialog;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.fragments.SerializableHandler;
import com.bravo.parse_generate_xml.Status;
import com.bravo.parse_generate_xml.cell_scan.CellScanCell;
import com.bravo.parse_generate_xml.cell_scan.CellScanNotif;
import com.bravo.parse_generate_xml.cell_scan.CellScanReq;
import com.bravo.parse_generate_xml.cell_scan.CellScanSibCell;
import com.bravo.utils.Logs;
import com.bravo.utils.SharePreferenceUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.bravo.femto.BcastCommonApi.changeBand;
import static com.bravo.femto.BcastCommonApi.checkChannels;
import static com.bravo.femto.BcastCommonApi.sendTcpMsg;

/**
 * Created by Jack.liao on 2016/11/4.
 */

public class FragmentCellScan extends RevealAnimationBaseFragment {
    private final String TAG = "FragmentCellScan";
    private final int CELL_SCAN_FAIL = 2;
    //cell scan
    private Spinner spinner_afc;
    private EditText edit_channels;
    private TextView tv_threshold;
    private SeekBar sb_threshold;
    private AdapterView.OnItemClickListener mOnItemClickListener;
    private AdapterCell adapterCell;
    private int iCurBtsState;
    private List<CellScanCell> cells = new ArrayList<>();
    private String strCurTech;
    private String strControlIP;
    private Spinner spinner_port;
    private String strBand;
    private Spinner spinner_band;
    private TextView tv_band;
    private int THRESHOLD_MAX  = 140;//都是负数LTE=140-44 UMTS/GSM=115-25
    private CustomProgressDialog CellScanDialog;
    private boolean bMultiBand;
    private String FemtoSn;
    private OneBtnHintDialog hintDialog;
    @Override
    public void onResume() {
        super.onResume();
        loadData();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity)context).getSettingBtn().setImageResource(R.drawable.btn_scan_selector);
        ((RevealAnimationActivity)context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                if (CheckObserverMode(strControlIP) && !ButtonUtils.isFastDoubleClick()){
                    CellScan();
                }
                InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0); //强制隐藏键盘
                super.recordOnClick(v, "Cell Scan Event");
            }
        });
        ((RevealAnimationActivity)context).getSettingBtn().setOnLongClickListener(new RecordOnLongClick() {
            @Override
            public void recordOnLongClick(View view, String strMsg) {
                Intent intent = new Intent(context, SnifferHistoryActivity.class);
                ((BaseActivity)context).startActivityWithAnimation(intent);
                super.recordOnLongClick(view, "Cell Scan Long Click Event ");
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_cellscan);
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
        //band
        tv_band = (TextView) contentView.findViewById(R.id.band);
        spinner_band = (Spinner) contentView.findViewById(R.id.spinner_band);
        //spinner afc
        spinner_afc = (Spinner) contentView.findViewById(R.id.spinner_afc);
        spinner_port = (Spinner) contentView.findViewById(R.id.spinner_port);
        edit_channels = (EditText) contentView.findViewById(R.id.cellscan_channels);
        //threshold
        tv_threshold = (TextView) contentView.findViewById(R.id.tx_threshold);
        sb_threshold = (SeekBar) contentView.findViewById(R.id.sb_threshold);
        sb_threshold.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_threshold.setText(progress - THRESHOLD_MAX + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        initDialog();
    }

    private void initDialog() {
        CellScanDialog = new CustomProgressDialog(context, R.style.dialog_style);
        hintDialog = new OneBtnHintDialog(context, R.style.dialog_style);
        CellScanDialog.setCancelable(false);
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
        public CellScanCell getGroup(int groupPosition) {
            return cells.get(groupPosition);
        }

        @Override
        public CellScanSibCell getChild(int groupPosition, int childPosition) {
            return cells.get(groupPosition).getSibCells().get(childPosition);
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
            CellScanCell cellScanCell = getGroup(groupPosition);
            convertView = LayoutInflater.from(mContext).inflate(R.layout.cell_list_item, null);
            ((TextView) convertView.findViewById(R.id.listchannel)).setText(cellScanCell.getChannel());
            ((TextView) convertView.findViewById(R.id.listcid)).setText(cellScanCell.getCid());
            ((TextView) convertView.findViewById(R.id.listmcc)).setText(cellScanCell.getMcc());
            ((TextView) convertView.findViewById(R.id.listmnc)).setText(cellScanCell.getMnc());

            if (strCurTech.equals("4G")) {
                (convertView.findViewById(R.id.layout_lte)).setVisibility(View.VISIBLE);
                ((TextView) convertView.findViewById(R.id.lte_tac)).setText(cellScanCell.getTechSpecific().getTac());
                ((TextView) convertView.findViewById(R.id.lte_pci)).setText(cellScanCell.getTechSpecific().getPci());
                ((TextView) convertView.findViewById(R.id.lte_rsrp)).setText(cellScanCell.getTechSpecific().getRsrp());
                ((TextView) convertView.findViewById(R.id.lte_bandwidth)).setText(cellScanCell.getTechSpecific().getBandwidth());
            } else if (strCurTech.equals("3G")) {
                (convertView.findViewById(R.id.layout_umts)).setVisibility(View.VISIBLE);
                ((TextView) convertView.findViewById(R.id.umts_lac)).setText(cellScanCell.getTechSpecific().getLac());
                ((TextView) convertView.findViewById(R.id.umts_psc)).setText(cellScanCell.getTechSpecific().getPsc());
                ((TextView) convertView.findViewById(R.id.umts_rscp)).setText(cellScanCell.getTechSpecific().getRscp());
            } else {
                (convertView.findViewById(R.id.layout_gsm)).setVisibility(View.VISIBLE);
                ((TextView) convertView.findViewById(R.id.gsm_rssi)).setText(cellScanCell.getTechSpecific().getRssi());
                ((TextView) convertView.findViewById(R.id.gsm_lac)).setText(cellScanCell.getTechSpecific().getLac());
                ((TextView) convertView.findViewById(R.id.gsm_bsic)).setText(cellScanCell.getTechSpecific().getBsic());
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

    public void CellScan() {
        if (iCurBtsState == 0) {
            hintDialog(true, true, "Warning", "System Error");
        } else if (iCurBtsState == 3 || iCurBtsState == 4) {
            hintDialog(true, true, "Warning", "BTS = " + iCurBtsState);
        } else if(strBand.equals("0")) {
            hintDialog(true, true, "Warning", "Band =  N/A");
        } else if (((!strBand.equals("255") && !bMultiBand && checkChannels(edit_channels, strBand, strCurTech)) ||
                        ((strBand.equals("255") || bMultiBand) && checkChannels(edit_channels, spinner_band.getSelectedItem().toString(), strCurTech)))) {
            String checkBand;
            if (strBand.equals("255") || bMultiBand) {
                checkBand = spinner_band.getSelectedItem().toString();
            } else {
                checkBand = strBand;
            }
            if (BcastCommonApi.checkBandClash(context, checkBand)) {
                TwoBtnHintDialog dialog = new TwoBtnHintDialog(context, R.style.dialog_style);
                dialog.setOnBtnClickListener(new TwoBtnHintDialog.OnBtnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(v.getId() == R.id.two_btn_dialog_right_btn) {
                            snifferMsg();
                        }
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
                dialog.setTitle("Hint");
                dialog.setContent("Band Conflict");
                dialog.setRightBtnContent("Continue");
            } else {
                snifferMsg();
                saveData();
            }
        }
    }

    private void snifferMsg() {
        try {
            cells.clear();
            adapterCell.UpdateList();
            CellScanReq cellScanReq = new CellScanReq();
            if (strBand.equals("255")  || bMultiBand) {
                cellScanReq.setBand(spinner_band.getSelectedItem().toString());
            } else {
                cellScanReq.setBand(strBand);
            }
            cellScanReq.setChannels(edit_channels.getText().toString());
            cellScanReq.setAntennaPort(String.valueOf(spinner_port.getSelectedItemPosition() + 1));
            cellScanReq.setThreshold(tv_threshold.getText().toString());
            cellScanReq.setAfc(spinner_afc.getSelectedItem().toString());
            sendTcpMsg(context, CellScanReq.toXml(cellScanReq));
            ScanDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new SerializableHandler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CELL_SCAN_FAIL:
                    // CustomToast.showToast(context, "Cell Scan Failure", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }

        }
    };
    private void ScanDialog() {
        if (!CellScanDialog.isShowing()) {
            CellScanDialog.show();
            CellScanDialog.setTitle("Cell Scanning");
            CellScanDialog.setContent("Please wait...!");
            Thread thread = new Thread() {
                public void run() {
                    try {
                        //bDialogState = true;
                        int iCount = 0;
                        while (iCurBtsState != 6 && iCurBtsState != 2 && iCount < 60) {
                            sleep(200);
                            iCount++;
                        }
                        if (iCount < 60) {
                            while ((iCurBtsState == 6 || iCurBtsState == 2)) {
                                sleep(1000);
                            }
                        } else if (iCount >= 60) {
                            Message msg = new Message();
                            msg.what = CELL_SCAN_FAIL;
                            handler.sendMessage(msg);
                        }
                        CellScanDialog.dismiss();
                    } catch (InterruptedException e) {
                        // TODO 自动生成的 catch 块
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
    }

    @Override
    public void onPause() {
        saveData();
        super.onPause();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    private void saveData() {
        SharedPreferences preferences = context.getSharedPreferences("cell_scan",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("channels" + strCurTech, edit_channels.getText().toString());
        editor.putInt("afc" + strCurTech, spinner_afc.getSelectedItemPosition());
        editor.putInt("port" + strCurTech,spinner_port.getSelectedItemPosition());
        editor.putInt("threshold" + strCurTech, sb_threshold.getProgress());
        if (bMultiBand) {
            editor.putInt("multiband_item" + FemtoSn, spinner_band.getSelectedItemPosition());
        }
        editor.commit();
    }

    private void loadData() {
        iCurBtsState = Integer.parseInt(SharePreferenceUtils.getInstance(context).getString("status_notif_bts" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "1"));
        strCurTech = SharePreferenceUtils.getInstance(context).getString("status_notif_tech" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        strControlIP = SharePreferenceUtils.getInstance(context).getString("status_notif_controller_ip" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        strBand = SharePreferenceUtils.getInstance(context).getString("status_notif_band" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "0");
        FemtoSn = SharePreferenceUtils.getInstance(context).getString("status_notif_sn" + ((ProxyApplication) context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication) context.getApplicationContext()).getiTcpPort(), "");
        bMultiBand = SharePreferenceUtils.getInstance(context).getBoolean("multi_band_status" + FemtoSn, false);
        //band
        updateBand();
        if (TextUtils.isEmpty(strCurTech)) {
            return;
        }
        if (!strCurTech.equals("4G")) {
            THRESHOLD_MAX = 115;
            sb_threshold.setMax(90);
        }
        SharedPreferences sp = context.getSharedPreferences("cell_scan", MODE_PRIVATE);
        edit_channels.setText(sp.getString("channels" + strCurTech, ""));
        spinner_afc.setSelection(sp.getInt("afc" + strCurTech, 0));
        spinner_port.setSelection(sp.getInt("port" + strCurTech, 0));
        sb_threshold.setProgress(sp.getInt("threshold" + strCurTech, 0));
        tv_threshold.setText(sb_threshold.getProgress() - THRESHOLD_MAX + "");
    }
    private void updateBand() {
        if (bMultiBand) {
            tv_band.setVisibility(View.GONE);
            contentView.findViewById(R.id.layout_band).setVisibility(View.VISIBLE);
            String band[] = SharePreferenceUtils.getInstance(context).getString("multi_band_info" + FemtoSn, "N/A").split(",");
            ArrayList<String> list = new ArrayList<String>();
            for(int i = 0; i < band.length; i++){
                list.add(band[i]);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_band.setAdapter(adapter);
            SharedPreferences sp = context.getSharedPreferences("cell_scan", MODE_PRIVATE);
            spinner_band.setSelection(sp.getInt("multiband_item" + FemtoSn, 0));
        } else {
            if (strBand.equals("255") && !TextUtils.isEmpty(strCurTech)) {
                int iTech = 0;
                if (strCurTech.equals("4G")) {
                    iTech = 3;
                } else if (strCurTech.equals("3G")) {
                    iTech = 2;
                } else if (strCurTech.equals("2G")) {
                    iTech = 1;
                }
                contentView.findViewById(R.id.layout_band).setVisibility(View.VISIBLE);
                tv_band.setVisibility(View.GONE);
                changeBand(context, iTech, (LinearLayout) contentView.findViewById(R.id.layout_band), spinner_band);
            } else {
                tv_band.setText(strBand);
                tv_band.setVisibility(View.VISIBLE);
                spinner_band.setVisibility(View.GONE);
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void CellScanNotif(CellScanNotif scn){
        if (iCurBtsState != 6) {
            iCurBtsState = 6;
        }
//         CustomToast.showToast(context, "Cell Scan Notif Channel=" + scn.getChannel() + ",Total=" + scn.getCells().size());
        CellScanDialog.setContent("Please wait...!" + " Channel=" + scn.getChannel() + ",Total=" + scn.getCells().size());
        for (int i = 0; i < scn.getCells().size(); i++) {
            scn.getCells().get(i).setChannel(scn.getChannel());
            cells.add(scn.getCells().get(i));
        }
        adapterCell.UpdateList();
    }

    private int iBtsFiveCount = 0;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void StatusNotif(Status s) {
        iCurBtsState = Integer.parseInt(s.getBtsState());
        strControlIP = s.getControllerClient();
        strBand = s.getBand();
        if (!TextUtils.isEmpty(s.getTech()) && !s.getTech().equals(strCurTech)) {
            strCurTech = s.getTech();
            loadData();
        } else if (!bMultiBand){
            updateBand();
        }
        if (iCurBtsState == 5) {
            iBtsFiveCount++;
            if (iBtsFiveCount >= 3) {
                hintDialog(true, true, "Warning", "Syatem Error");
            }
            return;
        }
        iBtsFiveCount = 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (CellScanDialog.isShowing()) {
            CellScanDialog.dismiss();
        }
        if (hintDialog.isShowing()) {
            hintDialog.dismiss();
        }
    }

    private void hintDialog(boolean setCancelable, boolean setCanceledOnTouchOutside, String strTitle, String strContent) {
        hintDialog.setCancelable(setCancelable);
        hintDialog.setCanceledOnTouchOutside(setCanceledOnTouchOutside);
        if (!hintDialog.isShowing()) {
            hintDialog.show();
            hintDialog.setTitle(strTitle);
            hintDialog.setContent(strContent);
        }
    }
}
