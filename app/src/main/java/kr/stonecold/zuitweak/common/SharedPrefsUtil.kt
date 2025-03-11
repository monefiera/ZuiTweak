package kr.stonecold.zuitweak.common

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.util.Log
import java.io.File

@Suppress("unused")
object SharedPrefsUtil {
    private lateinit var sharedPreferences: SharedPreferences

    var isInitialized: Boolean = false
        private set

    fun init() {
        try {
            @Suppress("DEPRECATION")
            setupPrefs()
            isInitialized = true
        } catch (e: Exception) {
            Log.d("SharedPrefsUtil", "Module disabled due to an exception: ${e.message}")
            isInitialized = false
        }
    }

    private fun setupPrefs() {
        val appPrefsDir = "${Constants.BASE_DIR}/prefs"

        val cmd = arrayOf("su", "-c", """
            mkdir -p ${Constants.PREF_PATH} && \
            chown root:root ${Constants.BASE_DIR} && chmod 777 ${Constants.BASE_DIR} && chcon u:object_r:magisk_file:s0 ${Constants.BASE_DIR} && \
            chown root:root $appPrefsDir && chmod 777 $appPrefsDir && chcon u:object_r:magisk_file:s0 $appPrefsDir && \
            chown root:root ${Constants.PREF_PATH} && chmod 777 ${Constants.PREF_PATH} && chcon u:object_r:magisk_file:s0 ${Constants.PREF_PATH}
        """.trimIndent())

        try {
            val process = Runtime.getRuntime().exec(cmd)
            val exitCode = process.waitFor()
            if (exitCode == 0) {
                println("Setup completed successfully.")
            } else {
                println("Setup failed with exit code $exitCode")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createFile(name: String) {
        val fullPath = "${Constants.PREF_PATH}/$name"
        val appPrefsDir = "${Constants.BASE_DIR}/prefs"

        val cmd = arrayOf("su", "-c", """
            mkdir -p ${Constants.PREF_PATH} && \
            chown root:root ${Constants.BASE_DIR} && chmod 777 ${Constants.BASE_DIR} && chcon u:object_r:magisk_file:s0 ${Constants.BASE_DIR} && \
            chown root:root $appPrefsDir && chmod 777 $appPrefsDir && chcon u:object_r:magisk_file:s0 $appPrefsDir && \
            chown root:root ${Constants.PREF_PATH} && chmod 777 ${Constants.PREF_PATH} && chcon u:object_r:magisk_file:s0 ${Constants.PREF_PATH}
            touch $fullPath
            chown root:root $fullPath && chmod 777 $fullPath && chcon u:object_r:magisk_file:s0 $fullPath
        """.trimIndent())

        try {
            val process = Runtime.getRuntime().exec(cmd)
            val exitCode = process.waitFor()
            if (exitCode == 0) {
                println("Setup completed successfully.")
            } else {
                println("Setup failed with exit code $exitCode")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteFile(name: String) {
        val fullPath = "${Constants.PREF_PATH}/$name"

        val cmd = arrayOf("su", "-c", """
            rm -rf $fullPath
        """.trimIndent())

        try {
            val process = Runtime.getRuntime().exec(cmd)
            val exitCode = process.waitFor()
            if (exitCode == 0) {
                println("Setup completed successfully.")
            } else {
                println("Setup failed with exit code $exitCode")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getOptionValue(key: String, defaultValue: Boolean): Boolean {
        if (!isInitialized) {
            throw UninitializedPropertyAccessException("SharedPrefsUtil has not been initialized. Call init(context) first.")
        }
        return if (File("${Constants.PREF_PATH}/$key").exists()) {
            true
        } else {
            defaultValue
        }
    }

    fun setOptionValue(key: String, value: Boolean) {
        if (!isInitialized) {
            throw UninitializedPropertyAccessException("SharedPrefsUtil has not been initialized. Call init(context) first.")
        }

        if (!File("${Constants.PREF_PATH}/$key").exists() && value) {
            createFile(key)
        } else if (File("${Constants.PREF_PATH}/$key").exists() && !value) {
            deleteFile(key)
        }
    }

    fun deleteOptionValue(key: String) {
        if (!isInitialized) {
            throw UninitializedPropertyAccessException("SharedPrefsUtil has not been initialized. Call init(context) first.")
        }

        if (File("${Constants.PREF_PATH}/$key").exists()) {
            deleteFile(key)
        }
    }

    fun getAllOptions(): Map<String, *> {
        if (!isInitialized) {
            throw UninitializedPropertyAccessException("SharedPrefsUtil has not been initialized. Call init(context) first.")
        }

        val dir = File(Constants.PREF_PATH)
        if (!dir.isDirectory) {
            return emptyMap<String, Any?>()
        }

        val files = dir.listFiles()
        return files?.associate { file ->
            file.name to true
        } ?: emptyMap<String, Any?>()
    }

    fun clearAllOptions() {
        if (!isInitialized) {
            throw UninitializedPropertyAccessException("SharedPrefsUtil has not been initialized. Call init(context) first.")
        }

        val cmd = arrayOf("su", "-c", """
            rm -rf ${Constants.PREF_PATH}/*
        """.trimIndent())

        try {
            val process = Runtime.getRuntime().exec(cmd)
            val exitCode = process.waitFor()
            if (exitCode == 0) {
                println("Setup completed successfully.")
            } else {
                println("Setup failed with exit code $exitCode")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
