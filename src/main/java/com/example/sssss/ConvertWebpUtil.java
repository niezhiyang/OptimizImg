package com.example.sssss;

import com.intellij.openapi.project.Project;

import java.io.File;

/**
 * @author niezhiyang
 * since 2021/6/4
 */
public class ConvertWebpUtil {

    public static File securityFormatWebp(File file) {
        if (ImageUtil.isImage(file)) {
                if (file.getName().endsWith("jpg") || file.getName().endsWith("jpeg")) {
                    //jpg
                    return formatWebp(file);
                } else if (file.getName().endsWith("png")) {
                    //png
                    if (!ImageUtil.isAlphaPNG(file)) {
                        //不包含透明通道
                        return  formatWebp(file);
                    } else {
                        return  formatWebp(file);
                        //包含透明通道的png，进行压缩
//                        CompressUtil.compressImg(file, project);
                    }
                }
        }
        return null;

    }

    private static File formatWebp(File imgFile) {
        System.out.println(imgFile.getName()+"转化webp");
        if (ImageUtil.isImage(imgFile)) {
            String filePath = imgFile.getPath().substring(0, imgFile.getPath().lastIndexOf(".")) + ".webp";
            File webpFile = new File(filePath);
            Tools.cmd("cwebp", imgFile.getPath()+" -o "+webpFile.getPath()+" -lossless");
//            Tools.cmd("cwebp", imgFile.getPath()+" -o "+webpFile.getPath()+" -m 6 -quiet");
            System.out.println(imgFile.getName() + " 大小是："+imgFile.length()+"----web的大小是："+webpFile.length());


            if (webpFile.length() < imgFile.length()) {
                if (imgFile.exists()) {
                    imgFile.delete();
                }
            } else {
                //如果webp的大的话就抛弃
                if (webpFile.exists()) {
                    webpFile.delete();
                }
            }
            return webpFile;
        }
        return null;
    }


}
