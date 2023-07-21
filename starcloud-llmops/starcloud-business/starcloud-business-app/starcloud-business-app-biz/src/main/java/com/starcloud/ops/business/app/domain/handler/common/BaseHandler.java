package com.starcloud.ops.business.app.domain.handler.common;

import cn.hutool.core.util.TypeUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.llm.langchain.core.tools.utils.OpenAIUtils;
import lombok.Data;

import java.lang.reflect.Type;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
public abstract class BaseHandler<Q, R> {

    private String name;

    private String description;

    protected abstract HandlerResponse<R> _execute(HandlerContext<Q> context);

    /**
     * 执行步骤
     */
    public HandlerResponse<R> execute(HandlerContext<Q> context) {

        HandlerResponse<R> response = this._execute(context);

        response.getTotalTokens();
        response.getOutput();

//        if (response.getSuccess() && this.getBenefitsType() != null) {
//            //权益记录
//            userBenefitsService.expendBenefits(this.getBenefitsType().getCode(), response.getTotalTokens(), context.getUserId(), context.getConversationUid());
//        }

        return response;

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
