/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.dodola.rocoosample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.dodola.rocoosample.service.NuwaDownloadService;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(new HelloHack().showHello());
        //开启下载补丁服务
        if (false) {
            Intent service = new Intent(this, NuwaDownloadService.class);
            service.putExtra("url", "http://192.168.1.27:8001/yd_xhfk/uploadfiles/patch.jar");
            startService(service);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
        return false;
    }
}
