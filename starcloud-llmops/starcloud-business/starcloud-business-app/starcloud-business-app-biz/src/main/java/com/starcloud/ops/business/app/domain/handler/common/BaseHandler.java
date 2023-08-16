package com.starcloud.ops.business.app.domain.handler.common;

import cn.hutool.core.util.TypeUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.business.app.domain.entity.chat.Interactive.InteractiveInfo;
import com.starcloud.ops.business.app.domain.entity.skill.HandlerSkill;
import com.starcloud.ops.llm.langchain.core.tools.utils.OpenAIUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Slf4j
@Data
public abstract class BaseHandler<Q, R> {

    /**
     * 给用户看的功能名称
     */
    private String userName;

    /**
     * 给用户看的功能描述
     */
    private String userDescription;

    /**
     * 给LLM的功能名称
     */
    private String name;

    /**
     * 给LLM的功能描述
     */
    private String description;


    /**
     * 技能图标
     */
    private String icon;

    /**
     * 获取handler标签，前端分类筛选用
     *
     * @return
     */
    public List<String> getTags() {
        return Arrays.asList("system");
    }

    protected abstract HandlerResponse<R> _execute(HandlerContext<Q> context);


    /**
     * 生成个handler 实例
     *
     * @param name
     * @return
     */
    public static BaseHandler of(String name) {

        //@todo
        return null;
    }

    /**
     * 执行步骤
     */
    public HandlerResponse<R> execute(HandlerContext<Q> context) {

        HandlerResponse<R> handlerResponse = new HandlerResponse();
        handlerResponse.setSuccess(false);

        try {

            //@todo 默认执行开始 tips 提示

            //设置入参
            handlerResponse.setMessage(JSONUtil.toJsonStr(context.getRequest()));

            //中间的交互提示 可以在 具体的handler内继续调用
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

            log.error("BaseHandler {} execute is fail: {}", this.getClass().getSimpleName(), e.getMessage(), e);
        }

        //@todo 默认执行结束 tips 提示

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
