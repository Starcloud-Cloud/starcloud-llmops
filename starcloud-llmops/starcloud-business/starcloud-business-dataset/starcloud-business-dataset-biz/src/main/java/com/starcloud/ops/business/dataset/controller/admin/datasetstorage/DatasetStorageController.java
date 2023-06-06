package com.starcloud.ops.business.dataset.controller.admin.datasetstorage;

import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStoragePageReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageRespVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageUpdateReqVO;
import com.starcloud.ops.business.dataset.convert.datasetstorage.DatasetStorageConvert;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage.DatasetStorageDO;
import com.starcloud.ops.business.dataset.service.datasetstorage.DatasetStorageService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;



@Tag(name = "管理后台 - 数据集源数据存储")
@RestController
@RequestMapping("/llm/dataset-storage")
@Validated
public class DatasetStorageController {

    @Resource
    private DatasetStorageService datasetStorageService;

    @PostMapping("/create")
    @Operation(summary = "创建数据集源数据存储")
    @PreAuthorize("@ss.hasPermission('llm:dataset-storage:create')")
    public CommonResult<Long> createDatasetStorage(@Valid @RequestBody DatasetStorageCreateReqVO createReqVO) {
        return success(datasetStorageService.createDatasetStorage(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新数据集源数据存储")
    @PreAuthorize("@ss.hasPermission('llm:dataset-storage:update')")
    public CommonResult<Boolean> updateDatasetStorage(@Valid @RequestBody DatasetStorageUpdateReqVO updateReqVO) {
        datasetStorageService.updateDatasetStorage(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除数据集源数据存储")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('llm:dataset-storage:delete')")
    public CommonResult<Boolean> deleteDatasetStorage(@RequestParam("id") Long id) {
        datasetStorageService.deleteDatasetStorage(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得数据集源数据存储")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('llm:dataset-storage:query')")
    public CommonResult<DatasetStorageRespVO> getDatasetStorage(@RequestParam("id") Long id) {
        DatasetStorageDO datasetStorage = datasetStorageService.getDatasetStorage(id);
        return success(DatasetStorageConvert.INSTANCE.convert(datasetStorage));
    }

    @GetMapping("/list")
    @Operation(summary = "获得数据集源数据存储列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    @PreAuthorize("@ss.hasPermission('llm:dataset-storage:query')")
    public CommonResult<List<DatasetStorageRespVO>> getDatasetStorageList(@RequestParam("ids") Collection<Long> ids) {
        List<DatasetStorageDO> list = datasetStorageService.getDatasetStorageList(ids);
        return success(DatasetStorageConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获得数据集源数据存储分页")
    @PreAuthorize("@ss.hasPermission('llm:dataset-storage:query')")
    public CommonResult<PageResult<DatasetStorageRespVO>> getDatasetStoragePage(@Valid DatasetStoragePageReqVO pageVO) {
        PageResult<DatasetStorageDO> pageResult = datasetStorageService.getDatasetStoragePage(pageVO);
        return success(DatasetStorageConvert.INSTANCE.convertPage(pageResult));
    }


}