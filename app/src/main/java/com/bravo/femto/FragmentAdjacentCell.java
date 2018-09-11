package com.bravo.femto;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.adapters.AdapterAdjCell;
import com.bravo.custom_view.OneBtnHintDialog;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.custom_view.RecordOnItemLongClick;
import com.bravo.database.AdjacentCell;
import com.bravo.database.AdjacentCellDao;
import com.bravo.dialog.DialogAddAdjCell;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.listview.UserDefineListView;
import com.bravo.parse_generate_xml.cell_scan.CellScanSibCell;
import com.bravo.parse_generate_xml.cell_scan.CellScanTechSpecific;
import com.bravo.utils.Logs;
import com.bravo.utils.SharePreferenceUtils;

import java.util.List;

/**
 * Created by Jack.liao on 2017/4/20.
 */

public class FragmentAdjacentCell extends RevealAnimationBaseFragment {
    public UserDefineListView userDefineListView;
    private Button BtnAdd;
    private EditText edit_search;
    private AdapterAdjCell adapterAdjCell;
    public String strCurTech;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_adjacentcell);
    }
    @Override
    public void initView() {
        adapterAdjCell = new AdapterAdjCell(context, this);
        userDefineListView = (UserDefineListView) contentView.findViewById(R.id.adjcell_list);
        userDefineListView.setAdapter(adapterAdjCell);
        userDefineListView.setOnItemLongClickListener(new RecordOnItemLongClick() {
            @Override
            public void recordOnItemLongClick(AdapterView<?> parent, View view, final int position, long id, String strMsg) {
                if (userDefineListView.canClick()) {
                    DialogAddAdjCell dialogAddAdjCell = new DialogAddAdjCell(context, R.style.dialog_style, adapterAdjCell.getItem(position), new DialogAddAdjCell.OnAddAdjCellDialogListener() {
                        @Override
                        public void AddAdjCellCallBack(CellScanSibCell cellScanSibCells) {
                            isExistAdjCell(cellScanSibCells, position);
                        }

                    });
                    dialogAddAdjCell.show();
                }
                super.recordOnItemLongClick(parent, view, position, id, "AdjacentCell Item Long Event");
            }
        });

        edit_search = (EditText) contentView.findViewById(R.id.search);
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    adapterAdjCell.getFilter().filter(s);
                } else {
                    loadAdjCellList();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        BtnAdd = (Button) contentView.findViewById(R.id.add);
        BtnAdd.setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                DialogAddAdjCell dialogAddAdjCell = new DialogAddAdjCell(context, R.style.dialog_style, new DialogAddAdjCell.OnAddAdjCellDialogListener() {
                    @Override
                    public void AddAdjCellCallBack(CellScanSibCell cellScanSibCells) {
                        isExistAdjCell(cellScanSibCells, -1);
                    }

                });
                dialogAddAdjCell.show();
                super.recordOnClick(v, "Add Adjacent Cell Event");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.GONE);
        loadAdjCellList();
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        strCurTech = SharePreferenceUtils.getInstance(context).getString("status_notif_tech" +
                ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() +
                ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        if (strCurTech.equals("3G")) {
          contentView.findViewById(R.id.lac).setVisibility(View.VISIBLE);
            contentView.findViewById(R.id.psc).setVisibility(View.VISIBLE);
        } else if (strCurTech.equals("4G")) {
            contentView.findViewById(R.id.tac).setVisibility(View.VISIBLE);
            contentView.findViewById(R.id.pci).setVisibility(View.VISIBLE);
        }
    }

    private void insertAdjCell(CellScanSibCell cellScanSibCell) {
        AdjacentCell adjacentCell;
        int iCid = Integer.parseInt(cellScanSibCell.getCid());
        if (strCurTech.equals("4G")) {
            adjacentCell = new AdjacentCell(null, strCurTech, Integer.parseInt(cellScanSibCell.getChannel()), Integer.parseInt(cellScanSibCell.getRncid()), -1, -1,
                    iCid, Integer.parseInt(cellScanSibCell.getTechSpecific().getTac()), Integer.parseInt(cellScanSibCell.getTechSpecific().getPci()), cellScanSibCell.getbCheck());
        } else if (strCurTech.equals("3G")) {
            adjacentCell = new AdjacentCell(null, strCurTech, Integer.parseInt(cellScanSibCell.getChannel()), Integer.parseInt(cellScanSibCell.getRncid()),
                    Integer.parseInt(cellScanSibCell.getTechSpecific().getLac()), Integer.parseInt(cellScanSibCell.getTechSpecific().getPsc()), iCid, -1, -1,cellScanSibCell.getbCheck());
        } else {
            adjacentCell = new AdjacentCell(null, strCurTech, Integer.parseInt(cellScanSibCell.getChannel()), Integer.parseInt(cellScanSibCell.getRncid()), -1, -1,
                    iCid , -1, -1, cellScanSibCell.getbCheck());
        }
        Logs.d(TAG, strCurTech + ",channel=" + Integer.parseInt(cellScanSibCell.getChannel()) +",rncid=" + Integer.parseInt(cellScanSibCell.getRncid()) +",cid=" + iCid +",tac=" +
                Integer.parseInt(cellScanSibCell.getTechSpecific().getTac()) + ",pci=" + Integer.parseInt(cellScanSibCell.getTechSpecific().getPci()) + ",check=" + cellScanSibCell.getbCheck());
        ProxyApplication.getDaoSession().getAdjacentCellDao().insert(adjacentCell);
    }


    private void loadAdjCellList() {
        adapterAdjCell.removeAll();
        List<AdjacentCell> adjacentCells;
        if (strCurTech.equals("4G")) {
            adjacentCells = ProxyApplication.getDaoSession().getAdjacentCellDao().queryBuilder().where(AdjacentCellDao.Properties.ILac.eq(-1),
                    AdjacentCellDao.Properties.IPsc.eq(-1)).build().list();
        } else if (strCurTech.equals("3G")) {
            adjacentCells = ProxyApplication.getDaoSession().getAdjacentCellDao().queryBuilder().where(AdjacentCellDao.Properties.ITac.eq(-1),
                    AdjacentCellDao.Properties.IPci.eq(-1)).build().list();
        } else {
            adjacentCells = ProxyApplication.getDaoSession().getAdjacentCellDao().queryBuilder().where(AdjacentCellDao.Properties.ILac.eq(-1),
                    AdjacentCellDao.Properties.IPsc.eq(-1), AdjacentCellDao.Properties.ITac.eq(-1), AdjacentCellDao.Properties.IPci.eq(-1)).build().list();
        }
        for (int i = 0; i < adjacentCells.size(); i++) {
            CellScanSibCell cellScanSibCell = new CellScanSibCell();
            cellScanSibCell.setChannel(String.valueOf(adjacentCells.get(i).getIChannel()));
            cellScanSibCell.setRncid(String.valueOf(adjacentCells.get(i).getIRncid()));
            cellScanSibCell.setTechSpecific(new CellScanTechSpecific());
            cellScanSibCell.getTechSpecific().setTac(String.valueOf(adjacentCells.get(i).getITac()));
            cellScanSibCell.getTechSpecific().setPci(String.valueOf(adjacentCells.get(i).getIPci()));
            cellScanSibCell.getTechSpecific().setLac(String.valueOf(adjacentCells.get(i).getILac()));
            cellScanSibCell.getTechSpecific().setPsc(String.valueOf(adjacentCells.get(i).getIPsc()));
            cellScanSibCell.setCid(String.valueOf(adjacentCells.get(i).getICid()));
            cellScanSibCell.setbCheck(adjacentCells.get(i).getBCheck());
            adapterAdjCell.addAdjCell(cellScanSibCell);
            Logs.d(TAG, "adjacentCell=" + cellScanSibCell.toString());
        }
        adapterAdjCell.updateAdjCellList();
    }

    public void deleteAdjCell(int iIndex) {
        AdjacentCell adjacentCell;
        int iChannel, iRncid, iCid = -1, iTac = -1, iPci = -1, iPsc = -1, iLac = -1;
        iChannel = Integer.parseInt(adapterAdjCell.getItem(iIndex).getChannel());
        iRncid = Integer.parseInt(adapterAdjCell.getItem(iIndex).getRncid());
        iCid = Integer.parseInt(adapterAdjCell.getItem(iIndex).getCid());

        if (strCurTech.equals("4G")) {
            iTac  = Integer.parseInt(adapterAdjCell.getItem(iIndex).getTechSpecific().getTac());
            iPci = Integer.parseInt(adapterAdjCell.getItem(iIndex).getTechSpecific().getPci());
        } else if (strCurTech.equals("3G")) {
            iLac = Integer.parseInt(adapterAdjCell.getItem(iIndex).getTechSpecific().getLac());
            iPsc = Integer.parseInt(adapterAdjCell.getItem(iIndex).getTechSpecific().getPsc());
        }
        adjacentCell = ProxyApplication.getDaoSession().getAdjacentCellDao().queryBuilder().where(AdjacentCellDao.Properties.StrTech.eq(strCurTech),
                AdjacentCellDao.Properties.IChannel.eq(iChannel), AdjacentCellDao.Properties.IRncid.eq(iRncid),
                AdjacentCellDao.Properties.ICid.eq(iCid), AdjacentCellDao.Properties.ITac.eq(iTac), AdjacentCellDao.Properties.IPci.eq(iPci),
                AdjacentCellDao.Properties.ILac.eq(iLac), AdjacentCellDao.Properties.IPsc.eq(iPsc)).build().unique();
        if (adjacentCell != null) {
            Logs.d(TAG, "delete->adjacentCell.getId()=" + adjacentCell.getId());
            ProxyApplication.getDaoSession().getAdjacentCellDao().deleteByKey(adjacentCell.getId());
        }
    }

    private boolean isExistAdjCell(CellScanSibCell cellScanSibCell, int iPosition) {
        AdjacentCell adjacentCell;
        int iChannel, iRncid, iCid = -1, iTac = -1, iPci = -1, iPsc = -1, iLac = -1;
        iChannel = Integer.parseInt(cellScanSibCell.getChannel());
        iRncid = Integer.parseInt(cellScanSibCell.getRncid());
        iCid = Integer.parseInt(cellScanSibCell.getCid());

        if (strCurTech.equals("4G")) {
            iTac  = Integer.parseInt(cellScanSibCell.getTechSpecific().getTac());
            iPci = Integer.parseInt(cellScanSibCell.getTechSpecific().getPci());
        } else if (strCurTech.equals("3G")) {
            iLac = Integer.parseInt(cellScanSibCell.getTechSpecific().getLac());
            iPsc = Integer.parseInt(cellScanSibCell.getTechSpecific().getPsc());
        }
        adjacentCell = ProxyApplication.getDaoSession().getAdjacentCellDao().queryBuilder().where(AdjacentCellDao.Properties.StrTech.eq(strCurTech),
                AdjacentCellDao.Properties.IChannel.eq(iChannel), AdjacentCellDao.Properties.IRncid.eq(iRncid),
                AdjacentCellDao.Properties.ICid.eq(iCid), AdjacentCellDao.Properties.ITac.eq(iTac), AdjacentCellDao.Properties.IPci.eq(iPci),
                AdjacentCellDao.Properties.ILac.eq(iLac), AdjacentCellDao.Properties.IPsc.eq(iPsc)).build().unique();
        if (adjacentCell != null) {
            OneBtnHintDialog hintDialog = new OneBtnHintDialog(context, R.style.dialog_style);;
            hintDialog.setCancelable(false);
            hintDialog.show();
            hintDialog.setBtnContent("OK");
            hintDialog.setTitle("Warning");
            hintDialog.setContent("Already Exist");
           return true;
        }
        if (iPosition > -1) {
            CellScanSibCell replacCell = adapterAdjCell.getItem(iPosition);
            iChannel = Integer.parseInt(replacCell.getChannel());
            iRncid = Integer.parseInt(replacCell.getRncid());
            iCid = Integer.parseInt(replacCell.getCid());

            if (strCurTech.equals("4G")) {
                iTac  = Integer.parseInt(replacCell.getTechSpecific().getTac());
                iPci = Integer.parseInt(replacCell.getTechSpecific().getPci());
            } else if (strCurTech.equals("3G")) {
                iLac = Integer.parseInt(replacCell.getTechSpecific().getLac());
                iPsc = Integer.parseInt(replacCell.getTechSpecific().getPsc());
            }
            adjacentCell = ProxyApplication.getDaoSession().getAdjacentCellDao().queryBuilder().where(AdjacentCellDao.Properties.StrTech.eq(strCurTech),
                    AdjacentCellDao.Properties.IChannel.eq(iChannel), AdjacentCellDao.Properties.IRncid.eq(iRncid),
                    AdjacentCellDao.Properties.ICid.eq(iCid), AdjacentCellDao.Properties.ITac.eq(iTac), AdjacentCellDao.Properties.IPci.eq(iPci),
                    AdjacentCellDao.Properties.ILac.eq(iLac), AdjacentCellDao.Properties.IPsc.eq(iPsc)).build().unique();
            if (adjacentCell != null) {
                adjacentCell.setIChannel(Integer.parseInt(cellScanSibCell.getChannel()));
                adjacentCell.setICid(Integer.parseInt(cellScanSibCell.getCid()));
                adjacentCell.setIRncid(Integer.parseInt(cellScanSibCell.getRncid()));
                if (strCurTech.equals("4G")) {
                    adjacentCell.setITac(Integer.parseInt(cellScanSibCell.getTechSpecific().getTac()));
                    adjacentCell.setIPci(Integer.parseInt(cellScanSibCell.getTechSpecific().getPci()));
                } else if (strCurTech.equals("3G")) {
                    adjacentCell.setILac(Integer.parseInt(cellScanSibCell.getTechSpecific().getLac()));
                    adjacentCell.setIPsc(Integer.parseInt(cellScanSibCell.getTechSpecific().getPsc()));
                }
                ProxyApplication.getDaoSession().getAdjacentCellDao().update(adjacentCell);
                adapterAdjCell.updateAjdCell(iPosition, cellScanSibCell);
            }
        } else {
            adapterAdjCell.addAdjCell(cellScanSibCell);
            insertAdjCell(cellScanSibCell);
            if (TextUtils.isEmpty(edit_search.getText().toString())) {
                adapterAdjCell.updateAdjCellList();
            } else {
                edit_search.setText("");
            }
        }
        return false;
    }

    public void updateAdjCell(CellScanSibCell cellScanSibCell) {
        AdjacentCell adjacentCell;
        int iChannel, iRncid, iCid = -1, iTac = -1, iPci = -1, iPsc = -1, iLac = -1;
        iChannel = Integer.parseInt(cellScanSibCell.getChannel());
        iRncid = Integer.parseInt(cellScanSibCell.getRncid());
        iCid = Integer.parseInt(cellScanSibCell.getCid());

        if (strCurTech.equals("4G")) {
            iTac  = Integer.parseInt(cellScanSibCell.getTechSpecific().getTac());
            iPci = Integer.parseInt(cellScanSibCell.getTechSpecific().getPci());
        } else if (strCurTech.equals("3G")) {
            iLac = Integer.parseInt(cellScanSibCell.getTechSpecific().getLac());
            iPsc = Integer.parseInt(cellScanSibCell.getTechSpecific().getPsc());
        }
        adjacentCell = ProxyApplication.getDaoSession().getAdjacentCellDao().queryBuilder().where(AdjacentCellDao.Properties.StrTech.eq(strCurTech),
                AdjacentCellDao.Properties.IChannel.eq(iChannel), AdjacentCellDao.Properties.IRncid.eq(iRncid),
                AdjacentCellDao.Properties.ICid.eq(iCid), AdjacentCellDao.Properties.ITac.eq(iTac), AdjacentCellDao.Properties.IPci.eq(iPci),
                AdjacentCellDao.Properties.ILac.eq(iLac), AdjacentCellDao.Properties.IPsc.eq(iPsc)).build().unique();
        if (adjacentCell != null) {
            adjacentCell.setBCheck(cellScanSibCell.getbCheck());
            ProxyApplication.getDaoSession().getAdjacentCellDao().update(adjacentCell);
        }
    }
}
