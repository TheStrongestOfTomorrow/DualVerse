# DualVerse ProGuard Configuration

# Keep application classes
-keep class com.dualverse.** { *; }

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep data classes for serialization
-keep class com.dualverse.data.** { *; }

# Keep enums
-keepclassmembers enum com.dualverse.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Kotlin rules
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Retrofit/OkHttp (if used)
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Security
-keep class com.dualverse.core.security.** { *; }
-keepclassmembers class com.dualverse.core.security.** {
    native <methods>;
}

# Virtualization
-keep class com.dualverse.core.virtualization.** { *; }
-keepclassmembers class com.dualverse.core.virtualization.** {
    native <methods>;
}

# Remove logging in release
-assumenosideeffects class timber.log.Timber {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Optimization settings
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable

# Allow optimization
-allowaccessmodification
