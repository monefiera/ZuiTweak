package kr.stonecold.zuitweak.hooks

import android.content.Context
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_LoadPackage

@Suppress("unused")
class HookEnableWLANTether : HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.ROW,
        title = "WLAN 핫스팟 활성화",
        description = "WLAN 핫스팟 기능을 활성화 합니다.",
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
                    ::hookTopLevelTetherPreferenceControllerGetAvailabilityStatus,
                    ::hookWifiEnterpriseRestrictionUtilsIsWifiTetheringAllowed,
                    ::hookWifiTetherPreferenceControllerIsAvailable,
                    ::hookUtilsIsSupportTether,
                )
            }
        }
    }

    private fun hookTopLevelTetherPreferenceControllerGetAvailabilityStatus(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.lenovo.settings.homepage.controller.TopLevelTetherPreferenceController"
        val methodName = "getAvailabilityStatus"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant(0)

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookWifiEnterpriseRestrictionUtilsIsWifiTetheringAllowed(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settingslib.wifi.WifiEnterpriseRestrictionUtils"
        val methodName = "isWifiTetheringAllowed"
        val parameterTypes = arrayOf<Any>(Context::class.java)
        val callback = XC_MethodReplacement.returnConstant(true)

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookWifiTetherPreferenceControllerIsAvailable(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settings.wifi.tether.WifiTetherPreferenceController"
        val methodName = "isAvailable"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant(true)

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookUtilsIsSupportTether(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settings.Utils"
        val methodName = "isSupportTether"
        val parameterTypes = arrayOf<Any>(Context::class.java)
        val callback = XC_MethodReplacement.returnConstant(true)

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }
}
