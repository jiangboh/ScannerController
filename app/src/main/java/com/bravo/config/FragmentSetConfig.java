package com.bravo.config;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.custom_view.CustomToast;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.database.FemtoList;
import com.bravo.database.FemtoListDao;
import com.bravo.femto.ButtonUtils;
import com.bravo.femto.IPEdit;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.parse_generate_xml.Status;
import com.bravo.parse_generate_xml.udp.ActionResponse;
import com.bravo.parse_generate_xml.udp.SetConfig;
import com.bravo.utils.Logs;
import com.bravo.utils.SharePreferenceUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.bravo.femto.BcastCommonApi.sendUdpMsg;

/**
 * Created by Jack.liao on 2016/11/4.
 */

public class FragmentSetConfig extends RevealAnimationBaseFragment {
    private final String TAG = "SetConfigFragment";
    private Spinner spinner_mode;
    private EditText edit_port;
    private EditText edit_retry;
    private IPEdit ipEdit_default_gw;
    private IPEdit ipEdit_nb_gw;
    private String strControlIP;
    private Spinner spinner_interval;
    String strCurTech;
    @Override
    public void onResume() {
        super.onResume();
        loadData();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity)context).getSettingBtn().setImageResource(R.drawable.btn_config_selector);
        ((RevealAnimationActivity)context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                if (CheckObserverMode(strControlIP) && !ButtonUtils.isFastDoubleClick()){
                    SetConfig();
                }

                InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0); //强制隐藏键盘
                super.recordOnClick(v, "Set Config Femto Event");
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setconfig);
    }

    @Override
    public void initView() {
        //spinner mode config
        spinner_mode = (Spinner) contentView.findViewById(R.id.spinner_mode);
        spinner_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    contentView.findViewById(R.id.layout_nb_gw).setVisibility(View.GONE);
                } else {
                    contentView.findViewById(R.id.layout_nb_gw).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //spinner_interval
        spinner_interval = (Spinner) contentView.findViewById(R.id.spinner_interval);
        //port
        edit_port = (EditText) contentView.findViewById(R.id.tcp_port);
        //retry
        edit_retry = (EditText) contentView.findViewById(R.id.tcp_retry);
        //default-gw
        ipEdit_default_gw = (IPEdit) contentView.findViewById(R.id.ip_default);

        //nb-gw
        ipEdit_nb_gw = (IPEdit) contentView.findViewById(R.id.ip_nb);

        if (spinner_mode.getSelectedItemPosition() == 1) {
            contentView.findViewById(R.id.layout_default_gw).setVisibility(View.GONE);
            contentView.findViewById(R.id.layout_nb_gw).setVisibility(View.GONE);
        }
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        strControlIP = SharePreferenceUtils.getInstance(context).getString("status_notif_controller_ip" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
    }

    @Override
    public void onPause() {
        saveData();
        super.onPause();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void saveData() {
        SharedPreferences preferences = context.getSharedPreferences("set_config",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("port" + strCurTech, edit_port.getText().toString());
        editor.putString("retry" + strCurTech, edit_retry.getText().toString());
        editor.putInt("mode" + strCurTech, spinner_mode.getSelectedItemPosition());
        editor.putString("default_gw" + strCurTech, ipEdit_default_gw.getIPAddtrss());
        editor.putString("nb_gw" + strCurTech, ipEdit_nb_gw.getIPAddtrss());
        editor.putInt("interval_time" + strCurTech, spinner_interval.getSelectedItemPosition());
        editor.commit();
    }

    private void loadData() {
        strCurTech = SharePreferenceUtils.getInstance(context).getString("status_notif_tech" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        if (TextUtils.isEmpty(strCurTech)) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences("set_config", MODE_PRIVATE);
        String mode = SharePreferenceUtils.getInstance(context).getString("status_notif_mode" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ((ProxyApplication)context.getApplicationContext()).getiTcpPort(), "");
        if(TextUtils.isEmpty(mode)) {
            spinner_mode.setSelection(sp.getInt("mode" + strCurTech, 1));
        } else {
            if (mode.equals("CNE")) {
                spinner_mode.setSelection(1);
            }
        }
        edit_port.setText(sp.getString("port" + strCurTech, "8021"));
        edit_retry.setText(sp.getString("retry" + strCurTech, "3"));
        ipEdit_default_gw.initIpAddress(sp.getString("default_gw" + strCurTech,"172.17.1.1"));
        ipEdit_nb_gw.initIpAddress(sp.getString("nb_gw" + strCurTech, "172.17.1.18"));
        spinner_interval.setSelection(sp.getInt("interval_time" + strCurTech, 5));
    }

    private void SetConfig() {
        if (!ipEdit_nb_gw.getValid() || !ipEdit_default_gw.getValid()) {
             CustomToast.showToast(context, "Please input correct default_gw address");
        } else {
            SetConfig setConfig = new SetConfig();
            if(spinner_mode.getSelectedItemPosition() == 0){
                setConfig.setMode("CN");
                if (!TextUtils.isEmpty(ipEdit_nb_gw.getIPAddtrss())) {
                    setConfig.setNbGw(ipEdit_nb_gw.getIPAddtrss());
                } else {
                     CustomToast.showToast(context, "Please input correct nb_gw address");
                }

            } else {
                setConfig.setMode("CNE");
            }
            setConfig.setStatusInterval(spinner_interval.getSelectedItem().toString());
            setConfig.setDefaultGw(ipEdit_default_gw.getIPAddtrss());
            setConfig.setTcpPort(edit_port.getText().toString());
            setConfig.setTcpRetry(edit_retry.getText().toString());
            sendUdpMsg(context, setConfig.toXml(setConfig));
            saveData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ActionRes(ActionResponse as) {
        if (as.getActionType().equals("set-config")) {
            if (as.getActionStatus().equals("SUCCESS")) {
                CustomToast.showToast(context, "Set Config Success");
                if (!updateFemtoListInfo()) {
//                    Socket socket = ((ProxyApplication) context.getApplicationContext()).getCurSocket();
//                    if (spinner_interval.getSelectedItemPosition() != 0 && socket != null && socket.isConnected()) {
//                        try {
//                            socket.setSoTimeout(spinner_interval.getSelectedItemPosition() * 4000);
//                        } catch (SocketException e) {
//                            e.printStackTrace();
//                        }
//                    } else if (socket != null){
//                        try {
//                            socket.setSoTimeout(100000);
//                        } catch (SocketException e) {
//                            e.printStackTrace();
//                        }
//                    }
                }
            } else {
                 CustomToast.showToast(context, "Set Config Failure");
            }
        }
    }

    private boolean updateFemtoListInfo() {
        FemtoList femtoList = ProxyApplication.getDaoSession().getFemtoListDao().queryBuilder().where(FemtoListDao.Properties.Mac.eq(((ProxyApplication)context.getApplicationContext()).getCurMacAddress()),
                FemtoListDao.Properties.UdpPort.eq(((ProxyApplication)context.getApplicationContext()).getiUdpPort())).build().unique();
        if (femtoList != null && femtoList.getPort() != Integer.parseInt(edit_port.getText().toString())) { //数据存在更新且端口号不对就更新数据库
            femtoList.setPort(Integer.parseInt(edit_port.getText().toString()));
            ProxyApplication.getDaoSession().getFemtoListDao().update(femtoList);
//            EventBus.getDefault().post(new EventBusMsgCloseSocket(femtoList.getIp(), ((ProxyApplication)context.getApplicationContext()).getiUdpPort()));
            List<FemtoList> femtoLists = ProxyApplication.getDaoSession().getFemtoListDao().queryBuilder().list();
            for (int i = 0; i < femtoLists.size(); i++) {
                Logs.d(TAG, "lmj config1 SSID=" +  femtoLists.get(i).getSSID() + ",Mac=" + femtoLists.get(i).getMac() + ",Ip=" + femtoLists.get(i).getIp() + ",Port=" + femtoLists.get(i).getPort());
            }
            return true;
        } else if(femtoList == null && ((ProxyApplication)(context.getApplicationContext())).getiTcpPort() != Integer.parseInt(edit_port.getText().toString())){
            femtoList = new FemtoList();
            ProxyApplication pa = (ProxyApplication)(context.getApplicationContext());
            femtoList.setIp(pa.getCurSocketAddress());
            femtoList.setMac(pa.getCurMacAddress());
            femtoList.setPort(Integer.parseInt(edit_port.getText().toString()));
            femtoList.setSSID(pa.getCurSocketAddress());
            femtoList.setUdpPort(((ProxyApplication)context.getApplicationContext()).getiUdpPort());
            ProxyApplication.getDaoSession().getFemtoListDao().save(femtoList);
            List<FemtoList> femtoLists = ProxyApplication.getDaoSession().getFemtoListDao().queryBuilder().list();
            for (int i = 0; i < femtoLists.size(); i++) {
                Logs.d(TAG, "lmj config2 SSID=" +  femtoLists.get(i).getSSID() + ",Mac=" + femtoLists.get(i).getMac() + ",Ip=" + femtoLists.get(i).getIp() + ",Port=" + femtoLists.get(i).getPort());
            }
            return true;
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void StatusNotif(Status s) {
        strControlIP = s.getControllerClient();
    }
}
