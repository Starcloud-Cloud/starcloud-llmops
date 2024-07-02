package com.starcloud.ops.business.app.domain.manager;

import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import dm.jdbc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 应用默认配置管理器
 *
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Component
@Slf4j
public class AppDefaultConfigManager {

    /**
     * 内容生成步骤默认Prompt
     */
    private static final String RESPONSE_JSON_PARSER_PROMPT = "Your response should be in JSON format.\n" +
            "Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation.\n" +
            "Do not include markdown code blocks in your response.\n" +
            "Here is the JSON Schema instance your output must adhere to:\n" +
            "```\n {{STEP_RESP_JSONSCHEMA}} \n```";

    /**
     * 应用字典服务
     */
    @Resource
    private AppDictionaryService appDictionaryService;

    /**
     * 获取默认应用配置
     *
     * @return 默认应用配置
     */
    public Map<String, String> configuration() {
        return MapUtils.emptyIfNull(appDictionaryService.defaultAppConfiguration());
    }

    /**
     * 获取默认模型类型
     *
     * @param modelType 模型类型
     * @param modelEnum 模型枚举
     * @return 默认模型类型
     */
    public String defaultLlmModelType(String modelType, AppModelEnum modelEnum, AppTypeEnum appTypeEnum) {
        return defaultLlmModelType(modelType, modelEnum, appTypeEnum, configuration());
    }

    /**
     * 获取默认模型类型
     *
     * @param modelType     模型类型
     * @param modelEnum     模型枚举
     * @param configuration 配置
     * @return 默认模型类型
     */
    public String defaultLlmModelType(String modelType, AppModelEnum modelEnum, AppTypeEnum appTypeEnum, Map<String, String> configuration) {
        // 默认模型类型
        String defaultModelType = MapUtils.emptyIfNull(configuration)
                .getOrDefault(AppConstants.DEFAULT_MODEL_TYPE, modelType);

        // 如果是媒体矩阵类型的应用。
        if (AppTypeEnum.MEDIA_MATRIX.equals(appTypeEnum)) {
            return MapUtils.emptyIfNull(configuration)
                    .getOrDefault(AppConstants.DEFAULT_MEDIA_MATRIX_MODEL_TYPE, defaultModelType);
        }

        // 生成模式
        if (AppModelEnum.COMPLETION.equals(modelEnum)) {
            return MapUtils.emptyIfNull(configuration)
                    .getOrDefault(AppConstants.DEFAULT_COMPLETION_MODEL_TYPE, defaultModelType);
        }
        // 聊天模式
        else if (AppModelEnum.CHAT.equals(modelEnum)) {
            return MapUtils.emptyIfNull(configuration)
                    .getOrDefault(AppConstants.DEFAULT_CHAT_MODEL_TYPE, defaultModelType);
        }
        // 其他情况
        else {
            return defaultModelType;
        }
    }

    /**
     * 获取内容生成步骤默认Prompt
     *
     * @return 内容生成步骤默认Prompt
     */
    public String defaultContentStepPrompt() {
        return defaultContentStepPrompt(configuration());
    }

    /**
     * 获取内容生成步骤默认Prompt
     *
     * @return 内容生成步骤默认Prompt
     */
    public String defaultContentStepPrompt(Map<String, String> configuration) {
        return MapUtils.emptyIfNull(configuration)
                .getOrDefault(CreativeConstants.DEFAULT_CONTENT_STEP_PROMPT, StringUtil.EMPTY);
    }

    /**
     * 获取JSON解析默认Prompt
     *
     * @return JSON解析默认Prompt
     */
    public String defaultResponseJsonParserPrompt() {
        return defaultResponseJsonParserPrompt(configuration());
    }

    /**
     * 获取JSON解析默认Prompt
     *
     * @return JSON解析默认Prompt
     */
    public String defaultResponseJsonParserPrompt(Map<String, String> configuration) {
        return MapUtils.emptyIfNull(configuration)
                .getOrDefault(CreativeConstants.DEFAULT_RESPONSE_JSON_PARSER_PROMPT, RESPONSE_JSON_PARSER_PROMPT);
    }

}
