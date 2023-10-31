package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.json.JSONUtil;
import cn.kstry.framework.core.annotation.Invoke;
import cn.kstry.framework.core.annotation.NoticeResult;
import cn.kstry.framework.core.annotation.NoticeSta;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.annotation.TaskComponent;
import cn.kstry.framework.core.annotation.TaskService;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.domain.handler.textgeneration.OpenAIChatHandler;
import com.starcloud.ops.business.app.service.chat.callback.MySseCallBackHandler;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Open AI Chat 步骤实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Slf4j
@TaskComponent(name = "OpenAIChatActionHandler")
public class OpenAIChatActionHandler extends BaseActionHandler<OpenAIChatActionHandler.Request, OpenAIChatActionHandler.Response> {


    @NoticeSta
    @NoticeResult
    @TaskService(name = "OpenAIChatActionHandler", invoke = @Invoke(timeout = 180000))
    @Override
    public ActionResponse execute(@ReqTaskParam(reqSelf = true) AppContext context, ScopeDataOperator scopeDataOperator) {
        return super.execute(context, scopeDataOperator);
    }

    /**
     * 获取当前handler消耗的权益点数
     *
     * @param request 请求参数
     * @return 权益点数
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected Integer getCostPoints(Request request) {
        Map<String, Object> params = request.getStepParams();
        String aiModel = String.valueOf(Optional.ofNullable(params.get("MODEL")).orElse(ModelTypeEnum.GPT_3_5_TURBO.getName()));
        if (ModelTypeEnum.GPT_4.getName().equals(aiModel)) {
            return 30;
        }
        return 1;
    }

    /**
     * 执行OpenApi生成的步骤
     *
     * @param request 请求参数
     * @return 执行结果
     */
    @Override
    @SuppressWarnings("all")
    @JsonIgnore
    @JSONField(serialize = false)
    protected ActionResponse doExecute(Request request) {

        log.info("OpenAI ChatGPT Action 执行开始: 请求参数：\n{}", JSONUtil.parse(request).toStringPretty());
        StreamingSseCallBackHandler callBackHandler = new MySseCallBackHandler(this.getAppContext().getSseEmitter());
        OpenAIChatHandler handler = new OpenAIChatHandler(callBackHandler);

        //获取前端传的完整字段（老结构）
        Map<String, Object> params = request.getStepParams();
        Long userId = this.getAppContext().getUserId();
        Long endUser = this.getAppContext().getEndUserId();
        String conversationId = this.getAppContext().getConversationUid();

        String model = String.valueOf(params.getOrDefault("MODEL", ModelTypeEnum.GPT_3_5_TURBO.getName()));
        String prompt = String.valueOf(params.getOrDefault("PROMPT", "hi, what you name?"));
        Integer maxTokens = Integer.valueOf((String) params.getOrDefault("MAX_TOKENS", "1000"));
        Double temperature = Double.valueOf((String) params.getOrDefault("TEMPERATURE", "0.7"));

        // 构建请求
        OpenAIChatHandler.Request handlerRequest = new OpenAIChatHandler.Request();
        handlerRequest.setStream(true);
        handlerRequest.setModel(model);
        handlerRequest.setPrompt(prompt);
        handlerRequest.setMaxTokens(maxTokens);
        handlerRequest.setTemperature(temperature);
        // 数据集支持
        if (request.getEnabledDateset()) {
            handlerRequest.setDocsUid(request.getDatesetList());
        }
        // 构建请求
        HandlerContext handlerContext = HandlerContext.createContext(this.getAppUid(), conversationId, userId, endUser, this.getAppContext().getScene(), handlerRequest);
        // 执行步骤
        HandlerResponse<String> handlerResponse = handler.execute(handlerContext);
        ActionResponse response = convert(handlerResponse, request);
        log.info("OpenAI ChatGPT Action 执行结束: 响应结果：\n {}", JSONUtil.parse(response).toStringPretty());
        return response;
    }

    /**
     * 转换响应结果
     *
     * @param handlerResponse 响应结果
     * @param request         请求参数
     * @return 转换后的响应结果
     */
    @SuppressWarnings("all")
    @JsonIgnore
    @JSONField(serialize = false)
    private ActionResponse convert(HandlerResponse handlerResponse, Request request) {
        ActionResponse actionResponse = new ActionResponse();
        actionResponse.setSuccess(handlerResponse.getSuccess());
        actionResponse.setErrorCode(String.valueOf(handlerResponse.getErrorCode()));
        actionResponse.setErrorMsg(handlerResponse.getErrorMsg());
        actionResponse.setType(handlerResponse.getType());
        actionResponse.setIsShow(true);
        actionResponse.setMessage(handlerResponse.getMessage());
        actionResponse.setAnswer(handlerResponse.getAnswer());
        actionResponse.setOutput(JsonData.of(handlerResponse.getOutput()));
        actionResponse.setMessageTokens(handlerResponse.getMessageTokens());
        actionResponse.setMessageUnitPrice(handlerResponse.getMessageUnitPrice());
        actionResponse.setAnswerTokens(handlerResponse.getAnswerTokens());
        actionResponse.setAnswerUnitPrice(handlerResponse.getAnswerUnitPrice());
        actionResponse.setTotalTokens(handlerResponse.getTotalTokens());
        actionResponse.setTotalPrice(handlerResponse.getTotalPrice());
        actionResponse.setStepConfig(handlerResponse.getStepConfig());
        // 权益点数, 成功正常扣除, 失败不扣除
        actionResponse.setCostPoints(handlerResponse.getSuccess() ? this.getCostPoints(request) : 0);
        return actionResponse;
    }

    /**
     * 请求实体
     */
    @Data
    public static class Request {

        /**
         * 老参数直接传入
         */
        @Deprecated
        private Map<String, Object> stepParams;


        /**
         * 后续新参数 都是一个个独立字段即可
         */
        private String prompt;


        private Boolean enabledDateset = false;

        /**
         * 数据集支持
         */
        private List<String> datesetList;

    }

    /**
     * 响应实体
     */
    @Data
    public static class Response {

        private String content;

        public Response(String content) {
            this.content = content;
        }
    }

}
