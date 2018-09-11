package com.bravo.FemtoController;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bravo.R;
import com.bravo.adapters.RevealAnimationMenuListAdapter;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.log.Local_Fragment;
import com.bravo.log.Remote_Fragment;
import com.bravo.system.Upgrade_Fragment;

import java.util.ArrayList;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class RevealAnimationActivity extends BaseActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView lv;

    private int curPosition = 0;
    private ArrayList<RevealAnimationBaseFragment> fragments;
    private ArrayList<String> menuList;
    private ArrayList<Integer> iconResList;

    private static final int CIRCULAR_REVEAL_ANIMATION_DURATION = 500;
    public static final String FRAGMENTS = "fragments";
    public static final String MENU_LIST = "menuList";
    public static final String ICON_RES_LIST = "iconResList";
    public static final String TITLE = "title";
    private ImageView settingBtn;
    private RevealAnimationMenuListAdapter adapter;
    private TextView subHeader;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_reveal_animation);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        lv = (ListView) findViewById(R.id.drawer_layout_lv);
        subHeader = (TextView) findViewById(R.id.reveal_animation_activity_subhead_title_tv);
        setActionBar();
        initStatusView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Intent intent = getIntent();
        fragments = (ArrayList<RevealAnimationBaseFragment>) intent.getSerializableExtra(FRAGMENTS);
        menuList = intent.getStringArrayListExtra(MENU_LIST);
        iconResList = intent.getIntegerArrayListExtra(ICON_RES_LIST);
        String title = intent.getStringExtra(TITLE);
        settingBtn = (ImageView) findViewById(R.id.reveal_animation_activity_setting_btn);

        TextView titleTv = (TextView)findViewById(R.id.reveal_animation_activity_title_tv);
        titleTv.setText(title);

        subHeader.setText(menuList.get(curPosition));

        adapter = new RevealAnimationMenuListAdapter(this);
        adapter.setData(menuList,iconResList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int[] coordinate = new int[2];
                view.getLocationOnScreen(coordinate);
                replaceFragment(coordinate[1],position);
                adapter.setCheckedPosition(curPosition);
                subHeader.setText(menuList.get(curPosition));
                drawerLayout.closeDrawer(lv);
            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragments.get(0))
                .commit();
    }

    public ImageView getSettingBtn(){
        return settingBtn;
    }

    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(
                this,                  // host Activity
                drawerLayout,         // DrawerLayout object
                toolbar,  // nav drawer icon to replace 'Up' caret
                R.string.drawer_open,  // "open drawer" description
                R.string.drawer_close   //"close drawer" description
        ){
            @Override
            public void onDrawerOpened(View drawerView) {
                fragments.get(curPosition).takeScreenShot();
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }

    public void changeFragment(int position,Bundle bundle){
        RevealAnimationBaseFragment fragment = fragments.get(position);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        curPosition = position;
        adapter.setCheckedPosition(curPosition);
        subHeader.setText(menuList.get(curPosition));
    }

    private void replaceFragment(int topPosition, int clickedPosition) {
        if (clickedPosition == curPosition) {
            return;
        }
        View view = findViewById(R.id.content_frame);
        int finalRadius = Math.max(view.getWidth(), view.getHeight());
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(view, 0, topPosition, 0, finalRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(CIRCULAR_REVEAL_ANIMATION_DURATION);

        findViewById(R.id.content_overlay).setBackgroundDrawable(new BitmapDrawable(getResources(), fragments.get(curPosition).getBitmap()));
        animator.start();
        RevealAnimationBaseFragment fragment = fragments.get(clickedPosition);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        curPosition = clickedPosition;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public ArrayList<RevealAnimationBaseFragment> getFragments() {
        return fragments;
    }

    public ArrayList<String> getMenuList() {
        return menuList;
    }

    @Override
    public void onBackPressed() {
        RevealAnimationBaseFragment fragment = fragments.get(curPosition);
        if(fragment instanceof Local_Fragment){
            if(((Local_Fragment)fragment).onBackPressed()){
                super.onBackPressed();
            }
        }else if(fragment instanceof Remote_Fragment){
            if(((Remote_Fragment)fragment).onBackPressed()){
                super.onBackPressed();
            }
        }else if(fragment instanceof Upgrade_Fragment){
            if(((Upgrade_Fragment)fragment).onBackPressed()){
                super.onBackPressed();
            }
        }else{
            super.onBackPressed();
        }
    }
}
