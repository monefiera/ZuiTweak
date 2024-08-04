package kr.stonecold.zuitweak.hooks

import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_LoadPackage

@Suppress("unused")
class HookEnableBatteryOverheatNotify : HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.ROW,
        title = "배터리 전력 소비 경고 활성화",
        description = "비정상적인 전력 소비 경고를 활성화합니다.",
        defaultSelected = false,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = arrayOf("ROW")
    override val hookTargetPackage: Array<String> = arrayOf("com.android.settings")
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.android.settings" -> {
                executeHooks(
                    lpparam,
                    ::hookZuiBatteryOverheatPreferenceControllerGetAvailabilityStatus,
                )
            }
        }
    }

    private fun hookZuiBatteryOverheatPreferenceControllerGetAvailabilityStatus(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settings.fuelgauge.ZuiBatteryOverheatPreferenceController"
        val methodName = "getAvailabilityStatus"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant(0)

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }
}
