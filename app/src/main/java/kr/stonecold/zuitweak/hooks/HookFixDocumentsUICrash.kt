package kr.stonecold.zuitweak.hooks

import android.graphics.Rect
import android.view.MotionEvent
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

@Suppress("unused")
class HookFixDocumentsUICrash : HookBaseHandleLoadPackage() {
    override val menuItem = HookMenuItem(
        category = HookMenuCategory.PRC,
        title = "DocumentsUI 강제 종료 수정",
        description = "DocumentsUI에서 Mouse Drag시 발생하는 강제 종료 오류를 수정합니다.",
        defaultSelected = false,
    )

    override val hookTargetDevice: Array<String> = emptyArray()
    override val hookTargetRegion: Array<String> = arrayOf("PRC")
    override val hookTargetPackage: Array<String> = arrayOf("com.android.documentsui")
    override val hookTargetPackageOptional: Array<String> = emptyArray()

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            "com.android.documentsui" -> {
                executeHooks(
                    lpparam,
                    ::hookListDocumentHolderInDragRegion,
                )
            }
        }
    }

    private fun hookListDocumentHolderInDragRegion(lpparam: XC_LoadPackage.LoadPackageParam) {
        val className = "com.android.documentsui.dirlist.ListDocumentHolder"
        val methodName = "inDragRegion"
        val parameterTypes = arrayOf<Any>(MotionEvent::class.java)
        val callback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                try {
                    val thisObject = param.thisObject
                    val motionEvent = param.args[0] as MotionEvent

                    // Original method implementation
                    val itemView = XposedHelpers.getObjectField(thisObject, "itemView")
                    if (XposedHelpers.callMethod(itemView, "isActivated") as Boolean) {
                        param.result = true
                        return
                    }

                    val mIconLayout = XposedHelpers.getObjectField(thisObject, "mIconLayout")
                    val mTitle = XposedHelpers.getObjectField(thisObject, "mTitle")
                    if (mIconLayout == null || mTitle == null) {
                        param.result = false
                        return
                    }

                    val iArr = IntArray(2)
                    XposedHelpers.callMethod(mIconLayout, "getLocationOnScreen", iArr)
                    val rect = Rect()
                    val mTitlePaint = XposedHelpers.callMethod(mTitle, "getPaint")
                    val mTitleText = XposedHelpers.callMethod(mTitle, "getText") as String
                    XposedHelpers.callMethod(mTitlePaint, "getTextBounds", mTitleText, 0, mTitleText.length, rect)

                    val i = iArr[0]
                    val mIconLayoutWidth = XposedHelpers.callMethod(mIconLayout, "getWidth") as Int
                    val mIconLayoutHeight = XposedHelpers.callMethod(mIconLayout, "getHeight") as Int
                    val rectWidth = rect.width()
                    val rectHeight = rect.height()

                    val newRect = Rect(i, iArr[1], mIconLayoutWidth + i + rectWidth, iArr[1] + maxOf(mIconLayoutHeight, rectHeight))
                    param.result = newRect.contains(motionEvent.rawX.toInt(), motionEvent.rawY.toInt())
                } catch (e: Throwable) {
                    handleHookException(tag, e, className, methodName, *parameterTypes)
                }
            }
        }

        executeHook(lpparam, className, methodName, *parameterTypes, callback)
    }
}
