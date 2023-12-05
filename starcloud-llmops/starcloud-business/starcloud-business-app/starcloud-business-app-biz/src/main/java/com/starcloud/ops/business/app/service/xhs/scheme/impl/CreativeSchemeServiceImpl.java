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
import com.starcloud.ops.business.app.enums.xhs.plan.CreativeTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeRefersSourceEnum;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeTypeEnum;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
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
import java.util.function.Function;
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

    /**
     * 获取图片模板
     *
     * @return 图片模板
     */
    @Override
    public List<CreativeImageTemplateDTO> templates() {
        return creativeImageManager.templates();
    }

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
            List<VariableItemDTO> variable = Optional.ofNullable(item.getConfiguration())
                    .map(CreativeSchemeConfigDTO::getCopyWritingTemplate)
                    .map(CreativeSchemeCopyWritingTemplateDTO::getVariables).orElse(Lists.newArrayList());
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
        AppMarketRespVO executeApp = creativeAppManager.getExecuteApp(CreativeTypeEnum.XHS.name());
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
        handlerAndValidate(request);
        if (CollectionUtil.isEmpty(request.getUseImages())) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_UPLOAD_IMAGE_EMPTY);
        }
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (Objects.isNull(loginUserId)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.USER_MAY_NOT_LOGIN);
        }
        AppMarketRespVO executeApp = creativeAppManager.getExecuteApp(CreativeTypeEnum.XHS.name());

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

        String answer = creativeAppManager.execute(executeRequest);
        List<XhsAppExecuteResponse> responses = CreativeAppUtils.handleAnswer(answer, executeRequest.getAppUid(), executeRequest.getN());
        if (CollectionUtil.isEmpty(responses)) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_EXAMPLE_FAILURE);
        }

        // 一条失败，全部失败
        for (XhsAppExecuteResponse response : responses) {
            if (!response.getSuccess() ||
                    Objects.isNull(response.getCopyWriting()) ||
                    StringUtils.isBlank(response.getCopyWriting().getTitle()) ||
                    StringUtils.isBlank(response.getCopyWriting().getContent())) {
                throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_EXAMPLE_FAILURE, response.getErrorMsg());
            }
        }

        // 图片素材列表
        List<String> imageUrlList = request.getUseImages();
        // 随机打散图片素材列表
        List<String> disperseImageUrlList = CreativeImageUtils.disperseImageUrlList(imageUrlList, executeRequest.getN());

        // 查询Poster模板列表，每一次都是获取最新的海报模板参数。避免海报模板修改无法感知。
        List<CreativeImageTemplateDTO> posterTemplateList = creativeImageManager.templates();
        // Poster模板Map
        Map<String, CreativeImageTemplateDTO> posterMap = CollectionUtil.emptyIfNull(posterTemplateList).stream()
                .collect(Collectors.toMap(CreativeImageTemplateDTO::getId, Function.identity()));

        CreativeSchemeConfigDTO configuration = request.getConfiguration();
        CreativeSchemeImageTemplateDTO imageTemplate = configuration.getImageTemplate();
        List<CreativeImageStyleDTO> styleList = imageTemplate.getStyleList();
        List<CreativeSchemeExampleDTO> resultList = Lists.newArrayList();
        for (int i = 0; i < responses.size(); i++) {
            CreativeSchemeExampleDTO example = new CreativeSchemeExampleDTO();
            CopyWritingContentDTO copyWriting = responses.get(i).getCopyWriting();
            // 随机获取一个图片样式
            int randomInt = RandomUtil.randomInt(styleList.size());
            CreativeImageStyleDTO creativeImageStyle = styleList.get(randomInt);

            List<CreativeImageTemplateDTO> templateList = creativeImageStyle.getTemplateList();
            if (CollectionUtil.isEmpty(templateList)) {
                continue;
            }
            XhsImageStyleExecuteRequest imageStyleExecuteRequest = new XhsImageStyleExecuteRequest();
            List<XhsImageExecuteRequest> imageExecuteRequests = Lists.newArrayList();

            for (int j = 0; j < templateList.size(); j++) {
                CreativeImageTemplateDTO template = templateList.get(j);
                if (!posterMap.containsKey(template.getId())) {
                    continue;
                }
                CreativeImageTemplateDTO posterTemplate = posterMap.get(template.getId());

                XhsImageExecuteRequest imageExecuteRequest = new XhsImageExecuteRequest();
                imageExecuteRequest.setId(posterTemplate.getId());
                imageExecuteRequest.setName(posterTemplate.getName());
                imageExecuteRequest.setIndex(j + 1);
                imageExecuteRequest.setIsMain(j == 0);

                Map<String, Object> params = Maps.newHashMap();
                List<String> imageParamList = Lists.newArrayList();
                // 获取第主图模板的参数
                if (j == 0) {
                    List<VariableItemDTO> mainImageVariableList = CollectionUtil.emptyIfNull(posterTemplate.getVariables()).stream().filter(item -> "IMAGE".equals(item.getStyle())).collect(Collectors.toList());
                    for (int k = 0; k < mainImageVariableList.size(); k++) {
                        VariableItemDTO variableItem = mainImageVariableList.get(k);
                        if (k == 0) {
                            String imageUrl = disperseImageUrlList.get(i);
                            params.put(variableItem.getField(), imageUrl);
                            imageParamList.add(imageUrl);
                        } else {
                            params.put(variableItem.getField(), CreativeImageUtils.randomImageList(imageParamList, imageUrlList, mainImageVariableList.size()));
                        }
                    }
                    List<VariableItemDTO> mainOtherVariableList = CollectionUtil.emptyIfNull(posterTemplate.getVariables()).stream().filter(item -> !"IMAGE".equals(item.getStyle())).collect(Collectors.toList());
                    for (VariableItemDTO variableItem : mainOtherVariableList) {
                        if ("TEXT".equals(variableItem.getStyle())) {
                            if ("TITLE".equalsIgnoreCase(variableItem.getField())) {
                                params.put(variableItem.getField(), copyWriting.getImgTitle());
                            } else if ("SUB_TITLE".equalsIgnoreCase(variableItem.getField())) {
                                params.put(variableItem.getField(), copyWriting.getImgSubTitle());
                            } else {
                                params.put(variableItem.getField(), variableItem.getValue());
                            }
                        } else {
                            params.put(variableItem.getField(), variableItem.getValue());
                        }
                    }
                } else {
                    List<VariableItemDTO> imageVariableList = CollectionUtil.emptyIfNull(posterTemplate.getVariables()).stream().filter(item -> "IMAGE".equals(item.getStyle())).collect(Collectors.toList());
                    for (VariableItemDTO variableItem : CollectionUtil.emptyIfNull(posterTemplate.getVariables())) {
                        if ("IMAGE".equals(variableItem.getStyle())) {
                            params.put(variableItem.getField(), CreativeImageUtils.randomImageList(imageParamList, imageUrlList, imageVariableList.size()));
                        } else if ("TEXT".equals(variableItem.getStyle())) {
                            if ("TITLE".equalsIgnoreCase(variableItem.getField())) {
                                params.put(variableItem.getField(), copyWriting.getImgTitle());
                            } else if ("SUB_TITLE".equalsIgnoreCase(variableItem.getField())) {
                                params.put(variableItem.getField(), copyWriting.getImgSubTitle());
                            } else {
                                params.put(variableItem.getField(), variableItem.getValue());
                            }
                        } else {
                            params.put(variableItem.getField(), variableItem.getValue());
                        }
                    }
                }
                imageExecuteRequest.setParams(params);
                imageExecuteRequests.add(imageExecuteRequest);
            }
            imageStyleExecuteRequest.setId(creativeImageStyle.getId());
            imageStyleExecuteRequest.setName(creativeImageStyle.getName());
            imageStyleExecuteRequest.setImageRequests(imageExecuteRequests);
            XhsImageStyleExecuteResponse styleResponse = creativeImageManager.styleExecute(imageStyleExecuteRequest);
            if (Objects.isNull(styleResponse) || CollectionUtil.isEmpty(styleResponse.getImageResponses())) {
                throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_EXAMPLE_FAILURE, "图片生成失败");
            }
            List<XhsImageExecuteResponse> imageResponses = styleResponse.getImageResponses();
            List<CreativeImageDTO> collect = imageResponses.stream().map(item -> {
                CreativeImageDTO image = new CreativeImageDTO();
                image.setIndex(item.getIndex());
                image.setIsMain(item.getIsMain());
                image.setUrl(item.getUrl());
                return image;
            }).collect(Collectors.toList());
            example.setCopyWriting(copyWriting);
            example.setImages(collect);
            resultList.add(example);
        }

        return resultList;
    }


    /**
     * 处理请求并进行验证
     *
     * @param request 创作方案请求对象
     */
    private void handlerAndValidate(CreativeSchemeReqVO request) {
        // 如果是普通用户或者为空，强制设置为用户类型
        if (UserUtils.isNotAdmin() || StringUtils.isBlank(request.getType())) {
            request.setType(CreativeSchemeTypeEnum.USER.name());
        }
        if (StringUtils.isBlank(request.getDescription())) {
            request.setDescription(StringUtils.EMPTY);
        }
        request.validate();
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
}
