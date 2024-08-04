package kr.stonecold.zuitweak.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kr.stonecold.zuitweak.common.XposedUtil

@Suppress("unused")
class HookEnablePenService: HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.COMMON,
        title = "Pen 서비스 활성화",
        description = "Pairing 없이 Pen Service를 활성화하여 호환 펜 사용이 가능하도록 합니다.",
        defaultSelected = false,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = emptyArray()
    override val hookTargetPackage: Array<String> = arrayOf("android", "com.lenovo.penservice")
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "android" -> {
                executeHooks(
                    lpparam,
                    ::hookBluetoothPenConnectPolicyInitSystemProperties,
                )
            }

            "com.lenovo.penservice" -> {
                executeHooks(
                    lpparam,
                    ::hookBtPenModelsGetBTPenNames,
                )
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
                    val thisObject = param.thisObject
                    if (checkClassMethod(className, lpparam.classLoader, "updateTpModeStatus", Boolean::class.java, Int::class.java) == true) {
                        // TB371FC 방식
                        XposedHelpers.callMethod(thisObject, "updateTpModeStatus", true, 1)
                    } else if (checkClassMethod(className, lpparam.classLoader, "updateTpModeStatus", Boolean::class.java) == true) {
                        // TB320FC 방식
                        XposedHelpers.callMethod(thisObject, "updateTpModeStatus", true)
                    } else {
                        XposedUtil.xposedError(tag, "Failed to find updateTpModeStatus method")
                    }
                } catch (e: Throwable) {
                    handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
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
                    if (!modifiedList.contains("Lenovo Tab Pen Plus")) {
                        modifiedList.add("Lenovo Tab Pen Plus")
                        modified = true
                    }

                    if (modified) {
                        param.result = modifiedList
                    }
                } catch (e: Throwable) {
                    handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }
}
