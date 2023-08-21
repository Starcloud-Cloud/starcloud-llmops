package com.starcloud.ops.business.dataset.controller.admin.datasetstorage;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@Tag(name = "星河云海 - 数据集 - 源数据存储", description = "星河云海数据集管理")
@RestController
@RequestMapping("/llm/dataset-storage")
@Validated
public class DatasetStorageController {


}