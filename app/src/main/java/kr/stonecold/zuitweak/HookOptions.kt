package kr.stonecold.zuitweak

import android.content.Context
import kr.stonecold.zuitweak.common.*
import kr.stonecold.zuitweak.hooks.HookMenuCategory

object HookOptions {
    fun getHookOptions(context: Context): List<Pair<String, List<HookOption>>> {
        val categoryOrder = listOf(
            HookMenuCategory.COMMON to LanguageUtil.getString(R.string.hook_menu_category_common),
            HookMenuCategory.ROW to LanguageUtil.getString(R.string.hook_menu_category_row),
            HookMenuCategory.PRC to LanguageUtil.getString(R.string.hook_menu_category_prc),
            HookMenuCategory.DEVICE to LanguageUtil.getString(R.string.hook_menu_category_device),
            HookMenuCategory.UNFUCKZUI to LanguageUtil.getString(R.string.hook_menu_category_unfuckzui),
            HookMenuCategory.DEVELOPMENT to LanguageUtil.getString(R.string.hook_menu_category_development),
        )

        val categoriesWithOptions = mutableMapOf<String, MutableList<HookOption>>()

        val hooks = HookManager.getAllHooks()

        hooks.filter { hook -> !hook.menuItem.isDebug || BuildConfig.DEBUG }
            .forEach { hook ->
                val categoryName = categoryOrder.find { it.first == hook.menuItem.category }?.second ?: "기타"

                val (isEnabled, message) = hook.isEnabledMenu(context)

                val regionInfo = if (hook.hookTargetRegion.isNotEmpty()) "[${hook.hookTargetRegion.joinToString()}]" else ""
                val deviceInfo = if (hook.hookTargetDevice.isNotEmpty()) "[${hook.hookTargetDevice.joinToString()}]" else ""
                val targetPackages = (hook.hookTargetPackage + hook.hookTargetPackageOptional).joinToString()
                var optionDescription = "$regionInfo$deviceInfo${hook.menuItem.description}\nTarget: $targetPackages"
                if (message.isNotEmpty()) {
                    optionDescription += "\n${message}"
                }

                val option = HookOption(
                    key = hook.javaClass.simpleName,
                    title = hook.menuItem.title,
                    description = optionDescription,
                    isEnabledOption = isEnabled,
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
    val isEnabledOption: Boolean,
    val defaultEnabled: Boolean,
)
