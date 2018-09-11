package com.bravo.femto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.IdRes;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bravo.FemtoController.BaseActivity;
import com.bravo.FemtoController.ProxyApplication;
import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.adapters.AdapterConnTarget;
import com.bravo.adapters.ThreeLevelExpandableAdapter;
import com.bravo.custom_view.CustomProgressDialog;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.OneBtnHintDialog;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.custom_view.RecordOnItemClick;
import com.bravo.custom_view.RecordOnItemLongClick;
import com.bravo.custom_view.TwoBtnHintDialog;
import com.bravo.data_ben.TargetDataStruct;
import com.bravo.database.TargetUser;
import com.bravo.database.User;
import com.bravo.database.UserDao;
import com.bravo.dialog.DialogAddTarget;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.fragments.SerializableHandler;
import com.bravo.parse_generate_xml.Status;
import com.bravo.parse_generate_xml.TargetDetach;
import com.bravo.parse_generate_xml.bcast_end.BcastEndReq;
import com.bravo.parse_generate_xml.bcast_end.BcastEndRes;
import com.bravo.parse_generate_xml.bcast_start.BcastStartImplicitRedir;
import com.bravo.parse_generate_xml.bcast_start.BcastStartReq;
import com.bravo.parse_generate_xml.bcast_start.BcastStartRes;
import com.bravo.parse_generate_xml.cell_scan.CellScanCell;
import com.bravo.parse_generate_xml.cell_scan.CellScanNotif;
import com.bravo.parse_generate_xml.cell_scan.CellScanSibCell;
import com.bravo.parse_generate_xml.cell_scan.CellScanTechSpecific;
import com.bravo.parse_generate_xml.conn_request.ConnRequestNotif;
import com.bravo.parse_generate_xml.cs_fallback.CsFallbackRes;
import com.bravo.parse_generate_xml.target_attach.TargetAttach;
import com.bravo.parse_generate_xml.target_position.TargetPosition;
import com.bravo.utils.SharePreferenceUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.POWER_SERVICE;
import static com.bravo.femto.BcastCommonApi.changeBand;
import static com.bravo.femto.BcastCommonApi.checkChannel;
import static com.bravo.femto.BcastCommonApi.checkChannels;
import static com.bravo.femto.BcastCommonApi.checkTextEmpty;
import static com.bravo.femto.BcastCommonApi.loadCurTargetList;
import static com.bravo.femto.BcastCommonApi.saveBcastInfo;
import static com.bravo.femto.BcastCommonApi.secToTime;
import static com.bravo.femto.BcastCommonApi.sendFallBack;
import static com.bravo.femto.BcastCommonApi.sendTargetList;
import static com.bravo.femto.BcastCommonApi.sendTcpMsg;
import static com.bravo.femto.BcastCommonApi.switchTechView;
import static com.bravo.femto.BcastCommonApi.updateBcastInfoEndTime;

/**
 * Created by Jack.liao on 2016/11/4.
 */

public class FragmentBcastStart extends RevealAnimationBaseFragment {
    private final String TAG = "BcastStartFragment";
    private final int TIMER_SECOND = 1;
    private final int BCAST_END_TIMEOUT = 4;
    private final int BCAST_SCANNING = 0;
    private final int BCAST_START = 13;
    private final int BCAST_END = 14;
    private final int BCAST_FAILURE = 3;
    //bcast start
    private TextView tv_band;
    private EditText edit_cid;
    private EditText edit_rncid;
    private EditText edit_mcc;
    private EditText edit_mnc;
    private EditText edit_channels;
    private EditText edit_eplmn;
    private EditText edit_operatorname;
    private EditText edit_auto_switch_interval;
    //tech-specific;
    private EditText edit_techlac;
    private EditText edit_techpsc;
    private EditText edit_techtac;
    private EditText edit_techpci;
    private EditText edit_techncc;
    private EditText edit_techbcc;
    private Spinner spinner_bandwidth;

    private Spinner spinner_power_level;
    private Spinner spinner_port;
    private EditText edit_antennagain;
    private Spinner spinner_irtech;
    private Spinner spinner_irband;
    private EditText edit_irchannel;

    private boolean bDialogState = true;
    private AdapterConnTarget adapterConnTarget;
    private ListView TargetListView;
    private ExpandableListView CellListView;
    private AdapterCell adapterCell;
    private List<CellScanCell> cells = new ArrayList<>();
    //dialog
    private CustomProgressDialog proDialog;
    private OneBtnHintDialog hintDialog;
    private String strControlIP;
    private String strCurTech;
    private int iCurBtsState;
    private ArrayList<CellScanSibCell> sibCells = new ArrayList<>();
    private PowerManager.WakeLock mWakeLock;
    private String strBand;
    private Spinner spinner_band;
    //threshold
    private int THRESHOLD_MAX = 140;//都是负数LTE=140-44 UMTS/GSM=115-25
    private SeekBar sb_threshold;
    private TextView tv_threshold;
    //cs fallback
    private Spinner spinner_cstech;
    private Spinner spinner_csband;
    private EditText edit_cschannel;
    private boolean bCSUpdateState = false;
    private boolean bIRUpdateStats = false;
    private RadioGroup radioGroup;
    private String FemtoSn = null;
    private boolean bMultiBand;
    private int iBcastTimer;//小区运行时间单位s
    private Timer bcastTimer;//小区定时器
    @Override
    public void onResume() {
        super.onResume();
        loadData();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        iCurBtsState = Integer.parseInt(SharePreferenceUtils.getInstance(context).getString("status_notif_bts" +
                ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() +
                ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "1"));
        if (iCurBtsState == 3 || iCurBtsState == 4) {
            BcastUIState(BCAST_END);
            loadCurTargetList(adapterConnTarget, context);
            loadCellList();
        } else if (iCurBtsState == 6) {
            BcastUIState(BCAST_SCANNING);
            BcastDialog();
        } else {
            BcastUIState(BCAST_START);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bcaststart);
        //屏幕不休眠
        PowerManager pManager = ((PowerManager) context.getSystemService(POWER_SERVICE));
        mWakeLock = pManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ON_AFTER_RELEASE, TAG);
        mWakeLock.acquire();
    }

    @Override
    public void initView() {
        spinner_band = (Spinner) contentView.findViewById(R.id.spinner_band);
        tv_band = (TextView) contentView.findViewById(R.id.band);
//        showFemtoState(strBand, iCurBtsState, contentView);
//        if (view == null) {
//        view = inflater.inflate(R.layout.fragment_bcaststart, null);
//        } else {
//            ViewGroup parent = (ViewGroup) view.getParent();
//            if (parent != null){
//                parent.removeView(view);
//            }
//            return view;
//        }

        //bcast start
        edit_cid = (EditText) contentView.findViewById(R.id.cid);
        edit_cid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    if (Integer.parseInt(s.toString()) > 268435455) {
                        edit_cid.setError("must<268435455");
                    }
                } else {
                    edit_cid.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_rncid = (EditText) contentView.findViewById(R.id.rncid);
        edit_mcc = (EditText) contentView.findViewById(R.id.mcc);
        edit_mnc = (EditText) contentView.findViewById(R.id.mnc);
        edit_channels = (EditText) contentView.findViewById(R.id.bcast_channels);
        edit_eplmn = (EditText) contentView.findViewById(R.id.eplmn);
        edit_operatorname = (EditText) contentView.findViewById(R.id.operator_name);
        edit_auto_switch_interval = (EditText) contentView.findViewById(R.id.auto_switch_interval);
        edit_auto_switch_interval.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    if (Integer.parseInt(s.toString()) < 30) {
                        edit_auto_switch_interval.setError("must>=30");
                    }
                } else {
                    edit_auto_switch_interval.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //tech-specific;
        edit_techlac = (EditText) contentView.findViewById(R.id.lac);
        edit_techpsc = (EditText) contentView.findViewById(R.id.psc);
        edit_techtac = (EditText) contentView.findViewById(R.id.tac);
        edit_techtac.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    if (Integer.parseInt(s.toString()) > 65535) {
                        edit_techtac.setError("must<65536");
                    }
                } else {
                    edit_techtac.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_techpci = (EditText) contentView.findViewById(R.id.pci);
        edit_techpci.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    if (Integer.parseInt(s.toString()) > 503) {
                        edit_techpci.setError("must<504");
                    }
                } else {
                    edit_techpci.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_techncc = (EditText) contentView.findViewById(R.id.ncc);
        edit_techbcc = (EditText) contentView.findViewById(R.id.bcc);
        spinner_bandwidth = (Spinner) contentView.findViewById(R.id.bandwidth);
        //spinner power_level
        spinner_power_level = (Spinner) contentView.findViewById(R.id.power_level);
        //spinner port
        spinner_port = (Spinner) contentView.findViewById(R.id.spinner_port);
        edit_antennagain = (EditText) contentView.findViewById(R.id.antenna_gain);
        //implicit-redirect
        spinner_irtech = (Spinner) contentView.findViewById(R.id.ir_tech);
        spinner_irband = (Spinner) contentView.findViewById(R.id.ir_band);
        edit_irchannel = (EditText) contentView.findViewById(R.id.ir_channel);
        spinner_irtech.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (bIRUpdateStats) {
                    changeBand(context, position, (LinearLayout) contentView.findViewById(R.id.layout_ir_bc), spinner_irband);
                } else {
                    bIRUpdateStats = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //targetlist
        TargetListView = (ListView) contentView.findViewById(R.id.targetlist);
        adapterConnTarget = new AdapterConnTarget(context, (RadioButton) contentView.findViewById(R.id.target_tab));
        TargetListView.setAdapter(adapterConnTarget);
        TargetListView.setOnItemLongClickListener(new RecordOnItemLongClick() {
            @Override
            public void recordOnItemLongClick(AdapterView<?> parent, View view, final int position, long id, String strMsg) {
                final TargetDataStruct targetDataStruct = adapterConnTarget.getItem(position);
                if (targetDataStruct.getAuthState() == 0) {
                    //((RevealAnimationActivity)context).changeFragment(3, new Bundle());
                    DialogAddTarget dialogAddTarget = new DialogAddTarget(context, R.style.dialog_style, new DialogAddTarget.OnAddTargetDialogListener() {
                        @Override
                        public void AddTargetCallBack(TargetDataStruct addTarget) {
                            List<User> users = ProxyApplication.getDaoSession().getUserDao().queryBuilder().where(UserDao.Properties.Unique.eq(SharePreferenceUtils.getInstance(context).getString("status_notif_unique" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "")),
                                    UserDao.Properties.SrtImsi.eq(addTarget.getImsi())).build().list();
                            //判断用户是否存在数据库 存在改变状态
                            if (users.size() != 0) {
                                User updateData = users.get(0);
                                updateData.setIAuth(2);
                                ProxyApplication.getDaoSession().getUserDao().update(updateData);
                                adapterConnTarget.removeTarget(position);
                                targetDataStruct.setAuthState(2);
                                adapterConnTarget.addTarget(targetDataStruct);
                            }
                            //add target
                            TargetUser targetUser = new TargetUser(null, addTarget.getImsi(), addTarget.getImei(),
                                    addTarget.getName(), true, addTarget.getStrTech(),
                                    addTarget.getStrBand(), addTarget.getStrChannel(), addTarget.isbRedir());
                            ProxyApplication.getDaoSession().getTargetUserDao().insert(targetUser);
                            sendTargetList(context, strCurTech);
                        }
                    }, targetDataStruct.getImsi(), targetDataStruct.getImei());
                    dialogAddTarget.show();
                }
                super.recordOnItemLongClick(parent, view, position, id, "User Item Long Click Event " + targetDataStruct.getImsi());
            }
        });
        TargetListView.setOnItemClickListener(new RecordOnItemClick() {
            @Override
            public void recordOnItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3, String strMsg) {
                TargetDataStruct targetDataStruct = adapterConnTarget.getItem(arg2);
                if (targetDataStruct.getAuthState() == 1) {
                    Intent intent = new Intent(context, AttachInfoActivity.class);
                    intent.putExtra("imsi",targetDataStruct.getImsi());
                    intent.putExtra("imei", targetDataStruct.getImei());
                    ((BaseActivity)context).startActivityWithAnimation(intent);
                }
                super.recordOnItemClick(arg0, arg1, arg2, arg3, "User Item Click Event " + targetDataStruct.getImsi());
            }
        });
        //cell list
        CellListView = (ExpandableListView) contentView.findViewById(R.id.celllist);
        CellListView.setGroupIndicator(null);
        adapterCell = new AdapterCell(context, null);
        CellListView.setAdapter(adapterCell);

        //2017-07-10 threshold
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
        //radiogroup
        radioGroup = (RadioGroup) contentView.findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (contentView.findViewById(R.id.target_tab).getId() == checkedId) {
                    switchList(false);
                } else {
                    switchList(true);
                }
            }
        });
        //cs fallback
        spinner_cstech = (Spinner) contentView.findViewById(R.id.cs_tech);
        spinner_csband = (Spinner) contentView.findViewById(R.id.cs_band);
        edit_cschannel = (EditText) contentView.findViewById(R.id.cs_channel);
        spinner_cstech.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (bCSUpdateState) {
                    changeBand(context, position, (LinearLayout) contentView.findViewById(R.id.layout_cs_bc), spinner_csband);
                } else {
                    bCSUpdateState = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        InitBcastEndDialog();
    }
//    //switch cell list = true and target list = false
    private void switchList(boolean bFlag) {
        if (bFlag) {
            radioGroup.check(contentView.findViewById(R.id.cell_tab).getId());
            TargetListView.setVisibility(View.GONE);
            CellListView.setVisibility(View.VISIBLE);
        } else {
            radioGroup.check(contentView.findViewById(R.id.target_tab).getId());
            CellListView.setVisibility(View.GONE);
            TargetListView.setVisibility(View.VISIBLE);
        }
    }

    private void BcastStart() {
        if (iCurBtsState == 0) {
            hintDialog(true, true, "Warning", "System Error");
        } else if(strBand.equals("0")) {
            hintDialog(true, true, "Warning", "Band =  N/A");
        } else if (!TextUtils.isEmpty(edit_auto_switch_interval.getText().toString()) && Integer.parseInt(edit_auto_switch_interval.getText().toString()) < 30) {
            edit_auto_switch_interval.requestFocus();
            edit_auto_switch_interval.setError("must>=30");
        } else if (!TextUtils.isEmpty(edit_cid.getText().toString()) && Integer.parseInt(edit_cid.getText().toString()) > 268435455) {
            edit_cid.requestFocus();
            edit_cid.setError("must<268435455");
        } else if (!TextUtils.isEmpty(edit_techtac.getText().toString()) && Integer.parseInt(edit_techtac.getText().toString()) > 65535) {
            edit_techtac.requestFocus();
            edit_techtac.setError("must<65536");
        } else if (!TextUtils.isEmpty(edit_techpci.getText().toString()) && Integer.parseInt(edit_techpci.getText().toString()) > 503) {
            edit_techpci.requestFocus();
            edit_techpci.setError("must<504");
        } else if (checkTextEmpty(edit_mcc) && checkTextEmpty(edit_mnc) &&
                ((!strBand.equals("255") && !bMultiBand && checkChannels(edit_channels, strBand, strCurTech)) ||
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
                            BcastStartMsg();
                        }
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
                dialog.setTitle("Hint");
                dialog.setContent("Band Conflict");
                dialog.setRightBtnContent("Continue");
            } else {
                saveData();
                BcastStartMsg();
            }
        }
    }
    private void BcastStartMsg() {
        edit_channels.setError(null);
        BcastStartReq bcastStartReq = new BcastStartReq();
        bcastStartReq.setCid(edit_cid.getText().toString());
        bcastStartReq.setRncid(edit_rncid.getText().toString());
        bcastStartReq.setTech(strCurTech);
        if (strCurTech.equals("2G")) {
            CellScanTechSpecific techSpecific = new CellScanTechSpecific();
            bcastStartReq.setTechSpecific(techSpecific);
            bcastStartReq.getTechSpecific().setNcc(edit_techncc.getText().toString());
            bcastStartReq.getTechSpecific().setBcc(edit_techbcc.getText().toString());
            bcastStartReq.getTechSpecific().setLac(edit_techlac.getText().toString());
        } else if (strCurTech.equals("3G") && (!TextUtils.isEmpty(edit_techlac.getText().toString()) || !TextUtils.isEmpty(edit_techpsc.getText().toString()))) {
            CellScanTechSpecific techSpecific = new CellScanTechSpecific();
            bcastStartReq.setTechSpecific(techSpecific);
            bcastStartReq.getTechSpecific().setLac(edit_techlac.getText().toString());
            bcastStartReq.getTechSpecific().setPsc(edit_techpsc.getText().toString());
        } else if (strCurTech.equals("4G")) {
            if (!TextUtils.isEmpty(edit_cschannel.getText().toString())) {
                if (!checkChannel(spinner_cstech.getSelectedItemPosition(),
                        Integer.parseInt(spinner_csband.getSelectedItem().toString()),
                        edit_cschannel.getText().toString())) {
                    edit_cschannel.requestFocus();
                    edit_cschannel.setError("error");
                    return;
                }
            }
            CellScanTechSpecific techSpecific = new CellScanTechSpecific();
            bcastStartReq.setTechSpecific(techSpecific);
            bcastStartReq.getTechSpecific().setTac(edit_techtac.getText().toString());
            bcastStartReq.getTechSpecific().setPci(edit_techpci.getText().toString());
            bcastStartReq.getTechSpecific().setBandwidth(spinner_bandwidth.getSelectedItem().toString());
            edit_cschannel.setError(null);
        }
        bcastStartReq.setMcc(edit_mcc.getText().toString());
        bcastStartReq.setMnc(edit_mnc.getText().toString());
        bcastStartReq.setChannels(edit_channels.getText().toString());
        if (strBand.equals("255") || bMultiBand) {
            bcastStartReq.setBand(spinner_band.getSelectedItem().toString());
        } else {
            bcastStartReq.setBand(strBand);
        }
        bcastStartReq.setEplmn(edit_eplmn.getText().toString());
        bcastStartReq.setOperatorName(edit_operatorname.getText().toString());
        bcastStartReq.setAutoSwitchInterval(edit_auto_switch_interval.getText().toString());
        switch (spinner_power_level.getSelectedItemPosition()){
            case 0:
                bcastStartReq.setPower("1");
                break;
            case 1:
            case 2:
                bcastStartReq.setPower(String.valueOf(spinner_power_level.getSelectedItemPosition()*2));
                break;
            case 3:
                bcastStartReq.setPower("MAX");
                break;
            default:
                bcastStartReq.setPower("1");
                break;
        }
        bcastStartReq.setAntennaPort(String.valueOf(spinner_port.getSelectedItemPosition() + 1));
        bcastStartReq.setAntennaGain(edit_antennagain.getText().toString());
        bcastStartReq.setCellscanThreshold(tv_threshold.getText().toString());
        if (spinner_irtech.getSelectedItemId() != 0 && !TextUtils.isEmpty(edit_irchannel.getText().toString())) {
            if (!checkChannel(spinner_irtech.getSelectedItemPosition(),
                    Integer.parseInt(spinner_irband.getSelectedItem().toString()),
                    edit_irchannel.getText().toString())) {
                edit_irchannel.requestFocus();
                edit_irchannel.setError("error");
                return;
            } else {
                BcastStartImplicitRedir implicitRedir = new BcastStartImplicitRedir();
                bcastStartReq.setImplicitRedir(implicitRedir);
                bcastStartReq.getImplicitRedir().setTech(spinner_irtech.getSelectedItem().toString());
                bcastStartReq.getImplicitRedir().setBand(spinner_irband.getSelectedItem().toString());
                bcastStartReq.getImplicitRedir().setChannel(edit_irchannel.getText().toString());
                edit_irchannel.setError(null);
            }
        }
        BcastCommonApi.getAdjCell(sibCells, strCurTech);
        if (sibCells.size() > 0) bcastStartReq.setSibCells(sibCells);
        BcastDialog();
        sendTcpMsg(context, bcastStartReq.toXml(bcastStartReq));
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

    private void BcastEnd() {
        if (iCurBtsState == 0) {
            hintDialog(true, true, "Warning", "System Error");
        } else {
            try {
                BcastEndReq bcastEndReq = new BcastEndReq();
                sendTcpMsg(context, BcastEndReq.toXml(bcastEndReq));
                proDialog.setCancelable(true);
                proDialog.setCanceledOnTouchOutside(false);
                if (!proDialog.isShowing()) {
                    proDialog.show();
                    proDialog.setTitle("Bcast End Req");
                    proDialog.setContent("Please Wait...");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void BcastDialog() {
        if (!proDialog.isShowing()) {
            proDialog.setCancelable(false);
            proDialog.show();
            proDialog.setTitle("Bcast Start Req");
            proDialog.setContent("Please Wait...");
            Thread thread = new Thread() {
                public void run() {
                    try {
                        bDialogState = true;
                        int iCount = 0;
                        while (iCurBtsState != 6 && iCurBtsState != 2 && iCount < 30 && bDialogState) {
                            iCount++;
                            sleep(500);
                        }
                        if (iCount < 30 && bDialogState) {
                            Message msg = new Message();
                            while ((iCurBtsState == 6 || iCurBtsState == 2) && bDialogState) {
                                sleep(1000);
                            }
                            iCount = 0;
                            while (bDialogState && iCount < 180) {
                                sleep(1000);
                                iCount++;
                                if (iCurBtsState == 1) {
                                    iCount = 180;
                                }
                            }
                            if (iCount >= 180 && bDialogState) {//fail
                                msg = new Message();
                                msg.what = BCAST_END_TIMEOUT;
                                handler.sendMessage(msg);
                            }
                        } else if (iCount >= 30) {
                            Message msg = new Message();
                            msg.what = BCAST_FAILURE;
                            handler.sendMessage(msg);
                        }
                        proDialog.dismiss();
                    } catch (InterruptedException e) {
                        // TODO 自动生成的 catch 块
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
    }
    class MyTimer extends TimerTask implements Serializable {

        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = TIMER_SECOND;
            handler.sendMessage(message);
        }
    }
    private void BcastUIState(int iUiState) {
        ScrollView layout_start = (ScrollView) contentView.findViewById(R.id.layout_start);
        LinearLayout layout_end = (LinearLayout) contentView.findViewById(R.id.layout_end);
        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.VISIBLE);
        String Band = SharePreferenceUtils.getInstance(context).getString("status_notif_band" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        switch (iUiState) {
            case BCAST_END:
                //lmj
                switchBcastTimer(true);
                ((RevealAnimationActivity) context).getSettingBtn().setImageResource(R.drawable.btn_end_selector);
                ((RevealAnimationActivity) context).getSettingBtn().setOnClickListener(new RecordOnClick() {
                    @Override
                    public void recordOnClick(View v, String strMsg) {
                        if (CheckObserverMode(strControlIP) && !ButtonUtils.isFastDoubleClick()) {
                            BcastEnd();
                        }
                        super.recordOnClick(v, "Bcast End event");
                    }
                });
                layout_start.setVisibility(View.GONE);
                layout_end.setVisibility(View.VISIBLE);
                ((TextView) contentView.findViewById(R.id.cur_band)).setText("Band: " + Band);
                ((TextView) contentView.findViewById(R.id.cur_port)).setText("Port: " + SharePreferenceUtils.getInstance(context).getInt("port" + FemtoSn + strCurTech, 0));
                ((TextView) contentView.findViewById(R.id.cur_power)).setText("Power Level: " + SharePreferenceUtils.getInstance(context).getString("power" + FemtoSn + strCurTech, null));
                break;
            default:
                switchBcastTimer(false);
                ((RevealAnimationActivity) context).getSettingBtn().setImageResource(R.drawable.btn_start_selector);
                ((RevealAnimationActivity) context).getSettingBtn().setOnClickListener(new RecordOnClick() {
                    @Override
                    public void recordOnClick(View v, String strMsg) {
                        if (CheckObserverMode(strControlIP) && !ButtonUtils.isFastDoubleClick()) {
                            BcastStart();
                        }
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0); //强制隐藏键盘
                        super.recordOnClick(v, "Bcast Start Event");
                    }
                });
                layout_start.setVisibility(View.VISIBLE);
                layout_end.setVisibility(View.GONE);
                adapterCell.removeAll();
                adapterConnTarget.RemoveAll();
                //band
                updateBand();
                //clear port power
                SharePreferenceUtils.getInstance(context).setInt("port" + FemtoSn, 0);
                SharePreferenceUtils.getInstance(context).setString("power" + FemtoSn, null);
                break;
        }
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
            SharedPreferences sp = context.getSharedPreferences("bcast_params", MODE_PRIVATE);
            spinner_band.setSelection(sp.getInt("multiband_item" + FemtoSn, 0));
        } else {
            if (strBand.equals("255") && !TextUtils.isEmpty(strCurTech)) {
                tv_band.setVisibility(View.GONE);
                int iTech = 0;
                if (spinner_band.getAdapter() != null &&
                        ((strCurTech.equals("4G") && spinner_band.getAdapter().getCount() == getResources().getStringArray(R.array.band_4g).length) ||
                        (strCurTech.equals("3G") && spinner_band.getAdapter().getCount() == getResources().getStringArray(R.array.band_3g).length) ||
                        (strCurTech.equals("2G") && spinner_band.getAdapter().getCount() == getResources().getStringArray(R.array.band_2g).length))) {
                    return;
                } else if ( strCurTech.equals("4G")) {
                    iTech = 3;
                } else if (strCurTech.equals("3G")) {
                    iTech = 2;
                } else if (strCurTech.equals("2G")) {
                    iTech = 1;
                }
                changeBand(context, iTech, (LinearLayout) contentView.findViewById(R.id.layout_band), spinner_band);
            } else {
                contentView.findViewById(R.id.layout_band).setVisibility(View.GONE);
                tv_band.setVisibility(View.VISIBLE);
                tv_band.setText(strBand);
            }
        }
    }
    private Handler handler = new SerializableHandler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BCAST_FAILURE:
                     CustomToast.showToast(context, "Bcast Start Failure");
                    break;
                case BCAST_END_TIMEOUT:
                    CustomToast.showToast(context, "Bcast Start Timeout");
                    break;
                case TIMER_SECOND://小区计时
                    iBcastTimer++;
                    ((TextView)contentView.findViewById(R.id.timemeter)).setText(secToTime(iBcastTimer));
                    break;
                default:
                    break;
            }

        }
    };

    protected void InitBcastEndDialog() {
        proDialog = new CustomProgressDialog(context, R.style.dialog_style);
        hintDialog = new OneBtnHintDialog(context, R.style.dialog_style);
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

    private class AdapterCell extends ThreeLevelExpandableAdapter {

        public AdapterCell(Context context, AdapterView.OnItemClickListener litener) {
            super(context, litener);
        }

        public void updateList() {
            ((RadioButton) contentView.findViewById(R.id.cell_tab)).setText("Cell List(" + cells.size() + ")");
            notifyDataSetChanged();
        }

        public void removeAll() {
            cells.clear();
            notifyDataSetChanged();
            ((RadioButton) contentView.findViewById(R.id.cell_tab)).setText("Cell List(0)");
            SharePreferenceUtils.getInstance(context).setInt("CellTotal" + FemtoSn, 0);
        }

        @Override
        public int getGroupCount() {
            return cells.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 0;
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
//            TextView textView = new TextView(mContext);
//            textView.setWidth(parent.getWidth());
//            textView.setHeight(100);
//            textView.setTextSize(20);
//            textView.setTextColor(getResources().getColor(
//                    android.R.color.secondary_text_dark));
//            if (secondLevelPosition != 0) {
//                CellScanSibCell sibCellList = getChild(firstLevelPosition, secondLevelPosition);
//                textView.setText("   " + String.valueOf(sibCellList.getCid()));
//            } else {
//                textView.setText("Channel:    " + cells.get);
//            }
//            return textView;
            return null;
        }
    }
    private void saveData() {
        SharedPreferences preferences = context.getSharedPreferences("bcast_params",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("mcc" + strCurTech, edit_mcc.getText().toString());
        editor.putString("mnc" + strCurTech, edit_mnc.getText().toString());
        editor.putString("channels" + strCurTech, edit_channels.getText().toString());
        editor.putInt("power_level" + strCurTech, spinner_power_level.getSelectedItemPosition());
        editor.putInt("port" + strCurTech, spinner_port.getSelectedItemPosition());
        editor.putString("cid" + strCurTech, edit_cid.getText().toString());
        editor.putString("ncc" + strCurTech, edit_techncc.getText().toString());
        editor.putString("bcc" + strCurTech, edit_techbcc.getText().toString());
        editor.putString("rncid" + strCurTech, edit_rncid.getText().toString());
        editor.putString("eplmn" + strCurTech, edit_eplmn.getText().toString());
        editor.putString("name" + strCurTech, edit_operatorname.getText().toString());
        editor.putString("switch" + strCurTech, edit_auto_switch_interval.getText().toString());
        editor.putString("antennagain" + strCurTech, edit_antennagain.getText().toString());
        editor.putString("lac" + strCurTech, edit_techlac.getText().toString());
        editor.putString("pci" + strCurTech, edit_techpci.getText().toString());
        editor.putString("psc" + strCurTech, edit_techpsc.getText().toString());
        editor.putString("tac" + strCurTech, edit_techtac.getText().toString());
        editor.putInt("ir_tech" + strCurTech, spinner_irtech.getSelectedItemPosition());
        editor.putInt("ir_band" + strCurTech, spinner_irband.getSelectedItemPosition());
        editor.putString("ir_channel" + strCurTech, edit_irchannel.getText().toString());
        editor.putInt("threshold" + strCurTech, sb_threshold.getProgress());
        editor.putInt("bandwidth" + strCurTech, spinner_bandwidth.getSelectedItemPosition());
        editor.putInt("cs_tech" + strCurTech, spinner_cstech.getSelectedItemPosition());
        editor.putString("cs_channel" + strCurTech, edit_cschannel.getText().toString());
        editor.putInt("cs_band" + strCurTech, spinner_csband.getSelectedItemPosition());
        editor.putInt("multiband_item" + FemtoSn, spinner_band.getSelectedItemPosition());
        editor.commit();
    }
    private void loadData() {
        strControlIP = SharePreferenceUtils.getInstance(context).getString("status_notif_controller_ip" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        strBand = SharePreferenceUtils.getInstance(context).getString("status_notif_band" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "0");
        FemtoSn = SharePreferenceUtils.getInstance(context).getString("status_notif_sn" + ((ProxyApplication) context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication) context.getApplicationContext()).getiTcpPort(), "");
        if (!SharePreferenceUtils.getInstance(context).getString("status_notif_tech" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "").equals(strCurTech)) {
            ((TextView) contentView.findViewById(R.id.cur_channel)).setText("Channel: ");
            (contentView.findViewById(R.id.layout_lte)).setVisibility(View.GONE);
            (contentView.findViewById(R.id.layout_umts)).setVisibility(View.GONE);
            (contentView.findViewById(R.id.layout_gsm)).setVisibility(View.GONE);
        }
        bMultiBand = SharePreferenceUtils.getInstance(context).getBoolean("multi_band_status" + FemtoSn, false);
        strCurTech = SharePreferenceUtils.getInstance(context).getString("status_notif_tech" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        if (TextUtils.isEmpty(strCurTech)) {
            return;
        }
        //显示2 3 4g特定的参数
        switchTechView(strCurTech, contentView);
        if (!strCurTech.equals("4G")) {
            THRESHOLD_MAX = 115;
            sb_threshold.setMax(90);
        }
        adapterConnTarget.updateCurTech(strCurTech);

        SharedPreferences sp = context.getSharedPreferences("bcast_params", MODE_PRIVATE);
        edit_mcc.setText(sp.getString("mcc" + strCurTech, ""));
        edit_mnc.setText(sp.getString("mnc" + strCurTech, ""));
        edit_channels.setText(sp.getString("channels" + strCurTech, ""));
        spinner_power_level.setSelection(sp.getInt("power_level" + strCurTech, 2));
        spinner_port.setSelection(sp.getInt("port" + strCurTech, 0));
        edit_cid.setText(sp.getString("cid" + strCurTech, ""));
        edit_techncc.setText(sp.getString("ncc" + strCurTech, ""));
        edit_techbcc.setText(sp.getString("bcc" + strCurTech, ""));
        edit_rncid.setText(sp.getString("rncid" + strCurTech, ""));
        edit_eplmn.setText(sp.getString("eplmn" + strCurTech, ""));
        edit_operatorname.setText(sp.getString("name" + strCurTech, ""));
        edit_auto_switch_interval.setText(sp.getString("switch" + strCurTech, ""));
        edit_antennagain.setText(sp.getString("antennagain" + strCurTech, ""));
        int irtech_item = sp.getInt("ir_tech" + strCurTech, 0);
        if (irtech_item == spinner_irtech.getSelectedItemPosition()) {
            bIRUpdateStats = true;
        } else {
            bIRUpdateStats = false;
        }
        spinner_irtech.setSelection(irtech_item);
        if (spinner_irtech.getSelectedItemPosition() != 0) {
            changeBand(context, spinner_irtech.getSelectedItemPosition(), (LinearLayout) contentView.findViewById(R.id.layout_ir_bc), spinner_irband);
            spinner_irband.setSelection(sp.getInt("ir_band" + strCurTech, 0));
            edit_irchannel.setText(sp.getString("ir_channel" + strCurTech, ""));
        }
        edit_techpci.setText(sp.getString("pci" + strCurTech, ""));
        edit_techtac.setText(sp.getString("tac" + strCurTech, ""));
        spinner_bandwidth.setSelection(sp.getInt("bandwidth" + strCurTech, 1));
        spinner_cstech.setSelection(sp.getInt("cs_tech" + strCurTech, 0));
        edit_techlac.setText(sp.getString("lac" + strCurTech, ""));
        edit_techpsc.setText(sp.getString("psc" + strCurTech, ""));
        if (strCurTech.equals("4G")) {
            if (spinner_cstech.getSelectedItemPosition() != 0 ) {
                contentView.findViewById(R.id.layout_cs_bc).setVisibility(View.VISIBLE);
                changeBand(context, spinner_cstech.getSelectedItemPosition(), (LinearLayout) contentView.findViewById(R.id.layout_cs_bc), spinner_csband);
                spinner_csband.setSelection(sp.getInt("cs_band" + strCurTech, 0));
                edit_cschannel.setText(sp.getString("cs_channel" + strCurTech, ""));
            }
        }
        sb_threshold.setProgress(sp.getInt("threshold" + strCurTech, 0));
        tv_threshold.setText(sb_threshold.getProgress() - THRESHOLD_MAX + "");
    }
    private void saveCellList(CellScanCell cellScanCell) {
        int iIndex = cells.size();
        SharePreferenceUtils.getInstance(context).setString("channel" + FemtoSn + String.valueOf(iIndex), cellScanCell.getChannel());
        SharePreferenceUtils.getInstance(context).setString("cid" + FemtoSn + String.valueOf(iIndex), cellScanCell.getCid());
        SharePreferenceUtils.getInstance(context).setString("mcc" + FemtoSn + String.valueOf(iIndex), cellScanCell.getMcc());
        SharePreferenceUtils.getInstance(context).setString("mnc" + FemtoSn + String.valueOf(iIndex), cellScanCell.getMnc());
        SharePreferenceUtils.getInstance(context).setString("tac" + FemtoSn + String.valueOf(iIndex), cellScanCell.getTechSpecific().getTac());
        SharePreferenceUtils.getInstance(context).setString("pci" + FemtoSn + String.valueOf(iIndex), cellScanCell.getTechSpecific().getPci());
        SharePreferenceUtils.getInstance(context).setString("rsrp" + FemtoSn + String.valueOf(iIndex), cellScanCell.getTechSpecific().getRsrp());
        SharePreferenceUtils.getInstance(context).setString("lac" + FemtoSn + String.valueOf(iIndex), cellScanCell.getTechSpecific().getLac());
        SharePreferenceUtils.getInstance(context).setString("rscp" + FemtoSn + String.valueOf(iIndex), cellScanCell.getTechSpecific().getRscp());
        SharePreferenceUtils.getInstance(context).setString("bsic" + FemtoSn + String.valueOf(iIndex), cellScanCell.getTechSpecific().getBsic());
        SharePreferenceUtils.getInstance(context).setString("rssi" + FemtoSn + String.valueOf(iIndex), cellScanCell.getTechSpecific().getRssi());
        SharePreferenceUtils.getInstance(context).setString("bandwidth" + FemtoSn + String.valueOf(iIndex), cellScanCell.getTechSpecific().getBandwidth());
        cells.add(cellScanCell);
    }
    private void loadCellList() {
        cells.clear();
        int iCellListTotal = SharePreferenceUtils.getInstance(context).getInt("CellTotal" + FemtoSn, 0);
        if (iCellListTotal > 0) {
            for (int i = 0; i < iCellListTotal; i++) {
                CellScanCell cellScanCell = new CellScanCell();
                CellScanTechSpecific techSpecific = new CellScanTechSpecific();
                cellScanCell.setTechSpecific(techSpecific);
                cellScanCell.setChannel(SharePreferenceUtils.getInstance(context).getString("channel" + FemtoSn + String.valueOf(i), ""));
                cellScanCell.setCid(SharePreferenceUtils.getInstance(context).getString("cid" + FemtoSn + String.valueOf(i), ""));
                cellScanCell.setMcc(SharePreferenceUtils.getInstance(context).getString("mcc" + FemtoSn + String.valueOf(i), ""));
                cellScanCell.setMnc(SharePreferenceUtils.getInstance(context).getString("mnc" + FemtoSn + String.valueOf(i), ""));
                techSpecific.setTac(SharePreferenceUtils.getInstance(context).getString("tac" + FemtoSn + String.valueOf(i), ""));
                techSpecific.setPci(SharePreferenceUtils.getInstance(context).getString("pci" + FemtoSn + String.valueOf(i), ""));
                techSpecific.setRsrp(SharePreferenceUtils.getInstance(context).getString("rsrp" + FemtoSn + String.valueOf(i), ""));
                techSpecific.setLac(SharePreferenceUtils.getInstance(context).getString("lac" + FemtoSn + String.valueOf(i), ""));
                techSpecific.setPsc(SharePreferenceUtils.getInstance(context).getString("psc" + FemtoSn + String.valueOf(i), ""));
                techSpecific.setRscp(SharePreferenceUtils.getInstance(context).getString("rscp" + FemtoSn + String.valueOf(i), ""));
                techSpecific.setRssi(SharePreferenceUtils.getInstance(context).getString("rssi" + FemtoSn + String.valueOf(i), ""));
                techSpecific.setBsic(SharePreferenceUtils.getInstance(context).getString("bsic" + FemtoSn + String.valueOf(i), ""));
                techSpecific.setBandwidth( SharePreferenceUtils.getInstance(context).getString("bandwidth" + FemtoSn + String.valueOf(i), ""));
                cells.add(cellScanCell);
            }
        }
        adapterCell.updateList();
    }

    private int iCounter = 0;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void TargetPosition(TargetPosition tp) {
        TargetDataStruct targetDataStruct = new TargetDataStruct();
        targetDataStruct.setImsi(tp.getImsi());
        targetDataStruct.setAuthState(1);
        targetDataStruct.setbPositionStatus(true);
        adapterConnTarget.AttachTarget(targetDataStruct);
        //处理异常断开
        if (iCounter < ((adapterConnTarget.getAuthTotal() - 1)*3)) {//讲接收到的position存起来
            iCounter++;
        } else if (iCounter == ((adapterConnTarget.getAuthTotal() - 1)*3) && iCounter != 0) {
            adapterConnTarget.checkAuthTarget();
            iCounter = 255;
        } else {
            iCounter = 255;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void TargetAttach(TargetAttach ta) {
        TargetDataStruct targetDataStruct = new TargetDataStruct();
        targetDataStruct.setImsi(ta.getImsi());
        targetDataStruct.setImei(ta.getImei());
        targetDataStruct.setAuthState(1);
        targetDataStruct.setbPositionStatus(true);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
        targetDataStruct.setStrAttachtime(formatter.format(new Date()));
        adapterConnTarget.AttachTarget(targetDataStruct);
        switchList(false);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ConnReqNotif(ConnRequestNotif crn) {
        TargetDataStruct targetDataStruct = new TargetDataStruct();
        targetDataStruct.setImsi(crn.getImsi());
        if (crn.getTechSpecific() != null)
            targetDataStruct.setImei(crn.getTechSpecific().getImei());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
        Date curDate = new Date();//获取当前时间
        targetDataStruct.setStrConntime(formatter.format(curDate));
        targetDataStruct.setAuthState(crn.getiAuth());
        //List<User> users =  ProxyApplication.getDaoSession().getUserDao().queryBuilder().where(UserDao.Properties.SrtImsi.eq(crn.getImsi())).build().list();
        if (/*users.size() != 0*/crn.getiCount() > 1) {//已连接，count++
            targetDataStruct.setCount(/*users.get(0).getICount()*/crn.getiCount());
            adapterConnTarget.RepeatTarget(targetDataStruct);
        } else {//第一次连接
            adapterConnTarget.addTarget(targetDataStruct);
        }
    }
    private int iBtsFiveCount = 0;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void StatusNotif(Status s) {
        iCurBtsState = Integer.parseInt(s.getBtsState());
        strControlIP = s.getControllerClient();
        if (!TextUtils.isEmpty(s.getTech()) && !s.getTech().equals(strCurTech)) {
            strCurTech = s.getTech();
            loadData();
        }
        strBand = s.getBand();
        if (iCurBtsState == 3 || iCurBtsState == 4) {
            if (contentView.findViewById(R.id.layout_end).getVisibility() == View.GONE)  { BcastUIState(BCAST_END); }
            ((TextView) contentView.findViewById(R.id.cur_channel)).setText("Channel: " + s.getChannel());
            bDialogState = false;
            if (strCurTech.equals("4G")) {
                (contentView.findViewById(R.id.layout_lte)).setVisibility(View.VISIBLE);
                ((TextView) contentView.findViewById(R.id.lte_tac)).setText("TAC: " + s.getTechSpecific().getTac());
                ((TextView) contentView.findViewById(R.id.lte_pci)).setText("PCI: " + s.getTechSpecific().getPci());
            } else if (strCurTech.equals("3G")) {
                (contentView.findViewById(R.id.layout_umts)).setVisibility(View.VISIBLE);
                ((TextView) contentView.findViewById(R.id.umts_lac)).setText("LAC: " + s.getTechSpecific().getLac());
                ((TextView) contentView.findViewById(R.id.umts_psc)).setText("PSC: " + s.getTechSpecific().getPsc());
            } else if (strCurTech.equals("2G")) {
                (contentView.findViewById(R.id.layout_gsm)).setVisibility(View.VISIBLE);
                ((TextView) contentView.findViewById(R.id.gsm_lac)).setText("LAC: " + s.getTechSpecific().getLac());
                ((TextView) contentView.findViewById(R.id.gsm_bsic)).setText("BSIC: " + s.getTechSpecific().getBsic());
            }
        } else if (iCurBtsState == 5) {
            iBtsFiveCount++;
            if (iBtsFiveCount >= 3) {
                hintDialog(true, true, "Warning", "Please reboot Femto");
            }
            return;
        } else {
            if (contentView.findViewById(R.id.layout_start).getVisibility() == View.GONE) {
                BcastUIState(BCAST_START);
            } else if (!bMultiBand){
                updateBand();
            }
        }
        iBtsFiveCount = 0;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BcastEndRes(BcastEndRes ber) {
        try {
            if (ber.getStatus().equals("SUCCESS")) {
                iCurBtsState = 1;
                BcastUIState(BCAST_START);
                updateBcastInfoEndTime(context, true);
            } else {
                 CustomToast.showToast(context, "Bcast End Failure");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (proDialog.isShowing()) {
            proDialog.dismiss();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BcastStartRes(BcastStartRes bsr) {
        try {
            if (bsr.getStatus().equals("SUCCESS")) {
                bDialogState = false;
                sendTargetList(context, strCurTech);
                if (strCurTech.equals("4G")) {//LTE 才有cs fallback
                    sendFallBack(context, contentView);
                }
                SharePreferenceUtils.getInstance(context).setString("status_notif_cid" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), bsr.getCid());
                SharePreferenceUtils.getInstance(context).setString("status_notif_channel" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), bsr.getChannel());
                String Tech = SharePreferenceUtils.getInstance(context).getString("status_notif_tech" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
                String Band = SharePreferenceUtils.getInstance(context).getString("status_notif_band" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "0");
                String Unique = FemtoSn + Band + Tech + /*bsr.getChannel() + */bsr.getCid();
                SharePreferenceUtils.getInstance(context).setString("status_notif_unique" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), Unique);
                saveBcastInfo(context, true);
                //port power
                SharePreferenceUtils.getInstance(context).setInt("port" + FemtoSn + strCurTech, spinner_port.getSelectedItemPosition() + 1);
                SharePreferenceUtils.getInstance(context).setString("power" + FemtoSn + strCurTech, spinner_power_level.getSelectedItem().toString());
                BcastUIState(BCAST_END);
            } else {
                BcastUIState(BCAST_START);
                bDialogState = false;

                 CustomToast.showToast(context, "Bcast Start Failure");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void CellScanNotif(CellScanNotif scn){
        if (scn.getCells().size() > 0) {
            switchList(true);
            for (int i = 0; i < scn.getCells().size(); i++) {
                scn.getCells().get(i).setChannel(scn.getChannel());
                saveCellList(scn.getCells().get(i));
            }
            adapterCell.updateList();
        }
        SharePreferenceUtils.getInstance(context).setInt("CellTotal" + FemtoSn, cells.size());
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void TargetDetach(TargetDetach targetDetach) {
        TargetDataStruct targetDataStruct = new TargetDataStruct();
        targetDataStruct.setImsi(targetDetach.getImsi());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
        targetDataStruct.setStrDetachtime(formatter.format(new Date()));
        adapterConnTarget.TargetDetach(targetDataStruct);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void CSFallBack(CsFallbackRes csFallbackRes) {
        if (csFallbackRes.getStatus().equals("SUCCESS")) {
            CustomToast.showToast(context, "cs fallback success");
        } else {
            CustomToast.showToast(context, "cs fallback failure");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        switchBcastTimer(false);
        if (proDialog.isShowing()) {
            proDialog.dismiss();
        }
        if (hintDialog.isShowing()) {
            hintDialog.dismiss();
        }
        if(null != mWakeLock){
            mWakeLock.release();
        }
    }

    private void switchBcastTimer(boolean bFlag) {
        if (bFlag) {//open
            Long starttime = SharePreferenceUtils.getInstance(context).getLong("status_notif_starttime" +
                            ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() +
                            ((ProxyApplication)context.getApplicationContext()).getiTcpPort(),
                    System.currentTimeMillis());
            if (System.currentTimeMillis() > starttime) {
                iBcastTimer = (int)(System.currentTimeMillis() - starttime)/1000;
            } else {
                iBcastTimer = 0;
            }
            if (bcastTimer == null) {
                bcastTimer = new Timer();
                bcastTimer.schedule(new MyTimer(), 1000, 1500);
            }
        } else {
            iBcastTimer = 0;
            if (bcastTimer != null) {
                bcastTimer.purge();
                bcastTimer.cancel();
                bcastTimer = null;
            }
        }
    }
}