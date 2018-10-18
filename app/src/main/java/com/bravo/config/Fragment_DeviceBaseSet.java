package com.bravo.config;

import android.os.Bundle;
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
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.utils.Logs;
import com.bravo.xml.LTE_GeneralPara;

import java.util.ArrayList;


/**
 * Created by admin on 2018-10-17.
 */

public class Fragment_DeviceBaseSet extends RevealAnimationBaseFragment {
    private final String TAG = "Fragment_DeviceBaseSet";
    private final int LTE_CELL_CONFIG = 0;
    private final int LTE_SON_CONFIG = 1;
    private final int LTE_WORKE_MODE = 2;
    private final int MAX_CONFIG = 3;

    private int[] changeList;

    private ArrayList<String> dList;
    private LTE_GeneralPara lte_GeneralPara = null;
    private boolean valueError = false;

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
        changeList = new int[MAX_CONFIG];
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
                if (saveData()) {
                    CustomToast.showToast(context, "参数配置成功！");
                }
            }
        });
    }

    @Override
    public void onPause() {
        Logs.d(TAG, "onPause",true);
        saveData();
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

    private void openDialog(String str)
    {
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

    private boolean saveData() {
        if (valueError) {
            openDialog("参数不在范围内，请重新输入正确参数");
            return false;
        }
        if (changeList[LTE_CELL_CONFIG] > 0) {
            Logs.d(TAG,"修改配置LTE_CELL_CONFIG");
        }
        if (changeList[LTE_SON_CONFIG] > 0) {
            Logs.d(TAG,"修改配置LTE_SON_CONFIG");
        }
        if (changeList[LTE_WORKE_MODE] > 0) {
            Logs.d(TAG,"修改配置LTE_WORKE_MODE");
        }
        return true;
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

            if (deviceDate.getMode().equals(DeviceDataStruct.MODE.LTE_TDD)
                    || deviceDate.getMode().equals(DeviceDataStruct.MODE.LTE_FDD)
                    || deviceDate.getMode().equals(DeviceDataStruct.MODE.WCDMA)) {
                l_Device_lte.setVisibility(View.VISIBLE);
                lte_GeneralPara = (LTE_GeneralPara)deviceDate.getGeneralPara();
                init_lte_workMode(lte_GeneralPara);
                init_lte_cellSet(lte_GeneralPara);
                init_lte_son_info(lte_GeneralPara);
            } else if (deviceDate.getMode().equals(DeviceDataStruct.MODE.CDMA)
                    || deviceDate.getMode().equals(DeviceDataStruct.MODE.GSM_V2)) {
                l_Device_zyf.setVisibility(View.VISIBLE);
            } else if (deviceDate.getMode().equals(DeviceDataStruct.MODE.GSM)) {
                l_Device_hjt.setVisibility(View.VISIBLE);
            } else {
                Logs.e(TAG, String.format("您选择的产品Mode为%s,目前不支持该类型!",deviceDate.getMode()));
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    private void init_lte_workMode(LTE_GeneralPara para){
        int index;
        String defult;
        if (para == null) return;
        ArrayAdapter<String> adapter;
        String[] selectFreq={"自动","手动"};
        String[] mode={"半自动","全自动"};
        lte_selectFreq = (Spinner) contentView.findViewById(R.id.lte_manualFreq);
        adapter = new ArrayAdapter<String>(context,R.layout.my_spinner,selectFreq);
        lte_selectFreq.setAdapter(adapter);
        if (para.getManualfreq() == 1) {
            index = 1;
        } else {
            index = 0;
        }
        lte_selectFreq.setSelection(index ,true);
        lte_selectFreq.setOnItemSelectedListener(new MyOnItemSelectedListener(lte_selectFreq,selectFreq[para.getManualfreq()],LTE_WORKE_MODE));

        lte_selectmode = (Spinner) contentView.findViewById(R.id.lte_boot);
        adapter = new ArrayAdapter<String>(context,R.layout.my_spinner,mode);
        lte_selectmode.setAdapter(adapter);
        if (para.getBootmode() == 1) {
            index = 1;
        } else {
            index = 0;
        }
        lte_selectmode.setSelection(index ,true);
        lte_selectmode.setOnItemSelectedListener(new MyOnItemSelectedListener(lte_selectmode,mode[para.getBootmode()],LTE_WORKE_MODE));

    }

    private void init_lte_cellSet(LTE_GeneralPara para){
        int index;
        if (para == null) return;

        lte_earfcn = (EditText) contentView.findViewById(R.id.lte_earfcn);
        lte_earfcn.setText(String.valueOf(para.getEarfcndl()));
        lte_earfcn.addTextChangedListener(new MyTextWatcher(lte_earfcn,String.valueOf(para.getEarfcndl()),LTE_CELL_CONFIG,0,65535));

        lte_pci = (EditText) contentView.findViewById(R.id.lte_pci);
        lte_pci.setText(String.valueOf(para.getPci()));
        lte_pci.addTextChangedListener(new MyTextWatcher(lte_pci,String.valueOf(para.getPci()),LTE_CELL_CONFIG,0,512));

        lte_cid = (EditText) contentView.findViewById(R.id.lte_cellid);
        lte_cid.setText(String.valueOf(para.getCid()));
        lte_cid.addTextChangedListener(new MyTextWatcher(lte_cid,String.valueOf(para.getCid()),LTE_CELL_CONFIG,0,Integer.MAX_VALUE));

        lte_tac = (EditText) contentView.findViewById(R.id.lte_tac);
        lte_tac.setText(String.valueOf(para.getTac()));
        lte_tac.addTextChangedListener(new MyTextWatcher(lte_tac,String.valueOf(para.getTac()),LTE_CELL_CONFIG,0,65535));

        lte_mcc = (EditText) contentView.findViewById(R.id.lte_mcc);
        lte_mcc.setText(String.valueOf(para.getMcc()));
        lte_mcc.addTextChangedListener(new MyTextWatcher(lte_mcc,String.valueOf(para.getMcc()),LTE_CELL_CONFIG,0,999));

        lte_mnc = (EditText) contentView.findViewById(R.id.lte_mnc);
        lte_mnc.setText(String.valueOf(para.getMnc()));
        lte_mnc.addTextChangedListener(new MyTextWatcher(lte_mnc,String.valueOf(para.getMnc()),LTE_CELL_CONFIG,0,999));

        lte_power = (EditText) contentView.findViewById(R.id.lte_txpower);
        lte_power.setText(String.valueOf(para.getPower()));
        lte_power.addTextChangedListener(new MyTextWatcher(lte_power,String.valueOf(para.getPower()),LTE_CELL_CONFIG,Integer.MIN_VALUE,Integer.MAX_VALUE));

        lte_periodtac = (EditText) contentView.findViewById(R.id.lte_periodTac);
        lte_periodtac.setText(String.valueOf(para.getPeriodtac()));
        lte_periodtac.addTextChangedListener(new MyTextWatcher(lte_periodtac,String.valueOf(para.getPeriodtac()),LTE_CELL_CONFIG,0,Integer.MAX_VALUE));

        String[] name={"5","10","15","20"};
        lte_bw = (Spinner) contentView.findViewById(R.id.lte_bw);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,R.layout.my_spinner,name);
        lte_bw.setAdapter(adapter);
        index = adapter.getPosition(String.valueOf(para.getBandwitch()));
        lte_bw.setSelection(index ,true);
        lte_bw.setOnItemSelectedListener(new MyOnItemSelectedListener(lte_bw,String.valueOf(para.getBandwitch()),LTE_CELL_CONFIG));
    }

    private void init_lte_son_info(LTE_GeneralPara para) {
        int index;
        if (para == null) return;

        lte_sonEarfcn = (EditText) contentView.findViewById(R.id.lte_son_earfcn);
        lte_sonEarfcn.setText(para.getEarfcnlist());
        lte_sonEarfcn.addTextChangedListener(new MyTextWatcher(lte_sonEarfcn,para.getEarfcnlist(),LTE_SON_CONFIG));
    }

    private class  MyTextWatcher implements TextWatcher {
        private String defultValue;
        private boolean checkRandge;
        private int MinValue;
        private int MaxValue;
        private EditText et;
        private int configFlag;

        public MyTextWatcher(EditText et,String defultValue,int flag) {
            this.et = et;
            this.configFlag = flag;
            this.defultValue = defultValue;
            this.checkRandge = false;
        }
        public MyTextWatcher(EditText et,String defultValue,int flag,int minValue,int maxValue) {
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
                        if (value >= MinValue && value<=MaxValue) {
                            et.setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.colorHalfDialogTitle));
                            valueError = false;
                        } else {
                            et.setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.colorAccent));
                            CustomToast.showToast(context, String.format("输入值(%d)不在[%d-%d]范围内",value,MinValue,MaxValue));
                            valueError = true;
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
        private Spinner sp;

        public MyOnItemSelectedListener(Spinner sp,String defult,int flag) {
            this.sp = sp;
            this.configFlag = flag;
            this.defultValue = defult;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Logs.d(TAG, "您选择了:" + sp.getSelectedItem().toString());
            //TextView tv = (TextView)contentView.findViewById(android.R.id.text1);
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

}
