package kr.stonecold.zuitweak.hooks

import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kr.stonecold.zuitweak.common.Util
import kr.stonecold.zuitweak.common.XposedUtil

@Suppress("unused")
class HookFixBatteryIconTestMode : HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.PRC,
        title = "배터리 아이콘 사라짐 수정",
        description = "test_mode 활성화 시 배터리 아이콘이 사라지는 문제를 해결합니다.",
        defaultSelected = false,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = arrayOf("PRC")
    override val hookTargetVersion: Array<String> = emptyArray()

    override val hookTargetPackage: Array<String> = arrayOf("com.android.systemui")
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun isEnabledCustomCheck(): Boolean {
        val testModeValue = Util.getProperty("persist.sys.lenovo.is_test_mode", "false").uppercase()
        return testModeValue == "TRUE"
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.android.systemui" -> {
                hookXSystemUtilIsCTSGTSTest(lpparam)
            }
        }
    }

    private fun hookXSystemUtilIsCTSGTSTest(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.systemui.util.XSystemUtil"
        val methodName = "isCTSGTSTest"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant(false)

        XposedUtil.executeHook(tag, lpparam, className, methodName, *parameterTypes, callback)
    }
}
