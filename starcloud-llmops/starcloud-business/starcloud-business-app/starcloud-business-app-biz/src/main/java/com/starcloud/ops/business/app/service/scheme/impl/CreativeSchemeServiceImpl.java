package com.starcloud.ops.business.app.service.scheme.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Maps;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.scheme.dto.CopyWritingContentDTO;
import com.starcloud.ops.business.app.api.scheme.dto.CreativeSchemeConfigDTO;
import com.starcloud.ops.business.app.api.scheme.dto.CreativeSchemeCopyWritingTemplateDTO;
import com.starcloud.ops.business.app.api.scheme.dto.CreativeSchemeImageTemplateDTO;
import com.starcloud.ops.business.app.api.scheme.dto.ImageExampleDTO;
import com.starcloud.ops.business.app.api.scheme.vo.request.CreativeSchemeListReqVO;
import com.starcloud.ops.business.app.api.scheme.vo.request.CreativeSchemeModifyReqVO;
import com.starcloud.ops.business.app.api.scheme.vo.request.CreativeSchemePageReqVO;
import com.starcloud.ops.business.app.api.scheme.vo.request.CreativeSchemeReqVO;
import com.starcloud.ops.business.app.api.scheme.vo.response.CreativeSchemeListOptionRespVO;
import com.starcloud.ops.business.app.api.scheme.vo.response.CreativeSchemeRespVO;
import com.starcloud.ops.business.app.api.xhs.XhsImageStyleDTO;
import com.starcloud.ops.business.app.api.xhs.XhsImageTemplateDTO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.scheme.vo.CreativeSchemeSseReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppExecuteResponse;
import com.starcloud.ops.business.app.convert.scheme.CreativeSchemeConvert;
import com.starcloud.ops.business.app.dal.databoject.scheme.CreativeSchemeDO;
import com.starcloud.ops.business.app.dal.mysql.scheme.CreativeSchemeMapper;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.plan.CreativeTypeEnum;
import com.starcloud.ops.business.app.enums.scheme.CreativeSchemeRefersSourceEnum;
import com.starcloud.ops.business.app.enums.scheme.CreativeSchemeTypeEnum;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.business.app.service.scheme.CreativeSchemeService;
import com.starcloud.ops.business.app.service.xhs.XhsService;
import com.starcloud.ops.business.app.util.CreativeUtil;
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
import java.util.stream.Collectors;

import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.WORKFLOW_CONFIG_FAILURE;

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
    private XhsService xhsService;

    @Resource
    private AppService appService;

    @Resource
    private AppDictionaryService appDictionaryService;

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
        AppValidate.notNull(creativeScheme, ErrorCodeConstants.CREATIVE_SCHEME_NOT_EXIST);
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
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_SCHEME_NAME_EXIST);
        }
        CreativeSchemeDO scheme = CreativeSchemeConvert.INSTANCE.convertCreateRequest(request);
        handlerScheme(scheme, request);
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
        AppValidate.notNull(creativeScheme, ErrorCodeConstants.CREATIVE_SCHEME_NOT_EXIST);

        CreativeSchemeDO scheme = new CreativeSchemeDO();
        scheme.setUid(IdUtil.simpleUUID());
        scheme.setName(getCopyName(creativeScheme.getName()));
        scheme.setType(creativeScheme.getType());
        scheme.setCategory(creativeScheme.getCategory());
        scheme.setTags(creativeScheme.getTags());
        scheme.setDescription(creativeScheme.getDescription());
        scheme.setRefers(creativeScheme.getRefers());
        scheme.setConfiguration(creativeScheme.getConfiguration());
        scheme.setCopyWritingExample(creativeScheme.getCopyWritingExample());
        scheme.setImageExample(creativeScheme.getImageExample());
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
        AppValidate.notNull(creativeScheme, ErrorCodeConstants.CREATIVE_SCHEME_NOT_EXIST);
        // 如果修改了名称，校验名称是否重复
        if (!creativeScheme.getName().equals(request.getName()) && creativeSchemeMapper.distinctName(request.getName())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_SCHEME_NAME_EXIST);
        }
        CreativeSchemeDO scheme = CreativeSchemeConvert.INSTANCE.convertModifyRequest(request);
        scheme.setId(creativeScheme.getId());
        handlerScheme(scheme, request);
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
        AppValidate.notNull(creativeScheme, ErrorCodeConstants.CREATIVE_SCHEME_NOT_EXIST);
        creativeSchemeMapper.deleteById(creativeScheme.getId());
    }

    /**
     * 分析生成要求
     *
     * @param request 创作方案需求请求
     */
    @Override
    public void summary(CreativeSchemeSseReqVO request) {
        AppMarketRespVO executeApp = xhsService.getExecuteApp(CreativeTypeEnum.XHS.name());
        AppExecuteReqVO executeRequest = new AppExecuteReqVO();
        if (Objects.nonNull(request.getSseEmitter())) {
            executeRequest.setSseEmitter(request.getSseEmitter());
        }
        executeRequest.setUserId(SecurityFrameworkUtils.getLoginUserId());
        executeRequest.setMode(AppModelEnum.COMPLETION.name());
        executeRequest.setScene(AppSceneEnum.XHS_WRITING.name());
        executeRequest.setAppUid(executeApp.getUid());
        executeRequest.setN(1);
//        executeRequest.setAiModel(ModelTypeEnum.GPT_4_TURBO.getName());
        executeRequest.setAppReqVO(CreativeUtil.transform(executeApp, request));
        appService.asyncExecute(executeRequest);
    }

    /**
     * 创建文案示例
     *
     * @param request 创作方案需求请求
     * @return 文案示例
     */
    @Override
    public List<CopyWritingContentDTO> example(CreativeSchemeReqVO request) {
        handlerAndValidate(request);
        AppMarketRespVO executeApp = xhsService.getExecuteApp(CreativeTypeEnum.XHS.name());
        AppExecuteReqVO executeRequest = new AppExecuteReqVO();
        List<WorkflowStepWrapperRespVO> stepWrapperList = Optional.ofNullable(executeApp).map(AppMarketRespVO::getWorkflowConfig).map(WorkflowConfigRespVO::getSteps)
                .orElseThrow(() -> ServiceExceptionUtil.exception(WORKFLOW_CONFIG_FAILURE));
        // 获取第二步的步骤。约定，生成小红书内容为第二步
        WorkflowStepWrapperRespVO stepWrapper = stepWrapperList.get(1);
        AppValidate.notNull(stepWrapper, ErrorCodeConstants.WORKFLOW_STEP_NOT_EXIST, executeApp.getName());
        executeRequest.setUserId(SecurityFrameworkUtils.getLoginUserId());
        executeRequest.setMode(AppModelEnum.COMPLETION.name());
        executeRequest.setScene(AppSceneEnum.XHS_WRITING.name());
        executeRequest.setAppUid(executeApp.getUid());
        executeRequest.setStepId(stepWrapper.getField());
        executeRequest.setN(5);
        executeRequest.setAiModel(ModelTypeEnum.GPT_3_5_TURBO_16K.getName());
        executeRequest.setAppReqVO(CreativeUtil.transform(executeApp, request));

        String answer = xhsService.execute(executeRequest);
        List<XhsAppExecuteResponse> responses = CreativeUtil.handleAnswer(answer, executeRequest.getAppUid(), executeRequest.getN());
        if (CollectionUtil.isEmpty(responses)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_SCHEME_EXAMPLE_FAILURE);
        }

        String errorMsg = "";
        List<CopyWritingContentDTO> list = Lists.newArrayList();
        for (XhsAppExecuteResponse response : responses) {
            if (!response.getSuccess() ||
                    Objects.isNull(response.getCopyWriting()) ||
                    StringUtils.isBlank(response.getCopyWriting().getTitle()) ||
                    StringUtils.isBlank(response.getCopyWriting().getContent())) {
                errorMsg = response.getErrorMsg();
                continue;
            }
            list.add(response.getCopyWriting());
        }

        if (CollectionUtil.isEmpty(list)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_SCHEME_EXAMPLE_FAILURE, errorMsg);
        }
        return list;
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
        CreativeSchemeConfigDTO configuration = request.getConfiguration();
        if (Objects.isNull(configuration)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_SCHEME_CONFIGURATION_NOT_NULL, request.getName());
        }
        CreativeSchemeImageTemplateDTO imageTemplate = configuration.getImageTemplate();
        if (Objects.isNull(imageTemplate)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_SCHEME_IMAGE_TEMPLATE_NOT_NULL, request.getName());
        }
        List<XhsImageStyleDTO> styleList = imageTemplate.getStyleList();
        if (CollectionUtil.isEmpty(styleList)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_SCHEME_IMAGE_TEMPLATE_STYLE_LIST_NOT_EMPTY, request.getName());
        }

        CreativeSchemeCopyWritingTemplateDTO copyWritingTemplate = configuration.getCopyWritingTemplate();
        if (Objects.isNull(copyWritingTemplate)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_SCHEME_COPY_WRITING_TEMPLATE_NOT_NULL, request.getName());
        }
        if (StringUtils.isBlank(copyWritingTemplate.getSummary())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_SCHEME_COPY_WRITING_TEMPLATE_SUMMARY_NOT_NULL, request.getName());
        }
        if (StringUtils.isBlank(copyWritingTemplate.getDemand())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_SCHEME_COPY_WRITING_TEMPLATE_DEMAND_NOT_NULL, request.getName());
        }
    }

    /**
     * 处理创作方案配置
     *
     * @param scheme  创作方案
     * @param request 请求
     */
    private void handlerScheme(CreativeSchemeDO scheme, CreativeSchemeReqVO request) {

        CreativeSchemeConfigDTO configuration = request.getConfiguration();
        CreativeSchemeCopyWritingTemplateDTO copyWritingTemplate = configuration.getCopyWritingTemplate();
        // 设置创作方案的示例文案
        List<CopyWritingContentDTO> copyWritingExamples = Optional.ofNullable(copyWritingTemplate.getExample()).orElse(Lists.newArrayList());
        scheme.setCopyWritingExample(JSONUtil.toJsonStr(copyWritingExamples));

        CreativeSchemeImageTemplateDTO imageTemplate = configuration.getImageTemplate();
        List<XhsImageStyleDTO> list = imageTemplate.getStyleList();
        // 从字典中获取图片模板的示例图片
        List<XhsImageTemplateDTO> imageTemplates = xhsService.imageTemplates();
        Map<String, XhsImageTemplateDTO> templateMap = CollectionUtil.emptyIfNull(imageTemplates).stream().collect(Collectors.toMap(XhsImageTemplateDTO::getId, item -> item));

        List<ImageExampleDTO> imageExampleList = Lists.newArrayList();
        for (XhsImageStyleDTO style : list) {
            List<XhsImageTemplateDTO> templateList = style.getTemplateList();
            if (CollectionUtil.isEmpty(templateList)) {
                continue;
            }
            List<XhsImageTemplateDTO> imageTemplateList = Lists.newArrayList();
            for (XhsImageTemplateDTO template : templateList) {
                if (templateMap.containsKey(template.getId())) {
                    XhsImageTemplateDTO templateDTO = templateMap.get(template.getId());
                    if (StringUtils.isNotBlank(templateDTO.getExample())) {
                        XhsImageTemplateDTO xhsImageTemplateDTO = new XhsImageTemplateDTO();
                        xhsImageTemplateDTO.setId(templateDTO.getId());
                        xhsImageTemplateDTO.setName(templateDTO.getName());
                        xhsImageTemplateDTO.setExample(templateDTO.getExample());
                        imageTemplateList.add(xhsImageTemplateDTO);
                    }
                }
            }
            ImageExampleDTO imageExample = new ImageExampleDTO();
            imageExample.setId(style.getId());
            imageExample.setName(style.getName());
            imageExample.setTemplateList(imageTemplateList);
            imageExampleList.add(imageExample);
        }
        // 设置创作方案的示例图片
        scheme.setImageExample(JSONUtil.toJsonStr(imageExampleList));

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