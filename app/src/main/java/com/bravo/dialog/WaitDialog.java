package com.bravo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.custom_view.RecordOnItemClick;
import com.bravo.custom_view.RecordOnItemLongClick;
import com.bravo.data_ben.WaitDialogData;
import com.bravo.utils.Logs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * Created by admin on 2018-10-19.
 */

public class WaitDialog extends Dialog {
    private final String TAG="WaitDialog";
    private AdapterReqList adapterReq;

    private TextView progress_dialog_title ;
    private ListView listView;
    private Button b_ok;
    private Context mContext;

    public class AdapterReqList extends BaseAdapter {
        private  ArrayList<WaitDialogData> dataList = new ArrayList<>();
        Context context;

        public AdapterReqList(Context context ,ArrayList<WaitDialogData> dateList) {
            this.context = context;
            this.dataList = dateList;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.rquset_list_item, null);
                holder.requestTitle = ((TextView) convertView.findViewById(R.id.requestTitle));
                holder.requestResult = ((TextView) convertView.findViewById(R.id.requestResult));
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            WaitDialogData data = dataList.get(position);
            holder.requestTitle.setText(data.getTitle());
            if (data.getiRusult() == data.WAIT_SEND) {
                holder.requestResult.setText("等待发送");
                holder.requestResult.setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.colordialogtext));
            } else if (data.getiRusult() == data.SEND){
                holder.requestResult.setText("已发送");
                holder.requestResult.setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.colorHalfDialogTitle));
            } else if (data.getiRusult() == data.RUSULT_OK){
                holder.requestResult.setText("成功");
                holder.requestResult.setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.colorStatusOk));
            } else if (data.getiRusult() == data.RUSULT_FAIL){
                holder.requestResult.setText("失败");
                holder.requestResult.setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.colorStatusFail));
            } else {
                holder.requestResult.setText("");
                Logs.e(TAG,"设置参数后状态值错误");
            }

            return convertView;
        }

        private class ViewHolder {
            TextView requestTitle;
            TextView requestResult;
        }

        public void NotifyDataSetChanged (WaitDialogData wdd) {
            for (int i=0;i<dataList.size();i++) {
                if (dataList.get(i).getId() == wdd.getId()) {
                    dataList.get(i).setiRusult(wdd.getiRusult());
                    break;
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        Logs.d(TAG, "onCreate", true);

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        Logs.d(TAG,"onStop",true);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public WaitDialog(Context context) {
        super(context);//设置样式
        this.mContext = context;
        setContentView(R.layout.wait_dialog_style);

        setCanceledOnTouchOutside(false);//按对话框以外的地方不起作用，按返回键可以取消对话框
        getWindow().setGravity(Gravity.CENTER);

        progress_dialog_title = (TextView) findViewById(R.id.progress_dialog_title);
        listView = (ListView) findViewById(R.id.requselist);
        b_ok = (Button) findViewById(R.id.dialog_ok);
        b_ok.setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                cancel();
                //super.recordOnClick(v, "Cancel Add Target Event");
            }
        });
    }

    public void setList(ArrayList<WaitDialogData> dataList) {
        adapterReq = new AdapterReqList(mContext,dataList);
        listView.setAdapter(adapterReq);
        try {
            listView.setOnItemLongClickListener(new RecordOnItemLongClick() {
                @Override
                public void recordOnItemLongClick(AdapterView<?> parent, View view, final int position, long id, String strMsg) {
                    return;
                }
            });
            listView.setOnItemClickListener(new RecordOnItemClick() {
                @Override
                public void recordOnItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3, String strMsg) {
                    return;
                }
            });
        }catch (Exception e) {
            Logs.e(TAG,"点击界面出错：" + e.getMessage(),true);
        }
    }
    /**
     * 设置显示文字
     *
     * @param waitMsg
     */
    public void setText(CharSequence waitMsg) {
        progress_dialog_title.setText(waitMsg);
    }

    /**
     * 设置文字
     *
     * @param resId
     */
    public void setText(int resId) {
        progress_dialog_title.setText(resId);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            //Toast.makeText(mContext.getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ChangesSendStatus(WaitDialogData wdd) {
        Logs.d(TAG,"接收到发送状态改变事件",true,true);
       adapterReq.NotifyDataSetChanged(wdd);
    }
}
