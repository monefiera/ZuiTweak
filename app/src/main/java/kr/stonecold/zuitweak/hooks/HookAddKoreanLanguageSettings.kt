package kr.stonecold.zuitweak.hooks

import android.content.Context
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kr.stonecold.zuitweak.common.Util
import java.util.Locale

@Suppress("unused")
class HookAddKoreanLanguageSettings : HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.PRC,
        title = "한국어 설정 활성화",
        description = "언어 설정에 한국어 설정을 추가합니다.",
        defaultSelected = false,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = arrayOf("PRC")
    override val hookTargetPackage: Array<String> = arrayOf("com.android.settings")
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun isEnabled(): Boolean {
        val zuiKrPatchEnabled = Util.getProperty("ro.stonecold.krpatch.enabled", "false").uppercase()
        val testModeEnabled = Util.getProperty("persist.sys.lenovo.is_test_mode", "false").uppercase()
        return zuiKrPatchEnabled == "TRUE" || testModeEnabled == "TRUE"
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.android.settings" -> {
                executeHooks(
                    lpparam,
                    ::hookLocaleListEditorGetUserLocaleList,
                    ::hookLocaleListEditorGetAllLocaleList,
                    ::hookLocalePickerAxGetSupportedLocales,
                    ::hookUtilsGetChangedName,
                )
            }
        }
    }

    private fun hookLocaleListEditorGetUserLocaleList(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settings.localepicker.LocaleListEditor"
        val methodName = "getUserLocaleList"
        val parameterTypes = emptyArray<Any>()
        val callback = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                try {
                    val originalList = param.result as List<*>
                    val koKRLocaleInfo = XposedHelpers.callStaticMethod(
                        XposedHelpers.findClass("com.android.internal.app.LocaleStore", lpparam.classLoader),
                        "getLocaleInfo",
                        Locale.forLanguageTag("ko-KR")
                    )

                    if (!originalList.contains(koKRLocaleInfo)) {
                        val newList = ArrayList(originalList)
                        newList.add(koKRLocaleInfo)
                        param.result = newList
                    }
                } catch (e: Throwable) {
                    handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookLocaleListEditorGetAllLocaleList(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settings.localepicker.LocaleListEditor"
        val methodName = "getAllLocaleList"
        val parameterTypes = emptyArray<Any>()
        val callback = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                try {
                    val originalList = param.result as List<*>
                    val koKRLocaleInfo = XposedHelpers.callStaticMethod(
                        XposedHelpers.findClass("com.android.internal.app.LocaleStore", lpparam.classLoader),
                        "getLocaleInfo",
                        Locale.forLanguageTag("ko-KR")
                    )

                    if (!originalList.contains(koKRLocaleInfo)) {
                        val newList = ArrayList(originalList)
                        newList.add(koKRLocaleInfo)
                        param.result = newList
                    }
                } catch (e: Throwable) {
                    handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookLocalePickerAxGetSupportedLocales(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settings.localepicker.LocalePickerAx"
        val methodName = "getSupportedLocales"
        val parameterTypes = arrayOf<Any>(Context::class.java)
        val callback = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                try {
                    val originalLocales = param.result as Array<*>
                    val localesList = originalLocales.toMutableList()

                    // Check and add "ko-KR" if not already present
                    if (!localesList.contains("ko-KR")) {
                        localesList.add("ko-KR")
                        // Set the modified result back to param
                        param.result = localesList.toTypedArray()
                    }
                } catch (e: Throwable) {
                    handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookUtilsGetChangedName(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settings.Utils"
        val methodName = "getChangedName"
        val parameterTypes = arrayOf<Any>(
            "com.android.internal.app.LocaleStore\$LocaleInfo",
            Context::class.java
        )
        val callback = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                try {
                    val result = param.result as String
                    val localeInfo = param.args[0]
                    val localeInfoClass = localeInfo::class.java
                    val getIdMethod = localeInfoClass.getMethod("getId")
                    val id = getIdMethod.invoke(localeInfo) as String

                    if (result.isEmpty() && id == "ko-KR") {
                        param.result = "한국어"
                    }
                } catch (e: Throwable) {
                    handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }
}
