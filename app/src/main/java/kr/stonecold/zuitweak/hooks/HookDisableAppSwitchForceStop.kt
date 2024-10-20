package kr.stonecold.zuitweak.hooks

import android.R.attr.classLoader
import android.content.Context
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kr.stonecold.zuitweak.R
import kr.stonecold.zuitweak.common.*


@Suppress("unused")
class HookDisableAppSwitchForceStop : HookBaseHandleLoadPackage() {
    override val menuItem
        get() = HookMenuItem(
            category = HookMenuCategory.UNFUCKZUI,
            title = LanguageUtil.getString(R.string.hook_disable_app_switch_force_stop_title),
            description = LanguageUtil.getString(R.string.hook_disable_app_switch_force_stop_desc),
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
