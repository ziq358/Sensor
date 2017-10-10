#include <jni.h>
#include <string>
#include <math.h>
#include <malloc.h>
#include <android/bitmap.h>

#define PI 3.1415926
void gaussBlur(int* pix, int w, int h, int radius);

extern "C"
JNIEXPORT jstring JNICALL
Java_com_xogrp_john_myjni_NdkUtil_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from NdkUtil";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_xogrp_john_myjni_NdkUtil_gaussBlur(JNIEnv *env, jobject /* this */, jobject bmp) {
    AndroidBitmapInfo info = {0};//初始化BitmapInfo结构体
    int *data=NULL;//初始化Bitmap图像数据指针
    AndroidBitmap_getInfo(env, bmp, &info);
    AndroidBitmap_lockPixels(env, bmp, (void **) &data);//锁定Bitmap，并且获得指针
    /**********高斯模糊算法作对int数组进行处理***********/
    //调用gaussBlur函数，把图像数据指针、图片长宽和模糊半径传入
    gaussBlur(data,info.width,info.height,80);
    /****************************************************/
    AndroidBitmap_unlockPixels(env,bmp);//解锁
}

void gaussBlur(int* pix, int w, int h, int radius)
{
    float sigma = (float) (1.0 * radius / 2.57);
    float deno  = (float) (1.0 / (sigma * sqrt(2.0 * PI)));
    float nume  = (float) (-1.0 / (2.0 * sigma * sigma));
    float* gaussMatrix = (float*)malloc(sizeof(float)* (radius + radius + 1));
    float gaussSum = 0.0;
    for (int i = 0, x = -radius; x <= radius; ++x, ++i)
    {
        float g = (float) (deno * exp(1.0 * nume * x * x));
        gaussMatrix[i] = g;
        gaussSum += g;
    }
    int len = radius + radius + 1;
    for (int i = 0; i < len; ++i)
        gaussMatrix[i] /= gaussSum;
    int* rowData  = (int*)malloc(w * sizeof(int));
    int* listData = (int*)malloc(h * sizeof(int));
    for (int y = 0; y < h; ++y)
    {
        memcpy(rowData, pix + y * w, sizeof(int) * w);
        for (int x = 0; x < w; ++x)
        {
            float r = 0, g = 0, b = 0;
            gaussSum = 0;
            for (int i = -radius; i <= radius; ++i)
            {
                int k = x + i;
                if (0 <= k && k <= w)
                {
                    //得到像素点的rgb值
                    int color = rowData[k];
                    int cr = (color & 0x00ff0000) >> 16;
                    int cg = (color & 0x0000ff00) >> 8;
                    int cb = (color & 0x000000ff);
                    r += cr * gaussMatrix[i + radius];
                    g += cg * gaussMatrix[i + radius];
                    b += cb * gaussMatrix[i + radius];
                    gaussSum += gaussMatrix[i + radius];
                }
            }
            int cr = (int)(r / gaussSum);
            int cg = (int)(g / gaussSum);
            int cb = (int)(b / gaussSum);
            pix[y * w + x] = cr << 16 | cg << 8 | cb | 0xff000000;
        }
    }
    for (int x = 0; x < w; ++x)
    {
        for (int y = 0; y < h; ++y)
            listData[y] = pix[y * w + x];
        for (int y = 0; y < h; ++y)
        {
            float r = 0, g = 0, b = 0;
            gaussSum = 0;
            for (int j = -radius; j <= radius; ++j)
            {
                int k = y + j;
                if (0 <= k && k <= h)
                {
                    int color = listData[k];
                    int cr = (color & 0x00ff0000) >> 16;
                    int cg = (color & 0x0000ff00) >> 8;
                    int cb = (color & 0x000000ff);
                    r += cr * gaussMatrix[j + radius];
                    g += cg * gaussMatrix[j + radius];
                    b += cb * gaussMatrix[j + radius];
                    gaussSum += gaussMatrix[j + radius];
                }
            }
            int cr = (int)(r / gaussSum);
            int cg = (int)(g / gaussSum);
            int cb = (int)(b / gaussSum);
            pix[y * w + x] = cr << 16 | cg << 8 | cb | 0xff000000;
        }
    }
    free(gaussMatrix);
    free(rowData);
    free(listData);
}