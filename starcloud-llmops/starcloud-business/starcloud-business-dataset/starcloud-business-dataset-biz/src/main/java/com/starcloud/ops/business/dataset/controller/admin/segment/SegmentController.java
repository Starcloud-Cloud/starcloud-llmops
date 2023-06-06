package com.starcloud.ops.business.dataset.controller.admin.segment;


import cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.dataset.dal.dataobject.segment.DocumentSegmentDO;
import com.starcloud.ops.business.dataset.pojo.request.FileSplitRequest;
import com.starcloud.ops.business.dataset.pojo.response.SplitForecastResponse;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    @GetMapping("/split/detail/{datasetId}/{documentId}")
    @Operation(summary = "文档分段明细", description = "文档分段明细")
    public CommonResult<List<DocumentSegmentDO>> segmentDetail(
            @PathVariable("datasetId") String datasetId,
            @PathVariable("documentId") String documentId,
            @RequestParam(value = "disable", defaultValue = "true") boolean disable,
            @RequestParam(value = "disable", defaultValue = "-1") int lastPosition

    ) {
        List<DocumentSegmentDO> documentSegmentDOS = documentSegmentsService.segmentDetail(datasetId, disable, documentId, lastPosition);
        return CommonResult.success(documentSegmentDOS);
    }

    @GetMapping("/split/enable/{datasetId}/{documentId}")
    @Operation(summary = "文档分段禁用/启用", description = "文档分段禁用/启用")
    public CommonResult<Boolean> updateEnable(
            @PathVariable("datasetId") String datasetId,
            @PathVariable("documentId") String documentId,
            @RequestParam(value = "disable") boolean enable

    ) {
        boolean success = documentSegmentsService.updateEnable(datasetId, documentId, enable);
        return success ? CommonResult.success(true): CommonResult.error(GlobalErrorCodeConstants.LOCKED);
    }




}
