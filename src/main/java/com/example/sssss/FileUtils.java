package com.example.sssss;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class FileUtils {
    public static final String CHARSET_NAME = "UTF-8";
    public static String sCodelocatorImageFileDirPath;
    public static String sCodeLocatorPluginDir;
    public static String sCodeLocatorMainDirPath;
    public static String sCodelocatorTmpFileDirPath;
    public static String sCodelocatorHistoryFileDirPath;

    public static List<VirtualFile> getMatchFileList(VirtualFile[] files, Predicate<VirtualFile> predicate, boolean breakWhenFoundOne) {
        List<VirtualFile> result = new LinkedList<>();
        if (files == null) {
            return result;
        }
        for (VirtualFile file : files) {
            if (file.isDirectory()) {
                if ("build".equals(file.getName())) {
                    continue;
                }
                result.addAll(getMatchFileList(file.getChildren(), predicate, breakWhenFoundOne));
                if (breakWhenFoundOne && !result.isEmpty()) {
                    break;
                }
            } else {
                if (predicate.test(file)) {
                    result.add(file);
                    if (breakWhenFoundOne) {
                        break;
                    }
                }
            }
        }
        return result;
    }
    public static byte[] getFileContentBytes(File file) {
        if (!file.exists() || file.isDirectory()) {
            return null;
        }
        byte[] fileBytes = new byte[(int) file.length()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileBytes);
            in.close();
            return fileBytes;
        } catch (Exception e) {
            Log.e("读取文件失败 " + file.getAbsolutePath(), e);
        }
        return null;
    }
    public static void deleteChildFile(String filePath) {
        deleteChildFile(new File(filePath));
    }

    public static void deleteChildFile(File file) {
        if (file == null || !file.exists() || !file.isDirectory()) {
            return;
        }
        final File[] files = file.listFiles();
        for (File f : files) {
            deleteFile(f);
        }
    }

    public static void deleteFile(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            final File[] files = file.listFiles();
            for (File f : files) {
                deleteFile(f);
            }
            file.delete();
        } else {
            file.delete();
        }
    }
    public static void init() {
        initCodeLocatorDir();
    }
    public static String getFileContent(File file) {
        try {
            final byte[] fileContentBytes = getFileContentBytes(file);
            if (fileContentBytes != null) {
                return new String(fileContentBytes, CHARSET_NAME);
            }
        } catch (Exception e) {
            Log.e("读取文件失败 " + file.getAbsolutePath(), e);
        }
        return "";
    }
    public static File findFileByName(File root, String findFileName, int maxLevel, Comparator<File> fileComparator) {
        return findFileByName(root, findFileName, maxLevel, 0, fileComparator);
    }

    private static File findFileByName(File rootFile, String findFileName, int maxLevel, int currentLevel, Comparator<File> fileComparator) {
        if (findFileName == null) {
            return null;
        }
        if (findFileName.equals(rootFile.getName())) {
            return rootFile;
        }
        if (rootFile.isDirectory()) {
            final File[] files = rootFile.listFiles();
            if (files == null) {
                return null;
            }
            final List<File> fileList = Arrays.asList(files);
            if (fileComparator != null) {
                fileList.sort(fileComparator);
            }
            for (File f : fileList) {
                if (maxLevel < 0 || currentLevel < maxLevel) {
                    File file = findFileByName(f, findFileName, maxLevel, currentLevel + 1, fileComparator);
                    if (file != null) {
                        return file;
                    }
                }
            }
        }
        return null;
    }
    public static void saveImageToFile(Image image, File file) {
        try {
            if (image == null) {
                return;
            }
            if (image instanceof BufferedImage) {
                ImageIO.write((BufferedImage) image, "PNG", new FileOutputStream(file));
            } else {
                BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
                        image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                bufferedImage.getGraphics().drawImage(image, 0, 0, null);
                ImageIO.write(bufferedImage, "PNG", new FileOutputStream(file));
            }
        } catch (Exception e) {
            Log.e("保存图片失败", e);
        }
    }
    public static boolean canOpenFile(File file) {
        if (file == null || !file.exists() || file.getName() == null || file.isDirectory()) {
            return false;
        }
        final String name = file.getName().toLowerCase();
        String[] supportFileSuffix = new String[]{
                ".png", ".jpg", ".jpeg", ".txt", ".json", ".css", ".js", ".html"
        };
        for (String suffix : supportFileSuffix) {
            if (name.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }
    @NotNull
    public static ByteArrayOutputStream readByteArrayOutputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream;
        try {
            byte[] buffer = new byte[4096];
            int readLength = -1;
            byteArrayOutputStream = new ByteArrayOutputStream();
            while ((readLength = inputStream.read(buffer)) > 0) {
                byteArrayOutputStream.write(buffer, 0, readLength);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                //关闭流
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return byteArrayOutputStream;
    }
    public static String sUserDesktopPath;
    private static void initCodeLocatorDir() {
        final String userHomePath = System.getProperty("user.home");
        sUserDesktopPath = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
        initMainFileDir(userHomePath);
        initTmpFileDir();
        initHistoryFileDir();
        initImageFileDir();
        deleteChildFile(sCodelocatorImageFileDirPath);
        deleteChildFile(sCodelocatorTmpFileDirPath);
    }
    private static void initMainFileDir(String userHomePath) {
        final File file = new File(userHomePath, ".codeLocator_main");
        sCodeLocatorMainDirPath = file.getPath();
        if (!file.exists()) {
            final boolean mkdirs = file.mkdirs();
            if (!mkdirs && sUserDesktopPath.equals(userHomePath)) {
                initMainFileDir(sUserDesktopPath);
            }
        } else if (!file.isDirectory()) {
            file.delete();
            file.mkdirs();
        }
    }
    private static void initTmpFileDir() {
        final File file = new File(sCodeLocatorMainDirPath, "tempFile");
        sCodelocatorTmpFileDirPath = file.getPath();
        if (!file.exists()) {
            file.mkdirs();
        } else if (!file.isDirectory()) {
            file.delete();
            file.mkdirs();
        }
    }
    private static void initHistoryFileDir() {
        final File file = new File(sCodeLocatorMainDirPath, "historyFile");
        sCodelocatorHistoryFileDirPath = file.getPath();
        if (!file.exists()) {
            file.mkdirs();
        } else if (!file.isDirectory()) {
            file.delete();
            file.mkdirs();
        }
    }
    private static void initImageFileDir() {
        final File file = new File(sCodeLocatorMainDirPath, "image");
        sCodelocatorImageFileDirPath = file.getPath();
        if (!file.exists()) {
            file.mkdirs();
        } else if (!file.isDirectory()) {
            file.delete();
            file.mkdirs();
        }
    }

}
