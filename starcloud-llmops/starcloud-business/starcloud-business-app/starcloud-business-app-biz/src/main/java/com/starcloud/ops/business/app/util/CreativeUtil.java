package com.starcloud.ops.business.app.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.google.common.collect.Lists;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.plan.dto.CreativePlanAppExecuteDTO;
import com.starcloud.ops.business.app.api.plan.dto.CreativePlanConfigDTO;
import com.starcloud.ops.business.app.api.plan.dto.CreativePlanImageExecuteDTO;
import com.starcloud.ops.business.app.api.plan.dto.CreativePlanImageStyleExecuteDTO;
import com.starcloud.ops.business.app.api.scheme.dto.CopyWritingContentDTO;
import com.starcloud.ops.business.app.api.scheme.dto.CreativeSchemeConfigDTO;
import com.starcloud.ops.business.app.api.scheme.dto.CreativeSchemeCopyWritingTemplateDTO;
import com.starcloud.ops.business.app.api.scheme.vo.request.CreativeSchemeReqVO;
import com.starcloud.ops.business.app.api.scheme.vo.response.CreativeSchemeRespVO;
import com.starcloud.ops.business.app.api.xhs.XhsImageStyleDTO;
import com.starcloud.ops.business.app.api.xhs.XhsImageTemplateDTO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppExecuteResponse;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableGroupEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableStyleEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableTypeEnum;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.WORKFLOW_CONFIG_FAILURE;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Slf4j
@SuppressWarnings("unused")
public class CreativeUtil {

    private static final String NAME = "NAME";
    private static final String TYPE = "TYPE";
    private static final String CATEGORY = "CATEGORY";
    private static final String TAGS = "TAGS";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String REFERS = "REFERS";
    private static final String IS_PROMOTE_MP = "IS_PROMOTE_MP";
    private static final String MP_CODE = "MP_CODE";
    private static final String DEMAND = "DEMAND";
    private static final String EXAMPLE = "EXAMPLE";

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

        Map<String, List<VariableItemDTO>> paramMap = planConfig.getParamMap();
        List<VariableItemDTO> variableList = Optional.ofNullable(paramMap.get(scheme.getUid())).orElse(Lists.newArrayList());

        List<VariableItemDTO> params = Lists.newArrayList();
        params.add(ofInputVariableItem(NAME, scheme.getName()));
        params.add(ofInputVariableItem(TYPE, scheme.getType()));
        params.add(ofInputVariableItem(CATEGORY, scheme.getCategory()));
        params.add(ofInputVariableItem(TAGS, String.join(",", CollectionUtil.emptyIfNull(scheme.getTags()))));
        params.add(ofTextAreaVariableItem(DESCRIPTION, scheme.getDescription()));
        params.add(ofTextAreaVariableItem(REFERS, JSONUtil.toJsonStr(CollectionUtil.emptyIfNull(scheme.getRefers()))));
        params.add(ofTextAreaVariableItem(DEMAND, CreativeUtil.handlerDemand(copyWritingTemplate, variableList)));
        params.add(ofTextAreaVariableItem(EXAMPLE, copyWritingTemplate.getExample()));

        CreativePlanAppExecuteDTO appExecute = new CreativePlanAppExecuteDTO();
        appExecute.setUid(appUid);
        appExecute.setParams(params);
        appExecute.setScene(AppSceneEnum.XHS_WRITING.name());
        return appExecute;
    }

    /**
     * 获取小红书批量图片执行参数
     *
     * @param style 图片模板列表
     * @return 图片执行参数
     */
    public static CreativePlanImageStyleExecuteDTO getImageStyleExecuteRequest(XhsImageStyleDTO style) {
        // 图片参数信息
        List<CreativePlanImageExecuteDTO> imageExecuteRequestList = Lists.newArrayList();
        List<XhsImageTemplateDTO> templateList = CollectionUtil.emptyIfNull(style.getTemplateList());
        for (int i = 0; i < templateList.size(); i++) {
            XhsImageTemplateDTO template = templateList.get(i);
            CreativePlanImageExecuteDTO imageExecuteRequest = new CreativePlanImageExecuteDTO();
            imageExecuteRequest.setIndex(i + 1);
            imageExecuteRequest.setIsMain(i == 0);
            imageExecuteRequest.setImageTemplate(template.getId());
            imageExecuteRequest.setParams(template.getVariables());
            imageExecuteRequestList.add(imageExecuteRequest);
        }
        // 图片风格执行参数
        CreativePlanImageStyleExecuteDTO imageStyleExecuteRequest = new CreativePlanImageStyleExecuteDTO();
        imageStyleExecuteRequest.setId(style.getId());
        imageStyleExecuteRequest.setName(style.getName());
        imageStyleExecuteRequest.setImageRequests(imageExecuteRequestList);
        return imageStyleExecuteRequest;
    }

    /**
     * 将数据转为应用，参数替换
     *
     * @param app     应用市场应用
     * @param request 创作方案请求
     * @return 应用市场应用
     */
    public static AppReqVO transform(AppMarketRespVO app, CreativeSchemeReqVO request) {
        // 参数信息
        CreativeSchemeConfigDTO configuration = request.getConfiguration();
        CreativeSchemeCopyWritingTemplateDTO copyWritingTemplate = configuration.getCopyWritingTemplate();

        // 获取步骤配置信息
        WorkflowConfigRespVO config = app.getWorkflowConfig();
        AppValidate.notNull(config, WORKFLOW_CONFIG_FAILURE);

        List<WorkflowStepWrapperRespVO> stepWrapperList = config.getSteps();
        AppValidate.notEmpty(stepWrapperList, WORKFLOW_CONFIG_FAILURE);

        for (WorkflowStepWrapperRespVO stepWrapper : stepWrapperList) {
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
                    String refers = JSONUtil.toJsonStr(request.getRefers());
                    variableItem.setValue(refers);
                    variableItem.setDefaultValue(refers);

                } else if (DEMAND.equals(variableItem.getField()) && StringUtils.isNotBlank(copyWritingTemplate.getDemand())) {
                    String demand = handlerDemand(copyWritingTemplate, null);
                    variableItem.setValue(demand);
                    variableItem.setDefaultValue(demand);

                } else if (EXAMPLE.equals(variableItem.getField()) && CollectionUtil.isNotEmpty(copyWritingTemplate.getExample())) {
                    String example = JSONUtil.toJsonStr(copyWritingTemplate.getExample());
                    variableItem.setValue(example);
                    variableItem.setDefaultValue(example);
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
     * 处理需求文本，变量填充等
     *
     * @param copyWritingTemplate 文案生成模板
     * @param variableList        创作计划配置变量
     * @return 处理后的文案
     */
    public static String handlerDemand(CreativeSchemeCopyWritingTemplateDTO copyWritingTemplate, List<VariableItemDTO> variableList) {
        String demand = copyWritingTemplate.getDemand();
        if (CollectionUtil.isEmpty(variableList)) {
            variableList = Optional.ofNullable(copyWritingTemplate.getVariables()).orElse(Lists.newArrayList());
        }
        Map<String, VariableItemDTO> variableMap = CollectionUtil.emptyIfNull(variableList).stream().collect(Collectors.toMap(VariableItemDTO::getField, item -> item));
        for (VariableItemDTO variableItem : CollectionUtil.emptyIfNull(copyWritingTemplate.getVariables())) {
            String field = variableItem.getField();
            VariableItemDTO variable = variableMap.getOrDefault(field, variableItem);
            Object value = variable.getValue();
            if (Objects.isNull(value)) {
                value = Optional.ofNullable(variable.getDefaultValue()).orElse("");
            }
            demand = demand.replace("{" + field + "}", String.valueOf(value));
        }
        return demand;
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
        if (Objects.nonNull(request.getSseEmitter())) {
            executeRequest.setSseEmitter(request.getSseEmitter());
        }
        executeRequest.setStepId(request.getStepId());
        executeRequest.setUserId(request.getUserId());
        executeRequest.setMode(AppModelEnum.COMPLETION.name());
        executeRequest.setScene(StringUtils.isBlank(request.getScene()) ? AppSceneEnum.XHS_WRITING.name() : request.getScene());
        executeRequest.setAppUid(app.getUid());
        executeRequest.setN(request.getN());
        executeRequest.setAppReqVO(transform(app, request.getParams()));
        return executeRequest;
    }

    /**
     * 转换请求，转换为应用请求，并且填充参数
     *
     * @param app       应用
     * @param appParams 请求
     * @return 应用请求
     */
    public static AppReqVO transform(AppMarketRespVO app, Map<String, Object> appParams) {

        // 获取步骤配置信息
        WorkflowConfigRespVO config = app.getWorkflowConfig();
        AppValidate.notNull(config, WORKFLOW_CONFIG_FAILURE);

        List<WorkflowStepWrapperRespVO> stepWrapperList = config.getSteps();
        AppValidate.notEmpty(stepWrapperList, WORKFLOW_CONFIG_FAILURE);

        for (WorkflowStepWrapperRespVO stepWrapper : stepWrapperList) {
            VariableRespVO variable = stepWrapper.getVariable();
            List<VariableItemRespVO> variableList = variable.getVariables();
            if (CollectionUtil.isEmpty(variableList)) {
                continue;
            }
            for (VariableItemRespVO variableItem : variableList) {
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
        app.setWorkflowConfig(config);
        return AppMarketConvert.INSTANCE.convert(app);
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
            CopyWritingContentDTO copyWriting = JSONUtil.toBean(answer.trim(), CopyWritingContentDTO.class);
            if (Objects.isNull(copyWriting) || StringUtils.isBlank(copyWriting.getTitle()) || StringUtils.isBlank(copyWriting.getContent())) {
                log.error("生成格式不正确：原始数据：{}", answer);
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.XHS_APP_EXECUTE_RESULT_FORMAT_ERROR);
            } else {
                log.info("小红书执行应用成功。应用UID: {}, 生成条数: {}, 文案内容: {}", appUid, n, JSONUtil.toJsonStr(copyWriting));
                return XhsAppExecuteResponse.success(appUid, copyWriting, n);
            }
        } else {
            TypeReference<List<ChatCompletionChoice>> typeReference = new TypeReference<List<ChatCompletionChoice>>() {
            };
            List<ChatCompletionChoice> choices = JSONUtil.toBean(answer.trim(), typeReference, true);
            if (CollectionUtil.isEmpty(choices)) {
                log.error("生成结果为空：原始数据：{}", answer);
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.XHS_APP_EXECUTE_RESULT_NOT_EXIST);
            }
            if (choices.size() != n) {
                log.error("生成格式不正确：原始数据：{}", answer);
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.XHS_APP_EXECUTE_RESULT_FORMAT_ERROR);
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
                    appExecuteResponse.setErrorCode(ErrorCodeConstants.XHS_APP_EXECUTE_RESULT_NOT_EXIST.getCode().toString());
                    appExecuteResponse.setErrorMsg(ErrorCodeConstants.XHS_APP_EXECUTE_RESULT_NOT_EXIST.getMsg());
                    list.add(appExecuteResponse);
                } else {
                    CopyWritingContentDTO copyWriting = JSONUtil.toBean(JSONUtil.parseObj(content), CopyWritingContentDTO.class);
                    if (Objects.isNull(copyWriting) || StringUtils.isBlank(copyWriting.getTitle()) || StringUtils.isBlank(copyWriting.getContent())) {
                        log.warn("第[{}]生成失败：应用UID: {}, 总生成条数: {}, 原数据: {}", i + 1, appUid, n, content);
                        appExecuteResponse.setSuccess(Boolean.FALSE);
                        appExecuteResponse.setErrorCode(ErrorCodeConstants.XHS_APP_EXECUTE_RESULT_FORMAT_ERROR.getCode().toString());
                        appExecuteResponse.setErrorMsg(ErrorCodeConstants.XHS_APP_EXECUTE_RESULT_FORMAT_ERROR.getMsg());
                        list.add(appExecuteResponse);
                    } else {
                        log.info("第[{}]生成成功：应用UID: {}, 总生成条数: {}, 文案信息: {}", i + 1, appUid, n, JSONUtil.toJsonStr(copyWriting));
                        appExecuteResponse.setSuccess(Boolean.TRUE);
                        appExecuteResponse.setCopyWriting(copyWriting);
                        list.add(appExecuteResponse);
                    }
                }
            }
            log.info("小红书执行应用结束。应用UID: {}, 生成条数: {}, 结果: {}", appUid, n, list);
            return list;
        }
    }

    /**
     * 获取文本变量
     *
     * @param field 字段
     * @param value 值
     * @return 文本变量
     */
    private static VariableItemDTO ofInputVariableItem(String field, Object value) {
        VariableItemDTO variableItem = new VariableItemDTO();
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
    private static VariableItemDTO ofTextAreaVariableItem(String field, Object value) {
        VariableItemDTO variableItem = new VariableItemDTO();
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

}
