package com.bravo.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mingjt on 2016/7/8.
 */
public class SharePreferenceUtils {

    private static final String SHARE_PREFERENCES_NAME = "pixcel";

    private SharedPreferences sp;
    private SharedPreferences.Editor edit;
    private static SharePreferenceUtils instance = null;

    private SharePreferenceUtils(Context context){
        sp = context.getSharedPreferences(SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        edit = sp.edit();
    };

    public static SharePreferenceUtils getInstance(Context context){
        if(instance == null){
            synchronized (SharePreferenceUtils.class){
                if(instance == null){
                    instance = new SharePreferenceUtils(context);
                }
            }
        }
        return instance;
    }


    public void setString(String key, String value) {
        edit.putString(key, value);
        edit.commit();
    }


    public String getString(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    public void setBoolean(String key, boolean value) {
        edit.putBoolean(key, value);
        edit.commit();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public void setInt(String key, int value) {
        edit.putInt(key, value);
        edit.commit();
    }

    public int getInt(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    public float getFloat(String key, float defaultValue) {
        return sp.getFloat(key, defaultValue);
    }

    public void setFloat(String key, float value) {
        edit.putFloat(key, value);
        edit.commit();
    }

    public long getLong(String key, long defaultValue) {
        return sp.getLong(key, defaultValue);
    }

    public void setLong(String key, Long value) {
        edit.putLong(key, value);
        edit.commit();
    }

    public void delete(String key) {
        edit.remove(key);
        edit.commit();
    }

    public void clear(){
        edit.clear();
        edit.commit();
    }
}
