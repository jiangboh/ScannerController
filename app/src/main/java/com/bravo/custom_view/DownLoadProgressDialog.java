package com.bravo.custom_view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bravo.R;

public class DownLoadProgressDialog extends Dialog implements View.OnClickListener{
	private Context context;
	private TextView title,cancel,progress;
	private BtnClickListener clickListener;
	private ProgressBar progressBar;
	public DownLoadProgressDialog(Context context) {
		super(context);
		this.context = context;
	}

	public DownLoadProgressDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	public void setTitle(String str){
		title.setText(str);
	}

	public void setProgress(int progress){
		progressBar.setProgress(progress);
		this.progress.setText(progress+"%");
	}

	public void setClickListener(BtnClickListener clickListener) {
		this.clickListener = clickListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_download_progress);
		title = (TextView) findViewById(R.id.download_progress_dialog_title);
		progress = (TextView) findViewById(R.id.download_progress_dialog_progress);
		progressBar = (ProgressBar) findViewById(R.id.download_progress_dialog_progressbar);
		cancel = (TextView) findViewById(R.id.download_progress_dialog_cancel);
		cancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.download_progress_dialog_cancel:
				if(clickListener != null){
					clickListener.onCancelClick(v);
				}
				break;
		}
		DownLoadProgressDialog.this.dismiss();
	}

	public interface BtnClickListener{
		void onCancelClick(View v);
	}
}
