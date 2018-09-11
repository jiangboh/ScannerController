package com.bravo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.bravo.R;
import com.bravo.custom_view.RecordOnClick;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Jack.liao on 2017/11/3.
 */

public class DialogSuperUser extends Dialog {
    private final String TAG = "DialogSuperUser";
    private Spinner spinner_coresize;

    public DialogSuperUser(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_superuser);
        spinner_coresize = (Spinner) findViewById(R.id.spinner_coresize);
        ((Button) findViewById(R.id.ok)).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                saveData();
                dismiss();
            }
        });
        ((Button) findViewById(R.id.cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        loadData();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void saveData() {
        SharedPreferences preferences = getContext().getSharedPreferences("send_sms",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("core_size", spinner_coresize.getSelectedItemPosition());
        editor.commit();
    }
    private void loadData() {
        SharedPreferences sp = getContext().getSharedPreferences("send_sms", MODE_PRIVATE);
        spinner_coresize.setSelection(sp.getInt("core_size", 0));
    }
}
