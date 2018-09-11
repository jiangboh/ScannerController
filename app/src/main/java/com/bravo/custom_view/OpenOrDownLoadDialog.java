package com.bravo.custom_view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bravo.R;

public class OpenOrDownLoadDialog extends Dialog implements View.OnClickListener{
	private Context context;
	private TextView title,open,downLoad;
	private BtnClickListener clickListener;
	public OpenOrDownLoadDialog(Context context) {
		super(context);
		this.context = context;
	}

	public OpenOrDownLoadDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	public void setTitle(String str){
		title.setText(str);
	}

	public void setOpenContent(String str){
		open.setText(str);
	}
	public void setDownLoadContent(String str){
		downLoad.setText(str);
	}

	public void setClickListener(BtnClickListener clickListener) {
		this.clickListener = clickListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_open_or_download);
		title = (TextView) findViewById(R.id.open_or_download_dialog_title);
		open = (TextView) findViewById(R.id.open_or_download_dialog_open);
		open.setOnClickListener(this);
		downLoad = (TextView) findViewById(R.id.open_or_download_dialog_download);
		downLoad.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.open_or_download_dialog_open:
				if(clickListener != null){
					clickListener.onOpenClick(v);
				}
				break;
			case R.id.open_or_download_dialog_download:
				if(clickListener != null){
					clickListener.onDownLoadClick(v);
				}
				break;
		}
		OpenOrDownLoadDialog.this.dismiss();
	}

	public interface BtnClickListener{
		void onOpenClick(View v);
		void onDownLoadClick(View v);
	}
}
