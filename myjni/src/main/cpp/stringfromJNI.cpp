#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_xogrp_john_myjni_NdkUtil_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from NdkUtil";
    return env->NewStringUTF(hello.c_str());
}
