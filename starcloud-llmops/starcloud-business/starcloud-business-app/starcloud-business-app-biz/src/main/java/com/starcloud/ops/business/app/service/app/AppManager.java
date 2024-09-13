package com.starcloud.ops.business.app.service.app;

import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.verification.Verification;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppModifyExtendReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.CreativePlanModifyReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.model.plan.CreativePlanConfigurationDTO;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * 修改应用信息，并且修改扩展功能相关的信息
     *
     * @param request 请求参数
     */
    public AppRespVO modify(AppModifyExtendReqVO request) {
        // 更新应用
        AppRespVO appResponse = appService.modify(request);
        if (CollectionUtils.isNotEmpty(appResponse.getVerificationList())) {
            return appResponse;
        }

        // 如果是媒体矩阵应用
        if (AppTypeEnum.MEDIA_MATRIX.name().equalsIgnoreCase(request.getType())) {
            if (request.getPlanRequest() == null) {
                return appResponse;
            }
            // 更新创作计划
            CreativePlanModifyReqVO planRequest = request.getPlanRequest();
            // 不校验应用，因为应用更新时候已经进行过校验
            planRequest.setValidateApp(false);

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
                List<Verification> appVerificationList = ListUtils.emptyIfNull(appResponse.getVerificationList());
                appVerificationList.addAll(verificationList);
                appResponse.setVerificationList(appVerificationList);
            }
        }
        return appResponse;
    }
}
