package com.starcloud.ops.business.listing.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DictCreateReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DictModifyReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DictPageReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DictRespVO;
import com.starcloud.ops.business.listing.convert.ListingDictConvert;
import com.starcloud.ops.business.listing.dal.dataobject.ListingDictDO;
import com.starcloud.ops.business.listing.dal.mysql.ListingDictMapper;
import com.starcloud.ops.business.listing.dto.KeywordResumeDTO;
import com.starcloud.ops.business.listing.enums.AnalysisStatusEnum;
import com.starcloud.ops.business.listing.service.DictService;
import com.starcloud.ops.business.listing.service.KeywordAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.listing.enums.ErrorCodeConstant.*;

@Slf4j
@Service
public class DictServiceImpl implements DictService {

    @Resource
    private ListingDictMapper dictMapper;

    @Resource
    private KeywordAnalysisService keywordMetadataService;

    @Resource(name = "listingExecutor")
    private ThreadPoolTaskExecutor executor;


    @Override
    public DictRespVO create(DictCreateReqVO reqVO) {
        ListingDictDO dictDO = dictMapper.getByName(reqVO.getName());
        if (dictDO != null) {
            throw exception(DICT_NAME_EXISTS, reqVO.getName());
        }
        ListingDictDO listingDictDO = ListingDictConvert.INSTANCE.convert(reqVO);
        dictMapper.insert(listingDictDO);

        List<KeywordResumeDTO> keys = reqVO.getKeywordResume();
        if (CollectionUtil.isNotEmpty(keys)) {
            execute(keys, listingDictDO);
        }
        return ListingDictConvert.INSTANCE.convert(listingDictDO);
    }

    @Override
    public PageResult<DictRespVO> getDictPage(DictPageReqVO dictPageReqVO) {
        PageResult<ListingDictDO> page = dictMapper.page(dictPageReqVO);
        return ListingDictConvert.INSTANCE.convert(page);
    }


    @Override
    public void modify(DictModifyReqVO modifyReqVO) {
        ListingDictDO dictDO = getDict(modifyReqVO.getUid());
        if (AnalysisStatusEnum.ANALYSIS.name().equals(dictDO.getStatus())) {
            throw exception(KEYWORD_IS_ANALYSIS, modifyReqVO.getUid());
        }

        List<KeywordResumeDTO> keys = modifyReqVO.getKeywordResume();
        if (CollectionUtil.isNotEmpty(keys)) {
            execute(keys, dictDO);
        } else {
            ListingDictConvert.INSTANCE.updateParams(modifyReqVO, dictDO);
            dictMapper.updateById(dictDO);
        }
    }

    @Override
    public void deleteDict(List<String> uids) {
        dictMapper.delete(uids);
    }

    @Override
    public DictRespVO dictDetail(String uid) {
        ListingDictDO dictDO = getDict(uid);
        return ListingDictConvert.INSTANCE.convert(dictDO);
    }


    // metadata
    private void method() {
        // 关键词元数据list todo
//        List<KeywordMetaDataDTO> metaData = keywordMetadataService.getMetaData(respVO.getKeywordResume());
//
    }

    @Override
    public void addKeyword(DictModifyReqVO modifyReqVO) {
        ListingDictDO dictDO = getDict(modifyReqVO.getUid());
        if (CollectionUtil.isEmpty(modifyReqVO.getKeywordResume())) {
            return;
        }
        List<KeywordResumeDTO> oldKey = JSONUtil.parseArray(dictDO.getKeywordResume()).toList(KeywordResumeDTO.class);
        CollectionUtil.addAll(modifyReqVO.getKeywordResume(), oldKey);
        execute(modifyReqVO.getKeywordResume(),dictDO);
    }

    private void execute(List<KeywordResumeDTO> keys, ListingDictDO dictDO) {
        List<KeywordResumeDTO> keywords = keys.stream().peek(keywordDTO -> keywordDTO.setKeyword(keywordDTO.getKeyword().trim()))
                .distinct().collect(Collectors.toList());
        dictDO.setStatus(AnalysisStatusEnum.ANALYSIS.name());
        dictDO.setKeywordResume(JSONUtil.toJsonStr(keywords));
        dictMapper.updateById(dictDO);
        executor.execute(() -> {
            try {
                keywordMetadataService.analysisKeyword(keywords);
                dictDO.setStatus(AnalysisStatusEnum.ANALYSIS_END.name());
                dictMapper.updateById(dictDO);
            } catch (Exception e) {
                log.error("分析关键词失败", e);
                dictDO.setStatus(AnalysisStatusEnum.ANALYSIS_ERROR.name());
                dictMapper.updateById(dictDO);
            }
        });
    }

    private ListingDictDO getDict(String uid) {
        ListingDictDO dictDO = dictMapper.getByUid(uid);
        if (dictDO == null) {
            throw exception(DICT_NOT_EXISTS, uid);
        }
        return dictDO;
    }
}
