#include <string.h>
#include <jni.h>
#include "log.h"
#include "pixcell_hss.h"

#ifndef NELEM
# define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#endif

#define TEMP_NUM 32
void HssParser(JNIEnv* env, jclass clazz, jobject hssObj, jboolean bResync, pHssUeCtxt phss) {

    jboolean cTemp[TEMP_NUM];
    jstring strTemp;
    jfieldID fid;
    jboolean c[2];
    int i;
    //amf
    memset(cTemp, 0, TEMP_NUM);
    fid = (*env)->GetFieldID(env, clazz, "amf", "Ljava/lang/String;");
    strTemp = (*env)->GetObjectField(env, hssObj, fid);
    strncpy(cTemp, (*env)->GetStringUTFChars(env, strTemp, NULL), MAX_HSS_AMF_LEN*2);
    for(i = 0; i < MAX_HSS_AMF_LEN; i++)
    {
        sscanf(&cTemp[2 * i], "%02x", c);
        phss->amf[i] = c[0];
        LOGE("phss->amf[%d]=0x%02x", i, phss->amf[i]);
    }
    //op
    /*memset(cTemp, 0, TEMP_NUM);
    fid = (*env)->GetFieldID(env, clazz, "op", "Ljava/lang/String;");
    strTemp = (*env)->GetObjectField(env, hssObj, fid);
    strncpy(cTemp, (*env)->GetStringUTFChars(env, strTemp, NULL), MAX_HSS_OP_LEN*2);
    for(i = 0; i < MAX_HSS_OP_LEN; i++)
    {
        sscanf(&cTemp[2 * i], "%02x", c);
        phss->op[i] = c[0];
        //LOGE("phss->op[%d]=0x%02x", i, phss->op[i]);
    }*/
    //autnk
    memset(cTemp, 0, TEMP_NUM);
    fid = (*env)->GetFieldID(env, clazz, "authK", "Ljava/lang/String;");
    strTemp = (*env)->GetObjectField(env, hssObj, fid);
    strncpy(cTemp, (*env)->GetStringUTFChars(env, strTemp, NULL), MAX_HSS_AUTHK_LEN*2);
    for(i = 0; i < MAX_HSS_AUTHK_LEN; i++)
    {
        sscanf(&cTemp[2 * i], "%02x", c);
        phss->authK[i] = c[0];
        //LOGE("phss->authK[%d]=0x%02x", i, phss->authK[i]);
    }
    if  (bResync) {
            //auts
            memset(cTemp, 0, TEMP_NUM);
            fid = (*env)->GetFieldID(env, clazz, "Resyncauts", "Ljava/lang/String;");
            strTemp = (*env)->GetObjectField(env, hssObj, fid);
            strncpy(cTemp, (*env)->GetStringUTFChars(env, strTemp, NULL), PIXCELL_MAX_AUTS_OCTET*2);
            for(i = 0; i < PIXCELL_MAX_AUTS_OCTET; i++)
            {
                sscanf(&cTemp[2 * i], "%02x", c);
                phss->Resyncauts[i] = c[0];
                //LOGE("phss->Resyncauts[%d]=0x%02x", i, phss->Resyncauts[i]);
            }
            //resyncrand
            memset(cTemp, 0, TEMP_NUM);
            fid = (*env)->GetFieldID(env, clazz, "Resyncrand", "Ljava/lang/String;");
            strTemp = (*env)->GetObjectField(env, hssObj, fid);
            strncpy(cTemp, (*env)->GetStringUTFChars(env, strTemp, NULL), PIXCELL_MAX_RAND_OCTET*2);
            for(i = 0; i < PIXCELL_MAX_RAND_OCTET; i++)
            {
                sscanf(&cTemp[2 * i], "%02x", c);
                phss->Resyncrand[i] = c[0];
                //LOGE("phss->Resyncrand[%d]=0x%02x", i, phss->Resyncrand[i]);
            }
            //rand
            memset(cTemp, 0, TEMP_NUM);
            fid = (*env)->GetFieldID(env, clazz, "checkrand", "Ljava/lang/String;");
            strTemp = (*env)->GetObjectField(env, hssObj, fid);
            strncpy(cTemp, (*env)->GetStringUTFChars(env, strTemp, NULL), MAX_HSS_RAND_LEN*2);
            for(int i = 0; i < MAX_HSS_RAND_LEN; i++) {
                 sscanf(&cTemp[2 * i], "%02x", c);
                 phss->rand[i] = c[0];
                 //LOGE("phss->rand[%d]=0x%02x", i, phss->rand[i]);
            }
     } else {
     	    //authsqn
            fid = (*env)->GetFieldID(env, clazz, "authSqn", "Ljava/lang/String;");
            strTemp = (*env)->GetObjectField(env, hssObj, fid);
            strncpy(cTemp, (*env)->GetStringUTFChars(env, strTemp, NULL), MAX_HSS_SQN_LEN*2);
            if (strlen(cTemp) > 0) {
                for(int i = 0; i < MAX_HSS_SQN_LEN; i++) {
                     sscanf(&cTemp[2 * i], "%02x", c);
                     phss->sqn[i] = c[0];
                     LOGE("phss->authsqn[%d]=0x%02x", i, phss->sqn[i]);
                }
            }
     }

}

JNIEXPORT jint JNICALL
Java_com_magnet_socket_SocketTCP_PixcellAuthDataReqProc(JNIEnv* env,  jobject obj, jobject hssObj, jboolean bResync) {
    pHssUeCtxt phss = malloc(sizeof(HssUeCtxt));
    pPlmn pplmn = malloc(sizeof(Plmn));
	memset(phss, 0, sizeof(HssUeCtxt));
	memset(pplmn, 0, sizeof(Plmn));
    jclass clazz;
    jfieldID fid;
    jstring strTemp;
    int i;
    // mapping bar of C to struct
    clazz = (*env)->GetObjectClass(env, hssObj);
    if (0 == clazz) {
        LOGE("GetObjectClass returned 0");
        return false;
    }
    //

    HssParser(env, clazz, hssObj, bResync, phss);
    //imsi
    fid = (*env)->GetFieldID(env, clazz, "imsi", "Ljava/lang/String;");
    strTemp = (*env)->GetObjectField(env, hssObj, fid);
    strcpy(phss->imsi, (*env)->GetStringUTFChars(env, strTemp, NULL));
	for(i = 0; i < MAX_HSS_IMSI_LEN; i++) {
	    phss->imsi[i] = phss->imsi[i] - '0';
	}
    //imsi len
    phss->imsiLen = MAX_HSS_IMSI_LEN;//strlen(phss->imsi);
    //fid = (*env)->GetFieldID(env, clazz, "imsiLen", "I");
    //(*env)->SetIntField(env, hssObj, fid, phss->imsiLen);
    //snid
    fid = (*env)->GetFieldID(env, clazz, "snid", "Ljava/lang/String;");
    strTemp = (*env)->GetObjectField(env, hssObj, fid);
    jboolean jsnid[7];

    strcpy(jsnid, (*env)->GetStringUTFChars(env, strTemp, NULL));
    int iLen = strlen(jsnid);

    for(i = 0; i < iLen; i ++)
    {
        if((jsnid[i] < '0') || (jsnid[i] > '9'))
        {
        }else if(i < 3)
        {
            pplmn->mcc.digit[i] = jsnid[i] - '0';
             //LOGE("mcc[%d]=%d", i, pplmn->mcc.digit[i]);
        }
        else
        {
            pplmn->mnc.digit[i - 3] = jsnid[i] - '0';
            //LOGE("mnc[%d]=%d", i-3, pplmn->mnc.digit[i - 3]);
        }
    }
    pplmn->mnc.num = (iLen == (6) ? 3 : 2);



    if (bResync) {
        HssAuthResync(phss, phss->Resyncrand, phss->Resyncauts);
    }

    HssAuthDataGenerate(phss, pplmn);
    jbyte *jb;
    //rand
    fid = (*env)->GetFieldID(env, clazz, "rand", "[B");
    jbyteArray jbyterand = (*env)->NewByteArray(env, MAX_HSS_RAND_LEN);
    jb = (*env)->GetByteArrayElements(env, jbyterand, 0);
    memcpy(jb, phss->rand, MAX_HSS_RAND_LEN);
    (*env)->SetByteArrayRegion(env, jbyterand, 0, MAX_HSS_RAND_LEN, jb);
    (*env)->SetObjectField(env, hssObj, fid, jbyterand);
    //sres
    fid = (*env)->GetFieldID(env, clazz, "sres", "[B");
    jbyteArray jbytesres = (*env)->NewByteArray(env, MAX_HSS_SRES_LEN);
    jb = (*env)->GetByteArrayElements(env, jbytesres, 0);
    memcpy(jb, phss->sres, MAX_HSS_SRES_LEN);
    (*env)->SetByteArrayRegion(env, jbytesres, 0, MAX_HSS_SRES_LEN, jb);
    (*env)->SetObjectField(env, hssObj, fid, jbytesres);
    //ik
    fid = (*env)->GetFieldID(env, clazz, "ik", "[B");
    jbyteArray jbyteik = (*env)->NewByteArray(env, MAX_HSS_IK_LEN);
    jb = (*env)->GetByteArrayElements(env, jbyteik, 0);
    memcpy(jb, phss->ik, MAX_HSS_IK_LEN);
    (*env)->SetByteArrayRegion(env, jbyteik, 0, MAX_HSS_IK_LEN, jb);
    (*env)->SetObjectField(env, hssObj, fid, jbyteik);
    //ck
    fid = (*env)->GetFieldID(env, clazz, "kc", "[B");
    jbyteArray jbyteck = (*env)->NewByteArray(env, MAX_HSS_CK_LEN);
    jb = (*env)->GetByteArrayElements(env, jbyteck, 0);
    memcpy(jb, phss->ck, MAX_HSS_CK_LEN);
    (*env)->SetByteArrayRegion(env, jbyteck, 0, MAX_HSS_CK_LEN, jb);
    (*env)->SetObjectField(env, hssObj, fid, jbyteck);
    //ak
    fid = (*env)->GetFieldID(env, clazz, "ak", "[B");
    jbyteArray jbyteak = (*env)->NewByteArray(env, MAX_HSS_AK_LEN);
    jb = (*env)->GetByteArrayElements(env, jbyteak, 0);
    memcpy(jb, phss->ak, MAX_HSS_AK_LEN);
    (*env)->SetByteArrayRegion(env, jbyteak, 0, MAX_HSS_AK_LEN, jb);
    (*env)->SetObjectField(env, hssObj, fid, jbyteak);
    //sqn
    fid = (*env)->GetFieldID(env, clazz, "sqn", "[B");
    jbyteArray jbytesqn = (*env)->NewByteArray(env, MAX_HSS_SQN_LEN);
    jb = (*env)->GetByteArrayElements(env, jbytesqn, 0);
    memcpy(jb, phss->sqn, MAX_HSS_SQN_LEN);
    (*env)->SetByteArrayRegion(env, jbytesqn, 0, MAX_HSS_SQN_LEN, jb);
    (*env)->SetObjectField(env, hssObj, fid, jbytesqn);
    //kasme
    fid = (*env)->GetFieldID(env, clazz, "kasme", "[B");
    jbyteArray jbytekasme = (*env)->NewByteArray(env, MAX_HSS_KASME_LEN);
    jb = (*env)->GetByteArrayElements(env, jbytekasme, 0);
    memcpy(jb, phss->kasme, MAX_HSS_KASME_LEN);
    (*env)->SetByteArrayRegion(env, jbytekasme, 0, MAX_HSS_KASME_LEN, jb);
    (*env)->SetObjectField(env, hssObj, fid, jbytekasme);
    //autn
    fid = (*env)->GetFieldID(env, clazz, "autn", "[B");
    jbyteArray jbyteautn = (*env)->NewByteArray(env, MAX_HSS_AUTN_LEN);
    jb = (*env)->GetByteArrayElements(env, jbyteautn, 0);
    memcpy(jb, phss->autn, MAX_HSS_AUTN_LEN);
    (*env)->SetByteArrayRegion(env, jbyteautn, 0, MAX_HSS_AUTN_LEN, jb);
    (*env)->SetObjectField(env, hssObj, fid, jbyteautn);
    //tmsi
    fid = (*env)->GetFieldID(env, clazz, "tmsi", "[B");
    jbyteArray jbytetmsi = (*env)->NewByteArray(env, MAX_HSS_TMSI_LEN);
    jb = (*env)->GetByteArrayElements(env, jbytetmsi, 0);
    memcpy(jb, phss->tmsi, MAX_HSS_TMSI_LEN);
    (*env)->SetByteArrayRegion(env, jbytetmsi, 0, MAX_HSS_TMSI_LEN, jb);
    (*env)->SetObjectField(env, hssObj, fid, jbytetmsi);

    free(phss);
    phss = NULL;
    free(pplmn);
    pplmn = NULL;
    return true;
}
/*

static int android_mcu_setup(JNIEnv* env,  jobject obj,
		jint fd, jint baudrate, jint nbits, jchar parary, jint stopbits) {
	LOGE("init CommPort Configure!");
	return 0;
}
//д����
static int android_mcu_write(JNIEnv* env,  jobject obj,
		jint fd, jbyteArray send_data, jint s_num) {
	return 0;
}
//������
static int android_mcu_read(JNIEnv* env,  jobject obj,
		jint fd, jbyteArray msg_data, jint size, jint rcv_wait) {
	return 0;
}
//�رմ����豸
static void android_mcu_close(JNIEnv* env,  jobject obj, jint fd){
	close(fd);
	LOGE("Serial close success /n");
}*/
//����JNINativeMethod����,native������jni����һһ��Ӧ
/*
static const JNINativeMethod gMethods[] = {
		{"jni_auth",			"()I",		(int *)PixcellAuthDataReqProc},
		{"jni_mcu_serial_setup",		"(IIICI)I",	(int *)android_mcu_setup},
		{"jni_mcu_serial_write_data",	"(I[BI)I",	(int *)android_mcu_write},
		{"jni_mcu_serial_read_data",	"(I[BII)I",	(int *)android_mcu_read},
		{"jni_mcu_serial_close",		"(I)V",		(void *)android_mcu_close}
};*/
//ע��native����
/*
int RegisterNativeMethods(JNIEnv *env) {
	//ע�᱾�ط���.Load Ŀ����
	jclass clazz = (*env)->FindClass(env,classPathName);
    if (clazz == NULL)
    {
        LOGE("Native registration unable to find class '%s'", classPathName);
        return JNI_ERR;
    }
    //ע�᱾��native����
    if((*env)->RegisterNatives(env, clazz, gMethods, NELEM(gMethods)) < 0)
    {
        LOGE("ERROR: MediaPlayer native registration failed\n");
        return JNI_ERR;
    }
}*/
//System.loadLibrary("CommPort_jni");�Զ����øú���
/*
jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;
    if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        LOGE("ERROR: GetEnv failed\n");
        return JNI_ERR;
    }
    RegisterNativeMethods(env);

    return JNI_VERSION_1_4;
}*/
