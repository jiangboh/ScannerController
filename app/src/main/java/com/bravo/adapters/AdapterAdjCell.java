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
import android.widget.TextView;

import com.bravo.R;
import com.bravo.femto.FragmentAdjacentCell;
import com.bravo.parse_generate_xml.cell_scan.CellScanSibCell;

import java.util.ArrayList;

/**
 * Created by Jack.liao on 2017/4/20.
 */

public class AdapterAdjCell extends BaseAdapter implements Filterable {

    private Context context;
    private MyFilter mKeywrodFilter;
    private FragmentAdjacentCell fragmentAdjacentCell;
    private ArrayList<CellScanSibCell> sibCells = new ArrayList<>();
    private ArrayList<CellScanSibCell> mFilteredArrayList;
    private final Object mLock = new Object();

    public AdapterAdjCell(Context context, FragmentAdjacentCell fragmentAdjacentCell) {
        this.context =  context;
        this.fragmentAdjacentCell = fragmentAdjacentCell;
    }

    @Override
    public int getCount() {
        return sibCells.size();
    }

    @Override
    public CellScanSibCell getItem(int position) {
        return sibCells.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addAdjCell(CellScanSibCell cellScanSibCell) {
        sibCells.add(cellScanSibCell);
    }

    public void updateAdjCellList() {
        mFilteredArrayList = null;
        notifyDataSetChanged();
    }

    public void updateAjdCell(int iPosition, CellScanSibCell sibCell) {
        CellScanSibCell cellScanSibCell = sibCells.get(iPosition);
        cellScanSibCell.setChannel(sibCell.getChannel());
        cellScanSibCell.setRncid(sibCell.getRncid());
        cellScanSibCell.setCid(sibCell.getCid());
        if (fragmentAdjacentCell.strCurTech.equals("4G")) {
            cellScanSibCell.getTechSpecific().setTac(sibCell.getTechSpecific().getTac());
            cellScanSibCell.getTechSpecific().setPci(sibCell.getTechSpecific().getPci());
        } else if (fragmentAdjacentCell.strCurTech.equals("3g")) {
            cellScanSibCell.getTechSpecific().setLac(sibCell.getTechSpecific().getLac());
            cellScanSibCell.getTechSpecific().setPsc(sibCell.getTechSpecific().getPsc());
        }
        notifyDataSetChanged();
    }

    public void deleteadjcell(int iIndex) {
        sibCells.remove(iIndex);
        notifyDataSetChanged();
    }

    public void removeAll() {
        sibCells.clear();
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView tvChannel;
        TextView tvRncid;
        TextView tvCid;
        TextView tvLac;
        TextView tvPsc;
        TextView tvTac;
        TextView tvPci;
        TextView btnDelete;
        CheckBox checkBox;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_adjacentcell, null);
            viewHolder.tvChannel = (TextView) convertView.findViewById(R.id.channel);
            viewHolder.tvRncid = (TextView) convertView.findViewById(R.id.rncid);
            viewHolder.tvCid = (TextView) convertView.findViewById(R.id.cid);
            viewHolder.tvLac = (TextView) convertView.findViewById(R.id.lac);
            viewHolder.tvPsc = (TextView) convertView.findViewById(R.id.psc);
            viewHolder.tvTac = (TextView) convertView.findViewById(R.id.tac);
            viewHolder.tvPci = (TextView) convertView.findViewById(R.id.pci);
            viewHolder.btnDelete = (TextView) convertView.findViewById(R.id.delete);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkstate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sibCells.get(position).setbCheck(isChecked);
                fragmentAdjacentCell.updateAdjCell(sibCells.get(position));
            }
        });
        viewHolder.checkBox.setChecked(sibCells.get(position).getbCheck());

        viewHolder.tvChannel.setText(sibCells.get(position).getChannel());
        viewHolder.tvRncid.setText(sibCells.get(position).getRncid());
        viewHolder.tvCid.setText(" " + sibCells.get(position).getCid());
        if (fragmentAdjacentCell.strCurTech.equals("4G")) {
            viewHolder.tvTac.setVisibility(View.VISIBLE);
            viewHolder.tvTac.setText(" " + sibCells.get(position).getTechSpecific().getTac());
            viewHolder.tvPci.setVisibility(View.VISIBLE);
            viewHolder.tvPci.setText(" " + sibCells.get(position).getTechSpecific().getPci());
        } else if (fragmentAdjacentCell.strCurTech.equals("3G")) {
            viewHolder.tvLac.setVisibility(View.VISIBLE);
            viewHolder.tvLac.setText(" " + sibCells.get(position).getTechSpecific().getLac());
            viewHolder.tvPsc.setVisibility(View.VISIBLE);
            viewHolder.tvPsc.setText(" " + sibCells.get(position).getTechSpecific().getPsc());
        }

        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentAdjacentCell.deleteAdjCell(position);
                deleteadjcell(position);
                notifyDataSetChanged();
                fragmentAdjacentCell.userDefineListView.turnToNormal();
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
                    mFilteredArrayList = new ArrayList<>(sibCells);
                }
            }
            if (prefix == null || prefix.length() == 0) {
                synchronized (mLock) {
                    ArrayList<CellScanSibCell> list = new ArrayList<>(mFilteredArrayList);
                    results.values = list;
                    results.count = list.size();
                }
            } else {
                // 做正式的筛选
                String prefixString = prefix.toString();

                // 声明一个临时的集合对象 将原始数据赋给这个临时变量
                final ArrayList<CellScanSibCell> values = mFilteredArrayList;

                final int count = values.size();

                // 新的集合对象
                final ArrayList<CellScanSibCell> newValues = new ArrayList<>(count);

                for (int i = 0; i < count; i++) {
                    // 如果姓名的前缀相符或者电话相符就添加到新的集合
                    final CellScanSibCell value = (CellScanSibCell) values.get(i);
                    if(value.getChannel().indexOf(prefixString) != -1 ||
                            value.getRncid().indexOf(prefixString) != -1||
                            value.getCid().indexOf(prefixString) != -1) {
                        newValues.add(value);
                    } else if ((fragmentAdjacentCell.strCurTech.equals("4G") && (value.getTechSpecific().getTac().indexOf(prefixString) != -1 || value.getTechSpecific().getPci().indexOf(prefixString) != -1) ||
                            (fragmentAdjacentCell.strCurTech.equals("3g") && (value.getTechSpecific().getLac().indexOf(prefixString) != -1 || value.getTechSpecific().getPsc().indexOf(prefixString) != -1))))
                    {
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
            sibCells = (ArrayList<CellScanSibCell>) results.values;

            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
