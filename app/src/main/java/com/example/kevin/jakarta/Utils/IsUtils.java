package com.example.kevin.jakarta.Utils;

import java.util.regex.Pattern;

/**
 * Created by kevin on 16-12-14.
 */

public class IsUtils {
    private static final String PATTERN_ALPHABETIC_OR_NUMBERIC = "[A-Za-z0-9]*";
    private static final String PATTERN_NUMBERIC = "\\d*\\.{0,1}\\d*";

    public IsUtils() {
    }

    public static boolean isAlphabeticOrNumberic(String str) {
        return Pattern.compile("[A-Za-z0-9]*").matcher(str).matches();
    }

    public static boolean isNumeric(String str) {
        return Pattern.compile("\\d*\\.{0,1}\\d*").matcher(str).matches();
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNullOrEmpty(Object str) {
        return str == null || str.toString().length() == 0;
    }

    public static boolean isNullOrEmpty(String... strs) {
        if (strs != null && strs.length != 0) {
            String[] var1 = strs;
            int var2 = strs.length;

            for (int var3 = 0; var3 < var2; ++var3) {
                String str = var1[var3];
                if (str == null || str.length() == 0) {
                    return true;
                }
            }

            return false;
        } else {
            return true;
        }
    }

    public static boolean find(String str, String c) {
        return isNullOrEmpty(str) ? false : str.indexOf(c) > -1;
    }

    public static boolean findIgnoreCase(String str, String c) {
        return isNullOrEmpty(str) ? false : str.toLowerCase().indexOf(c.toLowerCase()) > -1;
    }

    public static boolean equals(String str1, String str2) {
        if (str1 == str2) {
            return true;
        } else {
            if (str1 == null) {
                str1 = "";
            }

            return str1.equals(str2);
        }
    }
}
