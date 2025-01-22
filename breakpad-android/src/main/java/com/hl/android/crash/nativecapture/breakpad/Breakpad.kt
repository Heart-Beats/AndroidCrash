package com.hl.android.crash.nativecapture.breakpad

import com.hl.android.crash.nativecapture.breakpad.callback.NativeCrashCallback


/**
 * 不能修改包名
 */
object Breakpad {

    init {
        System.loadLibrary("breakpad-android")
    }

    @JvmStatic
    external fun initBreakpadNative(path: String?, callback: NativeCrashCallback)
}