package com.test.a3dclock.test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.test.a3dclock.R;

/**
 * Camera与Matrix的比较：<br/>
 * Camera的rotate()相关方法是指定某一维度上旋转指定的角度。<br/>
 * Matrix的rotate()相关方法实现的效果是顺时针旋转指定的角度；与Camera指定Z轴旋转效果相同，但方向相反。<br/>
 * 
 * Camera的translate()方法根据某一维度上视点的位移实现图像的缩放，与Matrix的scale()相关方法作用效果相似，
 * 只是Matrix的scale()相关方法是直接指定缩放比例。<br/>
 * Camera不支持倾斜操作，Matrix可以直接实现倾斜操作。<br/>
 * 
 * <a href="http://my.oschina.net/arthor" class="referer" target="_blank">@author</a> Sodino E-mail:sodinoopen@hotmail.com
 * @version Time：2011-9-26 下午04:17:49
 */
public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener {
    private Camera camera;
    // views
    private SeekBar seekbarXRotate;
    private SeekBar seekbarYRotate;
    private SeekBar seekbarZRotate;
  
//    private TextView txtXRotate;
//    private TextView txtYRotate;
//    private TextView txtZRotate;
  
    private SeekBar seekbarXSkew;
    private SeekBar seekbarYSkew;
    private SeekBar seekbarZTranslate;
   
    
//    private TextView txtXTranslate;
//    private TextView txtYTranslate;
//    private TextView txtZTranslate;
    private ImageView imgResult;
  
    
    // integer params
    private int rotateX, rotateY, rotateZ;
    private float skewX, skewY;
    private int translateZ;
 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        // camera
        camera = new Camera();
        
        // rotate
        seekbarXRotate = (SeekBar) findViewById(R.id.seekbarXRotate);
        seekbarXRotate.setOnSeekBarChangeListener(this);
        seekbarYRotate = (SeekBar) findViewById(R.id.seekbarYRotate);
        seekbarYRotate.setOnSeekBarChangeListener(this);
        seekbarZRotate = (SeekBar) findViewById(R.id.seekbarZRotate);
        seekbarZRotate.setOnSeekBarChangeListener(this);
       
        
        //txtXRotate = (TextView) findViewById(R.id.txtXRotate);
        //txtYRotate = (TextView) findViewById(R.id.txtYRotate);
        //txtZRotate = (TextView) findViewById(R.id.txtZRotate);
       
        
        // translate
        seekbarXSkew = (SeekBar) findViewById(R.id.seekbarXSkew);
        seekbarXSkew.setOnSeekBarChangeListener(this);
        seekbarYSkew = (SeekBar) findViewById(R.id.seekbarYSkew);
        seekbarYSkew.setOnSeekBarChangeListener(this);
        seekbarZTranslate = (SeekBar) findViewById(R.id.seekbarZTranslate);
        seekbarZTranslate.setOnSeekBarChangeListener(this);
       
        
        //txtXTranslate = (TextView) findViewById(R.id.txtXSkew);
        //txtYTranslate = (TextView) findViewById(R.id.txtYSkew);
       // txtZTranslate = (TextView) findViewById(R.id.txtZTranslate);
        imgResult = (ImageView) findViewById(R.id.imgResult);
        System.out.println("33333333333333333333");
        
        // refresh
        refreshImage();
        
        
//        post pre set 其实代表了Matrix 中方法变换的次序，pre是向前加入队列执行，post从后面加入队列执行。
//        举一些例子：
//        matrix.preScale(2f,1f);  matrix.preTranslate(5f, 0f); matrix.postScale(0.2f, 1f);  matrix.postTranslate(0.5f, 0f);
//        方法执行的顺序为：
//        preTranslate(5, 0) -> preScale(2f, 1f) -> postScale(0.2f, 1f) -> postTranslate(0.5f, 0f)
        
//        而matrix的set方法则会对先前的pre和post操作进行清除，而后再设置它的值，比如下列的方法调用: 
//        matrix.postTranslate(2f, 0f); matrix.preScale(0.2f, 1f);   matrix.setScale(1f, 1f); matrix.postScale(5f, 1f); matrix.preTranslate(0.5f, 0f); 方法执行的顺序为：
//        translate(0.5f, 0f) -> scale(1f, 1f) ->  scale(5f, 1).
//        Canvas里scale, translate, rotate, concat方法都是pre方法，如果要进行更多的变换可以先从canvas获得matrix, 变换后再设置回canvas. 
    }
 
    
    
    private void refreshImage() {
        // 获取待处理的图像
        BitmapDrawable tmpBitDra = (BitmapDrawable) getResources().getDrawable(R.mipmap.ic_launcher);
        Bitmap tmpBit = tmpBitDra.getBitmap();
       
        // 开始处理图像
        // 1.获取处理矩阵
        // 记录一下初始状态。save()和restore()可以将图像过渡得柔和一些。
        // Each save should be balanced with a call to restore().
        camera.save();
       
        Matrix matrix = new Matrix();
        // rotate
        camera.rotateX(rotateX);
        camera.rotateY(rotateY);
        camera.rotateZ(rotateZ);
        // translate
        camera.translate(0, 0, translateZ);
        camera.getMatrix(matrix);
        // 恢复到之前的初始状态。
        camera.restore();
      
        // 设置图像处理的中心点
        matrix.preTranslate(tmpBit.getWidth() >> 1, tmpBit.getHeight() >> 1);
       
      
        matrix.preSkew(skewX, skewY);
//      setScale(float sx, float sy, float px, float py) 放大 
//      setSkew(float kx, float ky, float px, float py) 斜切 
//      setTranslate(float dx, float dy)  平移 
//      setRotate(float degrees, float px, float py)    旋转 
        
        // matrix.postSkew(skewX, skewY);
        // 直接setSkew()，则前面处理的rotate()、translate()等等都将无效。
        // matrix.setSkew(skewX, skewY);
        // 2.通过矩阵生成新图像(或直接作用于Canvas)
      
        
        Log.d("ANDROID_LAB", "width=" + tmpBit.getWidth() + "height=" + tmpBit.getHeight());
        Bitmap newBit = null;
        try {
            // 经过矩阵转换后的图像宽高有可能不大于0，此时会抛出IllegalArgumentException
            newBit = Bitmap.createBitmap(tmpBit, 0, 0, tmpBit.getWidth(), tmpBit.getHeight(), matrix, true);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        }
        if (newBit != null) {
            imgResult.setImageBitmap(newBit);
        }
    }
    
    
    
 
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == seekbarXRotate) {
            //txtXRotate.setText(progress + "゜");
            rotateX = progress;
        } else if (seekBar == seekbarYRotate) {
            //txtYRotate.setText(progress + "゜");
            rotateY = progress;
        } else if (seekBar == seekbarZRotate) {
            //txtZRotate.setText(progress + "゜");
            rotateZ = progress;
        } else if (seekBar == seekbarXSkew) {
            skewX = (progress - 100) * 1.0f / 100;
            //txtXTranslate.setText(String.valueOf(skewX));
        } else if (seekBar == seekbarYSkew) {
            skewY = (progress - 100) * 1.0f / 100;
            //txtYTranslate.setText(String.valueOf(skewY));
      
        } else if (seekBar == seekbarZTranslate) {
            translateZ = progress - 100;
            //txtZTranslate.setText(String.valueOf(translateZ));
        }
        refreshImage();
    }
 
    
    
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
 
    }
 
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
 
    }
}