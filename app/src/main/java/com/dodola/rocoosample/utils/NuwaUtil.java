package com.dodola.rocoosample.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Scanner;

/**
 * Created by Administrator on 2016/7/4.
 */
public class NuwaUtil {
    public enum LMODE {
        LMODE_NEW_INSTALL, LMODE_UPDATE, LMODE_AGAIN
    }

    //根据保存的版本，如果是LMODE_NEW_INSTALL和LMODE_AGAIN则尝试删除补丁文件，LMODE_UPDATE则保留

    /**
     * @param context
     * @param filePath    版本信息文件路径
     * @param fixFilePath 补丁文件路径
     */
    public static void check(Context context, String filePath, String fixFilePath) {
        LMODE mode = LMODE.LMODE_NEW_INSTALL;
        String lastVersion = read(filePath);
        String thisVersion = getAppVersion(context);
        System.out.println("lastVersion:" + lastVersion);
        System.out.println("thisVersion:" + thisVersion);
        // 首次启动
        if (TextUtils.isEmpty(lastVersion)) {
            System.out.println("首次启动");
            mode = LMODE.LMODE_NEW_INSTALL;
        }
        // 更新
        else if (!thisVersion.equals(lastVersion)) {
            System.out.println("更新(版本不一致)");
            mode = LMODE.LMODE_UPDATE;
        }
        // 二次启动(版本未变)
        else {
            System.out.println("二次启动(版本未变)");
            mode = LMODE.LMODE_AGAIN;
        }
        if (mode == LMODE.LMODE_NEW_INSTALL || mode == LMODE.LMODE_UPDATE) {
            deleteFile(new File(fixFilePath));
        }
        write(context, filePath);
    }

    //用到个上下文有关的保存信息的方法会导致加载热补丁的Hack报错，只能使用不用到上下文的文件保存方式
    private static void write(Context context, String filePath) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) { // 如果sdcard存在
            File file = new File(filePath); // 定义File类对象
            if (!file.getParentFile().exists()) { // 父文件夹不存在
                file.getParentFile().mkdirs(); // 创建文件夹
            }
            PrintStream out = null; // 打印流对象用于输出
            try {
                out = new PrintStream(new FileOutputStream(file, false)); // 替换
                out.println(getAppVersion(context));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    out.close(); // 关闭打印流
                    out = null;
                }
            }
        } else {
        }
    }

    // 文件读操作函数
    private static String read(String filePath) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) { // 如果sdcard存在
            File file = new File(filePath); // 定义File类对象
            try {
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
            } catch (Exception e) {
                return null;
            }
            Scanner scan = null; // 扫描输入
            StringBuilder sb = new StringBuilder();
            try {
                scan = new Scanner(new FileInputStream(file)); // 实例化Scanner
                while (scan.hasNext()) { // 循环读取
                    sb.append(scan.next() + "\n"); // 设置文本
                }
                return sb.toString().trim();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (scan != null) {
                    scan.close(); // 关闭打印流
                    scan = null;
                }
            }
        } else {
        }
        return null;
    }

    /**
     * 获取单个文件的MD5值！
     *
     * @param file
     * @return
     */

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        if (!file.exists()){
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    //获取当前版本号
    private static String getAppVersion(Context context) {

        String versionName = "";
        try {
            PackageManager pkgMng = context.getPackageManager();
            PackageInfo pkgInfo = pkgMng
                    .getPackageInfo(context.getPackageName(), 0);
            versionName = pkgInfo.versionName.trim();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return versionName;
    }

    public static void deleteFile(File file) {
        try {
            System.out.println("尝试删除补丁文件");
            if (file.exists()) { // 判断文件是否存在
                if (file.isFile()) { // 判断是否是文件
                    file.delete(); // delete()方法 你应该知道 是删除的意思;
                    System.out.println("删除了补丁文件");
                } else if (file.isDirectory()) { // 否则如果它是一个目录
                    File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                    for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                        deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                    }
                    file.delete();
                }
            }
        } catch (Exception e) {
            System.out.println("删除补丁文件出错");
            e.printStackTrace();
        }
    }

}
