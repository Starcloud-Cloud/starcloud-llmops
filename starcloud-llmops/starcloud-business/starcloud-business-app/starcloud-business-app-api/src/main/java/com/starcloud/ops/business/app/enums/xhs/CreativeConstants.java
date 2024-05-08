package com.starcloud.ops.business.app.enums.xhs;

import java.nio.file.Paths;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
public interface CreativeConstants {

    /**
     * 生成模式
     */
    String GENERATE_MODE = "GENERATE_MODE";

    /**
     * 参考内容
     */
    String REFERS = "REFERS";

    /**
     * 参考内容 带模仿要求
     */
    String REFERS_IMITATE = "REFERS_IMITATE";

    String REFERS_IMAGE = "REFERS_IMAGE";

    /**
     * 参考内容
     */
    String REFERS_COUNT = "REFERS_COUNT";

    /**
     * 段落数
     */
    String PARAGRAPH_COUNT = "PARAGRAPH_COUNT";

    /**
     * 生成要求
     */
    String REQUIREMENT = "REQUIREMENT";

    /**
     * 海报生成模式
     */
    String POSTER_MODE = "POSTER_MODE";

    /**
     * 海报风格
     */
    String POSTER_STYLE = "POSTER_STYLE";

    /**
     * 海报生成模式
     */
    String POSTER_STYLE_CONFIG = "POSTER_STYLE_CONFIG";

    /**
     * 海报生成模式
     */
    String SYSTEM_POSTER_STYLE_CONFIG = "SYSTEM_POSTER_STYLE_CONFIG";

    /**
     * 海报生成模式
     */
    String CUSTOM_POSTER_STYLE_CONFIG = "CUSTOM_POSTER_STYLE_CONFIG";

    /**
     * 标题
     */
    String TITLE = "TITLE";

    /**
     * 内容
     */
    String CONTENT = "CONTENT";

    /**
     * 内容
     */
    String TAG_LIST = "TAG_LIST";

    /**
     * 生成数量
     */
    String GENERATE_COUNT = "GENERATE_COUNT";

    /**
     * 资料类型
     */
    String MATERIAL_TYPE = "MATERIAL_TYPE";

    /**
     * 素材节点 素材列表
     */
    String MATERIAL_LIST = "MATERIAL_LIST";


    /**
     * 参考素材JsonSchema
     */
    String MATERIAL_JSONSCHEMA = "MATERIAL_JSONSCHEMA";

    /**
     * 步骤返回JsonSchema
     */
    String STEP_RESP_JSONSCHEMA = "STEP_RESP_JSONSCHEMA";

    /**
     * redis key
     */
    String MATERIAL_PREFIX = "material_parse_";

    String MATERIAL_PREFIX_COUNT = MATERIAL_PREFIX + "count_";

    String MATERIAL_IMPORT_ERROR = "MATERIAL_IMPORT_ERROR-";

    /**
     * 素材上传临时路径
     */
    String TMP_DIR_PATH = Paths.get(System.getProperty("java.io.tmpdir"), "material").toString();

    /**
     * word 解析地址
     */
    String WORD_PARSE = "http://cn-test.playwright.hotsalestar.com/playwright/material/parse";

    /**
     * 图片
     */
    String IMAGE = "IMAGE";

}
