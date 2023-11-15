package com.example.sssss;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;

public class MD5Utils {

    public static String getMD5(File file) {
        try {
            return DigestUtils.md5Hex(new FileInputStream(file));
        } catch (Exception e) {
        }
        return "";
    }

    public static String getMD5(String url) {
        try {
            return DigestUtils.md5Hex(url);
        } catch (Exception e) {
        }
        return "";
    }
}
