package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataPageReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataRespVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataUpdateReqVO;
import com.starcloud.ops.business.dataset.convert.datasetsourcedata.DatasetSourceDataConvert;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

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
    public CommonResult<Long> createDatasetSourceData(@Valid @RequestBody DatasetSourceDataCreateReqVO createReqVO) {
        return success(datasetSourceDataService.createDatasetSourceData(createReqVO));
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

    @GetMapping("/get")
    @Operation(summary = "获得数据集源数据")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:query')")
    public CommonResult<DatasetSourceDataRespVO> getDatasetSourceData(@RequestParam("id") Long id) {
        DatasetSourceDataDO datasetSourceData = datasetSourceDataService.getDatasetSourceData(id);
        return success(DatasetSourceDataConvert.INSTANCE.convert(datasetSourceData));
    }

    @GetMapping("/list")
    @Operation(summary = "获得数据集源数据列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:query')")
    public CommonResult<List<DatasetSourceDataRespVO>> getDatasetSourceDataList(@RequestParam("ids") Collection<Long> ids) {
        List<DatasetSourceDataDO> list = datasetSourceDataService.getDatasetSourceDataList(ids);
        return success(DatasetSourceDataConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获得数据集源数据分页")
    @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:query')")
    public CommonResult<PageResult<DatasetSourceDataRespVO>> getDatasetSourceDataPage(@Valid DatasetSourceDataPageReqVO pageVO) {
        PageResult<DatasetSourceDataDO> pageResult = datasetSourceDataService.getDatasetSourceDataPage(pageVO);
        return success(DatasetSourceDataConvert.INSTANCE.convertPage(pageResult));
    }

}