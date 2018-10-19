package com.bravo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.custom_view.RecordOnItemClick;
import com.bravo.custom_view.RecordOnItemLongClick;
import com.bravo.utils.Logs;

import java.util.ArrayList;

/**
 * Created by admin on 2018-10-19.
 */

public class WaitDialog extends Dialog {
    private final String TAG="WaitDialog";
    private TextView progress_dialog_title ;
    private ListView listView;
    private Context mContext;

    public class WaitDialogData {
        private String title;
        private String rusult;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getRusult() {
            return rusult;
        }

        public void setRusult(String rusult) {
            this.rusult = rusult;
        }
    }

    public class AdapterReqList extends BaseAdapter {
        private  ArrayList<WaitDialogData> dateList = new ArrayList<>();

        public AdapterReqList(ArrayList<WaitDialogData> dateList) {
            this.dateList = dateList;
        }

        @Override
        public int getCount() {
            return dateList.size();
        }

        @Override
        public Object getItem(int position) {
            return dateList.get(position);
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
            holder.requestTitle.setText(dateList.get(position).getTitle());
            holder.requestResult.setText(dateList.get(position).getRusult());

            return convertView;
        }

        private class ViewHolder {
            TextView requestTitle;
            TextView requestResult;
        }
    }

    public WaitDialog(Context context) {
        super(context);//设置样式
        setCanceledOnTouchOutside(false);//按对话框以外的地方不起作用，按返回键可以取消对话框
        getWindow().setGravity(Gravity.CENTER);
        setContentView(R.layout.wait_dialog_style);
        this.mContext = context;
        progress_dialog_title = (TextView) findViewById(R.id.progress_dialog_title);
        listView = (ListView) findViewById(R.id.requselist);
    }


    public void setList(ArrayList<WaitDialogData> dataList) {
        AdapterReqList adapterReq = new AdapterReqList(dataList);
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
}
