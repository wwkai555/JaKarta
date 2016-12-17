package com.example.kevin.jakarta.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Created by kevin on 16-12-14.
 */

public class CacheUtil {
    private static final String KEY = "JaKarTa";
    private SharedPreferences preferences;
    private static CacheUtil cacheUtil;

    public static synchronized CacheUtil getInstance() {
        if (cacheUtil == null) {
            cacheUtil = new CacheUtil();
        }
        return cacheUtil;
    }

    private CacheUtil() {
    }

    public void init(@NonNull Context ctx) {
        preferences = ctx.getSharedPreferences(KEY, Context.MODE_PRIVATE);
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

}
