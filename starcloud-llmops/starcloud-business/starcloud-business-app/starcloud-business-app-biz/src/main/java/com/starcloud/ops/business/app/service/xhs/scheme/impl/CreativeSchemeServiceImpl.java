package com.starcloud.ops.business.app.service.xhs.scheme.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Maps;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.execute.XhsAppExecuteResponse;
import com.starcloud.ops.business.app.api.xhs.execute.XhsImageExecuteRequest;
import com.starcloud.ops.business.app.api.xhs.execute.XhsImageExecuteResponse;
import com.starcloud.ops.business.app.api.xhs.execute.XhsImageStyleExecuteRequest;
import com.starcloud.ops.business.app.api.xhs.execute.XhsImageStyleExecuteResponse;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CopyWritingContentDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeImageDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeImageStyleDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeImageTemplateDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeSchemeConfigDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeSchemeCopyWritingTemplateDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeSchemeExampleDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeSchemeExampleRequest;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeSchemeImageTemplateDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemeListReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemeModifyReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemePageReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemeReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.CreativeSchemeListOptionRespVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.CreativeSchemeRespVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.scheme.vo.CreativeSchemeSseReqVO;
import com.starcloud.ops.business.app.convert.xhs.scheme.CreativeSchemeConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.scheme.CreativeSchemeDO;
import com.starcloud.ops.business.app.dal.mysql.xhs.scheme.CreativeSchemeMapper;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeModeEnum;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeRefersSourceEnum;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeTypeEnum;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.business.app.service.xhs.executor.CreativeImageCreativeThreadPoolHolder;
import com.starcloud.ops.business.app.service.xhs.manager.CreativeAppManager;
import com.starcloud.ops.business.app.service.xhs.manager.CreativeImageManager;
import com.starcloud.ops.business.app.service.xhs.scheme.CreativeSchemeService;
import com.starcloud.ops.business.app.util.CreativeAppUtils;
import com.starcloud.ops.business.app.util.CreativeImageUtils;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.business.app.util.UserUtils;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * 创作方案服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-14
 */
@Slf4j
@Service
public class CreativeSchemeServiceImpl implements CreativeSchemeService {

    @Resource
    private CreativeSchemeMapper creativeSchemeMapper;

    @Resource
    private CreativeAppManager creativeAppManager;

    @Resource
    private CreativeImageManager creativeImageManager;

    @Resource
    private AppDictionaryService appDictionaryService;

    @Resource
    private CreativeImageCreativeThreadPoolHolder creativeImageCreativeThreadPoolHolder;

    /**
     * 获取创作方案元数据
     *
     * @return 创作方案元数据
     */
    @Override
    public Map<String, Object> metadata() {
        Map<String, Object> metadata = Maps.newHashMap();
        metadata.put("category", appDictionaryService.creativeSchemeCategoryTree());
        metadata.put("refersSource", CreativeSchemeRefersSourceEnum.options());
        return metadata;
    }

    /**
     * 获取创作方案详情
     *
     * @param uid 创作方案UID
     * @return 创作方案详情
     */
    @Override
    public CreativeSchemeRespVO get(String uid) {
        CreativeSchemeDO creativeScheme = creativeSchemeMapper.get(uid);
        AppValidate.notNull(creativeScheme, CreativeErrorCodeConstants.SCHEME_NOT_EXIST);
        return CreativeSchemeConvert.INSTANCE.convertResponse(creativeScheme);
    }

    /**
     * 获取创作方案列表
     *
     * @param query 查询条件
     * @return 创作方案列表
     */
    @Override
    public List<CreativeSchemeRespVO> list(CreativeSchemeListReqVO query) {
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (Objects.isNull(loginUserId)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.USER_MAY_NOT_LOGIN);
        }
        query.setLoginUserId(String.valueOf(loginUserId));
        query.setIsAdmin(UserUtils.isAdmin());
        List<CreativeSchemeDO> list = creativeSchemeMapper.list(query);
        return CreativeSchemeConvert.INSTANCE.convertList(list);
    }

    /**
     * 查询并且校验创作方案是否存在
     *
     * @param uidList 创作方案UID列表
     * @return 创作方案列表
     */
    @Override
    public List<CreativeSchemeRespVO> list(List<String> uidList) {
        if (CollectionUtil.isEmpty(uidList)) {
            return Collections.emptyList();
        }
        List<CreativeSchemeDO> list = creativeSchemeMapper.listByUidList(uidList);
        return CreativeSchemeConvert.INSTANCE.convertList(list);
    }

    /**
     * 获取创作方案列表
     *
     * @param query 查询条件
     * @return 创作方案列表
     */
    @Override
    public List<CreativeSchemeListOptionRespVO> listOption(CreativeSchemeListReqVO query) {
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (Objects.isNull(loginUserId)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.USER_MAY_NOT_LOGIN);
        }
        query.setLoginUserId(String.valueOf(loginUserId));
        query.setIsAdmin(UserUtils.isAdmin());
        List<CreativeSchemeRespVO> list = list(query);
        return CollectionUtil.emptyIfNull(list).stream().map(item -> {
            List<VariableItemDTO> variable = Optional.ofNullable(item.getConfiguration()).map(CreativeSchemeConfigDTO::getCopyWritingTemplate).map(CreativeSchemeCopyWritingTemplateDTO::getVariables).orElse(Lists.newArrayList());
            CreativeSchemeListOptionRespVO option = new CreativeSchemeListOptionRespVO();
            option.setUid(item.getUid());
            option.setName(item.getName());
            option.setVariables(variable);
            option.setDescription(item.getDescription());
            option.setCreateTime(item.getCreateTime());
            return option;
        }).collect(Collectors.toList());
    }

    /**
     * 分页查询创作方案
     *
     * @param query 查询条件
     * @return 创作方案列表
     */
    @Override
    public PageResult<CreativeSchemeRespVO> page(CreativeSchemePageReqVO query) {
        IPage<CreativeSchemeDO> page = creativeSchemeMapper.page(PageUtil.page(query), query);
        return CreativeSchemeConvert.INSTANCE.convertPage(page);
    }

    /**
     * 创建创作方案
     *
     * @param request 创作方案请求
     */
    @Override
    public void create(CreativeSchemeReqVO request) {
        handlerAndValidate(request);
        if (creativeSchemeMapper.distinctName(request.getName())) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_NAME_EXIST);
        }
        CreativeSchemeDO scheme = CreativeSchemeConvert.INSTANCE.convertCreateRequest(request);
        creativeSchemeMapper.insert(scheme);
    }

    /**
     * 复制创作方案
     *
     * @param request 请求
     */
    @Override
    public void copy(UidRequest request) {
        CreativeSchemeDO creativeScheme = creativeSchemeMapper.get(request.getUid());
        AppValidate.notNull(creativeScheme, CreativeErrorCodeConstants.SCHEME_NOT_EXIST);

        CreativeSchemeDO scheme = new CreativeSchemeDO();
        scheme.setUid(IdUtil.simpleUUID());
        scheme.setName(getCopyName(creativeScheme.getName()));
        scheme.setType(creativeScheme.getType());
        scheme.setCategory(creativeScheme.getCategory());
        scheme.setTags(creativeScheme.getTags());
        scheme.setDescription(creativeScheme.getDescription());
        scheme.setMode(creativeScheme.getMode());
        scheme.setRefers(creativeScheme.getRefers());
        scheme.setConfiguration(creativeScheme.getConfiguration());
        scheme.setUseImages(creativeScheme.getUseImages());
        scheme.setExample(creativeScheme.getExample());
        scheme.setCreateTime(LocalDateTime.now());
        scheme.setUpdateTime(LocalDateTime.now());
        scheme.setDeleted(Boolean.FALSE);
        creativeSchemeMapper.insert(scheme);
    }

    /**
     * 修改创作方案
     *
     * @param request 创作方案请求
     */
    @Override
    public void modify(CreativeSchemeModifyReqVO request) {
        handlerAndValidate(request);
        CreativeSchemeDO creativeScheme = creativeSchemeMapper.get(request.getUid());
        AppValidate.notNull(creativeScheme, CreativeErrorCodeConstants.SCHEME_NOT_EXIST);
        // 如果修改了名称，校验名称是否重复
        if (!creativeScheme.getName().equals(request.getName()) && creativeSchemeMapper.distinctName(request.getName())) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_NAME_EXIST);
        }
        CreativeSchemeDO scheme = CreativeSchemeConvert.INSTANCE.convertModifyRequest(request);
        scheme.setId(creativeScheme.getId());
        creativeSchemeMapper.updateById(scheme);
    }

    /**
     * 删除创作方案
     *
     * @param uid 创作方案UID
     */
    @Override
    public void delete(String uid) {
        CreativeSchemeDO creativeScheme = creativeSchemeMapper.get(uid);
        AppValidate.notNull(creativeScheme, CreativeErrorCodeConstants.SCHEME_NOT_EXIST);
        creativeSchemeMapper.deleteById(creativeScheme.getId());
    }

    /**
     * 分析生成要求
     *
     * @param request 创作方案需求请求
     */
    @Override
    public void summary(CreativeSchemeSseReqVO request) {
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (Objects.isNull(loginUserId)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.USER_MAY_NOT_LOGIN);
        }
        AppMarketRespVO executeApp = creativeAppManager.getExecuteApp(CreativeSchemeModeEnum.RANDOM_IMAGE_TEXT);
        WorkflowStepWrapperRespVO stepWrapper = CreativeAppUtils.firstStep(executeApp);

        AppExecuteReqVO executeRequest = new AppExecuteReqVO();
        if (Objects.nonNull(request.getSseEmitter())) {
            executeRequest.setSseEmitter(request.getSseEmitter());
        }
        executeRequest.setUserId(SecurityFrameworkUtils.getLoginUserId());
        executeRequest.setMode(AppModelEnum.COMPLETION.name());
        executeRequest.setScene(AppSceneEnum.XHS_WRITING.name());
        executeRequest.setAppUid(executeApp.getUid());
        executeRequest.setStepId(stepWrapper.getField());
        executeRequest.setN(1);
        executeRequest.setAppReqVO(CreativeAppUtils.transform(executeApp, request, stepWrapper.getField()));
        creativeAppManager.asyncAppExecute(executeRequest);
    }

    /**
     * 创建文案示例
     *
     * @param request 创作方案需求请求
     * @return 文案示例
     */
    @Override
    public List<CreativeSchemeExampleDTO> example(CreativeSchemeReqVO request) {
        log.info("生成示例开始.....");
        handlerAndValidate(request);
        // 图片素材列表校验
        if (CollectionUtil.isEmpty(request.getUseImages())) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_UPLOAD_IMAGE_EMPTY);
        }
        // 获取当前登录用户ID
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (Objects.isNull(loginUserId)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.USER_MAY_NOT_LOGIN);
        }

        // 生成文案示例
        List<CopyWritingContentDTO> copyWritingList = copyWritingExample(request, loginUserId);

        // 处理图片示例参数
        List<CreativeSchemeExampleRequest> schemeExampleRequests = handleImageExampleRequest(request, copyWritingList);
        if (CollectionUtil.isEmpty(schemeExampleRequests)) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_EXAMPLE_FAILURE, "参数异常！");
        }

        List<CreativeSchemeExampleDTO> result = imageExample(schemeExampleRequests);
        log.info("生成示例结束.....");
        return result;
    }

    /**
     * 生成文案示例
     *
     * @param request     创作方案需求请求
     * @param loginUserId 登录用户ID
     * @return 文案示例
     */
    private List<CopyWritingContentDTO> copyWritingExample(CreativeSchemeReqVO request, Long loginUserId) {
        log.info("构建生成文案示例请求....");
        // 获取执行小红书的应用
        AppMarketRespVO executeApp = creativeAppManager.getExecuteApp(CreativeSchemeModeEnum.RANDOM_IMAGE_TEXT);
        // 构建小红书应用执行请求
        AppExecuteReqVO executeRequest = new AppExecuteReqVO();
        // 获取第二步的步骤。约定，生成小红书内容为第二步
        WorkflowStepWrapperRespVO stepWrapper = CreativeAppUtils.secondStep(executeApp);
        executeRequest.setUserId(loginUserId);
        executeRequest.setMode(AppModelEnum.COMPLETION.name());
        executeRequest.setScene(AppSceneEnum.XHS_WRITING.name());
        executeRequest.setAppUid(executeApp.getUid());
        executeRequest.setStepId(stepWrapper.getField());
        executeRequest.setN(3);
        executeRequest.setAiModel(ModelTypeEnum.GPT_3_5_TURBO_16K.getName());
        executeRequest.setAppReqVO(CreativeAppUtils.transform(executeApp, request, stepWrapper.getField()));
        log.info("生成文案示开始....");
        String answer = creativeAppManager.execute(executeRequest);
        List<XhsAppExecuteResponse> responses = CreativeAppUtils.handleAnswer(answer, executeRequest.getAppUid(), executeRequest.getN());
        if (CollectionUtil.isEmpty(responses)) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_EXAMPLE_FAILURE);
        }
        // 一条失败，全部失败
        List<CopyWritingContentDTO> resultList = Lists.newArrayList();
        for (XhsAppExecuteResponse response : responses) {
            if (!response.getSuccess() || Objects.isNull(response.getCopyWriting()) || StringUtils.isBlank(response.getCopyWriting().getTitle()) || StringUtils.isBlank(response.getCopyWriting().getContent())) {
                log.error("生成文案示例失败！{}", response.getErrorMsg());
                throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_EXAMPLE_FAILURE, response.getErrorMsg());
            }
            resultList.add(response.getCopyWriting());
        }
        log.info("生成文案示例成功！");
        return resultList;
    }

    /**
     * 图片示例
     *
     * @param schemeExampleRequests 请求
     * @return 示例
     */
    private List<CreativeSchemeExampleDTO> imageExample(List<CreativeSchemeExampleRequest> schemeExampleRequests) {
        log.info("开始生成图片示例....");
        List<Throwable> exceptions = Lists.newArrayList();
        List<CompletableFuture<CreativeSchemeExampleDTO>> futures = Lists.newArrayList();
        ThreadPoolExecutor executor = creativeImageCreativeThreadPoolHolder.executor();
        for (CreativeSchemeExampleRequest schemeExampleRequest : schemeExampleRequests) {
            CompletableFuture<CreativeSchemeExampleDTO> future = CompletableFuture.
                    supplyAsync(() -> singleImageExample(schemeExampleRequest), executor)
                    .exceptionally(throwable -> {
                        log.error("生成图片示例异常！", throwable);
                        exceptions.add(throwable);
                        return null;
                    });
            futures.add(future);
        }

        // 合并任务
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        // 等待任务完成
        try {
            allOf.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("生成图片示例异常！", e);
        }
        if (!CollectionUtil.isEmpty(exceptions)) {
            String message = exceptions.get(0).getMessage();
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_EXAMPLE_FAILURE, message);
        }

        List<CreativeSchemeExampleDTO> result = futures.stream().filter(Objects::nonNull).map(CompletableFuture::join).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(result)) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_EXAMPLE_FAILURE, "生成图片示例异常！");
        }
        log.info("生成图片示例结束....");
        return result;
    }

    /**
     * 单个图片示例
     *
     * @param schemeExampleRequest 请求
     * @return 结果
     */
    private CreativeSchemeExampleDTO singleImageExample(CreativeSchemeExampleRequest schemeExampleRequest) {
        log.info("开始生成单个图片示例....");
        // 生成图片示例
        XhsImageStyleExecuteRequest imageStyleRequest = schemeExampleRequest.getImageStyleRequest();
        XhsImageStyleExecuteResponse styleResponse = creativeImageManager.styleExecute(imageStyleRequest);
        // 校验图片示例
        if (Objects.isNull(styleResponse) || !styleResponse.getSuccess() ||
                CollectionUtil.isEmpty(styleResponse.getImageResponses())) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_EXAMPLE_FAILURE, styleResponse.getErrorMessage());
        }
        // 组装返回结果
        List<XhsImageExecuteResponse> imageResponses = styleResponse.getImageResponses();
        List<CreativeImageDTO> imageList = imageResponses.stream()
                .map(item -> {
                    CreativeImageDTO image = new CreativeImageDTO();
                    image.setIndex(item.getIndex());
                    image.setIsMain(item.getIsMain());
                    image.setUrl(item.getUrl());
                    return image;
                }).collect(Collectors.toList());
        CreativeSchemeExampleDTO example = new CreativeSchemeExampleDTO();
        example.setCopyWriting(schemeExampleRequest.getCopyWriting());
        example.setImages(imageList);
        log.info("生成单个图片示例结束....");
        return example;
    }

    /**
     * 处理图片示例请求
     *
     * @param request         创作方案请求对象
     * @param copyWritingList 文案示例列表
     * @return 图片示例请求
     */
    private List<CreativeSchemeExampleRequest> handleImageExampleRequest(CreativeSchemeReqVO request, List<CopyWritingContentDTO> copyWritingList) {
        log.info("处理图片参数开始...");
        // 图片素材列表
        List<String> imageUrlList = request.getUseImages();
        // 随机打散图片素材列表
        List<String> disperseImageUrlList = CreativeImageUtils.disperseImageUrlList(imageUrlList, copyWritingList.size());
        // Poster海报模板 Map
        Map<String, CreativeImageTemplateDTO> posterMap = creativeImageManager.mapTemplate();
        // 图片模板风格列表
        List<CreativeImageStyleDTO> styleList = Optional.ofNullable(request.getConfiguration()).map(CreativeSchemeConfigDTO::getImageTemplate).map(CreativeSchemeImageTemplateDTO::getStyleList).orElseThrow(() -> ServiceExceptionUtil.exception(CreativeErrorCodeConstants.STYLE_IMAGE_TEMPLATE_NOT_EMPTY));

        List<CreativeSchemeExampleRequest> resultList = Lists.newArrayList();

        for (int i = 0; i < copyWritingList.size(); i++) {
            // 随机获取一个图片样式
            CreativeImageStyleDTO creativeImageStyle = styleList.get(RandomUtil.randomInt(styleList.size()));
            List<CreativeImageTemplateDTO> templateList = creativeImageStyle.getTemplateList();
            if (CollectionUtil.isEmpty(templateList)) {
                throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_IMAGE_TEMPLATE_STYLE_TEMPLATE_LIST_NOT_EMPTY);
            }

            CreativeSchemeExampleRequest exampleRequest = new CreativeSchemeExampleRequest();
            // 获取文案信息
            CopyWritingContentDTO copyWriting = copyWritingList.get(i);
            exampleRequest.setCopyWriting(copyWriting);
            // 图片模板风格执行请求
            XhsImageStyleExecuteRequest imageStyleExecuteRequest = new XhsImageStyleExecuteRequest();
            List<XhsImageExecuteRequest> imageExecuteRequests = Lists.newArrayList();

            for (int j = 0; j < templateList.size(); j++) {
                // 根据模板ID获取海报模板以及参数信息
                CreativeImageTemplateDTO template = templateList.get(j);
                CreativeImageTemplateDTO posterTemplate = CreativeImageUtils.mergeTemplate(template, posterMap);

                // 海报模板参数构建
                List<VariableItemDTO> variables = CollectionUtil.emptyIfNull(posterTemplate.getVariables());
                Map<String, Object> params = Maps.newHashMap();
                List<String> imageParamList = Lists.newArrayList();
                // 获取第主图模板的参数
                if (j == 0) {
                    List<VariableItemDTO> mainImageVariableList = CreativeImageUtils.imageTypeVariableList(variables);
                    for (int k = 0; k < mainImageVariableList.size(); k++) {
                        VariableItemDTO variableItem = mainImageVariableList.get(k);
                        if (k == 0) {
                            String imageUrl = disperseImageUrlList.get(i);
                            params.put(variableItem.getField(), imageUrl);
                            imageParamList.add(imageUrl);
                        } else {
                            params.put(variableItem.getField(), CreativeImageUtils.randomImage(imageParamList, imageUrlList, mainImageVariableList.size()));
                        }
                    }
                    List<VariableItemDTO> mainOtherVariableList = CreativeImageUtils.otherTypeVariableList(variables);
                    for (VariableItemDTO variableItem : mainOtherVariableList) {
                        if ("TEXT".equals(variableItem.getType())) {
                            if (Objects.isNull(variableItem.getValue()) ||
                                    ((variableItem.getValue() instanceof String) && StringUtils.isBlank((String) variableItem.getValue()))) {
                                if ("TITLE".equalsIgnoreCase(variableItem.getField())) {
                                    params.put(variableItem.getField(), Optional.ofNullable(copyWriting.getImgTitle()).orElse(StringUtils.EMPTY));
                                } else if ("SUB_TITLE".equalsIgnoreCase(variableItem.getField())) {
                                    params.put(variableItem.getField(), Optional.ofNullable(copyWriting.getImgSubTitle()).orElse(StringUtils.EMPTY));
                                } else {
                                    params.put(variableItem.getField(), StringUtils.EMPTY);
                                }
                            } else {
                                params.put(variableItem.getField(), Optional.ofNullable(variableItem.getValue()).orElse(StringUtils.EMPTY));
                            }
                        } else {
                            params.put(variableItem.getField(), Optional.ofNullable(variableItem.getValue()).orElse(StringUtils.EMPTY));
                        }
                    }
                } else {
                    List<VariableItemDTO> imageVariableList = CreativeImageUtils.imageTypeVariableList(variables);
                    for (VariableItemDTO variableItem : variables) {
                        if ("IMAGE".equals(variableItem.getType())) {
                            params.put(variableItem.getField(), CreativeImageUtils.randomImage(imageParamList, imageUrlList, imageVariableList.size()));
                        } else {
                            params.put(variableItem.getField(), Optional.ofNullable(variableItem.getValue()).orElse(StringUtils.EMPTY));
                        }
                    }
                }
                // 构建图片执行请求
                XhsImageExecuteRequest imageExecuteRequest = new XhsImageExecuteRequest();
                imageExecuteRequest.setId(posterTemplate.getId());
                imageExecuteRequest.setName(posterTemplate.getName());
                imageExecuteRequest.setIndex(j + 1);
                imageExecuteRequest.setIsMain(j == 0);
                imageExecuteRequest.setParams(params);
                imageExecuteRequests.add(imageExecuteRequest);
            }

            imageStyleExecuteRequest.setId(creativeImageStyle.getId());
            imageStyleExecuteRequest.setName(creativeImageStyle.getName());
            imageStyleExecuteRequest.setImageRequests(imageExecuteRequests);
            exampleRequest.setImageStyleRequest(imageStyleExecuteRequest);
            resultList.add(exampleRequest);
        }
        log.info("处理图片示例参数结束....");
        return resultList;
    }

    /**
     * 生成一个复制名称的私有方法
     *
     * @param name 原始名称
     * @return 复制名称
     */
    private String getCopyName(String name) {
        String copyName = name + "-Copy";
        if (!creativeSchemeMapper.distinctName(copyName)) {
            return copyName;
        }
        return getCopyName(copyName);
    }

    /**
     * 处理请求并进行验证
     *
     * @param request 创作方案请求对象
     */
    private void handlerAndValidate(CreativeSchemeReqVO request) {
        request.validate();
        // 如果是普通用户或者为空，强制设置为用户类型
        if (UserUtils.isNotAdmin() || StringUtils.isBlank(request.getType())) {
            request.setType(CreativeSchemeTypeEnum.USER.name());
        }
        if (StringUtils.isBlank(request.getDescription())) {
            request.setDescription(StringUtils.EMPTY);
        }
    }
}
