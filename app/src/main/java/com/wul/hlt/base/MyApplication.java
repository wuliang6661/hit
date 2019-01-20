package com.wul.hlt.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;


import com.wul.hlt.util.SPUtils;
import com.wul.hlt.util.Utils;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

/**
 * 作者 by wuliang 时间 16/10/26.
 * <p>
 * 程序的application
 */

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    public static SPUtils spUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        CustomActivityOnCrash.install(this);
//        CustomActivityOnCrash.setErrorActivityClass(CustomErrorActivity.class);
        /***初始化工具类*/
        Utils.init(this);
        spUtils = new SPUtils("hit");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
