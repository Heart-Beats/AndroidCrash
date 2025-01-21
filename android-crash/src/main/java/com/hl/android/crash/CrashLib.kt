package com.hl.android.crash

/**
 * @author 张磊  on  2025/01/20 at 13:38
 * Email: 913305160@qq.com
 */
internal object CrashLib {

	init {
		System.loadLibrary("android-crash")
	}

	private external fun testCrash()

	@JvmStatic
	fun nativeCrashTest() {
		testCrash()
	}

	@JvmStatic
	fun javaCrashTest() {
		throw NullPointerException()
	}
}