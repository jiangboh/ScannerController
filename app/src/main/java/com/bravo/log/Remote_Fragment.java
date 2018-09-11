package com.bravo.log;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Base64;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.HttpAuthHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.FemtoController.RevealAnimationActivity;
import com.bravo.R;
import com.bravo.custom_view.DownLoadProgressDialog;
import com.bravo.custom_view.OpenOrDownLoadDialog;
import com.bravo.fragments.RevealAnimationBaseFragment;
import com.bravo.utils.FileUtils;
import com.bravo.utils.Logs;
import com.bravo.utils.SimpleDateUtils;
import com.bravo.wifi.WifiAP;
import com.bravo.wifi.WifiAdmin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * A simple {@link Fragment} subclass.
 */
public class Remote_Fragment extends RevealAnimationBaseFragment implements View.OnClickListener{
    private final static String TAG = "Remote_Fragment";
    private EditText et_url;
    private Button btn_visit;
    private WebView web;
    private DownLoadProgressDialog progressDialog;

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
            et_url.setText("http://" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + "/log");
        } else if (mWifiAP.isApEnabled()) {
            et_url.setText("http://" + ((ProxyApplication)context.getApplicationContext()).getCurSocketAddress() + ":10080/log");
        }

        btn_visit = (Button)contentView.findViewById(R.id.btn_visit);

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
                Logs.d("123456","shouldOverrideUrlLoading url == " + url);
                final String tempUrl = url;
                if(url.endsWith(".log")){
                    OpenOrDownLoadDialog dialog = new OpenOrDownLoadDialog(context,R.style.dialog_style);
                    dialog.setClickListener(new OpenOrDownLoadDialog.BtnClickListener() {
                        @Override
                        public void onOpenClick(View v) {
                            web.loadUrl(tempUrl);
                            Logs.w(TAG, "Remote Log Open Event " + tempUrl, "Record_Event", true);
                        }

                        @Override
                        public void onDownLoadClick(View v) {
                            //TODO
                            downLoad(tempUrl);
                            Logs.w(TAG, "Remote Log DownLoad Event " + tempUrl, "Record_Event", true);
                        }
                    });
                    dialog.show();
                }else{
                    view.loadUrl(url);
                }
                et_url.setText(url);
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

        web.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Logs.d("123456","触发了下载监听！！！！");
                downLoad(url);
                /*Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);*/
            }
        });
        btn_visit.setOnClickListener(this);

       // web.setOnTouchListener(touchListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((RevealAnimationActivity)context).getSettingBtn().setVisibility(View.GONE);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_visit:
                conn(et_url.getText().toString());
                break;
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

    private void downLoad(String url){
        final DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask();
        progressDialog = new DownLoadProgressDialog(context,R.style.dialog_style);
        progressDialog.setClickListener(new DownLoadProgressDialog.BtnClickListener() {
            @Override
            public void onCancelClick(View v) {
            if(downloadAsyncTask.getStatus() != AsyncTask.Status.FINISHED && !downloadAsyncTask.isCancelled()){
                downloadAsyncTask.cancel(true);
            }
            }
        });
        progressDialog.setCancelable(false);
        FileUtils fileUtils = new FileUtils(context);
        String logDirName = SimpleDateUtils.formatTime("yyyy-MM-dd") + " Logs";
        File file = new File(fileUtils.getLogCacheDir() + File.separator + logDirName);
        file.mkdir();
        final String fileName = url.substring(url.lastIndexOf("/")+1);
        downloadAsyncTask.execute(url,file.getAbsolutePath()+ File.separator + fileName);
    }

    private class DownloadAsyncTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            progressDialog.show();
            progressDialog.setProgress(0);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            try {
                URL url = new URL(params[0]);
                URLConnection conn = url.openConnection();
                String credentials = "admin:admin";
                final String basicAuth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                conn.setRequestProperty("Authorization", basicAuth);
                // 建立链接
                conn.connect();
                // 获取输入流
                inputStream = conn.getInputStream();
                outputStream = new FileOutputStream(new File(params[1]));
                // 获取文件流大小，用于更新进度
                long file_length = conn.getContentLength();
                int len;
                int total_length = 0;
                byte[] data = new byte[1024];
                while ((len = inputStream.read(data)) != -1) {
                    total_length += len;
                    int value = (int) ((total_length / (float) file_length) * 100);
                    // 调用update函数，更新进度
                    publishProgress(value);
                    outputStream.write(data, 0, len);
                }
                if(total_length >= file_length){
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean finish) {
            if(finish){
                progressDialog.dismiss();
            }
        }
    }
}
