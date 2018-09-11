package com.bravo.log;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bravo.FemtoController.BaseActivity;
import com.bravo.R;
import com.bravo.custom_view.RecordOnClick;
import com.bravo.pull_to_refresh.PullToRefreshBase;
import com.bravo.pull_to_refresh.PullToRefreshScrollView;
import com.bravo.pull_to_refresh.internal.LoadingLayout;
import com.bravo.utils.FileUtils;
import com.bravo.utils.Utils;

import java.io.File;
import java.io.FileInputStream;

public class LocalLogDetailActivity extends BaseActivity {
    private final String TAG = "LocalLogDetailActivity";
    private TextView logTv,pageHintTv,settingTv;
    private String fileName,dirName;
    private PullToRefreshScrollView scrollView;
    private static final int PAGE_SIZE = 20*1024;
    private int page = 0,fileLength,totalPage;
    private final static String TO_LAST_PAGE = "To last page";
    private final static String TO_FIRST_PAGE = "To first page";
    private FileUtils fileUtils;
    private File file;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_local_log_detail);
        findViewById(R.id.iv_activity_back).setOnClickListener(new RecordOnClick() {
            @Override
            public void recordOnClick(View v, String strMsg) {
                onBackPressed();
                super.recordOnClick(v, "Back Event " + TAG);
            }
        });
        settingTv = (TextView) findViewById(R.id.tv_activity_right);
        settingTv.setText(TO_LAST_PAGE);
        fileUtils = new FileUtils(mContext);
        TextView title = (TextView) findViewById(R.id.tv_activity_title);
        title.setText(R.string.femto);
        logTv = (TextView) findViewById(R.id.log_msg_tv);
        pageHintTv = (TextView) findViewById(R.id.log_msg_page_hint_tv);
        scrollView = (PullToRefreshScrollView) findViewById(R.id.log_msg_scrollView);
        LoadingLayout headerLayout = scrollView.getHeaderLayout();
        headerLayout.setPullLabel("Pull to load previous page…");
        headerLayout.setReleaseLabel("Release to load…");
        LoadingLayout footerLayout = scrollView.getFooterLayout();
        footerLayout.setPullLabel("Pull to load next page…");
        footerLayout.setReleaseLabel("Release to load…");
        scrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                page --;
                scrollView.onRefreshComplete();
                String str = fileUtils.openBigTxtFile(file,page,PAGE_SIZE);
                if(!TextUtils.isEmpty(str)){
                    logTv.setText(str);
                    if(page <= 0){
                        page = 0;
                        scrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                    }else{
                        scrollView.setMode(PullToRefreshBase.Mode.BOTH);
                    }
                }else{
                    page ++;
                }
                pageHintTv.setText((page + 1) + "/" + totalPage);
                String strS = settingTv.getText().toString();
                if(totalPage > 1 && page ==  0 && TO_FIRST_PAGE.equals(strS)){
                    settingTv.setText(TO_LAST_PAGE);
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                page ++;
                scrollView.onRefreshComplete();
                String str = fileUtils.openBigTxtFile(file,page,PAGE_SIZE);
                if(!TextUtils.isEmpty(str)){
                    logTv.setText(str);
                    scrollView.getRefreshableView().scrollTo(0,0);
                    if(page >= totalPage){
                        page = totalPage;
                        scrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }else{
                        scrollView.setMode(PullToRefreshBase.Mode.BOTH);
                    }
                }else{
                    page --;
                }
                pageHintTv.setText((page + 1) + "/" + totalPage);
                String strS = settingTv.getText().toString();
                if(totalPage>1 && totalPage == (page +1) && TO_LAST_PAGE.equals(strS)){
                    settingTv.setText(TO_FIRST_PAGE);
                }
            }
        });

        int navigationBarH = Utils.getNavigationBarHeight(mContext);
        if(navigationBarH > 0){
            ((RelativeLayout)scrollView.getParent()).setPadding(0,0,0,navigationBarH);
        }
        initStatusView();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        fileName = getIntent().getStringExtra("fileName");
        dirName = getIntent().getStringExtra("dirName");
        TextView title = (TextView) findViewById(R.id.tv_activity_title);
        title.setText(fileName);
        file = new File(fileUtils.getLogCacheDir()
                + File.separator + dirName + File.separator,fileName);
        fileLength = (int)file.length();
        totalPage = fileLength%PAGE_SIZE == 0 ? fileLength/PAGE_SIZE : fileLength/PAGE_SIZE +1;
        if(totalPage > 1){
            settingTv.setVisibility(View.VISIBLE);
            settingTv.setOnClickListener(new RecordOnClick() {
                @Override
                public void recordOnClick(View v, String strMsg) {
                    String strS = settingTv.getText().toString();
                    if(TO_FIRST_PAGE.equals(strS) && page != 0){
                        page = 0;
                        settingTv.setText(TO_LAST_PAGE);
                        String str = fileUtils.openBigTxtFile(file,page,PAGE_SIZE);
                        if(!TextUtils.isEmpty(str)){
                            logTv.setText(str);
                            pageHintTv.setText((page + 1) + "/" + totalPage);
                        }
                        scrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                    }
                    if(TO_LAST_PAGE.equals(strS) && page != totalPage){
                        page = totalPage -1;
                        settingTv.setText(TO_FIRST_PAGE);
                        String str = fileUtils.openBigTxtFile(file,page,PAGE_SIZE);
                        if(!TextUtils.isEmpty(str)){
                            logTv.setText(str);
                            pageHintTv.setText((page + 1) + "/" + totalPage);
                        }
                        scrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }
                    super.recordOnClick(v, "Page Event " + settingTv.getText().toString());
                }
            });
        }else{
            settingTv.setVisibility(View.GONE);
        }
        String str = null;
        if(fileLength <= PAGE_SIZE){
            scrollView.setMode(PullToRefreshBase.Mode.DISABLED);
            try{
                str = fileUtils.readTextInputStream(new FileInputStream(file));
            }catch (Exception e){
                e.printStackTrace();
            }
            pageHintTv.setText("1/1");
        }else{
            scrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
            str = fileUtils.openBigTxtFile(file,page,PAGE_SIZE);
            pageHintTv.setText("1/" + totalPage);
        }
        if(!TextUtils.isEmpty(str)){
            logTv.setText(str);
        }
    }
}
