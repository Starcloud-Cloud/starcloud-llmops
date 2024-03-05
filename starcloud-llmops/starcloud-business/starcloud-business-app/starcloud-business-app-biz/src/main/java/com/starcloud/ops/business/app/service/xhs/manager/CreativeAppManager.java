package com.starcloud.ops.business.app.service.xhs.manager;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketListQuery;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.execute.XhsAppCreativeExecuteRequest;
import com.starcloud.ops.business.app.api.xhs.execute.XhsAppCreativeExecuteResponse;
import com.starcloud.ops.business.app.api.xhs.execute.XhsAppExecuteRequest;
import com.starcloud.ops.business.app.api.xhs.execute.XhsAppExecuteResponse;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteRespVO;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.enums.market.AppMarketTagTypeEnum;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.util.CreativeAppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 创作中心文案生成管理器
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Slf4j
@Component
public class CreativeAppManager {

    @Resource
    private AppService appService;

    @Resource
    private AppMarketService appMarketService;

    /**
     * 根据类型获取应用列表
     *
     * @return 文案模板列表
     */
    public List<AppMarketRespVO> juzhenAppMarketplaceList() {
        AppMarketListQuery query = new AppMarketListQuery();
        query.setIsSimple(Boolean.FALSE);
        query.setTags(AppMarketTagTypeEnum.XIAO_HONG_SHU_CUSTOM_WRITING.getTags());
        List<AppMarketRespVO> list = appMarketService.list(query);
        return CollectionUtil.emptyIfNull(list);
    }

    /**
     * 异步执行应用
     *
     * @param executeRequest 请求
     */
    public void asyncAppExecute(AppExecuteReqVO executeRequest) {
        // 执行应用
        appService.asyncExecute(executeRequest);
    }

    /**
     * 通用执行应用
     *
     * @param request 请求
     * @return 响应
     */
    public String execute(AppExecuteReqVO request) {
        try {
            AppExecuteRespVO executeResponse = appService.execute(request);
            ActionResponse actionResponse = (ActionResponse) executeResponse.getResult();
            return actionResponse.getAnswer();
        } catch (ServiceException exception) {
            throw exception;
        } catch (Exception exception) {
            throw ServiceExceptionUtil.exception(new ErrorCode(350500100, exception.getMessage()));
        }
    }

    /**
     * 执行应用
     *
     * @param request 请求
     * @return 响应
     */
    public List<XhsAppExecuteResponse> execute(XhsAppExecuteRequest request) {
        Integer n = Objects.nonNull(request.getN()) && request.getN() > 0 ? request.getN() : 1;
        request.setN(n);
        try {

            //把 uid 和 step ,改成 根据模式 对应的枚举配置写死 在创作的时候保存就行了。
            AppMarketRespVO appMarket = appMarketService.get(request.getUid());
            log.info("创作中心：执行应用开始。参数为\n：{}", JSONUtil.parse(request).toStringPretty());

            // 获取第二步的步骤。约定，生成小红书内容为第二步
            WorkflowStepWrapperRespVO stepWrapper = CreativeAppUtils.secondStep(appMarket);
            request.setStepId(stepWrapper.getField());
            // 执行应用
            String answer = execute(CreativeAppUtils.buildExecuteRequest(appMarket, request));

            // 返回结果需要解析，解析根据 模式 枚举配置来，
            return CreativeAppUtils.handleAnswer(answer, request.getUid(), n);
        } catch (ServiceException exception) {
            log.error("创作中心：执行应用失败。应用UID: {}, 生成条数: {}, 错误码: {}, 错误信息: {}", request.getUid(), n, exception.getCode().toString(), exception.getMessage());
            return XhsAppExecuteResponse.failure(request.getUid(), exception.getCode().toString(), exception.getMessage(), n);
        } catch (Exception exception) {
            log.error("创作中心：执行应用失败。应用UID: {}, 生成条数: {}, 错误码: {}, 错误信息: {}", request.getUid(), n, "750100310", exception.getMessage());
            return XhsAppExecuteResponse.failure(request.getUid(), "750100310", exception.getMessage(), n);
        }
    }

    /**
     * 批量执行应用, 同步执行
     *
     * @param requests 请求
     * @return 响应
     */
    @SuppressWarnings("all")
    public List<XhsAppCreativeExecuteResponse> bathAppCreativeExecute(List<XhsAppCreativeExecuteRequest> requests) {
        log.info("创作中心：执行批量生成应用开始......!");
        if (CollectionUtil.isEmpty(requests)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(750100411, "应用参数不能为空！"));
        }
        // 首先按照创作计划进行分组
        Map<String, List<XhsAppCreativeExecuteRequest>> planMap = requests.stream().collect(Collectors.groupingBy(XhsAppCreativeExecuteRequest::getPlanUid));
        log.info("创作中心：执行批量生成应用，按照创作计划进行分组，共有{}个创作计划, 创作计划UID分别是：{}", planMap.size(), planMap.keySet());
        // 默认执行参数一样
        List<XhsAppCreativeExecuteResponse> responseList = new ArrayList<>();
        for (Map.Entry<String, List<XhsAppCreativeExecuteRequest>> planEntry : planMap.entrySet()) {
            log.info("当前创作计划UID：{}", planEntry.getKey());
            List<XhsAppCreativeExecuteRequest> planGroupRequestList = planEntry.getValue();
            if (CollectionUtil.isEmpty(planGroupRequestList)) {
                log.info("当前创作计划UID：{}，没有执行参数, 跳过！", planEntry.getKey());
                continue;
            }
            // 再按照创作方案进行分组
            Map<String, List<XhsAppCreativeExecuteRequest>> schemeMap = planGroupRequestList.stream().collect(Collectors.groupingBy(XhsAppCreativeExecuteRequest::getSchemeUid));
            log.info("当前创作计划UID：{}，按照创作方案进行分组，共有{}个创作方案, 创作方案UID分别是：{}", planEntry.getKey(), schemeMap.size(), schemeMap.keySet());
            for (Map.Entry<String, List<XhsAppCreativeExecuteRequest>> schemeEntry : schemeMap.entrySet()) {
                log.info("当前创作计划UID：{}，当前创作方案UID：{}", planEntry.getKey(), schemeEntry.getKey());
                List<XhsAppCreativeExecuteRequest> schemeGroupRequestList = schemeEntry.getValue();
                if (CollectionUtil.isEmpty(schemeGroupRequestList)) {
                    log.info("当前创作计划UID：{}，当前创作方案UID：{}，没有执行参数, 跳过！", planEntry.getKey(), schemeEntry.getKey());
                    continue;
                }

                // 执行应用
                log.info("执行参数：生成条数: {}, 执行参数： \n{}", schemeGroupRequestList.size(), JSONUtil.parse(schemeGroupRequestList).toStringPretty());
                XhsAppCreativeExecuteRequest request = schemeGroupRequestList.get(0);
                request.setN(schemeGroupRequestList.size());
                List<XhsAppExecuteResponse> responses = this.execute(request);
                // 构建响应
                for (int i = 0; i < responses.size(); i++) {
                    XhsAppCreativeExecuteResponse response = new XhsAppCreativeExecuteResponse();
                    XhsAppExecuteResponse item = responses.get(i);
                    XhsAppCreativeExecuteRequest executeRequest = schemeGroupRequestList.get(i);
                    response.setUid(item.getUid());
                    response.setSuccess(item.getSuccess());
                    response.setCopyWriting(item.getCopyWriting());
                    response.setErrorCode(item.getErrorCode());
                    response.setErrorMsg(item.getErrorMsg());
                    response.setPlanUid(executeRequest.getPlanUid());
                    response.setSchemeUid(executeRequest.getSchemeUid());
                    response.setBusinessUid(executeRequest.getBusinessUid());
                    response.setContentUid(executeRequest.getContentUid());
                    response.setSchemeMode(executeRequest.getSchemeMode());
                    responseList.add(response);
                }
                log.info("创作计划UID：{}，创作方案UID：{}，执行结束！", planEntry.getKey(), schemeEntry.getKey());
            }
            log.info("创作计划UID：{}，执行结束！", planEntry.getKey());
        }
        log.info("创作中心：执行批量生成应用结束......! \n {}", JSONUtil.parse(responseList).toStringPretty());
        return responseList;
    }


    /**
     * 批量执行应用, 同步执行
     *
     * @param requests 请求
     * @return 响应
     */
    @SuppressWarnings("all")
    public XhsAppCreativeExecuteResponse creativePracticalExecute(XhsAppCreativeExecuteRequest request) {
        request.setN(1);
        try {

            //把 uid 和 step ,改成 根据模式 对应的枚举配置写死 在创作的时候保存就行了。
            AppMarketRespVO appMarket = appMarketService.get(request.getUid());
            log.info("创作中心：执行应用开始(干货文模式)。参数为\n：{}", JSONUtil.parse(request).toStringPretty());

            // 获取第二步的步骤。约定，生成小红书内容为第二步
            WorkflowStepWrapperRespVO stepWrapper = CreativeAppUtils.secondStep(appMarket);
            request.setStepId(stepWrapper.getField());
            // 执行应用
            String answer = execute(CreativeAppUtils.buildExecuteRequest(appMarket, request));
            // 返回结果需要解析，解析根据 模式 枚举配置来，
            return CreativeAppUtils.handlePracticalAnswer(answer, request);
        } catch (ServiceException exception) {
            log.error("创作中心：执行应用失败(干货文模式)。应用UID: {}, 错误码: {}, 错误信息: {}", request.getUid(), exception.getCode().toString(), exception.getMessage());
            return XhsAppCreativeExecuteResponse.buildFailure(request, exception.getCode().toString(), exception.getMessage());
        } catch (Exception exception) {
            log.error("创作中心：执行应用失败(干货文模式)。应用UID: {}, 错误码: {}, 错误信息: {}", request.getUid(), "750100310", exception.getMessage());
            return XhsAppCreativeExecuteResponse.buildFailure(request, "750100310", exception.getMessage());
        }
    }
}
