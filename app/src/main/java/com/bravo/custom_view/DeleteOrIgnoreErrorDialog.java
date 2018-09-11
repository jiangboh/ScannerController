package com.bravo.custom_view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bravo.R;
import com.bravo.parse_generate_xml.ErrorNotif;

public class DeleteOrIgnoreErrorDialog extends Dialog implements View.OnClickListener{

	private Context context;
	private ErrorNotif en;
	private OnClickListener onClickListener;
	public static final int GROUP = 0;
	public static final int ITEM = 1;
	private int type = 0;
	private Button ignore;

	public DeleteOrIgnoreErrorDialog(Context context) {
		super(context);
		this.context = context;
	}

	public DeleteOrIgnoreErrorDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	public void setErrorNotif(ErrorNotif en){
		this.en = en;
	}

	public void setIgnoreText(String str){
		ignore.setText(str);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_delete_or_ignore_error);
		ignore = (Button) findViewById(R.id.delete_or_ignore_error_ignore);
		ignore.setOnClickListener(this);
		findViewById(R.id.delete_or_ignore_error_delete).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(onClickListener != null){
			v.setTag(en);
			onClickListener.onClick(v);
		}
		this.dismiss();
	}

	public interface OnClickListener{
		void onClick(View view);
	}
}
