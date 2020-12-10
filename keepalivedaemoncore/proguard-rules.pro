-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
#-dontoptimize
#-dontobfuscate
-verbose
-dump class_files.txt
-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#-allowaccessmodification
-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile, LineNumberTable, *Annotation*, EnclosingMethod
-renamesourcefileattribute SourceFile

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-dontwarn java.lang.invoke.*
-dontwarn android.webkit.JavascriptInterface
-dontwarn android.**


-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclasseswithmembernames class * {
    native <methods>;
}

###
-dontwarn com.keepalive.daemon.core**
-keeppackagenames com.keepalive.daemon.core, com.keepalive.daemon.core.**
#-flattenpackagehierarchy com.keepalive.daemon.core

-keep class com.keepalive.daemon.core.** {
    public protected *;
}

-keep public enum com.keepalive.daemon.core.**$** {
    **[] $VALUES;
    public *;
}