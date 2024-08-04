package kr.stonecold.zuitweak.hooks

import android.view.View
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

@Suppress("unused")
class HookRemoveLockscreenShortcuts : HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.COMMON,
        title = "잠금 화면 바로 가기 제거",
        description = "잠금 화면 하단의 바로 가기를 제거합니다.",
        defaultSelected = false,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = emptyArray()
    override val hookTargetPackage: Array<String> = arrayOf("com.android.systemui")
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.android.systemui" -> {
                executeHooks(
                    lpparam,
                    ::hookKeyguardBottomAreaViewUpdateLeftAffordanceIcon,
                    ::hookKeyguardBottomAreaViewUpdateRightAffordanceIcon,
                    ::hookKeyguardBottomAreaViewUpdateCameraVisibility,
                )
            }
        }
    }

    private fun hookKeyguardBottomAreaViewUpdateLeftAffordanceIcon(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.systemui.statusbar.phone.KeyguardBottomAreaView"
        val methodName = "updateLeftAffordanceIcon"
        val parameterTypes = emptyArray<Any>()
        val callback = object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    try {
                        val mLeftAffordanceView = XposedHelpers.getObjectField(param.thisObject, "mLeftAffordanceView")
                        if (mLeftAffordanceView != null) {
                            XposedHelpers.callMethod(mLeftAffordanceView, "setVisibility", View.GONE)
                            param.result = null
                        }
                    } catch (e: Throwable) {
                        handleHookException(tag, e, className, methodName, *parameterTypes)
                    }
                }
            }

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookKeyguardBottomAreaViewUpdateRightAffordanceIcon(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.systemui.statusbar.phone.KeyguardBottomAreaView"
        val methodName = "updateRightAffordanceIcon"
        val parameterTypes = emptyArray<Any>()
        val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    val mRightAffordanceView = XposedHelpers.getObjectField(param.thisObject, "mRightAffordanceView")
                    if (mRightAffordanceView != null) {
                        XposedHelpers.callMethod(mRightAffordanceView, "setVisibility", View.GONE)
                        param.result = null
                    }
                } catch (e: Throwable) {
                    handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookKeyguardBottomAreaViewUpdateCameraVisibility(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.systemui.statusbar.phone.KeyguardBottomAreaView"
        val methodName = "updateCameraVisibility"
        val parameterTypes = emptyArray<Any>()
        val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    val mRightAffordanceView = XposedHelpers.getObjectField(param.thisObject, "mRightAffordanceView")
                    if (mRightAffordanceView != null) {
                        XposedHelpers.callMethod(mRightAffordanceView, "setVisibility", View.GONE)
                        param.result = null
                    }
                } catch (e: Throwable) {
                    handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }
}
