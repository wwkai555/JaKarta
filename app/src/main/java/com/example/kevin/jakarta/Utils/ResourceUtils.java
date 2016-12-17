package com.example.kevin.jakarta.Utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kevin on 16-12-14.
 */

public class ResourceUtils {
    public ResourceUtils() {
    }

    public static String getTextFromAssets(Context context, String fileName) {
        String result = "";

        try {
            InputStream e = context.getResources().getAssets().open(fileName);
            int lenght = e.available();
            byte[] buffer = new byte[lenght];
            e.read(buffer);
            result = new String(buffer, "UTF-8");
            e.close();
        } catch (Exception var6) {
            System.out.print(var6.toString());
        }

        return result;
    }

    public static boolean copyToSdcard(Context ctx, String fileName, String target) {
        InputStream in = null;
        FileOutputStream out = null;

        boolean length;
        try {
            in = ctx.getAssets().open(fileName);
            out = new FileOutputStream(target);
            byte[] ex = new byte[1024];

            int length1;
            while ((length1 = in.read(ex)) > 0) {
                out.write(ex, 0, length1);
            }

            return true;
        } catch (Exception var20) {
            length = false;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception var19) {
                ;
            }

            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception var18) {
                ;
            }

        }

        return length;
    }

    public static Drawable loadImageFromAsserts(Context ctx, String fileName) {
        try {
            InputStream e = ctx.getResources().getAssets().open(fileName);
            return Drawable.createFromStream(e, (String) null);
        } catch (IOException var3) {
            if (var3 != null) {
                ;
            }
        } catch (OutOfMemoryError var4) {
            if (var4 != null) {
                ;
            }
        } catch (Exception var5) {
            if (var5 != null) {
                ;
            }
        }

        return null;
    }

    public static void copyDatabase(Context ctx, String dbName) {
        if (ctx != null) {
            File f = ctx.getDatabasePath(dbName);
            if (!f.exists()) {
                if (!f.getParentFile().exists()) {
                    f.getParentFile().mkdir();
                }

                try {
                    InputStream in = ctx.getAssets().open(dbName);
                    FileOutputStream out = new FileOutputStream(f.getAbsolutePath());
                    byte[] buffer = new byte[1024];

                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }

                    in.close();
                    out.close();
                } catch (Exception var7) {
                    ;
                }
            }
        }

    }

    @Nullable
    public static String[] listAssetsFile(@NonNull Context ctx, @NonNull String path) {
        AssetManager assetManager = ctx.getAssets();

        try {
            String[] e = assetManager.list(path);
            return e;
        } catch (IOException var4) {
            var4.printStackTrace();
            return null;
        }
    }
}
