package com.starcloud.ops.business.app.feign;

import com.starcloud.ops.business.app.feign.dto.video.VideoGeneratorConfig;
import com.starcloud.ops.business.app.feign.dto.video.VideoGeneratorResult;
import com.starcloud.ops.business.app.feign.dto.video.VideoRecordResult;
import com.starcloud.ops.business.app.feign.request.video.ImagePdfRequest;
import com.starcloud.ops.business.app.feign.request.video.WordbookPdfRequest;
import com.starcloud.ops.business.app.feign.response.PdfGeneratorResponse;
import com.starcloud.ops.business.app.feign.response.VideoGeneratorResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@FeignClient(name = "${feign.remote.video.name}", url = "${feign.remote.video.url}", path = "/api")
public interface VideoGeneratorClient {

    /**
     * 执行视频生成
     *
     * @return 海报
     */
    @PostMapping(value = "/v1/videos")
    VideoGeneratorResponse<VideoGeneratorResult> videoGenerator(VideoGeneratorConfig config);

    /**
     * 获取生成结果
     *
     * @return 模板列表
     */
    @GetMapping(value = "/v1/videos/{video_id}")
    VideoGeneratorResponse<VideoRecordResult> getGeneratorResult(@PathVariable("video_id") String video_id);


    /**
     * 服务检测
     *
     * @return 模板列表
     */
    @PostMapping(value = "/v1/health")
    void health();

    /**
     * 生成图片pdf
     *
     * @param request 请求参数
     * @return 生成结果
     */
    @PostMapping(value = "v1/pdf/image")
    VideoGeneratorResponse<PdfGeneratorResponse> generateImagePdf(ImagePdfRequest request);

    /**
     * 生成单词本pdf
     *
     * @param request 请求参数
     * @return 生成结果
     */
    @PostMapping(value = "v1/pdf/wordbook")
    VideoGeneratorResponse<PdfGeneratorResponse> generateWordBookPdf(WordbookPdfRequest request);

}
