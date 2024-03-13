package com.starcloud.ops.business.app.service.xhs.plan.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.image.dto.UploadImageInfoDTO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.content.dto.CreativeContentExecuteDTO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanConfigurationDTO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanModifyReqVO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanPageQuery;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanReqVO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.CreativeSchemeConfigurationDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.BaseSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.PosterSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.CreativeSchemeRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.response.CreativePlanBatchRespVO;
import com.starcloud.ops.business.app.convert.xhs.plan.CreativePlanConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanPO;
import com.starcloud.ops.business.app.dal.mysql.xhs.plan.CreativePlanMapper;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentStatusEnum;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanStatusEnum;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativeRandomTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativeTypeEnum;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.xhs.batch.CreativePlanBatchService;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanService;
import com.starcloud.ops.business.app.service.xhs.scheme.CreativeSchemeService;
import com.starcloud.ops.business.app.util.CreativeAppUtils;
import com.starcloud.ops.business.app.util.CreativeUploadUtils;
import com.starcloud.ops.business.app.util.CreativeUtils;
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.business.app.util.UserUtils;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
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
    private RedissonClient redissonClient;

    @Resource
    private AppMarketService appMarketService;

    @Resource
    private CreativePlanBatchService planBatchService;

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

        Map<String, CreativePlanBatchRespVO> planBatchRespMap = planBatchService.latestBatch(planUidList).stream().collect(Collectors.toMap(CreativePlanBatchRespVO::getPlanUid, Function.identity()));
        // 用户创建者ID列表。
        List<Long> creatorList = records.stream().map(item -> Long.valueOf(item.getCreator())).distinct().collect(Collectors.toList());
        // 获取用户创建者ID，昵称 Map。
        Map<Long, String> creatorMap = UserUtils.getUserNicknameMapByIds(creatorList);

        List<CreativePlanRespVO> collect = records.stream().map(item -> {
            CreativePlanRespVO response = CreativePlanConvert.INSTANCE.convertResponse(item);
            response.setCreator(creatorMap.get(Long.valueOf(item.getCreator())));

            CreativePlanBatchRespVO creativePlanBatchRespVO = planBatchRespMap.get(item.getUid());
            if (Objects.isNull(creativePlanBatchRespVO)) {
                response.setTotal(0);
                response.setSuccessCount(0);
                response.setFailureCount(0);
            } else {
                response.setTotal(creativePlanBatchRespVO.getTotalCount());
                response.setSuccessCount(creativePlanBatchRespVO.getSuccessCount());
                response.setFailureCount(creativePlanBatchRespVO.getFailureCount());
            }
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
        copyPlan.setTags(plan.getTags());
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
        if (!CreativePlanStatusEnum.PENDING.name().equals(plan.getStatus())
                && !CreativePlanStatusEnum.COMPLETE.name().equals(plan.getStatus())
                && !CreativePlanStatusEnum.FAILURE.name().equals(plan.getStatus())) {
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
    public void updatePlanStatus(String planUid, Long batch) {
        log.info("开始更新计划状态，planUid: {}", planUid);
        String key = "creative-plan-update-status-" + planUid;
        RLock lock = redissonClient.getLock(key);
        try {
            if (!lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                return;
            }
            // 更新批次状态
            planBatchService.updateCompleteStatus(planUid, batch);
            List<CreativeContentDO> contentList = CollectionUtil.emptyIfNull(creativeContentService.listByPlanUid(planUid, batch));
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
        RLock lock = redissonClient.getLock(uid);
        try {
            if (!lock.tryLock(1, TimeUnit.MINUTES)) {
                return;
            }
            doExecute(uid);
        } catch (InterruptedException e) {
            log.warn("InterruptedException");
        } finally {
            lock.unlock();
        }
    }

    public void doExecute(String uid) {
        // 基本校验
        AppValidate.notBlank(uid, CreativeErrorCodeConstants.PLAN_UID_REQUIRED);
        CreativePlanRespVO creativePlan = this.get(uid);
        // 校验是否有在执行的批次
        if (CreativePlanStatusEnum.RUNNING.name().equals(creativePlan.getStatus())
                || CreativePlanStatusEnum.PAUSE.name().equals(creativePlan.getStatus())) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_IS_EXECUTING, uid);
        }

        // 目前只支持随机执行
        if (!CreativeRandomTypeEnum.RANDOM.name().equals(creativePlan.getRandomType())) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_RANDOM_TYPE_NOT_SUPPORTED, creativePlan.getRandomType());
        }

        // 校验创作计划配置
        Optional.ofNullable(creativePlan.getConfiguration()).ifPresent(CreativePlanConfigurationDTO::validate);

        // 批量执行随机任务  新增批次
        long batch = System.currentTimeMillis();
        planBatchService.createBatch(batch, creativePlan);
        creativePlan.setBatch(batch);

        // 生成任务
        this.bathCreativeContent(creativePlan);
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
     * @param creativePlan 创作计划
     */
    private void bathCreativeContent(CreativePlanRespVO creativePlan) {


    }

    /**
     * 处理创作内容执行参数
     *
     * @param creativePlan 创作计划
     * @return 创作内容执行参数
     */
    private List<CreativeContentExecuteDTO> handlerCreativeContentExecuteParams(CreativePlanRespVO creativePlan) {

        // 获取计划配置信息
        CreativePlanConfigurationDTO configuration = creativePlan.getConfiguration();
        // 查询最新创作方案
        CreativeSchemeRespVO scheme = creativeSchemeService.get(configuration.getSchemeUid());
        // 获取配置并且校验
        CreativeSchemeConfigurationDTO schemeConfiguration = scheme.getConfiguration();
        schemeConfiguration.validate();
        // 查询应用详细信息
        AppMarketRespVO appMarket = appMarketService.get(schemeConfiguration.getAppUid());

        // 处理创作内容执行参数
        List<CreativeContentExecuteDTO> list = Lists.newArrayList();

        // 获取计划配置的变量列表
        List<VariableItemRespVO> planVariableList = configuration.getVariableList();
        // 获取方案配置的步骤列表, 把计划配置的变量列表合并到方案配置的步骤列表中
        List<BaseSchemeStepDTO> schemeStepList = CreativeUtils.mergeSchemeStepVariable(schemeConfiguration.getSteps(), planVariableList);

        // 获取海报步骤
        PosterSchemeStepDTO posterSchemeStep = CreativeUtils.getPosterSchemeStep(schemeStepList);

        // 如果没有海报步骤，直接创建一个执行参数
        if (Objects.isNull(posterSchemeStep)) {
            AppMarketRespVO app = CreativeUtils.handlerExecuteApp(schemeConfiguration, appMarket);
            CreativeContentExecuteDTO planExecute = new CreativeContentExecuteDTO();
            planExecute.setSchemeUid(scheme.getUid());
            planExecute.setAppResponse(app);
            list.add(planExecute);
        }

        // 如果有海报步骤，则需要创建多个执行参数, 每一个海报参数创建一个执行参数
        for (PosterStyleDTO posterStyle : CollectionUtil.emptyIfNull(posterSchemeStep.getStyleList())) {
            AppMarketRespVO app = CreativeAppUtils.transformCustomExecute(schemeStepList, posterStyle, appMarket, configuration.getImageUrlList());
            CreativeContentExecuteDTO planExecute = new CreativeContentExecuteDTO();
            planExecute.setSchemeUid(scheme.getUid());
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
        CreativePlanConfigurationDTO configuration = request.getConfiguration();
        AppValidate.notNull(configuration, CreativeErrorCodeConstants.PLAN_CONFIG_NOT_NULL, request.getName());
        configuration.validate();
    }
}
