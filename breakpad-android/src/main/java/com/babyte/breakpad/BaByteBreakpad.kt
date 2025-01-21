package com.babyte.breakpad

import com.babyte.breakpad.callback.NativeCrashCallback


/**
 * 不能修改包名
 */
object BaByteBreakpad {

    init {
        System.loadLibrary("breakpad-android")
    }

    @JvmStatic
    external fun initBreakpadNative(path: String?, callback: NativeCrashCallback)
}