package kr.stonecold.zuitweak.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import kr.stonecold.zuitweak.common.XposedUtil


@Suppress("unused")
class DebugHandleLoadPackage : HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.DEVELOPMENT,
        title = "디버그 (HandleLoadPackage)",
        description = "디버그 (HandleLoadPackage)",
        defaultSelected = false,
        isDebug = true,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = emptyArray()
    override val hookTargetVersion: Array<String> = emptyArray()

    override val hookTargetPackage: Array<String> = arrayOf("kr.stonecold.exmaple")
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when(lpparam.packageName) {
            "kr.stonecold.exmaple" -> {
                hookDebug(lpparam)
            }
        }
    }

    private fun hookDebug(lpparam: LoadPackageParam) {
        val className = "kr.stonecold.exmaple.app"
        val methodName = "sampleMethod"
        val parameterTypes = arrayOf<Any>(String::class.java)
        val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                XposedUtil.xposedHookMessage(tag, lpparam, className, methodName, param, "before")
            }

            override fun afterHookedMethod(param: MethodHookParam) {
                XposedUtil.xposedHookMessage(tag, lpparam, className, methodName, param, "after")
            }
        }

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }
}
