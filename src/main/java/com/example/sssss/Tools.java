package com.example.sssss;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.example.sssss.FileUtils.sCodeLocatorMctoolsPath;

/**
 * @author niezhiyang
 * since 2021/6/5
 */
public class Tools {
    public static void cmd(String cmd, String params) {
        String cmdStr = "";

        if (isCmdExist(cmd)) {
            cmdStr = cmd + " " + params;
        } else {
            if (isMac()) {
                cmdStr = sCodeLocatorMctoolsPath + "mac/" + cmd + " " + params;
            } else if (isWindows()) {
                cmdStr = sCodeLocatorMctoolsPath + "windows/" + cmd + " " + params;

            } else if (isLinux()) {
                cmdStr = sCodeLocatorMctoolsPath + "linux/" + cmd + " " + params;

            }
        }
        if (cmdStr == "") {
            return;
        }
        outputMessage(cmdStr);

    }

    private static void outputMessage(String cmd) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Tools: 错误了"+e.toString());

        }

    }

    private static void chmod() {
        outputMessage("chmod 755 -R "+sCodeLocatorMctoolsPath);
    }


    private static boolean isCmdExist(String cmd) {
        String result = "";
        if (isMac() || isLinux()) {
            result = executeCmd("which " + cmd);
        } else {
            executeCmd("where " + cmd);
        }

        return result != null && !result.isEmpty();
    }

    private static boolean isLinux() {
        return System.getProperty("os.name").startsWith("Linux");
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").startsWith("win");
    }

    private static boolean isMac() {
        return System.getProperty("os.name").startsWith("Mac OS");
    }

    private static String executeCmd(String cmd) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return bufferReader.readLine();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "";

    }
}
