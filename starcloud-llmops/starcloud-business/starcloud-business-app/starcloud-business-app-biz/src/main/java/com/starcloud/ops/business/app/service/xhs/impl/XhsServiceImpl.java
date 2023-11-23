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
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.plan.CreativeTypeEnum;
import com.starcloud.ops.business.app.feign.dto.PosterParam;
import com.starcloud.ops.business.app.feign.dto.PosterTemplateDTO;
import com.starcloud.ops.business.app.feign.request.poster.PosterRequest;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.poster.PosterService;
import com.starcloud.ops.business.app.service.xhs.XhsService;
import com.starcloud.ops.business.app.util.CreativeUtil;
import com.starcloud.ops.business.app.validate.AppValidate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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

    /**
     * 获取图片模板
     *
     * @return 图片模板
     */
    @Override
    public List<XhsImageTemplateResponse> imageTemplates() {
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

            XhsImageTemplateResponse response = new XhsImageTemplateResponse();
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
                response.setCopyWriting(item.getCopyWriting());
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

            // 获取海报图片模板
//            List<XhsImageTemplateResponse> posterTemplates = imageTemplates();
//            Optional<XhsImageTemplateResponse> optional = CollectionUtil.emptyIfNull(posterTemplates).stream().filter(item -> StringUtils.equals(item.getId(), imageTemplate)).findFirst();
//            if (!optional.isPresent()) {
//                throw ServiceExceptionUtil.exception(new ErrorCode(350400203, "不支持的图片模板！"));
//            }
            
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
    public List<XhsImageExecuteResponse> bathImageExecute(XhsBathImageExecuteRequest request) {
        log.info("小红书执行批量生成图片开始");
        List<XhsImageExecuteRequest> imageRequestList = request.getImageRequests();
        if (CollectionUtil.isEmpty(imageRequestList)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(350400202, "图片参数不能为空！"));
        }
        // 图片执行结果
        List<XhsImageExecuteResponse> imageResponses = Lists.newArrayList();
        for (XhsImageExecuteRequest imageRequest : imageRequestList) {
            imageRequest.setImageTemplate(imageRequest.getImageTemplate());
            imageRequest.setIndex(imageRequest.getIndex());
            imageRequest.setIsMain(imageRequest.getIsMain());
            imageRequest.setParams(imageRequest.getParams());
            XhsImageExecuteResponse response = imageExecute(imageRequest);
            imageResponses.add(response);
        }
        log.info("小红书执行批量生成图片结束");
        return imageResponses;
    }

}
