# 3DClock

## 做这东西有几个要注意的

### 1 drawText 的坑深入人心

### 2 颜色渐变

    SweepGradient mSweepGradient = new SweepGradient(x, y, mGradientColor, mGradientPosition);

    mMatrix.setRotate(- 90, mCenterPoint.x, mCenterPoint.y);
 
    mSweepGradient.setLocalMatrix(mMatrix);
 
    mPaint.setShader(mSweepGradient);
 
    canvas.drawArc(mRectf, 0, 360, false, mPaint);
 
### 3 让绘制的地方透明

    参考【android】绘制圆环的三种方式 ：http://blog.csdn.net/u011494050/article/details/39251239
 
    mPaint.setAlpha(Color.TRANSPARENT);

    mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN)); //Xfermode 的用法超多慢慢研究

    mPaint.setAntiAlias(true);  //坑 不知道怎么 setXfermod 后 antiAlias 就不生效了
