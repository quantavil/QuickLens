# ProGuard rules for QuickLens

# OkHttp/Okio - Reference: https://github.com/square/okhttp/blob/master/okhttp/src/main/resources/META-INF/proguard/okhttp3.pro
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# Keep Kotlin metadata
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class kotlin.Metadata { *; }

# Keep Room entities
-keep class com.quantavil.quicklens.data.** { *; }

# Keep DAO methods
-keep interface com.quantavil.quicklens.data.SearchHistoryDao { *; }

# Keep WebView JavaScript interface (if any)
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Preserve the special static methods that are required in all enumeration classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# Entry Points (Manifest classes)
-keep class com.quantavil.quicklens.QuickLensAccessibilityService { *; }
-keep class com.quantavil.quicklens.QuickLensSessionService { *; }
-keep class com.quantavil.quicklens.QuickLensRecognitionService { *; }
-keep class com.quantavil.quicklens.OverlayActivity { *; }
-keep class com.quantavil.quicklens.MainActivity { *; }
