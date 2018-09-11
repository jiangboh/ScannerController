package com.bravo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.test.AutomaticTestActivity;

/**
 * Created by Jack.liao on 2017/7/3.
 */

public class AdapterAutomaticTest extends BaseAdapter  {
    private Context context;
    private String[] strs;
    private int[] iResult;//0=normal,1=success,2=failure
    private int iType;//1=config,2=scan,3==active

    public AdapterAutomaticTest(Context context, String[] strs, int iTestType) {
        this.context = context;
        this.strs = strs;
        this.iType = iTestType;
        iResult = new int[strs.length];
        for (int i = 0 ;i < iResult.length; i++) {
            iResult[i] = 0;
        }
    }

    @Override
    public int getCount() {
        return strs.length;
    }

    @Override
    public Object getItem(int position) {
        return iResult[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView tvItem;
        Button btnStart;
        Button btnLog;
        TextView tvResult;
        CheckBox checkBox;
    }

    public void setResult(int iPosition, String strResult) {
        if (strResult.equals("SUCCESS")) {
            this.iResult[iPosition] = 1;
            int iIndex = -1;
            if (iType == ((AutomaticTestActivity)context).TYPE_SCAN) {
                iIndex = iPosition;
                ((AutomaticTestActivity)context).changeCheckBox(iIndex, false);
            } else if (iType == ((AutomaticTestActivity)context).TYPE_ACTIVE){
                iIndex = iPosition + 2;
                ((AutomaticTestActivity)context).changeCheckBox(iIndex, false);
            }
        } else {
            this.iResult[iPosition] = 2;
        }

        notifyDataSetChanged();
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_automatic_test,  null);
            viewHolder.tvItem = (TextView) convertView.findViewById(R.id.item_name);
            viewHolder.btnStart = (Button) convertView.findViewById(R.id.test_start);
            viewHolder.btnLog = (Button) convertView.findViewById(R.id.test_log);
            viewHolder.tvResult = (TextView) convertView.findViewById(R.id.test_result);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkstate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        //type
        if (iType == ((AutomaticTestActivity)context).TYPE_CONFIG) {
            viewHolder.checkBox.setVisibility(View.GONE);
        }
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int iIndex = -1;
                if (iType == ((AutomaticTestActivity)context).TYPE_SCAN) {
                    iIndex = position;
                } else if (iType == ((AutomaticTestActivity)context).TYPE_ACTIVE){
                    iIndex = position + 2;
                } else{
                    return;
                }
                ((AutomaticTestActivity)context).changeCheckBox(iIndex, isChecked);
            }
        });
        //set item name
        viewHolder.tvItem.setText(strs[position]);
        //start
        viewHolder.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AutomaticTestActivity)context).sendMsg(iType, position);
            }
        });
        switch (iResult[position]) {
            case 0:
//                viewHolder.btnLog.setVisibility(View.GONE);
                viewHolder.tvResult.setVisibility(View.GONE);
                break;
            case 1:
                viewHolder.btnStart.setEnabled(false);
//                viewHolder.btnLog.setVisibility(View.GONE);
                viewHolder.tvResult.setVisibility(View.VISIBLE);
                viewHolder.tvResult.setText("Success");
                viewHolder.tvResult.setTextColor(Color.parseColor("#00FF00"));
                viewHolder.checkBox.setVisibility(View.GONE);
                break;
            case 2:
//                viewHolder.btnLog.setVisibility(View.VISIBLE);
                viewHolder.tvResult.setVisibility(View.VISIBLE);
                viewHolder.tvResult.setText("Failure");
                viewHolder.tvResult.setTextColor(Color.parseColor("#FF0000"));
                break;
            default:
//                viewHolder.btnLog.setVisibility(View.GONE);
                viewHolder.tvResult.setVisibility(View.GONE);
                break;
        }
        return convertView;
    }
}
