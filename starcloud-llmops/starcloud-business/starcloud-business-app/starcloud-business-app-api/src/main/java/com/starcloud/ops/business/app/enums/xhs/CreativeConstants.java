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
     * 字段配置prompt
     */
    String DEFAULT_CONTENT_STEP_PROMPT = "DEFAULT_CONTENT_STEP_PROMPT";

    /**
     * JSON解析PROMPT
     */
    String DEFAULT_RESPONSE_JSON_PARSER_PROMPT = "DEFAULT_RESPONSE_JSON_PARSER_PROMPT";

    /**
     * 小红书链接
     */
    String XHS_NOTE_URL = "XHS_NOTE_URL";

    /**
     * 字段映射
     */
    String FIELD_MAP = "FIELD_MAP";

    /**
     * 图片链接集合
     */
    String IMAGE_OCR_URL = "IMAGE_OCR_URL";

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
     * 资料类型 参考素材中定义素材类型
     */
    String MATERIAL_TYPE = "MATERIAL_TYPE";

    /**
     * 业务类型 上传素材节点 选择AbstractMaterialHandler
     */
    String BUSINESS_TYPE = "BUSINESS_TYPE";

    String PICTURE = "picture";

    /**
     * 图片搜索习惯
     */
    String SEARCH_HABITS = "SEARCH_HABITS";

    /**
     * 素材字段定义
     */
    String MATERIAL_DEFINE = "MATERIAL_DEFINE";

    /**
     * 素材节点 素材列表
     */
    String MATERIAL_LIST = "MATERIAL_LIST";

    /**
     * 素材节点 素材列表
     */
    String MATERIAL_USAGE_MODEL = "MATERIAL_USAGE_MODEL";

    /**
     * 素材节点 素材生成配置
     */
    String MATERIAL_GENERATE_CONFIG = "MATERIAL_GENERATE_CONFIG";

    /**
     * 素材节点 素材生成配置
     */
    String CUSTOM_MATERIAL_GENERATE_CONFIG = "CUSTOM_MATERIAL_GENERATE_CONFIG";

    /**
     * 素材库配置
     */
    String LIBRARY_QUERY = "LIBRARY_QUERY";

    /**
     * 选择执行条件下素材库查询条件
     */
    String SELECT_MATERIAL_QUERY = "SELECT_MATERIAL_QUERY";

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
    String MATERIAL_TMP_DIR_PATH = Paths.get(System.getProperty("java.io.tmpdir"), "material").toString();

    /**
     * word 解析地址
     */
    String WORD_PARSE = "http://cn-test.playwright.hotsalestar.com/playwright/material/parse";

    /**
     * 图片
     */
    String IMAGE = "IMAGE";

}
