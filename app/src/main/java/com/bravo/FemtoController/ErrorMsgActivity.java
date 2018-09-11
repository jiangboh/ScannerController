package com.bravo.FemtoController;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.adapters.ErrorMsgListAdapter;
import com.bravo.custom_view.DeleteOrIgnoreErrorDialog;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.parse_generate_xml.ErrorNotif;
import com.bravo.utils.Utils;

import java.util.ArrayList;


public class ErrorMsgActivity extends BaseActivity{
    private ExpandableListView listView;
    private ErrorMsgListAdapter adapter;
    private DeleteOrIgnoreErrorDialog dialog;
    @Override
    protected void initView() {
        setContentView(R.layout.activity_error_msg);
        findViewById(R.id.iv_activity_back).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                onBackPressed();
                super.recordOnClick(v, "Back Event " + TAG);
            }
        });
        ((TextView) findViewById(R.id.tv_activity_title)).setText(R.string.error_msg);
        listView = (ExpandableListView) findViewById(R.id.error_msg_list);
        adapter = new ErrorMsgListAdapter();
        listView.setAdapter(adapter);
        listView.setGroupIndicator(null);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = view.getTag();
                dialog = new DeleteOrIgnoreErrorDialog(mContext,R.style.dialog_style);
                dialog.setOnClickListener(new DeleteOrIgnoreErrorDialog.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ErrorNotif en = (ErrorNotif) view.getTag();
                        if(view.getId() == R.id.delete_or_ignore_error_delete){
                            if(dialog.getType() == DeleteOrIgnoreErrorDialog.GROUP){
                                Utils.deleteErrorsByIp(en.getIpAddress());
                            }else if(dialog.getType() == DeleteOrIgnoreErrorDialog.ITEM){
                                Utils.deleteError(en);
                            }
                        }else if(view.getId() == R.id.delete_or_ignore_error_ignore){
                            if(dialog.getType() == DeleteOrIgnoreErrorDialog.GROUP){
                                ArrayList<ErrorNotif> errors = Utils.errors.get(en.getIpAddress());
                                if(Utils.isAllIgnores(errors)){
                                    for(ErrorNotif temEn : errors){
                                        Utils.deleteIgnoreError(temEn);
                                    }
                                }else{
                                    for(ErrorNotif temEn : errors){
                                        Utils.addIgnoreError(temEn);
                                    }
                                }

                            }else if(dialog.getType() == DeleteOrIgnoreErrorDialog.ITEM){
                                if(Utils.isIgnoreError(en)){
                                    Utils.deleteIgnoreError(en);
                                }else{
                                    Utils.addIgnoreError(en);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
                if(obj instanceof ErrorMsgListAdapter.ParentViewHolder){
                    ErrorNotif en = (ErrorNotif)((ErrorMsgListAdapter.ParentViewHolder)obj).ipAddress.getTag();
                    dialog.setErrorNotif(en);
                    dialog.setType(DeleteOrIgnoreErrorDialog.GROUP);
                    dialog.show();
                    ArrayList<ErrorNotif> errors = Utils.errors.get(en.getIpAddress());
                    if(Utils.isAllIgnores(errors)){
                        dialog.setIgnoreText("Attention All");
                    }else{
                        dialog.setIgnoreText("Ignore All");
                    }
                }else if(obj instanceof ErrorMsgListAdapter.ChildViewHolder){
                    ErrorNotif en = (ErrorNotif)((ErrorMsgListAdapter.ChildViewHolder)obj).time.getTag();
                    dialog.setErrorNotif(en);
                    dialog.setType(DeleteOrIgnoreErrorDialog.ITEM);
                    dialog.show();
                    if(Utils.isIgnoreError(en)){
                        dialog.setIgnoreText("Attention");
                    }else{
                        dialog.setIgnoreText("Ignore");
                    }
                }
                return true;
            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        registeEventBus();
    }

    @Override
    public void errorNotif(ErrorNotif en) {
        super.errorNotif(en);
        adapter.notifyDataSetChanged();
    }
}
