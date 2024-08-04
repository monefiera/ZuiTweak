package kr.stonecold.zuitweak.hooks

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kr.stonecold.zuitweak.common.Util
import kr.stonecold.zuitweak.common.XposedUtil
import kr.stonecold.zuitweak.common.XposedUtil.LogLevel
import kr.stonecold.zuitweak.common.XposedUtil.currentLogLevel
import kotlinx.coroutines.*

interface IHookBase {
    val tag: String
        get() = this::class.java.simpleName

    val menuItem: HookMenuItem

    val hookTargetDevice: Array<String>
    val hookTargetRegion: Array<String>

    val hookTargetPackage: Array<String>
    val hookTargetPackageOptional: Array<String>

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam)

    fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam)
}

data class HookMenuItem(
    val category: HookMenuCategory,
    val title: String,
    val description: String,
    val defaultSelected: Boolean = true,
    val isDebug: Boolean = false
)

enum class HookMenuCategory {
    COMMON,
    ROW,
    PRC,
    DEVICE,
    DEVELOPMENT,
}

abstract class HookBase: IHookBase {
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    private val methodExistenceMap: MutableMap<String, Boolean?> = mutableMapOf()

    open fun isEnabled(): Boolean {
        return true
    }

    private fun getMetaKey(className: String, methodName: String, vararg parameterTypes: Any): String {
        val parameterTypeNames = parameterTypes.mapNotNull { param ->
            when (param) {
                is Class<*> -> {
                    if (param.isArray) {
                        param.componentType.name + "[]"
                    } else {
                        param.name
                    }
                }

                is String -> param
                else -> null
            }
        }.toTypedArray()

        val qualifiedName = if (methodName.isEmpty()) className else "$className.$methodName"
        val methodKey = "$qualifiedName(${parameterTypeNames.joinToString(",")})"

        return methodKey
    }

    fun getClassMethodName(className: String, methodName: String, vararg parameterTypes: Any): String {
        val parameterTypeNames = parameterTypes.mapNotNull { param ->
            when (param) {
                is Class<*> -> {
                    if (param.isArray) {
                        param.componentType.name + "[]"
                    } else {
                        param.name
                    }
                }

                is String -> param
                else -> null
            }
        }.toTypedArray()

        val qualifiedName = if (methodName.isEmpty()) className else "$className.$methodName"
        val classMethodName = "$qualifiedName(${parameterTypeNames.joinToString(",") { it.split(".").last() }})"

        return classMethodName
    }

    fun checkClassMethod(clazzOrClassName: Any, classLoader: ClassLoader, methodName: String, vararg parameterTypes: Any): Boolean? {
        require(clazzOrClassName is Class<*> || clazzOrClassName is String) {
            "Unsupported type: ${clazzOrClassName::class.simpleName}"
        }

        val className = if (clazzOrClassName is Class<*>) clazzOrClassName.name else clazzOrClassName as String
        val methodKey = this.getMetaKey(className, methodName, *parameterTypes)
        val classMethodName = this.getClassMethodName(className, methodName, *parameterTypes)

        if (methodExistenceMap[methodKey] == null) {
            val methodExists = Util.getMethod(clazzOrClassName, classLoader, methodName, *parameterTypes) != null
            methodExistenceMap[methodKey] = methodExists

            XposedUtil.xposedDebug(tag, "Method existence check for $classMethodName: $methodExists")
        }

        return methodExistenceMap[methodKey]
    }

    fun executeHooks(lpparam: XC_LoadPackage.LoadPackageParam, vararg hooks: (XC_LoadPackage.LoadPackageParam) -> Unit) {
        CoroutineScope(Dispatchers.Default).launch {
            val jobs = hooks.map { hook ->
                async {
                    withContext(Dispatchers.Default) {
                        hook(lpparam)
                    }
                }
            }
            jobs.awaitAll()
        }
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {}

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {}

    fun handleHookException(tag:String, e: Throwable, clazzOrClassName: Any,  methodName: String, vararg parameterTypes: Any) {
        if (currentLogLevel.level < LogLevel.ERROR.level) {
            return
        }

        require(clazzOrClassName is Class<*> || (clazzOrClassName is String && clazzOrClassName.isNotEmpty())) {
            "Unsupported type: ${clazzOrClassName::class.simpleName}"
        }

        val className = if (clazzOrClassName is Class<*>) clazzOrClassName.name else clazzOrClassName as String
        val classMethodName = this.getClassMethodName(className, methodName, *parameterTypes)
        val stackTraceString = e.stackTrace.joinToString("\n") { element ->
            "at ${element.className}.${element.methodName}(${element.fileName}:${element.lineNumber})"
        }

        XposedUtil.xposedException(tag, "Error occurred while hooking $classMethodName: ${e.message}\nStack Trace:\n${stackTraceString}"
        )
    }
}

abstract class HookBaseHandleLoadPackage : HookBase() {
    open fun executeHook(lpparam: XC_LoadPackage.LoadPackageParam, clazzOrClassName: Any, methodName: String, vararg parameterTypesAndCallback: Any) {
        require(clazzOrClassName is Class<*> || (clazzOrClassName is String && clazzOrClassName.isNotEmpty())) {
            "Unsupported type: ${clazzOrClassName::class.simpleName}"
        }
        require(parameterTypesAndCallback.isNotEmpty() && parameterTypesAndCallback.last() is XC_MethodHook) {
            "No callback defined"
        }

        val parameterTypes = parameterTypesAndCallback.dropLast(1).toTypedArray()
        val callback = parameterTypesAndCallback.last() as XC_MethodHook
        val className = if (clazzOrClassName is Class<*>) clazzOrClassName.name else clazzOrClassName as String
        val classMethodName = getClassMethodName(className, methodName, *parameterTypes)

        var successRegister = true
        try {
            if (checkClassMethod(className, lpparam.classLoader, methodName, *parameterTypes) != true) {
                XposedUtil.xposedError(tag, "Failed to register hook for $classMethodName: class or method was not found")
                successRegister = false
            } else {
                if (clazzOrClassName is Class<*>) {
                    if (methodName.isNotEmpty()) {
                        XposedHelpers.findAndHookMethod(clazzOrClassName, methodName, *parameterTypes, callback)
                    } else {
                        XposedHelpers.findAndHookConstructor(clazzOrClassName, *parameterTypes, callback)
                    }
                } else {
                    if (methodName.isNotEmpty()) {
                        XposedHelpers.findAndHookMethod(className, lpparam.classLoader, methodName, *parameterTypes, callback)
                    } else {
                        XposedHelpers.findAndHookConstructor(className, lpparam.classLoader, *parameterTypes, callback)
                    }
                }
            }
        } catch (e: Throwable) {
            val stackTraceString = e.stackTrace.joinToString("\n") { element ->
                "at ${element.className}.${element.methodName}(${element.fileName}:${element.lineNumber})"
            }
            XposedUtil.xposedException(tag, "Failed to register hook for $classMethodName: ${e.message}\nStack Trace:\n${stackTraceString}")
            successRegister = false
        }

        if (successRegister) {
            XposedUtil.xposedInfo(tag, "Successfully registered hook for $classMethodName")
        }
    }
}

abstract class HookBaseHandleInitPackageResources : HookBase()
