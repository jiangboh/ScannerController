#ifndef __PIXCELL_COM_H__
#define __PIXCELL_COM_H__

#include <sys/types.h>

#define PIXCELL_MAX_MCC_DIGIT   3
#define PIXCELL_MAX_MNC_DIGIT   3
#define PIXCELL_MAX_PLMN_CNT    16

#define PIXCELL_MAX_EARFCN_LST     512
#define PIXCELL_MAX_IMSI_LST   100
#define PIXCELL_MAX_IMEI_LST   100

#define PIXCELL_MAX_NEIGH_CELL_LST  32
#define PIXCELL_MAX_CELL_INFO_LST  64//256
#define PIXCELL_MAX_SIB_CELL_LST   32//64

#define PIXCELL_MAX_IMSI_DIGIT  15
#define PIXCELL_MAX_IMEI_DIGIT  15

#define PIXCELL_MAX_RAND_OCTET  16
#define PIXCELL_MAX_AUTN_OCTET  16
#define PIXCELL_MAX_KASME_OCTET  32
#define PIXCELL_MIN_XRES_OCTET  4
#define PIXCELL_MAX_XRES_OCTET  16
#define PIXCELL_MAX_AUTS_OCTET  14


#define PIXCELL_MAX_SMS_SENDER_DIGIT    15
#define PIXCELL_MAX_SMS_DATA_LEN        140

#define PIXCELL_MAX_SN_STR      64
#define PIXCELL_MAX_VER_STR     64

#define PIXCELL_MAX_ACTIVE_TIMER 10

#define PIXCELL_MAX_ERR_STR     64

#define PIXCELL_MAX_TECH_STR    4

//EntityId
#define ENTNA               0   //Not available
#define ENTPIXCELL          1
#define ENTPIXCELLAGENT     2
#define ENTOAM              3
#define ENTEPC              4
#define ENTTMR              5

#define PIXCELL_MAX_MSG_LEN     81920


#define ROK         0
#define RFAIL        -1


//Type define
typedef struct
{
    jboolean  entityId;
    jboolean  msgType;
    unsigned short msgLen;
} PixcellMsgHdr;

//Type enum
typedef enum
{
    ERROR_CODE_NONE,
    ERROR_CODE_INVALID_PARAMETERS,
    ERROR_CODE_STATE_MSG_MISMATCH,
    ERROR_CODE_MAX
} PixcellErrorCode;

typedef enum
{
    RAT_NONE,
    RAT_GSM,
    RAT_UMTS,
    RAT_LTE
} RatMode;

typedef enum
{
    BOOT_MODE_FEMTO,
    BOOT_MODE_SCANNER
} BootMode;

typedef enum
{
    FREQ_TYPE_NONE,
    FREQ_TYPE_SEARCH,
    FREQ_TYPE_MANUAL
} FreqType;

typedef enum
{
    CLEAN_CHANNEL_MODE0,
    CLEAN_CHANNEL_MODE1,
    CLEAN_CHANNEL_MODE2,
    CLEAN_CHANNEL_MODE3
} CleanChannelMode;

typedef enum
{
    PIXCELL_ACT_MODE_NONE,
    PIXCELL_ACT_MODE_START,
    PIXCELL_ACT_MODE_STOP
} PixcellActMode;

typedef enum
{
    MEAS_NONE,
    MEAS_POW,
    MEAS_GPS,
    MEAS_BOTH
} MeasType;

typedef enum
{
    INT_MS120,
    INT_MS240,
    INT_MS480,
    INT_MS640,
    INT_MS1024,
    INT_MS2048,
    INT_MS5120,
    INT_MS10240,
    INT_MIN1,
    INT_MIN6,
    INT_MIN12,
    INT_MIN30,
    INT_MIN60
} ReportInter;

typedef enum
{
    SCAN_MODE_FEMTO,
    SCAN_MODE_IMSI_CAPTURE,
    SCAN_MODE_SCANNER
} ScanMode;

typedef enum
{
    SECURITY_MODE_OPEN,
    SECURITY_MODE_WPA_PSK,
    SECURITY_MODE_WPA2_PSK,
    SECURITY_MODE_WPA_WPA2_PSK_AUTO
}SecurityMode;

typedef enum
{
    ENC_ALG_TKIP,
    ENC_ALG_AES,
    ENC_ALG_TKIP_AES_AUTO
}EncryptionAlgorithm;

typedef struct
{
    unsigned short year;
    jboolean  month;
    jboolean  day;
    jboolean  hour;
    jboolean  min;
    jboolean  sec;
    jboolean  resv;
} Date;

typedef struct
{
    jboolean  digit[PIXCELL_MAX_MCC_DIGIT];
    jboolean  resv;
} Mcc;

typedef struct
{
    jboolean  num;
    jboolean  digit[PIXCELL_MAX_MNC_DIGIT];
} Mnc;

typedef struct
{
    Mcc mcc;
    Mnc mnc;
} Plmn,*pPlmn;

typedef struct
{
    unsigned short earfcnDl;
    unsigned short earfcnUl;
} Earfcn;

typedef struct
{
    unsigned short arfcnDl;
    unsigned short arfcnUl;
} Arfcn;


typedef struct
{
    jboolean  num;
    jboolean  resv[3];
    Earfcn         earfcn[PIXCELL_MAX_EARFCN_LST];
} EarfcnLst;

typedef struct
{
    jboolean num;
    jboolean digit[PIXCELL_MAX_IMSI_DIGIT];
} Imsi;

typedef struct
{
    jboolean num;
    jboolean resv[3];
    Imsi          imsi[PIXCELL_MAX_IMSI_LST];
} ImsiLst;

typedef struct
{
    jboolean digit[PIXCELL_MAX_IMEI_DIGIT];
    jboolean resv;
} Imei;

typedef struct
{
    jboolean num;
    jboolean resv[3];
    Imei imei[PIXCELL_MAX_IMEI_LST];
} ImeiLst;

typedef struct
{
    jboolean    ratMode;
    jboolean    band;
    jboolean    resv[2];
    unsigned short   channel;
} CleanChannel;

typedef struct
{
    jboolean  ratType;
    unsigned long  enbId;
    unsigned long  cellId;
    unsigned short earfcnDl;
    unsigned short pci;
    unsigned short tac;
} NeighCell;

typedef struct
{
    jboolean num;
    jboolean resv[3];
    NeighCell     neighCell[PIXCELL_MAX_NEIGH_CELL_LST];
} NeighCellLst;

typedef struct
{
    jboolean ratType;
    unsigned long eNbId;
    unsigned long cellId;
    unsigned short channel;
    unsigned short pci;
    unsigned short tac;
    unsigned short psc;
    unsigned short lac;
}SibCell;

typedef struct
{
    jboolean num;
    jboolean resv[3];
    SibCell       sibCell[PIXCELL_MAX_SIB_CELL_LST];
}SibCellLst;

typedef struct
{
    unsigned long  eNbId;
    unsigned long  cellId;
    Plmn           plmn;
    unsigned short tac;
    unsigned short pci;
    int            signal;
    SibCellLst     sibCellLst;
} CellInfo;

typedef struct
{
    jboolean num;
    jboolean resv[3];
    CellInfo      cellInfo[PIXCELL_MAX_CELL_INFO_LST];
} CellLst;

typedef struct
{
    jboolean num;
    jboolean resv[3];
    jboolean octet[PIXCELL_MAX_XRES_OCTET];
} Xres;

typedef struct
{
    jboolean  rand[PIXCELL_MAX_RAND_OCTET];
    jboolean  autn[PIXCELL_MAX_AUTN_OCTET];
    jboolean  kasme[PIXCELL_MAX_KASME_OCTET];
    Xres    xres;
} EpcAv;

typedef struct
{
    jboolean num;
    jboolean digit[PIXCELL_MAX_SMS_SENDER_DIGIT];
} SmsSender;

typedef struct
{
    jboolean len;
    jboolean resv[3];
    char          data[PIXCELL_MAX_SMS_DATA_LEN];
} SmsData;


typedef struct
{
    struct
    {
        jboolean imeiPres:1;
    }pres;
    jboolean resv[3];
    int           measRpt;
    Imsi          imsi;
    Imei          imei;
}ScannerRpt;

typedef struct
{
    jboolean rand[PIXCELL_MAX_RAND_OCTET];
    jboolean auts[PIXCELL_MAX_AUTS_OCTET];
    jboolean resv[2];
}AuthReSynchInfo;
#endif
