package com.bravo.custom_view;

import android.text.TextUtils;
import android.view.View;

import com.bravo.utils.Logs;

public abstract class RecordOnLongClick implements View.OnLongClickListener {
    private final String TAG = "RecordOnLongClick";
    public void recordOnLongClick(View v, String strMsg){
        if (!TextUtils.isEmpty(strMsg)) {
            Logs.w(TAG, strMsg, "Record_Event", true);
        }
    };

    @Override
    public boolean onLongClick(View v) {
        recordOnLongClick(v, null);
        return true;
    }
}
