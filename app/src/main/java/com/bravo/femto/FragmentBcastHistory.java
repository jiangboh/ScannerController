package com.bravo.femto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bravo.FemtoController.BaseActivity;
import com.bravo.FemtoController.ProxyApplication;
import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.adapters.AdapterBcastHistory;
import com.bravo.custom_view.OneBtnHintDialog;
import com.bravo.custom_view.RecordOnItemClick;
import com.bravo.database.BcastHistory;
import com.bravo.database.BcastHistoryDao;
import com.bravo.database.User;
import com.bravo.database.UserDao;
import com.bravo.fragments.RevealAnimationBaseFragment;

import java.util.List;

public class FragmentBcastHistory extends RevealAnimationBaseFragment {
    private String TAG = "FragmentBcastHistory";
    private ListView bcastHistoryList;
    private AdapterBcastHistory adapterBcastHistory;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bcast_history);
    }

    @Override
    public void initView() {
        bcastHistoryList = (ListView) contentView.findViewById(R.id.bcast_history);
        adapterBcastHistory = new AdapterBcastHistory(context);
        bcastHistoryList.setAdapter(adapterBcastHistory);

        bcastHistoryList.setOnItemClickListener(new RecordOnItemClick() {
            @Override
            public void recordOnItemClick(AdapterView<?> parent, View view, int position, long id, String strMsg) {
                BcastHistory bcastHistory = (BcastHistory) adapterBcastHistory.getItem(position);
                String Unique = bcastHistory.getFemtoSn() + bcastHistory.getBand() + bcastHistory.getTech() + /*bcastHistory.getChannel() + */bcastHistory.getCid();
                List<User> users = null;
                if (((BcastHistory) adapterBcastHistory.getItem(position)).getEndtime() != 0) {
                    users = ProxyApplication.getDaoSession().getUserDao().queryBuilder().where(UserDao.Properties.Unique.eq(Unique),
                            UserDao.Properties.ConnTime.gt(((BcastHistory) adapterBcastHistory.getItem(position)).getStatrtime()),
                    UserDao.Properties.ConnTime.lt(((BcastHistory) adapterBcastHistory.getItem(position)).getEndtime())).build().list();
                } else {
                    users = ProxyApplication.getDaoSession().getUserDao().queryBuilder().where(UserDao.Properties.Unique.eq(Unique),
                            UserDao.Properties.ConnTime.gt(((BcastHistory) adapterBcastHistory.getItem(position)).getStatrtime())).build().list();
                }

                if (users.size() != 0) {
                    Intent intent = new Intent(context, HistoryActivity.class);
                    intent.putExtra("unique",Unique);
                    intent.putExtra("starttime", ((BcastHistory) adapterBcastHistory.getItem(position)).getStatrtime());
                    intent.putExtra("endtime", ((BcastHistory) adapterBcastHistory.getItem(position)).getEndtime());
                    ((BaseActivity)context).startActivityWithAnimation(intent);
                }
                super.recordOnItemClick(parent, view, position, id, "History Item Click Event " + bcastHistory.toString());
            }
        });
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public void onResume() {
        super.onResume();
        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.GONE);
        List<BcastHistory> bcastHistories = ProxyApplication.getDaoSession().getBcastHistoryDao().queryBuilder().orderDesc(BcastHistoryDao.Properties.Id).list();
        if (bcastHistories.size() == 0) {
            OneBtnHintDialog hintDialog = new OneBtnHintDialog(context, R.style.dialog_style);
            hintDialog.setListener(new OneBtnHintDialog.BtnClickListener() {
                @Override
                public void onBtnClick(View v) {
                    ((RevealAnimationActivity)context).changeFragment(0, new Bundle());
                }
            });
            hintDialog.setCancelable(false);
            hintDialog.show();
            hintDialog.setBtnContent("OK");
            hintDialog.setTitle("Warning");
            hintDialog.setContent("No History");
        } else {
            adapterBcastHistory.setBcastHistories(bcastHistories);
        }
    }
}
