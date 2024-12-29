package kr.stonecold.zuitweak.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kr.stonecold.zuitweak.R
import kr.stonecold.zuitweak.common.*

@Suppress("unused")
class HookDisableAutoPenButton : HookBaseHandleLoadPackage() {
    override val menuItem
        get() = HookMenuItem(
            category = HookMenuCategory.COMMON,
            title = LanguageUtil.getString(R.string.hook_disable_auto_pen_button_title),
            description = LanguageUtil.getString(R.string.hook_disable_auto_pen_button_desc),
            defaultSelected = false,
        )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = emptyArray()
    override val hookTargetVersion: Array<String> = emptyArray()

    override val hookTargetPackage: Array<String> = arrayOf("com.lenovo.penservice")
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.lenovo.penservice" -> {
                hookSystemSettingsPutPenAssistiveTouch(lpparam)
            }
        }
    }

    private fun hookSystemSettingsPutPenAssistiveTouch(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.lenovo.pen.cap.util.SystemSettings"
        val methodName = "putPenAssistiveTouch"
        val parameterTypes = arrayOf<Any>("android.content.Context", Int::class.java)
        val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    val callStack = Throwable().stackTrace
                    val isCalledFromDispatchPenStatus = callStack.any { stackTraceElement ->
                        stackTraceElement.className == "com.lenovo.pen.bt.controller.BluetoothReceiverController" && stackTraceElement.methodName == "dispatchPenStatus"
                    }

                    if (isCalledFromDispatchPenStatus) {
                        XposedUtil.xposedInfo(tag, "Skipping SystemSettings.putPenAssistiveTouch when called from BluetoothReceiverController.dispatchPenStatus"
                        )
                        param.result = null // 호출 무시
                    }
                } catch (e: Throwable) {
                    XposedUtil.handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }
}
