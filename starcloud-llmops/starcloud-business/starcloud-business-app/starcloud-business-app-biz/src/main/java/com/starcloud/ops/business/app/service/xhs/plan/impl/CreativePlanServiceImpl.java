package com.starcloud.ops.business.app.service.xhs.plan.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.image.dto.UploadImageInfoDTO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentCreateReqVO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanConfigurationDTO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanExecuteDTO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanModifyReqVO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanPageQuery;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanReqVO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.CreativeSchemeConfigDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.BaseSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.PosterSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterTemplateDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterVariableDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.CreativeSchemeRespVO;
import com.starcloud.ops.business.app.convert.xhs.plan.CreativePlanConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentBusinessPO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanPO;
import com.starcloud.ops.business.app.dal.mysql.xhs.plan.CreativePlanMapper;
import com.starcloud.ops.business.app.domain.entity.BaseAppEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.action.PosterActionHandler;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentStatusEnum;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanStatusEnum;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativeRandomTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativeTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.poster.PosterModeEnum;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.service.xhs.manager.CreativeAppManager;
import com.starcloud.ops.business.app.service.xhs.manager.CreativeImageManager;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanService;
import com.starcloud.ops.business.app.service.xhs.scheme.CreativeSchemeService;
import com.starcloud.ops.business.app.util.CreativeAppUtils;
import com.starcloud.ops.business.app.util.CreativeImageUtils;
import com.starcloud.ops.business.app.util.CreativeUploadUtils;
import com.starcloud.ops.business.app.util.CreativeUtils;
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.business.app.util.UserUtils;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Slf4j
@Service
public class CreativePlanServiceImpl implements CreativePlanService {

    @Resource
    private CreativeSchemeService creativeSchemeService;

    @Resource
    private CreativeContentService creativeContentService;

    @Resource
    private CreativePlanMapper creativePlanMapper;

    @Resource
    private CreativeAppManager creativeAppManager;

    @Resource
    private CreativeImageManager creativeImageManager;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private AppMarketService appMarketService;

    /**
     * 上传图片
     *
     * @param image 上传图片
     * @return 图片信息
     */
    @Override
    public UploadImageInfoDTO uploadImage(MultipartFile image) {
        log.info("Creative 开始上传图片，ContentType: {}, imageName: {}", image.getContentType(), image.getOriginalFilename());
        return CreativeUploadUtils.uploadImage(image, ImageUploadUtils.UPLOAD);
    }

    /**
     * 获取创作计划详情
     *
     * @param uid 创作计划UID
     * @return 创作计划详情
     */
    @Override
    public CreativePlanRespVO get(String uid) {
        AppValidate.notBlank(uid, CreativeErrorCodeConstants.PLAN_UID_REQUIRED);
        CreativePlanDO creativePlan = creativePlanMapper.get(uid);
        AppValidate.notNull(creativePlan, CreativeErrorCodeConstants.PLAN_NOT_EXIST, uid);
        return CreativePlanConvert.INSTANCE.convertResponse(creativePlan);
    }

    /**
     * 获取创作计划分页列表
     *
     * @param query 请求参数
     * @return 创作计划分页列表
     */
    @Override
    public PageResp<CreativePlanRespVO> page(CreativePlanPageQuery query) {
        IPage<CreativePlanPO> page = creativePlanMapper.page(PageUtil.page(query), query);
        if (page == null) {
            return PageResp.of(Collections.emptyList(), 0L, 1L, 10L);
        }
        List<CreativePlanPO> records = page.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return PageResp.of(Collections.emptyList(), page.getTotal(), page.getCurrent(), page.getSize());
        }
        List<String> planUidList = records.stream().map(CreativePlanPO::getUid).collect(Collectors.toList());
        List<CreativeContentBusinessPO> businessList = creativeContentService.listGroupByPlanUid(planUidList);
        Map<String, List<CreativeContentBusinessPO>> businessMap = businessList.stream().collect(Collectors.groupingBy(CreativeContentBusinessPO::getPlanUid));

        // 用户创建者ID列表。
        List<Long> creatorList = records.stream().map(item -> Long.valueOf(item.getCreator())).distinct().collect(Collectors.toList());
        // 获取用户创建者ID，昵称 Map。
        Map<Long, String> creatorMap = UserUtils.getUserNicknameMapByIds(creatorList);

        List<CreativePlanRespVO> collect = records.stream().map(item -> {
            CreativePlanRespVO response = CreativePlanConvert.INSTANCE.convertResponse(item);
            response.setCreator(creatorMap.get(Long.valueOf(item.getCreator())));
            // 总数
            List<CreativeContentBusinessPO> businessItemList = CollectionUtil.emptyIfNull(businessMap.get(item.getUid()));

            int successCount = (int) businessItemList.stream().filter(businessItem -> CreativeContentStatusEnum.EXECUTE_SUCCESS.getCode().equals(businessItem.getStatus())).count();

            int failureCount = (int) businessItemList.stream().filter(businessItem -> CreativeContentStatusEnum.EXECUTE_ERROR_FINISHED.getCode().equals(businessItem.getStatus())).count();

            response.setTotal(businessItemList.size());
            response.setSuccessCount(successCount);
            response.setFailureCount(failureCount);
            return response;
        }).collect(Collectors.toList());

        return PageResp.of(collect, page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 创建创作计划
     *
     * @param request 创作计划请求
     */
    @Override
    public String create(CreativePlanReqVO request) {
        handlerAndValidate(request);
        if (creativePlanMapper.distinctName(request.getName())) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_NAME_EXIST, request.getName());
        }
        CreativePlanDO plan = CreativePlanConvert.INSTANCE.convertCreateRequest(request);
        creativePlanMapper.insert(plan);
        return plan.getUid();
    }

    /**
     * 复制创作计划
     *
     * @param request 创作计划请求
     */
    @Override
    public String copy(UidRequest request) {
        AppValidate.notBlank(request.getUid(), CreativeErrorCodeConstants.PLAN_UID_REQUIRED);
        CreativePlanDO plan = creativePlanMapper.get(request.getUid());
        AppValidate.notNull(plan, CreativeErrorCodeConstants.PLAN_NOT_EXIST, request.getUid());

        CreativePlanDO copyPlan = new CreativePlanDO();
        copyPlan.setUid(IdUtil.fastSimpleUUID());
        copyPlan.setName(getCopyName(plan.getName()));
        copyPlan.setType(plan.getType());
        copyPlan.setConfiguration(plan.getConfiguration());
        copyPlan.setRandomType(plan.getRandomType());
        copyPlan.setTotal(plan.getTotal());
        copyPlan.setStatus(CreativePlanStatusEnum.PENDING.name());
        copyPlan.setStartTime(null);
        copyPlan.setEndTime(null);
        copyPlan.setElapsed(0L);
        copyPlan.setDescription(plan.getDescription());
        copyPlan.setDeleted(Boolean.FALSE);
        copyPlan.setCreateTime(LocalDateTime.now());
        copyPlan.setEndTime(LocalDateTime.now());
        creativePlanMapper.insert(copyPlan);
        return copyPlan.getUid();
    }

    /**
     * 修改创作计划
     *
     * @param request 创作计划请求
     */
    @Override
    public String modify(CreativePlanModifyReqVO request) {
        AppValidate.notBlank(request.getUid(), CreativeErrorCodeConstants.PLAN_UID_REQUIRED);
        handlerAndValidate(request);
        CreativePlanDO plan = creativePlanMapper.get(request.getUid());
        AppValidate.notNull(plan, CreativeErrorCodeConstants.PLAN_NOT_EXIST, request.getUid());
        if (!CreativePlanStatusEnum.PENDING.name().equals(plan.getStatus())) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_STATUS_NOT_SUPPORT_MODIFY);
        }
        // 名称做了修改，且修改之后的名称已经存在
        if (!plan.getName().equals(request.getName()) && creativePlanMapper.distinctName(request.getName())) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_NAME_EXIST, request.getName());
        }
        CreativePlanDO modifyPlan = CreativePlanConvert.INSTANCE.convertModifyRequest(request);
        modifyPlan.setId(plan.getId());
        creativePlanMapper.updateById(modifyPlan);
        return modifyPlan.getUid();
    }

    /**
     * 修改创作计划状态
     *
     * @param uid    创作计划UID
     * @param status 修改状态
     */
    @Override
    public void updateStatus(String uid, String status) {
        AppValidate.notBlank(uid, CreativeErrorCodeConstants.PLAN_UID_REQUIRED);
        AppValidate.notBlank(status, CreativeErrorCodeConstants.PLAN_STATUS_REQUIRED);

        if (!CreativePlanStatusEnum.contains(status) || CreativePlanStatusEnum.PENDING.name().equals(status)) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_STATUS_NOT_SUPPORT_MODIFY);
        }

        CreativePlanDO plan = creativePlanMapper.get(uid);
        AppValidate.notNull(plan, CreativeErrorCodeConstants.PLAN_NOT_EXIST, uid);

        // 更新
        LocalDateTime now = LocalDateTime.now();
        // 计算耗时，毫秒数 now - startTime
        Duration duration = Duration.between(plan.getStartTime(), now);
        LambdaUpdateWrapper<CreativePlanDO> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(CreativePlanDO::getStatus, status);
        updateWrapper.set(CreativePlanDO::getEndTime, LocalDateTime.now());
        updateWrapper.set(CreativePlanDO::getElapsed, duration.toMillis());
        updateWrapper.set(CreativePlanDO::getUpdateTime, now);
        updateWrapper.eq(CreativePlanDO::getUid, uid);
        creativePlanMapper.update(null, updateWrapper);
    }

    /**
     * 更新计划状态
     *
     * @param planUid 计划UID
     */
    @Override
    public void updatePlanStatus(String planUid) {
        log.info("开始更新计划状态，planUid: {}", planUid);
        String key = "creative-plan-update-status-" + planUid;
        RLock lock = redissonClient.getLock(key);
        try {
            if (!lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                return;
            }
            List<CreativeContentDO> contentList = CollectionUtil.emptyIfNull(creativeContentService.listByPlanUid(planUid));
            // 当前计划下只有全部执行成功的，则计划完成
            boolean complete = contentList.stream().allMatch(item -> CreativeContentStatusEnum.EXECUTE_SUCCESS.getCode().equals(item.getStatus()));
            if (complete) {
                log.info("将要更新计划为【完成】状态，planUid: {}", planUid);
                updateStatus(planUid, CreativePlanStatusEnum.COMPLETE.name());
                return;
            }
            // 当前计划下只要有彻底失败的，则计划失败
            boolean failure = contentList.stream().anyMatch(item -> CreativeContentStatusEnum.EXECUTE_ERROR_FINISHED.getCode().equals(item.getStatus()));
            if (failure) {
                log.info("将要更新计划为【失败】状态，planUid: {}", planUid);
                updateStatus(planUid, CreativePlanStatusEnum.FAILURE.name());
            }
            log.info("更新计划状态完成，planUid: {}", planUid);
        } catch (Exception exception) {
            log.warn("更新计划失败: {}", planUid, exception);
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_UPDATE_STATUS_FAILED, planUid, exception.getMessage());
        } finally {
            lock.unlock();
        }
    }

    /**
     * 删除创作计划
     *
     * @param uid 创作计划UID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String uid) {
        AppValidate.notBlank(uid, CreativeErrorCodeConstants.PLAN_UID_REQUIRED);
        CreativePlanDO plan = creativePlanMapper.get(uid);
        AppValidate.notNull(plan, CreativeErrorCodeConstants.PLAN_NOT_EXIST, uid);
        // 删除创作计划
        creativePlanMapper.deleteById(plan.getId());
        // 删除创作计划下的创作内容
        creativeContentService.deleteByPlanUid(uid);
    }

    /**
     * 执行创作计划
     *
     * @param uid 创作计划UID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(String uid) {
        // 基本校验
        AppValidate.notBlank(uid, CreativeErrorCodeConstants.PLAN_UID_REQUIRED);
        CreativePlanRespVO plan = this.get(uid);
        // 目前只支持随机执行
        if (!CreativeRandomTypeEnum.RANDOM.name().equals(plan.getRandomType())) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_RANDOM_TYPE_NOT_SUPPORTED, plan.getRandomType());
        }
        // 生成任务
        this.creativeBathContentTask(plan);
        // 更新状态
        LambdaUpdateWrapper<CreativePlanDO> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(CreativePlanDO::getStatus, CreativePlanStatusEnum.RUNNING.name());
        updateWrapper.set(CreativePlanDO::getStartTime, LocalDateTime.now());
        updateWrapper.set(CreativePlanDO::getUpdateTime, LocalDateTime.now());
        updateWrapper.eq(CreativePlanDO::getUid, uid);
        creativePlanMapper.update(null, updateWrapper);
    }

    /**
     * 创建随机任务
     *
     * @param plan 创作计划
     */
    private void creativeBathContentTask(CreativePlanRespVO plan) {
        // 获取生成任务数量量
        Integer total = plan.getTotal();
        CreativePlanConfigurationDTO config = plan.getConfiguration();
        // 图片素材列表
        List<String> imageUrlList = config.getImageUrlList();
        // 随机打散图片素材列表
        List<String> disperseImageUrlList = CreativeImageUtils.disperseImageUrlList(imageUrlList, total);
        // 处理创作内容执行参数
        List<CreativePlanExecuteDTO> executeParamsList = handlerCreativeContentExecuteParams(plan);
        // 循环处理创作内容
        List<CreativeContentCreateReqVO> creativeContentCreateRequestList = new ArrayList<>(total);
        for (int i = 0; i < total; i++) {
            // 业务UID
            String businessUid = IdUtil.fastSimpleUUID();

            CreativeContentCreateReqVO appCreateRequest = new CreativeContentCreateReqVO();

            // 随机获取一条执行参数
            CreativePlanExecuteDTO executeParam = SerializationUtils.clone(executeParamsList.get(RandomUtil.randomInt(executeParamsList.size())));

            // 获取应用步骤配置信息
            AppMarketRespVO appResponse = executeParam.getAppResponse();
            if (appResponse == null) {
                throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_APP_NOT_EXIST);
            }
            WorkflowConfigRespVO workflowConfig = appResponse.getWorkflowConfig();
            if (workflowConfig == null) {
                throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_CONFIG_NOT_NULL);
            }
            List<WorkflowStepWrapperRespVO> stepWrapperList = CollectionUtil.emptyIfNull(workflowConfig.getSteps());

            // 循环处理步骤
            for (WorkflowStepWrapperRespVO stepWrapper : stepWrapperList) {
                // 图片处理
                if (PosterActionHandler.class.getSimpleName().equals(stepWrapper.getFlowStep().getHandler())) {
                    VariableRespVO variable = stepWrapper.getVariable();
                    List<VariableItemRespVO> variables = variable.getVariables();
                    // 将变量列表转换为Map
                    Map<String, Object> variableMap = variables.stream()
                            .collect(Collectors.toMap(VariableItemRespVO::getField, VariableItemRespVO::getValue));

                    // 获取图片生成模式
                    String posterMode = String.valueOf(variableMap.getOrDefault(CreativeConstants.POSTER_MODE, PosterModeEnum.RANDOM.name()));
                    // 获取海报样式配置信息
                    PosterStyleDTO posterStyle = JSONUtil.toBean(String.valueOf(variableMap.getOrDefault(CreativeConstants.POSTER_STYLE, "{}")), PosterStyleDTO.class);
                    // 获取海报模板列表配置
                    List<PosterTemplateDTO> templateList = CollectionUtil.emptyIfNull(posterStyle.getTemplateList());

                    // 图片顺序插入
                    if (PosterModeEnum.SEQUENCE.name().equals(posterMode)) {
                        Integer maxImageCount = posterStyle.getMaxImageCount();
                        Map<Integer, List<String>> imageMap = CreativeUtils.splitImageListToMap(imageUrlList, maxImageCount);
                        List<String> imageList = imageMap.get(i % imageMap.size());
                        posterStyle.setImageMaterialList(imageList);
                    } else {
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
                        List<String> imageParamList = Lists.newArrayList();
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

            appCreateRequest.setPlanUid(plan.getUid());
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
            appCreateRequest.setIsTest(Boolean.FALSE);
            creativeContentCreateRequestList.add(appCreateRequest);
        }
        // 批量插入任务
        creativeContentService.create(creativeContentCreateRequestList);
    }

    /**
     * 处理创作内容执行参数
     *
     * @param plan 创作计划
     * @return 创作内容执行参数
     */
    private List<CreativePlanExecuteDTO> handlerCreativeContentExecuteParams(CreativePlanRespVO plan) {

        // 配置信息
        CreativePlanConfigurationDTO configuration = plan.getConfiguration();
        configuration.validate();

        /*
         * 改为一个创作计划只有一个创作方案
         */
        // 查询创作方案
        CreativeSchemeRespVO scheme = creativeSchemeService.get(configuration.getSchemeUid());
        // 获取配置并且校验
        CreativeSchemeConfigDTO schemeConfiguration = scheme.getConfiguration();
        schemeConfiguration.validate();

        // 处理创作内容执行参数
        List<CreativePlanExecuteDTO> list = Lists.newArrayList();

        // 获取计划配置的变量列表
        List<VariableItemRespVO> planVariableList = configuration.getVariableList();
        // 获取方案配置的步骤列表, 把计划配置的变量列表合并到方案配置的步骤列表中
        List<BaseSchemeStepDTO> schemeStepList = CreativeUtils.mergeSchemeStepVariable(schemeConfiguration.getSteps(), planVariableList);

        // 查询应用信息
        AppMarketRespVO appMarket = appMarketService.get(schemeConfiguration.getAppUid());

        // 获取海报步骤
        PosterSchemeStepDTO posterSchemeStep = CreativeUtils.getPosterSchemeStep(schemeStepList);

        // 如果没有海报步骤，直接创建一个执行参数
        if (Objects.isNull(posterSchemeStep)) {
            AppMarketRespVO app = CreativeUtils.handlerExecuteApp(schemeConfiguration, appMarket);
            CreativePlanExecuteDTO planExecute = new CreativePlanExecuteDTO();
            planExecute.setSchemeUid(scheme.getUid());
            planExecute.setSchemeMode(scheme.getMode());
            planExecute.setAppResponse(app);
            list.add(planExecute);
        }

        // 如果有海报步骤，则需要创建多个执行参数, 每一个海报参数创建一个执行参数
        for (PosterStyleDTO posterStyle : CollectionUtil.emptyIfNull(posterSchemeStep.getStyleList())) {
            AppMarketRespVO app = CreativeAppUtils.transformCustomExecute(schemeStepList, posterStyle, appMarket, configuration.getImageUrlList());
            CreativePlanExecuteDTO planExecute = new CreativePlanExecuteDTO();
            planExecute.setSchemeUid(scheme.getUid());
            planExecute.setSchemeMode(scheme.getMode());
            planExecute.setAppResponse(app);
            list.add(planExecute);
        }

        return list;
    }

    /**
     * 获取复制名称
     *
     * @param name 名称
     * @return 复制名称
     */
    private String getCopyName(String name) {
        String copyName = name + "-Copy";
        if (!creativePlanMapper.distinctName(copyName)) {
            return copyName;
        }
        return getCopyName(copyName);
    }

    /**
     * 处理并且校验
     *
     * @param request 创作计划请求
     */
    private void handlerAndValidate(CreativePlanReqVO request) {
        if (!CreativeTypeEnum.contains(request.getType())) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_TYPE_NOT_SUPPORTED, request.getType());
        }
        CreativePlanConfigurationDTO config = request.getConfiguration();
        AppValidate.notNull(config, CreativeErrorCodeConstants.PLAN_CONFIG_NOT_NULL, request.getName());
        config.validate();
    }
}
