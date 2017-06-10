package com.test.a3dclock.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.test.a3dclock.R;

/**
 * @project： 3DClock
 * @package： com.test.a3dclock.activity
 * @class: ToucheClockActivity
 * @author: 陆伟
 * @Date: 2017/6/10 15:28
 * @desc： TODO
 */
public class ToucheClockActivity extends AppCompatActivity {
    private static final String TAG = "ToucheClockActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_clock);
    }
}
