package com.starcloud.ops.business.app.domain.manager;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import com.starcloud.ops.business.app.domain.entity.chat.ModelProviderEnum;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.ChatErrorCodeConstants;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import com.starcloud.ops.llm.langchain.core.utils.TokenCalculator;
import dm.jdbc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

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

    @Resource
    private PermissionApi permissionApi;

    /**
     * 获取默认应用配置
     *
     * @return 默认应用配置
     */
    public Map<String, String> configuration() {
        return MapUtils.emptyIfNull(appDictionaryService.defaultAppConfiguration());
    }

    /**
     * 获取应用大模型
     *
     * @return 默认模型类型
     */
    public ModelTypeEnum getLlmModelType(String model, Long userId, Boolean isPermission) {
        Map<String, String> map = this.defaultLlmModelTypeMap();
        return getLlmModelType(model, userId, isPermission, map);
    }

    /**
     * 获取应用大模型
     *
     * @return 默认模型类型
     */
    public ModelTypeEnum getLlmModelType(String model, Long userId, Boolean isPermission, Map<String, String> map) {

        ModelProviderEnum modelProvider = ModelProviderEnum.fromName(model);
        // 是否走权限
        if (Objects.nonNull(isPermission) && isPermission) {
            // 权限相关
            if (StringUtils.isNotBlank(modelProvider.getPermissions())) {
                String permissions = modelProvider.getPermissions();
                if (!permissionApi.hasAnyPermissions(userId, permissions)) {
                    //没权限抛异常
                    throw ServiceExceptionUtil.exception(ChatErrorCodeConstants.CONFIG_MODEL_ERROR, model);
                }
            }
        }
        String defaultModel = map.getOrDefault("DEFAULT", ModelTypeEnum.GPT_3_5_TURBO.getName());
        String llmModelType = map.getOrDefault(modelProvider.name(), defaultModel);
        return TokenCalculator.fromName(llmModelType);
    }

    /**
     * 获取默认模型类型映射关系
     *
     * @return 默认模型类型
     */
    public Map<String, String> defaultLlmModelTypeMap() {
        return defaultLlmModelTypeMap(configuration());
    }

    /**
     * 获取默认模型类型映射关系
     *
     * @param configuration 配置
     * @return 默认模型类型
     */
    public Map<String, String> defaultLlmModelTypeMap(Map<String, String> configuration) {
        String modelMap = MapUtils.emptyIfNull(configuration)
                .get(AppConstants.DEFAULT_LLM_MODEL_TYPE_MAP);

        return JsonUtils.parseMap(modelMap, String.class, String.class);
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
