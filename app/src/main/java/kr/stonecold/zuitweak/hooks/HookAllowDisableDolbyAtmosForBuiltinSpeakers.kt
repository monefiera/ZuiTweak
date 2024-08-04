package kr.stonecold.zuitweak.hooks

import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kotlin.String
import kotlin.Suppress

@Suppress("unused")
class HookAllowDisableDolbyAtmosForBuiltinSpeakers : HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.COMMON,
        title = "내장 스피커 Dolby Atmos 비활성화 허용",
        description = "Dolby Atmos 설정 중 내장 스피커 비활성화를 허용합니다.",
        defaultSelected = false,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = emptyArray()
    override val hookTargetPackage: Array<String> = arrayOf("com.android.settings", "com.android.systemui")
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.android.settings" -> {
                executeHooks(
                    lpparam,
                    ::hookDolbyAtmosPreferenceFragmentGetheadsetStatus,
                )
            }

            "com.android.systemui" -> {
                executeHooks(
                    lpparam,
                    ::hookQDolbyAtmosTileIsHeadSetConnect,
                    ::hookQDolbyAtmosDetailViewIsHeadSetConnect,
                )
            }
        }
    }

    private fun hookDolbyAtmosPreferenceFragmentGetheadsetStatus(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settings.dolby.DolbyAtmosPreferenceFragment"
        val methodName = "getheadsetStatus"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant(1)

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookQDolbyAtmosTileIsHeadSetConnect(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.systemui.qs.tiles.QDolbyAtmosTile"
        val methodName = "isHeadSetConnect"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant(true)

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookQDolbyAtmosDetailViewIsHeadSetConnect(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.systemui.qs.tiles.QDolbyAtmosDetailView"
        val methodName = "isHeadSetConnect"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant(true)

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }
}
