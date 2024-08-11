package kr.stonecold.zuitweak.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kr.stonecold.zuitweak.common.XposedUtil

@Suppress("unused")
class HookRemoveUnusedGameServiceIcons : HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.COMMON,
        title = "게임 서비스 미사용 아이콘 제거",
        description = "게임 서비스에서 보이는 QQ, WeChat, Line, WhatsApp 아이콘을 제거합니다.",
        defaultSelected = false,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = emptyArray()
    override val hookTargetVersion: Array<String> = emptyArray()

    override val hookTargetPackage: Array<String> = arrayOf("com.zui.game.service")
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.zui.game.service" -> {
                hookDeviceUtilsIsRow(lpparam)
                hookSettingsIsRow(lpparam)
                hookCommonUtilKtIsROWVersion(lpparam)
                hookFeatureKeyCompanionCreateByKeys(lpparam)
            }
        }
    }

    private fun hookDeviceUtilsIsRow(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.zui.util.DeviceUtils"
        val methodName = "isRow"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant(true)

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookSettingsIsRow(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.zui.game.service.di.Settings"
        val methodName = "isRow"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant(true)

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookCommonUtilKtIsROWVersion(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.zui.ugame.gamesetting.util.CommonUtilKt"
        val methodName = "isROWVersion"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant(true)

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookFeatureKeyCompanionCreateByKeys(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.zui.game.service.FeatureKey\$Companion"
        val methodName = "createByKeys"
        val parameterTypes = arrayOf<Any>(Array<String>::class.java)
        val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    @Suppress("UNCHECKED_CAST")
                    val keys = param.args[0] as? Array<String> ?: return
                    val excludedKeys = listOf("key_line", "key_qq", "key_we_chat", "key_whats_app")
                    val filteredKeys = keys.filterNot { it in excludedKeys }
                    param.args[0] = filteredKeys.toTypedArray()
                } catch (e: Throwable) {
                    XposedUtil.handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }
}
