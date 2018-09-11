package com.bravo.custom_view;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.bravo.utils.Logs;

/**
 * Created by Jack.liao on 2017/9/11.
 */

public class RecordOnItemLongClick implements AdapterView.OnItemLongClickListener {
    private final String TAG = "RecordOnItemLongClick";
    public void recordOnItemLongClick(AdapterView<?> parent, View view, int position, long id, String strMsg){
        if (!TextUtils.isEmpty(strMsg)) {
            Logs.w(TAG, strMsg, "Record_Event", true);
        }
    };
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        recordOnItemLongClick(parent, view, position, id, null);
        return true;
    }
}
