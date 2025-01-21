package com.hl.android.crash.callback;

import com.hl.android.crash.data.NativeCrashInfo;

/**
 * @author 张磊  on  2025/01/21 at 19:31
 * Email: 913305160@qq.com
 */
public interface OnNativeCrashListener extends OnCrashListener {
    void onCrash(NativeCrashInfo nativeCrashInfo);
}
