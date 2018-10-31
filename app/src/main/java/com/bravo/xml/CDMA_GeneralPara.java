package com.bravo.xml;

/**
 * Created by admin on 2018-10-23.
 */

public class CDMA_GeneralPara {
    private String sn;
    private GeneralPara sys1;
    private GeneralPara sys2;

    public CDMA_GeneralPara() {
        sys1 = new GeneralPara();
        sys2 = new GeneralPara();
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public GeneralPara getSys1() {
        return sys1;
    }

    public void setSys1(GeneralPara sys1) {
        this.sys1 = sys1;
    }

    public GeneralPara getSys2() {
        return sys2;
    }

    public void setSys2(GeneralPara sys2) {
        this.sys2 = sys2;
    }




    public class GeneralPara {
        private int bWorkingMode;

        public int getbWorkingMode() {
            return bWorkingMode;
        }

        public void setbWorkingMode(int bWorkingMode) {
            this.bWorkingMode = bWorkingMode;
        }
    }
}
