package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.*;
import com.starcloud.ops.business.dataset.convert.datasetsourcedata.DatasetSourceDataConvert;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.enums.DataSourceDataModelEnum;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import com.starcloud.ops.business.dataset.service.datasets.DatasetsService;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.dto.SourceDataUploadDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Slf4j
@Tag(name = "管理后台 - 数据集源数据")
@RestController
@RequestMapping("/llm/dataset-source-data")
@Validated
public class DatasetSourceDataController {

    @Resource
    private DatasetsService datasetsService;

    @Resource
    private DatasetSourceDataService datasetSourceDataService;


    @GetMapping("/page/{datasetId}")
    @Operation(summary = "获得数据集源数据存储分页")
    // @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:create')")
    public CommonResult<PageResult<DatasetSourceDataRespVO>> getDatasetStoragePage(@PathVariable("datasetId") String datasetId,
                                                                                   @Validated DatasetSourceDataPageReqVO pageVO) {

        pageVO.setDatasetId(datasetId);
        PageResult<DatasetSourceDataDO> pageResult = datasetSourceDataService.getDatasetSourceDataPage(pageVO);
        return success(DatasetSourceDataConvert.INSTANCE.convertPage(pageResult));
    }


    @GetMapping("/list/document/{datasetId}")
    @Operation(summary = "获得数据集源数据列表-类型为文档型")
    // @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:query')")
    public CommonResult<List<DatasetSourceDataRespVO>> getDatasetSourceDataByDocumentList(@PathVariable("datasetId") String datasetId) {

        // 判断数据集是否存在，不存在则创建数据集
        try {
            datasetsService.validateDatasetsExists(datasetId);
        } catch (Exception e) {
            log.info("应用{}不存在数据集，开始创建数据集，数据集 UID 为应用 ID", datasetId);
            String datasetName = String.format("应用%s的数据集", "datasetId");
            datasetsService.createDatasetsByApplication(datasetId, datasetName);
        }

        List<DatasetSourceDataDO> list = datasetSourceDataService.getDatasetSourceDataList(datasetId, DataSourceDataModelEnum.DOCUMENT.getStatus());
        return success(DatasetSourceDataConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/list/qa/{datasetId}")
    @Operation(summary = "获得数据集源数据列表-类型为QA型")
    // @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:query')")
    public CommonResult<List<DatasetSourceDataRespVO>> getDatasetSourceDataByQaList(@PathVariable("datasetId") String datasetId) {

        // 判断数据集是否存在，不存在则创建数据集
        try {
            datasetsService.validateDatasetsExists(datasetId);
        } catch (Exception e) {
            log.info("应用{}不存在数据集，开始创建数据集，数据集 UID 为应用 ID", datasetId);
            String datasetName = String.format("应用%s的数据集", "datasetId");
            datasetsService.createDatasetsByApplication(datasetId, datasetName);
        }

        List<DatasetSourceDataDO> list = datasetSourceDataService.getDatasetSourceDataList(datasetId,DataSourceDataModelEnum.QUESTION_AND_ANSWERS.getStatus());
        return success(DatasetSourceDataConvert.INSTANCE.convertList(list));
    }

    @PostMapping("/uploadFile/{datasetId}/{batch}")
    @Operation(summary = "上传文件-支持批量上传")
    // @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:create')")
    public CommonResult<SourceDataUploadDTO> uploadFiles(@PathVariable("datasetId") String datasetId,
                                                         @PathVariable("batch") String batch,
                                                         @RequestParam(value = "files") MultipartFile files) {
        SplitRule splitRule = new SplitRule();
        splitRule.setAutomatic(false);
        splitRule.setRemoveExtraSpaces(true);
        splitRule.setRemoveExtraSpaces(true);
        splitRule.setChunkSize(500);
        splitRule.setPattern(null);
        SourceDataUploadDTO sourceDataUrlUploadDTO = datasetSourceDataService.uploadFilesSourceData(files, batch, splitRule, datasetId);
        return success(sourceDataUrlUploadDTO);
    }

    @PostMapping("/uploadUrls/{datasetId}/{batch}")
    @Operation(summary = "上传URL-支持批量上传")
    // @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:create')")
    public CommonResult<SourceDataUploadDTO> uploadUrls(@PathVariable("datasetId") String datasetId,
                                                        @PathVariable("batch") String batch,
                                                        @Validated @RequestBody List<UploadUrlReqVO> reqVO) {
        SplitRule splitRule = new SplitRule();
        splitRule.setAutomatic(false);
        splitRule.setRemoveExtraSpaces(true);
        splitRule.setRemoveExtraSpaces(true);
        splitRule.setChunkSize(500);
        splitRule.setPattern(null);
        SourceDataUploadDTO sourceDataUrlUploadDTO = datasetSourceDataService.uploadUrlsSourceData(reqVO, batch,  splitRule, datasetId);
        return success(sourceDataUrlUploadDTO);
    }


    @PostMapping("/uploadCharacters/{datasetId}/{batch}")
    @Operation(summary = "上传字符-支持批量上传")
    // @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:create')")
    public CommonResult<SourceDataUploadDTO> uploadCharacter(@PathVariable("datasetId") String datasetId,
                                                             @PathVariable("batch") String batch,
                                                             @Validated @RequestBody List<UploadCharacterReqVO> reqVO) {
        SplitRule splitRule = new SplitRule();
        splitRule.setAutomatic(false);
        splitRule.setRemoveExtraSpaces(true);
        splitRule.setRemoveExtraSpaces(true);
        splitRule.setChunkSize(500);
        splitRule.setPattern(null);
        SourceDataUploadDTO sourceDataUrlUploadDTO = datasetSourceDataService.uploadCharactersSourceData(reqVO, batch,  splitRule, datasetId);
        return success(sourceDataUrlUploadDTO);
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
    // @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:delete')")
    public CommonResult<Boolean> deleteDatasetSourceData(@RequestParam("id") String uid) {
        datasetSourceDataService.deleteDatasetSourceData(uid);
        return success(true);
    }


}