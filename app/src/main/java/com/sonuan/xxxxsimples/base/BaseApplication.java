package com.sonuan.xxxxsimples.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * @author wusongyuan
 * @date 2017.07.05
 * @desc
 */

public class BaseApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
