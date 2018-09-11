package com.bravo.fragments;

import android.os.Bundle;
import android.widget.TextView;

import com.bravo.R;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class ContentFragmentOne extends RevealAnimationBaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
    }

    @Override
    public void initView() {
        TextView tv = (TextView) contentView.findViewById(R.id.testTv);
        tv.setText("Fragment One !!!");
        tv.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

}

