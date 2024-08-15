package kr.stonecold.zuitweak.common

import androidx.core.content.edit
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

    @Suppress("UNCHECKED_CAST")
    fun <T> getOptionValue(key: String, defaultValue: T): T {
        xsp.reload()

        return when (defaultValue) {
            is Boolean -> xsp.getBoolean(key, defaultValue) as T
            is Int -> xsp.getInt(key, defaultValue) as T
            is Float -> xsp.getFloat(key, defaultValue) as T
            is Long -> xsp.getLong(key, defaultValue) as T
            is String -> xsp.getString(key, defaultValue) as T
            else -> throw IllegalArgumentException("Unsupported type.")
        }
    }

    fun <T> setOptionValue(key: String, value: T) {
        xsp.reload()

        @Suppress("DEPRECATION")
        val editor = xsp.edit()

        when (value) {
            is Boolean -> editor.putBoolean(key, value)
            is Int -> editor.putInt(key, value)
            is Float -> editor.putFloat(key, value)
            is Long -> editor.putLong(key, value)
            is String -> editor.putString(key, value)
            else -> throw IllegalArgumentException("Unsupported type.")
        }

        editor.apply()
    }
}
