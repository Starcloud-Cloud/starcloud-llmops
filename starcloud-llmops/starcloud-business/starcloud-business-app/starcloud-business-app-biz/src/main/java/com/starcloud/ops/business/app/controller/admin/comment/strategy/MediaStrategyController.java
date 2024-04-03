package com.starcloud.ops.business.app.controller.admin.comment.strategy;

import com.starcloud.ops.business.app.controller.admin.comment.strategy.vo.MediaStrategyPageReqVO;
import com.starcloud.ops.business.app.controller.admin.comment.strategy.vo.MediaStrategyRespVO;
import com.starcloud.ops.business.app.controller.admin.comment.strategy.vo.MediaStrategySaveReqVO;
import com.starcloud.ops.business.app.controller.admin.comment.strategy.vo.MediaStrategyUpdateStatusReqVO;
import com.starcloud.ops.business.app.dal.databoject.comment.MediaStrategyDO;
import com.starcloud.ops.business.app.service.comment.MediaStrategyService;
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

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;

import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;


@Tag(name = "管理后台 - 媒体回复策略")
@RestController
@RequestMapping("/marketing/media-strategy")
@Validated
public class MediaStrategyController {

    @Resource
    private MediaStrategyService mediaStrategyService;

    @PostMapping("/create")
    @Operation(summary = "创建媒体回复策略")
    public CommonResult<Long> createMediaStrategy(@Valid @RequestBody MediaStrategySaveReqVO createReqVO) {
        return success(mediaStrategyService.createMediaStrategy(getLoginUserId(), createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新媒体回复策略")
    public CommonResult<Boolean> updateMediaStrategy(@Valid @RequestBody MediaStrategySaveReqVO updateReqVO) {
        mediaStrategyService.updateMediaStrategy(getLoginUserId(), updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除媒体回复策略")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteMediaStrategy(@RequestParam("id") Long id) {
        mediaStrategyService.deleteMediaStrategy(getLoginUserId(), id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得媒体回复策略")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<MediaStrategyRespVO> getMediaStrategy(@RequestParam("id") Long id) {
        MediaStrategyDO mediaStrategy = mediaStrategyService.getMediaStrategy(getLoginUserId(), id);
        return success(BeanUtils.toBean(mediaStrategy, MediaStrategyRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得媒体回复策略分页")
    public CommonResult<PageResult<MediaStrategyRespVO>> getMediaStrategyPage(@Valid MediaStrategyPageReqVO pageReqVO) {
        PageResult<MediaStrategyDO> pageResult = mediaStrategyService.getMediaStrategyPage(getLoginUserId(), pageReqVO);
        return success(BeanUtils.toBean(pageResult, MediaStrategyRespVO.class));
    }


    @PostMapping("/update-status")
    @Operation(summary = "更改策略状态")
    public CommonResult<Boolean> updateMediaStrategyStatus(@Valid @RequestBody MediaStrategyUpdateStatusReqVO updateStatusReqVO) {
        mediaStrategyService.updateMediaStrategyStatus(getLoginUserId(), updateStatusReqVO.getId(), updateStatusReqVO.getStatus());
        return success(true);
    }

}