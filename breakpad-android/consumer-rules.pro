# JNI 实现层需要通过反射调用  NativeCrashCallback 的 onCrash 方法通知 java 层，因此保持该接口中的所有方法及变量不被混淆
-keepclassmembers interface  com.hl.android.crash.nativecapture.breakpad.callback.NativeCrashCallback{
	*;
}

# 保留实现了 NativeCrashCallback 接口的类不被混淆
-keepclassmembers class * implements com.hl.android.crash.nativecapture.breakpad.callback.NativeCrashCallback