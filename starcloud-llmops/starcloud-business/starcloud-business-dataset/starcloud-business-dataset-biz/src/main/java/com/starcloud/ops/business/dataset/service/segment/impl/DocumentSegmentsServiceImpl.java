package com.starcloud.ops.business.dataset.service.segment.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.xiaoymin.knife4j.core.util.Assert;
import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageUpLoadRespVO;
import com.starcloud.ops.business.dataset.convert.segment.DocumentSegmentConvert;
import com.starcloud.ops.business.dataset.dal.dataobject.datasets.DatasetsDO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.DocumentSegmentDO;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.SegmentsEmbeddingsDO;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.SplitRulesDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasetsourcedata.DatasetSourceDataMapper;
import com.starcloud.ops.business.dataset.dal.mysql.segment.DocumentSegmentMapper;
import com.starcloud.ops.business.dataset.dal.mysql.segment.SegmentsEmbeddingsDOMapper;
import com.starcloud.ops.business.dataset.dal.mysql.segment.SplitRulesMapper;
import com.starcloud.ops.business.dataset.enums.DocumentSegmentEnum;
import com.starcloud.ops.business.dataset.pojo.dto.RecordDTO;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import com.starcloud.ops.business.dataset.pojo.request.FileSplitRequest;
import com.starcloud.ops.business.dataset.pojo.request.MatchTestRequest;
import com.starcloud.ops.business.dataset.pojo.request.SimilarQueryRequest;
import com.starcloud.ops.business.dataset.pojo.response.MatchTestResponse;
import com.starcloud.ops.business.dataset.pojo.response.SplitForecastResponse;
import com.starcloud.ops.business.dataset.service.datasets.DatasetsService;
import com.starcloud.ops.business.dataset.service.datasetstorage.DatasetStorageService;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import com.starcloud.ops.business.dataset.util.dataset.TextCleanUtils;
import com.starcloud.ops.llm.langchain.core.indexes.splitter.SplitterContainer;
import com.starcloud.ops.llm.langchain.core.model.embeddings.BasicEmbedding;
import com.starcloud.ops.llm.langchain.core.model.llm.document.*;
import com.starcloud.ops.llm.langchain.core.utils.TokenCalculator;
import com.starcloud.ops.llm.langchain.core.indexes.vectorstores.BasicVectorStore;
import com.starcloud.ops.llm.langchain.core.utils.TokenUtils;
import com.starcloud.ops.llm.langchain.core.utils.VectorSerializeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.DATASETS_EMBEDDING_ERROR;
import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.DATASETS_NOT_EXIST_ERROR;

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
    private SplitRulesMapper splitRulesMapper;

    @Autowired
    private DatasetSourceDataMapper sourceDataMapper;

    @Override
    public SplitForecastResponse splitForecast(FileSplitRequest fileSplitRequest) {
        validateTenantId(fileSplitRequest.getDocumentId());
        Tika tika = new Tika();
        tika.setMaxStringLength(-1);
        try {
            DatasetStorageUpLoadRespVO upLoadRespVO = datasetStorageService.getDatasetStorageByUID(fileSplitRequest.getDocumentId());
            String text = tika.parseToString(new URL(upLoadRespVO.getStorageKey()));
            SplitRule splitRule = fileSplitRequest.getSplitRule();
            String cleanText = TextCleanUtils.cleanText(text, splitRule);
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
    @Transactional(rollbackFor = Exception.class)
    public void indexDoc(String datasetId, String documentId, List<String> splitText) {
        Assert.notBlank(datasetId, "datasetId is null");
        Assert.notBlank(documentId, "documentId is null");
        log.info("start embedding index,datasetId={},documentId={}", datasetId, documentId);
        long start = System.currentTimeMillis();
        validateTenantId(documentId);
        DatasetsDO datasets = datasetsService.getDatasets(datasetId);
        if (datasets == null) {
            throw exception(DATASETS_NOT_EXIST_ERROR);
        }
        try {
            List<DocumentSegmentDTO> segments = new ArrayList<>(splitText.size());
            Long tenantId = TenantContextHolder.getTenantId();
            String creator = datasets.getCreator();
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
                documentSegmentDO.setStatus(DocumentSegmentEnum.INDEXING.getCode());
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
            log.info("embedding finished , time consume {}", embeddingEnd - start);
            segmentMapper.updateStatus(documentId, DocumentSegmentEnum.COMPLETED.getCode());
        } catch (Exception e) {
            log.error("embedding index error:", e);
            throw exception(DATASETS_EMBEDDING_ERROR);
        }
    }


    @Override
    public void splitAndIndex(SplitRule splitRule, String datasetId, String documentId, String url) {
        Assert.notBlank(datasetId, "datasetId is null");
        Assert.notBlank(documentId, "documentId is null");
        log.info("start split and index,datasetId={},documentId={},url={},splitRule={}", datasetId, documentId, url, splitRule);
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
            String cleanText = TextCleanUtils.cleanText(text, splitRule);
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
                documentSegmentDO.setStatus(DocumentSegmentEnum.INDEXING.getCode());
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
            SplitRulesDO splitRulesDO = new SplitRulesDO();
            splitRulesDO.setAutomatic(splitRule.getAutomatic());
            splitRulesDO.setRules(JSON.toJSONString(splitRule));
            splitRulesDO.setId(ruleId);
            splitRulesDO.setDatasetId(datasetId);
            splitRulesDO.setTenantId(tenantId);
            splitRulesDO.setCreator(creator);
            splitRulesDO.setDocumentId(documentId);
            splitRulesDO.setUpdater(creator);

            splitRulesMapper.insert(splitRulesDO);
            segmentMapper.updateStatus(documentId, DocumentSegmentEnum.COMPLETED.getCode());
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
    public List<DocumentSegmentDO> segmentDetail(String datasetId, boolean disable, String docId, int lastPosition) {
        Assert.notBlank(datasetId, "datasetId is null");
        Assert.notBlank(docId, "documentId is null");
        validateTenantId(docId);
        return segmentMapper.segmentDetail(datasetId, disable, docId, lastPosition);
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
        Assert.notBlank(documentId, "documentId is null");
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
    public MatchTestResponse matchTest(MatchTestRequest request) {
        validateTenantId(request.getDocumentId());
        List<DocumentSegmentDO> segmentDOS = segmentMapper.selectByDocId(request.getDocumentId());
        List<String> segmentIds = segmentDOS.stream().map(DocumentSegmentDO::getId).collect(Collectors.toList());
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
        return MatchTestResponse.builder().records(recordDTOS).queryText(request.getText()).build();
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
