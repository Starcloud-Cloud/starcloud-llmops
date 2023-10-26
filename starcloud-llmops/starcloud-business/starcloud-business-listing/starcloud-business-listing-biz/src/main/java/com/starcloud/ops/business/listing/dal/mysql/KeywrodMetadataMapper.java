package com.starcloud.ops.business.listing.dal.mysql;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.starcloud.ops.business.listing.controller.admin.vo.request.QueryKeywordMetadataPageReqVO;
import com.starcloud.ops.business.listing.dal.dataobject.KeywordMetadataDO;
import com.starcloud.ops.business.listing.enums.SellerSpriteMarketEnum;
import com.starcloud.ops.business.listing.enums.SellerSpriteOrderByEnum;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KeywrodMetadataMapper extends BaseMapperX<KeywordMetadataDO> {
    default PageResult<KeywordMetadataDO> selectPage(QueryKeywordMetadataPageReqVO reqVO) {
        LambdaQueryWrapper<KeywordMetadataDO> queryWrapper = new LambdaQueryWrapperX<KeywordMetadataDO>()
                .eqIfPresent(KeywordMetadataDO::getMarketId, SellerSpriteMarketEnum.valueOf(reqVO.getMarketName()).getCode())
                .inIfPresent(KeywordMetadataDO::getKeyword, reqVO.getIncludeKeywords())
                .betweenIfPresent(KeywordMetadataDO::getSearchRank, reqVO.getMinSearchRank(), reqVO.getMaxSearchRank())
                .betweenIfPresent(KeywordMetadataDO::getSearches, reqVO.getMinSearch(), reqVO.getMaxSearch())
                .betweenIfPresent(KeywordMetadataDO::getPurchases, reqVO.getMinPurchases(), reqVO.getMaxPurchases())
                .betweenIfPresent(KeywordMetadataDO::getPurchaseRate, reqVO.getMinPurchasesRate(), reqVO.getMaxPurchasesRate())
                .betweenIfPresent(KeywordMetadataDO::getSpr, reqVO.getMinSPR(), reqVO.getMaxSPR())
                .betweenIfPresent(KeywordMetadataDO::getTitleDensity, reqVO.getMinTitleDensity(), reqVO.getMaxTitleDensity())
                .betweenIfPresent(KeywordMetadataDO::getProducts, reqVO.getMinProducts(), reqVO.getMaxProducts())
                .betweenIfPresent(KeywordMetadataDO::getSupplyDemandRatio, reqVO.getMinSupplyDemandRatio(), reqVO.getMaxSupplyDemandRatio())
                .betweenIfPresent(KeywordMetadataDO::getAdProducts, reqVO.getMinAdProducts(), reqVO.getMaxAdProducts())
                .betweenIfPresent(KeywordMetadataDO::getMonopolyClickRate, reqVO.getMinMonopolyClickRate(), reqVO.getMaxMonopolyClickRate())
                .betweenIfPresent(KeywordMetadataDO::getBid, reqVO.getMinBid(), reqVO.getMaxBid())
                .betweenIfPresent(KeywordMetadataDO::getWordCount, reqVO.getMinWordCount(), reqVO.getMaxWordCount())
                .eqIfPresent(KeywordMetadataDO::getAmazonChoice, reqVO.getAmazonChoice())
                .notIn(CollUtil.isNotEmpty(reqVO.getExcludeKeywords()), KeywordMetadataDO::getKeyword, reqVO.getExcludeKeywords());
        if (reqVO.getOrderColumn()!=null&&reqVO.getOrderColumn()>0){
            SellerSpriteOrderByEnum byCode = SellerSpriteOrderByEnum.getByCode(reqVO.getOrderColumn());
            if (reqVO.getDesc()) {
                queryWrapper.orderByDesc(byCode.getOrderColumn());
            } else {
                queryWrapper.orderByAsc(byCode.getOrderColumn());
            }
        }else {
            queryWrapper.orderByDesc(KeywordMetadataDO::getId);
        }


        return selectPage(reqVO, queryWrapper);
    }
}
