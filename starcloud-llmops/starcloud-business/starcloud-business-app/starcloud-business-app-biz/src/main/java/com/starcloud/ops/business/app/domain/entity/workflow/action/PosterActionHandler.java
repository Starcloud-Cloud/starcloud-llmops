package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.core.collection.CollectionUtil;
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
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.poster.PosterTitleModeEnum;
import com.starcloud.ops.business.app.service.xhs.executor.PosterThreadPoolHolder;
import com.starcloud.ops.business.app.util.CreativeUtils;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.llm.langchain.core.model.multimodal.qwen.ChatVLQwen;
import com.starcloud.ops.llm.langchain.core.schema.message.multimodal.HumanMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.multimodal.MultiModalMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
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
    @TaskService(name = "PosterActionHandler", invoke = @Invoke(timeout = 180000))
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

        return null;

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

        log.info("海报生成 Action 执行开始......");
        // 海报模版参数
        String posterStyle = String.valueOf(this.getAppContext().getContextVariablesValue(CreativeConstants.POSTER_STYLE, Boolean.FALSE));
        // 转为海报模版对象
        PosterStyleDTO style = JsonUtils.parseObject(posterStyle, PosterStyleDTO.class);
        // 校验海报模版
        style.validate();

        // 海报风格参数填充
        assemble(style);

        // 获取线程池
        ThreadPoolExecutor executor = POSTER_TEMPLATE_THREAD_POOL_HOLDER.executor();
        // 任务列表
        List<CompletableFuture<HandlerResponse<PosterGenerationHandler.Response>>> futureList = CollectionUtil.emptyIfNull(style.getTemplateList()).stream()
                .map(item -> CompletableFuture.supplyAsync(() -> poster(item), executor)).collect(Collectors.toList());
        // 任务合并
        CompletableFuture<List<HandlerResponse<PosterGenerationHandler.Response>>> allFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]))
                .thenApply(v -> futureList.stream().map(CompletableFuture::join).collect(Collectors.toList()));
        // 获取结果
        List<HandlerResponse<PosterGenerationHandler.Response>> handlerResponseList = allFuture.join();

        // 如果有一个失败，则返回失败
        Optional<HandlerResponse<PosterGenerationHandler.Response>> failureOption = handlerResponseList.stream().filter(item -> !item.getSuccess()).findFirst();
        if (failureOption.isPresent()) {
            return failureResponse(failureOption.get(), style);
        }

        // 构建响应结果
        List<PosterGenerationHandler.Response> list = handlerResponseList.stream().map(HandlerResponse::getOutput).collect(Collectors.toList());
        ActionResponse response = new ActionResponse();
        response.setSuccess(Boolean.TRUE);
        response.setType(handlerResponseList.get(0).getType());
        response.setIsShow(Boolean.TRUE);
        response.setStepConfig(JsonUtils.toJsonString(style));
        response.setMessage(JsonUtils.toJsonString(style));
        response.setAnswer(JsonUtils.toJsonString(list));
        response.setOutput(JsonData.of(list));
        response.setCostPoints(list.size());
        response.setAiModel(null);
        log.info("海报生成 Action 执行结束......");
        return response;
    }

    /**
     * 海报风格参数填充
     *
     * @param posterStyle 海报风格
     */
    private void assemble(PosterStyleDTO posterStyle) {
        List<PosterTemplateDTO> posterTemplateList = CollectionUtil.emptyIfNull(posterStyle.getTemplateList());

        // 把每一个变量的uuid和value放到此map中
        Map<String, Object> templateVariableMap = CreativeUtils.getPosterStyleVariableMap(posterStyle);
        // 替换变量，未找到的占位符会被替换为空字符串
        Map<String, Object> replaceValueMap = this.getAppContext().parseMapFromVariables(templateVariableMap, this.getAppContext().getStepId());

        // 循环处理，进行变量替换
        for (PosterTemplateDTO posterTemplate : posterTemplateList) {
            List<PosterVariableDTO> variableList = CollectionUtil.emptyIfNull(posterTemplate.getVariableList());
            for (PosterVariableDTO variable : variableList) {
                // 从作用域数据中获取变量值
                Object value = replaceValueMap.getOrDefault(variable.getUuid(), variable.getValue());
                variable.setValue(value);
            }
            posterTemplate.setVariableList(variableList);
        }

        posterStyle.setTemplateList(posterTemplateList);
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
                            item -> Optional.ofNullable(item.getValue()).orElse(StringUtils.EMPTY))
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
     * 失败返回结果
     *
     * @param failure 失败结果
     * @param style   海报风格
     * @return 失败返回结果
     */
    @NotNull
    private static ActionResponse failureResponse(HandlerResponse<PosterGenerationHandler.Response> failure, PosterStyleDTO style) {
        log.info("海报生成 Action 执行失败......");
        ActionResponse response = new ActionResponse();
        response.setSuccess(Boolean.FALSE);
        response.setErrorCode(String.valueOf(failure.getErrorCode()));
        response.setErrorMsg(failure.getErrorMsg());
        response.setType(failure.getType());
        response.setIsShow(Boolean.TRUE);
        response.setMessage(JsonUtils.toJsonString(style));
        response.setStepConfig(JsonUtils.toJsonString(style));
        response.setCostPoints(0);
        return response;
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
