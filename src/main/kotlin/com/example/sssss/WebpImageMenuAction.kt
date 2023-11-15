package com.example.sssss

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager
import java.util.*
import java.util.function.Predicate

class WebpImageMenuAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val roots = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(e.dataContext) ?: return
        val list = FileUtils.getMatchFileList(roots, sPredicate, false)
        val frame = WindowManager.getInstance().getFrame(project) ?: return
        val dialog = TinyImageDialog(project!!, list, Arrays.asList(*roots), false, null, null)
        dialog.setDialogSize(frame)
        dialog.isVisible = true
        dialog.isAlwaysOnTop = false
    }
    companion object {

        private val sSupportedImageType = listOf("png", "jpg", "jpeg")

        @JvmField
        var sPredicate =
            Predicate<VirtualFile> { virtualFile ->
                if (virtualFile.extension == null) {
                    false
                } else {
                    !virtualFile.path.contains("build/intermediates/")
                            && sSupportedImageType.contains(virtualFile.extension!!.toLowerCase())
                            && !virtualFile.name.toLowerCase().endsWith(".9.png")
                            && !virtualFile.name.toLowerCase().endsWith(".9.webp")
                }
            }
    }
}