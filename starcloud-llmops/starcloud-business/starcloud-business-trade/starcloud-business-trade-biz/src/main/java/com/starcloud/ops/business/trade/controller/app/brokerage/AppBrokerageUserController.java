package com.starcloud.ops.business.trade.controller.app.brokerage;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.annotations.PreAuthenticated;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.starcloud.ops.business.trade.controller.app.brokerage.vo.user.*;
import com.starcloud.ops.business.trade.convert.brokerage.BrokerageRecordConvert;
import com.starcloud.ops.business.trade.convert.brokerage.BrokerageUserConvert;
import com.starcloud.ops.business.trade.dal.dataobject.brokerage.BrokerageUserDO;
import com.starcloud.ops.business.trade.enums.brokerage.BrokerageRecordBizTypeEnum;
import com.starcloud.ops.business.trade.enums.brokerage.BrokerageRecordStatusEnum;
import com.starcloud.ops.business.trade.enums.brokerage.BrokerageWithdrawStatusEnum;
import com.starcloud.ops.business.trade.service.brokerage.BrokerageRecordService;
import com.starcloud.ops.business.trade.service.brokerage.BrokerageUserService;
import com.starcloud.ops.business.trade.service.brokerage.BrokerageWithdrawService;
import com.starcloud.ops.business.trade.service.brokerage.bo.BrokerageWithdrawSummaryRespBO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 APP - 分销用户")
@RestController
@RequestMapping("/llm/trade/brokerage-user")
@Validated
@Slf4j
public class AppBrokerageUserController {

    @Resource
    private BrokerageUserService brokerageUserService;
    @Resource
    private BrokerageRecordService brokerageRecordService;
    @Resource
    private BrokerageWithdrawService brokerageWithdrawService;

    @Resource
    private AdminUserService adminUserService;

    @GetMapping("/get")
    @Operation(summary = "获得个人分销信息")
    @PreAuthenticated
    public CommonResult<AppBrokerageUserRespVO> getBrokerageUser() {
        Optional<BrokerageUserDO> user = Optional.ofNullable(brokerageUserService.getBrokerageUser(getLoginUserId()));
        // 返回数据
        AppBrokerageUserRespVO respVO = new AppBrokerageUserRespVO()
                .setBrokerageEnabled(user.map(BrokerageUserDO::getBrokerageEnabled).orElse(false))
                .setBrokeragePrice(user.map(BrokerageUserDO::getBrokeragePrice).orElse(0))
                .setFrozenPrice(user.map(BrokerageUserDO::getFrozenPrice).orElse(0));
        return success(respVO);
    }

    @PutMapping("/bind")
    @Operation(summary = "绑定推广员")
    @PreAuthenticated
    public CommonResult<Boolean> bindBrokerageUser(@Valid @RequestBody AppBrokerageUserBindReqVO reqVO) {
        return success(brokerageUserService.bindBrokerageUser(getLoginUserId(), reqVO.getBindUserId()));
    }

    @GetMapping("/get-summary")
    @Operation(summary = "获得个人分销统计")
    @PreAuthenticated
    public CommonResult<AppBrokerageUserMySummaryRespVO> getBrokerageUserSummary() {
        // 查询当前登录用户信息
        BrokerageUserDO brokerageUser = brokerageUserService.getBrokerageUser(getLoginUserId());
        // 统计用户昨日的佣金
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime beginTime = LocalDateTimeUtil.beginOfDay(yesterday);
        LocalDateTime endTime = LocalDateTimeUtil.endOfDay(yesterday);
        Integer yesterdayPrice = brokerageRecordService.getSummaryPriceByUserId(brokerageUser.getId(),
                BrokerageRecordBizTypeEnum.ORDER, BrokerageRecordStatusEnum.SETTLEMENT, beginTime, endTime);
        // 统计用户提现的佣金
        Integer withdrawPrice = brokerageWithdrawService.getWithdrawSummaryListByUserId(Collections.singleton(brokerageUser.getId()),
                        BrokerageWithdrawStatusEnum.AUDIT_SUCCESS).stream()
                .findFirst().map(BrokerageWithdrawSummaryRespBO::getPrice).orElse(0);
        // 统计分销用户数量（一级）
        Long firstBrokerageUserCount = brokerageUserService.getBrokerageUserCountByBindUserId(brokerageUser.getId(), 1);
        // 统计分销用户数量（二级）
        Long secondBrokerageUserCount = brokerageUserService.getBrokerageUserCountByBindUserId(brokerageUser.getId(), 2);

        // 拼接返回
        return success(BrokerageUserConvert.INSTANCE.convert(yesterdayPrice, withdrawPrice, firstBrokerageUserCount, secondBrokerageUserCount, brokerageUser));
    }

    @GetMapping("/rank-page-by-user-count")
    @Operation(summary = "获得分销用户排行分页（基于用户量）")
    @PreAuthenticated
    public CommonResult<PageResult<AppBrokerageUserRankByUserCountRespVO>> getBrokerageUserRankPageByUserCount(AppBrokerageUserRankPageReqVO pageReqVO) {
        // 分页查询
        PageResult<AppBrokerageUserRankByUserCountRespVO> pageResult = brokerageUserService.getBrokerageUserRankPageByUserCount(pageReqVO);
        // 拼接数据
        Map<Long, AdminUserDO> userMap = adminUserService.getUserMap(convertSet(pageResult.getList(), AppBrokerageUserRankByUserCountRespVO::getId));
        return success(BrokerageUserConvert.INSTANCE.convertPage03(pageResult, userMap));
    }

    @GetMapping("/rank-page-by-price")
    @Operation(summary = "获得分销用户排行分页（基于佣金）")
    @PreAuthenticated
    public CommonResult<PageResult<AppBrokerageUserRankByPriceRespVO>> getBrokerageUserChildSummaryPageByPrice(AppBrokerageUserRankPageReqVO pageReqVO) {
        // 分页查询
        PageResult<AppBrokerageUserRankByPriceRespVO> pageResult = brokerageRecordService.getBrokerageUserChildSummaryPageByPrice(pageReqVO);
        // 拼接数据
        Map<Long, AdminUserDO> userMap = adminUserService.getUserMap(convertSet(pageResult.getList(), AppBrokerageUserRankByPriceRespVO::getId));
        return success(BrokerageRecordConvert.INSTANCE.convertPage03(pageResult, userMap));
    }

    @GetMapping("/child-summary-page")
    @Operation(summary = "获得下级分销统计分页")
    @PreAuthenticated
    public CommonResult<PageResult<AppBrokerageUserChildSummaryRespVO>> getBrokerageUserChildSummaryPage(
            AppBrokerageUserChildSummaryPageReqVO pageReqVO) {
        PageResult<AppBrokerageUserChildSummaryRespVO> pageResult = brokerageUserService.getBrokerageUserChildSummaryPage(pageReqVO, getLoginUserId());
        return success(pageResult);
    }

    @GetMapping("/get-rank-by-price")
    @Operation(summary = "获得分销用户排行（基于佣金）")
    @Parameter(name = "times", description = "时间段", required = true)
    public CommonResult<Integer> getRankByPrice(
            @RequestParam("times") @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND) LocalDateTime[] times) {
        return success(brokerageRecordService.getUserRankByPrice(getLoginUserId(), times));
    }

}
