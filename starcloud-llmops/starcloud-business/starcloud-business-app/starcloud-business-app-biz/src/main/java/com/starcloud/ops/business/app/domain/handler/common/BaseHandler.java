package com.starcloud.ops.business.app.domain.handler.common;

import cn.hutool.core.util.TypeUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.business.app.domain.entity.chat.Interactive.InteractiveInfo;
import com.starcloud.ops.llm.langchain.core.tools.utils.OpenAIUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Slf4j
@Data
public abstract class BaseHandler<Q, R> {

    private String name;

    private String description;

    protected abstract HandlerResponse<R> _execute(HandlerContext<Q> context);

    /**
     * 执行步骤
     */
    public HandlerResponse<R> execute(HandlerContext<Q> context) {

        HandlerResponse<R> handlerResponse = new HandlerResponse();
        handlerResponse.setSuccess(false);

        try {

            //设置入参
            handlerResponse.setMessage(JSONUtil.toJsonStr(context.getRequest()));

            handlerResponse = this._execute(context);
            handlerResponse.setSuccess(true);

        } catch (Exception e) {

            handlerResponse.setErrorCode("-1");
            handlerResponse.setErrorMsg(e.getMessage());

            //异常，使用最近一次的互动信息
            InteractiveInfo current = context.getCurrentInteractive();

            current.setStatus(1);
            current.setSuccess(false);
            current.setErrorCode("-1");
            current.setErrorMsg(e.getMessage());

            context.sendCallbackInteractiveEnd(current);

            log.error("BaseHandler execute is fail: {}", e.getMessage(), e);
        }

        handlerResponse.getTotalTokens();
        handlerResponse.getOutput();

        return handlerResponse;

    }

    /**
     * @return
     */
    public JsonNode getInputSchemas() {

        Type query = TypeUtil.getTypeArgument(this.getClass());
        if (query.getTypeName().contains("Object")) {

            return null;

        } else {

            Class<Q> cc = (Class<Q>) query;
            return OpenAIUtils.serializeJsonSchema(cc);
        }

    }

}
