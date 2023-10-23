package com.starcloud.ops.business.listing.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.listing.controller.admin.vo.request.QueryKeywordMetadataPageReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.SellerSpriteListingVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.KeywordMetadataBasicRespVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.KeywordMetadataRespVO;

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

}
