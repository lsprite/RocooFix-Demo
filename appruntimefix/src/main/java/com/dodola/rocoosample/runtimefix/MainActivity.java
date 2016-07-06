/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.dodola.rocoosample.runtimefix;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.dodola.rocoofix.RocooFix;
import com.dodola.rocoosample.HelloHack;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.findViewById(R.id.btnFixMeRuntime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelloHack hacks = new HelloHack();
                System.out.println(hacks.showHello());
                RocooFix.initPathFromAssetsRuntime(MainActivity.this, "patch.jar");
                HelloHack hack1 = new HelloHack();
                System.out.println(hack1.showHello());
                Toast.makeText(MainActivity.this, hack1.showHello(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //指定到data/data/应用程序包名/xxx  目录下，防止applyPatchRuntime()方法4.1.2以上报
    //Caused by: java.lang.IllegalArgumentException: Optimized data directory /storage/emulated/0 is not owned by the current user.
    //Shared storage cannot protect your application from code injection attacks.
    //http://vjson.com/wordpress/android防止代码注入攻击.html
    File file = new File(this.getDir("libs", Context.MODE_PRIVATE) + File.separator + "patch.jar");
}
