package com.starcloud.ops.business.app.service.image.strategy.handler;

import com.starcloud.ops.business.app.api.image.vo.request.BaseImageRequest;
import com.starcloud.ops.business.app.api.image.vo.response.BaseImageResponse;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageCreateReqVO;
import org.springframework.stereotype.Component;

/**
 * 图片处理器
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-22
 */
@Component
public abstract class BaseImageHandler<Request extends BaseImageRequest, Response extends BaseImageResponse> {

    /**
     * 构建图片配置信息配置
     *
     * @param request 请求
     */
    public abstract void handleRequest(Request request);

    /**
     * 处理图片
     *
     * @param request 图片请求
     * @return 图片响应
     */
    public abstract Response handleImage(Request request);

    /**
     * 处理日志消息
     *
     * @param messageRequest 日志信息
     * @param request        请求
     * @param response       响应
     */
    public abstract void handleLogMessage(LogAppMessageCreateReqVO messageRequest, Request request, Response response);

    /**
     * 处理图片
     *
     * @param request 图片请求
     * @return 图片响应
     */
    public Response handle(Request request) {
        handleRequest(request);
        return this.handleImage(request);
    }
}
