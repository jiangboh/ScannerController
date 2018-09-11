package com.bravo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bravo.FemtoController.ProxyApplication;

/**
 * Created by Jack.liao on 2017/1/6.
 */

public class GreenDaoManager {

    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private static GreenDaoManager mInstance; //单例
    private SQLiteDatabase db;
    private GreenDaoManager(){
        if (mInstance == null) {
            MySQLiteOpenHelper devOpenHelper = new
                    MySQLiteOpenHelper(ProxyApplication.getContext(), "femto-db", null);//此处为自己需要处理的表
            mDaoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
            mDaoSession = mDaoMaster.newSession();
            db = devOpenHelper.getWritableDatabase();
        }
    }

    public static GreenDaoManager getInstance() {
        if (mInstance == null) {
            synchronized (GreenDaoManager.class) {//保证异步处理安全操作
                if (mInstance == null) {
                    mInstance = new GreenDaoManager();
                }
            }
        }
        return mInstance;
    }

//    public DaoMaster getMaster() {
//        return mDaoMaster;
//    }
    public DaoSession getSession() {
        return mDaoSession;
    }
//    public DaoSession getNewSession() {
//        mDaoSession = mDaoMaster.newSession();
//        return mDaoSession;
//    }

    public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {
        public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            MigrationHelper.migrate(db, AdjacentCellDao.class, BcastHistoryDao.class, FemtoListDao.class, TargetUserDao.class, UserDao.class, SnifferHistoryDao.class);
        }
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}
