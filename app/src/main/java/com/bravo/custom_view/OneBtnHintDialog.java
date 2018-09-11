package com.bravo.custom_view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bravo.R;

public class OneBtnHintDialog extends Dialog{

	private Context context;
	private TextView title,content;
	private Button btn;
	private BtnClickListener listener;
	public OneBtnHintDialog(Context context) {
		super(context);
		this.context = context;
	}

	public OneBtnHintDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	public void setListener(BtnClickListener listener) {
		this.listener = listener;
	}

	public void setTitle(String str){
		title.setText(str);
	}

	public void setContent(String str){
		content.setText(str);
	}

	public void setBtnContent(String str){
		btn.setText(str);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_one_btn);
		title = (TextView) findViewById(R.id.one_btn_dialog_title);
		content = (TextView) findViewById(R.id.one_btn_dialog_content);
		btn = (Button) findViewById(R.id.one_btn_dialog_btn);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(listener != null){
					listener.onBtnClick(v);
				}
				OneBtnHintDialog.this.dismiss();
			}
		});
	}

	public interface BtnClickListener{
		void onBtnClick(View v);
	}

}
