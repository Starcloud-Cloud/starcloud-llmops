package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
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
import com.starcloud.ops.business.app.api.xhs.plan.dto.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.poster.PosterTemplateDTO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.poster.PosterVariableDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.PosterTitleDTO;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.domain.handler.poster.PosterGenerationHandler;
import com.starcloud.ops.business.app.enums.app.AppStepResponseTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialFieldTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.poster.PosterTitleModeEnum;
import com.starcloud.ops.business.app.service.xhs.executor.PosterThreadPoolHolder;
import com.starcloud.ops.business.app.util.CreativeUtils;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import com.starcloud.ops.llm.langchain.core.model.multimodal.qwen.ChatVLQwen;
import com.starcloud.ops.llm.langchain.core.schema.message.multimodal.HumanMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.multimodal.MultiModalMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
     * 获取用户权益类型
     *
     * @return 权益类型
     */
    @Override
    protected AdminUserRightsTypeEnum getUserRightsType() {
        return AdminUserRightsTypeEnum.MAGIC_IMAGE;
    }

    /**
     * 暂时不返回任何结构
     *
     * @return
     */
    @Override
    public JsonSchema getOutVariableJsonSchema(WorkflowStepWrapper workflowStepWrapper) {
        ObjectSchema objectSchema = new ObjectSchema();
        objectSchema.setTitle(workflowStepWrapper.getStepCode());
        objectSchema.setDescription(workflowStepWrapper.getDescription());
        objectSchema.setId(workflowStepWrapper.getFlowStep().getHandler());

        for (int i = 0; i < 10; i++) {
            if (i == 0) {
                StringSchema schema = new StringSchema();
                schema.setTitle("首图");
                schema.setDescription("首图-" + MaterialFieldTypeEnum.image.getCode());
                objectSchema.putProperty("首图", schema);
                continue;
            }
            StringSchema schema = new StringSchema();
            schema.setTitle("图片" + i);
            schema.setDescription("图片" + i + "-" + MaterialFieldTypeEnum.image.getCode());
            objectSchema.putProperty("图片" + i, schema);
        }
        return objectSchema;
    }

    /**
     * 执行OpenApi生成的步骤
     *
     * @return 执行结果
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected ActionResponse doExecute() {
        try {
            log.info("海报生成 Action 执行开始......");

            /*
             * 获取到待执行的海报风格，并进行校验
             */
            // 获取海报风格
            PosterStyleDTO style = getPosterStyle();
            // 校验海报模版
            style.validate();

            /*
             * 对风格的模板进行标记处理
             * 是否依赖别的模板的生成结果
             * 只做标记和必要的校验，不进行参数的填充。
             */
            markerDependencyTemplate(style);

            /*
             * 执行不依赖结果的海报模板，并且获取到结果
             */
            List<PosterGenerationHandler.Response> undependencyResponse = batchPoster(style, Boolean.FALSE);

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
            putNoDependencyResultContext(style, undependencyResponse);
            // 执行依赖结果的模板列表
            List<PosterGenerationHandler.Response> dependencyResponse = batchPoster(style, Boolean.TRUE);
            // 对最终结果进行处理，合并，排序
            List<PosterGenerationHandler.Response> list = handlerAllResponse(style, dependencyResponse, undependencyResponse);
            // 处理并且返回结果
            return successResponse(list, style);
        } catch (ServiceException exception) {
            throw exception;
        } catch (Exception exception) {
            throw ServiceExceptionUtil.exception(new ErrorCode(350400200, "海报生成失败：" + exception.getMessage()));
        }
    }

    /**
     * 获取海报风格
     *
     * @return 海报风格
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private PosterStyleDTO getPosterStyle() {
        // 海报模版参数
        String posterStyle = String.valueOf(this.getAppContext().getContextVariablesValue(CreativeConstants.POSTER_STYLE, Boolean.FALSE));
        if (StringUtils.isBlank(posterStyle) || "null".equalsIgnoreCase(posterStyle) || "{}".equals(posterStyle)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(350400200,
                    "海报校验失败：海报风格配置为空，请检查您的图片配置或联系管理员！"));
        }
        // 转为海报模版对象
        try {
            PosterStyleDTO style = JsonUtils.parseObject(posterStyle, PosterStyleDTO.class);
            if (Objects.isNull(style)) {
                throw ServiceExceptionUtil.exception(new ErrorCode(350400200,
                        "海报校验失败：海报风格配置为空，请检查您的图片配置或联系管理员！"));
            }
            return style;
        } catch (Exception e) {
            throw ServiceExceptionUtil.exception(new ErrorCode(350400200,
                    "海报校验失败：海报风格配置解析失败，请检查您的图片配置或联系管理员！"));
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
        return style.getPosterTemplateList().stream()
                .anyMatch(PosterTemplateDTO::getIsDependency);
    }

    /**
     * 标记依赖模板，仅仅做标记，不进行处理
     *
     * @param posterStyle 海报风格
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private void markerDependencyTemplate(PosterStyleDTO posterStyle) {
        // 校验海报风格模板
        List<PosterTemplateDTO> templateList = posterStyle.getPosterTemplateList();

        for (PosterTemplateDTO posterTemplate : templateList) {
            List<PosterVariableDTO> variableList = posterTemplate.getPosterVariableList();
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
                Pattern pattern = Pattern.compile(this.getAppContext().getStepWrapper().getStepCode() + "\\.(首图|图片\\d)");
                Matcher matcher = pattern.matcher(value);
                // 如果匹配到，则设置为依赖，并且跳出变量列表循环，否则抛出异常
                boolean matched = false;
                while (matcher.find()) {
                    // 提取出匹配到的数字
                    String group = matcher.group();
                    Integer dependencyIndex = Integer.parseInt(group.substring(group.length() - 1));
                    if (dependencyIndex > templateList.size()) {
                        throw ServiceExceptionUtil.exception(new ErrorCode(350400200,
                                "海报校验失败：[" + posterStyle.getName() + "][" +
                                        posterTemplate.getName() + "][" +
                                        variable.getLabel() + "] " +
                                        "变量依赖的模板生成结果超出模板数量，经检查您的模板变量配置！"));
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
            throw ServiceExceptionUtil.exception(new ErrorCode(350400200,
                    "海报校验失败：[" + posterStyle.getName() + "]" +
                            "所有的海报模板均有依赖其他海报模板生成结果的变量，导致无法进行图片生成，请检查您的海报模板变量配置！"));
        }
        posterStyle.setTemplateList(templateList);
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
        return posterStyle.getPosterTemplateList().stream()
                .filter(item -> item.getIsDependency().equals(isDependencyResult))
                .filter(item -> Objects.isNull(item.getIsExecute()) || item.getIsExecute())
                .collect(Collectors.toList());
    }

    /**
     * 海报风格参数填充
     *
     * @param posterStyle        海报风格
     * @param isDependencyResult 是否依赖结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private void assemble(PosterStyleDTO posterStyle, boolean isDependencyResult) {
        // 获取海报模版列表
        List<PosterTemplateDTO> posterTemplateList = posterStyle.getPosterTemplateList();

        // 循环处理海报模板列表
        for (PosterTemplateDTO posterTemplate : posterTemplateList) {
            // 如果 isDependencyResult 和 isDependency 不一致，则跳过, 不进行处理
            if (!posterTemplate.getIsDependency().equals(isDependencyResult)) {
                continue;
            }

            // 获取海报模板变量列表
            List<PosterVariableDTO> variableList = posterTemplate.getPosterVariableList();
            // 如果变量列表为空，则直接执行图片生成
            if (CollectionUtil.isEmpty(variableList)) {
                posterTemplate.setIsExecute(Boolean.TRUE);
                continue;
            }

            // 获取所有变量的uuid和value并且放到此map中
            Map<String, Object> variableMap = CreativeUtils.getPosterVariableMap(variableList);
            // 替换变量，未找到的占位符会被替换为空字符串
            Map<String, Object> replaceValueMap = this.getAppContext().parseMapFromVariables(variableMap, this.getAppContext().getStepId());
            // 判断变量替换之后，是否所有的变量都是空的。
            boolean isAllValueIsInvalid = MapUtil.emptyIfNull(replaceValueMap).values().stream()
                    .allMatch(item -> StringUtil.objectBlank(item));

            // 如果所有变量处理之后值为空
            if (isAllValueIsInvalid) {
                // 如果设置所有变量为空报错(null/false)，则报错!
                if (Objects.isNull(posterStyle.getNoExecuteIfEmpty()) || !posterStyle.getNoExecuteIfEmpty()) {
                    throw ServiceExceptionUtil.exception(new ErrorCode(350400200,
                            "海报校验失败：[" + posterStyle.getName() +
                                    "][" + posterTemplate.getName() +
                                    "] 变量值都为空。或者占位符无法替换，请检查您的图片变量配置！"));
                }
                // 如果设置所有变量为空(true)不执行，则不执行
                else {
                    posterTemplate.setIsExecute(Boolean.FALSE);
                    continue;
                }
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
    private void putNoDependencyResultContext(PosterStyleDTO style, List<PosterGenerationHandler.Response> undependencyResponse) {
        // 将不依赖结果的模板结果放入到全局上下文中。保证 undependencyResponse 和 templateList 顺序一致
        Map<String, Object> posterResult = new HashMap<>();
        List<PosterGenerationHandler.Response> responseList = SerializationUtils.clone(new ArrayList<>(undependencyResponse));
        List<PosterTemplateDTO> templateList = style.getPosterTemplateList();
        for (int i = 0; i < templateList.size(); i++) {
            PosterTemplateDTO template = templateList.get(i);
            if (i == 0) {
                if (!template.getIsDependency()) {
                    posterResult.put("首图", responseList.remove(0).getUrl());
                } else {
                    posterResult.put("首图", null);
                }
                continue;
            }
            if (!template.getIsDependency()) {
                posterResult.put("图片" + i, responseList.remove(0).getUrl());
            } else {
                posterResult.put("图片" + i, null);
            }
        }
        ActionResponse response = new ActionResponse();
        response.setSuccess(Boolean.TRUE);
        response.setType(AppStepResponseTypeEnum.JSON.name());
        response.setOutput(JsonData.of(posterResult));
        this.getAppContext().setActionResponse(response);
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
    private List<PosterGenerationHandler.Response> handlerAllResponse(PosterStyleDTO style,
                                                                      List<PosterGenerationHandler.Response> dependencyResponse,
                                                                      List<PosterGenerationHandler.Response> undependencyResponse) {
        // 合并结果，需要和 style 的 templateList 顺序一致
        List<PosterGenerationHandler.Response> list = new ArrayList<>();
        List<PosterGenerationHandler.Response> dependencyList = SerializationUtils.clone(new ArrayList<>(dependencyResponse));
        List<PosterGenerationHandler.Response> undependencyList = SerializationUtils.clone(new ArrayList<>(undependencyResponse));
        for (PosterTemplateDTO template : style.getPosterTemplateList()) {
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
        response.setMessage(JsonUtils.toJsonString(style));
        response.setAnswer(JsonUtils.toJsonPrettyString(list));
        response.setOutput(JsonData.of(list));
        response.setCostPoints(list.size());
        response.setAiModel(null);
        log.info("海报生成 Action 执行结束......");
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
    private List<PosterGenerationHandler.Response> batchPoster(PosterStyleDTO posterStyle, boolean isDependencyResult) {
        // 组装海报风格参数
        assemble(posterStyle, isDependencyResult);
        // 现获取到依赖结果的模板列表
        List<PosterTemplateDTO> templateList = getPosterTemplateList(posterStyle, isDependencyResult);
        // 执行依赖结果的模板列表
        return doBatchPoster(templateList);
    }

    /**
     * 批量执行海报生成
     *
     * @param posterTemplateList 海报模版列表
     * @return 生成结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private List<PosterGenerationHandler.Response> doBatchPoster(List<PosterTemplateDTO> posterTemplateList) {
        // 如果海报模版列表为空，则返回空列表
        if (CollectionUtil.isEmpty(posterTemplateList)) {
            return Collections.emptyList();
        }
        // 获取线程池
        ThreadPoolExecutor executor = POSTER_TEMPLATE_THREAD_POOL_HOLDER.executor();
        // 任务列表，只执行需要执行的图片，isExecute 为空或者为true，都执行，为false则不需要执行改图片
        List<CompletableFuture<HandlerResponse<PosterGenerationHandler.Response>>> futureList = CollectionUtil.emptyIfNull(posterTemplateList)
                .stream()
                .map(item -> CompletableFuture.supplyAsync(() -> poster(item), executor))
                .collect(Collectors.toList());

        // 任务合并
        CompletableFuture<List<HandlerResponse<PosterGenerationHandler.Response>>> allFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]))
                .thenApply(v -> futureList.stream().map(CompletableFuture::join).collect(Collectors.toList()));

        // 获取结果
        List<HandlerResponse<PosterGenerationHandler.Response>> handlerResponseList = allFuture.join();

        // 如果有一个失败，则返回失败
        Optional<HandlerResponse<PosterGenerationHandler.Response>> failureOption = handlerResponseList.stream()
                .filter(Objects::nonNull)
                .filter(item -> !item.getSuccess())
                .findFirst();

        if (failureOption.isPresent()) {
            throw ServiceExceptionUtil.exception(new ErrorCode(failureOption.get().getErrorCode(), failureOption.get().getErrorMsg()));
        }

        // 构建响应结果
        return handlerResponseList.stream()
                .filter(Objects::nonNull)
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
    private HandlerResponse<PosterGenerationHandler.Response> poster(PosterTemplateDTO posterTemplate) {
        try {
            // 构建请求
            PosterGenerationHandler.Request handlerRequest = new PosterGenerationHandler.Request();
            handlerRequest.setCode(posterTemplate.getCode());
            handlerRequest.setName(posterTemplate.getName());
            handlerRequest.setIsMain(posterTemplate.getIsMain());
            handlerRequest.setIndex(posterTemplate.getIndex());
            Map<String, Object> params = CollectionUtil.emptyIfNull(posterTemplate.getVariableList())
                    .stream()
                    .collect(Collectors.toMap(
                            PosterVariableDTO::getField,
                            // 如果变量为值为空，则设置为空字符串
                            PosterVariableDTO::emptyIfNullValue)
                    );

            handlerRequest.setParams(params);

            // 构建请求
            HandlerContext<PosterGenerationHandler.Request> handlerContext = HandlerContext.createContext(
                    this.getAppUid(),
                    this.getAppContext().getConversationUid(),
                    this.getAppContext().getUserId(),
                    this.getAppContext().getEndUserId(),
                    this.getAppContext().getScene(),
                    handlerRequest
            );
            PosterGenerationHandler handler = new PosterGenerationHandler();
            return handler.execute(handlerContext);
        } catch (ServiceException exception) {
            log.info("海报图片生成: 生成图片失败(ServiceException): 错误码：{}，错误信息：{}", exception.getCode(), exception.getMessage());
            HandlerResponse<PosterGenerationHandler.Response> handlerResponse = new HandlerResponse<>();
            handlerResponse.setSuccess(Boolean.FALSE);
            handlerResponse.setErrorCode(exception.getCode());
            handlerResponse.setErrorMsg(exception.getMessage());
            return handlerResponse;
        } catch (Exception exception) {
            log.info("海报图片生成: 生成图片失败(Exception): 错误码：{}，错误信息：{}", 350400200, exception.getMessage());
            HandlerResponse<PosterGenerationHandler.Response> handlerResponse = new HandlerResponse<>();
            handlerResponse.setSuccess(Boolean.FALSE);
            handlerResponse.setErrorCode(350400200);
            handlerResponse.setErrorMsg(exception.getMessage());
            return handlerResponse;
        }
    }

    /**
     * 处理图片标题
     *
     * @param posterStyle 海报风格
     * @param title       文案标题
     * @param content     文案内容
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private void handlerPosterTitle(PosterStyleDTO posterStyle, String title, String content) {
        List<PosterTemplateDTO> templateList = CollectionUtil.emptyIfNull(posterStyle.getTemplateList());

        // 循环处理
        for (PosterTemplateDTO posterTemplate : templateList) {
            // 默认模式生成
            String titleGenerateMode = Optional.ofNullable(posterTemplate.getTitleGenerateMode()).orElse(PosterTitleModeEnum.DEFAULT.name());
            if (PosterTitleModeEnum.DEFAULT.name().equals(titleGenerateMode)) {
                List<PosterVariableDTO> variableList = posterTemplate.getVariableList();
                for (PosterVariableDTO variable : variableList) {
                    // 获取变量值，不存在取默认值，默认值不存在，为空字符串
                    Object value = Objects.nonNull(variable.getValue()) ? variable.getValue() :
                            Objects.nonNull(variable.getDefaultValue()) ? variable.getDefaultValue() : "";

                    // 从变量缓存中获取变量值
                    Map<String, Object> objectMap = this.getAppContext().getContextVariablesValues(MaterialActionHandler.class);

                    //素材.docs[1].url
                    Object replaceValue = objectMap.getOrDefault(value, value);
                    variable.setValue(replaceValue);
                }
                posterTemplate.setVariableList(variableList);

            } else if (PosterTitleModeEnum.AI.name().equals(titleGenerateMode)) {
                this.getAppContext().putVariable(CreativeConstants.TITLE, title);
                this.getAppContext().putVariable(CreativeConstants.CONTENT, content);
                this.getAppContext().putVariable(CreativeConstants.REQUIREMENT, posterTemplate.getTitleGenerateRequirement());
                // 执行多模态生成海报标题副标题
                MultimodalPosterTitleResponse response = multimodalPosterTitle(posterTemplate);
                if (!response.getSuccess()) {
                    throw ServiceExceptionUtil.exception(new ErrorCode(350400200, response.errorMessage));
                }
                // 获取结果，并且进行变量替换
                PosterTitleDTO posterTitle = response.getPosterTitle();

                // 变量替换
                List<PosterVariableDTO> variableList = posterTemplate.getVariableList();
                posterTemplate.setVariableList(variableList);
            } else {
                log.error("不支持的图片标题生成模式: {}", titleGenerateMode);
                throw ServiceExceptionUtil.exception(new ErrorCode(350400200, "不支持的图片标题生成模式: " + titleGenerateMode));
            }
        }
        posterStyle.setTemplateList(templateList);
    }

    /**
     * 多模态处理
     *
     * @param posterTemplate 海报模版
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public MultimodalPosterTitleResponse multimodalPosterTitle(PosterTemplateDTO posterTemplate) {
        try {
            // 获取变量值
            Map<String, Object> variablesValues = this.getAppContext().getContextVariablesValues();

            // 构建消息列表
            List<Map<String, Object>> messages = new ArrayList<>();

            // 获取标题提示
            String prompt = String.valueOf(variablesValues.getOrDefault("PROMPT", "图片上画了什么？"));
            messages.add(Collections.singletonMap(MultiModalMessage.MESSAGE_TEXT_KEY, prompt));

            // 图片变量列表
            List<PosterVariableDTO> imageVariableList = CollectionUtil.emptyIfNull(posterTemplate.getVariableList()).stream()
                    .filter(item -> "IMAGE".equals(item.getType())).collect(Collectors.toList());
            // 处理需要上传的图片
            List<String> urlList = new ArrayList<>();
            // 如果图片数量大于2，只取前2个，否则取全部
            for (int i = 0; i < imageVariableList.size(); i++) {
                if (i > 1) {
                    break;
                }
                PosterVariableDTO imageVariable = imageVariableList.get(i);
                Object value = imageVariable.getValue();
                if (Objects.isNull(value)) {
                    continue;
                }
                String imageUrl = String.valueOf(value) + IMAGE_URL_LIMIT_PIXEL;
                urlList.add(imageUrl);
                messages.add(Collections.singletonMap(MultiModalMessage.MESSAGE_IMAGE_KEY, imageUrl));
            }

            // 调用通义千问VL模型
            HumanMessage humanMessage = new HumanMessage(messages);
            ChatVLQwen chatVLQwen = new ChatVLQwen();
            String call = chatVLQwen.call(Arrays.asList(humanMessage));

            if (log.isDebugEnabled()) {
                log.info("通义千问多模态执行结果: {}", call);
            }

            // 判断结果是否为空
            if (StrUtil.isBlank(call)) {
                log.error("通义千问多模态执行失败：标题生成结果为空");
                return MultimodalPosterTitleResponse.failure("350400200", "标题生成结果为空！", prompt, urlList);
            }

            // 解析结果
            if (!call.contains("标题") || !call.contains("副标题")) {
                return MultimodalPosterTitleResponse.failure("350400200", "标题生成格式不正确！", prompt, urlList);
            }
            Integer titleIndex = call.indexOf("标题");
            Integer subTitleIndex = call.indexOf("副标题");
            String title = call.substring(titleIndex + 3, subTitleIndex).trim();
            String subTitle = call.substring(subTitleIndex + 4).trim();

            PosterTitleDTO posterTitle = new PosterTitleDTO();
            posterTitle.setImgTitle(title);
            posterTitle.setImgSubTitle(subTitle);

            // 构建结果并且返回
            MultimodalPosterTitleResponse response = new MultimodalPosterTitleResponse();
            response.setSuccess(Boolean.TRUE);
            response.setPrompt(prompt);
            response.setUrlList(urlList);
            response.setCostPoints(1);
            response.setPosterTitle(posterTitle);

            return response;
        } catch (Exception exception) {
            log.error("通义千问多模态执行失败：{}", exception.getMessage());
            return MultimodalPosterTitleResponse.failure("350400200", exception.getMessage());
        }
    }

    /**
     * 多模态生成标题返回结果
     */
    @Data
    @ToString
    @EqualsAndHashCode
    @NoArgsConstructor
    public static class MultimodalPosterTitleResponse {

        /**
         * 是否成功
         */
        private Boolean success;

        /**
         * 错误码
         */
        private String errorCode;

        /**
         * 错误信息
         */
        private String errorMessage;

        /**
         * 消耗点数
         */
        private Integer costPoints;

        /**
         * 提示词
         */
        private String prompt;

        /**
         * 图片列表
         */
        private List<String> urlList;

        /**
         * 海报标题
         */
        private PosterTitleDTO posterTitle;

        /**
         * 失败返回结果
         *
         * @param errorCode    错误码
         * @param errorMessage 错误信息
         * @return 失败返回结果
         */
        public static MultimodalPosterTitleResponse failure(String errorCode, String errorMessage) {
            MultimodalPosterTitleResponse response = new MultimodalPosterTitleResponse();
            response.setSuccess(Boolean.FALSE);
            response.setErrorCode(errorCode);
            response.setErrorMessage(errorMessage);
            response.setCostPoints(0);
            return response;
        }

        /**
         * 失败返回结果
         *
         * @param errorCode    错误码
         * @param errorMessage 错误信息
         * @return 失败返回结果
         */
        public static MultimodalPosterTitleResponse failure(String errorCode, String errorMessage, String prompt, List<String> urlList) {
            MultimodalPosterTitleResponse response = new MultimodalPosterTitleResponse();
            response.setSuccess(Boolean.FALSE);
            response.setErrorCode(errorCode);
            response.setErrorMessage(errorMessage);
            response.setPrompt(prompt);
            response.setUrlList(urlList);
            response.setCostPoints(0);
            return response;
        }
    }
}
