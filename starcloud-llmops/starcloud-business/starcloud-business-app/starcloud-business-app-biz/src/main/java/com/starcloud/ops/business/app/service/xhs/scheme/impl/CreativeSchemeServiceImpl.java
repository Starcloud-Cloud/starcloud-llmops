package com.starcloud.ops.business.app.service.xhs.scheme.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.google.common.collect.Maps;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketListQuery;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.CreativeSchemeConfigurationDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeAppStepSchemeReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemeExampleReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemeListReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemeModifyReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemePageReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemeReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.GenerateOptionReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.CreativeSchemeListOptionRespVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.CreativeSchemeRespVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.CreativeSchemeTemplateGroupRespVO;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.convert.xhs.scheme.CreativeSchemeConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.scheme.CreativeSchemeDO;
import com.starcloud.ops.business.app.dal.mysql.xhs.scheme.CreativeSchemeMapper;
import com.starcloud.ops.business.app.domain.entity.AppMarketEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.workflow.action.MaterialActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.PosterActionHandler;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanSourceEnum;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeContentGenerateModelEnum;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeRefersSourceEnum;
import com.starcloud.ops.business.app.model.creative.CreativeOptionDTO;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.xhs.scheme.CreativeSchemeService;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.business.app.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
    private AppDictionaryService appDictionaryService;

    @Resource
    private AppMarketService appMarketService;

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
        metadata.put("generateMode", CreativeContentGenerateModelEnum.options());
        return metadata;
    }

    /**
     * 获取创作方案模板配置
     *
     * @return 创作方案配置
     */
    @Override
    public List<CreativeSchemeTemplateGroupRespVO> schemeTemplateList() {

//        // 查询符合条件的应用列表
//        List<AppMarketRespVO> appList = creativeAppManager.juzhenAppMarketplaceList();
//
//        // 查询分类列表，因为目前只有一级分类，所以直接使用一级分类
//        List<AppCategoryVO> appCategoryList = appDictionaryService.categoryList(Boolean.TRUE);
//
//        // 应用列表按照分类分组
//        Map<String, List<CreativeSchemeTemplateRespVO>> map = CollectionUtil.emptyIfNull(appList).stream().collect(Collectors.groupingBy(AppMarketRespVO::getCategory,
//                Collectors.mapping(item -> {
//                    // 获取所有步骤
//                    List<WorkflowStepWrapperRespVO> stepWrapperList = Optional.ofNullable(item.getWorkflowConfig())
//                            .map(WorkflowConfigRespVO::getSteps).orElseThrow(() -> ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_APP_STEPS_REQUIRED));
//
//                    // 构建创作方案步骤
//                    List<BaseSchemeStepDTO> schemeStepList = Lists.newArrayList();
//                    for (WorkflowStepWrapperRespVO stepWrapper : stepWrapperList) {
//                        BaseSchemeStepEntity schemeStep = SchemeStepFactory.factory(stepWrapper);
//                        schemeStepList.add(CreativeSchemeStepConvert.INSTANCE.convert(schemeStep));
//                    }
//
//                    // 获取到上传素材步骤
//                    MaterialSchemeStepDTO materialSchemeStep = CreativeUtils.getMaterialSchemeStep(schemeStepList);
//                    // 获取上传素材类型
//                    String materialType = Optional.ofNullable(materialSchemeStep).map(MaterialSchemeStepDTO::getMaterialType).orElse(null);
//                    // 组装创作方案模板信息
//                    CreativeSchemeTemplateRespVO creativeSchemeTemplate = new CreativeSchemeTemplateRespVO();
//                    creativeSchemeTemplate.setAppUid(item.getUid());
//                    creativeSchemeTemplate.setAppName(item.getName());
//                    creativeSchemeTemplate.setTags(item.getTags());
//                    creativeSchemeTemplate.setStepCount(stepWrapperList.size());
//                    creativeSchemeTemplate.setDescription(item.getDescription());
//                    creativeSchemeTemplate.setVersion(item.getVersion());
//                    creativeSchemeTemplate.setExample(item.getExample());
//                    creativeSchemeTemplate.setMaterialType(materialType);
//                    creativeSchemeTemplate.setSteps(schemeStepList);
//                    return creativeSchemeTemplate;
//                }, Collectors.toList())
//
//        ));
//
//        return CollectionUtil.emptyIfNull(appCategoryList).stream().map(item -> {
//            CreativeSchemeTemplateGroupRespVO response = new CreativeSchemeTemplateGroupRespVO();
//            response.setParentCode(item.getParentCode());
//            response.setCode(item.getCode());
//            response.setName(item.getName());
//            response.setTemplateList(CollectionUtil.emptyIfNull(map.get(item.getCode())));
//            return response;
//        }).collect(Collectors.toList());
        return Collections.emptyList();
    }

    /**
     * 获取创作方案详情
     *
     * @param uid 创作方案UID
     * @return 创作方案详情
     */
    @Override
    public CreativeSchemeRespVO get(String uid) {
        return get(uid, Boolean.FALSE);
    }

    /**
     * 获取创作方案详情
     *
     * @param uid             创作方案UID
     * @param isLatestExample 是否获取最新的示例
     * @return 创作方案详情
     */
    @Override
    public CreativeSchemeRespVO get(String uid, Boolean isLatestExample) {
        CreativeSchemeDO creativeScheme = creativeSchemeMapper.get(uid);
        AppValidate.notNull(creativeScheme, CreativeErrorCodeConstants.SCHEME_NOT_EXIST);
        CreativeSchemeRespVO schemeResponse = CreativeSchemeConvert.INSTANCE.convertResponse(creativeScheme);

        // 否则需要从应用中获取最新的示例
        CreativeSchemeConfigurationDTO configuration = schemeResponse.getConfiguration();
        // 合并海报信息
        // mergePoster(configuration);

        // 如果不需要获取最新的示例，则直接返回
        if (!isLatestExample) {
            schemeResponse.setConfiguration(configuration);
            return schemeResponse;
        }

        String appUid = configuration.getAppUid();
        AppMarketRespVO appMarketResponse = appMarketService.get(appUid);
        configuration.setExample(appMarketResponse.getExample());
        schemeResponse.setConfiguration(configuration);
        return schemeResponse;
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
     * 根据创作方案UID列表获取创作方案列表
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
//        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
//        if (Objects.isNull(loginUserId)) {
//            throw ServiceExceptionUtil.exception(ErrorCodeConstants.USER_MAY_NOT_LOGIN);
//        }
//        query.setLoginUserId(String.valueOf(loginUserId));
//        query.setIsAdmin(UserUtils.isAdmin());
//        List<CreativeSchemeRespVO> list = list(query);
//        return CollectionUtil.emptyIfNull(list).stream().map(item -> {
//            CreativeSchemeListOptionRespVO option = new CreativeSchemeListOptionRespVO();
//            List<BaseSchemeStepDTO> steps = item.getConfiguration().getSteps();
//
//            // 资料库类型字段获取
//            MaterialSchemeStepDTO materialSchemeStep = CreativeUtils.getMaterialSchemeStep(steps);
//            AppValidate.notNull(materialSchemeStep, "无法获取资料库类型步骤！");
//            MaterialTypeEnum materialType = MaterialTypeEnum.of(materialSchemeStep.getMaterialType());
//            AppValidate.notNull(materialType, "无法获取资料库类型！");
//
//            // 变量信息填充
//            VariableSchemeStepDTO variableSchemeStep = CreativeUtils.getVariableSchemeStep(steps);
//            if (variableSchemeStep != null) {
//                option.setVariableList(CollectionUtil.emptyIfNull(variableSchemeStep.getVariableList()));
//            } else {
//                option.setVariableList(Collections.emptyList());
//            }
//
//            option.setUid(item.getUid());
//            option.setName(item.getName());
//            option.setMaterialType(materialType.getTypeCode());
//            option.setMaterialTypeName(materialType.getDesc());
//            option.setDescription(item.getDescription());
//            option.setCreateTime(item.getCreateTime());
//            option.setTags(item.getTags());
//            return option;
//        }).collect(Collectors.toList());
        return Collections.emptyList();
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

        PageResult<CreativeSchemeRespVO> pageResult = new PageResult<>();
        if (CollectionUtil.isEmpty(page.getRecords())) {
            return new PageResult<>(Collections.emptyList(), page.getTotal());
        }

        List<CreativeSchemeDO> records = page.getRecords();

        // 用户创建者ID列表。
        List<Long> creatorList = records.stream().map(item -> Long.valueOf(item.getCreator())).distinct().collect(Collectors.toList());
        // 获取用户创建者ID，昵称 Map。
        Map<Long, String> creatorMap = UserUtils.getUserNicknameMapByIds(creatorList);

        // 用户更新者ID列表。
        List<Long> updaterList = records.stream().map(item -> Long.valueOf(item.getUpdater())).distinct().collect(Collectors.toList());
        // 获取用户更新者ID，昵称 Map。
        Map<Long, String> updaterMap = UserUtils.getUserNicknameMapByIds(updaterList);

        // 获取到所有的创作应用UID列表
        List<String> appUidList = records.stream().map(item -> {
            if (StringUtils.isNotBlank(item.getConfiguration())) {
                CreativeSchemeConfigurationDTO configuration = JsonUtils.parseObject(item.getConfiguration(), CreativeSchemeConfigurationDTO.class);
                if (configuration != null) {
                    return configuration.getAppUid();
                }
            }
            return null;
        }).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        AppMarketListQuery marketQuery = new AppMarketListQuery();
        marketQuery.setUidList(appUidList);
        List<AppMarketRespVO> list = appMarketService.list(marketQuery);
        Map<String, AppMarketRespVO> appMap = list.stream().collect(Collectors.toMap(AppMarketRespVO::getUid, item -> item));

        List<CreativeSchemeRespVO> collect = records.stream().map(item -> {
            CreativeSchemeRespVO response = CreativeSchemeConvert.INSTANCE.convertResponse(item);
            response.setCreator(creatorMap.get(Long.valueOf(item.getCreator())));
            response.setUpdater(updaterMap.get(Long.valueOf(item.getUpdater())));
            response.setAppName(appMap.get(response.getConfiguration().getAppUid()).getName());
            return response;
        }).collect(Collectors.toList());

        pageResult.setTotal(page.getTotal());
        pageResult.setList(collect);
        return pageResult;
    }

    /**
     * 创建创作方案
     *
     * @param request 创作方案请求
     */
    @Override
    public String create(CreativeSchemeReqVO request) {
        handlerAndValidate(request);
        if (creativeSchemeMapper.distinctName(request.getName())) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.SCHEME_NAME_EXIST);
        }
        CreativeSchemeDO scheme = CreativeSchemeConvert.INSTANCE.convertCreateRequest(request);
        scheme.setCreateTime(LocalDateTime.now());
        creativeSchemeMapper.insert(scheme);
        // 返回创作方案UID
        return scheme.getUid();
    }

    /**
     * 复制创作方案
     *
     * @param request 请求
     */
    @Override
    public String copy(UidRequest request) {
        CreativeSchemeDO creativeScheme = creativeSchemeMapper.get(request.getUid());
        AppValidate.notNull(creativeScheme, CreativeErrorCodeConstants.SCHEME_NOT_EXIST);

        CreativeSchemeDO scheme = new CreativeSchemeDO();
        scheme.setUid(IdUtil.simpleUUID());
        scheme.setName(getCopyName(creativeScheme.getName()));
        scheme.setType(creativeScheme.getType());
        scheme.setCategory(creativeScheme.getCategory());
        scheme.setTags(creativeScheme.getTags());
        scheme.setDescription(creativeScheme.getDescription());
        scheme.setConfiguration(creativeScheme.getConfiguration());
        scheme.setUseImages(creativeScheme.getUseImages());
        scheme.setCreateTime(LocalDateTime.now());
        scheme.setUpdateTime(LocalDateTime.now());
        scheme.setDeleted(Boolean.FALSE);
        creativeSchemeMapper.insert(scheme);
        // 返回创作方案UID
        return scheme.getUid();
    }

    /**
     * 修改创作方案
     *
     * @param request 创作方案请求
     */
    @Override
    public String modify(CreativeSchemeModifyReqVO request) {
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
        // 返回创作方案UID
        return scheme.getUid();
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
     * 获取创作方案选项
     *
     * @param stepSchemeReqVO 应用UID
     * @return 创作方案选项
     */
    @Override
    public List<CreativeOptionDTO> options(CreativeAppStepSchemeReqVO stepSchemeReqVO) {

        AppMarketEntity appMarketEntity;

        if (ObjectUtil.isNotNull(stepSchemeReqVO.getAppReqVO())) {

            appMarketEntity = AppFactory.factoryMarket(stepSchemeReqVO.getAppUid(), stepSchemeReqVO.getAppReqVO());
        } else {

            appMarketEntity = AppFactory.factoryMarket(stepSchemeReqVO.getAppUid());
        }


        return this.workflowStepOptions(appMarketEntity, stepSchemeReqVO.getStepCode());
    }

    @Override
    public List<CreativeOptionDTO> newOptions(GenerateOptionReqVO reqVO) {
        AppMarketEntity marketEntity = AppMarketConvert.INSTANCE.convert(reqVO.getAppReqVO());

        List<WorkflowStepWrapper> workflowStepWrappers = Optional.ofNullable(marketEntity.getWorkflowConfig().getSteps()).orElse(new ArrayList<>());

        List<CreativeOptionDTO> result = new ArrayList<>(workflowStepWrappers.size());
        for (WorkflowStepWrapper stepWrapper : workflowStepWrappers) {
            String stepCode = stepWrapper.getStepCode();
            String desc = stepWrapper.getDescription();
            String stepHandler = stepWrapper.getHandler();

            CreativeOptionDTO stepOption = new CreativeOptionDTO();
            stepOption.setName(stepCode);
            stepOption.setDescription(desc);
            stepOption.setCode(stepCode);
            stepOption.setStepHandler(stepHandler);

            JsonSchema intJsonNode = stepWrapper.getInVariableJsonSchema();
            stepOption.setInJsonSchema(JsonSchemaUtils.jsonNode2Str(intJsonNode));

            if (stepCode.equals(reqVO.getStepCode())) {
                if (PosterActionHandler.class.getSimpleName().equalsIgnoreCase(stepWrapper.getFlowStep().getHandler())) {
                    JsonSchema outJsonNode = stepWrapper.getOutVariableJsonSchema();
                    stepOption.setOutJsonSchema(JsonSchemaUtils.jsonNode2Str(outJsonNode));
                    stepOption.setCurrentStep(true);
                    result.add(stepOption);
                }
                return result;
            }

            if (MaterialActionHandler.class.getSimpleName().equalsIgnoreCase(stepWrapper.getFlowStep().getHandler())) {
                // 素材表头jsonschema 从素材库单独计算
                String uid;
                if (CreativePlanSourceEnum.isApp(reqVO.getSource())) {
                    uid = marketEntity.getUid();
                } else {
                    uid = reqVO.getPlanUid();
                }
                JsonSchema outJsonNode = JsonSchemaUtils.getOutVariableJsonSchema(uid);
                stepOption.setOutJsonSchema(JsonSchemaUtils.jsonNode2Str(outJsonNode));
                result.add(stepOption);
            } else {
                JsonSchema outJsonNode = stepWrapper.getOutVariableJsonSchema();
                stepOption.setOutJsonSchema(JsonSchemaUtils.jsonNode2Str(outJsonNode));
                result.add(stepOption);
            }
        }
        return result;
    }

    /**
     * 创建文案示例
     *
     * @param request 创作方案需求请求
     */
    @Override
    public void example(CreativeSchemeExampleReqVO request) {

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

    private void mergePoster(CreativeSchemeConfigurationDTO configuration) {
//        // 获取到步骤列表
//        List<BaseSchemeStepDTO> schemeStepList = CollectionUtil.emptyIfNull(configuration.getSteps());
//        PosterSchemeStepDTO posterSchemeStep = CreativeUtils.getPosterSchemeStep(schemeStepList);
//        if (Objects.isNull(posterSchemeStep)) {
//            return;
//        }
//        // 查询最新的海报模板
//        Map<String, PosterTemplateDTO> templateMap = creativeImageManager.mapPosterTemplate();
//        // 获取到海报样式集合
//        List<PosterStyleDTO> styleList = CollectionUtil.emptyIfNull(posterSchemeStep.getStyleList());
//        List<PosterStyleDTO> posterStyleList = Lists.newArrayList();
//
//        for (PosterStyleDTO posterStyle : styleList) {
//            PosterStyleDTO style = SerializationUtils.clone(posterStyle);
//
//            // 处理海报模板
//            List<PosterTemplateDTO> templateList = Lists.newArrayList();
//            List<PosterTemplateDTO> posterTemplateList = CollectionUtil.emptyIfNull(style.getTemplateList());
//
//            for (PosterTemplateDTO posterTemplate : posterTemplateList) {
//
//                // 如果最新的海报模板不存在，则跳过，从现有的列表中删除。
//                if (!templateMap.containsKey(posterTemplate.getUuid()) || Objects.isNull(templateMap.get(posterTemplate.getUuid()))) {
//                    continue;
//                }
//                // 进行海报模板参数的合并
//                PosterTemplateDTO template = templateMap.get(posterTemplate.getUuid());
//                PosterTemplateDTO mergeTemplate = CreativeUtils.mergePosterTemplate(posterTemplate, template);
//                templateList.add(mergeTemplate);
//            }
//
//            // 替换海报模板
//            posterStyle.setTemplateList(templateList);
//            posterStyleList.add(posterStyle);
//        }
//
//        posterSchemeStep.setStyleList(posterStyleList);
//        schemeStepList.set(schemeStepList.indexOf(posterSchemeStep), posterSchemeStep);
//        configuration.setSteps(schemeStepList);
    }

    /**
     * 直接遍历所有节点，每个节点返回入参和出参的两种结构
     * 只返回当前stepCode 上游的节点参数
     *
     * @param appMarketEntity 应用市场应用
     */
    protected List<CreativeOptionDTO> workflowStepOptions(AppMarketEntity appMarketEntity, String currentStepCode) {
        List<WorkflowStepWrapper> workflowStepWrappers = Optional.ofNullable(appMarketEntity.getWorkflowConfig().getSteps()).orElse(new ArrayList<>());

        List<CreativeOptionDTO> result = new ArrayList<>(workflowStepWrappers.size());
        for (WorkflowStepWrapper stepWrapper : workflowStepWrappers) {
            String stepCode = stepWrapper.getStepCode();
            String desc = stepWrapper.getDescription();

            CreativeOptionDTO stepOption = new CreativeOptionDTO();
            stepOption.setName(stepCode);
            stepOption.setDescription(desc);
            stepOption.setCode(stepCode);

            JsonSchema intJsonNode = stepWrapper.getInVariableJsonSchema();
            stepOption.setInJsonSchema(JsonSchemaUtils.jsonNode2Str(intJsonNode));

            if (stepCode.equals(currentStepCode)) {
                if (PosterActionHandler.class.getSimpleName().equalsIgnoreCase(stepWrapper.getFlowStep().getHandler())) {
                    JsonSchema outJsonNode = stepWrapper.getOutVariableJsonSchema();
                    stepOption.setOutJsonSchema(JsonSchemaUtils.jsonNode2Str(outJsonNode));
                    stepOption.setCurrentStep(true);
                    result.add(stepOption);
                }
                return result;
            }

            JsonSchema outJsonNode = stepWrapper.getOutVariableJsonSchema();
            stepOption.setOutJsonSchema(JsonSchemaUtils.jsonNode2Str(outJsonNode));
            result.add(stepOption);

        }
        return result;
    }

    /**
     * 处理请求并进行验证
     *
     * @param request 创作方案请求对象
     */
    @SuppressWarnings("all")
    private void handlerAndValidate(CreativeSchemeReqVO request) {
//        request.validate();
//        // 如果是普通用户或者为空，强制设置为用户类型
//        if (UserUtils.isNotAdmin() || StringUtils.isBlank(request.getType())) {
//            request.setType(CreativeSchemeTypeEnum.USER.name());
//        }
//        if (StringUtils.isBlank(request.getDescription())) {
//            request.setDescription(StringUtils.EMPTY);
//        }
//
//        // 处理创作方案配置
//        CreativeSchemeConfigurationDTO configuration = request.getConfiguration();
//
//        // 资料库步骤校验
//        MaterialSchemeStepDTO materialSchemeStep = CreativeUtils.getMaterialSchemeStep(CollectionUtil.emptyIfNull(configuration.getSteps()));
//        AppValidate.notNull(materialSchemeStep, "创作模版配置异常，资料库步骤是必须的！请联系管理员！");
//        // 获取到具体的资料库类型枚举
//        MaterialTypeEnum materialType = MaterialTypeEnum.of(materialSchemeStep.getMaterialType());
//        AppValidate.notNull(materialType, "资料库类型不支持，请联系管理员{}！", materialSchemeStep.getMaterialType());
//        // 获取资料库的具体处理器
//        AbstractMaterialHandler materialHandler = materialHandlerHolder.getHandler(materialType.getTypeCode());
//        AppValidate.notNull(materialHandler, "资料库类型不支持，请联系管理员{}！", materialType.getTypeCode());
//
//        // 处理海报信息，填充必要的信息
//        List<BaseSchemeStepDTO> steps = CollectionUtil.emptyIfNull(configuration.getSteps())
//                .stream()
//                .map(item -> {
//                    if (!PosterActionHandler.class.getSimpleName().equals(item.getCode())) {
//                        return item;
//                    }
//                    PosterSchemeStepDTO posterStep = (PosterSchemeStepDTO) item;
//                    List<PosterStyleDTO> posterStyleList = CollectionUtil.emptyIfNull(posterStep.getStyleList());
//                    // 校验海报样式
//                    posterStyleList.forEach(materialHandler::validatePosterStyle);
//                    // 处理海报样式
//                    posterStep.setStyleList(CreativeUtils.preHandlerPosterStyleList(posterStep.getStyleList()));
//                    return posterStep;
//                })
//                .collect(Collectors.toList());
//
//        configuration.setSteps(steps);
//        request.setConfiguration(configuration);
    }
}
