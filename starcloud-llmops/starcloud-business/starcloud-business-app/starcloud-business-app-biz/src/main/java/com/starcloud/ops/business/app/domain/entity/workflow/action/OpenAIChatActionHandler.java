package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.TypeUtil;
import cn.kstry.framework.core.annotation.*;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.domain.handler.textgeneration.OpenAIChatHandler;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @TaskService(name = "OpenAIChatActionHandler", invoke = @Invoke(timeout = 180000))
    @Override
    public ActionResponse execute(@ReqTaskParam(reqSelf = true) AppContext context, ScopeDataOperator scopeDataOperator) {


        return super.execute(context, scopeDataOperator);
    }


    @Override
    protected ActionResponse _execute(Request request) {

        OpenAIChatHandler openAIChatHandler = new OpenAIChatHandler(new StreamingSseCallBackHandler(this.getAppContext().getSseEmitter()));


        //获取前端传的完整字段（老结构）
        Map<String, Object> params = request.getStepParams();

        Long userId = this.getAppContext().getUserId();
        String conversationId = this.getAppContext().getConversationId();

        String prompt = (String) params.getOrDefault("PROMPT", "hi, what you name?");
        Integer maxTokens = Integer.valueOf((String) params.getOrDefault("MAX_TOKENS", "1000"));
        Double temperature = Double.valueOf((String) params.getOrDefault("TEMPERATURE", "0.7"));


        OpenAIChatHandler.Request handlerRequest = new OpenAIChatHandler.Request();

        handlerRequest.setStream(true);
        handlerRequest.setPrompt(prompt);
        handlerRequest.setMaxTokens(maxTokens);
        handlerRequest.setTemperature(temperature);

        if (request.getEnabledDateset()) {
            handlerRequest.setDocsUid(request.getDatesetList());
        }


        HandlerContext handlerContext = HandlerContext.createContext(conversationId, userId, handlerRequest);

        HandlerResponse<String> handlerResponse = openAIChatHandler.execute(handlerContext);

        return convert(handlerResponse);
    }

    private ActionResponse convert(HandlerResponse handlerResponse) {

        ActionResponse actionResponse = new ActionResponse();

        actionResponse.setSuccess(handlerResponse.getSuccess());
        actionResponse.setErrorCode(handlerResponse.getErrorCode());
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


        return actionResponse;

    }


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


    @Data
    public static class Response {

        private String content;

        public Response(String content) {
            this.content = content;
        }
    }

}
