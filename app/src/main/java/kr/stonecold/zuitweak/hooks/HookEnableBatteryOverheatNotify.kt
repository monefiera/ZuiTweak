package kr.stonecold.zuitweak.hooks

import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kr.stonecold.zuitweak.common.Constants
import kr.stonecold.zuitweak.common.XposedUtil

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
    override val hookTargetVersion: Array<String> = emptyArray()

    override val hookTargetPackage: Array<String> = arrayOf("com.android.settings")
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.android.settings" -> {
                when (Constants.deviceVersion) {
                    "16.0" -> {
                        hookZuiBatteryOverheatPreferenceControllergetAvailabilityStatus(lpparam)
                    }

                    "15.0" -> {
                        hookZuiBatteryOverheatPreferenceControllerGetAvailabilityStatus15(lpparam)
                    }
                }
            }
        }
    }

    private fun hookZuiBatteryOverheatPreferenceControllergetAvailabilityStatus(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.lenovo.settings.fuelgauge.ZuiBatteryOverheatPreferenceController"
        val methodName = "getAvailabilityStatus"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant(0)

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookZuiBatteryOverheatPreferenceControllerGetAvailabilityStatus15(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settings.fuelgauge.ZuiBatteryOverheatPreferenceController"
        val methodName = "getAvailabilityStatus"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant(0)

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }
}
