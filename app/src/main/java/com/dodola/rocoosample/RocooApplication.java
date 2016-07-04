/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.dodola.rocoosample;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.dodola.rocoofix.RocooFix;

import java.io.File;

/**
 * Created by sunpengfei on 16/5/24.
 */
public class RocooApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        RocooFix.init(this);
        RocooFix.applyPatch(this, Environment.getExternalStorageDirectory().getAbsolutePath().concat("/Android/data/" + getPackageName() + "/patch.jar"));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        File fixPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + getPackageName());
        try {
            if (!fixPath.exists())
                fixPath.mkdirs();
        } catch (Exception e) {

        }
        NuwaUtil.check(this, Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + getPackageName() + "/nuwa_version.txt", Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + getPackageName() + "/patch.jar");
    }
}
