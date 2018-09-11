LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE:= authentication_jni
LOCAL_SRC_FILES:= authentication_jni.c pixcell_hss.c alg.c hmac.c usha.c sha1.c sha384-512.c sha224-256.c
LOCAL_LDLIBS    :=-llog

include $(BUILD_SHARED_LIBRARY)
