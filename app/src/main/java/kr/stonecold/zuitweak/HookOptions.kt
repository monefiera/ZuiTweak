package kr.stonecold.zuitweak

import android.content.Context
import kr.stonecold.zuitweak.common.Util
import kr.stonecold.zuitweak.hooks.HookMenuCategory

object HookOptions {
    fun getHookOptions(context: Context): List<Pair<String, List<HookOption>>> {
        val categoryOrder = listOf(
            HookMenuCategory.COMMON to "공통",
            HookMenuCategory.ROW to "ROW (글로벌롬)",
            HookMenuCategory.PRC to "PRC (내수롬)",
            HookMenuCategory.DEVICE to "장비 특화",
            HookMenuCategory.DEVELOPMENT to "개발 중"
        )

        val categoriesWithOptions = mutableMapOf<String, MutableList<HookOption>>()

        val deviceModel = Util.getModel()
        val deviceRegion = Util.getProperty("ro.config.lgsi.orgregion", "ro.config.lgsi.region", "UNKNOWN").uppercase()

        val hooks = HookManager.getAllHooks()

        hooks.filter { hook -> !hook.menuItem.isDebug || BuildConfig.DEBUG }
            .forEach { hook ->
                val categoryName = categoryOrder.find { it.first == hook.menuItem.category }?.second ?: "기타"

                val isAllPackageInstalled = hook.hookTargetPackage.all { pkg -> Util.isPackageInstalled(context, pkg) }
                val isDeviceMatch = hook.hookTargetDevice.isEmpty() || hook.hookTargetDevice.contains(deviceModel)
                val isRegionMatch = hook.hookTargetRegion.isEmpty() || hook.hookTargetRegion.contains(deviceRegion)
                val isEnabled = hook.isEnabled()

                val isDisabled = !isAllPackageInstalled || !isDeviceMatch || !isRegionMatch || !isEnabled

                val targetPackages = (hook.hookTargetPackage + hook.hookTargetPackageOptional).joinToString()
                val regionInfo = if (hook.hookTargetRegion.isNotEmpty()) "[${hook.hookTargetRegion.joinToString()}]" else ""
                val deviceInfo = if (hook.hookTargetDevice.isNotEmpty()) "[${hook.hookTargetDevice.joinToString()}]" else ""
                var optionDescription = "Target: $targetPackages\n$regionInfo$deviceInfo${hook.menuItem.description}"
                val debugInfo = mutableListOf<String>()
                if (!isAllPackageInstalled) {
                    @Suppress("KotlinConstantConditions")
                    debugInfo.add("PackageMatch: $isAllPackageInstalled")
                }
                if (!isDeviceMatch) {
                    @Suppress("KotlinConstantConditions")
                    debugInfo.add("DeviceMatch: $isDeviceMatch")
                }
                if (!isRegionMatch) {
                    @Suppress("KotlinConstantConditions")
                    debugInfo.add("RegionMatch: $isRegionMatch")
                }
                if (!isEnabled) {
                    @Suppress("KotlinConstantConditions")
                    debugInfo.add("FunEnabled: $isEnabled")
                }
                if (debugInfo.isNotEmpty()) {
                    optionDescription += "\n${debugInfo.joinToString(", ")}"
                }

                val option = HookOption(
                    key = hook.javaClass.simpleName,
                    title = hook.menuItem.title,
                    description = optionDescription,
                    isDisabled = isDisabled,
                    defaultEnabled = hook.menuItem.defaultSelected,
                )

                categoriesWithOptions.computeIfAbsent(categoryName) { mutableListOf() }.add(option)
            }

        return categoryOrder.mapNotNull { (_, name) ->
            categoriesWithOptions[name]?.let { name to it }
        }
    }
}

data class HookOption(
    val key: String,
    val title: String,
    val description: String,
    val isDisabled: Boolean,
    val defaultEnabled: Boolean,
)
