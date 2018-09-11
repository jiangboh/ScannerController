package com.bravo.custom_view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.bravo.R;

public class CustomProgressDialog extends Dialog{
	private Context context;
	private TextView title,content;
	private String strTitle, strContent;
	public CustomProgressDialog(Context context) {
		super(context);
		this.context = context;
	}

	public CustomProgressDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	public void setTitle(String str){
		title.setText(str);
	}

	public void setContent(String str){
		content.setText(str);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_progress);
		title = (TextView) findViewById(R.id.progress_dialog_title);
		content = (TextView) findViewById(R.id.progress_dialog_content);
	}

}
