package com.starcloud.ops.business.limits.controller.admin.userbenefitsusagelog;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsusagelog.vo.UserBenefitsUsageLogCreateReqVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsusagelog.vo.UserBenefitsUsageLogPageReqVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsusagelog.vo.UserBenefitsUsageLogRespVO;
import com.starcloud.ops.business.limits.convert.userbenefitsusagelog.UserBenefitsUsageLogConvert;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefitsusagelog.UserBenefitsUsageLogDO;
import com.starcloud.ops.business.limits.service.userbenefitsusagelog.UserBenefitsUsageLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;


@Tag(name = "星河云海 - 用户权益使用日志")
@RestController
@RequestMapping("/llm/benefits-usage-log")
@Validated
public class UserBenefitsUsageLogController {

    @Resource
    private UserBenefitsUsageLogService userBenefitsUsageLogService;

    @PostMapping("/create")
    @Operation(summary = "创建用户权益使用日志")
    @PreAuthorize("@ss.hasPermission('llm:user-benefits-usage-log:create')")
    public CommonResult<Long> createUserBenefitsUsageLog(@Valid @RequestBody UserBenefitsUsageLogCreateReqVO createReqVO) {
        return success(userBenefitsUsageLogService.createUserBenefitsUsageLog(createReqVO));
    }



    @GetMapping("/page")
    @Operation(summary = "获得用户权益使用日志分页")
    @PreAuthorize("@ss.hasPermission('llm:user-benefits-usage-log:query')")
    public CommonResult<PageResult<UserBenefitsUsageLogRespVO>> getUserBenefitsUsageLogPage(@Valid UserBenefitsUsageLogPageReqVO pageVO) {
        PageResult<UserBenefitsUsageLogDO> pageResult = userBenefitsUsageLogService.getUserBenefitsUsageLogPage(pageVO);
        return success(UserBenefitsUsageLogConvert.INSTANCE.convertPage(pageResult));
    }
}
