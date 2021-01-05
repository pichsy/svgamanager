package com.pichs.svgamanager;

import android.os.Handler;
import android.os.Looper;

public class SvgaUtils {
    private static final Handler mHandler;

    static {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static void runOnUiThread(final Runnable runnable) {
        if (mHandler != null) {
            mHandler.post(runnable);
        }
    }
}
