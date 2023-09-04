package com.starcloud.ops.business.dataset.service.segment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.DocumentSegmentDO;
import com.starcloud.ops.business.dataset.pojo.request.*;
import com.starcloud.ops.business.dataset.pojo.response.MatchQueryVO;
import com.starcloud.ops.business.dataset.pojo.response.SplitForecastResponse;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import org.apache.tika.exception.TikaException;

import java.io.IOException;
import java.net.MalformedURLException;
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

    void splitDoc(String datasetId, String dataSourceId, SplitRule splitRule) throws IOException, TikaException;

    String segmentSummary(String documentId, String text, SplitRule splitRule, Integer summarySize);
    /**
     * 分段明细
     */
    PageResult<DocumentSegmentDO> segmentDetail(SegmentPageQuery pageQuery);

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
    public MatchQueryVO matchQuery(MatchQueryRequest request);

    /**
     * 匹配文档中的分段
     * @param request
     * @return
     */
    MatchQueryVO matchQuery(MatchByDocIdRequest request);

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
