package com.starcloud.ops.business.app.service.xhs.manager;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.xhs.execute.XhsImageCreativeExecuteRequest;
import com.starcloud.ops.business.app.api.xhs.execute.XhsImageCreativeExecuteResponse;
import com.starcloud.ops.business.app.api.xhs.execute.XhsImageExecuteRequest;
import com.starcloud.ops.business.app.api.xhs.execute.XhsImageExecuteResponse;
import com.starcloud.ops.business.app.api.xhs.execute.XhsImageStyleExecuteRequest;
import com.starcloud.ops.business.app.api.xhs.execute.XhsImageStyleExecuteResponse;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeImageTemplateDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeImageTemplateTypeDTO;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.feign.dto.PosterParam;
import com.starcloud.ops.business.app.feign.dto.PosterTemplateDTO;
import com.starcloud.ops.business.app.feign.dto.PosterTemplateTypeDTO;
import com.starcloud.ops.business.app.feign.request.poster.PosterRequest;
import com.starcloud.ops.business.app.service.poster.PosterService;
import com.starcloud.ops.business.app.service.xhs.executor.CreativeImageStyleThreadPoolHolder;
import com.starcloud.ops.business.app.util.CreativeAppUtils;
import com.starcloud.ops.business.app.util.CreativeImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Slf4j
@Component
public class CreativeImageManager {

    @Resource
    private PosterService posterService;

    @Resource
    private CreativeImageStyleThreadPoolHolder creativeImageStyleThreadPoolHolder;

    /**
     * 获取图片模板
     *
     * @return 图片模板
     */
    public List<CreativeImageTemplateDTO> templates() {
        List<PosterTemplateDTO> templates = posterService.templates();
        return CollectionUtil.emptyIfNull(templates).stream().map(item -> {
            List<PosterParam> params = CollectionUtil.emptyIfNull(item.getParams());
            int imageNumber = (int) params.stream().filter(param -> "image".equals(param.getType())).count();
            List<VariableItemDTO> variables = params.stream().map(param -> {
                if ("image".equals(param.getType())) {
                    return CreativeImageUtils.ofImageVariable(param.getId(), param.getName(), Optional.ofNullable(param.getOrder()).orElse(Integer.MAX_VALUE));
                } else if ("text".equals(param.getType())) {
                    return CreativeAppUtils.ofInputVariable(param.getId(), param.getName(), Optional.ofNullable(param.getOrder()).orElse(Integer.MAX_VALUE));
                } else {
                    return null;
                }
            }).filter(Objects::nonNull).sorted(Comparator.comparingInt(VariableItemDTO::getOrder)).collect(Collectors.toList());

            CreativeImageTemplateDTO response = new CreativeImageTemplateDTO();
            response.setId(item.getId());
            response.setName(item.getLabel());
            response.setExample(item.getTempUrl());
            response.setVariables(variables);
            response.setImageNumber(imageNumber);
            return response;
        }).collect(Collectors.toList());
    }

    /**
     * 获取图片模板 Map
     *
     * @return 图片模板 Map
     */
    public Map<String, CreativeImageTemplateDTO> mapTemplate() {
        if (CollectionUtils.isNotEmpty(templates())) {
            return templates().stream().collect(Collectors.toMap(CreativeImageTemplateDTO::getId, Function.identity()));
        }
        return Maps.newHashMap();
    }

    /**
     * 根据类型分组获取模板列表
     *
     * @return 模板列表
     */
    public List<CreativeImageTemplateTypeDTO> templateGroupByType() {
        List<PosterTemplateTypeDTO> templateTypeList = posterService.templateGroupByType();
        return CollectionUtil.emptyIfNull(templateTypeList).stream()
                .map(item -> {
                    // 获取模板列表
                    List<CreativeImageTemplateDTO> templateList = CollectionUtil.emptyIfNull(item.getList()).stream()
                            .map(templateItem -> {
                                CreativeImageTemplateDTO template = new CreativeImageTemplateDTO();
                                template.setId(templateItem.getId());
                                template.setName(templateItem.getLabel());
                                template.setExample(templateItem.getTempUrl());
                                return template;
                            }).collect(Collectors.toList());
                    // 组装模板类型
                    CreativeImageTemplateTypeDTO templateType = new CreativeImageTemplateTypeDTO();
                    templateType.setId(item.getId());
                    templateType.setName(item.getLabel());
                    templateType.setOrder(item.getOrder());
                    templateType.setList(templateList);
                    return templateType;
                }).collect(Collectors.toList());
    }

    /**
     * 异步执行图片
     *
     * @param request 请求
     * @return 响应
     */
    public XhsImageExecuteResponse execute(XhsImageExecuteRequest request) {
        log.info("海报图片生成：执行生成图片开始: 执行参数: \n{}", JSONUtil.parse(request).toStringPretty());
        XhsImageExecuteResponse response = XhsImageExecuteResponse.ofBase();
        try {
            String id = request.getId();
            // 参数校验
            if (StringUtils.isBlank(id)) {
                throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.POSTER_ID_REQUIRED);
            }
            response.setId(id);

            Map<String, Object> params = request.getParams();
            if (CollectionUtil.isEmpty(params)) {
                throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.POSTER_PARAMS_REQUIRED);
            }

            // 获取海报图片模板
            List<CreativeImageTemplateDTO> posterTemplates = templates();
            Optional<CreativeImageTemplateDTO> optional = CollectionUtil.emptyIfNull(posterTemplates).stream().filter(item -> StringUtils.equals(item.getId(), id)).findFirst();
            if (!optional.isPresent()) {
                throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.POSTER_NOT_SUPPORTED, request.getName());
            }

            // 执行生成海报图片
            PosterRequest posterRequest = new PosterRequest();
            posterRequest.setId(request.getId());
            posterRequest.setParams(request.getParams());
            String url = posterService.poster(posterRequest);

            // 构建响应
            response.setSuccess(Boolean.TRUE);
            response.setIsMain(request.getIsMain());
            response.setIndex(request.getIndex());
            response.setUrl(url);
            log.info("海报图片生成: 执行生成图片成功，id：{}，url：{}\n", id, url);
        } catch (ServiceException exception) {
            log.info("海报图片生成: 生成图片失败(ServiceException): 错误码：{}，错误信息：{}", exception.getCode(), exception.getMessage());
            response.setErrorCode(exception.getCode().toString());
            response.setErrorMsg(exception.getMessage());
        } catch (Exception exception) {
            log.info("海报图片生成: 生成图片失败(Exception): 错误码：{}，错误信息：{}", 350400200, exception.getMessage());
            response.setErrorCode("750100110");
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
    public XhsImageStyleExecuteResponse styleExecute(XhsImageStyleExecuteRequest request) {
        try {
            log.info("创作中心：图片风格执行：图片生成开始：风格名称：{}, 风格ID：{}", request.getName(), request.getId());
            List<XhsImageExecuteRequest> imageRequestList = request.getImageRequests();
            if (CollectionUtil.isEmpty(imageRequestList)) {
                throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.STYLE_IMAGE_TEMPLATE_NOT_EMPTY, request.getName());
            }

            ThreadPoolExecutor threadPoolExecutor = creativeImageStyleThreadPoolHolder.executor();
            List<CompletableFuture<XhsImageExecuteResponse>> imageFutureList = Lists.newArrayList();
            for (XhsImageExecuteRequest imageExecuteRequest : imageRequestList) {
                CompletableFuture<XhsImageExecuteResponse> future = CompletableFuture.supplyAsync(() -> execute(imageExecuteRequest), threadPoolExecutor);
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
                return XhsImageStyleExecuteResponse.failure(request.getId(), request.getName(), 750100212, "创作中心：图片风格执行：图片生成失败：" + message, imageResponseList);
            }

            // 构建响应
            XhsImageStyleExecuteResponse response = new XhsImageStyleExecuteResponse();
            response.setSuccess(Boolean.TRUE);
            response.setId(request.getId());
            response.setName(request.getName());
            response.setImageResponses(imageResponseList);
            log.info("创作中心：图片风格执行：图片生成结束：风格名称：{}, 风格ID：{}", request.getName(), request.getId());
            return response;
        } catch (ServiceException exception) {
            log.info("创作中心：图片风格执行：图片生成失败(ServiceException): 错误码：{}，错误信息：{}", exception.getCode(), exception.getMessage());
            return XhsImageStyleExecuteResponse.failure(request.getId(), request.getName(), exception.getCode(), exception.getMessage(), null);
        } catch (Exception exception) {
            log.info("创作中心：图片风格执行：图片生成失败(Exception): 错误码：{}，错误信息：{}", 750100210, exception.getMessage());
            return XhsImageStyleExecuteResponse.failure(request.getId(), request.getName(), 750100210, exception.getMessage(), null);
        }
    }

    /**
     * 异步批量执行图片
     *
     * @param request 请求
     * @return 响应
     */
    public XhsImageCreativeExecuteResponse creativeExecute(XhsImageCreativeExecuteRequest request) {
        try {
            log.info("创作中心：图片生成开始：创作计划UID：{}, 创作方案UID：{}, 创作任务UID：{}", request.getPlanUid(), request.getSchemeUid(), request.getContentUid());
            // 执行图片生成
            XhsImageStyleExecuteResponse imageStyleResponse = styleExecute(request.getImageStyleRequest());
            if (Objects.isNull(imageStyleResponse)) {
                throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.CREATIVE_IMAGE_RESPONSE_NOT_NULL);
            }
            if (!imageStyleResponse.getSuccess()) {
                Integer code = Objects.isNull(imageStyleResponse.getErrorCode()) ? 750100312 : imageStyleResponse.getErrorCode();
                String message = StringUtils.isBlank(imageStyleResponse.getErrorMessage()) ? "创作中心：图片生成失败" : imageStyleResponse.getErrorMessage();
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
            log.info("创作中心：：图片生成结束：创作计划UID：{}, 创作方案UID：{}, 创作任务UID：{}", request.getPlanUid(), request.getSchemeUid(), request.getContentUid());
            return response;
        } catch (ServiceException exception) {
            log.info("创作中心：：图片生成失败(ServiceException): 错误码：{}，错误信息：{}", exception.getCode(), exception.getMessage());
            return XhsImageCreativeExecuteResponse.failure(request, exception.getCode(), exception.getMessage(), null);
        } catch (Exception exception) {
            log.info("创作中心：：图片生成失败(Exception): 错误码：{}，错误信息：{}", 750100310, exception.getMessage());
            return XhsImageCreativeExecuteResponse.failure(request, 750100310, exception.getMessage(), null);
        }
    }

}
