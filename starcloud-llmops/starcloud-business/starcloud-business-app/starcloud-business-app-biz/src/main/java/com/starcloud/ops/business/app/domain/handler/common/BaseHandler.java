package com.starcloud.ops.business.app.domain.handler.common;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
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
    @SuppressWarnings("all")
    @JsonIgnore
    @JSONField(serialize = false)
    protected abstract HandlerResponse<R> _execute(HandlerContext<Q> context);

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
        try {
            log.info("步骤节点处理器【开始执行】: 处理器名称: {}", this.getClass().getSimpleName());

            // 记录开始时间
            long start = System.currentTimeMillis();
            // 执行具体的处理器
            HandlerResponse<R> handlerResponse = this._execute(context);
            // 处理返回结果
            handlerResponse.setType(this.getClass().getSimpleName());
            // 临时放这里
            if (StringUtils.isBlank(handlerResponse.getMessage())) {
                handlerResponse.setMessage(JsonUtils.toJsonString(context.getRequest()));
            }
            if (StringUtils.isBlank(handlerResponse.getAnswer())) {
                handlerResponse.setAnswer(JsonUtils.toJsonString(handlerResponse.getOutput()));
            }
            // 记录执行时间
            handlerResponse.setElapsed(System.currentTimeMillis() - start);
            log.info("步骤节点处理器【执行成功】: 处理器名称: {}, 执行时间: {}ms",
                    this.getClass().getSimpleName(), handlerResponse.getElapsed());

            return handlerResponse;
        } catch (ServiceException exception) {
            log.error("步骤节点处理器【执行异常】: 处理器名称: {}, 错误码: {}, 异常信息: {}",
                    this.getClass().getSimpleName(), exception.getCode(), exception.getMessage());
            // 发送错误交互反馈
            context.sendCurrentInteractiveError(exception.getCode(), exception.getMessage());
            throw exception;
        } catch (Exception exception) {
            ErrorCode errorCode = ErrorCodeConstants.EXECUTE_STEP_HANDLER_FAILURE;
            log.error("步骤节点处理器【执行异常】: 处理器名称: {}, 异常信息: {}",
                    this.getClass().getSimpleName(), exception.getMessage());
            // 发送错误交互反馈
            context.sendCurrentInteractiveError(errorCode.getCode(), errorCode.getMsg());
            throw ServiceExceptionUtil.exceptionWithCause(errorCode, exception.getMessage(), exception);
        }
    }

}
