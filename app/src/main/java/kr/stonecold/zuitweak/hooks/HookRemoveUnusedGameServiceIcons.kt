package kr.stonecold.zuitweak.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kr.stonecold.zuitweak.R
import kr.stonecold.zuitweak.common.*

@Suppress("unused")
class HookRemoveUnusedGameServiceIcons : HookBaseHandleLoadPackage() {
    override val menuItem
        get() = HookMenuItem(
            category = HookMenuCategory.COMMON,
            title = LanguageUtil.getString(R.string.hook_remove_unused_game_service_icons_title),
            description = LanguageUtil.getString(R.string.hook_remove_unused_game_service_icons_desc),
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
