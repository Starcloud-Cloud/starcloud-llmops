package com.starcloud.ops.business.listing.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.PageUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.starcloud.ops.business.listing.controller.admin.vo.request.*;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DictRespVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DraftDetailExcelVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DraftRespVO;
import com.starcloud.ops.business.listing.convert.ListingDraftConvert;
import com.starcloud.ops.business.listing.convert.ListingKeywordConvert;
import com.starcloud.ops.business.listing.dal.dataobject.KeywordBindDO;
import com.starcloud.ops.business.listing.dal.dataobject.ListingDraftDO;
import com.starcloud.ops.business.listing.dal.mysql.KeywordBindMapper;
import com.starcloud.ops.business.listing.dal.mysql.ListingDraftMapper;
import com.starcloud.ops.business.listing.dto.DraftConfigDTO;
import com.starcloud.ops.business.listing.dto.DraftContentConfigDTO;
import com.starcloud.ops.business.listing.dto.DraftItemScoreDTO;
import com.starcloud.ops.business.listing.dto.KeywordMetaDataDTO;
import com.starcloud.ops.business.listing.enums.AnalysisStatusEnum;
import com.starcloud.ops.business.listing.enums.DraftSortFieldEnum;
import com.starcloud.ops.business.listing.service.DictService;
import com.starcloud.ops.business.listing.service.DraftService;
import com.starcloud.ops.business.listing.service.KeywordBindService;
import com.starcloud.ops.business.listing.utils.ListingDraftScoreUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.listing.enums.ErrorCodeConstant.*;

@Slf4j
@Service
public class DraftServiceImpl implements DraftService {

    @Resource
    private ListingDraftMapper draftMapper;

    @Resource
    private KeywordBindService keywordBindService;

    @Resource
    private DictService dictService;

    @Resource
    private KeywordBindMapper keywordBindMapper;

    @Resource(name = "listingExecutor")
    private ThreadPoolTaskExecutor executor;

    @Override
    public DraftRespVO create(DraftCreateReqVO reqVO) {
        ListingDraftDO draftDO = ListingDraftConvert.INSTANCE.convert(reqVO);
        draftDO.setVersion(1);
        draftDO.setUid(IdUtil.fastSimpleUUID());
        draftDO.setStatus(AnalysisStatusEnum.ANALYSIS_END.name());
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

        List<String> keywordBinds = keywordBindMapper.getByDraftId(draftDO.getId()).stream()
                .map(KeywordBindDO::getKeyword).collect(Collectors.toList());
        respVO.setKeywordResume(keywordBinds);
        if (AnalysisStatusEnum.ANALYSIS.name().equals(respVO.getStatus())) {
            return respVO;
        }
        List<KeywordMetaDataDTO> metaData = keywordBindService.getMetaData(respVO.getKeywordResume(), draftDO.getEndpoint());
        respVO.setKeywordMetaData(metaData);
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DraftRespVO saveDraftVersion(DraftReqVO reqVO) {
        if (StringUtils.isBlank(reqVO.getUid())) {

            ListingDraftDO draftDO = ListingDraftConvert.INSTANCE.convert(reqVO);
            draftDO.setVersion(1);
            draftDO.setUid(IdUtil.fastSimpleUUID());
            draftDO.setStatus(AnalysisStatusEnum.ANALYSIS_END.name());
            DraftItemScoreDTO itemScoreDTO = calculationScore(draftDO);
            draftDO.setItemScore(JSONUtil.toJsonStr(itemScoreDTO));
            draftMapper.insert(draftDO);
            if (CollectionUtils.isNotEmpty(reqVO.getKeys())) {
                List<String> keys = reqVO.getKeys().stream().map(String::trim).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
                TreeSet<String> treeSet = CollUtil.toTreeSet(keys, String.CASE_INSENSITIVE_ORDER);
                List<String> distinctKeys = new ArrayList<>(treeSet);
                keywordBindService.addDraftKeyword(distinctKeys, draftDO.getId());
                draftDO.setStatus(AnalysisStatusEnum.ANALYSIS.name());
                updateById(draftDO);
                executor.execute(() -> {
                    try {
                        long start = System.currentTimeMillis();
                        keywordBindService.analysisKeyword(distinctKeys, draftDO.getEndpoint());
                        long end = System.currentTimeMillis();
                        draftDO.setAnalysisTime(end - start);
                        updateDo(draftDO, distinctKeys);
                        draftDO.setStatus(AnalysisStatusEnum.ANALYSIS_END.name());
                    } catch (Exception e) {
                        log.error("analysis keyword error", e);
                        draftDO.setStatus(AnalysisStatusEnum.ANALYSIS_ERROR.name());
                    }
                    updateById(draftDO);
                });
            }
            return ListingDraftConvert.INSTANCE.convert(draftDO);
        }

        ListingDraftDO draftDO = getVersion(reqVO.getUid(), reqVO.getVersion());

        List<KeywordBindDO> keywordBind = keywordBindMapper.getByDraftId(draftDO.getId());
        if (StringUtils.isNotBlank(reqVO.getEndpoint()) && !reqVO.getEndpoint().equals(draftDO.getEndpoint())) {
            if (CollectionUtils.isNotEmpty(keywordBind)) {
                throw exception(KEYWORD_IS_NOT_EMPTY);
            }
        }
        ListingDraftConvert.INSTANCE.update(reqVO, draftDO);
        draftDO.setVersion(1);

        if (CollectionUtils.isNotEmpty(reqVO.getKeys())) {
            DraftOperationReqVO operationReqVO = new DraftOperationReqVO();
            operationReqVO.setUid(reqVO.getUid());
            operationReqVO.setVersion(reqVO.getVersion());
            operationReqVO.setAddKey(reqVO.getKeys());
            addKeyword(operationReqVO);
        } else {
            DraftItemScoreDTO itemScoreDTO = calculationScore(draftDO);
            draftDO.setItemScore(JSONUtil.toJsonStr(itemScoreDTO));
            updateDo(draftDO, keywordBind.stream().map(KeywordBindDO::getKeyword).collect(Collectors.toList()));
        }

        updateById(draftDO);
        return ListingDraftConvert.INSTANCE.convert(draftDO);
    }


    @Override
    public List<DraftDetailExcelVO> export(List<Long> ids) {
        List<ListingDraftDO> listingDraft = draftMapper.selectBatchIds(ids);
        if (CollectionUtils.isEmpty(listingDraft)) {
            return Collections.emptyList();
        }

        return ListingDraftConvert.INSTANCE.convertExcel(listingDraft);
    }

    @Override
    public void delete(List<Long> ids) {
        draftMapper.deleteBatchIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addKeyword(DraftOperationReqVO reqVO) {
        if (CollectionUtils.isEmpty(reqVO.getAddKey())) {
            throw exception(new ErrorCode(500, "关键词不能为空"));
        }

        ListingDraftDO draftDO = getVersion(reqVO.getUid(), reqVO.getVersion());
        validStatus(draftDO);

        List<String> newKey = reqVO.getAddKey().stream()
                .map(String::trim).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        TreeSet<String> treeSet = CollUtil.toTreeSet(newKey, String.CASE_INSENSITIVE_ORDER);

        List<String> oldKey = keywordBindMapper.getByDraftId(draftDO.getId())
                .stream().map(KeywordBindDO::getKeyword).collect(Collectors.toList());
        // 新 + 旧 去重
        treeSet.addAll(oldKey);
        List<String> allKeys = new ArrayList<>(treeSet);
        if (treeSet.size() > 2000) {
            throw exception(new ErrorCode(500, "关键词总个数不能超过2000，新增后数量:{}"), treeSet.size());
        }

        // 新 + 旧 - 旧   待新增
        oldKey.forEach(treeSet::remove);
        List<String> addKey = new ArrayList<>(treeSet);
        if (CollectionUtils.isEmpty(addKey)) {
            return;
        }
        keywordBindService.addDraftKeyword(addKey, draftDO.getId());

        draftDO.setStatus(AnalysisStatusEnum.ANALYSIS.name());
        DraftItemScoreDTO itemScoreDTO = calculationScore(draftDO);
        draftDO.setItemScore(JSONUtil.toJsonStr(itemScoreDTO));
        updateById(draftDO);

        executor.execute(() -> {
            try {
                long start = System.currentTimeMillis();
                keywordBindService.analysisKeyword(addKey, draftDO.getEndpoint());
                long end = System.currentTimeMillis();
                draftDO.setAnalysisTime(end - start);
                updateDo(draftDO, allKeys);
                draftDO.setStatus(AnalysisStatusEnum.ANALYSIS_END.name());
            } catch (Exception e) {
                log.error("analysis error", e);
                draftDO.setStatus(AnalysisStatusEnum.ANALYSIS_ERROR.name());
            }
            updateById(draftDO);
        });
    }

    @Override
    public void removeKeyword(DraftOperationReqVO reqVO) {
        if (CollectionUtils.isEmpty(reqVO.getRemoveBindKey())) {
            throw exception(new ErrorCode(500, "删除关键词不能为空"));
        }
        ListingDraftDO draftDO = getVersion(reqVO.getUid(), reqVO.getVersion());
        List<String> keys = keywordBindMapper.getByDraftId(draftDO.getId()).stream().map(KeywordBindDO::getKeyword).collect(Collectors.toList());
        List<String> removeKey = reqVO.getRemoveBindKey().stream().map(String::trim).collect(Collectors.toList());
        keys.removeAll(removeKey);
        updateDo(draftDO, keys);
        updateById(draftDO);
        keywordBindMapper.deleteDraftKey(removeKey, draftDO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DraftRespVO importDict(ImportDictReqVO reqVO) {
        if (CollectionUtils.isEmpty(reqVO.getDictUid())) {
            throw exception(new ErrorCode(500, "词库uid不能为空"));
        }
        List<String> keys = new ArrayList<>();
        for (String dictUid : reqVO.getDictUid()) {
            DictRespVO dictDetail = dictService.dictDetail(dictUid);
            if (CollectionUtils.isNotEmpty(dictDetail.getKeywordResume())) {
                keys.addAll(dictDetail.getKeywordResume());
            }
        }

        if (CollectionUtils.isEmpty(keys)) {
            throw exception(new ErrorCode(500, "词库中没有关键词"));
        }
        if (StringUtils.isBlank(reqVO.getUid())) {
            return saveDraftVersion(reqVO);
        }

        DraftOperationReqVO operationReqVO = new DraftOperationReqVO();
        operationReqVO.setUid(reqVO.getUid());
        operationReqVO.setVersion(reqVO.getVersion());
        operationReqVO.setAddKey(keys);
        addKeyword(operationReqVO);

        return detail(reqVO.getUid(), reqVO.getVersion());
    }

    @Override
    public void batchExecute(List<DraftOperationReqVO> operationReq) {
        for (DraftOperationReqVO draftOperationReqVO : operationReq) {
            ListingDraftDO draftDO = getVersion(draftOperationReqVO.getUid(), draftOperationReqVO.getVersion());
            validStatus(draftDO);

        }
    }

    @Override
    public DraftRespVO score(DraftReqVO reqVO) {
        ListingDraftDO draftDO = ListingDraftConvert.INSTANCE.convert(reqVO);
        updateSearchers(draftDO, reqVO.getKeys());

        DraftRespVO respVO = ListingDraftConvert.INSTANCE.convert(draftDO);
        DraftItemScoreDTO draftItemScoreDTO = calculationScore(draftDO);
        respVO.setItemScore(draftItemScoreDTO);
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DraftRespVO cloneDraft(DraftOperationReqVO reqVO) {
        ListingDraftDO sourceDraft = getVersion(reqVO.getUid(), reqVO.getVersion());
        if (AnalysisStatusEnum.ANALYSIS.name().equals(sourceDraft.getStatus())) {
            throw exception(KEYWORD_IS_ANALYSIS);
        }
        List<String> keys = keywordBindMapper.getByDraftId(sourceDraft.getId()).stream().map(KeywordBindDO::getKeyword).collect(Collectors.toList());

        sourceDraft.setId(null);
        sourceDraft.setUid(IdUtil.fastSimpleUUID());
        sourceDraft.setVersion(1);
        sourceDraft.setCreator(null);
        sourceDraft.setUpdater(null);
        sourceDraft.setCreateTime(null);
        sourceDraft.setUpdater(null);
        draftMapper.insert(sourceDraft);
        keywordBindService.addDraftKeyword(keys, sourceDraft.getId());
        return detail(sourceDraft.getUid(), sourceDraft.getVersion());
    }

    @Override
    public String searchTermRecommend(String uid, Integer version) {
        ListingDraftDO draftDO = getVersion(uid, version);
        List<KeywordMetaDataDTO> sortMetaData = keywordBindService.getMetaData(draftDO.getId(), draftDO.getEndpoint());
        StringJoiner sj = new StringJoiner(org.apache.commons.lang3.StringUtils.SPACE);
        for (KeywordMetaDataDTO sortMetaDatum : sortMetaData) {
            if (sj.length() + sortMetaDatum.getKeyword().length() > 250) {
                break;
            }
            sj.add(sortMetaDatum.getKeyword());
        }
        return sj.toString();
    }

    private DraftItemScoreDTO calculationScore(ListingDraftDO draftDO) {
        String title = draftDO.getTitle();
        Map<String, String> fiveDesc = ListingDraftConvert.INSTANCE.parseFiveDesc(draftDO.getFiveDesc());
        String productDesc = draftDO.getProductDesc();
        String searchTerm = draftDO.getSearchTerm();

        return DraftItemScoreDTO.builder()
                .titleLength(ListingDraftScoreUtil.judgmentLength(title, 150, 250))
                .titleUppercase(ListingDraftScoreUtil.titleUppercase(title))
                .withoutSpecialChat(ListingDraftScoreUtil.withoutSpecialChat(title))
                .fiveDescLength(ListingDraftScoreUtil.judgmentLength(fiveDesc, 150, 200))
                .allUppercase(ListingDraftScoreUtil.allUppercase(fiveDesc))
                .partUppercase(ListingDraftScoreUtil.partUppercase(fiveDesc))
                .fiveDescScore(ListingDraftScoreUtil.fiveDescScore(fiveDesc))
                .productLength(ListingDraftScoreUtil.judgmentLength(productDesc, 1500, 2000))
                .withoutUrl(!ListingDraftScoreUtil.contains(productDesc))
                .searchTermLength((StringUtils.isBlank(searchTerm)) || searchTerm.length() < 250)
                .build();

    }

    /**
     * 推荐词
     */
    private void updateRecommendKey(ListingDraftDO draftDO, List<String> keys) {
        TreeSet<String> allSet = CollUtil.toTreeSet(keys, String.CASE_INSENSITIVE_ORDER);
        keys = new ArrayList<>(allSet);
        List<KeywordMetaDataDTO> metaData = keywordBindService.getMetaData(keys, draftDO.getEndpoint());
        if (CollectionUtils.isEmpty(metaData)) {
            return;
        }

        DraftConfigDTO draftConfigDTO = ListingDraftConvert.INSTANCE.parseConfig(draftDO.getConfig());

        Integer fiveDescNum = draftConfigDTO.getFiveDescNum();

        Map<String, DraftContentConfigDTO> fiveDescConfig = Optional.ofNullable(draftConfigDTO.getFiveDescConfig()).orElseGet(HashMap::new);

        DraftContentConfigDTO titleConfig = splitData(draftConfigDTO.getTitleConfig(), metaData);
        metaData = CollUtil.sub(metaData, 5, metaData.size());
        draftConfigDTO.setTitleConfig(titleConfig);
        for (int i = 1; i <= fiveDescNum; i++) {
            DraftContentConfigDTO draftContentConfigDTO = splitData(fiveDescConfig.get(String.valueOf(i)), metaData);
            metaData = CollUtil.sub(metaData, 5, metaData.size());
            fiveDescConfig.put(String.valueOf(i), draftContentConfigDTO);
        }
        draftConfigDTO.setFiveDescConfig(fiveDescConfig);

        DraftContentConfigDTO productConfig = splitData(draftConfigDTO.getProductDescConfig(), metaData);
        metaData = CollUtil.sub(metaData, 5, metaData.size());
        draftConfigDTO.setProductDescConfig(productConfig);
        DraftContentConfigDTO searchConfig = splitData(draftConfigDTO.getSearchTermConfig(), metaData);
        draftConfigDTO.setSearchTermConfig(searchConfig);

        draftDO.setConfig(ListingDraftConvert.INSTANCE.jsonStr(draftConfigDTO));
    }

    private void updateDo(ListingDraftDO draftDO, List<String> keys) {
        updateSearchers(draftDO, keys);
        updateRecommendKey(draftDO, keys);
    }

    private DraftContentConfigDTO splitData(DraftContentConfigDTO config, List<KeywordMetaDataDTO> metaData) {
        if (config == null) {
            config = new DraftContentConfigDTO();
        }
        if (metaData.size() > 0) {
            List<KeywordMetaDataDTO> keywordMetaData = metaData.subList(0, Math.min(metaData.size(), 5));
            config.setRecommendKeys(ListingKeywordConvert.INSTANCE.convert2(keywordMetaData));
        } else {
            config.setRecommendKeys(Collections.emptyList());
        }
        return config;
    }

    /**
     * 更新搜索量
     */
    private void updateSearchers(ListingDraftDO draftDO, List<String> keys) {
        TreeSet<String> allSet = CollUtil.toTreeSet(keys, String.CASE_INSENSITIVE_ORDER);
        keys = new ArrayList<>(allSet);
        List<KeywordMetaDataDTO> metaData = keywordBindService.getMetaData(keys, draftDO.getEndpoint());
        if (CollectionUtils.isEmpty(metaData)) {
            draftDO.setTotalSearches(0L);
            draftDO.setMatchSearchers(0L);
            draftDO.setScore((double) 0);
            return;
        }
        Long totalSearches = metaData.stream().mapToLong(KeywordMetaDataDTO::mouthSearches).sum();

        DraftRespVO respVO = ListingDraftConvert.INSTANCE.convert(draftDO);
        Map<String, KeywordMetaDataDTO> metaMap = metaData.stream().collect(Collectors.toMap(KeywordMetaDataDTO::getKeyword, Function.identity()));

        Long matchSearchers = containsKeySearchers(keys, respVO, metaMap);
        // 搜索量
        draftDO.setTotalSearches(totalSearches);
        draftDO.setMatchSearchers(matchSearchers);
        draftDO.setScore((double) 0);
    }

    /**
     * 搜索量计算
     */
    private Long containsKeySearchers(List<String> keys, DraftRespVO respVO, Map<String, KeywordMetaDataDTO> metaMap) {
        String title = respVO.getTitle();
        String productDesc = respVO.getProductDesc();
        String searchTerm = respVO.getSearchTerm();
        StringJoiner sj = new StringJoiner(org.apache.commons.lang3.StringUtils.SPACE);
        DraftConfigDTO draftConfig = respVO.getDraftConfig();
        if (!(draftConfig != null && draftConfig.getTitleConfig() != null && draftConfig.getTitleConfig().getIgnoreUse())) {
            sj.add(title);
        }

        if (!(draftConfig != null && draftConfig.getProductDescConfig() != null && draftConfig.getProductDescConfig().getIgnoreUse())) {
            sj.add(productDesc);
        }
        if (!(draftConfig != null && draftConfig.getSearchTermConfig() != null && !draftConfig.getSearchTermConfig().getIgnoreUse())) {
            sj.add(searchTerm);
        }

        if (draftConfig == null || draftConfig.getFiveDescConfig() == null && respVO.getFiveDesc() != null) {
            sj.add(respVO.getFiveDesc().values().stream().reduce("", (a, b) -> a + b));
        } else if (respVO.getFiveDesc() != null) {
            Map<String, String> fiveDesc = respVO.getFiveDesc();
            Map<String, DraftContentConfigDTO> fiveDescConfig = draftConfig.getFiveDescConfig();
            for (String idx : fiveDesc.keySet()) {
                if (fiveDescConfig.get(idx) == null || !fiveDescConfig.get(idx).getIgnoreUse()) {
                    sj.add(fiveDesc.get(idx));
                }
            }
        }

        String content = sj.toString().toLowerCase();

        if (StringUtils.isBlank(content)) {
            return 0L;
        }

        List<String> contentKeys = keys.stream().map(String::toLowerCase).filter(content::contains).distinct().collect(Collectors.toList());
        long titleSearchers = 0L;
        for (String key : contentKeys) {
            KeywordMetaDataDTO keywordMetaDataDTO = metaMap.get(key);
            titleSearchers += keywordMetaDataDTO == null ? 0L : keywordMetaDataDTO.mouthSearches();
        }
        return titleSearchers;
    }

    private void validStatus(ListingDraftDO draftDO) {
        if (AnalysisStatusEnum.ANALYSIS.name().equals(draftDO.getStatus())) {
            throw exception(KEYWORD_IS_ANALYSIS);
        }
        if (AnalysisStatusEnum.EXECUTING.name().equals(draftDO.getStatus())) {
            throw exception(DRAFT_IS_EXECUTING);
        }
    }

    private void updateById(ListingDraftDO draftDO) {
        draftDO.setUpdateTime(null);
        draftMapper.updateById(draftDO);
    }

    private ListingDraftDO getVersion(String uid, Integer version) {
        ListingDraftDO draftDO = draftMapper.getVersion(uid, version);
        if (draftDO == null) {
            throw exception(DRAFT_NOT_EXISTS, uid, version);
        }
        return draftDO;
    }
}
