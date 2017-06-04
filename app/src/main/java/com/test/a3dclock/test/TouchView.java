package com.test.a3dclock.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.audiofx.LoudnessEnhancer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * @project： 3DClock
 * @package： com.test.a3dclock.test
 * @class: TouchView
 * @author: 陆伟
 * @Date: 2017/6/4 21:53
 * @desc： TODO
 */
public class TouchView extends ImageView {
    private static final String TAG = "TouchView";

    private Camera camera = new Camera();
    private Matrix matrixCanvas = new Matrix();
    private Canvas mCanvas;

    private float canvasMaxRotateDegree = 20;
    private float canvasRotateY;
    private float canvasRotateX;

    public TouchView(Context context) {
        this(context, null);
    }

    public TouchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //mBitmap = BitmapFactory.decodeResource(getResources(), android.R.mipmap.sym_def_app_icon);

        //mBitmap = getDrawingCache(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //mRadius = Math.min(w, h) / 2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        invalidate();
        int action = event.getActionMasked();
        switch(action) {
            case MotionEvent.ACTION_DOWN: {
                //cancelSteadyAnimIfNeed();
                rotateCanvasWhenMove(x, y);
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                rotateCanvasWhenMove(x, y);
                return true;
            }
            case MotionEvent.ACTION_UP: {
                //cancelSteadyAnimIfNeed();
                //startNewSteadyAnim();
                return true;
            }
        }

        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Log.e(TAG, "onDraw: ");
        mCanvas = canvas;
        //mCameraMatrix.setTranslate(getWidth() / 2, getHeight() / 2);
        //mCanvas.setMatrix(mCameraMatrix);
        rotateCanvas(canvas);
        canvas.setMatrix(matrixCanvas);
        super.onDraw(canvas);
    }

    private void rotateCanvasWhenMove(float x, float y) {

        float dx = x - getWidth() / 2;
        float dy = y - getHeight() / 2;

        float percentX = dx / (getWidth() / 2);
        float percentY = dy / (getHeight() / 2);

        if(percentX > 1f) {
            percentX = 1f;
        } else if(percentX < - 1f) {
            percentX = - 1f;
        }
        if(percentY > 1f) {
            percentY = 1f;
        } else if(percentY < - 1f) {
            percentY = - 1f;
        }

        canvasRotateY = canvasMaxRotateDegree * percentX;
        canvasRotateX = - (canvasMaxRotateDegree * percentY);

        Log.e(TAG, "rotateCanvasWhenMove: x = " + x + " y = " + y + " canvasRotateX = " + canvasRotateX + " canvasRotateY = " + canvasRotateY);
    }

    private void rotateCanvas(Canvas canvas) {
        Log.e(TAG, "rotateCanvas: ");
        matrixCanvas.reset();

        camera.save();
        camera.rotateX(canvasRotateX);
        camera.rotateY(canvasRotateY);
        camera.getMatrix(matrixCanvas);
        camera.restore();

        int matrixCenterX = getWidth() / 2;
        int matrixCenterY = getHeight() / 2;
        // This moves the center of the view into the upper left corner (0,0)
        // which is necessary because Matrix always uses 0,0, as it's transform point
        matrixCanvas.preTranslate(- matrixCenterX, - matrixCenterY);
        // This happens after the camera rotations are applied, moving the view
        // back to where it belongs, allowing us to rotate around the center or
        // any point we choose
        matrixCanvas.postTranslate(matrixCenterX, matrixCenterY);

        canvas.concat(matrixCanvas);
    }

}
