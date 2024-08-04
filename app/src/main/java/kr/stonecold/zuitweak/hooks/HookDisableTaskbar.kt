package kr.stonecold.zuitweak.hooks

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

@Suppress("unused")
class HookDisableTaskbar : HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.COMMON,
        title = "작업표시줄 표시 비활성화",
        description = "동작탐색 시 작업 표시줄 표시 기능을 제거합니다. (3버튼 탐식시 버튼 사라짐)",
        defaultSelected = false,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = emptyArray()
    override val hookTargetPackage: Array<String> = arrayOf("com.zui.launcher", "com.android.settings", "com.android.systemui")
    override val hookTargetPackageOptional: Array<String> = arrayOf("com.zui.desktoplauncher")

    private var isPad: ThreadLocal<Boolean?> = ThreadLocal.withInitial { null }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.zui.launcher", "com.zui.desktoplauncher" -> {
                executeHooks(
                    lpparam,
                    ::hookDeviceProfileBuilderBuild,
                )
            }

            "com.android.settings" -> {
                executeHooks(
                    lpparam,
                    ::hookSettingsHomepageActivityOnCreate,
                )
            }

            "com.android.systemui" -> {
                executeHooks(
                    lpparam,
                    ::hookUtilitiesIsTablet,
                    ::hookXSystemUtilIsPad,
                    ::hookNavigationBarInflaterViewOnLikelyDefaultLayoutChange,
                )
            }
        }
    }

    private fun hookDeviceProfileBuilderBuild(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "${lpparam.packageName}.DeviceProfile\$Builder"
        val methodName = "build"
        val parameterTypes = arrayOf<Any>(Boolean::class.java)
        val callback = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                try {
                    if (param.hasThrowable()) {
                        return
                    }
                    val profile = param.result
                    if (XposedHelpers.getIntField(profile, "displayId") == 0) {
                        XposedHelpers.setBooleanField(profile, "isTaskbarPresent", false)
                    }
                } catch (e: Throwable) {
                    handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookSettingsHomepageActivityOnCreate(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settings.homepage.SettingsHomepageActivity"
        val methodName = "onCreate"
        val parameterTypes = arrayOf<Any>(Bundle::class.java)
        val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    val activity = param.thisObject as Activity
                    val window = activity.window
                    window.navigationBarColor = 0x00000000
                    @Suppress("DEPRECATION")
                    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                } catch (e: Throwable) {
                    handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookUtilitiesIsTablet(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.systemui.shared.recents.utilities.Utilities"
        val methodName = "isTablet"
        val parameterTypes = arrayOf<Any>(Context::class.java)
        val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    val stack = Thread.currentThread().stackTrace
                    for (line in stack) {
                        if (line.className.contains("NavigationBarController")) {
                            param.result = false
                            return
                        }
                    }
                } catch (e: Throwable) {
                    handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookXSystemUtilIsPad(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.systemui.util.XSystemUtil"
        val methodName = "isPad"
        val parameterTypes = emptyArray<Any>()
        val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    val hookedValue = isPad.get()
                    if (hookedValue != null) {
                        param.result = hookedValue == true
                    }
                } catch (e: Throwable) {
                    handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookNavigationBarInflaterViewOnLikelyDefaultLayoutChange(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.systemui.navigationbar.NavigationBarInflaterView"
        val methodName = "onLikelyDefaultLayoutChange"
        val parameterTypes = emptyArray<Any>()
        val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    isPad.set(false)
                } catch (e: Throwable) {
                    handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }

            override fun afterHookedMethod(param: MethodHookParam?) {
                try {
                    isPad.set(null)
                } catch (e: Throwable) {
                    handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }
}
