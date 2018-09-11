#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <string.h>
#include "pixcell_hss.h"
#include "pixcell_if.h"
#include "sha.h"
#include "alg5.h"
#include "log.h"

void HssGenerateRand(jboolean *pRand)
{

    int tempRand;
    int i;

    srand(time(NULL));

    for(i = 0; i < MAX_HSS_RAND_LEN; i ++)
    {
        tempRand = rand();
        pRand[i] = (jboolean)tempRand;
		//LOGE("rand[%d]=0x%02x", i, pRand[i]);
    }


    /*pRand[0] = 0x52;
    pRand[1] = 0x6d;
    pRand[2] = 0xe2;
    pRand[3] = 0x2a;
    pRand[4] = 0xe5;
    pRand[5] = 0xce;
    pRand[6] = 0xaf;
    pRand[7] = 0xd6;
    pRand[8] = 0x7b;
    pRand[9] = 0xbe;
    pRand[10] = 0x82;
    pRand[11] = 0x97;
    pRand[12] = 0xd2;
    pRand[13] = 0x2d;
    pRand[14] = 0x09;
    pRand[15] = 0x9d;*/
    
}

void HssGenerateSqn(jboolean *pSqn)
{
    unsigned long long ll;

    ll = pSqn[0];
    ll = (ll << 8) | pSqn[1];
    ll = (ll << 8) | pSqn[2];
    ll = (ll << 8) | pSqn[3];
    ll = (ll << 8) | pSqn[4];
    ll = (ll << 3) | (pSqn[5] >> 5);

    ll++;

    pSqn[0] = ll >> 35;
    pSqn[1] = ll >> 27;
    pSqn[2] = ll >> 19;
    pSqn[3] = ll >> 11;
    pSqn[4] = ll >> 3;
    pSqn[5] = ((ll & 0x7) << 5) | (pSqn[5] & 0x1f);
}


int HssCmpUeImsi64(const void *a, const void *b)
{
    if(((const HssUeCtxt *)a)->imsiValue > ((const HssUeCtxt *)b)->imsiValue)
    {
        return 1;
    }
    else if(((const HssUeCtxt *)a)->imsiValue == ((const HssUeCtxt *)b)->imsiValue)
    {
        return 0;
    }
    else
    {
        return -1;
    }
}

void HssImsiDigitsToInt64(jboolean *pImsi, jboolean len, long long *pInt64)
{
    int i = 0;
    long long temp = 0;
    char imsiStr[16];

    for(i = 0; i < len; i ++)
    {
        temp = (temp * 10) + pImsi[i];
    }

    *pInt64 = temp;

    for(i = 0; i < len; i ++)
    {
        sprintf(&imsiStr[i], "%d", pImsi[i]);
    }

    LOGE("IMSI %s to Int64 %llu", imsiStr, temp);
}

int HssKDFasmeEncodePlmn(jboolean *pKasme, Plmn *pPlmn)
{
    pKasme[0] = pPlmn->mcc.digit[0] + (pPlmn->mcc.digit[1] << 4);
    if(pPlmn->mnc.num == 2)
    {
        pKasme[1] = pPlmn->mcc.digit[2] + 0xF0;
    }
    else
    {
        pKasme[1] = pPlmn->mcc.digit[2] + (pPlmn->mnc.digit[2] << 4);
    }

    pKasme[2] = pPlmn->mnc.digit[0] + (pPlmn->mnc.digit[1] << 4);

    return ROK;
}

int HssKDFasme(HssUeCtxt *pUeCtxt, Plmn *pPlmn)
{
    int i;
    jboolean bInputKey[32];
    jboolean bInputS[14];
    /*3GPP 33-401 A.2*/
    memcpy(bInputKey, pUeCtxt->ck, MAX_HSS_CK_LEN);
    memcpy(&bInputKey[MAX_HSS_CK_LEN], pUeCtxt->ik, MAX_HSS_IK_LEN);

    bInputS[0] = 0x10;

    HssKDFasmeEncodePlmn(&bInputS[1], pPlmn);
    bInputS[4] = 0x00;
    bInputS[5] = 0x03;

    for(i = 0; i < 6; i ++)
    {
        bInputS[6 + i] = (pUeCtxt->sqn[i] ^ pUeCtxt->ak[i]);
    }

    bInputS[12] = 0x00;
    bInputS[13] = 0x06;

    hmac(SHA256, bInputS, 14, bInputKey, 32, pUeCtxt->kasme);
    return ROK;
}

int HssAuthDataGenerate(HssUeCtxt *pUeCtxt, Plmn *pPlmn)
{
    jboolean mac_a[8];

    /* Generate Rand Value */
    HssGenerateRand(pUeCtxt->rand);
	 /*for(int i = 0; i < MAX_HSS_RAND_LEN; i++) {
         LOGE("pUeCtxt->rand[%d]=0x%02x", i, pUeCtxt->rand[i]);
    }*/
    /* Generate SRES, CK, IK, AK by Key and Rand  */
    f2345(pUeCtxt->authK, pUeCtxt->rand, pUeCtxt->sres, pUeCtxt->ck, pUeCtxt->ik, pUeCtxt->ak);
	
    /* Autn = SQN-AK||AMF||MAC-A*/
    /* Generate SQN */
	jboolean authsqn[MAX_HSS_SQN_LEN];
	for (int i = 0; i < MAX_HSS_SQN_LEN; i++) {
		authsqn[i] = pUeCtxt->sqn[i];
	}
	LOGE("Sqn old 0x%02x%02x%02x%02x%02x%02x", pUeCtxt->sqn[0], pUeCtxt->sqn[1], pUeCtxt->sqn[2], pUeCtxt->sqn[3], pUeCtxt->sqn[4], pUeCtxt->sqn[5]);
    HssGenerateSqn(pUeCtxt->sqn);
    LOGE("Sqn new 0x%02x%02x%02x%02x%02x%02x", pUeCtxt->sqn[0], pUeCtxt->sqn[1], pUeCtxt->sqn[2], pUeCtxt->sqn[3], pUeCtxt->sqn[4], pUeCtxt->sqn[5]);

    /* Generate MAC-A */
    bzero(mac_a, 8);
    f1(pUeCtxt->authK, pUeCtxt->rand, pUeCtxt->sqn, pUeCtxt->amf, mac_a);
	
    /* Generate Autn */
    fautn(pUeCtxt->sqn, pUeCtxt->ak, pUeCtxt->amf, mac_a, pUeCtxt->autn);
    HssKDFasme(pUeCtxt, pPlmn);
	for (int i = 0; i < MAX_HSS_SQN_LEN; i++) {
			pUeCtxt->sqn[i] = authsqn[i];
	}
	LOGE("auth Sqn 0x%02x%02x%02x%02x%02x%02x", pUeCtxt->sqn[0], pUeCtxt->sqn[1], pUeCtxt->sqn[2], pUeCtxt->sqn[3], pUeCtxt->sqn[4], pUeCtxt->sqn[5]);

    return ROK;
}

int HssAuthResync(HssUeCtxt *pUeCtxt, jboolean *pRand, jboolean *pAuts)
{
    int i ;
    jboolean ak_star[6];
    jbyte akStr[16];
    f5star(pUeCtxt->authK, pUeCtxt->rand, ak_star);
    for (i = 0; i < 6; i ++)
    {
        sprintf (&akStr[i], "%x", pUeCtxt->ak[i]);
    }

    LOGE ("AK %s", akStr);

    for (i = 0; i < 6; i ++)
    {
        sprintf (&akStr[i], "%x", ak_star[i]);
    }

    LOGE ("AK_Star %s", akStr);    

    for( i = 0; i < MAX_HSS_SQN_LEN; i ++ )
    {
        pAuts[i] = pAuts[i] ^ ak_star[i];
    }

    memcpy(pUeCtxt->sqn, pAuts, MAX_HSS_SQN_LEN);

    for (i = 0; i < MAX_HSS_SQN_LEN; i ++)
    {
        sprintf (&akStr[2*i], "%02x", pUeCtxt->sqn[i]);
    }

    LOGE ("sqn %s", akStr);    

    return ROK;

}

