package com.starcloud.ops.business.app.domain.handler.poster;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.domain.handler.common.BaseToolHandler;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.feign.dto.PosterTemplateDTO;
import com.starcloud.ops.business.app.feign.request.poster.PosterRequest;
import com.starcloud.ops.business.app.service.poster.PosterService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@SuppressWarnings("all")
@Data
@Slf4j
@Component
public class PosterGenerationHandler extends BaseToolHandler<PosterGenerationHandler.Request, PosterGenerationHandler.Response> {

    /**
     * 海报生成服务
     */
    private static final PosterService POSTER_SERVICE = SpringUtil.getBean(PosterService.class);

    /**
     * 执行 handler，返回结果
     *
     * @param context 请求上下文
     * @return 执行结果
     */
    @Override
    protected HandlerResponse<Response> _execute(HandlerContext<Request> context) {
        HandlerResponse<Response> handlerResponse = new HandlerResponse<>();
        handlerResponse.setSuccess(Boolean.FALSE);
        try {
            Request request = context.getRequest();
            if (Objects.isNull(request)) {
                throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.POSTER_PARAMS_REQUIRED);
            }
            handlerResponse.setMessage(JSONUtil.toJsonStr(request));
            handlerResponse.setStepConfig(JSONUtil.toJsonStr(request));

            // 执行生成图片
            Response response = this.poster(request);
            // 处理响应结果
            handlerResponse.setSuccess(Boolean.TRUE);
            handlerResponse.setAnswer(JSONUtil.toJsonStr(response));
            handlerResponse.setOutput(response);
        } catch (ServiceException exception) {
            log.info("海报图片生成: 生成图片失败(ServiceException): 错误码：{}，错误信息：{}", exception.getCode(), exception.getMessage());
            handlerResponse.setErrorCode(exception.getCode());
            handlerResponse.setErrorMsg(exception.getMessage());
        } catch (Exception exception) {
            log.info("海报图片生成: 生成图片失败(Exception): 错误码：{}，错误信息：{}", 350400200, exception.getMessage());
            handlerResponse.setErrorCode(350400200);
            handlerResponse.setErrorMsg(exception.getMessage());
        }
        return handlerResponse;
    }

    /**
     * 异步执行图片
     *
     * @param request 请求
     * @return 响应
     */
    public Response poster(Request request) {
        log.info("海报图片生成：执行生成图片开始: 执行参数: \n{}", JSONUtil.parse(request).toStringPretty());
        try {

            // 校验模版ID
            if (StringUtils.isBlank(request.getId())) {
                throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.POSTER_ID_REQUIRED);
            }

            // 校验参数
            Map<String, Object> params = request.getParams();
            if (CollectionUtil.isEmpty(params)) {
                throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.POSTER_PARAMS_REQUIRED);
            }

            // 校验图片模板是否存在
            List<PosterTemplateDTO> templates = POSTER_SERVICE.templates();
            Optional<PosterTemplateDTO> templateOption = templates.stream().filter(item -> StringUtils.equals(item.getId(), request.getId())).findFirst();
            if (!templateOption.isPresent()) {
                throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.POSTER_NOT_SUPPORTED, request.getName());
            }

            // 调用海报生成服务
            PosterRequest posterRequest = new PosterRequest();
            posterRequest.setId(request.getId());
            posterRequest.setParams(params);
            String url = POSTER_SERVICE.poster(posterRequest);

            Response response = new Response();
            response.setId(request.getId());
            response.setName(request.getName());
            response.setIsMain(request.getIsMain());
            response.setIndex(request.getIndex());
            log.info("海报图片生成: 执行生成图片成功，执行结果：\n{}", JSONUtil.parse(response).toStringPretty());
            response.setUrl(url);
            return response;
        } catch (ServiceException exception) {
            throw exception;
        } catch (Exception exception) {
            throw ServiceExceptionUtil.exception(new ErrorCode(350400200, exception.getMessage()));
        }
    }

    /**
     * 海报图片生成请求
     */
    @Data
    public static class Request implements Serializable {

        private static final long serialVersionUID = -3860064643395400824L;

        /**
         * 海报图片模板ID
         */
        @Schema(description = "图片模板")
        private String id;

        /**
         * 海报图片模板名称
         */
        @Schema(description = "图片模板名称")
        private String name;

        /**
         * 是否是海报主图
         */
        @Schema(description = "是否是主图")
        private Boolean isMain;

        /**
         * 海报图片序号
         */
        @Schema(description = "图片序号")
        private Integer index;

        /**
         * 海报图片生成参数
         */
        @Schema(description = "图片生成参数")
        private Map<String, Object> params;

    }

    /**
     * 海报图片模板响应
     */
    @Data
    public static class Response implements Serializable {

        private static final long serialVersionUID = 6596267450066096458L;

        /**
         * 海报图片模板ID
         */
        @Schema(description = "图片模板")
        private String id;

        /**
         * 海报图片模板名称
         */
        @Schema(description = "图片模板名称")
        private String name;

        /**
         * 是否是海报主图
         */
        @Schema(description = "是否是主图")
        private Boolean isMain;

        /**
         * 海报图片序号
         */
        @Schema(description = "图片序号")
        private Integer index;

        /**
         * 海报图片地址
         */
        @Schema(description = "海报图片地址")
        private String url;

    }


}