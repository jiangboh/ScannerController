package com.bravo.FemtoController;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.bravo.database.DaoSession;
import com.bravo.database.FemtoList;
import com.bravo.database.FemtoListDao;
import com.bravo.database.GreenDaoManager;
import com.bravo.no_http.NoHttp;
import com.bravo.no_http.download.DownloadQueue;
import com.bravo.no_http.rest.RequestQueue;
import com.bravo.utils.Logs;
import com.bravo.utils.LogsManager;
import com.bravo.utils.SharePreferenceUtils;
import com.bravo.utils.Utils;

import java.net.Socket;

public class ProxyApplication extends Application {
	public static final String TAG = "ProxyApplication";

	private Socket curSocket;
	private String curIp;
	private int iTcpPort;
	private int iUdpPort;

	private static Context mContext;
	private RequestQueue mRequestQueue;
    protected DownloadQueue mDownloadQueue;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		//初始化log工具
		LogsManager.getInstance().init(this);
		//greenDao全局配置,只希望有一个数据库操作对象
		GreenDaoManager.getInstance();
		// 程序崩溃时触发线程  以下用来捕获程序崩溃异常
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				StringBuilder sb = new StringBuilder();
				sb.append(Utils.getVersionName(ProxyApplication.this));
				sb.append("    ");
				sb.append(Utils.getVersionCode(ProxyApplication.this));
				sb.append(Utils.getMobileInfo());
				sb.append(ex.getMessage());
				Logs.e(TAG,sb.toString(),"crashMsg",true);
				mRequestQueue.cancelAll();
				mRequestQueue.stop();
				restartApp();
			}
		});
		NoHttp.initialize(this);
		mRequestQueue = NoHttp.newRequestQueue(Runtime.getRuntime().availableProcessors() + 1);
        mDownloadQueue = NoHttp.newDownloadQueue(Runtime.getRuntime().availableProcessors() + 1);
	}

	/**
	 * 重启APP
	 */
	public void restartApp(){
		Intent intent = new Intent(this,LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		//结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public static DaoSession getDaoSession(){
		return GreenDaoManager.getInstance().getSession();
	}

	public Socket getCurSocket() {
		return curSocket;
	}

	public String getCurSocketAddress() {
		return curIp;
	}
	public int getiTcpPort() {
		return iTcpPort;
	}

	public int getiUdpPort() {
		return iUdpPort;
	}

	public void setiUdpPort(int iUdpPort) {
		this.iUdpPort = iUdpPort;
	}

	public String getCurMacAddress() {
		return SharePreferenceUtils.getInstance(this).getString(curIp, "0.0.0.0");
	}

	public String getDevName(String strIP) {
		FemtoList femtoList = ProxyApplication.getDaoSession().getFemtoListDao().queryBuilder().where(FemtoListDao.Properties.Mac.eq(SharePreferenceUtils.getInstance(this).getString(strIP, "0.0.0.0")),
				FemtoListDao.Properties.UdpPort.eq(iUdpPort)).build().unique();
		if (femtoList != null) { //数据存在更新
			return femtoList.getSSID();
		} else {
			return SharePreferenceUtils.getInstance(this).getString(strIP, "0.0.0.0");
		}
	}

	public void setCurSocket(Socket curSocket) {
		if (curSocket != null) {
			curIp = curSocket.getInetAddress().getHostAddress();
			iTcpPort = curSocket.getPort();
			this.curSocket = curSocket;
		} else {
			curIp = null;
			this.curSocket = null;
		}
	}

	public RequestQueue getmRequestQueue(){
		return mRequestQueue;
	}

    public DownloadQueue getmDownloadQueue() {
        return mDownloadQueue;
    }

    @Override
    public void onTerminate() {
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mDownloadQueue.cancelAll();
        mDownloadQueue.stop();
        super.onTerminate();
    }

    public static Context getContext() {
		return mContext;
	}

}
