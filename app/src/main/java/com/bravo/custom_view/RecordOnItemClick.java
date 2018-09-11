package com.bravo.custom_view;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.bravo.utils.Logs;

/**
 * Created by Jack.liao on 2017/9/11.
 */

public class RecordOnItemClick implements AdapterView.OnItemClickListener {
    private final String TAG = "RecordOnItemClick";
    public void recordOnItemClick(AdapterView<?> parent, View view, int position, long id, String strMsg){
        if (!TextUtils.isEmpty(strMsg)) {
            Logs.w(TAG, strMsg, "Record_Event", true);
        }
    };
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        recordOnItemClick(parent, view, position, id, null);
    }
}
