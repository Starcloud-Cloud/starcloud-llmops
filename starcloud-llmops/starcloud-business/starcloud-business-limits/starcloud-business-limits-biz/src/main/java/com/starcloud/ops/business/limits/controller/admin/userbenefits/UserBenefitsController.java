package com.starcloud.ops.business.limits.controller.admin.userbenefits;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.*;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefitsstrategy.UserBenefitsStrategyDO;
import com.starcloud.ops.business.limits.enums.BenefitsStrategyTypeEnums;
import com.starcloud.ops.business.limits.enums.BenefitsTypesEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.limits.service.userbenefitsstrategy.UserBenefitsStrategyService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static com.starcloud.ops.business.limits.enums.ErrorCodeConstants.USER_BENEFITS_OPERATION_FAIL_CANNOT_USE;
import static com.starcloud.ops.business.limits.enums.ErrorCodeConstants.USER_BENEFITS_USAGE_USER_ATTENDANCE_FAIL;

@Tag(name = "星河云海 - 用户权益")
@RestController
@RequestMapping("/llm/user-benefits")
@Validated
public class UserBenefitsController {

    @Resource
    private UserBenefitsService userBenefitsService;
    @Resource
    private UserBenefitsStrategyService userBenefitsStrategyService;

    @PostMapping("/code")
    @RateLimiter(name="backendA")
    @Operation(summary = "根据 Code 激活使用权益")
    public CommonResult<Boolean> createUserBenefits(@Validated @RequestParam("code") String code) {
        UserBenefitsStrategyDO userBenefitsStrategy = userBenefitsStrategyService.getUserBenefitsStrategy(code);
        if (BenefitsTypesEnums.PAY_BENEFITS.name().equals(userBenefitsStrategy.getTypes())){
            throw exception(USER_BENEFITS_OPERATION_FAIL_CANNOT_USE);
        }
        return success(userBenefitsService.addUserBenefitsByCode(code, getLoginUserId()));
    }

    @PostMapping("/info")
    @Operation(summary = "获取权益信息")
    public CommonResult<UserBenefitsInfoResultVO> updateUserBenefits() {
        return success(userBenefitsService.getUserBenefits(getLoginUserId()));
    }


    @GetMapping("/page")
    @Operation(summary = "获得用户权益 - 分页")
    public CommonResult<PageResult<UserBenefitsPagInfoResultVO>> getUserBenefitsPage(@Validated UserBenefitsPageReqVO pageVO) {
        PageResult<UserBenefitsPagInfoResultVO> pageResult = userBenefitsService.getUserBenefitsPage(pageVO);
        return success(pageResult);
    }


    @PostMapping("/checkSignIn")
    @Operation(summary = "判断用户是否签到")
    public CommonResult<Boolean> checkSignIn() {
        return success(userBenefitsService.hasSignInBenefitToday(getLoginUserId()));
    }

    @PostMapping("/signIn")
    @Operation(summary = "用户签到")
    public CommonResult<Boolean> signIn() {
        Boolean result = userBenefitsService.addUserBenefitsByStrategyType(BenefitsStrategyTypeEnums.USER_ATTENDANCE.getName(), getLoginUserId());
        if (!result){
            throw exception(USER_BENEFITS_USAGE_USER_ATTENDANCE_FAIL);
        }
        return success(true);
    }

    @PostMapping("/expendBenefits")
    @Operation(summary = "权益扣减测试")
    @Deprecated
    public CommonResult<Boolean> expendBenefits(String benefitsType, Long amount, Long userId, String outId) {
        userBenefitsService.expendBenefits(benefitsType, amount, userId, outId);
        return success(true);
    }

    @PostMapping("/expiredReminder")
    @Operation(summary = "权益过期提醒")
    public CommonResult<ExpiredReminderVO> expiredReminder() {
        return success(userBenefitsService.getBenefitsExpired());
    }


}
