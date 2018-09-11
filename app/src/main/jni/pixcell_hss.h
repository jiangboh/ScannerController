#ifndef __PIXCELL_HSS_H__
#define __PIXCELL_HSS_H__
#include <jni.h>
#include "pixcell_if.h"

#define MAX_HSS_UE_CTXT       20480

#define MAX_HSS_IMSI_LEN    15
#define MAX_HSS_TMSI_LEN    4
#define MAX_HSS_AUTHK_LEN   16
#define MAX_HSS_SRES_LEN    8
#define MAX_HSS_CK_LEN      16
#define MAX_HSS_IK_LEN      16
#define MAX_HSS_AK_LEN      6
#define MAX_HSS_SQN_LEN     6
#define MAX_HSS_RAND_LEN    16
#define MAX_HSS_KASME_LEN   32
#define MAX_HSS_AUTN_LEN    16
#define MAX_HSS_AMF_LEN     2
#define MAX_HSS_OP_LEN      16


typedef struct
{
    jboolean usedFlag;
    int imsiLen;
    jboolean imsi[MAX_HSS_IMSI_LEN];
    long long  imsiValue;
    jboolean tmsi[MAX_HSS_TMSI_LEN];
    jboolean authK[MAX_HSS_AUTHK_LEN];
    jboolean sres[MAX_HSS_SRES_LEN];
    jboolean ck[MAX_HSS_CK_LEN];
    jboolean ik[MAX_HSS_IK_LEN];
    jboolean ak[MAX_HSS_AK_LEN];
    jboolean sqn[MAX_HSS_SQN_LEN];
    jboolean rand[MAX_HSS_RAND_LEN];
    jboolean kasme[MAX_HSS_KASME_LEN];
    jboolean autn[MAX_HSS_AUTN_LEN];
	jboolean Resyncrand[PIXCELL_MAX_RAND_OCTET];
    jboolean Resyncauts[PIXCELL_MAX_AUTS_OCTET];
	jboolean amf[MAX_HSS_AMF_LEN];
    //jboolean op[MAX_HSS_OP_LEN];
}HssUeCtxt,*pHssUeCtxt;

typedef struct
{
    unsigned char amf[MAX_HSS_AMF_LEN];
    unsigned char op[MAX_HSS_OP_LEN];
    int ueNum;
    HssUeCtxt ueCtxt[MAX_HSS_UE_CTXT];
}HssCtxt;

typedef union 
{
    int i;
    unsigned char uc[4];
} sqn_union;

HssUeCtxt *HssGetUeCtxtByImsi(unsigned char *pImsi, unsigned char len);
int HssAuthDataGenerate(HssUeCtxt *pUeCtxt, Plmn *pPlmn);
int HssInitCtxt();
int HssAuthResync(HssUeCtxt *pUeCtxt, unsigned char *pRand, unsigned char *pAuts);
#endif

