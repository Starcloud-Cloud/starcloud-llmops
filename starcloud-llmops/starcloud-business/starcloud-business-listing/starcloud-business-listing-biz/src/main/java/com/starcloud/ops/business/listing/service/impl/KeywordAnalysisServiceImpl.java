package com.starcloud.ops.business.listing.service.impl;

import com.starcloud.ops.business.listing.dto.KeywordMetaDataDTO;
import com.starcloud.ops.business.listing.dto.KeywordResumeDTO;
import com.starcloud.ops.business.listing.service.KeywordAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class KeywordAnalysisServiceImpl implements KeywordAnalysisService {
    @Override
    public void analysisKeyword(List<KeywordResumeDTO> keys) {

    }

    @Override
    public List<KeywordMetaDataDTO> getMetaData(List<KeywordResumeDTO> keywordResume) {
        return null;
    }
}
