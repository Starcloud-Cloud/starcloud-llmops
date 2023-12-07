package com.starcloud.ops.business.mission.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionDetailVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.ClaimReqVO;
import com.starcloud.ops.business.mission.service.CustomerClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;


@RestController
@RequestMapping("/llm/xhs/customer")
@Tag(name = "星河云海-小红书认领", description = "小红书认领")
public class CustomerClaimlController {

    @Resource
    private CustomerClaimService claimService;


    @GetMapping("/detail/{uid}")
    @Operation(summary = "任务详情")
    @PermitAll
    public CommonResult<SingleMissionDetailVO> detailById(@PathVariable("uid") String uid) {
        return CommonResult.success(claimService.missionDetail(uid));
    }


    @PutMapping("/claim")
    @Operation(summary = "认领任务")
    @PermitAll
    public CommonResult<Boolean> claim(@Valid @RequestBody ClaimReqVO reqVO) {
        claimService.claim(reqVO);
        return CommonResult.success(true);
    }

}
