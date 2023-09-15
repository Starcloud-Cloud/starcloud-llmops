package com.starcloud.ops.business.dataset.controller.admin.segment;


import cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.DocumentSegmentDO;
import com.starcloud.ops.business.dataset.pojo.request.FileSplitRequest;
import com.starcloud.ops.business.dataset.pojo.request.MatchByDataSetIdRequest;
import com.starcloud.ops.business.dataset.pojo.request.MatchByDocIdRequest;
import com.starcloud.ops.business.dataset.pojo.request.SegmentPageQuery;
import com.starcloud.ops.business.dataset.pojo.response.MatchQueryVO;
import com.starcloud.ops.business.dataset.pojo.response.SplitForecastResponse;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/llm/dataset/segment")
@Tag(name = "管理后台 - 数据集源文档分段")
public class SegmentController {

    @Autowired
    private DocumentSegmentsService documentSegmentsService;

    @PostMapping("/split/forecast")
    @Operation(summary = "文档拆分预测", description = "文档拆分预测")
    public CommonResult<SplitForecastResponse> splitForecast(@RequestBody @Valid FileSplitRequest fileSplitRequest) {
        SplitForecastResponse splitForecastResponse = documentSegmentsService.splitForecast(fileSplitRequest);
        return CommonResult.success(splitForecastResponse);
    }

    @GetMapping("/segment/detail")
    @Operation(summary = "文档分段明细", description = "文档分段明细")
    public CommonResult<PageResult<DocumentSegmentDO>> segmentDetail(@Validated @RequestBody SegmentPageQuery pageQuery
    ) {
        return CommonResult.success(documentSegmentsService.segmentDetail(pageQuery));
    }

    @GetMapping("/split/enable/{documentId}")
    @Operation(summary = "文档禁用/启用", description = "文档禁用/启用")
    public CommonResult<Boolean> updateEnable(
            @PathVariable("documentId") Long documentId,
            @RequestParam(value = "disable") boolean disable

    ) {
        documentSegmentsService.updateEnable(documentId, disable);
        return CommonResult.success(true);
    }

    @PostMapping("/match/dataset/text")
    @Operation(summary = "数据集分段命中测试", description = "文档分段命中测试-datasetUid")
    public CommonResult<MatchQueryVO> matchDatasetTest(@RequestBody @Valid MatchByDataSetIdRequest request) {
        return CommonResult.success(documentSegmentsService.matchQuery(request));
    }


    @PostMapping("/match/document/text")
    @Operation(summary = "文档分段命中测试", description = "文档分段命中测试-docId")
    public CommonResult<MatchQueryVO> matchDocTest(@RequestBody @Valid MatchByDocIdRequest request) {
        request.setUserId(WebFrameworkUtils.getLoginUserId());
        return CommonResult.success(documentSegmentsService.matchQuery(request));
    }

    @DeleteMapping("/split/delete/{datasetId}/{documentId}}")
    @Operation(summary = "删除文档分段", description = "删除文档分段")
    public CommonResult<Boolean> deleteDoc(@PathVariable("datasetId") String datasetId,
                                           @PathVariable("documentId") String documentId) {
        documentSegmentsService.deleteSegment(datasetId, documentId);
        return CommonResult.success(true);
    }

}
