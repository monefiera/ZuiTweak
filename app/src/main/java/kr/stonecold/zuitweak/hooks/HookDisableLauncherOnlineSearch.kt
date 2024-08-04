package kr.stonecold.zuitweak.hooks

import android.content.Context
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_LoadPackage

@Suppress("unused")
class HookDisableLauncherOnlineSearch : HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.PRC,
        title = "런처 온라인 검색 제거",
        description = "런처에서 보이는 중국어 검색을 비활성화 합니다.",
        defaultSelected = false,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = arrayOf("PRC")
    override val hookTargetPackage: Array<String> = arrayOf("com.zui.launcher")
    override val hookTargetPackageOptional: Array<String> = arrayOf("com.zui.desktoplauncher")

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.zui.launcher", "com.zui.desktoplauncher" -> {
                executeHooks(
                    lpparam,
                    ::hookGraphicsUtilsIsZuiRow,
                    ::hookUtilitiesIsZuiRow,
                    ::hookHotWordDownLoadHotWordDownLoad,
                    ::hookQuicksAppDownLoadDownLoadQuicksApp,
                    ::hookQuicksAppDownLoadDownloadAllData,
                    ::hookSearchDownloadHelperDownloadGameApp,
                    ::hookSearchDownloadHelperGetGameAppList,
                    ::hookInternetGlobalSearchIsInternetSearchEnabled,
                    ::hookGlobalSearchUtilsGetDatabaseType,
                    ::hookDownloadSpanCheckFileAndDownLoadDao,
                )
            }
        }
    }

    private fun hookGraphicsUtilsIsZuiRow(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "${lpparam.packageName}.icons.GraphicsUtils"
        val methodName = "isZuiRow"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant(false)

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookUtilitiesIsZuiRow(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "${lpparam.packageName}.Utilities"
        val methodName = "isZuiRow"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant(true)

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookHotWordDownLoadHotWordDownLoad(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "${lpparam.packageName}.globalsearch.HotWordDownLoad"
        val methodName = "hotWordDownLoad"
        val parameterTypes = arrayOf<Any>(
            String::class.java,
            "${lpparam.packageName}.Launcher"
        )
        val callback = XC_MethodReplacement.returnConstant(null)

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookQuicksAppDownLoadDownLoadQuicksApp(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "${lpparam.packageName}.globalsearch.QuicksAppDownLoad"
        val methodName = "downLoadQuicksApp"
        val parameterTypes = arrayOf<Any>(
            "${lpparam.packageName}.globalsearch.SearchViewInterface",
            String::class.java,
            String::class.java
        )
        val callback = XC_MethodReplacement.returnConstant(null)

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookQuicksAppDownLoadDownloadAllData(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "${lpparam.packageName}.globalsearch.QuicksAppDownLoad"
        val methodName = "downloadAllData"
        val parameterTypes = arrayOf<Any>(
            Context::class.java,
            "${lpparam.packageName}.globalsearch.quicksBean.QuicksDocs",
            String::class.java
        )
        val callback = XC_MethodReplacement.returnConstant(null)

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookSearchDownloadHelperDownloadGameApp(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "${lpparam.packageName}.globalsearch.SearchDownloadHelper"
        val methodName = "downloadGameApp"
        val parameterTypes = arrayOf<Any>(
            Context::class.java,
            String::class.java,
            String::class.java,
            "${lpparam.packageName}.globalsearch.quicksBean.NetGameSimpleInfo"
        )
        val callback = XC_MethodReplacement.returnConstant(null)

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookSearchDownloadHelperGetGameAppList(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "${lpparam.packageName}.globalsearch.SearchDownloadHelper"
        val methodName = "getGameAppList"
        val parameterTypes = arrayOf<Any>(
            "${lpparam.packageName}.GlobalSearchView",
            Int::class.java
        )
        val callback = XC_MethodReplacement.returnConstant(null)

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookInternetGlobalSearchIsInternetSearchEnabled(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "${lpparam.packageName}.globalsearch.InternetGlobalSearch"
        val methodName = "isInternetSearchEnabled"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant(false)

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookGlobalSearchUtilsGetDatabaseType(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "${lpparam.packageName}.globalsearch.utils.GlobalSearchUtils"
        val methodName = "getDatabaseType"
        val parameterTypes = emptyArray<Any>()
        val callback = XC_MethodReplacement.returnConstant("global_empty.db")

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }

    private fun hookDownloadSpanCheckFileAndDownLoadDao(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "${lpparam.packageName}.DownloadSpan"
        val methodName = "checkFileAndDownLoadDao"
        val parameterTypes = arrayOf<Any>(String::class.java)
        val callback = XC_MethodReplacement.returnConstant(null)

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }
}
