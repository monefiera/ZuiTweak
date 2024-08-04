package kr.stonecold.zuitweak.hooks

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.UserManager
import android.view.View
import android.widget.LinearLayout
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kr.stonecold.zuitweak.common.Util
import kr.stonecold.zuitweak.common.XposedUtil

@Suppress("unused")
class HookEnableMultipleSpace : HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.ROW,
        title = "Multiple Space 활성화",
        description = "글로벌롬에서 Multiple Space를 활성화합니다. (ZuiTweak-magisk 필요)",
        defaultSelected = false,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = arrayOf("ROW")
    override val hookTargetPackage: Array<String> = arrayOf("com.android.settings", "com.zui.launcher")
    override val hookTargetPackageOptional: Array<String> = arrayOf("com.zui.desktoplauncher")

    override fun isEnabled(): Boolean {
        val zuiMultispaceEnabled = Util.getProperty("ro.stonecold.zuimultispace.enabled", "false").uppercase()
        return zuiMultispaceEnabled == "TRUE"
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.android.settings" -> {
                executeHooks(
                    lpparam,
                    ::hookMultipleSpaceManagerSendZuiAddProfileBroadcast,
                    ::hookStorageItemPreferenceShowArrowImageIfNeed,
                    ::hookUtilsGetTargetFragment,
                    ::hookApplicationsManagerFragmentCreateHeader,
                    ::hookSecondaryUserControllerGetSecondaryUserControllers,
                    ::hookSecondaryUserControllerConstructor,
                )
            }

            "com.zui.launcher", "com.zui.desktoplauncher" -> {
                executeHooks(
                    lpparam,
                    ::hookGraphicsUtilsIsZuiRow,
                    ::hookUtilitiesIsZuiRow,
                )
            }
        }
    }

    private fun hookMultipleSpaceManagerSendZuiAddProfileBroadcast(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settings.feature.multiplespace.manager.MultipleSpaceManager"
        val methodName = "sendZuiAddProfileBroadcast"
        val parameterTypes = emptyArray<Any>()
        val callback = object : XC_MethodReplacement() {
            override fun replaceHookedMethod(param: MethodHookParam) {
                try {
                    val thisObject = param.thisObject
                    val context = XposedHelpers.getObjectField(thisObject, "mContext") as? Context

                    if (context == null) {
                        XposedUtil.xposedDebug(tag, "mContext is null, nothing will be done.")
                        return
                    }

                    val intent = Intent("zui.intent.action.MANAGED_PROFILE_ADDED")
                    intent.setPackage("com.zui.launcher")
                    context.sendBroadcast(intent)
                } catch (e: Throwable) {
                    handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
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

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
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

                    val manageApplicationsClassName = XposedHelpers.findClass(
                        "com.android.settings.applications.manageapplications.ManageApplications",
                        lpparam.classLoader
                    ).name
                    val applicationsManagerFragmentClassName = XposedHelpers.findClass(
                        "com.android.settings.applications.manageapplications.ApplicationsManagerFragment",
                        lpparam.classLoader
                    ).name

                    if (fragmentMap.containsKey(applicationsManagerFragmentClassName)) {
                        fragmentMap[manageApplicationsClassName] = fragmentMap.remove(applicationsManagerFragmentClassName)!!
                    }

                    val storageDashboardFragmentClassName = XposedHelpers.findClass(
                        "com.android.settings.deviceinfo.StorageDashboardFragment",
                        lpparam.classLoader
                    ).name

                    if (fragmentMap.containsKey(storageDashboardFragmentClassName)) {
                        fragmentMap.remove(storageDashboardFragmentClassName)
                    }
                } catch (e: Throwable) {
                    handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookApplicationsManagerFragmentCreateHeader(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settings.applications.manageapplications.ApplicationsManagerFragment"
        val methodName = "createHeader"
        val parameterTypes = emptyArray<Any>()
        val callback = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                try {
                    val clazz = param.thisObject.javaClass
                    val field = clazz.getDeclaredField("apps_layout1")
                    field.isAccessible = true
                    val appsLayout1 = field.get(param.thisObject) as LinearLayout
                    appsLayout1.visibility = View.VISIBLE
                } catch (e: Throwable) {
                    handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookSecondaryUserControllerGetSecondaryUserControllers(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settings.deviceinfo.storage.SecondaryUserController"
        val methodName = "getSecondaryUserControllers"
        val parameterTypes = arrayOf<Any>(
            Context::class.java,
            UserManager::class.java,
            Boolean::class.java,
            "com.android.settings.deviceinfo.storage.StorageItemPreferenceController"
        )
        val callback = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                try {
                    val returnValue = param.result as ArrayList<*>

                    val noSecondaryUserControllerClass = XposedHelpers.findClass(
                        "com.android.settings.deviceinfo.storage.SecondaryUserController\$NoSecondaryUserController",
                        lpparam.classLoader
                    )

                    if (returnValue.none { noSecondaryUserControllerClass.isInstance(it) }) {
                        val modifiedList = ArrayList(returnValue)
                        val context = param.args[0] as Context
                        val noSecondaryUserController = XposedHelpers.newInstance(noSecondaryUserControllerClass, context)
                        modifiedList.add(noSecondaryUserController)
                        param.result = modifiedList
                    }
                } catch (e: Throwable) {
                    handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookSecondaryUserControllerConstructor(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.settings.deviceinfo.storage.SecondaryUserController"
        val methodName = "" //Constructor
        val parameterTypes = arrayOf<Any>(
            Context::class.java,
            "android.content.pm.UserInfo",
            "com.android.settings.deviceinfo.storage.StorageItemPreferenceController"
        )
        val callback = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                try {
                    val isShowMultiSpaceField = XposedHelpers.findField(param.thisObject.javaClass, "isShowMultiSpace")
                    isShowMultiSpaceField.isAccessible = true

                    val currentValue = isShowMultiSpaceField.getBoolean(param.thisObject)
                    XposedUtil.xposedDebug(tag, "Current isShowMultiSpace value: $currentValue")

                    isShowMultiSpaceField.setBoolean(param.thisObject, true)
                } catch (e: Throwable) {
                    XposedUtil.xposedException(tag, "Error occurred while hooking $className: ${e.message}")
                }
            }
        }

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookGraphicsUtilsIsZuiRow(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "${lpparam.packageName}.icons.GraphicsUtils"
        val methodName = "isZuiRow"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant(false)

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookUtilitiesIsZuiRow(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "${lpparam.packageName}.Utilities"
        val methodName = "isZuiRow"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant(true)

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }
}
