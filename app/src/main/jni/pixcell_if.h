#ifndef __PIXCELL_IF_H__
#define __PIXCELL_IF_H__

#include <stdbool.h>
#include "pixcell_com.h"

#define PIXCELL_MAX_CLIENT_NUM  100


//Msg type
typedef enum
{
    PIXCELL_ONLINE_NOTIFY,
    PIXCELL_REG_REQ,
    PIXCELL_UNREG_REQ,
    PIXCELL_SET_CONF_REQ,
    PIXCELL_ACTION_RSP,
    PIXCELL_ACT_REQ,
    PIXCELL_ACT_RSP,
    PIXCELL_DEACT_REQ,
    PIXCELL_DEACT_RSP,
    PIXCELL_TARGET_LIST,
    PIXCELL_CONNECT_NOTIFY,
    PIXCELL_ATTACH_NOTIFY,
    PIXCELL_DETACH_NOTIFY,
    PIXCELL_POS_NOTIFY,
    PIXCELL_AUTH_REQ_NOTIFY,
    PIXCELL_AUTH_DATA_RSP,
    PIXCELL_REDIR_REQ,
    PIXCELL_REDIR_RSP,
    PIXCELL_SMS_REQ,
    PIXCELL_SMS_RSP,
    PIXCELL_PARA_CHANGE_NOTIFY,
    PIXCELL_CELL_SCAN,
    PIXCELL_CELL_NOTIFY,
    PIXCELL_SWITCH_TECH_REQ,
    PIXCELL_SWITCH_TECH_RSP,
    PIXCELL_SWITCH_MODE_REQ,
    PIXCELL_SWITCH_MODE_RSP,
    PIXCELL_GPS_NOTIFY,
    PIXCELL_ERR_NOTIFY,
    PIXCELL_STATUS_NOTIFY,
    PIXCELL_SILENT_CALL_REQ,
    PIXCELL_SILENT_CALL_RSP,    
    PIXCELL_MSG_MAX
} PixcellMsgType;

typedef enum
{
    PIXCELL_PROC_ACT,
    PIXCELL_PROC_DEACT,
    PIXCELL_PROC_TARGET_LIST,
    PIXCELL_PROC_AUTH,
    PIXCELL_PROC_REDIRECT,
    PIXCELL_PROC_SMS,
    PIXCELL_PROC_SCAN,
    PIXCELL_PROC_SWITCH_TECH,
    PIXCELL_PROC_SWITCH_MODE
}PixcellProcCode;

typedef enum
{
    PIXCELL_CLIENT_LAPTOP,
    PIXCELL_CLIENT_ANDROID,
    PIXCELL_CLIENT_UNKNOWN
}PixcellClientType;

typedef enum
{
    MODE_WIFI_AP,
    MODE_WIFI_CLIENT,
    MODE_ETHERNET,
    MODE_UNKNOWN
}PixcellConMode;

typedef enum
{
    PIXCELL_ACTION_REG,
    PIXCELL_ACTION_UNREG,
    PIXCELL_ACTION_SETCONF,
    PIXCELL_ACTION_UNKNOWN
}PixcellActionType;

typedef enum
{
    PIXCELL_FEMTO_STATE_NOT_OPERATIONAL = -1,
    PIXCELL_FEMTO_STATE_IDLE = 0,
    PIXCELL_FEMTO_STATE_BUSY,
    PIXCELL_FEMTO_STATE_BROADCASTING,
    PIXCELL_FEMTO_STATE_SERVING
}PixcellFemtoState;

typedef struct
{
    float latitude;
    float longitude;
}Gps;

typedef struct
{
    unsigned long rscpDis;
    unsigned long rttDis;
    Gps           gps;
}MeasDis;

typedef struct
{
    Plmn plmn;
    unsigned short mmeGrpId;
    unsigned char  mmeCode;
    unsigned int   mTmsi;
}Guti;

typedef struct
{
    unsigned long ip;
}Client;

typedef struct
{
    unsigned char num;
    Client        client[PIXCELL_MAX_CLIENT_NUM];
}ClientLst;

typedef struct
{
    SmsSender   smsSender;
    SmsData     smsData;
} Sms;


typedef struct
{
    PixcellMsgHdr msgHdr;
    char          sn[PIXCELL_MAX_SN_STR];
    unsigned char band;
    unsigned char resv[3];
}PixcellOnlineNotify;

typedef struct
{
    PixcellMsgHdr msgHdr;
    unsigned char clientType;
    unsigned char resv[3];
}PixcellRegReq;

typedef struct
{
    PixcellMsgHdr msgHdr;
}PixcellUnRegReq;

typedef struct
{
    PixcellMsgHdr msgHdr;
    unsigned char clientType;
    unsigned char conMode;
    unsigned char resv[2];
    unsigned int  tcpPort;
    unsigned int  tcpRetry;
    unsigned int  defGw;
    unsigned int  nbGw;
}PixcellSetConfReq;

typedef struct
{
    PixcellMsgHdr msgHdr;
    unsigned char actionType;
    unsigned char err;
    unsigned char resv[2];
}PixcellActionRsp;

typedef struct
{
    PixcellMsgHdr  msgHdr;
    struct
    {
        unsigned char autoSwIntPres:1;
        unsigned char eNbIdPres:1;
        unsigned char cellIdPres:1;
        unsigned char tacPres:1;
        unsigned char pciPres:1;
    }pres;
    unsigned char  actMode; //start/stop
    unsigned char  band;
    unsigned char  antGain;//antenna gain
    unsigned char  autoSwInt; //Auto Switch Interval
    unsigned short power;
    unsigned long  eNbId;
    unsigned long  cellId;
    unsigned short tac;
    unsigned short pci;
    Plmn           plmn;
    EarfcnLst      earfcnLst;
    NeighCellLst   neighCellLst;
} PixcellActReq;

typedef struct
{
    PixcellMsgHdr msgHdr;
    PixcellErrorCode err;
    unsigned long    eNbId;
    unsigned long    cellId;
    Earfcn           earfcn;
    unsigned short   pci;
    unsigned short   tac;
}PixcellActRsp;

typedef struct
{
    PixcellMsgHdr msgHdr;
}PixcellDeactReq;

typedef struct
{
    PixcellMsgHdr msgHdr;
    PixcellErrorCode err;
}PixcellDeactRsp;


typedef struct
{
    PixcellMsgHdr msgHdr;
    ImsiLst       imsiLst;
}PixcellTargetListReq;

typedef struct
{
    PixcellMsgHdr  msgHdr;
    Imsi           imsi;
    unsigned long  distance;
} PixcellConReqNotify;

typedef struct
{
    PixcellMsgHdr msgHdr;
    Imsi          imsi;
}PixcellAttachNotify;

typedef struct
{
    PixcellMsgHdr msgHdr;
    Imsi          imsi;
}PixcellDetachNotify;

typedef struct
{
    PixcellMsgHdr msgHdr;
    Imsi          imsi;
    MeasDis       measDis;
}PixcellPosNotify;

typedef struct
{
    PixcellMsgHdr msgHdr;
    struct
    {
        unsigned char resyncPres:1;
        unsigned char gutiPres:1;
    }pres;    
    Imsi          imsi;
    unsigned char netType;
    Plmn          snId;
    AuthReSynchInfo resync;
    Guti          guti;
}PixcellAuthReqNotify;

typedef struct
{
    PixcellMsgHdr msgHdr;
    Imsi          imsi;
    EpcAv         epcAv;
} PixcellDoAuthReq;

typedef struct
{
    PixcellMsgHdr msgHdr;
    Imsi          imsi;
    unsigned char ratType;
    unsigned char band;
    unsigned short channel;
}PixcellRedirReq;

typedef struct
{
    PixcellMsgHdr msgHdr;
    PixcellErrorCode err;
}PixcellRedirRsp;

typedef struct
{
    PixcellMsgHdr msgHdr;
    Imsi          imsi;
    Sms           sms;
}PixcellSmsReq;

typedef struct
{
    PixcellMsgHdr msgHdr;
    PixcellErrorCode err;
}PixcellSmsRsp;

typedef struct
{
    PixcellMsgHdr msgHdr;
    unsigned long eNbId;
    unsigned long cellId;
    Earfcn        earfcn;
    unsigned short pci;
    unsigned short tac;
}PixcellParaChangeNotify;

typedef struct
{
    PixcellMsgHdr msgHdr;
    unsigned char band;
    EarfcnLst     earfcnLst;
    int           threshold;
}PixcellCellScanReq;

typedef struct
{
    PixcellMsgHdr msgHdr;
    unsigned short earfcnDl;
    CellLst       cellLst;
}PixcellCellNotify;

typedef struct
{
    PixcellMsgHdr msgHdr;
    unsigned char ratType;
}PixcellSwitchTechReq;

typedef struct
{
    PixcellMsgHdr msgHdr;
    PixcellErrorCode err;
}PixcellSwitchTechRsp;

typedef struct
{
    PixcellMsgHdr msgHdr;
    unsigned char mode;
}PixcellSwitchModeReq;

typedef struct
{
    PixcellMsgHdr msgHdr;
    PixcellErrorCode err;
}PixcellSwitchModeRsp;

typedef struct
{
    PixcellMsgHdr msgHdr;
    Gps           gps;
    unsigned int  accuracy;
    int           bearing;
}PixcellGpsNotify;

typedef struct
{
    PixcellMsgHdr msgHdr;
    PixcellErrorCode err;
    char          errStr[PIXCELL_MAX_ERR_STR];
}PixcellErrNotify;

typedef struct
{
    PixcellMsgHdr msgHdr;
    char          sn[PIXCELL_MAX_SN_STR];
    char          ver[PIXCELL_MAX_VER_STR];
    ClientLst     clientLst;
    Client        ctrlClient;
    unsigned char ratType;
    unsigned char band;
    unsigned short channel;
    unsigned long rncId;
    unsigned long cellId;
    Plmn          plmn;
    unsigned short tac;
    unsigned short pci;
    unsigned char batLev;
    bool          batCharging;
    char          state;
}PixcellStatusNotify;

typedef struct
{
    PixcellMsgHdr msgHdr;
    Imsi          imsi;
    unsigned char silentCall;
    unsigned char resv[3];
}PixcellSilentCallReq;

typedef struct
{
    PixcellMsgHdr msgHdr;
    PixcellErrorCode err;
}PixcellSilentCallRsp;


#if 0
//Msg struct
typedef struct
{
    PixcellMsgHdr msgHdr;
    Date          date;
} PixcellStatusReq;

typedef struct
{
    PixcellMsgHdr  msgHdr;
    unsigned char  batPct;
    unsigned char  resv;     //reserved
    unsigned short alarm;
} PixcellStatusRsp;

typedef struct
{
    PixcellMsgHdr  msgHdr;
    struct
    {
        unsigned char cellIdPres:1;
        unsigned char tacPres:1;
        unsigned char ccPres:1;
    }pres;
    unsigned char  band;
    unsigned char  ant;    //antenna
    unsigned char  afc;    //Auto Frequency Control
    RatMode        ratMode;
    unsigned char  powerLevel;
    unsigned char  smsFlag;
    unsigned long  cellId;
    unsigned short tac;
    unsigned short pci;
    Plmn           plmn;
    FreqType       freqType;
    EarfcnLst      earfcnLst;
    ImsiLst        imsiLst;
    ImeiLst        imeiLst;
    CleanChannel   cleanChan;
    NeighCellLst   neighCellLst;
} PixcellConfReq;

typedef struct
{
    PixcellMsgHdr    msgHdr;
    PixcellErrorCode err;
} PixcellConfRsp;

typedef struct
{
    PixcellMsgHdr  msgHdr;
    PixcellActMode actMode;
    ScanMode       scanMode;
    unsigned char  timeOut;
} PixcellActReq;

typedef struct
{
    PixcellMsgHdr    msgHdr;
    unsigned char    batPct;
    unsigned char    resv;
    unsigned short   batTime;    
    PixcellErrorCode err;
} PixcellActRsp;

typedef struct
{
    PixcellMsgHdr  msgHdr;
    unsigned short earfcnDl;
    CellLst        cellLst;
    unsigned long  timeStamp;
} PixcellCellInfo;

typedef struct
{
    PixcellMsgHdr  msgHdr;
    struct
    {
        unsigned char imeiPres:1;
    }pres;
    unsigned long  id;
    Imsi           imsi;
    Imei           imei;
    int            measRpt;
    unsigned char  status;
    unsigned char  batPct;
    unsigned short batTime;
    unsigned long  timeStamp;
} PixcellScannerRpt;

typedef struct
{
    PixcellMsgHdr msgHdr;
    unsigned long id;
} PixcellScannerRptAck;

typedef struct
{
    PixcellMsgHdr msgHdr;
    struct
    {
        unsigned char reSynchInfoPres:1;
    }pres;
    unsigned char   netType;
    unsigned char   resv[2];
    Imsi            imsi;
    Plmn            snId;
    AuthReSynchInfo reSynchInfo;
} PixcellAuthDataReq;

typedef struct
{
    PixcellMsgHdr msgHdr;
    EpcAv         epcAv;
} PixcellAuthDataRsp;

typedef struct
{
    PixcellMsgHdr msgHdr;
    Imsi          imsi;
    CleanChannel  cleanChan;
} PixcellCleanChannelReq;

typedef struct
{
    PixcellMsgHdr    msgHdr;
    PixcellErrorCode err;
} PixcellCleanChannelRsp;


typedef struct
{
    PixcellMsgHdr msgHdr;
    SmsSender     smsSender;
    SmsData       smsData;
} PixcellSmsReq;

typedef struct
{
    PixcellMsgHdr    msgHdr;
    unsigned char    batPct;
    unsigned short   batTime;
    PixcellErrorCode err;
} PixcellSmsRsp;

typedef struct
{
    PixcellMsgHdr msgHdr;
    ImsiLst       imsiLst;
    MeasType      measType;
    ReportInter   rptInter;
} PixcellTraceReq;

typedef struct
{
    PixcellMsgHdr    msgHdr;
    Imsi             imsi;
    PixcellErrorCode err;
} PixcellTraceRsp;

typedef struct
{
    PixcellMsgHdr msgHdr;
    Imsi          imsi;
    int           measRpt;
    MeasType      measType;
} PixcellMeasRpt;

typedef struct
{
    PixcellMsgHdr msgHdr;
} PixcellTraceStopReq;


typedef struct
{
    PixcellMsgHdr msgHdr;
    unsigned char band;
    EarfcnLst     earfcnLst;
} PixcellScanCell;

typedef struct
{
    PixcellMsgHdr msgHdr;
} PixcellFemtoStatusReq;

typedef struct
{
    PixcellMsgHdr  msgHdr;
    unsigned char  status;
    unsigned char  band;
    unsigned long  cellId;
    FreqType       freqType;
    Earfcn         earfcn;
    unsigned short pci;
    Plmn           plmn;
    unsigned short tac;
    char           sn[PIXCELL_MAX_SN_STR];
    char           ver[PIXCELL_MAX_VER_STR];
    unsigned long  timeStamp;
} PixcellFemtoStatusRsp;

#endif

#endif

