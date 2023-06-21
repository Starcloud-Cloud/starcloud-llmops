package com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo.*;
import com.starcloud.ops.business.limits.convert.userbenefitsstrategy.UserBenefitsStrategyConvert;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefitsstrategy.UserBenefitsStrategyDO;
import com.starcloud.ops.business.limits.enums.BenefitsStrategyEffectiveUnitEnums;
import com.starcloud.ops.business.limits.enums.BenefitsStrategyLimitIntervalEnums;
import com.starcloud.ops.business.limits.enums.BenefitsStrategyTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefitsstrategy.UserBenefitsStrategyService;
import com.starcloud.ops.business.limits.service.util.BenefitsOperationService;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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


    @PostMapping("/generate-code/{strategyType}")
    @Operation(summary = "创建权益策略code ")
    @PreAuthorize("@ss.hasPermission('starcloud-business-limits:user-benefits-strategy:create')")
    public CommonResult<String> generateCode(@ApiParam("策略类型") @PathVariable(value = "strategyType") String strategyType) {
        return success(userBenefitsStrategyService.generateUniqueCode(strategyType));
    }

    @PostMapping("/check-code/{code}/{strategyType}")
    @Operation(summary = "验证策略 code 是否合法 ")
    @PreAuthorize("@ss.hasPermission('starcloud-business-limits:user-benefits-strategy:create')")
    public CommonResult<Boolean> checkCode(@ApiParam("主键") @PathVariable(value = "code") String code,@ApiParam("策略类型") @PathVariable(value = "strategyType") String strategyType) {
        return success(userBenefitsStrategyService.checkCode(code,strategyType));
    }

    @PostMapping("/master-config/{strategyType}")
    @Operation(summary = "是否更新其他同系统类型配置 ")
    @PreAuthorize("@ss.hasPermission('starcloud-business-limits:user-benefits-strategy:create')")
    public CommonResult<Boolean> hasMasterConfigStrategy(@ApiParam("主键") @PathVariable(value = "strategyType") String strategyType) {
        return success(userBenefitsStrategyService.hasMasterConfigStrategy(strategyType));
    }


    @PostMapping("/create")
    @Operation(summary = "创建权益策略 ")
    @PreAuthorize("@ss.hasPermission('starcloud-business-limits:user-benefits-strategy:create')")
    public CommonResult<Long> createUserBenefitsStrategy(@Validated @RequestBody UserBenefitsStrategyCreateReqVO createReqVO) {
        return success(userBenefitsStrategyService.createUserBenefitsStrategy(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新权益策略 ")
    @PreAuthorize("@ss.hasPermission('starcloud-business-limits:user-benefits-strategy:update')")
    public CommonResult<Boolean> updateUserBenefitsStrategy(@Validated @RequestBody UserBenefitsStrategyUpdateReqVO updateReqVO) {
        userBenefitsStrategyService.updateStrategy(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除权益策略 ")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('starcloud-business-limits:user-benefits-strategy:delete')")
    public CommonResult<Boolean> deleteUserBenefitsStrategy(@RequestParam("id") Long id) {
        benefitsOperationService.deleteUserBenefitsStrategy(id);
        return success(true);
    }

    @DeleteMapping("/enabled")
    @Operation(summary = "启用权益策略 ")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('starcloud-business-limits:user-benefits-strategy:delete')")
    public CommonResult<Boolean> enabledBenefitsStrategy(@RequestParam("id") Long id) {
        userBenefitsStrategyService.enabledBenefitsStrategy(id);
        return success(true);
    }
    @DeleteMapping("/unenabled")
    @Operation(summary = "停用权益策略 ")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('starcloud-business-limits:user-benefits-strategy:delete')")
    public CommonResult<Boolean> unEnabledBenefitsStrategy(@RequestParam("id") Long id) {
        userBenefitsStrategyService.unEnabledBenefitsStrategy(id);
        return success(true);
    }
    @DeleteMapping("/archived")
    @Operation(summary = "归档权益策略 ")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('starcloud-business-limits:user-benefits-strategy:delete')")
    public CommonResult<Boolean> archivedBenefitsStrategy(@RequestParam("id") Long id) {
        userBenefitsStrategyService.archivedBenefitsStrategy(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户权益策略表 ")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('starcloud-business-limits:user-benefits-strategy:query')")
    public CommonResult<UserBenefitsStrategyRespVO> getUserBenefitsStrategy(@RequestParam("id") Long id) {
        UserBenefitsStrategyDO userBenefitsStrategy = userBenefitsStrategyService.getUserBenefitsStrategy(id);
        return success(UserBenefitsStrategyConvert.convert(userBenefitsStrategy));
    }


    @GetMapping("/page")
    @Operation(summary = "获得权益策略表 分页")
    @PreAuthorize("@ss.hasPermission('starcloud-business-limits:user-benefits-strategy:query')")
    public CommonResult<PageResult<UserBenefitsStrategyRespVO>> getUserBenefitsStrategyPage(@Validated UserBenefitsStrategyPageReqVO pageVO) {
        PageResult<UserBenefitsStrategyDO> pageResult = userBenefitsStrategyService.getUserBenefitsStrategyPage(pageVO);
        return success(UserBenefitsStrategyConvert.convertPage(pageResult));
    }

    @GetMapping("/base/strategyType")
    @Operation(summary = "获得权益策略类型")
    public ResponseEntity<List<BaseEnumsVO>> getBenefitsStrategy() {
        List<BaseEnumsVO> strategyList = Arrays.stream(BenefitsStrategyTypeEnums.values())
                .map(strategy -> new BaseEnumsVO(strategy.getName(), strategy.getChineseName()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(strategyList);
    }

    @GetMapping("/base/effectiveUnit")
    @Operation(summary = "获得权益策略有效时间单位")
    public ResponseEntity<List<BaseEnumsVO>> getBenefitsEffectiveUnit() {
        List<BaseEnumsVO> strategyList = Arrays.stream(BenefitsStrategyEffectiveUnitEnums.values())
                .map(strategy -> new BaseEnumsVO(strategy.getName(), strategy.getChineseName()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(strategyList);
    }

    @GetMapping("/base/limitInterval")
    @Operation(summary = "获得权益策略时间单位")
    public ResponseEntity<List<BaseEnumsVO>> getBenefitsLimitInterval() {
        List<BaseEnumsVO> strategyList = Arrays.stream(BenefitsStrategyLimitIntervalEnums.values())
                .map(strategy -> new BaseEnumsVO(strategy.getName(), strategy.getChineseName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(strategyList);
    }


}
