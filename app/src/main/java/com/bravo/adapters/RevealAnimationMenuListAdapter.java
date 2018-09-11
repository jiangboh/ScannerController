package com.bravo.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bravo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/2/21.
 */

public class RevealAnimationMenuListAdapter extends SimpleBaseAdapter<String> {

    private int checkedPosition = 0;
    private ArrayList<Integer> iconsResId;
    public RevealAnimationMenuListAdapter(Context context) {
        super(context);
    }

    public void setCheckedPosition(int checkedPosition){
        this.checkedPosition = checkedPosition;
        notifyDataSetChanged();
    }
    public void setData(List<String> lists, ArrayList<Integer> iconsResId) {
        this.lists = lists;
        this.iconsResId = iconsResId;
        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.reveal_animation_menu_item, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.reveal_animation_item_iv);
            holder.tv = (TextView) convertView.findViewById(R.id.reveal_animation_item_tv);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        holder.tv.setText(lists.get(position));
        if(iconsResId == null){
            holder.icon.setVisibility(View.INVISIBLE);
        }else{
            holder.icon.setVisibility(View.VISIBLE);
            holder.icon.setImageResource(iconsResId.get(position));
        }
        if(position == checkedPosition){
            holder.tv.setEnabled(true);
            convertView.setEnabled(true);
            holder.icon.setEnabled(true);
        }else{
            holder.tv.setEnabled(false);
            convertView.setEnabled(false);
            holder.icon.setEnabled(false);
        }
        return convertView;
    }
    class ViewHolder{
        ImageView icon;
        TextView tv;
    }
}
