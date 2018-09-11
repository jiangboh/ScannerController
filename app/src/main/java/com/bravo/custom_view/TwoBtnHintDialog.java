package com.bravo.custom_view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bravo.R;

public class TwoBtnHintDialog extends Dialog implements  View.OnClickListener{

	private Context context;
	private TextView title,content;
	private Button leftBtn,rightBtn;
	private OnBtnClickListener onBtnClickListener;
	public TwoBtnHintDialog(Context context) {
		super(context);
		this.context = context;
	}

	public TwoBtnHintDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	public void setTitle(String str){
		title.setText(str);
	}

	public void setContent(String str){
		content.setText(str);
	}

	public void setLeftBtnContent(String str){
		leftBtn.setText(str);
	}

	public void setRightBtnContent(String str){
		rightBtn.setText(str);
	}

	public void setOnBtnClickListener(OnBtnClickListener onBtnClickListener) {
		this.onBtnClickListener = onBtnClickListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_two_btn);
		title = (TextView) findViewById(R.id.two_btn_dialog_title);
		content = (TextView) findViewById(R.id.two_btn_dialog_content);
		leftBtn = (Button)findViewById(R.id.two_btn_dialog_left_btn);
		leftBtn.setOnClickListener(this);

		rightBtn = (Button) findViewById(R.id.two_btn_dialog_right_btn);
		rightBtn.setOnClickListener(this);
	}
	public interface OnBtnClickListener{
		void onClick(View v);
	}

	@Override
	public void onClick(View v) {
		if(onBtnClickListener != null){
			onBtnClickListener.onClick(v);
		}
		this.dismiss();
	}
}
