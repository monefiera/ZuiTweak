package kr.stonecold.zuitweak.hooks

import de.robv.android.xposed.callbacks.XC_LoadPackage

@Suppress("unused")
class DebugHandleLoadPackage : HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.DEVELOPMENT,
        title = "디버그 (HandleLoadPackage)",
        description = "디버그 (HandleLoadPackage)",
        defaultSelected = false,
        isDebug = true,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = emptyArray()
    override val hookTargetPackage: Array<String> = emptyArray()
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
    }
}
