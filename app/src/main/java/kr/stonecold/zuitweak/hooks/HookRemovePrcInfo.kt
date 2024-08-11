package kr.stonecold.zuitweak.hooks

import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kr.stonecold.zuitweak.common.XposedUtil

@Suppress("unused")
class HookRemovePrcInfo : HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.PRC,
        title = "PRC 정보 삭제",
        description = "숨겨져 있는 PRC 관련 정보를 삭제합니다.",
        defaultSelected = false,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = arrayOf("PRC")
    override val hookTargetVersion: Array<String> = emptyArray()

    override val hookTargetPackage: Array<String> = arrayOf("com.android.settings")
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.android.settings" -> {
                hookAboutDeviceFragmentUpdateRowPreference(lpparam)
                hookAboutDeviceFragmentUpdateShenqiService(lpparam)
                hookAboutDeviceFragmentCheckIntentTblenovoCenter(lpparam)
            }
        }
    }

    private fun hookAboutDeviceFragmentUpdateRowPreference(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settings.deviceinfo.aboutphone.AboutDeviceFragment"
        val methodName = "updateRowPreference"
        val parameterTypes = emptyArray<Any>()
        val callback = object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) {
                try {
                    XposedHelpers.callMethod(param.thisObject, "removePreference", "legal_container")
                    XposedHelpers.callMethod(param.thisObject, "removePreference", "regulatory_information")
                    XposedHelpers.callMethod(param.thisObject, "removePreference", "key_shenqi_serve_line")
                    XposedHelpers.callMethod(param.thisObject, "removePreference", "key_shenqi_serve")
                } catch (e: Throwable) {
                    XposedUtil.handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookAboutDeviceFragmentUpdateShenqiService(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settings.deviceinfo.aboutphone.AboutDeviceFragment"
        val methodName = "updateShenqiService"
        val parameterTypes = emptyArray<Any>()
        val callback = object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) {
                try {
                    XposedHelpers.callMethod(param.thisObject, "removePreference", "key_shenqi_serve")
                } catch (e: Throwable) {
                    XposedUtil.handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookAboutDeviceFragmentCheckIntentTblenovoCenter(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settings.deviceinfo.aboutphone.AboutDeviceFragment"
        val methodName = "checkIntentTblenovoCenter"
        val parameterTypes = emptyArray<Any>()
        val callback = object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) {
                try {
                    XposedHelpers.callMethod(param.thisObject, "removePreference", "feed_back")
                } catch (e: Throwable) {
                    XposedUtil.handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }
}
