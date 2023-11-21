package com.example.sssss;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.command.CommandAdapter;
import com.intellij.openapi.command.CommandEvent;
import com.intellij.openapi.command.CommandListener;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.util.*;
import java.util.Timer;

public class CodeLocatorStartupActivity implements StartupActivity {
    Timer timer = new Timer();
    @Override
    public void runActivity(@NotNull Project project) {
        Disposable tempDisposable = Disposer.newDisposable();
        subscribeFileCopy(project, tempDisposable);
    }


    private void subscribeFileCopy(@NotNull Project project, Disposable tempDisposable) {
        project.getMessageBus().connect(tempDisposable).subscribe(CommandListener.TOPIC, new CommandAdapter() {
            @Override
            public void commandFinished(@NotNull CommandEvent event) {
                if (project != event.getProject()) {
                    return;
                }
                if (CodeLocatorApplicationInitializedListener.sAddImageFiles.isEmpty()) {
                    return;
                }
//                timer.cancel();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        showCompressDialog();
                    }
                }, 1000);
                final ArrayList<VirtualFile> virtualFiles = new ArrayList<>(CodeLocatorApplicationInitializedListener.sAddImageFiles);
                CodeLocatorApplicationInitializedListener.sAddImageFiles.clear();
                Project project = event.getProject();
                final List<VirtualFile> list = FileUtils.getMatchFileList(virtualFiles.toArray(new VirtualFile[virtualFiles.size()]), TinyImageMenuAction.sPredicate, false);
                final JFrame frame = WindowManager.getInstance().getFrame(project);
                if (frame == null) {
                    return;
                }
                long total = 0;
                for (VirtualFile f : list) {
                    total += f.getLength();
                }
                System.out.println("total=?" + total);
                if (total > 3) {
                    TinyImageDialog dialog = new TinyImageDialog(project,
                            list, virtualFiles, true, null, null);
                    dialog.setDialogSize(frame);
                    dialog.setVisible(true);
                    dialog.setAlwaysOnTop(false);
                } else {
                    ProgressManager.getInstance()
                            .run(new Task.Backgroundable(project, "TinyPng 压缩中...", true) {

                                private HashMap<VirtualFile, File> compressMap = new HashMap<>();

                                private final String projectImageStoreKey = TinyImageDialog.getProjectImageStoreKey(project);

                                @Override
                                public void onCancel() {
                                    super.onCancel();
                                    ThreadUtils.runOnUIThread(() -> {
                                        TinyImageDialog dialog = new TinyImageDialog(project, list, virtualFiles, true, compressMap, projectImageStoreKey);
                                        dialog.setDialogSize(frame);
                                        dialog.setVisible(true);
                                        dialog.setAlwaysOnTop(false);
                                    });
                                }

                                @Override
                                public void run(@NotNull ProgressIndicator indicator) {
                                    if (!new File(FileUtils.sCodelocatorImageFileDirPath, projectImageStoreKey).exists()) {
                                        new File(FileUtils.sCodelocatorImageFileDirPath, projectImageStoreKey).mkdir();
                                    }
                                    boolean isSameSize = false;
                                    for (VirtualFile f : list) {
                                        try {
                                            if (indicator.isCanceled()) {
                                                return;
                                            }
                                            indicator.setText("TinyPng 压缩中... " + f.getName());
                                            final UploadInfo uploadInfo = TinyPng.tinifyFile(projectImageStoreKey, new File(f.getPath()));
                                            final File file = uploadInfo.getOutput().getFile();
                                            if (file != null) {
                                                compressMap.put(f, file);
                                                isSameSize = (f.getLength() == file.length());
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (indicator.isCanceled()) {
                                        return;
                                    }
                                    if (list.size() == 1 && isSameSize) {
                                        return;
                                    }
                                    ThreadUtils.runOnUIThread(() -> {
                                        TinyImageDialog dialog = new TinyImageDialog(
                                                project,
                                                list,
                                                virtualFiles,
                                                true,
                                                compressMap,
                                                projectImageStoreKey);
                                        dialog.setDialogSize(frame);
                                        dialog.setVisible(true);
                                        dialog.setAlwaysOnTop(false);
                                    });
                                }
                            });
                }
            }
        });
    }

    private void showCompressDialog() {

    }
}
