package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.*;
import com.starcloud.ops.business.dataset.enums.DataSourceDataModelEnum;
import com.starcloud.ops.business.dataset.enums.DataSourceDataTypeEnum;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.dto.SourceDataUploadDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Slf4j
@Tag(name = "星河云海 - 数据集 - 源数据", description = "星河云海数据集管理")
@RestController
@RequestMapping("/llm/dataset-source-data")
@Validated
public class DatasetSourceDataController {

    @Resource
    private DatasetSourceDataService datasetSourceDataService;


    // @PostMapping("/page")
    // @Operation(summary = "获得数据集源数据存储分页")
    // public CommonResult<PageResult<ListDatasetSourceDataRespVO>> getDatasetStoragePage(@RequestBody DatasetSourceDataPageReqVO pageVO) {
    //     PageResult<DatasetSourceDataDO> pageResult = datasetSourceDataService.getDatasetSourceDataPage(pageVO);
    //     return success(DatasetSourceDataConvert.INSTANCE.convertPage(pageResult));
    // }

    @PostMapping("/details/split")
    @Operation(summary = "获得源数据分块详情")
    public CommonResult<PageResult<DatasetSourceDataSplitPageRespVO>> getSourceDataDetailsInfo(@RequestBody DatasetSourceDataSplitPageReqVO reqVO) {
        return success(datasetSourceDataService.getSplitDetails(reqVO));
    }

    @GetMapping("/details/{uid}")
    @Operation(summary = "获得源数据详情内容")
    public CommonResult<DatasetSourceDataDetailRespVO> getSourceDataDetailsInfo(@PathVariable("uid") String uid) {
        return success(datasetSourceDataService.getSourceDataDetailByUID(uid, false));
    }

    @GetMapping("/list/document/{appId}")
    @Operation(summary = "获取源数据列表List-类型为文档型")
    public CommonResult<List<DatasetSourceDataDetailRespVO>> getDatasetSourceDataByDocumentList(@PathVariable("appId") String appId) {
        return success(datasetSourceDataService.getApplicationSourceDataList(appId, DataSourceDataModelEnum.DOCUMENT.getStatus(), false));
    }

    @GetMapping("/list/qa/{appId}")
    @Operation(summary = "获得数据集源数据列表-类型为QA型")
    public CommonResult<List<DatasetSourceDataDetailRespVO>> getDatasetSourceDataByQaList(@PathVariable("appId") String appId) {
        return success(datasetSourceDataService.getApplicationSourceDataList(appId, DataSourceDataModelEnum.QUESTION_AND_ANSWERS.getStatus(), false));
    }

    @PostMapping("/uploadFiles")
    @Operation(summary = "上传文件")
    @OperateLog(enable = false)
    public CommonResult<SourceDataUploadDTO> uploadFiles(UploadFileReqVO reqVO) {
        reqVO.setCleanSync(false);
        reqVO.setSplitSync(false);
        reqVO.setIndexSync(false);
        reqVO.setDataModel(DataSourceDataModelEnum.DOCUMENT.getStatus());
        reqVO.setDataType(DataSourceDataTypeEnum.DOCUMENT.name());
        SourceDataUploadDTO sourceDataUrlUploadDTO = datasetSourceDataService.uploadFilesSourceData(reqVO,null);
        return success(sourceDataUrlUploadDTO);
    }

    @PostMapping("/uploadUrls")
    @Operation(summary = "批量上传HTML")
    @OperateLog(enable = false)
    public CommonResult<List<SourceDataUploadDTO>> uploadUrls(@Validated @RequestBody UploadUrlReqVO reqVO) {

        reqVO.setCleanSync(false);
        reqVO.setSplitSync(false);
        reqVO.setIndexSync(false);
        reqVO.setDataModel(DataSourceDataModelEnum.DOCUMENT.getStatus());
        reqVO.setDataType(DataSourceDataTypeEnum.HTML.name());
        return success(datasetSourceDataService.uploadUrlsSourceData(reqVO,null));
    }


    @PostMapping("/uploadCharacters")
    @Operation(summary = "上传字符串文本")
    @OperateLog(enable = false)
    public CommonResult<List<SourceDataUploadDTO>> uploadCharacter(@Validated @RequestBody UploadCharacterReqVO reqVO) {
        reqVO.setCleanSync(false);
        reqVO.setSplitSync(false);
        reqVO.setIndexSync(false);

        reqVO.setDataModel(DataSourceDataModelEnum.DOCUMENT.getStatus());
        reqVO.setDataType(DataSourceDataTypeEnum.CHARACTERS.name());
        return success(datasetSourceDataService.uploadCharactersSourceData(reqVO,null));
    }

    @PutMapping("/update")
    @Operation(summary = "更新数据集源数据")
    public CommonResult<Boolean> updateDatasetSourceData(@Valid @RequestBody DatasetSourceDataUpdateReqVO updateReqVO) {
        datasetSourceDataService.updateDatasetSourceData(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除数据集源数据")
    @Parameter(name = "id", description = "编号", required = true)
    // @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:delete')")
    public CommonResult<Boolean> deleteDatasetSourceData(@RequestParam("id") String uid) {
        datasetSourceDataService.deleteDatasetSourceData(uid);
        return success(true);
    }

    @PostMapping("/enable")
    @Operation(summary = "启用源数据")
    @Parameter(name = "uid", description = "编号", required = true)
    public CommonResult<Boolean> enable(@RequestParam("uid") String uid) {
        datasetSourceDataService.enable(uid);
        return success(true);
    }

    @PostMapping("/disable")
    @Operation(summary = "禁用源数据")
    @Parameter(name = "uid", description = "编号", required = true)
    public CommonResult<Boolean> disable(@RequestParam("uid") String uid) {
        datasetSourceDataService.disable(uid);
        return success(true);
    }


}