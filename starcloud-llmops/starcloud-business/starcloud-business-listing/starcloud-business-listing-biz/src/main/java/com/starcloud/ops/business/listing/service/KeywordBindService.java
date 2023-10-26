package com.starcloud.ops.business.listing.service;

import com.starcloud.ops.business.listing.dto.KeywordMetaDataDTO;

import java.util.List;

public interface KeywordBindService {

    /**
     * 分析关键词
     *
     * @param keys
     * @param endpoint {@link com.starcloud.ops.business.listing.enums.SellerSpriteMarketEnum}
     */
    void analysisKeyword(List<String> keys, String endpoint);

    /**
     * 查询关键词
     *
     * @param keys
     * @param endpoint {@link com.starcloud.ops.business.listing.enums.SellerSpriteMarketEnum}
     * @return
     */
    List<KeywordMetaDataDTO> getMetaData(List<String> keys, String endpoint);

    /**
     * 查询关键词元数据 按搜索量排序
     *
     * @param draftId
     * @param endpoint
     * @return
     */
    List<KeywordMetaDataDTO> getMetaData(Long draftId, String endpoint);


    /**
     * 草稿新增关键词
     *
     * @param keys
     * @param draftId
     */
    void addDraftKeyword(List<String> keys, Long draftId);

    /**
     * 词库新增关键词
     *
     * @param keys
     * @param dictId
     */
    void addDictKeyword(List<String> keys, Long dictId);


}
