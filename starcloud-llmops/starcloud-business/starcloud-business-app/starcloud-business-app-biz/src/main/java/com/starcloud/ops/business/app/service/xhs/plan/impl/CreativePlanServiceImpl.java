package com.starcloud.ops.business.app.service.xhs.plan.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.image.dto.UploadImageInfoDTO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.verification.Verification;
import com.starcloud.ops.business.app.api.xhs.material.MaterialFieldConfigDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.response.CreativePlanBatchRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.CreateSameAppReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.CreativePlanGetQuery;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.CreativePlanListQuery;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.CreativePlanModifyReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.CreativePlanPageQuery;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.CreativePlanUpgradeReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.convert.xhs.plan.CreativePlanConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanDTO;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanMaterialDO;
import com.starcloud.ops.business.app.dal.mysql.xhs.plan.CreativePlanMapper;
import com.starcloud.ops.business.app.dal.mysql.xhs.plan.CreativePlanMaterialMapper;
import com.starcloud.ops.business.app.domain.entity.workflow.action.MaterialActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.PosterActionHandler;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.enums.materiallibrary.MaterialBindTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanSourceEnum;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanStatusEnum;
import com.starcloud.ops.business.app.model.plan.CreativePlanConfigurationDTO;
import com.starcloud.ops.business.app.model.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.recommend.RecommendStepWrapperFactory;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.xhs.batch.CreativePlanBatchService;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.service.xhs.material.CreativeMaterialManager;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanService;
import com.starcloud.ops.business.app.util.CreativeUtils;
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import com.starcloud.ops.business.app.verification.VerificationUtils;
import com.starcloud.ops.framework.common.api.dto.Option;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private CreativePlanMaterialMapper creativePlanMaterialMapper;

    @Resource
    private CreativeMaterialManager creativeMaterialManager;

    @Resource
    private CreativePlanBatchService creativePlanBatchService;

    @Resource
    private CreativeContentService creativeContentService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private AppService appService;

    @Resource
    private AppMarketService appMarketService;

    @Resource
    private TransactionTemplate transactionTemplate;


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
     * 上传图片
     *
     * @param image 上传图片
     * @return 图片信息
     */
    @Override
    public UploadImageInfoDTO uploadImage(MultipartFile image) {
        return ImageUploadUtils.uploadImage(image, ImageUploadUtils.UPLOAD_PATH);
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
     * 创作计划集合
     *
     * @return 创作计划集合
     */
    @Override
    public List<CreativePlanRespVO> list(Integer limit) {
        List<CreativePlanDTO> list = creativePlanMapper.list(WebFrameworkUtils.getLoginUserId().toString(), limit);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().map(CreativePlanConvert.INSTANCE::convert)
                .collect(Collectors.toList());
    }


    @Override
    public List<CreativePlanRespVO> list(CreativePlanListQuery query) {

        query.setUserId(WebFrameworkUtils.getLoginUserId());

        List<CreativePlanDTO> list = creativePlanMapper.query(query);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().map(CreativePlanConvert.INSTANCE::convert)
                .collect(Collectors.toList());
    }

    /**
     * 获取创作计划详情，如果不存在则创建
     *
     * @param query 应用UID
     * @return 创作计划详情
     */
    @Override
    public CreativePlanRespVO getOrCreate(CreativePlanGetQuery query) {
        AppValidate.notBlank(query.getAppUid(), "应用UID为必填项！");
        /*
         * 1. 创作计划UID如果不存在：直接使用应用UID查询创作计划：对应从应用市场进入创作计划场景。
         *    1.1 每个用户只能看到自己对应应用的创作计划。
         *    1.2 如果不存在，则创建一个创作计划：对应从应用市场进入创作计划场景。
         *    1.3 如果存在，则处理数据后返回。
         *
         * 2. 创作计划UID如果存在：直接使用创作计划UID查询创作计划：对应直接输入URL进行查询
         *    只用框架的数据权限控制，不需要额外处理。
         *    2.1：如果用户是管理员，则可以查询到该创作计划。处理数据后返回。查询不到直接抛出异常。
         *    2.2：如果用户是普通用户，则只能查询到自己的创作计划。处理数据后返回。查询不到直接抛出异常。
         */
        CreativePlanDO plan;
        if (StringUtils.isBlank(query.getUid())) {
            if (CreativePlanSourceEnum.isApp(query.getSource())) {
                // 计划-应用：一对一对应关系
                plan = creativePlanMapper.getByAppUid(query.getAppUid(), query.getSource());
            } else {
                // 计划-应用市场：一对多对应关系
                Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
                plan = creativePlanMapper.getByAppUid(query.getAppUid(), loginUserId, query.getSource());
            }
        } else {
            plan = creativePlanMapper.get(query.getUid());
            AppValidate.notNull(plan, "创作计划不存在！UID: {}", query.getUid());
        }

        // 查询应用
        AppMarketRespVO appMarketResponse = this.getAppInformation(query.getAppUid(), query.getSource());

        // 如果存在，则直接返回
        if (Objects.nonNull(plan)) {
            // 数据转换
            CreativePlanRespVO creativePlanResponse = CreativePlanConvert.INSTANCE.convertResponse(plan);
            // 处理配置信息
            CreativePlanConfigurationDTO configuration = creativePlanResponse.getConfiguration();

            // 合并应用市场配置，某一些配置项需要保持最新
            AppMarketRespVO appInformation = CreativeUtils.mergeAppInformation(configuration.getAppInformation(), appMarketResponse);
            appInformation.supplementStepVariable(RecommendStepWrapperFactory.getStepVariable());
            configuration.setAppInformation(appInformation);
            // 迁移旧素材数据
            CreativePlanMaterialDO planMaterialDO = creativePlanMaterialMapper.getMaterial(plan.getUid());
            if (AppTypeEnum.MEDIA_MATRIX.name().equals(appInformation.getType()) &&
                    CollectionUtil.isNotEmpty(planMaterialDO.getMaterialList())) {
                WorkflowStepWrapperRespVO stepByHandler = appInformation.getStepByHandler(MaterialActionHandler.class.getSimpleName());
                if (Objects.nonNull(stepByHandler)) {
                    if (CreativePlanSourceEnum.MARKET.name().equalsIgnoreCase(query.getSource())) {
                        // 应用市场从数据库迁移素材库
                        creativeMaterialManager.migrateFromData(appInformation.getName(), plan.getUid(),
                                MaterialBindTypeEnum.CREATION_PLAN.getCode(), stepByHandler, planMaterialDO.getMaterialList(), Long.valueOf(plan.getCreator()));
                        planMaterialDO.setMaterialList(Collections.emptyList());
                    } else if (CreativePlanSourceEnum.APP.name().equalsIgnoreCase(query.getSource())) {
                        // 我的应用 执行计划使用同一个素材库 不需要单独copy
                        planMaterialDO.setMaterialList(Collections.emptyList());
                    }
                    planMaterialDO.setConfiguration(JsonUtils.toJsonString(configuration));
                    creativePlanMaterialMapper.updateById(planMaterialDO);
                }
            } else if (AppTypeEnum.MEDIA_MATRIX.name().equals(appInformation.getType()) &&
                    CollectionUtil.isEmpty(planMaterialDO.getMaterialList())) {
                WorkflowStepWrapperRespVO stepByHandler = appInformation.getStepByHandler(MaterialActionHandler.class.getSimpleName());
                if (Objects.nonNull(stepByHandler)) {
                    String stepVariableValue = stepByHandler.getVariableToString(CreativeConstants.LIBRARY_QUERY);
                    if (CreativePlanSourceEnum.APP.name().equalsIgnoreCase(query.getSource())) {
                        // 我的应用 执行计划使用同一个素材库 不需要单独copy
                        planMaterialDO.setMaterialList(Collections.emptyList());
                        creativePlanMaterialMapper.updateById(planMaterialDO);
                    } else if (StringUtils.isNotBlank(stepVariableValue)) {
                        // 从变量配置中迁移
                        creativeMaterialManager.migrateFromConfig(appInformation.getName(), plan.getUid(),
                                MaterialBindTypeEnum.CREATION_PLAN.getCode(), stepVariableValue, Long.valueOf(plan.getCreator()));
                        stepByHandler.putVariable(CreativeConstants.LIBRARY_QUERY, "");
                        planMaterialDO.setConfiguration(JsonUtils.toJsonString(configuration));
                        creativePlanMaterialMapper.updateById(planMaterialDO);
                    } else {
                        // 没有数据库配置和变量配置 新建一个空素材库
                        creativeMaterialManager.createEmptyLibrary(appInformation.getName(), plan.getUid(),
                                MaterialBindTypeEnum.CREATION_PLAN.getCode(), Long.valueOf(plan.getCreator()));
                    }
                }
            }

            // 使海报风格配置保持最新，直接从 appInformation 获取，需要保证上面已经是把最新的数据更新到 appInformation 中了。
            List<PosterStyleDTO> imageStyleList = CreativeUtils.mergeImagePosterStyleList(configuration.getImageStyleList(), appInformation);
            configuration.setImageStyleList(imageStyleList);

            creativePlanResponse.setConfiguration(configuration);
            return creativePlanResponse;
        }

        // 新的创作计划配置
        CreativePlanConfigurationDTO configuration = CreativeUtils.assemblePlanConfiguration(appMarketResponse, query.getSource());

        // 创建一个计划
        CreativePlanMaterialDO creativePlan = new CreativePlanMaterialDO();
        creativePlan.setUid(IdUtil.fastSimpleUUID());
        creativePlan.setAppUid(appMarketResponse.getUid());
        creativePlan.setVersion(appMarketResponse.getVersion());
        creativePlan.setSource(query.getSource());
        creativePlan.setConfiguration(JsonUtils.toJsonString(configuration));
        creativePlan.setTotalCount(3);
        creativePlan.setStatus(CreativePlanStatusEnum.PENDING.name());
        creativePlan.setDeleted(Boolean.FALSE);
        creativePlan.setCreateTime(LocalDateTime.now());
        creativePlan.setUpdateTime(LocalDateTime.now());

        if (CreativePlanSourceEnum.MARKET.name().equalsIgnoreCase(query.getSource())) {
            // 应用市场新建执行计划 copy 素材库
            creativeMaterialManager.upgradeMaterialLibrary(appMarketResponse.getUid(), creativePlan, appMarketResponse.getName());
        }

        creativePlanMaterialMapper.insert(creativePlan);
        return get(creativePlan.getUid());
    }

    /**
     * 创建同款应用
     *
     * @param request 创作计划请求
     */
    @Override
    public String createSameApp(CreateSameAppReqVO request) {
        // 如果useAppMarket为空或者为true时使用应用最新市场配置
        if (Objects.isNull(request.getUseAppMarket()) || request.getUseAppMarket()) {
            return appMarketService.createSameApp(request.getAppMarketUid());
        }
        return "";
    }

    /**
     * 修改创作计划
     *
     * @param request 创作计划请求
     */
    @Override
    public CreativePlanRespVO modify(CreativePlanModifyReqVO request) {
        request.setValidateType(ValidateTypeEnum.UPDATE.name());
        // 处理并且校验请求
        List<Verification> verifications = request.validate();

        // 查询创作计划，并且校验是否存在
        CreativePlanDO plan = creativePlanMapper.get(request.getUid());
        VerificationUtils.notNullCreative(verifications, plan, request.getUid(), "创作计划更新失败！应用创作计划不存在！UID: " + request.getUid());

        // 更新创作计划
        CreativePlanMaterialDO modifyPlan = CreativePlanConvert.INSTANCE.convertModifyRequest(request);
        modifyPlan.setId(plan.getId());
        creativePlanMaterialMapper.updateById(modifyPlan);

        CreativePlanDO creativePlan = creativePlanMapper.get(request.getUid());
        CreativePlanRespVO planResponse = CreativePlanConvert.INSTANCE.convertResponse(creativePlan);
        planResponse.setVerificationList(verifications);

        return planResponse;
    }

    /**
     * 修改配置
     *
     * @param request 创作计划请求
     * @return 创作计划UID
     */
    @Override
    public CreativePlanRespVO modifyConfiguration(CreativePlanModifyReqVO request) {
        request.setValidateType(ValidateTypeEnum.CONFIG.name());
        // 处理并且校验请求
        List<Verification> verifications = request.validate();

        CreativePlanDO plan = creativePlanMapper.get(request.getUid());
        VerificationUtils.notNullCreative(verifications, plan, request.getUid(), "创作计划更新失败！应用创作计划不存在！UID: " + request.getUid());

        CreativePlanDO modifyPlan = new CreativePlanDO();
        // 素材单独存放
        request.getConfiguration().setMaterialList(null);
        modifyPlan.setConfiguration(JsonUtils.toJsonString(request.getConfiguration()));
        modifyPlan.setId(plan.getId());
        creativePlanMapper.updateById(modifyPlan);

        CreativePlanDO creativePlan = creativePlanMapper.get(request.getUid());
        CreativePlanRespVO planResponse = CreativePlanConvert.INSTANCE.convertResponse(creativePlan);
        planResponse.setVerificationList(verifications);

        return planResponse;
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

    @Override
    public void deleteByAppUid(String appUid) {
        if (StringUtils.isBlank(appUid)) {
            return;
        }
        List<String> planUids = creativePlanMapper.getPlanUid(appUid);
        if (CollectionUtil.isEmpty(planUids)) {
            return;
        }
        for (String planUid : planUids) {
            delete(planUid);
        }
    }

    /**
     * 更新计划状态
     *
     * @param planUid 计划UID
     */
    @Override
    public void updatePlanStatus(String planUid, String batchUid) {
        log.info("更新计划状态【开始】，planUid: {},batchUid= {}", planUid, batchUid);
        String key = "creative-plan-update-status-" + planUid + "-" + batchUid;
        RLock lock = redissonClient.getLock(key);
        try {
            if (lock != null && !lock.tryLock(5, 5, TimeUnit.SECONDS)) {
                log.info("wait creative-plan-update-status timeout,planUid={},batchUid={}", planUid, batchUid);
                return;
            }

            transactionTemplate.executeWithoutResult(status -> {
                CreativePlanDO plan = creativePlanMapper.get(planUid);
                AppValidate.notNull(plan, "创作计划不存在！UID: {}", planUid);

                // 创作计划批次状态更新
                creativePlanBatchService.updateStatus(batchUid);

                // 查询当前计划下所有的创作批次
//                CreativePlanBatchListReqVO bathQuery = new CreativePlanBatchListReqVO();
//                bathQuery.setPlanUid(planUid);
//                List<CreativePlanBatchRespVO> batchList = CollectionUtil.emptyIfNull(creativePlanBatchService.listStatus(bathQuery));
//
//                // 查询当前计划下所有的创作内容
//                CreativeContentListReqVO contentQuery = new CreativeContentListReqVO();
//                contentQuery.setPlanUid(planUid);
//                List<CreativeContentRespVO> contentList = CollectionUtil.emptyIfNull(creativeContentService.listStatus(contentQuery));
//
//                // 当前计划下的所有批次都是完成且所有任务全部执行成功的，则计划完成
//                boolean bathComplete = batchList.stream()
//                        .allMatch(item -> CreativePlanStatusEnum.COMPLETE.name().equals(item.getStatus()));
//                boolean contentComplete = contentList.stream()
//                        .allMatch(item -> CreativeContentStatusEnum.SUCCESS.name().equals(item.getStatus()));
//                if (bathComplete && contentComplete) {
//                    log.info("将要更新计划为【完成】状态，planUid: {}", planUid);
//                    updateStatus(planUid, CreativePlanStatusEnum.COMPLETE.name());
//                    log.info("更新计划状态【结束】，planUid: {}", planUid);
//                    return;
//                }
//
//                // 当前计划下只要有彻底失败的，则计划失败
//                boolean contentFailure = contentList.stream()
//                        .anyMatch(item -> CreativeContentStatusEnum.ULTIMATE_FAILURE.name().equals(item.getStatus()));
//
//                if (contentFailure) {
//                    log.info("将要更新计划为【失败】状态，planUid: {}", planUid);
//                    updateStatus(planUid, CreativePlanStatusEnum.FAILURE.name());
//                    log.info("更新计划状态【结束】，planUid: {}", planUid);
//                    return;
//                }

                // 查询当前批次
                CreativePlanBatchRespVO batch = creativePlanBatchService.get(batchUid);
                // 如果当前批次是完成状态，则计划完成
                if (CreativePlanStatusEnum.COMPLETE.name().equals(batch.getStatus())) {
                    log.info("将要更新计划为【完成】状态，planUid: {}", planUid);
                    updateStatus(planUid, CreativePlanStatusEnum.COMPLETE.name());
                    log.info("更新计划状态【结束】，planUid: {}", planUid);
                    return;
                }

                // 如果当前批次是失败状态，则计划失败
                if (CreativePlanStatusEnum.FAILURE.name().equals(batch.getStatus())) {
                    log.info("将要更新计划为【失败】状态，planUid: {}", planUid);
                    updateStatus(planUid, CreativePlanStatusEnum.FAILURE.name());
                    log.info("更新计划状态【结束】，planUid: {}", planUid);
                    return;
                }

                log.info("不需要更新计划状态，planUid: {}，status：{}", planUid, plan.getStatus());
                log.info("更新计划状态【结束】，planUid: {}", planUid);
            });

        } catch (Exception exception) {
            log.warn("更新计划失败: {}", planUid, exception);
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_UPDATE_STATUS_FAILED, planUid, exception.getMessage());
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    /**
     * 升级创作计划
     *
     * @param request 执行请求
     */
    @Override
    public void upgrade(CreativePlanUpgradeReqVO request) {
        // 查询创作计划，并且校验是否存在
        CreativePlanDO plan = creativePlanMapper.get(request.getUid());
        AppValidate.notNull(plan, CreativeErrorCodeConstants.PLAN_NOT_EXIST, request.getUid());

        // 查询应用，并接校验应用是否存在
        AppMarketRespVO latestAppMarket = this.getAppInformation(request.getAppUid(), plan.getSource());

        // 是否全量覆盖，默认非全量覆盖
        boolean isFullCover = Objects.isNull(request.getIsFullCover()) ? Boolean.FALSE : request.getIsFullCover();

        // 版本判断
        if (!isFullCover && plan.getVersion() >= latestAppMarket.getVersion()) {
            throw ServiceExceptionUtil.exception(new ErrorCode(ErrorCodeConstants.PARAMETER_EXCEPTION.getCode(), "已是最新版本！不需要更新！"));
        }

        // 计划配置
        CreativePlanConfigurationDTO configuration = request.getConfiguration();
        // 获取应用配置
        AppMarketRespVO appInformation = configuration.getAppInformation();
        // 更新升级之后的计划
        CreativePlanMaterialDO creativePlan = new CreativePlanMaterialDO();
        // 如果全量覆盖，否则直接使用最新应用配置
        if (isFullCover) {
            configuration.setMaterialList(Collections.emptyList());
            // 把最新的海报步骤填充到配置中
            WorkflowStepWrapperRespVO posterStepWrapper = latestAppMarket.getStepByHandler(PosterActionHandler.class.getSimpleName());
            List<PosterStyleDTO> posterStyleList = CreativeUtils.getPosterStyleListByStepWrapper(posterStepWrapper);
            configuration.setImageStyleList(CollectionUtil.emptyIfNull(posterStyleList));
            // copy素材库
            creativeMaterialManager.upgradeMaterialLibrary(latestAppMarket.getUid(), plan.getUid());

        }
        // 如果不是全量覆盖，只更新应用配置
        else {
            latestAppMarket.merge(appInformation);
        }
        configuration.setAppInformation(latestAppMarket);

        creativePlan.setId(plan.getId());
        creativePlan.setAppUid(latestAppMarket.getUid());
        creativePlan.setVersion(latestAppMarket.getVersion());
        creativePlan.setConfiguration(JsonUtils.toJsonString(configuration));
        creativePlan.setUpdateTime(LocalDateTime.now());
        creativePlan.setTotalCount(request.getTotalCount());

        creativePlanMaterialMapper.updateById(creativePlan);
    }

    /**
     * 获取应用信息
     *
     * @param appUid 应用UID
     * @param source 创作计划来源
     * @return 应用信息
     */
    @Override
    public AppMarketRespVO getAppInformation(String appUid, String source) {
        AppValidate.notBlank(appUid, "应用UID为必填项！");
        AppValidate.notBlank(source, "创作计划来源为必填项！");
        if (CreativePlanSourceEnum.isApp(source)) {
            AppRespVO appResponse = appService.get(appUid);
            AppMarketRespVO appMarketResponse = AppMarketConvert.INSTANCE.convert(appResponse);
            if (Objects.isNull(appMarketResponse.getVersion())) {
                appMarketResponse.setVersion(1);
            }
            return appMarketResponse;
        } else {
            return appMarketService.get(appUid);
        }
    }

    /**
     * 获取应用配置
     *
     * @param uid        应用UID
     * @param planSource 创作计划来源
     * @return 应用配置
     */
    @Override
    public AppMarketRespVO getAppRespVO(String uid, String planSource) {
        if (CreativePlanSourceEnum.isApp(planSource)) {
            // 预览模式 从我的应用拿最新配置
            AppRespVO appResponse = appService.get(uid);
            AppMarketRespVO appMarketResponse = AppMarketConvert.INSTANCE.convert(appResponse);
            if (Objects.isNull(appMarketResponse.getVersion())) {
                appMarketResponse.setVersion(1);
            }
            return appMarketResponse;
        } else {
            // 应用市场 区分版本 从执行计划拿当前配置
            CreativePlanRespVO planRespVO = get(uid);
            return planRespVO.getConfiguration().getAppInformation();
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
        if (CreativePlanStatusEnum.PENDING.name().equals(status)) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_STATUS_NOT_ALLOW_UPDATE, status);
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
     * 处理并且校验
     *
     * @param request 创作计划请求
     */
    private void handlerAndValidate(CreativePlanModifyReqVO request) {
        request.validate();
    }

}
