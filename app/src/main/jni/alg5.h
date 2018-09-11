#ifndef _ALG5_H_
#define _ALG5_H_
#include <jni.h>

void f1 ( unsigned char k[16], unsigned char rand[16], unsigned char sqn[6], unsigned char amf[2],
      unsigned char mac_a[8] );
void f2345 ( unsigned char k[16], unsigned char rand[16],
         jboolean res[8], unsigned char ck[16], unsigned char ik[16], unsigned char ak[6] );
void f1star( unsigned char k[16], unsigned char rand[16], unsigned char sqn[6], unsigned char amf[2],
         unsigned char mac_s[8]);
void f5star( unsigned char k[16], unsigned char rand[16],
         unsigned char ak[6] );
void ComputeOPc( unsigned char op_c[16] );
void RijndaelKeySchedule( unsigned char key[16] );
void RijndaelEncrypt( unsigned char input[16], unsigned char output[16] );
void c2( unsigned char xres[8], unsigned char sres[4] );
void c3( unsigned char ck[16], unsigned char ik[16], unsigned char kc[8] );
void fautn(unsigned char sqn[6], unsigned char ak[6], unsigned char amf[2], unsigned char mac_a[8], unsigned char autn[16]);
void fauts(unsigned char auts[14], unsigned char sqn[6], unsigned char ak[6], unsigned char mac_s[8]);

#endif
