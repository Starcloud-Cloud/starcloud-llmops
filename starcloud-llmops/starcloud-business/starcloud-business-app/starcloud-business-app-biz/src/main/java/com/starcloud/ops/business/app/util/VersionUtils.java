package com.starcloud.ops.business.app.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 版本工具类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
public class VersionUtils {

    /**
     * 第二位和第三位进位的最大值
     */
    private static final int MAX_NUMBER = 99;

    /**
     * 默认版本号
     */
    private static final String DEFAULT_VERSION = "1.0.0";

    /**
     * 比较版本号
     *
     * @param version1 版本号1
     * @param version2 版本号2
     * @return 比较结果: 如果 version1 小于 version2。则返回 -1；如果 version1 大于 version2。则返回 1；相等则返回 0。
     */
    public static int compareVersion(String version1, String version2) {

        String[] v1Parts = StringUtils.isNoneBlank(version1) ? version1.split("\\.") : DEFAULT_VERSION.split("\\.");
        String[] v2Parts = StringUtils.isNoneBlank(version2) ? version2.split("\\.") : DEFAULT_VERSION.split("\\.");

        if (v1Parts.length != 3 || v2Parts.length != 3) {
            throw new IllegalArgumentException("Version Compare Failed: " + version1 + " vs " + version2 + " The version format is incorrect, version format like: 1.0.0");
        }

        for (int i = 0; i < v1Parts.length; i++) {
            int v1, v2;
            if (i == 0) {
                v1 = StringUtils.isNoneBlank(v1Parts[i]) ? Integer.parseInt(v1Parts[i]) : 1;
                v2 = StringUtils.isNoneBlank(v2Parts[i]) ? Integer.parseInt(v2Parts[i]) : 1;
            } else {
                v1 = StringUtils.isNoneBlank(v1Parts[i]) ? Integer.parseInt(v1Parts[i]) : 0;
                v2 = StringUtils.isNoneBlank(v2Parts[i]) ? Integer.parseInt(v2Parts[i]) : 0;
            }

            if (v1 < v2) {
                return -1;
            } else if (v1 > v2) {
                return 1;
            }
        }

        return 0;
    }

    /**
     * 获取下一个版本号
     *
     * @param version 当前版本号
     * @return 下一个版本号
     */
    public static String nextVersion(String version) {

        // 如果为空，返回默认版本号
        if (StringUtils.isBlank(version)) {
            return DEFAULT_VERSION;
        }

        String[] parts = version.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Version Compare Failed: " + version + " The version format is incorrect, version format like: 1.0.0");
        }

        int major = StringUtils.isNoneBlank(parts[0]) ? Integer.parseInt(parts[0]) : 1;
        int minor = StringUtils.isNoneBlank(parts[1]) ? Integer.parseInt(parts[1]) : 0;
        if (minor > MAX_NUMBER) {
            minor = MAX_NUMBER;
        }
        int patch = StringUtils.isNoneBlank(parts[2]) ? Integer.parseInt(parts[2]) : 0;
        if (patch > MAX_NUMBER) {
            patch = MAX_NUMBER;
        }

        if (patch < MAX_NUMBER) {
            patch++;
        } else {
            patch = 0;
            if (minor < MAX_NUMBER) {
                minor++;
            } else {
                minor = 0;
                major++;
            }
        }

        return major + "." + minor + "." + patch;
    }

    public static void main(String[] args) {
        System.out.println(compareVersion("2.0.0", "1.0.10"));
        System.out.println(nextVersion("1.99.99"));

    }
}

