package kr.stonecold.zuitweak.common

import androidx.core.content.edit
import de.robv.android.xposed.XSharedPreferences
import java.io.File

@Suppress("unused")
object XposedPrefsUtil {
    fun isFeatureEnabled(featureName: String, defaultValue: Boolean = true): Boolean {
        return if(File(Constants.PREF_PATH + "/" + featureName).exists()) {
            true
        } else {
            defaultValue
        }
    }

    fun reload() {
    }
}
