package com.starcloud.ops.business.user.controller.admin.signin;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;

import com.starcloud.ops.business.user.controller.admin.signin.vo.config.AdminUserSignInConfigCreateReqVO;
import com.starcloud.ops.business.user.controller.admin.signin.vo.config.AdminUserSignInConfigRespVO;
import com.starcloud.ops.business.user.controller.admin.signin.vo.config.AdminUserSignInConfigUpdateReqVO;
import com.starcloud.ops.business.user.controller.admin.signin.vo.config.AppAdminUserSignInConfigRespVO;
import com.starcloud.ops.business.user.convert.signin.AdminUserSignInConfigConvert;
import com.starcloud.ops.business.user.dal.dataobject.signin.AdminUserSignInConfigDO;
import com.starcloud.ops.business.user.service.signin.AdminUserSignInConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

// TODO 芋艿：url
@Tag(name = "管理后台 - 系统会员签到规则")
@RestController
@RequestMapping("/admin/sign-in/config")
@Validated
public class AdminUserSignInConfigController {

    @Resource
    private AdminUserSignInConfigService signInConfigService;

    @PostMapping("/create")
    @Operation(summary = "创建签到规则")
    @PreAuthorize("@ss.hasPermission('point:sign-in-config:create')")
    public CommonResult<Long> createSignInConfig(@Valid @RequestBody AdminUserSignInConfigCreateReqVO createReqVO) {
        return success(signInConfigService.createSignInConfig(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新签到规则")
    @PreAuthorize("@ss.hasPermission('point:sign-in-config:update')")
    public CommonResult<Boolean> updateSignInConfig(@Valid @RequestBody AdminUserSignInConfigUpdateReqVO updateReqVO) {
        signInConfigService.updateSignInConfig(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除签到规则")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('point:sign-in-config:delete')")
    public CommonResult<Boolean> deleteSignInConfig(@RequestParam("id") Long id) {
        signInConfigService.deleteSignInConfig(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得签到规则")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('point:sign-in-config:query')")
    public CommonResult<AdminUserSignInConfigRespVO> getSignInConfig(@RequestParam("id") Long id) {
        AdminUserSignInConfigDO signInConfig = signInConfigService.getSignInConfig(id);
        return success(AdminUserSignInConfigConvert.INSTANCE.convert(signInConfig));
    }

    @GetMapping("/list")
    @Operation(summary = "获得签到规则列表")
    @PreAuthorize("@ss.hasPermission('point:sign-in-config:query')")
    public CommonResult<List<AdminUserSignInConfigRespVO>> getSignInConfigList() {
        List<AdminUserSignInConfigDO> list = signInConfigService.getSignInConfigList();
        return success(AdminUserSignInConfigConvert.INSTANCE.convertList(list));
    }


    @GetMapping("/u/list")
    @Operation(summary = "获得签到规则列表")
    public CommonResult<List<AppAdminUserSignInConfigRespVO>> getSignInConfigListByUser() {
        List<AdminUserSignInConfigDO> pageResult = signInConfigService.getSignInConfigList(CommonStatusEnum.ENABLE.getStatus());
        return success(AdminUserSignInConfigConvert.INSTANCE.convertList02(pageResult));
    }

}
