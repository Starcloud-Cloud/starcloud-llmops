package com.starcloud.ops.business.app.service.image.strategy.handler;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import com.starcloud.ops.business.app.api.image.vo.request.RemoveTextRequest;
import com.starcloud.ops.business.app.api.image.vo.response.RemoveTextResponse;
import com.starcloud.ops.business.app.convert.image.ImageConvert;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.feign.request.clipdrop.ImageFileClipDropRequest;
import com.starcloud.ops.business.app.feign.response.ClipDropImage;
import com.starcloud.ops.business.app.service.image.clipdrop.ClipDropImageService;
import com.starcloud.ops.business.app.service.image.strategy.ImageScene;
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import com.starcloud.ops.business.app.util.ImageUtils;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageCreateReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Objects;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-25
 */
@Slf4j
@Component
@ImageScene(AppSceneEnum.IMAGE_REMOVE_TEXT)
public class RemoveTextHandler extends BaseImageHandler<RemoveTextRequest, RemoveTextResponse> {

    @Resource
    private ClipDropImageService clipDropImageService;

    /**
     * 构建图片配置信息配置
     *
     * @param request 请求
     */
    @Override
    public void handleRequest(RemoveTextRequest request) {
        log.info("RemoveTextHandler handleRequest: 默认不做处理：{}", JSONUtil.toJsonStr(request));
    }

    /**
     * 处理图片
     *
     * @param request 图片请求
     * @return 图片响应
     */
    @Override
    public RemoveTextResponse handle(RemoveTextRequest request) {
        log.info("RemoveTextHandler handle: 处理去除文字图片请求开始：处理前数据：{}", JSONUtil.toJsonStr(request));
        ImageFileClipDropRequest imageFileClipDropRequest = new ImageFileClipDropRequest();
        imageFileClipDropRequest.setImageFile(ImageUploadUtils.getImageFile(request.getImageUrl()));
        ClipDropImage clipDropImage = clipDropImageService.removeText(imageFileClipDropRequest);
        if (Objects.isNull(clipDropImage)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_IMAGE_FAIL, "去除文字失败");
        }
        ImageDTO image = ImageConvert.INSTANCE.convert(clipDropImage);
        RemoveTextResponse response = new RemoveTextResponse();
        response.setOriginalUrl(request.getImageUrl());
        response.setImages(Collections.singletonList(image));
        log.info("RemoveTextHandler handle: 处理去除文字图片请求结束：处理后结果：{}", JSONUtil.toJsonStr(response));
        return response;
    }

    /**
     * 处理日志消息
     *
     * @param messageRequest 日志信息
     * @param request        请求
     * @param response       响应
     */
    @Override
    public void handleLogMessage(LogAppMessageCreateReqVO messageRequest, RemoveTextRequest request, RemoveTextResponse response) {
        messageRequest.setAiModel("clip-drop");
    }
}
