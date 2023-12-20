package com.starcloud.ops.business.user.controller.admin.signin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.annotations.PreAuthenticated;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.starcloud.ops.business.user.controller.admin.signin.vo.record.AdminUserSignInRecordPageReqVO;
import com.starcloud.ops.business.user.controller.admin.signin.vo.record.AdminUserSignInRecordRespVO;
import com.starcloud.ops.business.user.controller.admin.signin.vo.record.AppAdminUserSignInRecordRespVO;
import com.starcloud.ops.business.user.controller.admin.signin.vo.record.AppAdminUserSignInRecordSummaryRespVO;
import com.starcloud.ops.business.user.convert.signin.AdminUserSignInRecordConvert;
import com.starcloud.ops.business.user.dal.dataobject.signin.AdminUserSignInRecordDO;
import com.starcloud.ops.business.user.service.signin.AdminUserSignInRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 系统会员签到记录")
@RestController
@RequestMapping("/admin/sign-in/record")
@Validated
public class AdminUserSignInRecordController {

    @Resource
    private AdminUserSignInRecordService signInRecordService;

    @Resource
    private AdminUserService adminUserService;

    @GetMapping("/page")
    @Operation(summary = "获得签到记录分页")
    @PreAuthorize("@ss.hasPermission('point:sign-in-record:query')")
    public CommonResult<PageResult<AdminUserSignInRecordRespVO>> getSignInRecordPage(@Valid AdminUserSignInRecordPageReqVO pageVO) {
        // 执行分页查询
        PageResult<AdminUserSignInRecordDO> pageResult = signInRecordService.getSignInRecordPage(pageVO);
        if (CollectionUtils.isEmpty(pageResult.getList())) {
            return success(PageResult.empty(pageResult.getTotal()));
        }

        // 拼接结果返回
        List<AdminUserDO> users = adminUserService.getUserList(
                convertSet(pageResult.getList(), AdminUserSignInRecordDO::getUserId));
        return success(AdminUserSignInRecordConvert.INSTANCE.convertPage(pageResult, users));
    }


    @GetMapping("/u/get-summary")
    @Operation(summary = "系统会员-获得个人签到统计")
    @PreAuthenticated
    public CommonResult<AppAdminUserSignInRecordSummaryRespVO> getSignInRecordSummary() {
        return success(signInRecordService.getSignInRecordSummary(getLoginUserId()));
    }

    @PostMapping("/u/create")
    @Operation(summary = "系统会员-签到")
    @PreAuthenticated
    public CommonResult<AppAdminUserSignInRecordRespVO> createSignInRecord() {
        AdminUserSignInRecordDO recordDO = signInRecordService.createSignRecord(getLoginUserId());
        return success(AdminUserSignInRecordConvert.INSTANCE.coverRecordToAppRecordVo(recordDO));
    }

    @GetMapping("/u/page")
    @Operation(summary = "系统会员-获得签到记录分页")
    @PreAuthenticated
    public CommonResult<PageResult<AppAdminUserSignInRecordRespVO>> getSignRecordPage(PageParam pageParam) {
        PageResult<AdminUserSignInRecordDO> pageResult = signInRecordService.getSignRecordPage(getLoginUserId(), pageParam);
        return success(AdminUserSignInRecordConvert.INSTANCE.convertPage02(pageResult));
    }
}
