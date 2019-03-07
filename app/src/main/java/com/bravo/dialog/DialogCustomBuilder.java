package com.bravo.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import com.bravo.R;

import java.lang.reflect.Field;

/**
 * Created by admin on 2018-11-6.
 */

public class DialogCustomBuilder {
    private Context context;
    private String title;
    private String message;
    private OkBtnClickListener OkListener;
    private CancelBtnClickListener CancelListener;

    private AlertDialog.Builder builder;

    public DialogCustomBuilder(Context context,String title,String msg) {
        this.context = context;
        this.title = title;
        this.message = msg;
        builder = new AlertDialog.Builder(context);
    }

    public interface CancelBtnClickListener{
        void onBtnClick(DialogInterface arg0, int arg1);
    }

    public interface OkBtnClickListener{
        void onBtnClick(DialogInterface arg0, int arg1);
    }

    public void setOkListener(OkBtnClickListener listener) {
        this.OkListener = listener;
    }

    public void setCancelListener(CancelBtnClickListener listener) {
        this.CancelListener = listener;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setIcon(R.drawable.icon_help);//设置图标
        builder.setTitle(title);//设置对话框的标题
        builder.setMessage(message);//设置对话框的内容
        builder.setCancelable(false);//设置点击其它地，该对话框不关闭
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  //这个是设置确定按钮
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if(OkListener != null){
                    OkListener.onBtnClick(arg0,arg1);
                }
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  //取消按钮
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if(CancelListener != null){
                    CancelListener.onBtnClick(arg0,arg1);
                }
            }
        });

        AlertDialog b = builder.create();
        b.show();  //必须show一下才能看到对话框，跟Toast一样的道理
        try
        {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object alertController = mAlert.get(b);

            Field mTitleView = alertController.getClass().getDeclaredField("mTitleView");
            mTitleView.setAccessible(true);
            TextView title = (TextView) mTitleView.get(alertController);
            title.setTextColor(Color.RED);
        } catch (NoSuchFieldException e){
            e.printStackTrace();
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        } catch (IllegalAccessException e){
            e.printStackTrace();
        }
        b.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        b.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
    }
}
