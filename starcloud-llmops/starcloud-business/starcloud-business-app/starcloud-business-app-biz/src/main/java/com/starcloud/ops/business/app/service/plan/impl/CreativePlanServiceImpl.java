package com.starcloud.ops.business.app.service.plan.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.plan.dto.CreativePlanAppExecuteDTO;
import com.starcloud.ops.business.app.api.plan.dto.CreativePlanConfigDTO;
import com.starcloud.ops.business.app.api.plan.dto.CreativePlanExecuteDTO;
import com.starcloud.ops.business.app.api.plan.dto.CreativePlanImageExecuteDTO;
import com.starcloud.ops.business.app.api.plan.dto.CreativePlanImageStyleExecuteDTO;
import com.starcloud.ops.business.app.api.plan.vo.request.CreativePlanModifyReqVO;
import com.starcloud.ops.business.app.api.plan.vo.request.CreativePlanPageQuery;
import com.starcloud.ops.business.app.api.plan.vo.request.CreativePlanReqVO;
import com.starcloud.ops.business.app.api.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.api.scheme.dto.CreativeSchemeConfigDTO;
import com.starcloud.ops.business.app.api.scheme.dto.CreativeSchemeCopyWritingTemplateDTO;
import com.starcloud.ops.business.app.api.scheme.dto.CreativeSchemeImageTemplateDTO;
import com.starcloud.ops.business.app.api.scheme.dto.CreativeSchemeReferenceDTO;
import com.starcloud.ops.business.app.api.scheme.vo.response.CreativeSchemeRespVO;
import com.starcloud.ops.business.app.api.xhs.XhsImageStyleDTO;
import com.starcloud.ops.business.app.api.xhs.XhsImageTemplateDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentCreateReq;
import com.starcloud.ops.business.app.convert.plan.CreativePlanConvert;
import com.starcloud.ops.business.app.dal.databoject.plan.CreativePlanDO;
import com.starcloud.ops.business.app.dal.databoject.plan.CreativePlanPO;
import com.starcloud.ops.business.app.dal.databoject.xhs.XhsCreativeContentDO;
import com.starcloud.ops.business.app.dal.mysql.plan.CreativePlanMapper;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.plan.CreativePlanStatusEnum;
import com.starcloud.ops.business.app.enums.plan.CreativeRandomTypeEnum;
import com.starcloud.ops.business.app.enums.plan.CreativeTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.XhsCreativeContentStatusEnums;
import com.starcloud.ops.business.app.enums.xhs.XhsCreativeContentTypeEnums;
import com.starcloud.ops.business.app.service.plan.CreativePlanService;
import com.starcloud.ops.business.app.service.scheme.CreativeSchemeService;
import com.starcloud.ops.business.app.service.xhs.XhsCreativeContentService;
import com.starcloud.ops.business.app.service.xhs.XhsService;
import com.starcloud.ops.business.app.util.CreativeUtil;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private XhsCreativeContentService xhsCreativeContentService;

    @Resource
    private XhsService xhsService;

    @Resource
    private CreativeSchemeService creativeSchemeService;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 获取创作计划详情
     *
     * @param uid 创作计划UID
     * @return 创作计划详情
     */
    @Override
    public CreativePlanRespVO get(String uid) {
        AppValidate.notBlank(uid, ErrorCodeConstants.CREATIVE_PLAN_UID_REQUIRED);
        CreativePlanDO creativePlan = creativePlanMapper.get(uid);
        AppValidate.notNull(creativePlan, ErrorCodeConstants.CREATIVE_PLAN_NOT_EXIST, uid);
        return CreativePlanConvert.INSTANCE.convertResponse(creativePlan);
    }

    /**
     * 获取模板列表
     *
     * @return 模板列表
     */
    @Override
    public List<CreativePlanRespVO> listTemplates() {
        CreativePlanConfigDTO config = new CreativePlanConfigDTO();
        CreativePlanRespVO redBookResponse = new CreativePlanRespVO();
        redBookResponse.setUid("red-book");
        redBookResponse.setName("小红书模板");
        redBookResponse.setType(CreativeTypeEnum.XHS.name());
        redBookResponse.setConfig(config);
        redBookResponse.setRandomType(CreativeRandomTypeEnum.RANDOM.name());
        redBookResponse.setTotal(5);
        redBookResponse.setDescription("小红书模板");
        return Collections.singletonList(redBookResponse);
    }

    /**
     * 获取创作计划分页列表
     *
     * @param query 请求参数
     * @return 创作计划分页列表
     */
    @Override
    public PageResp<CreativePlanRespVO> page(CreativePlanPageQuery query) {
        IPage<CreativePlanPO> page = creativePlanMapper.pageCreativePlan(PageUtil.page(query), query);
        return CreativePlanConvert.INSTANCE.convertPage(page);
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
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_NAME_EXIST, request.getName());
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
        AppValidate.notBlank(request.getUid(), ErrorCodeConstants.CREATIVE_PLAN_UID_REQUIRED);
        CreativePlanDO plan = creativePlanMapper.get(request.getUid());
        AppValidate.notNull(plan, ErrorCodeConstants.CREATIVE_PLAN_NOT_EXIST, request.getUid());

        CreativePlanDO copyPlan = new CreativePlanDO();
        copyPlan.setUid(IdUtil.fastSimpleUUID());
        copyPlan.setName(getCopyName(plan.getName()));
        copyPlan.setType(plan.getType());
        copyPlan.setConfig(plan.getConfig());
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
        AppValidate.notBlank(request.getUid(), ErrorCodeConstants.CREATIVE_PLAN_UID_REQUIRED);
        handlerAndValidate(request);
        CreativePlanDO plan = creativePlanMapper.get(request.getUid());
        AppValidate.notNull(plan, ErrorCodeConstants.CREATIVE_PLAN_NOT_EXIST, request.getUid());
        if (!CreativePlanStatusEnum.PENDING.name().equals(plan.getStatus())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_STATUS_NOT_SUPPORT_MODIFY);
        }
        // 名称做了修改，且修改之后的名称已经存在
        if (!plan.getName().equals(request.getName()) && creativePlanMapper.distinctName(request.getName())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_NAME_EXIST, request.getName());
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
        AppValidate.notBlank(uid, ErrorCodeConstants.CREATIVE_PLAN_UID_REQUIRED);
        AppValidate.notBlank(status, ErrorCodeConstants.CREATIVE_PLAN_STATUS_REQUIRED);

        if (!CreativePlanStatusEnum.contains(status) || CreativePlanStatusEnum.PENDING.name().equals(status)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_STATUS_NOT_SUPPORT_MODIFY);
        }

        CreativePlanDO plan = creativePlanMapper.get(uid);
        AppValidate.notNull(plan, ErrorCodeConstants.CREATIVE_PLAN_NOT_EXIST, uid);

        // 更新
        LocalDateTime now = LocalDateTime.now();
        // 计算耗时，毫秒数 now - startTime
        Duration duration = Duration.between(plan.getStartTime(), now);
        LambdaUpdateWrapper<CreativePlanDO> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(CreativePlanDO::getStatus, status);
        updateWrapper.set(CreativePlanDO::getEndTime, LocalDateTime.now());
        updateWrapper.set(CreativePlanDO::getElapsed, duration.toMillis());
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
        String key = "xhs-plan-" + planUid;
        RLock lock = redissonClient.getLock(key);
        try {
            if (!lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                return;
            }
            List<XhsCreativeContentDO> contentList = xhsCreativeContentService.listByPlanUid(planUid);
            // 是否全部执行结束
            boolean unComplete = contentList.stream().anyMatch(xhsCreativeContentDO -> {
                if (xhsCreativeContentDO.getRetryCount() != null && xhsCreativeContentDO.getRetryCount() > 3) {
                    return false;
                }
                return !XhsCreativeContentStatusEnums.EXECUTE_SUCCESS.getCode().equals(xhsCreativeContentDO.getStatus());
            });
            updateStatus(planUid, unComplete ? CreativePlanStatusEnum.RUNNING.name() : CreativePlanStatusEnum.COMPLETE.name());
        } catch (Exception e) {
            log.warn("更新计划失败", e);
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
        AppValidate.notBlank(uid, ErrorCodeConstants.CREATIVE_PLAN_UID_REQUIRED);
        CreativePlanDO plan = creativePlanMapper.get(uid);
        AppValidate.notNull(plan, ErrorCodeConstants.CREATIVE_PLAN_NOT_EXIST, uid);
        // 删除创作计划
        creativePlanMapper.deleteById(plan.getId());
        // 删除创作计划下的创作内容
        xhsCreativeContentService.deleteByPlanUid(uid);
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
        AppValidate.notBlank(uid, ErrorCodeConstants.CREATIVE_PLAN_UID_REQUIRED);
        CreativePlanRespVO plan = this.get(uid);
        // 目前只支持随机执行
        if (!CreativeRandomTypeEnum.RANDOM.name().equals(plan.getRandomType())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_RANDOM_TYPE_NOT_SUPPORTED, plan.getRandomType());
        }
        // 批量执行随机任务
        this.bathRandomTask(plan);
        // 更新状态
        LambdaUpdateWrapper<CreativePlanDO> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(CreativePlanDO::getStatus, CreativePlanStatusEnum.RUNNING.name());
        updateWrapper.set(CreativePlanDO::getStartTime, LocalDateTime.now());
        updateWrapper.eq(CreativePlanDO::getUid, uid);
        creativePlanMapper.update(null, updateWrapper);
    }

    /**
     * 创建随机任务
     *
     * @param plan 创作计划
     */
    private void bathRandomTask(CreativePlanRespVO plan) {
        // 获取生成任务数量量
        Integer total = plan.getTotal();
        CreativePlanConfigDTO config = plan.getConfig();
        // 图片素材列表
        List<String> imageUrlList = config.getImageUrlList();
        // 处理创作内容执行参数
        List<CreativePlanExecuteDTO> executeParamsList = handlerCreativeContentExecuteParams(plan);
        // 插入任务
        List<XhsCreativeContentCreateReq> xhsCreativeContentCreateReqList = new ArrayList<>(total * 2);
        for (int i = 0; i < total; i++) {
            String businessUid = IdUtil.fastSimpleUUID();
            int randomInt = RandomUtil.randomInt(executeParamsList.size());
            CreativePlanExecuteDTO executeParam = SerializationUtils.clone(executeParamsList.get(randomInt));

            // 应用执行任务
            XhsCreativeContentCreateReq appCreateRequest = new XhsCreativeContentCreateReq();
            // 克隆图片执行参数, 防止引用问题
            CreativePlanAppExecuteDTO appExecuteRequest = executeParam.getAppExecuteRequest();
            appCreateRequest.setPlanUid(plan.getUid());
            appCreateRequest.setSchemeUid(executeParam.getSchemeUid());
            appCreateRequest.setBusinessUid(businessUid);
            appCreateRequest.setType(XhsCreativeContentTypeEnums.COPY_WRITING.getCode());
            appCreateRequest.setTempUid(appExecuteRequest.getUid());
            appCreateRequest.setExecuteParams(CreativePlanExecuteDTO.ofApp(appExecuteRequest));
            xhsCreativeContentCreateReqList.add(appCreateRequest);

            // 图片执行任务
            XhsCreativeContentCreateReq imageCreateRequest = new XhsCreativeContentCreateReq();
            // 克隆图片执行参数, 防止引用问题
            CreativePlanImageStyleExecuteDTO imageStyleExecuteRequest = executeParam.getImageStyleExecuteRequest();
            String tempUid = CollectionUtil.emptyIfNull(imageStyleExecuteRequest.getImageRequests()).stream().map(CreativePlanImageExecuteDTO::getImageTemplate).collect(Collectors.joining(","));
            imageCreateRequest.setPlanUid(plan.getUid());
            imageCreateRequest.setSchemeUid(executeParam.getSchemeUid());
            imageCreateRequest.setBusinessUid(businessUid);
            imageCreateRequest.setType(XhsCreativeContentTypeEnums.PICTURE.getCode());
            imageCreateRequest.setTempUid(tempUid);
            imageCreateRequest.setExecuteParams(CreativePlanExecuteDTO.ofImageStyle(imageStyleExecuteRequest));
            imageCreateRequest.setUsePicture(imageUrlList);
            xhsCreativeContentCreateReqList.add(imageCreateRequest);
        }

        // 批量插入任务
        xhsCreativeContentService.create(xhsCreativeContentCreateReqList);
    }

    /**
     * 处理创作内容执行参数
     *
     * @param plan 创作计划
     * @return 创作内容执行参数
     */
    private List<CreativePlanExecuteDTO> handlerCreativeContentExecuteParams(CreativePlanRespVO plan) {
        // 校验配置信息
        validateCreativePlanConfig(plan);
        // 配置信息
        CreativePlanConfigDTO planConfig = plan.getConfig();
        // 查询并且校验创作方案是否存在
        List<CreativeSchemeRespVO> schemeList = getSchemeList(planConfig.getSchemeUidList());
        // 查询并且校验应用是否存在
        AppMarketRespVO app = xhsService.getExecuteApp(CreativeTypeEnum.XHS.name());
        // 处理创作内容执行参数
        List<CreativePlanExecuteDTO> list = Lists.newArrayList();
        for (CreativeSchemeRespVO scheme : schemeList) {
            // 校验创作配置方案
            validateCreativeSchemeConfig(scheme);
            CreativeSchemeConfigDTO configuration = scheme.getConfiguration();
            CreativeSchemeImageTemplateDTO imageTemplate = configuration.getImageTemplate();
            // 获取应用执行参数
            CreativePlanAppExecuteDTO appExecute = CreativeUtil.getXhsAppExecuteRequest(scheme, planConfig, app.getUid());
            for (XhsImageStyleDTO style : imageTemplate.getStyleList()) {
                List<XhsImageTemplateDTO> templateList = style.getTemplateList();
                AppValidate.notEmpty(templateList, ErrorCodeConstants.CREATIVE_SCHEME_IMAGE_TEMPLATE_STYLE_TEMPLATE_LIST_NOT_EMPTY, style.getName());
                // 图片执行参数
                CreativePlanImageStyleExecuteDTO styleExecute = CreativeUtil.getImageStyleExecuteRequest(style);
                CreativePlanExecuteDTO planExecute = new CreativePlanExecuteDTO();
                planExecute.setSchemeUid(scheme.getUid());
                planExecute.setAppExecuteRequest(appExecute);
                planExecute.setImageStyleExecuteRequest(styleExecute);
                list.add(planExecute);
            }
        }
        return list;
    }

    /**
     * 查询并且校验创作方案是否存在
     *
     * @param schemeUidList 创作方案UID列表
     * @return 创作方案列表
     */
    private List<CreativeSchemeRespVO> getSchemeList(List<String> schemeUidList) {
        List<CreativeSchemeRespVO> schemeList = creativeSchemeService.list(schemeUidList);
        AppValidate.notEmpty(schemeList, ErrorCodeConstants.CREATIVE_PLAN_SCHEME_NOT_EXIST);
        return schemeList;
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
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_TYPE_NOT_SUPPORTED, request.getType());
        }
        CreativePlanConfigDTO config = request.getConfig();
        AppValidate.notNull(config, ErrorCodeConstants.CREATIVE_PLAN_CONFIG_NOT_NULL, request.getName());
        config.validate();
    }

    /**
     * 校验创作计划配置
     *
     * @param plan 创作计划
     */
    private void validateCreativePlanConfig(CreativePlanRespVO plan) {
        // 校验配置信息
        CreativePlanConfigDTO config = plan.getConfig();
        AppValidate.notNull(config, ErrorCodeConstants.CREATIVE_PLAN_CONFIG_NOT_NULL, plan.getName());
        // 图片素材列表
        AppValidate.notEmpty(config.getImageUrlList(), ErrorCodeConstants.CREATIVE_PLAN_UPLOAD_IMAGE_EMPTY);
        // 校验创作方案
        AppValidate.notEmpty(config.getSchemeUidList(), ErrorCodeConstants.CREATIVE_PLAN_SCHEME_NOT_EMPTY);
        // 创作计划变量校验
        if (CollectionUtil.isEmpty(config.getParamMap())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_PARAM_MAP_NOT_EMPTY);
        }
    }

    /**
     * 校验创作方案配置
     *
     * @param scheme 创作方案
     */
    private void validateCreativeSchemeConfig(CreativeSchemeRespVO scheme) {
        // 获取创作方案的参考账号信息
        List<CreativeSchemeReferenceDTO> refers = scheme.getRefers();
        AppValidate.notEmpty(refers, ErrorCodeConstants.CREATIVE_SCHEME_REFERS_NOT_EMPTY, scheme.getName());

        CreativeSchemeConfigDTO configuration = scheme.getConfiguration();
        AppValidate.notNull(configuration, ErrorCodeConstants.CREATIVE_SCHEME_CONFIGURATION_NOT_NULL, scheme.getName());

        CreativeSchemeCopyWritingTemplateDTO copyWritingTemplate = configuration.getCopyWritingTemplate();
        AppValidate.notNull(copyWritingTemplate, ErrorCodeConstants.CREATIVE_SCHEME_COPY_WRITING_TEMPLATE_NOT_NULL, scheme.getName());

        CreativeSchemeImageTemplateDTO imageTemplate = configuration.getImageTemplate();
        AppValidate.notNull(imageTemplate, ErrorCodeConstants.CREATIVE_SCHEME_IMAGE_TEMPLATE_NOT_NULL, scheme.getName());

        List<XhsImageStyleDTO> styleList = imageTemplate.getStyleList();
        AppValidate.notEmpty(styleList, ErrorCodeConstants.CREATIVE_SCHEME_IMAGE_TEMPLATE_STYLE_LIST_NOT_EMPTY, scheme.getName());
    }
}
