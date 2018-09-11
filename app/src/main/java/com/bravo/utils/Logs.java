package com.bravo.utils;

import android.util.Log;

/**
 * Created by lenovo on 2017/1/16.
 */

public class Logs {
    public static void d(String tag,String logMsg){
        Log.d(tag,logMsg);
    }

    public static void d(String tag,String logMsg,boolean needSave){
        Log.d(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(tag, logMsg,"d");
        }
    }

    /**
     * @param tag 过滤器
     * @param logMsg log信息
     * @param fileName 保存的文件名
     * @param needSave 是否需要存到sdCard
     */
    public static void d(String tag,String logMsg,String fileName,boolean needSave){
        Log.d(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(tag, logMsg, fileName);
        }
    }

    /**
     * @param tag 过滤器
     * @param logMsg log信息
     * @param addition 存储模式（覆盖/续写）
     * @param needSave 是否需要存到sdCard
     */
    public static void d(String tag,String logMsg,boolean addition,boolean needSave){
        Log.d(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(tag, logMsg,"d",addition);
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
        Log.d(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(tag, logMsg, fileName, addition);
        }
    }

    public static void i(String tag,String logMsg){
        Log.i(tag,logMsg);
    }

    public static void i(String tag,String logMsg,boolean needSave){
        Log.i(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(tag, logMsg,"i");
        }
    }

    /**
     * @param tag 过滤器
     * @param logMsg log信息
     * @param fileName 保存的文件名
     * @param needSave 是否需要存到sdCard
     */
    public static void i(String tag,String logMsg,String fileName,boolean needSave){
        Log.i(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(tag, logMsg, fileName);
        }
    }

    /**
     * @param tag 过滤器
     * @param logMsg log信息
     * @param addition 存储模式（覆盖/续写）
     * @param needSave 是否需要存到sdCard
     */
    public static void i(String tag,String logMsg,boolean addition,boolean needSave){
        Log.i(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(tag, logMsg,"i",addition);
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
        Log.i(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(tag, logMsg, fileName, addition);
        }
    }

    public static void e(String tag,String logMsg){
        Log.e(tag,logMsg);
    }

    public static void e(String tag,String logMsg,boolean needSave){
        Log.e(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(tag, logMsg,"e");
        }
    }

    /**
     * @param tag 过滤器
     * @param logMsg log信息
     * @param fileName 保存的文件名
     * @param needSave 是否需要存到sdCard
     */
    public static void e(String tag,String logMsg,String fileName,boolean needSave){
        Log.e(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(tag, logMsg, fileName);
        }
    }

    /**
     * @param tag 过滤器
     * @param logMsg log信息
     * @param addition 存储模式（覆盖/续写）
     * @param needSave 是否需要存到sdCard
     */
    public static void e(String tag,String logMsg,boolean addition,boolean needSave){
        Log.e(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(tag, logMsg,"e",addition);
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
        Log.e(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(tag, logMsg, fileName, addition);
        }
    }

    public static void w(String tag,String logMsg){
        Log.w(tag,logMsg);
    }

    public static void w(String tag,String logMsg,boolean needSave){
        Log.w(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(tag, logMsg,"w");
        }
    }

    /**
     * @param tag 过滤器
     * @param logMsg log信息
     * @param fileName 保存的文件名
     * @param needSave 是否需要存到sdCard
     */
    public static void w(String tag,String logMsg,String fileName,boolean needSave){
        Log.w(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(tag, logMsg, fileName);
        }
    }

    /**
     * @param tag 过滤器
     * @param logMsg log信息
     * @param addition 存储模式（覆盖/续写）
     * @param needSave 是否需要存到sdCard
     */
    public static void w(String tag,String logMsg,boolean addition,boolean needSave){
        Log.w(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(tag, logMsg,"w",addition);
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
        Log.w(tag,logMsg);
        if(needSave){
            LogsManager.getInstance().saveData(tag, logMsg, fileName, addition);
        }
    }
}
