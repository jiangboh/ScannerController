package com.bravo.fragments;

import android.os.Bundle;
import android.widget.TextView;

import com.bravo.R;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class ContentFragmentTwo extends RevealAnimationBaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
    }

    @Override
    public void initView() {
        TextView tv = (TextView) contentView.findViewById(R.id.testTv);
        tv.setText("Fragment Two !!!");
        tv.setBackgroundColor(getResources().getColor(android.R.color.white));
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

}

