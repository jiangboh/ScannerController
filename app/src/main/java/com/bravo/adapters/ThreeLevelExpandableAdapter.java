package com.bravo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import com.bravo.R;

/**
 * Created by Jack.liao on 2016/9/27.
 */

public abstract class ThreeLevelExpandableAdapter extends BaseExpandableListAdapter {
    public static final String TAG = "ThreeLevelExpandableAdapter";
    public Context mContext;
    private OnItemClickListener mListener;

    private class CustExpListview extends ExpandableListView {

        public CustExpListview(Context context) {
            super(context);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            WindowManager wm = (WindowManager) getContext()
                    .getSystemService(Context.WINDOW_SERVICE);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(wm.getDefaultDisplay().getWidth(),
                    MeasureSpec.AT_MOST);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(wm.getDefaultDisplay().getHeight(),
                    MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public ThreeLevelExpandableAdapter(Context context,
                                       OnItemClickListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        // for the second level, every child is a single ExpandableListView
        // which has only one child
        ChildExpandableListAdapter carStyleAdapter = new ChildExpandableListAdapter(
                groupPosition, childPosition);
        CustExpListview SecondLevelexplv = new CustExpListview(mContext);
        SecondLevelexplv.setAdapter(carStyleAdapter);
        SecondLevelexplv.setGroupIndicator(null);

        return SecondLevelexplv;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ChildExpandableListAdapter extends BaseExpandableListAdapter {
        private int mFatherGroupPosition, mChildGroupPosition;

        public ChildExpandableListAdapter(int groupPosition, int childPosition) {
            mFatherGroupPosition = groupPosition;
            mChildGroupPosition = childPosition;
        }

        @Override
        public int getGroupCount() {
            // every second level has only one group,which will show second
            // level contents
            return 1;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            // every second level has only one child that is a gridview,this
            // gridview will contains three level contents
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return ThreeLevelExpandableAdapter.this.getChild(
                    mFatherGroupPosition, groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
//			if (mChildGroupPosition == 0) {
//				GridView gridView = null;
//				if (convertView == null) {
//					LayoutInflater layoutInflater = (LayoutInflater) mContext
//							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//					convertView = layoutInflater.inflate(R.layout.cell_list,
//							null);
//				}
//					return convertView;
//			} else {
//            Log.e("lmj", "mFatherGroupPosition=" + mFatherGroupPosition + ",mChildGroupPosition" + mChildGroupPosition);
            return getSecondLevleView(mFatherGroupPosition, mChildGroupPosition, isExpanded, convertView, parent);
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.cell_list_item,
                        null);
            }
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }
    /**
     * implement this method to get the view that will show on the second level
     *
     * @param firstLevelPosition
     * @param secondLevelPosition
     * @param isExpanded
     * @param convertView
     * @param parent
     * @return the view that will show on the second level
     */
    public abstract View getSecondLevleView(int firstLevelPosition,
                                            int secondLevelPosition, boolean isExpanded, View convertView,
                                            ViewGroup parent);
}
