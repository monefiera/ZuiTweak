package kr.stonecold.zuitweak.hooks

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kr.stonecold.zuitweak.common.XposedUtil

@Suppress("unused")
class HookChangePackageInstaller : HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.UNFUCKZUI,
        title = "Package Installer 변경",
        description = "Package Installer를 AOSP 스타일로 변경합니다.",
        defaultSelected = false,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = arrayOf("PRC")
    override val hookTargetVersion: Array<String> = emptyArray()

    override val hookTargetPackage: Array<String> = arrayOf("com.android.packageinstaller")
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.android.packageinstaller" -> {
                hookUtilsIsCTSandGTS(lpparam)
                hookUtilsIsCTSandGTS2(lpparam)
                hookInstallStartOnCreate(lpparam)

                val rStyleCls = XposedHelpers.findClass("com.android.packageinstaller.R\$style", lpparam.classLoader)
                val newStyleId = XposedHelpers.getStaticIntField(rStyleCls, "Theme_AlertDialogActivity")
                hookPackageInstallerActivityOnCreate(lpparam, newStyleId)
                hookInstallStagingOnCreate(lpparam, newStyleId)
            }
        }
    }

    private fun hookUtilsIsCTSandGTS(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.packageinstaller.extra.Utils"
        val methodName = "isCTSandGTS"
        val parameterTypes = arrayOf<Any>(String::class.java)
        val callback = XC_MethodReplacement.returnConstant(true)

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookUtilsIsCTSandGTS2(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.packageinstaller.extra.Utils"
        val methodName = "isCTSandGTS"
        val parameterTypes = arrayOf<Any>(String::class.java, Intent::class.java)
        val callback = XC_MethodReplacement.returnConstant(true)

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookPackageInstallerActivityOnCreate(lpparam: XC_LoadPackage.LoadPackageParam, newStyleId: Int) {
        val className = "com.android.packageinstaller.PackageInstallerActivity"
        val methodName = "onCreate"
        val parameterTypes = arrayOf<Any>(Bundle::class.java)
        val callback = ActivityStyleHook(newStyleId, true)

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookInstallStagingOnCreate(lpparam: XC_LoadPackage.LoadPackageParam, newStyleId: Int) {
        val className = "com.android.packageinstaller.InstallStaging"
        val methodName = "onCreate"
        val parameterTypes = arrayOf<Any>(Bundle::class.java)
        val callback = ActivityStyleHook(newStyleId, true)

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookInstallStartOnCreate(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.packageinstaller.InstallStart"
        val methodName = "onCreate"
        val parameterTypes = arrayOf<Any>(Bundle::class.java)
        val callback = ActivityStyleHook(android.R.style.Theme_Translucent_NoTitleBar, false)

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }

    private class ActivityStyleHook(private val newStyleId: Int, private val overrideAnimation: Boolean) : XC_MethodHook() {
        @SuppressLint("NewApi")
        override fun beforeHookedMethod(param: MethodHookParam) {
            try {
                val activity = param.thisObject as Activity
                activity.setTheme(newStyleId)
                // Hidden API를 이용한 투명 상태 설정
                val setTranslucentMethod = Activity::class.java.getMethod("setTranslucent", Boolean::class.javaPrimitiveType)
                setTranslucentMethod.invoke(activity, true)
                if (overrideAnimation) {
                    @Suppress("DEPRECATION")
                    activity.overridePendingTransition(0, 0)
                }
            } catch (e: Throwable) {
                XposedUtil.xposedException("ActivityStyleHook", "Exception while hooking method: ${e.message}")
            }
        }
    }
}
