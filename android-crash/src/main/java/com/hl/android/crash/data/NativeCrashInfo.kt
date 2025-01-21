package com.hl.android.crash.data

import android.util.Log


data class NativeCrashInfo(

/**
 * minidump 导出文件路径
 */
val miniDumpPath: String?,

/**
 *  native异常信息
 */
val nativeInfo: String,

/**
 *  native 层调用栈
 */
val nativeThreadTrack: String,

/**
 * java 层的调用栈
 */
val jvmThreadTrack: String
) {

	/**
	 * 格式打印信息
	 * @param [tag] 标签
	 * @param [logMaxLength] 日志最大长度
	 */
	fun formatPrint(tag: String, logMaxLength: Int = 20) {
		formatPrint(tag, this.miniDumpPath ?: "dump minidump file failed", logMaxLength)
		formatPrint(tag, this.nativeInfo, logMaxLength)
		formatPrint(tag, this.nativeThreadTrack, logMaxLength)
		formatPrint(tag, this.jvmThreadTrack, logMaxLength)
	}

	private fun formatPrint(tag: String, msg: String, logMaxLength: Int) {
		val stringBuilder = StringBuilder()

		val takeSize = if (logMaxLength < 0) 0 else logMaxLength + 1
		msg.reader().readLines().take(takeSize).forEach {
			stringBuilder.appendLine(it)
		}
		if (stringBuilder.isNotEmpty()) {
			Log.e(tag, " \n$stringBuilder")
			stringBuilder.clear()
		}
	}
}
