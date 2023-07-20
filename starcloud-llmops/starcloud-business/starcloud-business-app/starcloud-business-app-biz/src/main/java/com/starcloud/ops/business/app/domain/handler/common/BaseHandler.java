package com.starcloud.ops.business.app.domain.handler.common;

import cn.hutool.core.util.TypeUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.llm.langchain.core.model.llm.LLMUtils;
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

    private UserBenefitsService userBenefitsService = SpringUtil.getBean(UserBenefitsService.class);

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

        if (response.getSuccess() && this.getBenefitsType() != null) {
            //权益记录
            userBenefitsService.expendBenefits(this.getBenefitsType().getCode(), response.getTotalTokens(), context.getUserId(), context.getConversationUid());
        }

        return response;

    }


    /**
     * 获取当前handler消耗的权益类型，如果返回自动扣除权益，返回null,则不处理权益扣除
     *
     * @return
     */
    public BenefitsTypeEnums getBenefitsType() {
        return BenefitsTypeEnums.TOKEN;
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
