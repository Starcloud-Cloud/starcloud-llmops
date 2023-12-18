package com.starcloud.ops.business.user.controller.admin.level;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.*;
import com.starcloud.ops.business.user.convert.level.AdminUserLevelConvert;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelConfigDO;
import com.starcloud.ops.business.user.service.level.AdminUserLevelConfigService;
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

@Tag(name = "管理后台 - 会员等级")
@RestController
@RequestMapping("/admin/level")
@Validated
public class AdminUserLevelConfigController {

    @Resource
    private AdminUserLevelConfigService adminUserLevelConfigService;

    @PostMapping("/create")
    @Operation(summary = "创建会员等级")
    @PreAuthorize("@ss.hasPermission('member:level:create')")
    public CommonResult<Long> createLevel(@Valid @RequestBody AdminUserLevelConfigCreateReqVO createReqVO) {
        return success(adminUserLevelConfigService.createLevel(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新会员等级")
    @PreAuthorize("@ss.hasPermission('member:level:update')")
    public CommonResult<Boolean> updateLevel(@Valid @RequestBody AdminUserLevelConfigUpdateReqVO updateReqVO) {
        adminUserLevelConfigService.updateLevel(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除会员等级")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('member:level:delete')")
    public CommonResult<Boolean> deleteLevel(@RequestParam("id") Long id) {
        adminUserLevelConfigService.deleteLevel(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得会员等级")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('member:level:query')")
    public CommonResult<AdminUserLevelConfigRespVO> getLevel(@RequestParam("id") Long id) {
        AdminUserLevelConfigDO level = adminUserLevelConfigService.getLevelConfig(id);
        return success(AdminUserLevelConvert.INSTANCE.convert(level));
    }

    @GetMapping("/list-all-simple")
    @Operation(summary = "获取会员等级精简信息列表", description = "只包含被开启的会员等级，主要用于前端的下拉选项")
    public CommonResult<List<AdminUserLevelConfigSimpleRespVO>> getSimpleLevelList() {
        // 获用户列表，只要开启状态的
        List<AdminUserLevelConfigDO> list = adminUserLevelConfigService.getEnableLevelList();
        // 排序后，返回给前端
        return success(AdminUserLevelConvert.INSTANCE.convertSimpleList(list));
    }

    @GetMapping("/list")
    @Operation(summary = "获得会员等级列表")
    @PreAuthorize("@ss.hasPermission('member:level:query')")
    public CommonResult<List<AdminUserLevelConfigRespVO>> getLevelList(@Valid AdminUserLevelConfigListReqVO listReqVO) {
        List<AdminUserLevelConfigDO> result = adminUserLevelConfigService.getLevelList(listReqVO);
        return success(AdminUserLevelConvert.INSTANCE.convertList(result));
    }

}
