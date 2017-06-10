package com.test.a3dclock.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.test.a3dclock.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnClockView;
    private Button mBtnRoateTest;
    private Button mBtnRoate;
    private Button mBtn3DClockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnClockView = (Button) findViewById(R.id.btnClockView);
        mBtnRoateTest = (Button) findViewById(R.id.btnRoateTest);
        mBtnRoate = (Button) findViewById(R.id.btnRoate);
        mBtn3DClockView = (Button) findViewById(R.id.btn3DClock);

        mBtnClockView.setOnClickListener(this);
        mBtnRoateTest.setOnClickListener(this);
        mBtnRoate.setOnClickListener(this);
        mBtn3DClockView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnClockView:
                startActivity(ClockViewActivity.class);
                break;

            case R.id.btnRoateTest:
                startActivity(RoateTestAtivity.class);
                break;

            case R.id.btnRoate:
                startActivity(TouchRoateAtivity.class);
                break;

            case R.id.btn3DClock:
                startActivity(ToucheClockActivity.class);
                break;
        }
    }

    public void startActivity(Class<? extends Activity> activity){
        startActivity(new Intent(this, activity));
    }
}
