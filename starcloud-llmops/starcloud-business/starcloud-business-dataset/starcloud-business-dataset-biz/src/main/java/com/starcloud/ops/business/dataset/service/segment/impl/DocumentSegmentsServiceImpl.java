package com.starcloud.ops.business.dataset.service.segment.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import com.github.xiaoymin.knife4j.core.util.Assert;
import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageUpLoadRespVO;
import com.starcloud.ops.business.dataset.convert.segment.DocumentSegmentConvert;
import com.starcloud.ops.business.dataset.dal.dataobject.datasets.DatasetsDO;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.DocumentSegmentDO;
import com.starcloud.ops.business.dataset.dal.es.ElasticsearchRepository;
import com.starcloud.ops.business.dataset.dal.mysql.segment.DocumentSegmentMapper;
import com.starcloud.ops.business.dataset.enums.DocumentSegmentEnum;
import com.starcloud.ops.business.dataset.enums.EmbeddingTypeEnum;
import com.starcloud.ops.business.dataset.pojo.dto.RecordDTO;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import com.starcloud.ops.business.dataset.pojo.request.*;
import com.starcloud.ops.business.dataset.pojo.response.MatchQueryVO;
import com.starcloud.ops.business.dataset.pojo.response.SplitForecastResponse;
import com.starcloud.ops.business.dataset.service.datasets.DatasetsService;
import com.starcloud.ops.business.dataset.service.datasetstorage.DatasetStorageService;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import com.starcloud.ops.business.dataset.service.task.IndexThreadPoolExecutor;
import com.starcloud.ops.business.dataset.service.task.SummaryEntity;
import com.starcloud.ops.business.dataset.service.task.SummaryTask;
import com.starcloud.ops.business.dataset.util.dataset.TextCleanAndSplitUtils;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.UserBenefitsBaseResultVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.UserBenefitsInfoResultVO;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.log.api.embedding.EmbeddingReqDTO;
import com.starcloud.ops.business.log.convert.LogEmbeddingConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogEmbeddingDO;
import com.starcloud.ops.business.log.service.embmedding.LogEmbeddingService;
import com.starcloud.ops.llm.langchain.core.indexes.splitter.SplitterContainer;
import com.starcloud.ops.llm.langchain.core.indexes.vectorstores.BasicVectorStore;
import com.starcloud.ops.llm.langchain.core.model.embeddings.BasicEmbedding;
import com.starcloud.ops.llm.langchain.core.model.llm.document.DocumentSegmentDTO;
import com.starcloud.ops.llm.langchain.core.model.llm.document.EmbeddingDetail;
import com.starcloud.ops.llm.langchain.core.model.llm.document.KnnQueryDTO;
import com.starcloud.ops.llm.langchain.core.model.llm.document.KnnQueryHit;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import com.starcloud.ops.llm.langchain.core.utils.TokenCalculator;
import com.starcloud.ops.llm.langchain.core.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.*;
import static com.starcloud.ops.business.limits.enums.ErrorCodeConstants.USER_BENEFITS_NOT_ADEQUATE;

@Service
@Slf4j
public class DocumentSegmentsServiceImpl implements DocumentSegmentsService {

    @Autowired
    private BasicEmbedding basicEmbedding;

    @Autowired
    private DocumentSegmentMapper segmentMapper;


    @Autowired
    private BasicVectorStore basicVectorStore;

    @Autowired
    private DatasetStorageService datasetStorageService;

    @Autowired
    private DatasetsService datasetsService;

    @Autowired
    private UserBenefitsService userBenefitsService;

    @Autowired
    private SummaryTask summaryTask;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private LogEmbeddingService logEmbeddingService;

    @Override
    public SplitForecastResponse splitForecast(FileSplitRequest fileSplitRequest) {
        Tika tika = new Tika();
        tika.setMaxStringLength(-1);
        try {
            DatasetStorageUpLoadRespVO upLoadRespVO = datasetStorageService.getDatasetStorageByUID(fileSplitRequest.getDocumentId());
            String text = tika.parseToString(new URL(upLoadRespVO.getStorageKey()));
            SplitRule splitRule = fileSplitRequest.getSplitRule();
            String cleanText = TextCleanAndSplitUtils.splitText(text, splitRule);
            List<String> splitText = SplitterContainer.TOKEN_TEXT_SPLITTER.getSplitter().splitText(cleanText, splitRule.getChunkSize(), splitRule.getSeparator());
            Long totalTokens = splitText.stream().mapToLong(split -> TokenUtils.tokens(ModelType.TEXT_DAVINCI_002, split)).sum();
            BigDecimal totalPrice = TokenCalculator.getTextPrice(totalTokens, ModelTypeEnum.TEXT_EMBEDDING_ADA_002);
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
        List<DocumentSegmentDO> segmentDOS = segmentMapper.selectByDocId(documentId);
        segmentDOS = segmentDOS.stream().filter(documentSegmentDO -> !DocumentSegmentEnum.INDEXED.getCode().equals(documentSegmentDO.getStatus())).collect(Collectors.toList());
        Long tenantId = TenantContextHolder.getTenantId();
        String creator = datasets.getCreator();

        Integer reduce = segmentDOS.stream().map(DocumentSegmentDO::getWordCount).reduce(0, Integer::sum);
        UserBenefitsInfoResultVO userBenefits = userBenefitsService.getUserBenefits(Long.valueOf(creator));
        Optional<UserBenefitsBaseResultVO> baseResultVO = userBenefits.getBenefits().stream().filter(b -> BenefitsTypeEnums.TOKEN.getCode().equals(b.getType())).findFirst();
        if (!baseResultVO.isPresent()) {
            log.warn("{}  token 权益为空", creator);
            throw exception(USER_BENEFITS_NOT_ADEQUATE, creator, reduce / 10 , 0);
        } else {
            Long totalNum = baseResultVO.get().getTotalNum();
            // 每个字符预估 1.5个token ，   embedding价格按 1/15 计算
            if (reduce / 10 > totalNum) {
                log.warn("{}  token 权益为不足，size={}，tokens={}", creator, reduce, totalNum);
                throw exception(USER_BENEFITS_NOT_ADEQUATE, creator, reduce / 10 , totalNum);
            }
        }
        CountDownLatch countDownLatch = new CountDownLatch(segmentDOS.size());
        AtomicInteger atomicInteger = new AtomicInteger(0);

        for (DocumentSegmentDO segmentDO : segmentDOS) {
            IndexThreadPoolExecutor.execute(() -> {
                try {
                    String split = segmentDO.getContent();
                    String segmentHash = segmentDO.getSegmentHash();
                    DocumentSegmentDTO documentSegmentDTO = new DocumentSegmentDTO();
                    documentSegmentDTO.setTenantId(tenantId);
                    documentSegmentDTO.setCreator(creator);
                    documentSegmentDTO.setDatasetId(datasetId);
                    documentSegmentDTO.setDocumentId(documentId);
                    documentSegmentDTO.setSegmentId(segmentDO.getId());
                    documentSegmentDTO.setContent(split);
                    documentSegmentDTO.setPosition(segmentDO.getPosition());
                    documentSegmentDTO.setWordCount(segmentDO.getWordCount());
                    documentSegmentDTO.setSegmentHash(segmentHash);
                    documentSegmentDTO.setStatus(true);
                    documentSegmentDTO.setCreateTime(System.currentTimeMillis());
                    DocumentSegmentDTO repeat = ElasticsearchRepository.getByHash(segmentHash);
                    if (ObjectUtil.isNotNull(repeat)) {
                        segmentDO.setTokens(repeat.getTokens());
                        documentSegmentDTO.setTokens(repeat.getTokens());
                        documentSegmentDTO.setVector(repeat.getVector());
                    } else {
                        EmbeddingReqDTO reqDTO = new EmbeddingReqDTO();
                        reqDTO.setDocumentId(documentId);
                        reqDTO.setType(EmbeddingTypeEnum.DOCUMENT.name());
                        reqDTO.setContent(split);
                        reqDTO.setUserId(creator);
                        reqDTO.setUpdater(creator);

                        EmbeddingDetail embeddingDetail = embedding(reqDTO);
                        segmentDO.setTokens(embeddingDetail.getTotalTokens());
                        documentSegmentDTO.setVector(embeddingDetail.getEmbedding());
                        documentSegmentDTO.setTokens(embeddingDetail.getTotalTokens());
                    }
                    basicVectorStore.addSegment(Collections.singletonList(documentSegmentDTO));
                    atomicInteger.incrementAndGet();
                    segmentDO.setStatus(DocumentSegmentEnum.INDEXED.getCode());
                    segmentMapper.updateById(segmentDO);
                } catch (Exception e) {
                    log.error("embedding index error:", e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }


        try {
            boolean await = countDownLatch.await(100, TimeUnit.SECONDS);
            int successNum = atomicInteger.get();
            if (!await) {
                log.info("count down finish, success number {}, total ={}", successNum, segmentDOS.size());
                throw exception(new ErrorCode(501, "部分索引失败，请重试"));
            }
            if (successNum < segmentDOS.size()) {
                log.info("count down finish, success number {}, total ={}", successNum, segmentDOS.size());
                throw exception(new ErrorCode(501, "部分索引失败，请重试"));
            }
            long end = System.currentTimeMillis();
            log.info("index success， success number {}, total ={} ,rt：{} ms", successNum, segmentDOS.size(), end - start);
        } catch (InterruptedException e) {
            log.info("index error，docId = {}", documentId, e);
            throw exception(new ErrorCode(501, "index InterruptedException"));
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
        List<String> splitText = SplitterContainer.CHARACTER_TEXT_SPLITTER.getSplitter().splitText(text, splitRule.getChunkSize(), splitRule.getSeparator());
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


    private LocalDateTime ofMill(long mill) {
        Instant instant = Instant.ofEpochMilli(mill);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    @Override
    public PageResult<DocumentSegmentDO> segmentDetail(SegmentPageQuery pageQuery) {
        return segmentMapper.page(pageQuery);
    }

    @Override
    public void updateEnable(Long documentId, boolean disable) {
        List<DocumentSegmentDO> documentSegmentDOS = segmentMapper.selectByDocIds(Collections.singletonList(documentId));
        if (CollectionUtils.isEmpty(documentSegmentDOS)) {
            return;
        }
        List<DocumentSegmentDTO> documentSegmentDTOS = documentSegmentDOS.stream()
                .map(doc -> DocumentSegmentDTO.builder().segmentId(doc.getId()).status(!disable).build())
                .collect(Collectors.toList());
        basicVectorStore.updateSegment(documentSegmentDTOS);
        segmentMapper.updateEnable(String.valueOf(documentId), disable);
    }

    @Override
    public void deleteSegment(String datasetId, String documentId) {
        Assert.notBlank(datasetId, "datasetId is null");
        Assert.notBlank(documentId, "dataId is null");
        basicVectorStore.deleteSegment(Collections.singletonList(documentId));
        segmentMapper.deleteSegment(datasetId, documentId);
    }


    @Override
    public MatchQueryVO matchQuery(MatchByDataSetIdRequest request) {
        List<String> datasetIds;
        try {
            // FIXME: 2023/9/12  这里的参数为 appId 即应用 ID
            DatasetsDO datasetsDO = datasetsService.getDatasetInfoByAppId(Optional.ofNullable(request.getDatasetUid()).orElse(new ArrayList<>()).stream().findFirst().orElse(""));
            datasetIds = Collections.singletonList(datasetsDO.getId().toString());
        } catch (Exception e) {
            log.error("matchQuery.getDatasets is fail: {}, {}", e.getMessage(), request.getDatasetUid());
            return null;

        }

        EmbeddingReqDTO reqDTO = new EmbeddingReqDTO();
        reqDTO.setType(EmbeddingTypeEnum.QUERY.name());
        reqDTO.setContent(request.getText());
        reqDTO.setUserId(request.getUserId().toString());
        reqDTO.setUpdater(request.getUserId().toString());

        EmbeddingDetail queryText = embedding(reqDTO);
        KnnQueryDTO knnQueryDTO = KnnQueryDTO.builder()
                .datasetIds(datasetIds).minScore(request.getMinScore()).k(request.getK()).build();
        List<KnnQueryHit> knnQueryHitList = basicVectorStore.knnSearch(queryText.getEmbedding(), knnQueryDTO);
        return MatchQueryVO.builder().records(buildRecord(knnQueryHitList)).tokens(queryText.getTotalTokens()).queryText(request.getText()).build();
    }

    private List<RecordDTO> buildRecord(List<KnnQueryHit> knnQueryHitList) {
        return knnQueryHitList.stream().map(knnQueryHit -> {
            RecordDTO recordDTO = DocumentSegmentConvert.INSTANCE.convert(knnQueryHit.getDocument());
            recordDTO.setScore(knnQueryHit.getScore());
            return recordDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public MatchQueryVO matchQuery(MatchByDocIdRequest request) {
        if (CollectionUtils.isEmpty(request.getDocId())) {
            return MatchQueryVO.builder().queryText(request.getText()).build();
        }

        EmbeddingReqDTO reqDTO = new EmbeddingReqDTO();
        reqDTO.setType(EmbeddingTypeEnum.QUERY.name());
        reqDTO.setContent(request.getText());
        reqDTO.setUserId(request.getUserId().toString());
        reqDTO.setUpdater(request.getUserId().toString());

        EmbeddingDetail queryText = embedding(reqDTO);
        KnnQueryDTO knnQueryDTO = KnnQueryDTO.builder()
                .documentIds(request.getDocId().stream().map(String::valueOf).collect(Collectors.toList()))
                .k(request.getK()).minScore(request.getMinScore()).build();
        List<KnnQueryHit> knnQueryHitList = basicVectorStore.knnSearch(queryText.getEmbedding(), knnQueryDTO);
        return MatchQueryVO.builder().records(buildRecord(knnQueryHitList)).queryText(request.getText()).tokens(queryText.getTotalTokens()).build();
    }


    private EmbeddingDetail embedding(EmbeddingReqDTO reqDTO) {
        String hash = strToHex(reqDTO.getContent());
        try {
            String embJson = redisTemplate.boundValueOps(hash).get();
            EmbeddingDetail bean = JSONUtil.toBean(embJson, EmbeddingDetail.class);
            if (bean != null && !CollectionUtils.isEmpty(bean.getEmbedding())) {
                return bean;
            }
        } catch (Exception e) {
            log.warn("获取缓存embedding失败", e);
        }
        try {
            userBenefitsService.allowExpendBenefits(BenefitsTypeEnums.TOKEN.getCode(), Long.valueOf(reqDTO.getUserId()));
            EmbeddingDetail embeddingDetail = basicEmbedding.embedText(reqDTO.getContent());
            userBenefitsService.expendBenefits(BenefitsTypeEnums.TOKEN.getCode(),
                    embeddingDetail.getTotalTokens() / 15, Long.valueOf(reqDTO.getUserId()), null);
            redisTemplate.boundValueOps(hash).set(JSONUtil.toJsonStr(embeddingDetail), 1, TimeUnit.DAYS);
            reqDTO.setTextHash(hash);
            reqDTO.setWordCount(reqDTO.getContent().length());
            reqDTO.setTokens(embeddingDetail.getTotalTokens());
            createEmbeddingLog(reqDTO);
            return embeddingDetail;
        } catch (ServiceException e) {
            log.error("权益计算异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("计算embedding异常", e);
            throw ServiceExceptionUtil.exception(new ErrorCode(500, "计算embedding异常,请重试:" + e.getMessage()));
        }
    }


    private void createEmbeddingLog(EmbeddingReqDTO reqDTO) {
        TenantContextHolder.setIgnore(true);
        LogEmbeddingDO logEmbeddingDO = LogEmbeddingConvert.INSTANCE.convert(reqDTO);
        logEmbeddingService.createLog(logEmbeddingDO);
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
