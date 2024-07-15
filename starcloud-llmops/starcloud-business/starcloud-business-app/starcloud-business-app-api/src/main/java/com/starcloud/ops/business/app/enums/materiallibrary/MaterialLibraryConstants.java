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


}
