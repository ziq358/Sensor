#include <string.h>
#include <jni.h>
JNIEXPORT jstring JNICALL
Java_com_xogrp_john_sensor_JniTestActivity_stringFromJNI(JNIEnv *env, jobject instance) {

    return (*env)->NewStringUTF(env, "Hello from JNI !");
}

