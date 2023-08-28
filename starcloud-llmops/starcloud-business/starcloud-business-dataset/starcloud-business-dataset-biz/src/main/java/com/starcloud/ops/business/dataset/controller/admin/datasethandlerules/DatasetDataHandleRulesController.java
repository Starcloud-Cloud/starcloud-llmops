package com.starcloud.ops.business.dataset.controller.admin.datasethandlerules;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo.*;
import com.starcloud.ops.business.dataset.enums.HandleRuleFromSceneEnum;
import com.starcloud.ops.business.dataset.service.datasethandlerules.DatasetDataHandleRulesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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
    private DatasetDataHandleRulesService datasetDataHandleRulesService;


    @PostMapping("/page")
    @Operation(summary = "获取分页数据")
    public CommonResult<PageResult<DatasetHandleRulesRespVO>> createRule(@Validated @RequestBody DatasetHandleRulesPageReqVO pageReqVO) {
        return success(datasetDataHandleRulesService.getRulePage(pageReqVO));
    }

    @PostMapping("/createRule")
    @Operation(summary = "创建数据集规则")
    public CommonResult<Boolean> createRule(@Validated @RequestBody DatasetHandleRulesCreateReqVO createReqVO) {
        createReqVO.setFromScene(HandleRuleFromSceneEnum.USER.name());
        return success(datasetDataHandleRulesService.createDefaultRules(createReqVO));
    }

    @PostMapping("/update")
    @Operation(summary = "更新规则")
    public CommonResult<Boolean> updateDatasets(@Validated @RequestBody DatasetHandleRulesUpdateReqVO updateReqVO) {
        updateReqVO.setFromScene(HandleRuleFromSceneEnum.USER.name());
        datasetDataHandleRulesService.updateRules(updateReqVO);
        return success(true);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除规则")
    public CommonResult<Boolean> delete(@RequestParam Long ruleId) {
        return success(datasetDataHandleRulesService.deleteRule(ruleId));
    }

    @PostMapping("/debugRule")
    @Operation(summary = "规则调试")
    public CommonResult<DatasetHandleRulesDebugRespVO> debugRule(@Validated @RequestBody
                                          DatasetHandleRulesDebugReqVO debugReqVO) {
        return success(datasetDataHandleRulesService.debugRule(debugReqVO));
    }

    @PostMapping("/ruleType")
    @Operation(summary = "规则类型")
    public CommonResult<List<HandleRuleTypeRespVO>> getRuleType() {
        return success(datasetDataHandleRulesService.getRuleType());
    }

    @PostMapping("/formatType")
    @Operation(summary = "转换格式")
    public CommonResult<List<HandleRuleTypeRespVO>> getFormatType() {
        return success(datasetDataHandleRulesService.getFormatType());
    }

}