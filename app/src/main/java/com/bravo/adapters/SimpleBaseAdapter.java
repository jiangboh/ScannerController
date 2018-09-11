package com.bravo.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
/**
 * 对BaseAdapter 进行一定程度的封装简化书写
 *@author jintian.ming
 *@createDate 2016/12/20
 */
public abstract class SimpleBaseAdapter<T> extends BaseAdapter {

    protected List<T> lists = new ArrayList<T>();
    protected String TAG = getClass().getSimpleName();
    protected Context context;
    protected LayoutInflater inflater;

    public SimpleBaseAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void addData(T t) {
        if (lists != null) {
            lists.add(t);
            notifyDataSetChanged();
        }
    }

    public void setData(List<T> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }

    public void addDatas(List<T> lists) {
        this.lists.addAll(lists);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        return lists.size();
    }

    public void clearDatas() {
        lists.clear();
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {

        return lists.get(position);
    }

    public List<T> getDataList() {
        return lists;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

}
