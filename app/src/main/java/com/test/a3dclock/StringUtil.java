package com.test.a3dclock;

import android.view.View;

/**
 * @project： 3DClock
 * @package： com.test.a3dclock
 * @class: StringUtil
 * @author: 陆伟
 * @Date: 2017/6/4 13:52
 * @desc： TODO
 */
public class StringUtil {
    private static final String TAG = "StringUtil";

    public static String measureMode2String(int mode) {
        String modeStr = null;
        switch(mode) {
            case View.MeasureSpec.UNSPECIFIED:
                modeStr = "UNSPECIFIED";
                break;

            case View.MeasureSpec.EXACTLY:
                modeStr = "EXACTLY";
                break;

            case View.MeasureSpec.AT_MOST:
                modeStr = "AT_MOST";
                break;

            default:
                modeStr = "别乱搞没这种mode";
                break;
        }
        return modeStr;
    }
}
