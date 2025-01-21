#include <stdio.h>
#include <jni.h>



/**
 * 引起 crash
 */
void Crash() {
    volatile int *a = (int *) (NULL);
    *a = 1;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_hl_android_crash_CrashLib_testCrash(JNIEnv *env, jobject thiz) {
    Crash();
}