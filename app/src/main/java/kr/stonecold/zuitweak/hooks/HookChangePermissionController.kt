package kr.stonecold.zuitweak.hooks

//noinspection SuspiciousImport
import android.R
import android.app.Activity
import android.os.Bundle
import android.view.Window
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kr.stonecold.zuitweak.common.XposedUtil

@Suppress("unused")
class HookChangePermissionController : HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.UNFUCKZUI,
        title = "Permission Controller 변경",
        description = "Permission Controller를 AOSP 스타일로 변경합니다.",
        defaultSelected = false,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = arrayOf("PRC")
    override val hookTargetVersion: Array<String> = emptyArray()

    override val hookTargetPackage: Array<String> = arrayOf("com.android.permissioncontroller")
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.android.permissioncontroller" -> {
                hookZuiUtilsIsCTSandGTS(lpparam)
                hookGrantPermissionsActivityOnCreate(lpparam)
            }
        }
    }

    private fun hookZuiUtilsIsCTSandGTS(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.permissioncontroller.extra.ZuiUtils"
        val methodName = "isCTSandGTS"
        val parameterTypes = arrayOf<Any>(String::class.java)
        val callback = XC_MethodReplacement.returnConstant(true)

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookGrantPermissionsActivityOnCreate(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.permissioncontroller.permission.ui.GrantPermissionsActivity"
        val methodName = "onCreate"
        val parameterTypes = arrayOf<Any>(Bundle::class.java)
        val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    val activity = param.thisObject as Activity
                    activity.setTheme(R.style.Theme_DeviceDefault_Light_Dialog_Alert)
                    activity.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    val rootView = activity.window.decorView
                    rootView.setFilterTouchesWhenObscured(true)
                    rootView.setPadding(0, 0, 0, 0)
                } catch (e: Throwable) {
                    XposedUtil.handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }
}
