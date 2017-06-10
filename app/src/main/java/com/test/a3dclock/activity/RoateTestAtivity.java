package com.test.a3dclock.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.test.a3dclock.R;

/**
 * @project： 3DClock
 * @package： com.test.a3dclock.activity
 * @class: RoateTestAtivity
 * @author: 陆伟
 * @Date: 2017/6/10 0:45
 * @desc： TODO
 */
public class RoateTestAtivity extends AppCompatActivity {
    private static final String TAG = "RoateTestAtivity";
    private ImageView mImageView;
    private ImageView mImageView1;
    private ImageView mImageView2;
    private ImageView mImageView3;
    private ImageView mImageViewTest;

    private Camera mCamera = new Camera();
    private Matrix mMatrix = new Matrix();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roate_test);

        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView1 = (ImageView) findViewById(R.id.imageView_1);
        mImageView2 = (ImageView) findViewById(R.id.imageView_2);
        mImageView3 = (ImageView) findViewById(R.id.imageView_3);
        mImageViewTest = (ImageView) findViewById(R.id.imageView_test);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.girl);

        mCamera.save();
        mCamera.rotateY(45.0f);
        mCamera.getMatrix(mMatrix);
        mCamera.restore();

        Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mMatrix, true);
        mImageView1.setImageBitmap(bitmap1);

        mMatrix.preTranslate(0, -bitmap.getHeight() / 2);
        mMatrix.postTranslate(0, bitmap.getHeight() / 2);
        Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mMatrix, true);
        mImageView2.setImageBitmap(bitmap2);

        //mMatrix.preTranslate(0, -bitmap.getHeight());
        mMatrix.postTranslate(0, bitmap.getHeight());
        Bitmap bitmap3 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mMatrix, true);
        mImageView3.setImageBitmap(bitmap3);

        //mMatrix.postTranslate(0, bitmap.getHeight() / 2);
        Bitmap bitmap4 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mMatrix, true);
        mImageViewTest.setImageBitmap(bitmap4);
    }
}
