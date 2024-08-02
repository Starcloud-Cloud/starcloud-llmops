package com.starcloud.ops.business.app.controller.admin.image;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.image.dto.ImageMetaDTO;
import com.starcloud.ops.business.app.api.image.dto.UploadImageInfoDTO;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageReqVO;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageRespVO;
import com.starcloud.ops.business.app.enums.RecommendAppEnum;
import com.starcloud.ops.business.app.service.image.ImageService;
import com.starcloud.ops.business.app.service.image.impl.PixabayServiceImpl;
import com.starcloud.ops.business.app.service.image.impl.dto.repose.PixabayImageResult;
import com.starcloud.ops.business.app.service.image.impl.dto.request.PixabayImageRequestDTO;
import com.starcloud.ops.business.app.service.limit.AppLimitRequest;
import com.starcloud.ops.business.app.service.limit.AppLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    private  PixabayServiceImpl pixabayService;

    @GetMapping("/meta")
    @Operation(summary = "生成图片元数据", description = "生成图片元数据")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<Map<String, List<ImageMetaDTO>>> meta() {
        return CommonResult.success(imageService.meta());
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传图片", description = "上传图片")
    @ApiOperationSupport(order = 30, author = "nacoyer")
    public CommonResult<UploadImageInfoDTO> upload(@RequestPart("image") MultipartFile image) {
        return CommonResult.success(imageService.upload(image));
    }

    @PostMapping(value = "/uploadLimitPixel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传图片,图片大小不能超过 1024 x 1024", description = "上传图片")
    @ApiOperationSupport(order = 30, author = "nacoyer")
    public CommonResult<UploadImageInfoDTO> uploadLimitPixel(@RequestPart("image") MultipartFile image) {
        return CommonResult.success(imageService.uploadLimit1024(image));
    }

    @PostMapping("/generate")
    @Operation(summary = "图片生成接口(文生图，图生图)", description = "图片生成接口(文生图，图生图)")
    @ApiOperationSupport(order = 30, author = "nacoyer")
    public CommonResult<ImageRespVO> textToImage(@Validated @RequestBody ImageReqVO request) {
        request.setAppUid(RecommendAppEnum.GENERATE_IMAGE.name());
        // 执行限流
        AppLimitRequest limitRequest = AppLimitRequest.of(request.getAppUid(), request.getScene());
        appLimitService.appLimit(limitRequest);
        return CommonResult.success(imageService.execute(request));
    }

    @PostMapping(value = "/upscale")
    @Operation(summary = "图片放大接口", description = "图片放大接口")
    @ApiOperationSupport(order = 40, author = "nacoyer")
    public CommonResult<ImageRespVO> upscale(@Validated @RequestBody ImageReqVO request) {
        request.setAppUid(RecommendAppEnum.UPSCALING_IMAGE.name());
        // 执行限流
        AppLimitRequest limitRequest = AppLimitRequest.of(request.getAppUid(), request.getScene());
        appLimitService.appLimit(limitRequest);
        return CommonResult.success(imageService.execute(request));
    }

    @PostMapping(value = "/removeBackground")
    @Operation(summary = "图片去背景接口", description = "图片去背景接口")
    @ApiOperationSupport(order = 50, author = "nacoyer")
    public CommonResult<ImageRespVO> removeBackground(@Validated @RequestBody ImageReqVO request) {
        request.setAppUid(RecommendAppEnum.REMOVE_BACKGROUND_IMAGE.name());
        // 执行限流
        AppLimitRequest limitRequest = AppLimitRequest.of(request.getAppUid(), request.getScene());
        appLimitService.appLimit(limitRequest);
        return CommonResult.success(imageService.execute(request));
    }

    @PostMapping(value = "/removeText")
    @Operation(summary = "图片去字接口", description = "图片去字接口")
    @ApiOperationSupport(order = 60, author = "nacoyer")
    public CommonResult<ImageRespVO> removeText(@Validated @RequestBody ImageReqVO request) {
        request.setAppUid(RecommendAppEnum.REMOVE_TEXT_IMAGE.name());
        AppLimitRequest limitRequest = AppLimitRequest.of(request.getAppUid(), request.getScene());
        appLimitService.appLimit(limitRequest);
        return CommonResult.success(imageService.execute(request));
    }

    @PostMapping(value = "/sketchToImage")
    @Operation(summary = "轮廓生图接口", description = "轮廓生图接口")
    @ApiOperationSupport(order = 60, author = "nacoyer")
    public CommonResult<ImageRespVO> sketchToImage(@Validated @RequestBody ImageReqVO request) {
        request.setAppUid(RecommendAppEnum.SKETCH_TO_IMAGE.name());
        // 执行限流
        AppLimitRequest limitRequest = AppLimitRequest.of(request.getAppUid(), request.getScene());
        appLimitService.appLimit(limitRequest);
        return CommonResult.success(imageService.execute(request));
    }

    @PostMapping(value = "/variants")
    @Operation(summary = "图片裂变", description = "图片裂变")
    @ApiOperationSupport(order = 70, author = "nacoyer")
    public CommonResult<ImageRespVO> variants(@Validated @RequestBody ImageReqVO request) {
        request.setAppUid(RecommendAppEnum.VARIANTS_IMAGE.name());
        // 执行限流
        AppLimitRequest limitRequest = AppLimitRequest.of(request.getAppUid(), request.getScene());
        appLimitService.appLimit(limitRequest);
        return CommonResult.success(imageService.execute(request));
    }


    @PostMapping(value = "/search")
    @Operation(summary = "获取pixabay图片", description = "获取pixabay图片")
    @ApiOperationSupport(order = 80, author = " Cusack Alan")
    public CommonResult<PixabayImageResult> search(@Validated @RequestBody PixabayImageRequestDTO request) {
        return CommonResult.success(pixabayService.getPixabayImage(request));
    }

}
