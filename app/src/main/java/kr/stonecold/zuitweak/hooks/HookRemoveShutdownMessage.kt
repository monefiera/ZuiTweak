package kr.stonecold.zuitweak.hooks

import de.robv.android.xposed.callbacks.XC_InitPackageResources
import kr.stonecold.zuitweak.common.Constants
import kr.stonecold.zuitweak.common.XposedUtil

@Suppress("unused")
class HookRemoveShutdownMessage : HookBaseHandleInitPackageResources() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.COMMON,
        title = "종료 메시지 제거",
        description = "하단에 보이는 종료 메시지를 제거합니다.",
        defaultSelected = false,
        isDebug = true,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = emptyArray()
    override val hookTargetVersion: Array<String> = emptyArray()

    override val hookTargetPackage: Array<String> = arrayOf("com.android.systemui")
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun isEnabledCustomCheck(): Boolean {
        return Constants.deviceVersion == "16.0"
    }

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        when(resparam.packageName) {
            "com.android.systemui" -> {
                hookRemoveMessage(resparam)
            }
        }
    }

    private fun hookRemoveMessage(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        val resKey = "shutdown_dialog_tips_message"
        val shutdownDialogTipsMessage = ""
        resparam.res.setReplacement(resparam.packageName, "string", resKey, shutdownDialogTipsMessage)

        XposedUtil.xposedDebug(tag, "Successfully replaced ${resparam.packageName}.$resKey: $shutdownDialogTipsMessage")
    }
}
