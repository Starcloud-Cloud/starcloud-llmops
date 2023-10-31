package com.starcloud.ops.business.app.domain.handler.common;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.enums.ChatErrorCodeConstants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@SuppressWarnings("all")
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
     * 执行 handler，返回结果
     *
     * @param context 请求上下文
     * @return 执行结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected abstract HandlerResponse<R> _execute(HandlerContext<Q> context);

    /**
     * 生成个handler 实例
     *
     * @param name handler 名称
     * @return handler
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public static BaseHandler of(String name) {
        try {
            //头部小写驼峰
            return SpringUtil.getBean(StrUtil.lowerFirst(name));
        } catch (Exception e) {
            log.error("BaseHandler of is fail: {}", name);
        }
        return null;
    }

    /**
     * 获取handler 标签，前端分类筛选用
     *
     * @return 标签
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public List<String> getTags() {
        return Collections.singletonList("system");
    }

    /**
     * 执行步骤
     *
     * @param context 上下文
     * @return 执行结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public HandlerResponse<R> execute(HandlerContext<Q> context) {
        HandlerResponse<R> handlerResponse = new HandlerResponse<>();
        handlerResponse.setSuccess(false);
        long start = System.currentTimeMillis();
        try {
            // @todo 默认执行开始 tips 提示
            // 中间的交互提示 可以在 具体的handler内继续调用
            HandlerResponse<R> source = this._execute(context);
            // 设置的属性copy
            BeanUtil.copyProperties(source, handlerResponse);
            handlerResponse.setSuccess(true);
            handlerResponse.setType(this.getClass().getSimpleName());
            // 临时放这里
            if (StringUtils.isBlank(source.getMessage())) {
                handlerResponse.setMessage(JsonUtils.toJsonString(context.getRequest()));
            }
            if (StringUtils.isBlank(source.getAnswer())) {
                handlerResponse.setAnswer(JsonUtils.toJsonString(source.getOutput()));
            }
        } catch (ServiceException e) {
            log.error("BaseHandler（{}） execute is error: {}", this.getClass().getSimpleName(), e.getMessage(), e);
            handlerResponse.setErrorCode(e.getCode());
            handlerResponse.setErrorMsg(e.getMessage());
            context.sendCurrentInteractiveError(handlerResponse.getErrorCode(), handlerResponse.getErrorMsg());

        } catch (Exception e) {
            log.error("BaseHandler（{}）execute is fail: {}", this.getClass().getSimpleName(), e.getMessage(), e);
            handlerResponse.setErrorCode(ChatErrorCodeConstants.TOOL_RUN_ERROR.getCode());
            handlerResponse.setErrorMsg(e.getMessage());
            context.sendCurrentInteractiveError(handlerResponse.getErrorCode(), handlerResponse.getErrorMsg());
        }
        handlerResponse.setElapsed(System.currentTimeMillis() - start);
        return handlerResponse;

    }

}
