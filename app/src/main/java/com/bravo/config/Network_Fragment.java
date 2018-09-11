package com.bravo.config;

import android.os.Bundle;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.wifi.WifiAP;
import com.bravo.wifi.WifiAdmin;

public class Network_Fragment extends RevealAnimationBaseFragment {
    private final static String TAG = "Network_Fragment";
    private WebView web;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_remote);
    }

    @Override
    public void initView() {

        web = (WebView)contentView.findViewById(R.id.web);
        web.setVerticalScrollBarEnabled(false);
        web.setHorizontalScrollBarEnabled(false);
        WebSettings ws = web.getSettings();
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //是否允许脚本支持
        ws.setJavaScriptEnabled(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        ws.setSaveFormData(false);
        ws.setSavePassword(false);
        ws.setAppCacheEnabled(true);
        ws.setAppCacheMaxSize(10240);
//      ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        //是否允许缩放
        ws.setBuiltInZoomControls(true);
        ws.setDisplayZoomControls(false); //隐藏webview缩放按钮
        web.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            };
            @Override
            public void onReceivedHttpAuthRequest(WebView view,
                                                  HttpAuthHandler handler, String host, String realm) {
                handler.proceed("admin", "admin");
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (errorCode == 401) {
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.GONE);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        WifiAdmin mWifiAdmin = new WifiAdmin(context);
        WifiAP mWifiAP = new WifiAP(context);
        if (mWifiAdmin.isWifiConnected()) {
            conn("http://" + mWifiAdmin.getGateway() + "1");
        } else if (mWifiAP.isApEnabled()) {
            conn("http://" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress());
        }
    }

    /**
     * 访问url
     * @param urlStr
     */
    private void conn(String urlStr){
        String url = "";
        if(urlStr.contains("http://")){
            url = urlStr;
        }else{
            url = "http://"+urlStr;
        }
        web.loadUrl(url);
    }

    public boolean onBackPressed(){
        if(web.canGoBack()){
            web.goBack();
            return false;
        }else{
            return true;
        }
    }

}
