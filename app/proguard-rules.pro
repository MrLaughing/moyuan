# Keep AndroidX annotations
-keep class androidx.annotation.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep class **._MembersInjector { *; }

# Room
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase

# Retrofit / OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepattributes Signature
-keepattributes *Annotation*

# Gson
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keep class com.mrlaughing.moyuan.data.remote.dto.** { *; }
