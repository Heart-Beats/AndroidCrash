package com.hl.android.crash.nativecapture.breakpad.callback;

/**
 * @author 张磊  on  2025/01/20 at 19:23
 * Email: 913305160@qq.com
 */
public interface NativeCrashCallback {

    void onCrash(
            String miniDumpPath,
            String crashInfo,
            String nativeThreadTrack,
            String crashThreadName
    );
}
