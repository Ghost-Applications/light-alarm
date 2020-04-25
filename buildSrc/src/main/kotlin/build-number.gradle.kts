extra["buildNumber"] = when {
    hasProperty("cash.andrew.lightalarm.buildNumber") -> properties["cash.andrew.lightalarm.buildNumber"]
    else -> System.getenv("LIGHT_ALARM_BUILD_NUMBER") ?: ""
}
