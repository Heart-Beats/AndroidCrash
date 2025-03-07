package com.hl.android.crash.demo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.hl.android.crash.AndroidCrash
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val crashDir = this.getExternalFilesDir(null)
        val nativeCrashDir = File(crashDir, "nativeCrash")
        val javCrashDir = File(crashDir, "javaCrash")

        val androidCrash = AndroidCrash.Builder(this)
            .addJavaCrashListener(/*javCrashDir.absolutePath, */) { javaCrashInfo ->
                Log.d(TAG, "onCrash: $javaCrashInfo")
            }
            .addNativeCrashListener(/*nativeCrashDir.absolutePath,*/) { nativeCrashInfo ->
                Log.d(TAG, "onCrash: $nativeCrashInfo")
                nativeCrashInfo.formatPrint(TAG)
            }
            .build()

        findViewById<Button>(R.id.javaCrash).setOnClickListener {
            thread(name = "测试崩溃线程") {
                androidCrash.javaCrashTest()
            }
        }

        findViewById<Button>(R.id.nativeCrash).setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                androidCrash.nativeCrashTest()
            }
        }
    }
}