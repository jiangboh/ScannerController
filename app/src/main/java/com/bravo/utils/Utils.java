package com.bravo.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bravo.BuildConfig;
import com.bravo.parse_generate_xml.ErrorNotif;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static final String BUILD_TYPE_STR = BuildConfig.BUILD_TYPE;

    private static final String TAG = "utils";

    public static LinkedHashMap<String, ArrayList<ErrorNotif>> errors = new LinkedHashMap<>();
    public static ArrayList<ErrorNotif> ignoreErrors = new ArrayList<>();
    public static ArrayList<String> ips = new ArrayList<>();

    public static void mySleep(int timer) {
        try {
            Thread.sleep(timer);//每隔1s执行一次
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启软键盘
     * @param context
     * @param view
     */
    public static void showSoftInput(Context context, View view) {
        InputMethodManager inputManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 收起软键盘
     * @param context
     * @param view
     */
    public static void hidenSoftInput(Context context, View view) {
        View view1 = ((Activity) context).getWindow().peekDecorView();
        if (view1 != null) {
            InputMethodManager inputManager = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param context
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    /**
     * 判断是否有可用的网络
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            if (nInfo != null && nInfo.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        }
        return false;
    }

    /**
     * 访问百度测试网络（需在子线程中进行会阻塞主线程）
     *
     * @return
     */
    public static boolean ping() {
        Runtime runtime = Runtime.getRuntime(); // 获取当前程序的运行进对象
        Process process = null; // 声明处理类对象
        String line = null; // 返回行信息
        InputStream is = null; // 输入流
        InputStreamReader isr = null; // 字节流
        BufferedReader br = null;
        String ip = "www.baidu.com";
        boolean res = false;// 结果
        try {
            process = runtime.exec("ping " + ip); // PING
            is = process.getInputStream(); // 实例化输入流
            isr = new InputStreamReader(is);// 把输入流转换成字节流
            br = new BufferedReader(isr);// 从字节中读取文本
            while ((line = br.readLine()) != null) {
                if (line.contains("TTL")) {
                    res = true;
                    break;
                }
            }
            is.close();
            isr.close();
            br.close();
        } catch (IOException e) {
            Logs.d(TAG,"ping baidu 异常："+e.getMessage());
        }
        return res;

    }

    /**
     * 按照给定长度保留小数点后的位数
     * @param count
     * @param length 保留小数点后length位
     * @return
     */
    public static String formatDecimalF(float count,int length) {
        String result = String.format("%."+length+"f", count);
        if (!"0.0".equals(result) && result.endsWith(".0")) {
            result = result.replace(".0", "");
        }
        return result;
    }

    /**
     * 按照给定长度保留小数点后的位数
     * @param count
     * @param length 保留小数点后length位
     * @return
     */
    public static String formatDecimaD(double count,int length) {
        String result = String.format("%."+length+"f", count);
        if (!"0.0".equals(result) && result.endsWith(".0")) {
            result = result.replace(".0", "");
        }
        return result;
    }

    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * 将字符串进行MD5加密
     * @param string
     * @return
     * @throws Exception
     */
    public static String encode(String string) throws Exception {
        byte[] hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    /**
     * 判断是否为手机号码
     *
     * @param number 手机号码
     * @return
     */
    public static boolean isCellPhone(String number) {
        Matcher match = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$").matcher(number);
        return match.matches();
    }

    /**
     * 判断是否为固定电话号码
     * @param number 固定电话号码
     * @return
     */
    public static boolean isFixedPhone(String number) {
        Matcher match = Pattern.compile("^(010|02\\d|0[3-9]\\d{2})?\\d{6,8}$").matcher(number);
        return match.matches();
    }

    /**
     * 判断是否为身份证号码或临时身份证号码
     * @param number 证件号码
     * @return
     */
    public static boolean isIDCardNum(String number) {
        Matcher match = Pattern.compile("(^\\d{15}$)|(^\\d{17}([0-9]|X|x)$)").matcher(number);
        return match.matches();
    }

    /**
     * sd卡挂载且可用
     *
     * @return
     */
    public static boolean isSdCardMounted() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取屏幕宽度
     * @param context
     * @return
     */
    public static int getScreenW(Context context){
       return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     * @param context
     * @return
     */
    public static int getScreenH(Context context){
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕密度
     * @param context
     * @return
     */
    public static float getScreenD(Context context){
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 获取版本名称
     * @param context
     * @return
     */
    public static String getVersionName(Context context){
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info =pm.getPackageInfo(context.getPackageName(), 0);
            return  info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "版本号未知";
        }
    }

    /**
     * 获取版本号
     * @param context
     * @return
     */
    public static int getVersionCode(Context context){
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info =pm.getPackageInfo(context.getPackageName(), 0);
            return  info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 获取手机的硬件信息
     * @return
     */
    public static String getMobileInfo() {
        StringBuffer sb = new StringBuffer();
        //通过反射获取系统的硬件信息
        try {

            Field[] fields = Build.class.getDeclaredFields();
            for(Field field: fields){
                //暴力反射 ,获取私有的信息
                field.setAccessible(true);
                String name = field.getName();
                String value = field.get(null).toString();
                sb.append(name+"="+value);
                sb.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void saveLog(String msg,String name,Context context) throws IOException {
        String time = SimpleDateUtils.formatTime("yyyy-MM-dd HH:mm:ss");
        if(name == null|| TextUtils.isEmpty(name)){
            name = time;
        }else{
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append(" ");
            sb.append(time);
            name = sb.toString();
        }
        FileUtils fu = new FileUtils(context);
        fu.writeTextFile(new File(fu.getLogCacheDir(),name),msg);
    }

    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
        }
        return hasNavigationBar;
    }

    //获取虚拟按键的高度
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (checkDeviceHasNavigationBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     * 判断虚拟按键栏是否重写
     *
     * @return
     */
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }


    /**
     * 检测Activity是否在当前Task的栈顶
     */
    public static boolean isTopActivy(Context context, String cmdName) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        String cmpNameTemp = null;

        if (null != runningTaskInfos) {
            cmpNameTemp = runningTaskInfos.get(0).topActivity.getClassName()
                    .toString();
           /* Logs.w("123456", "栈顶Activity名字为：" + cmpNameTemp
                    + "  要判断的Activity名字为：" + cmdName);*/
        }
        if (null == cmpNameTemp)
            return false;
        return cmpNameTemp.endsWith(cmdName);
    }

    public static void addError(ErrorNotif errorNotif){
        ArrayList<ErrorNotif> temData = errors.get(errorNotif.getIpAddress());
        if(temData == null){
            temData = new ArrayList<>();
            errors.put(errorNotif.getIpAddress(),temData);
            if(!ips.contains(errorNotif.getIpAddress())){
                ips.add(errorNotif.getIpAddress());
            }
            temData.add(errorNotif);
        }else{
            boolean isExit = false;
            for(ErrorNotif en : temData){
                if(en.getIpAddress().equals(errorNotif.getIpAddress())
                        && en.getErrorCode().equals(errorNotif.getErrorCode())
                        && en.getDetails().equals(errorNotif.getDetails())){
                    isExit = true;
                    en.setTime(errorNotif.getTime());
                }
            }
            if(!isExit){
                temData.add(errorNotif);
            }
        }
    }

    public static void deleteErrorsByIp(String ipAddress){
        if(TextUtils.isEmpty(ipAddress)){
            return;
        }
        if(errors.containsKey(ipAddress)){
            errors.remove(ipAddress);
        }
        if(ips.contains(ipAddress)){
            ips.remove(ipAddress);
        }
        ArrayList<Integer> indexs = new ArrayList<>();
        for(ErrorNotif en : ignoreErrors){
            if(en.getIpAddress().equals(ipAddress)){
                int index = ignoreErrors.indexOf(en);
                if(index != -1){
                    indexs.add(index);
                }
            }
        }
        if(indexs.size() > 0){
            for(int index : indexs){
                ignoreErrors.remove(index);
            }
        }
    }

    public static void deleteError(ErrorNotif errorNotif){
        ArrayList<ErrorNotif> temErrors = errors.get(errorNotif.getIpAddress());
        if(temErrors != null && temErrors.contains(errorNotif)){
            temErrors.remove(errorNotif);
        }
        Logs.d("errorMsg","删除后Errors size == " + temErrors.size(),true);
        if(temErrors.size() == 0){
            deleteErrorsByIp(errorNotif.getIpAddress());
        }
        ignoreErrors.remove(errorNotif);
    }

    public static void addIgnoreError(ErrorNotif errorNotif){
        if(!isIgnoreError(errorNotif)){
            ignoreErrors.add(errorNotif);
        }
    }

    public static void deleteIgnoreError(ErrorNotif errorNotif){
        int index = -1;
        for(ErrorNotif en : ignoreErrors){
            if(en.getIpAddress().equals(errorNotif.getIpAddress())
                    && en.getErrorCode().equals(errorNotif.getErrorCode())
                    && en.getDetails().equals(errorNotif.getDetails())){
                index = ignoreErrors.indexOf(en);
            }
        }
        if(index != -1){
            ignoreErrors.remove(index);
        }
    }

    public static boolean isIgnoreError(ErrorNotif errorNotif){
        if(ignoreErrors.size() > 0){
            for(ErrorNotif en : ignoreErrors){
//                Logs.d("123456","忽略的ErrorMsg == " + en.toString());
                if(en.getIpAddress().equals(errorNotif.getIpAddress())
                        && en.getErrorCode().equals(errorNotif.getErrorCode())
                        && en.getDetails().equals(errorNotif.getDetails())){
                    en.setTime(errorNotif.getTime());
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isAllIgnores(ArrayList<ErrorNotif> errors){
        for(ErrorNotif temEn : errors){
            if(!isIgnoreError(temEn)){
                return false;
            }
        }
        return true;
    }

    public static boolean hasAlert(){
        if(!errors.isEmpty()){
            if(ignoreErrors.isEmpty()){
                return true;
            }else{
                ArrayList<ErrorNotif> temData = new ArrayList<>();
                for(String ipAddress : errors.keySet()){
                    for(ErrorNotif en:errors.get(ipAddress)){
                        temData.add(en);
                    }
                }
                for(ErrorNotif temEn : temData){
                    if(!isIgnoreError(temEn)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static int getWifiIpByInt(Context context) {
        WifiManager mWifiManager;
        WifiInfo mWifiInfo;

        //获取wifi服务
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
        mWifiInfo = mWifiManager.getConnectionInfo();
        int ipAddress = mWifiInfo.getIpAddress();

        return ipAddress;
    }
    /*
    获取wifi的ip地址
     */
    public static String getWifiIp(Context context)
    {
        int ipAddress = getWifiIpByInt(context);

        return (ipAddress & 0xFF ) + "." +
                ((ipAddress >> 8 ) & 0xFF) + "." +
                ((ipAddress >> 16 ) & 0xFF) + "." +
                ( ipAddress >> 24 & 0xFF) ;
    }
    /*
    获取wifi的广播ip地址
     */
    public static String getWifiBroadcastIp(Context context)
    {
        int ipAddress = getWifiIpByInt(context)  | 0xFF000000;

        return (ipAddress & 0xFF ) + "." +
                ((ipAddress >> 8 ) & 0xFF) + "." +
                ((ipAddress >> 16 ) & 0xFF) + "." +
                ( ipAddress >> 24 & 0xFF) ;
    }

    public static Boolean isDebugVersion()
    {
        return BUILD_TYPE_STR.equals("debug");

    }
}
