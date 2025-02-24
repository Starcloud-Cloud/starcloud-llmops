package com.starcloud.ops.business.app.service.xhs.content.impl;

import cn.hutool.core.bean.BeanPath;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.app.dto.AppExecuteProgress;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.plugin.WordCheckContent;
import com.starcloud.ops.business.app.api.xhs.material.MaterialFieldConfigDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.*;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.*;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.convert.xhs.content.CreativeContentConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.batch.CreativePlanBatchDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanDO;
import com.starcloud.ops.business.app.dal.mysql.xhs.batch.CreativePlanBatchMapper;
import com.starcloud.ops.business.app.dal.mysql.xhs.content.CreativeContentMapper;
import com.starcloud.ops.business.app.dal.mysql.xhs.plan.CreativePlanMapper;
import com.starcloud.ops.business.app.domain.cache.AppStepStatusCache;
import com.starcloud.ops.business.app.domain.entity.workflow.JsonDocsDefSchema;
import com.starcloud.ops.business.app.domain.entity.workflow.action.MaterialActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.PosterActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentStatusEnum;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialUsageModel;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanSourceEnum;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanStatusEnum;
import com.starcloud.ops.business.app.feign.VideoGeneratorClient;
import com.starcloud.ops.business.app.feign.dto.PosterImage;
import com.starcloud.ops.business.app.feign.dto.PosterImageParam;
import com.starcloud.ops.business.app.feign.dto.video.*;
import com.starcloud.ops.business.app.feign.dto.video.v2.VideoGeneratorConfigV2;
import com.starcloud.ops.business.app.feign.request.poster.PosterRequest;
import com.starcloud.ops.business.app.feign.request.video.ImagePdfRequest;
import com.starcloud.ops.business.app.feign.request.video.WordbookPdfRequest;
import com.starcloud.ops.business.app.feign.response.PdfGeneratorResponse;
import com.starcloud.ops.business.app.feign.response.VideoGeneratorResponse;
import com.starcloud.ops.business.app.model.content.*;
import com.starcloud.ops.business.app.model.content.resource.CreativeContentResourceConfiguration;
import com.starcloud.ops.business.app.model.content.resource.CreativeContentResourceImage2PdfConfiguration;
import com.starcloud.ops.business.app.model.content.resource.CreativeContentResourceWordbook2PdfConfiguration;
import com.starcloud.ops.business.app.model.content.resource.ResourceContentInfo;
import com.starcloud.ops.business.app.model.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.model.poster.PosterTemplateDTO;
import com.starcloud.ops.business.app.model.poster.PosterVariableDTO;
import com.starcloud.ops.business.app.service.plugins.WuyouClient;
import com.starcloud.ops.business.app.service.poster.PosterService;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.service.xhs.executor.CreativeThreadPoolHolder;
import com.starcloud.ops.business.app.service.xhs.manager.CreativeExecuteManager;
import com.starcloud.ops.business.app.service.xhs.material.strategy.MaterialHandlerHolder;
import com.starcloud.ops.business.app.service.xhs.material.strategy.handler.AbstractMaterialHandler;
import com.starcloud.ops.business.app.service.xhs.material.strategy.metadata.MaterialMetadata;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanService;
import com.starcloud.ops.business.app.util.CreativeUtils;
import com.starcloud.ops.business.log.service.message.LogAppMessageService;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.PARAM_ERROR;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.VIDEO_ERROR;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.VIDEO_MERGE_ERROR;

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

    @Resource
    private WuyouClient wuyouClient;

    @Resource
    private LogAppMessageService logAppMessageService;

    @Resource
    private VideoGeneratorClient videoGeneratorClient;

    @Resource
    private PosterService posterService;

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
        CreativeContentRespVO contentRespVO = this.convertWithProgress(creativeContent);
        String quickConfiguration = contentRespVO.getExecuteParam().getQuickConfiguration();
        if (StringUtils.isNoneBlank(quickConfiguration)) {
            return contentRespVO;
        }
        AppMarketRespVO appInformation = contentRespVO.getExecuteParam().getAppInformation();
        contentRespVO.getExecuteParam().setQuickConfiguration(CreativeUtils.parseQuickConfiguration(appInformation));
        return contentRespVO;
    }

    /**
     * 分页查询创作内容
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @Override
    public PageResult<CreativeContentRespVO> page(CreativeContentPageReqVOV2 query) {
        IPage<CreativeContentDO> page = creativeContentMapper.page(query);
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
    @DataPermission(enable = false)
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

        CreativeContentExecuteResult executeResult = getExecuteResult(content);
        CreativeContentExecuteResult modifyResult = getExecuteResult(modify);
        if (Objects.isNull(modifyResult)) {
            modifyResult = executeResult;
        }
        if (Objects.isNull(modifyResult.getCopyWriting())) {
            modifyResult.setCopyWriting(executeResult.getCopyWriting());
        }
        if (CollectionUtil.isEmpty(modifyResult.getImageList())) {
            modifyResult.setImageList(executeResult.getImageList());
        }
        if (Objects.isNull(modifyResult.getVideo())) {
            modifyResult.setVideo(executeResult.getVideo());
        }
        if (Objects.isNull(modifyResult.getResource())) {
            modifyResult.setResource(executeResult.getResource());
        }
        modify.setExecuteResult(JsonUtils.toJsonString(modifyResult));
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
    @DataPermission(enable = false)
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
    @DataPermission(enable = false)
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
            // 查询创作内容，校验创作内容是否存在
            CreativeContentDO content = creativeContentMapper.get(request.getUid());
            AppValidate.notNull(content, "创作内容不存在({})", request.getUid());
            // 查询创作计划，校验创作计划是否存在
            CreativePlanRespVO planResponse = creativePlanService.get(content.getPlanUid());

            // 获取执行参数
            CreativeContentExecuteParam executeParam = request.getExecuteParam();
            // 获取应用
            AppMarketRespVO appInformation = this.handlerAppInformation(request);
            // 素材步骤
            WorkflowStepWrapperRespVO materialWrapper = this.materialStepWrapper(appInformation);
            // 素材步骤的步骤ID
            String materialStepId = materialWrapper.getStepCode();
            // 获取海报生成步骤
            WorkflowStepWrapperRespVO posterStepWrapper = this.posterStepWrapper(appInformation);
            // 海报步骤的步骤ID
            String posterStepId = posterStepWrapper.getStepCode();
            // 素材字段配置列表
            List<MaterialFieldConfigDTO> materialFieldList = this.materialFieldList(planResponse);
            // 获取素材库类型
            String businessType = businessType(planResponse, materialWrapper, appInformation);
            appInformation.putVariable(materialStepId, CreativeConstants.BUSINESS_TYPE, businessType);
            // 获取到素材使用模式
            MaterialUsageModel materialUsageModel = materialUsageModel(materialWrapper);
            appInformation.putVariable(materialStepId, CreativeConstants.MATERIAL_USAGE_MODEL, materialUsageModel.name());
            // 获取资料库的具体处理器
            AbstractMaterialHandler materialHandler = materialHandler(businessType);
            // 素材库列表
            List<Map<String, Object>> materialList = CreativeUtils.getMaterialListByStepWrapper(materialWrapper);
            AppValidate.notEmpty(materialList, "素材库列表不能为空，请联系管理员！");
            // 获取海报风格
            PosterStyleDTO posterStyle = handlerPosterStyle(posterStepWrapper, appInformation);

            // 构建素材库元数据
            MaterialMetadata materialMetadata = new MaterialMetadata();
            materialMetadata.setAppUid(appInformation.getUid());
            materialMetadata.setPlanUid(planResponse.getUid());
            materialMetadata.setUserId(SecurityFrameworkUtils.getLoginUserId());
            materialMetadata.setPlanSource(CreativePlanSourceEnum.of(request.getSource()));
            materialMetadata.setMaterialType(businessType);
            materialMetadata.setMaterialStepId(materialStepId);
            materialMetadata.setPosterStepId(posterStepId);
            materialMetadata.setMaterialUsageModel(materialUsageModel);
            materialMetadata.setMaterialFieldList(materialFieldList);
            materialMetadata.setAppInformation(appInformation);
            materialMetadata.setIsUpdateMaterialUsageCount(Boolean.FALSE);

            Map<String, PosterStyleDTO> posterStyleMap = Collections.singletonMap(content.getConversationUid(), posterStyle);
            Map<String, List<Map<String, Object>>> materialMap = materialHandler.handleMaterialMap(materialList, posterStyleMap, materialMetadata);
            // 获取该风格下，处理之后的素材列表
            List<Map<String, Object>> usageMaterialList = materialMap.get(content.getConversationUid());
            // 处理海报风格
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
        // 如果取消，执行中，成功或者最终失败，则不需要取消
        // 执行中的，需要等待执行完成，然后再取消。
        if (CreativeContentStatusEnum.SUCCESS.name().equals(status) ||
                CreativeContentStatusEnum.ULTIMATE_FAILURE.name().equals(status) ||
                CreativeContentStatusEnum.CANCELED.name().equals(status)) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = Objects.isNull(content.getStartTime()) ? now : content.getStartTime();
        long elapsed = Duration.between(start, now).toMillis();

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
     * 批量生成二维码
     *
     * @param requestList 请求
     * @return 二维码列表
     */
    @Override
    public List<CreativeContentQRCodeRespVO> batchQrCode(List<CreativeContentQRCodeReqVO> requestList) {
        if (CollectionUtils.isEmpty(requestList)) {
            return Collections.emptyList();
        }
        List<CreativeContentQRCodeRespVO> responseList = new ArrayList<>();
        for (CreativeContentQRCodeReqVO request : requestList) {
            responseList.add(qrCode(request));
        }
        return responseList;
    }

    /**
     * 生成二维码
     *
     * @param request 请求
     * @return 二维码
     */
    private CreativeContentQRCodeRespVO qrCode(CreativeContentQRCodeReqVO request) {
        String domain = request.getDomain();
        String uid = request.getUid();
        String type = request.getType();
        CreativeContentDO creativeContent = creativeContentMapper.get(uid);
        AppValidate.notNull(creativeContent, "创作内容不存在({})", uid);

        QrConfig config = new QrConfig();
        config.setCharset(StandardCharsets.UTF_8);
        domain = StrUtil.endWith(domain, "/") ? domain : domain + "/";
        String content;
        if ("video".equals(type)) {
            content = StrUtil.format("{}shareVideo?uid={}&type={}", domain, creativeContent.getUid(), type);
        } else {
            content = StrUtil.format("{}share?uid={}&type={}", domain, creativeContent.getUid(), type);
        }
        String base64 = QrCodeUtil.generateAsBase64(content, config, ImgUtil.IMAGE_TYPE_PNG);
        CreativeContentQRCodeRespVO response = new CreativeContentQRCodeRespVO();
        response.setUid(creativeContent.getUid());
        response.setBatchId(creativeContent.getBatchUid());
        response.setPlanUid(creativeContent.getPlanUid());
        response.setQrCode(base64);
        return response;
    }

    @Override
    public CreativeContentRiskRespVO risk(CreativeContentRiskReqVO reqVO) {
        WordCheckContent checkContent = wuyouClient.risk(reqVO.getContent());
        CreativeContentRiskRespVO respVO = new CreativeContentRiskRespVO();
        respVO.setContent(reqVO.getContent());
        String resContent = reqVO.getContent();
        if (StringUtils.isNoneBlank(checkContent.getTopRiskStr())) {
            for (String topRisk : checkContent.getTopRiskStr().split("、")) {
                resContent = resContent.replaceAll(topRisk, "<span class=\"jwy-topRisk\">" + topRisk + "</span>");
            }
        }

        if (StringUtils.isNoneBlank(checkContent.getLowRiskStr())) {
            for (String topRisk : checkContent.getLowRiskStr().split("、")) {
                resContent = resContent.replaceAll(topRisk, "<span class=\"jwy-lowRisk\">" + topRisk + "</span>");
            }
        }

        respVO.setResContent(resContent);
        respVO.setLowRiskStr(checkContent.getLowRiskStr());
        respVO.setTopRiskStr(checkContent.getTopRiskStr());
        respVO.setContentLength(checkContent.getContentLength());
        respVO.setRiskList(checkContent.getRiskList());
        return respVO;
    }

    /**
     * 获取资源信息
     *
     * @param uid 创作内容UID
     * @return 资源信息
     */
    @Override
    public CreativeContentResourceRespVO getResource(String uid) {
        CreativeContentDO content = creativeContentMapper.get(uid);
        AppValidate.notNull(content, "创作内容不存在({})", uid);

        // 获取执行参数
        CreativeContentExecuteParam executeParam = getExecuteParam(content);
        AppValidate.notNull(executeParam, "创作内容执行参数不存在({})", uid);
        CreativeContentResourceConfiguration resourceConfiguration = Optional.ofNullable(executeParam.getResourceConfiguration()).orElse(new CreativeContentResourceConfiguration());

        // 获取执行结果
        CreativeContentExecuteResult executeResult = getExecuteResult(content);
        AppValidate.notNull(executeResult, "创作内容执行结果不存在({})", uid);
        ResourceContentInfo resource = Optional.ofNullable(executeResult.getResource()).orElse(new ResourceContentInfo());

        // 获取视频信息
        VideoContentInfo video = executeResult.getVideo();
        AppValidate.notNull(video, "创作内容视频信息不存在({}), 请生成视频后重试！", uid);
        List<VideoContent> videoList = video.getVideoList();
        AppValidate.notEmpty(videoList, "创作内容视频列表为空({}), 请生成视频后重试！", uid);

        // 始终获取最新的完整视频，则从视频信息中获取
        String completeVideoUrl = Optional.ofNullable(video.getCompleteVideoUrl()).orElse(StringUtils.EMPTY);
        // 如果没有完整视频，则获取视频列表的第一个视频
        if (StringUtils.isBlank(completeVideoUrl)) {
            completeVideoUrl = videoList.get(0).getVideoUrl();
        }
        //AppValidate.notBlank(completeVideoUrl, "创作内容完整视频不存在，请合并视频后重试！");
        resource.setCompleteVideoUrl(completeVideoUrl);

        // 始终获取最新的完整音频，则从视频信息中获取
        String completeAudioUrl = Optional.ofNullable(video.getCompleteAudioUrl()).orElse(StringUtils.EMPTY);
        // AppValidate.notBlank(completeAudioUrl, "创作内容完整音频不存在，请合并视频后重试！");
        if (StringUtils.isBlank(completeAudioUrl)) {
            completeAudioUrl = videoList.get(0).getAudioUrl();
        }
        resource.setCompleteAudioUrl(completeAudioUrl);

        CreativeContentResourceRespVO response = new CreativeContentResourceRespVO();
        response.setUid(uid);
        response.setResourceConfiguration(resourceConfiguration);
        response.setResource(resource);
        return response;
    }

    /**
     * 保存资源配置
     *
     * @param request 请求
     */
    @Override
    public void saveResourceConfig(CreativeContentResourceConfigurationReqVO request) {
        CreativeContentDO content = creativeContentMapper.get(request.getUid());
        AppValidate.notNull(content, "创作内容不存在({})", request.getUid());

        // 参数封装
        CreativeContentExecuteParam executeParam = getExecuteParam(content);
        executeParam.setResourceConfiguration(request.getResourceConfiguration());
        content.setExecuteParam(JsonUtils.toJsonString(executeParam));

        // 结果封装
        CreativeContentExecuteResult executeResult = getExecuteResult(content);
        executeResult.setResource(request.getResource());
        content.setExecuteResult(JsonUtils.toJsonString(executeResult));

        creativeContentMapper.updateById(content);
    }

    /**
     * 生成图片PDF
     *
     * @param request 请求
     * @return PDF URL
     */
    @Override
    public String generateImagePdf(CreativeContentResourceConfigurationReqVO request) {
        CreativeContentDO content = creativeContentMapper.get(request.getUid());
        AppValidate.notNull(content, "创作内容不存在({})", request.getUid());

        CreativeContentExecuteResult executeResult = getExecuteResult(content);
        List<ImageContent> imageList = executeResult.getImageList();
        if (CollectionUtils.isEmpty(imageList)) {
            throw ServiceExceptionUtil.invalidParamException("图片生成列表不能为空");
        }
        List<String> imageUrlList = imageList.stream().map(ImageContent::getUrl).collect(Collectors.toList());

        ResourceContentInfo resource = executeResult.getResource();
        if (Objects.isNull(resource)) {
            resource = new ResourceContentInfo();
        }
        String videoUrl = resource.getCompleteVideoUrl();
        String audioUrl = resource.getCompleteAudioUrl();

        CreativeContentResourceConfiguration configuration = request.getResourceConfiguration();
        CreativeContentResourceImage2PdfConfiguration imagePdfConfiguration = configuration.getImagePdfConfiguration();
        if (Objects.isNull(imagePdfConfiguration)) {
            throw ServiceExceptionUtil.invalidParamException("图片PDF配置不能为空");
        }
        Boolean isAddAudioQrCode = imagePdfConfiguration.getIsAddAudioQrCode();
        Boolean isAddVideoQrCode = imagePdfConfiguration.getIsAddVideoQrCode();
        String qrCodeLocation = imagePdfConfiguration.getQrCodeLocation();

        String title = Optional.ofNullable(executeResult.getCopyWriting()).map(CopyWritingContent::getTitle).orElse("图片PDF");
        // 生成图片PDF
        ImagePdfRequest imagePdfRequest = new ImagePdfRequest();
        imagePdfRequest.setTitle(title);
        imagePdfRequest.setImageUrlList(imageUrlList);

        VideoGeneratorResponse<PdfGeneratorResponse> response = videoGeneratorClient.generateImagePdf(imagePdfRequest);
        if (response.getCode() != 0) {
            throw ServiceExceptionUtil.exception(VIDEO_ERROR, response.getMsg());
        }
        PdfGeneratorResponse data = response.getData();
        String pdfUrl = Optional.ofNullable(data).map(PdfGeneratorResponse::getUrl)
                .orElseThrow(() -> exception(VIDEO_ERROR, "生成单词卡PDF失败，请稍后重试！"));

        // 保存参数配置
        CreativeContentExecuteParam executeParam = getExecuteParam(content);
        CreativeContentResourceConfiguration resourceConfiguration = Optional.ofNullable(executeParam.getResourceConfiguration())
                .orElse(new CreativeContentResourceConfiguration());
        resourceConfiguration.setImagePdfConfiguration(imagePdfConfiguration);
        executeParam.setResourceConfiguration(resourceConfiguration);

        // 保存PDF URL
        resource.setImagePdfUrl(pdfUrl);
        executeResult.setResource(resource);
        content.setExecuteParam(JsonUtils.toJsonString(executeParam));
        content.setExecuteResult(JsonUtils.toJsonString(executeResult));
        creativeContentMapper.updateById(content);

        return pdfUrl;
    }

    /**
     * 生成视频PDF
     *
     * @param request 请求
     * @return PDF URL
     */
    @Override
    public String generateWordBookPdf(CreativeContentResourceConfigurationReqVO request) {
        CreativeContentDO content = creativeContentMapper.get(request.getUid());
        AppValidate.notNull(content, "创作内容不存在({})", request.getUid());

        // 获取执行使用的素材列表
        CreativeContentExecuteParam executeParam = getExecuteParam(content);
        AppMarketRespVO appInformation = executeParam.getAppInformation();
        // 素材步骤
        WorkflowStepWrapperRespVO materialWrapper = this.materialStepWrapper(appInformation);
        String materialStepId = materialWrapper.getStepCode();

        // 素材库列表
        List<Map<String, Object>> materialList = CreativeUtils.getMaterialListByStepWrapper(materialWrapper);
        AppValidate.notEmpty(materialList, "素材库列表不能为空，请联系管理员！");

        CreativeContentResourceConfiguration resourceConfiguration = request.getResourceConfiguration();
        CreativeContentResourceWordbook2PdfConfiguration wordbookPdfConfiguration = resourceConfiguration.getWordbookPdfConfiguration();

        // 单词字段
        String wordField = wordbookPdfConfiguration.getWordField();
        String paraphraseField = wordbookPdfConfiguration.getParaphraseField();

        // 处理海报模板
        PosterTemplateDTO posterTemplate = wordbookPdfConfiguration.getPosterTemplate();
        PosterTemplateDTO template = CreativeUtils.handlerPosterTemplate(posterTemplate, 0);

        List<PosterVariableDTO> variableList = template.getVariableList();
        List<String> variableNameList = CollectionUtil.emptyIfNull(variableList)
                .stream()
                .map(PosterVariableDTO::getLabel)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());

        // 获取单词字段的最大值
        int wordFiledCount = CreativeUtils.getWordFieldCount(variableNameList);
        // 获取单词释义字段的最大值
        int paraphraseFieldCount = CreativeUtils.getParaphraseFieldCount(variableNameList);
        // 确定每张图需要多少素材
        int count = Math.max(wordFiledCount, paraphraseFieldCount);
        if (count <= 0) {
            throw ServiceExceptionUtil.invalidParamException("单词卡模板配置异常！请联系管理员！");
        }
        List<String> wordbookUrlList = new ArrayList<>();

        if (materialList.size() == 1) {
            // 查询创作计划，校验创作计划是否存在
            CreativePlanRespVO planResponse = creativePlanService.get(content.getPlanUid());
            // 素材字段配置列表
            List<MaterialFieldConfigDTO> materialFieldList = this.materialFieldList(planResponse);
            Map<String, MaterialFieldConfigDTO> materialFieldMap = materialFieldList.stream()
                    .collect(Collectors.toMap(MaterialFieldConfigDTO::getFieldName, Function.identity()));

            // 计算素材字段的单词和释义的数量，取最大值
            int fieldCount = CreativeUtils.getMaterialFieldWordOrParaphraseCount(materialFieldMap, wordField, paraphraseField);
            // 如果素材字段的数量小于需要的数量，说明素材字段不够，只需要生成一张图
            if (fieldCount < count) {
                List<String> urlList = poster(template, materialStepId, materialList, wordField, paraphraseField, count, fieldCount, 0);
                wordbookUrlList.addAll(urlList);
            } else {
                // 计算一共需要多少张图，如果有余数，则需要多一张
                int size = fieldCount / count;
                int remainder = fieldCount % count;
                size = remainder > 0 ? size + 1 : size;
                for (int i = 0; i < size; i++) {
                    List<Map<String, Object>> subList = materialList.subList(i * count, Math.min((i + 1) * count, materialList.size()));
                    List<String> urlList = poster(template, materialStepId, subList, wordField, paraphraseField, count, fieldCount, i);
                    wordbookUrlList.addAll(urlList);
                }
            }
        } else {
            // 此时说明，素材只够生成一张图
            if (materialList.size() <= count) {
                List<String> urlList = poster(template, materialStepId, materialList, wordField, paraphraseField, count, 1,0);
                wordbookUrlList.addAll(urlList);
            } else {
                // 计算一共需要多少张图，如果有余数，则需要多一张
                int size = materialList.size() / count;
                int remainder = materialList.size() % count;
                size = remainder > 0 ? size + 1 : size;
                for (int i = 0; i < size; i++) {
                    List<Map<String, Object>> subList = materialList.subList(i * count, Math.min((i + 1) * count, materialList.size()));
                    List<String> urlList = poster(template, materialStepId, subList, wordField, paraphraseField, count, 1, i);
                    wordbookUrlList.addAll(urlList);
                }
            }
        }

        if (CollectionUtils.isEmpty(wordbookUrlList)) {
            throw ServiceExceptionUtil.invalidParamException("生成单词卡PDF失败，请稍后重试！");
        }

        CreativeContentExecuteResult executeResult = getExecuteResult(content);
        String title = Optional.ofNullable(executeResult).map(CreativeContentExecuteResult::getCopyWriting).map(CopyWritingContent::getTitle).orElse("抗遗忘默写单词本");
        // 生成 PDF
        WordbookPdfRequest wordbookPdfRequest = new WordbookPdfRequest();
        wordbookPdfRequest.setTitle(title);
        wordbookPdfRequest.setWordbookImageUrlList(wordbookUrlList);
        VideoGeneratorResponse<PdfGeneratorResponse> response = videoGeneratorClient.generateWordBookPdf(wordbookPdfRequest);
        if (response.getCode() != 0) {
            throw ServiceExceptionUtil.exception(VIDEO_ERROR, response.getMsg());
        }
        PdfGeneratorResponse data = response.getData();
        String pdfUrl = Optional.ofNullable(data).map(PdfGeneratorResponse::getUrl)
                .orElseThrow(() -> exception(VIDEO_ERROR, "生成单词卡PDF失败，请稍后重试！"));

        // 保存配置参数
        CreativeContentResourceConfiguration contentResourceConfiguration = Optional.ofNullable(executeParam.getResourceConfiguration())
                .orElse(new CreativeContentResourceConfiguration());
        contentResourceConfiguration.setWordbookPdfConfiguration(wordbookPdfConfiguration);
        executeParam.setResourceConfiguration(contentResourceConfiguration);

        // 保存PDF URL
        ResourceContentInfo resource = Optional.ofNullable(executeResult.getResource()).orElse(new ResourceContentInfo());
        resource.setWordbookPdfUrl(pdfUrl);
        executeResult.setResource(resource);
        content.setExecuteParam(JsonUtils.toJsonString(executeParam));
        content.setExecuteResult(JsonUtils.toJsonString(executeResult));
        creativeContentMapper.updateById(content);
        return pdfUrl;
    }

    /**
     * 获取分享资源
     *
     * @param uid 创作内容UID
     * @return 分享资源
     */
    @Override
    public CreativeContentShareResultRespVO getShareResult(String uid) {
        CreativeContentRespVO contentResponse = this.get(uid);

        CreativeContentExecuteResult executeResult = contentResponse.getExecuteResult();
        AppValidate.notNull(executeResult, "创作内容执行结果不存在({})", uid);

        ResourceContentInfo resource = Optional.ofNullable(executeResult.getResource()).orElse(new ResourceContentInfo());
        // 获取视频信息
        VideoContentInfo video = executeResult.getVideo();
        AppValidate.notNull(video, "创作内容视频信息不存在({}), 请生成视频后重试！", uid);
        List<VideoContent> videoList = video.getVideoList();
        AppValidate.notEmpty(videoList, "创作内容视频列表为空({}), 请生成视频后重试！", uid);

        // 始终获取最新的完整视频，则从视频信息中获取
        String completeVideoUrl = Optional.ofNullable(video.getCompleteVideoUrl()).orElse(StringUtils.EMPTY);
        // 如果没有完整视频，则获取视频列表的第一个视频
        if (StringUtils.isBlank(completeVideoUrl)) {
            completeVideoUrl = videoList.get(0).getVideoUrl();
        }
        //AppValidate.notBlank(completeVideoUrl, "创作内容完整视频不存在，请合并视频后重试！");
        resource.setCompleteVideoUrl(completeVideoUrl);

        // 始终获取最新的完整音频，则从视频信息中获取
        String completeAudioUrl = Optional.ofNullable(video.getCompleteAudioUrl()).orElse(StringUtils.EMPTY);
        // AppValidate.notBlank(completeAudioUrl, "创作内容完整音频不存在，请合并视频后重试！");
        resource.setCompleteAudioUrl(completeAudioUrl);

        executeResult.setResource(resource);

        CreativeContentShareResultRespVO response = new CreativeContentShareResultRespVO();
        response.setUid(uid);
        response.setExecuteResult(executeResult);
        return response;
    }

    @Override
    public void saveVideoConfig(VideoConfigReqVO reqVO) {

        // 校验创作内容是否存在
        CreativeContentDO creativeContent = creativeContentMapper.get(reqVO.getUid());
        if (Objects.isNull(creativeContent)) {
            throw ServiceExceptionUtil.invalidParamException("创作内容不存在");
        }

        // 保存视频配置参数
        CreativeContentExecuteParam executeParam = getExecuteParam(creativeContent);
        executeParam.setQuickConfiguration(reqVO.getQuickConfiguration());
        creativeContent.setExecuteParam(JsonUtils.toJsonString(executeParam));

        // 保存视频内容结果
        if (Objects.nonNull(reqVO.getVideo())) {
            CreativeContentExecuteResult executeResult = getExecuteResult(creativeContent);
            VideoContentInfo video = reqVO.getVideo();
            VideoContentInfo resultVideo = Optional.ofNullable(executeResult.getVideo()).orElse(new VideoContentInfo());

            // 视频列表
            List<VideoContent> videoList = video.getVideoList();
            if (CollectionUtils.isNotEmpty(videoList)) {
                resultVideo.setVideoList(videoList);
            }

            // 完整视频
            String completeVideo = video.getCompleteVideoUrl();
            if (StringUtils.isNotBlank(completeVideo)) {
                resultVideo.setCompleteVideoUrl(completeVideo);
            }

            // 完整的音频
            String completeAudio = video.getCompleteAudioUrl();
            if (StringUtils.isNotBlank(completeAudio)) {
                resultVideo.setCompleteAudioUrl(completeAudio);
            }

            // 如果视频列表和完整视频都为空，则设置为null
            if (CollectionUtils.isEmpty(videoList) && StringUtils.isBlank(completeVideo) && StringUtils.isBlank(completeAudio)) {
                executeResult.setVideo(null);
            }

            executeResult.setVideo(resultVideo);
            creativeContent.setExecuteResult(JsonUtils.toJsonString(executeResult));
        }
        creativeContentMapper.updateById(creativeContent);
    }

    // 只做透穿
    @Override
    @DataPermission(enable = false)
    public VideoGeneratorConfig generateVideo(VideoConfigReqVO reqVO) {
        if (StringUtils.isBlank(reqVO.getVideoConfig())) {
            throw exception(PARAM_ERROR, "生成视频配置必填");
        }
        VideoGeneratorConfig videoConfig = JSONUtil.toBean(reqVO.getVideoConfig(), VideoGeneratorConfig.class);
        if (Objects.isNull(videoConfig.getGlobalSettings())) {
            videoConfig.setGlobalSettings(new VideoGeneratorConfig.GlobalSettings());
        }

        if (JSONUtil.isTypeJSONObject(reqVO.getQuickConfiguration())) {
            JSONObject quickConfiguration = JSONObject.parseObject(reqVO.getQuickConfiguration());
            for (Map.Entry<String, Object> entry : quickConfiguration.entrySet()) {
                if (Objects.isNull(entry.getValue())) {
                    continue;
                }
                BeanPath beanPath = new BeanPath("globalSettings." + entry.getKey());
                try {
                    beanPath.set(videoConfig, entry.getValue());
                } catch (Exception ignored) {
                    log.warn("字段不存在 {}", entry.getKey());
                }
            }
        }

        String imageCode = reqVO.getImageCode();
        if (StringUtils.isBlank(imageCode)) {
            throw exception(PARAM_ERROR, "图片code必填");
        }

        CreativeContentDO creativeContent = creativeContentMapper.get(reqVO.getUid());
        if (Objects.isNull(creativeContent)) {
            throw exception(PARAM_ERROR, "创作内容不存在");
        }
        CreativeContentRespVO contentRespVO = CreativeContentConvert.INSTANCE.convert(creativeContent);
        Map<String, String> resources = buildResources(contentRespVO, reqVO.getImageUrl());

        List<ImageContent> imageContents = Optional.ofNullable(contentRespVO.getExecuteResult())
                .map(CreativeContentExecuteResult::getImageList).orElseThrow(() -> exception(PARAM_ERROR, "没有图片生成结果"));
        if (CollectionUtil.isEmpty(imageContents)) {
            throw exception(PARAM_ERROR, "没有图片生成结果");
        }

        if (Objects.isNull(videoConfig.getGlobalSettings().getBackground())) {
            videoConfig.getGlobalSettings().setBackground(new VideoGeneratorConfig.Background());
        }
        videoConfig.getGlobalSettings().getBackground().setSource(reqVO.getImageUrl());
        videoConfig.setResources(resources);
        videoConfig.setId(null);
        try {
            VideoGeneratorResponse<VideoGeneratorResult> generatorResponse = videoGeneratorClient.videoGenerator(videoConfig);
            if (generatorResponse.getCode() != 0) {
                throw ServiceExceptionUtil.exception(VIDEO_ERROR, generatorResponse.getMsg());
            }
            videoConfig.setId(generatorResponse.getData().getTaskId());
            return videoConfig;
        } catch (Exception e) {
            throw new ServiceException(500, e.getMessage());
        }
    }

    /**
     * 开始生成单条视频
     *
     * @param reqVO
     */
    @Override
    public VideoGeneratorConfigV2 generateVideoV2(VideoConfigReqVO reqVO) {
        if (StringUtils.isBlank(reqVO.getVideoConfig())) {
            throw exception(PARAM_ERROR, "生成视频配置必填");
        }
        VideoGeneratorConfigV2 videoConfig = JSONUtil.toBean(reqVO.getVideoConfig(), VideoGeneratorConfigV2.class);
        if (Objects.isNull(videoConfig.getGlobalSettings())) {
            videoConfig.setGlobalSettings(new VideoGeneratorConfigV2.GlobalSettings());
        }

        if (JSONUtil.isTypeJSONObject(reqVO.getQuickConfiguration())) {
            JSONObject quickConfiguration = JSONObject.parseObject(reqVO.getQuickConfiguration());
            for (Map.Entry<String, Object> entry : quickConfiguration.entrySet()) {
                if (Objects.isNull(entry.getValue())) {
                    continue;
                }
                BeanPath beanPath = new BeanPath("globalSettings." + entry.getKey());
                try {
                    beanPath.set(videoConfig, entry.getValue());
                } catch (Exception ignored) {
                    log.warn("字段不存在 {}", entry.getKey());
                }
            }
        }

        String imageCode = reqVO.getImageCode();
        if (StringUtils.isBlank(imageCode)) {
            throw exception(PARAM_ERROR, "图片code必填");
        }

        CreativeContentDO creativeContent = creativeContentMapper.get(reqVO.getUid());
        if (Objects.isNull(creativeContent)) {
            throw exception(PARAM_ERROR, "创作内容不存在");
        }
        CreativeContentRespVO contentRespVO = CreativeContentConvert.INSTANCE.convert(creativeContent);
        Map<String, String> resources = buildResources(contentRespVO, reqVO.getImageUrl());

        List<ImageContent> imageContents = Optional.ofNullable(contentRespVO.getExecuteResult())
                .map(CreativeContentExecuteResult::getImageList).orElseThrow(() -> exception(PARAM_ERROR, "没有图片生成结果"));
        if (CollectionUtil.isEmpty(imageContents)) {
            throw exception(PARAM_ERROR, "没有图片生成结果");
        }

        if (Objects.isNull(videoConfig.getGlobalSettings().getVideo().getBackground())) {
            videoConfig.getGlobalSettings().getVideo().setBackground(new VideoGeneratorConfigV2.BackgroundConfig());
        }
        videoConfig.getGlobalSettings().getVideo().getBackground().setSource(reqVO.getImageUrl());
        videoConfig.setResources(resources);
        videoConfig.setId(null);
        try {
            VideoGeneratorResponse<VideoGeneratorResult> generatorResponse = videoGeneratorClient.videoGeneratorV2(videoConfig);
            if (generatorResponse.getCode() != 0) {
                throw ServiceExceptionUtil.exception(VIDEO_ERROR, generatorResponse.getMsg());
            }
            videoConfig.setId(generatorResponse.getData().getTaskId());
            return videoConfig;
        } catch (Exception e) {
            throw new ServiceException(500, e.getMessage());
        }
    }

    // 只做透传
    @Override
    public VideoContent videoResult(VideoResultReqVO resultReqVO) {
        try {
            VideoGeneratorResponse<VideoRecordResult> generatorResult = videoGeneratorClient.getGeneratorResult(resultReqVO.getVideoUid());
            if (generatorResult.getCode() != 0) {
                throw ServiceExceptionUtil.exception(VIDEO_ERROR, generatorResult.getMsg());
            }
            VideoRecordResult data = generatorResult.getData();
            VideoContent content = new VideoContent();
            content.setVideoUid(resultReqVO.getVideoUid());
            content.setVideoUrl(data.getUrl());
            content.setProgress(data.getProgress());
            content.setStage(data.getStage());
            content.setStatus(data.getStatus());
            content.setError(data.getError());
            content.setCode(resultReqVO.getImageCode());
            content.setImageUrl(resultReqVO.getImageUrl());
            content.setAudioUrl(data.getAudioUrl());
            return content;
        } catch (Exception e) {
            throw new ServiceException(500, e.getMessage());
        }
    }


    // 只做透传
    @Override
    public VideoMergeResult videoMerge(VideoMergeConfig config) {
        try {
            VideoGeneratorResponse<VideoMergeResult> generatorResult = videoGeneratorClient.mergeVideos(config);
            if (generatorResult.getCode() != 0) {
                throw ServiceExceptionUtil.exception(VIDEO_MERGE_ERROR, generatorResult.getMsg());
            }
            return generatorResult.getData();
        } catch (Exception e) {
            throw new ServiceException(500, e.getMessage());
        }
    }


    /**
     * 视频生成并发更新加锁
     */
    public void lockUpdate(String uid, String quickConfiguration, VideoContent updateVideoContent) {
        RLock lock = redissonClient.getLock("video_update_" + uid);
        try {
            if (!lock.tryLock(1, 3, TimeUnit.SECONDS)) {
                return;
            }
            CreativeContentDO oldContent = creativeContentMapper.get(uid);
            CreativeContentExecuteParam executeParam = getExecuteParam(oldContent);
            executeParam.setQuickConfiguration(quickConfiguration);

            CreativeContentExecuteResult executeResult = getExecuteResult(oldContent);
            List<VideoContent> videoContentList = Optional.ofNullable(executeResult)
                    .map(CreativeContentExecuteResult::getVideo)
                    .map(VideoContentInfo::getVideoList)
                    .orElse(Collections.emptyList());

            for (VideoContent videoContent : videoContentList) {
                // update status
                if (Objects.equals(updateVideoContent.getVideoUid(), videoContent.getVideoUid())) {

                }
            }

            CreativeContentDO updateContent = new CreativeContentDO();
            updateContent.setId(oldContent.getId());
            updateContent.setExecuteParam(JsonUtils.toJsonString(executeParam));
            creativeContentMapper.updateById(updateContent);

        } catch (Exception e) {
            log.warn("");
        } finally {
            if (lock.isHeldByCurrentThread() && lock.isLocked()) {
                lock.unlock();
            }
        }


    }


    @Deprecated
    public void generate(VideoConfigReqVO reqVO) {
        // lock  校验执行状态
        saveVideoConfig(reqVO);
        CreativeContentDO creativeContent = creativeContentMapper.get(reqVO.getUid());
        CreativeContentRespVO response = CreativeContentConvert.INSTANCE.convert(creativeContent);

        PosterStyleDTO posterStyle = CreativeUtils.getPosterStyle(response.getExecuteParam().getAppInformation());
        List<PosterTemplateDTO> templateList = posterStyle.getTemplateList();

        Map<String, String> resources = buildResources(response, reqVO.getImageCode());
        log.info(JSONUtil.toJsonPrettyStr(resources));
        if (true) {
            return;
        }

        List<VideoContent> videoContentList = new ArrayList<>(templateList.size());
        for (PosterTemplateDTO posterTemplateDTO : templateList) {
            if (!posterTemplateDTO.getOpenVideoMode()) {
                continue;
            }
            String videoConfig = posterTemplateDTO.getVideoConfig();
            String quickConfiguration = reqVO.getQuickConfiguration();
            // 合并参数

            String code = posterTemplateDTO.getCode();
            for (ImageContent imageContent : response.getExecuteResult().getImageList()) {
                if (!Objects.equals(code, imageContent.getCode())) {
                    continue;
                }
                //  调用异步执行接口

//                VideoContent videoContent = new VideoContent(imageContent.getCode(),
//                        imageContent.getName(), imageContent.getIndex(), "");
//                videoContentList.add(videoContent);
            }
        }

        // 更新结果
        CreativeContentExecuteResult executeResult = response.getExecuteResult();
        VideoContentInfo video = Optional.ofNullable(executeResult.getVideo()).orElse(new VideoContentInfo());
        video.setVideoList(videoContentList);
        executeResult.setVideo(video);

        creativeContent.setExecuteResult(JsonUtils.toJsonString(executeResult));
        creativeContentMapper.updateById(creativeContent);

        // 异步轮询结果
        ThreadPoolExecutor executor = creativeThreadPoolHolder.executor();
        executor.execute(() -> generateResult(creativeContent.getUid()));
    }

    @Deprecated
    public List<VideoContent> videoResult(String uid) {
        CreativeContentDO creativeContent = creativeContentMapper.get(uid);
        CreativeContentExecuteResult executeResult = getExecuteResult(creativeContent);
        return Optional.ofNullable(executeResult)
                .map(CreativeContentExecuteResult::getVideo)
                .map(VideoContentInfo::getVideoList)
                .orElse(Collections.emptyList());
    }

    @Deprecated
    private void generateResult(String uid) {
        int a = 300;
        try {
            while (a > 0) {
                TimeUnit.MILLISECONDS.sleep(1);
                CreativeContentDO creativeContent = creativeContentMapper.get(uid);
                CreativeContentExecuteResult executeResult = getExecuteResult(creativeContent);
                List<VideoContent> videoContentList = Optional.ofNullable(executeResult)
                        .map(CreativeContentExecuteResult::getVideo)
                        .map(VideoContentInfo::getVideoList)
                        .orElse(Collections.emptyList());

                for (VideoContent videoContent : videoContentList) {
                    // update status
                }
                creativeContent.setExecuteResult(JsonUtils.toJsonString(executeResult));
                creativeContentMapper.updateById(creativeContent);
                a--;
            }
        } catch (Exception e) {
            log.error("update video generate result error", e);
            CreativeContentDO creativeContent = creativeContentMapper.get(uid);
            CreativeContentExecuteResult executeResult = getExecuteResult(creativeContent);
            List<VideoContent> videoContentList = Optional.ofNullable(executeResult)
                    .map(CreativeContentExecuteResult::getVideo)
                    .map(VideoContentInfo::getVideoList)
                    .orElse(Collections.emptyList());
            for (VideoContent videoContent : videoContentList) {
//                videoContent.setMsg(e.getMessage());
            }
            creativeContent.setExecuteResult(JsonUtils.toJsonString(executeResult));
            creativeContentMapper.updateById(creativeContent);
        }
    }


    private Map<String, String> buildResources(CreativeContentRespVO contentRespVO, String imageUrl) {
        Map<String, String> resources = new HashMap<>();
        for (ImageContent imageContent : contentRespVO.getExecuteResult().getImageList()) {
            if (Objects.equals(imageContent.getUrl(), imageUrl)) {
                Map<String, PosterImageParam> finalParams = imageContent.getFinalParams();
                if (CollectionUtil.isEmpty(finalParams)) {
                    throw exception(PARAM_ERROR, "没有图片生成参数");
                }
                for (Map.Entry<String, PosterImageParam> entry : finalParams.entrySet()) {
                    if (Objects.nonNull(entry.getValue()) && StringUtils.isNoneBlank(entry.getValue().getText())) {
                        resources.put(entry.getKey(), entry.getValue().getText());
                    }
                }
            }
        }
        return resources;
    }

    /**
     * 讲创作内容实体转为创作内容响应对象，带有进度信息
     *
     * @param creativeContent 创作内容实体
     * @return 创作内容响应对象
     */
    private CreativeContentRespVO convertWithProgress(CreativeContentDO creativeContent) {
        CreativeContentRespVO response = CreativeContentConvert.INSTANCE.convert(creativeContent);
        // 计算是否包含视频生成配置
//        response.getExecuteParam().getAppInformation()
//                .setOpenVideoMode(
//                        CreativeUtils.checkOpenVideoMode(response.getExecuteParam().getAppInformation())
//                );

        if (!CreativeContentStatusEnum.SUCCESS.name().equals(response.getStatus())) {
            // 获取执行进度
            AppExecuteProgress progress = appStepStatusCache.progress(response.getConversationUid());
            response.setProgress(progress);
        }
        return response;
    }

    /**
     * 处理 regenerate 请求，将请求中的 app 信息合并到最新应用信息中
     *
     * @param request regenerate 请求
     * @return 处理后的应用信息
     */
    private AppMarketRespVO handlerAppInformation(CreativeContentRegenerateReqVO request) {
        CreativeContentExecuteParam executeParam = request.getExecuteParam();
        AppMarketRespVO appInformation = executeParam.getAppInformation();

        // 查询最新应用详细信息，内部有校验，进行校验应用是否存在
        AppMarketRespVO latestAppMarket = creativePlanService.getAppInformation(appInformation.getUid(), request.getSource());
        // 合并应用市场配置，某一些配置项需要保持最新
        AppMarketRespVO app = CreativeUtils.mergeAppInformation(appInformation, latestAppMarket, false);
        executeParam.setAppInformation(app);
        request.setExecuteParam(executeParam);
        return app;
    }

    /**
     * 获取上传素材步骤
     *
     * @param appInformation 应用信息
     * @return 上传素材步骤
     */
    private WorkflowStepWrapperRespVO materialStepWrapper(AppMarketRespVO appInformation) {
        WorkflowStepWrapperRespVO materialStepWrapper = appInformation.getStepByHandler(MaterialActionHandler.class);
        AppValidate.notNull(materialStepWrapper, "创作内容执行失败，素材上传步骤是必须的！请检查您的配置或联系管理员！");
        return materialStepWrapper;
    }

    /**
     * 获取图片生成步骤
     *
     * @param appInformation 应用信息
     * @return 图片生成步骤
     */
    private WorkflowStepWrapperRespVO posterStepWrapper(AppMarketRespVO appInformation) {
        WorkflowStepWrapperRespVO posterStepWrapper = appInformation.getStepByHandler(PosterActionHandler.class);
        AppValidate.notNull(posterStepWrapper, "创作内容执行失败，图片生成步骤是必须的！请检查您的配置或联系管理员！");
        return posterStepWrapper;
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
            AppValidate.notEmpty(materialFieldList, "创作内容执行失败：素材字段配置不能为空，请联系管理员！");
            return materialFieldList;
        } catch (ServiceException exception) {
            log.error("获取素材字段配置失败", exception);
            throw ServiceExceptionUtil.invalidParamException(exception.getMessage());
        } catch (Exception exception) {
            log.error("获取素材字段配置失败", exception);
            throw ServiceExceptionUtil.invalidParamException("创作内容执行失败：获取素材字段配置失败，请联系管理员！");
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
        AppValidate.notNull(materialHandler, "创作内容执行失败：素材库类型不支持，请联系管理员{}！", businessType);
        return materialHandler;
    }

    /**
     * 处理海报风格
     *
     * @param posterStepWrapper 海报步骤
     * @param appInformation    应用信息
     * @return 海报风格
     */
    private PosterStyleDTO handlerPosterStyle(WorkflowStepWrapperRespVO posterStepWrapper, AppMarketRespVO appInformation) {
        PosterStyleDTO posterStyle = CreativeUtils.getPosterStyleByStepWrapper(posterStepWrapper);
        AppValidate.notNull(posterStyle, "创作内容执行失败: 图片生成配置不能为空！请配置图片生成后重试！");
        // 从应用市场获取最新的系统配置合并
        posterStyle = CreativeUtils.mergeImagePosterStyle(posterStyle, appInformation);
        // 处理一下海报风格
        return CreativeUtils.handlerPosterStyle(posterStyle);
    }

    /**
     * 生成海报
     *
     * @param template        海报模板
     * @param materialStepId  素材步骤ID
     * @param materialList    素材列表
     * @param wordField       单词字段
     * @param paraphraseField 单词释义字段
     * @return 海报列表
     */
    private List<String> poster(PosterTemplateDTO template,
                                String materialStepId,
                                List<Map<String, Object>> materialList,
                                String wordField,
                                String paraphraseField,
                                int count,
                                int fieldCount,
                                int i) {

        PosterTemplateDTO posterTemplateDTO = SerializationUtils.clone(template);
        List<PosterVariableDTO> templateVariableList = posterTemplateDTO.getVariableList();

        for (PosterVariableDTO posterVariableDTO : templateVariableList) {
            String name = posterVariableDTO.getLabel();
            if (CreativeUtils.isWordField(name)) {
                int index = CreativeUtils.getWordFieldIndex(name) - 1;
                if (index < 0) {
                    continue;
                }
                if (materialList.size() == 1 && fieldCount > 1) {
                    int handleIndex = index + 1;
                    int wordFieldIndex = i * count + handleIndex;
                    String handleWordField = CreativeUtils.handleField(wordField) + wordFieldIndex;
                    String value = "{{" + materialStepId + ".docs[0]." + handleWordField + "}}";
                    posterVariableDTO.setValue(value);
                    continue;
                }
                String value = "{{" + materialStepId + ".docs[" + index + "]." + wordField + "}}";
                posterVariableDTO.setValue(value);
                continue;
            }
            if (CreativeUtils.isParaphraseField(name)) {
                int index = CreativeUtils.getParaphraseFieldIndex(name) - 1;
                if (index < 0) {
                    continue;
                }
                if (materialList.size() == 1 && fieldCount > 1) {
                    int handleIndex = index + 1;
                    int paraphraseFieldIndex = i * count + handleIndex;
                    String handleParaphraseField = CreativeUtils.handleField(paraphraseField) + paraphraseFieldIndex;
                    String value = "{{" + materialStepId + ".docs[0]." + handleParaphraseField + "}}";
                    posterVariableDTO.setValue(value);
                    continue;
                }
                String value = "{{" + materialStepId + ".docs[" + index + "]." + paraphraseField + "}}";
                posterVariableDTO.setValue(value);
            }
        }

        Map<String, Object> materialMap = new HashMap<>();
        JsonDocsDefSchema materialData = new JsonDocsDefSchema();
        materialData.setDocs(materialList);
        materialMap.put(materialStepId, materialData);

        Map<String, Object> docPosterVariableMap = PosterActionHandler.getDocPosterVariableMap(posterTemplateDTO);
        Map<String, Object> replaceValueMap = AppContext.parseMapFromVariablesValues(docPosterVariableMap, materialMap);

        for (PosterVariableDTO posterVariableDTO : templateVariableList) {
            String uuid = posterVariableDTO.getUuid();
            // 从作用域数据中获取变量值
            Object value = replaceValueMap.get(uuid);
            // 如果从作用域数据中获取的变量值为空，则为空字符串。
            if (StringUtil.objectBlank(value)) {
                value = StringUtils.EMPTY;
            }
            posterVariableDTO.setValue(value);
        }

        Map<String, Object> params = CollectionUtil.emptyIfNull(templateVariableList).stream()
                .collect(Collectors.toMap(PosterVariableDTO::getField, PosterVariableDTO::emptyIfNullValue));

        PosterRequest posterRequest = new PosterRequest();
        posterRequest.setId(posterTemplateDTO.getCode());
        posterRequest.setParams(params);

        List<PosterImage> poster = posterService.poster(posterRequest);

        return CollectionUtil.emptyIfNull(poster).stream()
                .map(PosterImage::getUrl)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
    }

    public static CreativeContentExecuteParam getExecuteParam(CreativeContentDO content) {
        try {
            CreativeContentExecuteParam param = JsonUtils.parseObject(content.getExecuteParam(), CreativeContentExecuteParam.class);
            AppValidate.notNull(param, "获取创作内容执行参数失败");
            return param;
        } catch (Exception e) {
            log.error("获取创作内容执行参数失败", e);
            throw ServiceExceptionUtil.invalidParamException("获取创作内容执行参数失败");
        }
    }

    public static CreativeContentExecuteResult getExecuteResult(CreativeContentDO content) {
        try {
            CreativeContentExecuteResult result = JsonUtils.parseObject(content.getExecuteResult(), CreativeContentExecuteResult.class);
            AppValidate.notNull(result, "获取创作内容执行结果失败");
            return result;
        } catch (Exception e) {
            log.error("获取创作内容执行结果失败", e);
            throw ServiceExceptionUtil.invalidParamException("获取创作内容执行结果失败");
        }
    }

}
