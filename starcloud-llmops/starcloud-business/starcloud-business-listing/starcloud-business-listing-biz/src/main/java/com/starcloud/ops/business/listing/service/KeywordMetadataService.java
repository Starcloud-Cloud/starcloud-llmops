package com.starcloud.ops.business.listing.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.listing.controller.admin.vo.request.QueryKeywordMetadataPageReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.SellerSpriteListingVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.KeywordMetadataBasicRespVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.KeywordMetadataRespVO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.ExtendAsinReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.PrepareReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.ExtendAsinRequestDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.PrepareRequestDTO;

import java.util.List;

/**
 * 关键词原数据Service
 */
public interface KeywordMetadataService {

    /**
     * 查询-原数据根据关键词和站点关键词
     *
     * @param pageReqVO
     * @return
     */
    PageResult<KeywordMetadataRespVO> queryMetaData(QueryKeywordMetadataPageReqVO pageReqVO);


    /**
     * 新增原数据 -根据关键词和站点关键词
     *
     * @param keywordList 关键词
     * @param marketName
     * @return
     */
    Boolean addMetaData(List<String> keywordList, String marketName);



    /**
     * 新增原数据 -根据关键词和站点关键词
     *
     * @param keywordList 关键词
     * @param marketName
     * @return
     */
    List<KeywordMetadataBasicRespVO> getKeywordsBasic(List<String> keywordList, String marketName);


    /**
     * 新增原数据 -根据关键词和站点关键词
     *
     * @param asin 关键词
     * @param marketName
     * @return
     */
    SellerSpriteListingVO getListingByAsin(String asin, String marketName);

    /**
     * 根据 ASIN获取变体
     * @param prepareRequestDTO
     * @return PrepareReposeDTO
     */
    PrepareReposeDTO extendPrepare(PrepareRequestDTO prepareRequestDTO);


    /**
     * 根据 ASIN获取关键词拓展数据
     *
     * @param extendAsinRequestDTO
     * @return ExtendAsinReposeDTO
     */
    ExtendAsinReposeDTO extendAsin(ExtendAsinRequestDTO extendAsinRequestDTO);

}
