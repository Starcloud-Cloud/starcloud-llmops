package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.kstry.framework.core.annotation.Invoke;
import cn.kstry.framework.core.annotation.NoticeVar;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.annotation.TaskComponent;
import cn.kstry.framework.core.annotation.TaskService;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.starcloud.ops.business.app.api.verification.Verification;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractCreativeMaterialDTO;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.domain.handler.textgeneration.OpenAIChatHandler;
import com.starcloud.ops.business.app.domain.manager.AppDefaultConfigManager;
import com.starcloud.ops.business.app.domain.parser.JsonSchemaParser;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.enums.app.AppStepResponseTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeContentGenerateModelEnum;
import com.starcloud.ops.business.app.exception.ActionResponseException;
import com.starcloud.ops.business.app.service.chat.callback.MySseCallBackHandler;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.business.app.util.CostPointUtils;
import com.starcloud.ops.business.app.verification.VerificationUtils;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import com.starcloud.ops.llm.langchain.core.utils.TokenCalculator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@SuppressWarnings("all")
@Slf4j
@TaskComponent
public class CustomActionHandler extends BaseActionHandler {

    /**
     * 字典服务
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private static AppDictionaryService appDictionaryService = SpringUtil.getBean(AppDictionaryService.class);

    /**
     * 字典服务
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private static AppDefaultConfigManager appDefaultConfigManager = SpringUtil.getBean(AppDefaultConfigManager.class);

    /**
     * 流程执行器，action 执行入口
     *
     * @param context           上下文
     * @param scopeDataOperator 作用域数据操作器
     * @return 执行结果
     */
    @NoticeVar
    @TaskService(name = "CustomActionHandler", invoke = @Invoke(timeout = 180000))
    @Override
    public ActionResponse execute(@ReqTaskParam(reqSelf = true) AppContext context, ScopeDataOperator scopeDataOperator) {
        return super.execute(context, scopeDataOperator);
    }

    /**
     * 校验步骤
     *
     * @param wrapper      步骤包装器
     * @param validateType 校验类型
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    public List<Verification> validate(WorkflowStepWrapper wrapper, ValidateTypeEnum validateType) {
        List<Verification> verifications = new ArrayList<>();
        String stepName = wrapper.getName();
        String stepCode = wrapper.getStepCode();
        // 获取到生成模式变量
        Object generateModel = wrapper.getVariable(CreativeConstants.GENERATE_MODE);
        VerificationUtils.notNullStep(verifications, generateModel, stepCode,
                "【" + stepName + "】步骤参数错误，生成模式为必选项！");
        if (Objects.isNull(generateModel)) {
            return verifications;
        }

        // 生成模式校验
        String generate = String.valueOf(generateModel);
        if (!IEnumable.contains(generate, CreativeContentGenerateModelEnum.class)) {
            VerificationUtils.notNullStep(verifications, generateModel, stepCode,
                    "【" + stepName + "】步骤参数错误，生成模式不合法！");
        }

        // 生成模式校验, 随机生成和AI模仿生成需要参考素材
        if (CreativeContentGenerateModelEnum.RANDOM.name().equals(generate) ||
                CreativeContentGenerateModelEnum.AI_PARODY.name().equals(generate)) {

            // 参考素材类型变量
            Object materialTypeValue = wrapper.getVariable(CreativeConstants.MATERIAL_TYPE);
            VerificationUtils.notNullStep(verifications, materialTypeValue, stepCode,
                    "【" + stepName + "】步骤参数错误，参考素材类型不能为空！");
            if (Objects.isNull(materialTypeValue)) {
                return verifications;
            }
            // 参考素材类型校验
            String materialType = String.valueOf(materialTypeValue);
            if (!MaterialTypeEnum.NOTE_TITLE.getCode().equals(materialType) &&
                    !MaterialTypeEnum.NOTE_CONTENT.getCode().equals(materialType)) {
                VerificationUtils.notNullStep(verifications, materialTypeValue, stepCode,
                        "【" + stepName + "】步骤参数错误，参考素材类型不合法！");
            }

            // 参考素材变量
            Object refersValue = wrapper.getVariable(CreativeConstants.REFERS);
            VerificationUtils.notNullStep(verifications, refersValue, stepCode,
                    "【" + stepName + "】步骤参数错误，参考素材不能为空！");
            if (Objects.isNull(refersValue)) {
                return verifications;
            }
            String refers = String.valueOf(refersValue);
            if (StringUtils.isBlank(refers) || "[]".equals(refers) || "null".equals(refers)) {
                VerificationUtils.addVerificationStep(verifications, stepCode,
                        "【" + stepName + "】步骤参数错误，参考素材不能为空！");
            }
        }
        // AI自定义校验，文案生成要求不能为空
        else {
            // 文案生成要求变量
            Object requirementValue = wrapper.getVariable(CreativeConstants.REQUIREMENT);
            VerificationUtils.notNullStep(verifications, requirementValue, stepCode,
                    "【" + stepName + "】步骤参数错误，文案生成要求不能为空！");
            if (Objects.isNull(requirementValue)) {
                return verifications;
            }
            String requirement = String.valueOf(requirementValue);
            VerificationUtils.notBlankStep(verifications, requirement, stepCode,
                    "【" + stepName + "】步骤参数错误，文案生成要求不能为空！");
        }
        return verifications;
    }

    /**
     * 获取用户权益类型
     *
     * @return 权益类型
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected AdminUserRightsTypeEnum getUserRightsType() {
        return AdminUserRightsTypeEnum.MAGIC_BEAN;
    }

    /**
     * 获取输出变量的 JSON Schema
     *
     * @param stepWrapper 步骤包装器
     * @return 输出变量的 JSON Schema
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    public JsonSchema getOutVariableJsonSchema(WorkflowStepWrapper stepWrapper) {
        //优先返回 素材类型的结构
//        String refers = (String) params.get(CreativeConstants.MATERIAL_TYPE);
//        if (StrUtil.isNotBlank(refers)) {
//            //获取参考素材的结构
//            return JsonSchemaUtils.generateJsonSchema(MaterialTypeEnum.of(refers).getAClass());
//        }
        return super.getOutVariableJsonSchema(stepWrapper);
    }

    /**
     * 执行内容生成
     *
     * @param context 应用上下文
     * @return 执行结果
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected ActionResponse doExecute(AppContext context) {
        Map<String, Object> params = context.getContextVariablesValues();
        // 获取到生成模式
        String generateMode = String.valueOf(params.getOrDefault(CreativeConstants.GENERATE_MODE, CreativeContentGenerateModelEnum.AI_PARODY.name()));

        // 随机模式
        if (CreativeContentGenerateModelEnum.RANDOM.name().equals(generateMode)) {
            return this.doRandomExecute(context, params);
        }

        // AI仿写模式/ AI自定义模式
        if (CreativeContentGenerateModelEnum.AI_PARODY.name().equals(generateMode)
                || CreativeContentGenerateModelEnum.AI_CUSTOM.name().equals(generateMode)) {
            return this.doAiExecute(context, params, generateMode);
        }

        // 不支持的生成模式
        throw ServiceExceptionUtil.invalidParamException("【{}】步骤执行失败: 不支持的生成模式: {}", generateMode);
    }

    /**
     * 随机获取模式
     *
     * @param params 参数
     * @return 生成结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private ActionResponse doRandomExecute(AppContext context, Map<String, Object> params) {
        // 开始日志打印
        loggerBegin(context, CreativeContentGenerateModelEnum.RANDOM.name(), "生成步骤");

        // 获取到参考文案
        List<AbstractCreativeMaterialDTO> referList = this.getReferList(context, params, CreativeContentGenerateModelEnum.RANDOM.name());
        // 随机获取一条参考文案，作为生成结果
        AbstractCreativeMaterialDTO reference = referList.get(RandomUtil.randomInt(referList.size()));

        // 计算价格相关结果
        ModelTypeEnum llmModel = ModelTypeEnum.GPT_3_5_TURBO;
        String message = CreativeContentGenerateModelEnum.RANDOM.name();
        String answer = reference.generateContent();
        long messageTokens = message.length();
        long answerTokens = answer.length();
        long totalTokens = messageTokens + answerTokens;
        BigDecimal messageUnitPrice = TokenCalculator.getUnitPrice(llmModel, true);
        BigDecimal answerUnitPrice = TokenCalculator.getUnitPrice(llmModel, false);
        BigDecimal messagePrice = TokenCalculator.getTextPrice(messageTokens, llmModel, true);
        BigDecimal answerPrice = TokenCalculator.getTextPrice(answerTokens, llmModel, false);
        BigDecimal totalPrice = messagePrice.add(answerPrice);
        int costPoints = CostPointUtils.obtainMagicBeanCostPoint(llmModel.getName(), totalTokens);

        // 返回响应结果
        ActionResponse response = new ActionResponse();
        response.setSuccess(Boolean.TRUE);
        response.setType(AppStepResponseTypeEnum.TEXT.name());
        response.setIsShow(Boolean.TRUE);
        response.setAiModel(llmModel.getName());
        response.setMessage(message);
        response.setAnswer(answer);
        response.setOutput(JsonData.of(answer));
        response.setMessageTokens(messageTokens);
        response.setMessageUnitPrice(messageUnitPrice);
        response.setAnswerTokens(answerTokens);
        response.setAnswerUnitPrice(answerUnitPrice);
        response.setTotalTokens(totalTokens);
        response.setTotalPrice(totalPrice);
        response.setCostPoints(costPoints);
        response.setStepConfig(params);
        response.setIsSendSseAll(false);

        // 结束日志打印
        loggerSuccess(context, response, CreativeContentGenerateModelEnum.RANDOM.name(), "生成步骤");

        return response;
    }

    /**
     * AI 仿写模式
     *
     * @param params 参数
     * @return 生成结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private ActionResponse doAiExecute(AppContext context, Map<String, Object> params, String generateMode) {
        // 开始日志打印
        loggerBegin(context, generateMode, "生成步骤");
        // 处理上下文
        handlerContext(context, params, generateMode);
        // 执行步骤
        ActionResponse response = this.generate(context, generateMode);
        // 结束日志打印
        loggerSuccess(context, response, generateMode, "生成步骤");
        return response;
    }

    /**
     * 处理上下文
     *
     * @param context      上下文
     * @param generateMode 生成模式
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private void handlerContext(AppContext context, Map<String, Object> params, String generateMode) {
        // 处理参考内容
        if (CreativeContentGenerateModelEnum.AI_PARODY.name().equals(generateMode)) {
            // 获取到参考内容
            List<AbstractCreativeMaterialDTO> referList = getReferList(context, params, generateMode);
            // 需要交给 ChatGPT 的参考内容数量
            Integer refersCount = Integer.valueOf(String.valueOf(params.getOrDefault(CreativeConstants.REFERS_COUNT, "3")));
            // 处理参考内容
            List<AbstractCreativeMaterialDTO> handlerReferList = handlerReferList(referList, refersCount);
            // 处理参数，并且重新放入到上下文中
            context.putVariable(CreativeConstants.REFERS, JsonUtils.toJsonPrettyString(handlerReferList));

            // 处理用户要求
            Object requirementPrompt = params.getOrDefault(CreativeConstants.PARODY_REQUIREMENT, StringUtils.EMPTY);
            context.putVariable(CreativeConstants.PARODY_REQUIREMENT, requirementPrompt);

        } else {
            // 处理用户要求
            Object requirementPrompt = params.getOrDefault(CreativeConstants.CUSTOM_REQUIREMENT, StringUtils.EMPTY);
            context.putVariable(CreativeConstants.CUSTOM_REQUIREMENT, requirementPrompt);
        }

        // 获取到系统默认配置
        Map<String, String> defaultAppConfiguration = appDefaultConfigManager.configuration();

        // 处理返回结构JSON格式化提示
        String defaultResponseJsonParserPrompt = this.defaultResponseJsonParserPrompt(context, defaultAppConfiguration);
        context.putVariable(CreativeConstants.DEFAULT_RESPONSE_JSON_PARSER_PROMPT, defaultResponseJsonParserPrompt);

        // 处理提示词
        String defaultContentStepPrompt = this.defaultContentStepPrompt(defaultAppConfiguration);
        context.putVariable(CreativeConstants.DEFAULT_CONTENT_STEP_PROMPT, defaultContentStepPrompt);
    }

    /**
     * 获取应用执行模型
     *
     * @param context
     * @return 应用执行模型
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected String getLlmModelType(AppContext context) {
        String llmModelType = super.getLlmModelType(context);
        return TokenCalculator.fromName(llmModelType).getName();
    }

    /**
     * 执行AI生成
     *
     * @param handlerRequest 请求
     * @return 结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private ActionResponse generate(AppContext context, String generateMode) {

        // 获取到参数列表
        Map<String, Object> params = context.getContextVariablesValues();
        /*
         * 约定：prompt 为总的 prompt，包含了 AI仿写 和 AI自定义 的 prompt. 中间用 ---------- 分割
         * AI仿写为第一个 prompt
         * AI自定义为第二个 prompt
         */
        boolean isCustom;
        if (CreativeContentGenerateModelEnum.AI_PARODY.name().equals(generateMode)) {
            isCustom = false;
        } else if (CreativeContentGenerateModelEnum.AI_CUSTOM.name().equals(generateMode)) {
            isCustom = true;
        } else {
            throw ServiceExceptionUtil.invalidParamException("【{}】步骤执行失败: 不支持的生成模式！", context.getStepId());
        }

        // 获取到 prompt
        String prompt = this.getPrompt(context, params, isCustom);
        // 获取到大模型 model
        String model = this.getLlmModelType(context);
        // 获取到生成数量 n
        Integer n = Optional.ofNullable(context.getN()).orElse(1);
        // 获取到 maxTokens
        Integer maxTokens = Integer.valueOf(String.valueOf(params.getOrDefault(AppConstants.MAX_TOKENS, "4000")));
        // 获取到 temperature
        Double temperature = Double.valueOf(String.valueOf(params.getOrDefault(AppConstants.TEMPERATURE, "0.7")));

        // 打印参数日志
        loggerParamter(context, params, generateMode, "生成步骤");

        // 构建请求
        OpenAIChatHandler.Request handlerRequest = new OpenAIChatHandler.Request();
        handlerRequest.setStream(Objects.nonNull(context.getSseEmitter()));
        handlerRequest.setModel(model);
        handlerRequest.setN(n);
        handlerRequest.setPrompt(prompt);
        handlerRequest.setMaxTokens(maxTokens);
        handlerRequest.setTemperature(temperature);
        // 构建请求上下文
        HandlerContext<OpenAIChatHandler.Request> handlerContext = HandlerContext.createContext(
                context.getUid(),
                context.getConversationUid(),
                context.getUserId(),
                context.getEndUserId(),
                context.getScene(),
                handlerRequest
        );
        // 构建OpenAI处理器
        StreamingSseCallBackHandler callBackHandler = new MySseCallBackHandler(context.getSseEmitter());
        OpenAIChatHandler handler = new OpenAIChatHandler(callBackHandler);
        // 执行OpenAI处理器
        HandlerResponse<String> handlerResponse = handler.execute(handlerContext);
        // 转换并且返回响应结果
        return convert(context, handlerResponse);
    }

    /**
     * 转换响应结果
     *
     * @param handlerResponse 响应结果
     * @return 转换后的响应结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private ActionResponse convert(AppContext context, HandlerResponse handlerResponse) {
        // 计算权益点数
        Long tokens = handlerResponse.getMessageTokens() + handlerResponse.getAnswerTokens();
        String llmModelType = this.getLlmModelType(context);
        Integer costPoints = CostPointUtils.obtainMagicBeanCostPoint(llmModelType, tokens);

        // 构建响应结果
        ActionResponse response = new ActionResponse();
        // 调用节点 Handler，失败都是直接抛出异常，所以这里只要能获取到结果，都是执行成功的，失败的都会抛出异常。
        response.setSuccess(true);
        response.setErrorCode(String.valueOf(handlerResponse.getErrorCode()));
        response.setErrorMsg(handlerResponse.getErrorMsg());
        response.setType(handlerResponse.getType());
        response.setIsShow(true);
        response.setMessage(handlerResponse.getMessage());
        response.setAnswer(handlerResponse.getAnswer());
        response.setMessageTokens(handlerResponse.getMessageTokens());
        response.setMessageUnitPrice(handlerResponse.getMessageUnitPrice());
        response.setAnswerTokens(handlerResponse.getAnswerTokens());
        response.setAnswerUnitPrice(handlerResponse.getAnswerUnitPrice());
        response.setTotalTokens(handlerResponse.getTotalTokens());
        response.setTotalPrice(handlerResponse.getTotalPrice());
        response.setStepConfig(handlerResponse.getStepConfig());
        response.setAiModel(llmModelType);
        response.setCostPoints(handlerResponse.getSuccess() ? costPoints : 0);
        // 本身输出已经走 Sse了，不需要在发送一次完整的结果
        response.setIsSendSseAll(false);
        response.setOutput(this.parseOutput(context, response));
        return response;
    }

    /**
     * 解析输出内容
     *
     * @param context         上下文
     * @param handlerResponse 处理结果
     * @return 解析后的结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private JsonData parseOutput(AppContext context, ActionResponse response) {
        try {
            // 如果配置了 JsonSchema
            if (this.hasResponseJsonSchema(context)) {
                //获取当前定义的返回结构
                JsonSchema jsonSchema = this.getOutVariableJsonSchema(context);
                JsonSchemaParser jsonSchemaParser = new JsonSchemaParser(jsonSchema);
                JSON json = jsonSchemaParser.parse(response.getAnswer());
                return JsonData.of(json, jsonSchema);
            } else {
                //如果还是字符串结构，就自动包一层 data 结构 @todo 需要保证prompt不要格式化结果
                return JsonData.of(response.getAnswer());
            }
        } catch (Exception exception) {
            throw ActionResponseException.exception(response, exception);
        }
    }

    /**
     * 返回用户要求
     *
     * @param context 上下文
     * @return 用户要求
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private Object requirementPrompt(AppContext context, Map<String, Object> params) {
        return params.getOrDefault(CreativeConstants.REQUIREMENT, StringUtils.EMPTY);
    }

    /**
     * 获取 JSON 格式化提示
     *
     * @param context
     * @return
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private String defaultResponseJsonParserPrompt(AppContext context, Map<String, String> defaultAppConfiguration) {
        // 如果返回结果需要解析 JSON，返回提示
        if (this.hasResponseJsonSchema(context)) {
            return appDefaultConfigManager.defaultResponseJsonParserPrompt(defaultAppConfiguration);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 系统默认配置prompt
     *
     * @return prompt
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private String defaultContentStepPrompt(Map<String, String> defaultAppConfiguration) {
        return appDefaultConfigManager.defaultContentStepPrompt(defaultAppConfiguration);
    }

    /**
     * 获取 prompt
     *
     * @param params   参数
     * @param isCustom 是否是自定义
     * @return prompt
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private String getPrompt(AppContext context, Map<String, Object> params, boolean isCustom) {
        // 获取到 prompt
        String prompt = String.valueOf(params.getOrDefault(AppConstants.PROMPT, StrUtil.EMPTY));
        List<String> promptList = StrUtil.split(prompt, "----------");
        try {
            if (!isCustom) {
                prompt = promptList.get(0);
            } else {
                prompt = promptList.get(1);
            }
            // 判断 prompt 是否为空，如果为空，抛出异常，走catch逻辑获取默认配置
            if (StrUtil.isBlank(prompt)) {
                throw new RuntimeException("用户 prompt 为空！");
            }
        } catch (Exception e) {
            try {
                log.error("用户prompt配置异常！从字典中获取默认配置！");

                String defaultPrompt = MapUtil.emptyIfNull(appDictionaryService.defaultAppConfiguration())
                        .getOrDefault(CreativeConstants.DEFAULT_CONTENT_STEP_PROMPT, StrUtil.EMPTY);
                List<String> defaultPromptList = StrUtil.split(defaultPrompt, "----------");
                if (defaultPromptList.size() < 2) {
                    throw new RuntimeException("系统默认promp配置异常！检查您的配置或者联系管理员！");
                }

                if (!isCustom) {
                    prompt = defaultPromptList.get(0);
                } else {
                    prompt = defaultPromptList.get(1);
                }

                if (StrUtil.isBlank(prompt)) {
                    throw new RuntimeException("系统默认promp为空！");
                }

                // 放入到上下文中
                context.putModelVariable(AppConstants.PROMPT, prompt);
                // 重新获取替换后的 prompt
                prompt = String.valueOf(context.getContextVariablesValues().getOrDefault(AppConstants.PROMPT, StrUtil.EMPTY));
                // 如果还是为空，抛出异常
                if (StrUtil.isBlank(prompt)) {
                    throw new RuntimeException("系统默认promp为空！");
                }
            } catch (Exception exception) {
                log.error("【{}】步骤执行失败: prompt 配置异常，{}", context.getStepId(), exception.getMessage());
                throw ServiceExceptionUtil.invalidParamException("【{}】步骤执行失败: prompt 配置异常，请联系管理员或稍后重试！", context.getStepId());
            }
        }

        return prompt.trim();
    }

    /**
     * 获取参考内容
     *
     * @param params       参数
     * @param stepId       步骤ID
     * @param generateMode 生成模式
     * @return
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private List<AbstractCreativeMaterialDTO> getReferList(AppContext context, Map<String, Object> params, String generateMode) {
        // 获取到参考内容
        String refers = String.valueOf(params.getOrDefault(CreativeConstants.REFERS, "[]"));
        List<AbstractCreativeMaterialDTO> referList = JsonUtils.parseArray(refers, AbstractCreativeMaterialDTO.class);
        if (CollectionUtil.isEmpty(referList)) {
            throw ServiceExceptionUtil.invalidParamException("【{}】步骤执行失败: 生成模式为【{}】时，参考内容不能为空！", context.getStepId(), generateMode);
        }
        return referList;
    }

    /**
     * 处理参考内容
     *
     * @param referList 参考内容
     * @return 处理后的参考内容
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private String generateRefers(List<AbstractCreativeMaterialDTO> referList) {
        try {
            StringJoiner sj = new StringJoiner("\n");
            for (AbstractCreativeMaterialDTO materialDTO : referList) {
                sj.add(JsonUtils.toJsonString(materialDTO));
                JSONObject entries = JSONUtil.parseObj(materialDTO);
                JSONArray imitateTypeJSON = entries.getJSONArray("imitateType");
                if (Objects.isNull(imitateTypeJSON)) {
                    continue;
                }
                List<String> imitateType = imitateTypeJSON.toList(String.class);
                sj.add("模仿要求：模仿这条笔记的" + imitateType.stream().collect(Collectors.joining(",")));
            }
            return sj.toString();
        } catch (Exception e) {
            log.warn("generate Refers error", e);
            return JsonUtils.toJsonString(referList);
        }
    }

    /**
     * 处理参考内容
     *
     * @param referList   参考内容
     * @param refersCount 参考内容数量
     * @return 处理后的参考内容
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private List<AbstractCreativeMaterialDTO> handlerReferList(List<AbstractCreativeMaterialDTO> refersList, Integer refersCount) {
        // 随机选取
        Collections.shuffle(refersList);
        List<AbstractCreativeMaterialDTO> result = refersList.stream()
                .peek(AbstractCreativeMaterialDTO::clean)
                .limit(refersCount).collect(Collectors.toList());
        int i = 0;
        if ((i = refersCount - result.size()) == 0) {
            return result;
        }
        //  补齐元素
        for (int j = 0; j < i; j++) {
            result.add(refersList.get(RandomUtil.randomInt(refersList.size())));
        }
        return result;
    }

    /**
     * 记录开始日志
     *
     * @param context 上下文
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private void loggerBegin(AppContext context, String model, String title) {
        log.info("\n{}【开始执行】: " +
                        "\n\t执行步骤: {}, " +
                        "\n\t生成模式: {}," +
                        "\n\t步骤执行器: {}, " +
                        "\n\t应用UID: {}, " +
                        "\n\t会话UID: {}, " +
                        "\n\t权益用户: {}, " +
                        "\n\t权益租户: {}, ",
                title,
                context.getStepId(),
                model,
                this.getClass().getSimpleName(),
                context.getUid(),
                context.getConversationUid(),
                context.getUserId(),
                TenantContextHolder.getTenantId()
        );
    }

    /**
     * 记录参数日志
     *
     * @param context 上下文
     * @param param   参数
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private void loggerParamter(AppContext context, Object param, String model, String title) {
        log.info("\n{}【准备调用模型】: " +
                        "\n\t执行步骤: {}, " +
                        "\n\t生成模式: {}," +
                        "\n\t步骤执行器: {}, " +
                        "\n\t应用UID: {}, " +
                        "\n\t会话UID: {}, " +
                        "\n\t权益用户: {}, " +
                        "\n\t权益租户: {}, " +
                        "\n\t请求参数: {}",
                title,
                context.getStepId(),
                model,
                this.getClass().getSimpleName(),
                context.getUid(),
                context.getConversationUid(),
                context.getUserId(),
                TenantContextHolder.getTenantId(),
                JsonUtils.toJsonString(param)
        );
    }

    /**
     * 记录结束日志
     *
     * @param context  上下文
     * @param response 执行结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private void loggerSuccess(AppContext context, ActionResponse response, String model, String title) {
        log.info("\n{}【执行成功】: " +
                        "\n\t执行步骤: {}, " +
                        "\n\t生成模式: {}," +
                        "\n\t步骤执行器: {}, " +
                        "\n\t应用UID: {}, " +
                        "\n\t会话UID: {}, " +
                        "\n\t权益用户: {}, " +
                        "\n\t权益租户: {}, " +
                        "\n\t执行结果: {}",
                title,
                context.getStepId(),
                model,
                this.getClass().getSimpleName(),
                context.getUid(),
                context.getConversationUid(),
                context.getUserId(),
                TenantContextHolder.getTenantId(),
                JsonUtils.toJsonString(response.getOutput())
        );
    }

}
