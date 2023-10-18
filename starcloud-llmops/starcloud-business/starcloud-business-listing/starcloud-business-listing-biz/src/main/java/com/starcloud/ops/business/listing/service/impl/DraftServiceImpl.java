package com.starcloud.ops.business.listing.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.PageUtils;
import com.starcloud.ops.business.listing.controller.admin.vo.request.*;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DraftDetailExcelVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DraftRespVO;
import com.starcloud.ops.business.listing.convert.ListingDraftConvert;
import com.starcloud.ops.business.listing.dal.dataobject.ListingDraftDO;
import com.starcloud.ops.business.listing.dal.mysql.ListingDraftMapper;
import com.starcloud.ops.business.listing.dto.KeywordMetaDataDTO;
import com.starcloud.ops.business.listing.dto.KeywordResumeDTO;
import com.starcloud.ops.business.listing.enums.AnalysisStatusEnum;
import com.starcloud.ops.business.listing.enums.DraftSortFieldEnum;
import com.starcloud.ops.business.listing.service.DraftService;
import com.starcloud.ops.business.listing.service.KeywordAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.listing.enums.ErrorCodeConstant.DRAFT_NOT_EXISTS;
import static com.starcloud.ops.business.listing.enums.ErrorCodeConstant.KEYWORD_IS_ANALYSIS;

@Slf4j
@Service
public class DraftServiceImpl implements DraftService {

    @Resource
    private ListingDraftMapper draftMapper;

    @Resource
    private KeywordAnalysisService keywordMetadataService;

    @Resource(name = "listingExecutor")
    private ThreadPoolTaskExecutor executor;

    @Override
    public DraftRespVO create(DraftCreateReqVO reqVO) {
        ListingDraftDO draftDO = ListingDraftConvert.INSTANCE.convert(reqVO);
        draftDO.setVersion(1);
        draftMapper.insert(draftDO);
        return ListingDraftConvert.INSTANCE.convert(draftDO);
    }

    @Override
    public PageResult<DraftRespVO> getDraftPage(DraftPageReqVO pageParam) {
        List<ListingDraftDO> latestDrafts = draftMapper.getLatestDrafts(PageUtils.getStart(pageParam),
                pageParam.getPageSize(),
                DraftSortFieldEnum.getColumn(pageParam.getSortField()),
                BooleanUtil.isTrue(pageParam.getAsc()) ? "ASC" : "DESC");
        Long count = draftMapper.count();
        return new PageResult<>(ListingDraftConvert.INSTANCE.convert(latestDrafts), count);
    }

    @Override
    public List<DraftRespVO> listVersion(String uid) {
        List<ListingDraftDO> listingDraft = draftMapper.listVersion(uid);
        return ListingDraftConvert.INSTANCE.convert(listingDraft);
    }

    @Override
    public DraftRespVO detail(String uid, Integer version) {
        ListingDraftDO draftDO = getVersion(uid, version);
        DraftRespVO respVO = ListingDraftConvert.INSTANCE.convert(draftDO);
        List<KeywordMetaDataDTO> metaData = keywordMetadataService.getMetaData(respVO.getKeywordResume());
        respVO.setKeywordMetaData(metaData);
        return respVO;
    }

    @Override
    public void saveDraft(DraftSaveReqVO reqVO) {
        ListingDraftDO latest = draftMapper.getLatest(reqVO.getUid());
        ListingDraftDO draftDO = getVersion(reqVO.getUid(), reqVO.getVersion());
        ListingDraftConvert.INSTANCE.update(reqVO, draftDO);
        draftDO.setVersion(Math.addExact(latest.getVersion(), 1));
        draftMapper.updateById(draftDO);
    }


    @Override
    public List<DraftDetailExcelVO> export(List<DraftOperationReqVO> operationReq) {


        return Collections.emptyList();
    }

    @Override
    public void delete(List<DraftOperationReqVO> operationReq) {
        for (DraftOperationReqVO draftOperationReqVO : operationReq) {
            draftMapper.delete(draftOperationReqVO);
        }
    }

    @Override
    public void addKeyword(DraftSaveReqVO reqVO) {
        if (CollectionUtils.isEmpty(reqVO.getKeywordResume())) {
            throw exception(new ErrorCode(500,"关键词不能为空"));
        }

        ListingDraftDO draftDO = getVersion(reqVO.getUid(), reqVO.getVersion());
        if (AnalysisStatusEnum.ANALYSIS.name().equals(draftDO.getStatus())
                || AnalysisStatusEnum.EXECUTING.name().equals(draftDO.getStatus())) {
            throw exception(KEYWORD_IS_ANALYSIS);
        }
        List<KeywordResumeDTO> oldKey = JSONUtil.parseArray(draftDO.getKeywordResume()).toList(KeywordResumeDTO.class);
        List<KeywordResumeDTO> newKey = reqVO.getKeywordResume().stream().map(String::trim).distinct().map(KeywordResumeDTO::new).collect(Collectors.toList());
        List<KeywordResumeDTO> keywordResume = CollectionUtil.addAll(newKey, oldKey).stream().distinct().collect(Collectors.toList());

        draftDO.setStatus(AnalysisStatusEnum.ANALYSIS.name());
        draftDO.setKeywordResume(JSONUtil.toJsonStr(keywordResume));
        draftMapper.updateById(draftDO);
        executor.execute(() -> {
            keywordMetadataService.analysisKeyword(keywordResume);
            draftDO.setKeywordResume(JSONUtil.toJsonStr(keywordResume));
            draftDO.setStatus(AnalysisStatusEnum.ANALYSIS_END.name());
            draftMapper.updateById(draftDO);
        });
    }

    @Override
    public void batchExecute(List<DraftOperationReqVO> operationReq) {
        for (DraftOperationReqVO draftOperationReqVO : operationReq) {
            ListingDraftDO draftDO = getVersion(draftOperationReqVO.getUid(), draftOperationReqVO.getVersion());
            if (AnalysisStatusEnum.ANALYSIS.name().equals(draftDO.getStatus())
                    || AnalysisStatusEnum.EXECUTING.name().equals(draftDO.getStatus())) {

            }

        }
    }

    private ListingDraftDO getVersion(String uid, Integer version) {
        ListingDraftDO draftDO = draftMapper.getVersion(uid, version);
        if (draftDO == null) {
            throw exception(DRAFT_NOT_EXISTS, uid, version);
        }
        return draftDO;
    }
}
