package com.starcloud.ops.business.dataset.service.segment.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.xiaoymin.knife4j.core.util.Assert;
import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageUpLoadRespVO;
import com.starcloud.ops.business.dataset.convert.segment.DocumentSegmentConvert;
import com.starcloud.ops.business.dataset.dal.dataobject.datasethandlerules.DatasetHandleRulesDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasets.DatasetsDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.DocumentSegmentDO;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.SegmentsEmbeddingsDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasethandlerules.DatasetHandleRulesMapper;
import com.starcloud.ops.business.dataset.dal.mysql.datasetsourcedata.DatasetSourceDataMapper;
import com.starcloud.ops.business.dataset.dal.mysql.segment.DocumentSegmentMapper;
import com.starcloud.ops.business.dataset.dal.mysql.segment.SegmentsEmbeddingsDOMapper;
import com.starcloud.ops.business.dataset.enums.DocumentSegmentEnum;
import com.starcloud.ops.business.dataset.pojo.dto.RecordDTO;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import com.starcloud.ops.business.dataset.pojo.request.FileSplitRequest;
import com.starcloud.ops.business.dataset.pojo.request.MatchQueryRequest;
import com.starcloud.ops.business.dataset.pojo.request.SegmentPageQuery;
import com.starcloud.ops.business.dataset.pojo.request.SimilarQueryRequest;
import com.starcloud.ops.business.dataset.pojo.response.MatchQueryVO;
import com.starcloud.ops.business.dataset.pojo.response.SplitForecastResponse;
import com.starcloud.ops.business.dataset.service.datasets.DatasetsService;
import com.starcloud.ops.business.dataset.service.datasetstorage.DatasetStorageService;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import com.starcloud.ops.business.dataset.service.task.SummaryEntity;
import com.starcloud.ops.business.dataset.service.task.SummaryTask;
import com.starcloud.ops.business.dataset.util.dataset.TextCleanAndSplitUtils;
import com.starcloud.ops.llm.langchain.core.indexes.splitter.SplitterContainer;
import com.starcloud.ops.llm.langchain.core.model.embeddings.BasicEmbedding;
import com.starcloud.ops.llm.langchain.core.model.llm.document.*;
import com.starcloud.ops.llm.langchain.core.utils.TokenCalculator;
import com.starcloud.ops.llm.langchain.core.indexes.vectorstores.BasicVectorStore;
import com.starcloud.ops.llm.langchain.core.utils.TokenUtils;
import com.starcloud.ops.llm.langchain.core.utils.VectorSerializeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.*;

@Service
@Slf4j
public class DocumentSegmentsServiceImpl implements DocumentSegmentsService {

    @Autowired
    private BasicEmbedding basicEmbedding;

    @Autowired
    private DocumentSegmentMapper segmentMapper;

    @Autowired
    private SegmentsEmbeddingsDOMapper embeddingsDOMapper;

    @Autowired
    private BasicVectorStore basicVectorStore;

    @Autowired
    private DatasetStorageService datasetStorageService;

    @Autowired
    private DatasetsService datasetsService;

    @Autowired
    private DatasetHandleRulesMapper splitRulesMapper;

    @Autowired
    private DatasetSourceDataMapper sourceDataMapper;

    @Autowired
    private SummaryTask summaryTask;

    @Override
    public SplitForecastResponse splitForecast(FileSplitRequest fileSplitRequest) {
        validateTenantId(fileSplitRequest.getDocumentId());
        Tika tika = new Tika();
        tika.setMaxStringLength(-1);
        try {
            DatasetStorageUpLoadRespVO upLoadRespVO = datasetStorageService.getDatasetStorageByUID(fileSplitRequest.getDocumentId());
            String text = tika.parseToString(new URL(upLoadRespVO.getStorageKey()));
            SplitRule splitRule = fileSplitRequest.getSplitRule();
            String cleanText = TextCleanAndSplitUtils.splitText(text, splitRule);
            List<String> splitText = SplitterContainer.TOKEN_TEXT_SPLITTER.getSplitter().splitText(cleanText, splitRule.getChunkSize(), splitRule.getSeparator());
            Long totalTokens = splitText.stream().mapToLong(split -> TokenUtils.tokens(ModelType.TEXT_DAVINCI_002, split)).sum();
            BigDecimal totalPrice = TokenCalculator.getTextPrice(totalTokens, ModelType.TEXT_EMBEDDING_ADA_002);
            return SplitForecastResponse.builder().totalTokens(totalTokens).splitList(splitText).totalSegment(splitText.size()).totalPrice(totalPrice).build();
        } catch (IOException | TikaException e) {
            log.error("split forecast error:", e);
            throw new ServiceException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public String segmentSummary(String documentId, String text, SplitRule splitRule, Integer summarySize) {
        long start = System.currentTimeMillis();
        List<String> splitText = SplitterContainer.TOKEN_TEXT_SPLITTER.getSplitter().splitText(text, splitRule.getChunkSize(), splitRule.getSeparator());
        int size = splitText.size();
        CountDownLatch countDownLatch = new CountDownLatch(size);

        ConcurrentHashMap<Integer, String> resultMap = new ConcurrentHashMap<>(size);
        for (int i = 0; i < splitText.size(); i++) {
            SummaryEntity summaryEntity = new SummaryEntity(resultMap, countDownLatch);
            summaryEntity.setIndex(i);
            summaryEntity.setText(splitText.get(i));
            summaryEntity.setSummarySize(summarySize);
            summaryTask.execute(summaryEntity);
        }
        try {
            boolean await = countDownLatch.await(100, TimeUnit.SECONDS);
            if (!await || resultMap.size() < size) {
                log.info("count down wait={}", await);
                throw exception(SUMMARY_ERROR);
            }
            StringJoiner sj = new StringJoiner(StringUtils.LF);
            for (int i = 0; i < size; i++) {
                sj.add(resultMap.get(i));
            }
            long end = System.currentTimeMillis();
            log.info("summary success，rt：{} ms", end - start);
            return sj.toString();
        } catch (InterruptedException e) {
            log.info("总结文档失败，docId = {}", documentId, e);
            throw exception(SUMMARY_ERROR);
        }
    }

    @Override
    public void indexDoc(String datasetId, String documentId) {
        Assert.notBlank(datasetId, "datasetId is null");
        Assert.notBlank(documentId, "dataId is null");
        log.info("start embedding index,datasetId={},dataId={}", datasetId, documentId);
        long start = System.currentTimeMillis();
        DatasetsDO datasets = datasetsService.getDataById(Long.valueOf(datasetId));
        if (datasets == null) {
            throw exception(DATASETS_NOT_EXIST_ERROR);
        }
        try {
            List<DocumentSegmentDO> segmentDOS = segmentMapper.selectByDocId(documentId);
            segmentDOS = segmentDOS.stream().filter(documentSegmentDO -> !DocumentSegmentEnum.INDEXED.getCode().equals(documentSegmentDO.getStatus())).collect(Collectors.toList());
            Long tenantId = TenantContextHolder.getTenantId();
            String creator = datasets.getCreator();
            List<DocumentSegmentDTO> segments = new ArrayList<>(segmentDOS.size());
            for (DocumentSegmentDO segmentDO : segmentDOS) {
                String split = segmentDO.getContent();
                String segmentHash = segmentDO.getSegmentHash();
                DocumentSegmentDTO documentSegmentDTO = new DocumentSegmentDTO();
                documentSegmentDTO.setTenantId(tenantId);
                documentSegmentDTO.setCreator(creator);
                documentSegmentDTO.setDataSetId(datasetId);
                documentSegmentDTO.setDocumentId(documentId);
                documentSegmentDTO.setSegmentId(segmentDO.getId());
                documentSegmentDTO.setSegmentText(split);
                documentSegmentDTO.setStatus(true);
                SegmentsEmbeddingsDO segmentsEmbeddingsDO = embeddingsDOMapper.selectOneByHash(segmentHash);
                if (ObjectUtil.isNotNull(segmentsEmbeddingsDO)) {
                    segmentsEmbeddingsDO.setId(null);
                    segmentDO.setTokens(segmentsEmbeddingsDO.getTokens());
                    documentSegmentDTO.setVector(VectorSerializeUtils.deserialize(segmentsEmbeddingsDO.getVector()));
                } else {
                    EmbeddingDetail embeddingDetail = basicEmbedding.embedText(split);
                    segmentsEmbeddingsDO = new SegmentsEmbeddingsDO();
                    segmentsEmbeddingsDO.setTokens(embeddingDetail.getTotalTokens());
                    segmentsEmbeddingsDO.setVector(VectorSerializeUtils.serialize(embeddingDetail.getEmbedding()));
                    segmentDO.setTokens(embeddingDetail.getTotalTokens());
                    documentSegmentDTO.setVector(embeddingDetail.getEmbedding());
                }
                segmentsEmbeddingsDO.setTenantId(tenantId);
                segmentsEmbeddingsDO.setCreator(creator);
                segmentsEmbeddingsDO.setDatasetId(datasetId);
                segmentsEmbeddingsDO.setDocumentId(documentId);
                segmentsEmbeddingsDO.setDeleted(false);
                segmentsEmbeddingsDO.setSegmentId(segmentDO.getId());
                segmentsEmbeddingsDO.setUpdater(creator);
                segmentsEmbeddingsDO.setSegmentHash(segmentHash);
                segmentDO.setStatus(DocumentSegmentEnum.INDEXED.getCode());
                segmentMapper.updateById(segmentDO);
                embeddingsDOMapper.insert(segmentsEmbeddingsDO);
                segments.add(documentSegmentDTO);
            }
            basicVectorStore.addSegment(segments);
            long embeddingEnd = System.currentTimeMillis();
            log.info("embedding finished , time consume {}", embeddingEnd - start);
        } catch (Exception e) {
            log.error("embedding index error:", e);
            throw exception(DATASETS_EMBEDDING_ERROR);
        }
    }

    @Override
    public void splitDoc(String datasetId, String dataSourceId, SplitRule splitRule) throws IOException, TikaException {
        DatasetStorageUpLoadRespVO datasetStorage = datasetStorageService.getDatasetStorageByUID(dataSourceId);
        if (datasetStorage == null || StringUtils.isBlank(datasetStorage.getStorageKey())) {
            throw exception(DATASETS_NOT_EXIST_ERROR);
        }
        Tika tika = new Tika();
        tika.setMaxStringLength(-1);
        String text = tika.parseToString(new URL(datasetStorage.getStorageKey()));
        splitDoc(datasetId, dataSourceId, text, splitRule);
    }

    @Override
    public void splitDoc(String datasetId, String dataSourceId, String text, SplitRule splitRule) {
        Assert.notBlank(dataSourceId, "dataSourceId is null");
        List<String> splitText = SplitterContainer.TOKEN_TEXT_SPLITTER.getSplitter().splitText(text, splitRule.getChunkSize(), splitRule.getSeparator());
        DatasetsDO datasets = datasetsService.getDataById(Long.valueOf(datasetId));
        if (datasets == null) {
            throw exception(DATASETS_NOT_EXIST_ERROR);
        }
        String creator = datasets.getCreator();
        for (int i = 0; i < splitText.size(); i++) {
            String segmentId = IdUtil.getSnowflakeNextIdStr();
            String split = splitText.get(i);
            String segmentHash = strToHex(split);
            DocumentSegmentDO documentSegmentDO = new DocumentSegmentDO();
            documentSegmentDO.setId(segmentId);
            documentSegmentDO.setCreator(creator);
            documentSegmentDO.setUpdater(creator);
            documentSegmentDO.setDatasetId(String.valueOf(datasets.getId()));
            documentSegmentDO.setDocumentId(dataSourceId);
            documentSegmentDO.setPosition(i);
            documentSegmentDO.setContent(split);
            documentSegmentDO.setWordCount(split.length());
            documentSegmentDO.setSegmentHash(segmentHash);
            documentSegmentDO.setStatus(DocumentSegmentEnum.SPLIT.getCode());
            segmentMapper.insert(documentSegmentDO);
        }
    }

    @Override
    public void splitAndIndex(SplitRule splitRule, String datasetId, String documentId, String url) {
        Assert.notBlank(datasetId, "datasetId is null");
        Assert.notBlank(documentId, "dataId is null");
        log.info("start split and index,datasetId={},dataId={},url={},splitRule={}", datasetId, documentId, url, splitRule);
        long start = System.currentTimeMillis();
        validateTenantId(documentId);
        Tika tika = new Tika();
        tika.setMaxStringLength(-1);
        LambdaUpdateWrapper<DatasetSourceDataDO> updateWrapper = Wrappers.lambdaUpdate(DatasetSourceDataDO.class)
                .eq(DatasetSourceDataDO::getUid, documentId)
                .eq(DatasetSourceDataDO::getDatasetId, datasetId);
        try {
            String text = tika.parseToString(new URL(url));
            long parseEnd = System.currentTimeMillis();
            log.info("parse text finished , time consume {}", parseEnd - start);
            String cleanText = TextCleanAndSplitUtils.splitText(text, splitRule);
            long cleanEnd = System.currentTimeMillis();
            log.info("clean text finished , time consume {}", cleanEnd - parseEnd);
            List<String> splitText = SplitterContainer.TOKEN_TEXT_SPLITTER.getSplitter().splitText(cleanText, splitRule.getChunkSize(), splitRule.getSeparator());
            long splitEnd = System.currentTimeMillis();
            log.info("split text finished , time consume {}", splitEnd - cleanEnd);
            List<DocumentSegmentDTO> segments = new ArrayList<>(splitText.size());
            Long tenantId = TenantContextHolder.getTenantId();
            String creator = WebFrameworkUtils.getLoginUserId().toString();

            for (int i = 0; i < splitText.size(); i++) {
                String segmentId = IdUtil.getSnowflakeNextIdStr();
                String split = splitText.get(i);
                String segmentHash = strToHex(split);
                DocumentSegmentDTO documentSegmentDTO = new DocumentSegmentDTO();
                documentSegmentDTO.setTenantId(tenantId);
                documentSegmentDTO.setCreator(creator);
                documentSegmentDTO.setDataSetId(datasetId);
                documentSegmentDTO.setDocumentId(documentId);
                documentSegmentDTO.setSegmentId(segmentId);
                documentSegmentDTO.setSegmentText(split);
                documentSegmentDTO.setStatus(true);

                DocumentSegmentDO documentSegmentDO = new DocumentSegmentDO();
                documentSegmentDO.setId(segmentId);
                documentSegmentDO.setTenantId(tenantId);
                documentSegmentDO.setCreator(creator);
                documentSegmentDO.setUpdater(creator);
                documentSegmentDO.setDatasetId(datasetId);
                documentSegmentDO.setDocumentId(documentId);
                documentSegmentDO.setPosition(i);
                documentSegmentDO.setContent(split);
                documentSegmentDO.setWordCount(split.length());
                documentSegmentDO.setSegmentHash(segmentHash);
                documentSegmentDO.setStatus(DocumentSegmentEnum.INDEXED.getCode());
                SegmentsEmbeddingsDO segmentsEmbeddingsDO = embeddingsDOMapper.selectOneByHash(segmentHash);
                if (ObjectUtil.isNotNull(segmentsEmbeddingsDO)) {
                    segmentsEmbeddingsDO.setId(null);
                    documentSegmentDO.setTokens(segmentsEmbeddingsDO.getTokens());
                    documentSegmentDTO.setVector(VectorSerializeUtils.deserialize(segmentsEmbeddingsDO.getVector()));
                } else {
                    EmbeddingDetail embeddingDetail = basicEmbedding.embedText(split);
                    segmentsEmbeddingsDO = new SegmentsEmbeddingsDO();
                    segmentsEmbeddingsDO.setTokens(embeddingDetail.getTotalTokens());
                    segmentsEmbeddingsDO.setVector(VectorSerializeUtils.serialize(embeddingDetail.getEmbedding()));
                    documentSegmentDO.setTokens(embeddingDetail.getTotalTokens());
                    documentSegmentDTO.setVector(embeddingDetail.getEmbedding());
                }
                segmentsEmbeddingsDO.setTenantId(tenantId);
                segmentsEmbeddingsDO.setCreator(creator);
                segmentsEmbeddingsDO.setDatasetId(datasetId);
                segmentsEmbeddingsDO.setDocumentId(documentId);
                segmentsEmbeddingsDO.setDeleted(false);
                segmentsEmbeddingsDO.setSegmentId(segmentId);
                segmentsEmbeddingsDO.setUpdater(creator);
                segmentsEmbeddingsDO.setSegmentHash(segmentHash);
                segmentMapper.insert(documentSegmentDO);
                embeddingsDOMapper.insert(segmentsEmbeddingsDO);
                segments.add(documentSegmentDTO);
            }
            basicVectorStore.addSegment(segments);
            long embeddingEnd = System.currentTimeMillis();
            log.info("embedding finished , time consume {}", embeddingEnd - splitEnd);
            String ruleId = IdUtil.getSnowflakeNextIdStr();
            DatasetHandleRulesDO splitRulesDO = new DatasetHandleRulesDO();
            splitRulesDO.setCleanRule(JSONUtil.toJsonStr(splitRule));
            splitRulesDO.setSplitRule(JSONUtil.toJsonStr(splitRule));
            splitRulesDO.setId(Long.valueOf(ruleId));
            splitRulesDO.setDatasetId(Long.valueOf(datasetId));
            splitRulesDO.setTenantId(tenantId);
            splitRulesDO.setCreator(creator);
            splitRulesDO.setUpdater(creator);

            splitRulesMapper.insert(splitRulesDO);
            updateWrapper.set(DatasetSourceDataDO::getProcessingStartedTime, ofMill(start))
                    .set(DatasetSourceDataDO::getCleaningCompletedTime, ofMill(cleanEnd))
                    .set(DatasetSourceDataDO::getSplittingCompletedTime, ofMill(splitEnd))
                    .set(DatasetSourceDataDO::getIndexingTime, embeddingEnd - splitEnd)
                    .set(DatasetSourceDataDO::getDatasetProcessRuleId, ruleId)
                    .set(DatasetSourceDataDO::getEnabled, true);
        } catch (Exception e) {
            updateWrapper.set(DatasetSourceDataDO::getEnabled, false)
                    .set(DatasetSourceDataDO::getErrorMessage, e.getMessage());
            log.error("split and index error:", e);
            segmentMapper.updateStatus(documentId, DocumentSegmentEnum.ERROR.getCode());
        }
        long end = System.currentTimeMillis();
        updateWrapper.set(DatasetSourceDataDO::getCompletedAt, ofMill(end));
        sourceDataMapper.update(null, updateWrapper);
        log.info("spilt and index finished , time consume {}", end - start);
    }

    private LocalDateTime ofMill(long mill) {
        Instant instant = Instant.ofEpochMilli(mill);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    @Override
    public PageResult<DocumentSegmentDO> segmentDetail(SegmentPageQuery pageQuery) {
        return segmentMapper.page(pageQuery);
    }

    @Override
    public boolean updateEnable(String documentId, String segmentId, boolean enable) {
        Assert.notBlank(documentId, "datasetId is null");
        Assert.notBlank(segmentId, "segmentId is null");
        validateTenantId(documentId);
        int i = segmentMapper.updateEnable(documentId, segmentId, enable);
        return i > 0;
    }

    @Override
    public boolean deleteSegment(String datasetId, String documentId) {
        Assert.notBlank(datasetId, "datasetId is null");
        Assert.notBlank(documentId, "dataId is null");
        validateTenantId(documentId);
        int i = segmentMapper.deleteSegment(datasetId, documentId);
        return i > 0;
    }

    private void validateTenantId(String documentId) {
        LambdaQueryWrapper<DatasetSourceDataDO> queryWrapper = Wrappers.lambdaQuery(DatasetSourceDataDO.class)
                .eq(DatasetSourceDataDO::getUid, documentId)
                .eq(DatasetSourceDataDO::getTenantId, TenantContextHolder.getTenantId());
        Assert.isTrue(sourceDataMapper.exists(queryWrapper), "current tenant permission denied");
    }

    @Override
    public MatchQueryVO matchQuery(MatchQueryRequest request) {

        List<DocumentSegmentDO> segmentDOS = new ArrayList<>();
        try {
            DatasetsDO datasetsDO = datasetsService.getDatasets(Optional.ofNullable(request.getDatasetUid()).orElse(new ArrayList<>()).stream().findFirst().orElse(""));
            segmentDOS = segmentMapper.selectByDatasetIds(Arrays.asList(String.valueOf(datasetsDO.getId())));
        } catch (Exception e) {
            log.error("matchQuery.getDatasets is fail: {}", e.getMessage(), e);
        }

        List<String> segmentIds = segmentDOS.stream().map(DocumentSegmentDO::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(segmentIds)) {
            return MatchQueryVO.builder().queryText(request.getText()).build();
        }
        EmbeddingDetail queryText = basicEmbedding.embedText(request.getText());
        KnnQueryDTO knnQueryDTO = KnnQueryDTO.builder().segmentIds(segmentIds).k(request.getK()).build();
        List<KnnQueryHit> knnQueryHitList = basicVectorStore.knnSearch(queryText.getEmbedding(), knnQueryDTO);
        List<RecordDTO> recordDTOS = new ArrayList<>(knnQueryHitList.size());
        Map<String, DocumentSegmentDO> segmentDOMap = segmentDOS.stream().collect(Collectors.toMap(DocumentSegmentDO::getId, Function.identity(), (a, b) -> b));
        for (KnnQueryHit knnQueryHit : knnQueryHitList) {
            String segmentId = knnQueryHit.getDocument().getSegmentId();
            RecordDTO recordDTO = DocumentSegmentConvert.INSTANCE.segmentDo2Record(segmentDOMap.get(segmentId)).setScore(knnQueryHit.getScore());
            recordDTOS.add(recordDTO);
        }
        return MatchQueryVO.builder().records(recordDTOS).queryText(request.getText()).build();
    }

    @Override
    public List<String> similarQuery(SimilarQueryRequest request) {
        List<String> similarSegment = new ArrayList<>();
        if (CollectionUtils.isEmpty(request.getDatasetUid())) {
            return similarSegment;
        }
        List<DocumentSegmentDO> docSegments = segmentMapper.selectByDatasetIds(request.getDatasetUid());
        if (CollectionUtils.isEmpty(docSegments)) {
            return similarSegment;
        }
        List<String> segmentIds = docSegments.stream().map(DocumentSegmentDO::getId).collect(Collectors.toList());
        EmbeddingDetail queryEmbedding = basicEmbedding.embedText(request.getQuery());
        KnnQueryDTO knnQueryDTO = KnnQueryDTO.builder().segmentIds(segmentIds).k(request.getK()).build();

        List<KnnQueryHit> knnQueryHitList = basicVectorStore.knnSearch(queryEmbedding.getEmbedding(), knnQueryDTO);

        for (KnnQueryHit knnQueryHit : knnQueryHitList) {
            String segmentText = knnQueryHit.getDocument().getSegmentText();
            similarSegment.add(segmentText);
        }
        return similarSegment;
    }

    private String strToHex(String text) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
        }
        byte[] encodedHash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
