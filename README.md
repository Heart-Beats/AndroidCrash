* [AndroidCrash](#AndroidCrash)
* [接入使用](#接入使用)
* [现状](#现状)
* [设计意图](#设计意图)
* [整体流程](#整体流程)
* [功能介绍](#功能介绍)
  * [定位到so中具体代码行示例](#定位到so中具体代码行示例)
* [致谢](#致谢)

# AndroidCrash

图片要是无法显示，请使用梯子！！！

[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.heart-beats.crash/android-crash?label=android-crash&style=for-the-badge)](https://repo1.maven.org/maven2/io/github/heart-beats/crash/android-crash/)	[![image](https://img.shields.io/badge/SupportAndroidVersion-5--12-gree.svg)](https://developer.android.com/studio/releases/platforms?hl=zh-cn)	![image](https://img.shields.io/badge/supportABI-arm64--v8a|armeabi--v7a|x86|x86--64-gree.svg)

基于 [google/breakpad](https://github.com/google/breakpad) 的 Android Native 异常捕获库，在 native 层发生异常时java层能得到相关异常信息。

# 接入使用

1. 依赖地址

   根项目的build.gradle中:

   ~~~groovy
   allprojects {
       repositories {
           mavenCentral()//添加这一行
       }
   }
   ~~~

   模块的build.gradle中：

   ~~~groovy
   dependencies {   
       //添加这一行, releaseVersion 填最新的版本， 注意无需添加 V
   	implementation 'io.github.heart-beats.crash:android-crash:releaseVersion'
   }
   ~~~
2. 初始化

   ```kotlin
   val crashDir = this.getExternalFilesDir(null)
   val nativeCrashDir = File(crashDir, "nativeCrash")
   val javCrashDir = File(crashDir, "javaCrash")

   // 传入路径参数时，崩溃时会在对应目录下产生崩溃相关日志文件， 不传只有回调输出
   val androidCrash = AndroidCrash.Build(this)
       .addJavaCrashListener(/*javCrashDir.absolutePath, */) { javaCrashInfo ->
           Log.d(TAG, "onCrash: $javaCrashInfo")
       }
       .addNativeCrashListener(/*nativeCrashDir.absolutePath,*/) { nativeCrashInfo ->
           Log.d(TAG, "onCrash: $nativeCrashInfo")
           nativeCrashInfo.formatPrint(TAG)
       }
       .build()
   ```
3. 示例项目

   点击查看：[示例项目](https://github.com/Heart-Beats/AndroidCrash/tree/main/app)

# 现状

+ 发生 native 异常时，安卓系统会将 native 异常信息输出到 logcat 中，但是 java 层无法感知到 native 异常的发生，进而无法获取这些异常信息并上报到业务的异常监控系统。
+ 业务部门可以快速实现 java 层的异常监控系统（ java 层全局异常捕获的实现很简单），又或者业务部门已经实现了 java 层的异常监控系统，但没有覆盖到 native 层的异常捕获。
+ 安卓还可以接入 Breakpad，其导出的 minidump 文件不仅体积小信息还全，但有两个问题：
  + 1.和现状第1点的问题相同。
  + 2.：需要拉取 minidump 文件并经过比较繁琐的步骤才可以得出有用的信息：
    + 启动时检测 Breakpad 是否有导出过 minidump 文件，有则说明发生过 native 异常。
    + 到客户现场，或者远程拉取 minidump 文件。
    + 编译出自己电脑的操作系统的 minidump_stackwalk 工具。
    + 使用 minidump_stackwalk 工具翻译 minidump 文件内容，例如拿到崩溃时的程序计数器寄存器内的值（下文称为 pc 值）。
    + 找到对应崩溃 so 库 ABI 的 add2line 工具，并根据上一步拿到的pc值定位出发生异常的代码行数。

整个步骤十分复杂和繁琐，且没有 java 层的 crash 线程栈信息，不利于 java 开发者快速定位调用 native 的代码。

# 设计意图

1. 让 java 层有知悉 native 异常的通道：

   + java 开发者可以在 java 代码中得到 native 异常的情况，进而对 native 异常做出反应，而不是再次启动后去检测 Breakpad 是否有导出过 minidump 文件。
2. 增加信息的可用性，进而提升问题分析的效率：

   + 回调中提供 naive 异常信息、naive 和 java 调用栈信息和 minidump 文件文件路径，这些信息可以直接通过业务部门的异常监控系统上报。
   + 划分为两个阶段解决问题，我预想是大部分都在阶段一解决了问题，而不需要再对 minidump 文件进行分析，总体来讲是提升了分析效率的：

     + 阶段一：有了 java 的调用栈和 native 的调用栈信息，大部分异常原因都可以快速定位并分析出来。
     + 阶段二：回调中也会提供 minidump 文件的存储路径，业务部门可以按需拉取。（这一步需要业务部门本身有拉取日志的功能，且需要按上文”现状部分进行操作”，较费时费力）
3. 最少改动：

   + 让接入方不因为引入新功能而大量改动现有代码。例如：在 native崩溃回调处,使用现有的 java 层异常监控系统上报 native 异常信息。
4. 单一职责：

   + 只做 native 的 crash 捕获，不做系统内存情况、cpu 使用率、系统日志等信息的采集功能。

# 整体流程

图片要是无法显示，请使用梯子！！！

![image](https://github.com/BAByte/NativeCrash2Java/blob/main/pic/flow.png?raw=true)

# 功能介绍

+ 保留 breakpad 导出 minidump 文件功能 （可选择是否启用）
+ 发生 native 异常时将异常信息、native 层调用栈、java 层的调用栈通过回调提供给开发者，将这些信息输出到控制台的效果如下：

~~~bash
2022-02-14 11:33:08.598 30228-30253/com.babyte.banativecrash E/androidCrash:  
    /data/user/0/com.babyte.banativecrash/cache/f1474006-60ca-40f4-c9d8e89a-47e90c2e.dmp
2022-02-14 11:33:08.599 30228-30253/com.babyte.banativecrash E/androidCrash:  
    Operating system: Android 28 Linux 4.4.146 #37 SMP PREEMPT Wed Jan 20 18:26:59 CST 2021
    CPU: aarch64 (8 core)

    Crash reason: signal 11(SIGSEGV) Invalid address
    Crash address: 0000000000000000
    Crash pc: 0000000000000650
    Crash so: /data/app/com.babyte.banativecrash-ptLzOQ_6UYz-W3Vgyact8A==/lib/arm64/libnative-lib.so(arm64)
    Crash method: _Z5Crashv
2022-02-14 11:33:08.602 30228-30253/com.babyte.banativecrash E/androidCrash:  
    Thread[name:DefaultDispatch] (NOTE: linux thread name length limit is 15 characters)
    #00 pc 0000000000000650  /data/app/com.babyte.banativecrash-ptLzOQ_6UYz-W3Vgyact8A==/lib/arm64/libnative-lib.so (Crash()+20)
    #01 pc 0000000000000670  /data/app/com.babyte.banativecrash-ptLzOQ_6UYz-W3Vgyact8A==/lib/arm64/libnative-lib.so (Java_com_babyte_banativecrash_MainActivity_nativeCrash+20)
    #02 pc 0000000000565de0  /system/lib64/libart.so (offset 0xc1000) (art_quick_generic_jni_trampoline+144)
    #03 pc 000000000055cd88  /system/lib64/libart.so (offset 0xc1000) (art_quick_invoke_stub+584)
    #04 pc 00000000000cf740  /system/lib64/libart.so (art::ArtMethod::Invoke(art::Thread*, unsigned int*, unsigned int, art::JValue*, char const*)+200)
    #05 pc 00000000002823b8  /system/lib64/libart.so (offset 0xc1000)
...
2022-02-14 11:33:08.603 30228-30253/com.babyte.banativecrash E/androidCrash:  
    Thread[DefaultDispatcher-worker-1,5,main]
        at com.babyte.banativecrash.MainActivity.nativeCrash(Native Method)
        at com.babyte.banativecrash.MainActivity$onCreate$2$1.invokeSuspend(MainActivity.kt:39)
        at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
        at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:106)
        at kotlinx.coroutines.scheduling.CoroutineScheduler.runSafely(CoroutineScheduler.kt:571)
        at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.executeTask(CoroutineScheduler.kt:750)
        at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.runWorker(CoroutineScheduler.kt:678)
        at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.run(CoroutineScheduler.kt:665)
    Thread[DefaultDispatcher-worker-2,5,main]
        at java.lang.Object.wait(Native Method)
        at java.lang.Thread.parkFor$(Thread.java:2137)
        at sun.misc.Unsafe.park(Unsafe.java:358)
        at java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:353)
        at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.park(CoroutineScheduler.kt:795)
        at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.tryPark(CoroutineScheduler.kt:740)
        at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.runWorker(CoroutineScheduler.kt:711)
        at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.run(CoroutineScheduler.kt:665)
...
~~~

## 定位到so中具体代码行示例

可以使用 ndk 中的 add2line 工具根据 pc 值和带符号信息的 so 库，定位出具体代码行数。

例：从上文的异常信息中可以看到 abi 是 aarch64，对应的 so 库 abi 是 arm64，所以 add2line 的使用如下：

~~~shell
$ ./ndk/android-ndk-r16b/toolchains/aarch64-linux-android-4.9/prebuilt/darwin-x86_64/bin/aarch64-linux-android-addr2line -Cfe ~/arm64-v8a/libnative-lib.so 0000000000000650
~~~

输出结果如下：

~~~shell
Crash()
/Users/ba/AndroidStudioProjects/NativeCrash2Java/app/.cxx/cmake/debug/arm64-v8a/../../../../src/main/cpp/native-lib.cpp:6
~~~

# 致谢

+ 感谢 [NativeCrash2Java](https://github.com/BAByte/NativeCrash2Java) 库提供的 Native 崩溃通知 Java 层的实现
