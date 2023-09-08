package com.starcloud.ops.business.app.controller.admin.image;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.feign.ClipDropImageClient;
import com.starcloud.ops.business.app.feign.request.clipdrop.UpscaleClipDropRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-07
 */
@Slf4j
@RestController
@RequestMapping("/llm/image/test")
@Tag(name = "星河云海-图片生成测试", description = "图片生成测试")
public class ImageTextController {

    @Resource
    private ClipDropImageClient clipDropImageClient;


    @PostMapping(value = "/upscale", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "放大图片", description = "放大图片")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<Object> upscale(@Validated UpscaleClipDropRequest request) {
        ResponseEntity<byte[]> upscale = clipDropImageClient.upscale(request);
        System.out.println(upscale);
        log.info(JSONUtil.toJsonStr(upscale));
        return CommonResult.success(upscale);
    }

}
