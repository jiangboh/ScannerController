package com.bravo.config;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.adapters.AdapeterBlackWhiteImsi;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.custom_view.RecordOnItemClick;
import com.bravo.custom_view.RecordOnItemLongClick;
import com.bravo.database.BlackWhiteImsi;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.utils.Logs;

import static com.bravo.R.drawable.btn_scan_normal;

/**
 * Created by admin on 2018-11-2.
 */

public class FragmentBlackImsi extends RevealAnimationBaseFragment {
    private final String TAG = "FragmentBlackImsi";
    public static boolean isOpen = false;

    private Button AddButton;
    private Button SelAllButton;
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

        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity)context).getSettingBtn().setImageResource(btn_scan_normal);
        ((RevealAnimationActivity)context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {

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
                // TODO Auto-generated method stub
                Log.i("匿名内部类", "点击事件");
                BlackWhiteImsi bwi = new BlackWhiteImsi();
                bwi.setImsi("460000000000001");
                bwi.setName("公司");
                bwi.setStartRb(2);
                bwi.setStopRb(60);
                adapeter.setData(bwi);
            }
        });
        SelAllButton = (Button) contentView.findViewById(R.id.btnSelAll);

        listView = (ListView) contentView.findViewById(R.id.black_imsi_list);
        adapeter = new AdapeterBlackWhiteImsi(context,true);
        listView.setAdapter(adapeter);
        listView.setOnItemLongClickListener(new RecordOnItemLongClick() {
            @Override
            public void recordOnItemLongClick(AdapterView<?> parent, View view, final int position, long id, String strMsg) {

            }
        });
        listView.setOnItemClickListener(new RecordOnItemClick() {
            @Override
            public void recordOnItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                          long arg3, String strMsg) {

            }
        });

    }


    private void saveData() {

    }

    private void loadData() {
        /*SharedPreferences sp = context.getSharedPreferences(FragmentFindConfig.TABLE_NAME, MODE_PRIVATE);
        AllFindTime = sp.getInt(FragmentFindConfig.tn_AllFindTime,10);

        SharedPreferences sp1 = context.getSharedPreferences(Fragment_SystemConfig.TABLE_NAME, MODE_PRIVATE);
        udpPort = sp1.getInt(Fragment_SystemConfig.tn_LisenPort,Fragment_SystemConfig.DefultPort);*/
    }



    @Override
    public void onPause() {
        Logs.d(TAG,"onPause",true);
        saveData();
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


}
