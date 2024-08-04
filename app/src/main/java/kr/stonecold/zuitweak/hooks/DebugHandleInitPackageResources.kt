package kr.stonecold.zuitweak.hooks

import de.robv.android.xposed.callbacks.XC_InitPackageResources

@Suppress("unused")
class DebugHandleInitPackageResources : HookBaseHandleInitPackageResources() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.DEVELOPMENT,
        title = "디버그 (HandleInitPackageResources)",
        description = "디버그 (HandleInitPackageResources)",
        defaultSelected = false,
        isDebug = true,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = emptyArray()
    override val hookTargetPackage: Array<String> = emptyArray()
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
    }
}
