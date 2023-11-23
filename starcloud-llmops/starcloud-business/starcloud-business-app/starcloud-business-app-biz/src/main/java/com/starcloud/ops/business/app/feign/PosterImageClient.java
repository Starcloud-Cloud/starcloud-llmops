package com.starcloud.ops.business.app.feign;

import com.starcloud.ops.business.app.feign.dto.PosterDTO;
import com.starcloud.ops.business.app.feign.dto.PosterTemplateDTO;
import com.starcloud.ops.business.app.feign.request.poster.PosterRequest;
import com.starcloud.ops.business.app.feign.response.PosterResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@FeignClient(name = "${starcloud.feign.poster-image.url}", url = "${starcloud.feign.poster-image.url}", path = "/api")
public interface PosterImageClient {

    /**
     * 获取模板列表
     *
     * @return 模板列表
     */
    @GetMapping(value = "/template-exclude-json")
    PosterResponse<List<PosterTemplateDTO>> templates();

    /**
     * 生成海报
     *
     * @return 海报
     */
    @PostMapping(value = "/poster")
    PosterResponse<PosterDTO> poster(PosterRequest request);

}
