package kr.stonecold.zuitweak

import kr.stonecold.zuitweak.common.Constants
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
        registerHook(HookRemoveShutdownMessage())
        //registerHook(HookEnableTurnScreenOn()) ///테스트 필요

        //ROW
        if (Constants.deviceVersion == "16.0") {
            registerHook(HookEnableMultipleSpace16())
        }
        else {
            registerHook(HookEnableMultipleSpace())
        }
        registerHook(HookEnableHotspot())
        registerHook(HookEnableBatteryOverheatNotify())
        registerHook(HookEnableOneVisionSmartSplit())
        //registerHook(HookEnableTaskbarShowRecentApps()) //동작안함
        //registerHook(HookEnableStudyLauncher()) //추가 패키지 팔요

        //PRC
        registerHook(HookAddKoreanLanguageSettings())
        registerHook(HookDisableLauncherOnlineSearch())
        registerHook(HookFixDocumentsUICrash())
        registerHook(HookDisableStudyLauncher())
        registerHook(HookRemovePrcInfo())
        registerHook(HookFixBatteryIconTestMode())
        registerHook(HookEnableOneVisionSmartRotation())

        //DEVICE
        registerHook(HookEnablePenService())
        registerHook(HookChangeKeyboardHotkeys())

        //UNFUCKZUI
        //registerHook(HookDisableTaskbar()) //기본 기능으로 제공
        registerHook(HookChangeNotificationIcon())
        registerHook(HookAllowDisableDolbyAtmosForBuiltinSpeakers())
        registerHook(HookDisableAppSwitchForceStop())
        registerHook(HookDisableVirusScan())
        registerHook(HookChangePackageInstaller())
        registerHook(HookChangePermissionController())

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
