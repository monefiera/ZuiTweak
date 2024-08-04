package kr.stonecold.zuitweak.hooks

import android.annotation.SuppressLint
import android.content.res.Resources
import android.content.res.XModuleResources
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import kr.stonecold.zuitweak.R
import kr.stonecold.zuitweak.XposedInit
import kr.stonecold.zuitweak.common.Constants
import kr.stonecold.zuitweak.common.XposedUtil
import java.util.Locale

@Suppress("unused")
class HookApplyKoreanLanguage : HookBaseHandleInitPackageResources() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.DEVELOPMENT,
        title = "한국어 번역 적용",
        description = "한국어 번역을 적용합니다.",
        defaultSelected = false,
        isDebug = true,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = arrayOf("PRC")
    override val hookTargetPackage: Array<String> = arrayOf("com.android.settings")
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        XposedUtil.xposedDebug(tag, "country: ${Locale.getDefault().country}, language: ${Locale.getDefault().language}")

        if (resparam.packageName in hookTargetPackage) {
            applyKoreanTranslation(resparam)
        }
    }

    private fun applyKoreanTranslation(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        val packageName = resparam.packageName
        val packageNameBar = packageName.replace(".", "_")

        XposedUtil.xposedDebug(tag, "packageName: $packageName")

        val modRes = XModuleResources.createInstance(XposedInit.modulePath, resparam.res)
        val clazz = R.string::class.java
        val fields = clazz.fields

        for (resName in fields.filter { it.name.startsWith(packageNameBar) }.map { it.name.removePrefix("${packageNameBar}_") }) {
            @SuppressLint("DiscouragedApi")
            val resId = modRes.getIdentifier("${packageNameBar}_${resName}", "string", Constants.APPLICATION_ID)
            if (resId != 0) {
                try {
                    resparam.res.setReplacement(
                        packageName,
                        "string",
                        resName,
                        modRes.fwd(resId)
                    )
                    XposedUtil.xposedDebug(tag, "Successfully replaced $packageName.$resName.")
                } catch (e: Resources.NotFoundException) {
                    XposedUtil.xposedException(tag, "Error occurred while replacing $packageName.$resName: ${e.message}")
                    continue
                }
            } else {
                XposedUtil.xposedDebug(tag, "Cannot found resId $resName")
            }
        }
    }
}
