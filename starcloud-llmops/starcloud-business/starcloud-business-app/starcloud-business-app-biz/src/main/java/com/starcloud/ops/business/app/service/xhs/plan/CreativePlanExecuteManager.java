package com.starcloud.ops.business.app.service.xhs.plan;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.verification.Verification;
import com.starcloud.ops.business.app.api.xhs.material.MaterialFieldConfigDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.request.CreativePlanBatchReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentCreateReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.PlanExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.convert.xhs.batch.CreativePlanBatchConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanDO;
import com.starcloud.ops.business.app.dal.mysql.xhs.plan.CreativePlanMapper;
import com.starcloud.ops.business.app.domain.entity.BaseAppEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.action.MaterialActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.PosterActionHandler;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialUsageModel;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanSourceEnum;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanStatusEnum;
import com.starcloud.ops.business.app.model.content.CreativeContentExecuteParam;
import com.starcloud.ops.business.app.model.plan.ContentBatchRequest;
import com.starcloud.ops.business.app.model.plan.CreativePlanConfigurationDTO;
import com.starcloud.ops.business.app.model.plan.PlanExecuteResult;
import com.starcloud.ops.business.app.model.plan.PlanTotalCount;
import com.starcloud.ops.business.app.model.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.service.xhs.batch.CreativePlanBatchService;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.service.xhs.material.CreativeMaterialManager;
import com.starcloud.ops.business.app.service.xhs.material.strategy.MaterialHandlerHolder;
import com.starcloud.ops.business.app.service.xhs.material.strategy.handler.AbstractMaterialHandler;
import com.starcloud.ops.business.app.service.xhs.material.strategy.metadata.MaterialMetadata;
import com.starcloud.ops.business.app.util.CreativeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 创作计划执行管理
 *
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CreativePlanExecuteManager {

    /**
     * 计划执行锁前缀
     */
    private static final String PLAN_EXECUTE_LOCK_PREFIX = "creative-plan-execute-";

    private final CreativePlanService creativePlanService;

    private final CreativePlanMapper creativePlanMapper;

    private final CreativeMaterialManager creativeMaterialManager;

    private final CreativePlanBatchService creativePlanBatchService;

    private final CreativeContentService creativeContentService;

    private final RedissonClient redissonClient;

    private final MaterialHandlerHolder materialHandlerHolder;

    /**
     * 获取创作计划锁的 Key。
     *
     * @param planUid 计划UID
     * @return 获取创作计划锁的 Key
     */
    private static String lockKey(String planUid) {
        return PLAN_EXECUTE_LOCK_PREFIX + planUid;
    }

    /**
     * 计划执行
     *
     * @param request 执行请求
     * @return 执行结果
     */
    @Transactional(rollbackFor = Exception.class)
    public PlanExecuteResult execute(PlanExecuteRequest request) {
        String planUid = request.getUid();
        AppValidate.notBlank(planUid, "计划执行失败：计划UID不能为空！");
        RLock lock = redissonClient.getLock(lockKey(planUid));

        try {
            // 尝试获取锁，如果一分钟内获取不到，抛出异常
            if (!lock.tryLock(1, TimeUnit.MINUTES)) {
                throw new InterruptedException();
            }

            // 获取并且校验计划
            CreativePlanRespVO planResponse = this.getAndValidate(request);

            // 创作内容任务数据整合处理
            ContentBatchRequest batchRequest = this.buildContentRequestList(planResponse);

            // 新增一条计划批次
            String batchUid = this.createPlanBatch(planResponse);

            // 批量创建创作内容任务
            this.batchCreateContent(batchRequest.getContentRequestList(), batchUid);

            // 更新批次状态为执行中
            this.updatePlanBatchExecuting(batchUid);

            // 更新计划状态为执行中
            this.updatePlanExecuting(planUid);

            // 返回执行结果
            PlanExecuteResult result = new PlanExecuteResult();
            result.setPlanUid(planUid);
            result.setBatchUid(batchUid);
            result.setWarning(batchRequest.getMessage());
            return result;
        } catch (InterruptedException e) {
            log.error("计划执行失败", e);
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_EXECUTE_FAILURE);
        } catch (ServiceException exception) {
            log.error("计划执行失败", exception);
            throw exception;
        } catch (Exception exception) {
            log.error("计划执行失败", exception);
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_EXECUTE_FAILURE, exception.getMessage());
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    /**
     * 获取并且校验计划
     *
     * @param request 计划UID
     * @return 计划
     */
    private CreativePlanRespVO getAndValidate(PlanExecuteRequest request) {

        CreativePlanRespVO planResponse = creativePlanService.get(request.getUid());

        // 计划状态，只能修改待执行、已完成、失败的创作计划
        if (!CreativePlanStatusEnum.canModifyStatus(planResponse.getStatus())) {
            throw ServiceExceptionUtil.invalidParamException("计划执行失败：计划正在执行中，请稍后重试...");
        }

        // 计划来源
        CreativePlanSourceEnum planSource = CreativePlanSourceEnum.of(planResponse.getSource());
        AppValidate.notNull(planSource, "计划执行失败败：不支持的计划来源！");

        // 计划配置校验
        CreativePlanConfigurationDTO configuration = planResponse.getConfiguration();
        AppValidate.notNull(configuration, "计划执行失败：计划配置不能为空！");

        // 处理一下计划配置信息
        this.handlerConfiguration(configuration, request);

        // 计划配置校验
        List< Verification > verifications = configuration.validate(request.getUid(), ValidateTypeEnum.EXECUTE);
        if (CollectionUtil.isNotEmpty(verifications)) {
            Verification verification = verifications.get(0);
            throw ServiceExceptionUtil.invalidParamException(verification.getMessage());
        }

        return planResponse;
    }

    /**
     * 处理计划配置信息
     *
     * @param configuration 计划配置信息
     * @param request       请求参数
     */
    private void handlerConfiguration(CreativePlanConfigurationDTO configuration, PlanExecuteRequest request) {
        // 获取请求参数
        Map<String, Object> params = MapUtil.emptyIfNull(request.getParams());

        // 获取计划应用信息
        AppMarketRespVO appInformation = configuration.getAppInformation();
        AppValidate.notNull(appInformation, "计划执行失败：应用配置信息不能为空！");
        // 获取全局变量步骤。如果没有全局变量步骤，则不进行处理
        WorkflowStepWrapperRespVO variableStepWrapper = CreativeUtils.getVariableStepWrapper(appInformation);
        if (Objects.isNull(variableStepWrapper)) {
            return;
        }

        // 将参数填充到全局变量步骤中
        params.forEach((key, value) -> {
            // 将参数填充到全局变量步骤中
            appInformation.putVariable(variableStepWrapper.getStepCode(), key, value);
        });

        configuration.setAppInformation(appInformation);
    }

    /**
     * 构建创作内容请求列表。
     *
     * @param planResponse 计划
     * @return 创作内容请求列表
     */
    private ContentBatchRequest buildContentRequestList(CreativePlanRespVO planResponse) {

        /*
         * 相关数据处理准备
         */
        // 处理数据并且获取素材库元数据
        MaterialMetadata metadata = this.handlerAndGetMetadata(planResponse);
        // 获取素材库处理器
        AbstractMaterialHandler handler = materialHandler(metadata.getMaterialType());
        // 获取素材库素材列表
        List<Map<String, Object>> materialList = this.materialList(planResponse);

        // 获取计划配置信息
        CreativePlanConfigurationDTO configuration = planResponse.getConfiguration();
        // 获取海报风格配置
        AppMarketRespVO appInformation = configuration.getAppInformation();
        // 获取图片风格列表
        List<PosterStyleDTO> posterStyleList = configuration.getImageStyleList();

        /*
         * 构建创作内容执行参数列表
         */
        // 构建创作内容执行参数列表
        List<CreativeContentExecuteParam> contentExecuteList = CollectionUtil.newArrayList();
        // 如果有海报步骤，则需要创建多个执行参数, 每一个海报参数创建一个执行参数
        for (PosterStyleDTO posterStyle : posterStyleList) {
            if (!posterStyle.getEnable()) {
                continue;
            }
            // 校验海报样式
            handler.validatePosterStyle(posterStyle);
            // 处理并且填充应用
            AppMarketRespVO appMarketResponse = this.handlerExecuteApp(appInformation);
            // 将处理后的应用填充到执行参数中
            appMarketResponse.putVariable(metadata.getPosterStepId(), CreativeConstants.POSTER_STYLE, JsonUtils.toJsonString(posterStyle));
            CreativeContentExecuteParam planExecute = new CreativeContentExecuteParam();
            planExecute.setAppInformation(appMarketResponse);
            contentExecuteList.add(planExecute);
        }

        /*
         * 计算需要生成的任务总数
         */
        String message = "";
        // 计算需要生成的任务总数
        int totalCount = planResponse.getTotalCount();
        // 如果是选择执行，重新计算任务总数。
        if (MaterialUsageModel.SELECT.equals(metadata.getMaterialUsageModel())) {
            // 根据素材总数和风格进行计算可以生产任务的总数。
            PlanTotalCount calculated = handler.calculateTotalCount(materialList, posterStyleList);
            totalCount = calculated.getTotal();
            message = calculated.getWarning();
        }

        /*
         * 根据总数创建批量任务
         */
        List<CreativeContentCreateReqVO> contentRequestList = Lists.newArrayList();
        for (int index = 0; index < totalCount; index++) {
            // 对执行参数进行取模，按照顺序取出执行参数。构建创作内容创建请求
            int sequenceInt = index % contentExecuteList.size();
            CreativeContentExecuteParam contentExecuteParam = SerializationUtils.clone(contentExecuteList.get(sequenceInt));
            CreativeContentCreateReqVO contentRequest = this.buildContentRequest(planResponse, contentExecuteParam);
            contentRequestList.add(contentRequest);
        }

        // 从创作内容任务列表中获取每个任务的海报配置，组成列表。总数为任务总数。
        List<PosterStyleDTO> handlerPosterStyleList = getPosterStyleListFromRequest(contentRequestList, metadata.getPosterStepId());
        // 对每一个风格进行素材的分配
        Map<Integer, List<Map<String, Object>>> materialMap = handler.handleMaterialMap(materialList, handlerPosterStyleList, metadata);

        /*
         * 二次处理创作内容请求列表
         */
        List<CreativeContentCreateReqVO> handleContentRequestList = Lists.newArrayList();
        for (int index = 0; index < contentRequestList.size(); index++) {
            CreativeContentCreateReqVO contentCreateRequest = contentRequestList.get(index);
            CreativeContentExecuteParam contentExecute = contentCreateRequest.getExecuteParam();
            // 获取应用，处理海报相关信息
            AppMarketRespVO executeAppInformation = contentExecute.getAppInformation();
            // 获取待执行的海报风格
            PosterStyleDTO posterStyle = this.getExecutePosterStyle(metadata.getPosterStepId(), executeAppInformation);
            if (Objects.isNull(posterStyle)) {
                continue;
            }
            // 获取到该风格下的素材列表
            List<Map<String, Object>> handleMaterialList = materialMap.getOrDefault(index, Collections.emptyList());
            // 不同的处理器处理海报风格
            PosterStyleDTO style = handler.handlePosterStyle(posterStyle, handleMaterialList, metadata);
            // 将处理后的海报风格填充到执行参数中
            executeAppInformation.putVariable(metadata.getPosterStepId(), CreativeConstants.POSTER_STYLE, JsonUtils.toJsonString(style));
            // 将素材库的素材列表填充上传素材步骤变量中
            executeAppInformation.putVariable(metadata.getMaterialStepId(), CreativeConstants.MATERIAL_LIST, JsonUtils.toJsonString(handleMaterialList));
            // 更新执行参数
            contentExecute.setAppInformation(executeAppInformation);
            contentCreateRequest.setExecuteParam(contentExecute);
            handleContentRequestList.add(contentCreateRequest);
        }

        ContentBatchRequest contentBatchRequest = new ContentBatchRequest();
        contentBatchRequest.setMessage(message);
        contentBatchRequest.setContentRequestList(handleContentRequestList);
        return contentBatchRequest;
    }

    /**
     * 创建一条计划批次
     *
     * @param plan 计划
     * @return 批次UID
     */
    private String createPlanBatch(CreativePlanRespVO plan) {
        // 新增一条计划批次
        CreativePlanBatchReqVO batchRequest = CreativePlanBatchConvert.INSTANCE.convert(plan);
        return creativePlanBatchService.create(batchRequest);
    }

    /**
     * 批量创建创作内容任务
     *
     * @param contentRequestList 创作内容请求列表
     * @param batchUid           批次UID
     */
    private void batchCreateContent(List<CreativeContentCreateReqVO> contentRequestList, String batchUid) {
        for (CreativeContentCreateReqVO request : CollectionUtil.emptyIfNull(contentRequestList)) {
            request.setBatchUid(batchUid);
        }
        creativeContentService.batchCreate(contentRequestList);
    }

    /**
     * 更新批次状态为执行中
     *
     * @param batchUid 批次UID
     */
    private void updatePlanBatchExecuting(String batchUid) {
        creativePlanBatchService.startBatch(batchUid);
    }

    /**
     * 更新创作计划状态为执行中
     *
     * @param planUid 计划UID
     */
    private void updatePlanExecuting(String planUid) {
        LambdaUpdateWrapper<CreativePlanDO> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(CreativePlanDO::getStatus, CreativePlanStatusEnum.RUNNING.name());
        updateWrapper.set(CreativePlanDO::getUpdateTime, LocalDateTime.now());
        updateWrapper.eq(CreativePlanDO::getUid, planUid);
        creativePlanMapper.update(updateWrapper);
    }

    /**
     * 获取待执行的应用并且进行处理
     *
     * @param planResponse 待执行的应用信息
     * @return 处理后的应用信息
     */
    private AppMarketRespVO handlerAppInformation(CreativePlanRespVO planResponse) {
        // 获取计划配置信息
        CreativePlanConfigurationDTO configuration = planResponse.getConfiguration();
        // 获取计划应用信息
        AppMarketRespVO appInformation = configuration.getAppInformation();
        // 查询最新应用详细信息，内部有校验，进行校验应用是否存在
        AppMarketRespVO latestAppMarket = creativePlanService.getAppInformation(planResponse.getAppUid(), planResponse.getSource());
        // 合并应用市场配置，某一些配置项需要保持最新
        AppMarketRespVO app = CreativeUtils.mergeAppInformation(appInformation, latestAppMarket);
        configuration.setAppInformation(app);
        planResponse.setConfiguration(configuration);
        return app;
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
     * 构建素材库元数据
     *
     * @param planResponse 计划
     * @return 素材库元数据
     */
    private MaterialMetadata handlerAndGetMetadata(CreativePlanRespVO planResponse) {

        // 计划配置信息
        CreativePlanConfigurationDTO configuration = planResponse.getConfiguration();
        // 处理海报风格配置
        List<PosterStyleDTO> posterStyleList = this.posterStyleList(configuration);

        // 获取计划应用信息
        AppMarketRespVO appInformation = this.handlerAppInformation(planResponse);

        // 获取素材上传步骤
        WorkflowStepWrapperRespVO materialStepWrapper = this.materialStepWrapper(appInformation);
        // 素材步骤的步骤ID
        String materialStepId = materialStepWrapper.getStepCode();

        // 获取海报生成步骤
        WorkflowStepWrapperRespVO posterStepWrapper = this.posterStepWrapper(appInformation);
        // 海报步骤的步骤ID
        String posterStepId = posterStepWrapper.getStepCode();

        // 素材字段配置列表
        List<MaterialFieldConfigDTO> materialFieldList = this.materialFieldList(planResponse);

        // 获取业务类型
        String businessType = businessType(planResponse, materialStepWrapper, appInformation);
        appInformation.putVariable(materialStepId, CreativeConstants.BUSINESS_TYPE, businessType);

        // 获取到素材使用模式
        MaterialUsageModel materialUsageModel = materialUsageModel(materialStepWrapper);
        appInformation.putVariable(materialStepId, CreativeConstants.MATERIAL_USAGE_MODEL, materialUsageModel.name());

        // 更新计划信息
        configuration.setAppInformation(appInformation);
        configuration.setImageStyleList(posterStyleList);
        planResponse.setConfiguration(configuration);

        // 构建素材库元数据
        MaterialMetadata materialMetadata = new MaterialMetadata();
        materialMetadata.setPlanUid(planResponse.getUid());
        materialMetadata.setAppUid(appInformation.getUid());
        materialMetadata.setUserId(SecurityFrameworkUtils.getLoginUserId());
        materialMetadata.setPlanSource(CreativePlanSourceEnum.of(planResponse.getSource()));
        materialMetadata.setMaterialType(businessType);
        materialMetadata.setMaterialStepId(materialStepId);
        materialMetadata.setPosterStepId(posterStepId);
        materialMetadata.setMaterialUsageModel(materialUsageModel);
        materialMetadata.setMaterialFieldList(materialFieldList);

        return materialMetadata;
    }

    /**
     * 获取素材列表
     *
     * @param planResponse 计划
     * @return 素材列表
     */
    private List<Map<String, Object>> materialList(CreativePlanRespVO planResponse) {
        List<Map<String, Object>> materialList = creativeMaterialManager.getMaterialList(planResponse);
        // 素材库步骤不为空的话，上传素材不能为空
        AppValidate.notEmpty(materialList, "计划执行失败：素材列表不能为空，请上传或选择素材后重试！");
        return materialList;
    }

    /**
     * 获取上传素材步骤
     *
     * @param appInformation 应用信息
     * @return 上传素材步骤
     */
    private WorkflowStepWrapperRespVO materialStepWrapper(AppMarketRespVO appInformation) {
        WorkflowStepWrapperRespVO materialStepWrapper = appInformation.getStepByHandler(MaterialActionHandler.class);
        AppValidate.notNull(materialStepWrapper, "创作计划执行失败，素材上传步骤是必须的！请检查您的配置或联系管理员！");
        return materialStepWrapper;
    }

    /**
     * 获取素材字段配置信息
     *
     * @param planResponse 计划
     * @return 素材字段配置信息
     */
    private List<MaterialFieldConfigDTO> materialFieldList(CreativePlanRespVO planResponse) {
        try {
            List<MaterialFieldConfigDTO> materialFieldList = CreativeUtils.getMaterialFieldByStepWrapper(planResponse);
            AppValidate.notEmpty(materialFieldList, "计划执行失败：素材字段配置不能为空，请联系管理员！");
            return materialFieldList;
        } catch (ServiceException exception) {
            log.error("获取素材字段配置失败", exception);
            throw ServiceExceptionUtil.invalidParamException(exception.getMessage());
        } catch (Exception exception) {
            log.error("获取素材字段配置失败", exception);
            throw ServiceExceptionUtil.invalidParamException("计划执行失败：获取素材字段配置失败，请联系管理员！");
        }
    }

    /**
     * 获取业务类型
     *
     * @param planResponse        计划
     * @param materialStepWrapper 素材步骤
     * @param appInformation      应用信息
     * @return 业务类型
     */
    private String businessType(CreativePlanRespVO planResponse, WorkflowStepWrapperRespVO materialStepWrapper, AppMarketRespVO appInformation) {
        // 获取素材库类型
        String businessType = materialStepWrapper.getVariableToString(CreativeConstants.BUSINESS_TYPE);

        boolean isPicture;
        // 判断修改业务类型
        if (CreativePlanSourceEnum.isApp(planResponse.getSource())) {
            isPicture = CreativeUtils.judgePicture(appInformation.getUid());
        } else {
            isPicture = CreativeUtils.judgePicture(planResponse.getUid());
        }

        businessType = isPicture ? CreativeConstants.PICTURE : businessType;
        return businessType;
    }

    /**
     * 素材库使用模式
     *
     * @param materialStepWrapper 素材步骤
     * @return 素材库使用模式
     */
    private MaterialUsageModel materialUsageModel(WorkflowStepWrapperRespVO materialStepWrapper) {
        return CreativeUtils.getMaterialUsageModelByStepWrapper(materialStepWrapper);
    }

    /**
     * 获取到素材库处理器
     *
     * @param businessType 业务类型
     * @return 素材库处理器
     */
    private AbstractMaterialHandler materialHandler(String businessType) {
        AbstractMaterialHandler materialHandler = materialHandlerHolder.getHandler(businessType);
        AppValidate.notNull(materialHandler, "计划执行失败：素材库类型不支持，请联系管理员{}！", businessType);
        return materialHandler;
    }

    /**
     * 获取图片生成步骤
     *
     * @param appInformation 应用信息
     * @return 图片生成步骤
     */
    private WorkflowStepWrapperRespVO posterStepWrapper(AppMarketRespVO appInformation) {
        WorkflowStepWrapperRespVO posterStepWrapper = appInformation.getStepByHandler(PosterActionHandler.class);
        AppValidate.notNull(posterStepWrapper, "创作计划执行失败，图片生成步骤是必须的！请检查您的配置或联系管理员！");
        return posterStepWrapper;
    }

    /**
     * 获取待执行的海报风格
     *
     * @param posterStepId   海报步骤ID
     * @param appInformation 应用信息
     * @return 海报风格
     */
    private PosterStyleDTO getExecutePosterStyle(String posterStepId, AppMarketRespVO appInformation) {
        String posterStyleString = appInformation.getVariableToString(posterStepId, CreativeConstants.POSTER_STYLE);
        if (StringUtils.isBlank(posterStyleString) || "{}".equals(posterStyleString) || "null".equalsIgnoreCase(posterStyleString)) {
            return null;
        }

        try {
            return JsonUtils.parseObject(posterStyleString, PosterStyleDTO.class);
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * 获取创作内容任务的海报风格列表
     *
     * @param configuration 创作计划配置
     * @return 海报风格列表
     */
    private List<PosterStyleDTO> posterStyleList(CreativePlanConfigurationDTO configuration) {
        List<PosterStyleDTO> posterStyleList = configuration.getImageStyleList();

        posterStyleList = CreativeUtils.mergeImagePosterStyleList(posterStyleList, configuration.getAppInformation());
        return CreativeUtils.preHandlerPosterStyleList(posterStyleList);
    }

    /**
     * 从内容任务中获取，海报配置信息。
     *
     * @param contentCreateRequestList 内容人物列表
     * @param posterStepId             海报步骤
     * @return 海报列表
     */
    @NotNull
    private List<PosterStyleDTO> getPosterStyleListFromRequest(List<CreativeContentCreateReqVO> contentCreateRequestList, String posterStepId) {
        return CollectionUtil.emptyIfNull(contentCreateRequestList).stream().map(item -> {
            Optional<String> posterStyleOptional = Optional.ofNullable(item).map(CreativeContentCreateReqVO::getExecuteParam).map(CreativeContentExecuteParam::getAppInformation).map(appResponse -> appResponse.getVariableToString(posterStepId, CreativeConstants.POSTER_STYLE));

            if (!posterStyleOptional.isPresent()) {
                return null;
            }

            try {
                return JsonUtils.parseObject(posterStyleOptional.get(), PosterStyleDTO.class);
            } catch (Exception e) {
                return null;
            }

        }).collect(Collectors.toList());
    }

    /**
     * 构造创作内容创建请求
     *
     * @param planResponse        计划
     * @param contentExecuteParam 执行参数
     * @return 创作内容创建请求
     */
    private CreativeContentCreateReqVO buildContentRequest(CreativePlanRespVO planResponse, CreativeContentExecuteParam contentExecuteParam) {

        // 构造创作内容创建请求
        CreativeContentCreateReqVO createContentRequest = new CreativeContentCreateReqVO();
        createContentRequest.setPlanUid(planResponse.getUid());
        createContentRequest.setConversationUid(BaseAppEntity.createAppConversationUid());
        createContentRequest.setType(CreativeContentTypeEnum.ALL.name());
        createContentRequest.setSource(planResponse.getSource());
        createContentRequest.setExecuteParam(contentExecuteParam);
        return createContentRequest;
    }
}
