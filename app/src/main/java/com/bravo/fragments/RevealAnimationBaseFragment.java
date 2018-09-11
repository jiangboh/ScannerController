package com.bravo.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bravo.FemtoController.ProxyApplication;
import com.bravo.custom_view.CustomToast;
import com.bravo.utils.Logs;

import java.io.Serializable;
import java.net.Socket;


public abstract class RevealAnimationBaseFragment extends Fragment implements Serializable{

	protected View contentView;
	private int layoutId;
	protected String TAG = getClass().getSimpleName();
	protected Context context;
	private Bitmap bitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		if(contentView == null){
			contentView = inflater.inflate(layoutId, container,false);
			initView();
			initData(savedInstanceState);
		}
		ViewGroup parent = (ViewGroup) contentView.getParent();
		if (parent != null) {
			parent.removeView(contentView);
		}
		return contentView;
	}
	public void setContentView(int layoutId){
		this.layoutId = layoutId;
	}
	
	public abstract void initView();
	
	public abstract void initData(Bundle savedInstanceState);

	public void takeScreenShot() {
		contentView.post(new Runnable() {
			@Override
			public void run() {
				Bitmap bitmap1 = Bitmap.createBitmap(contentView.getWidth(),
						contentView.getHeight(), Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmap1);
				contentView.draw(canvas);
				bitmap = bitmap1;
			}
		});
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	protected boolean CheckObserverMode(String strControlIP) {
		String strSCIP ="N/A";
		Socket socket = ((ProxyApplication)context.getApplicationContext()).getCurSocket();
		if (socket != null){
			strSCIP = socket.getLocalAddress().getHostAddress();
		}

		Logs.d(TAG, "lmj sc ip=" + strSCIP + ",Scontroller ip=" + strControlIP);
		if (!strSCIP.equals(strControlIP)) {
			CustomToast.showToast(context, "Observer mode(Controller IP=" + strControlIP + ",SC IP=" + strSCIP+ ")");
			return false;
		} else {
			return true;
		}
	}
}
