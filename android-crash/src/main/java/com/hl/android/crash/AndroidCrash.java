package com.hl.android.crash;

import android.content.Context;

import com.hl.android.crash.callback.OnJavaCrashListener;
import com.hl.android.crash.callback.OnNativeCrashListener;
import com.hl.android.crash.impl.ICrash;
import com.hl.android.crash.impl.JavaCrashImp;
import com.hl.android.crash.impl.NativeCrashImp;

/**
 * @author 张磊  on  2025/01/20 at 13:38
 * Email: 913305160@qq.com
 */
public class AndroidCrash {

    /**
     * 上下文
     */
    private final Context context;


    private AndroidCrash(Context context) {
        this.context = context.getApplicationContext();
    }


    private void initCaptureJavaCrash(String javaCrashLogDir, OnJavaCrashListener onJavaCrashListener) {
        if (javaCrashLogDir == null && onJavaCrashListener == null) {
            return;
        }

        ICrash iCrash = new JavaCrashImp();
        iCrash.init(context, javaCrashLogDir, onJavaCrashListener);
    }

    private void initCaptureNativeCrash(String nativeCrashDumpDir, OnNativeCrashListener onNativeCrashListener) {
        if (nativeCrashDumpDir == null && onNativeCrashListener == null) {
            return;
        }

        ICrash nativeCrashImp = new NativeCrashImp();
        nativeCrashImp.init(context, nativeCrashDumpDir, onNativeCrashListener);
    }

    /**
     * native 崩溃测试
     *
     */
    public void nativeCrashTest() {
        CrashLib.nativeCrashTest();
    }

    /**
     * java 崩溃测试
     *
     */
    public void javaCrashTest() {
        CrashLib.javaCrashTest();
    }


    public static class Builder {
        private String javaCrashLogDir;
        private String nativeCrashDumpDir;
        private final Context context;
        private OnJavaCrashListener onJavaCrashListener;
        private OnNativeCrashListener onNativeCrashListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder addJavaCrashListener(String crashLogDir) {
            this.javaCrashLogDir = crashLogDir;
            return this;
        }

        public Builder addJavaCrashListener(OnJavaCrashListener onCrashListener) {
            this.onJavaCrashListener = onCrashListener;
            return this;
        }

        public Builder addJavaCrashListener(String crashLogDir, OnJavaCrashListener onCrashListener) {
            this.javaCrashLogDir = crashLogDir;
            this.onJavaCrashListener = onCrashListener;
            return this;
        }

        public Builder addNativeCrashListener(String dumpDir) {
            this.nativeCrashDumpDir = dumpDir;
            return this;
        }

        public Builder addNativeCrashListener(OnNativeCrashListener onNativeCrashListener) {
            this.onNativeCrashListener = onNativeCrashListener;
            return this;
        }

        public Builder addNativeCrashListener(String dumpDir, OnNativeCrashListener onNativeCrashListener) {
            this.nativeCrashDumpDir = dumpDir;
            this.onNativeCrashListener = onNativeCrashListener;
            return this;
        }

        public AndroidCrash build() {
            AndroidCrash androidCrash = new AndroidCrash(this.context);
            androidCrash.initCaptureJavaCrash(this.javaCrashLogDir, this.onJavaCrashListener);
            androidCrash.initCaptureNativeCrash(this.nativeCrashDumpDir, this.onNativeCrashListener);
            return androidCrash;
        }
    }

}
