package com.starcloud.ops.business.app.service.app;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.verification.Verification;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppModifyExtendReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.CreativePlanModifyReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.convert.xhs.plan.CreativePlanConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanDO;
import com.starcloud.ops.business.app.dal.mysql.xhs.plan.CreativePlanMapper;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanSourceEnum;
import com.starcloud.ops.business.app.model.plan.CreativePlanConfigurationDTO;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;

/**
 * 应用管理服务 <br>
 * <p>
 * 对应用和其扩展功能进行服务编排
 * </p>
 *
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class AppManager {

    /**
     * 应用服务
     */
    private final AppService appService;

    /**
     * 创作计划服务
     */
    private final CreativePlanService creativePlanService;

    /**
     * 创作计划 Mapper
     */
    private final CreativePlanMapper creativePlanMapper;

    /**
     * 事务模板
     */
    private final TransactionTemplate transactionTemplate;

    /**
     * 修改应用信息，并且修改扩展功能相关的信息
     *
     * @param request 请求参数
     */
    public AppRespVO modify(AppModifyExtendReqVO request) {

        return transactionTemplate.execute(status -> {

            // 更新应用
            AppRespVO appResponse = appService.modify(request);
            if (CollectionUtils.isNotEmpty(appResponse.getVerificationList())) {
                // 回滚事务
                status.setRollbackOnly();
                return appResponse;
            }

            // 如果是媒体矩阵应用，需要更新创作计划
            if (AppTypeEnum.MEDIA_MATRIX.name().equalsIgnoreCase(request.getType())) {
                /*
                 * 1. 如果请求不为空，则更新创作计划
                 * 2. 如果请求为空，则查询计划信息。
                 *    - 如果查询到，则进行更新计划配置的应用信息。
                 *    - 如果没有查询到，说明当前应用，还没有执行计划，则不进行处理。后续执行计划时候再进行操作。
                 */
                if (Objects.isNull(request.getPlanRequest())) {

                    CreativePlanDO planDO = creativePlanMapper.getByAppUid(request.getUid(), CreativePlanSourceEnum.APP.name());
                    // 如果没有查询到，则不进行处理，直接返回
                    if (Objects.isNull(planDO)) {
                        return appResponse;
                    }

                    // 只更新创作计划中的应用信息
                    CreativePlanRespVO plan = CreativePlanConvert.INSTANCE.convertResponse(planDO);
                    CreativePlanConfigurationDTO configuration = plan.getConfiguration();
                    configuration.setAppInformation(AppMarketConvert.INSTANCE.convert(appResponse));
                    creativePlanMapper.update(Wrappers.lambdaUpdate(CreativePlanDO.class)
                            .set(CreativePlanDO::getConfiguration, JsonUtils.toJsonString(configuration))
                            .eq(CreativePlanDO::getId, planDO.getId()));
                    return appResponse;
                }

                // 更新创作计划
                CreativePlanModifyReqVO planRequest = request.getPlanRequest();
                // 不校验应用，因为应用更新时候已经进行过校验
                planRequest.setValidateApp(false);
                planRequest.setSource(CreativePlanSourceEnum.APP.name());
                planRequest.setValidateType(ValidateTypeEnum.UPDATE.name());
                planRequest.setE(request.getE());

                // 将计划配置信息中的应用信息替换为最新的应用信息
                CreativePlanConfigurationDTO configuration = planRequest.getConfiguration();
                AppMarketRespVO appInformation = AppMarketConvert.INSTANCE.convert(appResponse);
                configuration.setAppInformation(appInformation);
                planRequest.setConfiguration(configuration);

                // 更新创作计划
                CreativePlanRespVO planResponse = creativePlanService.modify(planRequest);
                // 将创作计划的校验信息添加到应用的校验信息中
                List<Verification> verificationList = planResponse.getVerificationList();
                if (CollectionUtils.isNotEmpty(verificationList)) {
                    // 如果错误信息不为空，则进行回滚操作。
                    status.setRollbackOnly();

                    List<Verification> appVerificationList = ListUtils.emptyIfNull(appResponse.getVerificationList());
                    appVerificationList.addAll(verificationList);
                    appResponse.setVerificationList(appVerificationList);
                }
            }

            return appResponse;
        });
    }
}
