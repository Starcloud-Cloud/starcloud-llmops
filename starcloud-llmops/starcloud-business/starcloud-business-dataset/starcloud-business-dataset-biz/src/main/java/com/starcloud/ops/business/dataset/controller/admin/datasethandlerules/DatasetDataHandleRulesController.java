package com.starcloud.ops.business.dataset.controller.admin.datasethandlerules;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo.DatasetHandleRulesDebugReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo.DatasetHandleRulesUpdateReqVO;
import com.starcloud.ops.business.dataset.service.datasethandlerules.DatasetDataHandleRulesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * @author : [wuruiqiang]
 * @version : [v1.0]
 * @className : DatasetsController
 * @description : [数据集]
 * @createTime : [2023/5/31 16:00]
 * @updateUser : [wuruiqiang]
 * @updateTime : [2023/5/31 16:00]
 * @updateRemark : [暂无修改]
 */
@RestController
@RequestMapping("/llm/dataset/rule")
@Tag(name = "星河云海 - 数据集 - 预处理规则", description = "星河云海数据集管理")
@Validated
public class DatasetDataHandleRulesController {

    @Resource
    private DatasetDataHandleRulesService datasetDataHandleRules;

    @PostMapping("/init/{datasetId}")
    @Operation(summary = "创建数据集规则")
    public CommonResult<Boolean> initRule(@PathVariable("datasetId") Long datasetId) {
        return success(datasetDataHandleRules.createDefaultRules(datasetId));
    }

    @PutMapping("/update")
    @Operation(summary = "更新规则")
    public CommonResult<Boolean> updateDatasets(@Validated @RequestBody DatasetHandleRulesUpdateReqVO updateReqVO) {
        datasetDataHandleRules.updateRules(updateReqVO);
        return success(true);
    }

    @PutMapping("/debugRule")
    @Operation(summary = "规则调试")
    public CommonResult<String> debugRule(@Validated @RequestBody
                                          DatasetHandleRulesDebugReqVO debugReqVO) {
        return success(datasetDataHandleRules.debugRule(debugReqVO));
    }

}