package com.bravo.test;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bravo.FemtoController.BaseActivity;
import com.bravo.R;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET;
import static android.net.NetworkCapabilities.TRANSPORT_CELLULAR;

/**
 * Created by lenovo on 2017/12/25.
 */

public class NetTestActivity extends BaseActivity {
    private ConnectivityManager connectivityManager;
    @Override
    protected void initData(Bundle savedInstanceState) {

    }
    @Override
    protected void initView() {
        setContentView(R.layout.activity_nettest);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }
    @TargetApi(21)
    public void onClick(View v){
        Log.d("123456","11111111111！！！");
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        builder.addCapability(NET_CAPABILITY_INTERNET);
        //强制使用蜂窝数据网络-移动数据
        builder.addTransportType(TRANSPORT_CELLULAR);
        NetworkRequest build = builder.build();
        forceSendRequestByMobileData(build);
    }
    @TargetApi(23)
    private void forceSendRequestByMobileData(NetworkRequest build) {

        connectivityManager.requestNetwork(build, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                Log.d("123456","获得移动通信许可！！！" + network.toString());
                connectivityManager.setProcessDefaultNetwork(network);
                requestData(network);
            }

            @Override
            public void onLost(Network network) {
//                NetworkUtil.setNetwork(BindNetActivity.this, null);
                try {
                    connectivityManager.unregisterNetworkCallback(this);
                } catch (SecurityException e) {
                    Log.d("123456", "Failed to unregister network callback");
                }
                Log.d("123456","onLost()" + network.toString());
            }

        });
    }

    @TargetApi(21)
    private void requestData(final Network network){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("123456", "开启线程访问网络！！！！！！！！！！");
                try {
                    OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && network != null) {
                        builder.socketFactory(network.getSocketFactory());
                    }
                    OkHttpClient client = builder.build();//创建OkHttpClient对象

                    Request request = new Request.Builder()
                            .url("http://www.baidu.com")//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        Log.d("123456","response.code()=="+response.code());
                        Log.d("123456","response.message()=="+response.message());
                        Log.d("123456","res=="+response.body().string());
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。

                    }
                } catch (Exception e) {
                    Log.d("123456","Exception =="+ e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
