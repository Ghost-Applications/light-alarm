// load in keystore values if available otherwise load in debug values
extra["keystorePassword"] = when {
    project.hasProperty("cash.andrew.lightalarm.keystorePassword") -> properties["cash.andrew.lightalarm.keystorePassword"]
    else -> System.getenv("LIGHT_ALARM_KEYSTORE_PASSWORD") ?: "android"
}


extra["aliasKeyPassword"] = when {
    project.hasProperty("cash.andrew.lightalarm.aliasKeyPassword") -> project.properties["cash.andrew.lightalarm.aliasKeyPassword"]
    else -> System.getenv("LIGHT_ALARM_ALIAS_KEY_PASSWORD") ?: "android"
}


extra["storeKeyAlias"] = when {
    project.hasProperty("cash.andrew.lightalarm.storeKeyAlias") -> project.properties["cash.andrew.lightalarm.storeKeyAlias"]
    else -> System.getenv("LIGHT_ALARM_STORE_KEY_ALIAS") ?: "androiddebugkey"
}

extra["keystoreLocation"] = when {
    hasProperty("cash.andrew.lightalarm.keystoreLocation") -> properties["cash.andrew.lightalarm.keystoreLocation"]
    else -> System.getenv("LIGHT_ALARM_KEYSTORE_LOCATION") ?: "keys/debug.keystore"
}
