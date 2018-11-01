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


    public class ConfigOrCarrierPara {
        private int flag; //0为Config参数；1为Carrier参数
        private String sn;
        private int sys;
        private GeneralPara gPara;

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public int getSys() {
            return sys;
        }

        public void setSys(int sys) {
            this.sys = sys;
        }

        public GeneralPara getgPara() {
            return gPara;
        }

        public void setgPara(GeneralPara gPara) {
            this.gPara = gPara;
        }
    }

    public class GeneralPara {
        private int bWorkingMode;
        private int bC;
        private int wRedirectCellUarfcn;
        private int wUARFCN;
        private int wPhyCellId;
        private long wCellId;
        private String bPLMNId;
        private int wLAC;
        private int bTxPower;
        private int bRxGain;

        private int wARFCN1;
        private int bARFCN1Mode;
        private int wARFCN1Duration;
        private int wARFCN1Period;
        private int wARFCN2;
        private int bARFCN2Mode;
        private int wARFCN2Duration;
        private int wARFCN2Period;
        private int wARFCN3;
        private int bARFCN3Mode;
        private int wARFCN3Duration;
        private int wARFCN3Period;
        private int wARFCN4;
        private int bARFCN4Mode;
        private int wARFCN4Duration;
        private int wARFCN4Period;

        public int getbWorkingMode() {
            return bWorkingMode;
        }

        public void setbWorkingMode(int bWorkingMode) {
            this.bWorkingMode = bWorkingMode;
        }

        public int getbC() {
            return bC;
        }

        public void setbC(int bC) {
            this.bC = bC;
        }

        public int getwRedirectCellUarfcn() {
            return wRedirectCellUarfcn;
        }

        public void setwRedirectCellUarfcn(int wRedirectCellUarfcn) {
            this.wRedirectCellUarfcn = wRedirectCellUarfcn;
        }

        public int getwUARFCN() {
            return wUARFCN;
        }

        public void setwUARFCN(int wUARFCN) {
            this.wUARFCN = wUARFCN;
        }

        public int getwPhyCellId() {
            return wPhyCellId;
        }

        public void setwPhyCellId(int wPhyCellId) {
            this.wPhyCellId = wPhyCellId;
        }

        public long getwCellId() {
            return wCellId;
        }

        public void setwCellId(long wCellId) {
            this.wCellId = wCellId;
        }

        public String getbPLMNId() {
            return bPLMNId;
        }

        public void setbPLMNId(String bPLMNId) {
            this.bPLMNId = bPLMNId;
        }

        public int getwLAC() {
            return wLAC;
        }

        public void setwLAC(int wLAC) {
            this.wLAC = wLAC;
        }

        public int getbTxPower() {
            return bTxPower;
        }

        public void setbTxPower(int bTxPower) {
            this.bTxPower = bTxPower;
        }

        public int getbRxGain() {
            return bRxGain;
        }

        public void setbRxGain(int bRxGain) {
            this.bRxGain = bRxGain;
        }

        public int getwARFCN1() {
            return wARFCN1;
        }

        public void setwARFCN1(int wARFCN1) {
            this.wARFCN1 = wARFCN1;
        }

        public int getbARFCN1Mode() {
            return bARFCN1Mode;
        }

        public void setbARFCN1Mode(int bARFCN1Mode) {
            this.bARFCN1Mode = bARFCN1Mode;
        }

        public int getwARFCN1Duration() {
            return wARFCN1Duration;
        }

        public void setwARFCN1Duration(int wARFCN1Duration) {
            this.wARFCN1Duration = wARFCN1Duration;
        }

        public int getwARFCN1Period() {
            return wARFCN1Period;
        }

        public void setwARFCN1Period(int wARFCN1Period) {
            this.wARFCN1Period = wARFCN1Period;
        }

        public int getwARFCN2() {
            return wARFCN2;
        }

        public void setwARFCN2(int wARFCN2) {
            this.wARFCN2 = wARFCN2;
        }

        public int getbARFCN2Mode() {
            return bARFCN2Mode;
        }

        public void setbARFCN2Mode(int bARFCN2Mode) {
            this.bARFCN2Mode = bARFCN2Mode;
        }

        public int getwARFCN2Duration() {
            return wARFCN2Duration;
        }

        public void setwARFCN2Duration(int wARFCN2Duration) {
            this.wARFCN2Duration = wARFCN2Duration;
        }

        public int getwARFCN2Period() {
            return wARFCN2Period;
        }

        public void setwARFCN2Period(int wARFCN2Period) {
            this.wARFCN2Period = wARFCN2Period;
        }

        public int getwARFCN3() {
            return wARFCN3;
        }

        public void setwARFCN3(int wARFCN3) {
            this.wARFCN3 = wARFCN3;
        }

        public int getbARFCN3Mode() {
            return bARFCN3Mode;
        }

        public void setbARFCN3Mode(int bARFCN3Mode) {
            this.bARFCN3Mode = bARFCN3Mode;
        }

        public int getwARFCN3Duration() {
            return wARFCN3Duration;
        }

        public void setwARFCN3Duration(int wARFCN3Duration) {
            this.wARFCN3Duration = wARFCN3Duration;
        }

        public int getwARFCN3Period() {
            return wARFCN3Period;
        }

        public void setwARFCN3Period(int wARFCN3Period) {
            this.wARFCN3Period = wARFCN3Period;
        }

        public int getwARFCN4() {
            return wARFCN4;
        }

        public void setwARFCN4(int wARFCN4) {
            this.wARFCN4 = wARFCN4;
        }

        public int getbARFCN4Mode() {
            return bARFCN4Mode;
        }

        public void setbARFCN4Mode(int bARFCN4Mode) {
            this.bARFCN4Mode = bARFCN4Mode;
        }

        public int getwARFCN4Duration() {
            return wARFCN4Duration;
        }

        public void setwARFCN4Duration(int wARFCN4Duration) {
            this.wARFCN4Duration = wARFCN4Duration;
        }

        public int getwARFCN4Period() {
            return wARFCN4Period;
        }

        public void setwARFCN4Period(int wARFCN4Period) {
            this.wARFCN4Period = wARFCN4Period;
        }
    }
}
