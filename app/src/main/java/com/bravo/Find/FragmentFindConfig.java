package com.bravo.Find;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.custom_view.OneBtnHintDialog;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.utils.Logs;

import static android.content.Context.MODE_PRIVATE;
import static com.bravo.R.drawable.btn_config_normal;
import static com.bravo.R.id.sb_AllFindTime;

/**
 * Created by admin on 2018-9-26.
 */

public class FragmentFindConfig extends RevealAnimationBaseFragment {
    private final String TAG = "FragmentFindConfig";

    public static final String TABLE_NAME = "DeviceFind";
    public static final String tn_AllFindTime = "AllFindTime";

    private int min = 5;
    private int allfindtime = 10;
    private TextView tv_allFindTime;
    private SeekBar sb_allFindTime;

    @Override
    public void onResume() {
        super.onResume();
        loadData();

        ((RevealAnimationActivity) context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity) context).getSettingBtn().setImageResource(btn_config_normal);
        ((RevealAnimationActivity) context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                saveData();
                openDialog();
            }
        });

    }

    @Override
    public void initView() {

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        tv_allFindTime = (TextView) contentView.findViewById(R.id.tx_AllFindTime);
        sb_allFindTime = (SeekBar) contentView.findViewById(sb_AllFindTime);
        sb_allFindTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_allFindTime.setText((min + progress) + "秒");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                allfindtime = min + sb_allFindTime.getProgress();
                Log.v("停止滑动时的值：", String.valueOf(allfindtime));
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_find_config);
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
        hintDialog.setContent("搜索配置成功！");
        hintDialog.setListener(new OneBtnHintDialog.BtnClickListener() {
            @Override
            public void onBtnClick(View v) {

            }
        });
    }
    private void loadData() {
        SharedPreferences sp = context.getSharedPreferences(TABLE_NAME, MODE_PRIVATE);
        allfindtime = sp.getInt(tn_AllFindTime,10);
        Log.v("初始值：", String.valueOf(allfindtime));
        sb_allFindTime.setProgress(allfindtime - min);
        tv_allFindTime.setText(String.valueOf(allfindtime)+"秒");
    }

    private void saveData() {
        SharedPreferences preferences = context.getSharedPreferences(TABLE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(tn_AllFindTime, allfindtime);
        Log.v("保存值：", String.valueOf(allfindtime));
        editor.commit();
    }

}