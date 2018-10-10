package com.bravo.scanner;

import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.adapters.AdapterScannerSearch;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.fragments.RevealAnimationBaseFragment;

import org.greenrobot.eventbus.EventBus;

import static android.content.Context.POWER_SERVICE;

/**
 * Created by admin on 2018-9-30.
 */

public class FragmentScannerSearch extends RevealAnimationBaseFragment {
    private final String TAG = "FragmentScannerSearch";
    private final int UP_FLAG = 0;
    private final int DOWN_FLAG = 1;

    public final String TABLE_NAME = "ScannerSearch";
    public final String tn_imsi = "imsi";

    private PowerManager.WakeLock mWakeLock;
    private ListView TargetListView;
    private AdapterScannerSearch adapterScannerSearch;
    private LinearLayout imsiLayout;
    private TextView imsiText;
    private ImageView bDown;
    private boolean isDown = false;

    @Override
    public void onResume() {
        super.onResume();
        //loadData();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.VISIBLE);
        ((RevealAnimationActivity)context).getSettingBtn().setImageResource(R.drawable.btn_end_normal);
        ((RevealAnimationActivity)context).getSettingBtn().setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                /*saveData();
                SearchClick();
                switchBtu(false);*/
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_scanner_search);

        //屏幕不休眠
        PowerManager pManager = ((PowerManager) context.getSystemService(POWER_SERVICE));
        mWakeLock = pManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ON_AFTER_RELEASE, TAG);
        mWakeLock.acquire();
    }

    @Override
    public void initView() {
        /*imsiLayout = (LinearLayout)contentView.findViewById(R.id.layout_imsi);
        imsiText = (TextView) contentView.findViewById(R.id.scanner_imsi);

        bDown = (ImageView)contentView.findViewById(R.id.sInfo_down);
        bDown.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                switchBtu(!isDown);
            }
        });
        TargetListView = (ListView) contentView.findViewById(R.id.scannerlist);

        try {
            TargetListView.setOnItemLongClickListener(new RecordOnItemLongClick() {
                @Override
                public void recordOnItemLongClick(AdapterView<?> parent, View view, final int position, long id, String strMsg) {
                    return;
                }
            });
            TargetListView.setOnItemClickListener(new RecordOnItemClick() {
                @Override
                public void recordOnItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3, String strMsg) {
                    return;
                }
            });
        }catch (Exception e) {
            Logs.e(TAG,"点击界面出错：" + e.getMessage());
        }*/
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        //SharedPreferences sp = context.getSharedPreferences(TABLE_NAME, MODE_PRIVATE);
        //imsiText.setText(sp.getString(tn_imsi,""));

        //switchBtu(false);
    }

    /*private void saveData() {
        SharedPreferences preferences = context.getSharedPreferences(TABLE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(tn_imsi, imsiText.getText().toString());

        editor.commit();
    }

    private void SearchClick(){
        ArrayList<TargetDataStruct> tds = new ArrayList<TargetDataStruct>();
        ArrayList<TargetDataStruct> mList = AdapterScanner.getList();
        String imsi = imsiText.getText().toString();
        for (int i = 0; i < mList.size(); i++) {
            if (!imsi.isEmpty()) {
                if (!imsi.equals(mList.get(i).getImsi())) {
                    continue;
                }
            }

            tds.add(mList.get(i));
        }
        adapterScannerSearch = new AdapterScannerSearch(context,tds,TargetListView);
        TargetListView.setAdapter(adapterScannerSearch);
        adapterScannerSearch.notifyDataSetChanged();
    }

    private void switchBtu(boolean downflag)
    {
        if(downflag)
        {
            Message message = new Message();
            message.what = DOWN_FLAG;
            handler.sendMessage(message);
        }
        else
        {
            Message message = new Message();
            message.what = UP_FLAG;
            handler.sendMessage(message);
        }
    }

    private Handler handler = new SerializableHandler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UP_FLAG:
                    isDown = false;
                    bDown.setImageResource(R.mipmap.icon_up_defult);
                    imsiLayout.setVisibility(View.GONE);
                    break;
                case DOWN_FLAG:
                    isDown = true;
                    bDown.setImageResource(R.mipmap.icon_down_defult);
                    imsiLayout.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }

        }
    };*/
}
