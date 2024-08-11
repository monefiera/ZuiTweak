package kr.stonecold.zuitweak.hooks

import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kr.stonecold.zuitweak.common.XposedUtil

@Suppress("unused")
class HookEnableZuiCameraShutterOption : HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.COMMON,
        title = "카메라 무음 설정 활성화",
        description = "카메라 앱내 설정에 무음 설정을 활성화 합니다.",
        defaultSelected = false,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = emptyArray()
    override val hookTargetVersion: Array<String> = emptyArray()

    override val hookTargetPackage: Array<String> = arrayOf("com.zui.camera")
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.zui.camera" -> {
                hookApiHelperIsForceCameraSound(lpparam)
            }
        }
    }

    private fun hookApiHelperIsForceCameraSound(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.zui.camera.developer.common.ApiHelper"
        val methodName = "isForceCameraSound"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant(false)

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }
}
