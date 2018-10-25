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
import android.widget.TextView;

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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 2018-10-17.
 */

public class Fragment_DeviceBaseSet extends RevealAnimationBaseFragment {
    private final String TAG = "Fragment_DeviceBaseSet";
    private final int OPEN_DIALOG = 0;
    private final int SEND_LTE_COMMAND = 1;
    private final int SEND_HJT_COMMAND = 2;
    private final int SEND_ZYF_COMMAND = 3;

    private static int viewId = 0;

    //保存所有控件是否修改，修改后值是否在范围内
    private HashMap<Integer,ValueChange> ViewChangeList = new HashMap<Integer,ValueChange>() ;
    //保存修改过的Config数组，每个Config表示要下发的命令集合
    private ArrayList<Integer> ChangeConfigList = new ArrayList<Integer>();

    //所有上线的设备列表
    private ArrayList<String> dList;
    //保存修改后lte设备的参数值
    private LTE_GeneralPara lteGeneralPara = null;
    //private boolean valueError = false;
    private ArrayList<WaitDialogData> sendDateList = null;
    //保存注册过的EditText控件及事件
    private HashMap<EditText,MyTextWatcher> editTextArray = new HashMap<EditText,MyTextWatcher>();
    private HashMap<Spinner,MyOnItemSelectedListener> spinnerArray = new HashMap<Spinner,MyOnItemSelectedListener>();

    //保存选择的设备数据
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


    public class ValueChange {
        private int flag;
        private boolean changes;
        private boolean outRang;

        public ValueChange(int flag) {
            this.flag = flag;
            this.changes = false;
            this.outRang = false;
        }

        /*public ValueChange(int flag,Boolean change) {
            this.flag = flag;
            this.changes = change;
            this.
        }*/

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

        public boolean isChanges() {
            return changes;
        }

        public void setChanges(boolean changes) {
            this.changes = changes;
        }

        public boolean isOutRang() {
            return outRang;
        }

        public void setOutRang(boolean outRang) {
            this.outRang = outRang;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logs.d(TAG, "onCreate",true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_device_set_base);

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
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
        lteGeneralPara = new LTE_GeneralPara();
        //changeList = new int[HandleRecvXmlMsg.MAX_CONFIG];
        //viewList = new ArrayList<Boolean>();
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
        EventBus.getDefault().unregister(this);
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

    private void registerEditView(EditText et,String defult,int flag,int min,int max){
        if (et == null) {
            Logs.e(TAG,"EditText控件未赋值。");
            return;
        }
        if (editTextArray.containsKey(et)) {
            editTextArray.get(et).setDeflut(defult);
        } else {  //未注册
            MyTextWatcher mtw = new MyTextWatcher(et,defult,flag,min,max);
            et.addTextChangedListener(mtw);
            editTextArray.put(et,mtw);
        }
        et.setText(defult);
        et.setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.colorHalfTransparent));
    }

    private void registerEditView(EditText et,String defult,int flag){
        if (et == null) {
            Logs.e(TAG,"EditText控件未赋值。");
            return;
        }
        if (editTextArray.containsKey(et)) {
            editTextArray.get(et).setDeflut(defult);
        } else {  //未注册
            MyTextWatcher mtw = new MyTextWatcher(et,defult,flag);
            et.addTextChangedListener(mtw);
            editTextArray.put(et,mtw);
        }
        et.setText(defult);
        et.setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.colorHalfTransparent));
    }

    private void registerSpinner(Spinner sp,int index,int flag){
        if (sp == null) {
            Logs.e(TAG,"Spinner控件未赋值。");
            return;
        }

        if (spinnerArray.containsKey(sp)) {
            spinnerArray.get(sp).setDeflut(sp.getAdapter().getItem(index).toString());
        } else { //未注册
            MyOnItemSelectedListener listen = new MyOnItemSelectedListener(sp,sp.getAdapter().getItem(index).toString(),flag);
            sp.setOnItemSelectedListener(listen);
            spinnerArray.put(sp,listen);
        }
        sp.setSelection(index ,true);
    }

    private boolean checkChanges(){
        ChangeConfigList.clear();
        for(int i =0;i<ViewChangeList.size();i++) {
            //Logs.d(TAG,String.format("ViewId=%d",i));
            if (ViewChangeList.get(i).isChanges()) {

                Logs.d(TAG,String.format("标志%d修改了",ViewChangeList.get(i).getFlag()));
                if (!ChangeConfigList.contains(ViewChangeList.get(i).getFlag()))
                    ChangeConfigList.add(ViewChangeList.get(i).getFlag());

                if (ViewChangeList.get(i).isOutRang()) {
                    openDialog("参数不在范围内，请重新输入正确参数");
                    return false;
                }
            }
        }

        sendDateList.clear();
        for(int i = 0;i< ChangeConfigList.size();i++) {
            int configId = ChangeConfigList.get(i);
            if (configId == HandleRecvXmlMsg.LTE_WORKE_MODE) {
                sendDateList.add(new WaitDialogData(HandleRecvXmlMsg.LTE_WORKE_MODE,"工作模式"));
            } else if (configId == HandleRecvXmlMsg.LTE_CELL_CONFIG) {
                sendDateList.add(new WaitDialogData(HandleRecvXmlMsg.LTE_CELL_CONFIG,"小区配置"));
            } else if (configId == HandleRecvXmlMsg.LTE_SON_CONFIG) {
                sendDateList.add(new WaitDialogData(HandleRecvXmlMsg.LTE_SON_CONFIG,"扫频配置"));
            } else if (configId == HandleRecvXmlMsg.LTE_OTHER_PLMN) {
                sendDateList.add(new WaitDialogData(HandleRecvXmlMsg.LTE_OTHER_PLMN,"等效PLMN"));
            } else if (configId == HandleRecvXmlMsg.LTE_PERIOD_FREQ) {
                sendDateList.add(new WaitDialogData(HandleRecvXmlMsg.LTE_PERIOD_FREQ,"周期变频"));
            } else if (configId == HandleRecvXmlMsg.LTE_SYSTEM_SET) {
                sendDateList.add(new WaitDialogData(HandleRecvXmlMsg.LTE_SYSTEM_SET,"系统配置"));
            } else if (configId == HandleRecvXmlMsg.LTE_SYNC_SET) {
                sendDateList.add(new WaitDialogData(HandleRecvXmlMsg.LTE_SYNC_SET,"同步配置"));
            }
        }

        if (sendDateList.size() <= 0) {
            CustomToast.showToast(context, "没有配置需要修改");
            return false;
        }

        Message message = new Message();
        message.what = OPEN_DIALOG;
        handler.sendMessage(message);

        return true;
    }

    private boolean saveData() {

        if (!checkChanges()) return false;

        new Thread() {
            @Override
            public void run() {
                super.run();
                Utils.mySleep(2000);//休眠3秒
                String mode = deviceDate.getMode();
                if (mode.equals(DeviceDataStruct.MODE.LTE_TDD)
                        || mode.equals(DeviceDataStruct.MODE.LTE_FDD)
                        || mode.equals(DeviceDataStruct.MODE.WCDMA)) {
                     saveLteData();
                } else if (mode.equals(DeviceDataStruct.MODE.CDMA)
                        || mode.equals(DeviceDataStruct.MODE.GSM_V2)) {

                } else if (mode.equals(DeviceDataStruct.MODE.GSM)) {

                }
            }
        }.start();

        return true;
    }

    private boolean saveLteData() {
        //LTE_GeneralPara lteGeneralPara = new LTE_GeneralPara();
        lteGeneralPara.setBootmode(lte_selectmode.getSelectedItemPosition());
        lteGeneralPara.setManualfreq(lte_selectFreq.getSelectedItemPosition());

        lteGeneralPara.setEarfcndl(Integer.parseInt(lte_earfcn.getText().toString()));
        lteGeneralPara.setPci(Integer.parseInt(lte_pci.getText().toString()));
        lteGeneralPara.setBandwitch(Integer.parseInt(lte_bw.getSelectedItem().toString()));
        lteGeneralPara.setCid(Integer.parseInt(lte_cid.getText().toString()));
        lteGeneralPara.setMcc(Integer.parseInt(lte_mcc.getText().toString()));
        lteGeneralPara.setMnc(Integer.parseInt(lte_mnc.getText().toString()));
        lteGeneralPara.setTac(Integer.parseInt(lte_tac.getText().toString()));
        lteGeneralPara.setPower(Integer.parseInt(lte_power.getText().toString()));
        lteGeneralPara.setPeriodtac(Integer.parseInt(lte_periodtac.getText().toString()));

        lteGeneralPara.setEarfcnlist(lte_sonEarfcn.getText().toString());

        lteGeneralPara.setOtherplmn(lte_otherPlmn.getText().toString());

        lteGeneralPara.setPeriodFreqTime(Integer.parseInt(lte_periodFreqTime.getText().toString()));
        lteGeneralPara.setPeriodFreqFreq(lte_periodFreqFreq.getText().toString());

        lteGeneralPara.setNtpServer(lte_ntpServer.getText().toString());
        lteGeneralPara.setNtppri(Integer.parseInt(lte_ntpPri.getSelectedItem().toString()));
        lteGeneralPara.setGps_select(lte_gpsSelect.getSelectedItemPosition());
        lteGeneralPara.setBandoffset(lte_bandOffset.getText().toString());

        lteGeneralPara.setSource(lte_source.getSelectedItemPosition());
        lteGeneralPara.setManualEnable(lte_MEnable.getSelectedItemPosition());
        lteGeneralPara.setManualEarfcn(Integer.parseInt(lte_MEarfcn.getText().toString()));
        lteGeneralPara.setManualPci(Integer.parseInt(lte_MPci.getText().toString()));
        lteGeneralPara.setManualBw(Integer.parseInt(lte_MBw.getSelectedItem().toString()));


        new Thread() {
            @Override
            public void run() {
                super.run();
                for(int i = 0;i< ChangeConfigList.size();i++) {
                    int configId = ChangeConfigList.get(i);

                    Message message = new Message();
                    message.what = SEND_LTE_COMMAND;
                    Bundle bundle = new Bundle();
                    bundle.putInt("CONFIG_ID",configId);
                    message.setData(bundle);
                    handler.sendMessage(message);

                    Utils.mySleep(3000);
                }
            }
        }.start();

        return true;
    }

    private boolean SendLteCommand(LTE_GeneralPara para,int configId) {
        if (configId == HandleRecvXmlMsg.LTE_WORKE_MODE) {
            //ChangeConfigList[HandleRecvXmlMsg.LTE_WORKE_MODE] = 0;
            Logs.d(TAG, "修改配置LTE_WORKE_MODE");
            new LTE(context).SetWorkMode(deviceDate.getIp(), deviceDate.getPort(), para);
        }
        if (configId == HandleRecvXmlMsg.LTE_CELL_CONFIG) {
            //ChangeConfigList[HandleRecvXmlMsg.LTE_CELL_CONFIG] = 0;
            Logs.d(TAG, "修改配置LTE_CELL_CONFIG");
            new LTE(context).SetConfiguration(deviceDate.getIp(), deviceDate.getPort(), deviceDate.getMode(), para);
        }
        if (configId == HandleRecvXmlMsg.LTE_SON_CONFIG) {
            //ChangeConfigList[HandleRecvXmlMsg.LTE_SON_CONFIG] = 0;
            Logs.d(TAG, "修改配置LTE_SON_CONFIG");
            new LTE(context).SetSonEarfcn(deviceDate.getIp(), deviceDate.getPort(), para);
        }
        if (configId == HandleRecvXmlMsg.LTE_OTHER_PLMN) {
            //ChangeConfigList[HandleRecvXmlMsg.LTE_OTHER_PLMN] = 0;
            Logs.d(TAG, "修改配置LTE_OTHER_PLMN");
            new LTE(context).SetApParameter(deviceDate.getIp(), deviceDate.getPort(),
                    "CFG_OTHER_PLMN", para.getOtherplmn());
        }
        if (configId == HandleRecvXmlMsg.LTE_PERIOD_FREQ) {
            //ChangeConfigList[HandleRecvXmlMsg.LTE_PERIOD_FREQ] = 0;
            Logs.d(TAG, "修改配置LTE_PERIOD_FREQ");
            new LTE(context).SetApParameter(deviceDate.getIp(), deviceDate.getPort(),
                    "CFG_PERIOD_FREQ",
                    String.format("%d:%s", para.getPeriodFreqTime(), para.getPeriodFreqFreq()));
        }
        if (configId == HandleRecvXmlMsg.LTE_SYSTEM_SET) {
            //ChangeConfigList[HandleRecvXmlMsg.LTE_SYSTEM_SET] = 0;
            Logs.d(TAG, "修改配置LTE_SYSTEM_SET");
            new LTE(context).SetSystemRequest(deviceDate.getIp(), deviceDate.getPort(), para);
        }
        if (configId == HandleRecvXmlMsg.LTE_SYNC_SET) {
            //ChangeConfigList[HandleRecvXmlMsg.LTE_SYNC_SET] = 0;
            Logs.d(TAG, "修改配置LTE_SYNC_SET");
            new LTE(context).SetSyncInfo(deviceDate.getIp(), deviceDate.getPort(), para);

        }

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
        registerSpinner(lte_selectFreq,index,HandleRecvXmlMsg.LTE_WORKE_MODE);

        lte_selectmode = (Spinner) contentView.findViewById(R.id.lte_boot);
        String[] modeList={"半自动","全自动"};
        adapter = new ArrayAdapter<String>(context,R.layout.my_spinner,modeList);
        lte_selectmode.setAdapter(adapter);
        if (para.getBootmode() == 1) {
            index = 1;
        } else {
            index = 0;
        }
        registerSpinner(lte_selectmode,index,HandleRecvXmlMsg.LTE_WORKE_MODE);

    }

    private void init_lte_cellSet(String mode,LTE_GeneralPara para){
        int index;
        if (para == null) return;

        lte_earfcn = (EditText) contentView.findViewById(R.id.lte_earfcn);
        registerEditView(lte_earfcn,String.valueOf(para.getEarfcndl()),HandleRecvXmlMsg.LTE_CELL_CONFIG,0,65535);

        lte_pci = (EditText) contentView.findViewById(R.id.lte_pci);
        registerEditView(lte_pci,String.valueOf(para.getPci()),HandleRecvXmlMsg.LTE_CELL_CONFIG,0,512);

        lte_cid = (EditText) contentView.findViewById(R.id.lte_cellid);
        if (mode.equals(DeviceDataStruct.MODE.WCDMA)) {
            lte_cid.setVisibility(View.GONE);
        } else {
            registerEditView(lte_cid,String.valueOf(para.getCid()),HandleRecvXmlMsg.LTE_CELL_CONFIG,0,Integer.MAX_VALUE);
        }

        lte_tac = (EditText) contentView.findViewById(R.id.lte_tac);
        registerEditView(lte_tac,String.valueOf(para.getTac()),HandleRecvXmlMsg.LTE_CELL_CONFIG,0,65535);

        lte_mcc = (EditText) contentView.findViewById(R.id.lte_mcc);
        registerEditView(lte_mcc,String.valueOf(para.getMcc()),HandleRecvXmlMsg.LTE_CELL_CONFIG,0,999);

        lte_mnc = (EditText) contentView.findViewById(R.id.lte_mnc);
        registerEditView(lte_mnc,String.valueOf(para.getMnc()),HandleRecvXmlMsg.LTE_CELL_CONFIG,0,999);

        lte_power = (EditText) contentView.findViewById(R.id.lte_txpower);
        registerEditView(lte_power,String.valueOf(para.getPower()),HandleRecvXmlMsg.LTE_CELL_CONFIG,Integer.MIN_VALUE,Integer.MAX_VALUE);

        lte_periodtac = (EditText) contentView.findViewById(R.id.lte_periodTac);
        registerEditView(lte_periodtac,String.valueOf(para.getPeriodtac()),HandleRecvXmlMsg.LTE_CELL_CONFIG,0,Integer.MAX_VALUE);

        lte_bw = (Spinner) contentView.findViewById(R.id.lte_bw);
        if (mode.equals(DeviceDataStruct.MODE.WCDMA)) {
            lte_bw.setVisibility(View.GONE);
        } else {
            String[] name={"5","10","15","20"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,R.layout.my_spinner,name);
            lte_bw.setAdapter(adapter);
            index = adapter.getPosition(String.valueOf(para.getBandwitch()));
            registerSpinner(lte_bw,index,HandleRecvXmlMsg.LTE_CELL_CONFIG);
        }
    }

    private void init_lte_son_info(String mode,LTE_GeneralPara para) {
        if (para == null) return;
        lte_sonEarfcn = (EditText) contentView.findViewById(R.id.lte_son_earfcn_list);
        /*lte_sonEarfcn.setText(para.getEarfcnlist());
        lte_sonEarfcn.addTextChangedListener(new MyTextWatcher(lte_sonEarfcn,para.getEarfcnlist(),HandleRecvXmlMsg.LTE_SON_CONFIG));*/
        if (mode.equals(DeviceDataStruct.MODE.WCDMA)) {
            ((LinearLayout) contentView.findViewById(R.id.lte_son_earfcn)).setVisibility(View.GONE);
        } else {
            registerEditView(lte_sonEarfcn,para.getEarfcnlist(),HandleRecvXmlMsg.LTE_SON_CONFIG);
        }
    }

    private void init_lte_otherPlmn(String mode,LTE_GeneralPara para) {
        if (para == null) return;

        lte_otherPlmn = (EditText) contentView.findViewById(R.id.lte_plmnList);
        if (mode.equals(DeviceDataStruct.MODE.WCDMA)) {
            ((LinearLayout) contentView.findViewById(R.id.lte_other_plmn)).setVisibility(View.GONE);
        } else {
            registerEditView(lte_otherPlmn, para.getOtherplmn(),HandleRecvXmlMsg.LTE_OTHER_PLMN);
        }
    }

    private void init_lte_periodFreq(String mode,LTE_GeneralPara para) {
        if (para == null) return;

        if (mode.equals(DeviceDataStruct.MODE.WCDMA)) {
            ((LinearLayout) contentView.findViewById(R.id.lte_period_freq)).setVisibility(View.GONE);
        } else {
            lte_periodFreqTime = (EditText) contentView.findViewById(R.id.lte_periodFreqTime);
            registerEditView(lte_periodFreqTime,String.valueOf(para.getPeriodFreqTime()),HandleRecvXmlMsg.LTE_PERIOD_FREQ,0,Integer.MAX_VALUE);

            lte_periodFreqFreq = (EditText) contentView.findViewById(R.id.lte_periodFreqFreq);
            registerEditView(lte_periodFreqFreq, para.getPeriodFreqFreq(), HandleRecvXmlMsg.LTE_PERIOD_FREQ);
        }
    }

    private void init_lte_systemSet(String mode,LTE_GeneralPara para) {
        int index;
        if (para == null) return;

        lte_ntpServer = (EditText) contentView.findViewById(R.id.lte_ntpServer);
        registerEditView(lte_ntpServer,para.getNtpServer(),HandleRecvXmlMsg.LTE_SYSTEM_SET);

        String[] pri={"1","2","3","4","5","6","7","8","9"};
        lte_ntpPri = (Spinner) contentView.findViewById(R.id.lte_ntpPri);
        ArrayAdapter<String> adapterPri = new ArrayAdapter<String>(context,R.layout.my_spinner,pri);
        lte_ntpPri.setAdapter(adapterPri);
        index = adapterPri.getPosition(String.valueOf(para.getNtppri()));
        registerSpinner(lte_ntpPri,index,HandleRecvXmlMsg.LTE_SYSTEM_SET);

        String[] gps={"无GSP设备","有GPS设备"};
        lte_gpsSelect = (Spinner) contentView.findViewById(R.id.lte_gpsEnable);
        ArrayAdapter<String> adapterGps = new ArrayAdapter<String>(context,R.layout.my_spinner,gps);
        lte_gpsSelect.setAdapter(adapterGps);
        if(para.getGps_select() == 1) {
            index = 1;
        } else {
            index = 0;
        }
        registerSpinner(lte_gpsSelect,index,HandleRecvXmlMsg.LTE_SYSTEM_SET);

        lte_bandOffset = (EditText) contentView.findViewById(R.id.lte_gpsOffset);
        registerEditView(lte_bandOffset, para.getBandoffset(), HandleRecvXmlMsg.LTE_SYSTEM_SET);
    }

    private void init_lte_syncSet(String mode,LTE_GeneralPara para) {
        int index;
        if (para == null) return;

        if (mode.equals(DeviceDataStruct.MODE.WCDMA)) {
            ((LinearLayout) contentView.findViewById(R.id.lte_sync_info)).setVisibility(View.GONE);
        } else {
            String[] source={"GPS","CNM","未同步"};
            lte_source = (Spinner) contentView.findViewById(R.id.lte_syncSource);
            ArrayAdapter<String> adapterSource = new ArrayAdapter<String>(context,R.layout.my_spinner,source);
            lte_source.setAdapter(adapterSource);
            index = adapterSource.getPosition(source[para.getSource()]);
            registerSpinner(lte_source,index,HandleRecvXmlMsg.LTE_SYNC_SET);

            String[] manual = {"关闭","开启"};
            lte_MEnable = (Spinner) contentView.findViewById(R.id.lte_manualSync);
            ArrayAdapter<String> adapterManual = new ArrayAdapter<String>(context,R.layout.my_spinner,manual);
            lte_MEnable.setAdapter(adapterManual);
            index = adapterManual.getPosition(manual[para.getManualEnable()]);
            registerSpinner(lte_MEnable,index,HandleRecvXmlMsg.LTE_SYNC_SET);

            lte_MEarfcn = (EditText) contentView.findViewById(R.id.lte_manualSyncFreq);
            registerEditView(lte_MEarfcn,String.valueOf(para.getManualEarfcn()),HandleRecvXmlMsg.LTE_SYNC_SET,0,65535);

            lte_MPci = (EditText) contentView.findViewById(R.id.lte_manualSyncPci);
            registerEditView(lte_MPci, String.valueOf(para.getManualPci()), HandleRecvXmlMsg.LTE_SYNC_SET,0,512);

            String[] name={"5","10","15","20"};
            lte_MBw = (Spinner) contentView.findViewById(R.id.lte_manualSyncBw);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,R.layout.my_spinner,name);
            lte_MBw.setAdapter(adapter);
            index = adapter.getPosition(String.valueOf(para.getManualBw()));
            registerSpinner(lte_MBw,index,HandleRecvXmlMsg.LTE_SYNC_SET);
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
            this.et = et;
            this.configFlag = flag;
            this.defultValue = defultValue;
            this.checkRandge = false;
            ViewChangeList.put(this.vId,new ValueChange(this.configFlag));
        }
        public MyTextWatcher(EditText et,String defultValue,int flag,int minValue,int maxValue) {
            this.vId =viewId++;
            this.et = et;
            this.configFlag = flag;
            this.defultValue = defultValue;
            this.checkRandge = true;
            this.MinValue = minValue;
            this.MaxValue = maxValue;
            ViewChangeList.put(this.vId,new ValueChange(this.configFlag));
        }

        public void setDeflut(String defult) {
            this.defultValue = defult;
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
            Logs.d(TAG,String.format("标志(%d)Id(%d):输入后值：%s;默认值:%s",configFlag,vId,s,defultValue));
            if (s.toString().equals(defultValue)) {
                et.setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.colorHalfTransparent));
                ViewChangeList.get(vId).setChanges(false);
            } else {
                ViewChangeList.get(vId).setChanges(true);
                if (checkRandge) {
                    try {
                        int value = Integer.parseInt(s.toString());
                        if (value >= MinValue && value<=MaxValue) {  //范围内
                            et.setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.colorHalfDialogTitle));
                            //Logs.d(TAG,"ViewId=" + vId + ";set false");
                            ViewChangeList.get(vId).setOutRang(false);
                        } else {
                            et.setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.colorAccent));
                            CustomToast.showToast(context, String.format("输入值(%d)不在[%d-%d]范围内",value,MinValue,MaxValue));
                            //Logs.d(TAG,"ViewId=" + vId + ";set true");
                            ViewChangeList.get(vId).setOutRang(true);
                        }
                    } catch (Exception e) {}
                } else {
                    et.setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.colorHalfDialogTitle));
                }
            }
        }
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
                LTE_GeneralPara para = (LTE_GeneralPara) deviceDate.getGeneralPara();
                SelectLteInit(mode,para);

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
            ViewChangeList.put(this.vId,new ValueChange(this.configFlag));
        }

        public void setDeflut(String defult) {
            this.defultValue = defult;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Logs.d(TAG, String.format("标志(%d)Id(%d):您选择了:%s",configFlag,vId,sp.getSelectedItem().toString()));
            TextView tv = (TextView) view;
            if (sp.getSelectedItem().toString().equals(defultValue)) {
                tv.setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.colorHalfTransparent));
                ViewChangeList.get(vId).setChanges(false);
            } else {
                tv.setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.colorHalfDialogTitle));
                ViewChangeList.get(vId).setChanges(true);
            }
            //tv.setGravity(Gravity.CENTER);
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
                case OPEN_DIALOG:
                    WaitDialog waitDialog = new WaitDialog(context);
                    waitDialog.setList(sendDateList);
                    waitDialog.show();
                    break;

                case SEND_LTE_COMMAND:
                    int configId = msg.getData().getInt("CONFIG_ID");
                    SendLteCommand(lteGeneralPara,configId);
                    break;

                default:
                    break;
            }
        }
    };


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ChangesSendStatus(LTE_GeneralPara para) {
        if (para.getSn().equals(deviceDate.getSN())) {
            Logs.d(TAG, "接收到参数改变改变事件", true, true);
            SelectLteInit(deviceDate.getMode(), para);
        }
    }

}
