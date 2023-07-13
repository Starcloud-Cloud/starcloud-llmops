package com.starcloud.ops.business.app.controller.admin.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.app.feign.response.VSearchImage;
import com.starcloud.ops.business.app.feign.request.VSearchImageRequest;
import com.starcloud.ops.business.app.feign.response.VSearchResponse;
import com.starcloud.ops.business.app.feign.VSearchClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-12
 */
@RestController
@RequestMapping("/admin/app/text")
@Tag(name = "TextController", description = "文本")
public class TextController {

    @Resource
    private VSearchClient vSearchClient;

    @Operation(summary = "hello")
    @GetMapping("/hello")
    public String hello() {
        return vSearchClient.hello();
    }

    @Operation(summary = "生成图片")
    @PostMapping("/generateImage")
    public CommonResult<List<VSearchImage>> stabilityImage(@Validated @RequestBody VSearchImageRequest request) {
        VSearchResponse<List<VSearchImage>> response = vSearchClient.generateImage(request);
        if (response.getSuccess()) {
            return CommonResult.success(response.getResult());
        }
        return CommonResult.error(response.getCode(), response.getMessage());
    }
}
