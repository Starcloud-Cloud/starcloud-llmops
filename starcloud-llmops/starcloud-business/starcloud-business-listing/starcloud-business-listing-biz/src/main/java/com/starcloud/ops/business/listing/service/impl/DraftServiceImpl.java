package com.starcloud.ops.business.listing.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.PageUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.starcloud.ops.business.app.enums.market.AppMarketTagTypeEnum;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DraftCreateReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DraftOperationReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DraftPageReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DraftReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.ImportDictReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DictRespVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DraftDetailExcelVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DraftRespVO;
import com.starcloud.ops.business.listing.convert.ListingAiConfigConvert;
import com.starcloud.ops.business.listing.convert.ListingDraftConvert;
import com.starcloud.ops.business.listing.convert.ListingKeywordConvert;
import com.starcloud.ops.business.listing.dal.dataobject.KeywordBindDO;
import com.starcloud.ops.business.listing.dal.dataobject.ListingDraftDO;
import com.starcloud.ops.business.listing.dal.dataobject.ListingDraftUserDTO;
import com.starcloud.ops.business.listing.dal.mysql.KeywordBindMapper;
import com.starcloud.ops.business.listing.dal.mysql.ListingDraftMapper;
import com.starcloud.ops.business.listing.dto.AiConfigDTO;
import com.starcloud.ops.business.listing.dto.DraftConfigDTO;
import com.starcloud.ops.business.listing.dto.DraftContentConfigDTO;
import com.starcloud.ops.business.listing.dto.DraftItemScoreDTO;
import com.starcloud.ops.business.listing.dto.KeywordMetaDataDTO;
import com.starcloud.ops.business.listing.enums.AnalysisStatusEnum;
import com.starcloud.ops.business.listing.enums.DraftSortFieldEnum;
import com.starcloud.ops.business.listing.enums.ListExecuteEnum;
import com.starcloud.ops.business.listing.service.DictService;
import com.starcloud.ops.business.listing.service.DraftService;
import com.starcloud.ops.business.listing.service.KeywordBindService;
import com.starcloud.ops.business.listing.service.ListingGenerateService;
import com.starcloud.ops.business.listing.utils.ListingDraftScoreUtil;
import com.starcloud.ops.business.listing.vo.ListingGenerateRequest;
import com.starcloud.ops.business.listing.vo.ListingGenerateResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.listing.enums.DictTypeConstants.LISTING_CONFIG;
import static com.starcloud.ops.business.listing.enums.ErrorCodeConstant.DRAFT_IS_EXECUTING;
import static com.starcloud.ops.business.listing.enums.ErrorCodeConstant.DRAFT_NOT_EXISTS;
import static com.starcloud.ops.business.listing.enums.ErrorCodeConstant.KEYWORD_IS_ANALYSIS;
import static com.starcloud.ops.business.listing.enums.ErrorCodeConstant.KEYWORD_IS_NOT_EMPTY;

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

    @Resource
    private DictDataService dictDataService;

    @Resource
    private ListingGenerateService listingGenerateService;

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
        List<ListingDraftUserDTO> latestDrafts = draftMapper.getLatestDrafts(PageUtils.getStart(pageParam),
                pageParam.getPageSize(),
                DraftSortFieldEnum.getColumn(pageParam.getSortField()),
                BooleanUtil.isTrue(pageParam.getAsc()) ? "ASC" : "DESC");
        Long count = draftMapper.count();
        return new PageResult<>(ListingDraftConvert.INSTANCE.convert2(latestDrafts), count);
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
        List<KeywordMetaDataDTO> metaData = keywordBindService.getMetaData(respVO.getKeywordResume(), draftDO.getEndpoint(), false);
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
            updateScore(draftDO);
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
                        draftDO.setStatus(AnalysisStatusEnum.ANALYSIS_END.name());
                    } catch (Exception e) {
                        log.error("analysis keyword error", e);
                        draftDO.setStatus(AnalysisStatusEnum.ANALYSIS_ERROR.name());
                    }
                    updateDo(draftDO, distinctKeys);
                    updateScore(draftDO);
                    updateById(draftDO);
                });
            }
            return ListingDraftConvert.INSTANCE.convert(draftDO);
        }

        ListingDraftDO draftDO = getVersion(reqVO.getUid(), reqVO.getVersion());
        validStatus(draftDO);
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
            updateScore(draftDO);
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
        if (CollectionUtils.isNotEmpty(addKey)) {
            keywordBindService.addDraftKeyword(addKey, draftDO.getId());
        }
        draftDO.setStatus(AnalysisStatusEnum.ANALYSIS.name());
        updateScore(draftDO);
        updateById(draftDO);

        executor.execute(() -> {
            try {
                long start = System.currentTimeMillis();
                keywordBindService.analysisKeyword(newKey, draftDO.getEndpoint());
                long end = System.currentTimeMillis();
                draftDO.setAnalysisTime(end - start);
                draftDO.setStatus(AnalysisStatusEnum.ANALYSIS_END.name());
            } catch (Exception e) {
                log.error("analysis error", e);
                draftDO.setStatus(AnalysisStatusEnum.ANALYSIS_ERROR.name());
            }
            updateDo(draftDO, allKeys);
            updateScore(draftDO);
            updateById(draftDO);
        });
    }

    @Override
    public void removeKeyword(DraftOperationReqVO reqVO) {
        if (CollectionUtils.isEmpty(reqVO.getRemoveBindKey())) {
            throw exception(new ErrorCode(500, "删除关键词不能为空"));
        }
        ListingDraftDO draftDO = getVersion(reqVO.getUid(), reqVO.getVersion());
        List<String> removeKey = reqVO.getRemoveBindKey().stream().map(String::trim).collect(Collectors.toList());
        keywordBindMapper.deleteDraftKey(removeKey, draftDO.getId());
        List<String> keys = keywordBindMapper.getByDraftId(draftDO.getId()).stream().map(KeywordBindDO::getKeyword).collect(Collectors.toList());
        updateDo(draftDO, keys);
        updateScore(draftDO);
        updateById(draftDO);
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
        reqVO.setKeys(keys);

        return saveDraftVersion(reqVO);
    }

    @Override
    public void batchExecute(List<Long> ids) {
        DictDataDO dictDataDO = dictDataService.parseDictData(LISTING_CONFIG, "execute_num");
        int maxNum = dictDataDO == null ? 20 : Integer.parseInt(dictDataDO.getValue());
        if (ids.size() > maxNum) {
            throw exception(new ErrorCode(500, "最多同时执行{}个listing"), maxNum);
        }
        List<ListingDraftDO> listingDrafts = draftMapper.selectBatchIds(ids).stream()
                .filter(draftDO -> !ListExecuteEnum.EXECUTING.name().equals(draftDO.getExecuteStatus()))
                .collect(Collectors.toList());
        for (ListingDraftDO listingDraftDO : listingDrafts) {
            executor.execute(() -> executorListing(listingDraftDO));
        }
    }

    @Override
    public DraftRespVO score(DraftReqVO reqVO) {
        ListingDraftDO draftDO = ListingDraftConvert.INSTANCE.convert(reqVO);
        ListingDraftDO draft = draftMapper.getVersion(reqVO.getUid(), reqVO.getVersion());
        if (draft != null) {
            List<String> keys = keywordBindMapper.getByDraftId(draft.getId()).stream().map(KeywordBindDO::getKeyword).collect(Collectors.toList());
            updateSearchers(draftDO, keys);
            draftDO.setStatus(draft.getStatus());
        } else {
            draftDO.setStatus(AnalysisStatusEnum.ANALYSIS_END.name());
        }

        updateScore(draftDO);
        return ListingDraftConvert.INSTANCE.convert(draftDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DraftRespVO cloneDraft(DraftOperationReqVO reqVO) {
        ListingDraftDO draftDO = getVersion(reqVO.getUid(), reqVO.getVersion());
        if (AnalysisStatusEnum.ANALYSIS.name().equals(draftDO.getStatus())) {
            throw exception(KEYWORD_IS_ANALYSIS);
        }
        List<String> keys = keywordBindMapper.getByDraftId(draftDO.getId()).stream().map(KeywordBindDO::getKeyword).collect(Collectors.toList());

        draftDO.setTitle("Copy-" + (draftDO.getTitle() == null ? draftDO.getId().toString() : draftDO.getTitle()));
        draftDO.setId(null);
        draftDO.setUid(IdUtil.fastSimpleUUID());
        draftDO.setVersion(1);
        draftDO.setCreator(null);
        draftDO.setUpdater(null);
        draftDO.setCreateTime(null);
        draftDO.setUpdater(null);
        draftDO.setStatus(AnalysisStatusEnum.ANALYSIS_END.name());
        draftDO.setExecuteStatus("");
        updateDo(draftDO, keys);
        updateScore(draftDO);
        draftMapper.insert(draftDO);
        keywordBindService.addDraftKeyword(keys, draftDO.getId());
        return detail(draftDO.getUid(), draftDO.getVersion());
    }

    @Override
    public String searchTermRecommend(DraftReqVO reqVO) {
        ListingDraftDO draftDO = getVersion(reqVO.getUid(), reqVO.getVersion());

        String title = reqVO.getTitle();
        Map<String, String> fiveDesc = reqVO.getFiveDesc();
        String productDesc = reqVO.getProductDesc();
        StringJoiner listing = new StringJoiner(org.apache.commons.lang3.StringUtils.SPACE);
        for (String value : fiveDesc.values()) {
            listing.add(value);
        }
        listing.add(title);
        listing.add(productDesc);
        String str = listing.toString().toLowerCase();

        List<KeywordMetaDataDTO> sortMetaData = keywordBindService.getMetaData(draftDO.getId(), draftDO.getEndpoint(), true);
        StringJoiner sj = new StringJoiner(org.apache.commons.lang3.StringUtils.SPACE);
        for (KeywordMetaDataDTO sortMetaDatum : sortMetaData) {
            if (sj.length() + sortMetaDatum.getKeyword().length() > 250) {
                break;
            }
            if (str.contains(sortMetaDatum.getKeyword().toLowerCase())) {
                continue;
            }
            sj.add(sortMetaDatum.getKeyword());
        }
        return sj.toString();
    }

    @Override
    public void refresh(String uid, Integer version) {
        ListingDraftDO draftDO = getVersion(uid, version);
        List<String> keys = keywordBindMapper.getByDraftId(draftDO.getId()).stream().map(KeywordBindDO::getKeyword).collect(Collectors.toList());
        draftDO.setStatus(AnalysisStatusEnum.ANALYSIS.name());
        updateById(draftDO);
        executor.execute(() -> {
            try {
                long start = System.currentTimeMillis();
                keywordBindService.analysisKeyword(keys, draftDO.getEndpoint());
                draftDO.setStatus(AnalysisStatusEnum.ANALYSIS_END.name());
                long end = System.currentTimeMillis();
                draftDO.setAnalysisTime(end - start);
                updateDo(draftDO, keys);
                updateScore(draftDO);
                updateById(draftDO);
            } catch (Exception e) {
                log.error("refresh error", e);
                draftDO.setStatus(AnalysisStatusEnum.ANALYSIS_ERROR.name());
                updateById(draftDO);
            }
        });
    }

    private void executorListing(ListingDraftDO draftDO) {
        if (AnalysisStatusEnum.ANALYSIS.name().equals(draftDO.getStatus())) {
            updateError(draftDO, "词库关键词分析中,稍后重试");
            return;
        }

        DraftConfigDTO draftConfigDTO = ListingDraftConvert.INSTANCE.parseConfig(draftDO.getConfig());
        AiConfigDTO aiConfigDTO = Optional.ofNullable(draftConfigDTO.getAiConfigDTO()).orElseGet(AiConfigDTO::new);
        if (StringUtils.isBlank(aiConfigDTO.getProductFeature())) {
            updateError(draftDO, "产品特征不能为空");
            return;
        }

        draftDO.setExecuteStatus(ListExecuteEnum.EXECUTING.name());
        updateById(draftDO);

        Long start = System.currentTimeMillis();

        try {
            log.info("开始生成listing");
            ListingGenerateRequest request = ListingAiConfigConvert.INSTANCE.convert(aiConfigDTO);
            String conversationUid = IdUtil.fastSimpleUUID();
            request.setConversationUid(conversationUid);
            request.setDraftUid(draftDO.getUid());
            request.setListingType(AppMarketTagTypeEnum.LISTING_TITLE.name());
            request.setKeywords(recommendKeys(draftConfigDTO.getTitleConfig()));
            ListingGenerateResponse titleResp = listingGenerateService.execute(request);
            if (!titleResp.getSuccess()) {
                updateError(draftDO, "标题生成失败：" + titleResp.getErrorMsg());
                return;
            }
            Long titleEnd = System.currentTimeMillis();
            log.info("生成title成功，{} ms", titleEnd - start);
            // title
            String title = titleResp.getAnswer();
            draftDO.setTitle(title);
            // fiveDesc
            int fiveDescNum = draftConfigDTO.getFiveDescNum() == null ? 5 : draftConfigDTO.getFiveDescNum();
            HashMap<String, String> fiveDesc = new HashMap<>(fiveDescNum);
            for (int i = 1; i <= fiveDescNum; i++) {
                if (draftConfigDTO.getFiveDescConfig() != null) {
                    request.setKeywords(recommendKeys(draftConfigDTO.getFiveDescConfig().get(String.valueOf(i))));
                } else {
                    request.setKeywords(Collections.emptyList());
                }

                request.setTitle(title);
                request.setBulletPoints(new ArrayList<>(fiveDesc.values()));
                request.setListingType(AppMarketTagTypeEnum.LISTING_BULLET_POINT.name());
                ListingGenerateResponse fiveDescResp = listingGenerateService.execute(request);
                if (!fiveDescResp.getSuccess()) {
                    updateError(draftDO, "五点描述生成失败：" + fiveDescResp.getErrorMsg());
                    return;
                }
                String desc = fiveDescResp.getAnswer();
                fiveDesc.put(String.valueOf(i), desc);
            }
            Long fiverDescEnd = System.currentTimeMillis();
            log.info("生成五点描述成功，{} ms", fiverDescEnd - titleEnd);
            draftDO.setFiveDesc(ListingDraftConvert.INSTANCE.jsonStr(fiveDesc));
            // productDesc
            request.setBulletPoints(new ArrayList<>(fiveDesc.values()));
            request.setListingType(AppMarketTagTypeEnum.LISTING_PRODUCT_DESCRIPTION.name());
            request.setKeywords(recommendKeys(draftConfigDTO.getProductDescConfig()));
            ListingGenerateResponse productDescResp = listingGenerateService.execute(request);
            if (!productDescResp.getSuccess()) {
                updateError(draftDO, "产品描述生成失败：" + productDescResp.getErrorMsg());
                return;
            }
            Long productDescEnd = System.currentTimeMillis();
            log.info("生成产品描述成功，{} ms", productDescEnd - fiverDescEnd);
            String desc = productDescResp.getAnswer();
            draftDO.setProductDesc(desc);

            Long end = System.currentTimeMillis();
            log.info("生成listing全部结束. {} ms", end - start);

            List<String> keywordBinds = keywordBindMapper.getByDraftId(draftDO.getId()).stream().map(KeywordBindDO::getKeyword).collect(Collectors.toList());
            updateDo(draftDO, keywordBinds);
            updateScore(draftDO);

            draftDO.setExecuteStatus(ListExecuteEnum.EXECUTED.name());
            draftDO.setExecuteTime(end - start);
            draftDO.setErrorMsg("");
            updateById(draftDO);
        } catch (Exception e) {
            log.error("生成listing失败", e);
            draftDO.setErrorMsg(e.getMessage());
            draftDO.setExecuteStatus(ListExecuteEnum.EXECUTE_ERROR.name());
            updateById(draftDO);
        }
    }

    private List<String> recommendKeys(DraftContentConfigDTO contentConfigDTO) {
        if (contentConfigDTO == null) {
            return Collections.emptyList();
        }
        return contentConfigDTO.getKeys();
    }

    private void updateError(ListingDraftDO draftDO, String msg) {
        draftDO.setErrorMsg(msg);
        draftDO.setExecuteStatus(ListExecuteEnum.EXECUTE_ERROR.name());
        updateById(draftDO);
    }

    private void updateScore(ListingDraftDO draftDO) {
        DraftItemScoreDTO itemScoreDTO = calculationScore(draftDO);
        draftDO.setScore(itemScoreDTO.totalScore());
        draftDO.setItemScore(JSONUtil.toJsonStr(itemScoreDTO));
        draftDO.setScoreProportion(itemScoreDTO.scoreProportion());
    }

    private DraftItemScoreDTO calculationScore(ListingDraftDO draftDO) {
        String title = draftDO.getTitle();
        Map<String, String> fiveDesc = ListingDraftConvert.INSTANCE.parseFiveDesc(draftDO.getFiveDesc());
        String productDesc = draftDO.getProductDesc();
        String searchTerm = draftDO.getSearchTerm();

        return DraftItemScoreDTO.builder()
                .titleLength(ListingDraftScoreUtil.judgmentLength(title, 150, 200))
                .titleUppercase(ListingDraftScoreUtil.titleUppercase(title))
                .withoutSpecialChat(ListingDraftScoreUtil.withoutSpecialChat(title))
                .fiveDescLength(ListingDraftScoreUtil.judgmentLength(fiveDesc, 150, 200))
                .allUppercase(ListingDraftScoreUtil.allUppercase(fiveDesc))
                .partUppercase(ListingDraftScoreUtil.partUppercase(fiveDesc))
                .fiveDescScore(ListingDraftScoreUtil.fiveDescScore(fiveDesc))
                .productLength(ListingDraftScoreUtil.judgmentLength(productDesc, 1500, 2000))
                .withoutUrl(ListingDraftScoreUtil.withOutUrl(productDesc))
                .searchTermLength(ListingDraftScoreUtil.judgmentLength(searchTerm, 0, 250))
                .build();

    }

    /**
     * 推荐词
     */
    private void updateRecommendKey(ListingDraftDO draftDO, List<String> keys) {
        if (keys == null) {
            keys = Collections.emptyList();
        }
        TreeSet<String> allSet = CollUtil.toTreeSet(keys, String.CASE_INSENSITIVE_ORDER);
        keys = new ArrayList<>(allSet);
        List<KeywordMetaDataDTO> metaData = keywordBindService.getMetaData(keys, draftDO.getEndpoint(), true);
        if (metaData == null) {
            metaData = Collections.emptyList();
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
        if (CollectionUtils.isEmpty(keys)) {
            draftDO.setTotalSearches(0L);
            draftDO.setMatchSearchers(0L);
            draftDO.setSearchersProportion(0.00);
            return;
        }
        TreeSet<String> allSet = CollUtil.toTreeSet(keys, String.CASE_INSENSITIVE_ORDER);
        keys = new ArrayList<>(allSet);
        List<KeywordMetaDataDTO> metaData = keywordBindService.getMetaData(keys, draftDO.getEndpoint(), true);
        if (CollectionUtils.isEmpty(metaData)) {
            draftDO.setTotalSearches(0L);
            draftDO.setMatchSearchers(0L);
            draftDO.setSearchersProportion(0.00);
            return;
        }
        Long totalSearches = metaData.stream().mapToLong(KeywordMetaDataDTO::mouthSearches).sum();

        DraftRespVO respVO = ListingDraftConvert.INSTANCE.convert(draftDO);
        Map<String, KeywordMetaDataDTO> metaMap = metaData.stream().collect(Collectors.toMap(KeywordMetaDataDTO::getKeyword, Function.identity()));

        String content = listString(respVO);
        List<String> contentKeys = keys.stream().map(String::toLowerCase).filter(content::contains).distinct().collect(Collectors.toList());
        long matchSearchers = 0L;

        BigDecimal matchSize = BigDecimal.valueOf(0);
        for (String key : contentKeys) {
            KeywordMetaDataDTO keywordMetaDataDTO = metaMap.get(key);
            if (keywordMetaDataDTO != null) {
                matchSize = matchSize.add(BigDecimal.ONE);
                matchSearchers += keywordMetaDataDTO.mouthSearches();
            }
        }

        // 搜索量
        draftDO.setTotalSearches(totalSearches);
        draftDO.setMatchSearchers(matchSearchers);
        draftDO.setSearchersProportion(BigDecimal.valueOf(matchSearchers).divide(BigDecimal.valueOf(totalSearches),2, RoundingMode.HALF_UP).doubleValue());
    }


    /**
     * 搜索量计算
     */
    private String listString(DraftRespVO respVO) {
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
        return sj.toString().toLowerCase();
    }

    private void validStatus(ListingDraftDO draftDO) {
        if (AnalysisStatusEnum.ANALYSIS.name().equals(draftDO.getStatus())) {
            throw exception(KEYWORD_IS_ANALYSIS);
        }
        if (ListExecuteEnum.EXECUTING.name().equals(draftDO.getExecuteStatus())) {
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
