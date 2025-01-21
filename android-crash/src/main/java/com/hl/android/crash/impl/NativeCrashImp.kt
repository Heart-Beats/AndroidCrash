package com.hl.android.crash.impl

import android.content.Context
import com.babyte.breakpad.BaByteBreakpad.initBreakpadNative
import com.babyte.breakpad.callback.NativeCrashCallback
import com.hl.android.crash.callback.OnCrashListener
import com.hl.android.crash.callback.OnNativeCrashListener
import com.hl.android.crash.data.NativeCrashInfo
import java.io.File

class NativeCrashImp : ICrash {

	override fun init(context: Context, crashLogDir: String?, onCrashListener: OnCrashListener) {
		if (crashLogDir != null) {
			File(crashLogDir).run {
				if (!exists()) {
					// 如果日志文件目录不存在，则进行创建
					mkdirs()
				}
			}
		}

		val onNativeCrashListener = onCrashListener as OnNativeCrashListener
		initBreakpad(crashLogDir) {
			onNativeCrashListener.onCrash(it)
		}
	}

	/**
	 * @param miniDumpDir 输出 minidump文件到 dir 目录
	 * @param nativeCrashWholeCallBack 将 minidump文件路径，native 层的异常信息，java层异常信息回调给开发者
	 */
	private fun initBreakpad(miniDumpDir: String?, nativeCrashWholeCallBack: (NativeCrashInfo) -> Unit) {
		initBreakpadNative(miniDumpDir, NativeCrashCallback { miniDumpPath, crashInfo, nativeThreadTrack, crashThreadName ->
			val nativeCrashInfo =
				NativeCrashInfo(miniDumpDir ?: miniDumpPath, crashInfo, nativeThreadTrack, getStackString(crashThreadName))
			nativeCrashWholeCallBack(nativeCrashInfo)
		})
	}

	private fun getStackString(crashThreadName: String): String {
		val stringBuilder = StringBuilder()
		for ((thread, stack) in Thread.getAllStackTraces()) {
			if (thread.name.contains(crashThreadName)) {
				stringBuilder.appendLine("$thread")
				stack.forEach {
					stringBuilder.appendLine("     at $it")
				}
			}
		}
		return stringBuilder.toString()
	}
}
