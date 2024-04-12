package com.starcloud.ops.business.app.service.xhs.plan.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.bath.vo.request.CreativePlanBatchListReqVO;
import com.starcloud.ops.business.app.api.xhs.bath.vo.request.CreativePlanBatchReqVO;
import com.starcloud.ops.business.app.api.xhs.bath.vo.response.CreativePlanBatchRespVO;
import com.starcloud.ops.business.app.api.xhs.content.dto.CreativeContentExecuteParam;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentCreateReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentListReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractCreativeMaterialDTO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanConfigurationDTO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanListQuery;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanModifyReqVO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanPageQuery;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanReqVO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.convert.xhs.batch.CreativePlanBatchConvert;
import com.starcloud.ops.business.app.convert.xhs.plan.CreativePlanConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanDO;
import com.starcloud.ops.business.app.dal.mysql.xhs.plan.CreativePlanMapper;
import com.starcloud.ops.business.app.domain.entity.BaseAppEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.action.MaterialActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.PosterActionHandler;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentStatusEnum;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanStatusEnum;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.xhs.batch.CreativePlanBatchService;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.service.xhs.material.strategy.MaterialHandlerHolder;
import com.starcloud.ops.business.app.service.xhs.material.strategy.handler.AbstractMaterialHandler;
import com.starcloud.ops.business.app.service.xhs.material.strategy.metadata.MaterialMetadata;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanService;
import com.starcloud.ops.business.app.util.CreativeUtils;
import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
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
    private CreativePlanMapper creativePlanMapper;

    @Resource
    private CreativePlanBatchService creativePlanBatchService;

    @Resource
    private CreativeContentService creativeContentService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private AppMarketService appMarketService;

    @Resource
    private MaterialHandlerHolder materialHandlerHolder;

    /**
     * 创作计划元数据
     *
     * @return 元数据
     */
    @Override
    public Map<String, List<Option>> metadata() {
        return new HashMap<>();
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
    public List<CreativePlanRespVO> list(CreativePlanListQuery query) {
        List<CreativePlanDO> list = creativePlanMapper.list(query);
        return CreativePlanConvert.INSTANCE.convertList(list);
    }

    /**
     * 获取创作计划分页列表
     *
     * @param query 请求参数
     * @return 创作计划分页列表
     */
    @Override
    public PageResult<CreativePlanRespVO> page(CreativePlanPageQuery query) {
        IPage<CreativePlanDO> page = creativePlanMapper.page(query);
        if (Objects.isNull(page) || CollectionUtil.isEmpty(page.getRecords())) {
            return PageResult.empty();
        }

        List<CreativePlanRespVO> collect = page.getRecords()
                .stream()
                .map(CreativePlanConvert.INSTANCE::convertResponse)
                .collect(Collectors.toList());

        return PageResult.of(collect, page.getTotal());
    }

    /**
     * 获取创作计划详情，如果不存在则创建
     *
     * @param appUid 应用UID
     * @return 创作计划详情
     */
    @Override
    public CreativePlanRespVO getOrCreate(String appUid) {
        AppValidate.notBlank(appUid, "应用UID为必填项！");
        // 查询计划
        CreativePlanDO plan = creativePlanMapper.getByAppUid(appUid);
        // 查询应用
        AppMarketRespVO appMarketResponse = appMarketService.get(appUid);

        // 如果存在，则直接返回
        if (Objects.nonNull(plan)) {
            // 数据转换
            CreativePlanRespVO creativePlanResponse = CreativePlanConvert.INSTANCE.convertResponse(plan);
            // 处理配置信息
            CreativePlanConfigurationDTO configuration = creativePlanResponse.getConfiguration();

            // 海报图片风格处理
            List<PosterStyleDTO> imageStyleList = CreativeUtils.mergePosterStyle(configuration.getImageStyleList(), appMarketResponse);
            configuration.setImageStyleList(imageStyleList);

            // 应用配置处理
            AppMarketRespVO appInformation = CreativeUtils.mergeAppPosterStyleConfig(configuration.getAppInformation(), appMarketResponse);
            configuration.setAppInformation(appInformation);

            creativePlanResponse.setConfiguration(configuration);
            return creativePlanResponse;
        }

        // 新的创作计划配置
        CreativePlanConfigurationDTO configuration = CreativeUtils.assemblePlanConfiguration(appMarketResponse);

        // 创建一个计划
        CreativePlanDO creativePlan = new CreativePlanDO();
        creativePlan.setUid(IdUtil.fastSimpleUUID());
        creativePlan.setAppUid(appMarketResponse.getUid());
        creativePlan.setVersion(appMarketResponse.getVersion());
        creativePlan.setConfiguration(JsonUtils.toJsonString(configuration));
        creativePlan.setTags(JsonUtils.toJsonString(Collections.emptyList()));
        creativePlan.setTotalCount(3);
        creativePlan.setStatus(CreativePlanStatusEnum.PENDING.name());
        creativePlan.setDeleted(Boolean.FALSE);
        creativePlan.setCreateTime(LocalDateTime.now());
        creativePlan.setUpdateTime(LocalDateTime.now());
        creativePlanMapper.insert(creativePlan);
        return get(creativePlan.getUid());
    }

    /**
     * 创建创作计划
     *
     * @param request 创作计划请求
     */
    @Override
    public String create(CreativePlanReqVO request) {
        handlerAndValidate(request);
        CreativePlanDO plan = CreativePlanConvert.INSTANCE.convertCreateRequest(request);
        creativePlanMapper.insert(plan);
        return plan.getUid();
    }

    /**
     * 修改创作计划
     *
     * @param request 创作计划请求
     */
    @Override
    public String modify(CreativePlanModifyReqVO request) {

        // 基本校验
        AppValidate.notBlank(request.getUid(), CreativeErrorCodeConstants.PLAN_UID_REQUIRED);
        handlerAndValidate(request);

        // 查询创作计划，并且校验是否存在
        CreativePlanDO plan = creativePlanMapper.get(request.getUid());
        AppValidate.notNull(plan, CreativeErrorCodeConstants.PLAN_NOT_EXIST, request.getUid());

        // 校验创作计划状态，只能修改待执行、已完成、失败的创作计划
        if (!CreativePlanStatusEnum.canModifyStatus(plan.getStatus())) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_STATUS_NOT_SUPPORT_MODIFY);
        }

        // 更新创作计划
        CreativePlanDO modifyPlan = CreativePlanConvert.INSTANCE.convertModifyRequest(request);
        modifyPlan.setId(plan.getId());
        creativePlanMapper.updateById(modifyPlan);
        return modifyPlan.getUid();
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
        // 删除创作计划下的创作批次
        creativePlanBatchService.deleteByPlanUid(uid);
        // 删除创作计划下的创作内容
        creativeContentService.deleteByPlanUid(uid);
    }

    /**
     * 更新计划状态
     *
     * @param planUid 计划UID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePlanStatus(String planUid, String batchUid) {
        log.info("开始更新计划状态，planUid: {}", planUid);
        String key = "creative-plan-update-status-" + planUid;
        RLock lock = redissonClient.getLock(key);
        try {
            if (!lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                return;
            }

            // 创作计划批次状态更新
            creativePlanBatchService.updateStatus(batchUid);

            // 查询当前计划下所有的创作批次
            CreativePlanBatchListReqVO bathQuery = new CreativePlanBatchListReqVO();
            bathQuery.setPlanUid(planUid);
            List<CreativePlanBatchRespVO> batchList = CollectionUtil.emptyIfNull(creativePlanBatchService.list(bathQuery));

            // 查询当前计划下所有的创作内容
            CreativeContentListReqVO contentQuery = new CreativeContentListReqVO();
            contentQuery.setPlanUid(planUid);
            List<CreativeContentRespVO> contentList = CollectionUtil.emptyIfNull(creativeContentService.list(contentQuery));


            // 当前计划下的所有批次都是完成且所有任务全部执行成功的，则计划完成
            boolean bathComplete = batchList
                    .stream()
                    .allMatch(item -> CreativePlanStatusEnum.COMPLETE.name().equals(item.getStatus()));
            boolean contentComplete = contentList
                    .stream()
                    .allMatch(item -> CreativeContentStatusEnum.SUCCESS.name().equals(item.getStatus()));

            if (bathComplete && contentComplete) {
                log.info("将要更新计划为【完成】状态，planUid: {}", planUid);
                updateStatus(planUid, CreativePlanStatusEnum.COMPLETE.name());
                return;
            }

            // 当前计划下只要有彻底失败的，则计划失败
            boolean contentFailure = contentList
                    .stream()
                    .anyMatch(item -> CreativeContentStatusEnum.ULTIMATE_FAILURE.name().equals(item.getStatus()));
            if (contentFailure) {
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
     * 修改创作计划状态
     *
     * @param uid    创作计划UID
     * @param status 修改状态
     */
    public void updateStatus(String uid, String status) {

        // 基本校验
        AppValidate.notBlank(uid, CreativeErrorCodeConstants.PLAN_UID_REQUIRED);
        AppValidate.notBlank(status, CreativeErrorCodeConstants.PLAN_STATUS_REQUIRED);
        // 校验状态，状态必须是枚举值，且不能将状态修改为待执行
        if (!CreativePlanStatusEnum.containsAndPending(status)) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_STATUS_NOT_SUPPORT_MODIFY);
        }

        // 查询创作计划，并且校验是否存在
        CreativePlanDO plan = creativePlanMapper.get(uid);
        AppValidate.notNull(plan, CreativeErrorCodeConstants.PLAN_NOT_EXIST, uid);

        // 更新创作计划状态
        LambdaUpdateWrapper<CreativePlanDO> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(CreativePlanDO::getStatus, status);
        updateWrapper.set(CreativePlanDO::getUpdateTime, LocalDateTime.now());
        updateWrapper.eq(CreativePlanDO::getUid, uid);
        creativePlanMapper.update(updateWrapper);
    }

    /**
     * 执行创作计划
     *
     * @param uid 创作计划UID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(String uid) {
        RLock lock = redissonClient.getLock(uid);
        try {
            if (!lock.tryLock(1, TimeUnit.MINUTES)) {
                return;
            }
            doExecute(uid);
        } catch (InterruptedException e) {
            log.error("计划执行失败", e);
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_EXECUTE_FAILURE);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 执行创作计划
     *
     * @param uid 创作计划UID
     */
    public void doExecute(String uid) {
        // 基本校验
        AppValidate.notBlank(uid, CreativeErrorCodeConstants.PLAN_UID_REQUIRED);
        CreativePlanRespVO creativePlan = this.get(uid);

        // 批量执行随机任务  新增批次
        CreativePlanBatchReqVO bathRequest = CreativePlanBatchConvert.INSTANCE.convert(creativePlan);
        String batchUid = creativePlanBatchService.create(bathRequest);

        // 生成任务
        this.bathCreativeContent(creativePlan, batchUid);
        // 更新状态
        LambdaUpdateWrapper<CreativePlanDO> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(CreativePlanDO::getStatus, CreativePlanStatusEnum.RUNNING.name());
        updateWrapper.set(CreativePlanDO::getUpdateTime, LocalDateTime.now());
        updateWrapper.eq(CreativePlanDO::getUid, uid);
        creativePlanMapper.update(updateWrapper);
    }

    /**
     * 创建随机任务
     *
     * @param creativePlan 创作计划
     */
    @SuppressWarnings("all")
    public void bathCreativeContent(CreativePlanRespVO creativePlan, String batchUid) {

        // 获取到计划配置
        CreativePlanConfigurationDTO configuration = creativePlan.getConfiguration();
        configuration.validate();

        /*
         * 获取计划应用信息
         */
        // 获取计划应用信息
        AppMarketRespVO appMarket = configuration.getAppInformation();
        // 查询最新应用详细信息，内部有校验，进行校验应用是否存在
        AppMarketRespVO latestAppMarket = appMarketService.get(creativePlan.getAppUid());

        /*
         * 获取到素材库步骤，素材库类型，素材库处理器
         */
        // 素材库步骤校验
        WorkflowStepWrapperRespVO materialStepWrapper = appMarket.getStepByHandler(MaterialActionHandler.class.getSimpleName());
        AppValidate.notNull(materialStepWrapper, "创作计划应用配置异常，资料库步骤是必须的！请联系管理员！");
        // 获取素材库类型
        String materialType = materialStepWrapper.getStepVariableValue(CreativeConstants.MATERIAL_TYPE);
        AppValidate.notBlank(materialType, "创作计划应用配置异常，资料库步骤配置的变量{}是必须的！请联系管理员！", CreativeConstants.MATERIAL_TYPE);
        // 获取到具体的素材库类型枚举
        MaterialTypeEnum materialTypeEnum = MaterialTypeEnum.of(materialType);
        AppValidate.notNull(materialTypeEnum, "素材库类型不支持，请联系管理员{}！", materialType);
        // 获取资料库的具体处理器
        AbstractMaterialHandler materialHandler = materialHandlerHolder.getHandler(materialType);
        AppValidate.notNull(materialHandler, "素材库类型不支持，请联系管理员{}！", materialType);

        /*
         * 将配置信息平铺为，进行平铺，生成执行参数，方便后续进行随机。
         */
        List<CreativeContentExecuteParam> creativeContentExecuteList = CollectionUtil.newArrayList();

        // 获取海报步骤
        WorkflowStepWrapperRespVO posterStepWrapper = appMarket.getStepByHandler(PosterActionHandler.class.getSimpleName());
        // 获取到海报配置
        List<PosterStyleDTO> posterStyleList = configuration.getImageStyleList();
        // 如果没有海报步骤或者海报风格配置不存在，直接创建一个执行参数
        if (Objects.isNull(posterStepWrapper) || CollectionUtil.isEmpty(posterStyleList)) {
            CreativeContentExecuteParam planExecute = new CreativeContentExecuteParam();
            planExecute.setAppInformation(handlerExecuteApp(appMarket));
            creativeContentExecuteList.add(planExecute);
        }

        // 素材步骤的步骤ID
        String materialStepId = materialStepWrapper.getField();
        // 海报步骤的步骤ID
        String posterStepId = posterStepWrapper.getField();
        // 对海报风格配置进行合并处理，保持为最新。
        posterStyleList = CreativeUtils.mergePosterStyle(posterStyleList, latestAppMarket);

        // 如果有海报步骤，则需要创建多个执行参数, 每一个海报参数创建一个执行参数
        for (PosterStyleDTO posterStyle : posterStyleList) {
            if (!posterStyle.getEnable()) {
                continue;
            }
            // 校验海报样式
            materialHandler.validatePosterStyle(posterStyle);
            posterStyle = CreativeUtils.handlerPosterStyle(posterStyle);

            // 处理并且填充应用
            AppMarketRespVO appMarketResponse = handlerExecuteApp(appMarket);
            // 将处理后的应用填充到执行参数中
            Map<String, Object> variableMap = Collections.singletonMap(CreativeConstants.POSTER_STYLE, JsonUtils.toJsonString(posterStyle));
            appMarketResponse.putStepVariable(posterStepId, variableMap);

            CreativeContentExecuteParam planExecute = new CreativeContentExecuteParam();
            planExecute.setAppInformation(appMarketResponse);
            creativeContentExecuteList.add(planExecute);
        }

        /*
         * 根据总数创建批量任务
         */
        List<CreativeContentCreateReqVO> contentCreateRequestList = Lists.newArrayList();
        for (int index = 0; index < creativePlan.getTotalCount(); index++) {
            // 对执行参数进行取模，按照顺序取出执行参数。构建创作内容创建请求
            int sequenceInt = index % creativeContentExecuteList.size();
            CreativeContentExecuteParam contentExecuteRequest = SerializationUtils.clone(creativeContentExecuteList.get(sequenceInt));
            // 构造创作内容创建请求
            CreativeContentCreateReqVO createContentRequest = new CreativeContentCreateReqVO();
            createContentRequest.setPlanUid(creativePlan.getUid());
            createContentRequest.setBatchUid(batchUid);
            createContentRequest.setConversationUid(BaseAppEntity.createAppConversationUid());
            createContentRequest.setType(CreativeContentTypeEnum.ALL.name());
            createContentRequest.setTags(creativePlan.getTags());
            createContentRequest.setExecuteParam(contentExecuteRequest);
            contentCreateRequestList.add(createContentRequest);
        }

        // 如果海报步骤为空，直接创建任务返回即可。
        if (Objects.isNull(posterStepWrapper) || CollectionUtil.isEmpty(posterStyleList)) {
            creativeContentService.batchCreate(contentCreateRequestList);
            return;
        }

        /*
         * 二次处理批量任务
         */
        // 获取创作计划的素材配置
        List<AbstractCreativeMaterialDTO> materialList = CollectionUtil.emptyIfNull(configuration.getMaterialList());
        // 从创作内容任务列表中获取每个任务的海报配置，组成列表。总数为任务总数。
        List<PosterStyleDTO> contentPosterStyleList = getPosterStyleList(contentCreateRequestList, posterStepId);
        // 素材处理器进行素材处理
        Map<Integer, List<AbstractCreativeMaterialDTO>> materialMap = materialHandler.handleMaterialMap(materialList, contentPosterStyleList);
        // 二次处理批量内容任务
        for (int index = 0; index < contentCreateRequestList.size(); index++) {
            CreativeContentCreateReqVO contentCreateRequest = contentCreateRequestList.get(index);
            CreativeContentExecuteParam contentExecute = contentCreateRequest.getExecuteParam();
            // 获取应用，处理海报相关信息
            AppMarketRespVO appResponse = contentExecute.getAppInformation();
            if (Objects.isNull(posterStepWrapper)) {
                continue;
            }
            VariableItemRespVO posterStyleVariable = appResponse.getStepVariable(posterStepId, CreativeConstants.POSTER_STYLE);
            if (StringUtil.objectNotBlank(posterStyleVariable)) {
                // 获取海报风格
                PosterStyleDTO posterStyle = JsonUtils.parseObject(String.valueOf(posterStyleVariable.getValue()), PosterStyleDTO.class);
                // 获取到该风格下的素材列表
                List<AbstractCreativeMaterialDTO> handleMaterialList = materialMap.getOrDefault(index, Collections.emptyList());

                // 不同的处理器处理海报风格
                MaterialMetadata metadata = new MaterialMetadata();
                metadata.setMaterialType(materialType);
                metadata.setMaterialStepId(materialStepId);
                PosterStyleDTO style = materialHandler.handlePosterStyle(posterStyle, handleMaterialList, metadata);

                // 将处理后的海报风格填充到执行参数中
                Map<String, Object> variableMap = Collections.singletonMap(CreativeConstants.POSTER_STYLE, JsonUtils.toJsonString(style));
                appResponse.putStepVariable(posterStepId, variableMap);

                // 将素材库的素材列表填充上传素材步骤变量中
                Map<String, Object> handleMaterialMap = Collections.singletonMap(CreativeConstants.MATERIAL_LIST, JsonUtils.toJsonString(handleMaterialList));
                appResponse.putStepVariable(materialStepId, handleMaterialMap);
            }
            contentExecute.setAppInformation(appResponse);
            contentCreateRequest.setExecuteParam(contentExecute);
        }

        /*
         * 批量创建任务
         */
        creativeContentService.batchCreate(contentCreateRequestList);
    }

    /**
     * 处理执行的应用，参数填充等
     *
     * @param appMarketResponse 应用信息
     * @return 处理后的应用信息
     */
    @SuppressWarnings("all")
    private AppMarketRespVO handlerExecuteApp(AppMarketRespVO appMarketResponse) {
        // 复制一份，避免修改原应用的数据
        AppMarketRespVO appMarket = SerializationUtils.clone(appMarketResponse);

        return appMarket;
    }

    /**
     * 从内容任务中获取，海报配置信息。
     *
     * @param contentCreateRequestList 内容人物列表
     * @param posterStepId             海报步骤
     * @return 海报列表
     */
    @NotNull
    private static List<PosterStyleDTO> getPosterStyleList(List<CreativeContentCreateReqVO> contentCreateRequestList, String posterStepId) {
        return CollectionUtil.emptyIfNull(contentCreateRequestList)
                .stream()
                .map(item -> {
                    Optional<String> posterStyleOptional = Optional.ofNullable(item)
                            .map(CreativeContentCreateReqVO::getExecuteParam)
                            .map(CreativeContentExecuteParam::getAppInformation)
                            .map(appResponse -> appResponse.getStepVariableValue(posterStepId, CreativeConstants.POSTER_STYLE));

                    if (!posterStyleOptional.isPresent()) {
                        return null;
                    }

                    try {
                        return JsonUtils.parseObject(posterStyleOptional.get(), PosterStyleDTO.class);
                    } catch (Exception e) {
                        return null;
                    }

                })
                .collect(Collectors.toList());
    }

    /**
     * 处理并且校验
     *
     * @param request 创作计划请求
     */
    private void handlerAndValidate(CreativePlanReqVO request) {
        request.validate();
        CreativePlanConfigurationDTO configuration = request.getConfiguration();

        // 处理海报风格数据
        List<PosterStyleDTO> imageStyleList = CollectionUtil.emptyIfNull(configuration.getImageStyleList());
        List<PosterStyleDTO> styleList = CreativeUtils.preHandlerPosterStyleList(imageStyleList);

        configuration.setImageStyleList(styleList);
        request.setConfiguration(configuration);
    }
}
