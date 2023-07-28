package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataUpdateReqVO;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.service.dto.SourceDataUploadRespDTO;
import com.starcloud.ops.business.dataset.service.dto.SourceDataUrlUploadDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 数据集源数据")
@RestController
@RequestMapping("/llm/dataset-source-data")
@Validated
public class DatasetSourceDataController {

    @Resource
    private DatasetSourceDataService datasetSourceDataService;

    @PostMapping("/uploadFiles/{datasetId}")
    @Operation(summary = "上传文件-支持批量上传")
    // @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:create')")
    public CommonResult<SourceDataUrlUploadDTO> uploadFiles(@PathVariable("datasetId")  String datasetId,
                                                            @RequestParam(value = "files") MultipartFile[] files) {
        SplitRule splitRule = new SplitRule();
        splitRule.setAutomatic(false);
        splitRule.setRemoveExtraSpaces(true);
        splitRule.setRemoveExtraSpaces(true);
        splitRule.setChunkSize(500);
        splitRule.setPattern(null);
        SourceDataUrlUploadDTO sourceDataUrlUploadDTO = datasetSourceDataService.uploadFilesSourceData(files, splitRule, datasetId);
        return success(sourceDataUrlUploadDTO);
    }

    @PostMapping("/uploadUrls/{datasetId}")
    @Operation(summary = "上传URL-支持批量上传")
    // @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:create')")
    public CommonResult<SourceDataUrlUploadDTO> uploadUrls(@PathVariable("datasetId") String datasetId,
                                                           @RequestParam(value = "urls") List<String> urls) {
        SplitRule splitRule = new SplitRule();
        splitRule.setAutomatic(false);
        splitRule.setRemoveExtraSpaces(true);
        splitRule.setRemoveExtraSpaces(true);
        splitRule.setChunkSize(500);
        splitRule.setPattern(null);
        SourceDataUrlUploadDTO sourceDataUrlUploadDTO = datasetSourceDataService.uploadUrlsSourceData(urls, splitRule, datasetId);
        return success(sourceDataUrlUploadDTO);
    }


    @PostMapping("/uploadCharacters/{datasetId}")
    @Operation(summary = "上传字符-支持批量上传")
    // @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:create')")
    public CommonResult<SourceDataUrlUploadDTO> uploadCharacters(@PathVariable("datasetId") String datasetId,
                                                                 @RequestParam(value = "characters") List<String> characters) {
        SplitRule splitRule = new SplitRule();
        splitRule.setAutomatic(false);
        splitRule.setRemoveExtraSpaces(true);
        splitRule.setRemoveExtraSpaces(true);
        splitRule.setChunkSize(500);
        splitRule.setPattern(null);
        SourceDataUrlUploadDTO sourceDataUrlUploadDTO = datasetSourceDataService.uploadCharactersSourceData(characters, splitRule, datasetId);
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
    @PreAuthorize("@ss.hasPermission('llm:dataset-source-data:delete')")
    public CommonResult<Boolean> deleteDatasetSourceData(@RequestParam("id") String uid) {
        datasetSourceDataService.deleteDatasetSourceData(uid);
        return success(true);
    }


}