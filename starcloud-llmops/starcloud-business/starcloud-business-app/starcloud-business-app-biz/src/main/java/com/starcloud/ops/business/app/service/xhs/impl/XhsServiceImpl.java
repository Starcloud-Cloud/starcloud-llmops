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
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsBathImageExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageExecuteResponse;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.xhs.XhsImageTemplateEnum;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.xhs.XhsService;
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import com.starcloud.ops.business.app.validate.AppValidate;
import lombok.extern.slf4j.Slf4j;
import net.fastposter.client.FastposterClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
    private FastposterClient fastposterClient;

    /**
     * 获取图片模板
     *
     * @return 图片模板
     */
    @Override
    public List<XhsImageTemplateDTO> imageTemplates() {
        return XhsImageTemplateEnum.templateList();
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
        AppExecuteReqVO executeRequest = buildExecuteRequest(appMarket, request.getParams());
        // 执行应用
        appService.asyncExecute(executeRequest);
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
            XhsImageTemplateEnum templateEnum = XhsImageTemplateEnum.of(imageTemplate);
            AppValidate.notNull(templateEnum, new ErrorCode(350400203, "不支持的图片模板！"));

            // 参数校验
            List<VariableItemRespVO> variables = templateEnum.variables();
            for (VariableItemRespVO variable : variables) {
                if ("IMAGE".equals(variable.getType()) && (!params.containsKey(variable.getField()) || Objects.isNull(params.get(variable.getField())))) {
                    throw ServiceExceptionUtil.exception(new ErrorCode(350400204, "图片参数{}是必填的！"), variable.getLabel());
                }
            }

            // 执行生成图片
            log.info("小红书执行生成图片调用 FastPoster 开始!");
            byte[] bytes = fastposterClient.buildPoster(templateEnum.getCode()).params(params).build().bytes();
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
     * 构建执行请求
     *
     * @param app       应用
     * @param appParams 请求
     * @return 执行请求
     */
    private AppExecuteReqVO buildExecuteRequest(AppMarketRespVO app, Map<String, Object> appParams) {
        AppExecuteReqVO executeRequest = new AppExecuteReqVO();
        executeRequest.setMode(AppModelEnum.COMPLETION.name());
        executeRequest.setScene(AppSceneEnum.XHS_WRITING.name());
        executeRequest.setAppUid(app.getUid());
        executeRequest.setAppReqVO(transform(app, appParams));
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
                variableItem.setValue(null);
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
