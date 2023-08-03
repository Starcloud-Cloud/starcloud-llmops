package com.starcloud.ops.business.dataset.controller.admin.segment;


import cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.DocumentSegmentDO;
import com.starcloud.ops.business.dataset.pojo.request.FileSplitRequest;
import com.starcloud.ops.business.dataset.pojo.request.MatchQueryRequest;
import com.starcloud.ops.business.dataset.pojo.request.SegmentPageQuery;
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

    @GetMapping("/split/enable/{documentId}/{segmentId}")
    @Operation(summary = "文档分段禁用/启用", description = "文档分段禁用/启用")
    public CommonResult<Boolean> updateEnable(
            @PathVariable("documentId") String documentId,
            @PathVariable("segmentId") String segmentId,
            @RequestParam(value = "disable") boolean enable

    ) {
        boolean success = documentSegmentsService.updateEnable(documentId, segmentId, enable);
        return success ? CommonResult.success(true) : CommonResult.error(GlobalErrorCodeConstants.LOCKED);
    }

    @PostMapping("/match/text")
    @Operation(summary = "文档分段命中测试", description = "文档分段命中测试")
    public CommonResult matchTest(@RequestBody @Valid MatchQueryRequest request) {
        return CommonResult.success(documentSegmentsService.matchQuery(request));
    }



}
