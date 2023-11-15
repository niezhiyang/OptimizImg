package com.example.sssss;

import com.intellij.ide.ApplicationInitializedListener;
import com.intellij.openapi.vfs.*;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class CodeLocatorApplicationInitializedListener implements ApplicationInitializedListener {
    @Override
    public void componentsInitialized() {
        FileUtils.init();
        registerVirtualFileListener();
    }
    public static LinkedList<VirtualFile> sAddImageFiles = new LinkedList<>();
    private void registerVirtualFileListener() {
        VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileAdapter() {
            @Override
            public void fileCreated(@NotNull VirtualFileEvent event) {
                if (!(event instanceof VirtualFileCopyEvent)) {
                    return;
                }
                super.fileCreated(event);
                final VirtualFile file = event.getFile();
                if (!TinyImageMenuAction.sPredicate.test(file)) {
                    return;
                }
                sAddImageFiles.add(file);
            }
        });
    }
}
