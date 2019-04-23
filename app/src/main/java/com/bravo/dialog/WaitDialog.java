package com.bravo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.custom_view.RecordOnItemClick;
import com.bravo.custom_view.RecordOnItemLongClick;
import com.bravo.data_ben.WaitDialogData;
import com.bravo.fragments.SerializableHandler;
import com.bravo.utils.Logs;
import com.bravo.xml.HandleRecvXmlMsg;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.bravo.data_ben.WaitDialogData.RUSULT_OK;

/**
 * Created by admin on 2018-10-19.
 */

public class WaitDialog extends Dialog {
    private final String TAG="WaitDialog";
    private AdapterReqList adapterReq;
    private boolean isDataAlign = false;
    private static boolean isTimeOut = false;
    private static int checkNum = 0;

    private static Timer timer=null;
    private final int COMMAND_SEND_TIMEOUT = 0;
    private final int COMMAND_SEND_CHECK = 1;

    private TextView progress_dialog_title ;
    private ListView listView;
    private Button b_ok;
    private ProgressBar progressBar;
    private Context mContext;

    private onIsSendOkListener isSendOkListener;

    public interface onIsSendOkListener {
        void isSendOk(String sn);
    }

    public void setSendOkListener(onIsSendOkListener listener) {
        this.isSendOkListener = listener;
    }

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
            holder.requestResult.setGravity(Gravity.RIGHT | Gravity.CENTER);
            if (data.getiRusult() == data.WAIT_SEND) {
                holder.requestResult.setText("等待发送");
                holder.requestResult.setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.colordialogtext));
            } else if (data.getiRusult() == data.SEND){
                holder.requestResult.setText("已发送");
                holder.requestResult.setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.colorHalfDialogTitle));
            } else if (data.getiRusult() == RUSULT_OK){
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
                if (dataList.get(i).getId() == HandleRecvXmlMsg.AP_DATA_ALIGN_SET) { //为了匹配黑白名单设置，用sn作为主键
                    if (!isDataAlign)
                    {
                        Logs.w(TAG,"该消息不是数据对齐消息，当前为黑白名单设置状态，不处理该消息",true,true);
                        break;
                    }
                    if (dataList.get(i).getTitle().equals(wdd.getTitle())) {
                        if(dataList.get(i).getiRusult() <= wdd.getiRusult()) {
                            Logs.d(TAG,String.format("设备[%s]当前状态更改为[%d]。",
                                    wdd.getTitle(),wdd.getiRusult()),true,true);
                            dataList.get(i).setiRusult(wdd.getiRusult());
                        } else {
                            Logs.w(TAG,String.format("设备[%s]当前状态[%d]>更改状态[%d],不做状态更改。",
                                    wdd.getTitle(),dataList.get(i).getiRusult(),wdd.getiRusult()),true,true);
                        }

                        if (wdd.getiRusult() == RUSULT_OK ) //保存发送成功的sn
                        {
                            if (isSendOkListener != null)
                                isSendOkListener.isSendOk(wdd.getTitle());
                        }
                        break;
                    }
                } else {
                    if (isDataAlign)
                    {
                        Logs.w(TAG,"该消息是数据对齐消息，当前不是黑白名单设置，不处理该消息",true,true);
                        break;
                    }
                    if (dataList.get(i).getId() == wdd.getId()) {
                        dataList.get(i).setiRusult(wdd.getiRusult());
                        break;
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    public void setDataAlignFlag(boolean isDataAlign)
    {
        this.isDataAlign = isDataAlign;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        Logs.d(TAG, "onCreate", true);

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        isTimeOut = false;
        checkNum = 0;

        timer = new Timer();
        timer.schedule(new MyTimer(),1000, 1000);  //15秒未收到结果，认为命令发送失败
    }

    class MyTimer extends TimerTask implements Serializable {
        @Override
        public void run() {
            Message message = new Message();
            checkNum++;
            if (checkNum >= 15) {
                //Logs.w(TAG,"接收消息已超时。",true,true);
                message.what = COMMAND_SEND_TIMEOUT;
            }
            else
            {
                //Logs.w(TAG,"检测消息是否接收完成",true,true);
                message.what = COMMAND_SEND_CHECK;
            }
            handler.sendMessage(message);
        }
    }

    private Handler handler = new SerializableHandler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case COMMAND_SEND_TIMEOUT:
                    isTimeOut = true;
                    for (WaitDialogData wdd : adapterReq.dataList) {
                        if (wdd.getiRusult() != WaitDialogData.RUSULT_OK) {
                            wdd.setiRusult(WaitDialogData.RUSULT_FAIL);
                            adapterReq.NotifyDataSetChanged(wdd);
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                    progress_dialog_title.setText("任务全部完成");
                    b_ok.setEnabled(true);
                    b_ok.setTextColor(ContextCompat.getColor(mContext.getApplicationContext(),R.color.colorDialogOkEnable));
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    break;
                case COMMAND_SEND_CHECK:
                    boolean allSend = true;
                    for (int i=0;i<adapterReq.dataList.size();i++) {
                        //Logs.w(TAG,String.format("[%d]当前状态=%d",i,adapterReq.dataList.get(i).getiRusult()),true,true);
                        if (adapterReq.dataList.get(i).getiRusult() != WaitDialogData.RUSULT_OK &&
                                adapterReq.dataList.get(i).getiRusult() != WaitDialogData.RUSULT_FAIL) {
                            allSend = false;
                            Logs.d(TAG,String.format("[%s]当前状态为接收未完成！",
                                    adapterReq.dataList.get(i).getTitle()),true,true);
                            break;
                        }
                    }
                    if (allSend) {
                        progressBar.setVisibility(View.GONE);
                        progress_dialog_title.setText("任务全部完成");
                        b_ok.setEnabled(true);
                        b_ok.setTextColor(ContextCompat.getColor(mContext.getApplicationContext(),R.color.colorDialogOkEnable));
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onStop() {
        Logs.d(TAG,"onStop",true);
        isTimeOut = false;
        checkNum = 0;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
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

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        b_ok = (Button) findViewById(R.id.dialog_ok);
        b_ok.setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                cancel();
                //super.recordOnClick(v, "Cancel Add Target Event");
            }
        });
        b_ok.setEnabled(false);
        b_ok.setTextColor(ContextCompat.getColor(context.getApplicationContext(),R.color.colorDialogOkDisable));
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
            //return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ChangesSendStatus(WaitDialogData wdd) {
        if (isTimeOut)
        {
            Logs.w(TAG,"接收到发送状态改变事件,但此时已超时，不作状态改变。",true,true);
            return;
        }
        if (adapterReq == null) return;
        Logs.d(TAG,String.format("接收到[%s]发送状态改变事件=%d,状态=%d",
                wdd.getTitle(),wdd.getId(),wdd.getiRusult()),true,true);
        adapterReq.NotifyDataSetChanged(wdd);
    }
}
