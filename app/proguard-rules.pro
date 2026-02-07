# Add project specific ProGuard rules here.
# By default, the flags in this used by the SDK are already in
# default configuration
-keepattributes Signature
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
