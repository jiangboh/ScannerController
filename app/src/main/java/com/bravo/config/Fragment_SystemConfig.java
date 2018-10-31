package com.bravo.config;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.OneBtnHintDialog;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.utils.Logs;

import static android.content.Context.MODE_PRIVATE;
import static com.bravo.R.drawable.btn_config_normal;
import static com.bravo.R.id.tv_LisenPort;

/**
 * Created by admin on 2018-10-16.
 */

public class Fragment_SystemConfig extends RevealAnimationBaseFragment {
    private final String TAG = "Fragment_SystemConfig";

    public static final String TABLE_NAME = "FragmentSystemConfig";
    public static final String tn_MaxNum = "MaxNum";
    public static final String tn_LisenPort = "tn_ListenPort";
    public static final int DefultPort = 14721;
    public static final int DefultMaxNum = 60;
    public static final int MinNum = 30;
    public static final int MaxNum = 600;

    private int iNum = 1000;
    private TextView tv_MaxNum;
    private SeekBar sb_MaxNum;

    private int PortText;
    private EditText tv_port;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logs.d(TAG, "onCreate",true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_system_config);
    }

    @Override
    public void initView() {
        Logs.d(TAG, "initView",true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        Logs.d(TAG, "initData",true);
        tv_port = (EditText) contentView.findViewById(tv_LisenPort);
        tv_MaxNum = (TextView) contentView.findViewById(R.id.tx_MaxTime);
        sb_MaxNum = (SeekBar) contentView.findViewById(R.id.sb_MaxTime);

        sb_MaxNum.setMax(Fragment_SystemConfig.MaxNum - Fragment_SystemConfig.MinNum);
        sb_MaxNum.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_MaxNum.setText((Fragment_SystemConfig.MinNum + progress) + "秒");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                iNum = Fragment_SystemConfig.MinNum + sb_MaxNum.getProgress();
                Log.v("停止滑动时的值：", String.valueOf(iNum));
            }
        });
    }

    @Override
    public void onResume() {
        Logs.d(TAG, "onResume",true);
        super.onResume();
        loadData();
        ((RevealAnimationActivity) context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity) context).getSettingBtn().setImageResource(btn_config_normal);
        ((RevealAnimationActivity) context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                if (saveData()) {
                    CustomToast.showToast(context, "系统配置成功！重启App后生效");
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
        hintDialog.setTitle("失败");
        hintDialog.setContent(str);
        hintDialog.setListener(new OneBtnHintDialog.BtnClickListener() {
            @Override
            public void onBtnClick(View v) {

            }
        });
    }
    private void loadData() {
        SharedPreferences sp = context.getSharedPreferences(Fragment_SystemConfig.TABLE_NAME, MODE_PRIVATE);
        iNum = sp.getInt(Fragment_SystemConfig.tn_MaxNum,Fragment_SystemConfig.DefultMaxNum);
        Log.v("初始值：", String.valueOf(iNum));
        sb_MaxNum.setProgress(iNum - Fragment_SystemConfig.MinNum);
        tv_MaxNum.setText(String.valueOf(iNum)+"秒");

        PortText = sp.getInt(Fragment_SystemConfig.tn_LisenPort,Fragment_SystemConfig.DefultPort);
        tv_port.setText(String.valueOf(PortText));
        tv_port.setSelection(tv_port.getText().toString().length());//将光标移至文字末尾
    }

    private boolean saveData() {
        int port = Integer.parseInt(tv_port.getText().toString());
        if (port < 1000 || port > 65535){
            openDialog(String.format("UDP监听端口必须为[%d-%d]范围内的值",1000,65535));
            return false;
        }

        SharedPreferences preferences = context.getSharedPreferences(Fragment_SystemConfig.TABLE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Fragment_SystemConfig.tn_MaxNum, iNum);
        editor.putInt(Fragment_SystemConfig.tn_LisenPort, port);
        Log.v("保存值：", String.valueOf(iNum));
        editor.commit();

        return true;
    }

}
