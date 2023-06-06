package com.starcloud.ops.business.dataset.service.segment;

import com.starcloud.ops.business.dataset.dal.dataobject.segment.DocumentSegmentDO;
import com.starcloud.ops.business.dataset.pojo.request.FileSplitRequest;
import com.starcloud.ops.business.dataset.pojo.response.SplitForecastResponse;
import com.starcloud.ops.llm.langchain.core.model.llm.document.SplitRule;

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
     * 分段明细
     */
    public List<DocumentSegmentDO> segmentDetail(String datasetId, boolean disable, String docId, int lastPosition);

    /**
     * 启用/禁用分段
     */
    public boolean updateEnable(String datasetId, String segmentId, boolean enable);

    /**
     * 删除文档所有分段
     */
    public boolean deleteSegment(String datasetId, String documentId);
}
