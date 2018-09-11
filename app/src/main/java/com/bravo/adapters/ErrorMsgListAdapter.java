package com.bravo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.parse_generate_xml.ErrorNotif;
import com.bravo.utils.Utils;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/9/21.
 */

public class ErrorMsgListAdapter extends BaseExpandableListAdapter {

    @Override
    public int getGroupCount() {
        return Utils.errors.keySet().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String ipAddress = Utils.ips.get(groupPosition);
        return Utils.errors.get(ipAddress).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        String ipAddress = Utils.ips.get(groupPosition);
        return Utils.errors.get(ipAddress);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return ((ArrayList<ErrorNotif>)getGroup(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentViewHolder holder = null;
        if(convertView == null){
            holder = new ParentViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.error_msg_parent_item,null);
            holder.name = (TextView) convertView.findViewById(R.id.error_msg_femto_name);
            holder.ipAddress = (TextView) convertView.findViewById(R.id.error_msg_femto_ip);
            convertView.setTag(holder);
        }else{
            holder = (ParentViewHolder) convertView.getTag();
        }
        ArrayList<ErrorNotif> errors = (ArrayList<ErrorNotif>)getGroup(groupPosition);
        if(errors != null && errors.size() > 0){
            ErrorNotif en = errors.get(0);
            holder.name.setText(en.getName());
            holder.ipAddress.setText(en.getIpAddress());
            holder.ipAddress.setTag(en);
        }
        return convertView;
    }
    public class ParentViewHolder{
        public TextView name,ipAddress;
    }
    public class ChildViewHolder{
        public TextView errorCode,errorMsg,time;
    }
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder = null;
        if(convertView == null){
            holder = new ChildViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.error_msg_child_item,null);
            holder.errorCode = (TextView) convertView.findViewById(R.id.error_msg_code);
            holder.errorMsg = (TextView) convertView.findViewById(R.id.error_msg_msg);
            holder.time = (TextView) convertView.findViewById(R.id.error_msg_time);
            convertView.setTag(holder);
        }else{
            holder = (ChildViewHolder) convertView.getTag();
        }
        ErrorNotif en = (ErrorNotif)getChild(groupPosition,childPosition);
        holder.errorCode.setText("error code:" + en.getErrorCode());
        holder.errorMsg.setText("error msg:" + en.getDetails());
        holder.time.setText("the last time : " + en.getTime());
        holder.time.setTag(en);
        if(Utils.isIgnoreError(en)){
            holder.errorCode.setSelected(true);
            holder.errorMsg.setSelected(true);
            holder.time.setSelected(true);
            convertView.setBackgroundResource(R.color.colorErrorMsgBg);
        }else {
            holder.errorCode.setSelected(false);
            holder.errorMsg.setSelected(false);
            holder.time.setSelected(false);
            convertView.setBackgroundResource(android.R.color.white);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
