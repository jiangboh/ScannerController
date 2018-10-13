package com.bravo.FemtoController;

/**
 * Created by Jack.liao on 2017/9/7.
 */

import android.content.Context;
import android.text.TextUtils;

import com.bravo.utils.Logs;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScanDeviceTool {

    private static final String TAG = "ScanDeviceTool";

    /** 核心池大小 **/
    private int CORE_POOL_SIZE;
    /** 线程池最大线程数 **/
    private int MAX_IMUM_POOL_SIZE;
    private String mLocAddress;// 局域网IP地址头,如：192.168.1.
    private ThreadPoolExecutor mExecutor;// 线程池对象

    /**
     * TODO<扫描局域网内ip，找到对应服务器>
     *
     * @return void
     */
    public void scan(String strGateway, Context context) {
        mLocAddress = strGateway;
        if (TextUtils.isEmpty(mLocAddress)) {
            Logs.e(TAG, "扫描失败，请检查wifi网络");
            return;
        }
        CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();
        MAX_IMUM_POOL_SIZE = 253 - (CORE_POOL_SIZE*5);
        /**
         * 1.核心池大小 2.线程池最大线程数 3.表示线程没有任务执行时最多保持多久时间会终止
         * 4.参数keepAliveTime的时间单位，有7种取值,当前为毫秒
         * 5.一个阻塞队列，用来存储等待执行的任务，这个参数的选择也很重要，会对线程池的运行过程产生重大影响
         * ，一般来说，这里的阻塞队列有以下几种选择：
         */
        mExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE + 1, (CORE_POOL_SIZE*5) + 1,
                2000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(
                MAX_IMUM_POOL_SIZE));
        // 新建线程池
        for (int i = 2; i < 255; i++) {
            final int lastAddress = i;
            Runnable run = new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    StartPing(mLocAddress + lastAddress);
                }
            };
            mExecutor.execute(run);
        }

        mExecutor.shutdown();
        while (true) {
            try {
                if (mExecutor.isTerminated()) {// 扫描结束,开始验证
                    break;
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public boolean StartPing(String strAddress) {
        boolean success = false;
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("ping -c 1 -i 0.2 -W 1 " +strAddress);
            int status = p.waitFor();
            if (status == 0) {
                success=true;
            } else {
                success=false;
            }
        } catch (IOException e) {
            success=false;
        } catch (InterruptedException e) {
            success=false;
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
        return success;
    }
    /**
     * TODO<销毁正在执行的线程池>
     *
     * @return void
     */
    public void destory() {
        if (mExecutor != null) {
            mExecutor.shutdownNow();
        }
    }
}
