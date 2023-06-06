package com.starcloud.ops.business.dataset.controller.admin.datasets;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsPageReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsRespVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsUpdateReqVO;
import com.starcloud.ops.business.dataset.convert.datasets.DatasetsConvert;
import com.starcloud.ops.business.dataset.dal.dataobject.datasets.DatasetsDO;
import com.starcloud.ops.business.dataset.service.datasets.DatasetsService;
import io.swagger.annotations.ApiParam;
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

/**
 * @className    : DatasetsController
 * @description  : [数据集]
 * @author       : [wuruiqiang]
 * @version      : [v1.0]
 * @createTime   : [2023/5/31 16:00]
 * @updateUser   : [wuruiqiang]
 * @updateTime   : [2023/5/31 16:00]
 * @updateRemark : [暂无修改]
 */
@RestController
@RequestMapping("/llm/dataset")
@Tag(name = "星河云海-数据集", description = "星河云海数据集管理")
@Validated
public class DatasetsController {

    @Resource
    private DatasetsService datasetsService;

    @PostMapping("/create")
    @Operation(summary = "创建数据集")
    @PreAuthorize("@ss.hasPermission('starcloud-llmops:datasets:create')")
    public CommonResult<String> createDatasets(@Valid @RequestBody DatasetsCreateReqVO createReqVO) {
        return success(datasetsService.createDatasets(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新数据集")
    //@PreAuthorize("@ss.hasPermission('starcloud-llmops:datasets:update')")
    public CommonResult<Boolean> updateDatasets(@Valid @RequestBody DatasetsUpdateReqVO updateReqVO) {
        datasetsService.updateDatasets(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除数据集")
    @Parameter(name = "id", description = "编号", required = true)
    //@PreAuthorize("@ss.hasPermission('starcloud-llmops:datasets:delete')")
    public CommonResult<Boolean> deleteDatasets(@RequestParam("uid") @ApiParam(value = "数据集编号", required = true) String uid) {
        datasetsService.deleteDatasets(uid);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得数据集")
    @Parameter(name = "id", description = "编号", required = true)
    //@PreAuthorize("@ss.hasPermission('starcloud-llmops:datasets:query')")
    public CommonResult<DatasetsRespVO> getDatasets(@RequestParam("uid")  @ApiParam(value = "数据集编号", required = true) String uid) {
        DatasetsDO datasets = datasetsService.getDatasets(uid);
        return success(DatasetsConvert.convert(datasets));
    }


    @GetMapping("/page")
    @Operation(summary = "获得数据集分页")
    //@PreAuthorize("@ss.hasPermission('starcloud-llmops:datasets:query')")
    public CommonResult<PageResult<DatasetsRespVO>> getDatasetsPage(@Valid DatasetsPageReqVO pageVO) {
        PageResult<DatasetsDO> pageResult = datasetsService.getDatasetsPage(pageVO);
        return success(DatasetsConvert.convertPage(pageResult));
    }
}