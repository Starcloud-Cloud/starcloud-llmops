package com.starcloud.ops.business.listing.service.sellersprite;

import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.ExtendAsinReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.KeywordMinerReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.PrepareRepose;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.ExtendAsinRequestDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.KeywordMinerRequestDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.PrepareRequestDTO;

/**
 * 卖家精灵接口
 */
public interface SellerSpriteService {

    /**
     * 获取可查询时间
     */
    void getDateList();

    /**
     * 关键词挖掘- 根据关键词获取数据
     */
    KeywordMinerReposeDTO keywordMiner(KeywordMinerRequestDTO keywordMinerRequestDTO);

    /**
     * 关键词反查
     */
    void keywordReversing();

    /**
     * 根据 ASIN 获取变种
     */
    PrepareRepose extendPrepare(PrepareRequestDTO prepareRequestDTO);

    /**
     * 根据 ASIN 拓展流量词
     */
    ExtendAsinReposeDTO extendAsin(ExtendAsinRequestDTO extendAsinRequestDTO);

    /**
     * 根据 ASIN 获取 Listing
     */
    void getListingByAsin();

    /**
     * 品牌检测
     */
    void checkBrand();


}
