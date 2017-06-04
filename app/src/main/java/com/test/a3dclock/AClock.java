package com.test.a3dclock;

import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.Calendar;

public class AClock extends View {
    private static final String TAG = "AClock";

    private float mTickMarkOutSideRadio = 0.9f;
    private float mTickMarkInSideRadio = 0.7f;
    private float mSecondRadio = 0.65f;
    private float mSecondHandSizeRadio = 0.1f;
    private float mMinHandRadio = 0.5f;
    private float mHourHandRadio = 0.4f;
    private float mCoverCircleOutSizeRadio = 0.08f;
    private float mCoverCircleInSizeRadio = 0.04f;


    private int mBackgroundColor = 0xFF9999FF;
    private int mTextColor = 0xFF333333;
    private int mTickMarkColorDark = 0xFFFF6633;
    private int mTickMarkColorLight = 0xFFFFFF99;
    private int mSecondHandColor = 0xFFFFFF99;
    private int mMinHandColor = 0xFFFFFF66;
    private int mHourHandColor = 0xFFFF6633;
    private int mCoverCircleColor = 0xFFFF6633;

    private int[] mGradientColor = new int[] {mTickMarkColorDark, mTickMarkColorLight};
    private float[] mGradientPosition = new float[] {0.75f, 1f};
    private SweepGradient mSweepGradient;


    private int mWidth;     //view 的 width
    private int mHeight;    //view 的 height

    private Point mCenterPoint = new Point();   //中心点
    private int mRadius;  //时钟半径

    private Rect mClockRect = new Rect();   //clock 的范围
    private Rect mTextRect = new Rect();    //文字 的范围


    private RectF mClockArcStrokRectf = new RectF();    //围绕文字的4段圆弧的圆形范围
    private RectF mClockTickMarkOutRectf = new RectF(); //刻度 的 外圆 形范围
    private RectF mClockTickMarkInRectf = new RectF();  //刻度 的 内圆 形范围
    private RectF mCoverCircleInRectf = new RectF();   //中心小圆圈 的 内圆 形范围
    private RectF mCoverCircleOutRectf = new RectF();  //中心小圆圈 的 内圆 形范围

    private Matrix mGradientMatrix = new Matrix();

    private Paint mTransparentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mArcSidePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTickMarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSecondHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mMinHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mHourHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCoverCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Path mSecondHandPath = new Path();   //秒针样式的绘制路径
    private Path mMinHandPath = new Path();   //秒针样式的绘制路径
    private Path mHourHandPath = new Path();   //秒针样式的绘制路径

    private float mSecondHandDegrees = 0;   //秒针角度
    private float mMinHandDegrees = 0;   //分针角度
    private float mHourHandDegrees = 0;   //时针角度

    private PorterDuffXfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);  // 上下层都显示。下层居上显示。

    private float mStrokeWidth; //本view中， 线条的宽度

    private ValueAnimator mSecondAnimator;  //让时针动起来的度动画

    public AClock(Context context) {
        this(context, null);
    }

    public AClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AClock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //擦除颜色的Paint（相当于橡皮擦）
        mTransparentPaint.setAlpha(Color.TRANSPARENT);
        mTransparentPaint.setXfermode(mXfermode);
        mTransparentPaint.setAntiAlias(true);


    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.e(TAG, "onAttachedToWindow: ");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        startNewSecondAnim();
        getContext().registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.e(TAG, "onDetachedFromWindow: ");
        cancelSecondAnimIfNeed();
        getContext().unregisterReceiver(mReceiver);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        Log.e(TAG, "onMeasure: widthMode  = " + StringUtil.measureMode2String(widthMode) + " widthSize = " + widthSize);
        Log.e(TAG, "onMeasure: heightMode = " + StringUtil.measureMode2String(heightMode) + " heightSize = " + heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;

        mCenterPoint.set(mWidth / 2, mHeight / 2);
        mRadius = (int) (Math.min(mWidth, mHeight) * 0.95f / 2);

        //clock的绘制区域
        mClockRect.set(mCenterPoint.x - mRadius,    //左
                       mCenterPoint.y - mRadius,    //上
                       mCenterPoint.x + mRadius,    //右
                       mCenterPoint.y + mRadius);   //下

        //最外围的围绕文字的 stroke 的 圆形范围
        mClockArcStrokRectf.set(mCenterPoint.x - mRadius * 0.95f,    //左
                                mCenterPoint.y - mRadius * 0.95f,    //上
                                mCenterPoint.x + mRadius * 0.95f,    //右
                                mCenterPoint.y + mRadius * 0.95f);  //下

        //TickMark 外圆环 的 圆形范围
        mClockTickMarkOutRectf.set(mCenterPoint.x - mRadius * mTickMarkOutSideRadio,    //左
                                   mCenterPoint.y - mRadius * mTickMarkOutSideRadio,    //上
                                   mCenterPoint.x + mRadius * mTickMarkOutSideRadio,    //右
                                   mCenterPoint.y + mRadius * mTickMarkOutSideRadio);  //下

        //TickMark 内圆环 的 圆形范围
        mClockTickMarkInRectf.set(mCenterPoint.x - mRadius * mTickMarkInSideRadio,    //左
                                  mCenterPoint.y - mRadius * mTickMarkInSideRadio,    //上
                                  mCenterPoint.x + mRadius * mTickMarkInSideRadio,    //右
                                  mCenterPoint.y + mRadius * mTickMarkInSideRadio);  //下

        //中心圆环CoverCircle 外圆环 的 圆形范围
        mCoverCircleOutRectf.set(mCenterPoint.x - mRadius * mCoverCircleOutSizeRadio,   //左
                                 mCenterPoint.y - mRadius * mCoverCircleOutSizeRadio,    //上
                                 mCenterPoint.x + mRadius * mCoverCircleOutSizeRadio,    //右
                                 mCenterPoint.y + mRadius * mCoverCircleOutSizeRadio);  //下

        //中心圆环CoverCircle 内圆环 的 圆形范围
        mCoverCircleInRectf.set(mCenterPoint.x - mRadius * mCoverCircleInSizeRadio,   //左
                                mCenterPoint.y - mRadius * mCoverCircleInSizeRadio,    //上
                                mCenterPoint.x + mRadius * mCoverCircleInSizeRadio,    //右
                                mCenterPoint.y + mRadius * mCoverCircleInSizeRadio);  //下

        //秒针的绘制路径初始化
        float secondHandTopX = mCenterPoint.x;
        float secondHandTopY = mCenterPoint.y - mRadius * mSecondRadio;
        float secondHeight = mRadius * mSecondHandSizeRadio;
        mSecondHandPath.moveTo(secondHandTopX, secondHandTopY);
        mSecondHandPath.lineTo(secondHandTopX + secondHeight, secondHandTopY + secondHeight);
        mSecondHandPath.lineTo(secondHandTopX - secondHeight, secondHandTopY + secondHeight);

        //分针绘制路径
        float minHandTopX = mCenterPoint.x;
        float minHandTopY = mCenterPoint.y - mRadius * mMinHandRadio;
        float minHornWidth = mRadius * 0.01f;
        float minHornHeight = mRadius * 0.006f;
        float minBottomWidrh = mRadius * 0.018f;
        float minBottomY = mRadius * 0.07f;
        mMinHandPath.moveTo(minHandTopX, minHandTopY);
        mMinHandPath.lineTo(minHandTopX - minHornWidth, minHandTopY + minHornHeight);
        mMinHandPath.lineTo(minHandTopX - minBottomWidrh, mCenterPoint.y - minBottomY);
        mMinHandPath.lineTo(minHandTopX + minBottomWidrh, mCenterPoint.y - minBottomY);
        mMinHandPath.lineTo(minHandTopX + minHornWidth, minHandTopY + minHornHeight);


        //时针绘制路径
        float hourHandTopX = mCenterPoint.x;
        float hourHandTopY = mCenterPoint.y - mRadius * mHourHandRadio;
        float hourHornWidth = mRadius * 0.012f;
        float hourHornHeight = mRadius * 0.006f;
        float hourBottomWidrh = mRadius * 0.02f;
        float hourBottomY = mRadius * 0.07f;
        mHourHandPath.moveTo(hourHandTopX, hourHandTopY);
        mHourHandPath.lineTo(hourHandTopX - hourHornWidth, hourHandTopY + hourHornHeight);
        mHourHandPath.lineTo(hourHandTopX - hourBottomWidrh, mCenterPoint.y - hourBottomY);
        mHourHandPath.lineTo(hourHandTopX + hourBottomWidrh, mCenterPoint.y - hourBottomY);
        mHourHandPath.lineTo(hourHandTopX + hourHornWidth, hourHandTopY + hourHornHeight);

        mSweepGradient = new SweepGradient(mCenterPoint.x, mCenterPoint.y, mGradientColor, mGradientPosition);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawText(canvas);
        drawArcSize(canvas);
        drawTickMark(canvas);
        drawSecondHand(canvas);
        drawMinHand(canvas);
        drawHourHand(canvas);
        drawCoverCircle(canvas);
    }

    /*绘制 时刻文字*/
    private void drawText(Canvas canvas) {
        mTextPaint.setColor(mTextColor);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setTextSize(mRadius * 0.1f);

        //绘制文字
        String textStr = "12";
        mTextPaint.getTextBounds(textStr, 0, textStr.length(), mTextRect);
        canvas.drawText(textStr, mCenterPoint.x - mTextRect.width() / 2, mCenterPoint.y - mRadius + mTextRect.height(), mTextPaint);

        textStr = "3";
        mTextPaint.getTextBounds(textStr, 0, textStr.length(), mTextRect);
        canvas.drawText(textStr, mCenterPoint.x + mRadius - (mTextRect.width() + mTextRect.height()) / 2, mCenterPoint.y + mTextRect.height() / 2, mTextPaint);
        canvas.drawText("6", mCenterPoint.x - mTextRect.width() / 2, mCenterPoint.y + mRadius, mTextPaint);
        canvas.drawText("9", mCenterPoint.x - mRadius, mCenterPoint.y + mTextRect.height() / 2, mTextPaint);
    }

    /*绘制 圆弧边*/
    private void drawArcSize(Canvas canvas) {
        mStrokeWidth = mRadius * 0.015f;

        mArcSidePaint.setColor(mTextColor);
        mArcSidePaint.setStyle(Paint.Style.STROKE);
        mArcSidePaint.setStrokeWidth(mStrokeWidth);

        //绘制边界
        for(int i = 0; i < 4; i++) {
            canvas.drawArc(mClockArcStrokRectf, 5 + i * 90, 80, false, mArcSidePaint);
        }
    }

    /*绘制 刻度*/
    private void drawTickMark(Canvas canvas) {
        mTransparentPaint.setStrokeWidth(mStrokeWidth);
        mTransparentPaint.setAntiAlias(true);

        //matrix默认会在三点钟方向开始颜色的渐变，为了吻合
        //钟表十二点钟顺时针旋转的方向，把秒针旋转的角度减去90度
        mGradientMatrix.setRotate(mSecondHandDegrees - 90, mCenterPoint.x, mCenterPoint.y);
        mSweepGradient.setLocalMatrix(mGradientMatrix);
        mTickMarkPaint.setShader(mSweepGradient);
        canvas.drawArc(mClockTickMarkOutRectf, 0, 360, false, mTickMarkPaint);

        //让内部分透明
        canvas.drawArc(mClockTickMarkInRectf, 0, 360, false, mTransparentPaint);

        canvas.save();
        for(int i = 0; i < 200; i++) {
            //mTickMarkMatrix.setRotate((float) (1.6 * i), mCenterPoint.x, mCenterPoint.y);
            canvas.drawLine(mCenterPoint.x, mCenterPoint.y - mRadius * mTickMarkOutSideRadio, mCenterPoint.x, mCenterPoint.y - mRadius * mTickMarkInSideRadio, mTransparentPaint);
            canvas.rotate(1.8f, mCenterPoint.x, mCenterPoint.y);
        }
        canvas.restore();
    }

    /*绘制 秒针*/
    private void drawSecondHand(Canvas canvas) {
        mSecondHandPaint.setColor(mSecondHandColor);
        drawCLockHand(canvas, mSecondHandPaint, mSecondHandPath, mSecondHandDegrees);
    }

    /*绘制 分针*/
    private void drawMinHand(Canvas canvas) {
        mMinHandPaint.setColor(mMinHandColor);
        drawCLockHand(canvas, mMinHandPaint, mMinHandPath, mMinHandDegrees);
    }

    /*绘制 时针*/
    private void drawHourHand(Canvas canvas) {
        mHourHandPaint.setColor(mHourHandColor);
        drawCLockHand(canvas, mHourHandPaint, mHourHandPath, mHourHandDegrees);
    }

    /* 时钟的 时针、分针、秒针*/
    private void drawCLockHand(Canvas canvas, Paint paint, Path handPath, float degrees) {
        canvas.save();
        canvas.rotate(degrees, mCenterPoint.x, mCenterPoint.y);
        canvas.drawPath(handPath, paint);
        canvas.restore();
    }

    /*绘制 中心 小圆环*/
    private void drawCoverCircle(Canvas canvas) {
        mCoverCirclePaint.setColor(mCoverCircleColor);

        canvas.drawArc(mCoverCircleOutRectf, 0, 360, false, mCoverCirclePaint);
        canvas.drawArc(mCoverCircleInRectf, 0, 360, false, mTransparentPaint);
    }

    private void startNewSecondAnim() {
        Log.i(TAG, "startNewSecondAnim");
        cancelSecondAnimIfNeed();
        updateTimePointer();

        final float startDegree = 0f;
        final float endDegree = 360f;
        mSecondAnimator = ValueAnimator.ofFloat(startDegree, endDegree);
        // FIXME 不知为何某些机型动画实际执行时间是duration的一半
        // 在构造函数里启动动画就正常，在其他方法（如onAttachedToWindow或onSizeChanged）里调用本方法就不正常
        // 出问题的机型：小米4、小米1S
        mSecondAnimator.setDuration(60 * 1000);
        mSecondAnimator.setInterpolator(new LinearInterpolator());
        mSecondAnimator.setRepeatMode(ValueAnimator.RESTART);
        mSecondAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mSecondAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            private float lastDrawValue = 0;    //最近一次绘画的值
            private float drawInterval = 0.1f;  //绘画的间隔

            private float lastUpdatePointerValue = 0;   //最近更新的值
            private float updatePointerInterval = 360 / 60 * 5; //更新值的间隔

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float newValue = (float) animation.getAnimatedValue();
                // Check if it is the time to update pointer position
                float increasedPointerValue = newValue - lastUpdatePointerValue;
                //增加的值<0
                if(increasedPointerValue < 0) {
                    increasedPointerValue = endDegree + increasedPointerValue;
                }
                if(increasedPointerValue >= updatePointerInterval) {
                    lastUpdatePointerValue = newValue;
                    updateTimePointer();
                }

                // Check if it is the time to invalidate
                float increasedDrawValue = newValue - lastDrawValue;
                if(increasedDrawValue < 0) {
                    increasedDrawValue = endDegree + increasedDrawValue;
                }

                if(increasedDrawValue >= drawInterval) {
                    lastDrawValue = newValue;
                    mSecondHandDegrees += increasedDrawValue;
                    invalidate();

                    //Log.d(TAG, String.format("newValue:%s , currentPlayTime:%s", newValue, animation.getCurrentPlayTime()));

                }
            }
        });
        mSecondAnimator.start();
    }

    private void cancelSecondAnimIfNeed() {
        if(mSecondAnimator != null && (mSecondAnimator.isStarted() || mSecondAnimator.isRunning())) {
            mSecondAnimator.cancel();

            Log.i(TAG, "cancelSecondAnimIfNeed");
        }
    }


    private void updateTimePointer() {
        Log.i(TAG, "updateTimePointer: ");
        int millsecond = Calendar.getInstance().get(Calendar.MILLISECOND);
        int second = Calendar.getInstance().get(Calendar.SECOND);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        int hour = Calendar.getInstance().get(Calendar.HOUR);
        float percentSecond = (float) ((1.0 * second + 1.0 * millsecond / 1000) / 60);
        float percentMinute = (float) (1.0 * (minute * 60 + second) / (60 * 60));
        float percentHour = (float) (1.0 * (hour * 60 * 60 + minute * 60 + second) / (60 * 60 * 12));
        mSecondHandDegrees = 360 * percentSecond;
        mMinHandDegrees = 360 * percentMinute;
        mHourHandDegrees = 360 * percentHour;
    }

    private Receiver mReceiver = new Receiver();

    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent == null) return;

            String action = intent.getAction();
            if(TextUtils.isEmpty(action)) {
                return;
            }

            Log.e(TAG, "onReceive: 收到广播" + String.format("action -> %s", action));
            if(action.equals(Intent.ACTION_SCREEN_ON)) {
                startNewSecondAnim();
            } else if(action.equals(Intent.ACTION_SCREEN_OFF)) {
                cancelSecondAnimIfNeed();
            } else if(action.equals(Intent.ACTION_TIME_TICK)) {
                updateTimePointer();
            }
        }
    }

}
