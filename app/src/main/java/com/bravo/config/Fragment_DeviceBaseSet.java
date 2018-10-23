package com.bravo.config;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.OneBtnHintDialog;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.data_ben.DeviceDataStruct;
import com.bravo.data_ben.DeviceFragmentStruct;
import com.bravo.data_ben.WaitDialogData;
import com.bravo.dialog.WaitDialog;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.fragments.SerializableHandler;
import com.bravo.utils.Logs;
import com.bravo.utils.Utils;
import com.bravo.xml.HandleRecvXmlMsg;
import com.bravo.xml.LTE;
import com.bravo.xml.LTE_GeneralPara;

import java.util.ArrayList;

/**
 * Created by admin on 2018-10-17.
 */

public class Fragment_DeviceBaseSet extends RevealAnimationBaseFragment {
    private final String TAG = "Fragment_DeviceBaseSet";
    private static int viewId = 0;

    private int[] changeList;
    private ArrayList<Boolean> viewList;

    private ArrayList<String> dList;
    private LTE_GeneralPara lte_GeneralPara = null;
    //private boolean valueError = false;
    private ArrayList<WaitDialogData> sendDateList = null;

    private DeviceDataStruct deviceDate = null;

    private LinearLayout l_Device_lte;
    private LinearLayout l_Device_zyf;
    private LinearLayout l_Device_hjt;

    private Spinner s_deviceSelect;

    //lte配置
    private Spinner lte_selectFreq;
    private Spinner lte_selectmode;

    private Spinner lte_bw;
    private EditText lte_earfcn;
    private EditText lte_pci;
    private EditText lte_cid;
    private EditText lte_mcc;
    private EditText lte_mnc;
    private EditText lte_tac;
    private EditText lte_power;
    private EditText lte_periodtac;

    private EditText lte_sonEarfcn;

    private EditText lte_otherPlmn;

    private EditText lte_periodFreqTime;
    private EditText lte_periodFreqFreq;

    private EditText lte_ntpServer;
    private EditText lte_bandOffset;
    private Spinner lte_ntpPri;
    private Spinner lte_gpsSelect;

    private Spinner lte_source;
    private Spinner lte_MEnable;
    private EditText lte_MEarfcn;
    private EditText lte_MPci;
    private Spinner lte_MBw;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logs.d(TAG, "onCreate",true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_device_set_base);
    }

    @Override
    public void initView() {
        Logs.d(TAG, "initView",true);
        l_Device_lte =(LinearLayout)contentView.findViewById(R.id.config_lte);
        l_Device_zyf =(LinearLayout)contentView.findViewById(R.id.config_zyf);
        l_Device_hjt =(LinearLayout)contentView.findViewById(R.id.config_hjt);
        l_Device_zyf.setVisibility(View.GONE);
        l_Device_lte.setVisibility(View.GONE);
        l_Device_hjt.setVisibility(View.GONE);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        Logs.d(TAG, "initData",true);
        changeList = new int[HandleRecvXmlMsg.MAX_CONFIG];
        viewList = new ArrayList<Boolean>();
        sendDateList = new ArrayList<WaitDialogData>();

        dList = DeviceFragmentStruct.getSnList();

        s_deviceSelect = (Spinner) contentView.findViewById(R.id.deviceSelect);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,R.layout.my_spinner ,dList);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item ,dList);
        s_deviceSelect.setAdapter(adapter);
        //添加事件Spinner事件监听
        s_deviceSelect.setOnItemSelectedListener(new SpinnerSelectedListener());
        s_deviceSelect.setSelection(0 ,true);
    }

    @Override
    public void onResume() {
        Logs.d(TAG, "onResume",true);
        super.onResume();
        loadData();
        ((RevealAnimationActivity) context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity) context).getSettingBtn().setImageResource(com.bravo.R.drawable.btn_config_normal);
        ((RevealAnimationActivity) context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                if (deviceDate == null) {
                    CustomToast.showToast(context, "未选择设备");
                    return;
                }
                if (null == DeviceFragmentStruct.getDevice(deviceDate.getSN())) {
                    CustomToast.showToast(context, String.format("设备%s已经下线",deviceDate.getSN()));
                    return;
                }
                if (saveData()) {
                    //CustomToast.showToast(context, "参数配置成功！");
                }
            }
        });
    }

    @Override
    public void onPause() {
        Logs.d(TAG, "onPause",true);
        super.onPause();
    }

    @Override
    public void onStop() {
        Logs.d(TAG, "onStop",true);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Logs.d(TAG, "onDestroy",true);
        super.onDestroy();

    }

    private void openDialog(String str){
        OneBtnHintDialog hintDialog;
        hintDialog = new OneBtnHintDialog(context, R.style.dialog_style);
        hintDialog.setCanceledOnTouchOutside(false);
        hintDialog.show();
        hintDialog.setBtnContent("确定");
        hintDialog.setTitle("错误");
        hintDialog.setContent(str);
        hintDialog.setListener(new OneBtnHintDialog.BtnClickListener() {
            @Override
            public void onBtnClick(View v) {
            }
        });
    }

    private void loadData() {

    }

    private boolean checkChanges(){
        for(int i =0;i<viewList.size();i++) {
            //Logs.d(TAG,String.format("ViewId=%d",i));
            if (viewList.get(i)) {
                openDialog("参数不在范围内，请重新输入正确参数");
                return false;
            }
        }

        sendDateList.clear();
        for(int i = 0;i< changeList.length;i++) {
            if (changeList[i] > 0) {
                if (i == HandleRecvXmlMsg.LTE_WORKE_MODE) {
                    sendDateList.add(new WaitDialogData(HandleRecvXmlMsg.LTE_WORKE_MODE,"工作模式"));
                } else if (i == HandleRecvXmlMsg.LTE_CELL_CONFIG) {
                    sendDateList.add(new WaitDialogData(HandleRecvXmlMsg.LTE_CELL_CONFIG,"小区配置"));
                } else if (i == HandleRecvXmlMsg.LTE_SON_CONFIG) {
                    sendDateList.add(new WaitDialogData(HandleRecvXmlMsg.LTE_SON_CONFIG,"扫频配置"));
                } else if (i == HandleRecvXmlMsg.LTE_OTHER_PLMN) {
                    sendDateList.add(new WaitDialogData(HandleRecvXmlMsg.LTE_OTHER_PLMN,"等效PLMN"));
                } else if (i == HandleRecvXmlMsg.LTE_PERIOD_FREQ) {
                    sendDateList.add(new WaitDialogData(HandleRecvXmlMsg.LTE_PERIOD_FREQ,"周期变频"));
                } else if (i == HandleRecvXmlMsg.LTE_SYSTEM_SET) {
                    sendDateList.add(new WaitDialogData(HandleRecvXmlMsg.LTE_SYSTEM_SET,"系统配置"));
                } else if (i == HandleRecvXmlMsg.LTE_SYNC_SET) {
                    sendDateList.add(new WaitDialogData(HandleRecvXmlMsg.LTE_SYNC_SET,"同步配置"));
                }
            }
        }

        if (sendDateList.size() <= 0) {
            CustomToast.showToast(context, "没有配置需要修改");
            return false;
        }

        Message message = new Message();
        message.what = 0;
        handler.sendMessage(message);

        return true;
    }

    private boolean saveData() {
        if (!checkChanges()) return false;
        Utils.mySleep(1000); //延时一秒

        String mode = deviceDate.getMode();
        if (mode.equals(DeviceDataStruct.MODE.LTE_TDD)
                || mode.equals(DeviceDataStruct.MODE.LTE_FDD)
                || mode.equals(DeviceDataStruct.MODE.WCDMA)) {
            return saveLteData();
        } else if (mode.equals(DeviceDataStruct.MODE.CDMA)
                || mode.equals(DeviceDataStruct.MODE.GSM_V2)) {

        } else if (mode.equals(DeviceDataStruct.MODE.GSM)) {

        }
        return true;
    }

    private boolean saveLteData() {
        LTE_GeneralPara para = new LTE_GeneralPara();

        if (changeList[HandleRecvXmlMsg.LTE_WORKE_MODE] > 0) {
            changeList[HandleRecvXmlMsg.LTE_WORKE_MODE] = 0;
            Logs.d(TAG,"修改配置LTE_WORKE_MODE");
            para.setBootmode(lte_selectmode.getSelectedItemPosition());
            para.setManualfreq(lte_selectFreq.getSelectedItemPosition());
            new LTE(context).SetWorkMode(deviceDate.getIp(),deviceDate.getPort(),para);
        }
        if (changeList[HandleRecvXmlMsg.LTE_CELL_CONFIG] > 0) {
            changeList[HandleRecvXmlMsg.LTE_CELL_CONFIG] = 0;
            Logs.d(TAG,"修改配置LTE_CELL_CONFIG");
            para.setEarfcndl(Integer.parseInt(lte_earfcn.getText().toString()));
            para.setPci(Integer.parseInt(lte_pci.getText().toString()));
            para.setBandwitch(Integer.parseInt(lte_bw.getSelectedItem().toString()));
            para.setCid(Integer.parseInt(lte_cid.getText().toString()));
            para.setMcc(Integer.parseInt(lte_mcc.getText().toString()));
            para.setMnc(Integer.parseInt(lte_mnc.getText().toString()));
            para.setTac(Integer.parseInt(lte_tac.getText().toString()));
            para.setPower(Integer.parseInt(lte_power.getText().toString()));
            para.setPeriodtac(Integer.parseInt(lte_periodtac.getText().toString()));
            new LTE(context).SetConfiguration(deviceDate.getIp(),deviceDate.getPort(),deviceDate.getMode(),para);
        }
        if (changeList[HandleRecvXmlMsg.LTE_SON_CONFIG] > 0) {
            changeList[HandleRecvXmlMsg.LTE_SON_CONFIG] = 0;
            Logs.d(TAG,"修改配置LTE_SON_CONFIG");
            para.setEarfcnlist(lte_sonEarfcn.getText().toString());
            new LTE(context).SetSonEarfcn(deviceDate.getIp(),deviceDate.getPort(),para);
        }
        if (changeList[HandleRecvXmlMsg.LTE_OTHER_PLMN] > 0) {
            changeList[HandleRecvXmlMsg.LTE_OTHER_PLMN] = 0;
            Logs.d(TAG,"修改配置LTE_OTHER_PLMN");
            para.setOtherplmn(lte_otherPlmn.getText().toString());
            new LTE(context).SetApParameter(deviceDate.getIp(),deviceDate.getPort(),
                    "CFG_OTHER_PLMN",para.getOtherplmn());
        }
        if (changeList[HandleRecvXmlMsg.LTE_PERIOD_FREQ] > 0) {
            changeList[HandleRecvXmlMsg.LTE_PERIOD_FREQ] = 0;
            Logs.d(TAG,"修改配置LTE_PERIOD_FREQ");
            para.setPeriodFreqTime(Integer.parseInt(lte_periodFreqTime.getText().toString()));
            para.setPeriodFreqFreq(lte_periodFreqFreq.getText().toString());
            new LTE(context).SetApParameter(deviceDate.getIp(),deviceDate.getPort(),
                    "CFG_PERIOD_FREQ",
                    String.format("%d:%s",para.getPeriodFreqTime(),para.getPeriodFreqFreq()));
        }
        if (changeList[HandleRecvXmlMsg.LTE_SYSTEM_SET] > 0) {
            changeList[HandleRecvXmlMsg.LTE_SYSTEM_SET] = 0;
            Logs.d(TAG,"修改配置LTE_SYSTEM_SET");
            para.setNtpServer(lte_ntpServer.getText().toString());
            para.setNtppri(Integer.parseInt(lte_ntpPri.getSelectedItem().toString()));
            para.setGps_select(lte_gpsSelect.getSelectedItemPosition());
            para.setBandoffset(lte_bandOffset.getText().toString());
            new LTE(context).SetSystemRequest(deviceDate.getIp(),deviceDate.getPort(),para);
        }
        if (changeList[HandleRecvXmlMsg.LTE_SYNC_SET] > 0) {
            changeList[HandleRecvXmlMsg.LTE_SYNC_SET] = 0;
            Logs.d(TAG,"修改配置LTE_SYNC_SET");
            para.setSource(lte_source.getSelectedItemPosition());
            para.setManualEnable(lte_MEnable.getSelectedItemPosition());
            para.setManualEarfcn(Integer.parseInt(lte_MEarfcn.getText().toString()));
            para.setManualPci(Integer.parseInt(lte_MPci.getText().toString()));
            para.setManualBw(Integer.parseInt(lte_MBw.getSelectedItem().toString()));
            new LTE(context).SetSyncInfo(deviceDate.getIp(),deviceDate.getPort(),para);
        }

        SelectLteInit(deviceDate.getMode(),para);
        return true;
    }

    private void SelectLteInit(String mode,LTE_GeneralPara gPara) {
        if (gPara == null) return;

        init_lte_workMode(mode,gPara);
        init_lte_cellSet(mode,gPara);
        init_lte_son_info(mode,gPara);
        init_lte_otherPlmn(mode,gPara);
        init_lte_periodFreq(mode,gPara);
        init_lte_systemSet(mode,gPara);
        init_lte_syncSet(mode,gPara);
    }

    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Logs.d(TAG, "您选择了:" + dList.get(position));

            l_Device_zyf.setVisibility(View.GONE);
            l_Device_lte.setVisibility(View.GONE);
            l_Device_hjt.setVisibility(View.GONE);

            deviceDate = DeviceFragmentStruct.getDevice(dList.get(position));
            if (deviceDate == null) {
                Logs.e(TAG, String.format("您选择的产品SN为%s,未在设备列表中找到该设备!",dList.get(position)));
                return;
            }

            String mode = deviceDate.getMode();
            if (mode.equals(DeviceDataStruct.MODE.LTE_TDD)
                    || mode.equals(DeviceDataStruct.MODE.LTE_FDD)
                    || mode.equals(DeviceDataStruct.MODE.WCDMA)) {
                l_Device_lte.setVisibility(View.VISIBLE);
                lte_GeneralPara = (LTE_GeneralPara) deviceDate.getGeneralPara();
                SelectLteInit(mode,lte_GeneralPara);

            } else if (mode.equals(DeviceDataStruct.MODE.CDMA)
                    || mode.equals(DeviceDataStruct.MODE.GSM_V2)) {
                l_Device_zyf.setVisibility(View.VISIBLE);
            } else if (mode.equals(DeviceDataStruct.MODE.GSM)) {
                l_Device_hjt.setVisibility(View.VISIBLE);
            } else {
                Logs.e(TAG, String.format("您选择的产品Mode为%s,目前不支持该类型!",deviceDate.getMode()));
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    private void init_lte_workMode(String mode,LTE_GeneralPara para){
        int index;
        //String defult;
        if (para == null) return;
        ArrayAdapter<String> adapter;
        String[] selectFreq={"自动","手动"};
        lte_selectFreq = (Spinner) contentView.findViewById(R.id.lte_manualFreq);
        adapter = new ArrayAdapter<String>(context,R.layout.my_spinner,selectFreq);
        lte_selectFreq.setAdapter(adapter);
        if (para.getManualfreq() == 1) {
            index = 1;
        } else {
            index = 0;
        }
        lte_selectFreq.setSelection(index ,true);
        lte_selectFreq.setOnItemSelectedListener(
                new MyOnItemSelectedListener(lte_selectFreq,selectFreq[para.getManualfreq()],HandleRecvXmlMsg.LTE_WORKE_MODE));

        lte_selectmode = (Spinner) contentView.findViewById(R.id.lte_boot);
        String[] modeList={"半自动","全自动"};
        adapter = new ArrayAdapter<String>(context,R.layout.my_spinner,modeList);
        lte_selectmode.setAdapter(adapter);
        if (para.getBootmode() == 1) {
            index = 1;
        } else {
            index = 0;
        }
        lte_selectmode.setSelection(index ,true);
        lte_selectmode.setOnItemSelectedListener(
                new MyOnItemSelectedListener(lte_selectmode,modeList[para.getBootmode()], HandleRecvXmlMsg.LTE_WORKE_MODE));

    }

    private void init_lte_cellSet(String mode,LTE_GeneralPara para){
        int index;
        if (para == null) return;

        lte_earfcn = (EditText) contentView.findViewById(R.id.lte_earfcn);
        lte_earfcn.setText(String.valueOf(para.getEarfcndl()));
        lte_earfcn.addTextChangedListener(new MyTextWatcher(lte_earfcn,String.valueOf(para.getEarfcndl()),HandleRecvXmlMsg.LTE_CELL_CONFIG,0,65535));

        lte_pci = (EditText) contentView.findViewById(R.id.lte_pci);
        lte_pci.setText(String.valueOf(para.getPci()));
        lte_pci.addTextChangedListener(new MyTextWatcher(lte_pci,String.valueOf(para.getPci()),HandleRecvXmlMsg.LTE_CELL_CONFIG,0,512));

        lte_cid = (EditText) contentView.findViewById(R.id.lte_cellid);
        lte_cid.setText(String.valueOf(para.getCid()));
        lte_cid.addTextChangedListener(new MyTextWatcher(lte_cid,String.valueOf(para.getCid()),HandleRecvXmlMsg.LTE_CELL_CONFIG,0,Integer.MAX_VALUE));
        if (mode.equals(DeviceDataStruct.MODE.WCDMA)) {
            lte_cid.setVisibility(View.GONE);
        }

        lte_tac = (EditText) contentView.findViewById(R.id.lte_tac);
        lte_tac.setText(String.valueOf(para.getTac()));
        lte_tac.addTextChangedListener(new MyTextWatcher(lte_tac,String.valueOf(para.getTac()),HandleRecvXmlMsg.LTE_CELL_CONFIG,0,65535));

        lte_mcc = (EditText) contentView.findViewById(R.id.lte_mcc);
        lte_mcc.setText(String.valueOf(para.getMcc()));
        lte_mcc.addTextChangedListener(new MyTextWatcher(lte_mcc,String.valueOf(para.getMcc()),HandleRecvXmlMsg.LTE_CELL_CONFIG,0,999));

        lte_mnc = (EditText) contentView.findViewById(R.id.lte_mnc);
        lte_mnc.setText(String.valueOf(para.getMnc()));
        lte_mnc.addTextChangedListener(new MyTextWatcher(lte_mnc,String.valueOf(para.getMnc()),HandleRecvXmlMsg.LTE_CELL_CONFIG,0,999));

        lte_power = (EditText) contentView.findViewById(R.id.lte_txpower);
        lte_power.setText(String.valueOf(para.getPower()));
        lte_power.addTextChangedListener(new MyTextWatcher(lte_power,String.valueOf(para.getPower()),HandleRecvXmlMsg.LTE_CELL_CONFIG,Integer.MIN_VALUE,Integer.MAX_VALUE));

        lte_periodtac = (EditText) contentView.findViewById(R.id.lte_periodTac);
        lte_periodtac.setText(String.valueOf(para.getPeriodtac()));
        lte_periodtac.addTextChangedListener(new MyTextWatcher(lte_periodtac,String.valueOf(para.getPeriodtac()),HandleRecvXmlMsg.LTE_CELL_CONFIG,0,Integer.MAX_VALUE));

        String[] name={"5","10","15","20"};
        lte_bw = (Spinner) contentView.findViewById(R.id.lte_bw);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,R.layout.my_spinner,name);
        lte_bw.setAdapter(adapter);
        index = adapter.getPosition(String.valueOf(para.getBandwitch()));
        lte_bw.setSelection(index ,true);
        lte_bw.setOnItemSelectedListener(new MyOnItemSelectedListener(lte_bw,String.valueOf(para.getBandwitch()),HandleRecvXmlMsg.LTE_CELL_CONFIG));
        if (mode.equals(DeviceDataStruct.MODE.WCDMA)) {
            lte_bw.setVisibility(View.GONE);
        }
    }

    private void init_lte_son_info(String mode,LTE_GeneralPara para) {
        if (para == null) return;

        lte_sonEarfcn = (EditText) contentView.findViewById(R.id.lte_son_earfcn_list);
        lte_sonEarfcn.setText(para.getEarfcnlist());
        lte_sonEarfcn.addTextChangedListener(new MyTextWatcher(lte_sonEarfcn,para.getEarfcnlist(),HandleRecvXmlMsg.LTE_SON_CONFIG));
        if (mode.equals(DeviceDataStruct.MODE.WCDMA)) {
            ((LinearLayout) contentView.findViewById(R.id.lte_son_earfcn)).setVisibility(View.GONE);
        }
    }

    private void init_lte_otherPlmn(String mode,LTE_GeneralPara para) {
        if (para == null) return;

        lte_otherPlmn = (EditText) contentView.findViewById(R.id.lte_plmnList);
        lte_otherPlmn.setText(para.getOtherplmn());
        lte_otherPlmn.addTextChangedListener(new MyTextWatcher(lte_otherPlmn, para.getOtherplmn(),HandleRecvXmlMsg.LTE_OTHER_PLMN));
        if (mode.equals(DeviceDataStruct.MODE.WCDMA)) {
            ((LinearLayout) contentView.findViewById(R.id.lte_other_plmn)).setVisibility(View.GONE);
        }
    }

    private void init_lte_periodFreq(String mode,LTE_GeneralPara para) {
        if (para == null) return;

        lte_periodFreqTime = (EditText) contentView.findViewById(R.id.lte_periodFreqTime);
        lte_periodFreqTime.setText(String.valueOf(para.getPeriodFreqTime()));
        lte_periodFreqTime.addTextChangedListener(new MyTextWatcher(lte_periodFreqTime,String.valueOf(para.getPeriodFreqTime()),HandleRecvXmlMsg.LTE_PERIOD_FREQ,0,Integer.MAX_VALUE));


        lte_periodFreqFreq = (EditText) contentView.findViewById(R.id.lte_periodFreqFreq);
        lte_periodFreqFreq.setText(para.getPeriodFreqFreq());
        lte_periodFreqFreq.addTextChangedListener(new MyTextWatcher(lte_periodFreqFreq, para.getPeriodFreqFreq(), HandleRecvXmlMsg.LTE_PERIOD_FREQ));
        if (mode.equals(DeviceDataStruct.MODE.WCDMA)) {
            ((LinearLayout) contentView.findViewById(R.id.lte_period_freq)).setVisibility(View.GONE);
        }
    }

    private void init_lte_systemSet(String mode,LTE_GeneralPara para) {
        int index;
        if (para == null) return;

        lte_ntpServer = (EditText) contentView.findViewById(R.id.lte_ntpServer);
        lte_ntpServer.setText(para.getNtpServer());
        lte_ntpServer.addTextChangedListener(new MyTextWatcher(lte_ntpServer,para.getNtpServer(),HandleRecvXmlMsg.LTE_SYSTEM_SET));

        String[] pri={"1","2","3","4","5","6","7","8","9"};
        lte_ntpPri = (Spinner) contentView.findViewById(R.id.lte_ntpPri);
        ArrayAdapter<String> adapterPri = new ArrayAdapter<String>(context,R.layout.my_spinner,pri);
        lte_ntpPri.setAdapter(adapterPri);
        index = adapterPri.getPosition(String.valueOf(para.getNtppri()));
        lte_ntpPri.setSelection(index ,true);
        lte_ntpPri.setOnItemSelectedListener(new MyOnItemSelectedListener(lte_ntpPri,String.valueOf(para.getNtppri()),HandleRecvXmlMsg.LTE_SYSTEM_SET));

        String[] gps={"无GSP设备","有GPS设备"};
        lte_gpsSelect = (Spinner) contentView.findViewById(R.id.lte_gpsEnable);
        ArrayAdapter<String> adapterGps = new ArrayAdapter<String>(context,R.layout.my_spinner,gps);
        lte_gpsSelect.setAdapter(adapterGps);
        if(para.getGps_select() == 1) {
            index = 1;
        } else {
            index = 0;
        }
        lte_gpsSelect.setSelection(index ,true);
        lte_gpsSelect.setOnItemSelectedListener(new MyOnItemSelectedListener(lte_gpsSelect,gps[index],HandleRecvXmlMsg.LTE_SYSTEM_SET));

        lte_bandOffset = (EditText) contentView.findViewById(R.id.lte_gpsOffset);
        lte_bandOffset.setText(para.getBandoffset());
        lte_bandOffset.addTextChangedListener(new MyTextWatcher(lte_bandOffset, para.getBandoffset(), HandleRecvXmlMsg.LTE_SYSTEM_SET));
    }

    private void init_lte_syncSet(String mode,LTE_GeneralPara para) {
        int index;
        if (para == null) return;

        String[] source={"GPS","CNM","未同步"};
        lte_source = (Spinner) contentView.findViewById(R.id.lte_syncSource);
        ArrayAdapter<String> adapterSource = new ArrayAdapter<String>(context,R.layout.my_spinner,source);
        lte_source.setAdapter(adapterSource);
        index = adapterSource.getPosition(source[para.getSource()]);
        lte_source.setSelection(index ,true);
        lte_source.setOnItemSelectedListener(new MyOnItemSelectedListener(lte_source,source[para.getSource()],HandleRecvXmlMsg.LTE_SYNC_SET));

        String[] manual = {"关闭","开启"};
        lte_MEnable = (Spinner) contentView.findViewById(R.id.lte_manualSync);
        ArrayAdapter<String> adapterManual = new ArrayAdapter<String>(context,R.layout.my_spinner,manual);
        lte_MEnable.setAdapter(adapterManual);
        index = adapterManual.getPosition(manual[para.getManualEnable()]);
        lte_MEnable.setSelection(index ,true);
        lte_MEnable.setOnItemSelectedListener(new MyOnItemSelectedListener(lte_MEnable,manual[para.getManualEnable()],HandleRecvXmlMsg.LTE_SYNC_SET));

        lte_MEarfcn = (EditText) contentView.findViewById(R.id.lte_manualSyncFreq);
        lte_MEarfcn.setText(String.valueOf(para.getManualEarfcn()));
        lte_MEarfcn.addTextChangedListener(new MyTextWatcher(lte_MEarfcn,String.valueOf(para.getManualEarfcn()),HandleRecvXmlMsg.LTE_SYNC_SET,0,65535));


        lte_MPci = (EditText) contentView.findViewById(R.id.lte_manualSyncPci);
        lte_MPci.setText(String.valueOf(para.getManualPci()));
        lte_MPci.addTextChangedListener(new MyTextWatcher(lte_MPci, String.valueOf(para.getManualPci()), HandleRecvXmlMsg.LTE_SYNC_SET,0,512));

        String[] name={"5","10","15","20"};
        lte_MBw = (Spinner) contentView.findViewById(R.id.lte_manualSyncBw);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,R.layout.my_spinner,name);
        lte_MBw.setAdapter(adapter);
        index = adapter.getPosition(String.valueOf(para.getManualBw()));
        lte_MBw.setSelection(index ,true);
        lte_MBw.setOnItemSelectedListener(new MyOnItemSelectedListener(lte_MBw,String.valueOf(para.getManualBw()),HandleRecvXmlMsg.LTE_SYNC_SET));
        if (mode.equals(DeviceDataStruct.MODE.WCDMA)) {
            ((LinearLayout) contentView.findViewById(R.id.lte_sync_info)).setVisibility(View.GONE);
        }
    }

    private class  MyTextWatcher implements TextWatcher {
        private String defultValue;
        private boolean checkRandge;
        private int MinValue;
        private int MaxValue;
        private EditText et;
        private int configFlag;
        private int vId;

        public MyTextWatcher(EditText et,String defultValue,int flag) {
            this.vId = viewId++;
            viewList.add(false);
            this.et = et;
            this.configFlag = flag;
            this.defultValue = defultValue;
            this.checkRandge = false;
        }
        public MyTextWatcher(EditText et,String defultValue,int flag,int minValue,int maxValue) {
            this.vId =viewId++;
            viewList.add(false);
            this.et = et;
            this.configFlag = flag;
            this.defultValue = defultValue;
            this.checkRandge = true;
            this.MinValue = minValue;
            this.MaxValue = maxValue;
        }
        // 输入文本之前的状态
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        // 输入文本中的状态
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        // 输入文本之后的状态
        @Override
        public void afterTextChanged(Editable s) {
            Logs.d(TAG,"输入后值："+ s + ";默认值:" + defultValue);
            if (s.toString().equals(defultValue)) {
                et.setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.colorHalfTransparent));
                changeList[configFlag] --;
            } else {
                changeList[configFlag] ++;
                if (checkRandge) {
                    try {
                        int value = Integer.parseInt(s.toString());
                        if (value >= MinValue && value<=MaxValue) {  //范围内
                            et.setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.colorHalfDialogTitle));
                            //Logs.d(TAG,"ViewId=" + vId + ";set false");
                            viewList.set(vId, false);
                        } else {
                            et.setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.colorAccent));
                            CustomToast.showToast(context, String.format("输入值(%d)不在[%d-%d]范围内",value,MinValue,MaxValue));
                            //Logs.d(TAG,"ViewId=" + vId + ";set true");
                            viewList.set(vId, true);
                        }
                    } catch (Exception e) {}
                } else {
                    et.setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.colorHalfDialogTitle));
                }
            }
        }
    }

    class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        private String defultValue;
        private int configFlag;
        private int vId;
        private Spinner sp;

        public MyOnItemSelectedListener(Spinner sp,String defult,int flag) {
            this.vId =viewId++;
            this.sp = sp;
            this.configFlag = flag;
            this.defultValue = defult;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Logs.d(TAG, "您选择了:" + sp.getSelectedItem().toString());
            //TextView tv = (TextView)contentView.findViewById(android.R.vId.text1);
            if (sp.getSelectedItem().toString().equals(defultValue)) {
                //tv.setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.colorHalfTransparent));
                changeList[configFlag] --;
            } else {
                //tv.setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.colorHalfDialogTitle));
                changeList[configFlag] ++;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }


    private Handler handler = new SerializableHandler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    WaitDialog waitDialog = new WaitDialog(context);;
                    waitDialog.setList(sendDateList);
                    waitDialog.show();
                    break;

                default:
                    break;
            }

        }
    };

}
