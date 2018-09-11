package com.bravo.custom_view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bravo.R;

/**
 * Created by lenovo on 2017/7/10.
 */

public class CustomToast {

    public static void showToast(Context context, String message) {
        showToast(context,message,Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, String message, int showTime) {
        //加载Toast布局
        View toastRoot = LayoutInflater.from(context).inflate(R.layout.custom_toast_layout, null);
        //初始化布局控件
        ((TextView) toastRoot.findViewById(R.id.custom_toast_tv)).setText(message);
        //Toast的初始化
        Toast toastStart = new Toast(context);
        //获取屏幕高度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        //Toast的Y坐标是屏幕高度的1/3，不会出现不适配的问题
        toastStart.setGravity(Gravity.TOP, 0, height *5/ 12);
        toastStart.setDuration(showTime);
        toastStart.setView(toastRoot);
        toastStart.show();
    }
}
