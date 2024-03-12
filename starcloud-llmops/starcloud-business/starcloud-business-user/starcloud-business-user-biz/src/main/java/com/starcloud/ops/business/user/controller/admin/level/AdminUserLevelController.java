package com.starcloud.ops.business.user.controller.admin.level;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelLimitRespVO;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelLimitUsedRespVO;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelPageReqVO;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelRespVO;
import com.starcloud.ops.business.user.convert.level.AdminUserLevelConvert;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelDO;
import com.starcloud.ops.business.user.service.level.AdminUserLevelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 系统会员等级记录")
@RestController
@RequestMapping("/admin/level-record")
@Validated
public class AdminUserLevelController {

    @Resource
    private AdminUserLevelService adminUserLevelService;

    @GetMapping("/get")
    @Operation(summary = "获得会员等级记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<AdminUserLevelRespVO> getLevelRecord(@RequestParam("id") Long id) {
        AdminUserLevelDO levelLog = adminUserLevelService.getLevel(id);
        return success(AdminUserLevelConvert.INSTANCE.convert(levelLog));
    }

    @GetMapping("/page")
    @Operation(summary = "获得会员等级记录分页")
    public CommonResult<PageResult<AdminUserLevelRespVO>> getLevelRecordPage(
            @Valid AdminUserLevelPageReqVO pageVO) {
        PageResult<AdminUserLevelDO> pageResult = adminUserLevelService.getLevelPage(pageVO);
        return success(AdminUserLevelConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/createInitLevelRecord")
    @Operation(summary = "获得会员等级记录分页")
    public CommonResult<Boolean> createInitLevelRecord() {
        adminUserLevelService.createInitLevelRecord(getLoginUserId());
        return success(Boolean.TRUE);
    }


    @GetMapping("/user_righs_limit_use")
    @Operation(summary = "用户权益是否超过使用限制")
    public CommonResult<AdminUserLevelLimitRespVO> validateLevelRightsLimit(@RequestParam("levelRightsCode") String levelRightsCode) {
        return success(adminUserLevelService.validateLevelRightsLimit(levelRightsCode, getLoginUserId()));
    }

    @GetMapping("/user_righs_limit_used_count")
    @Operation(summary = "用户权益使用限制查询")
    public CommonResult<AdminUserLevelLimitUsedRespVO> getLevelRightsLimitCount(@RequestParam("levelRightsCode") String levelRightsCode) {
        return success(adminUserLevelService.getLevelRightsLimitCount(levelRightsCode, getLoginUserId()));
    }


}
