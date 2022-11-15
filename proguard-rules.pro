# new R8 stuff
#-allowaccessmodification
-assumevalues class android.os.Build$VERSION {
    int SDK_INT return 21..1000;
}

-keepnames class com.squareup.moshi.JsonWriter, com.squareup.moshi.JsonReader, com.squareup.moshi.JsonAdapter
