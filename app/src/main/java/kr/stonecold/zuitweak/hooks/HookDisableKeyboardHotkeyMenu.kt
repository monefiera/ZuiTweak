package kr.stonecold.zuitweak.hooks

import android.R.attr.classLoader
import android.view.KeyEvent
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kr.stonecold.zuitweak.R
import kr.stonecold.zuitweak.common.LanguageUtil
import kr.stonecold.zuitweak.common.XposedUtil


@Suppress("unused")
class HookDisableKeyboardHotkeyMenu : HookBaseHandleLoadPackage() {
    override val menuItem
        get() = HookMenuItem(
            category = HookMenuCategory.COMMON,
            title = LanguageUtil.getString(R.string.hook_disable_keyboard_hotkey_menu_title),
            description = LanguageUtil.getString(R.string.hook_disable_keyboard_hotkey_menu_desc),
            defaultSelected = false,
        )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = emptyArray()
    override val hookTargetVersion: Array<String> = emptyArray()

    override val hookTargetPackage: Array<String> = arrayOf("android")
    override val hookTargetPackageOptional: Array<String> = emptyArray()
    override val hookTargetPackageRes: Array<String> = arrayOf("com.android.systemui")

    override var updateRes: ((resparam: XC_InitPackageResources.InitPackageResourcesParam) -> Unit)? = { resparam ->
        if (resparam.packageName == "com.android.systemui") {
            val resKey = "keyboard_shortcut_annotation"
            val resVal = ""
            resparam.res.setReplacement(resparam.packageName, "string", resKey, resVal)

            XposedUtil.xposedDebug(tag, "Successfully replaced ${resparam.packageName}.$resKey: $resVal")
        }
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "android" -> {
                hookKeyboardZuiKeyInputPolicyInterceptKeyBeforeDispatchingBefore(lpparam)
            }
        }
    }

    private fun hookKeyboardZuiKeyInputPolicyInterceptKeyBeforeDispatchingBefore(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.zui.server.input.keyboard.key.policy.KeyboardZuiKeyInputPolicy"
        val methodName = "interceptKeyBeforeDispatchingBefore"
        val parameterTypes = arrayOf<Any>(KeyEvent::class.java, Int::class.java)
        val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    val keyEvent = param.args[0] as KeyEvent
                    val keyCode = keyEvent.keyCode

                    if (keyCode == 113 || keyCode == 114) {
                        param.result = false
                    }
                } catch (e: Throwable) {
                    XposedUtil.handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }
}
