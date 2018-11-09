package com.bravo.config;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.data_ben.DeviceDataStruct;
import com.bravo.data_ben.DeviceFragmentStruct;
import com.bravo.data_ben.WaitDialogData;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.utils.Logs;
import com.bravo.xml.HandleRecvXmlMsg;
import com.bravo.xml.LTE;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 2018-11-8.
 */

public class FragmentRedirection extends RevealAnimationBaseFragment {
    private final String TAG = "FragmentRedirection";

    private static int viewId = 0;
    //所有上线的设备列表
    private ArrayList<String> dList;

    //保存所有控件是否修改，修改后值是否在范围内
    private HashMap<Integer,ValueChange> ViewChangeList = new HashMap<Integer,ValueChange>() ;
    //保存注册过的EditText控件及事件
    private HashMap<EditText,MyTextWatcher> editTextArray = new HashMap<EditText,MyTextWatcher>();
    //保存修改过的Config数组，每个Config表示要下发的命令集合
    private ArrayList<Integer> ChangeConfigList = new ArrayList<Integer>();
    private HashMap<Spinner,MyOnItemSelectedListener> spinnerArray = new HashMap<Spinner,MyOnItemSelectedListener>();
    //保存选择的设备数据
    private DeviceDataStruct deviceDate = null;

    private Redirection CurrRedirectionInfo;
    private int CurrCategory;

    private Spinner s_deviceSelect;

    //private Spinner s_category;
    private RadioGroup radiogroup;
    private RadioButton whiteRb;
    private RadioButton blackRb;
    private RadioButton otherRb;

    private Spinner s_rejectMethod;
    private Spinner s_priority;
    private EditText et_freq;
    private EditText et_addFreq;

    public class ValueChange {
        private int flag;
        private boolean changes = false;
        private boolean outRang = false;

        public ValueChange(int flag) {
            this.flag = flag;
            this.changes = false;
            this.outRang = false;
        }

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

    public class Redirection {
        public class Category {
            private int priorty;
            private int freq;
            private int rejectMethod;
            private String addFreq;

            public int getPriorty() {
                return priorty;
            }

            public void setPriorty(int priorty) {
                this.priorty = priorty;
            }

            public int getFreq() {
                return freq;
            }

            public void setFreq(int freq) {
                this.freq = freq;
            }

            public int getRejectMethod() {
                return rejectMethod;
            }

            public void setRejectMethod(int rejectMethod) {
                this.rejectMethod = rejectMethod;
            }

            public String getAddFreq() {
                return addFreq;
            }

            public void setAddFreq(String addFreq) {
                this.addFreq = addFreq;
            }
        }

        public static final int WHITE = 0;
        public static final int BLACK = 1;
        public static final int OTHER = 2;

        private String sn;
        private Category white;
        private Category black;
        private Category other;

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public Category getWhite() {
            return white;
        }

        public void setWhite(Category white) {
            this.white = white;
        }

        public Category getBlack() {
            return black;
        }

        public void setBlack(Category black) {
            this.black = black;
        }

        public Category getOther() {
            return other;
        }

        public void setOther(Category other) {
            this.other = other;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logs.d(TAG, "onCreate",true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_redirection);

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void initView() {
        Logs.d(TAG, "initView",true);

        radiogroup = (RadioGroup) contentView.findViewById(R.id.radiogroup);
        whiteRb = (RadioButton) contentView.findViewById(R.id.white_tab);
        blackRb = (RadioButton) contentView.findViewById(R.id.black_tab);
        otherRb = (RadioButton) contentView.findViewById(R.id.other_tab);

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup rg, int checkedId) {
                // TODO Auto-generated method stub
                if(checkedId == whiteRb.getId()){
                    CurrCategory = Redirection.WHITE;
                } else if(checkedId == blackRb.getId()){
                    CurrCategory = Redirection.BLACK;
                } else if(checkedId == otherRb.getId()){
                    CurrCategory = Redirection.OTHER;
                } else {
                    CurrCategory = Redirection.WHITE;
                }
                loadData();
            }
        });
        whiteRb.setChecked(true);

        s_rejectMethod = (Spinner) contentView.findViewById(R.id.reject_method);
        s_priority = (Spinner) contentView.findViewById(R.id.priority);

        et_freq =  (EditText) contentView.findViewById(R.id.earfcn);
        et_addFreq =  (EditText) contentView.findViewById(R.id.add_earfcn_list);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        Logs.d(TAG, "initData",true);
        viewId = 0;
        dList = DeviceFragmentStruct.getLTESnList();

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

    private void loadData() {
        int index;
        if (CurrRedirectionInfo == null) return;
        Redirection.Category category;
        if (CurrCategory ==  Redirection.WHITE ) {
            category = CurrRedirectionInfo.getWhite();
        } else if (CurrCategory ==  Redirection.BLACK ) {
            category = CurrRedirectionInfo.getBlack();
        } else if (CurrCategory ==  Redirection.OTHER ) {
            category = CurrRedirectionInfo.getOther();
        } else {
            Logs.w(TAG,"用户类型为" + CurrCategory + "错误。");
            return;
        }

        String[] modeList ={"永久拒绝","拒绝一次","不拒绝"};
        if (category.getRejectMethod() == 1) {
            index = 0;
        } else if (category.getRejectMethod() == 2) {
            index = 1;
        } else {
            index = 2;
        }
        registerSpinner(s_rejectMethod,modeList,index, HandleRecvXmlMsg.LTE_REDIRECTION_SET);

        String[] modeList1 ={"2G","3G","4G","不重定向"};
        index = category.getPriorty() - 2;
        registerSpinner(s_priority,modeList1,index, HandleRecvXmlMsg.LTE_REDIRECTION_SET);

        registerEditView(et_freq, category.getFreq(), HandleRecvXmlMsg.LTE_REDIRECTION_SET, 0, 65535);

        registerEditView(et_addFreq, category.getAddFreq(), HandleRecvXmlMsg.LTE_REDIRECTION_SET);

    }

    private void registerEditView(EditText et,int defult,int flag,int min,int max) {
        registerEditView(et,String.valueOf(defult),flag,min,max);
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
        String defult;
        if (sp == null) {
            Logs.e(TAG,"Spinner控件未赋值。");
            return;
        }

        if (index < 0) {
            Logs.e(TAG,"设备上报值在列表中未找到");
            index = 0;
            defult = "";
        } else {
            defult = sp.getAdapter().getItem(index).toString();
        }
        if (spinnerArray.containsKey(sp)) {
            spinnerArray.get(sp).setDeflut(defult);
        } else { //未注册
            MyOnItemSelectedListener listen = new MyOnItemSelectedListener(sp,defult,flag);
            sp.setOnItemSelectedListener(listen);
            spinnerArray.put(sp,listen);
        }
        sp.setSelection(index ,true);
    }

    private void registerSpinner(Spinner sp,String[] nameList,int index,int flag){
        if (sp == null) {
            Logs.e(TAG,"Spinner控件未赋值。");
            return;
        }

        String defult;
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(context,R.layout.my_spinner,nameList);
        sp.setAdapter(adapter);

        if (index < 0) {
            Logs.e(TAG,"设备上报值在列表中未找到");
            index = 0;
            defult = "";
        } else {
            defult = sp.getAdapter().getItem(index).toString();
        }

        if (spinnerArray.containsKey(sp)) {
            spinnerArray.get(sp).setDeflut(defult);
        } else { //未注册
            MyOnItemSelectedListener listen = new MyOnItemSelectedListener(sp,defult,flag);
            sp.setOnItemSelectedListener(listen);
            spinnerArray.put(sp,listen);
        }
        sp.setSelection(index ,true);
    }

    private boolean saveData() {
        if (!checkChanges()) return false;
        Redirection.Category category = new Redirection().new Category();
        int method = s_rejectMethod.getSelectedItemPosition();
        if (method == 0 ) {
            category.setRejectMethod(1);
        } else if (method == 1 ) {
            category.setRejectMethod(2);
        } else {
            category.setRejectMethod(0xFF);
        }

        category.setPriorty(s_priority.getSelectedItemPosition() + 2);
        category.setFreq(Integer.parseInt(et_freq.getText().toString()));
        category.setAddFreq(et_addFreq.getText().toString());
        new LTE(context).SendSetRedirectionRequest(deviceDate.getIp(),deviceDate.getPort(),
                CurrCategory,category);
        return true;
    }

    private void openDialog(String tit,String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.icon_error_errors);//设置图标
        builder.setTitle(tit);//设置对话框的标题
        builder.setMessage(String.format(str));//设置对话框的内容
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  //这个是设置确定按钮
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });

        /*builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  //取消按钮
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
*/
        AlertDialog b = builder.create();
        b.show();  //必须show一下才能看到对话框，跟Toast一样的道理

        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object alertController = mAlert.get(b);

            Field mTitleView = alertController.getClass().getDeclaredField("mTitleView");
            mTitleView.setAccessible(true);
            TextView title = (TextView) mTitleView.get(alertController);
            title.setTextColor(Color.RED);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        b.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        b.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLUE);

        return;
    }

    private boolean checkChanges(){
        ChangeConfigList.clear();

        for(int i =0;i<ViewChangeList.size();i++) {
            //Logs.d(TAG,String.format("ViewId=%d",i));
            if (ViewChangeList.get(i).isChanges()) {
                if (ViewChangeList.get(i).isOutRang()) {
                    openDialog("错误:","参数不在范围内，请重新输入正确参数");
                    return false;
                }

                if (!ChangeConfigList.contains(ViewChangeList.get(i).getFlag())) {
                    int configId = ViewChangeList.get(i).getFlag();
                    Logs.d(TAG,String.format("标志%d修改了",configId));

                    if (configId == HandleRecvXmlMsg.LTE_REDIRECTION_SET) {
                        ChangeConfigList.add(configId);
                    }
                }
            }
        }

        if (ChangeConfigList.size() <= 0) {
            CustomToast.showToast(context, "没有配置需要修改");
            return false;
        }

        return true;
    }

    private int getIndexInArray(String[] array,String name) {
        int index = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(name)) {
                index = i;
                break;
            }
        }

        return index;
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
                        if (s.equals(""))
                        {
                            ViewChangeList.get(vId).setOutRang(true); //如果为空，表示超出范围
                        } else {
                            int value = Integer.parseInt(s.toString());
                            if (value >= MinValue && value <= MaxValue) {  //范围内
                                et.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorHalfDialogTitle));
                                //Logs.d(TAG,"ViewId=" + vId + ";set false");
                                ViewChangeList.get(vId).setOutRang(false);
                            } else {
                                et.setTextColor(ContextCompat.getColor(context.getApplicationContext(), R.color.colorAccent));
                                CustomToast.showToast(context, String.format("输入值(%d)不在[%d-%d]范围内", value, MinValue, MaxValue));
                                //Logs.d(TAG,"ViewId=" + vId + ";set true");
                                ViewChangeList.get(vId).setOutRang(true);
                            }
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
            CurrRedirectionInfo = null;
            deviceDate = DeviceFragmentStruct.getDevice(dList.get(position));
            if (deviceDate == null) {
                Logs.e(TAG, String.format("您选择的产品SN为%s,未在设备列表中找到该设备!",dList.get(position)));
                return;
            }

            String mode = deviceDate.getMode();
            if (mode.equals(DeviceDataStruct.MODE.LTE_TDD)
                    || mode.equals(DeviceDataStruct.MODE.LTE_FDD)
                    || mode.equals(DeviceDataStruct.MODE.WCDMA)) {
                new LTE(context).SendGetRedirectionRequest(deviceDate.getIp(),deviceDate.getPort());
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void CdmaParaChangesEvens(Redirection  data) {
        if (deviceDate.getSN().equals(data.getSn())) {
            CurrRedirectionInfo = data;
            loadData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ChangesSendStatus(WaitDialogData wdd) {
        //Logs.d(TAG,"接收到发送状态改变事件",true,true);
        if (wdd.getiRusult() == WaitDialogData.RUSULT_FAIL) {
            openDialog("错误:", "重定向配置失败");
        } else {
            openDialog("成功:", "重定向配置成功");
            new LTE(context).SendGetRedirectionRequest(deviceDate.getIp(),deviceDate.getPort());
        }
    }
}
