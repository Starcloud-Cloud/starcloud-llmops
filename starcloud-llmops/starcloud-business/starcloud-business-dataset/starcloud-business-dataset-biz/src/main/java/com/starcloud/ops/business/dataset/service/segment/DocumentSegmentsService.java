package com.starcloud.ops.business.dataset.service.segment;

import com.starcloud.ops.business.dataset.dal.dataobject.segment.DocumentSegmentDO;
import com.starcloud.ops.business.dataset.pojo.request.FileSplitRequest;
import com.starcloud.ops.business.dataset.pojo.request.MatchTestRequest;
import com.starcloud.ops.business.dataset.pojo.request.SimilarQueryRequest;
import com.starcloud.ops.business.dataset.pojo.response.MatchTestResponse;
import com.starcloud.ops.business.dataset.pojo.response.SplitForecastResponse;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;

import java.util.List;

public interface DocumentSegmentsService {

    /**
     * 文档拆分预估
     *
     * @param fileSplitRequest
     * @return
     */
    public SplitForecastResponse splitForecast(FileSplitRequest fileSplitRequest);

    /**
     * 文件拆分、计算embedding
     *
     * @return
     */
    public void splitAndIndex(SplitRule splitRule, String datasetId, String documentId, String url);

    /**
     * 岸规则拆分文本
     * @param dataSourceId
     * @param text
     * @param splitRule
     */
    void splitDoc(String datasetId, String dataSourceId, String text, SplitRule splitRule);

    /**
     * 分段明细
     */
    public List<DocumentSegmentDO> segmentDetail(String datasetId, boolean disable, String docId, int lastPosition);

    /**
     * 启用/禁用分段
     */
    public boolean updateEnable(String documentId, String segmentId, boolean enable);

    /**
     * 删除文档所有分段
     */
    public boolean deleteSegment(String datasetId, String documentId);

    /**
     * 文本向量匹配测试
     *
     * @param request
     * @return
     */
    public MatchTestResponse matchTest(MatchTestRequest request);

    /**
     * 相似查询
     *
     * @param request
     * @return
     */
    List<String> similarQuery(SimilarQueryRequest request);

    /**
     * 创建embedding索引
     *
     * @param datasetId
     * @param documentId
     */
    void indexDoc(String datasetId, String documentId);
}
