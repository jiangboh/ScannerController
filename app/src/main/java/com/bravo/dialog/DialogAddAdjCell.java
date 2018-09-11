package com.bravo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.R;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.parse_generate_xml.cell_scan.CellScanSibCell;
import com.bravo.parse_generate_xml.cell_scan.CellScanTechSpecific;
import com.bravo.utils.SharePreferenceUtils;

/**
 * Created by Jack.liao on 2017/4/20.
 */

public class DialogAddAdjCell extends Dialog {
    private DialogAddAdjCell.OnAddAdjCellDialogListener addAdjCellDialogListener;
    private EditText edit_cid;
    private EditText edit_rncid;
    private EditText edit_lac;
    private EditText edit_psc;
    private EditText edit_tac;
    private EditText edit_pci;
    private EditText edit_channel;
    private String strCurTech;
    private CellScanSibCell cellScanSibCell;
    public DialogAddAdjCell(@NonNull Context context, int theme, DialogAddAdjCell.OnAddAdjCellDialogListener addAdjCellDialogListener) {
        super(context, theme);
        this.addAdjCellDialogListener = addAdjCellDialogListener;
    }
    public DialogAddAdjCell(@NonNull Context context, int theme, CellScanSibCell cellScanSibCell, DialogAddAdjCell.OnAddAdjCellDialogListener addAdjCellDialogListener) {
        super(context, theme);
        this.addAdjCellDialogListener = addAdjCellDialogListener;
        this.cellScanSibCell = cellScanSibCell;
    }

    public interface OnAddAdjCellDialogListener{
        void AddAdjCellCallBack(CellScanSibCell cellScanSibCell);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_adjacentcell);
        edit_channel = (EditText) findViewById(R.id.sib_channel);
        edit_rncid = (EditText) findViewById(R.id.sib_rncid);
        edit_cid = (EditText) findViewById(R.id.sib_cid);
        edit_lac = (EditText) findViewById(R.id.sib_lac);
        edit_psc = (EditText) findViewById(R.id.sib_psc);
        edit_tac = (EditText) findViewById(R.id.sib_tac);
        edit_pci = (EditText) findViewById(R.id.sib_pci);
        strCurTech = SharePreferenceUtils.getInstance(getContext()).getString("status_notif_tech" +
                ((ProxyApplication)getContext().getApplicationContext()).getCurSocketAddress() +
                ((ProxyApplication)getContext().getApplicationContext()).getiTcpPort(), "");
        if (strCurTech.equals("3G")) {
            findViewById(R.id.layout_umts).setVisibility(View.VISIBLE);
        } else if (strCurTech.equals("4G")){
            findViewById(R.id.layout_lte).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.ok).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                if (CheckTextEmpty()) {
                    CellScanSibCell cellScanSibCell = new CellScanSibCell();
                    cellScanSibCell.setChannel(edit_channel.getText().toString());
                    cellScanSibCell.setCid(edit_cid.getText().toString());
                    cellScanSibCell.setRncid(edit_rncid.getText().toString());
                    if (strCurTech.equals("4G")){
                        cellScanSibCell.setTechSpecific(new CellScanTechSpecific());
                        cellScanSibCell.getTechSpecific().setTac(edit_tac.getText().toString());
                        cellScanSibCell.getTechSpecific().setPci(edit_pci.getText().toString());
                    } else if (strCurTech.equals("3G")) {
                        cellScanSibCell.setTechSpecific(new CellScanTechSpecific());
                        cellScanSibCell.getTechSpecific().setLac(edit_lac.getText().toString());
                        cellScanSibCell.getTechSpecific().setPsc(edit_psc.getText().toString());
                    }
                    addAdjCellDialogListener.AddAdjCellCallBack(cellScanSibCell);
                    super.recordOnClick(v, "OK Add Adjacent Cell Event " + cellScanSibCell.toString());
                    dismiss();
                }

            }
        });

        findViewById(R.id.cancel).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                cancel();
                super.recordOnClick(v, "Cancel Add Adjacent Cell Event");
            }
        });

        if (cellScanSibCell != null) {
            edit_channel.setText(cellScanSibCell.getChannel());
            edit_rncid.setText(cellScanSibCell.getRncid());
            edit_cid.setText(cellScanSibCell.getCid());
            if (cellScanSibCell.getTechSpecific() != null) {
                edit_lac.setText(cellScanSibCell.getTechSpecific().getLac());
                edit_psc.setText(cellScanSibCell.getTechSpecific().getPsc());
                edit_tac.setText(cellScanSibCell.getTechSpecific().getTac());
                edit_pci.setText(cellScanSibCell.getTechSpecific().getPci());
            }
        }
    }

    private boolean CheckTextEmpty() {
        if (TextUtils.isEmpty(edit_channel.getText().toString())) {
            edit_channel.requestFocus();
            edit_channel.setError("N/A");
        } else if (TextUtils.isEmpty(edit_rncid.getText().toString())) {
            edit_rncid.requestFocus();
            edit_rncid.setError("N/A");
        } else if (TextUtils.isEmpty(edit_cid.getText().toString())) {
            edit_cid.requestFocus();
            edit_cid.setError("N/A");
        } else if (strCurTech.equals("4G") && TextUtils.isEmpty(edit_tac.getText().toString())) {
            edit_tac.requestFocus();
            edit_tac.setError("N/A");
        } else if (strCurTech.equals("4G") && TextUtils.isEmpty(edit_pci.getText().toString())) {
            edit_pci.requestFocus();
            edit_pci.setError("N/A");
        } else if(strCurTech.equals("3G") && TextUtils.isEmpty(edit_lac.getText().toString())) {
            edit_lac.requestFocus();
            edit_lac.setError("N/A");
        } else if(strCurTech.equals("3G") && TextUtils.isEmpty(edit_psc.getText().toString())) {
            edit_psc.requestFocus();
            edit_psc.setError("N/A");
        } else {
            return true;
        }
        return false;
    }
}
