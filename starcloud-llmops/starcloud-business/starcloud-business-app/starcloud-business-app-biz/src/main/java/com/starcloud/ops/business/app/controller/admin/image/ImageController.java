package com.starcloud.ops.business.app.controller.admin.image;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.image.dto.ImageMetaDTO;
import com.starcloud.ops.business.app.api.image.vo.request.HistoryGenerateImagePageQuery;
import com.starcloud.ops.business.app.api.image.vo.response.ImageMessageRespVO;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageReqVO;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.feign.request.clipdrop.ImageFileClipDropRequest;
import com.starcloud.ops.business.app.feign.response.ClipDropImage;
import com.starcloud.ops.business.app.feign.response.ImageResponse;
import com.starcloud.ops.business.app.service.image.ImageService;
import com.starcloud.ops.business.app.service.image.clipdrop.ClipDropImageService;
import com.starcloud.ops.business.app.service.limit.AppLimitRequest;
import com.starcloud.ops.business.app.service.limit.AppLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 图片生成接口
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@RestController
@RequestMapping("/llm/image")
@Tag(name = "星河云海-图片生成", description = "星河云海图片生成管理")
public class ImageController {

    @Resource
    private ImageService imageService;

    @Resource
    private AppLimitService appLimitService;

    @Resource
    private ClipDropImageService clipDropImageService;

    @GetMapping("/meta")
    @Operation(summary = "生成图片元数据", description = "生成图片元数据")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<Map<String, List<ImageMetaDTO>>> meta() {
        return CommonResult.success(imageService.meta());
    }

    @GetMapping("/history")
    @Operation(summary = "查询历史图片列表", description = "查询历史图片列表")
    @ApiOperationSupport(order = 20, author = "nacoyer")
    public CommonResult<PageResult<ImageMessageRespVO>> historyGenerateImages(HistoryGenerateImagePageQuery query) {
        return CommonResult.success(imageService.historyGenerateImages(query));
    }

    @PostMapping("/text-to-image")
    @Operation(summary = "文本生成图片", description = "文本生成图片")
    @ApiOperationSupport(order = 30, author = "nacoyer")
    public CommonResult<ImageMessageRespVO> textToImage(@Validated @RequestBody ImageReqVO request) {
        // 执行限流
        AppLimitRequest limitRequest = AppLimitRequest.of(request.getAppUid(), StringUtils.isBlank(request.getScene()) ? AppSceneEnum.WEB_IMAGE.name() : request.getScene());
        appLimitService.appLimit(limitRequest);
        return CommonResult.success(imageService.generateImage(request));
    }

    @PostMapping(value = "/removeBackground", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "去除背景", description = "去除背景")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<ClipDropImage> removeBackground(@Validated ImageFileClipDropRequest request) {
        ImageResponse<ClipDropImage> response = clipDropImageService.removeBackground(request);
        return CommonResult.success(response.getResult());
    }

}
