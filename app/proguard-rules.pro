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

# Keep edge-to-edge related classes for Android 15 compatibility
-keep class androidx.activity.ComponentActivity { *; }
-keep class androidx.core.view.WindowCompat { *; }
-keep class android.view.WindowInsets { *; }

# Keep specific Compose layout classes that might be accessed reflectively
-keep class androidx.compose.foundation.layout.WindowInsetsPaddingKt { *; }
-keep class androidx.compose.foundation.layout.WindowInsetsKt { *; }

# --- Gson model/serialization keep rules (prevent release-only crashes) ---
# Keep Gson classes
-dontwarn com.google.gson.**
-keep class com.google.gson.stream.** { *; }

# Keep our data model classes used with Gson (content payloads, questions, etc.)
-keep class com.hellogerman.app.data.entities.** { *; }
-keep class com.hellogerman.app.data.models.** { *; }

# Keep specific data classes that might be used with Gson
-keep class com.hellogerman.app.data.models.MyMemoryTranslationResponse { *; }
-keep class com.hellogerman.app.data.models.ResponseData { *; }
-keep class com.hellogerman.app.data.models.Match { *; }
-keep class com.hellogerman.app.data.models.EnglishWordDefinition { *; }
-keep class com.hellogerman.app.data.models.DictionarySearchResult { *; }

# If using generic type tokens anywhere, keep signatures
-keepattributes Signature

# --- Room keep rules ---
-dontwarn androidx.room.**
-keep class androidx.room.** { *; }
-keep class androidx.sqlite.db.** { *; }
-keep interface com.hellogerman.app.data.dao.** { *; }
-keep class com.hellogerman.app.data.HelloGermanDatabase { *; }
-keep class com.hellogerman.app.data.entities.** { *; }

# (Enums within our entities are already covered by the keep rule above)

# --- API and Networking keep rules ---
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

# Keep API service interfaces
-keep interface com.hellogerman.app.data.api.** { *; }

# --- TTS and Speech keep rules ---
-dontwarn android.speech.tts.**
-keep class android.speech.tts.** { *; }

# --- Compose and UI keep rules ---
-dontwarn androidx.compose.**

# --- Hilt/Dagger keep rules ---
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }

# --- Kotlin keep rules ---
-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }
-keep class kotlin.coroutines.** { *; }

# --- General optimization rules ---
-optimizationpasses 5
-allowaccessmodification
-dontpreverify
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose