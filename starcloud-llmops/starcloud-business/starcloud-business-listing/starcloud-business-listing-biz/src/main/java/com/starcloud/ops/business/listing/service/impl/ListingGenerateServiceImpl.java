package com.starcloud.ops.business.listing.service.impl;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketQuery;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.business.listing.service.ListingGenerateService;
import com.starcloud.ops.business.listing.vo.ListingGenerateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.EXECUTE_LISTING_CONFIG_FAILURE;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.EXECUTE_LISTING_STEP_FAILURE;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.EXECUTE_LISTING_VARIABLE_FAILURE;

/**
 * Listing 生成服务，用于生成 Listing 标题，五点描述，产品描述等
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-26
 */
@Slf4j
@Service
public class ListingGenerateServiceImpl implements ListingGenerateService {

    @Resource
    private AppService appService;

    @Resource
    private AppMarketService appMarketService;

    /**
     * 根据应用标签获取应用
     *
     * @param tags 应用标签
     * @return 应用
     */
    @Override
    public AppMarketRespVO getApp(List<String> tags) {
        AppMarketQuery query = new AppMarketQuery();
        query.setType(AppTypeEnum.SYSTEM.name());
        query.setModel(AppModelEnum.COMPLETION.name());
        query.setTags(tags);
        return appMarketService.get(query);
    }

    /**
     * 异步执行AI生成Listing标题或者五点描述或者产品描述等
     *
     * @param request 请求
     */
    @Override
    public void asyncExecute(ListingGenerateRequest request) {
        AppMarketRespVO app = this.getApp(request.getTags());
        AppExecuteReqVO executeRequest = buildExecuteRequest(request, app);
        appService.asyncExecute(executeRequest);
    }

    /**
     * 构建执行请求
     *
     * @param request 请求
     * @param app     应用
     * @return 执行请求
     */
    private AppExecuteReqVO buildExecuteRequest(ListingGenerateRequest request, AppMarketRespVO app) {
        AppExecuteReqVO executeRequest = new AppExecuteReqVO();
        executeRequest.setScene(AppSceneEnum.LISTING_GENERATE.name());
        executeRequest.setAppUid(app.getUid());
        executeRequest.setAiModel(request.getAiModel());
        executeRequest.setAppReqVO(transform(request, app));
        log.info("Listing生成，执行请求: {}\n", JSONUtil.parse(executeRequest).toStringPretty());
        return executeRequest;
    }

    /**
     * 转换请求，转换为应用请求，并且填充参数
     *
     * @param request 请求
     * @param app     应用
     * @return 应用请求
     */
    private AppReqVO transform(ListingGenerateRequest request, AppMarketRespVO app) {
        Map<String, String> requestMap = request.toMap();
        WorkflowConfigRespVO config = app.getWorkflowConfig();
        AppValidate.notNull(config, EXECUTE_LISTING_CONFIG_FAILURE);

        List<WorkflowStepWrapperRespVO> steps = config.getSteps();
        AppValidate.notEmpty(steps, EXECUTE_LISTING_STEP_FAILURE);

        // 直接取第一个步骤，执行第一步骤
        WorkflowStepWrapperRespVO stepWrapper = steps.get(0);
        AppValidate.notNull(stepWrapper, EXECUTE_LISTING_STEP_FAILURE);

        VariableRespVO variable = stepWrapper.getVariable();
        AppValidate.notNull(variable, EXECUTE_LISTING_VARIABLE_FAILURE);

        List<VariableItemRespVO> variables = variable.getVariables();
        AppValidate.notEmpty(variables, EXECUTE_LISTING_VARIABLE_FAILURE);

        List<VariableItemRespVO> fillVariables = Lists.newArrayList();
        // 填充变量
        for (VariableItemRespVO variableItem : variables) {
            if (requestMap.containsKey(variableItem.getField())) {
                variableItem.setValue(requestMap.get(variableItem.getField()));
                variableItem.setDefaultValue(requestMap.get(variableItem.getField()));
            }
            fillVariables.add(variableItem);
        }
        variable.setVariables(fillVariables);
        stepWrapper.setVariable(variable);
        steps.set(0, stepWrapper);
        config.setSteps(steps);
        app.setWorkflowConfig(config);
        return AppMarketConvert.INSTANCE.convert(app);
    }

}
