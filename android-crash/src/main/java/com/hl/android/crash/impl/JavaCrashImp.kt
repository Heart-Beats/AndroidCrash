package com.hl.android.crash.impl

import android.content.Context
import android.os.Process
import android.util.Log
import com.hl.android.crash.callback.OnCrashListener
import com.hl.android.crash.callback.OnJavaCrashListener
import com.hl.android.crash.data.JavaCrashInfo
import com.hl.android.crash.utils.ProcessUtil
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


typealias javaCrashCallBack = (String?, String, Throwable) -> Unit

class JavaCrashImp : ICrash {

	private val TAG: String = "JavaCrashImp"

	/**
	 * 初始化
	 *
	 * @param context
	 */
	override fun init(context: Context, crashLogDir: String?, onCrashListener: OnCrashListener) {
		val onJavaCrashListener = onCrashListener as OnJavaCrashListener

		handleUncaughtException(context, crashLogDir, onJavaCrashListener)
	}


	/**
	 * 处理未捕获的异常
	 */
	private fun handleUncaughtException(context: Context, crashLogDir: String?, javaCrashCallBack: OnJavaCrashListener) {
		// 获取系统默认或已设置的 UncaughtException 处理器
		val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

		Thread.setDefaultUncaughtExceptionHandler { thread: Thread, ex: Throwable? ->
			try {
				handleException(context, thread, ex, crashLogDir) { crashLogPath, stackTraceString, throwable ->
					val javaCrashInfo = JavaCrashInfo(crashLogPath, stackTraceString, throwable)
					javaCrashCallBack.onCrash(javaCrashInfo)
				}
			} catch (e: Exception) {
				Log.e(TAG, "handleUncaughtException  异常...", e)
			}

			// 处理完成之后让异常保持继续流转
			defaultHandler?.uncaughtException(thread, ex)
		}
	}


	/**
	 * 自定义错误处理,收集错误信息
	 */
	private fun handleException(context: Context, thread: Thread, ex: Throwable?, crashLogDir: String?, javaCrashCallBack: javaCrashCallBack) {
		if (ex == null) {
			return
		}

		var crashLogPath: String? = null
		if (crashLogDir != null) {
			File(crashLogDir).run {
				if (!exists()) {
					// 如果日志文件目录不存在，则进行创建
					mkdirs()
				}
			}

			val time = getNowFormatTime()
			crashLogPath = File(crashLogDir, "crash_$time.log").also {
				if (!it.exists()) {
					it.createNewFile()
				}
			}.absolutePath

			// 保存日志文件
			saveCrashInfo2File(context, thread, ex, crashLogPath)
		}

		val stackTraceString = getStackTraceAsString(context, thread, ex)

		javaCrashCallBack(crashLogPath, stackTraceString, ex)
	}

	/**
	 * 获取现在格式化时间字符串
	 */
	private fun getNowFormatTime(): String {
		// 用于格式化日期,作为日志文件名的一部分
		val formatter = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())
		val time = formatter.format(Date())
		return time
	}

	/**
	 * 保存堆栈错误信息到文件中
	 */
	private fun saveCrashInfo2File(context: Context, thread: Thread, ex: Throwable, filePath: String) {
		try {
			FileWriter(filePath, true).use { fileWriter ->
				PrintWriter(fileWriter).use { printWriter ->
					printWriter.println("FATAL EXCEPTION: ${thread.name}")
					printWriter.println("Process: ${ProcessUtil.getCurrentProcessName(context)}, PID: ${Process.myPid()} ")

					// 将异常堆栈信息写入文件
					ex.printStackTrace(printWriter)

					// 自定义写入格式，添加异常发生时间
					printWriter.println("Exception Capture at: ${getNowFormatTime()} \n")
				}
			}
		} catch (e: Exception) {
			Log.e(TAG, "saveCrashInfo2File  异常...", e)
		}
	}

	/**
	 * 获取堆栈错误信息
	 */
	private fun getStackTraceAsString(context: Context, thread: Thread, ex: Throwable): String {
		var stackTraceInfo = ""

		try {
			StringWriter().use { stringWriter ->
				PrintWriter(stringWriter).use { printWriter ->
					printWriter.println("FATAL EXCEPTION: ${thread.name}")
					printWriter.println("Process: ${ProcessUtil.getCurrentProcessName(context)}, PID: ${Process.myPid()} ")

					// 将堆栈信息写入到 StringWriter
					ex.printStackTrace(printWriter)

					// 自定义写入格式，添加异常发生时间
					printWriter.println("Exception Capture at: ${getNowFormatTime()} \n")

					// 将 StringWriter 转换为字符串
					stackTraceInfo = stringWriter.toString()
				}
			}
		} catch (e: Exception) {
			Log.e(TAG, "getStackTraceAsString 异常...", e)
		}
		return stackTraceInfo
	}
}
