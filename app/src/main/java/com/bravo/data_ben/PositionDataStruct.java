package com.bravo.data_ben;

/**
 * Created by admin on 2018-12-11.
 */

public class PositionDataStruct {
    /**
     * tradeDate : 20180502
     * value : 0.03676598
     */
    private String imsi;
    private String sn;
    private String tradeDate;
    private int value;
    private int rxGain;


    public PositionDataStruct(String sn,String imsi, String tradeDate, int value, int rxGain) {
        this.imsi = imsi;
        this.sn = sn;
        this.tradeDate = tradeDate;
        this.value = value;
        this.rxGain = rxGain;
    }

    public PositionDataStruct(String sn, String imsi, String tradeDate, int value) {
        this.imsi = imsi;
        this.sn = sn;
        this.tradeDate = tradeDate;
        this.value = value;
        this.rxGain = 0;
    }

    public int getRxGain() {
        return rxGain;
    }

    public void setRxGain(int rxGain) {
        this.rxGain = rxGain;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
