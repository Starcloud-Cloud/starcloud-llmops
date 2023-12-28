package com.starcloud.ops.business.app.domain.handler.poster;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.domain.handler.common.BaseToolHandler;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.feign.request.poster.PosterRequest;
import com.starcloud.ops.business.app.service.poster.PosterService;
import com.starcloud.ops.business.app.service.xhs.executor.PosterTemplateThreadPoolHolder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@Slf4j
@Component
public class PosterGenerationHandler extends BaseToolHandler<PosterGenerationHandler.Request, PosterGenerationHandler.Response> {

    /**
     * 海报生成服务
     */
    private static final PosterService POSTER_SERVICE = SpringUtil.getBean(PosterService.class);

    /**
     * 线程池
     */
    private static final PosterTemplateThreadPoolHolder POSTER_TEMPLATE_THREAD_POOL_HOLDER = SpringUtil.getBean(PosterTemplateThreadPoolHolder.class);

    /**
     * 执行 handler，返回结果
     *
     * @param context 请求上下文
     * @return 执行结果
     */
    @Override
    protected HandlerResponse<Response> _execute(HandlerContext<Request> context) {
        log.info("批量海报生成开始.......");
        long star = System.currentTimeMillis();
        HandlerResponse<Response> handlerResponse = new HandlerResponse<>();
        Request request = context.getRequest();
        handlerResponse.setSuccess(Boolean.FALSE);
        if (request == null || CollectionUtil.isEmpty(request.getPosterRequestList())) {
            handlerResponse.setErrorCode(750100119);
            handlerResponse.setErrorMsg("生成海报：请求参数不能为空！");
            handlerResponse.setElapsed(star - System.currentTimeMillis());
            return handlerResponse;
        }
        try {
            handlerResponse.setStepConfig(JSONUtil.toJsonStr(request));
            List<PosterTemplateRequest> posterRequestList = request.getPosterRequestList();
            // 获取执行线程池
            ThreadPoolExecutor executor = POSTER_TEMPLATE_THREAD_POOL_HOLDER.executor();
            // 执行异步海报生成
            List<CompletableFuture<PosterTemplateResponse>> futureList = posterRequestList.stream()
                    .map(item -> CompletableFuture.supplyAsync(() -> poster(item), executor)).collect(Collectors.toList());
            // 任务合并
            CompletableFuture<List<PosterTemplateResponse>> allFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]))
                    .thenApply(v -> futureList.stream().map(CompletableFuture::join).collect(Collectors.toList()));
            // 获取执行结果
            List<PosterTemplateResponse> posterResponseList = allFuture.join();
            Response response = new Response();
            response.setPosterResponseList(posterResponseList);

            // 构建响应
            handlerResponse.setAnswer(JSONUtil.toJsonStr(response));
            handlerResponse.setOutput(response);
        } catch (ServiceException exception) {
            log.info("批量海报图片生成: 生成图片失败(ServiceException): 错误码：{}，错误信息：{}", exception.getCode(), exception.getMessage());
            handlerResponse.setErrorCode(exception.getCode());
            handlerResponse.setErrorMsg(exception.getMessage());
        } catch (Exception exception) {
            log.info("批量海报图片生成: 生成图片失败(Exception): 错误码：{}，错误信息：{}", 350400200, exception.getMessage());
            handlerResponse.setErrorCode(750100120);
            handlerResponse.setErrorMsg(exception.getMessage());
        }
        log.info("批量海报生成结束......");
        handlerResponse.setElapsed(star - System.currentTimeMillis());
        return handlerResponse;
    }

    /**
     * 异步执行图片
     *
     * @param request 请求
     * @return 响应
     */
    public PosterTemplateResponse poster(PosterTemplateRequest request) {
        log.info("海报图片生成：执行生成图片开始: 执行参数: \n{}", JSONUtil.parse(request).toStringPretty());
        PosterTemplateResponse response = new PosterTemplateResponse();
        response.setSuccess(Boolean.FALSE);
        response.setId(request.getId());
        response.setName(request.getName());
        response.setIsMain(request.getIsMain());
        response.setIndex(request.getIndex());
        try {
            // 校验参数
            Map<String, Object> params = request.getParams();
            if (CollectionUtil.isEmpty(params)) {
                throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.POSTER_PARAMS_REQUIRED);
            }

            // 调用海报生成服务
            PosterRequest posterRequest = new PosterRequest();
            posterRequest.setId(request.getId());
            posterRequest.setParams(params);
            String url = POSTER_SERVICE.poster(posterRequest);

            // 构建响应
            response.setSuccess(Boolean.TRUE);
            response.setUrl(url);
            log.info("海报图片生成: 执行生成图片成功，id：{}，url：{}", request.getId(), url);
        } catch (ServiceException exception) {
            log.info("海报图片生成: 生成图片失败(ServiceException): 错误码：{}，错误信息：{}", exception.getCode(), exception.getMessage());
            response.setErrorCode(exception.getCode().toString());
            response.setErrorMessage(exception.getMessage());
        } catch (Exception exception) {
            log.info("海报图片生成: 生成图片失败(Exception): 错误码：{}，错误信息：{}", 350400200, exception.getMessage());
            response.setErrorCode("750100110");
            response.setErrorMessage(exception.getMessage());
        }
        return response;
    }

    @Data
    public static class Request implements Serializable {

        private static final long serialVersionUID = -3860064643395400824L;

        /**
         * 海报参数
         */
        private List<PosterTemplateRequest> posterRequestList;

    }


    @Data
    public static class Response implements Serializable {

        private static final long serialVersionUID = 6596267450066096458L;

        /**
         * 返回结果
         */
        private List<PosterTemplateResponse> posterResponseList;

    }


}
