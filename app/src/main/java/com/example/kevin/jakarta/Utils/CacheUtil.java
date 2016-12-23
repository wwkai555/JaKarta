package com.example.kevin.jakarta.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import org.apache.commons.codec.digest.Md5Crypt;

/**
 * Created by kevin on 16-12-14.
 */

public class CacheUtil {
    private static final String KEY = "JaKarTa";
    private SharedPreferences preferences;
    private static CacheUtil cacheUtil;
    private static String MUSIC_CACHE_DIR;

    public static synchronized CacheUtil getInstance() {
        if (cacheUtil == null) {
            cacheUtil = new CacheUtil();
        }
        return cacheUtil;
    }

    private CacheUtil() {
    }

    public void init(Context ctx) {
        preferences = ctx.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        MUSIC_CACHE_DIR = FileUtil.getMusicDir(ctx).getAbsolutePath();
    }

    public void putString(@NonNull String key, @NonNull String result) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, result);
        editor.apply();
    }

    public String getString(@NonNull String key) {
        return preferences.getString(key, "");
    }

    public void putBoolean(@NonNull String key, @NonNull boolean value) {
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(key, value);
            editor.apply();
        }
    }

    public boolean getBoolean(@NonNull String key) {
        if (preferences != null) {
            return preferences.getBoolean(key, false);
        } else {
            return false;
        }
    }

    public static String getMusicCachePath(String url) {
        return MUSIC_CACHE_DIR + "/" + Md5Crypt.apr1Crypt(url) + ".mp3";
    }

    public static String getMusicLyricPath(String url) {
        return MUSIC_CACHE_DIR + "/" + url.hashCode() + ".lrc";
    }

}
