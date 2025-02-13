package com.starcloud.ops.business.user.controller.admin.rights;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils;
import cn.iocoder.yudao.framework.security.core.annotations.PreAuthenticated;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.starcloud.ops.business.user.controller.admin.rights.vo.rights.*;
import com.starcloud.ops.business.user.convert.rights.AdminUserRightsConvert;
import com.starcloud.ops.business.user.dal.dataobject.rights.AdminUserRightsDO;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsStatusEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.business.user.service.level.AdminUserLevelConfigService;
import com.starcloud.ops.business.user.service.rights.AdminUserRightsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 系统会员权益")
@RestController
@RequestMapping("/llm/rights")
@Validated
public class AdminUserRightsController {

    @Resource
    private AdminUserRightsService adminUserRightsService;

    @Resource
    private AdminUserLevelConfigService adminUserLevelConfigService;

    @Resource
    private AdminUserService adminUserService;

    @GetMapping("/page")
    @Operation(summary = "管理员获取用户权益记录分页")
    public CommonResult<PageResult<AdminUserRightsRespVO>> getPointRecordPage(@Valid AdminUserRightsPageReqVO pageVO) {
        // 执行分页查询
        PageResult<AdminUserRightsDO> pageResult = adminUserRightsService.getRightsPage(pageVO);
        if (CollectionUtils.isEmpty(pageResult.getList())) {
            return success(PageResult.empty(pageResult.getTotal()));
        }

        // 拼接结果返回
        List<AdminUserDO> users = adminUserService.getUserList(
                convertSet(pageResult.getList(), AdminUserRightsDO::getUserId));
        return success(AdminUserRightsConvert.INSTANCE.convertPage(pageResult, users));
    }

    //===========================APP====ADMIN===================================================================
    @GetMapping("/u/page")
    @Operation(summary = "系统会员-获得用户权益记录分页")
    @PreAuthenticated
    public CommonResult<PageResult<AppAdminUserRightsRespVO>> getPointRecordPage(@Valid AppAdminUserRightsPageReqVO pageVO) {
        // PageResult<AdminUserRightsDO> pageResult = adminUserRightsService.getRightsPage(getLoginUserId(), pageVO);
        // List<AppAdminUserRightsRespVO> appAdminUserRightsRespVOS = AdminUserRightsConvert.INSTANCE.convertPage(pageResult.getList());
        //
        // PageResult<AppAdminUserRightsRespVO> result = AdminUserRightsConvert.INSTANCE.convertPage02(pageResult);
        //
        // result.getList().forEach(data -> {
        //     if (Objects.nonNull(data.getUserLevelId())) {
        //         data.setLevelName(adminUserLevelConfigService.getLevelConfig(data.getUserLevelId()).getName());
        //     }
        // });

        return success(adminUserRightsService.getRightsPage2(getLoginUserId(), pageVO));
    }

    @GetMapping("/u/reduceRights")
    @Operation(summary = "系统会员-权益扣减测试")
    @PreAuthenticated
    public CommonResult<Boolean> reduceRights() {
        // adminUserRightsService.reduceRights(getLoginUserId(), AdminUserRightsTypeEnum.MAGIC_BEAN, 1, AdminUserRightsBizTypeEnum.ADMIN_MINUS, "-1");
        return success(Boolean.TRUE);
    }


    @GetMapping("/u/getOriginalFixedRightsSums")
    @Operation(summary = "系统会员-权益模板数据测试")
    @PreAuthenticated
    public CommonResult<Integer> getOriginalFixedRightsSums() {
        return success(adminUserRightsService.getEffectiveNumsByType(getLoginUserId(), AdminUserRightsTypeEnum.TEMPLATE));
    }


    @GetMapping("/u/getRightsCollect")
    @Operation(summary = "系统会员-获取权益数据汇总")
    @PreAuthenticated
    public CommonResult<List<AdminUserRightsCollectRespVO>> getRightsCollect() {
        return success(adminUserRightsService.getRightsCollect(getLoginUserId()));
    }


}
