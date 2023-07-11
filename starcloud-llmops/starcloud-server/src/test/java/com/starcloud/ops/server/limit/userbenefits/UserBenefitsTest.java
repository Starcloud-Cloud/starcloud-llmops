package com.starcloud.ops.server.limit.userbenefits;


import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo.UserBenefitsStrategyCreateReqVO;
import com.starcloud.ops.business.limits.controller.admin.userbenefitsstrategy.vo.UserBenefitsStrategyUpdateReqVO;
import com.starcloud.ops.business.limits.enums.BenefitsStrategyEffectiveUnitEnums;
import com.starcloud.ops.business.limits.enums.BenefitsStrategyLimitIntervalEnums;
import com.starcloud.ops.business.limits.enums.BenefitsStrategyTypeEnums;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.limits.service.userbenefitsstrategy.UserBenefitsStrategyService;
import com.starcloud.ops.business.limits.service.util.BenefitsOperationService;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;


@Slf4j
@Import({StarcloudServerConfiguration.class, AdapterRuoyiProConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class UserBenefitsTest extends BaseDbUnitTest {

    @Resource
    private UserBenefitsStrategyService userBenefitsStrategyService;

    @Resource
    private UserBenefitsService userBenefitsService;

    @Resource
    private BenefitsOperationService benefitsOperationService;

    /**
     * 管理员创建权益策略
     */
    @Test
    public void SysCreateBenefitsStrategy() {

        UserBenefitsStrategyCreateReqVO createReqVO =new UserBenefitsStrategyCreateReqVO();

        // 创建签到权益 权益限制为一天签到一次，不做兑换次数限制 ，有效期为 1 年
        String code = userBenefitsStrategyService.generateUniqueCode(BenefitsStrategyTypeEnums.PAY_PLUS_YEAR.getName());
        createReqVO.setCode(code);
        createReqVO.setStrategyName("PLUS权益");
        createReqVO.setStrategyDesc("这是一个PLUS权益");
        createReqVO.setStrategyType(BenefitsStrategyTypeEnums.PAY_PLUS_YEAR.getName());
        createReqVO.setAppCount(1L);
        createReqVO.setDatasetCount(1L);
        createReqVO.setImageCount(2L);
        createReqVO.setTokenCount(2000L);
        createReqVO.setEffectiveNum(1L);
        createReqVO.setEffectiveUnit(BenefitsStrategyEffectiveUnitEnums.MONTH.getName());
        createReqVO.setLimitNum(10L);
        createReqVO.setLimitIntervalNum(1L);
        createReqVO.setLimitIntervalUnit(BenefitsStrategyLimitIntervalEnums.MONTH.getName());
        createReqVO.setEnabled(true);

        // 新增策略
        userBenefitsStrategyService.createUserBenefitsStrategy(createReqVO);
    }

    /**
     * 管理员创建权益策略
     */
    @Test
    public void SysUpdateBenefitsStrategy() {
        UserBenefitsStrategyUpdateReqVO userBenefitsStrategyUpdateReqVO = new UserBenefitsStrategyUpdateReqVO();
        // userBenefitsStrategyUpdateReqVO
        // // 删除策略
        // benefitsOperationService.updateUserBenefitsStrategy(1L);
    }
    /**
     * 管理员创建权益策略
     */
    @Test
    public void SysDeleteBenefitsStrategy() {
        // 删除策略
        benefitsOperationService.deleteUserBenefitsStrategy(1L);
    }

    /**
     * 根据类型使用系统权益
     */
    @Test
    public void addUserBenefitsByStrategyType() {
        // 使用系统权益
        userBenefitsService.addUserBenefitsByStrategyType(BenefitsStrategyTypeEnums.USER_ATTENDANCE.getName(),1L);
    }

    /**
     * 根据类型使用系统权益
     */
    @Test
    public void expendBenefits() {
        // 使用系统权益
        userBenefitsService.expendBenefits(BenefitsTypeEnums.TOKEN.getCode(), 1L,1L,"");
    }



}
