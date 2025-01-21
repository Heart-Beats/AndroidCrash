package com.hl.android.crash.data


data class JavaCrashInfo(

	/**
	 * 崩溃日志文件路径
	 */
	val crashLogPath: String?,

	/**
	 * java 层的调用栈
	 */
	val jvmThreadTrack: String,

	/**
	 * 异常信息
	 */
	val throwable: Throwable?
)
