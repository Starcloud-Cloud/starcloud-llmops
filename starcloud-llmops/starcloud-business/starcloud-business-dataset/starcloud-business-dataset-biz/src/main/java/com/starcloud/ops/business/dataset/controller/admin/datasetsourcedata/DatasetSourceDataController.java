package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataUpdateReqVO;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 数据集源数据")
@RestController
@RequestMapping("/llm/dataset-source-data")
@Validated
public class DatasetSourceDataController {

    @Resource
    private DatasetSourceDataService datasetSourceDataService;

    @PostMapping("/create")
    @Operation(summary = "创建数据集源数据")
    @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:create')")
    public CommonResult<Boolean> createDatasetSourceData(@Valid @RequestBody DatasetSourceDataCreateReqVO createReqVO) {
        datasetSourceDataService.createDatasetSourceData(createReqVO);
        return success(true);
    }

    @PutMapping("/update")
    @Operation(summary = "更新数据集源数据")
    @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:update')")
    public CommonResult<Boolean> updateDatasetSourceData(@Valid @RequestBody DatasetSourceDataUpdateReqVO updateReqVO) {
        datasetSourceDataService.updateDatasetSourceData(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除数据集源数据")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:delete')")
    public CommonResult<Boolean> deleteDatasetSourceData(@RequestParam("id") Long id) {
        datasetSourceDataService.deleteDatasetSourceData(id);
        return success(true);
    }

}