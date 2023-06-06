package com.starcloud.ops.business.dataset.controller.admin.datasetstorage;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageRespVO;
import com.starcloud.ops.business.dataset.service.datasetstorage.DatasetStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;



@Tag(name = "星河云海-数据集源数据存储")
@RestController
@RequestMapping("/llm/dataset-storage")
@Validated
public class DatasetStorageController {

    @Resource
    private DatasetStorageService datasetStorageService;

    @PostMapping(value = "/upload")
    @Operation(summary = "创建数据集源数据存储")
    public CommonResult<String> createDatasetStorage(MultipartFile file, String path) throws IOException {
        DatasetStorageRespVO datasetStorageRespVO = new DatasetStorageRespVO();
        datasetStorageRespVO.setFile(file);
        datasetStorageRespVO.setPath(path);
        return success(datasetStorageService.uploadSourceData(datasetStorageRespVO));
    }
}