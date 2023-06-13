package com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo.UserBenefitsStrategyCreateReqVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo.UserBenefitsStrategyPageReqVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo.UserBenefitsStrategyRespVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo.UserBenefitsStrategyUpdateReqVO;
import com.starcloud.ops.business.limits.convert.userbenefitsstrategy.UserBenefitsStrategyConvert;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefitsstrategy.UserBenefitsStrategyDO;
import com.starcloud.ops.business.limits.service.userbenefitsstrategy.UserBenefitsStrategyService;
import com.starcloud.ops.business.limits.service.util.BenefitsOperationService;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;


@Tag(name = "星河云海 - 权益策略 ")
@RestController
@RequestMapping("/llm/benefits-strategy")
@Validated
public class UserBenefitsStrategyController {

    @Resource
    private UserBenefitsStrategyService userBenefitsStrategyService;

    @Resource
    private BenefitsOperationService benefitsOperationService;


    @PostMapping("/generate-code")
    @Operation(summary = "创建权益策略code ")
    @PreAuthorize("@ss.hasPermission('starcloud-business-limits:user-benefits-strategy:create')")
    public CommonResult<String> generateCode() {
        return success(userBenefitsStrategyService.generateUniqueCode());
    }

    @PostMapping("/check-code/{code}")
    @Operation(summary = "验证策略 code 是否合法 ")
    @PreAuthorize("@ss.hasPermission('starcloud-business-limits:user-benefits-strategy:create')")
    public CommonResult<Boolean> checkCode(@ApiParam("主键") @PathVariable(value = "code") String code) {
        return success(userBenefitsStrategyService.checkCode(code));
    }


    @PostMapping("/create")
    @Operation(summary = "创建用户权益策略 ")
    @PreAuthorize("@ss.hasPermission('starcloud-business-limits:user-benefits-strategy:create')")
    public CommonResult<Long> createUserBenefitsStrategy(@Validated @RequestBody UserBenefitsStrategyCreateReqVO createReqVO) {
        return success(userBenefitsStrategyService.createUserBenefitsStrategy(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户权益策略 ")
    @PreAuthorize("@ss.hasPermission('starcloud-business-limits:user-benefits-strategy:update')")
    public CommonResult<Boolean> updateUserBenefitsStrategy(@Validated @RequestBody UserBenefitsStrategyUpdateReqVO updateReqVO) {
        benefitsOperationService.updateUserBenefitsStrategy(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户权益策略 ")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('starcloud-business-limits:user-benefits-strategy:delete')")
    public CommonResult<Boolean> deleteUserBenefitsStrategy(@RequestParam("id") Long id) {
        benefitsOperationService.deleteUserBenefitsStrategy(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户权益策略表 ")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('starcloud-business-limits:user-benefits-strategy:query')")
    public CommonResult<UserBenefitsStrategyRespVO> getUserBenefitsStrategy(@RequestParam("id") Long id) {
        UserBenefitsStrategyDO userBenefitsStrategy = userBenefitsStrategyService.getUserBenefitsStrategy(id);
        return success(UserBenefitsStrategyConvert.convert(userBenefitsStrategy));
    }

    @GetMapping("/list")
    @Operation(summary = "获得用户权益策略表 列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    @PreAuthorize("@ss.hasPermission('starcloud-business-limits:user-benefits-strategy:query')")
    public CommonResult<List<UserBenefitsStrategyRespVO>> getUserBenefitsStrategyList(@RequestParam("ids") Collection<Long> ids) {
        List<UserBenefitsStrategyDO> list = userBenefitsStrategyService.getUserBenefitsStrategyList(ids);
        return success(UserBenefitsStrategyConvert.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户权益策略表 分页")
    @PreAuthorize("@ss.hasPermission('starcloud-business-limits:user-benefits-strategy:query')")
    public CommonResult<PageResult<UserBenefitsStrategyRespVO>> getUserBenefitsStrategyPage(@Validated UserBenefitsStrategyPageReqVO pageVO) {
        PageResult<UserBenefitsStrategyDO> pageResult = userBenefitsStrategyService.getUserBenefitsStrategyPage(pageVO);
        return success(UserBenefitsStrategyConvert.convertPage(pageResult));
    }


}
