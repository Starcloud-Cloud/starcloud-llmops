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
@Tag(name = "管理后台 - 数据集（知识库）源数据")
@RestController
@RequestMapping("/llm/dataset-source-data")
@Validated
public class DatasetSourceDataController {

    @Resource
    private DatasetsService datasetsService;

    @Resource
    private DatasetSourceDataService datasetSourceDataService;

    @PostMapping("/page")
    @Operation(summary = "获得数据集源数据存储分页")
    // @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:create')")
    public CommonResult<PageResult<DatasetSourceDataRespVO>> getDatasetStoragePage(@RequestBody DatasetSourceDataPageReqVO pageVO) {
        PageResult<DatasetSourceDataDO> pageResult = datasetSourceDataService.getDatasetSourceDataPage(pageVO);
        return success(DatasetSourceDataConvert.INSTANCE.convertPage(pageResult));
    }

    @PostMapping("/details/split")
    @Operation(summary = "获得源数据分块详情")
    // @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:create')")
    public CommonResult<PageResult<DatasetSourceDataSplitPageRespVO>> getSourceDataDetailsInfo(@RequestBody DatasetSourceDataSplitPageReqVO reqVO) {

        return success(datasetSourceDataService.getSplitDetails(reqVO));
    }


    @GetMapping("/details/{uid}")
    @Operation(summary = "获得源数据详情")
    // @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:create')")
    public CommonResult<DatasetSourceDataDetailsInfoVO> getSourceDataDetailsInfo(@PathVariable("uid") String uid) {
        return success(datasetSourceDataService.getSourceDataDetailsInfo(uid,false));
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
        return success( datasetSourceDataService.getDatasetSourceDataList(datasetId, DataSourceDataModelEnum.DOCUMENT.getStatus()));
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
            String datasetName = String.format("应用%s的数据集", datasetId);
            datasetsService.createDatasetsByApplication(datasetId, datasetName);
        }
        return success(datasetSourceDataService.getDatasetSourceDataList(datasetId, DataSourceDataModelEnum.QUESTION_AND_ANSWERS.getStatus()));
    }

    @PostMapping("/uploadFiles")
    // @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:create')")
    public CommonResult<SourceDataUploadDTO> uploadFiles(@RequestParam(value = "file") MultipartFile file,
                                                         UploadFileReqVO reqVO) {
        SplitRule splitRule = new SplitRule();
        splitRule.setAutomatic(false);
        splitRule.setRemoveExtraSpaces(true);
        splitRule.setRemoveExtraSpaces(true);
        splitRule.setChunkSize(500);
        splitRule.setPattern(null);
        reqVO.setSplitRule(splitRule);
        reqVO.setSync(false);
        SourceDataUploadDTO sourceDataUrlUploadDTO = datasetSourceDataService.uploadFilesSourceData(file, reqVO);
        return success(sourceDataUrlUploadDTO);
    }

    @PostMapping("/uploadUrls")
    // @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:create')")
    public CommonResult<List<SourceDataUploadDTO> > uploadUrls(@Validated @RequestBody UploadUrlReqVO reqVO) {

        SplitRule splitRule = new SplitRule();
        splitRule.setAutomatic(false);
        splitRule.setRemoveExtraSpaces(true);
        splitRule.setRemoveExtraSpaces(true);
        splitRule.setChunkSize(500);
        splitRule.setPattern(null);
        reqVO.setSplitRule(splitRule);
        reqVO.setSync(false);
        return success(datasetSourceDataService.uploadUrlsSourceData(reqVO));
    }


    @PostMapping("/uploadCharacters")
    // @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:create')")
    public CommonResult<        List<SourceDataUploadDTO> > uploadCharacter(@Validated @RequestBody List<UploadCharacterReqVO> reqVO) {
        SplitRule splitRule = new SplitRule();
        splitRule.setAutomatic(false);
        splitRule.setRemoveExtraSpaces(true);
        splitRule.setRemoveExtraSpaces(true);
        splitRule.setChunkSize(500);
        splitRule.setPattern(null);
        reqVO.forEach(data -> data.setSplitRule(splitRule));
        reqVO.forEach(data -> data.setSync(false));
        return success(datasetSourceDataService.uploadCharactersSourceData(reqVO));
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