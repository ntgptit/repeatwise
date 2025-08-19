# Flutter và core classes
-keep class io.flutter.app.** { *; }
-keep class io.flutter.plugin.** { *; }
-keep class io.flutter.util.** { *; }
-keep class io.flutter.view.** { *; }
-keep class io.flutter.** { *; }
-keep class io.flutter.plugins.** { *; }

# Giữ lại tất cả các class trong các package chính của ứng dụng
-keep class com.example.spaced_learning_app.** { *; }
-keep class com.example.spaced_learning_app.presentation.screens.** { *; }
-keep class com.example.spaced_learning_app.presentation.widgets.** { *; }
-keep class com.example.spaced_learning_app.domain.models.** { *; }

# Giữ lại Kotlin Coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Giữ lại classes từ Play Core
-keep class com.google.android.play.core.splitcompat.SplitCompatApplication { *; }
-keep class com.google.android.play.core.splitinstall.** { *; }
-keep class com.google.android.play.core.tasks.** { *; }

# Nếu không muốn đưa các class này vào, có thể dontwarn thay vì keep
-dontwarn com.google.android.play.core.splitcompat.SplitCompatApplication
-dontwarn com.google.android.play.core.splitinstall.**
-dontwarn com.google.android.play.core.tasks.**

# Retrofit
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn retrofit2.Platform$Java8

# GSON
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class sun.misc.Unsafe { *; }

# Đảm bảo các model classes được giữ nguyên
-keep class * implements java.io.Serializable { *; }
-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Nếu sử dụng Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
