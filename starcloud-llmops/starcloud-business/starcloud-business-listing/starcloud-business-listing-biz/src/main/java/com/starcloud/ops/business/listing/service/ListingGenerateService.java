package com.starcloud.ops.business.listing.service;

import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.listing.vo.ListingGenerateRequest;

import java.util.List;

/**
 * Listing 生成服务，用于生成 Listing 标题，五点描述，产品描述等
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-26
 */
public interface ListingGenerateService {

    /**
     * 根据应用标签获取应用
     *
     * @param tags 应用标签
     * @return 应用
     */
    AppMarketRespVO getApp(List<String> tags);

    void asyncGenerate(ListingGenerateRequest request);

    /**
     * 异步生成 Listing 标题
     *
     * @param request 生成请求
     */
    void asyncGenerateTitle(ListingGenerateRequest request);

    /**
     * 异步生成 Listing 五点描述
     *
     * @param request 生成请求
     */
    void asyncGenerateFivePoint(ListingGenerateRequest request);

    /**
     * 异步生成 Listing 产品描述
     *
     * @param request 生成请求
     */
    void asyncGenerateProductDescription(ListingGenerateRequest request);


}
