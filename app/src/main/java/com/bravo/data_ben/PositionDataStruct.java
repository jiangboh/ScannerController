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


    public PositionDataStruct(String imsi,String sn,String tradeDate, int value) {
        this.imsi = imsi;
        this.sn = sn;
        this.tradeDate = tradeDate;
        this.value = value;
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
