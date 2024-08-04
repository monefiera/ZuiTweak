package kr.stonecold.zuitweak.common

import de.robv.android.xposed.XposedBridge
import kr.stonecold.zuitweak.BuildConfig

@Suppress("unused")
object XposedUtil {
    enum class LogLevel(val level: Int) {
        TRACE(0),
        DEBUG(1),
        VERBOSE(2),
        INFO(3),
        WARNING(4),
        ERROR(5),
        FATAL(6),
        NONE(7)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    var currentLogLevel: LogLevel = if (BuildConfig.DEBUG) {
        LogLevel.DEBUG
    } else {
        LogLevel.INFO
    }

    fun xposedTrace(tag: String, message: String) {
        if (currentLogLevel.level <= LogLevel.TRACE.level) {
            XposedBridge.log("$tag: $message")
        }
    }

    fun xposedDebug(tag: String, message: String) {
        if (currentLogLevel.level <= LogLevel.DEBUG.level) {
            XposedBridge.log("$tag: $message")
        }
    }

    fun xposedVerbose(tag: String, message: String) {
        if (currentLogLevel.level <= LogLevel.VERBOSE.level) {
            XposedBridge.log("$tag: $message")
        }
    }

    fun xposedInfo(tag: String, message: String) {
        if (currentLogLevel.level <= LogLevel.INFO.level) {
            XposedBridge.log("$tag: $message")
        }
    }

    fun xposedWarning(tag: String, message: String) {
        if (currentLogLevel.level <= LogLevel.WARNING.level) {
            XposedBridge.log("$tag: $message")
        }
    }

    fun xposedError(tag: String, message: String) {
        if (currentLogLevel.level <= LogLevel.ERROR.level) {
            XposedBridge.log("$tag: $message")
        }
    }

    fun xposedFatal(tag: String, message: String) {
        if (currentLogLevel.level <= LogLevel.FATAL.level) {
            XposedBridge.log("$tag: $message")
        }
    }

    fun xposedException(tag: String, message: String) {
        if (currentLogLevel.level <= LogLevel.ERROR.level) {
            XposedBridge.log("$tag: $message")
        }
    }
}
