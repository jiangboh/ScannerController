package com.bravo.scanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.FileSlelect.SelectFolderActivity;
import com.bravo.R;
import com.bravo.adapters.AdapterScanner;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.OneBtnHintDialog;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.utils.Logs;

import static android.content.Context.MODE_PRIVATE;
import static com.bravo.R.drawable.btn_config_normal;

/**
 * Created by admin on 2018-10-12.
 */

public class FragmentScannerConfig extends RevealAnimationBaseFragment {
    private final String TAG = "FragmentFindConfig";

    public static final String TABLE_NAME = "FragmentScannerConfig";
    public static final String tn_MaxNum = "MaxNum";
    public static final String tn_DupRemo = "DupRemo";
    public static final Boolean DefultDupRemo = true;
    public static final int DefultMaxNum = 10000;
    public static final int MinImsiNum = 1000;

    public static final String tn_RxGain = "RxGain";
    public static final int DefultRxGain = -15;
    public static final int MinRxGain = -30;

    public static final String tn_OpenOffset = "penOffset";
    public static final Boolean DefultOpenOffset = true;

    public static final String ImsiSavePath = "ImsiSavePath";

    private int iRxGain = DefultRxGain;
    private TextView tv_RxGain;
    private SeekBar sb_RxGain;


    private int iMaxNum = 1000;
    private TextView tv_MaxNum;
    private SeekBar sb_MaxNum;

    private Boolean isDupRemo;
    private CheckBox ck_DupRemo;

    private Boolean openOffset;
    private CheckBox ck_openOffset;

    private TextView tv_savePath;
    private Button btn_savePath;

    @Override
    public void onResume() {
        super.onResume();
        loadData();

        ((RevealAnimationActivity) context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity) context).getSettingBtn().setImageResource(btn_config_normal);
        ((RevealAnimationActivity) context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                if (saveData()) {
                    //openDialog();
                    CustomToast.showToast(context, "捕号配置成功");
                }
            }
        });

    }

    @Override
    public void initView() {

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        ck_DupRemo = (CheckBox) contentView.findViewById(R.id.ck_DupRemo);
        ck_DupRemo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isDupRemo = isChecked;
                Log.v(TAG,"isDupRemo：" + isDupRemo);
            }
        });

        ck_openOffset = (CheckBox) contentView.findViewById(R.id.ck_Offset);
        ck_openOffset.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                openOffset = isChecked;
                Log.v(TAG,"openOffset：" + isDupRemo);
            }
        });

        tv_MaxNum = (TextView) contentView.findViewById(R.id.tx_MaxNum);
        sb_MaxNum = (SeekBar) contentView.findViewById(R.id.sb_MaxNum);
        sb_MaxNum.setMax(20000 - FragmentScannerConfig.MinImsiNum);
        sb_MaxNum.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_MaxNum.setText((FragmentScannerConfig.MinImsiNum + progress) + "个");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                iMaxNum = FragmentScannerConfig.MinImsiNum + sb_MaxNum.getProgress();
                Log.v("停止滑动时的值：", String.valueOf(iMaxNum));
            }
        });

        tv_RxGain = (TextView) contentView.findViewById(R.id.tx_RxGain);
        sb_RxGain = (SeekBar) contentView.findViewById(R.id.sb_RxGain);
        sb_RxGain.setMax(5 - FragmentScannerConfig.MinRxGain);
        sb_RxGain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_RxGain.setText((FragmentScannerConfig.MinRxGain + progress) + "db");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                iRxGain = FragmentScannerConfig.MinRxGain + sb_RxGain.getProgress();
                Log.v("停止滑动时的值：", String.valueOf(iRxGain));
            }
        });

        tv_savePath = (TextView) contentView.findViewById(R.id.tv_imsiSavePath);
        tv_savePath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                CustomToast.showToast(context, tv_savePath.getText().toString());
            }
        });
        btn_savePath = (Button) contentView.findViewById(R.id.btn_imsiSavePath);
        btn_savePath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(context,SelectFolderActivity.class);
                startActivityForResult(intent,1);
            }
        });
        btn_savePath.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(context,SelectFolderActivity.class);
                startActivityForResult(intent,1);
                return false;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==1&&resultCode==2){
            //当请求码是1&&返回码是2进行下面操作
            loadData();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_scanner_config);
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

    private void openDialog()
    {
        OneBtnHintDialog hintDialog;
        hintDialog = new OneBtnHintDialog(context, R.style.dialog_style);
        hintDialog.setCanceledOnTouchOutside(false);
        hintDialog.show();
        hintDialog.setBtnContent("确定");
        hintDialog.setTitle("成功");
        hintDialog.setContent("捕号配置成功！");
        hintDialog.setListener(new OneBtnHintDialog.BtnClickListener() {
            @Override
            public void onBtnClick(View v) {

            }
        });
    }
    private void loadData() {
        SharedPreferences sp = context.getSharedPreferences(FragmentScannerConfig.TABLE_NAME, MODE_PRIVATE);
        iMaxNum = sp.getInt(FragmentScannerConfig.tn_MaxNum,FragmentScannerConfig.DefultMaxNum);
        Log.v("初始值：", String.valueOf(iMaxNum));
        sb_MaxNum.setProgress(iMaxNum - FragmentScannerConfig.MinImsiNum);
        tv_MaxNum.setText(String.valueOf(iMaxNum)+"个");

        iRxGain = sp.getInt(FragmentScannerConfig.tn_RxGain,FragmentScannerConfig.DefultRxGain);
        Log.v("初始值：", String.valueOf(iRxGain));
        sb_RxGain.setProgress(iRxGain - FragmentScannerConfig.MinRxGain);
        tv_RxGain.setText(String.valueOf(iRxGain)+"db");

        isDupRemo = sp.getBoolean(FragmentScannerConfig.tn_DupRemo,FragmentScannerConfig.DefultDupRemo);
        ck_DupRemo.setChecked(isDupRemo);

        openOffset = sp.getBoolean(FragmentScannerConfig.tn_OpenOffset,FragmentScannerConfig.DefultOpenOffset);
        ck_openOffset.setChecked(openOffset);

        tv_savePath.setText(sp.getString(FragmentScannerConfig.ImsiSavePath,""));
    }

    private boolean saveData() {
        SharedPreferences preferences = context.getSharedPreferences(FragmentScannerConfig.TABLE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(FragmentScannerConfig.tn_MaxNum, iMaxNum);
        editor.putInt(FragmentScannerConfig.tn_RxGain, iRxGain);
        editor.putBoolean(FragmentScannerConfig.tn_DupRemo, isDupRemo);
        editor.putBoolean(FragmentScannerConfig.tn_OpenOffset, openOffset);
        Log.v("保存值：", String.valueOf(iMaxNum));
        editor.commit();

        AdapterScanner.setIsDupRemo(isDupRemo);
        AdapterScanner.setMaxTotal(iMaxNum);

        FragmentpPositionListen.setOpenOffset(openOffset);
        return true;
    }

}
