package com.starcloud.ops.business.mission.controller.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.annotations.PreAuthenticated;
import com.starcloud.ops.business.mission.api.WechatUserBindService;
import com.starcloud.ops.business.mission.api.vo.request.WechatUserBindReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/llm/wechat/user")
@Tag(name = "微信小程序用户分组绑定", description = "微信小程序用户分组绑定")
public class WechatUserBindController {

    @Resource
    private WechatUserBindService wechatUserBindService;

    @PostMapping("/bind")
    @Operation(summary = "绑定分组")
    @PreAuthenticated
    public CommonResult<Boolean> bind(@Valid @RequestBody WechatUserBindReqVO reqVO) {
        wechatUserBindService.bindGroup(reqVO);
        return CommonResult.success(false);
    }

    @GetMapping("/bindUser")
    @Operation(summary = "绑定分组")
    @PreAuthenticated
    public CommonResult<String> getBindUser() {
        return CommonResult.success(wechatUserBindService.getBindUser());
    }
}
