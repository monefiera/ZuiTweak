package kr.stonecold.zuitweak.common

import kr.stonecold.zuitweak.BuildConfig

@Suppress("unused")
object Constants {
    const val APPLICATION_ID = BuildConfig.APPLICATION_ID
    const val LANGAUGE_PREFS_NAME = "langauge_config"
    const val BASE_DIR = "/data/misc/ff6bac4e-9639-4b10-9a35-276e4f56c556"
    const val PREF_PATH = "$BASE_DIR/prefs/$APPLICATION_ID"

    val deviceModel: String by lazy {
        return@lazy Util.getModel()
    }

    val deviceRegion: String by lazy {
        return@lazy Util.getProperty("ro.config.lgsi.region", "UNKNOWN").uppercase()
    }

    val deviceVersion: String by lazy {
        return@lazy Util.getProperty("ro.com.zui.version", "UNKNOWN").uppercase()
    }
}
