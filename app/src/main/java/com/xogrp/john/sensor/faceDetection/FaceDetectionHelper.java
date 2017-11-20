package com.xogrp.john.sensor.faceDetection;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.util.Log;

/**
 * Created by Administrator on 2017/11/18.
 */

public class FaceDetectionHelper {
    private static final String TAG = "ziq";
    private int mMaxFaces = 3;
    private int mHeadMultiple = 1;

    public CropResult cropBitmapByFace(Bitmap originalBitmap, float cropWidth, float cropHeight, int mMaxFaces, int headMultiple){
        this.mMaxFaces = mMaxFaces;
        // 默认1倍， 刚好一个头的长宽。
        if(headMultiple > 0){
            this.mHeadMultiple = headMultiple;
        }
        return cropBitmapByFace(originalBitmap, cropWidth, cropHeight);
    }

    public CropResult cropBitmapByFace(Bitmap originalBitmap, float cropWidth, float cropHeight){

        //没有这个。识别不正确
        Bitmap bitmapTarget = originalBitmap.copy(Bitmap.Config.RGB_565, true);

        if(!originalBitmap.isRecycled()){
            originalBitmap.recycle();
        }

        Bitmap mutableBitmap = forceConfig565(forceBitmapSize(bitmapTarget));
        //FaceDetector API 要求bitmap 满足 宽高都是双数、 Bitmap.Config.RGB_565
        FaceDetector.Face[] faces = getFaces(mutableBitmap, mMaxFaces);
        int faceCount = faces.length;
        Log.e(TAG, "cropBitmapByFace: faceCount = "+faceCount);

        if (faceCount == 0) {
            return new CropResult(mutableBitmap);
        }

        // 整个头部 大概 是eyesDistance 的 3-4 倍。
        //计算出现的头 的大致区域
        Area facesArea = new Area();
        for (int i = 0; i < faceCount; i++) {
            FaceDetector.Face face = faces[i];
            PointF faceCenterPoint = new PointF();
            face.getMidPoint(faceCenterPoint);
            facesArea.setLeft(faceCenterPoint.x - face.eyesDistance() * 2 * mHeadMultiple, 0);
            facesArea.setTop(faceCenterPoint.y - face.eyesDistance() * 2 * mHeadMultiple, 0);
            facesArea.setRight(faceCenterPoint.x + face.eyesDistance() * 2 * mHeadMultiple, mutableBitmap.getWidth());
            facesArea.setBottom(faceCenterPoint.y + face.eyesDistance() * 2 * mHeadMultiple, mutableBitmap.getHeight());
        }

        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawBitmap(mutableBitmap, new Matrix(), null);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAlpha(80);
        canvas.drawRect(facesArea.getLeft(), facesArea.getTop(), facesArea.getRight(), facesArea.getBottom(), paint);
        Log.e(TAG, "facesArea 1 = "+facesArea);

        // 按 想要的长 宽， 适应的调整所想要的图像大小。

        float bitmapAspectRatio = ((float) mutableBitmap.getWidth()) / ((float)mutableBitmap.getHeight());
        float cropAspectRatio = cropWidth / cropHeight;
        float faceAreaWidth = facesArea.getRight() - facesArea.getLeft();
        float faceAreaHeight = facesArea.getBottom() - facesArea.getTop();
        float faceAspectRatio = faceAreaWidth / faceAreaHeight;

        if(cropAspectRatio < faceAspectRatio){
            if(cropWidth < faceAreaWidth){
                cropWidth = faceAreaWidth;
                cropHeight = cropWidth / cropAspectRatio;
            }
        }else if(cropAspectRatio > faceAspectRatio){
            if(cropHeight < faceAreaHeight ){
                cropHeight = faceAreaHeight;
                cropWidth = cropHeight * cropAspectRatio;
            }
        }

        if(cropAspectRatio < bitmapAspectRatio){
            if(cropHeight > mutableBitmap.getHeight() ){
                cropHeight = mutableBitmap.getHeight();
                cropWidth = cropHeight * cropAspectRatio;
            }
        }else if(cropAspectRatio > bitmapAspectRatio){
            if(cropWidth > mutableBitmap.getWidth() ){
                cropWidth = mutableBitmap.getWidth();
                cropHeight = cropWidth / cropAspectRatio;
            }
        }



        float areaHeight = facesArea.getBottom() - facesArea.getTop();
        if(cropHeight != areaHeight){
            if(cropHeight > areaHeight){
                float dy = cropHeight - areaHeight;
                float remainTop = facesArea.getTop() - 0;
                float remainBottom = mutableBitmap.getHeight() - facesArea.getBottom();
                if (dy >= remainTop + remainBottom){
                    facesArea.setTop(0,0);
                    facesArea.setBottom(mutableBitmap.getHeight(), mutableBitmap.getHeight());
                }else{
                    float halfDyTop = dy / 2;
                    float halfDyBottom = dy / 2;
                    if(halfDyTop >= remainTop){
                        halfDyTop = halfDyTop - remainTop;
                        facesArea.setTop(0,0);
                        facesArea.setBottom(facesArea.getBottom() + halfDyTop + halfDyBottom, mutableBitmap.getHeight());
                    }else{
                        facesArea.setTop(facesArea.getTop() - halfDyTop,0);
                        remainTop = remainTop - halfDyTop;
                        halfDyTop = 0;
                        if(halfDyBottom >= remainBottom){
                            facesArea.setBottom(mutableBitmap.getHeight(), mutableBitmap.getHeight());
                            halfDyBottom = halfDyBottom - remainBottom;
                            facesArea.setTop(facesArea.getTop() - halfDyBottom,0);
                        }else{
                            facesArea.setBottom(facesArea.getBottom() + halfDyBottom, mutableBitmap.getHeight());
                        }
                    }
                }
            }else{
                float dy = areaHeight - cropHeight;
                float halfDy = dy / 2;
                facesArea.setTop(facesArea.getTop() + halfDy);
                facesArea.setBottom(facesArea.getBottom() - halfDy);
            }
        }

        float areaWidth = facesArea.getRight() - facesArea.getLeft();
        if(cropWidth != areaWidth){
            if(cropWidth > areaWidth){
                float dx = cropWidth - areaWidth;
                float remainLeft = facesArea.getLeft() - 0;
                float remainRight = mutableBitmap.getWidth() - facesArea.getRight();
                if (dx >= remainLeft + remainRight){
                    facesArea.setLeft(0,0);
                    facesArea.setRight(mutableBitmap.getWidth(), mutableBitmap.getWidth());
                }else{
                    float halfDxLeft = dx / 2;
                    float halfDxRight = dx / 2;
                    if(halfDxLeft >= remainLeft){
                        halfDxLeft = halfDxLeft - remainLeft;
                        facesArea.setTop(0,0);
                        facesArea.setRight(facesArea.getRight() + halfDxLeft + halfDxRight, mutableBitmap.getWidth());
                    }else{
                        facesArea.setLeft(facesArea.getLeft() - halfDxLeft,0);
                        remainLeft = remainLeft - halfDxLeft;
                        halfDxLeft = 0;
                        if(halfDxRight >= remainRight){
                            facesArea.setRight(mutableBitmap.getWidth(), mutableBitmap.getWidth());
                            halfDxRight = halfDxRight - remainRight;
                            facesArea.setLeft(facesArea.getLeft() - halfDxRight,0);
                        }else{
                            facesArea.setRight(facesArea.getRight() + halfDxRight, mutableBitmap.getWidth());
                        }
                    }
                }
            }else{
                float dx = areaWidth - cropWidth;
                float halfDx = dx / 2;
                facesArea.setLeft(facesArea.getLeft() + halfDx);
                facesArea.setRight(facesArea.getRight() - halfDx);
            }
        }

        Paint paint2 = new Paint();
        paint2.setColor(Color.GREEN);
        paint2.setAlpha(80);
        canvas.drawRect(facesArea.getLeft(), facesArea.getTop(), facesArea.getRight(), facesArea.getBottom(), paint2);
        Log.e(TAG, "facesArea 2 = "+facesArea);

        return new CropResult(mutableBitmap, facesArea);
    }

    private FaceDetector.Face[] getFaces(Bitmap mutableBitmap, int maxFaces){
        FaceDetector faceDetector = new FaceDetector(mutableBitmap.getWidth(), mutableBitmap.getHeight(), maxFaces);
        FaceDetector.Face[] faces = new FaceDetector.Face[maxFaces];
        int faceCount = faceDetector.findFaces(mutableBitmap, faces);
        FaceDetector.Face[] resultFaces = new FaceDetector.Face[faceCount];
        for (int i = 0; i < faceCount; i++) {
            resultFaces[i] = faces[i];
        }
        return resultFaces;
    }

    private Bitmap forceBitmapSize(Bitmap original) {
        int width = original.getWidth();
        int height = original.getHeight();

        if (width % 2 == 1) {
            width++;
        }
        if (height % 2 == 1) {
            height++;
        }

        Bitmap fixedBitmap = original;
        if (width != original.getWidth() || height != original.getHeight()) {
            fixedBitmap = Bitmap.createScaledBitmap(original, width, height, false);
        }

        if (fixedBitmap != original) {
            original.recycle();
        }
        return fixedBitmap;
    }

    private Bitmap forceConfig565(Bitmap original) {
        Bitmap convertedBitmap = original;
        if (original.getConfig() != Bitmap.Config.RGB_565) {
            convertedBitmap = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(convertedBitmap);
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            canvas.drawBitmap(original, 0, 0, paint);

            if (convertedBitmap != original) {
                original.recycle();
            }
        }

        return convertedBitmap;
    }

    public void setMaxFaces(int mMaxFaces) {
        this.mMaxFaces = mMaxFaces;
    }

    public static class Area {
        float left = -1, top = -1, right = -1, bottom = -1;

        public float getLeft() {return left;}
        public float getTop() {return top;}
        public float getRight() {return right;}
        public float getBottom() {return bottom;}

        public void setLeft(float left, float minLeft) {
            if(this.left == -1 || this.left > left ){
                this.left = left < minLeft ? minLeft : left;
            }
        }
        public void setTop(float top, float minTop) {
            if(this.top == -1 || this.top > top ){
                this.top = top < minTop ? minTop : top;
            }
        }
        public void setRight(float right, float maxRight) {
            if(this.right == -1 || this.right < right ){
                this.right = right > maxRight ? maxRight : right;
            }
        }
        public void setBottom(float bottom, float maxBottom) {
            if(this.bottom == -1 || this.bottom < bottom ){
                this.bottom = bottom > maxBottom ? maxBottom : bottom;
            }
        }

        public void setLeft(float left) {
            this.left = left;
        }

        public void setTop(float top) {
            this.top = top;
        }

        public void setRight(float right) {
            this.right = right;
        }

        public void setBottom(float bottom) {
            this.bottom = bottom;
        }

        @Override
        public String toString() {
            return "left = "+left+" top = "+top+" right = "+right+" bottom = "+bottom;
        }
    }

    public static class CropResult {
        Bitmap mBitmap;
        Area mArea;

        public CropResult(Bitmap bitmap, Area area) {
            mBitmap = bitmap;
            mArea = area;
        }

        public CropResult(Bitmap bitmap) {
            mBitmap = bitmap;
            mArea = new Area();
            mArea.setLeft(0);
            mArea.setTop(0);
            mArea.setRight(bitmap.getWidth());
            mArea.setBottom(bitmap.getHeight());
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }

        public Area getArea() {
            return mArea;
        }
    }
}
