package kr.stonecold.zuitweak

import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kr.stonecold.zuitweak.common.Util
import kr.stonecold.zuitweak.common.XposedPrefsUtil
import kr.stonecold.zuitweak.common.XposedUtil
import kr.stonecold.zuitweak.hooks.*

@Suppress("unused")
class XposedInit : IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources {
    val tag: String = this.javaClass.simpleName

    companion object {
        var modulePath: String? = null
        var deviceModel: String? = null
        var deviceRegion: String? = null
        lateinit var handleLoadPackagePackages: Array<String>
        lateinit var handleInitPackageResourcesPackages: Array<String>
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {
        if (startupParam == null) {
            return
        }

        modulePath = startupParam.modulePath
        XposedUtil.xposedDebug(tag, "Module Path: $modulePath")

        deviceModel = Util.getModel()
        XposedUtil.xposedDebug(tag, "Device Model: $deviceModel")

        deviceRegion = Util.getProperty("ro.config.lgsi.region", "UNKNOWN").uppercase()
        XposedUtil.xposedDebug(tag, "Device Region: $deviceRegion")

        val hooks = HookManager.getAllHooks()

        handleLoadPackagePackages = hooks
            .filterIsInstance<HookBaseHandleLoadPackage>()
            .filter { hook -> !hook.menuItem.isDebug || BuildConfig.DEBUG }
            .flatMap { it.hookTargetPackage.asList() + it.hookTargetPackageOptional.asList() }
            .distinct()
            .toTypedArray()
        XposedUtil.xposedDebug(tag, "HandleLoadPackage Packages: ${handleLoadPackagePackages.joinToString(", ")}")

        handleInitPackageResourcesPackages = hooks
            .filterIsInstance<HookBaseHandleInitPackageResources>()
            .filter { hook -> !hook.menuItem.isDebug || BuildConfig.DEBUG }
            .flatMap { it.hookTargetPackage.asList() + it.hookTargetPackageOptional.asList() }
            .distinct()
            .toTypedArray()
        XposedUtil.xposedDebug(tag, "HandleInitPackageResources Packages: ${handleInitPackageResourcesPackages.joinToString(", ")}")
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (deviceModel != "TB371FC" && deviceModel != "TB320FC") {
            XposedUtil.xposedError(tag, "Device Model: $deviceModel - This device is not supported")
            return
        }
        if (lpparam == null) {
            return
        }

        handleLoadPackageCustom(lpparam)

        if (handleLoadPackagePackages.isEmpty() || !handleLoadPackagePackages.contains(lpparam.packageName)) {
            return
        }

        val hooks = HookManager.getAllHooks()

        val filterHooks = hooks.filterIsInstance<HookBaseHandleLoadPackage>()
            .filter { hook -> hook.hookTargetPackage.contains(lpparam.packageName) || hook.hookTargetPackageOptional.contains(lpparam.packageName) }
            .filter { hook -> !hook.menuItem.isDebug || BuildConfig.DEBUG }

        if (filterHooks.isEmpty()) {
            return
        }

        XposedUtil.xposedInfo(tag, "HandleLoadPackage Loaded App: ${lpparam.packageName}")

        XposedPrefsUtil.reload()

        filterHooks.forEach { hook ->
            try {
                XposedUtil.xposedDebug(tag, "Checking hook: ${hook.javaClass.simpleName}")

                val isHookPrefsEnabled = XposedPrefsUtil.isFeatureEnabled(hook.javaClass.simpleName, hook.menuItem.defaultSelected)
                val isPackageMatch = hook.hookTargetPackage.contains(lpparam.packageName)
                val isDeviceMatch = hook.hookTargetDevice.isEmpty() || hook.hookTargetDevice.contains(deviceModel)
                val isRegionMatch = hook.hookTargetRegion.isEmpty() || hook.hookTargetRegion.contains(deviceRegion)
                val isEnabled = hook.isEnabled()

                XposedUtil.xposedDebug(tag, "Hook: ${hook.javaClass.simpleName}, PrefsEnabled: $isHookPrefsEnabled, PackageMatch: $isPackageMatch, DeviceMatch: $isDeviceMatch, RegionMatch: $isRegionMatch, FunEnabled: $isEnabled")

                if (isHookPrefsEnabled && isPackageMatch && isDeviceMatch && isRegionMatch && isEnabled) {
                    XposedUtil.xposedInfo(tag, "handleLoadPackage triggered for: ${hook.javaClass.simpleName}")
                    hook.handleLoadPackage(lpparam)
                } else {
                    XposedUtil.xposedDebug(tag, "handleLoadPackage not triggered for: ${hook.javaClass.simpleName}")
                }
            } catch (e: Exception) {
                XposedUtil.xposedException(tag, "Error during handleLoadPackage for: ${hook.javaClass.simpleName}: ${e.message}")
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleLoadPackageCustom(lpparam: XC_LoadPackage.LoadPackageParam) {
    }

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam?) {
        if (deviceModel != "TB371FC" && deviceModel != "TB320FC") {
            XposedUtil.xposedError(tag, "Device Model: $deviceModel - This device is not supported")
            return
        }
        if (resparam == null) {
            return
        }

        handleInitPackageResourcesCustom(resparam)

        if (handleInitPackageResourcesPackages.isEmpty() || !handleInitPackageResourcesPackages.contains(resparam.packageName)) {
            return
        }

        val hooks = HookManager.getAllHooks()

        val filterHooks = hooks.filterIsInstance<HookBaseHandleInitPackageResources>()
            .filter { hook -> hook.hookTargetPackage.contains(resparam.packageName) || hook.hookTargetPackageOptional.contains(resparam.packageName) }
            .filter { hook -> !hook.menuItem.isDebug || BuildConfig.DEBUG }

        if (filterHooks.isEmpty()) {
            return
        }

        XposedPrefsUtil.reload()

        XposedUtil.xposedInfo(tag, "HandleInitPackageResources Loaded App: ${resparam.packageName}")

        filterHooks.forEach { hook ->
            try {
                XposedUtil.xposedDebug(tag, "Checking hook: ${hook.javaClass.simpleName}")

                val isHookPrefsEnabled = XposedPrefsUtil.isFeatureEnabled(hook.javaClass.simpleName, hook.menuItem.defaultSelected)
                val isPackageMatch = hook.hookTargetPackage.contains(resparam.packageName)
                val isDeviceMatch = hook.hookTargetDevice.isEmpty() || hook.hookTargetDevice.contains(deviceModel)
                val isRegionMatch = hook.hookTargetRegion.isEmpty() || hook.hookTargetRegion.contains(deviceRegion)
                val isEnabled = hook.isEnabled()

                XposedUtil.xposedDebug(tag, "Hook: ${hook.javaClass.simpleName}, PrefsEnabled: $isHookPrefsEnabled, PackageMatch: $isPackageMatch, DeviceMatch: $isDeviceMatch, RegionMatch: $isRegionMatch, FunEnabled: $isEnabled")

                if (isHookPrefsEnabled && isPackageMatch && isDeviceMatch && isRegionMatch && isEnabled) {
                    XposedUtil.xposedInfo(tag, "handleInitPackageResources triggered for: ${hook.javaClass.simpleName}")
                    hook.handleInitPackageResources(resparam)
                } else {
                    XposedUtil.xposedDebug(tag, "handleInitPackageResources not triggered for: ${hook.javaClass.simpleName}")
                }
            } catch (e: Exception) {
                XposedUtil.xposedException(tag, "Error during handleInitPackageResources for: ${hook.javaClass.simpleName}: ${e.message}")
            }
        }
    }

    private fun handleInitPackageResourcesCustom(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        //돌비 아트모스
        if (resparam.packageName == "com.android.settings" || resparam.packageName == "com.android.systemui") {
            XposedUtil.xposedDebug(tag, "package: ${resparam.packageName}")
            val hook = HookManager.getHook(HookAllowDisableDolbyAtmosForBuiltinSpeakers::class.java) ?: return
            val hookName = HookAllowDisableDolbyAtmosForBuiltinSpeakersRes::class.simpleName

            val isHookPrefsEnabled = XposedPrefsUtil.isFeatureEnabled(hook.javaClass.simpleName, hook.menuItem.defaultSelected)
            val isPackageMatch = hook.hookTargetPackage.contains(resparam.packageName)
            val isDeviceMatch = hook.hookTargetDevice.isEmpty() || hook.hookTargetDevice.contains(deviceModel)
            val isRegionMatch = hook.hookTargetRegion.isEmpty() || hook.hookTargetRegion.contains(deviceRegion)
            val isEnabled = hook.isEnabled()

            XposedUtil.xposedDebug(tag, "Hook: $hookName, PrefsEnabled: $isHookPrefsEnabled, PackageMatch: $isPackageMatch, DeviceMatch: $isDeviceMatch, RegionMatch: $isRegionMatch, FunEnabled: $isEnabled")

            if (isHookPrefsEnabled && isPackageMatch && isDeviceMatch && isRegionMatch && isEnabled) {
                XposedUtil.xposedInfo(tag, "handleInitPackageResources triggered for: $hookName")
                HookAllowDisableDolbyAtmosForBuiltinSpeakersRes.updateDescription(resparam)
            }
        }
    }
}
