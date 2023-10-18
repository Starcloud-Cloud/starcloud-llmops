package com.starcloud.ops.business.listing.service;

import com.starcloud.ops.business.listing.dto.KeywordMetaDataDTO;
import com.starcloud.ops.business.listing.dto.KeywordResumeDTO;

import java.util.List;

public interface KeywordAnalysisService {

    /**
     * 分析关键词
     *
     * @param keys
     */
    void analysisKeyword(List<KeywordResumeDTO> keys);

    /**
     * 查询关键词元数据
     * @param keywordResume
     * @return
     */
    List<KeywordMetaDataDTO> getMetaData(List<KeywordResumeDTO> keywordResume);

}
