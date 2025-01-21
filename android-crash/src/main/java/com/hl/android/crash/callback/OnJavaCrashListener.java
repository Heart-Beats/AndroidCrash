package com.hl.android.crash.callback;

import com.hl.android.crash.data.JavaCrashInfo;

/**
 * @author 张磊  on  2025/01/21 at 19:29
 * Email: 913305160@qq.com
 */

public interface OnJavaCrashListener extends OnCrashListener {
    void onCrash(JavaCrashInfo javaCrashInfo);
}
