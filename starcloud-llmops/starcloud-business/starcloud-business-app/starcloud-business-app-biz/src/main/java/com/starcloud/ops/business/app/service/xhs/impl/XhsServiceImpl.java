package com.starcloud.ops.business.app.service.xhs.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.google.common.collect.Lists;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import com.starcloud.ops.business.app.api.image.dto.UploadImageInfoDTO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.XhsAppResponse;
import com.starcloud.ops.business.app.api.xhs.XhsImageTemplateDTO;
import com.starcloud.ops.business.app.api.xhs.XhsImageTemplateResponse;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppCreativeExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppCreativeExecuteResponse;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppExecuteResponse;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsBathImageExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageExecuteResponse;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.xhs.FastPosterFactory;
import com.starcloud.ops.business.app.service.xhs.XhsService;
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import com.starcloud.ops.business.app.validate.AppValidate;
import lombok.extern.slf4j.Slf4j;
import net.fastposter.client.FastposterClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.EXECUTE_LISTING_CONFIG_FAILURE;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.EXECUTE_LISTING_STEP_FAILURE;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.EXECUTE_LISTING_VARIABLE_FAILURE;

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
    private AppDictionaryService appDictionaryService;

    /**
     * 获取图片模板
     *
     * @return 图片模板
     */
    @Override
    public List<XhsImageTemplateResponse> imageTemplates() {
        List<XhsImageTemplateDTO> templateList = appDictionaryService.xhsImageTemplates();
        return CollectionUtil.emptyIfNull(templateList).stream().map(item -> {
            XhsImageTemplateResponse response = new XhsImageTemplateResponse();
            response.setId(item.getId());
            response.setName(item.getName());
            response.setImageNumber(item.getImageNumber());
            response.setVariables(item.getVariables());
            return response;
        }).collect(Collectors.toList());
    }

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
        response.setIcon(appMarket.getIcon());
        response.setDescription(appMarket.getDescription());
        response.setVariables(variableList);
        return response;
    }

    /**
     * 执行应用
     *
     * @param request 请求
     * @return 响应
     */
    @Override
    public XhsAppExecuteResponse appExecute(XhsAppExecuteRequest request) {
        XhsAppExecuteResponse response = new XhsAppExecuteResponse();
        response.setSuccess(Boolean.FALSE);
        // 执行应用
        try {
            // 参数校验
            if (StringUtils.isBlank(request.getUid())) {
                throw ServiceExceptionUtil.exception(new ErrorCode(350400101, "应用UID不能为空！"));
            }
            response.setUid(request.getUid());
            if (CollectionUtil.isEmpty(request.getParams())) {
                throw ServiceExceptionUtil.exception(new ErrorCode(350400202, "应用参数不能为空！"));
            }
            // 获取应用
            AppMarketRespVO appMarket = appMarketService.get(request.getUid());
            AppExecuteRespVO executeResponse = appService.execute(buildExecuteRequest(appMarket, request));
            if (!executeResponse.getSuccess()) {
                response.setErrorCode(executeResponse.getResultCode());
                response.setErrorMsg(executeResponse.getResultDesc());
                return response;
            }
            ActionResponse actionResult = (ActionResponse) executeResponse.getResult();
            if (Objects.isNull(actionResult)) {
                response.setErrorCode("350400205");
                response.setErrorMsg("执行结果不存在！请稍候重试或者联系管理员！");
                return response;
            }
            if (!actionResult.getSuccess()) {
                response.setErrorCode(actionResult.getErrorCode());
                response.setErrorMsg(actionResult.getErrorMsg());
                return response;
            }
            if (StringUtils.isBlank(actionResult.getAnswer())) {
                response.setErrorCode("350400206");
                response.setErrorMsg("执行结果内容不存在！请稍候重试或者联系管理员！");
                return response;
            }
            response.setSuccess(Boolean.TRUE);
            response.setText(actionResult.getAnswer());
            return response;
        } catch (ServiceException exception) {
            response.setErrorCode(exception.getCode().toString());
            response.setErrorMsg(exception.getMessage());
            return response;
        } catch (Exception exception) {
            response.setErrorCode("350400200");
            response.setErrorMsg(exception.getMessage());
            return response;
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
        AppExecuteReqVO executeRequest = buildExecuteRequest(appMarket, request);
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
    public List<XhsAppCreativeExecuteResponse> bathAppCreativeExecute(List<XhsAppCreativeExecuteRequest> requests) {
        if (CollectionUtil.isEmpty(requests)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(350400202, "应用参数不能为空！"));
        }
        // 应用应用uid去重，去除重复的应用。此场景约定为 同一个应用的执行参数是相同的。
        Map<String, List<XhsAppCreativeExecuteRequest>> groupRequestMap = requests.stream().collect(Collectors.groupingBy(XhsAppCreativeExecuteRequest::getUid));
        List<XhsAppCreativeExecuteRequest> groupRequests = new ArrayList<>();
        groupRequestMap.forEach((key, value) -> groupRequests.add(value.get(0)));

        // 应用任务集合
        List<CompletableFuture<XhsAppCreativeExecuteResponse>> appFutures = Lists.newArrayList();
        for (XhsAppCreativeExecuteRequest request : groupRequests) {
            appFutures.add(CompletableFuture.supplyAsync(() -> {
                XhsAppCreativeExecuteResponse response = new XhsAppCreativeExecuteResponse();
                XhsAppExecuteResponse appExecuteResponse = this.appExecute(request);
                response.setUid(request.getUid());
                response.setCreativeContentUid(response.getCreativeContentUid());
                response.setSuccess(appExecuteResponse.getSuccess());
                response.setErrorCode(appExecuteResponse.getErrorCode());
                response.setErrorMsg(appExecuteResponse.getErrorMsg());
                response.setText(appExecuteResponse.getText());
                return response;
            }));
        }
        CompletableFuture.allOf(appFutures.toArray(new CompletableFuture[0])).join();
        List<XhsAppCreativeExecuteResponse> appResponses = Lists.newArrayList();
        for (CompletableFuture<XhsAppCreativeExecuteResponse> appFuture : appFutures) {
            appResponses.add(appFuture.join());
        }
        return appResponses;
    }

    /**
     * 异步执行图片
     *
     * @param request 请求
     * @return 响应
     */
    @Override
    public XhsImageExecuteResponse imageExecute(XhsImageExecuteRequest request) {
        log.info("小红书执行生成图片开始");
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

            // 获取图片模板
            List<XhsImageTemplateDTO> templateList = appDictionaryService.xhsImageTemplates();
            Optional<XhsImageTemplateDTO> optional = CollectionUtil.emptyIfNull(templateList).stream().filter(item -> StringUtils.equals(item.getId(), imageTemplate)).findFirst();
            if (!optional.isPresent()) {
                throw ServiceExceptionUtil.exception(new ErrorCode(350400203, "不支持的图片模板！"));
            }
            XhsImageTemplateDTO template = optional.get();
            if (StringUtils.isBlank(template.getToken()) || StringUtils.isBlank(template.getPosterId())) {
                throw ServiceExceptionUtil.exception(new ErrorCode(350400203, "系统配置异常！请联系管理员！"));
            }

            FastposterClient fastposterClient = getFastPosterClient(imageTemplate);
            // 执行生成图片
            log.info("小红书执行生成图片调用 FastPoster 开始!");
            byte[] bytes = fastposterClient.buildPoster(template.getPosterId()).params(params).build().bytes();
            if (Objects.isNull(bytes)) {
                throw ServiceExceptionUtil.exception(new ErrorCode(350400205, "生成图片失败！"));
            }
            log.info("小红书执行生成图片成功，调用 FastPoster 成功!");

            // 上传图片
            UploadImageInfoDTO image = ImageUploadUtils.uploadImage(IdUtil.fastSimpleUUID() + ".png", ImageUploadUtils.UPLOAD, bytes);
            log.info("小红书执行生成图片，上传图片到OSS成功，url：{}", image.getUrl());
            response.setSuccess(Boolean.TRUE);
            response.setIsMain(request.getIsMain());
            response.setIndex(request.getIndex());
            response.setUrl(image.getUrl());
            log.info("小红书执行生成图片成功，imageTemplate：{}，url：{}\n", imageTemplate, image.getUrl());
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
    public List<XhsImageExecuteResponse> bathImageExecute(XhsBathImageExecuteRequest request) {

        log.info("小红书执行批量生成图片开始");
        List<XhsImageExecuteRequest> imageRequestList = request.getImageRequests();
        if (CollectionUtil.isEmpty(imageRequestList)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(350400202, "图片参数不能为空！"));
        }

        // 图片任务集合，并且处理图片参数
        List<CompletableFuture<XhsImageExecuteResponse>> imageFutures = Lists.newArrayList();
        for (int i = 0; i < imageRequestList.size(); i++) {
            XhsImageExecuteRequest imageRequest = imageRequestList.get(i);
            imageRequest.setIndex(i + 1);
            imageRequest.setIsMain(i == 0 ? Boolean.TRUE : Boolean.FALSE);
            imageFutures.add(CompletableFuture.supplyAsync(() -> imageExecute(imageRequest)));
        }

        CompletableFuture.allOf(imageFutures.toArray(new CompletableFuture[0])).join();

        // 图片执行结果
        List<XhsImageExecuteResponse> imageResponses = Lists.newArrayList();
        for (CompletableFuture<XhsImageExecuteResponse> imageFuture : imageFutures) {
            imageResponses.add(imageFuture.join());
        }
        log.info("小红书执行批量生成图片结束");
        return imageResponses;
    }

    /**
     * 获取 FastPosterClient
     *
     * @param templateId 模板ID
     * @return FastPosterClient
     */
    private FastposterClient getFastPosterClient(String templateId) {
        List<XhsImageTemplateDTO> templateList = appDictionaryService.xhsImageTemplates();
        Optional<XhsImageTemplateDTO> optional = CollectionUtil.emptyIfNull(templateList).stream().filter(item -> StringUtils.equals(item.getId(), templateId)).findFirst();
        if (!optional.isPresent()) {
            throw ServiceExceptionUtil.exception(new ErrorCode(350400203, "不支持的图片模板！"));
        }
        String token = optional.get().getToken();
        if (StringUtils.isBlank(token)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(350400203, "不支持的图片模板！"));
        }
        return FastPosterFactory.factory(token);
    }

    /**
     * 构建执行请求
     *
     * @param app     应用
     * @param request 请求
     * @return 执行请求
     */
    private AppExecuteReqVO buildExecuteRequest(AppMarketRespVO app, XhsAppExecuteRequest request) {
        AppExecuteReqVO executeRequest = new AppExecuteReqVO();
        if (Objects.nonNull(request.getSseEmitter())) {
            executeRequest.setSseEmitter(request.getSseEmitter());
        }
        executeRequest.setMode(AppModelEnum.COMPLETION.name());
        executeRequest.setScene(StringUtils.isBlank(request.getScene()) ? AppSceneEnum.XHS_WRITING.name() : request.getScene());
        executeRequest.setAppUid(app.getUid());
        executeRequest.setAppReqVO(transform(app, request.getParams()));
        return executeRequest;
    }

    /**
     * 转换请求，转换为应用请求，并且填充参数
     *
     * @param app       应用
     * @param appParams 请求
     * @return 应用请求
     */
    private AppReqVO transform(AppMarketRespVO app, Map<String, Object> appParams) {
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
            if (appParams.containsKey(variableItem.getField())) {
                variableItem.setValue(appParams.get(variableItem.getField()));
                variableItem.setDefaultValue(appParams.get(variableItem.getField()));
            } else {
                Object value = variableItem.getValue();
                if (Objects.isNull(value)) {
                    if (Objects.nonNull(variableItem.getDefaultValue())) {
                        value = variableItem.getDefaultValue();
                    }
                }
                variableItem.setValue(value);
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
