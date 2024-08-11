package kr.stonecold.zuitweak.hooks

import android.content.Context
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kr.stonecold.zuitweak.common.XposedUtil

@Suppress("unused")
class HookDisableAppSwitchForceStop : HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.UNFUCKZUI,
        title = "앱 강제 종료 기능 비활성화",
        description = "App Switcher에서 App 제거시 강제 종료를 비활성화합니다.",
        defaultSelected = false,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = emptyArray()
    override val hookTargetVersion: Array<String> = emptyArray()

    override val hookTargetPackage: Array<String> = arrayOf("com.zui.launcher")
    override val hookTargetPackageOptional: Array<String> = arrayOf("com.zui.desktoplauncher")

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.zui.launcher", "com.zui.desktoplauncher" -> {
                hookActivityManagerWrapperRemoveAllRunningAppProcesses(lpparam)
                hookActivityManagerWrapperRemoveAppProcess(lpparam)
            }
        }
    }

    private fun hookActivityManagerWrapperRemoveAllRunningAppProcesses(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.systemui.shared.system.ActivityManagerWrapper"
        val methodName = "removeAllRunningAppProcesses"
        val parameterTypes = arrayOf<Any>(Context::class.java, java.util.ArrayList::class.java)
        val callback = XC_MethodReplacement.returnConstant(null)

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookActivityManagerWrapperRemoveAppProcess(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.systemui.shared.system.ActivityManagerWrapper"
        val methodName = "removeAppProcess"
        val parameterTypes = arrayOf<Any>(Context::class.java, Int::class.java, String::class.java, Int::class.java)
        val callback = XC_MethodReplacement.returnConstant(null)

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }
}
