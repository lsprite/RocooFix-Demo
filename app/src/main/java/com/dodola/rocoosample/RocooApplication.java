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
public class RocooApplication extends Application implements Thread.UncaughtExceptionHandler {
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
        System.out.println("RocooApplication.attachBaseContext");
        File fixPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + getPackageName() + "/nuwafix");
        try {
            if (!fixPath.exists())
                fixPath.mkdirs();
        } catch (Exception e) {

        }
        NuwaUtil.check(this, Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + getPackageName() + "/nuwafix" + "/nuwa_version.txt",
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + getPackageName() + "/nuwafix" + "/patch.jar", Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + getPackageName() + "/nuwafix" + "/patch_error.txt");
        RocooFix.init(this);
        RocooFix.applyPatch(this, Environment.getExternalStorageDirectory().getAbsolutePath().concat("/Android/data/" + getPackageName() + "/nuwafix" + "/patch.jar"));
    }


    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("RocooApplication.onCreate");
        //设置Thread Exception Handler
        Thread.setDefaultUncaughtExceptionHandler(this);
        //
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if (errorContain(throwable)) {//因为热补丁引起的错误
            NuwaUtil.patchErrorWrite(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + getPackageName() + "/nuwafix" + "/patch_error.txt",
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + getPackageName() + "/nuwafix" + "/patch.jar");
            NuwaUtil.deleteFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + getPackageName() + "/nuwafix" + "/patch.jar"));
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    //com.dodola.rocoofix
    //Class ref in pre-verified class resolved to unexpected implementation
    private boolean errorContain(Throwable throwable) {
        try {
            for (StackTraceElement elem : throwable.getStackTrace()) {
                System.err.println(elem);
                if (elem.toString().equals("com.dodola.rocoofix") || elem.toString().equals("Class ref in pre-verified class resolved to unexpected implementation")) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
