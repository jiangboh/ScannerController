package com.bravo.config;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.adapters.AdapeterBlackWhiteImsi;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.custom_view.RecordOnItemClick;
import com.bravo.custom_view.RecordOnItemLongClick;
import com.bravo.data_ben.DeviceDataStruct;
import com.bravo.data_ben.DeviceFragmentStruct;
import com.bravo.data_ben.WaitDialogData;
import com.bravo.database.BlackWhiteImsi;
import com.bravo.database.BlackWhiteImsiDao;
import com.bravo.dialog.DialogAddImsi;
import com.bravo.dialog.DialogCustomBuilder;
import com.bravo.dialog.WaitDialog;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.fragments.SerializableHandler;
import com.bravo.socket_service.CommunicationService;
import com.bravo.utils.FileUtils;
import com.bravo.utils.Logs;
import com.bravo.utils.Utils;
import com.bravo.xml.HandleRecvXmlMsg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.bravo.R.drawable.btn_config_selector;

/**
 * Created by admin on 2018-11-7.
 */

public class FragmentWhiteImsi extends RevealAnimationBaseFragment {
    private final String TAG = "FragmentWhiteImsi";
    private final int OPEN_DIALOG = 0;

    public static boolean isOpen = false;
    private static ArrayList<String> SendOkList = new ArrayList<>();

    private int selectedItem = -1;
    private ArrayList<WaitDialogData> sendDateList = new ArrayList<>();

    private String md5="";
    private Button AddButton;
    private Button EditButton;
    private Button SelAllButton;
    private Button NoSelAllButton;
    private Button DelButton;
    private ListView listView;
    private AdapeterBlackWhiteImsi adapeter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logs.d(TAG,"onCreate",true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_black_imsi);
    }

    @Override
    public void onResume() {
        Logs.d(TAG,"onResume",true);
        super.onResume();
        loadData();

        showCheckBox(false);
        listView.setLongClickable(true);

        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity)context).getSettingBtn().setImageResource(btn_config_selector);
        ((RevealAnimationActivity)context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                dabase2file();
            }
        });
        isOpen = true;
    }

    @Override
    public void initView() {
        Logs.d(TAG,"initView",true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        Logs.d(TAG,"initData",true);

        AddButton = (Button) contentView.findViewById(R.id.btnAdd);
        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAddImsi dialog = new DialogAddImsi(context,false,null);
                dialog.setSaveListener(new DialogAddImsi.OnSaveData2Database() {
                    @Override
                    public boolean onSave(BlackWhiteImsi imsiInfo) {
                        return saveData(imsiInfo);
                    }
                });
                //这句话，就是决定上面的那个黑框，也就是dialog的title。
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();
            }
        });

        EditButton = (Button) contentView.findViewById(R.id.btnEdit);
        EditButton.setEnabled(false);
        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DialogAddImsi dialog = new DialogAddImsi(context,false,adapeter.getItem(selectedItem));
                dialog.setSaveListener(new DialogAddImsi.OnSaveData2Database() {
                    @Override
                    public boolean onSave(BlackWhiteImsi imsiInfo) {
                        return saveData(imsiInfo);
                    }
                });
                //这句话，就是决定上面的那个黑框，也就是dialog的title。
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();
            }
        });

        SelAllButton = (Button) contentView.findViewById(R.id.btnSelAll);
        SelAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                adapeter.setAllChecked(true);
            }
        });

        NoSelAllButton = (Button) contentView.findViewById(R.id.btnNoSelAll);
        NoSelAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                adapeter.setAllChecked(false);
            }
        });

        DelButton = (Button) contentView.findViewById(R.id.btnDel);
        DelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogCustomBuilder dialog = new DialogCustomBuilder(context,"删除白名单",
                        "删除所有选中的白名单?");
                dialog.setOkListener(new DialogCustomBuilder.OkBtnClickListener() {
                    @Override
                    public void onBtnClick(DialogInterface arg0, int arg1) {
                        delData();
                    }
                });
                dialog.setCancelListener(new DialogCustomBuilder.CancelBtnClickListener() {
                    @Override
                    public void onBtnClick(DialogInterface arg0, int arg1) {
                    }
                });
                dialog.show();
            }
        });

        listView = (ListView) contentView.findViewById(R.id.black_imsi_list);
        adapeter = new AdapeterBlackWhiteImsi(context,false);
        adapeter.setAllSelectListener(new AdapeterBlackWhiteImsi.OnAllSelect() {
            @Override
            public boolean onSelect(int allnum,int selnum) {
                if (allnum == selnum) {  //全部选中
                    SelAllButton.setVisibility(View.GONE);
                    NoSelAllButton.setVisibility(View.VISIBLE);
                } else {
                    SelAllButton.setVisibility(View.VISIBLE);
                    NoSelAllButton.setVisibility(View.GONE);
                }
                if (selnum != 0) {  //有被选中项
                    DelButton.setEnabled(true);
                } else {
                    DelButton.setEnabled(false);
                }
                return true;
            }
        });
        listView.setAdapter(adapeter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.setOnItemLongClickListener(new RecordOnItemLongClick() {
            @Override
            public void recordOnItemLongClick(AdapterView<?> parent, View view, final int position, long id, String strMsg) {
                if (adapeter.isShow()) {
                    return;
                } else {
                    //showOpervate();
                    listView.setLongClickable(false);
                    adapeter.setChecked(position);
                    showCheckBox(true);
                }
                return;
            }
        });
        listView.setOnItemClickListener(new RecordOnItemClick() {
            @Override
            public void recordOnItemClick(AdapterView<?> arg0, View view, int position,
                                          long id, String strMsg) {
                if (selectedItem != position) {
                    selectedItem = position;
                    EditButton.setEnabled(true);
                } else {
                    selectedItem = -1;
                    EditButton.setEnabled(false);
                }
                adapeter.setSelectedItem(selectedItem);
            }
        });

        loadData();
    }

    private void showCheckBox(boolean isShow) {
        if (isShow) {
            adapeter.setIsShow(true);
            AddButton.setEnabled(false);
            SelAllButton.setEnabled(true);
            NoSelAllButton.setEnabled(true);
            DelButton.setEnabled(true);
        } else {
            adapeter.setIsShow(false);
            AddButton.setEnabled(true);
            SelAllButton.setEnabled(false);
            NoSelAllButton.setEnabled(false);
            DelButton.setEnabled(false);
        }
    }

    private boolean saveData(BlackWhiteImsi imsiInfo) {
        BlackWhiteImsi FindImsi = null;
        if (imsiInfo.getImsi().trim().length() > 0) {
            FindImsi = ProxyApplication.getDaoSession().getBlackWhiteImsiDao().
                    queryBuilder().where(BlackWhiteImsiDao.Properties.Type.eq(BlackWhiteImsi.WHITE),
                    BlackWhiteImsiDao.Properties.Imsi.eq(imsiInfo.getImsi())).
                    build().unique();
            if (FindImsi != null) { //数据存在更新
                imsiInfo.setId(FindImsi.getId());
                ProxyApplication.getDaoSession().getBlackWhiteImsiDao().update(imsiInfo);
                adapeter.updataByImsi(imsiInfo);
                CustomToast.showToast(context, "该IMSI(" + imsiInfo.getImsi() + ")存在,已被更新");
            } else {//不存在保存
                ProxyApplication.getDaoSession().getBlackWhiteImsiDao().insert(imsiInfo);
                adapeter.addData(imsiInfo);
            }
        } else {
            if (imsiInfo.getImei().trim().length() > 0) {
                FindImsi = ProxyApplication.getDaoSession().getBlackWhiteImsiDao().
                        queryBuilder().where(BlackWhiteImsiDao.Properties.Type.eq(BlackWhiteImsi.WHITE),
                        BlackWhiteImsiDao.Properties.Imei.eq(imsiInfo.getImei())).
                        build().unique();
                if (FindImsi != null) { //数据存在更新
                    imsiInfo.setId(FindImsi.getId());
                    ProxyApplication.getDaoSession().getBlackWhiteImsiDao().update(imsiInfo);
                    adapeter.updataByImei(imsiInfo);
                    CustomToast.showToast(context, "该IMEI(" + imsiInfo.getImei() + ")存在,已被更新");
                } else {//不存在保存
                    ProxyApplication.getDaoSession().getBlackWhiteImsiDao().insert(imsiInfo);
                    adapeter.addData(imsiInfo);
                }
            }
        }
        return true;
    }

    private void delData() {
        ArrayList<BlackWhiteImsi> dataList = adapeter.getDataList();
        for(int i=0;i<dataList.size();i++) {
            if (dataList.get(i).isChecked()) {
                ProxyApplication.getDaoSession().getBlackWhiteImsiDao().delete(dataList.get(i));
            }
        }
        loadData();
        adapeter.notifyDataSetChanged();
    }

    private void loadData() {
        List<BlackWhiteImsi> imsiList = ProxyApplication.getDaoSession().getBlackWhiteImsiDao().
                queryBuilder().where(BlackWhiteImsiDao.Properties.Type.eq(BlackWhiteImsi.WHITE)).
                build().list();
        if (imsiList != null) {
            adapeter.setDataList(imsiList);
        }
    }

    private void dabase2file() {
        FileUtils fileUtils = new FileUtils(context);
        File file = new File(fileUtils.getFileCacheDir() + File.separator + "whitelist.txt");
        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<BlackWhiteImsi> imsiList =
                ProxyApplication.getDaoSession().getBlackWhiteImsiDao().queryBuilder().
                        where(BlackWhiteImsiDao.Properties.Type.eq(BlackWhiteImsi.WHITE)).
                        orderAsc(BlackWhiteImsiDao.Properties.Imsi).list();
        if (imsiList != null) {
            for(int i =0;i<imsiList.size();i++) {
                String str = String.format("%s\n",imsiList.get(i).getImsi(),
                        imsiList.get(i).getStartRb(),imsiList.get(i).getStopRb());
                try {
                    fileUtils.writeTextFile(file,str,true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        md5 = fileUtils.getFileMD5(file);
        Logs.d(TAG,"文件MD5值:(" + md5 + ")");

        if (md5.equals(CommunicationService.WhiteMd5)) {
        //    CustomToast.showToast(context, "白名单没有修改");
        //    return;
        }
        else
        {
            Log.d(TAG,"白名单修改了，删除以前保存的发送成功的sn");
            SendOkList.clear();//名单修改了，删除以前保存的发送成功的sn
        }

        DialogCustomBuilder dialog = new DialogCustomBuilder(context,"下发白名单","将白名单下发到所有在线AP?");
        dialog.setOkListener(new DialogCustomBuilder.OkBtnClickListener() {
            @Override
            public void onBtnClick(DialogInterface arg0, int arg1) {
                CommunicationService.WhiteMd5 = md5;

                sendDateList.clear();
                ArrayList<DeviceDataStruct> devicelist = DeviceFragmentStruct.getList();
                for (int i=0;i<devicelist.size();i++) {
                    if (SendOkList.contains(devicelist.get(i).getSN())) {
                        //已发送成功的AP直接显示成功
                        sendDateList.add(new WaitDialogData(
                                HandleRecvXmlMsg.AP_DATA_ALIGN_SET, devicelist.get(i).getSN(),WaitDialogData.RUSULT_OK));
                    }
                    else
                    {
                        sendDateList.add(new WaitDialogData(HandleRecvXmlMsg.AP_DATA_ALIGN_SET, devicelist.get(i).getSN()));
                    }
                }
                if (sendDateList.size() ==0) {
                    CustomToast.showToast(context, "没有要下发的设备");
                    return;
                }
                Message message = new Message();
                message.what = OPEN_DIALOG;
                handler.sendMessage(message);

                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        Utils.mySleep(2000);//休眠2秒
                        for(WaitDialogData list : sendDateList)
                        {
                            if (list.getiRusult() == WaitDialogData.WAIT_SEND)
                                new HandleRecvXmlMsg(context).GetGeneralParaRequest(list.getTitle());
                        }
                    }
                }.start();
            }
        });
        dialog.setCancelListener(new DialogCustomBuilder.CancelBtnClickListener() {
            @Override
            public void onBtnClick(DialogInterface arg0, int arg1) {
            }
        });
        dialog.show();

        return;
    }

    @Override
    public void onPause() {
        Logs.d(TAG,"onPause",true);
        //saveData();
        isOpen = false;
        super.onPause();
    }

    @Override
    public void onStop() {
        Logs.d(TAG,"onStop",true);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Logs.d(TAG,"onDestroy",true);
        super.onDestroy();

    }


    private Handler handler = new SerializableHandler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case OPEN_DIALOG:
                    WaitDialog waitDialog = new WaitDialog(context);
                    waitDialog.setList(sendDateList);
                    waitDialog.setDataAlignFlag(true);
                    waitDialog.setSendOkListener(new WaitDialog.onIsSendOkListener() {
                        @Override
                        public void isSendOk(String sn) {
                            SendOkList.add(sn);
                        }
                    });
                    waitDialog.show();
                    break;
                default:
                    break;
            }
        }
    };
}
