package kr.stonecold.zuitweak.common

import de.robv.android.xposed.XSharedPreferences

@Suppress("unused")
object XposedPrefsUtil {
    private val xsp: XSharedPreferences by lazy {
        XSharedPreferences(Constants.APPLICATION_ID, Constants.PREFS_NAME).apply {
            makeWorldReadable()
        }
    }

    fun isFeatureEnabled(featureName: String, defaultValue: Boolean = true): Boolean {
        xsp.reload()
        return xsp.getBoolean(featureName, defaultValue)
    }

    fun reload() {
        xsp.reload()
    }
}
