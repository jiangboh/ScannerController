package com.bravo.utils;

import android.content.Context;

import java.io.File;
import java.io.IOException;

/**
 * Created by lenovo on 2017/1/13.
 */

public class LogsManager {
    private static LogsManager instance = null;
    private FileUtils fileUtils;
    private static final String TAG = "LogsManager";
    private long lastCheckOverdueLogsTime = 0L;
    private LogsManager(){}

    public static LogsManager getInstance(){
        if(instance == null){
            synchronized (LogsManager.class){
                if(instance == null){
                    instance = new LogsManager();
                }
            }
        }
        return  instance;
    }

    public void init(Context context){
        fileUtils = new FileUtils(context);
    }

    public void saveData(String tag,String logMsg,String fileName){
        saveData(tag,logMsg,fileName,true);
    }

    public void checkOverdueLogs(){
        if((System.currentTimeMillis() - lastCheckOverdueLogsTime) < 24*60*60*1000L){
            return;
        }
        lastCheckOverdueLogsTime = System.currentTimeMillis();
        File[] files = new File(fileUtils.getLogCacheDir()).listFiles();
        if (files != null && files.length > 0) {
            for (File f : files) {
                if(f.isDirectory()){
                    String dirName = f.getName();
                    dirName = dirName.replace(" Logs","");
                    long time = SimpleDateUtils.parseDate("yyyy-MM-dd",dirName);
                    if((System.currentTimeMillis() - time) > 7*24*60*60*1000L){
                        fileUtils.deleteFile(f);
                    }
                }else{
                    f.delete();
                }
            }
        }
    }

    public void saveData(String tag,String logMsg,String fileName,boolean addition){
        checkOverdueLogs();
        String logDirName = SimpleDateUtils.formatTime("yyyy-MM-dd") + " Logs";
        File file = new File(fileUtils.getLogCacheDir() + File.separator + logDirName);
        file.mkdir();
        file = new File(file,fileName+".txt");
        saveData(file,"\n"+SimpleDateUtils.formatTime("yyyy-MM-dd HH:mm:ss")+"\n"+logMsg
                + "\n" + "—————————————————————————————",addition);
    }

    private void saveData(File file,String logMsg,boolean addition){
        try {
            fileUtils.writeTextFile(file,logMsg,addition);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
