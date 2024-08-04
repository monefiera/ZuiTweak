package kr.stonecold.zuitweak.hooks

import de.robv.android.xposed.callbacks.XC_InitPackageResources
import kr.stonecold.zuitweak.common.XposedUtil
import java.util.Locale
import kotlin.String
import kotlin.Suppress

@Suppress("unused")
object HookAllowDisableDolbyAtmosForBuiltinSpeakersRes {
    val tag: String = this::class.java.simpleName

    fun updateDescription(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        when (resparam.packageName) {
            "com.android.settings", "com.android.systemui" -> {
                val language = Locale.getDefault().language

                val dolbySwitchSummary = if (language == "ko") {
                    "태블릿 스피커를 사용 중일 때도 Dolby Atmos를 끌 수 있습니다"
                } else {
                    "When the tablet speaker is in use, Dolby Atmos can be turned off"
                }

                val resKey = when (resparam.packageName) {
                    "com.android.settings" -> "dolby_switch_summary"
                    "com.android.systemui" -> "doblyAtmos_title_desc"
                    else -> return
                }

                resparam.res.setReplacement(resparam.packageName, "string", resKey, dolbySwitchSummary)

                XposedUtil.xposedDebug(tag, "Successfully replaced ${resparam.packageName}.$resKey: $dolbySwitchSummary")
            }
        }
    }
}
