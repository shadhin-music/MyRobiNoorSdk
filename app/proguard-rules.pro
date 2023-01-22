# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

-keep class androidx.* {*;}
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
  }
-keep,allowobfuscation interface com.google.gson.annotations.SerializedName

-keep class org.xmlpull.v1.* {*;}

-keep public class * extends java.lang.Exception
# keep everything in this package from being renamed only

-dontwarn retrofit.**
-keep class retrofit.* { *; }
-keep class com.google.android.* {*;}
-keep class androidx.core.app.CoreComponentFactory { *; }
-keep class android.content.Context.*{*;}
-keep class android.content.Intent.*{*;}
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keepattributes Exceptions,InnerClasses,Signature
# Preserve the special static methods that are required in all enumeration classes.
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keepattributes InnerClasses, Signature, *Annotation*