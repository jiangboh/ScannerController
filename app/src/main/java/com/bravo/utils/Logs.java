package com.bravo.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by lenovo on 2017/1/16.
 */

public class Logs {
    private static final int VERBOSE = 1;
    private static final int DEBUG = 2;
    private static final int INFO = 3;
    private static final int WARN = 4;
    private static final int ERROR = 5;
    private static final int NOTHING = 6;

    private static int LEVEL = INFO;//
    private static String LOGNAME = "Log";//

    private static String customTagPrefix = "";  // 自定义Tag的前缀，可以是作者名
    private static String generateTag(StackTraceElement caller) {
        String tag = "%s.%s(Line:%d)"; // 占位符
        String callerClazzName = caller.getClassName(); // 获取到类名
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber()); // 替换
        tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ":" + tag;

        return tag;
    }
    private static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }

    public static void setLEVEL(int LEVEL) {
        Logs.LEVEL = LEVEL;
    }

    public static void setLOGNAME(String LOGNAME) {
        Logs.LOGNAME = LOGNAME;
    }

    public static int getLEVEL() {
        if (Utils.isDebugVersion()) {
            return VERBOSE;
        }
        return LEVEL;
    }

    public static void d(String tag, String logMsg){
        if (getLEVEL() > DEBUG) return;
        Log.d(tag,logMsg);
    }

    public static void d(String tag,String logMsg,boolean needSave){
        if (getLEVEL() > DEBUG) return;
        Log.d(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(generateTag(getCallerStackTraceElement()) , logMsg,LOGNAME);
        }
    }

    /**
     * @param tag 过滤器
     * @param logMsg log信息
     * @param fileName 保存的文件名
     * @param needSave 是否需要存到sdCard
     */
    public static void d(String tag,String logMsg,String fileName,boolean needSave){
        if (getLEVEL() > DEBUG) return;
        Log.d(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(generateTag(getCallerStackTraceElement()), logMsg, fileName);
        }
    }

    /**
     * @param tag 过滤器
     * @param logMsg log信息
     * @param addition 存储模式（覆盖/续写）
     * @param needSave 是否需要存到sdCard
     */
    public static void d(String tag,String logMsg,boolean addition,boolean needSave){
        if (getLEVEL() > DEBUG) return;
        Log.d(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(generateTag(getCallerStackTraceElement()), logMsg,LOGNAME,addition);
        }
    }

    /**
     * @param tag 过滤器
     * @param logMsg log信息
     * @param fileName 保存的文件名
     * @param addition 存储模式（覆盖/续写）
     * @param needSave 是否需要存到sdCard
     */
    public static void d(String tag,String logMsg,String fileName,boolean addition,boolean needSave){
        if (getLEVEL() > DEBUG) return;
        Log.d(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(generateTag(getCallerStackTraceElement()), logMsg, fileName, addition);
        }
    }

    public static void i(String tag,String logMsg){
        if (getLEVEL() > INFO) return;
        Log.i(tag,logMsg);
    }

    public static void i(String tag,String logMsg,boolean needSave){
        if (getLEVEL() > INFO) return;
        Log.i(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(generateTag(getCallerStackTraceElement()) , logMsg,LOGNAME);
        }
    }

    /**
     * @param tag 过滤器
     * @param logMsg log信息
     * @param fileName 保存的文件名
     * @param needSave 是否需要存到sdCard
     */
    public static void i(String tag,String logMsg,String fileName,boolean needSave){
        if (getLEVEL() > INFO) return;
        Log.i(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(generateTag(getCallerStackTraceElement()), logMsg, fileName);
        }
    }

    /**
     * @param tag 过滤器
     * @param logMsg log信息
     * @param addition 存储模式（覆盖/续写）
     * @param needSave 是否需要存到sdCard
     */
    public static void i(String tag,String logMsg,boolean addition,boolean needSave){
        if (getLEVEL() > INFO) return;
        Log.i(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(generateTag(getCallerStackTraceElement()), logMsg,LOGNAME,addition);
        }
    }

    /**
     * @param tag 过滤器
     * @param logMsg log信息
     * @param fileName 保存的文件名
     * @param addition 存储模式（覆盖/续写）
     * @param needSave 是否需要存到sdCard
     */
    public static void i(String tag,String logMsg,String fileName,boolean addition,boolean needSave){
        if (getLEVEL() > INFO) return;
        Log.i(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(generateTag(getCallerStackTraceElement()), logMsg, fileName, addition);
        }
    }

    public static void e(String tag,String logMsg){
        if (getLEVEL() > ERROR) return;
        Log.e(tag,logMsg);
    }

    public static void e(String tag,String logMsg,boolean needSave){
        if (getLEVEL() > ERROR) return;
        Log.e(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(generateTag(getCallerStackTraceElement()) , logMsg,LOGNAME);
        }
    }

    /**
     * @param tag 过滤器
     * @param logMsg log信息
     * @param fileName 保存的文件名
     * @param needSave 是否需要存到sdCard
     */
    public static void e(String tag,String logMsg,String fileName,boolean needSave){
        if (getLEVEL() > ERROR) return;
        Log.e(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(generateTag(getCallerStackTraceElement()), logMsg, fileName);
        }
    }

    /**
     * @param tag 过滤器
     * @param logMsg log信息
     * @param addition 存储模式（覆盖/续写）
     * @param needSave 是否需要存到sdCard
     */
    public static void e(String tag,String logMsg,boolean addition,boolean needSave){
        if (getLEVEL() > ERROR) return;
        Log.e(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(generateTag(getCallerStackTraceElement()), logMsg,LOGNAME,addition);
        }
    }

    /**
     * @param tag 过滤器
     * @param logMsg log信息
     * @param fileName 保存的文件名
     * @param addition 存储模式（覆盖/续写）
     * @param needSave 是否需要存到sdCard
     */
    public static void e(String tag,String logMsg,String fileName,boolean addition,boolean needSave){
        if (getLEVEL() > ERROR) return;
        Log.e(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(generateTag(getCallerStackTraceElement()), logMsg, fileName, addition);
        }
    }

    public static void w(String tag,String logMsg){
        if (getLEVEL() > WARN) return;
        Log.w(tag,logMsg);
    }

    public static void w(String tag,String logMsg,boolean needSave){
        if (getLEVEL() > WARN) return;
        Log.w(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(generateTag(getCallerStackTraceElement()) , logMsg,LOGNAME);
        }
    }

    /**
     * @param tag 过滤器
     * @param logMsg log信息
     * @param fileName 保存的文件名
     * @param needSave 是否需要存到sdCard
     */
    public static void w(String tag,String logMsg,String fileName,boolean needSave){
        if (getLEVEL() > WARN) return;
        Log.w(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(generateTag(getCallerStackTraceElement()), logMsg, fileName);
        }
    }

    /**
     * @param tag 过滤器
     * @param logMsg log信息
     * @param addition 存储模式（覆盖/续写）
     * @param needSave 是否需要存到sdCard
     */
    public static void w(String tag,String logMsg,boolean addition,boolean needSave){
        if (getLEVEL() > WARN) return;
        Log.w(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(generateTag(getCallerStackTraceElement()), logMsg,LOGNAME,addition);
        }
    }

    /**
     * @param tag 过滤器
     * @param logMsg log信息
     * @param fileName 保存的文件名
     * @param addition 存储模式（覆盖/续写）
     * @param needSave 是否需要存到sdCard
     */
    public static void w(String tag,String logMsg,String fileName,boolean addition,boolean needSave){
        if (getLEVEL() > WARN) return;
        Log.w(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(generateTag(getCallerStackTraceElement()), logMsg, fileName, addition);
        }
    }
}
