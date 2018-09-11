package com.bravo.system;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.R;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.wifi.WifiAP;
import com.bravo.wifi.WifiAdmin;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class Upgrade_Fragment extends RevealAnimationBaseFragment implements View.OnClickListener{
    private final static String TAG = "Upgrade_Fragment";
    private EditText et_url;
    private Button btn_visit;
    private WebView web;

    public final static int FILECHOOSER_RESULTCODE = 1;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;
    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_remote);
    }

    @Override
    public void initView() {
        et_url = (EditText)contentView.findViewById(R.id.et_url);
        //init address link
        WifiAdmin mWifiAdmin = new WifiAdmin(context);
        WifiAP mWifiAP = new WifiAP(context);
        if (mWifiAdmin.isWifiConnected()) {
            et_url.setText("http://" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + "/firmwareUpdate.shtml");
        } else if (mWifiAP.isApEnabled()) {
            et_url.setText("http://" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ":10080/firmwareUpdate.shtml");
        }

        btn_visit = (Button)contentView.findViewById(R.id.btn_visit);
        MyWebChromeClient myWCC = new MyWebChromeClient();
        myWCC.setWebCall(new MyWebChromeClient.WebCall() {
            @Override
            public void fileChose(ValueCallback<Uri> uploadMsg) {
                openFileChooserImpl(uploadMsg);
            }

            @Override
            public void fileChose5(ValueCallback<Uri[]> uploadMsg) {
                openFileChooserImplForAndroid5(uploadMsg);
            }


            private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"),
                        FILECHOOSER_RESULTCODE);
            }

            private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
                mUploadMessageForAndroid5 = uploadMsg;
                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("*/*");

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "File Chooser");

                startActivityForResult(chooserIntent,
                        FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
            }
        });
        web = (WebView)contentView.findViewById(R.id.web);
        web.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                et_url.setText(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                handler.proceed("admin","admin");
                super.onReceivedHttpAuthRequest(view, handler, host, realm);
            }
        });
        web.setWebChromeClient(myWCC);
        web.setVerticalScrollBarEnabled(false);
        web.setHorizontalScrollBarEnabled(false);
        WebSettings ws = web.getSettings();
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //是否允许脚本支持
        ws.setJavaScriptEnabled(true);
        // 设置可以访问文件
        ws.setAllowFileAccess(true);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        //是否允许缩放
        ws.setSupportZoom(true);
        ws.setBuiltInZoomControls(true);
        //隐藏webview缩放按钮
        ws.setDisplayZoomControls(false);


        btn_visit.setOnClickListener(this);

       // web.setOnTouchListener(touchListener);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        conn(et_url.getText().toString());
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
    //-------------------------------------------
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_visit:
                conn(et_url.getText().toString());
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        web.requestFocus();
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null
                    : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (intent == null || resultCode != RESULT_OK) ? null
                    : intent.getData();
            Log.d("123456","Uri == " + result);
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[] { result });
            }else{
                mUploadMessageForAndroid5.onReceiveValue(null);
            }
            mUploadMessageForAndroid5 = null;
        }
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
