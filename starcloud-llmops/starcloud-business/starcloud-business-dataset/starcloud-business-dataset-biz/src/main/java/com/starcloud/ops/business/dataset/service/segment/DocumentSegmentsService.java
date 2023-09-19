package com.starcloud.ops.business.dataset.service.segment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.DocumentSegmentDO;
import com.starcloud.ops.business.dataset.pojo.request.*;
import com.starcloud.ops.business.dataset.pojo.response.MatchQueryVO;
import com.starcloud.ops.business.dataset.pojo.response.SplitForecastResponse;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import org.apache.tika.exception.TikaException;

import javax.validation.Valid;
import java.io.IOException;

public interface DocumentSegmentsService {

    /**
     * 文档拆分预估
     *
     * @param fileSplitRequest
     * @return
     */
    public SplitForecastResponse splitForecast(FileSplitRequest fileSplitRequest);

    /**
     * 规则拆分文本
     * @param dataSourceId
     * @param text
     * @param splitRule
     */
    void splitDoc(String datasetId, String dataSourceId, String text, SplitRule splitRule);

    void splitDoc(String datasetId, String dataSourceId, SplitRule splitRule) throws IOException, TikaException;

    String segmentSummary(String documentId, String text, SplitRule splitRule, Integer summarySize);
    /**
     * 分段明细
     */
    PageResult<DocumentSegmentDO> segmentDetail(SegmentPageQuery pageQuery);

    /**
     * 启用/禁用文档
     */
    void updateEnable(Long documentId, boolean disable);

    /**
     * 删除文档所有分段
     */
    void deleteSegment(String datasetId, String documentId);

    /**
     * 文本向量匹配测试
     *
     * @param request
     * @return
     */
    MatchQueryVO matchQuery(@Valid MatchByDataSetIdRequest request);

    /**
     * 匹配文档中的分段
     * @param request
     * @return
     */
    MatchQueryVO matchQuery(@Valid MatchByDocIdRequest request);

    /**
     * 创建embedding索引
     *
     * @param datasetId
     * @param documentId
     */
    void indexDoc(String datasetId, String documentId);
}
