package com.starcloud.ops.business.app.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.google.common.collect.Lists;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.execute.XhsAppCreativeExecuteRequest;
import com.starcloud.ops.business.app.api.xhs.execute.XhsAppCreativeExecuteResponse;
import com.starcloud.ops.business.app.api.xhs.execute.XhsAppExecuteRequest;
import com.starcloud.ops.business.app.api.xhs.execute.XhsAppExecuteResponse;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanAppExecuteDTO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanConfigDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CopyWritingContentDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeImageTemplateDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeSchemeConfigDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeSchemeCopyWritingTemplateDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeSchemeReferenceDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.ParagraphDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.CreativeSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.CustomCreativeSchemeConfigDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemeReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.CreativeSchemeRespVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.scheme.vo.CreativeSchemeSseReqVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableGroupEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableStyleEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeModeEnum;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Slf4j
@SuppressWarnings("unused")
public class CreativeAppUtils {

    private static final String NAME = "NAME";
    private static final String TYPE = "TYPE";
    private static final String CATEGORY = "CATEGORY";
    private static final String TAGS = "TAGS";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String REFERS = "REFERS";
    private static final String IS_PROMOTE_MP = "IS_PROMOTE_MP";
    private static final String MP_CODE = "MP_CODE";
    private static final String SUMMARY = "SUMMARY";
    private static final String DEMAND = "DEMAND";
    private static final String EXAMPLE = "EXAMPLE";
    public static final String PARAGRAPH_COUNT = "PARAGRAPH_COUNT";
    public static final String PARAGRAPH_DEMAND = "PARAGRAPH_DEMAND";

    /**
     * 获取应用的第一步步骤配置
     *
     * @param appResponse 应用市场应用
     * @return 第一步步骤配置
     */
    public static WorkflowStepWrapperRespVO firstStep(AppMarketRespVO appResponse) {
        List<WorkflowStepWrapperRespVO> stepWrapperList = Optional.ofNullable(appResponse).map(AppMarketRespVO::getWorkflowConfig).map(WorkflowConfigRespVO::getSteps)
                .orElseThrow(() -> ServiceExceptionUtil.exception(ErrorCodeConstants.WORKFLOW_CONFIG_FAILURE));
        WorkflowStepWrapperRespVO stepWrapper = stepWrapperList.get(0);
        AppValidate.notNull(stepWrapper, ErrorCodeConstants.WORKFLOW_STEP_NOT_EXIST, appResponse.getName());
        return stepWrapper;
    }

    /**
     * 获取应用的第二步步骤配置
     *
     * @param appResponse 应用市场应用
     * @return 第二步步骤配置
     */
    public static WorkflowStepWrapperRespVO secondStep(AppMarketRespVO appResponse) {
        List<WorkflowStepWrapperRespVO> stepWrapperList = Optional.ofNullable(appResponse).map(AppMarketRespVO::getWorkflowConfig).map(WorkflowConfigRespVO::getSteps)
                .orElseThrow(() -> ServiceExceptionUtil.exception(ErrorCodeConstants.WORKFLOW_CONFIG_FAILURE));
        WorkflowStepWrapperRespVO stepWrapper = stepWrapperList.get(1);
        AppValidate.notNull(stepWrapper, ErrorCodeConstants.WORKFLOW_STEP_NOT_EXIST, appResponse.getName());
        return stepWrapper;
    }

    /**
     * 获取小红书应用执行参数
     *
     * @param scheme     创作任务
     * @param planConfig 计划配置
     * @param appUid     应用UID
     * @return 应用执行参数
     */
    public static CreativePlanAppExecuteDTO getXhsAppExecuteRequest(CreativeSchemeRespVO scheme, CreativePlanConfigDTO planConfig, String appUid) {

        CreativeSchemeConfigDTO configuration = scheme.getConfiguration();
        CreativeSchemeCopyWritingTemplateDTO copyWritingTemplate = configuration.getCopyWritingTemplate();

        Map<String, List<VariableItemRespVO>> paramMap = planConfig.getParamMap();
        List<VariableItemRespVO> variableList = Optional.ofNullable(paramMap.get(scheme.getUid())).orElse(Lists.newArrayList());

        List<VariableItemRespVO> params = Lists.newArrayList();
        params.add(ofInputVariableItem(NAME, scheme.getName()));
        params.add(ofInputVariableItem(TYPE, scheme.getType()));
        params.add(ofInputVariableItem(CATEGORY, scheme.getCategory()));
        params.add(ofInputVariableItem(TAGS, String.join(",", CollectionUtil.emptyIfNull(scheme.getTags()))));
        params.add(ofTextAreaVariableItem(DESCRIPTION, scheme.getDescription()));
        params.add(ofTextAreaVariableItem(REFERS, JSONUtil.toJsonStr(CollectionUtil.emptyIfNull(scheme.getRefers()))));
        params.add(ofTextAreaVariableItem(SUMMARY, copyWritingTemplate.getSummary()));
        params.add(ofTextAreaVariableItem(DEMAND, CreativeAppUtils.handlerDemand(copyWritingTemplate, variableList)));
        if (CreativeSchemeModeEnum.PRACTICAL_IMAGE_TEXT.name().equals(scheme.getMode())) {
            params.add(ofInputVariableItem(PARAGRAPH_COUNT, configuration.getParagraphCount()));
            params.add(ofInputVariableItem(PARAGRAPH_DEMAND, StrUtil.EMPTY));
        }

        CreativePlanAppExecuteDTO appExecute = new CreativePlanAppExecuteDTO();
        appExecute.setUid(appUid);
        appExecute.setParams(params);
        appExecute.setScene(AppSceneEnum.XHS_WRITING.name());
        return appExecute;
    }

    /**
     * 构建执行请求
     *
     * @param app     应用
     * @param request 请求
     * @return 执行请求
     */
    public static AppExecuteReqVO buildExecuteRequest(AppMarketRespVO app, XhsAppExecuteRequest request) {
        AppExecuteReqVO executeRequest = new AppExecuteReqVO();
        executeRequest.setStepId(request.getStepId());
        executeRequest.setUserId(request.getUserId());
        executeRequest.setMode(AppModelEnum.COMPLETION.name());
        executeRequest.setScene(StringUtils.isBlank(request.getScene()) ? AppSceneEnum.XHS_WRITING.name() : request.getScene());
        executeRequest.setAppUid(app.getUid());
        executeRequest.setN(request.getN());
        executeRequest.setAppReqVO(transform(app, request.getParams(), request.getStepId()));
        return executeRequest;
    }

    /**
     * 处理参考内容
     *
     * @param referenceList 处理参考内容
     * @return 参考内容
     */
    public static List<CreativeSchemeReferenceDTO> handlerReferences(List<CreativeSchemeReferenceDTO> referenceList) {
        return CollectionUtil.emptyIfNull(referenceList).stream().map(item -> {
            CreativeSchemeReferenceDTO reference = new CreativeSchemeReferenceDTO();
            reference.setTitle(item.getTitle());
            reference.setContent(item.getContent());
            reference.setImages(null);
            reference.setLink(null);
            reference.setSource(null);
            reference.setId(null);
            return reference;
        }).collect(Collectors.toList());
    }

    /**
     * 处理需求文本，变量填充等
     *
     * @param copyWritingTemplate 文案生成模板
     * @param variableList        创作计划配置变量
     * @return 处理后的文案
     */
    public static String handlerDemand(CreativeSchemeCopyWritingTemplateDTO copyWritingTemplate, List<VariableItemRespVO> variableList) {
        String demand = copyWritingTemplate.getDemand();
        if (CollectionUtil.isEmpty(variableList)) {
            variableList = Optional.ofNullable(copyWritingTemplate.getVariables()).orElse(Lists.newArrayList());
        }
        Map<String, VariableItemRespVO> variableMap = CollectionUtil.emptyIfNull(variableList).stream().collect(Collectors.toMap(VariableItemRespVO::getField, item -> item));
        for (VariableItemRespVO variableItem : CollectionUtil.emptyIfNull(copyWritingTemplate.getVariables())) {
            String field = variableItem.getField();
            VariableItemRespVO variable = variableMap.getOrDefault(field, variableItem);
            Object value = variable.getValue();
            if (Objects.isNull(value)) {
                value = Optional.ofNullable(variable.getDefaultValue()).orElse("");
            }
            demand = demand.replace("{" + field + "}", String.valueOf(value));
        }
        return demand;
    }

    /**
     * 将数据转为应用，参数替换
     *
     * @param app     应用市场应用
     * @param request 创作方案请求
     * @return 应用市场应用
     */
    public static AppReqVO transform(AppMarketRespVO app, CreativeSchemeReqVO request, String stepId) {
        // 参数信息
        CreativeSchemeConfigDTO configuration = request.getConfiguration();
        CreativeSchemeCopyWritingTemplateDTO copyWritingTemplate = configuration.getCopyWritingTemplate();

        // 获取步骤配置信息
        WorkflowConfigRespVO config = app.getWorkflowConfig();
        AppValidate.notNull(config, ErrorCodeConstants.WORKFLOW_CONFIG_FAILURE);

        List<WorkflowStepWrapperRespVO> stepWrapperList = config.getSteps();
        AppValidate.notEmpty(stepWrapperList, ErrorCodeConstants.WORKFLOW_CONFIG_FAILURE);

        for (WorkflowStepWrapperRespVO stepWrapper : stepWrapperList) {
            if (Objects.isNull(stepId) || !stepId.equalsIgnoreCase(stepWrapper.getField())) {
                continue;
            }
            VariableRespVO variable = stepWrapper.getVariable();
            List<VariableItemRespVO> variableList = variable.getVariables();
            if (CollectionUtil.isEmpty(variableList)) {
                continue;
            }
            for (VariableItemRespVO variableItem : variableList) {
                if (NAME.equals(variableItem.getField()) && StringUtils.isNotBlank(request.getName())) {
                    String name = request.getName();
                    variableItem.setValue(name);
                    variableItem.setDefaultValue(name);

                } else if (TYPE.equals(variableItem.getField()) && StringUtils.isNotBlank(request.getType())) {
                    String type = request.getType();
                    variableItem.setValue(type);
                    variableItem.setDefaultValue(type);

                } else if (CATEGORY.equals(variableItem.getField()) && StringUtils.isNotBlank(request.getCategory())) {
                    String category = request.getCategory();
                    variableItem.setValue(category);
                    variableItem.setDefaultValue(category);

                } else if (TAGS.equals(variableItem.getField()) && CollectionUtil.isNotEmpty(request.getTags())) {
                    String tags = String.join(",", request.getTags());
                    variableItem.setValue(tags);
                    variableItem.setDefaultValue(tags);

                } else if (DESCRIPTION.equals(variableItem.getField()) && StringUtils.isNotBlank(request.getDescription())) {
                    String description = request.getDescription();
                    variableItem.setValue(description);
                    variableItem.setDefaultValue(description);

                } else if (REFERS.equals(variableItem.getField()) && CollectionUtil.isNotEmpty(request.getRefers())) {
                    String refers = JSONUtil.toJsonStr(handlerReferences(request.getRefers()));
                    variableItem.setValue(refers);
                    variableItem.setDefaultValue(refers);
                } else if (SUMMARY.equals(variableItem.getField()) && StringUtils.isNotBlank(copyWritingTemplate.getSummary())) {
                    String summary = copyWritingTemplate.getSummary();
                    variableItem.setValue(summary);
                    variableItem.setDefaultValue(summary);

                } else if (DEMAND.equals(variableItem.getField()) && StringUtils.isNotBlank(copyWritingTemplate.getDemand())) {
                    String demand = handlerDemand(copyWritingTemplate, null);
                    variableItem.setValue(demand);
                    variableItem.setDefaultValue(demand);
                }
            }
            variable.setVariables(variableList);
            stepWrapper.setVariable(variable);
        }

        config.setSteps(stepWrapperList);
        app.setWorkflowConfig(config);
        return AppMarketConvert.INSTANCE.convert(app);
    }

    /**
     * 将数据转为应用，参数替换
     *
     * @param app     应用市场应用
     * @param request 创作方案请求
     * @return 应用市场应用
     */
    public static AppReqVO transform(AppMarketRespVO app, CreativeSchemeSseReqVO request, String stepId) {

        // 获取步骤配置信息
        WorkflowConfigRespVO config = app.getWorkflowConfig();
        AppValidate.notNull(config, ErrorCodeConstants.WORKFLOW_CONFIG_FAILURE);

        List<WorkflowStepWrapperRespVO> stepWrapperList = config.getSteps();
        AppValidate.notEmpty(stepWrapperList, ErrorCodeConstants.WORKFLOW_CONFIG_FAILURE);

        for (WorkflowStepWrapperRespVO stepWrapper : stepWrapperList) {
            if (Objects.isNull(stepId) || !stepId.equalsIgnoreCase(stepWrapper.getField())) {
                continue;
            }
            VariableRespVO variable = stepWrapper.getVariable();
            List<VariableItemRespVO> variableList = variable.getVariables();
            if (CollectionUtil.isEmpty(variableList)) {
                continue;
            }
            for (VariableItemRespVO variableItem : variableList) {
                if (NAME.equals(variableItem.getField()) && StringUtils.isNotBlank(request.getName())) {
                    String name = request.getName();
                    variableItem.setValue(name);
                    variableItem.setDefaultValue(name);

                } else if (TYPE.equals(variableItem.getField()) && StringUtils.isNotBlank(request.getType())) {
                    String type = request.getType();
                    variableItem.setValue(type);
                    variableItem.setDefaultValue(type);

                } else if (CATEGORY.equals(variableItem.getField()) && StringUtils.isNotBlank(request.getCategory())) {
                    String category = request.getCategory();
                    variableItem.setValue(category);
                    variableItem.setDefaultValue(category);

                } else if (TAGS.equals(variableItem.getField()) && CollectionUtil.isNotEmpty(request.getTags())) {
                    String tags = String.join(",", request.getTags());
                    variableItem.setValue(tags);
                    variableItem.setDefaultValue(tags);

                } else if (DESCRIPTION.equals(variableItem.getField()) && StringUtils.isNotBlank(request.getDescription())) {
                    String description = request.getDescription();
                    variableItem.setValue(description);
                    variableItem.setDefaultValue(description);

                } else if (REFERS.equals(variableItem.getField()) && CollectionUtil.isNotEmpty(request.getRefers())) {
                    String refers = JSONUtil.toJsonStr(handlerReferences(request.getRefers()));
                    variableItem.setValue(refers);
                    variableItem.setDefaultValue(refers);
                }
            }
            variable.setVariables(variableList);
            stepWrapper.setVariable(variable);
        }

        config.setSteps(stepWrapperList);
        app.setWorkflowConfig(config);
        return AppMarketConvert.INSTANCE.convert(app);
    }

    /**
     * 转换请求，转换为应用请求，并且填充参数
     *
     * @param app       应用
     * @param appParams 请求
     * @return 应用请求
     */
    public static AppReqVO transform(AppMarketRespVO app, Map<String, Object> appParams, String stepId) {
        AppMarketRespVO appMarket = SerializationUtils.clone(app);
        // 获取步骤配置信息
        WorkflowConfigRespVO config = appMarket.getWorkflowConfig();
        AppValidate.notNull(config, ErrorCodeConstants.WORKFLOW_CONFIG_FAILURE);

        List<WorkflowStepWrapperRespVO> stepWrapperList = config.getSteps();
        AppValidate.notEmpty(stepWrapperList, ErrorCodeConstants.WORKFLOW_CONFIG_FAILURE);

        for (WorkflowStepWrapperRespVO stepWrapper : stepWrapperList) {
            // 如果指定了步骤ID，则只替换指定步骤的参数
            if (Objects.isNull(stepId) || !stepId.equalsIgnoreCase(stepWrapper.getField())) {
                continue;
            }
            VariableRespVO variable = stepWrapper.getVariable();
            List<VariableItemRespVO> variableList = variable.getVariables();

            if (CollectionUtil.isEmpty(variableList)) {
                continue;
            }

            for (VariableItemRespVO variableItem : variableList) {

                if (REFERS.equals(variableItem.getField())) {
                    Object value = appParams.get(variableItem.getField());
                    if (value instanceof String && StringUtils.isNoneBlank((String) value)) {
                        TypeReference<List<CreativeSchemeReferenceDTO>> typeReference = new TypeReference<List<CreativeSchemeReferenceDTO>>() {
                        };
                        List<CreativeSchemeReferenceDTO> refers = JSONUtil.toBean((String) value, typeReference, false);
                        String refs = JSONUtil.toJsonStr(handlerReferences(refers));
                        variableItem.setValue(refs);
                        variableItem.setDefaultValue(refs);
                    }
                }
                if (appParams.containsKey(variableItem.getField()) && Objects.nonNull(appParams.get(variableItem.getField()))) {
                    Object value = appParams.get(variableItem.getField());
                    variableItem.setValue(value);
                    variableItem.setDefaultValue(value);
                } else {
                    Object value = Optional.ofNullable(variableItem.getValue()).orElse(variableItem.getDefaultValue());
                    variableItem.setValue(value);
                    variableItem.setDefaultValue(value);
                }
            }
            variable.setVariables(variableList);
            stepWrapper.setVariable(variable);
        }
        config.setSteps(stepWrapperList);
        appMarket.setWorkflowConfig(config);
        return AppMarketConvert.INSTANCE.convert(appMarket);
    }

    /**
     * 处理小红书应用执行结果
     *
     * @param answer 小红书生成结果
     * @param appUid 应用UID
     * @param n      生成条数
     * @return 应用执行结果
     */
    public static List<XhsAppExecuteResponse> handleAnswer(String answer, String appUid, Integer n) {
        if (n == 1) {
            try {
                CopyWritingContentDTO copyWriting = JSONUtil.toBean(answer.trim(), CopyWritingContentDTO.class);
                if (Objects.isNull(copyWriting) || StringUtils.isBlank(copyWriting.getTitle()) || StringUtils.isBlank(copyWriting.getContent())) {
                    log.error("生成格式不正确：原始数据：{}", answer);
                    throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.CREATIVE_APP_EXECUTE_RESULT_FORMAT_ERROR);
                } else {
                    log.info("小红书执行应用成功。应用UID: {}, 生成条数: {}, 文案内容: {}", appUid, n, JSONUtil.toJsonStr(copyWriting));
                    return XhsAppExecuteResponse.success(appUid, copyWriting, n);
                }
            } catch (Exception exception) {
                log.error("生成格式不正确：原始数据：{}", answer);
                throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.CREATIVE_APP_EXECUTE_RESULT_FORMAT_ERROR);
            }
        } else {
            TypeReference<List<ChatCompletionChoice>> typeReference = new TypeReference<List<ChatCompletionChoice>>() {
            };
            List<ChatCompletionChoice> choices = JSONUtil.toBean(answer.trim(), typeReference, true);
            if (CollectionUtil.isEmpty(choices)) {
                log.error("生成结果为空：原始数据：{}", answer);
                throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.CREATIVE_APP_EXECUTE_RESULT_NOT_EXIST);
            }
            if (choices.size() != n) {
                log.error("生成格式不正确：原始数据：{}", answer);
                throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.CREATIVE_APP_EXECUTE_RESULT_FORMAT_ERROR);
            }
            List<XhsAppExecuteResponse> list = new ArrayList<>();
            for (int i = 0; i < choices.size(); i++) {
                ChatCompletionChoice choice = choices.get(i);
                XhsAppExecuteResponse appExecuteResponse = new XhsAppExecuteResponse();
                appExecuteResponse.setUid(appUid);

                String content = Optional.ofNullable(choice).map(ChatCompletionChoice::getMessage).map(ChatMessage::getContent).orElse("");
                if (StringUtils.isBlank(content)) {
                    log.warn("第[{}]生成失败：应用UID: {}, 总生成条数: {}, 原始数据: {}", i + 1, appUid, n, content);
                    appExecuteResponse.setSuccess(Boolean.FALSE);
                    appExecuteResponse.setErrorCode(CreativeErrorCodeConstants.CREATIVE_APP_EXECUTE_RESULT_NOT_EXIST.getCode().toString());
                    appExecuteResponse.setErrorMsg(CreativeErrorCodeConstants.CREATIVE_APP_EXECUTE_RESULT_NOT_EXIST.getMsg());
                    list.add(appExecuteResponse);
                } else {
                    try {
                        CopyWritingContentDTO copyWriting = JSONUtil.toBean(JSONUtil.parseObj(content), CopyWritingContentDTO.class);
                        if (Objects.isNull(copyWriting) || StringUtils.isBlank(copyWriting.getTitle()) || StringUtils.isBlank(copyWriting.getContent())) {
                            log.warn("第[{}]生成失败：应用UID: {}, 总生成条数: {}, 原数据: {}", i + 1, appUid, n, content);
                            appExecuteResponse.setSuccess(Boolean.FALSE);
                            appExecuteResponse.setErrorCode(CreativeErrorCodeConstants.CREATIVE_APP_EXECUTE_RESULT_FORMAT_ERROR.getCode().toString());
                            appExecuteResponse.setErrorMsg(CreativeErrorCodeConstants.CREATIVE_APP_EXECUTE_RESULT_FORMAT_ERROR.getMsg());
                            list.add(appExecuteResponse);
                        } else {
                            log.info("第[{}]生成成功：应用UID: {}, 总生成条数: {}, 文案信息: {}", i + 1, appUid, n, JSONUtil.toJsonStr(copyWriting));
                            appExecuteResponse.setSuccess(Boolean.TRUE);
                            appExecuteResponse.setCopyWriting(copyWriting);
                            list.add(appExecuteResponse);
                        }
                    } catch (Exception e) {
                        log.warn("第[{}]生成失败：应用UID: {}, 总生成条数: {}, 原数据: {}", i + 1, appUid, n, content);
                        appExecuteResponse.setSuccess(Boolean.TRUE);
                        appExecuteResponse.setErrorCode(CreativeErrorCodeConstants.CREATIVE_APP_EXECUTE_RESULT_FORMAT_ERROR.getCode().toString());
                        appExecuteResponse.setErrorMsg(CreativeErrorCodeConstants.CREATIVE_APP_EXECUTE_RESULT_FORMAT_ERROR.getMsg());
                        list.add(appExecuteResponse);
                    }
                }
            }
            log.info("小红书执行应用结束。应用UID: {}, 生成条数: {}, 结果: {}", appUid, n, list);
            return list;
        }
    }

    public static XhsAppCreativeExecuteResponse handlePracticalAnswer(String answer, XhsAppCreativeExecuteRequest request) {
        CopyWritingContentDTO copyWriting = JSONUtil.toBean(answer.trim(), CopyWritingContentDTO.class);
        if (Objects.isNull(copyWriting) || StringUtils.isBlank(copyWriting.getTitle()) || CollectionUtil.isEmpty(copyWriting.getParagraphList())) {
            log.error("生成格式不正确：原始数据：{}", answer);
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.CREATIVE_APP_EXECUTE_RESULT_FORMAT_ERROR);
        }
        List<ParagraphDTO> paragraphList = CollectionUtil.emptyIfNull(copyWriting.getParagraphList()).stream().peek(item -> {
            item.setIsUseTitle(Boolean.FALSE);
            item.setIsUseContent(Boolean.FALSE);
        }).collect(Collectors.toList());
        copyWriting.setParagraphList(paragraphList);

        XhsAppCreativeExecuteResponse response = new XhsAppCreativeExecuteResponse();
        response.setPlanUid(request.getPlanUid());
        response.setSchemeUid(request.getSchemeUid());
        response.setBusinessUid(request.getBusinessUid());
        response.setContentUid(request.getContentUid());
        response.setSchemeMode(request.getSchemeMode());
        response.setSuccess(Boolean.TRUE);
        response.setCopyWriting(copyWriting);
        return response;
    }

    /**
     * 获取文本变量
     *
     * @param field 字段
     * @param value 值
     * @return 文本变量
     */
    private static VariableItemRespVO ofInputVariableItem(String field, Object value) {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(field);
        variableItem.setLabel(field);
        variableItem.setDescription(field);
        variableItem.setDefaultValue(value);
        variableItem.setValue(value);
        variableItem.setOrder(1);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.INPUT.name());
        variableItem.setGroup(AppVariableGroupEnum.PARAMS.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.FALSE);
        variableItem.setOptions(Lists.newArrayList());
        return variableItem;
    }

    /**
     * 获取文本域变量
     *
     * @param field 字段
     * @param value 值
     * @return 文本域变量
     */
    private static VariableItemRespVO ofTextAreaVariableItem(String field, Object value) {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(field);
        variableItem.setLabel(field);
        variableItem.setDescription(field);
        variableItem.setDefaultValue(value);
        variableItem.setValue(value);
        variableItem.setOrder(1);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.TEXTAREA.name());
        variableItem.setGroup(AppVariableGroupEnum.PARAMS.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.FALSE);
        variableItem.setOptions(Lists.newArrayList());
        return variableItem;
    }

    /**
     * 获取文本变量
     *
     * @param field 字段
     * @param label 值
     * @return 文本变量
     */
    public static VariableItemRespVO ofInputVariable(String field, String label, Integer order, Integer count) {
        VariableItemRespVO variableItem = new VariableItemRespVO();
        variableItem.setField(field);
        variableItem.setLabel(label);
        variableItem.setDescription(label);
        variableItem.setOrder(order);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.INPUT.name());
        variableItem.setGroup(AppVariableGroupEnum.PARAMS.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.FALSE);
        variableItem.setOptions(Lists.newArrayList());
        variableItem.setCount(count);
        return variableItem;
    }

    /**
     * 处理干货文本的内容
     *
     * @param copyWriting 干货文本
     * @return 干货文本内容
     */
    public static String buildPracticalCopyWritingContent(CopyWritingContentDTO copyWriting) {
        List<ParagraphDTO> paragraphList = copyWriting.getParagraphList();
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < CollectionUtil.emptyIfNull(paragraphList).size(); i++) {
            ParagraphDTO paragraph = paragraphList.get(i);
            content.append(paragraph.getParagraphTitle()).append("\n");
            content.append(paragraph.getParagraphContent()).append("\n\n");
        }
        return content.toString();
    }

    public static AppRespVO transformCustomExecute(CustomCreativeSchemeConfigDTO customConfiguration,
                                                  List<String> imageUrlList,
                                                  AppRespVO appResponse,
                                                  Map<String, CreativeImageTemplateDTO> posterMap,
                                                  Integer index) {

        String appUid = customConfiguration.getAppUid();
        List<CreativeSchemeStepDTO> schemeSteps = customConfiguration.getSteps();

        // 工作流配置信息
        WorkflowConfigRespVO workflowConfig = appResponse.getWorkflowConfig();

        List<WorkflowStepWrapperRespVO> stepWrappers = workflowConfig.getSteps();
        for (WorkflowStepWrapperRespVO stepWrapper : stepWrappers) {
            String field = stepWrapper.getField();
            Optional<CreativeSchemeStepDTO> stepOptional = schemeSteps.stream().filter(item -> field.equals(item.getId())).findFirst();
            if (!stepOptional.isPresent()) {
                continue;
            }

            CreativeSchemeStepDTO schemeStep = stepOptional.get();
            Map<String, Object> schemeVariableMap = toAppVariableMap(schemeStep, imageUrlList, index);

            stepWrapper.putVariable(schemeVariableMap);

        }
        workflowConfig.setSteps(stepWrappers);
        appResponse.setWorkflowConfig(workflowConfig);
        return appResponse;
    }

    public static Map<String, Object> toAppVariableMap(CreativeSchemeStepDTO schemeStep, List<String> imageUrlList, Integer index) {
        Map<String, Object> map = new HashMap<>();
        map.put("REFERS", JSONUtil.toJsonStr(handlerReferences(schemeStep.getRefers())));
        map.put("GENERATE_MODE", schemeStep.getGenerateMode());
        map.put("REQUIREMENT", handlerRequirement(schemeStep.getRequirement(), schemeStep.getVariables()));
        map.put("PARAGRAPH_COUNT", schemeStep.getParagraphCount());
        map.put("POSTER_MATERIAL", JSONUtil.toJsonStr(imageUrlList));
        map.put("POSTER_STYLE", JSONUtil.toJsonStr(schemeStep.getImageStyles().get(index)));
        return map;
    }

    /**
     * 处理需求文本，变量填充等
     *
     * @param requirement  文案生成模板
     * @param variableList 创作计划配置变量
     * @return 处理后的文案
     */
    public static String handlerRequirement(String requirement, List<VariableItemDTO> variableList) {
        for (VariableItemDTO variableItem : CollectionUtil.emptyIfNull(variableList)) {
            String field = variableItem.getField();
            Object value = Optional.ofNullable(variableItem.getValue()).orElse("");
            requirement = requirement.replace("{" + field + "}", String.valueOf(value));
        }
        return requirement;
    }
}
