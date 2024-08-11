package kr.stonecold.zuitweak.hooks

import android.content.Context
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kr.stonecold.zuitweak.common.XposedUtil

@Suppress("unused")
class HookDisableVirusScan : HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.UNFUCKZUI,
        title = "Virus Scan 비활성화",
        description = "SafeCenter의 Virus Scan 기능을 비활성화 합니다.",
        defaultSelected = false,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = arrayOf("PRC")
    override val hookTargetVersion: Array<String> = emptyArray()

    override val hookTargetPackage: Array<String> = arrayOf("com.zui.safecenter")
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.zui.safecenter" -> {
                hookManagerCreatorFGetManager(lpparam)
                hookAntiVirusInterfaceInitTMSApplication(lpparam)
            }
        }
    }

    private fun hookManagerCreatorFGetManager(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "tmsdk.fg.creator.ManagerCreatorF"
        val methodName = "getManager"
        val parameterTypes = arrayOf<Any>(Class::class.java)
        val callback = XC_MethodReplacement.returnConstant(null)

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookAntiVirusInterfaceInitTMSApplication(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.lenovo.safecenter.antivirus.external.AntiVirusInterface"
        val methodName = "initTMSApplication"
        val parameterTypes = arrayOf<Any>(Context::class.java, Boolean::class.java)
        val callback = XC_MethodReplacement.returnConstant(null)

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }
}
