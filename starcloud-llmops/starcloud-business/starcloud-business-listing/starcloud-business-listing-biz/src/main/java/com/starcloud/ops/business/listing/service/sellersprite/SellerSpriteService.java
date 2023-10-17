package com.starcloud.ops.business.listing.service.sellersprite;

import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.GetPrepareDTO;

/**
 * 卖家精灵接口
 */
public interface SellerSpriteService {

    /**
     *  获取可查询时间
     */
    void getDateList();

    /**
     * 关键词挖掘- 根据关键词获取数据
     */
    void keywordMiner();

    /**
     * 关键词反查
     */
    void keywordReversing();

    /**
     * 根据 ASIN 获取变种
     */
    void extendPrepare(GetPrepareDTO getPrepareDTO);

    /**
     * 根据 ASIN 拓展流量词
     */
    void extendAsin();

    /**
     * 根据 ASIN 获取 Listing
     */
    void getListingByAsin();

    /**
     *  品牌检测
     */
    void checkBrand();



}
