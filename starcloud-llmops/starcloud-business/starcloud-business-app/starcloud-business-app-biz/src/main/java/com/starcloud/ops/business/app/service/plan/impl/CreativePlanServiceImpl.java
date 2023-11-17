package com.starcloud.ops.business.app.service.plan.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketListQuery;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.plan.dto.CreativePlanConfigDTO;
import com.starcloud.ops.business.app.api.plan.vo.request.CreativePlanModifyReqVO;
import com.starcloud.ops.business.app.api.plan.vo.request.CreativePlanPageQuery;
import com.starcloud.ops.business.app.api.plan.vo.request.CreativePlanReqVO;
import com.starcloud.ops.business.app.api.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.api.scheme.dto.CreativeSchemeConfigDTO;
import com.starcloud.ops.business.app.api.scheme.dto.CreativeSchemeCopyWritingTemplateDTO;
import com.starcloud.ops.business.app.api.scheme.dto.CreativeSchemeImageTemplateDTO;
import com.starcloud.ops.business.app.api.scheme.dto.CreativeSchemeReferenceDTO;
import com.starcloud.ops.business.app.api.scheme.vo.request.CreativeSchemeListReqVO;
import com.starcloud.ops.business.app.api.scheme.vo.response.CreativeSchemeRespVO;
import com.starcloud.ops.business.app.api.xhs.XhsAppResponse;
import com.starcloud.ops.business.app.api.xhs.XhsImageStyleDTO;
import com.starcloud.ops.business.app.api.xhs.XhsImageTemplateDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsBathImageExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.dto.XhsCreativeContentExecuteParamsDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentCreateReq;
import com.starcloud.ops.business.app.convert.plan.CreativePlanConvert;
import com.starcloud.ops.business.app.dal.databoject.plan.CreativePlanDO;
import com.starcloud.ops.business.app.dal.databoject.plan.CreativePlanPO;
import com.starcloud.ops.business.app.dal.databoject.xhs.XhsCreativeContentDO;
import com.starcloud.ops.business.app.dal.mysql.plan.CreativePlanMapper;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.plan.CreativePlanStatusEnum;
import com.starcloud.ops.business.app.enums.plan.CreativeRandomTypeEnum;
import com.starcloud.ops.business.app.enums.plan.CreativeTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.XhsCreativeContentStatusEnums;
import com.starcloud.ops.business.app.enums.xhs.XhsCreativeContentTypeEnums;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.plan.CreativePlanService;
import com.starcloud.ops.business.app.service.scheme.CreativeSchemeService;
import com.starcloud.ops.business.app.service.xhs.XhsCreativeContentService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private XhsCreativeContentService xhsCreativeContentService;

    @Resource
    private AppMarketService appMarketService;

    @Resource
    private CreativeSchemeService creativeSchemeService;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 文案模板列表
     *
     * @param type 类型
     * @return 文案模板列表
     */
    @Override
    public List<XhsAppResponse> copyWritingTemplates(String type) {
        AppValidate.notBlank(type, ErrorCodeConstants.CREATIVE_PLAN_TYPE_REQUIRED);
        CreativeTypeEnum typeEnum = CreativeTypeEnum.of(type);
        if (typeEnum == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_TYPE_NOT_SUPPORTED, type);
        }
        AppMarketListQuery query = new AppMarketListQuery();
        query.setIsSimple(Boolean.FALSE);
        query.setTags(typeEnum.getTagType().getTags());
        List<AppMarketRespVO> list = appMarketService.list(query);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().map(item -> {
            List<VariableItemRespVO> variableList = Optional.ofNullable(item).map(AppMarketRespVO::getWorkflowConfig)
                    .map(WorkflowConfigRespVO::getSteps)
                    .map(steps -> steps.get(0)).map(WorkflowStepWrapperRespVO::getVariable)
                    .map(VariableRespVO::getVariables)
                    .orElseThrow(() -> ServiceExceptionUtil.exception(new ErrorCode(310900100, "系统步骤不能为空")));
            variableList = variableList.stream().filter(VariableItemRespVO::getIsShow).collect(Collectors.toList());
            XhsAppResponse response = new XhsAppResponse();
            response.setUid(item.getUid());
            response.setName(item.getName());
            response.setCategory(item.getCategory());
            response.setIcon(item.getIcon());
            response.setDescription(item.getDescription());
            response.setVariables(variableList);
            return response;
        }).collect(Collectors.toList());
    }

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
    public void create(CreativePlanReqVO request) {
        handlerAndValidate(request);
        if (creativePlanMapper.distinctName(request.getName())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_NAME_EXIST, request.getName());
        }
        CreativePlanDO plan = CreativePlanConvert.INSTANCE.convertCreateRequest(request);
        creativePlanMapper.insert(plan);
    }

    /**
     * 复制创作计划
     *
     * @param request 创作计划请求
     */
    @Override
    public void copy(UidRequest request) {
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
    }

    /**
     * 修改创作计划
     *
     * @param request 创作计划请求
     */
    @Override
    public void modify(CreativePlanModifyReqVO request) {
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

        // 校验配置信息
        CreativePlanConfigDTO config = plan.getConfig();
        AppValidate.notNull(config, ErrorCodeConstants.CREATIVE_PLAN_CONFIG_NOT_NULL, uid);

        // 图片素材列表
        List<String> imageUrlList = config.getImageUrlList();
        AppValidate.notEmpty(imageUrlList, ErrorCodeConstants.CREATIVE_PLAN_UPLOAD_IMAGE_EMPTY);

        // 校验创作方案
        List<String> schemeUidList = config.getSchemeUidList();
        AppValidate.notEmpty(schemeUidList, ErrorCodeConstants.CREATIVE_PLAN_SCHEME_NOT_EMPTY);

        // 查询创作方案
        CreativeSchemeListReqVO schemeQuery = new CreativeSchemeListReqVO();
        schemeQuery.setUidList(schemeUidList);
        List<CreativeSchemeRespVO> schemeList = creativeSchemeService.list(schemeQuery);
        AppValidate.notEmpty(schemeList, ErrorCodeConstants.CREATIVE_PLAN_SCHEME_NOT_EXIST);

        // 处理创作内容执行参数
        List<XhsCreativeContentExecuteParamsDTO> executeParamsList = handlerCreativeContentExecuteParams(schemeList);

        // 批量执行
        this.bathRandomTask(plan, executeParamsList);

        // 更新状态
        LambdaUpdateWrapper<CreativePlanDO> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(CreativePlanDO::getStatus, CreativePlanStatusEnum.RUNNING.name());
        updateWrapper.set(CreativePlanDO::getStartTime, LocalDateTime.now());
        updateWrapper.eq(CreativePlanDO::getUid, uid);
        creativePlanMapper.update(null, updateWrapper);
    }

    /**
     * 处理创作内容执行参数
     *
     * @param schemeList 创作方案列表
     * @return 创作内容执行参数
     */
    private List<XhsCreativeContentExecuteParamsDTO> handlerCreativeContentExecuteParams(List<CreativeSchemeRespVO> schemeList) {

        List<XhsAppResponse> xhsAppResponses = copyWritingTemplates(CreativeTypeEnum.XHS.name());
        AppValidate.notEmpty(xhsAppResponses, ErrorCodeConstants.CREATIVE_PLAN_APP_NOT_EXIST);

        XhsAppResponse appResponse = xhsAppResponses.get(0);
        AppValidate.notNull(appResponse, ErrorCodeConstants.CREATIVE_PLAN_APP_NOT_EXIST);

        List<XhsCreativeContentExecuteParamsDTO> list = Lists.newArrayList();
        for (CreativeSchemeRespVO scheme : schemeList) {
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

            // 获取应用执行参数
            XhsAppExecuteRequest appExecuteRequest = getXhsAppExecuteRequest(copyWritingTemplate, appResponse);

            for (XhsImageStyleDTO style : styleList) {
                List<XhsImageTemplateDTO> templateList = style.getTemplateList();
                AppValidate.notEmpty(templateList, ErrorCodeConstants.CREATIVE_SCHEME_IMAGE_TEMPLATE_STYLE_TEMPLATE_LIST_NOT_EMPTY, style.getName());
                // 图片执行参数
                XhsBathImageExecuteRequest bathImageExecuteRequest = getBathImageExecuteRequest(templateList);

                XhsCreativeContentExecuteParamsDTO executeParams = new XhsCreativeContentExecuteParamsDTO();
                executeParams.setAppExecuteRequest(appExecuteRequest);
                executeParams.setBathImageExecuteRequest(bathImageExecuteRequest);
                list.add(executeParams);
            }
        }

        return list;
    }

    /**
     * 获取小红书应用执行参数
     *
     * @param copyWritingTemplate 文案生成模板
     * @param appResponse         应用信息
     * @return 应用执行参数
     */
    private XhsAppExecuteRequest getXhsAppExecuteRequest(CreativeSchemeCopyWritingTemplateDTO copyWritingTemplate, XhsAppResponse appResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("IS_PROMOTE_MP", copyWritingTemplate.getIsPromoteMp());
        params.put("MP_CODE", copyWritingTemplate.getMpCode());
        params.put("DEMAND", copyWritingTemplate.getDemand());
        params.put("EXAMPLE", copyWritingTemplate.getExample());

        XhsAppExecuteRequest appExecuteRequest = new XhsAppExecuteRequest();
        appExecuteRequest.setUid(appResponse.getUid());
        appExecuteRequest.setParams(params);
        appExecuteRequest.setScene(AppSceneEnum.XHS_WRITING.name());
        return appExecuteRequest;
    }

    /**
     * 获取小红书批量图片执行参数
     *
     * @param templateList 图片模板列表
     * @return 图片执行参数
     */
    private XhsBathImageExecuteRequest getBathImageExecuteRequest(List<XhsImageTemplateDTO> templateList) {
        // 图片参数信息
        List<XhsImageExecuteRequest> imageExecuteRequestList = new ArrayList<>();
        // 处理图片变量
        for (XhsImageTemplateDTO template : templateList) {
            Map<String, Object> imageParams = new HashMap<>();
            for (VariableItemDTO variableItem : CollectionUtil.emptyIfNull(template.getVariables())) {
                // 图片参数，随机放入图片
                if ("IMAGE".equals(variableItem.getType())) {
                    imageParams.put(variableItem.getField(), "IMAGE_VARIABLE");
                } else {
                    imageParams.put(variableItem.getField(), variableItem.getValue());
                }
            }
            XhsImageExecuteRequest imageExecuteRequest = new XhsImageExecuteRequest();
            imageExecuteRequest.setImageTemplate(template.getId());
            imageExecuteRequest.setParams(imageParams);
            imageExecuteRequestList.add(imageExecuteRequest);
        }
        // 图片执行参数
        XhsBathImageExecuteRequest bathImageExecuteRequest = new XhsBathImageExecuteRequest();
        bathImageExecuteRequest.setImageRequests(imageExecuteRequestList);
        return bathImageExecuteRequest;
    }

    /**
     * 创建随机任务
     *
     * @param plan 创作计划
     */
    private void bathRandomTask(CreativePlanRespVO plan, List<XhsCreativeContentExecuteParamsDTO> executeParamsList) {
        // 获取生成任务数量量
        Integer total = plan.getTotal();
        CreativePlanConfigDTO config = plan.getConfig();
        // 图片素材列表
        List<String> imageUrlList = config.getImageUrlList();
        // 插入任务
        List<XhsCreativeContentCreateReq> xhsCreativeContentCreateReqList = new ArrayList<>(total * 2);
        for (int i = 0; i < total; i++) {
            String businessUid = IdUtil.fastSimpleUUID();
            int randomInt = RandomUtil.randomInt(executeParamsList.size());
            XhsCreativeContentExecuteParamsDTO executeParam = SerializationUtils.clone(executeParamsList.get(randomInt));

            // 应用执行任务
            XhsCreativeContentCreateReq appCreateRequest = new XhsCreativeContentCreateReq();
            // 克隆图片执行参数, 防止引用问题
            XhsAppExecuteRequest appExecuteRequest = executeParam.getAppExecuteRequest();
            appCreateRequest.setPlanUid(plan.getUid());
            appCreateRequest.setBusinessUid(businessUid);
            appCreateRequest.setType(XhsCreativeContentTypeEnums.COPY_WRITING.getCode());
            appCreateRequest.setTempUid(appExecuteRequest.getUid());
            appCreateRequest.setExecuteParams(XhsCreativeContentExecuteParamsDTO.ofApp(appExecuteRequest));
            xhsCreativeContentCreateReqList.add(appCreateRequest);

            // 图片执行任务
            XhsCreativeContentCreateReq imageCreateRequest = new XhsCreativeContentCreateReq();
            // 克隆图片执行参数, 防止引用问题
            XhsBathImageExecuteRequest bathImageExecuteRequest = executeParam.getBathImageExecuteRequest();
            String tempUid = CollectionUtil.emptyIfNull(bathImageExecuteRequest.getImageRequests()).stream().map(XhsImageExecuteRequest::getImageTemplate).collect(Collectors.joining(","));
            imageCreateRequest.setPlanUid(plan.getUid());
            imageCreateRequest.setBusinessUid(businessUid);
            imageCreateRequest.setType(XhsCreativeContentTypeEnums.PICTURE.getCode());
            imageCreateRequest.setTempUid(tempUid);
            imageCreateRequest.setExecuteParams(XhsCreativeContentExecuteParamsDTO.ofBathImage(bathImageExecuteRequest));
            imageCreateRequest.setUsePicture(imageUrlList);
            xhsCreativeContentCreateReqList.add(imageCreateRequest);
        }

        // 批量插入任务
        xhsCreativeContentService.create(xhsCreativeContentCreateReqList);
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

}
