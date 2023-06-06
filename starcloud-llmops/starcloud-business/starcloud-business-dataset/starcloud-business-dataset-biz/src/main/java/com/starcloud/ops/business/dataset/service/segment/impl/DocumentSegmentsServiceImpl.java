package com.starcloud.ops.business.dataset.service.segment.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.github.xiaoymin.knife4j.core.util.Assert;
import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.DocumentSegmentDO;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.SegmentsEmbeddingsDO;
import com.starcloud.ops.business.dataset.dal.mysql.segment.DocumentSegmentMapper;
import com.starcloud.ops.business.dataset.dal.mysql.segment.SegmentsEmbeddingsDOMapper;
import com.starcloud.ops.business.dataset.enums.DocumentSegmentEnum;
import com.starcloud.ops.business.dataset.pojo.request.FileSplitRequest;
import com.starcloud.ops.business.dataset.pojo.response.SplitForecastResponse;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import com.starcloud.ops.llm.langchain.core.embeddings.BasicEmbedding;
import com.starcloud.ops.llm.langchain.core.model.llm.document.DocumentSegment;
import com.starcloud.ops.llm.langchain.core.model.llm.document.EmbeddingDetail;
import com.starcloud.ops.llm.langchain.core.model.llm.document.SplitDetail;
import com.starcloud.ops.llm.langchain.core.model.llm.document.SplitRule;
import com.starcloud.ops.llm.langchain.core.parser.DocumentSegmentsParser;
import com.starcloud.ops.llm.langchain.core.utils.TokenCalculator;
import com.starcloud.ops.llm.langchain.core.vectorstores.BasicVectorStore;
import com.starcloud.ops.llm.langchain.core.utils.VectorSerializeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public SplitForecastResponse splitForecast(FileSplitRequest fileSplitRequest) {
        Tika tika = new Tika();
        try {
            String text = tika.parseToString(new URL(""));

            List<SplitDetail> splitDocs = DocumentSegmentsParser.INSTANCE.splitText(text, fileSplitRequest.getSplitRule());
            long totalTokens = splitDocs.stream().map(SplitDetail::getTokens).count();
            BigDecimal totalPrice = TokenCalculator.getTextPrice(totalTokens, ModelType.TEXT_EMBEDDING_ADA_002);
            return SplitForecastResponse.builder().splitList(splitDocs).totalPrice(totalPrice).build();
        } catch (IOException | TikaException e) {
            log.info("split forecast error:", e);
            throw new ServiceException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void splitAndIndex(SplitRule splitRule, String datasetId, String documentId, String url) {
        Assert.notBlank(datasetId, "datasetId is null");
        Assert.notBlank(documentId, "documentId is null");
        Tika tika = new Tika();
        try {
            long start = System.currentTimeMillis();
            String text = tika.parseToString(new URL(url));
            long parseEnd = System.currentTimeMillis();
            log.info("parse finished , time consume {}", parseEnd - start);
            List<SplitDetail> splitDocs = DocumentSegmentsParser.INSTANCE.splitText(text, splitRule);
            long splitEnd = System.currentTimeMillis();
            log.info("clean and split finished , time consume {}", splitEnd - parseEnd);
            List<DocumentSegment> segments = new ArrayList<>(splitDocs.size());
            Long tenantId = TenantContextHolder.getTenantId();
            String creator = WebFrameworkUtils.getLoginUserId().toString();

            for (int i = 0; i < splitDocs.size(); i++) {
                String segmentId = IdUtil.getSnowflakeNextIdStr();
                SplitDetail splitDoc = splitDocs.get(i);
                String segmentHash = strToHex(splitDoc.getSegment());
                DocumentSegment documentSegment = new DocumentSegment();
                documentSegment.setTenantId(tenantId);
                documentSegment.setCreator(creator);
                documentSegment.setDataSetId(datasetId);
                documentSegment.setDocumentId(documentId);
                documentSegment.setSegmentId(segmentId);
                documentSegment.setSegmentText(splitDoc.getSegment());
                documentSegment.setStatus(true);

                DocumentSegmentDO documentSegmentDO = new DocumentSegmentDO();
                documentSegmentDO.setId(segmentId);
                documentSegmentDO.setTenantId(tenantId);
                documentSegmentDO.setCreator(creator);
                documentSegmentDO.setUpdater(creator);
                documentSegmentDO.setDatasetId(datasetId);
                documentSegmentDO.setDocumentId(documentId);
                documentSegmentDO.setPosition(i);
                documentSegmentDO.setContent(splitDoc.getSegment());
                documentSegmentDO.setWordCount(splitDoc.getSegment().length());
                documentSegmentDO.setSegmentHash(segmentHash);
                documentSegmentDO.setStatus(DocumentSegmentEnum.COMPLETED.getCode());
                SegmentsEmbeddingsDO segmentsEmbeddingsDO = embeddingsDOMapper.selectOneByHash(segmentHash);
                if (ObjectUtil.isNotNull(segmentsEmbeddingsDO)) {
                    segmentsEmbeddingsDO.setId(null);
                    documentSegmentDO.setTokens(segmentsEmbeddingsDO.getTokens());
                    documentSegment.setVector(VectorSerializeUtils.deserialize(segmentsEmbeddingsDO.getVector()));
                } else {
                    EmbeddingDetail embeddingDetail = basicEmbedding.embedText(splitDoc.getSegment());
                    segmentsEmbeddingsDO = new SegmentsEmbeddingsDO();
                    segmentsEmbeddingsDO.setTokens(embeddingDetail.getTotalTokens());
                    segmentsEmbeddingsDO.setVector(VectorSerializeUtils.serialize(embeddingDetail.getEmbedding()));
                    documentSegmentDO.setTokens(embeddingDetail.getTotalTokens());
                    documentSegment.setVector(embeddingDetail.getEmbedding());
                }
                segmentsEmbeddingsDO.setTenantId(tenantId);
                segmentsEmbeddingsDO.setCreator(creator);
                segmentsEmbeddingsDO.setDatasetId(datasetId);
                segmentsEmbeddingsDO.setDocumentId(documentId);
                segmentsEmbeddingsDO.setDeleted(false);
                segmentMapper.insert(documentSegmentDO);
                embeddingsDOMapper.insert(segmentsEmbeddingsDO);
                segments.add(documentSegment);
            }
            basicVectorStore.addSegment(segments);
            long embeddingEnd = System.currentTimeMillis();
            // todo 更新doc状态

            log.info("embedding finished , time consume {}", embeddingEnd - splitEnd);
        } catch (IOException | TikaException | ClassNotFoundException e) {
            log.info("split and index error:", e);
            throw new ServiceException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    public List<DocumentSegmentDO> segmentDetail(String datasetId, boolean disable, String docId, int lastPosition) {
        Assert.notBlank(datasetId, "datasetId is null");
        Assert.notBlank(docId, "documentId is null");
        return segmentMapper.segmentDetail(datasetId, disable, docId, lastPosition);
    }

    @Override
    public boolean updateEnable(String datasetId, String segmentId, boolean enable) {
        Assert.notBlank(datasetId, "datasetId is null");
        Assert.notBlank(segmentId, "segmentId is null");
        int i = segmentMapper.updateEnable(datasetId, segmentId, enable);
        return i > 0;
    }

    @Override
    public boolean deleteSegment(String datasetId, String documentId) {
        Assert.notBlank(datasetId, "datasetId is null");
        Assert.notBlank(documentId, "documentId is null");
        int i = segmentMapper.deleteSegment(datasetId, documentId);
        return i > 0;
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
