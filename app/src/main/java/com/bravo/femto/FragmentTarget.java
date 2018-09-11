package com.bravo.femto;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.adapters.AdapterTargetList;
import com.bravo.custom_view.OneBtnHintDialog;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.custom_view.RecordOnItemLongClick;
import com.bravo.data_ben.TargetDataStruct;
import com.bravo.database.TargetUser;
import com.bravo.database.TargetUserDao;
import com.bravo.database.User;
import com.bravo.database.UserDao;
import com.bravo.dialog.DialogAddTarget;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.listview.UserDefineListView;
import com.bravo.parse_generate_xml.Status;
import com.bravo.utils.Logs;
import com.bravo.utils.SharePreferenceUtils;
import com.bravo.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Jack.liao on 2017/2/23.
 */

public class FragmentTarget extends RevealAnimationBaseFragment {
    private final String TAG = "FragmentTarget";
    public UserDefineListView userDefineListView;

    private EditText edit_search;
    private AdapterTargetList adapterTargetList;
    private String strControlIP;
    public String strCurTech;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_targetlist);
    }

    @Override
    public void initView() {
        adapterTargetList = new AdapterTargetList(context, this);
        userDefineListView = (UserDefineListView) contentView.findViewById(R.id.target_list);
        userDefineListView.setAdapter(adapterTargetList);
        userDefineListView.setOnItemLongClickListener(new RecordOnItemLongClick() {
            @Override
            public void recordOnItemLongClick(AdapterView<?> parent, View view, final int position, long id, String strMsg) {
                if (userDefineListView.canClick()) {
                    DialogAddTarget dialogAddTarget = new DialogAddTarget(context, R.style.dialog_style, new DialogAddTarget.OnAddTargetDialogListener() {
                        @Override
                        public void AddTargetCallBack(TargetDataStruct targetDataStruct) {
                            coverTarget(targetDataStruct, adapterTargetList.getItem(position));
                        }
                    }, adapterTargetList.getItem(position));
                    dialogAddTarget.show();
                }
                super.recordOnItemLongClick(parent, view, position, id, "Target Item Long Click Event");
            }
        });
        int navigationBarH = Utils.getNavigationBarHeight(context);
        if(navigationBarH > 0){
            userDefineListView.setPadding(0,0,0,navigationBarH);
        }

        edit_search = (EditText) contentView.findViewById(R.id.search);
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s)){
                    adapterTargetList.getFilter().filter(s);
                } else {
                    LoadTargetList();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ((Button) contentView.findViewById(R.id.add)).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                DialogAddTarget dialogAddTarget = new DialogAddTarget(context, R.style.dialog_style, new DialogAddTarget.OnAddTargetDialogListener() {
                    @Override
                    public void AddTargetCallBack(TargetDataStruct targetDataStruct) {
                        String strImsi = targetDataStruct.getImsi();
                        String strImei = targetDataStruct.getImei();
                        List<TargetUser> targetUsers;
                        if (TextUtils.isEmpty(strImsi) && TextUtils.isEmpty(strImei)) {
                            return;
                        } else if (TextUtils.isEmpty(strImsi)) {
                            targetUsers = ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(
                                    TargetUserDao.Properties.StrImei.eq(strImei)).build().list();
                        } else if (TextUtils.isEmpty(strImei)) {
                            targetUsers = ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(
                                    TargetUserDao.Properties.StrImsi.eq(strImsi)).build().list();
                        } else {
                            targetUsers = ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(
                                    ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().or(
                                            TargetUserDao.Properties.StrImei.eq(strImei), TargetUserDao.Properties.StrImsi.eq(strImsi))).build().list();
                        }
                        if (targetUsers.size() > 0) {
                            OneBtnHintDialog hintDialog = new OneBtnHintDialog(context, R.style.dialog_style);;
                            hintDialog.setCancelable(false);
                            hintDialog.show();
                            hintDialog.setBtnContent("OK");
                            hintDialog.setTitle("Warning");
                            hintDialog.setContent("Already Exist");
                        } else {
                            insertTarget(targetDataStruct);
                        }
                    }
                });
                dialogAddTarget.show();
                super.recordOnClick(v, "Add Target Event");
            }
        });
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        strControlIP = SharePreferenceUtils.getInstance(context).getString("status_notif_controller_ip" +
                ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() +
                ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        strCurTech = SharePreferenceUtils.getInstance(context).getString("status_notif_tech" +
                ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() +
                ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity)context).getSettingBtn().setImageResource(R.drawable.btn_send_selector);
        ((RevealAnimationActivity)context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                if (CheckObserverMode(strControlIP)){
                    BcastCommonApi.sendTargetList(context, strCurTech);
                }
                InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0); //强制隐藏键盘
                super.recordOnClick(v, "Send Target List Event");
            }
        });
        LoadTargetList();
    }

    private void insertTarget(TargetDataStruct targetDataStruct) {
        List<User> users = ProxyApplication.getDaoSession().getUserDao().queryBuilder().where(
                UserDao.Properties.Unique.eq(SharePreferenceUtils.getInstance(context).getString("status_notif_unique" +
                        ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() +
                        ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "")),
                UserDao.Properties.SrtImsi.eq(targetDataStruct.getImsi())).build().list();
        if (users.size() != 0) {
            User updateData = users.get(0);
            if (updateData.getIAuth() == 0) {
                updateData.setIAuth(2);
            }
            ProxyApplication.getDaoSession().getUserDao().update(updateData);
        }
        TargetUser targetUser = new TargetUser(null, targetDataStruct.getImsi(), targetDataStruct.getImei(),
                targetDataStruct.getName(), false, targetDataStruct.getStrTech(), targetDataStruct.getStrBand(), targetDataStruct.getStrChannel(),
                targetDataStruct.isbRedir());
        ProxyApplication.getDaoSession().getTargetUserDao().insert(targetUser);
        adapterTargetList.addTarget(targetDataStruct);
        if (TextUtils.isEmpty(edit_search.getText().toString())) {
            adapterTargetList.updateTargetList();
        } else {
            edit_search.setText("");
        }
    }

    public void updateTargetCheckBox(TargetDataStruct targetDataStruct) {
        TargetUser targetUser = ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(
                TargetUserDao.Properties.StrImsi.eq(targetDataStruct.getImsi()),
                TargetUserDao.Properties.StrImei.eq(targetDataStruct.getImei())).build().unique();

        if (targetUser != null) {
            targetUser.setBCheck(targetDataStruct.getCheckbox());
            ProxyApplication.getDaoSession().getTargetUserDao().update(targetUser);
            Logs.d(TAG, "lmj Update CheckBox success:name=" + targetUser.getStrName() + ",CheckBox=" + targetUser.getBCheck());
        }
    }

    private void coverTarget(TargetDataStruct newTarget, final TargetDataStruct oldTarget){
        TargetUser targetUser = ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(
                TargetUserDao.Properties.StrImsi.eq(oldTarget.getImsi()),
                TargetUserDao.Properties.StrImei.eq(oldTarget.getImei())).build().unique();
        if (targetUser != null) {
            targetUser.setStrImsi(newTarget.getImsi());
            targetUser.setStrImei(newTarget.getImei());
            targetUser.setStrName(newTarget.getName());
            targetUser.setStrTech(newTarget.getStrTech());
            targetUser.setStrBand(newTarget.getStrBand());
            targetUser.setStrChannel(newTarget.getStrChannel());
            targetUser.setBRedir(newTarget.isbRedir());
            ProxyApplication.getDaoSession().getTargetUserDao().update(targetUser);
            adapterTargetList.coverTarget(newTarget, oldTarget);
        }
    }

    private void LoadTargetList() {
        adapterTargetList.removeAll();
        List<TargetUser> targetUsers = ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().build().list();
        for (int i = 0; i < targetUsers.size(); i++) {
            TargetDataStruct targetDataStruct = new TargetDataStruct();
            targetDataStruct.setImsi(targetUsers.get(i).getStrImsi());
            targetDataStruct.setImei(targetUsers.get(i).getStrImei());
            targetDataStruct.setName(targetUsers.get(i).getStrName());
            targetDataStruct.setCheckbox(targetUsers.get(i).getBCheck());
            targetDataStruct.setStrTech(targetUsers.get(i).getStrTech());
            targetDataStruct.setStrBand(targetUsers.get(i).getStrBand());
            targetDataStruct.setStrChannel(targetUsers.get(i).getStrChannel());
            targetDataStruct.setbRedir(targetUsers.get(i).getBRedir());
            Logs.d(TAG, "lmj Load->" + targetDataStruct.toString());
            adapterTargetList.addTarget(targetDataStruct);
        }
        adapterTargetList.updateTargetList();
    }

    public void deleteTarget(TargetDataStruct targetDataStruct) {
        TargetUser targetUser = ProxyApplication.getDaoSession().getTargetUserDao().queryBuilder().where(
                TargetUserDao.Properties.StrImsi.eq(targetDataStruct.getImsi()),
                TargetUserDao.Properties.StrImei.eq(targetDataStruct.getImei())).build().unique();
        if (targetUser != null) {
            Logs.d(TAG, "lmj delete->" + targetDataStruct.toString());
            ProxyApplication.getDaoSession().getTargetUserDao().deleteByKey(targetUser.getId());
            List<User> users = ProxyApplication.getDaoSession().getUserDao().queryBuilder().where(
                    UserDao.Properties.Unique.eq(SharePreferenceUtils.getInstance(context).getString("status_notif_unique" +
                            ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() +
                            ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "")),
                    UserDao.Properties.SrtImsi.eq(targetUser.getStrImsi())).build().list();
            if (users.size() != 0) {
                User updateData = users.get(0);
                if (updateData.getIAuth() == 2) {
                    updateData.setIAuth(0);
                }
                ProxyApplication.getDaoSession().getUserDao().update(updateData);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void StatusNotif(Status s) {
        strControlIP = s.getControllerClient();
        strCurTech = s.getTech();
    }
}
