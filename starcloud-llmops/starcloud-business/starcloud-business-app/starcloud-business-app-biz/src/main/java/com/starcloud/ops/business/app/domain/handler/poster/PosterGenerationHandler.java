package com.starcloud.ops.business.app.domain.handler.poster;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.domain.handler.common.BaseToolHandler;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.feign.dto.PosterImage;
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
        try {
            log.info("海报图片生成【开始执行】");
            Request request = context.getRequest();
            // 执行生成图片
            Response response = this.poster(request);
            // 处理响应结果
            HandlerResponse<Response> handlerResponse = new HandlerResponse<>();
            handlerResponse.setSuccess(Boolean.TRUE);
            handlerResponse.setMessage(JSONUtil.toJsonStr(request));
            handlerResponse.setStepConfig(JSONUtil.toJsonStr(request));
            handlerResponse.setAnswer(JSONUtil.toJsonStr(response));
            handlerResponse.setOutput(response);
            log.info("海报图片生成【执行成功】: 生成结果: \n{}", JsonUtils.toJsonPrettyString(request));
            return handlerResponse;
        } catch (ServiceException exception) {
            log.info("海报图片生成:【执行失败】: 模板名称: {} 模板ID: {}, \n\t错误码：{}，错误信息：{}",
                    context.getRequest().getName(), context.getRequest().getCode(), exception.getCode(), exception.getMessage());
            throw exception;
        } catch (Exception exception) {
            log.info("海报图片生成:【执行失败】: 模板名称: {} 模板ID: {}, \n\t，错误信息：{}",
                    context.getRequest().getName(), context.getRequest().getCode(), exception.getMessage());
            throw ServiceExceptionUtil.exceptionWithCause(ErrorCodeConstants.EXECUTE_POSTER_FAILURE, exception.getMessage(), exception);
        }
    }

    /**
     * 异步执行图片
     *
     * @param request 请求
     * @return 响应
     */
    public Response poster(Request request) {
        // 校验模版ID
        if (StringUtils.isBlank(request.getCode())) {
            throw ServiceExceptionUtil.invalidParamException("图片模板ID不能为空！");
        }

        // 组装参数
        PosterRequest posterRequest = new PosterRequest();
        posterRequest.setId(request.getCode());
        posterRequest.setParams(MapUtil.emptyIfNull(request.getParams()));

        log.info("海报图片生成:【执行参数】：\n {}", JsonUtils.toJsonPrettyString(posterRequest));

        // 调用海报生成服务
        List<PosterImage> posterImageList = POSTER_SERVICE.poster(posterRequest);

        Response response = new Response();
        response.setCode(request.getCode());
        response.setName(request.getName());
        response.setIsMain(request.getIsMain());
        response.setIndex(request.getIndex());
        response.setUrlList(posterImageList);
        return response;
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
        private String code;

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
        @JsonPropertyDescription("图片模板Code")
        private String code;

        /**
         * 海报图片模板名称
         */
        @Schema(description = "图片模板名称")
        @JsonPropertyDescription("图片模板名称")
        private String name;

        /**
         * 是否是海报主图
         */
        @Schema(description = "是否是主图")
        @JsonPropertyDescription("是否是主图")
        private Boolean isMain;

        /**
         * 海报图片序号
         */
        @Schema(description = "图片序号")
        @JsonPropertyDescription("图片序号")
        private Integer index;

        /**
         * 海报图片地址
         */
        @Schema(description = "海报图片地址")
        @JsonPropertyDescription("海报图片地址")
        private List<PosterImage> urlList;

    }


}
