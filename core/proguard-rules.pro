# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn javax.lang.model.element.Modifier

# 基于 sdk/tools/proguard/proguard-android-optimize.txt 修改
-allowaccessmodification
-dontpreverify
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
# 输出生成信息
-verbose
-printmapping proguardMapping.txt
# 输出apk包内所有的class的内部结构
-dump dump.txt
# 未混淆的类和成员
-printseeds seeds.txt
# 列出从apk中删除的代码
-printusage unused.txt
# 关闭压缩
-dontshrink
# 不混淆本地方法
-keepclasseswithmembernames class * {
    native <methods>;
}
# 不混淆 Activity 在 XML 布局所设置的 onClick 属性值
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
# 不混淆枚举类
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# 不混淆 Parcelable 子类
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}
# 不混淆 R 文件中的字段
-keepclassmembers class **.R$* {
    public static <fields>;
}
# 指定代码的压缩级别
-optimizationpasses 5
# 不忽略库中的非public的类成员
-dontskipnonpubliclibraryclassmembers
# google推荐算法
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
# 避免混淆Annotation、内部类、泛型、匿名类
-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
# 保持四大组件
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
# 保持support下的所有类及其内部类
-keep class android.support.** {*;}
# 保留继承的
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**
# 保持自定义控件
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
# 保持所有实现 Serializable 接口的类成员
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
# WebView处理
-dontwarn android.webkit.WebView
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient
-keep public class android.webkit.WebView
-keep public class android.net.http.SslError
-keep public class android.webkit.WebViewClient
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String);
}
# 不混淆 WebView 设置的 JS 接口的方法名
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# androidx 混淆
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
-printconfiguration
-keep,allowobfuscation @interface androidx.annotation.Keep
-keep @androidx.annotation.Keep class *
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}


##---------------Begin: proguard configuration for kotlinx.coroutines  ----------
# When editing this file, update the following files as well:
# - META-INF/com.android.tools/proguard/coroutines.pro
# - META-INF/com.android.tools/r8/coroutines.pro
# ServiceLoader support
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
# Same story for the standard library's SafeContinuation that also uses AtomicReferenceFieldUpdater
-keepclassmembers class kotlin.coroutines.SafeContinuation {
    volatile <fields>;
}
# These classes are only required by kotlinx.coroutines.debug.AgentPremain, which is only loaded when
# kotlinx-coroutines-core is used as a Java agent, so these are not needed in contexts where ProGuard is used.
-dontwarn java.lang.instrument.ClassFileTransformer
-dontwarn sun.misc.SignalHandler
-dontwarn java.lang.instrument.Instrumentation
-dontwarn sun.misc.Signal
# Only used in `kotlinx.coroutines.internal.ExceptionsConstructor`.
# The case when it is not available is hidden in a `try`-`catch`, as well as a check for Android.
-dontwarn java.lang.ClassValue
# An annotation used for build tooling, won't be directly accessed.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
##---------------End: proguard configuration for kotlinx.coroutines   ----------


##---------------Begin: proguard configuration for OkHttp  ----------
# https://raw.githubusercontent.com/square/okhttp/master/okhttp/src/jvmMain/resources/META-INF/proguard/okhttp3.pro
# 如果您在使用 R8 作为默认编译器的 Android 项目中使用 OkHttp 作为依赖项，则您无需执行任何操作。具体的规则已经捆绑到 JAR 中，可以由 R8 自动解释。
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*
# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
##---------------End: proguard configuration for OkHttp  ----------

# OkIo
# https://github.com/square/okio/blob/master/okio/src/jvmMain/resources/META-INF/proguard/okio.pro
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

##---------------Begin: proguard configuration for Retrofit  ----------
# https://github.com/square/retrofit/blob/master/retrofit/src/main/resources/META-INF/proguard/retrofit2.pro
# 如果您使用的是 R8，则会自动包含收缩和混淆规则。
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod
# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault
# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**
# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit
# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*
# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
# Keep inherited services.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>
# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
##---------------End: proguard configuration for Retrofit  ----------

##---------------Begin: proguard configuration for Gson  ----------
# https://github.com/google/gson/blob/master/examples/android-proguard-example/proguard.cfg
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature
# For using GSON @Expose annotation
-keepattributes *Annotation*
# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }
# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
##---------------End: proguard configuration for Gson  ----------

# Timber
# https://github.com/JakeWharton/timber/blob/trunk/timber/consumer-proguard-rules.pro
-dontwarn org.jetbrains.annotations.**

# ViewBindingPropertyDelegate
# https://github.com/androidbroadcast/ViewBindingPropertyDelegate
-keep,allowoptimization class * implements androidx.viewbinding.ViewBinding {
    public static *** bind(android.view.View);
    public static *** inflate(...);
}