package com.starcloud.ops.business.mission.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionModifyReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SinglePageQueryReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionRespVO;
import com.starcloud.ops.business.mission.service.SingleMissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/llm/single")
@Tag(name = "星河云海 - 任务中心 单条任务", description = "任务中心 单条任务")
public class SingleMissionController {

    @Resource
    private SingleMissionService singleMissionService;

    @PutMapping("/add/{notificationUid}")
    @Operation(summary = "增加单条任务", description = "增加单条任务")
    public CommonResult<Boolean> addSingleMission(
            @PathVariable("notificationUid") String notificationUid,
            @RequestBody List<String> creativeUids) {
        singleMissionService.addSingleMission(notificationUid, creativeUids);
        return CommonResult.success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询任务", description = "分页查询任务")
    public CommonResult<PageResult<SingleMissionRespVO>> page(@Valid SinglePageQueryReqVO reqVO) {
        PageResult<SingleMissionRespVO> pageResult = singleMissionService.page(reqVO);
        return CommonResult.success(pageResult);
    }

    @PutMapping("/modify")
    @Operation(summary = "修改单条任务", description = "修改单条任务")
    public CommonResult<SingleMissionRespVO> modify(@Valid @RequestBody SingleMissionModifyReqVO reqVO) {
        return CommonResult.success(singleMissionService.modifySelective(reqVO));
    }

    @DeleteMapping("/delete/{uid}")
    @Operation(summary = "删除单条任务", description = "删除单条任务")
    public CommonResult<Boolean> delete(@PathVariable("uid") String uid) {
        singleMissionService.delete(uid);
        return CommonResult.success(true);
    }

    @PutMapping("/pick/{uid}")
    @Operation(summary = "领取任务", description = "领取任务")
    public CommonResult<Boolean> pick(@PathVariable("uid") String uid) {
        singleMissionService.pick(uid);
        return CommonResult.success(true);
    }
}