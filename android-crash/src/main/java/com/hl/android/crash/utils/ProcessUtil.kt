package com.hl.android.crash.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Process


/**
 * @author  张磊  on  2025/01/22 at 10:04
 * Email: 913305160@qq.com
 */
internal object ProcessUtil {

	/**
	 * 获取当前进程名称
	 * @param [context] 上下文
	 */
	fun getCurrentProcessName(context: Context): String? {
		val pid = Process.myPid()
		val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

		manager.runningAppProcesses?.forEach {
			if (it.pid == pid) {
				return it.processName
			}
		}

		return null
	}
}