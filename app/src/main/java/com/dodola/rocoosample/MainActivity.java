/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.dodola.rocoosample;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(new HelloHack().showHello());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        System.out.println("MainBottomActivity.KEYCODE_BACK");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
        return false;
    }
}
