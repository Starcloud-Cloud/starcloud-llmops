package com.starcloud.ops.business.listing.service.sellersprite;

import com.starcloud.ops.business.listing.controller.admin.vo.request.SellerSpriteListingVO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.ExtendAsinReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.KeywordMinerReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.PrepareReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.ExtendAsinRequestDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.PrepareRequestDTO;

import java.util.List;

/**
 * 卖家精灵接口
 */
public interface SellerSpriteService {

    /**
     * 获取可查询时间
     */
    void getDateList();

    /**
     * 关键词挖掘- 根据【单个】关键词获取数据
     * @param keyword
     * @param market
     */
    KeywordMinerReposeDTO keywordMiner(String keyword, Integer market);

    /**
     * 关键词挖掘- 根据【批量】关键词获取数据
     * @param keywordS
     * @param market
     * @return
     */
    KeywordMinerReposeDTO BatchKeywordMiner(List<String> keywordS,Integer market);

    /**
     * 关键词反查
     */
    void keywordReversing();

    /**
     * 根据 ASIN 获取变种
     */
    PrepareReposeDTO extendPrepare(PrepareRequestDTO prepareRequestDTO);

    /**
     * 根据 ASIN 拓展流量词
     */
    ExtendAsinReposeDTO extendAsin(ExtendAsinRequestDTO extendAsinRequestDTO);

    /**
     * 根据 ASIN 获取 Listing
     */
    SellerSpriteListingVO getListingByAsin(String asin, Integer market);

    /**
     * 品牌检测
     */
    void checkBrand();


    /**
     * 品牌检测
     */
    void AutoUpdateCheckCookies();

}
