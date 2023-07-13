package com.starcloud.ops.business.app.controller.admin.image;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-12
 */

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.image.vo.request.ImageReqVO;
import com.starcloud.ops.business.app.service.image.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 图片生成接口
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@RestController
@RequestMapping("/llm/image")
@Tag(name = "星河云海-应用管理", description = "星河云海应用管理")
public class ImageController {

    @Resource
    private ImageService imageService;

    @GetMapping("/meta")
    @Operation(summary = "生成图片元数据", description = "生成图片元数据")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<String> meta() {
        return CommonResult.success("");
    }

    @PostMapping("/text-to-image")
    @Operation(summary = "文本生成图片", description = "文本生成图片")
    @ApiOperationSupport(order = 20, author = "nacoyer")
    public CommonResult<String> textToImage(ImageReqVO request) {
        imageService.textToImage(request);
        return CommonResult.success("");
    }
}
