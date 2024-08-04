package kr.stonecold.zuitweak.hooks

import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_LoadPackage

@Suppress("unused")
class HookDisableStudyLauncher : HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.PRC,
        title = "Study launcher 비활성화",
        description = "Study launcher 기능을 비활성화 합니다.",
        defaultSelected = false,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = arrayOf("PRC")
    override val hookTargetPackage: Array<String> = arrayOf("com.android.settings")
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.android.settings" -> {
                executeHooks(
                    lpparam,
                    ::hookTopLevelStudyLauncherPreferenceControllerGetAvailabilityStatus,
                )
            }
        }
    }

    private fun hookTopLevelStudyLauncherPreferenceControllerGetAvailabilityStatus(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.lenovo.settings.homepage.controller.TopLevelStudyLauncherPreferenceController"
        val methodName = "getAvailabilityStatus"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant(3)

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }
}
