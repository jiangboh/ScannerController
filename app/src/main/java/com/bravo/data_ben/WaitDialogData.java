package com.bravo.data_ben;

/**
 * Created by admin on 2018-10-22.
 */

public class WaitDialogData {
    public static final int WAIT_SEND = 0;
    public static final int SEND = 1;
    public static final int RUSULT_OK = 2;
    public static final int RUSULT_FAIL = 3;

    private int id;
    private String title;
    private int iRusult;

    public WaitDialogData(int id,String title,int sRusult) {
        this.id = id;
        this.title = title;
        this.iRusult = sRusult;
    }

    public WaitDialogData(int id,String title) {
        this.id = id;
        this.title = title;
        this.iRusult = WAIT_SEND;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getiRusult() {
        return iRusult;
    }

    public void setiRusult(int iRusult) {
        this.iRusult = iRusult;
    }
}
