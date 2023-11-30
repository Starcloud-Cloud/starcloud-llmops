package com.starcloud.ops.business.app.service.xhs.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.google.common.collect.Lists;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketListQuery;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.XhsImageTemplateDTO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppCreativeExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppCreativeExecuteResponse;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppExecuteResponse;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageCreativeExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageCreativeExecuteResponse;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageExecuteResponse;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageStyleExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageStyleExecuteResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.plan.CreativeTypeEnum;
import com.starcloud.ops.business.app.feign.dto.PosterParam;
import com.starcloud.ops.business.app.feign.dto.PosterTemplateDTO;
import com.starcloud.ops.business.app.feign.request.poster.PosterRequest;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.poster.PosterService;
import com.starcloud.ops.business.app.service.xhs.XhsImageCreativeThreadPoolHolder;
import com.starcloud.ops.business.app.service.xhs.XhsImageStyleThreadPoolHolder;
import com.starcloud.ops.business.app.service.xhs.XhsService;
import com.starcloud.ops.business.app.util.CreativeUtil;
import com.starcloud.ops.business.app.validate.AppValidate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.WORKFLOW_CONFIG_FAILURE;

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

    @Resource
    private PosterService posterService;

    @Resource
    private XhsImageStyleThreadPoolHolder xhsImageStyleThreadPoolHolder;

    @Resource
    private XhsImageCreativeThreadPoolHolder xhsImageCreativeThreadPoolHolder;

    /**
     * 获取图片模板
     *
     * @return 图片模板
     */
    @Override
    public List<XhsImageTemplateDTO> imageTemplates() {
        List<PosterTemplateDTO> templates = posterService.templates();
        return CollectionUtil.emptyIfNull(templates).stream().map(item -> {
            List<PosterParam> params = CollectionUtil.emptyIfNull(item.getParams());
            int imageNumber = (int) params.stream().filter(param -> "image".equals(param.getType())).count();
            List<VariableItemDTO> variables = params.stream().map(param -> {
                if ("image".equals(param.getType())) {
                    return CreativeUtil.ofImageVariable(param.getId(), param.getName());
                } else if ("text".equals(param.getType())) {
                    return CreativeUtil.ofInputVariable(param.getId(), param.getName());
                } else {
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());

            XhsImageTemplateDTO response = new XhsImageTemplateDTO();
            response.setId(item.getId());
            response.setName(item.getLabel());
            response.setExample(item.getTempUrl());
            response.setVariables(variables);
            response.setImageNumber(imageNumber);
            return response;
        }).collect(Collectors.toList());
    }

    /**
     * 根据类型获取需要执行的应用信息
     *
     * @param type 计划类型
     * @return 应用信息
     */
    @Override
    public AppMarketRespVO getExecuteApp(String type) {
        List<AppMarketRespVO> apps = appMarketplaceList(type);
        AppValidate.notEmpty(apps, ErrorCodeConstants.CREATIVE_PLAN_APP_NOT_EXIST);
        AppMarketRespVO app = apps.get(0);
        AppValidate.notNull(app, ErrorCodeConstants.CREATIVE_PLAN_APP_NOT_EXIST);
        return app;
    }

    /**
     * 根据类型获取应用列表
     *
     * @param type 类型
     * @return 文案模板列表
     */
    @Override
    public List<AppMarketRespVO> appMarketplaceList(String type) {
        AppValidate.notBlank(type, ErrorCodeConstants.CREATIVE_PLAN_TYPE_REQUIRED);
        CreativeTypeEnum typeEnum = CreativeTypeEnum.of(type);
        if (typeEnum == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_TYPE_NOT_SUPPORTED, type);
        }
        // 查询
        AppMarketListQuery query = new AppMarketListQuery();
        query.setIsSimple(Boolean.FALSE);
        query.setTags(typeEnum.getTagType().getTags());
        List<AppMarketRespVO> list = appMarketService.list(query);

        return CollectionUtil.emptyIfNull(list);
    }

    /**
     * 通用执行应用
     *
     * @param request 请求
     * @return 响应
     */
    @Override
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
    @Override
    public List<XhsAppExecuteResponse> appExecute(XhsAppExecuteRequest request) {
        Integer n = Objects.nonNull(request.getN()) && request.getN() > 0 ? request.getN() : 1;
        request.setN(n);
        try {
            // 参数校验
            if (StringUtils.isBlank(request.getUid())) {
                return XhsAppExecuteResponse.failure("350400101", "应用UID不能为空, 生成条数: " + n, n);
            }
            log.info("小红书执行应用开始。参数为\n：{}", JSONUtil.parse(request).toStringPretty());
            // 获取应用
            AppMarketRespVO appMarket = appMarketService.get(request.getUid());
            List<WorkflowStepWrapperRespVO> stepWrapperList = Optional.ofNullable(appMarket).map(AppMarketRespVO::getWorkflowConfig).map(WorkflowConfigRespVO::getSteps)
                    .orElseThrow(() -> ServiceExceptionUtil.exception(WORKFLOW_CONFIG_FAILURE));
            // 获取第二步的步骤。约定，生成小红书内容为第二步
            WorkflowStepWrapperRespVO stepWrapper = stepWrapperList.get(1);
            request.setStepId(stepWrapper.getField());
            // 执行应用
            String answer = execute(CreativeUtil.buildExecuteRequest(appMarket, request));
            return CreativeUtil.handleAnswer(answer, request.getUid(), n);
        } catch (ServiceException exception) {
            log.error("小红书执行应用失败。应用UID: {}, 生成条数: {}, 错误码: {}, 错误信息: {}", request.getUid(), n, exception.getCode().toString(), exception.getMessage());
            return XhsAppExecuteResponse.failure(request.getUid(), exception.getCode().toString(), exception.getMessage(), n);
        } catch (Exception exception) {
            log.error("小红书执行应用失败。应用UID: {}, 生成条数: {}, 错误码: {}, 错误信息: {}", request.getUid(), n, "350400100", exception.getMessage());
            return XhsAppExecuteResponse.failure(request.getUid(), "350400100", exception.getMessage(), n);
        }
    }

    /**
     * 异步执行应用
     *
     * @param request 请求
     */
    @Override
    public void asyncAppExecute(XhsAppExecuteRequest request) {
        // 参数校验
        if (StringUtils.isBlank(request.getUid())) {
            throw ServiceExceptionUtil.exception(new ErrorCode(350400101, "应用UID不能为空！"));
        }
        if (CollectionUtil.isEmpty(request.getParams())) {
            throw ServiceExceptionUtil.exception(new ErrorCode(350400202, "应用参数不能为空！"));
        }
        // 获取应用
        AppMarketRespVO appMarket = appMarketService.get(request.getUid());
        List<WorkflowStepWrapperRespVO> stepWrapperList = Optional.ofNullable(appMarket).map(AppMarketRespVO::getWorkflowConfig).map(WorkflowConfigRespVO::getSteps)
                .orElseThrow(() -> ServiceExceptionUtil.exception(WORKFLOW_CONFIG_FAILURE));
        // 获取第二步的步骤。约定，生成小红书内容为第二步
        WorkflowStepWrapperRespVO stepWrapper = stepWrapperList.get(1);
        request.setStepId(stepWrapper.getField());
        AppExecuteReqVO executeRequest = CreativeUtil.buildExecuteRequest(appMarket, request);
        // 执行应用
        appService.asyncExecute(executeRequest);
    }

    /**
     * 批量执行应用, 同步执行
     *
     * @param requests 请求
     * @return 响应
     */
    @Override
    @SuppressWarnings("all")
    public List<XhsAppCreativeExecuteResponse> bathAppCreativeExecute(List<XhsAppCreativeExecuteRequest> requests) {
        log.info("小红书执行批量生成应用开始......!");
        if (CollectionUtil.isEmpty(requests)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(350400202, "应用参数不能为空！"));
        }
        // 首先按照创作计划进行分组
        Map<String, List<XhsAppCreativeExecuteRequest>> planMap = requests.stream().collect(Collectors.groupingBy(XhsAppCreativeExecuteRequest::getPlanUid));
        log.info("小红书执行批量生成应用，按照创作计划进行分组，共有{}个创作计划, 创作计划UID分别是：{}", planMap.size(), planMap.keySet());
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
                List<XhsAppExecuteResponse> responses = this.appExecute(request);
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
                    responseList.add(response);
                }
                log.info("创作计划UID：{}，创作方案UID：{}，执行结束！", planEntry.getKey(), schemeEntry.getKey());
            }
            log.info("创作计划UID：{}，执行结束！", planEntry.getKey());
        }
        log.info("小红书执行批量生成应用结束......! \n {}", JSONUtil.parse(responseList).toStringPretty());
        return responseList;
    }

    /**
     * 异步执行图片
     *
     * @param request 请求
     * @return 响应
     */
    @Override
    public XhsImageExecuteResponse imageExecute(XhsImageExecuteRequest request) {
        log.info("小红书执行生成图片开始: 执行参数: \n{}", JSONUtil.parse(request).toStringPretty());
        XhsImageExecuteResponse response = XhsImageExecuteResponse.ofBase();
        try {
            String imageTemplate = request.getImageTemplate();
            // 参数校验
            if (StringUtils.isBlank(imageTemplate)) {
                throw ServiceExceptionUtil.exception(new ErrorCode(350400201, "图片模板不能为空！"));
            }
            response.setImageTemplate(imageTemplate);

            Map<String, Object> params = request.getParams();
            if (CollectionUtil.isEmpty(params)) {
                throw ServiceExceptionUtil.exception(new ErrorCode(350400202, "图片参数不能为空！"));
            }

            // 获取海报图片模板
            List<XhsImageTemplateDTO> posterTemplates = imageTemplates();
            Optional<XhsImageTemplateDTO> optional = CollectionUtil.emptyIfNull(posterTemplates).stream().filter(item -> StringUtils.equals(item.getId(), imageTemplate)).findFirst();
            if (!optional.isPresent()) {
                throw ServiceExceptionUtil.exception(new ErrorCode(350400203, "不支持的图片模板或者图片模板不存在！"));
            }

            // 执行生成海报图片
            PosterRequest posterRequest = new PosterRequest();
            posterRequest.setId(request.getImageTemplate());
            posterRequest.setParams(request.getParams());
            String url = posterService.poster(posterRequest);

            response.setSuccess(Boolean.TRUE);
            response.setIsMain(request.getIsMain());
            response.setIndex(request.getIndex());
            response.setUrl(url);
            log.info("小红书执行生成图片成功，imageTemplate：{}，url：{}\n", imageTemplate, url);

        } catch (ServiceException exception) {
            log.info("小红书生成图片失败(ServiceException): 错误码：{}，错误信息：{}", exception.getCode(), exception.getMessage());
            response.setErrorCode(exception.getCode().toString());
            response.setErrorMsg(exception.getMessage());
        } catch (Exception exception) {
            log.info("小红书生成图片失败(Exception): 错误码：{}，错误信息：{}", 350400200, exception.getMessage());
            response.setErrorCode("350400200");
            response.setErrorMsg(exception.getMessage());
        }

        return response;
    }

    /**
     * 异步批量执行图片
     *
     * @param request 请求
     * @return 响应
     */
    @Override
    public XhsImageStyleExecuteResponse imageStyleExecute(XhsImageStyleExecuteRequest request) {
        try {
            log.info("小红书图片风格执行：图片生成开始：风格名称：{}, 风格ID：{}", request.getName(), request.getId());
            List<XhsImageExecuteRequest> imageRequestList = request.getImageRequests();
            if (CollectionUtil.isEmpty(imageRequestList)) {
                throw ServiceExceptionUtil.exception(new ErrorCode(350400202, "图片参数不能为空！"));
            }

            ThreadPoolExecutor threadPoolExecutor = xhsImageStyleThreadPoolHolder.executor();
            List<CompletableFuture<XhsImageExecuteResponse>> imageFutureList = Lists.newArrayList();
            for (XhsImageExecuteRequest imageExecuteRequest : imageRequestList) {
                CompletableFuture<XhsImageExecuteResponse> future = CompletableFuture.supplyAsync(() -> imageExecute(imageExecuteRequest), threadPoolExecutor);
                imageFutureList.add(future);
            }
            // 合并任务
            CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(imageFutureList.toArray(new CompletableFuture[0]));
            // 等待所有任务执行完成并且获取执行结果
            CompletableFuture<List<XhsImageExecuteResponse>> allFuture = allOfFuture
                    .thenApply(v -> imageFutureList.stream().map(CompletableFuture::join).collect(Collectors.toList()));
            // 获取执行结果
            List<XhsImageExecuteResponse> imageResponseList = allFuture.join();

            // 全部成功，才算成功。
            List<XhsImageExecuteResponse> failureList = imageResponseList.stream()
                    .filter(r -> !r.getSuccess())
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(failureList)) {
                String message = failureList.stream().map(XhsImageExecuteResponse::getErrorMsg).collect(Collectors.joining("；"));
                return XhsImageStyleExecuteResponse.failure(request.getId(), request.getName(), 350600119, "小红书图片风格执行：图片生成失败：" + message, imageResponseList);
            }

            // 构建响应
            XhsImageStyleExecuteResponse response = new XhsImageStyleExecuteResponse();
            response.setSuccess(Boolean.TRUE);
            response.setId(request.getId());
            response.setName(request.getName());
            response.setImageResponses(imageResponseList);
            log.info("小红书图片风格执行：图片生成结束：风格名称：{}, 风格ID：{}", request.getName(), request.getId());
            return response;
        } catch (ServiceException exception) {
            log.info("小红书图片风格执行：图片生成失败(ServiceException): 错误码：{}，错误信息：{}", exception.getCode(), exception.getMessage());
            return XhsImageStyleExecuteResponse.failure(request.getId(), request.getName(), exception.getCode(), exception.getMessage(), null);
        } catch (Exception exception) {
            log.info("小红书图片风格执行：图片生成失败(Exception): 错误码：{}，错误信息：{}", 350400200, exception.getMessage());
            return XhsImageStyleExecuteResponse.failure(request.getId(), request.getName(), 350400200, exception.getMessage(), null);
        }
    }

    /**
     * 异步批量执行图片
     *
     * @param request 请求
     * @return 响应
     */
    @Override
    public XhsImageCreativeExecuteResponse imageCreativeExecute(XhsImageCreativeExecuteRequest request) {
        try {
            log.info("小红书创作中心执行：图片生成开始：创作计划UID：{}, 创作方案UID：{}, 创作任务UID：{}", request.getPlanUid(), request.getSchemeUid(), request.getContentUid());
            // 执行图片生成
            XhsImageStyleExecuteResponse imageStyleResponse = imageStyleExecute(request.getImageStyleRequest());
            if (Objects.isNull(imageStyleResponse)) {
                return XhsImageCreativeExecuteResponse.failure(request, 350400200, "小红书创作中心执行：图片生成失败", null);
            }
            if (!imageStyleResponse.getSuccess()) {
                Integer code = Objects.isNull(imageStyleResponse.getErrorCode()) ? 350400200 : imageStyleResponse.getErrorCode();
                String message = StringUtils.isBlank(imageStyleResponse.getErrorMessage()) ? "小红书创作中心执行：图片生成失败" : imageStyleResponse.getErrorMessage();
                return XhsImageCreativeExecuteResponse.failure(request, code, message, imageStyleResponse);
            }
            // 构建响应
            XhsImageCreativeExecuteResponse response = new XhsImageCreativeExecuteResponse();
            response.setSuccess(Boolean.TRUE);
            response.setPlanUid(request.getPlanUid());
            response.setSchemeUid(request.getSchemeUid());
            response.setBusinessUid(request.getBusinessUid());
            response.setContentUid(request.getContentUid());
            response.setImageStyleResponse(imageStyleResponse);
            log.info("小红书创作中心执行：图片生成结束：创作计划UID：{}, 创作方案UID：{}, 创作任务UID：{}", request.getPlanUid(), request.getSchemeUid(), request.getContentUid());
            return response;
        } catch (ServiceException exception) {
            log.info("小红书创作中心执行：图片生成失败(ServiceException): 错误码：{}，错误信息：{}", exception.getCode(), exception.getMessage());
            return XhsImageCreativeExecuteResponse.failure(request, exception.getCode(), exception.getMessage(), null);
        } catch (Exception exception) {
            log.info("小红书创作中心执行：图片生成失败(Exception): 错误码：{}，错误信息：{}", 350400200, exception.getMessage());
            return XhsImageCreativeExecuteResponse.failure(request, 350400200, exception.getMessage(), null);
        }
    }

    /**
     * 批量执行创作中心小红书图片生成
     *
     * @param requestList 请求
     * @return 响应
     */
    @Override
    public List<XhsImageCreativeExecuteResponse> bathImageCreativeExecute(List<XhsImageCreativeExecuteRequest> requestList) {
        log.info("创作中心：小红书图片生成执行：图片生成开始");
        if (CollectionUtil.isEmpty(requestList)) {
            log.warn("创作中心：小红书图片生成执行：参数为空！图片生成结束");
            return Collections.emptyList();
        }
        // 获取异步Future
        ThreadPoolExecutor executor = xhsImageCreativeThreadPoolHolder.executor();
        List<CompletableFuture<XhsImageCreativeExecuteResponse>> imageFutureList = Lists.newArrayList();
        for (XhsImageCreativeExecuteRequest imageCreativeExecuteRequest : requestList) {
            CompletableFuture<XhsImageCreativeExecuteResponse> future = CompletableFuture.supplyAsync(() -> imageCreativeExecute(imageCreativeExecuteRequest), executor);
            imageFutureList.add(future);
        }
        // 合并任务
        CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(imageFutureList.toArray(new CompletableFuture[0]));
        // 等待所有任务执行完成并且获取执行结果
        CompletableFuture<List<XhsImageCreativeExecuteResponse>> allFuture = allOfFuture
                .thenApply(v -> imageFutureList.stream().map(CompletableFuture::join).collect(Collectors.toList()));
        // 获取执行结果
        List<XhsImageCreativeExecuteResponse> responses = allFuture.join();
        log.info("创作中心：小红书图片生成执行：图片生成结束");
        return responses;
    }


}
