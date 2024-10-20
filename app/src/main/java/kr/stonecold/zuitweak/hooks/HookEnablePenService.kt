package kr.stonecold.zuitweak.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kr.stonecold.zuitweak.R
import kr.stonecold.zuitweak.common.*

@Suppress("unused")
class HookEnablePenService: HookBaseHandleLoadPackage() {
    override val menuItem
        get() = HookMenuItem(
            category = HookMenuCategory.DEVICE,
            title = LanguageUtil.getString(R.string.hook_enable_pen_service_title),
            description = LanguageUtil.getString(R.string.hook_enable_pen_service_desc),
            defaultSelected = false,
        )

    override val hookTargetDevice: Array<String> = arrayOf("TB371FC")
    override val hookTargetRegion: Array<String> = emptyArray()
    override val hookTargetVersion: Array<String> = emptyArray()

    override val hookTargetPackage: Array<String> = arrayOf("android", "com.lenovo.penservice")
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "android" -> {
                hookBluetoothPenConnectPolicyInitSystemProperties(lpparam)
            }

            "com.lenovo.penservice" -> {
                hookBtPenModelsGetBTPenNames(lpparam)
            }
        }
    }

    private fun hookBluetoothPenConnectPolicyInitSystemProperties(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.zui.server.input.styluspen.pen.bluetooth.policy.BluetoothPenConnectPolicy"
        val methodName = "initSystemProperties"
        val parameterTypes = emptyArray<Any>()
        val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    var findMethod = false
                    if (Util.getMethod(className, lpparam.classLoader, "updateTpModeStatus", Boolean::class.java, Int::class.java) != null) {
                        // TB371FC 방식
                        XposedHelpers.callMethod(param.thisObject, "updateTpModeStatus", true, 1)
                        findMethod = true
                    } else if (Util.getMethod(className, lpparam.classLoader, "updateTpModeStatus", Boolean::class.java) != null) {
                        // TB320FC 방식
                        XposedHelpers.callMethod(param.thisObject, "updateTpModeStatus", true)
                        findMethod = true
                    } else {
                        XposedUtil.xposedError(tag, "Failed to find updateTpModeStatus method")
                    }
                    if (findMethod) {
                        param.result = null
                    }
                } catch (e: Throwable) {
                    XposedUtil.handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookBtPenModelsGetBTPenNames(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.lenovo.pen.bt.util.BtPenModels"
        val methodName = "getBT_PEN_NAMES"
        val parameterTypes = emptyArray<Any>()
        val callback = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                try {
                    val originalList = param.result as ArrayList<*>
                    val modifiedList = ArrayList(originalList)
                    var modified = false

                    if (!modifiedList.contains("Lenovo Stylus")) {
                        modifiedList.add("Lenovo Stylus")
                        modified = true
                    }

                    if (modified) {
                        param.result = modifiedList
                    }
                } catch (e: Throwable) {
                    XposedUtil.handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }
}
