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
-keep class androidx.activity.enableEdgeToEdge { *; }

# Keep system UI related classes
-keep class android.view.WindowInsets { *; }
-keep class androidx.compose.foundation.layout.* { *; }

# --- Gson model/serialization keep rules (prevent release-only crashes) ---
# Keep Gson classes
-dontwarn com.google.gson.**
-keep class com.google.gson.stream.** { *; }

# Keep our data model classes used with Gson (content payloads, questions, etc.)
-keep class com.hellogerman.app.data.entities.** { *; }
# Keep our data classes parsed by Gson for grammar content
-keep class com.hellogerman.app.ui.screens.GrammarContentLite { *; }
-keep class com.hellogerman.app.ui.screens.GrammarQuestionLite { *; }
-keep class com.hellogerman.app.data.GrammarContent { *; }
-keep class com.hellogerman.app.data.GrammarQuestion { *; }

# If using generic type tokens anywhere, keep signatures
-keepattributes Signature

# --- Room keep rules ---
-dontwarn androidx.room.**
-keep class androidx.room.** { *; }
-keep class androidx.sqlite.db.** { *; }
-keep interface com.hellogerman.app.data.dao.** { *; }
-keep class com.hellogerman.app.data.HelloGermanDatabase { *; }

# (Enums within our entities are already covered by the keep rule above)