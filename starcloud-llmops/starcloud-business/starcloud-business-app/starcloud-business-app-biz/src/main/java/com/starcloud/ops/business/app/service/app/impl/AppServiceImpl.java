package com.starcloud.ops.business.app.service.app.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.app.vo.request.AppPageQuery;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.AppUpdateReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.VariableReqVO;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.category.vo.AppCategoryVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.verification.Verification;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteRespVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginRespVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.dal.mysql.app.AppMapper;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.BaseAppEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.action.MaterialActionHandler;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.RecommendAppEnum;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSourceEnum;
import com.starcloud.ops.business.app.enums.app.AppStepResponseStyleEnum;
import com.starcloud.ops.business.app.enums.app.AppStepResponseTypeEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableGroupEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableStyleEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableTypeEnum;
import com.starcloud.ops.business.app.enums.materiallibrary.MaterialBindTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.business.app.recommend.RecommendAppCache;
import com.starcloud.ops.business.app.recommend.RecommendStepWrapperFactory;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.business.app.service.plugins.PluginsDefinitionService;
import com.starcloud.ops.business.app.service.publish.AppPublishService;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.service.xhs.material.CreativeMaterialManager;
import com.starcloud.ops.business.app.util.AppUtils;
import com.starcloud.ops.business.app.util.PinyinUtils;
import com.starcloud.ops.business.app.util.UserUtils;
import com.starcloud.ops.business.app.verification.VerificationUtils;
import com.starcloud.ops.business.mq.producer.AppDeleteProducer;
import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import com.starcloud.ops.framework.common.api.enums.LanguageEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.hutool.core.util.RandomUtil.BASE_CHAR_NUMBER_LOWER;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.DUPLICATE_LABEL;

/**
 * 应用管理服务实现类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@Slf4j
@Service
public class AppServiceImpl implements AppService {

    @Resource
    private AppMapper appMapper;

    @Resource
    private AppPublishService appPublishService;

    @Resource
    private AppDictionaryService appDictionaryService;

    @Resource
    private AppDeleteProducer appDeleteProducer;

    @Resource
    private AppMarketMapper appMarketMapper;

    @Resource
    private CreativeMaterialManager creativeMaterialManager;

    @Resource
    private CreativeContentService creativeContentService;

    @Resource
    private PluginsDefinitionService pluginsDefinitionService;

    /**
     * 查询应用语言列表
     *
     * @return 应用语言列表
     */
    @Override
    public Map<String, List<Option>> metadata() {
        Map<String, List<Option>> metadata = new HashMap<>();
        // 语言列表
        metadata.put("language", LanguageEnum.languageList());
        // 大模型
        metadata.put("llmModelType", AppUtils.llmModelTypeList());
        // 应用类型
        metadata.put("appType", AppTypeEnum.options(UserUtils.isAdmin()));
        // 素材类型
        metadata.put("materialType", MaterialTypeEnum.referOptions());
        // 变量分组
        metadata.put("variableGroup", AppVariableGroupEnum.options());
        // 变量类型
        metadata.put("variableType", AppVariableTypeEnum.options());
        // 变量样式
        metadata.put("variableStyle", AppVariableStyleEnum.options());
        // 返回类型
        metadata.put("responseType", AppStepResponseTypeEnum.options());
        // 返回样式
        metadata.put("responseStyle", AppStepResponseStyleEnum.options());
        return metadata;
    }

    /**
     * 查询应用分类列表
     *
     * @param isRoot 是否只根节点数据
     * @return 应用分类列表
     */
    @Override
    public List<AppCategoryVO> categoryList(Boolean isRoot) {
        return appDictionaryService.categoryList(isRoot);
    }

    /**
     * 查询应用分类列表
     *
     * @return 应用分类列表
     */
    @Override
    public List<AppCategoryVO> categoryTree() {
        // 查询应用分类字典数据
        return appDictionaryService.categoryTree();
    }

    /**
     * 查询推荐的应用列表
     *
     * @return 应用列表
     */
    @Override
    public List<AppRespVO> listRecommendedApps(String model) {
        return getAppTemplateList(model);
    }

    /**
     * 查询推荐的应用详情
     *
     * @param uid 推荐应用唯一标识
     * @return 应用详情
     */
    @Override
    public AppRespVO getRecommendApp(String uid) {
        return getAppTemplate(uid);
    }

    /**
     * 获取步骤列表
     *
     * @return 步骤列表
     */
    @Override
    public List<WorkflowStepWrapperRespVO> stepList(String type) {
        if (StringUtils.isBlank(type)) {
            throw ServiceExceptionUtil.invalidParamException(type, "应用类型是必选的!");
        }
        if (!IEnumable.contains(type, AppTypeEnum.class)) {
            throw ServiceExceptionUtil.invalidParamException(type, "非法的应用类型!");
        }
        // 应用为普通应用或系统应用
        if (AppTypeEnum.COMMON.name().equalsIgnoreCase(type) ||
                AppTypeEnum.SYSTEM.name().equalsIgnoreCase(type)) {
            return RecommendStepWrapperFactory.defCommonStepWrapperList();
        }
        // 应用为媒体矩阵
        if (AppTypeEnum.MEDIA_MATRIX.name().equalsIgnoreCase(type)) {
            return RecommendStepWrapperFactory.defMediaMatrixStepWrapperList();
        }
        throw ServiceExceptionUtil.invalidParamException(type, "非法的应用类型!");
    }

    /**
     * 分页查询应用列表
     *
     * @param query 查询条件
     * @return 应用列表
     */
    @Override
    public PageResp<AppRespVO> page(AppPageQuery query) {
        Page<AppDO> page = appMapper.page(query);
        return AppConvert.INSTANCE.convertPage(page);
    }

    /**
     * 根据应用 UID 获取应用详情
     *
     * @param uid 应用 UID
     * @return 应用详情
     */
    @Override
    public AppRespVO get(String uid) {
        AppDO app = appMapper.get(uid, Boolean.FALSE);
        AppValidate.notNull(app, ErrorCodeConstants.RESULT_NOT_EXIST, "应用不存在，请稍后重试或者联系管理员（{}）！", uid);
        AppRespVO appResponse = AppConvert.INSTANCE.convertResponse(app);
        if (AppTypeEnum.MEDIA_MATRIX.name().equals(appResponse.getType())) {
            // 迁移旧素材数据
            WorkflowStepWrapperRespVO stepByHandler = appResponse.getStepByHandler(MaterialActionHandler.class);
            if (CollectionUtil.isNotEmpty(app.getMaterialList()) && Objects.nonNull(stepByHandler)) {
                // 从数据库迁移
                creativeMaterialManager.migrateFromData(app.getName(), app.getUid(),
                        MaterialBindTypeEnum.APP_MAY.getCode(), stepByHandler, app.getMaterialList(), Long.valueOf(app.getCreator()));
                app.setMaterialList(Collections.emptyList());
                appMapper.updateById(app);
            } else if (CollectionUtil.isEmpty(app.getMaterialList()) && Objects.nonNull(stepByHandler)) {
                String stepVariableValue = stepByHandler.getVariableToString(CreativeConstants.LIBRARY_QUERY);

                if (StringUtils.isBlank(stepVariableValue)) {
                    // 新建
                    creativeMaterialManager.createEmptyLibrary(app.getName(), app.getUid(),
                            MaterialBindTypeEnum.APP_MAY.getCode(), Long.valueOf(app.getCreator()));
                } else {
                    // 从变量迁移
                    creativeMaterialManager.migrateFromConfig(app.getName(), app.getUid(),
                            MaterialBindTypeEnum.APP_MAY.getCode(), stepVariableValue, Long.valueOf(app.getCreator()));
                    stepByHandler.putVariable(CreativeConstants.LIBRARY_QUERY, "");
                    app.setConfig(JsonUtils.toJsonString(appResponse.getWorkflowConfig()));
                    appMapper.updateById(app);
                }
            }
        }
        return appResponse;
    }

    /**
     * 根据应用 UID 获取应用详情-简单
     *
     * @param uid 应用 UID
     * @return 应用详情
     */
    @Override
    public AppRespVO getSimple(String uid) {
        AppDO app = appMapper.get(uid, Boolean.TRUE);
        AppValidate.notNull(app, ErrorCodeConstants.RESULT_NOT_EXIST, "应用不存在，请稍后重试或者联系管理员（{}）！", uid);
        return AppConvert.INSTANCE.convertResponse(app, Boolean.FALSE);
    }

    /**
     * 创建应用
     *
     * @param request 应用信息
     */
    @Override
    public AppRespVO create(AppReqVO request) {
        List<Verification> verifications = handlerAndValidateRequest(request);
        AppEntity appEntity = AppConvert.INSTANCE.convert(request);
        appEntity.setCreator(String.valueOf(SecurityFrameworkUtils.getLoginUserId()));
        appEntity.setUpdater(String.valueOf(SecurityFrameworkUtils.getLoginUserId()));
        appEntity.setCreateTime(LocalDateTime.now());
        appEntity.setUpdateTime(LocalDateTime.now());
        appEntity.insert();

        verifications.addAll(appEntity.getVerificationList());
        AppRespVO appResponse = AppConvert.INSTANCE.convertResponse(appEntity);
        appResponse.setVerificationList(verifications);

        return appResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppRespVO create(AppUpdateReqVO request) {
        AppRespVO appRespVO = create((AppReqVO) request);
        if (AppTypeEnum.MEDIA_MATRIX.name().equals(request.getType())) {
            creativeMaterialManager.copyAppMaterial(request.getUid(), appRespVO.getName(), appRespVO.getUid());
        }
        return appRespVO;
    }

    /**
     * 复制应用
     *
     * @param request 模版应用
     */
    @Override
    public AppRespVO copy(UidRequest request) {

        String uid = request.getUid();
        AppValidate.notBlank(uid, ErrorCodeConstants.APP_UID_REQUIRED);

        AppDO app = appMapper.get(uid, Boolean.FALSE);
        AppValidate.notNull(app, ErrorCodeConstants.APP_NON_EXISTENT, uid);
        // 转换应用信息
        AppEntity appEntity = (AppEntity) AppConvert.INSTANCE.convert(app, Boolean.FALSE);
        // 新的UID
        appEntity.setUid(IdUtil.fastSimpleUUID());
        // 新的名称
        appEntity.setName(getCopyName(appEntity.getName()));
        appEntity.setCreator(null);
        appEntity.setUpdater(null);
        appEntity.setCreateTime(LocalDateTime.now());
        appEntity.setUpdateTime(LocalDateTime.now());
        // 查询配置的示例列表
        List<String> imageList = creativeContentService.listImage(appEntity.getExample());
        if (CollectionUtil.isNotEmpty(imageList)) {
            appEntity.setImages(imageList);
        }
        // 插入数据库
        appEntity.insert();
        if (AppTypeEnum.MEDIA_MATRIX.name().equals(appEntity.getType())) {
            creativeMaterialManager.copyAppMaterial(uid, appEntity.getName(), appEntity.getUid());
        }

        return AppConvert.INSTANCE.convertResponse(appEntity);
    }

    /**
     * 生成一个复制名称的私有方法
     *
     * @param name 原始名称
     * @return 复制名称
     */
    private String getCopyName(String name) {
        String copyName = name + "-Copy";
        if (!appMapper.duplicateName(copyName)) {
            return copyName;
        }
        return getCopyName(copyName);
    }

    /**
     * 更新应用
     *
     * @param request 更新请求信息
     */
    @Override
    public AppRespVO modify(AppUpdateReqVO request) {
        // 校验并且处理请求
        List<Verification> verifications = handlerAndValidateRequest(request);
        if (CollectionUtil.isNotEmpty(verifications)) {
            AppRespVO appResponse = new AppRespVO();
            appResponse.setUid(request.getUid());
            appResponse.setVerificationList(verifications);
            return appResponse;
        }
        // 更新应用。
        AppEntity appEntity = AppConvert.INSTANCE.convert(request);

        appEntity.setUid(request.getUid());
        appEntity.setUpdater(String.valueOf(SecurityFrameworkUtils.getLoginUserId()));
        appEntity.setUpdateTime(LocalDateTime.now());

        // 插件配置
        if (AppTypeEnum.MEDIA_MATRIX.name().equalsIgnoreCase(appEntity.getType())) {
            List<PluginRespVO> list = pluginsDefinitionService.list(appEntity.getUid());
            appEntity.setPluginList(CollectionUtil.emptyIfNull(list).stream().map(PluginRespVO::getUid).collect(Collectors.toList()));
        }

        appEntity.update();

        verifications.addAll(appEntity.getVerificationList());

        AppRespVO appResponse = this.get(request.getUid());
        appResponse.setVerificationList(verifications);

        return appResponse;
    }

    /**
     * 根据应用 UID 删除应用
     *
     * @param uid 应用 UID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String uid) {
        // 删除应用
        appMapper.delete(uid);
        // 删除应用发布信息
        appPublishService.deleteByAppUid(uid);
        // 删除其他资源
        appDeleteProducer.send(uid);
    }

    /**
     * 获取最新的wx mp聊天应用Uid
     */
    @Override
    public AppRespVO getRecently(Long userId) {
        LambdaQueryWrapper<AppDO> wrapper = Wrappers.lambdaQuery(AppDO.class)
                .eq(AppDO::getSource, AppSourceEnum.WX_WP.name())
                .eq(AppDO::getModel, AppModelEnum.CHAT.name())
                .eq(AppDO::getType, AppTypeEnum.COMMON.name())
                .eq(AppDO::getCreator, userId)
                .orderByDesc(AppDO::getUpdateTime)
                .last("limit 1");
        AppDO appDO = appMapper.selectOne(wrapper);
        if (appDO == null) {
            return null;
        }
        return AppConvert.INSTANCE.convertResponse(appMapper.selectOne(wrapper));
    }

    /**
     * 异步执行应用
     *
     * @param request 应用执行请求信息
     */
    @Override
    @SuppressWarnings("all")
    public AppExecuteRespVO execute(AppExecuteReqVO request) {
        BaseAppEntity app = AppFactory.factory(request);
        return (AppExecuteRespVO) app.execute(request);
    }

    /**
     * 异步执行应用
     *
     * @param request 应用执行请求信息
     */
    @Override
    @SuppressWarnings("all")
    public void asyncExecute(AppExecuteReqVO request) {
        try {
            BaseAppEntity app = AppFactory.factory(request);
            app.asyncExecute(request);
        } catch (Exception exception) {
            if (request.getSseEmitter() != null) {
                request.getSseEmitter().completeWithError(exception);
            }
        }
    }

    @Override
    public List<VariableItemRespVO> generalFieldCode(VariableReqVO reqVO) {
        List<VariableItemRespVO> variables = reqVO.getVariables();
        if (CollectionUtil.isEmpty(variables)) {
            return CollectionUtil.newArrayList();
        }

        // 是否存在重复的label
        Set<String> duplicateLabel = variables.stream()
                .collect(Collectors.groupingBy(e -> e.getLabel(), Collectors.counting()))
                .entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        if (CollectionUtil.isNotEmpty(duplicateLabel)) {
            throw exception(DUPLICATE_LABEL, duplicateLabel);
        }

        // 已有field的字段排在前面，不重复生成，防止新字段field重复
        variables = variables.stream().sorted((a, b) -> {
            if (StringUtils.isBlank(b.getField())) {
                return -1;
            }
            return 1;
        }).collect(Collectors.toList());

        List<String> fieldCodeExist = new ArrayList<>(variables.size());

        for (VariableItemRespVO variable : variables) {
            String label = variable.getLabel();
            AppValidate.notBlank(label, "变量 label 不能为空");
            if (StringUtils.isNotBlank(variable.getField())) {
                fieldCodeExist.add(variable.getField().toUpperCase());
                continue;
            }
            char[] nameChar = label.trim().toCharArray();
            StringBuilder sb = new StringBuilder();
            for (char c : nameChar) {
                sb.append(PinyinUtils.pinyinFirstChar(c).toUpperCase());
            }
            String code = pinyinFirstCharUnique(sb.toString(), fieldCodeExist);
            fieldCodeExist.add(code);
            variable.setField(code);
        }
        return variables;
    }

    /**
     * code重复拼接随机字符串
     *
     * @param field
     * @param fieldCodeExist
     * @return
     */
    private String pinyinFirstCharUnique(String field, List<String> fieldCodeExist) {
        if (fieldCodeExist.contains(field)) {
            return pinyinFirstCharUnique(field + RandomUtil.randomString(BASE_CHAR_NUMBER_LOWER, 1), fieldCodeExist);
        }
        return field;
    }

    /**
     * 处理校验请求
     *
     * @param request 请求信息
     */
    private List<Verification> handlerAndValidateRequest(AppReqVO request) {
        List<Verification> verifications = new ArrayList<>();

        // 应用类目校验
        if (AppModelEnum.COMPLETION.name().equals(request.getModel())) {
            Long tenantId = TenantContextHolder.getTenantId();
            if (Objects.isNull(tenantId)) {
                VerificationUtils.addVerificationApp(verifications, request.getName(), "不支持的租户类型!");
                return verifications;
            }

            // 如果类目为空，根据租户类型设置默认类目
            if (StringUtils.isBlank(request.getCategory())) {
                if (AppConstants.MOFAAI_TENANT_ID.equals(tenantId)) {
                    request.setCategory("SEO_WRITING_OTHER");
                } else if (AppConstants.JUZHEN_TENANT_ID.equals(tenantId)) {
                    request.setCategory("COMMON");
                } else {
                    VerificationUtils.addVerificationApp(verifications, request.getName(), "不支持的租户类型!");
                    return verifications;
                }
            }

            // 查询类目列表，判断类目是否支持
            List<AppCategoryVO> categoryList = appDictionaryService.categoryList(Boolean.FALSE);
            Optional<AppCategoryVO> categoryOptional = categoryList.stream()
                    .filter(category -> category.getCode().equals(request.getCategory()))
                    .findFirst();
            if (!categoryOptional.isPresent()) {
                VerificationUtils.addVerificationApp(verifications, request.getName(), "不支持的类目类型!");
                return verifications;
            }

            // 魔法AI租户，不支持选择一级分类
            AppCategoryVO category = categoryOptional.get();
            if (AppConstants.MOFAAI_TENANT_ID.equals(tenantId) &&
                    AppConstants.ROOT.equals(category.getParentCode())) {
                VerificationUtils.addVerificationApp(verifications, request.getName(), "不支持的选择一级分类，请检查后重试（" + request.getCategory() + "）！!");
                return verifications;
            }

            // 如果 icon 为空，设置默认 icon
            if (StringUtils.isBlank(request.getIcon())) {
                request.setIcon(category.getIcon());
            }
            // 查询配置的示例列表
            List<String> imageList = creativeContentService.listImage(request.getExample());
            if (CollectionUtil.isNotEmpty(imageList)) {
                request.setImages(imageList);
            } else {
                if (CollectionUtil.isNotEmpty(request.getImages())) {
                    request.setImages(request.getImages());
                } else {
                    // 图片默认为分类图片
                    request.setImages(Collections.singletonList(category.getImage()));
                }
            }

        }

        // 未指定应用类型，默认为普通应用
        if (StringUtils.isBlank(request.getType())) {
            request.setType(AppTypeEnum.COMMON.name());
        }

        // 系统应用，只有管理员可以创建
        if (AppTypeEnum.SYSTEM.name().equals(request.getType()) && UserUtils.isNotAdmin()) {
            VerificationUtils.addVerificationApp(verifications, request.getName(), "不支持的应用类型(" + request.getType() + ")!");
        }

        return verifications;
    }

    /**
     * 获取应用模板列表
     *
     * @return 应用模板列表
     */
    private List<AppRespVO> getAppTemplateList(String model) {
        model = StringUtils.isBlank(model) ? AppModelEnum.COMPLETION.name() : model;
        List<String> nameList = appDictionaryService.appTemplateAppNameList();
        if (CollectionUtil.isEmpty(nameList)) {
            return RecommendAppCache.get(model);
        }

        LambdaQueryWrapper<AppMarketDO> wrapper = appMarketMapper.queryMapper(Boolean.TRUE);
        wrapper.eq(AppMarketDO::getModel, model);
        wrapper.in(AppMarketDO::getName, nameList);
        List<AppMarketDO> appTemplateList = appMarketMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(appTemplateList)) {
            return RecommendAppCache.get(model);
        }

        // 按照 nameList 的顺序排序
        Map<String, AppMarketDO> appTemplateMap = appTemplateList.stream().collect(Collectors.toMap(AppMarketDO::getName, item -> item));
        return nameList.stream()
                .map(appTemplateMap::get)
                .filter(Objects::nonNull)
                .map(item -> {
                    AppMarketRespVO appMarketResponse = AppMarketConvert.INSTANCE.convertResponse(item);
                    AppRespVO appResponse = AppConvert.INSTANCE.convert(appMarketResponse);
                    appResponse.setSource(AppSourceEnum.WEB.name());
                    return appResponse;
                }).collect(Collectors.toList());
    }

    /**
     * 获取应用模板
     *
     * @param uid 应用唯一标识
     * @return 应用模板
     */
    private AppRespVO getAppTemplate(String uid) {
        if (RecommendAppEnum.isAppOrChat(uid)) {
            return RecommendAppCache.getRecommendApp(uid);
        }

        LambdaQueryWrapper<AppMarketDO> wrapper = appMarketMapper.queryMapper(Boolean.FALSE);
        wrapper.eq(AppMarketDO::getUid, uid);
        AppMarketDO appTemplate = appMarketMapper.selectOne(wrapper);
        AppValidate.notNull(appTemplate, "应用模板不存在！");
        AppMarketRespVO appMarketResponse = AppMarketConvert.INSTANCE.convertResponse(appTemplate);
        AppRespVO appResponse = AppConvert.INSTANCE.convert(appMarketResponse);
        appResponse.setSource(AppSourceEnum.WEB.name());
        return appResponse;
    }

}
