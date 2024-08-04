package kr.stonecold.zuitweak.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

@Suppress("unused")
object SharedPrefsUtil {
    private lateinit var sharedPreferences: SharedPreferences
    var isInitialized: Boolean = false
        private set

    @SuppressLint("WorldReadableFiles")
    fun init(context: Context) {
        try {
            @Suppress("DEPRECATION")
            sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_WORLD_READABLE)
            isInitialized = true
        } catch (e: Exception) {
            Log.d("SharedPrefsUtil", "Module disabled due to an exception: ${e.message}")
            isInitialized = false
        }
    }

    fun getOptionValue(key: String, defaultValue: Boolean): Boolean {
        if (!isInitialized) {
            throw UninitializedPropertyAccessException("SharedPrefsUtil has not been initialized. Call init(context) first.")
        }
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun setOptionValue(key: String, value: Boolean) {
        if (!isInitialized) {
            throw UninitializedPropertyAccessException("SharedPrefsUtil has not been initialized. Call init(context) first.")
        }
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun deleteOptionValue(key: String) {
        if (!isInitialized) {
            throw UninitializedPropertyAccessException("SharedPrefsUtil has not been initialized. Call init(context) first.")
        }
        val editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }

    fun getAllOptions(): Map<String, *> {
        if (!isInitialized) {
            throw UninitializedPropertyAccessException("SharedPrefsUtil has not been initialized. Call init(context) first.")
        }
        return sharedPreferences.all
    }

    fun clearAllOptions() {
        if (!isInitialized) {
            throw UninitializedPropertyAccessException("SharedPrefsUtil has not been initialized. Call init(context) first.")
        }
        sharedPreferences.edit().clear().apply()
    }
}
