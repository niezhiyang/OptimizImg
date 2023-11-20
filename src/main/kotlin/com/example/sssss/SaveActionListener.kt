package com.example.sssss;

import com.intellij.openapi.application.ApplicationManager
import com.intellij.util.getValue
import java.awt.event.ActionEvent
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class SaveActionListener(dialog: TinyImageDialog) : ActionListenerBase(dialog) {

    override fun actionPerformed(e: ActionEvent) {

        dialog.btnSave.isEnabled = false
        dialog.btnCancel.isEnabled = false
        ApplicationManager.getApplication().runWriteAction(object : Runnable {
            override fun run() {
                for (node in dialog.imageFileNodes) {
                    try {
                        if (!node.isChecked || node.compressedImageFile == null) {
                            continue
                        }
                        if (node.compressedImageFile!!.length() < node.virtualFile!!.length) {
                            val path = node.virtualFile!!.path
                            val pngOrWebp = path.substring(path.lastIndexOf(".")+1)

                            val pathCompressedImageFile = node.compressedImageFile!!.absolutePath
                            val pngOrWebpCompress =
                                pathCompressedImageFile.substring(pathCompressedImageFile.lastIndexOf(".")+1)
                            val file = File(path.substring(0,path.lastIndexOf(".")+1)+pngOrWebpCompress)
                            if(!file.exists()){
                                file.createNewFile()
                            }
                            val stre = FileOutputStream(file)
                            println("file = 》 ${file.absolutePath}")
//                            val stream = node.virtualFile!!.getOutputStream(this)
                            stre.write(FileUtils.getFileContentBytes(node.compressedImageFile))
                            stre.close()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        println("file = 》 失败")
                    }
                }
                dialog.dispose()
            }
        })
    }
}