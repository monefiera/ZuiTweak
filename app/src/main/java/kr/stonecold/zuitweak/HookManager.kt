package kr.stonecold.zuitweak

import kr.stonecold.zuitweak.hooks.*

@Suppress("unused")
object HookManager {
    private val hooks: MutableList<HookBase> = mutableListOf()

    init {
        //COMMON
        registerHook(HookEnableZuiCameraShutterOption())
        registerHook(HookRemoveLockscreenShortcuts())
        registerHook(HookRemoveUnusedGameServiceIcons())
        registerHook(HookEnableHiddenDisplaySettings())
        registerHook(HookEnablePenService())
        registerHook(HookDisableTaskbar())
        registerHook(HookChangeNotificationIcon())
        registerHook(HookAllowDisableDolbyAtmosForBuiltinSpeakers())
        registerHook(HookDisableAppSwitchForceStop())

        //ROW
        registerHook(HookEnableMultipleSpace())
        registerHook(HookEnableWLANTether())
        registerHook(HookEnableOneVisionSmartSplit())
        registerHook(HookEnableBatteryOverheatNotify())
        //registerHook(HookEnableStudyLauncher()) //추가 패키지 팔요

        //PRC
        registerHook(HookAddKoreanLanguageSettings())
        registerHook(HookDisableLauncherOnlineSearch())
        registerHook(HookFixDocumentsUICrash())
        registerHook(HookEnableOneVisionSmartRotation())
        registerHook(HookDisableStudyLauncher())
        registerHook(HookRemovePrcInfo())
        registerHook(HookFixBatteryIconTestMode())
        registerHook(HookDisableVirusScan())
        registerHook(HookChangePackageInstaller())
        registerHook(HookChangePermissionController())

        //DEVICE
        registerHook(HookChangeKeyboardHotkeys())

        //DEVELOPMENT
        if (BuildConfig.DEBUG) {
            registerHook(DebugHandleLoadPackage())
            registerHook(DebugHandleInitPackageResources())
            registerHook(HookApplyKoreanLanguage())
        }
    }

    private fun registerHook(hook: HookBase) {
        hooks.add(hook)
    }

    fun getHook(hookClass: Class<out HookBase>): HookBase? {
        return hooks.find { it::class.java == hookClass }
    }

    fun getAllHooks(): List<HookBase> {
        return hooks
    }
}
