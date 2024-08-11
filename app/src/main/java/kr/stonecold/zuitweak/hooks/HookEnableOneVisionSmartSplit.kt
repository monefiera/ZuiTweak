package kr.stonecold.zuitweak.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kr.stonecold.zuitweak.common.Constants
import kr.stonecold.zuitweak.common.XposedUtil


@Suppress("unused")
class HookEnableOneVisionSmartSplit : HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.ROW,
        title = "Smart Split 활성화",
        description = "One Vision의 Smart Split 기능을 활성화 합니다. (Smart Rotation 대체)",
        defaultSelected = false,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = arrayOf("ROW")
    override val hookTargetVersion: Array<String> = emptyArray()

    override val hookTargetPackage: Array<String> = arrayOf("com.android.settings")
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    val matchCriteria = when (Constants.deviceVersion) {
        "16.0" -> {
            arrayOf(
                "com.lenovo.settings.onevision.OneVisionSettingsFragment" to emptyArray<String>(),
                "com.lenovo.settings.onevision.EmbeddingAppFragment" to emptyArray<String>(),
                "com.lenovo.settings.onevision.EmbeddingAppFragment\$RecyclerAdapter" to emptyArray<String>(),
                "com.lenovo.settings.onevision.EmbeddingAppFragment\$SettingsObserver" to emptyArray<String>(),
            )
        }

        "15.0" -> {
            arrayOf(
                "com.android.settings.onevision.OneVisionSettingsFragment" to emptyArray<String>(),
                "com.android.settings.applications.apphorizontal.AppHorizontalSettingsFragment" to emptyArray<String>(),
                "com.android.settings.applications.apphorizontal.AppHorizontalSettingsFragment\$RecyclerAdapter" to emptyArray<String>(),
                "com.android.settings.applications.apphorizontal.AppHorizontalSettingsFragment\$SettingsObserver" to emptyArray<String>(),
            )
        }

        else -> {
            emptyArray()
        }
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.android.settings" -> {
                when (Constants.deviceVersion) {
                    "16.0" -> {
                        hookLenovoUtilsIsPrcVersion(lpparam)
                        hookLenovoUtilsIsRowVersion(lpparam)
                    }

                    "15.0" -> {
                        hookUtilsIsPrcVersion(lpparam)
                        hookUtilsIsRowVersion(lpparam)
                    }
                }
            }
        }
    }

    private fun hookLenovoUtilsIsPrcVersion(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.lenovo.common.utils.LenovoUtils"
        val methodName = "isPrcVersion"
        val parameterTypes = emptyArray<Class<*>>()
        val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    val stackTrace = Thread.currentThread().stackTrace
                    val calledFromElement = stackTrace.find { element ->
                        matchCriteria.any { (className, methods) ->
                            element.className == className && !methods.contains(element.methodName)
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

    private fun hookUtilsIsPrcVersion(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settings.Utils"
        val methodName = "isPrcVersion"
        val parameterTypes = emptyArray<Class<*>>()
        val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    val stackTrace = Thread.currentThread().stackTrace
                    val calledFromElement = stackTrace.find { element ->
                        matchCriteria.any { (className, methods) ->
                            element.className == className && !methods.contains(element.methodName)
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

    private fun hookLenovoUtilsIsRowVersion(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.lenovo.common.utils.LenovoUtils"
        val methodName = "isRowVersion"
        val parameterTypes = emptyArray<Class<*>>()
        val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    val stackTrace = Thread.currentThread().stackTrace
                    val calledFromElement = stackTrace.find { element ->
                        matchCriteria.any { (className, methods) ->
                            element.className == className && !methods.contains(element.methodName)
                        }
                    }

                    if (calledFromElement != null) {
                        XposedUtil.xposedDebug(tag, "$methodName method called from class: ${calledFromElement.className}, method: ${calledFromElement.methodName}")
                        param.result = false
                    }
                } catch (e: Throwable) {
                    XposedUtil.handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookUtilsIsRowVersion(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settings.Utils"
        val methodName = "isRowVersion"
        val parameterTypes = emptyArray<Class<*>>()
        val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    val stackTrace = Thread.currentThread().stackTrace
                    val calledFromElement = stackTrace.find { element ->
                        matchCriteria.any { (className, methods) ->
                            element.className == className && !methods.contains(element.methodName)
                        }
                    }

                    if (calledFromElement != null) {
                        XposedUtil.xposedDebug(tag, "$methodName method called from class: ${calledFromElement.className}, method: ${calledFromElement.methodName}")
                        param.result = false
                    }
                } catch (e: Throwable) {
                    XposedUtil.handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }
}
