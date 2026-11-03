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

# Gson reflects over DTO constructors and fields at runtime. Prevent R8 from
# removing or transforming these model classes while still allowing their names
# to be obfuscated in the release build.
-keep,allowobfuscation class com.jenil.f1comp.data.model.** {
    *;
}

# Keep the JSON property names unchanged because Gson matches backend keys to
# fields by name unless a field has an explicit @SerializedName annotation.
-keepclassmembers class com.jenil.f1comp.data.model.** {
    <fields>;
}

# Firestore discovers Kotlin properties through JavaBean getters at runtime.
# Keep this model and its getter names intact; allowing R8 to rename them makes
# Firestore see no serializable properties in minified release builds.
-keep class com.jenil.f1comp.data.model.UserProfile {
    *;
}

# These Room entity DTOs are also deserialised by Gson in Converters.
-keep,allowobfuscation class com.jenil.f1comp.data.local.entity.DriverInfo {
    *;
}
-keep,allowobfuscation class com.jenil.f1comp.data.local.entity.ComparisonSet {
    *;
}
-keep,allowobfuscation class com.jenil.f1comp.data.local.entity.StandingsComparison {
    *;
}
-keepclassmembers class com.jenil.f1comp.data.local.entity.DriverInfo {
    <fields>;
}
-keepclassmembers class com.jenil.f1comp.data.local.entity.ComparisonSet {
    <fields>;
}
-keepclassmembers class com.jenil.f1comp.data.local.entity.StandingsComparison {
    <fields>;
}
