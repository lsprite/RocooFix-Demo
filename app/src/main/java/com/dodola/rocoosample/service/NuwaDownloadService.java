package com.dodola.rocoosample.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;

import com.dodola.rocoosample.utils.NuwaUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/7/4.
 */
public class NuwaDownloadService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        System.out.println("NuwaDownloadService.onStart");
        System.out.println("原有补丁文件md5码:" + NuwaUtil.getFileMD5(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + getPackageName() + "/nuwafix" + "/patch.jar")));
        if (!TextUtils.isEmpty(intent.getStringExtra("url"))) {
            downFile(intent.getStringExtra("url"));
        }
    }

    private ExecutorService pool = null;
    private File tempFile = null;

    // 下载更新文件
    private void downFile(final String url) {
        System.out.println("NuwaDownloadService.downFile");
        if (pool == null) {
            pool = Executors.newFixedThreadPool(1);
        }
        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient client = new DefaultHttpClient();
                    // params[0]代表连接的url
                    HttpGet get = new HttpGet(url);
                    HttpResponse response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    long length = entity.getContentLength();
                    InputStream is = entity.getContent();
                    if (is != null) {
                        File rootFile = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + getPackageName() + "/nuwafix");
                        if (!rootFile.exists() && !rootFile.isDirectory())
                            rootFile.mkdirs();

                        tempFile = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + getPackageName() + "/nuwafix" + "/patch.jar");
                        if (tempFile.exists())
                            tempFile.delete();
                        tempFile.createNewFile();

                        // 已读出流作为参数创建一个带有缓冲的输出流
                        BufferedInputStream bis = new BufferedInputStream(is);

                        // 创建一个新的写入流，讲读取到的图像数据写入到文件中
                        FileOutputStream fos = new FileOutputStream(tempFile);
                        // 已写入流作为参数创建一个带有缓冲的写入流
                        BufferedOutputStream bos = new BufferedOutputStream(fos);

                        int read;
                        long count = 0;
                        int precent = 0;
                        byte[] buffer = new byte[1024];
                        while ((read = bis.read(buffer)) != -1) {
                            bos.write(buffer, 0, read);
                            count += read;
                            precent = (int) (((double) count / length) * 100);
                        }
                        bos.flush();
                        bos.close();
                        fos.flush();
                        fos.close();
                        is.close();
                        bis.close();
                        System.out.println("热补丁下载完成");
                    }
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                    System.out.println("热补丁下载失败，请检查网络是否可用");
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("热补丁下载失败，请检查该APP是否获取sd卡权限");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("热补丁下载失败，请检查sd卡状态");
                } finally {
                    stopSelf();
                }
            }
        });
    }

}
