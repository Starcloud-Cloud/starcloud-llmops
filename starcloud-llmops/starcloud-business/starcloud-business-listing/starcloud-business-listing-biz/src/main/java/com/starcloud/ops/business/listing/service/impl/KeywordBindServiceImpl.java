package com.starcloud.ops.business.listing.service.impl;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import com.starcloud.ops.business.listing.controller.admin.vo.response.KeywordMetadataBasicRespVO;
import com.starcloud.ops.business.listing.convert.ListingKeywordConvert;
import com.starcloud.ops.business.listing.dal.dataobject.KeywordBindDO;
import com.starcloud.ops.business.listing.dal.mysql.KeywordBindMapper;
import com.starcloud.ops.business.listing.dto.KeywordMetaDataDTO;
import com.starcloud.ops.business.listing.enums.KeywordBindTypeEnum;
import com.starcloud.ops.business.listing.service.KeywordBindService;
import com.starcloud.ops.business.listing.service.KeywordMetadataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;


@Slf4j
@Service
public class KeywordBindServiceImpl implements KeywordBindService {

    @Resource
    private KeywordBindMapper keywordBindMapper;

    @Resource
    private KeywordMetadataService metadataService;


    @Override
    public void analysisKeyword(List<String> keys, String endpoint) {
        long start = System.currentTimeMillis();
        Boolean success = metadataService.addMetaData(keys, endpoint);
        if (!success) {
            log.warn("分析关键词失败");
            throw exception(new ErrorCode(500, "分析关键词失败"));
        }
        long end = System.currentTimeMillis();
        log.info("分析关键词成功, {} ms", end - start);
    }

    @Override
    public List<KeywordMetaDataDTO> getMetaData(List<String> keys, String endpoint) {
        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptyList();
        }
        long start = System.currentTimeMillis();
        List<KeywordMetadataBasicRespVO> keywordsBasic = metadataService.getKeywordsBasic(keys, endpoint);
        long end = System.currentTimeMillis();
        log.info("查询关键词成功, {} ms", end - start);
        return ListingKeywordConvert.INSTANCE.convert(keywordsBasic);
    }

    @Override
    public void addDraftKeyword(List<String> keys, Long draftId) {
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        List<KeywordBindDO> bindDOList = new ArrayList<>(keys.size());
        for (String key : keys) {
            KeywordBindDO keywordBindDO = new KeywordBindDO();
            keywordBindDO.setKeyword(key);
            keywordBindDO.setDraftId(draftId);
            keywordBindDO.setType(KeywordBindTypeEnum.draft.name());
            keywordBindDO.setEnable(true);
            bindDOList.add(keywordBindDO);
        }
        keywordBindMapper.insertBatch(bindDOList);
    }

    @Override
    public void addDictKeyword(List<String> keys, Long dictId) {
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        List<KeywordBindDO> bindDOList = new ArrayList<>(keys.size());
        for (String key : keys) {
            KeywordBindDO keywordBindDO = new KeywordBindDO();
            keywordBindDO.setKeyword(key);
            keywordBindDO.setDictId(dictId);
            keywordBindDO.setType(KeywordBindTypeEnum.dict.name());
            keywordBindDO.setEnable(true);
            bindDOList.add(keywordBindDO);
        }
        keywordBindMapper.insertBatch(bindDOList);
    }
}
