package com.starcloud.ops.business.app.controller.admin.image;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.image.dto.ImageMetaDTO;
import com.starcloud.ops.business.app.api.image.dto.TextPrompt;
import com.starcloud.ops.business.app.controller.admin.image.vo.task.ImageTask;
import com.starcloud.ops.business.app.controller.admin.image.vo.task.StabilityImageR;
import com.starcloud.ops.business.app.controller.admin.image.vo.task.TaskExecuteRequest;
import com.starcloud.ops.business.app.enums.vsearch.MaskSourceEnum;
import com.starcloud.ops.business.app.enums.vsearch.SamplerEnum;
import com.starcloud.ops.business.app.feign.request.VectorSearchImageRequest;
import com.starcloud.ops.business.app.feign.request.stability.MaskingStabilityImageRequest;
import com.starcloud.ops.business.app.feign.response.VectorSearchImage;
import com.starcloud.ops.business.app.service.image.ProductImageService;
import com.starcloud.ops.business.app.service.image.stability.StabilityImageService;
import com.starcloud.ops.business.app.service.vsearch.VectorSearchService;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-12
 */
@RestController
@RequestMapping("/llm/image/product")
@Tag(name = "星河云海-生成商品图", description = "生成商品图")
public class ProductImageController {

    @Resource
    private ProductImageService productImageService;

    @PostMapping("/metadata")
    @Operation(summary = "获取元数据", description = "获取元数据")
    @ApiOperationSupport(order = 1, author = "nacoyer")
    public CommonResult<Map<String, List<ImageMetaDTO>>> metadata() {
        return CommonResult.success(productImageService.metadata());
    }

    @PostMapping("/list")
    @Operation(summary = "获取任务列表", description = "获取任务列表")
    @ApiOperationSupport(order = 2, author = "nacoyer")
    public CommonResult<List<ImageTask>> list() {
        return CommonResult.success(productImageService.list());
    }

    @PostMapping("/get/{taskId}")
    @Operation(summary = "获取任务", description = "获取任务")
    @ApiOperationSupport(order = 3, author = "nacoyer")
    public CommonResult<ImageTask> get(@PathVariable("taskId") String taskId) {
        return CommonResult.success(productImageService.get(taskId));
    }

    @PostMapping("/save")
    @Operation(summary = "保存任务", description = "保存任务")
    @ApiOperationSupport(order = 4, author = "nacoyer")
    public CommonResult<ImageTask> save(ImageTask request) {
        return CommonResult.success(productImageService.save(request));
    }

    @PostMapping("/delete/{taskId}")
    @Operation(summary = "删除任务", description = "删除任务")
    @ApiOperationSupport(order = 5, author = "nacoyer")
    public CommonResult<Boolean> delete(@PathVariable("taskId") String taskId) {
        productImageService.delete(taskId);
        return CommonResult.success(true);
    }

    @PostMapping("/execute")
    @Operation(summary = "执行任务", description = "执行任务")
    @ApiOperationSupport(order = 5, author = "nacoyer")
    public CommonResult<ImageTask> execute(@RequestBody TaskExecuteRequest request) {
        ImageTask imageTask = productImageService.get(request.getTaskId());
        if (request.getIsCheck()) {
            return CommonResult.success(imageTask);
        }
        // 异步执行
        productImageService.execute(request);
        return CommonResult.success(productImageService.get(request.getTaskId()));
    }


    @Resource
    private VectorSearchService vectorSearchService;

    @PostMapping(value = "/mask", consumes = "multipart/form-data")
    @Operation(summary = "执行任务", description = "执行任务")
    @ApiOperationSupport(order = 5, author = "nacoyer")
    public CommonResult<List<VectorSearchImage>> mask(StabilityImageR req) throws IOException {
        VectorSearchImageRequest request = new VectorSearchImageRequest();
        if (req.getInitImage() != null) {
            // initImage 转为 base64
            String init = Base64.getEncoder().encodeToString(req.getInitImage().getBytes());
            request.setInitImage(init);
        }

        if (req.getMaskImage() != null) {
            // maskImage 转为 base64
            String mask = Base64.getEncoder().encodeToString(req.getMaskImage().getBytes());
            request.setMaskImage(mask);
        }


        List<TextPrompt> prompts = new ArrayList<>();

        TextPrompt textPrompt = new TextPrompt();
        textPrompt.setText(req.getPrompt());
        textPrompt.setWeight(1.0);
        prompts.add(textPrompt);

        request.setEngine(req.getEngine());
        request.setPrompts(prompts);


        request.setStartSchedule(req.getStartSchedule());
        request.setHeight(req.getHeight());
        request.setWidth(req.getWidth());
        request.setSteps(req.getSteps());
        request.setCfgScale(8.0);
        request.setSampler(req.getSampler());
        request.setSamples(req.getSamples());

        return CommonResult.success(vectorSearchService.generateImage(request));
    }

    @Resource
    private StabilityImageService stabilityImageService;

    @PostMapping(value = "/mask2", consumes = "multipart/form-data")
    @Operation(summary = "执行任务", description = "执行任务")
    @ApiOperationSupport(order = 5, author = "nacoyer")
    public CommonResult<String> mask2(StabilityImageR req) throws IOException {
        MaskingStabilityImageRequest request = new MaskingStabilityImageRequest();
        if (req.getInitImage() != null) {
            // initImage 转为 base64
            String init = Base64.getEncoder().encodeToString(req.getInitImage().getBytes());
            request.setInitImage(init);
        }

        request.setMaskSource(MaskSourceEnum.MASK_IMAGE_WHITE.name());
        if (req.getMaskImage() != null) {
            // maskImage 转为 base64
            String mask = Base64.getEncoder().encodeToString(req.getMaskImage().getBytes());
            request.setMaskImage(mask);
        }

        List<TextPrompt> prompts = new ArrayList<>();

        TextPrompt textPrompt = new TextPrompt();
        textPrompt.setText(req.getPrompt());
        textPrompt.setWeight(1.0);
        prompts.add(textPrompt);

        request.setTextPrompts(prompts);
        request.setSteps(req.getSteps());
        request.setCfgScale(req.getCfgScale());
        request.setSampler(IEnumable.codeOf(req.getSampler(), SamplerEnum.class).name());
        request.setSamples(req.getSamples());
        stabilityImageService.masking(req.getEngine(), request);
        return CommonResult.success("success");
    }
}
