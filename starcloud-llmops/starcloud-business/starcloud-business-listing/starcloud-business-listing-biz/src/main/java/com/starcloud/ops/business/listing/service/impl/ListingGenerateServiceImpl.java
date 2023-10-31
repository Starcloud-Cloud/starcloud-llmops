package com.starcloud.ops.business.listing.service.impl;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketQuery;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteRespVO;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.business.listing.enums.ListingGenerateTypeEnum;
import com.starcloud.ops.business.listing.enums.ListingLanguageEnum;
import com.starcloud.ops.business.listing.enums.ListingWritingStyleEnum;
import com.starcloud.ops.business.listing.service.ListingGenerateService;
import com.starcloud.ops.business.listing.vo.ListingGenerateRequest;
import com.starcloud.ops.business.listing.vo.ListingGenerateResponse;
import com.starcloud.ops.framework.common.api.dto.Option;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
     * Listing 生成元数据
     *
     * @return Listing 生成元数据
     */
    @Override
    public Map<String, List<Option>> metadata() {
        Map<String, List<Option>> metadata = Maps.newHashMap();
        metadata.put("targetLanguage", ListingLanguageEnum.options());
        metadata.put("writingStyle", ListingWritingStyleEnum.options());
        return metadata;
    }

    /**
     * 根据应用标签获取应用
     *
     * @param listingType listing 类型
     * @return 应用
     */
    @Override
    public AppMarketRespVO getListingApp(String listingType) {
        AppMarketQuery query = new AppMarketQuery();
        query.setType(AppTypeEnum.SYSTEM.name());
        query.setModel(AppModelEnum.COMPLETION.name());
        ListingGenerateTypeEnum listingTypeEnum = ListingGenerateTypeEnum.valueOf(listingType);
        AppValidate.notNull(listingTypeEnum, ErrorCodeConstants.EXECUTE_LISTING_FAILURE, listingType);
        query.setTags(listingTypeEnum.getTags());
        return appMarketService.get(query);
    }

    /**
     * 同步执行AI生成Listing标题或者五点描述或者产品描述等
     *
     * @param request 请求
     */
    @Override
    public ListingGenerateResponse execute(ListingGenerateRequest request) {
        ListingGenerateResponse response = ofBaseResponse(request);
        try {
            log.info("同步Listing生成开始，Listing类型: {}", request.getListingType());
            AppMarketRespVO app = this.getListingApp(request.getListingType());
            log.info("同步Listing生成，应用市场查询成功 应用名称: {}, 应用UID: {}", app.getName(), app.getUid());
            AppExecuteReqVO executeRequest = buildExecuteRequest(request, app);
            AppExecuteRespVO executeResponse = appService.execute(executeRequest);

            if (Objects.isNull(executeResponse)) {
                return buildFailure(response, ErrorCodeConstants.EXECUTE_LISTING_RESULT_IS_NULL);
            }
            if (!executeResponse.getSuccess()) {
                return buildFailure(response, executeResponse.getResultCode(), executeResponse.getResultDesc());
            }
            if (Objects.isNull(executeResponse.getResult())) {
                return buildFailure(response, ErrorCodeConstants.EXECUTE_LISTING_RESULT_IS_NULL);
            }
            if (!(executeResponse.getResult() instanceof ActionResponse)) {
                return buildFailure(response, ErrorCodeConstants.EXECUTE_LISTING_RESULT_FAILURE);
            }

            ActionResponse result = (ActionResponse) executeResponse.getResult();
            if (!result.getSuccess()) {
                return buildFailure(response, result.getErrorCode(), result.getErrorMsg());
            }

            response.setSuccess(true);
            response.setAnswer(result.getAnswer());
            return response;
        } catch (ServiceException exception) {
            log.error("同步Listing生成，Listing类型: {}, 异常: {}", request.getListingType(), exception.getMessage());
            return buildFailure(response, exception.getCode().toString(), exception.getMessage());
        } catch (Exception exception) {
            log.error("同步Listing生成，Listing类型: {}, 异常: {}", request.getListingType(), exception.getMessage());
            return buildFailure(response, ErrorCodeConstants.EXECUTE_LISTING_FAILURE.getCode().toString(), exception.getMessage());
        } finally {
            log.info("同步Listing生成结束，Listing类型: {}, 执行结果:\n {}", request.getListingType(), JSONUtil.parse(response).toStringPretty());
        }

    }

    /**
     * 异步执行AI生成Listing标题或者五点描述或者产品描述等
     *
     * @param request 请求
     */
    @Override
    public void asyncExecute(ListingGenerateRequest request) {
        log.info("异步Listing生成，Listing类型: {}", request.getListingType());
        AppMarketRespVO app = this.getListingApp(request.getListingType());
        log.info("异步Listing生成，应用市场查询成功 应用名称: {}, 应用UID: {}", app.getName(), app.getUid());
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
        executeRequest.setSseEmitter(request.getSseEmitter());
        executeRequest.setConversationUid(request.getConversationUid());
        executeRequest.setMode(AppModelEnum.COMPLETION.name());
        executeRequest.setScene(AppSceneEnum.LISTING_GENERATE.name());
        executeRequest.setAppUid(app.getUid());
        executeRequest.setMediumUid(request.getDraftUid());
        executeRequest.setAiModel(request.getAiModel());
        executeRequest.setAppReqVO(transform(request, app));
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

    /**
     * 构建基础响应
     *
     * @param request 请求
     * @return 基础响应
     */
    private ListingGenerateResponse ofBaseResponse(ListingGenerateRequest request) {
        ListingGenerateResponse response = new ListingGenerateResponse();
        response.setSuccess(Boolean.FALSE);
        response.setConversationUid(request.getConversationUid());
        response.setListingType(request.getListingType());
        response.setDraftUid(request.getDraftUid());
        return response;
    }

    /**
     * 构建失败响应
     *
     * @param response  响应
     * @param errorCode 错误码
     */
    private ListingGenerateResponse buildFailure(ListingGenerateResponse response, ErrorCode errorCode) {
        return buildFailure(response, errorCode.getCode().toString(), errorCode.getMsg());
    }

    /**
     * 构建失败响应
     *
     * @param response  响应
     * @param errorCode 错误码
     * @param errorMsg  错误信息
     */
    private ListingGenerateResponse buildFailure(ListingGenerateResponse response, String errorCode, String errorMsg) {
        response.setErrorCode(errorCode);
        response.setErrorMsg(errorMsg);
        return response;
    }

}
