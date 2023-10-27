package com.starcloud.ops.business.listing.service.impl;

import com.starcloud.ops.business.app.api.market.vo.request.AppMarketQuery;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.listing.enums.ListingGenerateTypeEnum;
import com.starcloud.ops.business.listing.service.ListingGenerateService;
import com.starcloud.ops.business.listing.vo.ListingGenerateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Listing 生成服务，用于生成 Listing 标题，五点描述，产品描述等
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-26
 */
@Slf4j
@Service
public class ListingGenerateServiceImpl implements ListingGenerateService {

    @Resource
    private AppService appService;

    @Resource
    private AppMarketService appMarketService;

    /**
     * 根据应用标签获取应用
     *
     * @param tags 应用标签
     * @return 应用
     */
    @Override
    public AppMarketRespVO getApp(List<String> tags) {
        AppMarketQuery query = new AppMarketQuery();
        query.setType(AppTypeEnum.SYSTEM.name());
        query.setModel(AppModelEnum.COMPLETION.name());
        query.setTags(tags);
        return appMarketService.get(query);
    }

    @Override
    public void asyncGenerate(ListingGenerateRequest request) {
        AppMarketRespVO app = this.getApp(request.getTags());
        AppExecuteReqVO executeRequest = buildExecuteRequest(request, app);
        appService.asyncExecute(executeRequest);
    }

    /**
     * 异步生成 Listing 标题
     *
     * @param request 生成请求
     */
    @Override
    public void asyncGenerateTitle(ListingGenerateRequest request) {
        AppMarketRespVO app = this.getApp(ListingGenerateTypeEnum.TITLE.getTags());
        AppExecuteReqVO executeRequest = buildExecuteRequest(request, app);
        appService.asyncExecute(executeRequest);
    }


    /**
     * 异步生成 Listing 五点描述
     *
     * @param request 生成请求
     */
    @Override
    public void asyncGenerateFivePoint(ListingGenerateRequest request) {
        AppMarketRespVO app = this.getApp(ListingGenerateTypeEnum.FIVE_POINTS.getTags());

    }

    /**
     * 异步生成 Listing 产品描述
     *
     * @param request 生成请求
     */
    @Override
    public void asyncGenerateProductDescription(ListingGenerateRequest request) {
        AppMarketRespVO app = this.getApp(ListingGenerateTypeEnum.PRODUCT_DESCRIPTION.getTags());

    }

    /**
     * 构建执行请求
     *
     * @param request 请求
     * @param app     应用
     * @return 执行请求
     */
    private AppExecuteReqVO buildExecuteRequest(ListingGenerateRequest request, AppMarketRespVO app) {
        AppExecuteReqVO executeRequest = new AppExecuteReqVO();
        executeRequest.setScene(AppSceneEnum.LISTING_GENERATE.name());
        executeRequest.setAppUid(app.getUid());
        executeRequest.setAiModel(request.getAiModel());
//        executeRequest.setAppReqVO(AppMarketConvert.INSTANCE.convert(app));
        return executeRequest;
    }

}
