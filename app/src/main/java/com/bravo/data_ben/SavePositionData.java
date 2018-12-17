package com.bravo.data_ben;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by admin on 2018-12-12.
 */

public class SavePositionData {
    private static Lock lock = new ReentrantLock();

    private String imsi;
    private List<PositionDataStruct> dataList;

    public SavePositionData() {
        this.imsi = "";
        this.dataList = new ArrayList<PositionDataStruct>();
    }

    public SavePositionData(String imsi, List<PositionDataStruct> dataList) {
        this.imsi = imsi;
        this.dataList = dataList;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public List<PositionDataStruct> getDataList() {

        lock.lock();
        try{
            return dataList;
        } finally {
            lock.unlock();
        }
    }

    public void setDataList(List<PositionDataStruct> dataList) {
        lock.lock();
        try{
            this.dataList = dataList;
        } finally {
            lock.unlock();
        }
    }
}
