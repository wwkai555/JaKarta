package com.example.kevin.jakarta.Utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * Created by kevin on 16-12-14.
 */

public class FileUtil {
    private static final int BUFFER_SIZE = 1024;

    public FileUtil() {
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        } else {
            MessageDigest digest = null;
            FileInputStream in = null;
            byte[] buffer = new byte[1024];

            try {
                digest = MessageDigest.getInstance("MD5");
                in = new FileInputStream(file);

                while (true) {
                    int len;
                    if ((len = in.read(buffer, 0, 1024)) == -1) {
                        in.close();
                        break;
                    }

                    digest.update(buffer, 0, len);
                }
            } catch (Exception var10) {
                var10.printStackTrace();
                return null;
            }

            StringBuilder sb = new StringBuilder();
            byte[] var6 = digest.digest();
            int var7 = var6.length;

            for (int var8 = 0; var8 < var7; ++var8) {
                byte b = var6[var8];
                sb.append(String.format("%02x", new Object[]{Byte.valueOf(b)}));
            }

            return sb.toString();
        }
    }

    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (!"mounted".equals(Environment.getExternalStorageState()) && Environment.isExternalStorageRemovable()) {
            cachePath = context.getCacheDir().getPath();
        } else {
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) {
                cachePath = externalCacheDir.getPath();
            } else {
                cachePath = context.getCacheDir().getPath();
            }
        }

        return new File(cachePath + File.separator + uniqueName);
    }

    public static File getMusicDir(Context context) {
        File cacheDir = getDiskCacheDir(context, "music");
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }

        return cacheDir;
    }

    public static File getMusicThumbDir(Context context) {
        File cacheDir = getDiskCacheDir(context, "music_thumb");
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }

        return cacheDir;
    }

    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            String e = folderPath.toString();
            File myFilePath = new File(e);
            myFilePath.delete();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        } else if (!file.isDirectory()) {
            return flag;
        } else {
            String[] tempList = file.list();
            File temp = null;

            for (int i = 0; i < tempList.length; ++i) {
                if (path.endsWith(File.separator)) {
                    temp = new File(path + tempList[i]);
                } else {
                    temp = new File(path + File.separator + tempList[i]);
                }

                if (temp.isFile()) {
                    temp.delete();
                }

                if (temp.isDirectory()) {
                    delAllFile(path + "/" + tempList[i]);
                    delFolder(path + "/" + tempList[i]);
                    flag = true;
                }
            }

            return flag;
        }
    }

}
