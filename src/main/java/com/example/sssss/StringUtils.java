package com.example.sssss;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtils {

    public static Pattern sIpPattern = Pattern.compile("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}");

    public static Pattern sHexPattern = Pattern.compile("[0-9a-fA-F]+");

    public static Pattern sNumPattern = Pattern.compile("[0-9]+");

    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");

    public static String getStrNumber(String str, boolean hex) {
        if (hex) {
            final Matcher matcher = sHexPattern.matcher(str);
            if (matcher.find()) {
                return matcher.group();
            }
        } else {
            final Matcher matcher = sNumPattern.matcher(str);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return "";
    }



    public static String appendArgToUrl(String url, String arg) {
        if (arg == null || url == null) {
            return url;
        }
        if (url.contains("?")) {
            return url + arg;
        }
        return url + "?" + arg;
    }

    public static boolean fuzzyMatching(String source, String key) {
        if (source == null || key == null) {
            return false;
        }
        if (source.length() < key.length()) {
            return false;
        }
        source = source.toLowerCase();
        key = key.toLowerCase();
        int startIndex = 0;
        for (int i = 0; i < key.length(); i++) {
            final char charAt = key.charAt(i);
            startIndex = source.indexOf(charAt, startIndex);
            if (startIndex < 0) {
                return false;
            }
            startIndex = startIndex + 1;
        }
        return true;
    }

    public static String getNameWithoutPkg(String className) {
        int lastDotIndex = className.lastIndexOf(".");
        if (lastDotIndex > -1) {
            return className.substring(lastDotIndex + 1);
        }
        return className;
    }

    public static long getVersionInt(String versionStr) {
        if (versionStr == null) {
            return 0;
        }
        final Pattern compile = Pattern.compile("[0-9\\.]+");
        final Matcher matcher = compile.matcher(versionStr);
        if (matcher.find()) {
            versionStr = matcher.group();
        }

        final String[] split = versionStr.split("\\.");
        long versionNum = 0;
        try {
            for (int i = 0; i < split.length; i++) {
                int value = 0;
                if (i < split.length) {
                    if (!split[i].trim().isEmpty()) {
                        value = Integer.valueOf(split[i].trim());
                    }
                }
                versionNum = versionNum * 1000 + value;
            }
        } catch (Throwable t) {
            Log.e("getVersionIntError versionStr: " + versionStr, t);
        }
        return versionNum;
    }

    @Nullable
    public static String grepLine(String originStr, String key) {
        if (originStr == null) {
            return null;
        }
        final String[] split = originStr.split("\n");
        for (String line : split) {
            if (line != null && line.contains(key)) {
                return line;
            }
        }
        return null;
    }

    @Nullable
    public static List<String> grep(String originStr, String key) {
        if (originStr == null) {
            return null;
        }
        final ArrayList<String> grepLines = new ArrayList<>();
        final String[] split = originStr.split("\n");
        for (String line : split) {
            if (line != null && line.contains(key)) {
                grepLines.add(line);
            }
        }
        return grepLines;
    }

    @NotNull
    public static String getValue(String originStr, String key, String split) {
        if (originStr == null || key == null) {
            return "";
        }
        final int indexOfKey = originStr.indexOf(key);
        if (indexOfKey > -1) {
            if (split == null) {
                return originStr.substring(indexOfKey + key.length());
            }
            final int indexOfValueEnd = originStr.indexOf(split, indexOfKey + key.length());
            if (indexOfValueEnd > -1) {
                return originStr.substring(indexOfKey + key.length(), indexOfValueEnd);
            } else {
                return originStr.substring(indexOfKey + key.length());
            }
        }
        return "";
    }

    public static String getSimpleName(String className) {
        if (className == null || className.isEmpty()) {
            return className;
        }
        final int lastDot = className.lastIndexOf(".");
        if (lastDot < 0) {
            return className;
        }
        return className.substring(lastDot + 1);
    }

    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        boolean isInQuotationMarks = false;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
                case '"':
                    if (last != '\\') {
                        isInQuotationMarks = !isInQuotationMarks;
                    }
                    sb.append(current);
                    break;

                case '{':
                case '[':
                    sb.append(current);
                    if (!isInQuotationMarks) {
                        if ((jsonStr.length() > i + 1) && current == '[' && jsonStr.charAt(i + 1) == ']') {
                            sb.append(']');
                            i++;
                        } else {
                            sb.append('\n');
                            indent++;
                            addIndentBlank(sb, indent);
                        }
                    }
                    break;

                case '}':
                case ']':
                    if (!isInQuotationMarks) {
                        sb.append('\n');
                        indent--;
                        addIndentBlank(sb, indent);
                    }
                    sb.append(current);
                    break;

                case ',':
                    sb.append(current);
                    if (last != '\\' && !isInQuotationMarks) {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;

                default:
                    sb.append(current);
            }
        }
        return sb.toString();
    }

    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append("    ");
        }
    }

    public static String getFileSize(long fileSize) {
        return getFileSize(fileSize, false);
    }

    public static String getFileSize(long fileSize, boolean withOriginSize) {
        String unit = "B";
        float size = 0;
        if (fileSize < 1000) {
            size = fileSize;
        } else if (fileSize < 1000L * 1000L) {
            unit = "KB";
            size = (1.0f * fileSize / (1000L));
        } else if (fileSize < 1000L * 1000L * 1000L) {
            unit = "M";
            size = (1.0f * fileSize / (1000L * 1000L));
        } else if (fileSize < 1000L * 1000L * 1000L * 1000L) {
            unit = "G";
            size = (1.0f * fileSize / (1000L * 1000L * 1000L));
        } else {
            unit = "T";
            size = (1.0f * fileSize / (1000L * 1000L * 1000L * 1000L));
        }
        return String.format("%.1f", size) + unit + (withOriginSize ? (" (" + fileSize + "B)") : "");
    }




}
