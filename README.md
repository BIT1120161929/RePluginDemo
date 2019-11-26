# RePluginDemo

## 说明  
myhost为宿主  
myplugin和myplugin2都是插件  
更为详细的步骤在demo里面的注释中  

### 宿主host接入过程：  
1、在app级别的gradle中加入依赖  
classpath 'com.qihoo360.replugin:replugin-host-gradle:2.3.3'  
2、在module级别的gradle中加入依赖  
implementation ('com.qihoo360.replugin:replugin-host-lib:2.2.4')   
3、配置Application  



### 插件接入过程  
1、在APP级别的gradle中引入依赖，注意与宿主不是同一个依赖  
classpath 'com.qihoo360.replugin:replugin-plugin-gradle:2.2.4'  
2、在module级别的gradle中添加依赖，注意与宿主不是一个依赖  
implementation 'com.qihoo360.replugin:replugin-plugin-lib:2.2.4'  
3、将插件打包成apk，然后改名为xxx.jar并且放入宿主工程main/assets/plugins文件夹  
  
### 运行结果  
host界面  
<img src="image/host.jpg" width="188" height="400"/>  
plugin界面  
<img src="image/plugin.jpg" width="188" height="400"/>  
目前的问题在于插件会与屏幕不适配，目前还不知道是个什么原因  
通过将sdk版本调整至28并且使用最新的插件版本可以解决这个问题  
  
  
### 以下是几个大坑的解决  
#### 动态链接库查找问题  
2019-11-22 17:37:47.517 9849-9849/com.example.myhost E/linker: library "/system/lib64/libc++_shared.so" ("/system/lib64/libc++_shared.so") needed or dlopened by "/system/lib64/libnativeloader.so" is not accessible for the namespace: [name="classloader-namespace", ld_library_paths="", default_library_paths="", permitted_paths="/data:/mnt/expand"]
2019-11-22 17:37:47.518 9849-9849/com.example.myhost E/ws001: l.p.a spp|aac: dlopen failed: library "/system/lib64/libc++_shared.so" needed or dlopened by "/system/lib64/libnativeloader.so" is not accessible for the namespace "classloader-namespace"
    java.lang.UnsatisfiedLinkError: dlopen failed: library "/system/lib64/libc++_shared.so" needed or dlopened by "/system/lib64/libnativeloader.so" is not accessible for the namespace "classloader-namespace"  
    
上面的问题是因为没有将插件的*.so文件拷贝到宿主工程的对应路径上。下面的问题则是配置问题  
2019-11-22 18:02:41.182 12402-12402/com.example.myhost E/ws001: l.p.a spp|aac: com.qihoo360.replugin.PluginDexClassLoader[DexPathList[[zip file "/data/user/0/com.example.myhost/app_plugins_v3/live-10-10-1.jar"],nativeLibraryDirectories=[/data/user/0/com.example.myhost/app_plugins_v3_libs/live-10-10-1, /system/lib]]] couldn't find "libc++_shared.so"
    java.lang.UnsatisfiedLinkError: com.qihoo360.replugin.PluginDexClassLoader[DexPathList[[zip file "/data/user/0/com.example.myhost/app_plugins_v3/live-10-10-1.jar"],nativeLibraryDirectories=[/data/user/0/com.example.myhost/app_plugins_v3_libs/live-10-10-1, /system/lib]]] couldn't find "libc++_shared.so"
        at java.lang.Runtime.loadLibrary0(Runtime.java:1012)  
1、在module级别的gradle中设置jniLib  
```java
//    声明libs
sourceSets {
    main {
        jniLibs.srcDir 'libs'
    }
}
```  
2、将插件中对应的*.so文件拷贝到宿主app中libs中对应cpu架构的文件夹之下  
3、指定一个filter来在对应的文件夹下搜索*.so文件，这个加filter还有点坑人，宿主的gradle和插件的gradle中都需要加，不然还是会报找不到动态链接库的错误    
```java  
    //这个filter还挺重要
    ndk{
        abiFilters'armeabi-v7a'
    }
```  
  
#### ResourceNotFound  
2019-11-23 21:28:12.552 795-795/com.example.myhost E/AndroidRuntime: FATAL EXCEPTION: main  
    Process: com.example.myhost, PID: 795  
    java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.myhost/com.example.myhost.loader.a.ActivityN1NRNTS2}: android.content.res.Resources$NotFoundException: Drawable com.example.myhost:dimen/design_appbar_elevation with resource ID #0x7f060053  
        at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:3430)  
        at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:3614)  
        at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:86)  
        at android.app.servertransaction.TransactionExecutor.executeCallbacks(TransactionExecutor.java:108)  
        at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:68)  
        at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2199)  
        at android.os.Handler.dispatchMessage(Handler.java:112)  
        at android.os.Looper.loop(Looper.java:216)  
        at android.app.ActivityThread.main(ActivityThread.java:7625)  
        at java.lang.reflect.Method.invoke(Native Method)  
        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:524)  
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:987)  
     Caused by: android.content.res.Resources$NotFoundException: Drawable com.example.myhost:dimen/design_appbar_elevation with resource ID #0x7f060053  
     Caused by: android.content.res.Resources$NotFoundException: Resource "com.example.myhost:dimen/design_appbar_elevation" (7f060053) is not a Drawable (color or path): TypedValue{t=0x5/d=0x401 a=5 r=0x7f060053}  
        at android.content.res.ResourcesImpl.loadDrawableForCookie(ResourcesImpl.java:1090)  
        at android.content.res.ResourcesImpl.loadDrawable(ResourcesImpl.java:917)  
        at android.content.res.Resources.getDrawableForDensity(Resources.java:1074)  
        at android.content.res.Resources.getDrawable(Resources.java:1013)  
        at android.content.Context.getDrawable(Context.java:630)  
        at android.support.v4.content.ContextCompat.getDrawable(ContextCompat.java:358)  
        at android.support.v7.widget.AppCompatDrawableManager.getDrawable(AppCompatDrawableManager.java:198)  
        at android.support.v7.widget.AppCompatDrawableManager.getDrawable(AppCompatDrawableManager.java:186)  
        at android.support.v7.widget.AppCompatDrawableManager.checkVectorDrawableSetup(AppCompatDrawableManager.java:753)  
        at android.support.v7.widget.AppCompatDrawableManager.getDrawable(AppCompatDrawableManager.java:191)  
        at android.support.v7.widget.TintTypedArray.getDrawableIfKnown(TintTypedArray.java:85)  
        at android.support.v7.app.AppCompatDelegateImplBase.<init>(AppCompatDelegateImplBase.java:128)  
        at android.support.v7.app.AppCompatDelegateImplV9.<init>(AppCompatDelegateImplV9.java:149)  
        at android.support.v7.app.AppCompatDelegateImplV14.<init>(AppCompatDelegateImplV14.java:56)  
        at android.support.v7.app.AppCompatDelegateImplV23.<init>(AppCompatDelegateImplV23.java:31)  
        at android.support.v7.app.AppCompatDelegateImplN.<init>(AppCompatDelegateImplN.java:31)  
        at android.support.v7.app.AppCompatDelegate.create(AppCompatDelegate.java:198)  
        at android.support.v7.app.AppCompatDelegate.create(AppCompatDelegate.java:183)  
        at android.support.v7.app.AppCompatActivity.getDelegate(AppCompatActivity.java:519)  
        at android.support.v7.app.AppCompatActivity.onCreate(AppCompatActivity.java:70)  
        at up366.com.bukalivev2.MainActivity.onCreate(MainActivity.java:21)  
        at android.app.Activity.performCreate(Activity.java:7458)  
        at android.app.Activity.performCreate(Activity.java:7448)  
        at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1286)  
        at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:3409)  
        at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:3614)  
        at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:86)  
        at android.app.servertransaction.TransactionExecutor.executeCallbacks(TransactionExecutor.java:108)  
        at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:68)  
2019-11-23 21:28:12.552 795-795/com.example.myhost E/AndroidRuntime:     at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2199)  
        at android.os.Handler.dispatchMessage(Handler.java:112)  
        at android.os.Looper.loop(Looper.java:216)  
        at android.app.ActivityThread.main(ActivityThread.java:7625)  
        at java.lang.reflect.Method.invoke(Native Method)  
        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:524)  
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:987)  
报的是个资源找不到的问题，这个问题确实头疼了我很久，最后在[解决方案](https://github.com/Qihoo360/RePlugin/issues/882)的帮助下，成功解决了问题。这个问题是因为部分的Activity的父类AppCompatActivity没有被框架替换为PluginAppCompatActivity而导致的，只能通过暴力的方法将所有的Activity的父类替换为PluginAppCompatActivity。  
#### 玄学问题  
: D:\android\up366\live\BukaLiveV2\app\build\intermediates\transforms\desugar\release\9 does not exist.  
	at org.apache.tools.ant.types.AbstractFileSet.getDirectoryScanner(AbstractFileSet.java:492)  
这个问题没有解决。非常玄学，如果有见到类似问题的同志可以讨论讨论。因为我后面用了其他的方法绕过去了这个问题。  

#### 找不到对应的类
2019-11-25 13:59:24.449 17049-17144/com.example.myhost E/AndroidRuntime: FATAL EXCEPTION: Thread-6  
    Process: com.example.myhost, PID: 17049  
    java.lang.NoClassDefFoundError: Failed resolution of: Lorg/apache/commons/logging/LogFactory;  
        at org.apache.commons.httpclient.HttpClient.<clinit>(HttpClient.java:66)  
        at tv.buka.sdk.utils.HttpUtils.getUrl(HttpUtils.java:51)  
        at tv.buka.sdk.v3.manager.secret.NetManager$3.run(NetManager.java:180)  
        at java.lang.Thread.run(Thread.java:784)  
     Caused by: java.lang.ClassNotFoundException: Calling the loadClass method failed (InvocationTargetException)  
        at com.qihoo360.replugin.PluginDexClassLoader.loadClassFromHost(PluginDexClassLoader.java:161)  
        at com.qihoo360.replugin.PluginDexClassLoader.loadClass(PluginDexClassLoader.java:114)  
        at java.lang.ClassLoader.loadClass(ClassLoader.java:312)  
        at org.apache.commons.httpclient.HttpClient.<clinit>(HttpClient.java:66)   
        at tv.buka.sdk.utils.HttpUtils.getUrl(HttpUtils.java:51)   
        at tv.buka.sdk.v3.manager.secret.NetManager$3.run(NetManager.java:180)   
        at java.lang.Thread.run(Thread.java:784)   
     Caused by: java.lang.reflect.InvocationTargetException  
        at java.lang.reflect.Method.invoke(Native Method)  
        at com.qihoo360.replugin.PluginDexClassLoader.loadClassFromHost(PluginDexClassLoader.java:151)  
        at com.qihoo360.replugin.PluginDexClassLoader.loadClass(PluginDexClassLoader.java:114)   
        at java.lang.ClassLoader.loadClass(ClassLoader.java:312)   
        at org.apache.commons.httpclient.HttpClient.<clinit>(HttpClient.java:66)   
        at tv.buka.sdk.utils.HttpUtils.getUrl(HttpUtils.java:51)   
        at tv.buka.sdk.v3.manager.secret.NetManager$3.run(NetManager.java:180)   
        at java.lang.Thread.run(Thread.java:784)   
     Caused by: java.lang.ClassNotFoundException: Didn't find class "org.apache.commons.logging.LogFactory" on path: DexPathList[[zip file "/data/app/com.example.myhost-L02MQd0Pav2BIcSjW-Dq9A==/base.apk"],nativeLibraryDirectories=[/data/app/com.example.myhost-L02MQd0Pav2BIcSjW-Dq9A==/lib/arm, /data/app/com.example.myhost-L02MQd0Pav2BIcSjW-Dq9A==/base.apk!/lib/armeabi-v7a, /system/lib]]   
        at dalvik.system.BaseDexClassLoader.findClass(BaseDexClassLoader.java:134)  
        at com.qihoo360.replugin.RePluginClassLoader.findClass(RePluginClassLoader.java:172)  
        at java.lang.ClassLoader.loadClass(ClassLoader.java:379)  
        at com.qihoo360.replugin.RePluginClassLoader.loadClass(RePluginClassLoader.java:163)  
        at java.lang.reflect.Method.invoke(Native Method)   
        at com.qihoo360.replugin.PluginDexClassLoader.loadClassFromHost(PluginDexClassLoader.java:151)   
        at com.qihoo360.replugin.PluginDexClassLoader.loadClass(PluginDexClassLoader.java:114)   
        at java.lang.ClassLoader.loadClass(ClassLoader.java:312)   
        at org.apache.commons.httpclient.HttpClient.<clinit>(HttpClient.java:66)   
        at tv.buka.sdk.utils.HttpUtils.getUrl(HttpUtils.java:51)   
        at tv.buka.sdk.v3.manager.secret.NetManager$3.run(NetManager.java:180)   
        at java.lang.Thread.run(Thread.java:784)   
这个问题通过manifest中的配置即可解决。  
```java  
  <!--        这里两个是防止报错-->
    <uses-library android:name="org.apache.http.legacy" android:required="false"/>
    <uses-library android:name="org.apache.commons" android:required="false"/>
```  
  然后第一条配置可能导致上不了网，具体可以[解决方案](https://blog.csdn.net/xyx2999/article/details/82909582)来解决。  

