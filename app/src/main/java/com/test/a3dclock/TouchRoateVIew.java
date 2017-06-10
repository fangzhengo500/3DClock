package com.test.a3dclock;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * @project： 3DClock
 * @package： com.test.a3dclock
 * @class: TouchRoateVIew
 * @author: 陆伟
 * @Date: 2017/6/10 13:17
 * @desc： TODO
 */
public class TouchRoateVIew extends ImageView {
    private static final String TAG = "TouchRoateVIew";

    private float mMaxRoateDegree = 50;

    private Camera mCamera = new Camera();
    private Matrix mMatrix = new Matrix();
    private float mXRoateDegree;
    private float mYRoateDegree;

    public TouchRoateVIew(Context context) {
        super(context);
    }

    public TouchRoateVIew(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchRoateVIew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onDraw: ");
        float x = event.getX();
        float y = event.getY();
        //invalidate();
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getCameraRotate(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                getCameraRotate(x, y);
                break;

            case MotionEvent.ACTION_UP:
                mXRoateDegree = 0;
                mYRoateDegree = 0;
                break;
        }
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(TAG, "onDraw: ");
        setCameraRoate(canvas);
        super.onDraw(canvas);
    }

    private void setCameraRoate(Canvas canvas) {
        mMatrix.reset();
        mCamera.save();
        mCamera.rotateX(-mYRoateDegree);
        mCamera.rotateY(mXRoateDegree);
        mCamera.getMatrix(mMatrix);
        mCamera.restore();

        mMatrix.preTranslate(- getWidth() / 2, - getHeight() / 2);
        mMatrix.postTranslate(getWidth() / 2,getHeight() / 2);
        canvas.concat(mMatrix);
    }


    private void getCameraRotate(float x, float y) {

        float dx = x - getWidth() / 2;
        float dy = y - getHeight() / 2;

        float xRoatePercent = dx / (getWidth() / 2);
        float yRoatePercent = dy / (getHeight() / 2);



        mXRoateDegree = xRoatePercent * mMaxRoateDegree;
        mYRoateDegree = yRoatePercent * mMaxRoateDegree;
    }



}
