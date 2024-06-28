package com.starcloud.ops.business.app.util.MaterialLibrary;

/**
 * Filename:     starcloud-llmops
 * Description:  com.starcloud.ops.business.app.util.MaterialLibrary
 * Company:      mdc.ai Inc.
 *
 * @Author: djl
 * @version: 1.0
 * Create at:    2024/06/27  15:36
 * Modification History:
 * Date          Author      Version     Description
 * ------------------------------------------------------------------
 * 2024/06/27   AlanCusack    1.0         1.0 Version
 */
public class OperateImportUtil {

    /**
     * 获取文件后缀名
     *
     * @param fileName 文件名称
     * @return
     */
    public static String getFileExtension(String fileName) {

        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1) {
            // "文件名无扩展名，无法确定文件类型"
            return "";
        }
        // 不包含点，所以使用dotIndex + 1
        return fileName.substring(dotIndex + 1);
    }
}
