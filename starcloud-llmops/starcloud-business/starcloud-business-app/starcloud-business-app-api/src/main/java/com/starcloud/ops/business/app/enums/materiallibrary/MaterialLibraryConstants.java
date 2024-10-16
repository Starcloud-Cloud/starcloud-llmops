package com.starcloud.ops.business.app.enums.materiallibrary;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
public interface MaterialLibraryConstants {

    /**
     * 示例模板名称
     */
    String TEMPLATE_FILE_NAME = "导入模板.xlsx";

    /**
     * 支持的压缩格式
     */
    String[] SUPPORT_COMPRESS_FORMAT = {"ZIP", "RAR"};


    /**
     * 示例模板名称 解释的列
     */
    Integer TEMPLATE_FILE_EXPLAIN_CELL = 1;

    /**
     * 示例模板名称 解释的列
     */
    Integer TEMPLATE_FILE_TABLE_HEAD_CELL = 2;


    String TEMPLATE_FILE_SUFFIX = "的导入模板";


    String MATERIAL_LIBRARY_TEMPLATE_SYSTEM = "{}";

    String MATERIAL_LIBRARY_TEMPLATE_COMMON = "{}";

    String MATERIAL_LIBRARY_TEMPLATE_PUBLISH = "{}";

    String MATERIAL_LIBRARY_TEMPLATE_COPY = "{}";


    String MATERIAL_LIBRARY_IMAGE_TYPE_ERROR = "https://service-oss.mofaai.com.cn/unrecognizable.png";

    String MATERIAL_LIBRARY_FILE_TYPE_ERROR = "https://service-oss.mofaai.com.cn/unknown_type.png";

    String MATERIAL_LIBRARY_FILE_NO_FOUND = "https://service-oss.mofaai.com.cn/upload_failed.png";

    String MATERIAL_LIBRARY_FAIL_IMAGE_UPLOAD = "https://service-oss.mofaai.com.cn/FDDCD7C6E456E5B131CFCF780B837E74.png";

    String MATERIAL_IMAGE_UPLOAD_PREFIX = "material_image_";

    String MATERIAL_IMAGE_REDIS_PREFIX = "material_image:%s:%s";


}
