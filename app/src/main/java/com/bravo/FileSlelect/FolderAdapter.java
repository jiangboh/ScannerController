package com.bravo.FileSlelect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bravo.R;

import java.util.List;


public class FolderAdapter extends BaseAdapter {

    private List<FolderInfo> folderlist;
    private Context mContext;
    private LayoutInflater inflater;

    public FolderAdapter(List<FolderInfo> folderlist, Context mContext){
        this.folderlist=folderlist;
        this.mContext=mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return folderlist.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return folderlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder =new ViewHolder();
            convertView = inflater.inflate(R.layout.listview_folder, null);
            viewHolder.ivIcon=(ImageView) convertView.findViewById(R.id.iv_folderIcon);
            viewHolder.tvTotal=(TextView) convertView.findViewById(R.id.tv_folderTotal);
            viewHolder.tvTime=(TextView) convertView.findViewById(R.id.tv_folderTime);
            viewHolder.tvName=(TextView) convertView.findViewById(R.id.tv_folderName);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ivIcon.setImageResource(folderlist.get(position).getFolderIcon());
        viewHolder.tvName.setText(folderlist.get(position).getFolderName());
        viewHolder.tvTotal.setText(folderlist.get(position).getFolderToTal());
        viewHolder.tvTime.setText(folderlist.get(position).getFolderTime());
        return convertView;
    }

    class ViewHolder {
        public ImageView ivIcon;
        public TextView tvTotal,tvTime,tvName;
    }

}
