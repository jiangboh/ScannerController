package com.bravo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.data_ben.TargetDataStruct;
import com.bravo.femto.FragmentTarget;

import java.util.ArrayList;


/**
 * Created by Jack.liao on 2016/9/14.
 */
public class AdapterTargetList extends BaseAdapter implements Filterable {
    private final static String TAG = "AdapterTargetList";
    private ArrayList<TargetDataStruct> targetDataStructs = new ArrayList<>();
    private ArrayList<TargetDataStruct> mFilteredArrayList;
    private Context context;
    private MyFilter mKeywrodFilter;
    private FragmentTarget ft;
    private final Object mLock = new Object();

    public AdapterTargetList(Context context, FragmentTarget ft) {
        this.context =  context;
        this.ft = ft;
    }

    @Override
    public int getCount() {
        return targetDataStructs.size();
    }

    @Override
    public TargetDataStruct getItem(int position) {
        return targetDataStructs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addTarget(TargetDataStruct targetDataStruct) {
        targetDataStructs.add(targetDataStruct);
    }

    public void updateTargetList() {
        mFilteredArrayList = null;
        notifyDataSetChanged();
    }

    public void deleteTarget(int iIndex) {
        targetDataStructs.remove(iIndex);
        notifyDataSetChanged();
    }

    public void removeAll() {
        targetDataStructs.clear();
    }

    public void coverTarget(TargetDataStruct newTarget, TargetDataStruct oldTarget) {
        for (int i = 0; i < targetDataStructs.size(); i++) {
           if (targetDataStructs.get(i).getImsi().equals(oldTarget.getImsi()) &&
                    targetDataStructs.get(i).getImei().equals(oldTarget.getImei())) {
                targetDataStructs.get(i).setImsi(newTarget.getImsi());
                targetDataStructs.get(i).setImei(newTarget.getImei());
                targetDataStructs.get(i).setName(newTarget.getName());
                targetDataStructs.get(i).setStrTech(newTarget.getStrTech());
                targetDataStructs.get(i).setStrBand(newTarget.getStrBand());
                targetDataStructs.get(i).setStrChannel(newTarget.getStrChannel());
                targetDataStructs.get(i).setbRedir(newTarget.isbRedir());
                notifyDataSetChanged();
                return;
            }
        }
    }
    private class ViewHolder {
        CheckBox cbStatus;
        TextView tvImsi;
        TextView tvImei;
        TextView tvName;
        TextView tvDelete;
        LinearLayout layout_redir;
        TextView tvTech;
        TextView tvBand;
        TextView tvChannel;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_target_list, null);
            viewHolder.cbStatus = (CheckBox) convertView.findViewById(R.id.checkstate);
            viewHolder.tvImei = (TextView) convertView.findViewById(R.id.imei);
            viewHolder.tvImsi = (TextView) convertView.findViewById(R.id.imsi);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.tvDelete = (TextView) convertView.findViewById(R.id.delete);
            viewHolder.layout_redir = (LinearLayout) convertView.findViewById(R.id.layout_redir);
            viewHolder.tvTech = (TextView) convertView.findViewById(R.id.tech);
            viewHolder.tvBand = (TextView) convertView.findViewById(R.id.band);
            viewHolder.tvChannel = (TextView) convertView.findViewById(R.id.channel);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.cbStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                targetDataStructs.get(position).setCheckbox(isChecked);
                ft.updateTargetCheckBox(targetDataStructs.get(position));
            }
        });
        viewHolder.cbStatus.setChecked(targetDataStructs.get(position).getCheckbox());

        viewHolder.tvName.setText(targetDataStructs.get(position).getName());
        viewHolder.tvImsi.setText(targetDataStructs.get(position).getImsi());
        viewHolder.tvImei.setText(targetDataStructs.get(position).getImei());
        //redir
        if (targetDataStructs.get(position).isbRedir()){
            viewHolder.layout_redir.setVisibility(View.VISIBLE);
            viewHolder.tvTech.setText(targetDataStructs.get(position).getStrTech());
            viewHolder.tvBand.setText(targetDataStructs.get(position).getStrBand());
            viewHolder.tvChannel.setText(targetDataStructs.get(position).getStrChannel());
        } else {
            viewHolder.layout_redir.setVisibility(View.GONE);
        }

        viewHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft.deleteTarget(targetDataStructs.get(position));
                deleteTarget(position);
                ft.userDefineListView.turnToNormal();
            }
        });
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (mKeywrodFilter == null) {
            mKeywrodFilter = new MyFilter();
        }
        return mKeywrodFilter;
    }

    class MyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            // 持有过滤操作完成之后的数据。该数据包括过滤操作之后的数据的值以及数量。 count:数量 values包含过滤操作之后的数据的值
            FilterResults results = new FilterResults();

            if (mFilteredArrayList == null) {
                synchronized (mLock) {
                    // 将list的用户 集合转换给这个原始数据的ArrayList
                    mFilteredArrayList = new ArrayList<>(targetDataStructs);
                }
            }
            if (prefix == null || prefix.length() == 0) {
                synchronized (mLock) {
                    ArrayList<TargetDataStruct> list = new ArrayList<>(mFilteredArrayList);
                    results.values = list;
                    results.count = list.size();
                }
            } else {
                // 做正式的筛选
                String prefixString = prefix.toString();

                // 声明一个临时的集合对象 将原始数据赋给这个临时变量
                final ArrayList<TargetDataStruct> values = mFilteredArrayList;

                final int count = values.size();

                // 新的集合对象
                final ArrayList<TargetDataStruct> newValues = new ArrayList<>(count);

                for (int i = 0; i < count; i++) {
                    final TargetDataStruct value = (TargetDataStruct) values.get(i);
                    if(value.getImei().indexOf(prefixString) != -1 ||
                            value.getImsi().indexOf(prefixString) != -1||
                            value.getName().indexOf(prefixString) != -1) {
                        newValues.add(value);
                    }
                }
                // 然后将这个新的集合数据赋给FilterResults对象
                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            // 重新将与适配器相关联的List重赋值一下
            targetDataStructs = (ArrayList<TargetDataStruct>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}