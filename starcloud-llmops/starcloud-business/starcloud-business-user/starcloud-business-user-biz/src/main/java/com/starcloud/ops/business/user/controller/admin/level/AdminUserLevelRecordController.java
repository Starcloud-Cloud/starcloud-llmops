package com.starcloud.ops.business.user.controller.admin.level;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.user.controller.admin.level.vo.record.AdminUserLevelRecordPageReqVO;
import com.starcloud.ops.business.user.controller.admin.level.vo.record.AdminUserLevelRecordRespVO;
import com.starcloud.ops.business.user.convert.level.AdminUserLevelRecordConvert;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelRecordDO;
import com.starcloud.ops.business.user.service.level.AdminUserLevelRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 会员等级记录")
@RestController
@RequestMapping("/admin/level-record")
@Validated
public class AdminUserLevelRecordController {

    @Resource
    private AdminUserLevelRecordService adminUserLevelRecordService;

    @GetMapping("/get")
    @Operation(summary = "获得会员等级记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<AdminUserLevelRecordRespVO> getLevelRecord(@RequestParam("id") Long id) {
        AdminUserLevelRecordDO levelLog = adminUserLevelRecordService.getLevelRecord(id);
        return success(AdminUserLevelRecordConvert.INSTANCE.convert(levelLog));
    }

    @GetMapping("/page")
    @Operation(summary = "获得会员等级记录分页")
    public CommonResult<PageResult<AdminUserLevelRecordRespVO>> getLevelRecordPage(
            @Valid AdminUserLevelRecordPageReqVO pageVO) {
        PageResult<AdminUserLevelRecordDO> pageResult = adminUserLevelRecordService.getLevelRecordPage(pageVO);
        return success(AdminUserLevelRecordConvert.INSTANCE.convertPage(pageResult));
    }

}
