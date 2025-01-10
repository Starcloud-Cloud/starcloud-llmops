package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.kstry.framework.core.annotation.Invoke;
import cn.kstry.framework.core.annotation.NoticeVar;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.annotation.TaskComponent;
import cn.kstry.framework.core.annotation.TaskService;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import com.fasterxml.jackson.module.jsonSchema.types.StringSchema;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.verification.Verification;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.JsonDocsDefSchema;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.domain.handler.poster.PosterGenerationHandler;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.enums.app.AppStepResponseTypeEnum;
import com.starcloud.ops.business.app.enums.materiallibrary.MaterialFormatTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.feign.dto.PosterImage;
import com.starcloud.ops.business.app.model.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.model.poster.PosterTemplateDTO;
import com.starcloud.ops.business.app.model.poster.PosterTitleDTO;
import com.starcloud.ops.business.app.model.poster.PosterVariableDTO;
import com.starcloud.ops.business.app.service.xhs.executor.PosterThreadPoolHolder;
import com.starcloud.ops.business.app.util.CreativeUtils;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import com.starcloud.ops.llm.langchain.core.model.multimodal.qwen.ChatVLQwen;
import com.starcloud.ops.llm.langchain.core.schema.message.multimodal.HumanMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.multimodal.MultiModalMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@SuppressWarnings("all")
@Slf4j
@TaskComponent
public class PosterActionHandler extends BaseActionHandler {

    /**
     * 图片URL限制像素
     */
    private static final String IMAGE_URL_LIMIT_PIXEL = "?x-oss-process=image/resize,m_lfit,w_440,h_440";

    /**
     * 首图
     */
    private static final String MAIN_IMAGE = "首图";

    /**
     * 图片
     */
    private static final String IMAGE = "图片";

    /**
     * 提取素材索引正则
     */
    private static final Pattern MATERIAL_PATTERN = Pattern.compile("\\.docs\\[(\\d+)]");

    /**
     * 是否依赖的正则表达式
     */
    private static final String DEPENDENCY_REGEX = "%s\\.(" + MAIN_IMAGE + "|" + IMAGE + "\\d)";

    /**
     * 使用多模态正则表达式
     */
    private static final Pattern MULTIMODAL_PATTERN = Pattern.compile("AI分析\\.(图片标题|图片副标题)");

    /**
     * 线程池
     */
    private static final PosterThreadPoolHolder POSTER_TEMPLATE_THREAD_POOL_HOLDER = SpringUtil.getBean(PosterThreadPoolHolder.class);

    /**
     * 流程执行器，action 执行入口
     *
     * @param context           上下文
     * @param scopeDataOperator 作用域数据操作器
     * @return 执行结果
     */
    @NoticeVar
    @TaskService(name = "PosterActionHandler", invoke = @Invoke(timeout = 1800000))
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
//        String stepName = wrapper.getName();
//        String stepCode = wrapper.getStepCode();
//        Object systemPosterConfig = wrapper.getModelVariable(CreativeConstants.SYSTEM_POSTER_STYLE_CONFIG);
//
//        if (Objects.isNull(systemPosterConfig)) {
//            VerificationUtils.addVerificationStep(verifications, stepCode,
//                    "【" + stepName + "】步骤执行失败：系统风格模板配置为空！");
//            return verifications;
//        }
//
//        if (systemPosterConfig instanceof String) {
//            String systemPosterStyleConfig = String.valueOf(systemPosterConfig);
//            if (StringUtils.isBlank(systemPosterStyleConfig) || "[]".equals(systemPosterStyleConfig) || "null".equalsIgnoreCase(systemPosterStyleConfig)) {
//                VerificationUtils.addVerificationStep(verifications, stepCode,
//                        "【" + stepName + "】步骤执行失败：系统风格模板配置为空！");
//                return verifications;
//            }
//            List<PosterStyleDTO> systemPosterStyleList = JsonUtils.parseArray(systemPosterStyleConfig, PosterStyleDTO.class);
//
//            if (CollectionUtil.isEmpty(systemPosterStyleList)) {
//                VerificationUtils.addVerificationStep(verifications, stepCode,
//                        "【" + stepName + "】步骤执行失败：系统风格模板配置为空！");
//                return verifications;
//            }
//        }
//        if (systemPosterConfig instanceof List) {
//            List<PosterStyleDTO> systemPosterStyleList = (List<PosterStyleDTO>) systemPosterConfig;
//            if (CollectionUtil.isEmpty(systemPosterStyleList)) {
//                VerificationUtils.addVerificationStep(verifications, stepCode,
//                        "【" + stepName + "】步骤执行失败：系统风格模板配置为空！");
//                return verifications;
//            }
//            wrapper.putModelVariable(CreativeConstants.SYSTEM_POSTER_STYLE_CONFIG, JsonUtils.toJsonString(systemPosterStyleList));
//        }
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
        return AdminUserRightsTypeEnum.MAGIC_IMAGE;
    }

    /**
     * 暂时不返回任何结构
     *
     * @return
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    public JsonSchema getOutVariableJsonSchema(WorkflowStepWrapper stepWrapper) {
        ObjectSchema objectSchema = new ObjectSchema();
        objectSchema.setTitle(stepWrapper.getStepCode());
        objectSchema.setDescription(stepWrapper.getDescription());
        objectSchema.setId(stepWrapper.getFlowStep().getHandler());

        for (int i = 0; i < 10; i++) {
            if (i == 0) {
                StringSchema schema = new StringSchema();
                schema.setTitle(MAIN_IMAGE);
                schema.setDescription(MAIN_IMAGE + "-" + MaterialFormatTypeEnum.IMAGE.getCode());
                objectSchema.putProperty(MAIN_IMAGE, schema);
                continue;
            }
            StringSchema schema = new StringSchema();
            schema.setTitle(IMAGE + i);
            schema.setDescription(IMAGE + i + "-" + MaterialFormatTypeEnum.IMAGE.getCode());
            objectSchema.putProperty(IMAGE + i, schema);
        }
        return objectSchema;
    }

    /**
     * 执行OpenApi生成的步骤
     *
     * @param context
     * @return 执行结果
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected ActionResponse doExecute(AppContext context) {
        try {
            // 开始日志打印
            loggerBegin(context, "海报图片生成步骤");

            /*
             * 获取到待执行的海报风格，并进行校验
             */
            // 获取海报风格
            PosterStyleDTO style = getPosterStyle(context);
            // 校验海报模版
            style.validate();

            /*
             * 对风格的模板进行标记处理
             * 是否依赖别的模板的生成结果
             * 只做标记和必要的校验，不进行参数的填充。
             */
            markerDependencyTemplate(context, style);

            /*
             * 处理需要复制的模板 <br>
             * 并且进行复制模版的关于素材的变量的填充。
             * 需要依赖结果的模板，即使是复制的模板，也不会复制。
             */
            handlerCopyTemplate(context, style);

            /*
             * 执行不依赖结果的海报模板，并且获取到结果
             */
            List<PosterGenerationHandler.Response> undependencyResponse = batchPoster(context, style, Boolean.FALSE);

            /*
             * 判断是否要执行依赖结果的模板，
             * 只要有一个依赖其他模板生成结果，该值就为true。
             * 如果没有依赖其他模板生成结果的模板，则直接返回结果，此时顺序已经天然一致，不需要进行额外的排序
             * 如果有依赖其他模板生成结果的模板，执行依赖结果的模板
             */
            boolean hasDependency = hasDependencyTemplate(style);
            if (!hasDependency) {
                return successResponse(undependencyResponse, style);
            }

            /*
             * 执行依赖结果的模板
             * 首先将不依赖结果的模板结果放入到全局上下文中，以便后续的依赖结果的模板可以使用
             * 其次执行依赖结果的模板
             */
            // 将不依赖的模板结果的模板结果放入到全局上下文中
            putNoDependencyResultContext(context, style, undependencyResponse);
            // 执行依赖结果的模板列表
            List<PosterGenerationHandler.Response> dependencyResponse = batchPoster(context, style, Boolean.TRUE);
            // 对最终结果进行处理，合并，排序
            List<PosterGenerationHandler.Response> list = handlerAllResponse(style, dependencyResponse, undependencyResponse);
            // 处理并且返回结果
            ActionResponse response = successResponse(list, style);

            // 结束日志打印
            loggerSuccess(context, response, "海报图片生成步骤");

            return response;
        } catch (Exception exception) {
            // 抛出异常，一个总的错误码。
            throw ServiceExceptionUtil.exception0(ErrorCodeConstants.EXECUTE_POSTER_EXCEPTION.getCode(), exception.getMessage());
        }
    }

    /**
     * 获取海报风格
     *
     * @return 海报风格
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private PosterStyleDTO getPosterStyle(AppContext context) {
        // 海报模版参数
        String posterStyle = String.valueOf(context.getContextVariablesValue(CreativeConstants.POSTER_STYLE, Boolean.FALSE));
        if (StringUtils.isBlank(posterStyle) || "null".equalsIgnoreCase(posterStyle) || "{}".equals(posterStyle)) {
            throw ServiceExceptionUtil.invalidParamException("【{}】步骤执行失败：海报风格配置为空，请检查您的配置或联系管理员！", context.getStepId());
        }
        // 转为海报模版对象
        try {
            PosterStyleDTO style = JsonUtils.parseObject(posterStyle, PosterStyleDTO.class);
            if (Objects.isNull(style)) {
                throw ServiceExceptionUtil.invalidParamException("【{}】步骤执行失败：海报风格配置为空，请检查您的配置或联系管理员！", context.getStepId());
            }
            return style;
        } catch (Exception e) {
            throw ServiceExceptionUtil.invalidParamException("【{}】步骤执行失败：海报风格配置解析失败，请检查您的配置或联系管理员！", context.getStepId());
        }
    }

    /**
     * 判断是否有依赖结果的模板
     *
     * @param style 海报风格
     * @return 是否有依赖结果的模板
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private boolean hasDependencyTemplate(PosterStyleDTO style) {
        return style.posterTemplateList().stream().anyMatch(PosterTemplateDTO::getIsDependency);
    }

    /**
     * 标记依赖模板，仅仅做标记，不进行处理
     *
     * @param posterStyle 海报风格
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private void markerDependencyTemplate(AppContext context, PosterStyleDTO posterStyle) {
        // 校验海报风格模板
        List<PosterTemplateDTO> templateList = posterStyle.posterTemplateList();
        String stepCode = context.getStepWrapper().getStepCode();
        for (int i = 0; i < templateList.size(); i++) {
            PosterTemplateDTO posterTemplate = templateList.get(i);
            List<PosterVariableDTO> variableList = posterTemplate.posterVariableList();
            // 默认设置为不依赖生成结果
            posterTemplate.setIsDependency(Boolean.FALSE);
            // 循环处理变量列表
            for (PosterVariableDTO variable : variableList) {
                // 获取变量值
                String value = variable.emptyIfNullValue();
                // 如果变量值为空，则跳过，进行下一个变量的校验
                if (StringUtils.isBlank(value)) {
                    continue;
                }

                // 是否依赖图片生成结果的正则表达式
                Pattern pattern = Pattern.compile(String.format(DEPENDENCY_REGEX, stepCode));
                Matcher matcher = pattern.matcher(value);
                // 如果匹配到，则设置为依赖，并且跳出变量列表循环，否则抛出异常
                boolean matched = false;
                while (matcher.find()) {
                    String group = matcher.group();
                    if (group.contains(MAIN_IMAGE)) {
                        if (i == 0) {
                            throw ServiceExceptionUtil.invalidParamException(
                                    "【{}】步骤执行失败: [{}][{}][{}] 变量不能依赖自身生成结果，经检查您的模板变量配置！",
                                    context.getStepId(), posterStyle.getName(), posterTemplate.getName(), variable.getLabel());
                        }
                        matched = true;
                        continue;
                    }
                    // 提取出匹配到的数字
                    Integer dependencyIndex = Integer.parseInt(group.substring(group.length() - 1));
                    if (i == dependencyIndex) {
                        throw ServiceExceptionUtil.invalidParamException("" +
                                        "【{}】步骤执行失败: [{}][{}][{}] 变量不能依赖自身生成结果，经检查您的模板变量配置！",
                                context.getStepId(), posterStyle.getName(), posterTemplate.getName(), variable.getLabel());
                    }
                    if (dependencyIndex > templateList.size()) {
                        throw ServiceExceptionUtil.invalidParamException(
                                "【{}】步骤执行失败: [{}][{}][{}] 变量依赖的模板生成结果超出模板数量，经检查您的模板变量配置！",
                                context.getStepId(), posterStyle.getName(), posterTemplate.getName(), variable.getLabel());
                    }
                    matched = true;
                }
                if (matched) {
                    posterTemplate.setIsDependency(Boolean.TRUE);
                    break;
                }
            }
        }
        // 判断是否所有的模板都依赖图片生成的结果
        boolean allDependency = templateList.stream().allMatch(PosterTemplateDTO::getIsDependency);
        // 如果所有的模板都依赖图片生成的结果，则说明出现循环依赖，抛出异常
        if (allDependency) {
            throw ServiceExceptionUtil.invalidParamException(
                    "【{}】步骤执行失败: [{}] 所有的海报模板均有依赖其他海报模板生成结果的变量，导致无法进行图片生成，请检查您的海报模板变量配置！",
                    context.getStepId(), posterStyle.getName());
        }
        posterStyle.setTemplateList(templateList);
    }

    /**
     * 处理需要复制的模板
     *
     * @param style 海报风格
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private void handlerCopyTemplate(AppContext context, PosterStyleDTO style) {
        // 获取海报模板列表
        List<PosterTemplateDTO> templateList = style.posterTemplateList();

        PosterTemplateDTO copyTemplate = null;
        int copyIndex = -1;
        int copyTemplateNeedMaterialCount = -1;
        // 获取到最后一个需要复制的模板，倒序查找
        for (int i = templateList.size() - 1; i >= 0; i--) {
            PosterTemplateDTO template = templateList.get(i);
            if (Objects.nonNull(template) && Objects.nonNull(template.getIsCopy()) &&
                    template.getIsCopy() && !template.getIsDependency()) {
                // 计算需要复制到的模板需要的素材数量
                int templateNeedMaterialCount = calculateTemplateNeedMaterialCount(template);
                if (templateNeedMaterialCount == -1) {
                    continue;
                }
                copyTemplate = template;
                copyIndex = i;
                copyTemplateNeedMaterialCount = templateNeedMaterialCount;
                break;
            }
        }

        // 如果没有需要复制的模板，则直接返回
        if (Objects.isNull(copyTemplate) || copyIndex == -1 || copyTemplateNeedMaterialCount == -1) {
            return;
        }

        // 获取到素材列表
        List<Map<String, Object>> materialList = getMaterialList(context);
        if (CollectionUtil.isEmpty(materialList)) {
            return;
        }
        // 复制素材，并且截取需要的素材
        List<Map<String, Object>> copyMaterialList = SerializationUtils.clone(new ArrayList<>(materialList));

        int copyCount = 0;
        List<Map<String, Object>> subCopyMaterialList = new ArrayList<>();
        copyTemplate.setIsUseAllMaterial(true);

        if (Objects.nonNull(copyTemplate.getIsUseAllMaterial()) && copyTemplate.getIsUseAllMaterial()) {
            if (copyTemplateNeedMaterialCount < copyMaterialList.size()) {
                subCopyMaterialList = copyMaterialList.subList(copyTemplateNeedMaterialCount, copyMaterialList.size());
                int remainder = subCopyMaterialList.size() % copyTemplateNeedMaterialCount;
                copyCount = (subCopyMaterialList.size() / copyTemplateNeedMaterialCount) + (remainder == 0 ? 0 : 1);
            } else {
                subCopyMaterialList = new ArrayList<>();
                copyCount = 0;
            }
        } else {
            // 计算现在模板需要的素材数量
            int needMaterialCount = calculateNeedMaterialCount(style);
            // 如果素材足够
            if (needMaterialCount < copyMaterialList.size()) {
                // 此为需要进行复制的素材
                subCopyMaterialList = copyMaterialList.subList(needMaterialCount, copyMaterialList.size());
                // 计算需要复制的次数，取余如果余数不为 0 则加 1
                int remainder = subCopyMaterialList.size() % copyTemplateNeedMaterialCount;
                // 获取相除的整数部分
                copyCount = (subCopyMaterialList.size() / copyTemplateNeedMaterialCount) + (remainder == 0 ? 0 : 1);
            } else {
                subCopyMaterialList = new ArrayList<>();
                copyCount = 0;
            }
        }

        for (int i = 0; i < copyCount; i++) {
            PosterTemplateDTO copy = SerializationUtils.clone(copyTemplate);
            copy.setUuid(IdUtil.fastSimpleUUID());
            copy.setName("copy");

            List<Map<String, Object>> materials = new ArrayList<>();
            if (subCopyMaterialList.isEmpty()) {
                break;
            }
            if (copyTemplateNeedMaterialCount >= subCopyMaterialList.size()) {
                materials = subCopyMaterialList;
                subCopyMaterialList = new ArrayList<>();
            } else {
                materials = subCopyMaterialList.subList(0, copyTemplateNeedMaterialCount);
                subCopyMaterialList = subCopyMaterialList.subList(copyTemplateNeedMaterialCount, subCopyMaterialList.size());
            }

            WorkflowStepWrapper materialStepWrapper = context.getStepWrapper(MaterialActionHandler.class);

            Map<String, Object> materialMap = new HashMap<>();
            JsonDocsDefSchema materialData = new JsonDocsDefSchema();
            materialData.setDocs(materials);
            materialMap.put(materialStepWrapper.getStepCode(), materialData);

            // 截取需要的素材
            Map<String, Object> variableMap = getDocPosterVariableMap(copy);
            Map<String, Object> replaceValueMap = context.parseMapFromVariablesValues(variableMap, materialMap);
            Set<String> uuidList = replaceValueMap.keySet();

            // 循环处理变量列表，进行值填充
            for (PosterVariableDTO variable : copy.posterVariableList()) {
                String uuid = variable.getUuid();
                // 如果该变量不在作用域中，则跳过，交给后续处理。
                if (!uuidList.contains(uuid)) {
                    continue;
                }
                // 从作用域数据中获取变量值
                Object value = replaceValueMap.get(variable.getUuid());
                // 如果从作用域数据中获取的变量值为空，则为空字符串。
                if (StringUtil.objectBlank(value)) {
                    value = StringUtils.EMPTY;
                }
                variable.setValue(value);
            }
            copyIndex = copyIndex + 1;
            // 将复制的模板放入到模板列表中
            templateList.add(copyIndex, copy);
        }

        style.setTemplateList(templateList);
    }

    /**
     * 计算需要的素材数量
     *
     * @param style 海报风格
     * @return 需要的素材数量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private int calculateNeedMaterialCount(PosterStyleDTO style) {

        // 报模板，图片变量值，获取到选择素材的最大索引列表
        List<Integer> templateIndexList = new ArrayList<>();
        for (PosterTemplateDTO template : style.posterTemplateList()) {
            // 如果海报模板为空，设置默认值为 -1
            if (Objects.isNull(template)) {
                templateIndexList.add(-1);
                continue;
            }
            int templateMaxIndex = calculateTemplateNeedMaterialCount(template);
            templateIndexList.add(templateMaxIndex);
        }

        // 获取到最大的索引
        int maxIndex = templateIndexList
                .stream().max(Comparator.comparingInt(Integer::intValue)).orElse(-1);

        return maxIndex;
    }

    /**
     * 计算需要的素材数量
     *
     * @param style 海报风格
     * @return 需要的素材数量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private int calculateTemplateNeedMaterialCount(PosterTemplateDTO template) {

        // 如果海报模板为空，设置默认值为 -1
        if (Objects.isNull(template)) {
            return -1;
        }
        // 获取每一个海报模板，图片变量值，获取到选择素材的最大索引
        Integer maxIndex = template.posterVariableList().stream()
                .filter(Objects::nonNull)
                .map(item -> {
                    Integer matcher = matcherMax(item.emptyIfNullValue());
                    if (matcher == -1) {
                        return -1;
                    }
                    return matcher + 1;
                })
                .max(Comparator.comparingInt(Integer::intValue)).orElse(-1);
        return maxIndex;
    }

    /**
     * 获取素材列表
     *
     * @return 素材列表
     * @return
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private List<Map<String, Object>> getMaterialList(AppContext context) {
        // 获取素材的步骤
        WorkflowStepWrapper materialStepWrapper = context.getStepWrapper(MaterialActionHandler.class);
        // 获取素材的响应结果
        ActionResponse materialStepWrapperResponse = context.getStepResponse(materialStepWrapper.getStepCode());
        AppValidate.notNull(materialStepWrapperResponse, materialStepWrapper.getStepCode() + "步骤结果为空！无法进行图片变量替换！");
        // 获取素材的响应结果
        JsonData output = materialStepWrapperResponse.getOutput();
        JsonDocsDefSchema data = (JsonDocsDefSchema) output.getData();
        AppValidate.notNull(data, materialStepWrapper.getStepCode() + "步骤结果为空！无法进行图片变量替换！");
        List docs = data.getDocs();
        return docs;
    }

    /**
     * 获取海报模板
     *
     * @param posterStyle        海报风格
     * @param isDependencyResult 海报模板
     * @return 海报模板列表
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private List<PosterTemplateDTO> getPosterTemplateList(PosterStyleDTO posterStyle, boolean isDependencyResult) {
        return posterStyle.posterTemplateList().stream().filter(item -> item.getIsDependency().equals(isDependencyResult)).filter(item -> Objects.isNull(item.getIsExecute()) || item.getIsExecute()).collect(Collectors.toList());
    }

    /**
     * 海报风格参数填充
     *
     * @param posterStyle        海报风格
     * @param isDependencyResult 是否依赖结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private void assemble(AppContext context, PosterStyleDTO posterStyle, boolean isDependencyResult) {
        // 获取海报模版列表
        List<PosterTemplateDTO> posterTemplateList = posterStyle.posterTemplateList();
        String stepCode = context.getStepWrapper().getStepCode();
        WorkflowStepWrapper materialStepWrapper = context.getStepWrapper(MaterialActionHandler.class);

        // 循环处理海报模板列表
        for (PosterTemplateDTO posterTemplate : posterTemplateList) {
            // 如果 isDependencyResult 和 isDependency 不一致，则跳过, 不进行处理
            if (!posterTemplate.getIsDependency().equals(isDependencyResult)) {
                continue;
            }

            // 获取海报模板变量列表
            List<PosterVariableDTO> variableList = posterTemplate.posterVariableList();
            // 如果变量列表为空，则直接执行图片生成
            if (CollectionUtil.isEmpty(variableList)) {
                posterTemplate.setIsExecute(Boolean.TRUE);
                continue;
            }

            // 获取所有变量的uuid和value并且放到此map中
            Map<String, Object> variableMap = getPosterVariableMap(posterTemplate, Boolean.FALSE);
            // 替换变量，未找到的占位符会被替换为空字符串
            Map<String, Object> replaceValueMap = context.parseMapFromVariables(variableMap, stepCode);
            // 如果需要进行多模态生成标题，则进行多模态处理，这里是只有改值为 true 的时候才会进行多模态处理。
            if (isNeedMultimodal(posterTemplate)) {
                PosterTitleDTO posterTitle = multimodalTitle(context, posterTemplate, replaceValueMap);
                Map<String, Object> multimodalMap = new HashMap<>();
                multimodalMap.put("图片标题", posterTitle.getImgTitle());
                multimodalMap.put("图片副标题", posterTitle.getImgSubTitle());
                context.putVariableForce("AI分析", multimodalMap);
                // 重新获取变量值，包含多模态处理生成的标题
                variableMap = getPosterVariableMap(posterTemplate, Boolean.TRUE);
                // 重新进行一次变量替换
                replaceValueMap = context.parseMapFromVariables(variableMap, stepCode);
            }

            // 循环处理变量列表，进行值填充
            for (PosterVariableDTO variable : variableList) {
                // 从作用域数据中获取变量值
                Object value = replaceValueMap.get(variable.getUuid());
                // 如果从作用域数据中获取的变量值为空，则为空字符串。
                if (StringUtil.objectBlank(value)) {
                    value = StrUtil.EMPTY;
                }
                variable.setValue(value);
            }
            posterTemplate.setIsExecute(Boolean.TRUE);

            // 判断变量替换之后，是否所有的变量都是空的。
            boolean isAllValueIsInvalid = MapUtil.emptyIfNull(replaceValueMap)
                    .values().stream().allMatch(item -> StringUtil.objectBlank(item));

            // 如果所有变量处理之后值为空
            if (isAllValueIsInvalid) {
                // 如果设置所有变量为空报错(null/false)，则报错!
                if (Objects.isNull(posterStyle.getNoExecuteIfEmpty()) || !posterStyle.getNoExecuteIfEmpty()) {
                    throw ServiceExceptionUtil.invalidParamException(
                            "【{}】步骤执行失败:[{}][{}] 变量值都为空。或者占位符无法替换，请检查您的图片变量配置！",
                            context.getStepId(), posterStyle.getName(), posterTemplate.getName());
                }
                // 如果设置所有变量为空(true)不执行，则不执行
                else {
                    posterTemplate.setIsExecute(Boolean.FALSE);
                    continue;
                }
            }

            posterTemplate.setVariableList(variableList);
        }

        posterStyle.setTemplateList(posterTemplateList);
    }

    /**
     * 将不依赖结果的模板结果放入到全局上下文中
     *
     * @param style                海报风格
     * @param undependencyResponse 不依赖结果的模板结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private void putNoDependencyResultContext(AppContext context, PosterStyleDTO style, List<PosterGenerationHandler.Response> undependencyResponse) {
        // 将不依赖结果的模板结果放入到全局上下文中。保证 undependencyResponse 和 templateList 顺序一致
        Map<String, Object> posterResult = new HashMap<>();
        List<PosterGenerationHandler.Response> responseList = SerializationUtils.clone(new ArrayList<>(undependencyResponse));
        List<PosterTemplateDTO> templateList = style.posterTemplateList();
        for (int i = 0; i < templateList.size(); i++) {
            PosterTemplateDTO template = templateList.get(i);
            if (i == 0) {
                if (!template.getIsDependency()) {
                    String url = CollectionUtil.emptyIfNull(responseList.remove(0).getUrlList())
                            .stream()
                            .map(PosterImage::getUrl)
                            .findFirst().get();
                    posterResult.put(MAIN_IMAGE, url);
                } else {
                    posterResult.put(MAIN_IMAGE, null);
                }
                continue;
            }
            if (!template.getIsDependency()) {
                String url = CollectionUtil.emptyIfNull(responseList.remove(0).getUrlList())
                        .stream()
                        .map(PosterImage::getUrl)
                        .findFirst().get();
                posterResult.put(IMAGE + i, url);
            } else {
                posterResult.put(IMAGE + i, null);
            }
        }
        ActionResponse response = new ActionResponse();
        response.setSuccess(Boolean.TRUE);
        response.setType(AppStepResponseTypeEnum.JSON.name());
        response.setOutput(JsonData.of(posterResult));
        context.setActionResponse(response);
    }

    /**
     * 处理所有的结果
     *
     * @param style                海报风格
     * @param dependencyResponse   依赖结果的模板结果
     * @param undependencyResponse 不依赖结果的模板结果
     * @return 返回结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private List<PosterGenerationHandler.Response> handlerAllResponse(PosterStyleDTO style, List<PosterGenerationHandler.Response> dependencyResponse, List<PosterGenerationHandler.Response> undependencyResponse) {
        // 合并结果，需要和 style 的 templateList 顺序一致
        List<PosterGenerationHandler.Response> list = new ArrayList<>();
        List<PosterGenerationHandler.Response> dependencyList = SerializationUtils.clone(new ArrayList<>(dependencyResponse));
        List<PosterGenerationHandler.Response> undependencyList = SerializationUtils.clone(new ArrayList<>(undependencyResponse));
        for (PosterTemplateDTO template : style.posterTemplateList()) {
            if (template.getIsDependency()) {
                list.add(dependencyList.remove(0));
            } else {
                list.add(undependencyList.remove(0));
            }
        }
        return list;
    }

    /**
     * 执行成功返回结果
     *
     * @param list  结果列表
     * @param style 海报风格
     * @return 返回结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private ActionResponse successResponse(List<PosterGenerationHandler.Response> list, PosterStyleDTO style) {
        // 执行成功，构造返回结果
        ActionResponse response = new ActionResponse();
        response.setSuccess(Boolean.TRUE);
        response.setType(AppStepResponseTypeEnum.JSON.name());
        response.setIsShow(Boolean.TRUE);
        response.setStepConfig(JsonUtils.toJsonString(style));
        response.setMessage(" ");
        response.setAnswer(JsonUtils.toJsonPrettyString(list));
        response.setOutput(JsonData.of(list));
        response.setCostPoints(list.size());
        return response;
    }

    /**
     * 批量执行海报生成
     *
     * @param posterTemplateList 海报模版列表
     * @return 生成结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private List<PosterGenerationHandler.Response> batchPoster(AppContext context, PosterStyleDTO posterStyle, boolean isDependencyResult) {
        // 组装海报风格参数
        assemble(context, posterStyle, isDependencyResult);
        // 现获取到依赖结果的模板列表
        List<PosterTemplateDTO> templateList = getPosterTemplateList(posterStyle, isDependencyResult);
        // 执行依赖结果的模板列表
        return doBatchPoster(context, templateList);
    }

    /**
     * 批量执行海报生成
     *
     * @param posterTemplateList 海报模版列表
     * @return 生成结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private List<PosterGenerationHandler.Response> doBatchPoster(AppContext context, List<PosterTemplateDTO> posterTemplateList) {
        // 如果海报模版列表为空，则返回空列表
        if (CollectionUtil.isEmpty(posterTemplateList)) {
            return Collections.emptyList();
        }
        // 获取线程池
        ThreadPoolExecutor executor = POSTER_TEMPLATE_THREAD_POOL_HOLDER.executor();
        // 任务列表，只执行需要执行的图片，isExecute 为空或者为true，都执行，为false则不需要执行改图片
        List<CompletableFuture<HandlerResponse<PosterGenerationHandler.Response>>> futureList = CollectionUtil.emptyIfNull(posterTemplateList).stream()
                .map(item -> CompletableFuture.supplyAsync(() -> poster(context, item), executor)).collect(Collectors.toList());

        // 任务合并
        CompletableFuture<List<HandlerResponse<PosterGenerationHandler.Response>>> allFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]))
                .thenApply(v -> futureList.stream().map(CompletableFuture::join).collect(Collectors.toList()));

        // 获取结果
        List<HandlerResponse<PosterGenerationHandler.Response>> handlerResponseList = allFuture.join();

        // 如果有一个失败，则返回失败
        Optional<HandlerResponse<PosterGenerationHandler.Response>> failureOption = handlerResponseList.stream()
                .filter(Objects::nonNull).filter(item -> !item.getSuccess()).findFirst();

        if (failureOption.isPresent()) {
            HandlerResponse<PosterGenerationHandler.Response> failure = failureOption.get();
            throw ServiceExceptionUtil.exception(new ErrorCode(failure.getErrorCode(), failure.getErrorMsg()));
        }

        // 构建响应结果
        return handlerResponseList.stream().filter(Objects::nonNull)
                .map(HandlerResponse::getOutput)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 生成海报图片
     *
     * @param posterTemplate 海报模版
     * @return 生成结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private HandlerResponse<PosterGenerationHandler.Response> poster(AppContext context, PosterTemplateDTO posterTemplate) {
        try {
            // 构建请求
            PosterGenerationHandler.Request handlerRequest = new PosterGenerationHandler.Request();
            handlerRequest.setCode(posterTemplate.getCode());
            handlerRequest.setName(posterTemplate.getName());
            handlerRequest.setIsMain(posterTemplate.getIsMain());
            handlerRequest.setIndex(posterTemplate.getIndex());
            Map<String, Object> params = CollectionUtil.emptyIfNull(posterTemplate.getVariableList()).stream()
                    .collect(Collectors.toMap(PosterVariableDTO::getField, PosterVariableDTO::emptyIfNullValue));
            handlerRequest.setParams(params);

            // 构建请求
            HandlerContext<PosterGenerationHandler.Request> handlerContext = HandlerContext.createContext(
                    context.getUid(),
                    context.getConversationUid(),
                    context.getUserId(),
                    context.getEndUserId(),
                    context.getScene(),
                    handlerRequest
            );
            PosterGenerationHandler handler = new PosterGenerationHandler();
            return handler.execute(handlerContext);
        } catch (ServiceException exception) {
            HandlerResponse<PosterGenerationHandler.Response> handlerResponse = new HandlerResponse<>();
            handlerResponse.setSuccess(Boolean.FALSE);
            handlerResponse.setErrorCode(exception.getCode());
            handlerResponse.setErrorMsg(exception.getMessage());
            return handlerResponse;
        } catch (Exception exception) {
            HandlerResponse<PosterGenerationHandler.Response> handlerResponse = new HandlerResponse<>();
            handlerResponse.setSuccess(Boolean.FALSE);
            handlerResponse.setErrorCode(350400200);
            handlerResponse.setErrorMsg(exception.getMessage());
            return handlerResponse;
        }
    }

    /**
     * 多模态处理
     *
     * @param posterTemplate 海报模版
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public PosterTitleDTO multimodalTitle(AppContext context, PosterTemplateDTO posterTemplate, Map<String, Object> valueMap) {
        try {
            log.info("通义千问多模态【执行开始】: 海报模板：{}: {}", posterTemplate.getName(), posterTemplate.getCode());
            // 获取变量值
            Map<String, Object> params = context.getContextVariablesValues();

            /*
             * 构建多模态执行参数，并且执行
             */
            // 构建消息列表
            List<Map<String, Object>> messages = new ArrayList<>();
            // 获取提示词
            String prompt = String.valueOf(params.getOrDefault("PROMPT", "图片上画了什么？"));
            messages.add(Collections.singletonMap(MultiModalMessage.MESSAGE_TEXT_KEY, prompt));
            // 处理需要上传的图片
            List<String> urlList = imageMessageList(posterTemplate, valueMap);
            for (String imageUrl : urlList) {
                messages.add(Collections.singletonMap(MultiModalMessage.MESSAGE_IMAGE_KEY, imageUrl));
            }
            // 调用通义千问VL模型
            log.info("通义千问多模态【调用模型】: 执行参数: \n{}", JsonUtils.toJsonPrettyString(messages));
            HumanMessage humanMessage = new HumanMessage(messages);
            ChatVLQwen chatVLQwen = new ChatVLQwen();
            String call = chatVLQwen.call(Arrays.asList(humanMessage));

            // 判断结果是否为空
            if (StrUtil.isBlank(call)) {
                throw ServiceExceptionUtil.exception0(ErrorCodeConstants.EXECUTE_POSTER_FAILURE.getCode(),
                        "海报生成：AI分析多模态生成执行失败：响应结果为空！");
            }

            /*
             * 解析结果
             */
            // 解析结果
            if (!call.contains("标题") || !call.contains("副标题")) {
                throw ServiceExceptionUtil.exception0(ErrorCodeConstants.EXECUTE_POSTER_FAILURE.getCode(),
                        "海报生成：AI分析多模态生成执行失败：响应结果格式不正确！");
            }

            Integer titleIndex = call.indexOf("标题");
            Integer subTitleIndex = call.indexOf("副标题");
            String title = call.substring(titleIndex + 3, subTitleIndex).trim();
            String subTitle = call.substring(subTitleIndex + 4).trim();

            PosterTitleDTO posterTitle = new PosterTitleDTO();
            posterTitle.setImgTitle(title);
            posterTitle.setImgSubTitle(subTitle);
            log.info("通义千问多模态【执行结束】: 海报模板：{}: {}, 执行结果: \n{}",
                    posterTemplate.getName(), posterTemplate.getCode(), JsonUtils.toJsonPrettyString(posterTitle));

            return posterTitle;
        } catch (ServiceException exception) {
            log.error("通义千问多模态【执行失败】: 海报模板：{}: {}, 、\n\t错误码: {}, 错误信息: {}",
                    posterTemplate.getName(), posterTemplate.getCode(), exception.getCode(), exception.getMessage());
            throw exception;
        } catch (Exception exception) {
            log.error("通义千问多模态【执行失败】: 海报模板：{}: {}, 、\n\t错误信息: {}",
                    posterTemplate.getName(), posterTemplate.getCode(), exception.getMessage());
            throw ServiceExceptionUtil.exceptionWithCause(ErrorCodeConstants.EXECUTE_POSTER_FAILURE, exception.getMessage(), exception);
        }
    }

    /**
     * 获取模板变量集合，变量 UUID 和 value 的Map集合
     *
     * @param posterTemplateList 模板列表
     * @return 模板变量集合
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private static Map<String, Object> getPosterVariableMap(PosterTemplateDTO posterTemplate, boolean isIncludeMultimodal) {
        Map<String, Object> variableMap = new HashMap<>();
        for (PosterVariableDTO variable : posterTemplate.posterVariableList()) {
            // 如果不包含多模态处理生成标题，则不包含多模态处理生成标题的变量
            if (!isIncludeMultimodal) {
                if (MULTIMODAL_PATTERN.matcher(variable.emptyIfNullValue()).find()) {
                    continue;
                }
            }
            String value = variable.emptyIfNullValue();
            variableMap.put(variable.getUuid(), value);
        }
        return variableMap;
    }

    /**
     * 获取模板变量集合，变量 UUID 和 value 的Map集合
     *
     * @param posterTemplateList 模板列表
     * @return 模板变量集合
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private static Map<String, Object> getDocPosterVariableMap(PosterTemplateDTO posterTemplate) {
        Map<String, Object> variableMap = new HashMap<>();
        for (PosterVariableDTO variable : posterTemplate.posterVariableList()) {
            // 只过滤出素材变量，其他变量不处理，等待后续处理
            if (!MATERIAL_PATTERN.matcher(variable.emptyIfNullValue()).find()) {
                continue;
            }
            String value = variable.emptyIfNullValue();
            variableMap.put(variable.getUuid(), value);
        }
        return variableMap;
    }

    /**
     * 是否需要多模态处理生成标题
     *
     * @param posterTemplate 海报模版
     * @return 是否需要多模态处理生成标题
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private static boolean isNeedMultimodal(PosterTemplateDTO posterTemplate) {
        // 如果需要多模态处理生成标题，只要该值为 false，则不需要多模态处理。该值为 true，且变量中有匹配到多模态正则表达式，才需要多模态处理
        if (Objects.nonNull(posterTemplate.getIsMultimodalTitle()) && posterTemplate.getIsMultimodalTitle()) {
            // 获取所有的变量列表
            List<PosterVariableDTO> variableList = posterTemplate.posterVariableList();
            // 只要有一个变量匹配到多模态正则表达式，则需要多模态处理
            return variableList.stream().anyMatch(item -> MULTIMODAL_PATTERN.matcher(item.emptyIfNullValue()).find());
        }
        return false;
    }

    /**
     * 获取多模态参数图片消息列表
     *
     * @param posterTemplate 海报模版
     * @return 图片消息列表
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private static List<String> imageMessageList(PosterTemplateDTO posterTemplate, Map<String, Object> valueMap) {
        // 图片变量列表
        List<PosterVariableDTO> imageVariableList = CreativeUtils.getImageVariableList(posterTemplate);
        // 处理需要上传的图片
        List<String> urlList = new ArrayList<>();
        // 如果图片数量大于2，只取前2个，否则取全部
        for (PosterVariableDTO imageVariable : imageVariableList) {
            if (urlList.size() == 2) {
                break;
            }
            // 获取图片地址
            String value = String.valueOf(valueMap.getOrDefault(imageVariable.getUuid(), StrUtil.EMPTY));
            if (StringUtils.isBlank(value)) {
                continue;
            }
            String imageUrl = value + IMAGE_URL_LIMIT_PIXEL;
            urlList.add(imageUrl);
        }
        return urlList;
    }

    /**
     * 获取到 [n] 中的数字，如果包含多个 [n],只返回最大的数字
     *
     * @param input 输入
     * @return 返回的数字，没有匹配到，返回 -1
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private static Integer matcherMax(String input) {
        if (StringUtils.isBlank(input)) {
            return -1;
        }
        // 定义正则表达式匹配方括号中的数字
        Matcher matcher = MATERIAL_PATTERN.matcher(input);
        // 如果匹配到，则返回最大的数字。
        int max = -1;
        try {
            while (matcher.find()) {
                int number = Integer.parseInt(matcher.group(1));
                if (number > max) {
                    max = number;
                }
            }
        } catch (Exception exception) {
            log.error("{}解析素材正则异常：{}: {}", PosterActionHandler.class.getSimpleName(), input, exception.getMessage());
            return -1;
        }
        return max;
    }

}
