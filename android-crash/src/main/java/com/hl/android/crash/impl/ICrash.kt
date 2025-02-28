package com.hl.android.crash.impl

import android.content.Context
import com.hl.android.crash.callback.OnCrashListener

interface ICrash {
	/**
	 *
	 * @param context 上下文
	 * @param crashLogDir 崩溃日志存储目录
	 * @param onCrashListener 崩溃监听回调处理
	 */
	fun init(context: Context, crashLogDir: String?, onCrashListener: OnCrashListener?)
}
