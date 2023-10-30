package com.starcloud.ops.business.listing.service;

import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.listing.vo.ListingGenerateRequest;
import com.starcloud.ops.framework.common.api.dto.Option;

import java.util.List;
import java.util.Map;

/**
 * Listing 生成服务，用于生成 Listing 标题，五点描述，产品描述等
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-26
 */
public interface ListingGenerateService {

    /**
     * Listing 生成元数据
     *
     * @return Listing 生成元数据
     */
    Map<String, List<Option>> metadata();

    /**
     * 根据应用标签获取应用
     *
     * @param tags 应用标签
     * @return 应用
     */
    AppMarketRespVO getApp(List<String> tags);

    /**
     * 异步执行AI生成Listing标题或者五点描述或者产品描述等
     *
     * @param request 请求
     */
    void asyncExecute(ListingGenerateRequest request);


}
