package com.starcloud.ops.business.mission.controller.admin;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.mission.controller.admin.vo.request.NotificationCreateReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.NotificationModifyReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.NotificationPageQueryReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.NotificationRespVO;
import com.starcloud.ops.business.mission.service.NotificationCenterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/llm/notification")
@Tag(name = "星河云海 - 任务中心", description = "任务中心")
public class NotificationCenterController {

    @Resource
    private NotificationCenterService centerService;

    @PostMapping("/create")
    @Operation(summary = "创建任务", description = "创建任务")
    public CommonResult<NotificationRespVO> create(@Valid @RequestBody NotificationCreateReqVO reqVO) {
        return CommonResult.success(centerService.create(reqVO));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询任务列表", description = "分页查询任务列表")
    public CommonResult<PageResult<NotificationRespVO>> page(@Valid NotificationPageQueryReqVO reqVO) {
        PageResult<NotificationRespVO> pageResult = centerService.page(reqVO);
        return CommonResult.success(pageResult);
    }

    @PutMapping("/publish/{uid}")
    @Operation(summary = "发布/取消任务", description = "发布/取消任务")
    public CommonResult<Boolean> publish(@PathVariable("uid") String uid,
                                         @RequestParam("publish") Boolean publish) {
        centerService.publish(uid, publish);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete/{uid}")
    @Operation(summary = "删除任务", description = "删除任务")
    public CommonResult<Boolean> delete(@PathVariable("uid") String uid) {
        centerService.delete(uid);
        return CommonResult.success(true);
    }

    @DeleteMapping("/modify")
    @Operation(summary = "编辑", description = "编辑")
    public CommonResult<NotificationRespVO> modify(@Valid @RequestBody NotificationModifyReqVO reqVO) {
        NotificationRespVO respVO = centerService.modifySelective(reqVO);
        return CommonResult.success(respVO);
    }

}
