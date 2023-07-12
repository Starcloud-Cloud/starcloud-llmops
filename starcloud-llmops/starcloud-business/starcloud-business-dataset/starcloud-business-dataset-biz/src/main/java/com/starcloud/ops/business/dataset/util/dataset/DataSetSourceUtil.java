package com.starcloud.ops.business.dataset.util.dataset;

import org.springframework.web.multipart.MultipartFile;

/**
 * Filename:     starcloud-llmops
 * Description:  com.starcloud.ops.business.dataset.util.dataset
 * Company:      mdc.ai Inc.
 *
 * @Author: djl
 * @version: 1.0
 * Create at:    2023/07/06  14:50
 * Modification History:
 * Date          Author      Version     Description
 * ------------------------------------------------------------------
 * 2023/07/06   AlanCusack    1.0         1.0 Version
 */
public class DataSetSourceUtil {


    /**
     * 获取文件扩展名
     * @param file
     * @return
     */
    public static String getExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            int dotIndex = originalFilename.lastIndexOf(".");
            if (dotIndex > 0 && dotIndex < originalFilename.length() - 1) {
                return originalFilename.substring(dotIndex + 1).toLowerCase();
            }
        }
        return "";
    }

    /**
     * 获取文件MimeType
     * @param extension
     * @return
     */
    public static String getMimeType(String extension) {
        switch (extension) {
            case "txt":
                return "text/plain";
            case "pdf":
                return "application/pdf";
            case "doc":
            case "docx":
                return "application/msword";
            case "xls":
            case "xlsx":
                return "application/vnd.ms-excel";
            case "png":
                return "image/png";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            default:
                return "application/octet-stream";
        }
    }

}
