package com.starcloud.ops.business.app.service.xhs.impl;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.XhsAppResponse;
import com.starcloud.ops.business.app.api.xhs.XhsExecuteRequest;
import com.starcloud.ops.business.app.api.xhs.XhsExecuteResponse;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.xhs.XhsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-02
 */
@Slf4j
@Service
public class XhsServiceImpl implements XhsService {

    @Resource
    private AppService appService;

    @Resource
    private AppMarketService appMarketService;

    /**
     * 获取应用信息
     *
     * @param uid 应用UID
     * @return 应用信息
     */
    @Override
    public XhsAppResponse getApp(String uid) {
        AppMarketRespVO appMarket = appMarketService.get(uid);
        List<VariableItemRespVO> variableList = Optional.ofNullable(appMarket).map(AppMarketRespVO::getWorkflowConfig)
                .map(WorkflowConfigRespVO::getSteps)
                .map(steps -> steps.get(0)).map(WorkflowStepWrapperRespVO::getVariable)
                .map(VariableRespVO::getVariables)
                .orElseThrow(() -> ServiceExceptionUtil.exception(new ErrorCode(310900100, "系统步骤不能为空")));

        XhsAppResponse response = new XhsAppResponse();
        response.setUid(appMarket.getUid());
        response.setName(appMarket.getName());
        response.setCategory(appMarket.getCategory());
        response.setTags(appMarket.getTags());
        response.setImages(appMarket.getImages());
        response.setIcon(appMarket.getIcon());
        response.setDescription(appMarket.getDescription());
        response.setVariables(variableList);
        return response;
    }

    /**
     * 执行
     *
     * @param request 请求
     * @return 响应
     */
    @Override
    public XhsExecuteResponse execute(XhsExecuteRequest request) {


        return null;
    }
}
