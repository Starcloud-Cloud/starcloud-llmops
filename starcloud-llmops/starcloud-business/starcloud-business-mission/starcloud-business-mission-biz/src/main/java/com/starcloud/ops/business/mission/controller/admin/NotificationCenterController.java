package com.starcloud.ops.business.mission.controller.admin;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.mission.controller.admin.vo.request.NotificationCreateReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.NotificationModifyReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.NotificationPageQueryReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.NotificationRespVO;
import com.starcloud.ops.business.mission.service.NotificationCenterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/llm/notification")
@Tag(name = "星河云海 - 通告中心", description = "通告中心")
public class NotificationCenterController {

    @Resource
    private NotificationCenterService centerService;


    @GetMapping("/metadata")
    @Operation(summary = "获取通告中心元数据", description = "获取通告中心元数据")
    @PermitAll
    public CommonResult<Map<String, Object>> metadata() {
        return CommonResult.success(centerService.metadata());
    }

    @PostMapping("/create")
    @Operation(summary = "创建通告", description = "创建通告")
    public CommonResult<NotificationRespVO> create(@Valid @RequestBody NotificationCreateReqVO reqVO) {
        return CommonResult.success(centerService.create(reqVO));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询通告列表", description = "分页查询通告列表")
    public CommonResult<PageResult<NotificationRespVO>> page(@Valid NotificationPageQueryReqVO reqVO) {
        PageResult<NotificationRespVO> pageResult = centerService.page(reqVO);
        return CommonResult.success(pageResult);
    }

    @GetMapping("detail/{uid}")
    @Operation(summary = "通告明细", description = "通告明细明细")
    public CommonResult<NotificationRespVO> detail(@PathVariable("uid") String uid) {
        return CommonResult.success(centerService.selectByUid(uid));
    }

    @PutMapping("/publish/{uid}")
    @Operation(summary = "发布/取消通告", description = "发布/取消通告")
    public CommonResult<Boolean> publish(@PathVariable("uid") String uid,
                                         @RequestParam("publish") Boolean publish) {
        centerService.publish(uid, publish);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete/{uid}")
    @Operation(summary = "删除通告", description = "删除通告")
    public CommonResult<Boolean> delete(@PathVariable("uid") String uid) {
        centerService.delete(uid);
        return CommonResult.success(true);
    }

    @PutMapping("/modify")
    @Operation(summary = "编辑", description = "编辑")
    public CommonResult<NotificationRespVO> modify(@Valid @RequestBody NotificationModifyReqVO reqVO) {
        NotificationRespVO respVO = centerService.modifySelective(reqVO);
        return CommonResult.success(respVO);
    }

    @GetMapping("/code")
    @Operation(summary = "查询绑定邀请码", description = "查询绑定邀请码")
    public CommonResult<String> code() {
        String code = centerService.code();
        return CommonResult.success(code);
    }

}
