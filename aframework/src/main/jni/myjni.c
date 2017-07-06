//
// Created by daosong on 2015/8/29.
//

#include "com_eshangke_framework_ui_activities_NDKActivity.h"
//#include <android/log.h>
//#define  LOG_TAG  "System.out"
//#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG,  __VA_ARGS__)
//#define LOGINFO(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG,  __VA_ARGS__)





JNIEXPORT jstring JNICALL Java_com_eshangke_framework_ui_activities_NDKActivity_getStringFromNative
        (JNIEnv * env, jobject jObj){
   // LOGINFO("LOGINFO");
    return (*env)->NewStringUTF(env,"NDK 测试成功");
}