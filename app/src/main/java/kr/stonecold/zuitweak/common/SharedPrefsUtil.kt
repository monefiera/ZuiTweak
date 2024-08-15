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

    @Suppress("UNCHECKED_CAST")
    fun <T> getOptionValue(key: String, defaultValue: T): T {
        if (!isInitialized) {
            throw UninitializedPropertyAccessException("SharedPrefsUtil has not been initialized. Call init(context) first.")
        }
        return when(defaultValue) {
            is Boolean -> sharedPreferences.getBoolean(key, defaultValue) as T
            is Int ->sharedPreferences.getInt(key, defaultValue) as T
            is Float -> sharedPreferences.getFloat(key, defaultValue) as T
            is Long -> sharedPreferences.getLong(key, defaultValue) as T
            is String -> sharedPreferences.getString(key, defaultValue) as T
            else -> throw IllegalArgumentException("Unsupported type.")
        }
    }

    fun <T> setOptionValue(key: String, value: T) {
        if (!isInitialized) {
            throw UninitializedPropertyAccessException("SharedPrefsUtil has not been initialized. Call init(context) first.")
        }

        val editor = sharedPreferences.edit()

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
