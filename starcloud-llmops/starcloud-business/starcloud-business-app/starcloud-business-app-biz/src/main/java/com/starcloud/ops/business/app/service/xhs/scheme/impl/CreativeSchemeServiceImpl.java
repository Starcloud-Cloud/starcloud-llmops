package com.starcloud.ops.business.app.service.xhs.scheme.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.starcloud.ops.business.app.api.app.vo.params.JsonDataVO;
import com.starcloud.ops.business.app.api.app.vo.response.action.ActionResponseRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.action.WorkflowStepRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.category.vo.AppCategoryVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketListQuery;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentCreateReqVO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanExecuteDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeOptionDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.CreativeSchemeConfigurationDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.BaseSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.PosterSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.VariableSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterTemplateDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterVariableDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemeListReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemeModifyReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemePageReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemeReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.CreativeSchemeListOptionRespVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.CreativeSchemeRespVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.SchemeAppCategoryRespVO;
import com.starcloud.ops.business.app.convert.xhs.scheme.CreativeSchemeConvert;
import com.starcloud.ops.business.app.convert.xhs.scheme.CreativeSchemeStepConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.scheme.CreativeSchemeDO;
import com.starcloud.ops.business.app.dal.mysql.xhs.scheme.CreativeSchemeMapper;
import com.starcloud.ops.business.app.domain.entity.AppMarketEntity;
import com.starcloud.ops.business.app.domain.entity.BaseAppEntity;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.action.PosterActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.VariableActionHandler;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppStepResponseStyleEnum;
import com.starcloud.ops.business.app.enums.app.AppStepResponseTypeEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.CreativeOptionModelEnum;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.poster.PosterModeEnum;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeGenerateModeEnum;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeModeEnum;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeRefersSourceEnum;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeTypeEnum;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.service.xhs.manager.CreativeAppManager;
import com.starcloud.ops.business.app.service.xhs.manager.CreativeImageManager;
import com.starcloud.ops.business.app.service.xhs.scheme.CreativeSchemeService;
import com.starcloud.ops.business.app.service.xhs.scheme.entity.step.BaseSchemeStepEntity;
import com.starcloud.ops.business.app.service.xhs.scheme.factory.SchemeStepFactory;
import com.starcloud.ops.business.app.util.CreativeAppUtils;
import com.starcloud.ops.business.app.util.CreativeImageUtils;
import com.starcloud.ops.business.app.util.CreativeUtils;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.business.app.util.UserUtils;
import com.starcloud.ops.business.app.validate.AppValidate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private CreativeAppManager creativeAppManager;

    @Resource
    private AppDictionaryService appDictionaryService;

    @Resource
    private AppMarketService appMarketService;

    @Resource
    private CreativeContentService creativeContentService;

    @Resource
    private CreativeImageManager creativeImageManager;

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
        metadata.put("mode", CreativeSchemeModeEnum.options());
        metadata.put("generateMode", CreativeSchemeGenerateModeEnum.options());
        return metadata;
    }

    /**
     * 获取创作方案配置
     *
     * @return 创作方案配置
     */
    @Override
    public List<SchemeAppCategoryRespVO> appGroupList() {

        // 查询符合条件的应用列表
        List<AppMarketRespVO> appList = creativeAppManager.juzhenAppMarketplaceList();

        // 查询分类列表，因为目前只有一级分类，所以直接使用一级分类
        List<AppCategoryVO> appCategoryList = appDictionaryService.categoryList(Boolean.TRUE);

        // 应用列表按照分类分组
        Map<String, List<CreativeSchemeConfigurationDTO>> map = CollectionUtil.emptyIfNull(appList).stream().collect(Collectors.groupingBy(
                AppMarketRespVO::getCategory,
                Collectors.mapping(item -> {
                    // 获取所有步骤
                    List<WorkflowStepWrapperRespVO> stepWrapperList = Optional.ofNullable(item.getWorkflowConfig())
                            .map(WorkflowConfigRespVO::getSteps)
                            .orElseThrow(() -> ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_APP_STEPS_REQUIRED));

                    // 构建创作方案步骤
                    List<BaseSchemeStepDTO> schemeStepList = Lists.newArrayList();
                    for (WorkflowStepWrapperRespVO stepWrapper : stepWrapperList) {
                        BaseSchemeStepEntity schemeStep = SchemeStepFactory.factory(stepWrapper);
                        schemeStepList.add(CreativeSchemeStepConvert.INSTANCE.convert(schemeStep));
                    }

                    CreativeSchemeConfigurationDTO configuration = new CreativeSchemeConfigurationDTO();
                    configuration.setAppUid(item.getUid());
                    configuration.setAppName(item.getName());
                    configuration.setTags(item.getTags());
                    configuration.setStepCount(stepWrapperList.size());
                    configuration.setDescription(item.getDescription());
                    configuration.setVersion(item.getVersion());
                    configuration.setExample(item.getExample());
                    configuration.setSteps(schemeStepList);
                    return configuration;
                }, Collectors.toList())

        ));

        return CollectionUtil.emptyIfNull(appCategoryList).stream().map(item -> {
            SchemeAppCategoryRespVO response = new SchemeAppCategoryRespVO();
            response.setParentCode(item.getParentCode());
            response.setCode(item.getCode());
            response.setName(item.getName());
            response.setAppConfigurationList(CollectionUtil.emptyIfNull(map.get(item.getCode())));
            return response;
        }).collect(Collectors.toList());
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
        // 如果不需要获取最新的示例，则直接返回
        if (!isLatestExample) {
            return schemeResponse;
        }
        // 否则需要从应用中获取最新的示例
        CreativeSchemeConfigurationDTO configuration = schemeResponse.getConfiguration();
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
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (Objects.isNull(loginUserId)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.USER_MAY_NOT_LOGIN);
        }
        query.setLoginUserId(String.valueOf(loginUserId));
        query.setIsAdmin(UserUtils.isAdmin());
        List<CreativeSchemeRespVO> list = list(query);
        return CollectionUtil.emptyIfNull(list).stream().map(item -> {
            CreativeSchemeListOptionRespVO option = new CreativeSchemeListOptionRespVO();
            List<BaseSchemeStepDTO> steps = item.getConfiguration().getSteps();

            // 变量信息填充
            VariableSchemeStepDTO variableSchemeStep = CreativeUtils.getVariableSchemeStep(steps);
            if (variableSchemeStep != null) {
                option.setVariableList(CollectionUtil.emptyIfNull(variableSchemeStep.getVariableList()));
            } else {
                option.setVariableList(Collections.emptyList());
            }

            // 海报信息填充
            PosterSchemeStepDTO posterSchemeStep = CreativeUtils.getPosterSchemeStep(steps);
            if (posterSchemeStep != null) {
                option.setPosterMode(posterSchemeStep.getMode());
                Integer maxImageCount = Optional.ofNullable(posterSchemeStep.getStyleList())
                        .map(l -> l.get(0)).map(PosterStyleDTO::getMaxImageCount).orElse(0);
                option.setPosterImageCount(maxImageCount);
            } else {
                option.setPosterMode(PosterModeEnum.RANDOM.name());
                option.setPosterImageCount(0);
            }

            option.setUid(item.getUid());
            option.setName(item.getName());
            option.setMode(item.getMode());
            option.setDescription(item.getDescription());
            option.setCreateTime(item.getCreateTime());
            option.setTags(item.getTags());
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
                if (CreativeSchemeModeEnum.CUSTOM_IMAGE_TEXT.name().equalsIgnoreCase(item.getMode())) {
                    CreativeSchemeConfigurationDTO configuration = JsonUtils.parseObject(item.getConfiguration(), CreativeSchemeConfigurationDTO.class);
                    if (configuration != null) {
                        return configuration.getAppUid();
                    }
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
        scheme.setMode(creativeScheme.getMode());
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
     * @param appUid 应用UID
     * @return 创作方案选项
     */
    @Override
    public List<CreativeOptionDTO> options(String appUid) {
        List<CreativeOptionDTO> optionList = new ArrayList<>();

        //AppMarketRespVO appMarketResponse = appMarketService.get(appUid);

        //前端要传VO的
        AppMarketEntity appMarketEntity = AppFactory.factoryMarket(appUid);

        return this.workflowStepOptions(appMarketEntity);
//
//
//        // 判断应用类型是否为媒体矩阵
//        if (!AppTypeEnum.MEDIA_MATRIX.name().equals(appMarketResponse.getType())) {
//            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_TYPE_NONSUPPORT);
//        }
//        List<WorkflowStepWrapperRespVO> stepWrapperResponseList = Optional.ofNullable(appMarketResponse.getWorkflowConfig())
//                .map(WorkflowConfigRespVO::getSteps)
//                .orElseThrow(() -> ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_APP_STEPS_REQUIRED));
//
//        // 1.基本信息处理
//        List<CreativeOptionDTO> baseOptionList = new ArrayList<>();
//        Optional<WorkflowStepWrapperRespVO> variableStepWrapperOptional = stepWrapperResponseList.stream()
//                .filter(item -> VariableActionHandler.class.getSimpleName().equals(item.getFlowStep().getHandler()))
//                .findFirst();
//        if (variableStepWrapperOptional.isPresent()) {
//            WorkflowStepWrapperRespVO variableWrapper = variableStepWrapperOptional.get();
//            List<VariableItemRespVO> variables = CollectionUtil.emptyIfNull(variableWrapper.getVariable().getVariables());
//            for (VariableItemRespVO variableItem : variables) {
//                CreativeOptionDTO option = new CreativeOptionDTO();
//                option.setParentCode(CreativeOptionModelEnum.BASE_INFO.getPrefix());
//                option.setCode(variableItem.getField());
//                option.setName(variableItem.getLabel());
//                option.setType(JsonSchemaUtils.OBJECT);
//                option.setDescription(variableItem.getDescription());
//                option.setModel(CreativeOptionModelEnum.BASE_INFO.name());
//                option.setChildren(Collections.emptyList());
//                baseOptionList.add(option);
//            }
//
//            CreativeOptionDTO baseOption = new CreativeOptionDTO();
//            baseOption.setParentCode(JsonSchemaUtils.ROOT);
//            baseOption.setCode(CreativeOptionModelEnum.BASE_INFO.getPrefix());
//            baseOption.setName("基本信息");
//            baseOption.setType(JsonSchemaUtils.OBJECT);
//            baseOption.setDescription("基本信息");
//            baseOption.setModel(CreativeOptionModelEnum.BASE_INFO.name());
//            baseOption.setChildren(baseOptionList);
//            optionList.add(baseOption);
//        }
//
//        // 2.素材处理
//
//        // 3.步骤响应结果
//        List<CreativeOptionDTO> stepOptionList = new ArrayList<>();
//        for (WorkflowStepWrapperRespVO wrapper : stepWrapperResponseList) {
//            // 获取响应
//            ActionResponseRespVO actionResponse = Optional.ofNullable(wrapper)
//                    .map(WorkflowStepWrapperRespVO::getFlowStep)
//                    .map(WorkflowStepRespVO::getResponse)
//                    .orElseThrow(() -> ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_APP_CONFIG_REQUIRED));
//            // 如果不是 JSON 类型的响应，直接跳过
//            if (!AppStepResponseTypeEnum.JSON.name().equals(actionResponse.getType()) &&
//                    !AppStepResponseStyleEnum.JSON.name().equals(actionResponse.getStyle())) {
//                continue;
//            }
//            // 获取 JSON Schema
//            Optional<String> optional = Optional.ofNullable(actionResponse.getOutput())
//                    .map(JsonDataVO::getJsonSchema);
//            // 如果 JSON Schema 为空，直接跳过
//            if (!optional.isPresent() || StringUtils.isBlank(optional.get())) {
//                continue;
//            }
//            String jsonSchema = optional.get();
//            CreativeOptionDTO stepOption = JsonSchemaUtils.jsonSchemaToOptions(
//                    jsonSchema,
//                    wrapper.getField(),
//                    wrapper.getName(),
//                    wrapper.getDescription(),
//                    CreativeOptionModelEnum.STEP_RESPONSE.name()
//            );
//            stepOptionList.add(stepOption);
//        }
//        CreativeOptionDTO stepOption = new CreativeOptionDTO();
//        stepOption.setParentCode(JsonSchemaUtils.ROOT);
//        stepOption.setCode(CreativeOptionModelEnum.STEP_RESPONSE.getPrefix());
//        stepOption.setName("步骤响应结果");
//        stepOption.setType(JsonSchemaUtils.OBJECT);
//        stepOption.setDescription("步骤响应结果");
//        stepOption.setModel(CreativeOptionModelEnum.STEP_RESPONSE.name());
//        stepOption.setChildren(stepOptionList);
//        optionList.add(stepOption);
//
//        return optionList;
    }


    /**
     * 直接遍历所有节点，每个节点返回入参和出参的两种结构
     *
     * @param appMarketEntity
     */
    protected List<CreativeOptionDTO> workflowStepOptions(AppMarketEntity appMarketEntity) {

        List<CreativeOptionDTO> baseOptionList = new ArrayList<>();

        baseOptionList = Optional.ofNullable(appMarketEntity.getWorkflowConfig().getSteps()).orElse(new ArrayList<>()).stream().map((stepWrapper) -> {

            String stepCode = stepWrapper.getStepCode();
            String desc = stepWrapper.getDescription();

            JsonNode intJsonNode = stepWrapper.getInVariableJsonSchema();
            JsonNode outJsonNode = stepWrapper.getOutVariableJsonSchema();

            CreativeOptionDTO stepOption = new CreativeOptionDTO();
            stepOption.setName(stepCode);
            stepOption.setDescription(desc);
            stepOption.setCode(stepCode);

            stepOption.setInJsonSchema(JsonSchemaUtils.jsonNode2Str(intJsonNode));
            stepOption.setOutJsonSchema(JsonSchemaUtils.jsonNode2Str(outJsonNode));

            return stepOption;
        }).collect(Collectors.toList());

        return baseOptionList;

    }


    /**
     * 创建文案示例
     *
     * @param schemeRequest 创作方案需求请求
     */
    @Override
    public void example(CreativeSchemeModifyReqVO schemeRequest) {
        handlerAndValidate(schemeRequest);
        // 获取生成任务数量量
        int total = 3;
        // 图片素材列表
        List<String> imageUrlList = schemeRequest.getUseImages();
        // 随机打散图片素材列表
        List<String> disperseImageUrlList = CreativeImageUtils.disperseImageUrlList(imageUrlList, total);
        // 处理创作内容执行参数
        List<CreativePlanExecuteDTO> executeParamsList = handlerCreativeContentExecuteParams(schemeRequest);
        // 循环处理创作内容
        List<CreativeContentCreateReqVO> creativeContentCreateRequestList = new ArrayList<>(total * 2);
        for (int i = 0; i < total; i++) {
            // 业务UID
            String businessUid = IdUtil.fastSimpleUUID();
            // 随机获取执行参数
            CreativePlanExecuteDTO executeParam = SerializationUtils.clone(executeParamsList.get(RandomUtil.randomInt(executeParamsList.size())));
            if (CreativeSchemeModeEnum.CUSTOM_IMAGE_TEXT.name().equals(executeParam.getSchemeMode())) {

                CreativeContentCreateReqVO appCreateRequest = new CreativeContentCreateReqVO();
                AppMarketRespVO appResponse = executeParam.getAppResponse();
                if (appResponse == null) {
                    throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_APP_NOT_EXIST);
                }
                WorkflowConfigRespVO workflowConfig = appResponse.getWorkflowConfig();
                if (workflowConfig == null) {
                    throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_CONFIG_NOT_NULL);
                }
                List<WorkflowStepWrapperRespVO> stepWrapperList = CollectionUtil.emptyIfNull(workflowConfig.getSteps());

                for (WorkflowStepWrapperRespVO stepWrapper : stepWrapperList) {
                    // 图片处理
                    if (PosterActionHandler.class.getSimpleName().equals(stepWrapper.getFlowStep().getHandler())) {
                        VariableRespVO variable = stepWrapper.getVariable();
                        List<VariableItemRespVO> variables = variable.getVariables();
                        Map<String, Object> variableMap = variables.stream().collect(Collectors.toMap(VariableItemRespVO::getField, VariableItemRespVO::getValue));
                        String postStyleString = String.valueOf(variableMap.getOrDefault(CreativeConstants.POSTER_STYLE, ""));

                        PosterStyleDTO posterStyle = JSONUtil.toBean(postStyleString, PosterStyleDTO.class);

                        List<PosterTemplateDTO> templateList = CollectionUtil.emptyIfNull(posterStyle.getTemplateList());
                        // 获取首图模板
                        Optional<PosterTemplateDTO> mainImageOptional = templateList.stream().filter(PosterTemplateDTO::getIsMain).findFirst();
                        // 首图不存在，直接抛出异常
                        if (!mainImageOptional.isPresent()) {
                            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_IMAGE_MAIN_NOT_EXIST);
                        }
                        PosterTemplateDTO mainImageTemplate = mainImageOptional.get();
                        // 获取首图模板参数
                        List<PosterVariableDTO> mainImageTemplateVariableList = mainImageTemplate.getVariableList();
                        // 获取首图模板参数中的图片类型参数
                        List<PosterVariableDTO> mainImageStyleTemplateVariableList = CreativeImageUtils.imageTypeVariableList(mainImageTemplateVariableList);
                        // 首图图片参数素材图片替换
                        List<String> imageParamList = com.google.common.collect.Lists.newArrayList();
                        for (int j = 0; j < mainImageStyleTemplateVariableList.size(); j++) {
                            PosterVariableDTO variableItem = mainImageStyleTemplateVariableList.get(j);
                            if (j == 0) {
                                String imageUrl = disperseImageUrlList.get(i);
                                variableItem.setValue(imageUrl);
                                imageParamList.add(imageUrl);
                            } else {
                                variableItem.setValue(CreativeImageUtils.randomImage(imageParamList, imageUrlList, mainImageStyleTemplateVariableList.size()));
                            }
                        }

                        posterStyle.setTemplateList(templateList);

                        for (VariableItemRespVO variableItem : variables) {
                            if (CreativeConstants.POSTER_STYLE.equals(variableItem.getField())) {
                                variableItem.setValue(JSONUtil.toJsonStr(posterStyle));
                            }
                        }
                        variable.setVariables(variables);
                        stepWrapper.setVariable(variable);
                    }
                }

                workflowConfig.setSteps(stepWrapperList);
                appResponse.setWorkflowConfig(workflowConfig);

                appCreateRequest.setPlanUid("TEST");
                appCreateRequest.setSchemeUid(executeParam.getSchemeUid());
                appCreateRequest.setBusinessUid(businessUid);
                appCreateRequest.setConversationUid(BaseAppEntity.createAppConversationUid());
                appCreateRequest.setType(CreativeContentTypeEnum.ALL.getCode());
                appCreateRequest.setTempUid(appResponse.getUid());

                CreativePlanExecuteDTO appPlanExecute = new CreativePlanExecuteDTO();
                appPlanExecute.setAppResponse(appResponse);
                appPlanExecute.setSchemeUid(executeParam.getSchemeUid());
                appPlanExecute.setSchemeMode(executeParam.getSchemeMode());

                appCreateRequest.setExecuteParams(appPlanExecute);
                appCreateRequest.setIsTest(Boolean.TRUE);
                creativeContentCreateRequestList.add(appCreateRequest);
            }
        }
        // 批量插入任务
        creativeContentService.create(creativeContentCreateRequestList);

    }

    /**
     * 处理创作内容执行参数
     *
     * @param schemeRequest 创作方案
     * @return 创作内容执行参数
     */
    private List<CreativePlanExecuteDTO> handlerCreativeContentExecuteParams(CreativeSchemeModifyReqVO schemeRequest) {
        // 获取最新的海报模板参数。避免海报模板修改无法感知
        Map<String, PosterTemplateDTO> latestPosterMap = creativeImageManager.mapTemplate();
        // 处理创作内容执行参数
        List<CreativePlanExecuteDTO> list = Lists.newArrayList();
        // 获取自定义配置并且校验
        CreativeSchemeConfigurationDTO customConfiguration = schemeRequest.getConfiguration();
        customConfiguration.validate();
        // 查询应用信息
        AppMarketRespVO appMarketRespVO = appMarketService.get(customConfiguration.getAppUid());
        // 获取自定义配置的步骤列表，并且合并计划参数。
        List<BaseSchemeStepDTO> steps = CreativeAppUtils.mergeSchemeStepVariableList(customConfiguration.getSteps(), customConfiguration.getSteps());
        // 获取海报步骤
        Optional<BaseSchemeStepDTO> posterStepOptional = steps.stream().filter(item -> PosterActionHandler.class.getSimpleName().equals(item.getCode())).findFirst();

        // 如果没有海报步骤，直接创建一个执行参数
//        if (!posterStepOptional.isPresent()) {
//            AppMarketRespVO app = CreativeAppUtils.transformCustomExecute(customConfiguration, SerializationUtils.clone(appMarketRespVO));
//            CreativePlanExecuteDTO planExecute = new CreativePlanExecuteDTO();
//            planExecute.setSchemeUid(schemeRequest.getUid());
//            planExecute.setSchemeMode(schemeRequest.getMode());
//            planExecute.setAppResponse(app);
//            list.add(planExecute);
//        }
//
//        // 如果有海报步骤，则需要创建多个执行参数, 每一个海报参数创建一个执行参数
//        PosterSchemeStepDTO schemeStep = (PosterSchemeStepDTO) posterStepOptional.get();
//        for (PosterStyleDTO posterStyle : CollectionUtil.emptyIfNull(schemeStep.getStyleList())) {
//            PosterStyleDTO style = CreativeImageUtils.mergePosterTemplate(posterStyle, latestPosterMap);
//            AppMarketRespVO app = CreativeAppUtils.transformCustomExecute(steps, style, SerializationUtils.clone(appMarketRespVO), schemeRequest.getUseImages());
//            CreativePlanExecuteDTO planExecute = new CreativePlanExecuteDTO();
//            planExecute.setSchemeUid(schemeRequest.getUid());
//            planExecute.setSchemeMode(schemeRequest.getMode());
//            planExecute.setAppResponse(app);
//            list.add(planExecute);
//        }
        return list;
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

        // 处理创作方案配置
        CreativeSchemeConfigurationDTO configuration = request.getConfiguration();
        List<BaseSchemeStepDTO> steps = configuration.getSteps();
        List<BaseSchemeStepDTO> handlerStepList = Lists.newArrayList();

        for (BaseSchemeStepDTO step : steps) {
            // 海报步骤
            if (PosterActionHandler.class.getSimpleName().equals(step.getCode())) {
                PosterSchemeStepDTO posterStep = (PosterSchemeStepDTO) step;
                List<PosterStyleDTO> styleList = posterStep.getStyleList();
                List<PosterStyleDTO> handlerStyleList = Lists.newArrayList();
                for (PosterStyleDTO posterStyle : styleList) {
                    PosterStyleDTO style = CreativeUtils.handlerPosterStyle(posterStyle);
                    handlerStyleList.add(style);
                }
                // 获取最多的图片数量
                Integer maxImageCount = handlerStyleList.stream()
                        .max(Comparator.comparingInt(PosterStyleDTO::getImageCount))
                        .map(PosterStyleDTO::getImageCount).orElse(0);
                // 设置最多的图片数量
                handlerStyleList = handlerStyleList.stream().peek(item -> item.setMaxImageCount(maxImageCount)).collect(Collectors.toList());

                posterStep.setStyleList(handlerStyleList);
                handlerStepList.add(posterStep);
            } else {
                handlerStepList.add(step);
            }
        }
        configuration.setSteps(handlerStepList);
        request.setConfiguration(configuration);
    }
}
