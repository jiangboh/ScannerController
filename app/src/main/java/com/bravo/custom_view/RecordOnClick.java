package com.bravo.custom_view;

import android.text.TextUtils;
import android.view.View;

import com.bravo.utils.Logs;

/**
 * Created by Jack.liao on 2017/9/11.
 */

public abstract class RecordOnClick implements View.OnClickListener {
    private final String TAG = "RecordOnClick";
    public void recordOnClick(View v, String strMsg){
        if (!TextUtils.isEmpty(strMsg)) {
            Logs.w(TAG, strMsg, "Record_Event", true);
        }
    };

    @Override
    public void onClick(View v) {
        recordOnClick(v, null);
    }
}
