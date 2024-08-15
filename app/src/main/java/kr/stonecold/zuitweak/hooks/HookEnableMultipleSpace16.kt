package kr.stonecold.zuitweak.hooks

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kr.stonecold.zuitweak.R
import kr.stonecold.zuitweak.common.*

@Suppress("unused")
class HookEnableMultipleSpace16 : HookBaseHandleLoadPackage() {
    override val menuItem
        get() = HookMenuItem(
            category = HookMenuCategory.ROW,
            title = LanguageUtil.getString(R.string.hook_enable_multiple_space_title),
            description = LanguageUtil.getString(R.string.hook_enable_multiple_space_desc),
            defaultSelected = false,
        )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = arrayOf("ROW")
    override val hookTargetVersion: Array<String> = emptyArray()

    override val hookTargetPackage: Array<String> = arrayOf("android", "com.android.systemui", "com.android.settings", "com.zui.launcher")
    override val hookTargetPackageOptional: Array<String> = arrayOf("com.zui.desktoplauncher")

    override fun isEnabledCustomCheck(): Boolean {
        val zuiMultispaceEnabled = Util.getProperty("ro.stonecold.zuimultispace.enabled", "false").uppercase()
        return zuiMultispaceEnabled == "TRUE"
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "android" -> {
                hookUserHandleSupportsMultiSpace(lpparam)
            }

            "com.android.systemui" -> {
                hookUserHandleSupportsMultiSpace(lpparam)
            }

            "com.android.settings" -> {
                hookUserHandleSupportsMultiSpace(lpparam)
                hookMultiSpaceConstantSupportMultiSpace(lpparam)
                hookUserSettingsCanSwitchUserNow(lpparam)
                hookMultipleSpaceManagerSendZuiAddProfileBroadcast(lpparam)
                hookStorageItemPreferenceShowArrowImageIfNeed(lpparam)
                hookUtilsGetTargetFragment(lpparam)
                hookLenovoUtilIsRowVersion(lpparam)
                hookLenovoUtilIsPrcVersion(lpparam)
            }

            "com.zui.launcher", "com.zui.desktoplauncher" -> {
                hookGraphicsUtilsIsZuiRow(lpparam)
                hookUtilitiesIsZuiRow(lpparam)
                hookUtilitiesIsOverlayEnabled(lpparam)
            }
        }
    }

    private fun hookUserHandleSupportsMultiSpace(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "android.os.UserHandle"
        val methodName = "supportsMultiSpace"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant(true)

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookMultiSpaceConstantSupportMultiSpace(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.lenovo.common.multispace.MultiSpaceConstant"
        val methodName = "supportMultiSpace"
        val parameterTypes = emptyArray<Any>()
        val callback = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                try {
                    if (param.result != true) {
                        val clazz = XposedHelpers.findClass(className, lpparam.classLoader)
                        XposedHelpers.setStaticIntField(clazz, "mSupportsMultiSpace", 1)
                        param.result = true
                    }
                } catch (e: Throwable) {
                    XposedUtil.handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookUserSettingsCanSwitchUserNow(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settings.users.UserSettings"
        val methodName = "canSwitchUserNow"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant(false)

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookMultipleSpaceManagerSendZuiAddProfileBroadcast(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.lenovo.settings.multispace.manager.MultipleSpaceManager"
        val methodName = "sendZuiAddProfileBroadcast"
        val parameterTypes = emptyArray<Any>()
        val callback = object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) {
                try {
                    val context = XposedHelpers.getObjectField(param.thisObject, "mContext") as? Context

                    if (context == null) {
                        XposedUtil.xposedDebug(tag, "mContext is null, nothing will be done.")
                        return
                    }

                    val intent = Intent("zui.intent.action.MANAGED_PROFILE_ADDED")
                    intent.setPackage("com.zui.launcher")
                    context.sendBroadcast(intent)
                } catch (e: Throwable) {
                    XposedUtil.handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookStorageItemPreferenceShowArrowImageIfNeed(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settings.deviceinfo.StorageItemPreference"
        val methodName = "showArrowImageIfNeed"
        val parameterTypes = arrayOf<Any>(
            "androidx.preference.PreferenceViewHolder"
        )
        val callback = object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) {
                return
            }
        }

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookUtilsGetTargetFragment(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settings.Utils"
        val methodName = "getTargetFragment"
        val parameterTypes = arrayOf<Any>(Activity::class.java, String::class.java, Bundle::class.java)
        val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    val profileFragmentBridgeClass = XposedHelpers.findClass("com.android.settings.dashboard.profileselector.ProfileFragmentBridge", lpparam.classLoader)

                    @Suppress("UNCHECKED_CAST")
                    val fragmentMap = XposedHelpers.getStaticObjectField(profileFragmentBridgeClass, "FRAGMENT_MAP") as MutableMap<String, String>

                    val storageDashboardFragmentClassName = XposedHelpers.findClass(
                        "com.android.settings.deviceinfo.StorageDashboardFragment",
                        lpparam.classLoader
                    ).name

                    if (fragmentMap.containsKey(storageDashboardFragmentClassName)) {
                        fragmentMap.remove(storageDashboardFragmentClassName)
                    }
                } catch (e: Throwable) {
                    XposedUtil.handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookLenovoUtilIsRowVersion(lpparam: XC_LoadPackage.LoadPackageParam) {
        val matchCriteria = arrayOf(
            "com.android.settings.users.UserSettings" to emptyArray<String>(),
            "com.lenovo.settings.multispace.manager.MultipleSpaceManager" to emptyArray<String>(),
            "com.lenovo.settings.multispace.AppListFragment" to emptyArray<String>(),
            "com.lenovo.settings.applications.LenovoAppHeaderPreferenceController" to emptyArray<String>(),
        )

        val className = "com.lenovo.common.utils.LenovoUtils"
        val methodName = "isRowVersion"
        val parameterTypes = emptyArray<Any>()
        val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    val stackTrace = Thread.currentThread().stackTrace
                    val calledFromElement = stackTrace.find { element ->
                        matchCriteria.any { (className, methods) ->
                            if (methods.isEmpty()) {
                                element.className.startsWith(className)
                            } else {
                                element.className == className && methods.contains(element.methodName)
                            }
                        }
                    }

                    if (calledFromElement != null) {
                        XposedUtil.xposedDebug(tag, "$methodName method called from class: ${calledFromElement.className}, method: ${calledFromElement.methodName}")
                        param.result = false
                    }
                } catch (e: Throwable) {
                    XposedUtil.handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookLenovoUtilIsPrcVersion(lpparam: XC_LoadPackage.LoadPackageParam) {
        val matchCriteria = arrayOf(
            "com.android.settings.users.UserSettings" to emptyArray<String>(),
            "com.lenovo.settings.multispace.manager.MultipleSpaceManager" to emptyArray<String>(),
            "com.lenovo.settings.multispace.AppListFragment" to emptyArray<String>(),
            "com.lenovo.settings.applications.LenovoAppHeaderPreferenceController" to emptyArray<String>(),
        )

        val className = "com.lenovo.common.utils.LenovoUtils"
        val methodName = "isPrcVersion"
        val parameterTypes = emptyArray<Any>()
        val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    val stackTrace = Thread.currentThread().stackTrace
                    val calledFromElement = stackTrace.find { element ->
                        matchCriteria.any { (className, methods) ->
                            if (methods.isEmpty()) {
                                element.className.startsWith(className)
                            } else {
                                element.className == className && methods.contains(element.methodName)
                            }
                        }
                    }

                    if (calledFromElement != null) {
                        XposedUtil.xposedDebug(tag, "$methodName method called from class: ${calledFromElement.className}, method: ${calledFromElement.methodName}")
                        param.result = true
                    }
                } catch (e: Throwable) {
                    XposedUtil.handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookGraphicsUtilsIsZuiRow(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "${lpparam.packageName}.icons.GraphicsUtils"
        val methodName = "isZuiRow"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant(false)

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookUtilitiesIsZuiRow(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "${lpparam.packageName}.Utilities"
        val methodName = "isZuiRow"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant(true)

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookUtilitiesIsOverlayEnabled(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "${lpparam.packageName}.Utilities"
        val methodName = "isOverlayEnabled"
        val parameterTypes = arrayOf<Any>(Context::class.java)
        val callback = XC_MethodReplacement.returnConstant(true)

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }
}
