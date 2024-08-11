package kr.stonecold.zuitweak.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kr.stonecold.zuitweak.common.XposedUtil

@Suppress("unused")
class HookEnableTurnScreenOn : HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.COMMON,
        title = "화면 켜기 허용",
        description = "앱에서 화면을 켜기 허용을 활성화 합니다.",
        defaultSelected = false,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = arrayOf("ROW")
    override val hookTargetVersion: Array<String> = emptyArray()

    override val hookTargetPackage: Array<String> = arrayOf("com.android.settings")
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.android.settings" -> {
                hookSettingsPreferenceFragmentRemovePreference(lpparam)
                hookLenovoUtilIsPrcVersion(lpparam)
            }
        }
    }

    private fun hookSettingsPreferenceFragmentRemovePreference(lpparam: XC_LoadPackage.LoadPackageParam) {
        val preferenceNamesToCheck = setOf(
            "turn_screen_on",
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
                    XposedUtil.handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookLenovoUtilIsPrcVersion(lpparam: XC_LoadPackage.LoadPackageParam) {
        val matchCriteria = arrayOf(
            "com.android.settings.applications.specialaccess.SpecialAccessSettings" to emptyArray<String>(),
        )
        val className = "com.lenovo.common.utils.LenovoUtils"
        val methodName = "isPrcVersion"
        val parameterTypes = emptyArray<Any>()
        val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    val stackTrace = Thread.currentThread().stackTrace
                    val calledFromElement = stackTrace.find { element ->
                        matchCriteria.any { (className, methods) ->
                            if (methods.isEmpty()) {
                                element.className.startsWith(className)
                            } else {
                                element.className == className && methods.contains(element.methodName)
                            }
                        }
                    }

                    if (calledFromElement != null) {
                        XposedUtil.xposedDebug(tag, "$methodName method called from class: ${calledFromElement.className}, method: ${calledFromElement.methodName}")
                        param.result = true
                    }
                } catch (e: Throwable) {
                    XposedUtil.handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }
}
