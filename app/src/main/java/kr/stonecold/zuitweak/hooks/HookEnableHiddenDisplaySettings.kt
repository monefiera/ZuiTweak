package kr.stonecold.zuitweak.hooks

import android.content.Context
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_LoadPackage

@Suppress("unused")
class HookEnableHiddenDisplaySettings : HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.COMMON,
        title = "디스플레이 설정 확장",
        description = "숨겨진 디스플레이 설정 항목을 활성화 합니다.",
        defaultSelected = false,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = emptyArray()
    override val hookTargetPackage: Array<String> = arrayOf("com.android.settings")
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.android.settings" -> {
                executeHooks(lpparam,
                    ::hookUtilsIsFeatureEnabled,
                    ::hookSettingsPreferenceFragmentRemovePreference,
                )
            }
        }
    }

    private fun hookUtilsIsFeatureEnabled(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settings.Utils"
        val methodName = "isFeatureEnabled"
        val parameterTypes = arrayOf<Any>(String::class.java, Context::class.java)
        val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    val featureName = param.args[0] as String
                    if (featureName == "config_zuimiravision_enabled") {
                        param.result = true
                    }
                } catch (e: Throwable) {
                    handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookSettingsPreferenceFragmentRemovePreference(lpparam: XC_LoadPackage.LoadPackageParam) {
        val preferenceNamesToCheck = setOf(
            "gaming_hdr_enhancement",
            "screensaver",
            "video_color_enhance",
            "video_quality_enhance",
            "display_enhancement",
        )

        val className = "com.android.settings.SettingsPreferenceFragment"
        val methodName = "removePreference"
        val parameterTypes = arrayOf<Any>(String::class.java)
        val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    val preferenceName = param.args[0] as String
                    if (preferenceName in preferenceNamesToCheck) {
                        param.result = false
                    }
                } catch (e: Throwable) {
                    handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }
}
