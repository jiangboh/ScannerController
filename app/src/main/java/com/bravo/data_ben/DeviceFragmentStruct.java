package com.bravo.data_ben;

import com.bravo.utils.Logs;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by admin on 2018-9-18.
 */

public class DeviceFragmentStruct {
    private static String TAG = "DeviceFragmentStruct";
    private static ArrayList<DeviceDataStruct> dList =  new ArrayList<>();
    private static Lock lock = new ReentrantLock();
    private static Timer timer = null;

    public static void StartCheckApTimer(int time)
    {
       if (timer == null) {
           Logs.d(TAG,"启动定时检查在线状态线程。。。");
           timer = new Timer();
           timer.schedule(new MyTimer(),0, time);
       }
    }

    static class MyTimer extends TimerTask implements Serializable {
        @Override
        public void run() {
            // 需要做的事
            boolean del = false;
            int diff = 20;//秒
            Long curTime = System.currentTimeMillis();
            lock.lock();
            try {
                for(int i=dList.size()-1;i>=0;i--)
                {
                    Logs.d(TAG,String.format("设备最后条消息离现在已有(%d)秒 时间！",(curTime - dList.get(i).getLastTime() )/1000));
                    if ((curTime - dList.get(i).getLastTime())/1000 > diff)
                    {
                        Logs.d(TAG,String.format("设备%s[%s:%d]下线了！",dList.get(i).getSN(),dList.get(i).getIp(),dList.get(i).getPort()));
                        dList.remove(i);
                        del = true;
                    }
                }
            } finally {
                lock.unlock();
            }

            if (del) {
                DeviceDataStruct dds = new DeviceDataStruct();
                EventBus.getDefault().post(dds);
            }
        }
    }

    public static int  getSize(){
        lock.lock();
        try {
            return dList.size();
        }finally {
            lock.unlock();
        }
    }

    public static DeviceDataStruct getDevice(int index) {
        lock.lock();
        try {
            return dList.get(index);
        }finally {
            lock.unlock();
        }
    }

    public static DeviceDataStruct getDevice(String ip,int  port) {
        lock.lock();
        try {
            for(int i = 0 ;i<dList.size();i++)
            {
                if (dList.get(i).getIp().equals(ip) && dList.get(i).getPort() == port)
                {
                    return dList.get(i);
                }
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    public static DeviceDataStruct getDevice(String sn) {
        lock.lock();
        try {
            for(int i = 0 ;i<dList.size();i++)
            {
                if (dList.get(i).getSN().equals(sn))
                {
                    return dList.get(i);
                }
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    public static int inListIndex(String ip,int  port) {
        lock.lock();
        try {
            for(int i = 0 ;i<dList.size();i++)
            {
                if (dList.get(i).getIp().equals(ip) && dList.get(i).getPort() == port)
                {
                    return i;
                }
            }
            return -1;
        } finally {
            lock.unlock();
        }
    }

    public static int inListIndex(String sn) {
        lock.lock();
        try {
            for(int i = 0 ;i<dList.size();i++)
            {
                if (dList.get(i).getSN().equals(sn))
                {
                    return i;
                }
            }
            return -1;
        } finally {
            lock.unlock();
        }
    }

    public static void setListLastTime(int index,Long time) {
        lock.lock();
        try {
           dList.get(index).setLastTime(time);
           return;
        } finally {
            lock.unlock();
        }
    }

    public static void addList(DeviceDataStruct deviceInfo) {
        int index = inListIndex(deviceInfo.getIp(),deviceInfo.getPort());
        lock.lock();
        try {
            if (-1 == index)
                dList.add(deviceInfo);
            else
            {
                dList.get(index).setLastTime(System.currentTimeMillis());
                dList.get(index).setDetail(deviceInfo.getDetail());
            }
            return;
        } finally {
            lock.unlock();
        }
    }

    public static void removeList(int index) {
        lock.lock();
        try {
            dList.remove(index);
            return;
        } finally {
            lock.unlock();
        }
    }

    public static void clearList() {
        lock.lock();
        try {
            dList.clear();
            return;
        } finally {
            lock.unlock();
        }
    }

    public static int ChangeDetail(String ip,int  port,Long detail) {
        lock.lock();
        try {
            for(int i = 0 ;i<dList.size();i++)
            {
                if (dList.get(i).getIp().equals(ip) && dList.get(i).getPort() == port)
                {
                    dList.get(i).setDetail(detail);
                    return i;
                }
            }
            return -1;
        } finally {
            lock.unlock();
        }
    }

    public static int ChangeDetail(int  index,Long detail) {
        lock.lock();
        try {
            dList.get(index).setDetail(detail);
            return 0;
        } finally {
            lock.unlock();
        }
    }

    public static ArrayList<DeviceDataStruct> getList() {
        lock.lock();
        try {
            return dList;
        } finally {
            lock.unlock();
        }
    }
}
