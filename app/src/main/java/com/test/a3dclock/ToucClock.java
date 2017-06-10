package com.test.a3dclock;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.BounceInterpolator;

/**
 * @project： 3DClock
 * @package： com.test.a3dclock
 * @class: ToucClock
 * @author: 陆伟
 * @Date: 2017/6/10 15:30
 * @desc： TODO
 */
public class ToucClock extends AClock {
    private static final String TAG = "ToucClock";

    private float mMaxRoateDegree = 10;

    private Camera mCamera = new Camera();
    private Matrix mMatrix = new Matrix();
    private float mXRoateDegree;
    private float mYRoateDegree;
    private ValueAnimator mResetAnimator;

    public ToucClock(Context context) {
        super(context);
    }

    public ToucClock(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToucClock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onDraw: ");
        float x = event.getX();
        float y = event.getY();
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                cancelResetAnimatorIfNeed();
                getCameraRotate(x, y);
                return true;

            case MotionEvent.ACTION_MOVE:
                getCameraRotate(x, y);
                return true;

            case MotionEvent.ACTION_UP:
                startNewResertAnimator();
                return true;
        }
        return super.onTouchEvent(event);
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
        mCamera.rotateX(- mYRoateDegree);
        mCamera.rotateY(mXRoateDegree);
        mCamera.getMatrix(mMatrix);
        mCamera.restore();

        mMatrix.preTranslate(- getWidth() / 2, - getHeight() / 2);
        mMatrix.postTranslate(getWidth() / 2, getHeight() / 2);
        canvas.concat(mMatrix);
    }


    private void getCameraRotate(float x, float y) {

        float dx = x - getWidth() / 2;
        float dy = y - getHeight() / 2;

        float xRoatePercent = dx / (getWidth() / 2);
        float yRoatePercent = dy / (getHeight() / 2);

        if(xRoatePercent > 1) {
            xRoatePercent = 1;
        } else if(xRoatePercent < - 1) {
            xRoatePercent = - 1;
        }

        if(yRoatePercent > 1) {
            yRoatePercent = 1;
        } else if(yRoatePercent < - 1) {
            yRoatePercent = - 1;
        }

        mXRoateDegree = xRoatePercent * mMaxRoateDegree;
        mYRoateDegree = yRoatePercent * mMaxRoateDegree;
    }

    private void cancelResetAnimatorIfNeed() {
        if(mResetAnimator == null) return;

        if(mResetAnimator.isStarted() || mResetAnimator.isRunning()) {
            mResetAnimator.cancel();
        }
    }


    private void startNewResertAnimator() {
        cancelResetAnimatorIfNeed();

        mResetAnimator = ValueAnimator.ofFloat(1, 0);
        mResetAnimator.setDuration(1000);
        mResetAnimator.setInterpolator(new BounceInterpolator());
        mResetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            float startDegressX = 0;
            float startDegressY = 0;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(startDegressX == 0 && startDegressY == 0) {
                    startDegressX = mXRoateDegree;
                    startDegressY = mYRoateDegree;
                }

                float animatedValue = (float) animation.getAnimatedValue();

                mXRoateDegree = startDegressX * animatedValue;
                mYRoateDegree = startDegressY * animatedValue;
            }
        });

        mResetAnimator.start();
    }
}
