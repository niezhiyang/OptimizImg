package com.example.sssss;

import java.awt.event.ActionEvent

class CancelActionListener(dialog: TinyImageDialog) : ActionListenerBase(dialog) {

    override fun actionPerformed(e: ActionEvent) {
        val isInProgress = dialog.compressInProgress
        if (!isInProgress) {
            dialog.dispose()
        } else {
        }
        dialog.compressInProgress = false
        dialog.btnCancel.text = "取消"
    }

}