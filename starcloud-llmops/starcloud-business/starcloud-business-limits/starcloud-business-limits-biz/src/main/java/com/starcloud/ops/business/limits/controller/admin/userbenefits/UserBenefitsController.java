package com.starcloud.ops.business.limits.controller.admin.userbenefits;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.UserBenefitsInfoResultVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.UserBenefitsPageReqVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.UserBenefitsRespVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.UserBenefitsUpdateReqVO;
import com.starcloud.ops.business.limits.convert.userbenefits.UserBenefitsConvert;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefits.UserBenefitsDO;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "星河云海 - 用户权益")
@RestController
@RequestMapping("/llm/user-benefits")
@Validated
public class UserBenefitsController {

    @Resource
    private UserBenefitsService userBenefitsService;

    @PostMapping("/code")
    @Operation(summary = "根据 Code 激活使用权益")
    @PreAuthorize("@ss.hasPermission('llm:user-benefits:create')")
    public CommonResult<Boolean> createUserBenefits(@Validated @RequestParam("code") String code) {
        return success(userBenefitsService.addUserBenefitsByCode(code,getLoginUserId()));
    }

    @PutMapping("/info")
    @Operation(summary = "获取权益信息")
    @PreAuthorize("@ss.hasPermission('llm:user-benefits:info')")
    public CommonResult<UserBenefitsInfoResultVO> updateUserBenefits(@Validated @RequestBody UserBenefitsUpdateReqVO updateReqVO) {
        return success(userBenefitsService.getUserBenefits(getLoginUserId()));
    }


    @GetMapping("/page")
    @Operation(summary = "获得用户权益分页")
    @PreAuthorize("@ss.hasPermission('llm:user-benefits:query')")
    public CommonResult<PageResult<UserBenefitsRespVO>> getUserBenefitsPage(@Validated UserBenefitsPageReqVO pageVO) {
        PageResult<UserBenefitsDO> pageResult = userBenefitsService.getUserBenefitsPage(pageVO);
        return success(UserBenefitsConvert.INSTANCE.convertPage(pageResult));
    }


}
