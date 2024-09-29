package com.starcloud.ops.business.app.service.xhs.content.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.app.dto.AppExecuteProgress;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.material.MaterialFieldConfigDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentCreateReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentListReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentModifyReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentPageReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentRegenerateReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentTaskReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.CreativeContentExecuteRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.convert.xhs.content.CreativeContentConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.batch.CreativePlanBatchDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanDO;
import com.starcloud.ops.business.app.dal.mysql.xhs.batch.CreativePlanBatchMapper;
import com.starcloud.ops.business.app.dal.mysql.xhs.content.CreativeContentMapper;
import com.starcloud.ops.business.app.dal.mysql.xhs.plan.CreativePlanMapper;
import com.starcloud.ops.business.app.domain.cache.AppStepStatusCache;
import com.starcloud.ops.business.app.domain.entity.workflow.action.MaterialActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.PosterActionHandler;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentStatusEnum;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanSourceEnum;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanStatusEnum;
import com.starcloud.ops.business.app.model.content.CreativeContentExecuteParam;
import com.starcloud.ops.business.app.model.content.CreativeContentExecuteResult;
import com.starcloud.ops.business.app.model.content.ImageContent;
import com.starcloud.ops.business.app.model.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.service.xhs.executor.CreativeThreadPoolHolder;
import com.starcloud.ops.business.app.service.xhs.manager.CreativeExecuteManager;
import com.starcloud.ops.business.app.service.xhs.material.strategy.MaterialHandlerHolder;
import com.starcloud.ops.business.app.service.xhs.material.strategy.handler.AbstractMaterialHandler;
import com.starcloud.ops.business.app.service.xhs.material.strategy.metadata.MaterialMetadata;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanService;
import com.starcloud.ops.business.app.util.CreativeUtils;
import com.starcloud.ops.business.app.utils.MaterialDefineUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Service
@Slf4j
public class CreativeContentServiceImpl implements CreativeContentService {

    @Resource
    private CreativeContentMapper creativeContentMapper;

    @Resource
    private CreativePlanBatchMapper creativePlanBatchMapper;

    @Resource
    private CreativePlanMapper creativePlanMapper;

    @Resource
    @Lazy
    private CreativePlanService creativePlanService;

    @Resource
    private CreativeExecuteManager creativeExecuteManager;

    @Resource
    private MaterialHandlerHolder materialHandlerHolder;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private AppStepStatusCache appStepStatusCache;

    @Resource
    private CreativeThreadPoolHolder creativeThreadPoolHolder;

    /**
     * 获取创作内容详情
     *
     * @param uid 创作内容UID
     * @return 创作内容详情
     */
    @Override
    public CreativeContentRespVO get(String uid) {
        CreativeContentDO creativeContent = creativeContentMapper.get(uid);
        AppValidate.notNull(creativeContent, "创作内容不存在({})", uid);
        return CreativeContentConvert.INSTANCE.convert(creativeContent);
    }

    /**
     * 查询详情
     *
     * @param uid 创作内容UID
     * @return 创作内容详情
     */
    @Override
    public CreativeContentRespVO detail(String uid) {
        CreativeContentDO creativeContent = creativeContentMapper.get(uid);
        AppValidate.notNull(creativeContent, "创作内容不存在({})", uid);
        return this.convertWithProgress(creativeContent);
    }

    /**
     * 查询创作内容列表
     *
     * @param query 查询条件
     * @return 创作内容列表
     */
    @Override
    public List<CreativeContentRespVO> list(CreativeContentListReqVO query) {
        List<CreativeContentDO> list = creativeContentMapper.list(query);
        return CreativeContentConvert.INSTANCE.convertResponseList(list);
    }

    /**
     * 查询创作内容列表
     *
     * @param query 查询条件
     * @return 创作内容列表
     */
    @Override
    public List<CreativeContentRespVO> listStatus(CreativeContentListReqVO query) {
        List<CreativeContentDO> list = creativeContentMapper.listStatus(query);
        return CreativeContentConvert.INSTANCE.convertResponseList(list);
    }

    /**
     * 查询创作内容任务列表
     *
     * @param query 查询条件
     * @return 创作内容任务列表
     */
    @Override
    @TenantIgnore
    public List<CreativeContentRespVO> listTask(CreativeContentTaskReqVO query) {
        List<CreativeContentDO> list = creativeContentMapper.listTask(query);
        return CreativeContentConvert.INSTANCE.convertResponseList(list);
    }

    /**
     * 查询创作内容生成的图片
     *
     * @param uidList 创作内容UID集合
     * @return 图片URL集合
     */
    @Override
    public List<String> listImage(List<String> uidList) {
        if (CollectionUtils.isEmpty(uidList)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<CreativeContentDO> wrapper = Wrappers.lambdaQuery(CreativeContentDO.class);
        wrapper.select(CreativeContentDO::getUid, CreativeContentDO::getExecuteResult);
        wrapper.in(CreativeContentDO::getUid, uidList);
        wrapper.eq(CreativeContentDO::getStatus, CreativeContentStatusEnum.SUCCESS.name());
        List<CreativeContentDO> list = creativeContentMapper.selectList(wrapper);
        // 如果没有查询到数据，返回空集合
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<CreativeContentExecuteResult> collect = list.stream().map(CreativeContentConvert.INSTANCE::convert)
                .map(CreativeContentRespVO::getExecuteResult)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(collect)) {
            return Collections.emptyList();
        }

        List<String> imageList = new ArrayList<>();
        for (CreativeContentExecuteResult executeResult : collect) {
            if (Objects.isNull(executeResult)) {
                continue;
            }
            List<ImageContent> imageContentList = executeResult.getImageList();
            if (CollectionUtils.isEmpty(imageContentList)) {
                continue;
            }
            // 添加图片
            for (ImageContent image : imageContentList) {
                if (Objects.isNull(image) || StringUtils.isBlank(image.getUrl())) {
                    continue;
                }
                imageList.add(image.getUrl());
            }
        }
        return imageList;
    }

    /**
     * 分页查询创作内容
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @Override
    public PageResult<CreativeContentRespVO> page(CreativeContentPageReqVO query) {
        // 查询创作内容分页数据
        IPage<CreativeContentDO> page = this.creativeContentMapper.page(query);
        if (Objects.isNull(page) || CollectionUtil.isEmpty(page.getRecords())) {
            return PageResult.empty();
        }

        // 处理查询结果
        List<CreativeContentRespVO> collect = page.getRecords()
                .stream()
                .map(this::convertWithProgress)
                .collect(Collectors.toList());

        // 返回创作内容分页列表
        return PageResult.of(collect, page.getTotal());
    }

    /**
     * 创建装作内容
     *
     * @param request 请求
     * @return 创作内容UID
     */
    @Override
    public String create(CreativeContentCreateReqVO request) {
        CreativeContentDO content = CreativeContentConvert.INSTANCE.convert(request);
        creativeContentMapper.insert(content);
        return content.getUid();
    }

    /**
     * 批量创建创作内容
     *
     * @param requestList 批量请求
     */
    @Override
    public void batchCreate(List<CreativeContentCreateReqVO> requestList) {
        List<CreativeContentDO> convert = CreativeContentConvert.INSTANCE.convertList(requestList);
        creativeContentMapper.insertBatch(convert);
    }

    /**
     * 修改创作内容
     *
     * @param request 修改请求
     * @return 创作内容UID
     */
    @Override
    public String modify(CreativeContentModifyReqVO request) {
        request.validate();
        CreativeContentDO content = creativeContentMapper.get(request.getUid());
        AppValidate.notNull(content, "创作内容不存在({})", request.getUid());
        CreativeContentDO modify = CreativeContentConvert.INSTANCE.convert(request);
        modify.setId(content.getId());
        creativeContentMapper.updateById(modify);
        return content.getUid();
    }

    /**
     * 删除创作内容
     *
     * @param uid 创作内容UID
     */
    @Override
    public void delete(String uid) {
        CreativeContentDO content = creativeContentMapper.get(uid);
        AppValidate.notNull(content, "创作内容不存在({})", uid);
        creativeContentMapper.deleteById(content.getId());
    }

    /**
     * 删除计划下的所有创作内容
     *
     * @param planUid 计划UID
     */
    @Override
    public void deleteByPlanUid(String planUid) {
        creativeContentMapper.deleteByPlanUid(planUid);
    }

    /**
     * 执行创作内容
     *
     * @param request 执行请求
     * @return 执行结果
     */
    @Override
    public CreativeContentExecuteRespVO execute(CreativeContentExecuteReqVO request) {
        CreativeContentExecuteRespVO response = creativeExecuteManager.execute(request);
        creativePlanService.updatePlanStatus(response.getPlanUid(), response.getBatchUid());
        return response;
    }

    /**
     * 批量执行创作内容
     *
     * @param request 执行请求
     * @return 执行结果
     */
    @Override
    public List<CreativeContentExecuteRespVO> batchExecute(List<CreativeContentExecuteReqVO> request) {
        // 进行批量执行
        log.info("批量执行创作内容，数量为{}: ", request.size());
        List<CreativeContentExecuteRespVO> result = creativeExecuteManager.bathExecute(request);
        if (CollectionUtils.isEmpty(result)) {
            return Collections.emptyList();
        }

        // 更新计划状态
        log.info("批量执行创作内容，数量为{}，执行完成", request.size());
        Map<String, List<CreativeContentExecuteRespVO>> resultMap = result.stream().collect(Collectors.groupingBy(CreativeContentExecuteRespVO::getBatchUid));
        log.info("批量执行创作内容，开始更新计划和批次状态，批次列表：{}", resultMap.keySet());
        for (Map.Entry<String, List<CreativeContentExecuteRespVO>> entry : resultMap.entrySet()) {
            String batchUid = entry.getKey();
            List<CreativeContentExecuteRespVO> executeResponseList = entry.getValue();
            if (CollectionUtils.isEmpty(executeResponseList)) {
                continue;
            }
            CreativeContentExecuteRespVO executeResponse = executeResponseList.get(0);
            creativePlanService.updatePlanStatus(executeResponse.getPlanUid(), batchUid);
        }
        log.info("批量执行创作内容，数量为{}，执行完成", request.size());
        if (log.isDebugEnabled()) {
            log.debug("批量执行创作内容，执行结果为：{}", JsonUtils.toJsonPrettyString(result));
        }
        return result;
    }

    /**
     * 重新生成创作内容
     *
     * @param request 执行请求
     */
    @Override
    @SuppressWarnings("all")
    public void regenerate(CreativeContentRegenerateReqVO request) {
        String lockKey = "creative-content-regenerate-" + request.getUid();
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (!lock.tryLock(1, TimeUnit.MINUTES)) {
                log.warn("创作内容正在重试中({})...", request.getUid());
                return;
            }

            // 基础校验
            request.validate(ValidateTypeEnum.EXECUTE);
            CreativeContentExecuteParam executeParam = request.getExecuteParam();
            AppMarketRespVO appInformation = executeParam.getAppInformation();

            // 素材步骤
            WorkflowStepWrapperRespVO materialWrapper = appInformation.getStepByHandler(MaterialActionHandler.class.getSimpleName());
            AppValidate.notNull(materialWrapper, "创作计划应用配置异常，资料库步骤是必须的！请联系管理员！");

            // 获取素材库类型
            String businessType = materialWrapper.getVariableToString(CreativeConstants.BUSINESS_TYPE);
            // 判断修改业务类型
            Boolean isPicture = MaterialDefineUtil.judgePicture(appInformation);
            businessType = isPicture ? CreativeConstants.PICTURE : businessType;
            materialWrapper.putVariable(CreativeConstants.BUSINESS_TYPE, businessType);

            // 获取资料库的具体处理器
            AbstractMaterialHandler materialHandler = materialHandlerHolder.getHandler(businessType);
            AppValidate.notNull(materialHandler, "素材库类型不支持，请联系管理员{}！", businessType);

            // 素材库列表
            List<Map<String, Object>> materialList = CreativeUtils.getMaterialListByStepWrapper(materialWrapper);
            AppValidate.notEmpty(materialList, "素材库列表不能为空，请联系管理员！");

            // 素材字段配置列表
            List<MaterialFieldConfigDTO> fieldList = CreativeUtils.getMaterialFieldByStepWrapper(request);
            AppValidate.notEmpty(fieldList, "素材字段配置不能为空，请联系管理员！");

            // 海报步骤
            WorkflowStepWrapperRespVO posterWrapper = appInformation.getStepByHandler(PosterActionHandler.class.getSimpleName());
            AppValidate.notNull(posterWrapper, "创作计划应用配置异常，海报步骤是必须的！请联系管理员！");

            PosterStyleDTO posterStyle = CreativeUtils.getPosterStyleByStepWrapper(posterWrapper);
            AppValidate.notNull(posterStyle, "图片生成配置不能为空！请配置图片生成后重试！");

            // 查询创作内容并且校验
            CreativeContentDO content = creativeContentMapper.get(request.getUid());
            AppValidate.notNull(content, "创作内容不存在！");

            // 查询一次应用市场，获取最新的应用市场配置
            AppMarketRespVO latestAppMarket = creativePlanService.getAppInformation(appInformation.getUid(), content.getSource());
            appInformation = CreativeUtils.mergeAppInformation(appInformation, latestAppMarket);

            // 从应用市场获取最新的系统配置合并
            posterStyle = CreativeUtils.mergeImagePosterStyle(posterStyle, appInformation);
            // 处理一下海报风格
            posterStyle = CreativeUtils.handlerPosterStyle(posterStyle);

            // 素材步骤的步骤ID
            String materialStepId = materialWrapper.getField();
            // 海报步骤的步骤ID
            String posterStepId = posterWrapper.getField();

            materialHandler.validatePosterStyle(posterStyle);

            // 处理素材列表
            MaterialMetadata materialMetadata = new MaterialMetadata();
            materialMetadata.setMaterialType(businessType);
            materialMetadata.setMaterialStepId(materialStepId);
            materialMetadata.setMaterialFieldList(fieldList);
            materialMetadata.setPlanSource(CreativePlanSourceEnum.of(request.getSource()));
            materialMetadata.setPlanUid(request.getPlanUid());
            materialMetadata.setAppUid(appInformation.getUid());
            Map<Integer, List<Map<String, Object>>> materialMap = materialHandler.handleMaterialMap(materialList, Collections.singletonList(posterStyle), materialMetadata);

            // 获取该风格下，处理之后的素材列表
            List<Map<String, Object>> usageMaterialList = materialMap.get(0);

            PosterStyleDTO handlePosterStyle = materialHandler.handlePosterStyle(posterStyle, usageMaterialList, materialMetadata);

            // 将处理后的海报风格填充到执行参数中
            appInformation.putVariable(posterStepId, CreativeConstants.POSTER_STYLE, JsonUtils.toJsonString(handlePosterStyle));
            // 将素材库的素材列表填充上传素材步骤变量中
            appInformation.putVariable(materialStepId, CreativeConstants.MATERIAL_LIST, JsonUtils.toJsonString(usageMaterialList));

            executeParam.setAppInformation(appInformation);

            // 更新创作内容为最新的版本
            CreativeContentDO updateContent = new CreativeContentDO();
            updateContent.setId(content.getId());
            updateContent.setExecuteParam(JsonUtils.toJsonString(executeParam));
            updateContent.setUpdateTime(LocalDateTime.now());
            updateContent.setUpdater(String.valueOf(SecurityFrameworkUtils.getLoginUserId()));
            creativeContentMapper.updateById(updateContent);

            // 构建执行请求
            CreativeContentExecuteReqVO executeRequest = new CreativeContentExecuteReqVO();
            executeRequest.setUid(content.getUid());
            executeRequest.setPlanUid(content.getPlanUid());
            executeRequest.setBatchUid(content.getBatchUid());
            executeRequest.setForce(Boolean.TRUE);
            executeRequest.setTenantId(content.getTenantId());

            // 异步执行
            ThreadPoolExecutor executor = creativeThreadPoolHolder.executor();
            executor.execute(() -> {
                // 执行创作内容生成
                creativeExecuteManager.execute(executeRequest);
                // 重新生成之后，重新更新创作状态
                creativePlanService.updatePlanStatus(content.getPlanUid(), content.getBatchUid());
            });
        } catch (ServiceException exception) {
            log.error("创作内容重试执行失败", exception);
            throw exception;
        } catch (InterruptedException e) {
            log.error("创作内容重试执行失败", e);
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_EXECUTE_FAILURE);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ServiceExceptionUtil.exception(new ErrorCode(710100111, e.getMessage()));
        } finally {
            lock.unlock();
        }
    }

    /**
     * 失败重试
     *
     * @param uid 任务 uid
     */
    @Override
    public void retry(String uid) {
        // 查询任务信息
        CreativeContentDO content = creativeContentMapper.get(uid);
        AppValidate.notNull(content, "创作内容不存在({})", uid);

        // 如果当前状态不是最终失败，则不需要进行重试
        if (!CreativeContentStatusEnum.ULTIMATE_FAILURE.name().equals(content.getStatus())) {
            throw ServiceExceptionUtil.exception(new ErrorCode(ErrorCodeConstants.PARAMETER_EXCEPTION.getCode(), "该任务状态不需要进行重试！"), uid);
        }

        // 更新任务状态状态
        CreativeContentDO contentUpdate = new CreativeContentDO();
        contentUpdate.setId(content.getId());
        contentUpdate.setStatus(CreativeContentStatusEnum.INIT.name());
        contentUpdate.setRetryCount(0);
        contentUpdate.setElapsed(0L);
        contentUpdate.setStartTime(null);
        contentUpdate.setEndTime(null);
        creativeContentMapper.updateById(contentUpdate);

        // 更新计划批次状态
        LambdaUpdateWrapper<CreativePlanBatchDO> batchUpdateWrapper = Wrappers.lambdaUpdate(CreativePlanBatchDO.class);
        batchUpdateWrapper.eq(CreativePlanBatchDO::getUid, content.getPlanUid());
        batchUpdateWrapper.set(CreativePlanBatchDO::getStatus, CreativePlanStatusEnum.RUNNING.name());
        creativePlanBatchMapper.update(batchUpdateWrapper);

        // 更新计划状态状态
        LambdaUpdateWrapper<CreativePlanDO> planUpdateWrapper = Wrappers.lambdaUpdate(CreativePlanDO.class);
        planUpdateWrapper.eq(CreativePlanDO::getUid, content.getPlanUid());
        planUpdateWrapper.set(CreativePlanDO::getStatus, CreativePlanStatusEnum.RUNNING.name());
        creativePlanMapper.update(planUpdateWrapper);
    }

    /**
     * 取消创作内容
     *
     * @param uid 创作内容UID
     */
    @Override
    public void cancel(String uid) {
        CreativeContentDO content = creativeContentMapper.get(uid);
        AppValidate.notNull(content, "创作内容不存在({})", uid);
        cancel(content);
    }

    /**
     * 取消创作内容
     *
     * @param batchUid 批次UID
     */
    @Override
    public void cancelByBatchUid(String batchUid) {
        // 查询该批次下的所有创作内容任务
        CreativeContentListReqVO contentQuery = new CreativeContentListReqVO();
        contentQuery.setBatchUid(batchUid);
        List<CreativeContentDO> contentList = CollectionUtil.emptyIfNull(creativeContentMapper.listStatus(contentQuery));
        if (CollectionUtils.isEmpty(contentList)) {
            return;
        }
        // 取消创作内容
        contentList.forEach(this::cancel);
    }

    /**
     * 取消创作内容
     *
     * @param content 创作内容
     */
    public void cancel(CreativeContentDO content) {
        String status = content.getStatus();
        // 如果取消，成功或者最终失败，则不需要取消
        if (CreativeContentStatusEnum.SUCCESS.name().equals(status) ||
                CreativeContentStatusEnum.ULTIMATE_FAILURE.name().equals(status) ||
                CreativeContentStatusEnum.CANCELED.name().equals(status)) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        long elapsed = Duration.between(content.getStartTime(), now).toMillis();

        LambdaUpdateWrapper<CreativeContentDO> wrapper = Wrappers.lambdaUpdate(CreativeContentDO.class);
        wrapper.set(CreativeContentDO::getStatus, CreativeContentStatusEnum.CANCELED.name());
        wrapper.set(CreativeContentDO::getEndTime, now);
        wrapper.set(CreativeContentDO::getElapsed, elapsed);
        wrapper.set(CreativeContentDO::getUpdateTime, now);
        wrapper.eq(CreativeContentDO::getUid, content.getUid());
        creativeContentMapper.update(wrapper);
    }

    /**
     * 批量绑定创作内容
     *
     * @param uidList 创作内容UID集合
     * @return 绑定之后结果
     */
    @Override
    public List<CreativeContentRespVO> batchBind(List<String> uidList) {
        // 查询内容列表
        CreativeContentListReqVO query = new CreativeContentListReqVO();
        query.setUidList(uidList);
        query.setClaim(Boolean.FALSE);
        List<CreativeContentDO> contentList = creativeContentMapper.list(query);

        if (contentList.size() < uidList.size()) {
            throw exception(new ErrorCode(720100110, "存在已绑定的创作内容"));
        }

        creativeContentMapper.claim(uidList, Boolean.TRUE);
        // 返回数据
        return CreativeContentConvert.INSTANCE.convertResponseList(contentList);
    }

    /**
     * 批量解绑创作内容
     *
     * @param uidList 创作内容UID集合
     */
    @Override
    public void batchUnbind(List<String> uidList) {
        if (CollectionUtils.isEmpty(uidList)) {
            return;
        }
        creativeContentMapper.claim(uidList, Boolean.FALSE);
    }

    /**
     * 点赞
     *
     * @param uid 创作内容UID
     */
    @Override
    public void like(String uid) {
        CreativeContentDO content = creativeContentMapper.get(uid);
        AppValidate.notNull(content, "创作内容不存在({})", uid);
        CreativeContentDO updateContent = new CreativeContentDO();
        updateContent.setId(content.getId());
        updateContent.setLiked(Boolean.TRUE);
        creativeContentMapper.updateById(updateContent);
    }

    /**
     * 取消点赞
     *
     * @param uid 创作内容UID
     */
    @Override
    public void unlike(String uid) {
        CreativeContentDO content = creativeContentMapper.get(uid);
        AppValidate.notNull(content, "创作内容不存在({})", uid);
        CreativeContentDO updateContent = new CreativeContentDO();
        updateContent.setId(content.getId());
        updateContent.setLiked(Boolean.FALSE);
        creativeContentMapper.updateById(updateContent);
    }

    /**
     * 讲创作内容实体转为创作内容响应对象，带有进度信息
     *
     * @param creativeContent 创作内容实体
     * @return 创作内容响应对象
     */
    private CreativeContentRespVO convertWithProgress(CreativeContentDO creativeContent) {
        CreativeContentRespVO response = CreativeContentConvert.INSTANCE.convert(creativeContent);
        if (!CreativeContentStatusEnum.SUCCESS.name().equals(response.getStatus())) {
            // 获取执行进度
            AppExecuteProgress progress = appStepStatusCache.progress(response.getConversationUid());
            response.setProgress(progress);
        }
        return response;
    }
}
