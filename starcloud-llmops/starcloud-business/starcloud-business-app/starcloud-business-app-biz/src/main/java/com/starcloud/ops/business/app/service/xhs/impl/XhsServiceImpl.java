package com.starcloud.ops.business.app.service.xhs.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
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
import com.starcloud.ops.business.app.controller.admin.xhs.vo.dto.XhsGenerateContentDTO;
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
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatMessage;
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
        variableList = variableList.stream().filter(VariableItemRespVO::getIsShow).collect(Collectors.toList());
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
    public List<XhsAppExecuteResponse> appExecute(XhsAppExecuteRequest request) {
        Integer n = Objects.nonNull(request.getN()) && request.getN() > 0 ? request.getN() : 1;
        request.setN(n);
        // 参数校验
        if (StringUtils.isBlank(request.getUid())) {
            log.error("小红书执行应用失败。应用UID不能为空, 生成条数: {}", n);
            return XhsAppExecuteResponse.failure("350400101", "应用UID不能为空, 生成条数: " + n, n);
        }
        // 执行应用
        try {
            log.info("小红书执行应用开始。参数为\n：{}", JSONUtil.parse(request).toStringPretty());
            // 获取应用
            AppMarketRespVO appMarket = appMarketService.get(request.getUid());
            AppExecuteRespVO executeResponse = appService.execute(buildExecuteRequest(appMarket, request));
            if (!executeResponse.getSuccess()) {
                log.error("小红书执行应用失败。应用UID: {}, 生成条数: {}, 错误码: {}, 错误信息: {}",
                        request.getUid(), n, executeResponse.getResultCode(), executeResponse.getResultDesc());
                return XhsAppExecuteResponse.failure(request.getUid(), "350400101", executeResponse.getResultDesc(), n);
            }
            ActionResponse actionResult = (ActionResponse) executeResponse.getResult();
            if (Objects.isNull(actionResult)) {
                log.error("小红书执行应用失败。应用UID: {}, 生成条数: {}, 错误码: {}, 错误信息: {}",
                        request.getUid(), n, "350400205", "执行结果不存在！请稍候重试或者联系管理员！");
                return XhsAppExecuteResponse.failure(request.getUid(), "350400205", "执行结果不存在！请稍候重试或者联系管理员！", n);
            }
            if (!actionResult.getSuccess()) {
                log.error("小红书执行应用失败。应用UID: {}, 生成条数: {}, 错误码: {}, 错误信息: {}",
                        request.getUid(), n, actionResult.getErrorCode(), actionResult.getErrorMsg());
                return XhsAppExecuteResponse.failure(request.getUid(), actionResult.getErrorCode(), actionResult.getErrorMsg(), n);
            }

            String answer = actionResult.getAnswer();
            if (StringUtils.isBlank(answer)) {
                log.error("小红书执行应用失败。应用UID: {}, 生成条数: {}, 错误码: {}, 错误信息: {}",
                        request.getUid(), n, "350400206", "执行结果内容不存在！请稍候重试或者联系管理员！");
                return XhsAppExecuteResponse.failure(request.getUid(), "350400206", "执行结果内容不存在！请稍候重试或者联系管理员！", n);
            }

            if (n == 1) {
                XhsGenerateContentDTO generateContent = JSONUtil.toBean(answer.trim(), XhsGenerateContentDTO.class);

                if (Objects.isNull(generateContent) || StringUtils.isBlank(generateContent.getTitle()) || StringUtils.isBlank(generateContent.getContent())) {
                    log.error("小红书执行应用失败。应用UID: {}, 生成条数: {}, 错误码: {}, 错误信息: {}, 原始数据：{}",
                            request.getUid(), n, "350400208", "执行结果内容格式不正确！请稍候重试或者联系管理员！", answer);

                    return XhsAppExecuteResponse.failure(request.getUid(), "350400208", "执行结果内容格式不正确！请稍候重试或者联系管理员！", 1);
                } else {
                    String title = generateContent.getTitle();
                    String text = generateContent.getContent();
                    log.info("小红书执行应用成功。应用UID: {}, 生成条数: {}, 标题: {}, 内容: {}", request.getUid(), n, title, text);
                    return XhsAppExecuteResponse.success(request.getUid(), title, text, 1);
                }
            } else {
                TypeReference<List<ChatCompletionChoice>> typeReference = new TypeReference<List<ChatCompletionChoice>>() {
                };
                List<ChatCompletionChoice> choices = JSONUtil.toBean(answer.trim(), typeReference, true);
                if (CollectionUtil.isEmpty(choices)) {
                    log.error("小红书执行应用失败。应用UID: {}, 生成条数: {}, 错误码: {}, 错误信息: {}",
                            request.getUid(), n, "350400209", "执行结果内容为空！请稍候重试或者联系管理员！");
                    return XhsAppExecuteResponse.failure(request.getUid(), "350400209", "执行结果内容为空！请稍候重试或者联系管理员！", n);
                }
                if (choices.size() != n) {
                    log.error("小红书执行应用失败。应用UID: {}, 生成条数: {}, 实际市场条数：{},  错误码: {}, 错误信息: {}",
                            request.getUid(), n, choices.size(), "350400210", "执行结果内容条数不正确！请稍候重试或者联系管理员！");
                    return XhsAppExecuteResponse.failure(request.getUid(), "350400210", "执行结果内容条数不正确！请稍候重试或者联系管理员！", n);
                }
                List<XhsAppExecuteResponse> list = new ArrayList<>();
                for (int i = 0; i < choices.size(); i++) {
                    ChatCompletionChoice choice = choices.get(i);
                    XhsAppExecuteResponse appExecuteResponse = new XhsAppExecuteResponse();
                    appExecuteResponse.setUid(request.getUid());

                    String content = Optional.ofNullable(choice).map(ChatCompletionChoice::getMessage).map(ChatMessage::getContent).orElse("");
                    if (StringUtils.isBlank(content)) {
                        log.warn("第[{}]生成失败：应用UID: {}, 总生成条数: {}, 错误码: {}, 错误信息: {}, 原始数据: {}",
                                i + 1, request.getUid(), n, "350400211", "执行结果内容为空！", content);

                        appExecuteResponse.setSuccess(Boolean.FALSE);
                        appExecuteResponse.setErrorCode("350400211");
                        appExecuteResponse.setErrorMsg("执行结果内容为空！请稍候重试或者联系管理员！");
                        list.add(appExecuteResponse);
                    } else {
                        XhsGenerateContentDTO generateContent = JSONUtil.toBean(JSONUtil.parseObj(content), XhsGenerateContentDTO.class);
                        if (Objects.isNull(generateContent) || StringUtils.isBlank(generateContent.getTitle()) || StringUtils.isBlank(generateContent.getContent())) {
                            log.warn("第[{}]生成失败：应用UID: {}, 总生成条数: {}, 错误码: {}, 错误信息: {}, 原数据: {}",
                                    i + 1, request.getUid(), n, "350400212", "执行结果内容格式不正确！请稍候重试或者联系管理员！", content);
                        } else {
                            log.info("第[{}]生成成功：应用UID: {}, 总生成条数: {}, 标题: {}, 内容: {}",
                                    i + 1, request.getUid(), n, generateContent.getTitle(), generateContent.getContent());
                            appExecuteResponse.setSuccess(Boolean.TRUE);
                            appExecuteResponse.setTitle(generateContent.getTitle());
                            appExecuteResponse.setContent(generateContent.getContent());
                            list.add(appExecuteResponse);
                        }
                    }
                }
                log.info("小红书执行应用成功。应用UID: {}, 生成条数: {}, 结果: {}", request.getUid(), n, list);
                return list;
            }
        } catch (ServiceException exception) {
            log.error("小红书执行应用失败。应用UID: {}, 生成条数: {}, 错误码: {}, 错误信息: {}",
                    request.getUid(), n, exception.getCode().toString(), exception.getMessage());
            return XhsAppExecuteResponse.failure(request.getUid(), exception.getCode().toString(), exception.getMessage(), n);
        } catch (Exception exception) {
            log.error("小红书执行应用失败。应用UID: {}, 生成条数: {}, 错误码: {}, 错误信息: {}",
                    request.getUid(), n, "350400200", exception.getMessage());
            return XhsAppExecuteResponse.failure(request.getUid(), "350400200", exception.getMessage(), n);
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
        Map<String, List<XhsAppCreativeExecuteRequest>> groupMap = requests.stream().collect(Collectors.groupingBy(XhsAppCreativeExecuteRequest::getUid));

        // 默认执行参数一样
        List<XhsAppCreativeExecuteResponse> responseList = new ArrayList<>();
        for (Map.Entry<String, List<XhsAppCreativeExecuteRequest>> entry : groupMap.entrySet()) {
            List<XhsAppCreativeExecuteRequest> value = entry.getValue();
            if (CollectionUtil.isEmpty(value)) {
                continue;
            }
            XhsAppCreativeExecuteRequest request = value.get(0);
            request.setN(value.size());
            List<XhsAppExecuteResponse> responses = this.appExecute(request);
            for (int i = 0; i < responses.size(); i++) {
                XhsAppCreativeExecuteResponse response = new XhsAppCreativeExecuteResponse();
                XhsAppExecuteResponse item = responses.get(i);
                String contentUid = Optional.ofNullable(value.get(i)).map(XhsAppCreativeExecuteRequest::getCreativeContentUid).orElse("");
                response.setUid(item.getUid());
                response.setSuccess(item.getSuccess());
                response.setTitle(item.getTitle());
                response.setContent(item.getContent());
                response.setErrorCode(item.getErrorCode());
                response.setErrorMsg(item.getErrorMsg());
                response.setCreativeContentUid(contentUid);
                responseList.add(response);
            }
        }
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
        List<String> imageUrls = request.getImageUrls();
        if (CollectionUtil.isEmpty(imageUrls)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(350400201, "图片URL不能为空！"));
        }

        List<XhsImageExecuteRequest> imageRequestList = request.getImageRequests();
        if (CollectionUtil.isEmpty(imageRequestList)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(350400202, "图片参数不能为空！"));
        }
        // 图片执行结果
        List<XhsImageExecuteResponse> imageResponses = Lists.newArrayList();
        for (int i = 0; i < imageRequestList.size(); i++) {

            XhsImageExecuteRequest imageRequest = imageRequestList.get(i);
            imageRequest.setIndex(i + 1);
            imageRequest.setIsMain(i == 0 ? Boolean.TRUE : Boolean.FALSE);

            // 处理参数，随机取一个图片URL。
            Map<String, Object> handlerParams = imageRequest.getParams();
            for (Map.Entry<String, Object> entry : imageRequest.getParams().entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                // 如果是 IMAGE_VARIABLE，随机取一个图片URL
                if ("IMAGE_VARIABLE".equals(value)) {
                    int randomInt = RandomUtil.randomInt(imageUrls.size());
                    handlerParams.put(key, imageUrls.get(randomInt));
                } else {
                    handlerParams.put(key, value);
                }
            }

            imageRequest.setParams(handlerParams);

            XhsImageExecuteResponse response = imageExecute(imageRequest);
            imageResponses.add(response);
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
        executeRequest.setUserId(request.getUserId());
        executeRequest.setMode(AppModelEnum.COMPLETION.name());
        executeRequest.setScene(StringUtils.isBlank(request.getScene()) ? AppSceneEnum.XHS_WRITING.name() : request.getScene());
        executeRequest.setAppUid(app.getUid());
        executeRequest.setN(request.getN());
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
