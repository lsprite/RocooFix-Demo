/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.dodola.rocoosample;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.dodola.rocoofix.RocooFix;
import com.dodola.rocoosample.utils.NuwaUtil;

import java.io.File;

/**
 * Created by sunpengfei on 16/5/24.
 */
public class RocooApplication extends Application {
    private static RocooApplication instance;

    public static RocooApplication getInstance() {
        if (instance == null) {
            instance = new RocooApplication();
        }
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        File fixPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + getPackageName() + "/nuwafix");
        try {
            if (!fixPath.exists())
                fixPath.mkdirs();
        } catch (Exception e) {

        }
        NuwaUtil.check(this, Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + getPackageName() + "/nuwafix" + "/nuwa_version.txt",
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + getPackageName() + "/nuwafix" + "/patch.jar");
        RocooFix.init(this);
        RocooFix.applyPatch(this, Environment.getExternalStorageDirectory().getAbsolutePath().concat("/Android/data/" + getPackageName() + "/nuwafix" + "/patch.jar"));
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

}
